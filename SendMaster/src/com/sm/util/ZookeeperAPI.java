package com.sm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperAPI implements Watcher {

	private static Logger logger = LoggerFactory.getLogger(ZookeeperAPI.class);
	// private static final int SESSION_TIMEOUT = 10000;
	// private static final String CONNECTION_STRING =
	// "slave1:2181,slave2:2181,slave3:2181";
	public static String ZK_PATH;
	private ZooKeeper zk = null;
	private static ZookeeperAPI zookeeperClient;
	
	/**容错信息目录**/
	public static String errorContent;

	private CountDownLatch connectedSemaphore = new CountDownLatch(1);
	// 节点列表
	public static List<String> slaves = new ArrayList<String>();
	// worker存取数据节点
	// public static List<String> dataSlaves = new ArrayList<String>();
	// 保存节点负载数据目录
	public static List<String> etcInfoList = new ArrayList<String>();

	/**任务下发目录**/
	public static String dataTask;
	
	public static String dataPath;
	// public static String etcRequest;
	/**任务下发目录*/
	public static String questionPath;
	
	public static String znodes;
	public static String etcSlave;
	public static String etcQuestionSlave;
	public static String masterName;
	
	//普通任务管道
	public static String commonTaskChannel;
	//发题任务管道前缀
	public static String questionTaskChannelPrefix;
	
	//public static String contentErrosrBak ;
	//容错机器数量
	public static Integer contentErrorBakNum;//目前先配置写死，可能修改为按照任务分配
	/**备机监听管道**/
	public static String contentErrorChannelPrefix;
	
	public static ZookeeperAPI getZKClient(String config) {
		if (zookeeperClient == null) {
			Properties properties = new Properties();
			try {
				// properties.load(Cfs.class.getClassLoader().getResourceAsStream(fileName));
				File file = new File(config + "conf.properties");
				properties.load(new FileInputStream(file));
				ZK_PATH = properties.getProperty("ZK_PATH");
				// zookeeperGroup = properties.getProperty("zookeeperGroup");
				dataTask = properties.getProperty("dataTask");
				dataPath = properties.getProperty("dataPath");
				// etcRequest = properties.getProperty("etc_info");
				znodes = properties.getProperty("znodes");
				etcSlave = properties.getProperty("etc_slave");
				masterName = properties.getProperty("mastername");
				questionPath = properties.getProperty("dataQuestionTask");
				etcQuestionSlave = properties.getProperty("etc_question_slave");
				commonTaskChannel = properties.getProperty("commonTaskChannel");
				questionTaskChannelPrefix = properties.getProperty("questionTaskChannelPrefix");
				//contentErrosrBak = properties.getProperty("contentErrosrBak");
				contentErrorBakNum = Integer.parseInt(properties.getProperty("contentErrorBakNum"));
				errorContent = properties.getProperty("errorContent");
				
				contentErrorChannelPrefix = properties.getProperty("spareTaskChannelPrefix");
				// String[] nodes = properties.getProperty("nodes").split(",");
				// for (String string : nodes) {
				// slaves.add(properties.getProperty(string));
				// if(string.startsWith("slave")){
				// dataSlaves.add(properties.getProperty("data"+string));
				// }
				// etcInfoList.add(properties.getProperty("etc_"+string));
				// }
			} catch (IOException e) {
				logger.error("读取config/conf.properties 报错", e);
			}
			zookeeperClient = new ZookeeperAPI();
			zookeeperClient.createConnection(properties.getProperty("CONNECTION_STRING"),
					Integer.parseInt(properties.getProperty("SESSION_TIMEOUT")));
		}
		return zookeeperClient;

	}

	/**
	 * 创建ZK连接
	 * 
	 * @param connectString
	 *            ZK服务器地址列表
	 * @param sessionTimeout
	 *            Session超时时间
	 */
	public void createConnection(String connectString, int sessionTimeout) {
		this.releaseConnection();
		try {
			// Watcher wathcer = new Watcher(){
			// @Override
			// public void process(WatchedEvent event) {
			// //if(zk.exists(path, watch)event.getPath())
			// System.out.println("zkwatcher info : path="+ event.getPath()+ "
			// type="+ event.getType());
			// }
			// };
			zk = new ZooKeeper(connectString, sessionTimeout, this);
			connectedSemaphore.await();
		} catch (InterruptedException e) {
			logger.error("连接创建失败,connectString:" + connectString + ",sessionTimeout:" + sessionTimeout, e);
		} catch (IOException e) {
			logger.error("连接创建失败,connectString:" + connectString + ",sessionTimeout:" + sessionTimeout, e);
		}
	}

	/**
	 * 关闭ZK连接
	 */
	public void releaseConnection() {
		if (this.zk != null) {
			try {
				this.zk.close();
			} catch (InterruptedException e) {
				// ignore
				logger.error("关闭ZK连接报错", e);
			}
		}
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 *            节点path
	 * @param data
	 *            初始数据内容
	 * @return
	 */
	public boolean createPath(String path, String data, CreateMode createMode) {
		try {
			String node = this.zk.create(path, //
					data.getBytes(), //
					Ids.OPEN_ACL_UNSAFE, //
					createMode) + ", content: " + data;
			logger.info("节点创建成功, Path: " + node);
		} catch (KeeperException e) {
			logger.error("节点创建失败, path:" + path + ",data:" + ",createMode" + createMode, e);
		} catch (InterruptedException e) {
			logger.error("节点创建失败, path:" + path + ",data:" + ",createMode" + createMode, e);
		}
		return true;
	}

	/**
	 * 读取指定节点数据内容
	 * 
	 * @param path
	 *            节点path
	 * @return
	 */
	public String readData(String path) {
		try {
			logger.info("获取数据成功,path：" + path);
			return new String(this.zk.getData(path, false, null)); // 注意这个null，这里可以设置watcher
		} catch (KeeperException e) {
			logger.error("读取数据失败, path:" + path, e);
			return "";
		} catch (InterruptedException e) {
			logger.error("读取数据失败, path:" + path, e);
			return "";
		}
	}

	/**
	 * 读取指定节点数据内容
	 * 
	 * @param path
	 *            节点path
	 * @return
	 */
	public String readData(String path, Watcher watcher, Stat stat) {
		try {
			logger.info("获取数据成功,path：" + path);
			return new String(this.zk.getData(path, watcher, stat)); // 注意这个null，这里可以设置watcher
		} catch (KeeperException e) {
			logger.error("读取数据失败, path:" + path, e);
			return "";
		} catch (InterruptedException e) {
			logger.error("读取数据失败, path:" + path, e);
			return "";
		}
	}

	/**
	 * 读取指定节点子节点
	 * 
	 * @param path
	 *            节点path
	 * @return
	 */
	public List<String> getChildList(String path) {
		try {
			logger.info("获取成功,path:" + path);
			return zk.getChildren(path, false);// 这里可以设置watcher,
												// zk.getChildren(path, new
												// MyWatcher()) ;
		} catch (NoNodeException e) {
			try {
				List<String> children = zk.getChildren(path, false);
				return children;
			} catch (KeeperException | InterruptedException e1) {
				logger.error("读取失败, path:" + path, e1);
				return null;
			}
		} catch (Exception e) {
			logger.error("读取失败, path:" + path, e);
			return null;
		}
	}

	public List<String> getChildList(String path, Watcher watcher) {
		try {
			logger.info("获取成功,path:" + path);
			return zk.getChildren(path, watcher);// 这里可以设置watcher,
													// zk.getChildren(path, new
													// MyWatcher()) ;
		} catch (Exception e) {
			logger.error("读取失败, path:" + path, e);
			return null;
		}
	}

	public void getChildList(String path, Watcher watcher, ChildrenCallback cb, Object ctx) {
		try {
			logger.info("获取成功,path:" + path);
			zk.getChildren(path, watcher, cb, ctx); // 这里可以设置watcher,
													// zk.getChildren(path, new
													// MyWatcher()) ;
		} catch (Exception e) {
			logger.error("读取失败, path:" + path, e);
			e.printStackTrace();
		}
	}

	/**
	 * 判断节点是否存在
	 * 
	 * @param path
	 *            节点path
	 * @return
	 */
	public Stat isExist(String path) {
		try {
			logger.info("isExist成功, path:" + path);
			return zk.exists(path, true);// 这里可以设置watcher,zk.exists(path, new
											// MyWatcher());
		} catch (Exception e) {
			logger.error("isExist失败, path:" + path, e);
			return null;
		}
	}

	/**
	 * 判断节点是否存在
	 * 
	 * @param path
	 *            节点path
	 * @return
	 */
	public Stat isExist(String path, Watcher watcher) {
		try {
			logger.info("isExist成功, path:" + path);
			return zk.exists(path, watcher);// 这里可以设置watcher,zk.exists(path, new
											// MyWatcher());
		} catch (Exception e) {
			logger.error("isExist失败, path:" + path, e);
			return null;
		}
	}

	/**
	 * 判断节点是否存在
	 * 
	 * @param path
	 *            节点path
	 * @return
	 */
	public Stat isExist(String path, Boolean b) {
		try {
			logger.info("isExist成功, path:" + path);
			return zk.exists(path, b);// 这里可以设置watcher,zk.exists(path, new
										// MyWatcher());
		} catch (Exception e) {
			logger.error("isExist失败, path:" + path, e);
			return null;
		}
	}

	public void isExist(final String path, Watcher watcher, StatCallback cb, Object ctx) {
		try {
			logger.info("isExist成功, path:" + path);
			zk.exists(path, watcher, cb, ctx);// 这里可以设置watcher,zk.exists(path,
												// new MyWatcher());
		} catch (Exception e) {
			logger.error("isExist失败, path:" + path, e);
		}
	}

	/**
	 * 更新指定节点数据内容
	 * 
	 * @param path
	 *            节点path
	 * @param data
	 *            数据内容
	 * @return
	 */
	public boolean writeData(String path, String data) {
		try {
			logger.info("更新数据成功, path:" + path + "stat: " + this.zk.setData(path, data.getBytes(), -1));
		} catch (KeeperException e) {
			logger.error("更新数据失败, path:" + path + ",data:" + data, e);
		} catch (InterruptedException e) {
			logger.error("更新数据失败, path:" + path + ",data:" + data, e);
		}
		return false;
	}

	/**
	 * 删除指定节点
	 * 
	 * @param path
	 *            节点path
	 */
	public void deleteNode(String path) {
		try {

			this.zk.delete(path, -1);
			logger.info("删除节点成功, path:" + path);
		} catch (KeeperException e) {
			logger.error("更新数据失败, path:" + path, e);
		} catch (InterruptedException e) {
			logger.error("更新数据失败, path:" + path, e);
		}
	}

	/**
	 * 收到来自Server的Watcher通知后的处理。
	 */
	@Override
	public void process(WatchedEvent event) {

		if (KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}

	}
}