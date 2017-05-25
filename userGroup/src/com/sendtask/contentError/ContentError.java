package com.sendtask.contentError;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.sendtask.contentError.dict.CEdict;
import com.sendtask.contentError.kill.KillSubscribe;

/**
 * 容错业务
 * 
 * @author qiuxy
 */
public class ContentError {

	private static Logger logger = LoggerFactory.getLogger(ContentError.class);

	/**
	 * 容错根节点
	 */
	protected static String errorContentPath;

	// 配置文件路径
	protected static String configPath;
	
	protected static String spareTaskChannelPrefix;

	/**
	 * 初始化任务容错
	 * 
	 * @param cfgPath
	 * @param taskID
	 */
	public static void initTaskForError(String cfgPath, String taskID) {
		configPath = cfgPath;
		logger.info(":::taskID=" + taskID + "===初始化容错模块===");
		Config cfg = null;
		try {
			cfg = new Config(configPath+"conf.properties");
		} catch (IOException e) {
			logger.error("读取" + configPath + "配置报错", e);
		}
		if (cfg == null) {
			logger.error("读取" + configPath + "配置为NULL");
		} else {
			errorContentPath = cfg.getAsString("errorContent");
			spareTaskChannelPrefix = cfg.getAsString("spareTaskChannelPrefix");
			// cePathForTask = errorContentPath + "/" + taskID;
		}
	}

	public static void readKill(String taskID) {
		logger.info("监听死亡印记。。。。等待线程");
		// 起线程等监听

		logger.info("开始监听死亡印记。。。。消息队列");
		KillSubscribe ss = new KillSubscribe();
		logger.info("我看看属性======" + taskID);
		logger.info("监听这个啊嗷嗷啊啊" + CEdict.REDIS_KILL_FLAG + taskID);
		ss.subscribe(CEdict.REDIS_KILL_FLAG + taskID);
	}

	public static void writeKill(String taskID) {
		logger.info("准备加死亡印记。。。。");
		Redis.CONNECT.publish(CEdict.REDIS_KILL_FLAG + taskID, "KILL");
	}

}
