package com.sendtask.stop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

/**
 * redis监听
 * 
 * @author zhoujia
 */
public class StopSubpub extends JedisPubSub {
	private static Logger logger = LoggerFactory.getLogger(JedisPubSub.class);

	@Override
	public void onMessage(String channel, String message) {
		super.onMessage(channel, message);
		logger.info("服务端来通知了，通知message=" + message);
		Stop.stop(message);
	}
}
