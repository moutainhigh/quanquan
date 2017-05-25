package com.rulesfilter.yy.zj.subscrib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.rulesfilter.yy.zj.utils.ParamStatic;

/**
 * @author zhoujia
 *
 * @date 2015年8月12日
 */
public class SingalLister extends Thread{
	private static Logger logger = LoggerFactory.getLogger(SingalLister.class);
	private String host;
	
	private int port;
	
	public SingalLister(String host,int port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			logger.info("开始订阅用户临时key,host=" + host + " prot="+port + " 管道=" +ParamStatic.key_timeout_channel +" 和 " + ParamStatic.key_timeout_channel_nosee);
			Jedis jedis = new Jedis(host,port);
			JedisPubSub jp = new DelTimeOutkey();
			//jedisPubSub
			//jedis.subscribe(jp, ParamStatic.key_timeout_channel);
			jedis.psubscribe(jp, ParamStatic.key_timeout_channel,ParamStatic.key_timeout_channel_nosee);
		}catch (Exception e){
			logger.error("SingalLister run 异常：",e);
		}

		
	}

}
