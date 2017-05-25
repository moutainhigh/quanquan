package com.sm.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sm.util.StaticParam;
import com.sm.util.ZookeeperAPI;

/**
 * @author zhoujia
 *
 * @date 2015年7月17日
 */
public class ZookeeperService {
	private Logger logger = LoggerFactory.getLogger(ZookeeperService.class);
	ZookeeperAPI sample = new ZookeeperAPI();
	
	/***
	 * 创建zookeeper根目录
	 */
	public void writeRootPath(){
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(StaticParam.CONFIG_URL);
		if(zkClient.isExist(ZookeeperAPI.ZK_PATH) == null){
			//创建非暂态文件夹
			zkClient.createPath(ZookeeperAPI.ZK_PATH , "",CreateMode.PERSISTENT);
			zkClient.createPath(ZookeeperAPI.ZK_PATH + "/znodes", "nodes",CreateMode.PERSISTENT);
		}else{
			logger.info(ZookeeperAPI.ZK_PATH + "根目录已经存在");
		}
		logger.info("创建master节点");
		if(zkClient.isExist(ZookeeperAPI.ZK_PATH + "/znodes/master") == null){
			//创建master节点
			zkClient.createPath(ZookeeperAPI.ZK_PATH + "/znodes/master", "master", CreateMode.EPHEMERAL);
		}

	}
	
	
	/**
	 * 监控各个节点状态
	 */
	public void watcherSlave(){
		final ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(StaticParam.CONFIG_URL);
		
		final ChildrenCallback cb = new ChildrenCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, List<String> children) {
				ZookeeperAPI.slaves = children;
			}
		};
		
		
		Watcher watcher = new Watcher(){

			@Override
			public void process(WatchedEvent event) {
				logger.info("收到事件通知： state =" + event.getState() + "\n type=" + event.getType() + "\n path="+ event.getPath());
				
				zkClient.getChildList(ZookeeperAPI.znodes, this,cb,null);
				if(EventType.NodeChildrenChanged.equals(event.getType())){
					List<String> newchildList = zkClient.getChildList(ZookeeperAPI.znodes);
					if(ZookeeperAPI.slaves.size() > newchildList.size()){//减节点了
						ZookeeperAPI.slaves.removeAll(newchildList);
						logger.error(ZookeeperAPI.slaves.get(0) + "节点被删除或者挂了");
					}else if(ZookeeperAPI.slaves.size() < newchildList.size()){//加节点了
						newchildList.removeAll(ZookeeperAPI.slaves);
						logger.info(newchildList.get(0) + "节点注册成功");
					}
				}
			}
			
		};
		zkClient.getChildList(ZookeeperAPI.znodes, watcher,cb,null);
		
		
	}
	
	
	/**
	 * 创建数据目录
	 */
	public void createDataPath(){
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(StaticParam.CONFIG_URL);
		if(zkClient.isExist(ZookeeperAPI.dataTask)==null){
			//创建数据目录
			zkClient.createPath(ZookeeperAPI.dataPath, "schedule", CreateMode.PERSISTENT);
			//创建schedule目录
			//zkClient.createPath(ZookeeperAPI.dataTask, "schedule", CreateMode.PERSISTENT);
			
			//创建发题任务目录
			//zkClient.createPath(ZookeeperAPI.questionPath, "question", CreateMode.PERSISTENT);
			//创建发题etc 目录
			zkClient.createPath(ZookeeperAPI.etcQuestionSlave, "question_etc", CreateMode.PERSISTENT);
			logger.info("问题负载信息目录建立成功 :" + ZookeeperAPI.etcQuestionSlave);
			zkClient.createPath(ZookeeperAPI.etcSlave, "etcInfo", CreateMode.PERSISTENT);
			logger.info("其他任务负载信息目录建立成功 :" + ZookeeperAPI.etcSlave);
			//建立容错节点目录
			zkClient.createPath(ZookeeperAPI.errorContent, "contentError", CreateMode.PERSISTENT);
			
		}else{//如果目录下有数据目录为空，则建立对应目录
			if(zkClient.isExist(ZookeeperAPI.dataPath)==null){
				zkClient.createPath(ZookeeperAPI.dataPath, "schedule", CreateMode.PERSISTENT);
			}
			
//			if(zkClient.isExist(ZookeeperAPI.dataTask)==null){
//				zkClient.createPath(ZookeeperAPI.dataTask, "schedule", CreateMode.PERSISTENT);
//			}
			
//			if(zkClient.isExist(ZookeeperAPI.questionPath)==null){
//				zkClient.createPath(ZookeeperAPI.questionPath, "question", CreateMode.PERSISTENT);
//			}
			
			if(zkClient.isExist(ZookeeperAPI.etcQuestionSlave)==null){
				zkClient.createPath(ZookeeperAPI.etcQuestionSlave, "question_etc", CreateMode.PERSISTENT);
			}
			
			if(zkClient.isExist(ZookeeperAPI.etcSlave)==null){
				zkClient.createPath(ZookeeperAPI.etcSlave, "etc_info", CreateMode.PERSISTENT);
			}
			if(zkClient.isExist(ZookeeperAPI.errorContent)==null){
				zkClient.createPath(ZookeeperAPI.errorContent, "contentError", CreateMode.PERSISTENT);
			}
			
		}
	}
	
	
	/***
	 * 获取所有子节点
	 * @return
	 */
	public List<String> getAllSlaveNodes(){
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(StaticParam.CONFIG_URL);
		return zkClient.getChildList(ZookeeperAPI.znodes);
	}
	
	/***
	 * 获取所有发题子节点
	 * @return
	 */
	public List<String> getQuestionSlaveNodes(){
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(StaticParam.CONFIG_URL);
		List<String> childList = zkClient.getChildList(ZookeeperAPI.znodes);
		List<String> questionList = new ArrayList<String>()	;
		for (String string : childList) {
			if(string.startsWith("ques")){
				questionList.add(string);
			}
		}
		return questionList;
	}
	
	/***
	 * 获取所有其他任务子节点
	 * @return
	 */
	public List<String> getSendSlaveNodes(){
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(StaticParam.CONFIG_URL);
		List<String> childList = zkClient.getChildList(ZookeeperAPI.znodes);
		List<String> sendList = new ArrayList<String>();
		for (String string : childList) {
			if(string.startsWith("send")){
				sendList.add(string);
			}
		}
		return sendList;
	}
	
