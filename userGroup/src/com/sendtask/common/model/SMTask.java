package com.sendtask.common.model;

/**
 * 任务类
 * 
 * @author zhoujia
 * @date 创建时间：2015年7月16日 下午2:01:39
 */
public class SMTask {

	private String taskID;

	// private List<User> userList;

	private Rules rule;

	/***
	 * 这个任务使用那些节点 slave1,slave2,slave3
	 */
	private String slaveStr;

	/**
	 * 1 schedule通知所有从节点去做 2 schedule自己去做 3 特殊情况，节点为手动配置
	 */
	private String dataType;

	/**
	 * 具体任务的名字，jar包名字
	 */
	private String jarName;
	/**
	 * 并行度，分配几个节点做
	 */
	private Integer slaveNum;

	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSlaveStr() {
		return slaveStr;
	}

	public void setSlaveStr(String slaveStr) {
		this.slaveStr = slaveStr;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public Rules getRule() {
		return rule;
	}

	public void setRule(Rules rule) {
		this.rule = rule;
	}

	/**
	 * @return the slaveNum
	 */
	public Integer getSlaveNum() {
		return slaveNum;
	}

	/**
	 * @param slaveNum
	 *            the slaveNum to set
	 */
	public void setSlaveNum(Integer slaveNum) {
		this.slaveNum = slaveNum;
	}

}
