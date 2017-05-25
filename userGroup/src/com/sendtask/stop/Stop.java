package com.sendtask.stop;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.sendtask.common.model.ScheduleJob;
import com.sendtask.common.utils.QuartzAPI;
import com.sendtask.stop.dict.StopDict;
import com.sendtask.stop.model.StopTask;

public class Stop {
	/**
	 * redis监听节点
	 */
	private static String redisSubKey;
	/**
	 * redis数据节点
	 */
	private static String redisDataKey;
	/**
	 * 定时任务实现类
	 */
	@SuppressWarnings("rawtypes")
	private static Class stopJobClass;

	private static Logger logger = LoggerFactory.getLogger(Stop.class);

	/**
	 * 初始化停发模块
	 * 
	 * 注：调用前需先初始化redis和启动quartz
	 * 
	 * @param redisSubKey
	 *            redis监听节点
	 * @param redisDataKey
	 *            redis数据节点
	 * @param stopJobClass
	 *            定时任务实现类
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void init(String redisSubKey, String redisDataKey, Class stopJobClass, String taskID)
			throws Exception {
		logger.info("初始化停发监听");
		// 初始化参数
		Stop.redisSubKey = redisSubKey;
		Stop.redisDataKey = redisDataKey;
		Stop.stopJobClass = stopJobClass;
		logger.info("停发监听参数列表：：：");
		logger.info("redisSubKey===" + redisSubKey);
		logger.info("redisDataKey===" + redisDataKey);
		logger.info("stopJobClass===" + stopJobClass);
		// 防止在程序复活前发的消息丢失 先干一波
		if (Redis.shard.llen(redisDataKey) > 0) {
			logger.info("先停发一波！！！");
			stop(taskID);
		}
		logger.info("=== 启动停发等待线程 ===");
		// 起线程等监听
		Subscribe ss = new Subscribe();
		ss.subscribe(Stop.getRedisSubKey());
	}

	/**
	 * 执行停发
	 * 
	 * @param redisSubKey
	 *            redis监听节点
	 * @param redisDataKey
	 *            redis数据节点
	 * @param stopJobClass
	 *            定时任务实现类
	 * @throws SchedulerException
	 */
	public static void stop(String taskID) {
		logger.info("要停发了啊啊啊啊啊！！！" + Redis.shard.llen(redisDataKey));
		// 取redis的list
		//!!! 没插进来
		while (Redis.shard.llen(redisDataKey) > 0) {
			String data = Redis.shard.lpop(redisDataKey);
			logger.info("我看看redis里！！！" + data);
			StopTask st = (StopTask) Json.jsonParser(data, StopTask.class);
			logger.info("我看看！！！" + st.getStopId() + "===" + st.getStopDate());
			JobDataMap jdm = new JobDataMap();
			jdm.put("stopId", st.getStopId());
			jdm.put("taskID", taskID);
			ScheduleJob sj = new ScheduleJob(st.getStopId(), st.getStopId(), st.getStopDate(), jdm);
			if (st.getStopFlag() == StopDict.STOPFLAG_UPDATE) {
				logger.info("删任务了！！！" + st.getStopId());
				// 修改停发时间 就先把这个任务干掉
				try {
					QuartzAPI.removeJob(sj);
				} catch (SchedulerException e) {
					logger.error("很遗憾。。。删除停发定时任务失败了。。。定时任务ID=" + st.getStopId() + "，想别的辙吧。。。。。。。", e);
				}
			}
			// 新增停发时间任务
			logger.info("起任务了！！！" + st.getStopId());
			try {
				QuartzAPI.addJob(sj, stopJobClass);
			} catch (SchedulerException e) {
				logger.error("很遗憾。。。新建停发定时任务失败了。。。定时任务ID=" + st.getStopId() + "，想别的辙吧。。。。。。。", e);
			}
		}
	}

	/**
	 * @return the redisSubKey
	 */
	public static String getRedisSubKey() {
		return redisSubKey;
	}

	/**
	 * @return the redisDataKey
	 */
	public static String getRedisDataKey() {
		return redisDataKey;
	}

	/**
	 * @return the stopJobClass
	 */
	@SuppressWarnings("rawtypes")
	public static Class getStopJobClass() {
		return stopJobClass;
	}

}
