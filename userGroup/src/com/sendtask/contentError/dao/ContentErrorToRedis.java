package com.sendtask.contentError.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.redis.Redis;
import com.sendtask.contentError.dict.CEdict;

/**
 * 容错信息redis操作类
 * 
 * @author qiuxy
 */
public class ContentErrorToRedis {
	private static Logger logger = LoggerFactory
			.getLogger(ContentErrorToRedis.class);

	// // TODO 待删
	// public static void writeScheSlave(String taskID, String scheSlave) {
	// logger.info(":::enter ContentErrorToRedis.writeScheSlave:::");
	// logger.info(":::param list:::");
	// logger.info(":::taskID=" + taskID);
	// logger.info(":::scheSlave=" + scheSlave);
	// try {
	// Redis.shard.hset(CEdict.REDIS_CE_DATA_KEY + taskID,
	// CEdict.CE_REDIS_SCHESLAVE_KEY, scheSlave);
	// } catch (Exception e) {
	// logger.error(":::ContentErrorToRedis.writeScheSlave is error");
	// }
	//
	// }
	//
	// public static String readScheSlave(String taskID) {
	// logger.info(":::enter ContentErrorToRedis.readScheSlave:::");
	// logger.info(":::param list:::");
	// logger.info(":::taskID=" + taskID);
	// return Redis.shard.hget(CEdict.REDIS_CE_DATA_KEY + taskID,
	// CEdict.CE_REDIS_SCHESLAVE_KEY);
	// }

