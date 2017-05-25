package com.rulesfilter.yy.zj.subscrib;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import redis.clients.jedis.HostAndPort;
//import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;




import com.circle.core.redis.incr.ReConnectPublish;
//import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.rulesfilter.yy.zj.model.FilterRule;
import com.rulesfilter.yy.zj.thread.SubscribeThread;
import com.rulesfilter.yy.zj.utils.ParamStatic;

/**
 * @author zhoujia
 *
 * @date 2015年8月12日
 */
public class Subscribe {

	private static Logger logger = LoggerFactory.getLogger(Subscribe.class);
	
	String[] hostAndPort = null;
	
	public static final String addChannel = "addNewReportChannel";
	
	public static final String delChannel = "delNewReportChannel";
	
	public Subscribe(String configPath) {
		try {
			Config config = new Config(configPath + ParamStatic.configFileName);
			String asString = config.getAsString("redis.incr.nodes");
			hostAndPort = asString.split(",");
		} catch (Exception e) {
			logger.error("初始化redis失败，请检查redis配置",e);
		}
	}
	
	/***
	 * 订阅 filterName 来的信息
	 * @param filterName
	 * @param pfRule
	 * @return
	 */
	public void subscribe(String filterName,FilterRule pfRule){
		try {
			//HostAndPort hostAndPort = Redis.CONNECT.hostAndPort(filterName); 
			for (String string : hostAndPort) {
				//Jedis jedis = new Jedis(string.split(":")[0],Integer.parseInt(string.split(":")[1]));
				
				
				String name2 = pfRule.getClass().getName();
				logger.info("------------------------》开始添加订阅："+string+"《 ------------------------");
				if(name2.endsWith("ProcessFilterRule")){
					logger.info("---------------------------过程："+pfRule.getFilgerRuleId()+"-----------------------------");
					ReConnectPublish jp = new FilterSubpub(pfRule);
					jp.subscribe(new HostAndPort(string.split(":")[0], Integer.parseInt(string.split(":")[1])), filterName);
//					SubscribeThread st = new SubscribeThread(jp, filterName, new HostAndPort(string.split(":")[0], Integer.parseInt(string.split(":")[1])));
//					st.start();
				}else if(name2.endsWith("PointToPointRule")){
					logger.info("----------------------------点对点"+pfRule.getFilgerRuleId()+"---------------------------");
					ReConnectPublish jp = new PTPSubpub(pfRule);
					jp.subscribe(new HostAndPort(string.split(":")[0], Integer.parseInt(string.split(":")[1])), filterName);
//					SubscribeThread st = new SubscribeThread(jp, filterName,new HostAndPort(string.split(":")[0], Integer.parseInt(string.split(":")[1])));
//					st.start();
				}
			}
			
		} catch (Exception e) {
			logger.error("监听异常，filterName=" + filterName, e);
		}
	}
/*	*//***
	 * 解除对应规则的redis-channel的监听
	 * @param pfRule
	 * @param delType 解除类型， 0 过程规则， 1 点对点规则
	 *//*
	public void unSubpub(FilterRule pfRule,int delType){
		logger.info("解除监听 = " + pfRule.getFilgerRuleId() + " 类型=" + delType);
		if(delType == 0){
			List<FilterSubpub> list = FilterSubpub.hasActiveFilterRules;
			FilterSubpub fs = null;
			for (FilterSubpub filterSubpub : list) {
				if(filterSubpub.getPfRule().getFilgerRuleId().equals(pfRule.getFilgerRuleId())){
					fs = filterSubpub;
					break;
				}
			}
			if(fs == null){
				logger.error("这个过程过滤规则没有激活，不能删除", pfRule.getFilgerRuleId());
			}else{
				fs.unsubscribe();
			}
		}else if(delType == 1){
			List<PTPSubpub> ptpList = PTPSubpub.hasActivePtPRule;
			PTPSubpub ptp = null;
			for (PTPSubpub ptpSubpub : ptpList) {
				if(ptpSubpub.getPtpRule().getFilgerRuleId().equals(pfRule.getFilgerRuleId())){
					ptp = ptpSubpub;
					break;
				}
			}
			if(ptp == null){
				logger.error("这个点对点过滤规则没有激活，不能删除",pfRule.getFilgerRuleId());
			}else{
				ptp.unsubscribe();
			}
		}
	}*/
	
	
	
	
	/***
	 * 订阅 filterName 来的信息，存入hbase中，此记录为答题日志记录
	 * 订阅队列中的答题日志，每来一条则存入hbase中，如果这个log被刷单规则命中，则修改命中标志位
	 * @param filterName
	 * @return
	 */
	public void subscribeHbase(String filterName){

		try {
			for (String string : hostAndPort) {
//				Jedis jedis = new Jedis(string.split(":")[0],Integer.parseInt(string.split(":")[1]));
				ReConnectPublish jp = new RecordToHbase();
				//jedisPubSub
				logger.info("开始监听hbase log 记录 管道，每次来信息都会写入hbase中。。。。。。 " + filterName);
				jp.subscribe(new HostAndPort(string.split(":")[0], Integer.parseInt(string.split(":")[1])), filterName);
				
//				SubscribeThread st = new SubscribeThread(jp, filterName,new HostAndPort(string.split(":")[0], Integer.parseInt(string.split(":")[1])));
//				st.start();
			}
			
		} catch (Exception e) {
			logger.error("subscribeHbase 监听异常，filterName=" + filterName, e);
		}
	}
	
	
	
	
//	/***
//	 * 订阅 filterName 来的信息
//	 * @param filterName
//	 * @param pfRule
//	 * @return
//	 */
//	public Jedis subscribe(String filterName,PointToPointRule ptpRule){
//
//		//Redis.CONNECT.publish(key, message)
//		
//		try {
//			HostAndPort hostAndPort = Redis.CONNECT.hostAndPort(filterName); 
//			 
//			Jedis jedis = new Jedis(hostAndPort.getHost(),hostAndPort.getPort());
//			JedisPubSub jp = new PTPSubpub(ptpRule);
//			//jedisPubSub
//			jedis.subscribe(jp, filterName);
//			
//			return jedis;
//		} catch (Exception e) {
//			logger.error("监听异常，filterName=" + filterName, e);
//		}
//		return null;
//	}
	
	
	/**
	 * 添加新的过滤规则订阅
	 */
	public void subscribeAddNewRule(){
		for (String string : hostAndPort){
			ReConnectPublish addJp = new AddNewPubSub(this);
			ReConnectPublish delJp = new DelPubSub();
			logger.info("添加管道监控开始监听 " + addChannel + " " +string);
			addJp.subscribe(new HostAndPort(string.split(":")[0], Integer.parseInt(string.split(":")[1])), addChannel);
			logger.info("删除管道监听开始监听" + delChannel + " " +string);
			delJp.subscribe(new HostAndPort(string.split(":")[0], Integer.parseInt(string.split(":")[1])), delChannel);
		}
	}
	
	
	
	
	/**
	 * 订阅每个节点，如果有一个节点收到信息，则说明用户日志过期,过期之后删除日志中的过期日志
	 * 
	 * 需要redis设置key事件监听功能开放
	 * redis-cli config set notify-keyspace-events KEA
	 */
	public void subscribeDelKey(String configPath){
		try {
			
			Config conf = new Config(configPath + ParamStatic.configFileName);
	    	String hosts = conf.getAsString("redis.incr.nodes");
	    	String[] split = hosts.split(",");
	    	for (final String  string : split) {
	    		SingalLister sl = new SingalLister(string.split(":")[0], Integer.parseInt(string.split(":")[1]));
	    		sl.start();
				logger.info("监听临时key开始");
			}
			
		} catch (Exception e) {
			logger.error("监听异常，key事件监听异常=" + ParamStatic.key_timeout_channel, e);
		}
	}
	
	
}






