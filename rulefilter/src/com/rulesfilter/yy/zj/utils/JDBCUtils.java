package com.rulesfilter.yy.zj.utils;





import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
 * @author  zhoujia 
 * @date 创建时间：2015年8月24日 下午1:32:15   
 */
public class JDBCUtils {
	
	private static Logger logger = LoggerFactory.getLogger(JDBCUtils.class);
	
	private static Properties properties;
	private static String url = "jdbc:phoenix:hbase-1.wenaaa.com:2181";
	
	public static void initdb() {
		properties = new Properties();
		try {
			//File file = new File("config/conf.properties"); 本地
			File file = new File(ParamStatic.configPath + "conf.properties"); // 线上
			properties.load(new FileInputStream(file));

			url = properties.getProperty("phoneinx_url");
			// 加载MySql的驱动类
			//Class.forName("com.mysql.jdbc.Driver");
			Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
		} catch (ClassNotFoundException e) {
			logger.error("找不到驱动程序类 ，加载驱动失败！");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("配置文件未找到！");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() { 
		
		initdb();
		try {
			//System.out.println("==========================");
			Connection conn = null;
			conn = DriverManager.getConnection(url);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void disConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("数据库链接关闭失败");
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
}
