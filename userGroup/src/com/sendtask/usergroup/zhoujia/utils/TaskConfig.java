package com.sendtask.usergroup.zhoujia.utils;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TaskConfig {
	private static Logger logger = LoggerFactory.getLogger(TaskConfig.class);
	public static String dataPath;// 数据目录
	public static Config cfg;

	public static String ruleVersion;// 规则当前版本号
	public static List<String> rulesList;// 规则列表

	public static void initTask() {
		Config cfs = getConfig();
		dataPath = cfs.getAsString("dataPath");
	}

	public static Config getConfig() {
		Config cfg = null;
		try {
			cfg = new Config("config/conf.properties");
		} catch (IOException e) {
			logger.error("读取配置报错", e);
		}
		return cfg;
	}
}
