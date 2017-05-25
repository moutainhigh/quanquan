package com.sm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sm.model.SMTask;
import com.sm.model.User;
import com.sm.util.db.DBUtils;

public class UserDao {
	Connection con = null;
	public UserDao() {
		con = DBUtils.getConnetction();
	}
	
	
	/**
	 * task插入数据库
	 * @param task
	 * @throws SQLException
	 */
	public void addTask(SMTask task) throws SQLException{

		PreparedStatement pstmt = con.prepareStatement("insert into task (task_id,task,rule,slave_str,slave_num) values (?,?,?,?,?)");
		pstmt.setString(1, task.getTaskID());
		pstmt.setString(2, null);
		pstmt.setString(3, task.getRule().toString());
		pstmt.setString(4, task.getSlaveStr());
		pstmt.setInt(5, task.getSlaveNum());
		pstmt.executeUpdate();
		pstmt.close();
		
	}
	
	/***
	 * 查询  hour 小时未登录的用户
	 * @param hour
	 * @return
	 * @throws SQLException
	 */
	public List<User> findUserByNologinTime(Integer hour) throws SQLException{
		
		long time = System.currentTimeMillis() - hour * 60 *60 * 1000;
		
		PreparedStatement pstmt = con.prepareStatement("select * from user where lstime > ?");
		pstmt.setLong(1, time);
		
		ResultSet rs = pstmt.executeQuery();
		List<User> userList = new ArrayList<User>();
		User user = null;
		while (rs.next()) {
			user = new User();
			user.setUid(rs.getString(1));
			user.setPass(rs.getString(2));
			user.setName(rs.getString(3));
			user.setSex(rs.getInt(4));
			user.setUurl(rs.getString(5));
			user.setCompany(rs.getString(6));
			user.setAge(rs.getInt(7));
			user.setCity(rs.getString(8));
			user.setRemark(rs.getString(9));
			user.setMobile(rs.getString(10));
			user.setCdate(rs.getString(11));
			user.setSdate(rs.getLong(12));
			user.setLstime(rs.getLong(12));
			userList.add(user);
		}
		rs.close();
		
		pstmt.close();
		
		return userList;
	}
	
}
