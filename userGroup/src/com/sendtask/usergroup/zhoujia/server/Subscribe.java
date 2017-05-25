package com.sendtask.usergroup.zhoujia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.circle.core.util.Config;
import com.sendtask.usergroup.zhoujia.subsribe.SubscribeThread;
import com.sendtask.usergroup.zhoujia.subsribe.ToExit;
import com.sendtask.usergroup.zhoujia.utils.StaticParam;

/**
 * @author zhoujia
 *
 * @date 2015年9月2日
 */
public class Subscribe {
	private static Logger logger = LoggerFactory.getLogger(Subscribe.class);
	
	/***
	 * 自杀管道名
	 */
	public static final String killMySelfChannel = "groupKillMyself";
	
	String[] hostAndPort = null;
	
	public Subscribe() {
		try {
			Config config = new Config(StaticParam.config_path + "group_conf.properties");
			String asString = config.getAsString("redis.incr.nodes");
			hostAndPort = asString.split(",");
		} catch (Exception e) {
			logger.error("初始化redis失败，请检查redis配置",e);
		}
	}
	
	
	
	/***
	 * 订阅发来的pid，如果是自己的pid，则自杀，结束本分组进程
	 */
	public void subscribeKillself(){
		logger.info("启动监听通道 = = = " + killMySelfChannel);
		try {
			for (String string : hostAndPort) {
				Jedis jedis = new Jedis(string.split(":")[0],Integer.parseInt(string.split(":")[1]));
				JedisPubSub jp = new ToExit();
				//jedisPubSub
				logger.info("开始监听自杀管道");
				SubscribeThread st = new SubscribeThread(jedis, jp, killMySelfChannel);
				st.start();
			}
			
		} catch (Exception e) {
			logger.error("subscribeKillself 监听异常:" , e);
		}
	}
	

}
