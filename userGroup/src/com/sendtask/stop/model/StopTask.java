package com.sendtask.stop.model;

import java.util.Date;

/**
 * 停发任务
 * 
 * @author qiuxy
 */
public class StopTask {
	private String stopId;
	private Date stopDate;
	private Integer stopFlag;

	/**
	 * @return the stopId
	 */
	public String getStopId() {
		return stopId;
	}

	/**
	 * @param stopId
	 *            the stopId to set
	 */
	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

	/**
	 * @return the stopDate
	 */
	public Date getStopDate() {
		return stopDate;
	}

	/**
	 * @param stopDate
	 *            the stopDate to set
	 */
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	/**
	 * @return the stopFlag
	 */
	public Integer getStopFlag() {
		return stopFlag;
	}

	/**
	 * @param stopFlag
	 *            the stopFlag to set
	 */
	public void setStopFlag(Integer stopFlag) {
		this.stopFlag = stopFlag;
	}

}
