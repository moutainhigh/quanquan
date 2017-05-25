package com.rulesfilter.yy.zj.utils;

import java.io.IOException;

import com.rulesfilter.yy.zj.service.RuleFilterService;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.rulesfilter.yy.zj.model.AnsLog;
import com.rulesfilter.yy.zj.model.Question;
import com.rulesfilter.yy.zj.model.QuestionLog;

/**
 * @author zhoujia
 *
 * @date 2015年9月6日
 */
public class PareseQLogToAnsLog {
	
	private static Logger logger = LoggerFactory.getLogger(PareseQLogToAnsLog.class);

	private static String logIdPefix = "ANSLOG|";
	
	public static String paster(String questionLogStr,String ruleID){
		if(questionLogStr == null || "".equals(questionLogStr)){
			logger.error("解析字符串  questionLogStr 为空 questionLogStr=" + questionLogStr);
			return null;
		}
		
		QuestionLog questionLog = null;
		try {
			questionLog = (QuestionLog)Json.jsonParser(questionLogStr, QuestionLog.class);
			if(questionLog == null){
				logger.error("questionLog 为空 ==" + questionLog);
			}else{
				RuleFilterService service = new RuleFilterService();
				long userSelectBeginTime = service.getUserSelectBeginTime(questionLog.getQuestionId());
				//在这里查询一下用户进入筛选阶段的时间
				questionLog.setBeginSlectTime(userSelectBeginTime);
			}

		} catch (Exception e1) {
			logger.error("QuestionLog json 解析出错",e1);
		}
		Question question = null;
		try {
			logger.info("paster  qid ====== " + questionLog.getQuestionId());
			Get get = new Get(Bytes.toBytes(questionLog.getQuestionId()));
			Result result = CHbase.bean().get(ParamStatic.Hbase_Question_Name, get);
			question = new Question();
			question = question.create(result);
			question.setQid(questionLog.getQuestionId());
			
		} catch (IOException e) {
			logger.error("hbase 查询问题异常 ", e);
		}
		logger.info("===============userId = " + question.getAuid() + "===================quesUserId ====" + question.getQuid());

		try{
			AnsLog ansLog = new AnsLog();
			ansLog.setLogId(logIdPefix + questionLog.getQuestionId());
			ansLog.setUserId(question.getAuid()); // 如果这个字段为空，说明没有答题人，这个题目没有人回答
			ansLog.setQueUserId(question.getQuid());
			ansLog.setTime(question.getCdate());
			ansLog.setAnswerPhone(question.getAphone());
			ansLog.setQuestionPhone(question.getQphone());
			ansLog.setQuestionAccount(question.getQphone());
			ansLog.setQuestionIMEI(question.getPkey());
			ansLog.setQuestionIP(question.getIp());
			ansLog.setAnswerAccount(question.getAphone());
			ansLog.setAnswerIMEI(question.getPkey2());
			ansLog.setAnswerIP(question.getIp2());
			ansLog.setHit(ParamStatic.RULE_HIT_NO);
			ansLog.setCash(question.getCash().toEngineeringString());
			ansLog.setQid(questionLog.getQuestionId());
			ansLog.setAnsTime((int)(((questionLog.getEndAnswoerTime()==null?0:questionLog.getEndAnswoerTime())-(questionLog.getBeginAnswerTime()==null?0:questionLog.getBeginAnswerTime()) )/1000));//秒
			ansLog.setRuleContentNum(0);//回答字符数
			ansLog.setSelectTime((int)(((questionLog.getEndSlectTIme()==null?0:questionLog.getEndSlectTIme()) - (questionLog.getBeginSlectTime()==null?0:questionLog.getBeginSlectTime()) )/1000));
			ansLog.setRuleId(ruleID);

			questionLogStr = Json.json(ansLog);
		}catch (Exception e){
			logger.error("解析json出错", e);
		}
		logger.info("解析json 返回值===" + questionLogStr);
		return questionLogStr;
	}
}