	public static void writeScheSign(String taskID, String scheSign) {
		logger.info(":::enter ContentErrorToRedis.writeScheSign:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		logger.info(":::scheSign=" + scheSign);
		try {
			Redis.shard.hset(CEdict.REDIS_CE_DATA_KEY + taskID,
					CEdict.CE_REDIS_SCHESIGN_KEY, scheSign);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.writeScheSlave is error \n", e);
		}

	}

	public static String readScheSign(String taskID) {
		logger.info(":::enter ContentErrorToRedis.readScheSign:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		try {
			return Redis.shard.hget(CEdict.REDIS_CE_DATA_KEY + taskID,
					CEdict.CE_REDIS_SCHESIGN_KEY);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readScheSign is error \n", e);
			return null;
		}
	}

	public static void writeDbType(String taskID, String dbType) {
		logger.info(":::enter ContentErrorToRedis.writeDbType:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		logger.info(":::dbType=" + dbType);
		try {
			Redis.shard.hset(CEdict.REDIS_CE_DATA_KEY + taskID,
					CEdict.CE_REDIS_DBTYPE_KEY, dbType);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.writeDbType is error \n", e);
		}
	}

	public static String readDbType(String taskID) {
		logger.info(":::enter ContentErrorToRedis.readDbType:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		try {
			return Redis.shard.hget(CEdict.REDIS_CE_DATA_KEY + taskID,
					CEdict.CE_REDIS_DBTYPE_KEY);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readDbType is error \n", e);
			return null;
		}
	}

	public static void writeWorkerNum(String taskID, Integer workerNum) {
		logger.info(":::enter ContentErrorToRedis.writeWorkerNum:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		logger.info(":::workerNum=" + workerNum);
		try {
			Redis.shard.hset(CEdict.REDIS_CE_DATA_KEY + taskID,
					CEdict.CE_REDIS_WORKERNUM_KEY, workerNum.toString());
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.writeWorkerNum is error \n", e);
		}
	}

	public static String readWorkerNum(String taskID) {
		logger.info(":::enter ContentErrorToRedis.readWorkerNum:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		try {
			return Redis.shard.hget(CEdict.REDIS_CE_DATA_KEY + taskID,
					CEdict.CE_REDIS_WORKERNUM_KEY);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readWorkerNum is error \n", e);
			return null;
		}
	}

	public static void writeOffset(String topic, Long offset, Integer partition) {
		logger.info(":::enter ContentErrorToRedis.writeOffset:::");
		logger.info(":::param list:::");
		logger.info(":::topic=" + topic);
		logger.info(":::offset=" + offset);
		logger.info(":::partition=" + partition);
		try {
			Redis.shard.set(
					CEdict.REDIS_CE_DATA_KEY + topic + "|"
							+ partition.toString(), offset.toString());
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.writeOffset is error \n", e);
		}
	}

	public static String readOffset(String topic, Integer partition) {
		logger.info(":::enter ContentErrorToRedis.readOffset:::");
		logger.info(":::param list:::");
		logger.info(":::topic=" + topic);
		logger.info(":::partition=" + partition);
		try {
			return Redis.shard.get(CEdict.REDIS_CE_DATA_KEY + topic + "|"
					+ partition.toString());
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readOffset is error \n", e);
			return null;
		}
	}

	public static void writeSpare(String taskID, String spare) {
		logger.info(":::enter ContentErrorToRedis.writeSpare:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		logger.info(":::spare=" + spare);
		try {
			Redis.shard.hset(CEdict.REDIS_CE_DATA_KEY + taskID,
					CEdict.CE_REDIS_SPARE_KEY, spare);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.writeSpare is error \n", e);
		}
	}

	public static String readSpare(String taskID) {
		logger.info(":::enter ContentErrorToRedis.readSpare:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		try {
			return Redis.shard.hget(CEdict.REDIS_CE_DATA_KEY + taskID,
					CEdict.CE_REDIS_SPARE_KEY);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readSpare is error \n", e);
			return null;
		}
	}

	public static void writeWorkerSign(String taskID, String worker, String sign) {
		logger.info(":::enter ContentErrorToRedis.writeWorkerSign:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		logger.info(":::worker=" + worker);
		logger.info(":::sign=" + sign);
		try {
			Redis.shard.hset(CEdict.REDIS_CE_WORKER_SIGN_KEY + taskID, worker,
					sign);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.writeWorkerSign is error \n", e);
		}
	}

	public static Map<String, String> readWorkerSignMap(String taskID){
		logger.info(":::enter ContentErrorToRedis.readWorkerSignMap:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		try {
			return Redis.shard.hgetAll(CEdict.REDIS_CE_WORKER_SIGN_KEY + taskID);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readWorkerSign is error \n", e);
			return null;
		}
	}
	
	public static String readWorkerSign(String taskID, String worker) {
		logger.info(":::enter ContentErrorToRedis.readWorkerSign:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		logger.info(":::worker=" + worker);
		try {
			return Redis.shard.hget(CEdict.REDIS_CE_WORKER_SIGN_KEY + taskID,
					worker);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readWorkerSign is error \n", e);
			return null;
		}
	}

	public static void writeSlavePid(String taskID, String slave, Integer pid) {
		logger.info(":::enter ContentErrorToRedis.writeSlavePid:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		logger.info(":::slave=" + slave);
		logger.info(":::pid=" + pid);
		try {
			Redis.shard.hset(CEdict.REDIS_CE_SLAVE_PID_KEY + taskID, slave,
					pid.toString());
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.writeSlavePid is error \n", e);
		}
	}

	public static String readSlavePid(String taskID, String worker) {
		logger.info(":::enter ContentErrorToRedis.readSlavePid:::");
		logger.info(":::param list:::");
		logger.info(":::taskID=" + taskID);
		logger.info(":::worker=" + worker);
		try {
			return Redis.shard.hget(CEdict.REDIS_CE_SLAVE_PID_KEY + taskID,
					worker);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readSlavePid is error \n", e);
			return null;
		}
	}

	public static void writeTopicGroup(String topic, String group) {
		logger.info(":::enter ContentErrorToRedis.writeTopicGroup:::");
		logger.info(":::param list:::");
		logger.info(":::topic=" + topic);
		logger.info(":::group=" + group);
		try {
			Redis.shard.set(CEdict.REDIS_CE_KAFKA_OFFSET_KEY + topic, group);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.writeTopicGroup is error \n", e);
		}
	}

	public static String readTopicGroup(String topic) {
		logger.info(":::enter ContentErrorToRedis.readTopicGroup:::");
		logger.info(":::param list:::");
		logger.info(":::topic=" + topic);
		try {
			return Redis.shard.get(CEdict.REDIS_CE_KAFKA_OFFSET_KEY + topic);
		} catch (Exception e) {
			logger.error("ContentErrorToRedis.readTopicGroup is error \n", e);
			return null;
		}
	}
}
