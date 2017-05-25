package com.sendtask.contentError.kill;

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
public class KillSubscribe {
	private static Logger logger = LoggerFactory.getLogger(KillSubscribe.class);

	public Boolean subscribe(final String mid) {

		logger.info("mid == = = = = = = =  = == " + mid);
		try {
			Map<Integer, HostAndPort> nodes = Redis.CONNECT.getNodes();
			for (Map.Entry<Integer, HostAndPort> entry : nodes.entrySet()) {
				final Jedis jedis = new Jedis(entry.getValue().getHost(), entry
						.getValue().getPort());
				final KillSubpub ks = new KillSubpub();
				new Thread() {
					public void run() {
						jedis.subscribe(ks, mid);
					}
				}.start();
			}
			return true;
		} catch (Exception e) {
			logger.error("监听异常，mid=" + mid, e);
		}
		return false;
	}

}
