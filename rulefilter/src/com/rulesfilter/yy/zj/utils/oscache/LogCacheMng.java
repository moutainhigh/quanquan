package com.rulesfilter.yy.zj.utils.oscache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rulesfilter.yy.zj.model.AnsLog;

/**
 * @author zhoujia
 *
 * @date 2015年8月11日
 */


public class LogCacheMng {
	private static Logger logger = LoggerFactory.getLogger(LogCacheMng.class);
	private BaseCache logCache;

	private static LogCacheMng instance;
	private static Object lock = new Object();

	public LogCacheMng() {
		// 这个根据配置文件来，初始BaseCache而已;
		logCache = new BaseCache("log", 1800);
	}

	public static LogCacheMng getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new LogCacheMng();
				}
			}
		}
		return instance;
	}

	public void putLog(AnsLog log) {
		// TODO 自动生成方法存根
		logCache.put(log.getLogId(), log);
	}

	public void removeLog(String logID) {
		// TODO 自动生成方法存根
		logCache.remove(logID);
	}

	public AnsLog getLog(String LogID) {
		// TODO 自动生成方法存根
		try {
			return (AnsLog) logCache.get(LogID);
		} catch (Exception e) {
			// TODO 自动生成 catch 块
			logger.error("getNews>>newsID[" + LogID + "]>>",e);

//			AnsLog log = new AnsLog(LogID);
//			this.putNews(news);
//			return news;
			return null;
		}
	}

	public void removeAllNews() {
		// TODO 自动生成方法存根
		logCache.removeAll();
	}

}
