package com.sm.model;


/** 
 * @author  zhoujia 
 * @date 创建时间：2015年7月16日 下午2:15:35   
 */
public class Rules {
	
	/**周期**/
	private String cycleTime;
	
	/**
	 * redis中用户编组的key
	 */
	private String groupKey;
	
	private String qid;
	
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
	
	/**消息id**/
	private String messageId;
	
	/**用户分组 topic***/
	private String topic;
	
	/**创建时间**/
	private Long cDate = System.currentTimeMillis();
	
	@Override
	public String toString() {
		return cDate+""+qid;
	}
	
	
	public Long getcDate() {
		return cDate;
	}


	public String getSendCount() {
		return sendCount;
	}

	public void setSendCount(String sendCount) {
		this.sendCount = sendCount;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getAfterGet() {
		return afterGet;
	}

	public void setAfterGet(String afterGet) {
		this.afterGet = afterGet;
	}

	public String getQuanId() {
		return quanId;
	}

	public void setQuanId(String quanId) {
		this.quanId = quanId;
	}

	public String getOverTime() {
		return overTime;
	}

	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

	public String getCycleTime() {
		return cycleTime;
	}

	public void setCycleTime(String cycleTime) {
		this.cycleTime = cycleTime;
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

	
}
