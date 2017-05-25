package com.sm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.circle.core.redis.Redis;
import com.sm.exception.NoAvaileableNodeException;
import com.sm.model.NodeEtcInfo;
import com.sm.model.SMTask;
import com.sm.model.Schedule;
import com.sm.util.StaticParam;
import com.sm.util.ZookeeperAPI;

/** 
 * @author  zhoujia 
 * @date 创建时间：2015年7月16日 下午2:34:14   
 * 
 * 任务调度类
 */
public class ManageTask {
	
	private Logger logger = LoggerFactory.getLogger(ManageTask.class);
	/**
	 * 每台机器的负载情况
	 */
	public static Map<String, String> etcInfo = new HashMap<String, String>();
	
	/**
	 * 可用节点
	 */
	public List<String> availableSlave = new ArrayList<String>();
	
	/**
	 * 可用节点, 发题节点
	 */
	public List<String> questionSlave = new ArrayList<String>();
	
	/**redis节点总数量**/
	private static Integer redisNodesNum = 3;
	
	//初始化可用节点
	public void initCommonSlaveNode(){
		try {
			getMostEasySchedule();
			logger.info("普通任务 可用节点 =========== ",availableSlave);
		} catch (NoAvaileableNodeException e) {
			logger.error("没有可用节点，所有节点都很忙，请添加机器",e);
		}
	}
	//初始化可用节点
	public void initQuestionSlaveNode(){
		try {
			getMostEasyQuestionSchedule();
			logger.info("发题任务可用节点 ============ ",questionSlave);
		} catch (NoAvaileableNodeException e) {
			logger.error("没有可用节点，所有节点都很忙，请添加机器",e);
		}
	}
	
	/**
	 * 遍历任务，每次把任务分配到空闲的机器上（schedule）。
	 * @param tasklist
	 */
	public void sendTaskToSchedule(List<SMTask> tasklist) throws NoAvaileableNodeException{
		//ZookeeperAPI zkClient = ZookeeperAPI.getZKClient();
		logger.info("普通任务:tasklist长度："+tasklist.size());
		for (SMTask smTask : tasklist) {
			String ruleType=smTask.getDataType();
			logger.info("ruleType  = == " + ruleType);
			Schedule mostEasySchedule = getMostEasySchedule();
			logger.info("mostEasySchedule  = == " + mostEasySchedule);
			List<String> bakNode = new ArrayList<String>();
			if(availableSlave == null || availableSlave.size() <=0){
				logger.error("没有可用备机");
				logger.error("no available node ......");
				throw new NoAvaileableNodeException("没有可用节点，所有节点都十分繁忙");
			}else{
				for(int i=0; true; i++){
					if(bakNode.size() >= ZookeeperAPI.contentErrorBakNum){//根据配置选择备机，如果选择够配置数量则跳出
						break;
					}else {
						try {
							if(!availableSlave.get(i).equals(mostEasySchedule.getHost())){ // 如果这个机器不是schedule，则可以作为备机
								bakNode.add(availableSlave.get(i));
							}
						} catch (IndexOutOfBoundsException e) {
							logger.error("普通任务备机数量小于配置数量，无法完成配置，请添加节点！！！");
							break;
							//logger.error("the bak node less then config num ，can't finish config ，exit！！！");
							//System.exit(0);
						}
					}
				}
			}
			StringBuffer tasks = new StringBuffer();
			tasks.append(mostEasySchedule.getHost()).append(":").append(ruleType).append(":")
			.append(smTask.getTaskID()).append(":");
			for(int i=0;i<bakNode.size();i++){//拼接备机字符串
				if(i == bakNode.size()-1){
					tasks.append(bakNode.get(i));
				}else{
					tasks.append(bakNode.get(i)).append(",");
				}
			}
			tasks.append(";");
			//往zookeeper节点上写信息，分发任务到 host 
			//zkClient.writeData(ZookeeperAPI.dataTask, tasks.toString());
			//修改分配任务为redis消息队列，因为担心zookeeper性能和压力问题。
			logger.info("-------------->>发送到 " + mostEasySchedule.getHost()+"<<------------------");
			logger.info("---------------->>task_id:"+smTask.getTaskID() +"<<------------------");
			logger.info("-------------->>备机为: " + bakNode + "<<------------------");
			int random = new Random().nextInt(redisNodesNum);
			Jedis jedis = Redis.CONNECT.resource(String.valueOf(random)); // 随机找个节点，分发任务
			jedis.publish(ZookeeperAPI.commonTaskChannel, tasks.toString());
		}
		
		
	}
	
