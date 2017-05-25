package com.sm.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.sm.dao.UserDao;
import com.sm.model.Rules;
import com.sm.model.SMTask;
import com.sm.util.MD5;
import com.sm.util.StaticParam;

public class MasterTaskService {
	private Logger logger = LoggerFactory.getLogger(MasterTaskService.class);
	public UserDao userDao = new UserDao();
	
	
	/**
	 * 插入数据库
	 * @param tasks
	 * @throws SQLException
	 */
	public void addTaskToMysql(List<SMTask> tasks) {
		try {
			for (SMTask smTask : tasks) {
				userDao.addTask(smTask);
			}
		} catch (SQLException e) {
			logger.error("添加mysql异常\n tasks参数：" + tasks + "\n" ,e);
		}
	}
	
	/**
	 * 存放任务到task
	 * @param tasks
	 */
	public void addTaskToRedis(List<SMTask> tasks){
		for (SMTask smTask : tasks) {
			Redis.shard.set(smTask.getTaskID(), Json.json(smTask));
		}
	}

	
	
	/***
	 * 
	 * 获取task
	 * @param rules
	 * @return
	 */
	public List<SMTask> getTheTasks(List<Rules> rules,String jarName,int slaveNum){
		List<SMTask> taskList = new ArrayList<SMTask>();
		//String dataType = FileUtil.getDataType(jarName);
//		List<String> availableSlave = ManageTask.availableSlave;
		//主节点不再分配任务worker，由schedule分配
//		StringBuffer sb = new StringBuffer();
//		for(int i=0;i<availableSlave.size();i++){
//			if(i<availableSlave.size()-1){
//				sb.append(availableSlave.get(i)).append(",");
//			}else{
//				sb.append(availableSlave.get(i));
//			}
//		}
		for (Rules rule : rules) {
			SMTask task = new SMTask();
			task.setRule(rule);
			task.setTaskID(MD5.encode(rule.toString()));
			task.setJarName(jarName);
			//task.setSlaveStr(sb.toString());
			task.setSlaveNum(slaveNum);
			//task.setDataType(dataType.equals(StaticParam.TASK_TYPE_ONE)?"R":"M");
			task.setDataType(StaticParam.storeDateTypeRedis);
			
			taskList.add(task);
		}
		
		return taskList;
	}
	
	/**
	 * 发题Task解析
	 * @param qid
	 * @param jarName
	 * @param slaveNum
	 * @return
	 */
	public List<SMTask> getTheTasks(String qid,String jarName,int slaveNum){
		List<SMTask> taskList = new ArrayList<SMTask>();
		//String dataType = FileUtil.getDataType(jarName);
		Rules rule = new Rules();
		rule.setQid(qid);
		SMTask task = new SMTask();
		task.setRule(rule);
		task.setTaskID(MD5.encode(rule.toString()));
		task.setJarName(jarName);
		//task.setSlaveStr(sb.toString());
		task.setSlaveNum(slaveNum);
		task.setDataType(StaticParam.storeDateTypeRedis);
		//System.out.println("======================================================="+task.getDataType());
		taskList.add(task);
		return taskList;
	}
	
	/**
	 * 默认，拉人，停发 任务解析
	 * @param jarName
	 * @param slaveNum
	 * @return
	 */
	public List<SMTask> getTheTasks(String jarName,int slaveNum){
		List<SMTask> taskList = new ArrayList<SMTask>();
		//String dataType = FileUtil.getDataType(jarName);
		Rules rule = new Rules();
		SMTask task = new SMTask();
		task.setRule(rule);
		task.setTaskID(MD5.encode(rule.toString()));
		task.setJarName(jarName);
		task.setSlaveNum(slaveNum);
		task.setDataType(StaticParam.storeDateTypeRedis);
		taskList.add(task);
		return taskList;
	}
	
	/**
	 * 用户分组 任务解析
	 * @param topic	对应分组的kafka的topic  usergroup_topic_71
	 * @param jarName
	 * @param slaveNum
	 * @return
	 */
	public List<SMTask> getTheUsergroupTasks(String topicAndKey,String jarName,int slaveNum){
		String topic = topicAndKey.split("\\|")[0];
		String redisKey = topicAndKey.split("\\|")[1];
		List<SMTask> taskList = new ArrayList<SMTask>();
		//String dataType = FileUtil.getDataType(jarName);
		Rules rule = new Rules();
		SMTask task = new SMTask();
		rule.setTopic(topic);
		rule.setGroupKey(redisKey);
		task.setRule(rule);
		task.setTaskID(MD5.encode(rule.toString()));
		task.setJarName(jarName);
		task.setSlaveNum(slaveNum);
		task.setDataType(StaticParam.storeDateTypeRedis);
		taskList.add(task);
		return taskList;
	}
	
//	/****
//	 * 分发任务到每个从节点
//	 * @param slave
//	 * @throws TException 
//	 */
//	@Deprecated
//	public void sendTaskToSlave(Slave slave) throws TException{
//		ThriftClient tc_slave1 = ThriftClient.getClientInstance(slave.getIp());
//		Map<String, SMTask> taskMap = slave.getTaskMap();
//		for (String taskID : taskMap.keySet()) {
//			SMTask smTask = taskMap.get(taskID);
//			com.sm.thrift.model.SMTask task = new com.sm.thrift.model.SMTask();
//			com.sm.thrift.model.Rules rule = new com.sm.thrift.model.Rules();
//			rule.setCycleTime(smTask.getRule().getCycleTime());
//			rule.setMoney(smTask.getRule().getMoney());
//			rule.setNologinTime(smTask.getRule().getNologinTime());
//			task.setRule(rule);
//			task.setTaskID(smTask.getTaskID());
//			tc_slave1.systemService.sendMoneyBySMTask(task);
//		}
//		
//	}
	
	/**
	 * 直投卷任务解析
	 * @param qid
	 * @param jarName
	 * @param slaveNum
	 * @return
	 */
	public List<SMTask> getTheZhiTouTasks(String quanId,String jarName,int slaveNum){
		List<SMTask> taskList = new ArrayList<SMTask>();
		//String dataType = FileUtil.getDataType(jarName);
		Rules rule = new Rules();
		rule.setQuanId(quanId);
		SMTask task = new SMTask();
		task.setRule(rule);
		task.setTaskID(MD5.encode(rule.toString()));
		task.setJarName(jarName);
		task.setSlaveNum(slaveNum);
		task.setDataType(StaticParam.storeDateTypeRedis);
		taskList.add(task);
		return taskList;
	}
	
	/**
	 * 消息Task解析
	 * @param qid
	 * @param jarName
	 * @param slaveNum
	 * @return
	 */
	public List<SMTask> getTheMessageTasks(String msgId,String jarName,int slaveNum){
		List<SMTask> taskList = new ArrayList<SMTask>();
		//String dataType = FileUtil.getDataType(jarName);
		Rules rule = new Rules();
		rule.setMessageId(msgId);
		SMTask task = new SMTask();
		task.setRule(rule);
		task.setTaskID(MD5.encode(rule.toString()));
		task.setJarName(jarName);
		task.setSlaveNum(slaveNum);
		task.setDataType(StaticParam.storeDateTypeRedis);
		taskList.add(task);
		return taskList;
	}
	
	
	
	
	/**
	 * 延迟发送task，即当Task任即将要触发了
	 */
	public void sendTaskDely(){
		//TODO : 
		
	}
	
	
}
