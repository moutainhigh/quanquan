package com.rulesfilter.yy.zj.model;

import java.io.Serializable;

/**
 * @author zhoujia
 *
 * @date 2015年8月11日
 * 
 * cxx记录答题记录的日志
 */
public class AnsLog implements Serializable{
	
	public AnsLog() {
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2059993396909865228L;

	private String logId;
	
	/**回答人id**/
	private String userId;
	
	/**提问人ID**/
	private String queUserId;
	
	/**日志时间**/
	private Long time;
	
	/**答题人手机**/
	private String answerPhone;
	
//	/**答题人IMEI**/
//	private String IMEI;
	
//	/**答题人ip**/
//	private String IP;
	
	/**问题人手机***/
	private String questionPhone;
	
	/**提问人账号**/
	private String questionAccount;
	/**提问人IMEI**/
	private String questionIMEI;
	/**提问人IP**/
	private String questionIP;
	/**回答人账号**/
	private String answerAccount;
	/**回答人IMEI**/
	private String answerIMEI;
	/**回答人IP**/
	private String answerIP;
	
	public static final String isHit = "0";
	public static final String notHit = "1";
	/**是否被命中，命中的最后一条。例如一天三次触发违规，则第三次是被命中的，前两次不算被命中。  0命中， 1 未命中**/
	private String hit;
	
	
	/**问题id**/
	private String qid;
	
	/**问题金额**/
	private String cash;
	
	/**回答时间**/
	private Integer ansTime ;
	
	/**回答字符数**/
	private Integer ruleContentNum;
	
	/**筛选时间**/
	private Integer selectTime;
	
	/**被命中的规则id**/
	private String ruleId;
	
	
	/**用户回答记录hbase表明**/
	public static String hbase_table = "CIRCLE.ANSLOG";
	
	/**用户回答记录表 第一个family**/
	public static String hbase_family_1 = "0";
	
	/**用户回答记录 第二个family**/
	public static String hbase_family_2 = "rules";
	
	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getAnswerPhone() {
		return answerPhone;
	}

	public void setAnswerPhone(String answerPhone) {
		this.answerPhone = answerPhone;
	}

	public String getQuestionPhone() {
		return questionPhone;
	}

	public void setQuestionPhone(String questionPhone) {
		this.questionPhone = questionPhone;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}


	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	/**
	 * 回答者用户ID
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 提问者用户ID
	 * @return
	 */
	public String getQueUserId() {
		return queUserId;
	}

	public void setQueUserId(String queUserId) {
		this.queUserId = queUserId;
	}

	public Integer getAnsTime() {
		return ansTime;
	}

	public void setAnsTime(Integer ansTime) {
		this.ansTime = ansTime;
	}

	public Integer getRuleContentNum() {
		return ruleContentNum;
	}

	public void setRuleContentNum(Integer ruleContentNum) {
		this.ruleContentNum = ruleContentNum;
	}

	public Integer getSelectTime() {
		return selectTime;
	}

	public void setSelectTime(Integer selectTime) {
		this.selectTime = selectTime;
	}

	public String getQuestionAccount() {
		return questionAccount;
	}

	public void setQuestionAccount(String questionAccount) {
		this.questionAccount = questionAccount;
	}

	public String getQuestionIMEI() {
		return questionIMEI;
	}

	public void setQuestionIMEI(String questionIMEI) {
		this.questionIMEI = questionIMEI;
	}

	public String getQuestionIP() {
		return questionIP;
	}

	public void setQuestionIP(String questionIP) {
		this.questionIP = questionIP;
	}

	public String getAnswerAccount() {
		return answerAccount;
	}

	public void setAnswerAccount(String answerAccount) {
		this.answerAccount = answerAccount;
	}

	public String getAnswerIMEI() {
		return answerIMEI;
	}

	public void setAnswerIMEI(String answerIMEI) {
		this.answerIMEI = answerIMEI;
	}

	public String getAnswerIP() {
		return answerIP;
	}

	public void setAnswerIP(String answerIP) {
		this.answerIP = answerIP;
	}

	public String getHit() {
		return hit;
	}

	public void setHit(String hit) {
		this.hit = hit;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	
}
