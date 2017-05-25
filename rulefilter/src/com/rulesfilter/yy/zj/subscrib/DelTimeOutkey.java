package com.rulesfilter.yy.zj.subscrib;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.types.PInteger;
import org.elasticsearch.action.get.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.circle.core.redis.incr.ReConnectPublish;
import com.circle.core.util.CircleMD5;
import com.rulesfilter.yy.zj.model.AnsLog;
import com.rulesfilter.yy.zj.model.PointToPointRule;
import com.rulesfilter.yy.zj.model.User;
import com.rulesfilter.yy.zj.model.UserELK;
import com.rulesfilter.yy.zj.service.RuleFilterService;
import com.rulesfilter.yy.zj.utils.JDBCUtils;
import com.rulesfilter.yy.zj.utils.ParamStatic;

import redis.clients.jedis.JedisPubSub;

/**
 * 
 * 监听临时key，这些key记录这个用户的这条日志存在时间，如果key到期删除，则在用户对应的规则列表中删除他的日志记录
 * 
 * @author zhoujia
 *
 * @date 2015年8月12日
 */
public class DelTimeOutkey extends ReConnectPublish{

	//private static final String PointToPointRule = null;
	private static Logger logger = LoggerFactory.getLogger(DelTimeOutkey.class);
	
	public DelTimeOutkey() {
		this.delay_check=1000;
		this.delay_connect = 1000;
		this.delay_wait = 5000;
	}
	@Override
	public void onPMessage(String pattern, String channel, String message) {
		boolean checkreconnect = checkreconnect(channel, message);
		//System.out.println("删除用户的过期日志。。。。。啊啊啊啊啊阿 2" + message + "   "+ pattern + "   " + channel);
		//删除用户的过期日志。。。。。啊啊啊啊啊阿 2set       __key*__:record|tmp|*   			__keyspace@0__:record|tmp|userId|logId(ANSLONG|QID)|ruleId
		//删除用户的过期日志。。。。。啊啊啊啊啊阿 2expire    __key*__:record|tmp|*   			__keyspace@0__:record|tmp|userId|logId(ANSLONG|QID)|ruleId
		//删除用户的过期日志。。。。。啊啊啊啊啊阿 2expired   __key*__:record|tmp|*   			__keyspace@0__:record|tmp|process|userId1|userId2|logId(ANSLONG|QID)|ruleId
		
		//删除用户的过期日志。。。。。啊啊啊啊啊阿 2expired   __key*__:record|tmp|*   			__keyspace@0__:record|tmp|u1:userId1|u2:userId2|i1:IMEI1|i2:IMEI2|p1:IP1|p2:IP2|logId(ANSLONG|QID)|ruleId
		//删除用户的过期日志。。。。。啊啊啊啊啊阿 2expired   __key*__:nosee|record|tmp|*   	__keyspace@0__:nosee|record|tmp|userId1|userId2|type|typeValue1|typeValue2
		if(!checkreconnect){
			//logger.info("pattern = " + pattern +"  channel ="+channel +" message="+message);
			try {
				if("expired".equals(message)){//过期
					String[] patternLog = pattern.split(":");
					//如果是日志过期，从队列中删除过期日志。用户每次答题就有一条日志
					if(patternLog[1].equals(ParamStatic.tmpkey_pix + "*")){
						String[] split = channel.split("\\|");
						if("process".equals(split[2])){ // 面向过程规则key过期
							//__keyspace@0__:record|tmp|process|userId1|userId2|logId(ANSLONG|QID)|ruleId
							String userId1 = split[3];
							String userId2 = split[4];
							String logId = split[5] + "|" + split[6];
							String ruleId = split[7];
							String userLongKey1 = ParamStatic.userLogListKey(userId1,userId2,  ruleId);
							String userLongKey2 = ParamStatic.userLogListKey(userId2,userId1,  ruleId);
							String userTmpDelKey1 = ParamStatic.userProcessTmpDelKey(userId1,userId2, logId, ruleId);
							String userTmpDelKey2 = ParamStatic.userProcessTmpDelKey(userId2,userId1, logId, ruleId);
							Long lrem1 = Redis.shard.lrem(userLongKey1, 0, userTmpDelKey1);
							Long lrem2 = Redis.shard.lrem(userLongKey1, 0, userTmpDelKey2);
							Long lrem3 = Redis.shard.lrem(userLongKey2, 0, userTmpDelKey1);
							Long lrem4 = Redis.shard.lrem(userLongKey2, 0, userTmpDelKey1);

							logger.info("用户过程日志过期：userId1=" + userId1 +" userId2=" + userId2 + "\n logId="+logId +"\n ruleId="+ruleId+"lrem1="+lrem1+"lrem2="+lrem2+"lrem3="+lrem3+"lrem4="+lrem4); 
						} else if("norob".equals(split[2])){//过程规则拉黑禁抢日志过期
							//__keyspace@0__:record|tmp|norob|userId1|userId2|logId(ANSLONG|QID)|ruleId
							String userId1 = split[3];
							String userId2 = split[4];
							String logId = split[5] + "|" + split[6];
							String ruleId = split[7];
							String userTmpDelKey1 = ParamStatic.userNorobLogTmpDelKey(userId1,userId2, ruleId, logId);
							String userTmpDelKey2 = ParamStatic.userNorobLogTmpDelKey(userId2,userId1, ruleId, logId);
							String userLogKeyNorob1 = ParamStatic.userLogListKeyTimes(userId1, ruleId);
							String userLogKeyNorob2 = ParamStatic.userLogListKeyTimes(userId2, ruleId);
							Long lrem1 = Redis.shard.lrem(userLogKeyNorob1, 0, userTmpDelKey1);
							Long lrem2 = Redis.shard.lrem(userLogKeyNorob1, 0, userTmpDelKey2);
							Long lrem3 = Redis.shard.lrem(userLogKeyNorob2, 0, userTmpDelKey1);
							Long lrem4 = Redis.shard.lrem(userLogKeyNorob2, 0, userTmpDelKey2);
							logger.info("用户过程规则 禁抢拉黑日志过期 ： lrem1="+lrem1 + " lrem2="+lrem2+ " lrem3="+lrem3+ " lrem4="+lrem4);
							
						} else {//点对点规则key过期，日志过期在__key*__:record|tmp|* 管道只有两种过期日志，除了过程就是点对点
							//__keyspace@0__:record|tmp|u1:userId1|u2:userId2|i1:IMEI1|i2:IMEI2|p1:IP1|p2:IP2|logId(ANSLONG|QID)|ruleId
							//__keyspace@0__:record|tmp|u1:3015cacae30a4295bddf7c598f008cef|u2:a2d0934000e743ddb3e7d3edc9eaa4fe|i1:|i2:|p1:|p2:172.16.1.157|ANSLOG|767a2813d0534eaeaf754f671ff8e4dc|filterRule_point1
							String userId1 = split[2].length()==2?split[2].split(":")[1]:"";
							String userId2 = split[3].length()==2?split[3].split(":")[1]:"";
							String imei1 = split[4].length()==2?split[4].split(":")[1]:"";
							String imei2 = split[5].length()==2?split[5].split(":")[1]:"";
							String ip1 = split[6].length()==2?split[6].split(":")[1]:"";
							String ip2 = split[7].length()==2?split[7].split(":")[1]:"";
							String logId = split[8] + "|" + split[9];
							String ruleId = split[10];
							List<String> pointList = Redis.shard.lrange(ParamStatic.redisFilgerKey_point+"all", 0, -1);//找到所有点对点过滤规则
							PointToPointRule ppr = null; 
							for (String string : pointList) {
								PointToPointRule p = (PointToPointRule)Json.jsonParser(string, PointToPointRule.class);
								if(p.getFilgerRuleId().equals(ruleId)){
									ppr = p;
									break;
								}
							}
							if(ppr==null){
								logger.error("ppr 为空，请检查,ruleId = " + ruleId);
								return;
							}
							String userTmpDelKey = ParamStatic.userTmpDelKey(userId1, userId2,imei1,imei2,ip1,ip2,logId, ruleId);
							if(ParamStatic.POINT_RULE_ACC_YES.equals(ppr.getAccount())){//如果只设置账号匹配
								String userLongKey1 = ParamStatic.userPointLogListKeyAcc(userId1, userId2, ppr);
								String userLongKey2 = ParamStatic.userPointLogListKeyAcc(userId2, userId1, ppr);
								
								//因为tmpdelkey 是随意user1 和 user2拼的key，不分先后，所以删除两遍：分别是u1+u2和u2+u1，这样可以保证删除
								Long lrem1 = Redis.shard.lrem(userLongKey1, 0, userTmpDelKey);
								Long lrem2 = Redis.shard.lrem(userLongKey2, 0, userTmpDelKey);
								logger.info("acc ------ lrem1 = " + lrem1 + " lrem2="+lrem2);
							}
							if(ParamStatic.POINT_RULE_IMEI_YES.equals(ppr.getIMEI())){ // 如果设置了imei匹配
								String userPointLogListKeyIMEI1 = ParamStatic.userPointLogListKeyIMEI(imei1, imei2, ppr);
								String userPointLogListKeyIMEI2 = ParamStatic.userPointLogListKeyIMEI(imei2, imei1, ppr);
								
								Long lrem1 = Redis.shard.lrem(userPointLogListKeyIMEI1, 0, userTmpDelKey);
								Long lrem2 = Redis.shard.lrem(userPointLogListKeyIMEI2, 0, userTmpDelKey);
								logger.info("imei ------ lrem1 = " + lrem1 + " lrem2="+lrem2);
							}
							if(ParamStatic.POINT_RULE_IP_YES.equals(ppr.getIP())){
								String userPointLogListKeyIP1 = ParamStatic.userPointLogListKeyIP(ip1, ip2, ppr);
								String userPointLogListKeyIP2 = ParamStatic.userPointLogListKeyIP(ip1, ip2, ppr);
								
								Long lrem1 = Redis.shard.lrem(userPointLogListKeyIP1, 0, userTmpDelKey);
								Long lrem2 = Redis.shard.lrem(userPointLogListKeyIP2, 0, userTmpDelKey);
								logger.info("ip ------ lrem1 = " + lrem1 + " lrem2="+lrem2);
							}
							
							//logger.info("用户点对点日志过期：userId=" + userId1 + " " + userId2 + "\n logId="+logId +"\n ruleId="+ruleId );
						}
						
					}else if(patternLog[1].equals(ParamStatic.user_nosee_rule_temkey_pix + "*")){//不可见规则过期，删除规则
						String[] split = channel.split("\\|");
						if("norobt".equals(split[3])){//禁抢过期
							norobtOutDate(channel,ParamStatic.user_access_normal);
						}else if("Black".equals(split[3])){//拉黑过期
							norobtOutDate(channel,ParamStatic.user_access_normal);
						}else{//不可见过期
							this.noSeeOutDate(channel);
						}
					}

				}
			} catch (Exception e) {
				logger.error("删除过期key异常",e);
			}
		}
		
		
	}
	
