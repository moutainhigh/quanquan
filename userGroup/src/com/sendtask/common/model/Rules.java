package com.sendtask.common.model;

/**
 * @author zhoujia
 */
public class Rules {

	/** 周期 **/
	private String cycleTime;

	/**
	 * 每人发放几个
	 */
	private String sendCount;
	/**
	 * 用户分组 规则字符串
	 */
	private String userGroup;
	/**
	 * 领取后
	 */
	private String afterGet;
	/**
	 * 优惠券ID
	 */
	private String quanId;
	/**
	 * 过期时间
	 */
	private String overTime;

	/**
	 * redis中用户编组的key
	 */
	private String groupKey;
	
	/**题目id**/
	private String qid;
	
	/****/
	private String messageId;
	
	/**创建时间**/
	private Long cDate = System.currentTimeMillis();

	public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

	
	/**用户分组 topic***/
	private String topic;
	
	/**
	 * @return the cycleTime
	 */
	public String getCycleTime() {
		return cycleTime;
	}

	/**
	 * @param cycleTime
	 *            the cycleTime to set
	 */
	public void setCycleTime(String cycleTime) {
		this.cycleTime = cycleTime;
	}

	/**
	 * @return the sendCount
	 */
	public String getSendCount() {
		return sendCount;
	}

	/**
	 * @param sendCount
	 *            the sendCount to set
	 */
	public void setSendCount(String sendCount) {
		this.sendCount = sendCount;
	}

	/**
	 * @return the userGroup
	 */
	public String getUserGroup() {
		return userGroup;
	}

	/**
	 * @param userGroup
	 *            the userGroup to set
	 */
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * @return the afterGet
	 */
	public String getAfterGet() {
		return afterGet;
	}

	/**
	 * @param afterGet
	 *            the afterGet to set
	 */
	public void setAfterGet(String afterGet) {
		this.afterGet = afterGet;
	}

	/**
	 * @return the quanId
	 */
	public String getQuanId() {
		return quanId;
	}

	/**
	 * @param quanId
	 *            the quanId to set
	 */
	public void setQuanId(String quanId) {
		this.quanId = quanId;
	}

	/**
	 * @return the overTime
	 */
	public String getOverTime() {
		return overTime;
	}

	/**
	 * @param overTime
	 *            the overTime to set
	 */
	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Long getcDate() {
		return cDate;
	}

	public void setcDate(Long cDate) {
		this.cDate = cDate;
	}
	
	
}
