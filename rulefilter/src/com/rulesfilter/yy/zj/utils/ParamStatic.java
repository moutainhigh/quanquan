package com.rulesfilter.yy.zj.utils;

import com.rulesfilter.yy.zj.model.PointToPointRule;

/**
 * @author zhoujia
 *
 * @date 2015-8-11
 */
public class ParamStatic {
	
	public static final String log4jConfigFile = "log4j.properties";
	
	public static final String configFileName = "conf.properties";
	
	public static String configPath = "config";
	
	/**仅记录**/
	public static final String RULE_FILTER_RECORD = "1";
	
	/**不可见**/
	public static final String RULE_FILTER_NOSEE = "2";
	
	/**禁抢**/
	public static final String RULE_FILTER_FORBIDROB = "3";
	
	/**拉黑**/
	public static final String RULE_FILTER_FORBIDALL = "4";
	
	/**规则命中**/
	public static final String RULE_HIT_YES = "0";
	/**规则为命中**/
	public static final String RULE_HIT_NO = "1";
	
	
	public static final String Hbase_Question_Name = "CIRCLE.QUESTION";
	
	public static final String Hbase_User_Name = "CIRCLE.USER";
	
	//过程规则前缀
	public static String redisFilgerKey_process = "FILTERPROCESS|";
	
	//点对点规则前缀
	public static String redisFilgerKey_point = "FILTERPOINT|";
	
	
	//过程规则对象
	public static String filterRule = "filterRule";
	
	//点对点规则对象
	public static String pointRule = "pointRule";
	
	/**管道**/
	public static String filterChannel = "questionLogFilter";
	
	/** 0 正常用户 , 1 白名单用户 , 3 黑名单用户 ,意为:是否能进入本系统 */
	public static final int user_access_normal = 0;
	public static final int user_access_whiteList = 1;
	public static final int user_access_blackList = 3;
	public static final int user_access_norobt = 5;
	
	
	/**记录这条日志存在时间的key的前缀，key事件监听会监控这个前缀的key，如果这个被删除，则在回调中删除用户对应规则中的日志**/
	public static String tmpkey_pix = "record|tmp|";
	
	public static String user_nosee_rule_temkey_pix = "nosee|record|tmp|";
	
	/**日志 - key过期管道**/
	public static String key_timeout_channel = "__key*__:"+tmpkey_pix+"*";
	
	/**不可见规则 - key过期管道**/
	public static String key_timeout_channel_nosee = "__key*__:"+user_nosee_rule_temkey_pix+"*";
	
	/****用户不可见条件***/
	public static String REDIS_NOSEE = "NOSEE|";
	/**上面对应的redis中的hash的field**/
	public static String REDIS_NOSEE_ACC = "ACC";
	public static String REDIS_NOSEE_IMEI = "IMEI";
	public static String REDIS_NOSEE_IP = "IP";
	
	public static String NOSEE_RULE_YES = "1";
	public static String NOSEE_RULE_NO = "0";
	
	/**点对点规则勾选账号**/
	public static String POINT_RULE_ACC_YES = "1";
	
	/**点对点规则不勾选账号**/
	public static String POINT_RULE_ACC_NO = "0";
	
	/**点对点规则勾选IMEI**/
	public static String POINT_RULE_IMEI_YES = "1";
	
	/**点对点规则不勾选IMEI**/
	public static String POINT_RULE_IMEI_NO = "0";
	
	/**点对点规则勾选IP**/
	public static String POINT_RULE_IP_YES = "1";
	/**点对点规则不勾选IP**/
	public static String POINT_RULE_IP_NO = "0";
	
	/**管道名**/
	public static String ADD_NEW_CHANNEL = "ADD_NEW_CHANNEL|NEWRULE";
	
	public static final String POINT_RULE_YES = "yes";
	public static final String POINT_RULE_NO = "no";

	/**
	 * 按照次数计数的日志队列，适用于过程规则中的禁抢和拉黑
	 * 计算禁抢和拉黑的队列的key
	 * FILTERPROCESS|norob|userId|ruleId
	 * @param userId
	 * @param ruleId
	 * @return
	 */
	public static String userLogListKeyTimes(String userId, String ruleId){
		StringBuffer key = new StringBuffer();
		key.append(ParamStatic.redisFilgerKey_process).append("norob|").append(userId).append("|").append(ruleId);
		return key.toString();
	}
	
