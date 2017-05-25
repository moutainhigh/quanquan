package com.sendtask.contentError;

import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendtask.common.utils.ZookeeperAPI;

/**
 * 容错worker业务
 * 
 * @author qiuxy
 *
 */
public class WorkerContentError extends ContentError {
	private static Logger logger = LoggerFactory.getLogger(WorkerContentError.class);

	/**
	 * 创建taskID下的worker节点
	 * 
	 * @param workerName
	 */
	public static void createTaskIDWorkerNode(String workerName, String taskID) {
		logger.info("开始创建taskID下的worker节点 === createTaskIDWorkerNode");
		String cePathForTask = errorContentPath + "/" + taskID;
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		if (zkClient.isExist(cePathForTask) == null) {
			logger.error(cePathForTask + "节点未启动");
		} else {
			String taskWorker = cePathForTask + "/" + workerName;
			logger.info("workerName=====" + workerName);
			// 创建节点
			zkClient.createPath(taskWorker, taskWorker, CreateMode.EPHEMERAL);
		}
	}
}
