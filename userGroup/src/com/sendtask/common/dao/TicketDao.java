package com.sendtask.common.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.sendtask.common.model.BaseTK;

public class TicketDao {
	private Logger logger = LoggerFactory.getLogger(TicketDao.class);

	/**
	 * 删除各种券规则列表
	 * 
	 * @param cName
	 * @param redisKey
	 * @param delId
	 *            要删除的ID
	 */
	@SuppressWarnings("rawtypes")
	public void delRules(Class cName, String redisKey, String delId) {
		logger.info(":::==============delid=" + delId);
		List<String> list = Redis.shard.lrange(redisKey, 0,
				Redis.shard.llen(redisKey));
		String delData = null;
		for (String str : list) {
			logger.info(":::==============redisKey=" + redisKey);
			logger.info(":::str==="+str);
			logger.info(":::cName==="+cName.toString());
			try {
				BaseTK tk = (BaseTK) Json.jsonParser(str, cName);
				if (delId.equals(tk.getA())) {
					delData = str;
					continue;
				}
			} catch (Exception e) {
				//券规则格式有问题，转换失败跳过
				logger.info(":::ticket rule format is error, convert fail, ticket data === " + str + "\n", e);
			}
		}
		logger.info("del redis[" + redisKey + "],delData["+delData+"]=====");
		Redis.shard.lrem(redisKey, 0, delData);
	}
}