	/***
	 * 面向过程规则的禁抢和拉黑的日志的计时key，key到期则去面向过程的禁抢和拉黑日志队列中删除对应的记录
	 * record|tmp|norob|userId|quesUserId|logId(ANSLOG|QID)|ruleId
	 * @param userId
	 * @param quesUserId
	 * @param ruleId
	 * @param logId
	 * @return
	 */
	public static String userNorobLogTmpDelKey(String userId,String quesUserId, String ruleId,String logId){
		StringBuffer key = new StringBuffer();
		key.append(ParamStatic.tmpkey_pix).append("norob|")
		.append(userId).append("|").append(quesUserId)
		.append("|").append(logId).append("|").append(ruleId);
		return key.toString();
	}
	
	
	/***
	 * 计算用户面向规则的 日志队列的key
	 * 格式：FILTERPROCESS|userId1|userId2|ruleId
	 * @param userId
	 * @param ruleId
	 * @return
	 * 
	 */
	public static String userLogListKey(String userId1,String userId2,String ruleId){
		StringBuffer key = new StringBuffer();
		key.append(ParamStatic.redisFilgerKey_process).append(userId1).append("|").append(userId2).append("|").append(ruleId);
		return key.toString();
	}
	
	
	/***
	 * 计算点对点规则队列的key ，这个队列保存符合规则的日志，队列长度就是规则的检查点
	 * 格式 ： ACC|userId1|userId2|ruleId
	 * @param userId1
	 * @param userId2
	 * @param pr
	 * @return
	 */
	public static String userPointLogListKeyAcc(String userId1,String userId2,PointToPointRule pr){
		StringBuffer key = new StringBuffer();
		key.append("ACC|");
		key.append(userId1).append("|").append(userId2).append("|").append(pr.getFilgerRuleId());
		return key.toString();
	}
	/***
	 * 计算点对点规则队列的key ，这个队列保存符合规则的日志，队列长度就是规则的检查点
	 * 格式 ： IMEI|IEMI1|IEMI2|ruleId
	 * @param IMEI1
	 * @param IEMI2
	 * @param pr
	 * @return
	 */
	public static String userPointLogListKeyIMEI(String IMEI1,String IEMI2,PointToPointRule pr){
		StringBuffer key = new StringBuffer();
		key.append("IMEI|");
		key.append(IMEI1).append("|").append(IEMI2).append("|").append(pr.getFilgerRuleId());
		return key.toString();
	}
	/***
	 * 计算点对点规则队列的key ，这个队列保存符合规则的日志，队列长度就是规则的检查点
	 * 格式 ： IP|userId1|userId2|ruleId
	 * @param IP1
	 * @param IP2
	 * @param pr
	 * @return
	 */
	public static String userPointLogListKeyIP(String IP1,String IP2,PointToPointRule pr){
		StringBuffer key = new StringBuffer();
		key.append("IP|");
		key.append(IP1).append("|").append(IP2).append("|").append(pr.getFilgerRuleId());
		return key.toString();
	}
	
	
	/**
	 * 计算用户面向规则的临时计时key
	 * 格式 process.record|tmp|userId1|userId2|logId(ANSLONG|QID)|ruleId
	 * @param userId
	 * @param logId
	 * @param ruleId
	 * @return
	 */
	public static String userProcessTmpDelKey(String userId1,String userId2,String logId,String ruleId){
		StringBuffer key = new StringBuffer();
		key.append(ParamStatic.tmpkey_pix).append("process|").append(userId1).append("|").append(userId2)
		.append("|").append(logId).append("|").append(ruleId);
		return key.toString();
	}
	
	/**
	 * 计算用户点对点规则的临时key , 拼key是都是回答者在前，提问者在后， 例如 回答者ID|提问者ID
	 * 格式 record|tmp|u1:userId1|u2:userId2|i1:IMEI1|i2:IMEI2|p1:IP1|p2:IP2|logId(ANSLONG|QID)|ruleId
	 * @param userId1
	 * @param userId2
	 * @param logId
	 * @param ruleId
	 * @return
	 */
	public static String userTmpDelKey(String userId1,String userId2,String IMEI1,String IMEI2,String IP1, String IP2,String logId,String ruleId){
		StringBuffer key = new StringBuffer();
		key.append(ParamStatic.tmpkey_pix)
		.append("u1:"+userId1).append("|").append("u2:"+userId2).append("|")
		.append("i1:"+IMEI1).append("|").append("i2:"+IMEI2).append("|")
		.append("p1:"+IP1).append("|").append("p2:"+IP2).append("|")
		.append(logId).append("|").append(ruleId);
		return key.toString();
	}
	
