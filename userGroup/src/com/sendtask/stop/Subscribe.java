package com.sendtask.stop;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import com.circle.core.redis.Redis;

/**
 * redis监听
 * 
 * @author zhoujia
 */
public class Subscribe {
	private static Logger logger = LoggerFactory.getLogger(Subscribe.class);

	public Boolean subscribe(final String mid) throws Exception{
		logger.info("mid == = = = = = = =  = == " + mid);
		Map<Integer, HostAndPort> nodes = Redis.CONNECT.getNodes();
		for (Map.Entry<Integer, HostAndPort> entry : nodes.entrySet()) {
			final Jedis jedis = new Jedis(entry.getValue().getHost(), entry.getValue().getPort());
			final StopSubpub ks = new StopSubpub();
			new Thread() {
				public void run() {
					jedis.subscribe(ks, mid);
				}
			}.start();
		}
		return true;
	}

}
