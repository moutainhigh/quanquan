package com.rulesfilter.yy.zj.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.types.PInteger;
import org.apache.phoenix.schema.types.PLong;
import org.apache.thrift.TException;
import org.elasticsearch.action.get.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.circle.core.util.CircleMD5;
import com.circle.core.util.Config;
import com.rulesfilter.yy.zj.model.AnsLog;
import com.rulesfilter.yy.zj.model.FilterRule;
import com.rulesfilter.yy.zj.model.PointRule;
import com.rulesfilter.yy.zj.model.PointToPointRule;
import com.rulesfilter.yy.zj.model.ProcessFilterRule;
import com.rulesfilter.yy.zj.model.UserELK;
import com.rulesfilter.yy.zj.subscrib.Subscribe;
import com.rulesfilter.yy.zj.utils.JDBCUtils;
import com.rulesfilter.yy.zj.utils.ParamStatic;

/**
 * @author zhoujia
 *
 * @date 2015-8-11
 */
public class RuleFilterService {
	
	private Logger logger = LoggerFactory.getLogger(RuleFilterService.class);
	
//	/**记录已经添加的面向过程的规则**/
//	public static List<String> hadRecordPrcessRuleList = new ArrayList<String>();
//	/**记录已经记录的点对点规则*/
//	public static List<String> hadRecordPointRuleList = new ArrayList<String>();
	
	/**IMEI 权限hash的key**/
	public static final String IMEIAccessHash = "IMEIAccessHash";
	
	/**IP 权限hash的key**/
	public static final String IPAccessHash = "IPAccessHash";
	/**
	 * 过程规则对象
	 * @param filterRule
	 */
	public void addFilterRule(FilterRule filterRule){
		String ruleJson = Json.json(filterRule);
		Redis.shard.lpush(ParamStatic.filterRule, ruleJson);
	}
	
	/**
	 * 点对点规则对象
	 * @param pointRule
	 */
	public void addPointRule(PointRule pointRule){
		String ruleJson = Json.json(pointRule);
		Redis.shard.lpush(ParamStatic.pointRule, ruleJson);
	}
	
	
	
	
	/**
	 * 过程匹配规则存入redis
	 * @param pfRule
	 */
	public void addProcessRulesToRedis(List<ProcessFilterRule> pfRule){
		
		for(int i=0;i<pfRule.size();i++){
			pfRule.get(i).setPrRuleKey(ParamStatic.filterRule);
			String ruleJson = Json.json(pfRule);
			
			Redis.shard.lpush(ParamStatic.redisFilgerKey_process + "all", ruleJson);
		}
		
		
	}
	
