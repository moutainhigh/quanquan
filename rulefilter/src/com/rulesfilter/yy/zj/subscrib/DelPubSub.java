package com.rulesfilter.yy.zj.subscrib;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.circle.core.redis.incr.ReConnectPublish;

import redis.clients.jedis.JedisPubSub;

/**
 * @author zhoujia
 *
 * @date 2015年11月20日
 */
public class DelPubSub extends ReConnectPublish{
	private static Logger logger = LoggerFactory.getLogger(DelPubSub.class);
	public DelPubSub() {
		this.delay_check=20000;
		this.delay_connect = 10000;
		this.delay_wait = 60000;
	}
	@Override
	public void onMessage(String channel, String message) {
		boolean checkreconnect = checkreconnect(channel, message);
		if(message.equals("close")){
			close();
		}
		if(!checkreconnect){
			//message 的格式： type|ruleId
			logger.info("删除监听管道收到消息 message =" + message);
			String[] split = message.split("\\|");
			if(split != null && split.length == 2 && message.contains("|")){
				this.unSubpub(split[1], Integer.parseInt(split[0]));
			}else{
				logger.error("删除管道接受到的删除信息格式错误=" + message);
			}
		}
		
	}

	
	/***
	 * 解除对应规则的redis-channel的监听
	 * @param pfRule
	 * @param delType 解除类型， 1 过程规则， 2 点对点规则
	 */
	public void unSubpub(String FilterRuleId,int delType){
		logger.info("解除监听 = " + FilterRuleId + " 类型=" + delType);
		if(delType == 1){//过程规则
			List<FilterSubpub> list = FilterSubpub.hasActiveFilterRules;
			logger.info("list size = = " + list.size());
			for (FilterSubpub filterSubpub : list) {
				logger.info("unSubpub " + filterSubpub.getPfRule().getFilgerRuleId());
			}
			FilterSubpub fs = null;
			for (FilterSubpub filterSubpub : list) {
				if(filterSubpub.getPfRule().getFilgerRuleId().equals(FilterRuleId)){
					fs = filterSubpub;
					break;
				}
			}
			if(fs == null){
				logger.error("这个过程过滤规则没有激活，不能删除", FilterRuleId);
			}else{
				fs.close();
				//fs.unsubscribe();
				FilterSubpub.hasActiveFilterRules.remove(fs);
				logger.info("解除 " + FilterRuleId + " 的监听");
			}
		}else if(delType == 2){//点对点规则
			List<PTPSubpub> ptpList = PTPSubpub.hasActivePtPRule;
			PTPSubpub ptp = null;
			for (PTPSubpub ptpSubpub : ptpList) {
				if(ptpSubpub.getPtpRule().getFilgerRuleId().equals(FilterRuleId)){
					ptp = ptpSubpub;
					break;
				}
			}
			if(ptp == null){
				logger.error("这个点对点过滤规则没有激活，不能删除",FilterRuleId);
			}else{
				ptp.close();
				//ptp.unsubscribe();
				PTPSubpub.hasActivePtPRule.remove(ptp);
				logger.info("解除" + FilterRuleId + " 的监听");
			}
		}
	}
}
