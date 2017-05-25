package com.sendtask.common.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.sendtask.common.dao.TaskDao;
import com.sendtask.common.dict.SlaveDict;
import com.sendtask.common.model.SMTask;
import com.sendtask.common.model.Slave;
import com.sendtask.common.utils.TaskConfig;
import com.sendtask.netty.service.NettyService;

/**
 * 任务公共业务
 * 
 * @author qiuxy
 *
 */
public abstract class TaskService {

	protected static String data;// 传过来的数据
	protected static String slaveType;// 任务类型 schedule/worker
	protected static String taskID;// 任务ID
	protected static String dbType;// 读取数据源 R/M
	protected static String workerName;// 起worker名字=topicID(默认拉人)=redisKEY(消息直投)
	protected static String spareSlaves;// 备机
	protected static String configPath;// 配置文件路径
	protected static String confPrefix;// 单独任务的前缀

	private static Logger logger = LoggerFactory.getLogger(TaskService.class);

	// 打日志用taskID=taskID。。。 我懒
	protected static String logTaskID;

	/**
	 * 初始化参数
	 * 
	 * @param args
	 *            脚本传来的参数
	 * @param configPrefix
	 *            用于特殊redis配置的任务前缀
	 * @param logPathName
	 *            单独的日志配置路径
	 */
	public void init(String[] args, String configPrefix, String logPathName) {
		// FIXME 增加或减少参数别忘了改这!!!
		if (args.length != 5) {
			try {
				logger.error(":::taskID=" + args[1] + "的任务=====参数数量错误。。。参数数量=" + args.length + "，程序即将退出。。。");
			} catch (Exception e) {
				logger.error(
						":::这个任务连taskID都拿不到。。。。参数数量=" + args.length + "===还好还有任务前缀=" + configPrefix + "，程序即将退出。。。");
			}
			// 参数个数错了就别玩了
			System.exit(1);
		}

		slaveType = args[0];// worker或者sechdule
		taskID = args[1];// 任务id
		confPrefix = configPrefix;// 读取单独的redis配置而加的配置文件前缀
		logTaskID = ":::" + confPrefix + "的taskID=" + taskID + "的任务===";// 日志用打一下taskID
		dbType = args[2];// R 或者 M
		configPath = args[3]; // 配置文件路径
		// 初始化日志 需有configPath
		initLog(logPathName);
		// 初始化redis 需有configPath和configPrefix
		initRedis();
		// 初始化netty 需有configPath
		initNetty();
		// 配置文件参数 需有configPath
		TaskConfig.initTask(configPath);
		// 根据类型判断第5个参数的类型sechdule是备机列表，worker是worker名字=topicID(默认拉人)=redisKEY(消息直投)
		switch (slaveType) {
		case SlaveDict.scheduleTYPE:
			// 备机列表
			spareSlaves = args[4];
			break;
		case SlaveDict.workerTYPE:
			// worker名字=topicID(默认拉人)=redisKEY(消息直投)
			workerName = args[4];
			break;
		default:
			logger.error(logTaskID + "关键参数slaveType=[" + slaveType + "]出错，程序即将退出。。。");
			// 这个参数不对就别玩了
			System.exit(1);
			break;
		}

		// 存下全部数据
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			sb.append(":");
		}
		// 全部数据 在发给备机监听时会用
		data = sb.toString().substring(0, sb.length() - 1);
	}

	/**
	 * 启动log4j
	 */
	private void initLog(String logPathName) {
		PropertyConfigurator.configure(configPath + logPathName);
		logger.info(logTaskID + "log已经启动：）");
	}

	/**
	 * 启动redis 因为用特殊的配置文件
	 * 
	 * @param configPath
	 * @throws Exception
	 */
	private void initRedis() {
		try {
			// 初始化redis
			Config conf = new Config(configPath + confPrefix + "conf.properties");
			Redis.initialShard(conf);
			Redis.initial(conf);
		} catch (Exception e) {
			logger.error(logTaskID + "redis启动出错了啊啊啊啊啊啊，程序即将退出。。。", e);
			// redis都没初始化成功还干毛
			System.exit(1);
		}
		logger.info(logTaskID + "redis已经启动：）");
	}

	/**
	 * 初始化netty服务
	 * 
	 * @param configPath
	 * @param taskID
	 * @throws Exception
	 */
	private void initNetty() {
		// 监听netty服务器url
		try {
			// 0929已废弃从zk拿url的方法
			// NettyUrlService.init(configPath);
			NettyService.init(configPath);
		} catch (Exception e) {
			logger.error(logTaskID + "=====netty启动出错了啊啊啊啊啊啊，程序即将退出。。。", e);
			// netty初始化出错了 大B他哥不能让备机不接活了 所以得退出程序了。。。
			System.exit(1);
		}
		logger.info(logTaskID + "netty已经启动：）");
	}

	/**
	 * 拿任务
	 * 
	 * @return
	 */
	public SMTask getTask() {
		return getTask(taskID, dbType);
	}

	/**
	 * 拿任务
	 * 
	 * @param taskID
	 * @param dbType
	 * @return
	 */
	public SMTask getTask(String taskID, String dbType) {
		if (SlaveDict.redisDB.equals(dbType)) {
			try {
				SMTask smTask = (SMTask) Json.jsonParser(Redis.shard.get(taskID), SMTask.class);
				return smTask;
			} catch (Exception e) {
				logger.error(logTaskID + "dbType[" + dbType + "]=====在redis里拿任务出错了啊啊啊啊啊啊，程序即将退出。。。", e);
				// 拿任务都出错了还玩个毛
				System.exit(1);
				return null;
			}
		} else if (SlaveDict.mysqlDB.equals(dbType)) {
			TaskDao taskDao = new TaskDao(configPath);
			try {
				SMTask smTask = taskDao.getTaskById(taskID);
				return smTask;
			} catch (Exception e) {
				logger.error(logTaskID + "dbType[" + dbType + "]任务=====在mysql里拿任务出错了啊啊啊啊啊啊，程序即将退出。。。", e);
				// 拿任务都出错了还玩个毛
				System.exit(1);
				return null;
			}
		}
		logger.error(logTaskID + "dbType[" + dbType + "]任务=====类型都不对啊啊啊啊啊啊啊啊啊，程序即将退出。。。");
		// 都拿不到任务还玩个毛
		System.exit(1);
		return null;
	}

	/**
	 * 发任务给worker
	 * 
	 * @param data
	 *            机器列表
	 */
	public void sendToWorker(List<String> data) {
		ZookeeperService zkService = new ZookeeperService(configPath);
		List<Slave> slaveEtcs = zkService.getChildEtcs();
		// TODO 负载暂时不用 以后再加 预留
		// slaveEtcs = Etc.calculationEtc(slaveEtcs);
		logger.info(logTaskID + "有" + slaveEtcs.size() + "台满足要求能干活的机器，要" + data.size() + "台干活的机器");
		// 负载满足要求的机器不够干活的机器
		if (slaveEtcs.size() < data.size()) {
			logger.error(logTaskID + "大哥我干不了啊，要干data[" + data.size() + "]个活只有slaveEtcs[" + slaveEtcs.size()
					+ "]台机器，我不干了！！！，程序即将退出。。。");
			// XXX 机器数量不够 离散度高于机器数量 目前设计是一个slave在启动时只干一次该worker 所以我先退出程序了。。。
			System.exit(1);
		} else {
			// XXX 打乱一下slaveEtcs列表 如果第一个干不了硬起死了 容错回来还是他干 所以打乱一下 就有几率不是他了：）
			// 以后可能会去掉
			Collections.shuffle(slaveEtcs);
			for (int i = 0; i < data.size(); i++) {
				// slave负载节点全路径
				String slaveEtc = slaveEtcs.get(i).getHost();
				// 从最后一个/截取slave名
				String slaveName = slaveEtc.substring(slaveEtc.lastIndexOf("/"));
				// 截完第一个是/先替换掉 拿到slave名 再拼worker监听管道前缀
				String workerChannelName = TaskConfig.workerTaskChannelPrefix + slaveName.replace("/", "");
				// 0916 改为发redis订阅
				String wkData = taskID + ":" + dbType + ":" + data.get(i) + ":" + spareSlaves;
				logger.info(logTaskID + "准备发worker任务，参数列表:::taskID===" + taskID + "dbType===" + dbType + "workername==="
						+ data.get(i) + "zkURL===" + TaskConfig.dataPath + slaveName + "zkData===" + taskID + ":"
						+ dbType + ":" + data.get(i) + "===发到redis订阅通道[" + workerChannelName + "]，发送的data=[" + wkData
						+ "]");
				Redis.CONNECT.publish(workerChannelName, wkData);
			}
		}
	}

	/**
	 * schedule抽象方法 在各个任务中实现
	 */
	public abstract void schedule();

	/**
	 * worker抽象方法 在各个任务中实现
	 */
	public abstract void worker();

	public String getData() {
		return data;
	}

	public String getSlaveType() {
		return slaveType;
	}

	public String getTaskID() {
		return taskID;
	}

	public String getDbType() {
		return dbType;
	}

	public String getWorkerName() {
		return workerName;
	}

	public String getSpareSlaves() {
		return spareSlaves;
	}

	public String getConfigPath() {
		return configPath;
	}

	public String getConfPrefix() {
		return confPrefix;
	}

	public String getLogTaskID() {
		return logTaskID;
	}

}