	/***
	 * 
	 * 点对点规则匹配存入redis
	 * @param ptpRule
	 */
	public void addPointRulesToRedis(List<PointToPointRule> ptpRule){
		for(int i=0;i<ptpRule.size();i++){
			ptpRule.get(i).setPruleKey(ParamStatic.pointRule); 
			String ruleJson = Json.json(ptpRule);
			Redis.shard.lpush(ParamStatic.redisFilgerKey_point + "all", ruleJson);
		}
		
	}
	

//	/**
//	 *
//	 * @param log
//	 * @param userId
//	 */
//	public void setLogToRedis(AnsLog log,String userId){
//		String logJson = Json.json(log);
//		//Redis.shard.lpush(ParamStatic.redisFilgerKey+userId, logJson);
//		
//		//List<String> processRuleList = Redis.shard.lrange(ParamStatic.redisFilgerKey_process+userId, 0, -1);
//		//List<String> pointRuleList = Redis.shard.lrange(ParamStatic.redisFilgerKey_point+userId, 0, -1);
//		Boolean exists = Redis.shard.exists(ParamStatic.redisFilgerKey_process+userId);
//		if(!exists){//用户第一次答题
//			List<String> allProcessList = Redis.shard.lrange(ParamStatic.redisFilgerKey_process + "all", 0, -1);
//			List<String> allPointList = Redis.shard.lrange(ParamStatic.redisFilgerKey_point + "all", 0, -1);
//			for (int i = 0; i < allProcessList.size(); i++) {//回答过程
//				Redis.shard.hset(ParamStatic.redisFilgerKey_process + userId, "rule|"+i, logJson);
//			}
//			for (int i = 0; i < allPointList.size(); i++) {//点对点
//				Redis.shard.hset(ParamStatic.redisFilgerKey_point + userId, "rule|"+i, logJson);
//			}
//			
//		}else{//
//			Map<String, String> userProcessMap = Redis.shard.hgetAll(ParamStatic.redisFilgerKey_process+userId);
//			Map<String, String> userPointMap = Redis.shard.hgetAll(ParamStatic.redisFilgerKey_point+userId);
//			
//			
//			//pointRuleList.add(logJson);
//			//Redis.shard.lpush(key, strings)
//			
//			List<String> processRule = Redis.shard.lrange(ParamStatic.redisFilgerKey_process + "all", 0, -1);
//			
//			for (String proRule : processRule) {
//				ProcessFilterRule pfr = (ProcessFilterRule)Json.jsonParser(proRule, ProcessFilterRule.class);
//				
//				
//			}
//			
//		}
//		
//		
//		
//	}
	
	
//	public void setLogToRedis(AnsLog log){
//		
//		List<String> processRuls = Redis.shard.lrange(ParamStatic.redisFilgerKey_process + "all", 0, -1);
//		for (String string : processRuls) {
//			
//			
//			ProcessFilterRule pfr = (ProcessFilterRule)Json.jsonParser(string, ProcessFilterRule.class);
//			
//			FilterThread lt = new FilterThread(pfr, log);
//			lt.start();
//		}
//		
//		
//	}
	
	/***
	 * 启动入口
	 * 多个线程监听ParamStatic.filterChannel，每个线程在redis 中建立四个线程list分别为仅记录，不可见，禁抢，拉黑。
	 */
	public void init(String configPath){
		logger.info("初始化开始");
		try {
			//初始化hbase
			CHbase.instance(configPath + "hbase-site.xml");
			logger.info("----------------hbase初始化成功-------------------");
			Redis.initialShard(new Config(configPath + ParamStatic.configFileName));
			logger.info("----------------reids集群版初始化成功-------------------");
			Redis.initial(new Config(configPath + ParamStatic.configFileName));
			logger.info("----------------reids单机版初始化成功-------------------");
			CElastic.inital(new Config(configPath + ParamStatic.configFileName));
			logger.info("----------------elastic初始化成功-------------------");
		} catch (IOException e) {
			logger.error("hbase 初始化异常  路径：" + configPath +  "hbase-site.xml");
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("初始化 hbase ， redis ， elastic 失败", e);
			e.printStackTrace();
		}
		
		Subscribe ss = new Subscribe(configPath);
		List<String> processRuls = Redis.shard.lrange(ParamStatic.redisFilgerKey_process + "all", 0, -1);
		
		List<String> pointRuls = Redis.shard.lrange(ParamStatic.redisFilgerKey_point + "all", 0, -1);
		//添加添加新的规则管道，当有新的规则需要添加的时候，信息会打入这个管道，JedisSubpub收到信息会去把新的规则加上监听
		ss.subscribeAddNewRule();
		//订阅log，存hbase
		ss.subscribeHbase(ParamStatic.filterChannel);
		//SubscribeFilterChannel sfc = new SubscribeFilterChannel(ss);
		//sfc.start();//每次来个问题则监听写入hbase
		
		logger.info("processRuls 过程规则：" + processRuls);
		logger.info("pointRules 点对规则： " + pointRuls);
		//循环遍历过程规则，每个规则都订阅这个总的日志管道
		for (String string : processRuls) {
			ProcessFilterRule pfRule = (ProcessFilterRule)Json.jsonParser(string, ProcessFilterRule.class);
			//SubscribeRuleFilter srf = new SubscribeRuleFilter(ss, pfRule);
			//srf.start();
			logger.info("添加过程订阅 规则管道" + pfRule.getFilgerRuleId()+"--------------------");
			ss.subscribe(ParamStatic.filterChannel,pfRule);
		}
		
		//点对点规则，同上
		//[{"pruleKey":"pointRule","filgerRuleId":"pint_blacklist","everyDay":0.041666666666666664,"times":3,"status":"4","longDays":0.041666666666666664,"account":"1","imei":"0","ip":"0"}]
		for (String string : pointRuls) {
			PointToPointRule ptpRule = (PointToPointRule)Json.jsonParser(string, PointToPointRule.class);
			String string2 = Redis.shard.get("pointRule");
			PointRule prule = (PointRule)Json.jsonParser(string2,PointRule.class);
			ptpRule.setPrule(prule);
			//SubscribeRuleFilter srf = new SubscribeRuleFilter(ss, ptpRule);
			//srf.start();
			logger.info("添加点对点订阅   规则管道" + ptpRule.getFilgerRuleId()+"--------------------");
			ss.subscribe(ParamStatic.filterChannel,ptpRule);
		}
		//订阅临时key
		ss.subscribeDelKey(configPath);
	}
	
	
	
