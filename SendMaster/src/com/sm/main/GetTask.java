package com.sm.main;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sm.exception.NoAvaileableNodeException;
import com.sm.model.SMTask;
import com.sm.service.ManageTask;
import com.sm.service.MasterTaskService;
import com.sm.service.RuleConfig;

/**
 * @author zhoujia
 *
 * @date 2015年7月24日
 */
public class GetTask extends Thread{
	private Logger logger = LoggerFactory.getLogger(GetTask.class);
	//private String myqueueid="";
	//private int myid=0;
	public final static String zhitou="zhitou.jar";
	public final static String xiaoxi="xiaoxi.jar";
	public final static String moren="moren.jar";
	public final static String laren="laren.jar";
	public final static String tingfa="tingfa.jar";
	public final static String fati="fati.jar";
	public final static String usergroup="usergroup.jar";
	
	private String take;
	
	public GetTask(String take){
		//this.myid=myid;
		//this.myqueueid=myqueueid;
		this.take = take;
	}
	
	
//	public String path;
//	public String ruleType;
//	public GetTask(String path,String ruleType) {
//		this.path = path;
//		this.ruleType = ruleType;
//	}
	
	@Override
	public void run() {
		//while(true){
		//String take = null;
		try {
			//take = TaskThreadPool.taskabq[myid].take();
			String[] params = take.split(",");

			RuleConfig rc = new RuleConfig(params[0], params[1],Integer.parseInt(params[2]));
			MasterTaskService mts = new MasterTaskService();
			
			List<SMTask> theTasks = null;
			if(fati.equals(params[1])){//发题任务是一个
				theTasks = mts.getTheTasks(params[0], params[1], Integer.parseInt(params[2]));
			}else if(moren.equals(params[1]) || laren.equals(params[1]) || tingfa.equals(params[1])){
				//默认，拉人，停发
				theTasks = mts.getTheTasks(params[1], Integer.parseInt(params[2]));
			}else if(usergroup.equals(params[1])){ //分组  0:topic+groupKey  1:jarname  2:离散度
				theTasks = mts.getTheUsergroupTasks(params[0], params[1],Integer.parseInt(params[2]));
			}else if(xiaoxi.equals(params[1])){ //消息
				theTasks =mts.getTheMessageTasks(params[0], params[1], Integer.parseInt(params[2]));
			}else if(zhitou.equals(params[1])){ //直投
				theTasks = mts.getTheZhiTouTasks(params[0], params[1], Integer.parseInt(params[2]));
			}else {//其他，暂定为写文件来配置发送规则
				theTasks = mts.getTheTasks(rc.initRules(),params[1],Integer.parseInt(params[2]));
			}
			
//			if(FileUtil.getDataType(params[1]).equals(StaticParam.TASK_TYPE_ONE)){
//				mts.addTaskToRedis(theTasks);
//			}else{
//				mts.addTaskToMysql(theTasks);//插入数据库
//			}
			mts.addTaskToRedis(theTasks);
			ManageTask mTask = new ManageTask();
			if(fati.equals(params[1])){// 发题节点和其他任务节点是分开的
				//分发到各个发题节点    
				mTask.sendTaskToFatiSchedule(theTasks);
			}else{
				//分发到各个节点
				mTask.sendTaskToSchedule(theTasks);
			}

		} catch (NoAvaileableNodeException e) {
			logger.error(" 没有可用节点   "+ take ,e);
			//保存起来 TODO saveTo File
			
		}
			
		//}
		
		
	}

}
