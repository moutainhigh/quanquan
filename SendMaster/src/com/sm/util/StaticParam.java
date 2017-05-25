package com.sm.util;
/**
 * @author zhoujia
 *
 * @date 2015年7月23日
 */
public class StaticParam {
	
	/**cup使用率超过多少的时候报警**/
	public static double CPUPERSINT = 85;
	/**如果内存使用超过0.9 则说明内存负载过高*/
	public static double MEMORYPRESINT = 90;
	
	/**所有从节点一起干**/
	public static String TASK_TYPE_ALL = "1";
	
	/**只有一个节点干**/
	public static String TASK_TYPE_ONE = "2";
	
	/**配置文件路径***/
	public static String CONFIG_URL = "config/";
	
	public static String configFileName = "conf.properties";
	
	public static String configTaskFile = "task_conf.properties";
	
	public static String log4jConfigFileName = "log4j.properties";
	
	public static final String storeDateTypeRedis = "R";
	public static final String storeDateTypeMysql = "M";
	

}
