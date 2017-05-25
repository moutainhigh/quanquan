package com.sendtask.common.model;

import java.util.Date;

import org.quartz.JobDataMap;

public class ScheduleJob {
	private String jobName;
	private String jobGroup;
	private String cronExpression;// 任务时间表达式

	private Date startDate;
	private JobDataMap jobDataMap;// 任务数据

	public ScheduleJob() {
	}

	public ScheduleJob(String jobName, String jobGroup, String cronExpression, JobDataMap jobDataMap) {
		super();
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.cronExpression = cronExpression;
		this.jobDataMap = jobDataMap;
	}

	public ScheduleJob(String jobName, String jobGroup, Date startDate, JobDataMap jobDataMap) {
		super();
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.jobDataMap = jobDataMap;
		this.startDate = startDate;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

}
