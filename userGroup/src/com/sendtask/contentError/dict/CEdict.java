package com.sendtask.contentError.dict;

/**
 * 容错字典
 * 
 * @author qiuxy
 */
public class CEdict {
	/**
	 * 完成
	 */
	public static final String SIGN_OK = "2";
	/**
	 * 未完成
	 */
	public static final String SIGN_UNF = "1";
	/**
	 * 未开始
	 */
	public static final String SIGN_UNS = "0";
	/**
	 * kill标记
	 */
	public static final String REDIS_KILL_FLAG = "TASK-KILL|";

	// 容错用的redis key
	/**
	 * 记录任务信息
	 */
	public static final String REDIS_CE_DATA_KEY = "CE-DATA|";
	/**
	 * worker:sign map
	 */
	public static final String REDIS_CE_WORKER_SIGN_KEY = "CE-WS|";
	/**
	 * slave:pid map
	 */
	public static final String REDIS_CE_SLAVE_PID_KEY = "CE-SP|";
	/**
	 * worker:slave string
	 */
	public static final String REDIS_CE_WORKER_SLAVE_KEY = "CE-WS|";

	/**
	 * 读kafka存topic的offset的rediskey
	 */
	public static final String REDIS_CE_KAFKA_OFFSET_KEY = "CE-KO|";
	/**
	 * 容错参数们
	 */
	public static final String CE_REDIS_SCHESLAVE_KEY = "SCHESLAVE";
	public static final String CE_REDIS_SCHESIGN_KEY = "SCHESIGN";
	public static final String CE_REDIS_DBTYPE_KEY = "DBTYPE";
	public static final String CE_REDIS_WORKERNUM_KEY = "WORKERNUM";
	public static final String CE_REDIS_SPARE_KEY = "SPARE";
}
