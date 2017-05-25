package com.sendtask.contentError;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendtask.common.utils.ZookeeperAPI;
import com.sendtask.contentError.dao.ContentErrorToRedis;
import com.sendtask.contentError.dict.CEdict;

/**
 * 容错slave业务
 * 
 * @author qiuxy
 *
 */
public class SlaveContentError extends ContentError {
	private static Logger logger = LoggerFactory.getLogger(SlaveContentError.class);

	/**
	 * 创建taskID节点
	 * 
	 * @param dbType
	 */
	public static void createTaskIDNode(String dbType, String taskID) {
		logger.info("开始创建taskID节点 === createTaskIDNode");
		String cePathForTask = errorContentPath + "/" + taskID;
		logger.info("dbType===" + dbType);
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		// 容错节点是否存在
		if (zkClient.isExist(errorContentPath) == null) {
			logger.error(errorContentPath + "节点未启动");
		} else if (zkClient.isExist(cePathForTask) == null) {
			// 创建非暂态容错任务节点 和插入数据
			ContentErrorToRedis.writeDbType(taskID, dbType);
			zkClient.createPath(cePathForTask, "", CreateMode.PERSISTENT);
		} else {
			logger.error(cePathForTask + "节点已存在");
		}
	}

	/**
	 * 监听taskID下sche死活 slave用的
	 * 
	 * 如当前schedule已死 所有监听到的slave抢注schedule节点 抢到的继续任务 没抢到的继续
	 */
	public static void watcherScheduleForSlave(final CEWatcher cew, final String spare, final String taskID,
			final String ceTouchPath, final String slaveName) {
		logger.info(":::taskID=" + taskID + "===增加监听taskID下sche死活 slave用的====" + spare);
		final String cePathForTask = errorContentPath + "/" + taskID;
		final ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		final String cetSPath = cePathForTask + "/schedule";
		logger.info(":::taskID=" + taskID + "$$$$$$$$$$$$$$$容错schedule节点$$$$$$$$$$$$$$$cetSPath=" + cetSPath);

		// ++写的回调
		// final StringCallback sc = new StringCallback() {
		// @Override
		// public void processResult(int rc, String path, Object ctx, String
		// name) {
		// // TODO Auto-generated method stub
		// logger.info(SlaveConfig.slaveName + "抢到拉！！！");
		// cew.watcher(ContentErrorToRedis.readDbType(taskID), taskID, spare);
		// }
		// };

		zkClient.isExist(cetSPath, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				logger.info("watcherScheduleForSlave===监听到sche死了！！！" + spare);
				if (EventType.NodeDeleted.equals(event.getType())) {
					logger.info(":::taskID=" + taskID + "====监听到" + cetSPath + "死了！！！" + spare + "    "
							+ event.getPath() + "     " + event.getState().toString());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.info("睡出错了。。。。", e);
					}
					if (zkClient.isExist(cetSPath) != null) {
						logger.info("开了个玩笑。。。。。。。。");
						zkClient.isExist(cetSPath, this);
					} else {
						//
						String scheSign = ContentErrorToRedis.readScheSign(taskID);
						logger.info("scheSign===&&&&&&&" + scheSign);
						if (CEdict.SIGN_OK.equals(scheSign)) {
							// 任务完成了
							try {
								logger.info(":::taskID=" + taskID + "====备机任务完成，应删==" + ceTouchPath + taskID);
								Runtime.getRuntime().exec("rm " + ceTouchPath + taskID);
							} catch (IOException e) {
								logger.info(":::taskID=" + taskID + "====备机任务完成，删出错了==" + ceTouchPath + " " + taskID);
							}
						} else {
							// zkClient.createPath(cePathForTask + "/temp",
							// SlaveConfig.slaveName, CreateMode.EPHEMERAL, sc);
							// 抢创建schedule
							if (zkClient.createPath(cePathForTask + "/temp", slaveName, CreateMode.EPHEMERAL)) {
								logger.info(":::taskID=" + taskID + "====" + slaveName + "抢到拉！！！");
								// StaticParam.jp.unsubscribe();
								cew.watcher(ContentErrorToRedis.readDbType(taskID), taskID, spare);
							} else {
								logger.info(":::taskID=" + taskID + "====" + slaveName + "没抢到删temp节点！！！");
								// 删除临时节点
								zkClient.deleteNode(cePathForTask + "/temp");
							}
						}
					}
				}
			}
		});
	}
}