	/**
	 * 遍历任务，每次把任务分配到空闲发题的机器上（schedule）。
	 * @param tasklist
	 */
	public void sendTaskToFatiSchedule(List<SMTask> tasklist) throws NoAvaileableNodeException{
		//ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		logger.info("tasklist长度："+tasklist.size());
		for (SMTask smTask : tasklist) {
			Schedule mostEasySchedule = getMostEasyQuestionSchedule();
			String ruleType=smTask.getDataType();

			//计算备用节点
			List<String> bakNode = new ArrayList<String>();
			if(questionSlave == null || questionSlave.size() <=0){
				logger.error("没有发题可用备机");
				throw new NoAvaileableNodeException("没有可用节点，所有节点都十分繁忙");
			}else{
				for(int i=0; true; i++){
					if(bakNode.size() >= ZookeeperAPI.contentErrorBakNum){//根据配置选择备机，如果选择够配置数量则跳出
						break;
					}else {
						try {
							if(!questionSlave.get(i).equals(mostEasySchedule.getHost())){ // 如果这个机器不是schedule，则可以作为备机
								bakNode.add(questionSlave.get(i));
							}
						} catch (IndexOutOfBoundsException e) {
							logger.error("发题可用备机数量小于配置数量，无法完成配置，请添加机器！！！",e);
							break;
						}
					}
				}
			}
			
			//host:R:TaskId:(ques-1,1ues-2,ques-3)备机字符串:
			StringBuffer tasks = new StringBuffer();
			tasks.append(mostEasySchedule.getHost()).append(":").append(ruleType).append(":")
			.append(smTask.getTaskID()).append(":"); //.append(questionBak).append(";")
			for(int i=0;i<bakNode.size();i++){//拼接备机字符串
				if(i == bakNode.size()-1){
					tasks.append(bakNode.get(i));
				}else{
					tasks.append(bakNode.get(i)).append(",");
				}
			}
			tasks.append(";");
			logger.info("-------------->>发送到 " + mostEasySchedule.getHost()+"<<------------------");
			logger.info("---------------->>问题id:"+smTask.getRule().getQid() +"<<------------------");
			logger.info("---------------->>task_id:"+smTask.getTaskID() +"<<------------------");
			logger.info("-------------->>备机为： " + bakNode +"<<------------------");
			//往zookeeper节点上写信息，分发任务到 host 
			//zkClient.writeData(ZookeeperAPI.dataTask, tasks.toString());
			//修改分配任务为redis消息队列，因为担心zookeeper性能和压力问题。
			int random = new Random().nextInt(redisNodesNum);
			Jedis jedis = Redis.CONNECT.resource(String.valueOf(random)); // 随机找个节点，分发任务
			//问题管道为前缀加hostname
			jedis.publish(ZookeeperAPI.questionTaskChannelPrefix + mostEasySchedule.getHost(), tasks.toString());
		}
		
		
		
	}
	
	
	private Schedule getMostEasyQuestionSchedule() throws NoAvaileableNodeException{
		Map<String, NodeEtcInfo> scheduleMap = new HashMap<String, NodeEtcInfo>();
		questionSlave.clear();
		etcInfo.clear();
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(StaticParam.CONFIG_URL);
		//List<String> childList = zkClient.getChildList(ZookeeperAPI.etcSlave);
		List<String> questionList = zkClient.getChildList(ZookeeperAPI.etcQuestionSlave);
		
		logger.info("questionList = = = = = = = = = = == ooooooo "+questionList.toString());
		
		for (String etcQuestionPath : questionList) {
			etcInfo.put(etcQuestionPath, zkClient.readData(ZookeeperAPI.etcQuestionSlave+ "/" +etcQuestionPath));
		}
		//ZookeeperService zkService = new ZookeeperService();
		for(String nodeFile: questionList){
			if(nodeFile.equals(ZookeeperAPI.masterName)){//如果是主节点跳过，任务不能分发到主节点
				logger.error("节点是主节点！不能分配任务！");
				continue;
			}
			String etcInfos = etcInfo.get(nodeFile);
			logger.info("cpu percent = = == =  =" + getCupUesd(etcInfos));
			logger.info("memory percent = = == =  =" + getMemoryUsed(etcInfos) );
			if(getMemoryUsed(etcInfos) >= StaticParam.MEMORYPRESINT){//如果负载过高跳过  || getCupUesd(etcInfos) >= StaticParam.CPUPERSINT
				logger.error("节点：" + nodeFile + "负载过高，超过" + StaticParam.MEMORYPRESINT );
				continue;
			}else{
				questionSlave.add(nodeFile);
				NodeEtcInfo nodeEtcInfo = new NodeEtcInfo(getCupUesd(etcInfos), getMemoryUsed(etcInfos));
				logger.info("节点负载情况：==="+nodeEtcInfo);
				scheduleMap.put(nodeFile, nodeEtcInfo);
			}
		}
		if(questionSlave.size() <=0){
			throw new NoAvaileableNodeException("没有可用节点，所有节点都十分繁忙");
		}
		//cpu使用率最小的schedule
		String hostname = getHashNext(scheduleMap);//getMostlittleNum(scheduleMap);
		
		Schedule schedule = new Schedule();
		schedule.setHost(hostname);
		return schedule;
	}
	
