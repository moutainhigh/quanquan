package com.sendtask.usergroup.zhoujia.subsribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.sendtask.usergroup.zhoujia.utils.SystemUtils;

import redis.clients.jedis.JedisPubSub;

/**
 * @author zhoujia
 *
 * @date 2015年10月31日
 */
public class ToExit extends JedisPubSub{
	private static Logger logger = LoggerFactory.getLogger(ToExit.class);
	
	@Override
	public void onMessage(String channel, String message) {
		logger.info("自杀管道收到 信息 = = = = = = =" + message);
		int pid = SystemUtils.getPid();
		if(message != null && !message.isEmpty()){
			int parseInt = Integer.parseInt(message);
			if(pid == parseInt){
				logger.info("关闭分组 。。。。。。 pid = " + pid);
				System.exit(0);
			}
		}
		
	}
	
}
