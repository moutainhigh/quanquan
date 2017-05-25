package com.sendtask.contentError.kill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

/**
 * redis监听
 * 
 * @author zhoujia
 */
public class KillSubpub extends JedisPubSub {
	private static Logger logger = LoggerFactory.getLogger(JedisPubSub.class);

	// private KillSubpub ss ;

	@Override
	public void onMessage(String channel, String message) {
		super.onMessage(channel, message);
		logger.info("服务端来通知了，通知message=" + message);
		logger.info("要死了。。。。=。=");
		System.exit(0);
	}
}