	/**
	 * 禁抢/拉黑 过期
	 * __keyspace@0__:nosee|record|tmp|norobt/Black|userId|ACC(phone)|type(ACC、IMEI、IP)|typeValue(ACC的值或者IMEI的值或者IP的值)
	 * @param channel
	 * 
	 */
	public void norobtOutDate(String channel,int status){
		logger.info("禁抢规则过期");
		String[] split = channel.split("\\|");
		String userId = split[4];
		String phone = split[5];
		String type = split[6];
		String typeValue = split[7];
		String robitOrBlack = split[3];
		Map<String, String> findUserInfo = this.findUserInfo(userId);
		String oldAccess = findUserInfo.get("oldAccess");
		long norobitEndDate = Long.parseLong(findUserInfo.get("norobitEndDate"));
		logger.info("是哪个情况过期= = = " + robitOrBlack);
		if("norobt".equals(robitOrBlack) && oldAccess.equals("5")){//禁抢过期
			logger.info("如果禁抢过期且原来是禁抢状态，则说明没有被其他规则修改 没有没手动修改过，所以直接恢复为正常状态");
			//如果禁抢过期且原来是禁抢状态，则说明没有被其他规则修改 没有没手动修改过，所以直接恢复为正常状态
			changeAccessStatus(userId, phone, status);
		}else if("Black".equals(robitOrBlack)){//拉黑过期
			logger.info("如果是拉黑状态过期，则查看是否有未过期的禁抢规则，有的话恢复为禁抢，没有的话恢复为正常状态");
			//如果是拉黑状态过期，则查看是否有未过期的禁抢规则，有的话恢复为禁抢，没有的话恢复为正常状态
			Calendar cal = Calendar.getInstance();
			Calendar norobitCal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			norobitCal.setTimeInMillis(norobitEndDate);
			if(cal.after(norobitCal)){//禁抢过期直接恢复到正常
				logger.info("拉黑过期，恢复到正常状态");
				changeAccessStatus(userId, phone, 0);
			}else{//恢复到禁抢
				logger.info("拉黑过期，恢复到禁抢状态");
				changeAccessStatus(userId, phone, 5);
			}
		}
		
//		if(ParamStatic.REDIS_NOSEE_IMEI.equals(type)){//IMEI禁抢过期
//			logger.info("用户  IMEI禁抢过期 ：" + userId +" 的状态 ：过期，解除特殊状态，回复为普通状态0" );
//			Redis.shard.hdel(RuleFilterService.IMEIAccessHash, typeValue, typeValue+"|endDate");
//		}
//		if(ParamStatic.REDIS_NOSEE_IP.equals(type)){//IP禁抢过期
//			logger.info("用户  IP禁抢过期 ：" + userId +" 的状态 ：过期，解除特殊状态，回复为普通状态0" );
//			Redis.shard.hdel(RuleFilterService.IPAccessHash, typeValue, typeValue+"|endDate");
//		}
	}
	
