package test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sm.main.TaskThreadPool;
import com.sm.model.SMTask;
import com.sm.service.ManageTask;
import com.sm.service.MasterTaskService;
import com.sm.service.RuleConfig;
import com.sm.service.ZookeeperService;


/** 
 * @author  zhoujia 
 * @date 创建时间：2015年7月16日 下午2:33:12   
 */
public class Exec {

	
	public static void main(String[] args) throws  SQLException {
		
		
		ZookeeperService zkService = new ZookeeperService();
		//创建root根目录
		zkService.writeRootPath();
		//监控子节点
		zkService.watcherSlave();
		//创建数据目录
		zkService.createDataPath();
		
//		//初始化线程池
//		TaskThreadPool threadPool = new TaskThreadPool(); 
//		threadPool.runThreadPool();
//		
		 
//		MainThread mt = new MainThread();
//		mt.start();
		
		
		
//		//以下为测试代码
//		TaskArr ta = new TaskArr(mt);
//		MainThread.ruleFile = new CopyOnWriteArrayList<String>();
//		for(int i=0;i<100;i++){//测试代码
//			MainThread.ruleFile.add("config/rules,zhitou.jar");
//		}
//		try {
//			Thread.sleep(5000);
//			ta.start();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		
//		try {
//			RuleConfig rc = new RuleConfig("config/rules", "1");
//			MasterTaskService mts = new MasterTaskService();
//			
//			List<SMTask> theTasks = mts.getTheTasks(rc.initRules());
//			mts.addTaskToMysql(theTasks);//插入数据库
//			
//			ManageTask mTask = new ManageTask();
//			//分发到各个节点
//			mTask.sendTaskToSchedule(theTasks);
//			synchronized (Exec.class) {
//				Exec.class.wait();
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		
		
		
		
		
		
		
		//ThriftClient tc_slave1 =ThriftClient.getClientInstance("localhost");
//		RuleConfig rc = new RuleConfig();
//		
//		MasterTaskService mts = new MasterTaskService();
//		List<com.sm.model.SMTask> theTasks = mts.getTheTasks(rc.initRules());
		
		//制定分发计划
//		ManageTask mTask = new ManageTask();
//		mTask.sendTaskToSlave(theTasks);
		
//		List<SMTask> tasks = new ArrayList<SMTask>();
//		for (com.sm.model.SMTask smTask : theTasks) {
//			SMTask s = new SMTask();
//			Rules tRule = new Rules();
//			tRule.setCycleTime(smTask.getRule().getCycleTime());
//			tRule.setMoney(smTask.getRule().getMoney());
//			tRule.setNologinTime(smTask.getRule().getNologinTime());
//			s.setRule(tRule);
//			s.setTaskID(smTask.getTaskID());
//			tasks.add(s);
//		}
		//向从节点发送任务 TODO 计算负载最低的节点未做
//		List<Slave> slaves = ManageTask.slaves;
//		for (Slave slave : slaves) {
//			mts.sendTaskToSlave(slave);
//		}
		
		
		
		//测试
//		for (SMTask smTask : tasks) {
//			try {
//				tc_slave1.systemService.sendMoneyBySMTask(smTask);
//			} catch (TException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

	}

}








