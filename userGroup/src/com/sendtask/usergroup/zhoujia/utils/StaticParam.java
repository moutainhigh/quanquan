package com.sendtask.usergroup.zhoujia.utils;
/**
 * @author zhoujia
 *
 * @date 2015年7月29日
 */
public class StaticParam {

	//public static String elc_table_user = "ELC_TAB_REALUSER";
	
	public static String config_name = "usergroup_conf.properties";
	
	public static String config_path = "config/";
	
	/**redis中保存用户信息的hash中，用户分组的key**/
	public static String userInfo_groupKey = "groupId";
	
	
	/***
	 * redis中hashMap的key，filed为 groupId value 为pid
	 */
	public static String redisPidKey = "redisPidKey";
	public static String userGroupNode = "userGroupNode";
}
