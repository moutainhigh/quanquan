package com.sendtask.common.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.util.Config;

public class TaskConfig {
	private static Logger logger = LoggerFactory.getLogger(TaskConfig.class);
	public static String dataPath;// 数据目录
	public static String errorContentPath;
	public static Config cfg;
	public static String etcRootPath;
	public static Integer pid;

	public static String workerTaskChannelPrefix;
	public static double cpuPersint;

	public static void initTask(String configPath) {
		Config cfs = getConfig(configPath);
		dataPath = cfs.getAsString("dataPath");
		errorContentPath = cfs.getAsString("errorContent");
		etcRootPath = cfs.getAsString("etc_slave");
		cpuPersint = Double.valueOf(cfs.getAsString("cpuPersint"));
		workerTaskChannelPrefix = cfs.getAsString("workerTaskChannelPrefix");
	}

	public static Config getConfig(String configPath) {
		Config cfg = null;
		try {
			cfg = new Config(configPath + "conf.properties");
		} catch (IOException e) {
			logger.error("读取配置报错", e);
		}
		return cfg;
	}
}