	/**
	 * {CacheSize=6144, TotalSockets=4, Mhz=3300, Model=Core(TM) i5-4440 CPU @ 3.10GHz, TotalCores=4, Vendor=Intel, CoresPerSocket=16};
	 * CPU states: 9.6% user, 1.0% system, 0.0% nice, 3.0% wait, 86.2% idle;
	 * {User=3456760, SoftIrq=480, Idle=693590340, Stolen=0, Wait=594340, Total=698321840, Irq=0, Nice=31620, Sys=648300};
	 * Mem: 7897144K av, 4831364K used, 3065780K free;
	 * Swap: 8126460K av, 0K used, 8126460K free;
	 * 
	 * 计算最空闲机器 
	 * @return Schedule
	 */
	private Schedule getMostEasySchedule() throws NoAvaileableNodeException{
		Map<String, NodeEtcInfo> scheduleMap = new HashMap<String, NodeEtcInfo>();
		availableSlave.clear();
		etcInfo.clear();
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(StaticParam.CONFIG_URL);
		List<String> childList = zkClient.getChildList(ZookeeperAPI.etcSlave);
		
		for (String etcPath : childList) {
			etcInfo.put(etcPath, zkClient.readData(ZookeeperAPI.etcSlave+ "/" +etcPath));
		}
		ZookeeperService zkService = new ZookeeperService();
		for(String nodeFile:zkService.getSendSlaveNodes()){
			if(nodeFile.equals(ZookeeperAPI.masterName)){//如果是主节点跳过，任务不能分发到主节点
				logger.error("节点是主节点！不能分配任务！");
				continue;
			}
			String etcInfos = etcInfo.get(nodeFile);
			if(getMemoryUsed(etcInfos)>= StaticParam.MEMORYPRESINT ){//如果负载过高跳过 || getCupUesd(etcInfos) >= StaticParam.CPUPERSINT
				logger.error("节点：" + nodeFile + "负载过高，超过" + StaticParam.CPUPERSINT);
				continue;
			}else{
				availableSlave.add(nodeFile);
				NodeEtcInfo nodeEtcInfo = new NodeEtcInfo(getCupUesd(etcInfos), getMemoryUsed(etcInfos));
				logger.info("节点负载情况：==="+nodeEtcInfo);
				scheduleMap.put(nodeFile, nodeEtcInfo);
			}
		}
		if(availableSlave.size() <= 0){//没有可用节点，所有节点都十分繁忙
			throw new NoAvaileableNodeException("没有可用节点，所有节点都十分繁忙");
		}
		
		//cpu使用率最小的schedule
		String hostname = getMostlittleNum(scheduleMap);
		
		Schedule schedule = new Schedule();
		schedule.setHost(hostname);
		return schedule;
	}
	
	private String getMostlittleNum(Map<String, NodeEtcInfo> scheduleMap){
		String mKey = null;
		//double tmp=100;
//		选取最小		
//		for (String key : scheduleMap.keySet()) {
//			if(scheduleMap.get(key)<tmp){
//				tmp = scheduleMap.get(key);
//				mKey = key;
//			}
//		}
		
		//只要这台机器负载小于60就取这台机器
		for (String key : scheduleMap.keySet()) {
			
			//if(scheduleMap.get(key).getCpuPercent() < StaticParam.CPUPERSINT &&  scheduleMap.get(key).getMemPercent() < StaticParam.MEMORYPRESINT){ //&& 内存小于 指定值
			if(scheduleMap.get(key).getMemPercent() < StaticParam.MEMORYPRESINT){ //&& 内存小于 指定值
				mKey = key;
				break;
			}
		}
		return mKey;
				
	}
	
	/***
	 * 随机获取一个可用的机器几点
	 * @param scheduleMap
	 * @return
	 */
	private String getHashNext(Map<String, NodeEtcInfo> scheduleMap) throws NoAvaileableNodeException{
		String mKey = null;
		List<String> keys = new ArrayList<String>();
		
		for (String key : scheduleMap.keySet()) {
			//if(scheduleMap.get(key).getCpuPercent() < StaticParam.CPUPERSINT &&  scheduleMap.get(key).getMemPercent() < StaticParam.MEMORYPRESINT){ //&& 内存小于 指定值
			if(scheduleMap.get(key).getMemPercent() < StaticParam.MEMORYPRESINT){ //&& 内存小于 指定值
				keys.add(key);
			}
		}
		Random random = new Random();
		int i = 0;
		if(keys.size() > 0){
			i = random.nextInt(keys.size());
		}else{
			throw new NoAvaileableNodeException("没有可用节点，所有节点都很忙");
		}
		
		mKey = keys.get(i);
		return mKey;
	}
	