	/***
	 * 账号拉黑
	 * 规则自动更新状态操作，由多个规则对应多个状态，所以一个用户可以同时被不可见，禁抢和拉黑。状态可以重叠，重叠后，由于禁抢和拉黑只能对外显示一种状态
	 * 所以对外显示的状态有几种状态叠加而成，显示惩罚最严厉的状态。最严厉状态过期则按照次严厉的状态显示
	 * @param userId
	 * @param access 3是拉黑
	 * @param days 来黑天数
	 * @return
	 * @throws TException
	 */
	public String updateUserAccessStatus(String userId, int access,double days)
			throws TException {
		//查询原来的状态
		int blackTimes = 0;
		String phoneNum = null;
		long norobitEndDate = 0;
		long blackEndDate = 0;
		int oldAccess = 0;
		String sql = " select * from circle.user where id =  '" + userId +"' ";
		Connection connection = JDBCUtils.getConnection();
		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement(sql);
			ResultSet rs = pst.executeQuery(sql);
			while(rs.next()){
				userId = rs.getString("id");
				blackTimes = rs.getInt("BLACKSTATUSTIMES");
				phoneNum = rs.getString("MOBILE");
				norobitEndDate = rs.getLong("NOROBITENDDATE");//禁抢过期时间
				blackEndDate = rs.getLong("BLACKENDDATE");//拉黑过期时间
				oldAccess = rs.getInt("ACCESS");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		//首先更新禁抢或者拉黑状态，然后更新access状态，更新access的时候需要根据权重判断，如果是拉黑则无需禁抢，如果是禁抢则可以拉黑，越严厉的惩罚，权重越重
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		Calendar norobitCal = Calendar.getInstance();
		norobitCal.setTimeInMillis(norobitEndDate);
		Calendar blackCal = Calendar.getInstance();
		blackCal.setTimeInMillis(blackEndDate);
		Calendar endDateCal = Calendar.getInstance();
		endDateCal.setTimeInMillis(System.currentTimeMillis() + (int)(days*24*3600*1000));
		boolean after = endDateCal.after(norobitCal); //新的违规日期是否在原来之后，更短的不能更新更长的违规操作，越重的惩罚权重越重
		boolean balckIsOut = cal.after(blackCal);//判断拉黑是否过期，拉黑为过期则不能修改为禁抢状态
		boolean after2 = endDateCal.after(blackCal);// 新的拉黑解除日期是否在原来的拉黑日期之后
		
		if(oldAccess != 1 && access == 3 && after2){//如果不是在白名单中，如果是拉黑操作  如果这次拉黑的解除时间在上次之后，则直接执行
			logger.info("如果是拉黑操作  如果这次拉黑的解除时间在上次之后，则直接执行");
			updateAccessAction(userId, phoneNum, access, days, blackTimes, blackEndDate, norobitEndDate);
		}else if(oldAccess != 1 && access == 5 && balckIsOut && after){//如果不在白名单中，如果是禁抢操作，原来状态不是拉黑状态，且禁抢解除日期在原来之后
			logger.info("如果是禁抢操作，原来状态不是拉黑状态，且禁抢解除日期在原来之后");
			updateAccessAction(userId, phoneNum, access, days, blackTimes, blackEndDate, norobitEndDate);
		}
		return "ok";
	}
	
	private void updateAccessAction(String userId,String phoneNum,Integer access,Double days,int blackTimes, long blackEndDate,long norobitEndDate){
		CHbase bean = CHbase.bean();
		Put update = new Put(Bytes.toBytes(userId));
		update.addColumn(Bytes.toBytes("0"), Bytes.toBytes("ACCESS"), PInteger.INSTANCE.toBytes(access)); //状态
		update.addColumn(Bytes.toBytes("0"), Bytes.toBytes("KEEPLONGACCESS"), PInteger.INSTANCE.toBytes(days));//状态持续时间
		if(access == 3){//如是拉黑，拉黑次数 +1
			update.addColumn(Bytes.toBytes("0"), Bytes.toBytes("BLACKSTATUSTIMES"), PInteger.INSTANCE.toBytes(blackTimes+1));//拉黑次数加1
			blackEndDate = System.currentTimeMillis() + (int)(days*24*3600*1000);
			update.addColumn(Bytes.toBytes("0"), Bytes.toBytes("BLACKENDDATE"), PInteger.INSTANCE.toBytes(blackEndDate));//拉黑截止日志
			String encodeSha1 = CircleMD5.encodeSha1(phoneNum);
			Redis.shard.hset(encodeSha1, "bdate", String.valueOf(blackEndDate));
		}else if(access == 5){//禁抢
			norobitEndDate = System.currentTimeMillis() + (int)(days*24*3600*1000);
			update.addColumn(Bytes.toBytes("0"), Bytes.toBytes("NOROBITENDDATE"), PInteger.INSTANCE.toBytes(norobitEndDate));//禁抢截止日志
			String encodeSha1 = CircleMD5.encodeSha1(phoneNum);
			Redis.shard.hset(encodeSha1, "rdate", String.valueOf(norobitEndDate));
		}
		update.addColumn(Bytes.toBytes("0"), Bytes.toBytes("ACCESSSTATEBEGINTIME"), PLong.INSTANCE.toBytes(System.currentTimeMillis()));//状态开始时间
		try {
			bean.put("CIRCLE.USER", update);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//更新redis中的字段
		String key  = CircleMD5.encodeSha1(phoneNum);
		logger.info("user redis hash key = = " + key);
		Redis.shard.hset(key, "access", String.valueOf(access));
		
		//更新elastic
		CElastic elastic = CElastic.elastic();
		GetResponse userResponse = elastic.get("ELC_TAB_REALUSER", userId);
		String sourceAsString = userResponse.getSourceAsString();
		UserELK userELK = (UserELK)Json.jsonParser(sourceAsString, UserELK.class);
		userELK.setAccess(access);
		elastic.index("ELC_TAB_REALUSER", userId, userELK); 
	}
	
	
	/******
	 * 添加手机IMEI到黑名单
	 * @param IMEI 
	 * @param type 状态：白名单1，黑名单3，禁抢5，正常等。
	 * @param days 持续天数
	 * return String
	 * key - IMEIAccessHash - IMEI1: 白名单时间|禁抢时间|黑名单时间
	 * 
	 */
	public String addIEMIToAccessList(String IMEI, String type, double days)
			throws TException {
		logger.info("添加" + IMEI + " 为 "+type + " "+ days +" 天");
		if(ParamStatic.user_access_norobt == Integer.parseInt(type)){//禁抢
			String value = null;
			long endDate = System.currentTimeMillis() + (int)(days * 24 * 3600 * 1000);
			String hget = Redis.shard.hget(IMEIAccessHash, IMEI);
			if(hget==null || hget.isEmpty()){
				value = "0|" +endDate+ "|0";
			}else{
				String[] split = hget.split("\\|");
				split[1] = String.valueOf(endDate);
				value = split[0]+"|"+split[1]+"|"+split[2];
			}
			Redis.shard.hset(IMEIAccessHash, IMEI, value);
		}
		if(ParamStatic.user_access_blackList == Integer.parseInt(type)){//拉黑
			String value = null;
			long endDate = System.currentTimeMillis() + (int)(days * 24 * 3600 * 1000);
			String hget = Redis.shard.hget(IMEIAccessHash, IMEI);
			if(hget==null || hget.isEmpty()){
				value = "0|0|" + endDate ;
			}else{
				String[] split = hget.split("\\|");
				split[2] = String.valueOf(endDate);
				value = split[0]+"|"+split[1]+"|"+split[2];
			}
			Redis.shard.hset(IMEIAccessHash, IMEI, value);
		}
		
//		Redis.shard.hset(IMEIAccessHash, IMEI, type);
//		//插入截止日期，跑定时任务，判断是否超过这个时间，如果超过则删除
//		Redis.shard.hset(IMEIAccessHash, IMEI+ "|endDate", String.valueOf(System.currentTimeMillis() + (days * 24 * 3600*1000)));
		return "ok";
	}
	
	/**添加用户ip到黑名单**/
	public String addIPToAccessList(String IP, String type, double days) throws TException {
		if(ParamStatic.user_access_norobt == Integer.parseInt(type)){//禁抢
			String value = null;
			long endDate = System.currentTimeMillis() + (int)(days * 24 * 3600 * 1000);
			String hget = Redis.shard.hget(IPAccessHash, IP);
			if(hget==null || hget.isEmpty()){
				value = "0|" +endDate+ "|0";
			}else{
				String[] split = hget.split("\\|");
				split[1] = String.valueOf(endDate);
				value = split[0]+"|"+split[1]+"|"+split[2];
			}
			Redis.shard.hset(IPAccessHash, IP, value);
		}
		if(ParamStatic.user_access_blackList == Integer.parseInt(type)){//拉黑
			String value = null;
			long endDate = System.currentTimeMillis() + (int)(days * 24 * 3600 * 1000);
			String hget = Redis.shard.hget(IPAccessHash, IP);
			if(hget==null || hget.isEmpty()){
				value = "0|0|" + endDate ;
			}else{
				String[] split = hget.split("\\|");
				split[2] = String.valueOf(endDate);
				value = split[0]+"|"+split[1]+"|"+split[2];
			}
			Redis.shard.hset(IPAccessHash, IP, value);
		}
//		Redis.shard.hset(IPAccessHash, IP, type);
//		//插入截止日期，跑定时任务，判断是否超过这个时间，如果超过则删除
//		Redis.shard.hset(IPAccessHash, IP + "|endDate", String.valueOf(System.currentTimeMillis() + (days*24*3600*1000)));
		return "ok";
	}
	
	
	
	
	/**
	 * 处理关系不可见，即如果不可见，是双方互相不可见。 因为redis的数据结构是单向的不可见，所有处理方法是分别为两个人都添加不可见规则，这样就完成了互相不可见
	 */
	public void relationNoSee(AnsLog log, FilterRule pfr){
		logger.info("处理关系不可见  ansUserId=" + log.getUserId() +"  quesUserId="+log.getQueUserId());
		if(FilterRule.account_yes.equals(pfr.getAccount())){//如果是账号不可见
			if(!log.getUserId().isEmpty() && !log.getQueUserId().isEmpty()){
				String noSeeAcc1 = Redis.shard.get(ParamStatic.noSeeRuleKey(log.getUserId()));//回答人id
				String noSeeAcc2 = Redis.shard.get(ParamStatic.noSeeRuleKey(log.getQueUserId()));//提问者id
				this.handleNoSee(noSeeAcc1, noSeeAcc2, log.getUserId(), log.getQueUserId());
			}else{
				logger.error("获取用户id失败，无法进行acc不可见！！！");
			}
			
		}
		if(FilterRule.IMEI_yes.equals(pfr.getIMEI())){//如果是IMEI不可见
			logger.info("IMEI 不可见   log.getAnswerIMEI() ="+log.getAnswerIMEI() + " log.getQuestionIMEI()="+log.getQuestionIMEI());
			if(!log.getAnswerIMEI().isEmpty() && !log.getQuestionIMEI().isEmpty()){
				String noSeeIMEI1 = Redis.shard.get(ParamStatic.noSeeRuleKey(log.getAnswerIMEI()));//
				String noSeeIMEI2 = Redis.shard.get(ParamStatic.noSeeRuleKey(log.getQuestionIMEI()));//
				this.handleNoSee(noSeeIMEI1, noSeeIMEI2, log.getAnswerIMEI(), log.getQuestionIMEI());
			}else{
				logger.error("获取IMEI失败，无法进行IMEI不可见！！！");
			}
		}
		if(FilterRule.IP_yes.equals(pfr.getIP())){//如果是IP不可见
			logger.info("IP 不可见   log.getAnswerIP() ="+log.getAnswerIP() + " log.getQuestionIP()="+log.getQuestionIP());
			if(!log.getAnswerIP().isEmpty() && !log.getQuestionIP().isEmpty()){
				String noSeeIP1 = Redis.shard.get(ParamStatic.noSeeRuleKey(log.getAnswerIP()));//回答人id
				String noSeeIP2 = Redis.shard.get(ParamStatic.noSeeRuleKey(log.getQuestionIP()));//提问者id
				this.handleNoSee(noSeeIP1, noSeeIP2, log.getAnswerIP(),log.getQuestionIP());
			}else{
				logger.error("获取IP失败，无法进行IP不可见！！！");
			}
		}
		
		this.tmpDelKey(pfr, log);
		
	}
	
	/**
	 * 根据规则的定义，给用户设置不可见规则的临时定时key，此key过期则删除对应的不可见规则
	 * @param pfr
	 * @param log
	 */
	public void tmpDelKey(FilterRule pfr, AnsLog log){
		logger.info("设置不可见定时key");
		String account = pfr.getAccount();
		String IMEI = pfr.getIMEI();
		String IP = pfr.getIP();
		if(ParamStatic.NOSEE_RULE_YES.equals(account)){
			String tmpKey = ParamStatic.userRuleTmpDelKey(log.getUserId(), log.getQueUserId(), ParamStatic.REDIS_NOSEE_ACC, log.getQueUserId(),log.getUserId());
			Long ttl = Redis.CONNECT.ttl(tmpKey);
			if(ttl == -2){//如果之前两个人没有不可见，直接设置
				Redis.CONNECT.setex(tmpKey , (int)(pfr.getLongDays()*24*3600), log.getUserId());
			}else{ // 两个人之前已经不可见了，则判断这次的不可见时间和剩余的不可见时间，谁的长则按照谁的执行
				if(ttl < (int)(pfr.getLongDays()*24*3600)) {
					Redis.CONNECT.setex(tmpKey , (int)(pfr.getLongDays()*24*3600), log.getUserId());
				}
			}
		}
		if(ParamStatic.NOSEE_RULE_YES.equals(IMEI)){
			String tmpKey = ParamStatic.userRuleTmpDelKey(log.getUserId(), log.getQueUserId(), ParamStatic.REDIS_NOSEE_IMEI, log.getQuestionIMEI(),log.getAnswerIMEI());
			Long ttl = Redis.CONNECT.ttl(tmpKey);
			if(ttl == -2){//如果之前两个人没有不可见，直接设置
				Redis.CONNECT.setex(tmpKey , (int)(pfr.getLongDays()*24*3600), log.getUserId());
			}else{ // 两个人之前已经不可见了，则判断这次的不可见时间和剩余的不可见时间，谁的长则按照谁的执行
				if(ttl < (int)(pfr.getLongDays()*24*3600)) {
					Redis.CONNECT.setex(tmpKey , (int)(pfr.getLongDays()*24*3600), log.getUserId());
				}
			}
		}
		if(ParamStatic.NOSEE_RULE_YES.equals(IP)){
			String tmpKey = ParamStatic.userRuleTmpDelKey(log.getUserId(), log.getQueUserId(), ParamStatic.REDIS_NOSEE_IP, log.getQuestionIP(),log.getAnswerIP());
			Long ttl = Redis.CONNECT.ttl(tmpKey);
			if(ttl == -2){//如果之前两个人没有不可见，直接设置
				Redis.CONNECT.setex(tmpKey , (int)(pfr.getLongDays()*24*3600), log.getUserId());
			}else{ // 两个人之前已经不可见了，则判断这次的不可见时间和剩余的不可见时间，谁的长则按照谁的执行
				if(ttl < (int)(pfr.getLongDays()*24*3600)) {
					Redis.CONNECT.setex(tmpKey , (int)(pfr.getLongDays()*24*3600), log.getUserId());
				}
			}
		}
	}
	
	/**
	 * 设置不可见关系map
	 * @param noSeeValueAns  回答者不可见内容
	 * @param noSeeValueQue  提问者不可见内容
	 * @param ansObj   回答者（accID，IMEI，ip）
	 * @param quesObj	提问者（accID，IMEI，ip）
	 */
	public void handleNoSee(String noSeeValueAns,String noSeeValueQue, String ansObj,String quesObj){
		logger.info("设置不可见关系map");
		//处理不可见
		if(noSeeValueAns==null || noSeeValueAns.isEmpty()){
			Redis.shard.set(ParamStatic.noSeeRuleKey(ansObj),quesObj);
		}else{
			if(!noSeeValueAns.contains(quesObj)){
				Redis.shard.set(ParamStatic.noSeeRuleKey(ansObj),noSeeValueAns+"|"+quesObj);
			}
		}
		if(noSeeValueQue == null||noSeeValueQue.isEmpty()){
			Redis.shard.set(ParamStatic.noSeeRuleKey(quesObj),ansObj);
		}else{
			if(!noSeeValueQue.contains(ansObj)){
				Redis.shard.set(ParamStatic.noSeeRuleKey(quesObj),noSeeValueAns+"|"+ansObj);
			}
		}
	}


	public int getIsRot(String qid){
		int rob = 0; //是否是机器人
		try {
			GetResponse questionResponse = CElastic.elastic().get("art_que",qid);
			rob = Integer.parseInt(questionResponse.getSource().get("rob").toString());
		}catch(Exception e){
			logger.error("查询elastic中是否是机器人出错：",e);
		}
		return rob;
	}



	public long getUserSelectBeginTime(String qid) throws IOException {
		if(qid == null || qid.isEmpty()) return 0;
		Get get = new Get(Bytes.toBytes(qid));
		Result result = CHbase.bean().get("CIRCLE.QUESTION", get);
		if (result.isEmpty()) return 0;

		String endDate = null;
		NavigableMap<byte[], byte[]> maps = result.getFamilyMap(Bytes.toBytes("4"));
		for (byte[] cell : maps.keySet()) {
			System.out.println("cell "+  Bytes.toString(cell));

			// 2016-03-12 09:37:57:吧啦啦/c8cd0f3e8f5b466ab3a9d6f7eb840b15/17000000002/
			String msg_cache = Bytes.toString(maps.get(cell));

			String[] data = msg_cache.split("/");
			endDate = data[0].substring(0,19);
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date time = format.parse(endDate);
			return time.getTime();
		} catch (ParseException e) {
			logger.error("筛选开始时间格式解析错误 ： ", e);
			format = new SimpleDateFormat("HH:mm:ss");
			Date time = null;
			try {
				time = format.parse(endDate);
				return time.getTime();
			} catch (ParseException e1) {
				logger.error("\"HH:mm:ss\"  按照这个格式解析也出错，说明这个是老数据 ",e1);
			}

		}
		return 0;

	}
	
}
