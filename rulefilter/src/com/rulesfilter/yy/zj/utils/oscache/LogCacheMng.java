package com.rulesfilter.yy.zj.utils.oscache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rulesfilter.yy.zj.model.AnsLog;

/**
 * @author zhoujia
 *
 * @date 2015��8��11��
 */


public class LogCacheMng {
	private static Logger logger = LoggerFactory.getLogger(LogCacheMng.class);
	private BaseCache logCache;

	private static LogCacheMng instance;
	private static Object lock = new Object();

	public LogCacheMng() {
		// ������������ļ�������ʼBaseCache����;
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
		// TODO �Զ����ɷ������
		logCache.put(log.getLogId(), log);
	}

	public void removeLog(String logID) {
		// TODO �Զ����ɷ������
		logCache.remove(logID);
	}

	public AnsLog getLog(String LogID) {
		// TODO �Զ����ɷ������
		try {
			return (AnsLog) logCache.get(LogID);
		} catch (Exception e) {
			// TODO �Զ����� catch ��
			logger.error("getNews>>newsID[" + LogID + "]>>",e);

//			AnsLog log = new AnsLog(LogID);
//			this.putNews(news);
//			return news;
			return null;
		}
	}

	public void removeAllNews() {
		// TODO �Զ����ɷ������
		logCache.removeAll();
	}

}
