package com.sendtask.usergroup.zhoujia.utils;

/**
 * zhoujia
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {
	private static Properties properties;
	
	private static String url = "jdbc:mysql://localhost:3306/cms_db";
	private static String username = "root";
	private static String password = "123456";

	/**
	 * 
	 */
	public static void initdb() {
		properties = new Properties();
		try {
			File file = new File(StaticParam.config_path + "group_conf.properties");
			properties.load(new FileInputStream(file));
			username = properties.getProperty("dbuser");
			password = properties.getProperty("dbpwd");
			url = "jdbc:mysql://"+properties.getProperty("dbip")+":"+properties.getProperty("dbport")+"/"+properties.getProperty("dbname");
			// 加载MySql的驱动类
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驱动程序类 ，加载驱动失败！");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("配置文件未找到！");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnetction() {
		initdb();

		try {
			Connection con = DriverManager.getConnection(url, username,password);
			return con;
		} catch (SQLException se) {
			System.out.println("数据库连接失败！");
			se.printStackTrace();
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

	public static void main(String[] args) throws SQLException {
		Connection con = getConnetction();
		PreparedStatement pstmt = con.prepareStatement("select * from auth_user");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String pass = rs.getString(3); // 此方法比较高效
			System.out.println(pass);
		}
		rs.close();
		pstmt.close();
		disConnection(con);
	}

}