	/**
	 * 查询用户信息
	 * @param userId
	 * @return
	 */
	public Map<String, String> findUserInfo(String userId){
		Connection connection = JDBCUtils.getConnection();
		PreparedStatement pst = null;
		Map<String, String> user = new java.util.HashMap<String, String>();
		String sql = " select * from circle.user where id =  '" + userId +"' ";
		try {
			pst = connection.prepareStatement(sql);
			ResultSet rs = pst.executeQuery(sql);
			while(rs.next()){
				user.put("id", userId);
				user.put("blackTimes", String.valueOf(rs.getInt("BLACKSTATUSTIMES")));
				user.put("phoneNum", rs.getString("MOBILE"));
				user.put("norobitEndDate", String.valueOf(rs.getLong("NOROBITENDDATE")));//禁抢过期时间
				user.put("blackEndDate", String.valueOf(rs.getLong("BLACKENDDATE")));//拉黑过期时间
				user.put("oldAccess", String.valueOf(rs.getInt("ACCESS")));
				
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return user;
	}
	
	/**
	 * 更新用户权限状态
	 * @param userId
	 * @param phone
	 * @param status
	 */
	public void changeAccessStatus(String userId,String phone,int status){
		logger.info("修改用户的access的状态  userId="+userId+"  phone="+phone+ "  status="+status);
		//更新hbase
		CHbase bean = CHbase.bean();
		Put update = new Put(Bytes.toBytes(userId));
		update.addColumn(Bytes.toBytes("0"), Bytes.toBytes("ACCESS"), PInteger.INSTANCE.toBytes(status)); //状态
		try {
			bean.put("CIRCLE.USER", update);
		} catch (IOException e) {
			logger.error("更新hbase出错：",e);
		}
		
		//更新redis中的字段
		String key  = CircleMD5.encodeSha1(phone);
		logger.info("user redis hash key = = " + key);
		Redis.shard.hset(key, "access", String.valueOf(status));
		
		//更新elastic
		CElastic elastic = CElastic.elastic();
		GetResponse userResponse = elastic.get("ELC_TAB_REALUSER", userId);
		String sourceAsString = userResponse.getSourceAsString();
		UserELK userELK = (UserELK)Json.jsonParser(sourceAsString, UserELK.class);
		userELK.setAccess(status);
		elastic.index("ELC_TAB_REALUSER", userId, userELK); 
	}
	
	/***
	 * 不可见规则过期
	 * __keyspace@0__:nosee|record|tmp|userId1|userId2|type|typeValue1|typeValue2
	 */
	private void noSeeOutDate(String channel){
		logger.info("不可见规则过期");
		String[] split = channel.split("\\|");
		if(split.length != 8){
			logger.error("接收到的不可见key格式错误 ：channel= "+channel);
			return;
		}
		String userId1 = split[3];
		String userId2 = split[4];
		String type = split[5];
		String typeValue1 = split[6];
		String typeValue2 = split[7];
		
		String noSeeValue1 = Redis.shard.get(ParamStatic.noSeeRuleKey(typeValue1));
		String noSeeValue2 = Redis.shard.get(ParamStatic.noSeeRuleKey(typeValue2));
		if(noSeeValue1.contains(typeValue2)){
			noSeeValue1=noSeeValue1.replaceAll(typeValue2, "");
			noSeeValue1=noSeeValue1.replaceAll("(\\|)+", "|");
			Redis.shard.set(ParamStatic.noSeeRuleKey(typeValue1),noSeeValue1);
		}
		if(noSeeValue2.contains(typeValue1)){
			noSeeValue2 = noSeeValue2.replaceAll(typeValue1, "");
			noSeeValue2 = noSeeValue2.replaceAll("(\\|)+", "|");
			Redis.shard.set(ParamStatic.noSeeRuleKey(typeValue2), noSeeValue2);
		}
		
		
//		Map<String, String> noSeeMap1 = Redis.shard.hgetAll(ParamStatic.noSeeRuleKey(userId1));
//		if(noSeeMap1 != null && noSeeMap1.size()>0){
//			String rules1 = noSeeMap1.get(type);
//			rules1 = rules1.replaceAll(typeValue1 , "");//把过期的替换掉
//			rules1 = rules1.replaceAll("(\\|)+", "|");//替换掉val后会出现||的情形，把一个或者多个的|替换为一个|
//			noSeeMap1.put(type, rules1);
//			
//			for (String key : noSeeMap1.keySet()) {//删除之后再存入redis
//				Redis.shard.hset(ParamStatic.noSeeRuleKey(userId1), key, noSeeMap1.get(key));
//			}
//		}
//		
//		Map<String, String> noSeeMap2 = Redis.shard.hgetAll(ParamStatic.noSeeRuleKey(userId2));
//		if(noSeeMap2 != null && noSeeMap2.size()>0){
//			String rules = noSeeMap2.get(type);
//			rules = rules.replaceAll(typeValue2 , "");//把过期的替换掉
//			rules = rules.replaceAll("(\\|)+", "|");//替换掉val后会出现||的情形，把一个或者多个的|替换为一个|
//			noSeeMap2.put(type, rules);
//			
//			for (String key : noSeeMap2.keySet()) {//删除之后再存入redis
//				Redis.shard.hset(ParamStatic.noSeeRuleKey(userId2), key, noSeeMap2.get(key));
//			}
//		}
		logger.info("用户点对点不可见规则过期 userId = " + userId1 + " " + userId2);
	}
	

	
}
