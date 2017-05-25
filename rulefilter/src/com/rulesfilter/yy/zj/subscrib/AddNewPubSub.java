package com.rulesfilter.yy.zj.subscrib;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.redis.incr.ReConnectPublish;
import com.rulesfilter.yy.zj.model.PointToPointRule;
import com.rulesfilter.yy.zj.model.ProcessFilterRule;
import com.rulesfilter.yy.zj.utils.ParamStatic;

import redis.clients.jedis.JedisPubSub;

/**
 * 
 * 后台管理端添加新的规则，会发送新的消息，这个方法订阅这个管道，如果收到消息，则去redis中取出这个消息，
 * 把这个消息的监控加上
 * 
 * @author zhoujia
 *
 * @date 2015年8月29日
 */
public class AddNewPubSub extends ReConnectPublish{
	private static Logger logger = LoggerFactory.getLogger(AddNewPubSub.class);
	
	private static String prcessType = "prcess";
	
	private static String pointType = "point";
	
	private Subscribe ss;
//	private String configPath;
//	public AddNewPubSub(String configPath) {
//		this.configPath = configPath;
//	}
	public AddNewPubSub(Subscribe ss) {
		this.ss = ss;
		
		this.delay_check=20000;
		this.delay_connect = 10000;
		this.delay_wait = 60000;
	}
	/**
	 * message 是订阅来的信息格式，格式为   type|json	 例如 prcess|{xxx:xxx, xxx:xxx} 面向对象规则   , point|{xxx:xxx} 点对点规则
	 */
	@Override
	public void onMessage(String channel, String message) {
		boolean checkreconnect = checkreconnect(channel, message);
		if(!checkreconnect){
			logger.info("添加新的过滤规则， message=" + message);
			String[] split = message.split("\\|");
			if(split.length == 2){
				if(prcessType.equals(split[0])){//如果添加了面向过程规则
					ProcessFilterRule pfRule = (ProcessFilterRule)Json.jsonParser(split[1], ProcessFilterRule.class);
					boolean s = false;
					for (FilterSubpub pRule : FilterSubpub.hasActiveFilterRules) {
						if(pRule.getPfRule().getFilgerRuleId().equals(pfRule.getFilgerRuleId())){
							//说明这个规则已经有了，不用添加了
							s = true;
							break;
						}
					}
					if(!s){//如果这个规则没有添加订阅
						ss.subscribe(ParamStatic.filterChannel,pfRule); // 新的规则订阅
						logger.info("添加了新的面向过程过滤规则=" + split[1]);
					}else{
						logger.info("这个过程规则已经启动，无需再次启动" + pfRule.getFilgerRuleId());
					}
				}
				
				if(pointType.equals(split[0])){//如果添加了点对点规则
					boolean s = false;
					PointToPointRule ptpRule = (PointToPointRule)Json.jsonParser(split[1], PointToPointRule.class);
					for (PTPSubpub ptpRule1 : PTPSubpub.hasActivePtPRule) {
						if(ptpRule1.getPtpRule().getFilgerRuleId().equals(ptpRule.getFilgerRuleId())){
							//已经启动了
							s = true;
							break;
						}
					}
					if(!s){//如果这个规则没有添加订阅
						ss.subscribe(ParamStatic.filterChannel,ptpRule);
						logger.info("添加了新的点对点过滤规则=" + split[1]);
					}else{
						logger.info("这个点对点规则已经启动，无需再次启动" + ptpRule.getFilgerRuleId());
					}
				}
			}else{
				logger.error("管道接受到的信息格式不对，请检查信息格式！");
			}
		}
	}
}
