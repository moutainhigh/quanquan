package com.sendtask.usergroup.zhoujia.task;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.sendtask.common.service.TaskService;
import com.sendtask.usergroup.zhoujia.server.UserGroupService;
import com.sendtask.usergroup.zhoujia.utils.StaticParam;
import com.sendtask.usergroup.zhoujia.utils.SystemUtils;
import com.sendtask.usergroup.zhoujia.utils.TaskConfig;


/**
 * @author zhoujia
 *
 * @date 2015年7月30日
 */
public class ScheduleUserGroupExec {
	private static Logger logger = LoggerFactory.getLogger(ScheduleUserGroupExec.class);
	
	public static void main(String[] args) throws SQLException, IOException {
		logger.info("---------------->条件分组开始:schedule<---------------");

		// 返回pid给slave
		int pid = SystemUtils.getPid();
		System.out.println("pid: " + pid);
		
		TaskService ts = new UserGroupService();
//		// 初始化任务
		
		try {
			ts.init(args, "group_", "sc_usergroup_log4j.properties");
		} catch (Exception e) {
			logger.error("初始化出错，e=",e);
		}
		StaticParam.config_path = args[3];
		//PropertyConfigurator.configure(args[3] + "sc_usergroup_log4j.properties");

		logger.info("-用户分组任务开始--------------------------------------------------》");
		
		ts.schedule();
		
	}
}