	/**
	 * 读取信息中的cup占用率
	 * @param info
	 * @return
	 */
	private double getCupUesd(String info){
		try {
			if(info == null ||"".equals(info) ) 
				return 0;
			//logger.info("节点cpu负载信息："+info);
			String proc = info.split(";")[1];
			String user = proc.split(":")[1].split(",")[0];
			String persint = user.substring(0, user.indexOf("user"));
			persint = persint.substring(0, persint.indexOf("%"));
			return Double.parseDouble(persint);
		} catch (NumberFormatException e) {
			logger.debug("读取cpu占用率出错:",e);
		}
		return 0;
	}
	/**
	 * {CacheSize=6144, TotalSockets=4, Mhz=3300, Model=Core(TM) i5-4440 CPU @ 3.10GHz, TotalCores=4, Vendor=Intel, CoresPerSocket=16};
	 * CPU states: 9.6% user, 1.0% system, 0.0% nice, 3.0% wait, 86.2% idle;
	 * {User=3456760, SoftIrq=480, Idle=693590340, Stolen=0, Wait=594340, Total=698321840, Irq=0, Nice=31620, Sys=648300};
	 * Mem: 7897144K av, 4831364K used, 3065780K free;
	 * Swap: 8126460K av, 0K used, 8126460K free;
	 * 
	 * 计算最空闲机器 
	 * @return memoryUsed
	 */
	private double getMemoryUsed(String info){
		try {
			if(info == null ||"".equals(info) ) 
				return 0;
			//logger.info("节点内存负载信息："+info);
			String menInfo = info.split(";")[3];
			menInfo = menInfo.split(":")[1];
			String[] split = menInfo.split(",");
			Integer allMemory = Integer.parseInt(split[0].substring(0, split[0].length()-4).trim());
			Integer usedMemory = Integer.parseInt(split[1].substring(0, split[1].length()-6).trim());
//			System.out.println(allMemory  +  "   " + usedMemory);
			return (double)usedMemory/(double)allMemory;
		} catch (NumberFormatException e) {
			logger.debug("读取内存占用率出错:",e);
			e.printStackTrace();
		}
		return 0;
	}
	
//	public static void main(String[] args) {
//		long a = System.currentTimeMillis();
//		
//		ManageTask mt = new ManageTask();
////		for(int i=0;i<10000;i++){
////			mt.getCupUesd("{CacheSize=6144, TotalSockets=4, Mhz=3300, Model=Core(TM) i5-4440 CPU @ 3.10GHz, TotalCores=4, Vendor=Intel, CoresPerSocket=16}; CPU states: 90.6% user, 1.0% system, 0.0% nice, 3.0% wait, 86.2% idle;{User=3456760, SoftIrq=480, Idle=693590340, Stolen=0, Wait=594340, Total=698321840, Irq=0, Nice=31620, Sys=648300};Mem: 7897144K av, 4831364K used, 3065780K free;Swap: 8126460K av, 0K used, 8126460K free;");
////			mt.getMemoryUsed("{CacheSize=6144, TotalSockets=4, Mhz=3300, Model=Core(TM) i5-4440 CPU @ 3.10GHz, TotalCores=4, Vendor=Intel, CoresPerSocket=16}; CPU states: 90.6% user, 1.0% system, 0.0% nice, 3.0% wait, 86.2% idle;{User=3456760, SoftIrq=480, Idle=693590340, Stolen=0, Wait=594340, Total=698321840, Irq=0, Nice=31620, Sys=648300};Mem: 7897144K av, 4831364K used, 3065780K free;Swap: 8126460K av, 0K used, 8126460K free;");
////			//System.out.println(dd);
////		}
//		
//		double memoryUsed = mt.getMemoryUsed("{CacheSize=6144, TotalSockets=4, Mhz=3300, Model=Core(TM) i5-4440 CPU @ 3.10GHz, TotalCores=4, Vendor=Intel, CoresPerSocket=16}; CPU states: 90.6% user, 1.0% system, 0.0% nice, 3.0% wait, 86.2% idle;{User=3456760, SoftIrq=480, Idle=693590340, Stolen=0, Wait=594340, Total=698321840, Irq=0, Nice=31620, Sys=648300};Mem: 7897144K av, 4831364K used, 3065780K free;Swap: 8126460K av, 0K used, 8126460K free;");
//		
//		//long b = System.currentTimeMillis();
//		
//		System.out.println(memoryUsed);
//		
//
//	}
	
	
}
