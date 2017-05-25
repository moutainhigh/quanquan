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
import com.rulesfilter.yy.zj.model.FilterRule;
import com.rulesfilter.yy.zj.model.ProcessFilterRule;
import com.rulesfilter.yy.zj.model.ProcessRule;
import com.rulesfilter.yy.zj.service.RuleFilterService;
import com.rulesfilter.yy.zj.utils.ParamStatic;

/**
 * 
 * 这个线程处理面向过程规则
 * 
 * @author zhoujia
 *
 * @date 2015年8月12日
 */
public class FilterThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(FilterThread.class);
	
	private FilterRule pfr;
	
	private String logJson;
	
	RuleFilterService service = new RuleFilterService();
	
	public FilterThread(FilterRule pfr, String logJson) {
		this.pfr = pfr;
		this.logJson = logJson;
	}
	
	@Override
	public void run() {
		logger.info("------------------进入过程规则处理线程-----------------");
		AnsLog log = (AnsLog)Json.jsonParser(logJson, AnsLog.class);
		//String logInfoKey = userLongKey+"detail";
		//根据规则判断是否符合刷题规则
		logger.info("------是否违反过程规则 ： " + isIllquestion(log, pfr) + "-------------------");
		if(isIllquestion(log, pfr)){
			String logId = log.getLogId();
			String filgerRuleId = pfr.getFilgerRuleId();
			String ruleSign = null;
			CHbase bean = CHbase.bean();
			if(ParamStatic.RULE_FILTER_FORBIDROB.equals(pfr.getStatus()) 
					||ParamStatic.RULE_FILTER_FORBIDALL.equals(pfr.getStatus())){//如果禁抢和拉黑操作，是按照次数判断
				logger.info("这个规则的status是  禁抢或者拉黑=="+pfr.getFilgerRuleId());
				String userLogKeyNorob = ParamStatic.userLogListKeyTimes(log.getUserId(), pfr.getFilgerRuleId());
				String userLogKeyNorob1 = ParamStatic.userLogListKeyTimes(log.getQueUserId(), pfr.getFilgerRuleId());
				String userTmpDelKey = ParamStatic.userNorobLogTmpDelKey(log.getUserId(), log.getQueUserId(),pfr.getFilgerRuleId(), log.getLogId());
				Redis.CONNECT.setex(userTmpDelKey , (int)(pfr.getEveryDay()*24*3600), log.getUserId());
				//删除的时候要删除两遍，因为userId和quesUserID不一样
				Redis.shard.lpush(userLogKeyNorob, userTmpDelKey);
				Redis.shard.lpush(userLogKeyNorob1, userTmpDelKey);
				List<String> ruleCount = Redis.shard.lrange(userLogKeyNorob, 0, -1);
				if(pfr.getTimes() <= ruleCount.size()){//触发最低次数限制
					log.setHit(ParamStatic.RULE_HIT_YES);//被命中，添加命中标记
					switch (pfr.getStatus()) {
					case ParamStatic.RULE_FILTER_FORBIDROB://禁抢
						logger.info("过程规则 :禁抢==============================" + ParamStatic.RULE_FILTER_FORBIDROB  + "记录列表长度=" + ruleCount.size());
						ruleSign = CircleMD5.encodeSha1(logId + filgerRuleId +ParamStatic.RULE_FILTER_FORBIDROB) ;
						for (String string : ruleCount) { //如果此条被命中，则这个队列中的全部条目都是刷单记录，全部更新
							//record|tmp|norob|userId|quesUserId|logId(ANSLOG|QID)|ruleId
							logger.info("面向过程  被禁抢 - 循环更新 ---- 被命中记录 ===" + string);
							String[] split = string.split("\\|");
							String logIdStr = split[5] + "|" + split[6];
							this.updateHbaseTable(bean, logIdStr,AnsLog.isHit,ParamStatic.RULE_FILTER_FORBIDROB,ruleSign);
						}
						try { // 禁抢用户
							if(ParamStatic.NOSEE_RULE_YES.equals(pfr.getAccount())){//acc禁抢
								service.updateUserAccessStatus(log.getUserId(), ParamStatic.user_access_norobt, pfr.getLongDays());
								//设置acc禁抢计时key
								String userNoRobtTmpDelKey = ParamStatic.userNoRobtTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_ACC,log.getAnswerAccount());
								Redis.CONNECT.setex(userNoRobtTmpDelKey , (int)(pfr.getLongDays()*24*3600), "1");
							}
							if(ParamStatic.NOSEE_RULE_YES.equals(pfr.getIMEI())){//imei禁抢
								service.addIEMIToAccessList(log.getAnswerIMEI(), String.valueOf(ParamStatic.user_access_norobt), pfr.getLongDays());
								//设置IMEI禁抢计时key
//								String userNoRobtTmpDelKey = ParamStatic.userNoRobtTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_IMEI,log.getAnswerIMEI());
//								Redis.CONNECT.setex(userNoRobtTmpDelKey , (int)(pfr.getLongDays()*24*3600), "1");
							}
							if(ParamStatic.NOSEE_RULE_YES.equals(pfr.getIP())){//ip禁抢  
								service.addIPToAccessList(log.getAnswerIP(), String.valueOf(ParamStatic.user_access_norobt), pfr.getLongDays());
								//设置IP禁抢计时key
//								String userNoRobtTmpDelKey = ParamStatic.userNoRobtTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_IP,log.getAnswerIP());
//								Redis.CONNECT.setex(userNoRobtTmpDelKey , (int)(pfr.getLongDays()*24*3600), "1");
							}
						} catch (TException e) {
							logger.error("禁抢异常，请检查",e);
						}

						break;
					case ParamStatic.RULE_FILTER_FORBIDALL://拉黑
						logger.info("过程规则 :拉黑==============================" + ParamStatic.RULE_FILTER_FORBIDALL  + "记录列表长度=" + ruleCount.size());
						ruleSign = CircleMD5.encodeSha1(logId + filgerRuleId +ParamStatic.RULE_FILTER_FORBIDALL) ;
						for (String string : ruleCount) { //如果此条被命中，则这个队列中的全部条目都是刷单记录，全部更新
//							AnsLog logs = (AnsLog)Json.jsonParser(string, AnsLog.class);
//							logs.setRuleId(pfr.getFilgerRuleId());
//							logs.setHit(ParamStatic.RULE_HIT_YES);//被命中，添加命中标记
							logger.info("面向过程  被拉黑 - 循环更新 ---- 被命中记录 ===" + string);
							String[] split = string.split("\\|");
							String logIdStr = split[5] + "|" + split[6];
							this.updateHbaseTable(bean, logIdStr, AnsLog.isHit ,ParamStatic.RULE_FILTER_FORBIDALL,ruleSign);
						}
						try { // 拉黑用户
							if(ParamStatic.NOSEE_RULE_YES.equals(pfr.getAccount())){//acc拉黑
								service.updateUserAccessStatus(log.getUserId(), ParamStatic.user_access_blackList, (int)pfr.getLongDays().doubleValue());
								//设置拉黑过期计时key
//								String userBlackTmpDelKey = ParamStatic.userBlackTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_ACC,log.getAnswerAccount());
//								Redis.CONNECT.setex(userBlackTmpDelKey , (int)(pfr.getLongDays()*24*3600), "1");
							}
							if(ParamStatic.NOSEE_RULE_YES.equals(pfr.getIMEI())){//imei拉黑
								service.addIEMIToAccessList(log.getAnswerIMEI(), String.valueOf(ParamStatic.user_access_blackList), (int)pfr.getLongDays().doubleValue());
								//设置拉黑过期计时key
//								String userBlackTmpDelKey = ParamStatic.userBlackTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_IMEI,log.getAnswerIMEI());
//								Redis.CONNECT.setex(userBlackTmpDelKey , (int)(pfr.getLongDays()*24*3600), "1");
							}
							if(ParamStatic.NOSEE_RULE_YES.equals(pfr.getIP())){//ip拉黑     ip暂时先不开放
								service.addIPToAccessList(log.getAnswerIP(), String.valueOf(ParamStatic.user_access_blackList), (int)pfr.getLongDays().doubleValue());
								//设置拉黑过期计时key
//								String userBlackTmpDelKey = ParamStatic.userBlackTmpDelKey(log.getUserId(),log.getAnswerAccount(),ParamStatic.REDIS_NOSEE_IP,log.getAnswerIP());
//								Redis.CONNECT.setex(userBlackTmpDelKey , (int)(pfr.getLongDays()*24*3600), "1");
							}
							
						} catch (TException e) {
							logger.error("拉黑异常，请检查",e);
						}
						
						break;
					}
				}
				
			}else if(ParamStatic.RULE_FILTER_NOSEE.equals(pfr.getStatus())){//如果是不可见，则按照双方关系出现的次数进行判断
				logger.info("这个规则的status是不可见=="+pfr.getFilgerRuleId());
				String userLongKey1 = ParamStatic.userLogListKey(log.getUserId(),log.getQueUserId(), pfr.getFilgerRuleId());
				String userLongKey2 = ParamStatic.userLogListKey(log.getQueUserId(),log.getUserId(), pfr.getFilgerRuleId());
				//这个日志存在多长时间，在redis中设置一个ttl为pfr.getEveryDay()*24*3600 秒的 kv，如果可过期，则key事件通知会收到 事件通知，删除所在队列的信息
				String userTmpDelKey = ParamStatic.userProcessTmpDelKey(log.getUserId(),log.getQueUserId(), log.getLogId(), pfr.getFilgerRuleId());
				logger.info("key失效临时key存入reids key = = = =  = " + userTmpDelKey);
				Redis.CONNECT.setex(userTmpDelKey , (int)(pfr.getEveryDay()*24*3600), log.getUserId());
				
				Redis.shard.lpush(userLongKey1, userTmpDelKey);
				Redis.shard.lpush(userLongKey2, userTmpDelKey);
				//直接存json是因为，这些日志需要更新，如果一组记录被命中，需要修改一组记录的命中
				List<String> ruleCount = Redis.shard.lrange(userLongKey1, 0, -1);
				
				if(pfr.getTimes() <= ruleCount.size()){//触发最低次数限制，触发规则，按照规则的处理状态（仅记录、不可见、禁抢、拉黑）处理
					logger.info("---------触发面向过程过滤规则，被面向过程规则命中，过程id = " + pfr.getFilgerRuleId());
					log.setHit(ParamStatic.RULE_HIT_YES);//被命中，添加命中标记
					logger.info("过程规则 : 不可见=-=============================" + ParamStatic.RULE_FILTER_NOSEE  + "记录列表长度=" + ruleCount.size());
					ruleSign = CircleMD5.encodeSha1(logId + filgerRuleId +ParamStatic.RULE_FILTER_NOSEE) ;
					try {
						for (String string : ruleCount) { //如果此条被命中，则这个队列中的全部条目都是刷单记录，全部更新 process.record|tmp|4dbdb35c899b4a8482530fc52022bad1|2b78bf94e95b4d7eb6ba7edcab9c4d47|ANSLOG|7f31a495d23141dc99b7ea227d71c66e|filterRule_process1
							logger.info("面向过程  不可见被命中 - 循环更新 ---- 被命中记录 ===" + string);
							String[] split = string.split("\\|");
							String logIdStr = split[4] + "|" + split[5];
							this.updateHbaseTable(bean, logIdStr, AnsLog.isHit ,ParamStatic.RULE_FILTER_NOSEE,ruleSign);
							logger.info("面向过程 - hbase更新成功");
						}
					} catch (Exception e) {
						logger.error("更新数据异常",e);
					}
					//处理关系不可见
					service.relationNoSee(log, pfr);
				}
			}
			
			
		}else{
			logger.info("--不违反规则，结束线程--");
		}
	
		
		
		
		
		
		
	}
	
	/***
	 * 判断是否符合刷题规则
	 * @param log
	 * @param pfr
	 * @return
	 */
	public boolean isIllquestion(AnsLog log , FilterRule pfr){
		String name2 = pfr.getClass().getName();
		if(name2.endsWith("ProcessFilterRule")){
			ProcessFilterRule pfRule = (ProcessFilterRule)pfr;
			String prRuleKey = pfRule.getPrRuleKey();
			String string = Redis.shard.get(prRuleKey);
			ProcessRule processRule = (ProcessRule)Json.jsonParser(string, ProcessRule.class);
			logger.info("规则定义的最长答题时间："+processRule.getRuleAnswerTime() + "   用户答题时间："+log.getAnsTime());
			logger.info("规则定义的最长筛选时间："+processRule.getRuleSelectTime() + "   用户筛选时间："+log.getSelectTime());
			
			if(processRule.getRuleAnswerTime() != null && processRule.getRuleAnswerTime() > Math.abs(log.getAnsTime())){
				logger.info("答题时间小于指定时间，加入命中列表  答题时间："+ log.getAnsTime());
				return true;
			}
			if(processRule.getRuleContentNum()!=null && processRule.getRuleContentNum() > log.getRuleContentNum()){
				logger.info("回答字数小于指定数量，加入命中列表");
				return true;
			}
			if(processRule.getRuleSelectTime()!=null && processRule.getRuleSelectTime() > Math.abs(log.getSelectTime())){
				logger.info("筛选时间小于指定时间，加入命中列表    筛选时间：" + log.getSelectTime());
				return true;
			}
		}
		return false;
	}
	
	
	//此方法可能需要加锁
	/***
	 * 
	 *
	 * @param resultStatus  处理的状态，仅记录，不可见，禁抢，拉黑
	 */
	private void updateHbaseTable(CHbase bean, String logId,String hit, String resultStatus,String rulesSign){
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
			String rulesNameStr = oldRulesName+","+pfr.getFilgerRuleId();
			if(rulesNameStr.startsWith(",")){
				rulesNameStr = rulesNameStr.substring(1);
			}
			
			Put put = new Put(Bytes.toBytes(logId));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("userId".toUpperCase()),Bytes.toBytes(log.getUserId()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("time".toUpperCase()),PLong.INSTANCE.toBytes(log.getTime()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerPhone".toUpperCase()),Bytes.toBytes(log.getAnswerPhone()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerIMEI".toUpperCase()),Bytes.toBytes(log.getAnswerIMEI()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("questionIMEI".toUpperCase()),Bytes.toBytes(log.getQuestionIMEI()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerIP".toUpperCase()),Bytes.toBytes(log.getAnswerIP()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("questionIP".toUpperCase()),Bytes.toBytes(log.getQuestionIP()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("questionPhone".toUpperCase()),Bytes.toBytes(log.getQuestionPhone()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("qid".toUpperCase()),Bytes.toBytes(log.getQid()));
//			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("cash".toUpperCase()),PDecimal.INSTANCE.toBytes(new BigDecimal(log.getCash())));
			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("hit".toUpperCase()),Bytes.toBytes(hit));//是否被命中
			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("resultStatus".toUpperCase()),Bytes.toBytes(resultStatus));//处理状态
			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("rulesSign".toUpperCase()),Bytes.toBytes(rulesSign));//同组流水记录标记
			put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("rulesName".toUpperCase()),Bytes.toBytes(rulesNameStr));//标记被那个rule命中
			
			resultMap.put("R"+(resultMap.size() + 1), pfr.getFilgerRuleId() );
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
