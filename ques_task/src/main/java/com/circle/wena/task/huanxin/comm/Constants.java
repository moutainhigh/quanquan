package com.circle.wena.task.huanxin.comm;

/**
 * Constants
 * 
 * @author Lynch 2014-09-15
 *
 */
public class Constants {
	// API_HTTP_SCHEMA
	public static String API_HTTP_SCHEMA = "https";
	// API_SERVER_HOST
	public static String API_SERVER_HOST;
	// APPKEY
	public static String APPKEY;
	// APP_CLIENT_ID
	public static String APP_CLIENT_ID;
	// APP_CLIENT_SECRET
	public static String APP_CLIENT_SECRET;

	public static void inital(){
		Constants.API_SERVER_HOST = PropertiesUtils.getProperties().getProperty("API_SERVER_HOST");
		// APPKEY
		Constants.APPKEY = PropertiesUtils.getProperties().getProperty("APPKEY");
		// APP_CLIENT_ID
		Constants.APP_CLIENT_ID = PropertiesUtils.getProperties().getProperty("APP_CLIENT_ID");
		// APP_CLIENT_SECRET
		Constants.APP_CLIENT_SECRET = PropertiesUtils.getProperties().getProperty("APP_CLIENT_SECRET");
	}

}
