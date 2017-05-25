package com.sendtask.usergroup.zhoujia.subsribe;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * @author zhoujia
 *
 * @date 2015年10月31日
 */
public class SubscribeThread extends Thread{
	
	private Jedis jedis ;
	private JedisPubSub jp;
	private String channel;
	public SubscribeThread(Jedis jedis,JedisPubSub jp,String channel) {
		this.jedis = jedis;
		this.jp = jp;
		this.channel = channel;
	}
	@Override
	public void run() {
		jedis.subscribe(jp, channel);
	}
}
