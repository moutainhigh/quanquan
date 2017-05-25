package com.sendtask.common.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendtask.common.model.Slave;
import com.sendtask.common.utils.TaskConfig;
import com.sendtask.common.utils.ZookeeperAPI;

/**
 * 计划任务zk公共
 * 
 * @author qiuxy
 * @date 2015年7月17日
 */
public class ZookeeperService {

	private Logger logger = LoggerFactory.getLogger(ZookeeperService.class);

	private String configPath;

	public ZookeeperService(String confPath) {
		configPath = confPath;
	}

	/**
	 * 往path节点写入data
	 * 
	 * @param path
	 * @param data
	 */
	public void writeData(String path, String data) {
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		zkClient.writeData(path, data);
	}

	/**
	 * 往path节点追加写入data
	 * 
	 * @param path
	 * @param data
	 * @param divide
	 *            分隔
	 */
	public void appendData(String path, String data, String divide) {
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		String old = zkClient.readData(path);
		data = old + divide + data;
		zkClient.writeData(path, data);
	}

	/**
	 * 读取节点data
	 */
	public String readData(String path) {
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		return zkClient.readData(path);
	}

	/**
	 * 获取子节点的负载信息
	 * 
	 * @return
	 */
	public List<Slave> getChildEtcs() {
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		List<String> etcList = zkClient.getChildList(TaskConfig.etcRootPath);
		List<Slave> etcDataList = new CopyOnWriteArrayList<Slave>();
		for (String etc : etcList) {
			Slave sl = new Slave();
			sl.setHost(TaskConfig.etcRootPath + "/" + etc);
			sl.setEtcData(zkClient.readData(TaskConfig.etcRootPath + "/" + etc));
			etcDataList.add(sl);
		}
		return etcDataList;
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param data
	 * @param mode
	 */
	public boolean createPath(String path, String data, CreateMode mode) {
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		return createPath(path, data, zkClient, mode);
	}

	/**
	 * 创建节点公共
	 * 
	 * @param path
	 * @param data
	 * @param zkClient
	 * @param mode
	 */
	private boolean createPath(String path, String data, ZookeeperAPI zkClient, CreateMode mode) {
		if (zkClient.isExist(path) == null) {
			if (zkClient.createPath(path, data, mode)) {
				logger.info(path + "节点创建成功");
				return true;
			} else {
				logger.error(path + "节点创建失败");
				return false;
			}
		} else {
			logger.info(path + " 节点已存在");
			return false;
		}
	}

	/**
	 * 判断节点存在
	 */
	public Boolean isExist(String path) {
		ZookeeperAPI zkClient = ZookeeperAPI.getZKClient(configPath);
		return zkClient.isExist(path) != null ;
	}

}
