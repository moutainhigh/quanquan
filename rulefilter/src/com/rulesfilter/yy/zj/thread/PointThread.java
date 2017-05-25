package com.rulesfilter.yy.zj.thread;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
















//import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.circle.core.util.CircleMD5;
import com.rulesfilter.yy.zj.model.AnsLog;
import com.rulesfilter.yy.zj.model.PointToPointRule;
import com.rulesfilter.yy.zj.service.RuleFilterService;
import com.rulesfilter.yy.zj.utils.ParamStatic;

/**
 * 
 * 此线程处理点对点规则
 * @author zhoujia
 *
 * @date 2015年9月7日
 */
public class PointThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(PointThread.class);
	RuleFilterService service = new RuleFilterService();
	private PointToPointRule pr;
	
	private String logJson;
	
	public PointThread(PointToPointRule pr, String logJson) {
		this.pr = pr;
		this.logJson = logJson;
	}
	@Override
	public void run() {
		logger.info("------------------进入点对点规则处理线程-----------------");
		AnsLog log = (AnsLog)Json.jsonParser(logJson, AnsLog.class);
		
		//这个日志存在多长时间，在redis中设置一个ttl为pr.getEveryDay()*24*3600 秒的 kv，如果可过期，则key事件通知会收到 事件通知，删除所在队列的信息
		//record|tmp|u1:userId1|u2:userId2|i1:IMEI1|i2:IMEI2|p1:IP1|p2:IP2|logId(ANSLONG|QID)|ruleId
		String userTmpDelKey = ParamStatic.userTmpDelKey(log.getUserId(), log.getQueUserId(),log.getAnswerIMEI(),log.getAnswerIMEI(),log.getAnswerIP(),log.getQuestionIP(),log.getLogId(), pr.getFilgerRuleId());
		logger.info("key失效临时key存入reids key = = = " + userTmpDelKey);
		Redis.CONNECT.setex(userTmpDelKey , (int)(pr.getEveryDay()*24*3600), log.getUserId());
		//设置访问日志
		this.setUserLog(log, userTmpDelKey);

		List<String> ruleCount = null;
		if(ParamStatic.POINT_RULE_ACC_YES.equals(pr.getAccount())){//取出记录的日志，只要是设置规则的，随意取出都可以
			logger.info("账号匹配----" + pr.getFilgerRuleId());
			ruleCount = Redis.shard.lrange(ParamStatic.userPointLogListKeyAcc(log.getUserId(), log.getQueUserId(), pr), 0, -1);
		}else if(ParamStatic.POINT_RULE_IMEI_YES.equals(pr.getIMEI())){
			logger.info("IMEI匹配");
			ruleCount = Redis.shard.lrange(ParamStatic.userPointLogListKeyIMEI(log.getAnswerIMEI(), log.getQuestionIMEI(), pr), 0, -1);
		}else if(ParamStatic.POINT_RULE_IP_YES.equals(pr.getIP())){
			logger.info("IP匹配");
			ruleCount = Redis.shard.lrange(ParamStatic.userPointLogListKeyIP(log.getAnswerIP(), log.getQuestionIP(), pr), 0, -1);
		}
		
		String logId = log.getLogId();
		String filgerRuleId = pr.getFilgerRuleId();
		String ruleSign = null;
		CHbase bean = CHbase.bean();
		//CElastic elastic = CElastic.elastic();
		boolean isIllege = isIllquestion(log);
		logger.info("------点对点规则过滤是否违规："+isIllege+"---------------------");
		if(isIllege){//触发最低次数限制，触发规则，按照规则的处理状态（仅记录、不可见、禁抢、拉黑）处理
			logger.info("触发面向点对点过滤规则，被面向点对点规则命中，过程id = " + pr.getFilgerRuleId());
			log.setHit(ParamStatic.RULE_HIT_YES);//被命中，添加命中标记
			switch (pr.getStatus()) {
			case ParamStatic.RULE_FILTER_RECORD: // 仅记录
				logger.info("点对点规则：仅记录==============================" + ParamStatic.RULE_FILTER_RECORD );
				break;
			case ParamStatic.RULE_FILTER_NOSEE://不可见
				logger.info("点对点 不可见==============================" + ParamStatic.RULE_FILTER_NOSEE );
				ruleSign = CircleMD5.encodeSha1(logId + filgerRuleId +ParamStatic.RULE_FILTER_NOSEE) ;
					
				try {
					for (String string : ruleCount) { //如果此条被命中，则这个队列中的全部条目都是刷单记录，全部更新  string 格式record|tmp|u1:userId1|u2:userId2|i1:IMEI1|i2:IMEI2|p1:IP1|p2:IP2|logId(ANSLONG|QID)|ruleId
						logger.info("点对点规则  被不可见 - 循环更新 ---- 被命中记录 ===" + string);
						String logIdStr = this.getLogId(string);
						if(logIdStr == null) break;
						this.updateHbaseTable(bean,logIdStr,AnsLog.isHit,ParamStatic.RULE_FILTER_NOSEE,ruleSign);
						logger.info("点对点 更新成功------------------------");
					}
				} catch (Exception e) {
					logger.info("更新数据 异常 = ",e);
				}
				
				service.relationNoSee(log, pr);
				
				break;
			case ParamStatic.RULE_FILTER_FORBIDROB://禁抢
				logger.info("点对点规则：禁抢==============================" + ParamStatic.RULE_FILTER_FORBIDROB  );
				ruleSign = CircleMD5.encodeSha1(logId + filgerRuleId +ParamStatic.RULE_FILTER_FORBIDROB) ;
				for (String string : ruleCount) { //如果此条被命中，则这个队列中的全部条目都是刷单记录，全部更新
					logger.info("点对点规则  被禁抢 - 循环更新 ---- 被命中记录 ===" + string);
					String logIdStr = this.getLogId(string);
					if(logIdStr == null) break;
					this.updateHbaseTable(bean, logIdStr,AnsLog.isHit,ParamStatic.RULE_FILTER_FORBIDROB,ruleSign);
				}
				try { // 禁抢用户
					if(ParamStatic.NOSEE_RULE_YES.equals(pr.getAccount())){//账号禁抢
						service.updateUserAccessStatus(log.getUserId(), ParamStatic.user_access_norobt, pr.getLongDays());
						//设置禁抢计时key
						String userNoRobtTmpDelKey = ParamStatic.userNoRobtTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_ACC,log.getAnswerAccount());
						Redis.CONNECT.setex(userNoRobtTmpDelKey , (int)(pr.getLongDays()*24*3600), "1");
					}
					if(ParamStatic.NOSEE_RULE_YES.equals(pr.getIMEI())){//imei禁抢
						service.addIEMIToAccessList(log.getAnswerIMEI(), String.valueOf(ParamStatic.user_access_norobt), pr.getLongDays());
						//设置禁抢计时key
//						String userNoRobtTmpDelKey = ParamStatic.userNoRobtTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_IMEI,log.getAnswerIMEI());
//						Redis.CONNECT.setex(userNoRobtTmpDelKey , (int)(pr.getLongDays()*24*3600), "1");
					}
					if(ParamStatic.NOSEE_RULE_YES.equals(pr.getIP())){//ip禁抢
						service.addIPToAccessList(log.getAnswerIP(), String.valueOf(ParamStatic.user_access_norobt), pr.getLongDays());
						//设置禁抢计时key
//						String userNoRobtTmpDelKey = ParamStatic.userNoRobtTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_IP,log.getAnswerIP());
//						Redis.CONNECT.setex(userNoRobtTmpDelKey , (int)(pr.getLongDays()*24*3600), "1");
					}
				} catch (TException e) {
					logger.info("禁抢异常，请检查",e);
				}
				
				break;
			case ParamStatic.RULE_FILTER_FORBIDALL://拉黑
				logger.info("点对点规则：拉黑=-=============================" + ParamStatic.RULE_FILTER_FORBIDALL );
				ruleSign = CircleMD5.encodeSha1(logId + filgerRuleId +ParamStatic.RULE_FILTER_FORBIDALL) ;
				for (String string : ruleCount) { //如果此条被命中，则这个队列中的全部条目都是刷单记录，全部更新
					logger.info("点对点规则  被拉黑 - 循环更新 ---- 被命中记录 ===" + string);
					String logIdStr = this.getLogId(string);
					if(logIdStr == null) break;
					this.updateHbaseTable(bean, logIdStr,AnsLog.isHit,ParamStatic.RULE_FILTER_FORBIDALL,ruleSign);
				}
				try { // 拉黑用户
					if(ParamStatic.NOSEE_RULE_YES.equals(pr.getAccount())){//账号拉黑
						service.updateUserAccessStatus(log.getUserId(), ParamStatic.user_access_blackList, (int)pr.getLongDays().doubleValue());
						//设置拉黑过期计时key
						String userBlackTmpDelKey = ParamStatic.userBlackTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_ACC,log.getAnswerAccount());
						Redis.CONNECT.setex(userBlackTmpDelKey , (int)(pr.getLongDays()*24*3600), "1");
					}
					if(ParamStatic.NOSEE_RULE_YES.equals(pr.getIMEI())){//imei拉黑
						service.addIEMIToAccessList(log.getAnswerIMEI(), String.valueOf(ParamStatic.user_access_blackList), (int)pr.getLongDays().doubleValue());
						//设置拉黑过期计时key
//						String userBlackTmpDelKey = ParamStatic.userBlackTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_IMEI,log.getAnswerIMEI());
//						Redis.CONNECT.setex(userBlackTmpDelKey , (int)(pr.getLongDays()*24*3600), "1");
					}
					if(ParamStatic.NOSEE_RULE_YES.equals(pr.getIP())){//ip拉黑
						service.addIPToAccessList(log.getAnswerIP(), String.valueOf(ParamStatic.user_access_blackList), (int)pr.getLongDays().doubleValue());
						//设置拉黑过期计时key
//						String userBlackTmpDelKey = ParamStatic.userBlackTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_IP,log.getAnswerIP());
//						Redis.CONNECT.setex(userBlackTmpDelKey , (int)(pr.getLongDays()*24*3600), "1");
					}
				} catch (TException e) {
					logger.info("拉黑异常，请检查",e);
				}
				break;
			default:
				logger.error("没有对应的处理状态  pr.getStatus()==" + pr.getStatus());
				break;
			}
			
		}else{//
			logger.info("--不违规，线程结束--");
		}
		
		
	}
	/**
	 * record|tmp|u1:userId1|u2:userId2|i1:IMEI1|i2:IMEI2|p1:IP1|p2:IP2|logId(ANSLONG|QID)|ruleId
	 * record|tmp|u1:2b78bf94e95b4d7eb6ba7edcab9c4d47|u2:714eda6c64a54fb788cbdadb5bd08585|i1:867323029476544|i2:867323029476544|p1:172.16.1.144|p2:172.16.1.170|ANSLOG|d2fca727c1294ebeb40d5bf513a7820a|point_nosee
	 * @param str
	 * @return
	 */
	public String getLogId(String str){
		if(str != null){
			String[] split = str.split("\\|");
			if(split.length == 11){
				return split[8]+"|"+split[9];
			}
		}
		return null;
	}
	
	/***
	 * 设置user的访问日志
	 * @param log
	 * @param userTmpDelKey
	 */
	public void setUserLog(AnsLog log,String userTmpDelKey){
		logger.info("保存点对点日志队列    点对点规则设置 = "+pr.getPrule().getRuleAccount() + " "+pr.getPrule().getRuleIMEI() + pr.getPrule().getRuleIP());
		if(ParamStatic.POINT_RULE_YES.equals(pr.getPrule().getRuleAccount())){
			String userLogACCKey1 = ParamStatic.userPointLogListKeyAcc(log.getUserId(), log.getQueUserId(), pr);
			String userLogACCKey2 = ParamStatic.userPointLogListKeyAcc(log.getQueUserId(), log.getUserId(), pr);
			logger.info("点对点账号 记录存入redis userLogACCKey1 ="+userLogACCKey1 + " userLogACCKey2 = " + userLogACCKey2);
			Redis.shard.lpush(userLogACCKey1, userTmpDelKey);
			Redis.shard.lpush(userLogACCKey2, userTmpDelKey);
		}
		if(ParamStatic.POINT_RULE_YES.equals(pr.getPrule().getRuleIMEI())){
			String userLogIMEIKey1 = ParamStatic.userPointLogListKeyIMEI(log.getAnswerIMEI(), log.getQuestionIMEI(), pr);
			String userLogIMEIKey2 = ParamStatic.userPointLogListKeyIMEI(log.getQuestionIMEI(), log.getAnswerIMEI(), pr);
			logger.info("点对点IMEI 记录存入redis userLogIMEIKey1=" +userLogIMEIKey1+ " userLogIMEIKey2="+ userLogIMEIKey2);
			Redis.shard.lpush(userLogIMEIKey1, userTmpDelKey);
			Redis.shard.lpush(userLogIMEIKey2, userTmpDelKey);
		}
		if(ParamStatic.POINT_RULE_YES.equals(pr.getPrule().getRuleIP())){
			String userLogIPKey1 = ParamStatic.userPointLogListKeyIP(log.getAnswerIP(), log.getQuestionIP(), pr);
			String userLogIPKey2 = ParamStatic.userPointLogListKeyIP(log.getQuestionIP(), log.getAnswerIP(), pr);
			logger.info("点对点IP 记录存入redis userLogIMEIKey1=" +userLogIPKey1+ " userLogIMEIKey2="+ userLogIPKey2);
			Redis.shard.lpush(userLogIPKey1, userTmpDelKey);
			Redis.shard.lpush(userLogIPKey2, userTmpDelKey);
		}
	}
	
	
	/**
	 * 判断是否违规
	 * @param log
	 * @return
	 */
	public boolean isIllquestion(AnsLog log){
		//默认都是不违反规则的
		boolean accIll = false;
		boolean ImeiIll = false;
		boolean ipIll = false;
		try {
			if(ParamStatic.POINT_RULE_ACC_YES.equals(pr.getAccount())){
				String userLogACCKey1 = ParamStatic.userPointLogListKeyAcc(log.getUserId(), log.getQueUserId(), pr);
				long size = Redis.shard.llen(userLogACCKey1);
				accIll = size >= pr.getTimes();
				logger.info("点对点规则被账号匹配命中");
			}
			if(ParamStatic.POINT_RULE_IMEI_YES.equals(pr.getIMEI())){
				String userLogIMEIKey1 = ParamStatic.userPointLogListKeyIMEI(log.getAnswerIMEI(), log.getQuestionIMEI(), pr);
				long size = Redis.shard.llen(userLogIMEIKey1);
				ImeiIll = size >= pr.getTimes();
				logger.info("点对点规则被IMEI匹配命中");
			}
			if(ParamStatic.POINT_RULE_IP_YES.equals(pr.getIP())){
				String userLogIPKey1 = ParamStatic.userPointLogListKeyIP(log.getAnswerIP(), log.getQuestionIP(), pr);
				long size = Redis.shard.llen(userLogIPKey1);
				ipIll = size >= pr.getTimes();
				logger.info("点对点规则被IP匹配命中");
			}
			//三个规则，违反一个即为违反规则
			logger.info("判断结果= = =" + (accIll || ImeiIll || ipIll) );
		}catch (Exception e){
			logger.error("判断是否违规异常：", e);
		}

		return accIll || ImeiIll || ipIll;

	}
	
	
	//此方法可能需要加锁
		/***
		 * 
		 * @param bean
		 * @param resultStatus  处理的状态，仅记录，不可见，禁抢，拉黑
		 */
		private void updateHbaseTable(CHbase bean,String logId, String hit, String resultStatus,String rulesSign){
			try {
				Get get = new Get(Bytes.toBytes(logId));
				get.addFamily(Bytes.toBytes(AnsLog.hbase_family_2));
				Result result = bean.get(AnsLog.hbase_table, get);
				//List<String> rulesKV = new ArrayList<String>();
				Map<String, String> resultMap = new  HashMap<String, String>();
				for (KeyValue kv : result.raw()) {
					resultMap.put(new String(kv.getKey()), new String(kv.getValue()));
				}
				//读取原来的规则
				get.addFamily(Bytes.toBytes(AnsLog.hbase_family_1));
				result = bean.get(AnsLog.hbase_table, get);
				byte[] rulesName = result.getValue(Bytes.toBytes("0"), Bytes.toBytes("rulesName".toUpperCase()));
				String oldRulesName = new String(rulesName==null?"".getBytes():rulesName);
				String rulesNameStr = oldRulesName + "," + pr.getFilgerRuleId();
				if(rulesNameStr.startsWith(",")){
					rulesNameStr = rulesNameStr.substring(1);
				}
				
				Put put = new Put(Bytes.toBytes(logId));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("userId".toUpperCase()),Bytes.toBytes(log.getUserId()));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("time".toUpperCase()),PLong.INSTANCE.toBytes(log.getTime()));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerPhone".toUpperCase()),Bytes.toBytes(log.getAnswerPhone()));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerIMEI".toUpperCase()),Bytes.toBytes(log.getAnswerIMEI()));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("questionIMEI".toUpperCase()),Bytes.toBytes(log.getQuestionIMEI()));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerIP".toUpperCase()),Bytes.toBytes(log.getAnswerIP()));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("questionPhone".toUpperCase()),Bytes.toBytes(log.getQuestionPhone()));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("qid".toUpperCase()),Bytes.toBytes(log.getQid()));
//				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("cash".toUpperCase()),PDecimal.INSTANCE.toBytes(new BigDecimal(log.getCash())));
				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("hit".toUpperCase()),Bytes.toBytes(hit));//是否被命中
				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("resultStatus".toUpperCase()),Bytes.toBytes(resultStatus));//处理状态
				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("rulesSign".toUpperCase()),Bytes.toBytes(rulesSign));//同组流水记录标记
				
				put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("rulesName".toUpperCase()),Bytes.toBytes(rulesNameStr));//标记被那个rule命中
				
				resultMap.put("R"+(resultMap.size() + 1), pr.getFilgerRuleId() );
				//rules列
				for (String key : resultMap.keySet()) {
					String value = resultMap.get(key);
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_2),Bytes.toBytes(key),Bytes.toBytes(value));
				}
				
				bean.put(AnsLog.hbase_table, put);
			} catch (IOException e) {
				logger.error("updateHbaseTable 出错 : ",e);
			}
		}
}