//	/**
//	 * 监控字节点负载
//	 */
//	public void monitorEtcInfo(){
//		final ZookeeperAPI zkClient = ZookeeperAPI.getZKClient();
//		Watcher watcher = new Watcher(){
//			@Override
//			public void process(WatchedEvent event) {
//				//读取该机器上的配置信息
//				if(EventType.NodeDataChanged.equals(event.getType())){
//					String etcInfo = zkClient.readData(event.getPath(), this, null);
//					if(!ZookeeperAPI.etcRequest.equals(etcInfo)){
//						String [] str =  event.getPath().split("/");
//						String key = str[str.length-1];
//						ManageTask.etcInfo.put(key, etcInfo);
//					}
//				}
//			}
//			
//		};
//		for(String etcPath : ZookeeperAPI.etcInfoList){
//			zkClient.readData(etcPath, watcher, null);
//		}
//	}
	
	
	
	
	
	
	
//	/**
//	 * 发送读取各个节点负载的请求
//	 */
//	public void requestEtcInfo(){
//		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient();
//		for(String etcPath : ZookeeperAPI.etcInfoList){
//			zkClient.writeData(etcPath, ZookeeperAPI.etcRequest);
//		}
//	}
	
	
	
	
	
	
//	/**
//	 * 
//	 */
//	public void monitorWorkerData(){
//		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient();
//		zkClient.readData(ZookeeperAPI.ZK_PATH + "/data/worker1");
//	}
//	
//	
//	/**
//	 * 
//	 */
//	public void createSlaveNode(){
//		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient();
//		if(zkClient.isExist(ZookeeperAPI.ZK_PATH) != null){
//			zkClient.createPath(ZookeeperAPI.ZK_PATH + "/zodes/slave1", "slave1", CreateMode.EPHEMERAL);
//			
//		}else{
//			System.out.println("主节点未启动");
//		}
//	}
	
	
}