	/**
	 * 计算用户不可见规则的key
	 * @param userId
	 * @return
	 */
	public static String noSeeRuleKey(String userId){
		
		return REDIS_NOSEE + userId;
	}
	
//	/**
//	 * 计算 保存用户不可见规则时间的key ,当到期的时候删除不可见规则，用户可以重新可见
//	 * (面向过程)
//	 * @param userId
//	 * @param type
//	 * @param typeValue
//	 * @return
//	 */
//	public static String userRuleTmpDelKey(String userId,String type,String typeValue){
//		StringBuffer key = new StringBuffer();
//		key.append(user_nosee_rule_temkey_pix).append(userId).append("|").append(type).append("|").append(typeValue);
//		return key.toString();
//	}
	
	
	/**
	 * 计算 保存用户不可见规则时间的key ,当到期的时候删除不可见规则，用户可以重新可见
	 * <br>
	 * 						-----------------------
	 * 						↑                     ↓
	 * nosee|record|tmp|userId1|userId2|type|typeValue1|typeValue2
	 *                              ↓                     ↑
	 *                              -----------------------
	 * 如上图所示，user1 不可见 typeValue1  ，user2不可见typeValue2
	 * @param userId               
	 * @param type
	 * @param typeValue
	 * @return
	 */
	public static String userRuleTmpDelKey(String userId1,String userId2,String type,String typeValue1,String typeValue2){
		StringBuffer key = new StringBuffer();
		key.append(user_nosee_rule_temkey_pix).append(userId1).append("|").append(userId2).append("|").append(type).append("|").append(typeValue1).append("|").append(typeValue2);
		return key.toString();
	}
	
	/**
	 * nosee|record|tmp|norobt|userId|ACC(phone)|type(ACC、IMEI、IP)|typeValue(ACC的值或者IMEI的值或者IP的值)
	 * 禁抢临时key，计时
	 * @return
	 */
	public static String userNoRobtTmpDelKey(String userId,String phone,String type,String typeValue){
		StringBuffer key = new StringBuffer();
		key.append(user_nosee_rule_temkey_pix).append("norobt").append("|")
		.append(userId).append("|").append(phone).append("|").append(type)
		.append("|").append(typeValue);
		return key.toString();
	}
	/**
	 * nosee|record|tmp|Black|userId|ACC(phone)|type(ACC、IMEI、IP)|typeValue
	 * 拉黑临时key，计时
	 * @return
	 */
	public static String userBlackTmpDelKey(String userId,String phone,String type,String typeValue){
		StringBuffer key = new StringBuffer();
		key.append(user_nosee_rule_temkey_pix).append("Black").append("|")
		.append(userId).append("|").append(phone).append("|").append(type)
		.append("|").append(typeValue);
		return key.toString();
	}
	
	
	public static String U1AndU2(String user1,String user2) throws Exception{
		if(user1.equals(user2)){
			throw new Exception("user1 和 user2 不能相等");
		}
		String code = null;
		char[] charArray1 = user1.toCharArray();
		char[] charArray2 = user2.toCharArray();
		for (int i = 0; i < charArray2.length; i++) {
			if(charArray1[i]==charArray2[i]){
				continue;
			}else if(charArray1[i]<charArray2[i]){
				code = user1+user2;
				break;
			}else if(charArray1[i]>charArray2[i]){
				code = user2+user1;
				break;
			}
		}
		return code;
	}
	
	public static String stringToAscii(String value) {
		StringBuffer sbu = new StringBuffer();
		char[] chars = value.toCharArray(); 
		for (int i = 0; i < chars.length; i++) {
			if(i != chars.length - 1)
			{
				sbu.append((int)chars[i]).append(",");
			}
			else {
				sbu.append((int)chars[i]);
			}
		}
		return sbu.toString();
	}
	
}
