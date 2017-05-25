package com.sendtask.usergroup.zhoujia.task;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendtask.common.dict.SlaveDict;
import com.sendtask.common.service.TaskService;
import com.sendtask.common.utils.SystemUtils;
import com.sendtask.usergroup.zhoujia.server.UserGroupService;
import com.sendtask.usergroup.zhoujia.utils.StaticParam;


/**
 * @author zhoujia
 *
 * @date 2015年7月30日
 */
public class WorkUserGroupExec {
	private static Logger logger = LoggerFactory.getLogger(WorkUserGroupExec.class);
	public static void main(String[] args) throws SQLException, IOException {
		logger.info("--------------->条件分组开始:worker<--------------------");

		// 返回pid给slave
		int pid = SystemUtils.getPid();
		System.out.println("pid: " + pid);
		
		TaskService ts = new UserGroupService();
		// 初始化任务
		try {
			StaticParam.config_path = args[3];
			ts.init(args, "group_", "wk_usergroup_log4j.properties");
		} catch (Exception e) {
			logger.error("初始化出错，e=",e);
		}
		
//		PropertyConfigurator.configure(StaticParam.config_path + "wk_usergroup_log4j.properties");

		logger.info("---------------------------用户分组任务开始--------------------------------");
		
		// 判断是哪种任务
		if (SlaveDict.scheduleTYPE.equals(ts.getSlaveType())) {
			logger.info("schedule啊啊啊啊啊啊啊啊呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃");
			//DefTicketWaitThread th = new DefTicketWaitThread();
			ts.schedule();

		} else if (SlaveDict.workerTYPE.equals(ts.getSlaveType())) {
			logger.info("worker啊啊啊啊啊啊啊啊呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃");
			ts.worker();
		} else {
			logger.error("===" + ts.getSlaveType() + "===这个参数错了啊啊啊");
		}
		
	}
	
}
