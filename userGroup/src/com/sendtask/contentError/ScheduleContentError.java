package com.sendtask.contentError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.sendtask.common.service.TaskService;
import com.sendtask.common.utils.StringUtil;
import com.sendtask.common.utils.ZookeeperAPI;
import com.sendtask.contentError.dao.ContentErrorToRedis;
import com.sendtask.contentError.dict.CEdict;

/**
 * 容错schedule业务
 * 
 * @author qiuxy
 *
 */
public class ScheduleContentError extends ContentError {
	private static Logger logger = LoggerFactory.getLogger(ScheduleContentError.class);
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

	/**
	 * 创建taskID下的sche节点
	 */
	public static void createTaskIDScheNode(String taskID) {
		logger.info("开始创建taskID下的sche节点 === createTaskIDScheNode");
		String cePathForTask = errorContentPath + "/" + taskID;
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		// 容错节点是否存在
		if (zkClient.isExist(cePathForTask) == null) {
			logger.error(cePathForTask + "节点未启动");
		} else {
			String taskSchedule = cePathForTask + "/schedule";
			logger.info("taskSchedule=====" + cePathForTask);
			// 创建节点
			zkClient.createPath(taskSchedule, taskSchedule, CreateMode.EPHEMERAL);
		}
	}

	/**
	 * 监听taskID子节点数量 schedule用的
	 */
	public static void watcherTaskIDSonForSche(@SuppressWarnings("rawtypes") final Class taskService,
			final String cfgName, final String taskID) {
		final String cePathForTask = errorContentPath + "/" + taskID;
		logger.info("增加监听taskID子节点数量 schedule用的嗷嗷嗷===！！！！&&&&" + cePathForTask);
		final ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		zkClient.getChildList(cePathForTask, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				logger.info("watcherTaskIDSonForSche===监听到子节点数量变化");
				// 节点数据变了
				if (EventType.NodeChildrenChanged.equals(event.getType())) {// 子节点变化了
																			// 可能是有子节点挂了或者干完活了
					logger.info("我看看是不是进NodeChildrenChanged了嗷嗷嗷");
					logger.info("子节点变化了 可能是有子节点挂了或者干完活了");

					// 活完标示
					boolean flagYes = true;
					logger.info("@@@@@@@@@@@@@@@@@@@@@@@===" + taskID);

					// TODO 待优化
					Map<String, String> map = ContentErrorToRedis.readWorkerSignMap(taskID);

					logger.info("@@@@@@@@@@@@@@@@@@@@@@@" + map.size());
					if (map != null && map.size() > 0) {
						for (Map.Entry<String, String> entry : map.entrySet()) {
							// 如果有一个是未完成的 那就是活还没完
							logger.info("看看状态。。。。。。。。。" + entry.getValue());
							if (CEdict.SIGN_UNF.equals(entry.getValue())) {
								flagYes = false;
								break;
							}
						}
						if (flagYes) {
							logger.info("任务完成收工");
							scheduleOver(zkClient, taskID, configPath);
						}
					}

					if (cfgName == null) {
						restartSlave(taskService, taskID);
					} else {
						restartSlave(taskService, cfgName, taskID);
					}
					zkClient.getChildList(cePathForTask, this);
				} else {
					zkClient.getChildList(cePathForTask, this);
				}
			}
		});
	}

	public static void restartSlave(Class taskService, String cfgName, String taskID) {
		logger.info("重起worker@@@@@@@@@@@@@@@@@@@@@@@");
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		// TODO 待优化
		Map<String, String> map = ContentErrorToRedis.readWorkerSignMap(taskID);
		List<String> childs = getChildList(zkClient, taskID);
		logger.info("重起worker@@@@@@@@@@@@@@@@@@@@@@@childs" + childs.size());

		// 判断子节点数量 有1个是schedule节点
		if (childs != null && map != null) {
			logger.info("重起worker@@@@@@@@@@@@@@@@@@@@@@@tfe" + map.size());
			if (childs.size() < map.size()) {
				logger.info("yesyesworker@@@@@@@@@@@@@@@@@@@@@@@");
				// 子节点数量比容错信息记录的干活节点数量少
				logger.info("childs==============" + childs.size());
				logger.info("wsl==============" + map.size());
				// 需要再起几个worker
				List<String> wns = new ArrayList<String>();
				// 比较出容错信息记录里比当前节点多出的那些节点
				for (Map.Entry<String, String> entry : map.entrySet()) {
					// 不在子节点列表并且活没干完
					if (!childs.contains(entry.getKey()) && CEdict.SIGN_UNF.equals(entry.getValue())) {
						logger.info("不在子节点列表并且活没干完===========");
						wns.add(entry.getKey());
						// 往这个topic的zk里改offset
						int tmpIndex = 0;
						while (true) {
							String offset = ContentErrorToRedis.readOffset(entry.getKey(), tmpIndex);
							logger.info("我看看啊啊啊啊啊===========" + offset);
							if (StringUtil.isNullStr(offset)) {
								logger.info("没找到就是没起这么多线程");
								// 没找到就是没起这么多线程
								break;
							} else {
								logger.info("改kafaka的zookeeper的offset");
								// 改zk的offset
								writeOffsetToZk(entry.getKey(), offset, tmpIndex, cfgName);
								tmpIndex++;
							}
						}
					}
				}
				// 需要启worker
				if (wns.size() > 0) {
					TaskService ts = null;
					try {
						ts = (TaskService) Class.forName(taskService.getName()).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						logger.error("实例化任务业务报错" + e);
					}
					if (ts != null) {
						// 重启worker
						logger.info("要重启" + wns.size() + "个worker了！！！");
						ts.sendToWorker(wns);
					}
				}
			} else {
				logger.info("nononoworker@@@@@@@@@@@@@@@@@@@@@@@");
			}
		}
	}

	public static void restartSlave(Class taskService, String taskID) {
		logger.info(":::taskID=" + taskID + "=======重起worker@@@@@@@@@@@@@@@@@@@@@@@");
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		List<String> childs = getChildList(zkClient, taskID);
		logger.info(":::taskID=" + taskID + "=======重起worker@@@@@@@@@@@@@@@@@@@@@@@childs" + childs.size());
		// TODO 待优化
		Map<String, String> map = ContentErrorToRedis.readWorkerSignMap(taskID);

		// 判断子节点数量 有1个是schedule节点
		if (childs != null && map != null) {
			logger.info("重起worker@@@@@@@@@@@@@@@@@@@@@@@tfe" + map.size());
			if (childs.size() < map.size()) {
				logger.info("yesyesworker@@@@@@@@@@@@@@@@@@@@@@@");
				// 子节点数量比容错信息记录的干活节点数量少
				logger.info("childs==============" + childs.size());
				logger.info("wsl==============" + map.size());
				// 需要再起几个worker
				List<String> wns = new ArrayList<String>();
				// 比较出容错信息记录里比当前节点多出的那些节点
				for (Map.Entry<String, String> entry : map.entrySet()) {
					// 不在子节点列表并且活没干完
					if (!childs.contains(entry.getKey()) && CEdict.SIGN_UNF.equals(entry.getValue())) {
						wns.add(entry.getKey());
					}
				}
				// 需要启worker
				if (wns.size() > 0) {
					TaskService ts = null;
					try {
						ts = (TaskService) Class.forName(taskService.getName()).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						logger.error("实例化任务业务报错" + e);
					}
					if (ts != null) {
						// 重启worker
						logger.info("要重启" + wns.size() + "个worker了！！！");
						ts.sendToWorker(wns);
					}
				}
			} else {
				logger.info("nononoworker@@@@@@@@@@@@@@@@@@@@@@@");
			}
		}
	}

	private static List<String> getChildList(ZookeeperAPI zkClient, String taskID) {
		String cePathForTask = errorContentPath + "/" + taskID;
		List<String> childs = zkClient.getChildList(cePathForTask);
		for (String c : childs) {
			if ("schedule".equals(c)) {
				childs.remove(c);
				break;
			}
		}
		return childs;
	}

	/**
	 * schedule自杀
	 */
	public static void scheduleOver(ZookeeperAPI zkClient, String taskID, String spareSlaves) {
		String cePathForTask = errorContentPath + "/" + taskID;
		logger.info(":::enter scheduleOver===");
		// 通知备机停止监听
		logger.info(":::sendSpare SpareSlaves[" + spareSlaves + "]");
		// ContentError.sendSpare(TaskService.getSpareSlaves(), "KILL|" +
		// taskID);
		// 写redis schedule已完成的状态
		logger.info(":::writeScheSign taskID[" + taskID + "]");
		ContentErrorToRedis.writeScheSign(taskID, CEdict.SIGN_OK);
		// 活完了 收工 删节点
		logger.info(":::deleteNode cePathForTask-schedule[" + cePathForTask + "/schedule" + "]");
		zkClient.deleteNode(cePathForTask + "/schedule");
		// 删除任务节点
		logger.info(":::deleteNode cePathForTask[" + cePathForTask + "]");
		zkClient.deleteNode(cePathForTask);
		// 都干完了 自杀
		logger.info(":::KILL U!!!:::");
		System.exit(0);
	}

	/**
	 * schedule自杀
	 */
	public static void scheduleOver(String taskID) {
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		scheduleOver(zkClient, taskID, configPath);
	}

	/**
	 * 通知备机监听
	 * 
	 * @param spareSlaves
	 *            备机
	 * @param data
	 *            数据
	 */
	public static void sendSpare(String spareSlaves, String data) {
		logger.info("通知备机。。。。。。。。。。" + spareSlaves + "数据=======" + data);
		String[] temps = spareSlaves.split(",");
		for (String slave : temps) {
			logger.info("起。。。。。。。。。。" + slave);
			Redis.CONNECT.publish(spareTaskChannelPrefix + slave, data);
		}
	}

	/**
	 * 把offset写入kafka的zookeeper
	 */
	public static void writeOffsetToZk(String topic, String offset, int partition, String cfgName) {
		try {
			logger.info(configPath + "==================" + cfgName);
			Config config = new Config(configPath + cfgName);
			logger.info(config.getAsString("zookeeper.connect") + "==================");
			ZooKeeper zk = new ZooKeeper(config.getAsString("zookeeper.connect"), 10000, new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					if (KeeperState.SyncConnected == event.getState()) {
						connectedSemaphore.countDown();
					}
				}
			});
			connectedSemaphore.await();
			logger.info(zk.toString());
			// 拿group
			String group = ContentErrorToRedis.readTopicGroup(topic);
			String zkPath = "/consumers/" + group + "/offsets/" + topic + "/" + partition;
			logger.info("*******&&&&&&&&&&&&&&&&&");
			logger.info(zkPath);
			// 该节点不存在
			if (zk.exists(zkPath, null) == null) {
				// 创建再写
				Integer tmp = Integer.valueOf(offset) + 1;
				zk.create(zkPath, tmp.toString().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} else {
				zk.setData(zkPath, offset.getBytes(), -1);
			}
		} catch (IOException e) {
			logger.info("writeOffsetToZk出错", e);
			logger.error("writeOffsetToZk出错", e);
		} catch (KeeperException e) {
			logger.info("writeOffsetToZk出错", e);
			logger.error("writeOffsetToZk出错", e);
		} catch (InterruptedException e) {
			logger.info("writeOffsetToZk出错", e);
			logger.error("writeOffsetToZk出错", e);
		}
	}

}
