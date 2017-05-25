package com.rulesfilter.yy.zj.thread;

import com.circle.core.redis.incr.ReConnectPublish;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * @author zhoujia
 *
 * @date 2015年9月16日
 */
public class SubscribeThread extends Thread{
//	private Jedis jedis ;
	private ReConnectPublish jp;
	private String channel;
	private HostAndPort hap;
	public SubscribeThread(ReConnectPublish jp,String channel,HostAndPort hap) {
//		this.jedis = jedis;
		this.jp = jp;
		this.channel = channel;
		this.hap = hap;
	}
	
	@Override
	public void run() {
		//jedis.subscribe(jp, channel);
		jp.subscribe(hap,channel);
	}
}
