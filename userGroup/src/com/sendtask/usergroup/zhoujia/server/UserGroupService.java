package com.sendtask.usergroup.zhoujia.server;

import java.io.IOException;

import com.sendtask.common.service.ZookeeperService;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.sendtask.common.model.SMTask;
import com.sendtask.common.service.TaskService;
import com.sendtask.usergroup.zhoujia.kafka.NewUserstart;
import com.sendtask.usergroup.zhoujia.model.Group;
import com.sendtask.usergroup.zhoujia.utils.Config;
import com.sendtask.usergroup.zhoujia.utils.StaticParam;
import com.sendtask.usergroup.zhoujia.utils.SystemUtils;


/**
 * @author zhoujia
 *
 * @date 2015年7月30日
 */
public class UserGroupService extends TaskService{

	private static Logger logger = LoggerFactory.getLogger(UserGroupService.class);
	
	@Override
	public void schedule() {
//		logger.info("启动schedule:taskID="+taskID + ",dbType=" + dbType);
//		// 把任务查出来
//		SMTask smTask = getTask(taskID, dbType);
//		String[] workers = null;
//		// 发指定slave执行worker
//		ZookeeperService zkService = new ZookeeperService("");
//		workers = smTask.getSlaveStr().split(",");
//		for (String wk : workers) {
//			Config cfs = TaskConfig.getConfig();
//			String dataPath = cfs.getAsString("dataPath");
//			zkService.writeData(dataPath + "/" + wk, taskID + ":" + smTask.getDataType());
//		}
		//smTask.getRule().getGroupKey();
		
		
		logger.info("启动schedule:taskID="+taskID + ",dbType=" + dbType);
		SMTask task = this.getTask(taskID, dbType);
		logger.info("task = = = = = " + task);
		String key = task.getRule().getGroupKey();
		String topic = task.getRule().getTopic();
		logger.info("key = = = = = " + key + "  " + topic);
		String groupJson = Redis.shard.get(key);
		Group group = (Group)Json.jsonParser(groupJson,Group.class);
		logger.info("groupJson  = == "  + groupJson);
		Config config = null;
        Config group_conf = null;
		//打通kafka
		//通知相旭
		try {
			config = new Config(StaticParam.config_path + "usergroup.properties");
            group_conf = new Config(StaticParam.config_path + "group_conf.properties");
		} catch (IOException e) {
			logger.error("配置文件异常 ：usergroup.properties",e); 
		}
		//把pid 存入redis中， 为结构了hashMap 
		int pid = SystemUtils.getPid();
		Redis.shard.hset(StaticParam.redisPidKey, String.valueOf(group.getGroupId()), String.valueOf(pid));


        try {
            ZookeeperService zkService = new ZookeeperService(StaticParam.config_path);
            //String asString = group_conf.getAsString(StaticParam.userGroupNode);
            if(group_conf!=null ){
                String path = group_conf.getAsString(StaticParam.userGroupNode);
                logger.info("path============"+path + "  是否存在 ===== " + zkService.isExist(path));

                //zkClient.isExist(ZookeeperAPI.dataPath)==null
                if(zkService.isExist(path)){//如果节点不存在，则创建节点
                    //zkService.writeData(path,String.valueOf(pid));
                    zkService.createPath("/userGroups","", CreateMode.PERSISTENT);
                    zkService.createPath(path,String.valueOf(pid), CreateMode.PERSISTENT);
                    zkService.createPath(path+"/"+String.valueOf(group.getGroupId()),String.valueOf(pid), CreateMode.EPHEMERAL);

                }else{
                    zkService.createPath(path+"/"+String.valueOf(group.getGroupId()),String.valueOf(pid), CreateMode.EPHEMERAL);
                }
            }
        }catch (Exception e){
            logger.error("创建节点失败",e);
        }


		Subscribe ssbe = new Subscribe();
		ssbe.subscribeKillself();
		NewUserstart newUserstart = new NewUserstart(config,group,topic);
	}

	@Override
	public void worker() {
		logger.info("启动work：taskID="+taskID + ",dbType=" + dbType);
		SMTask task = this.getTask(taskID, dbType);
		
		String key = task.getRule().getGroupKey();
		String topic = task.getRule().getTopic();
		String groupJson = Redis.shard.get(key);
		Group group = (Group)Json.jsonParser(groupJson,Group.class);
		Config config = null;
		try {
			config = new Config(StaticParam.config_path + "usergroup.properties");
		} catch (IOException e) {
			logger.error("配置文件异常 ：usergroup.properties",e); 
		}
		
		NewUserstart newUserstart = new NewUserstart(config,group,topic);
	}
	
}
