package com.sendtask.common.utils;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.sendtask.common.model.ScheduleJob;

/**
 * @title 定时器操作类
 * @author qiuxy
 */
public class QuartzAPI {
	private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

	/**
	 * 启动
	 * 
	 * @throws SchedulerException
	 */
	public static void start() throws SchedulerException {
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.start();
	}

	/**
	 * 关闭
	 * 
	 * @throws SchedulerException
	 */
	public static void shutdown() throws SchedulerException {
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.shutdown();
	}

	/**
	 * 添加任务
	 * 
	 * @param sj
	 * @throws SchedulerException
	 */
	public static String addJob(ScheduleJob sj, Class job) throws SchedulerException {
		JobBuilder jb = JobBuilder.newJob(job);
		if (sj.getJobDataMap() != null) {
			jb.setJobData(sj.getJobDataMap());
		}
		JobDetail jobDetail = jb.withIdentity(sj.getJobName(), sj.getJobName()).build();

		String cron = sj.getCronExpression();

		Trigger trigger = null;
		if (cron != null && cron.length() > 0) {
			// 传个cron表达式一直干
			trigger = TriggerBuilder.newTrigger().withIdentity(sj.getJobName(), sj.getJobName())
					.withSchedule(CronScheduleBuilder.cronSchedule(sj.getCronExpression())).build();
		} else {
			// 传个时间到点就干然后就下班
			trigger = TriggerBuilder.newTrigger().withIdentity(sj.getJobName(), sj.getJobName())
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()).startAt(sj.getStartDate()).build();
		}

		if (trigger == null) {
			return ReturnUtil.ERROR;
		}

		// SimpleScheduleBuilder.simpleSchedule()
		// .withIntervalInHours(24)
		// .withIntervalInSeconds(5) // 时间间隔
		// .withRepeatCount(3) // 重复次数

		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.scheduleJob(jobDetail, trigger);
		return ReturnUtil.SUCCESS;
	}

	/**
	 * 移除任务
	 * 
	 * @param sj
	 * @throws SchedulerException
	 */
	public static void removeJob(ScheduleJob sj) throws SchedulerException {
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.pauseTrigger(new TriggerKey(sj.getJobName(), sj.getJobGroup()));// 停止触发器
		scheduler.unscheduleJob(new TriggerKey(sj.getJobName(), sj.getJobGroup()));// 移除触发器
		scheduler.deleteJob(new JobKey(sj.getJobName(), sj.getJobGroup()));// 删除任务
	}
}