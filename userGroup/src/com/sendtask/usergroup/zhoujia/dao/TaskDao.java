package com.sendtask.usergroup.zhoujia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.sendtask.common.model.SMTask;
import com.sendtask.usergroup.zhoujia.utils.DBUtils;


public class TaskDao {
	Connection con = null;

	public TaskDao() {
		con = DBUtils.getConnetction();
	}
	public void closeConn(){
		try {
			if(con != null && !con.isClosed()){
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public SMTask getTaskById(String taskID) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement("select * from task where task_id = ?");
		pstmt.setString(1, taskID);
		ResultSet rs = pstmt.executeQuery();
		SMTask smTask = null;
		while (rs.next()) {
			smTask = new SMTask();
			smTask.setTaskID(taskID);
			smTask.setDataType(rs.getString("data_type"));
			smTask.setJarName(rs.getString("jarname"));
			smTask.setSlaveStr(rs.getString("slave_str"));
			// TODO 等正式表设计出来再改
		}
		rs.close();
		return smTask;
	}

	/**解析用户分组数据***/
	public Map<Integer, String> groupDetailDataInit(Integer id) throws SQLException{
		PreparedStatement pstmt = con.prepareStatement("select * from group_detail where id = ?");
		pstmt.setInt(1, id);
		ResultSet rs = pstmt.executeQuery();
		String value = null;
		while (rs.next()) {
			value = rs.getString("value").replaceAll("\'", "");
		}
		rs.close();
		return getStringMap(value);
	}
	
	
	private Map<Integer, String> getStringMap(String jsonStr){
		String content = jsonStr.substring(1, jsonStr.length()-1);
		String[] split = content.split(",");
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (String string : split) {
			map.put(Integer.parseInt(string.split(":")[0]), string.split(":")[1]);
		}
		return map;
	}

	
	
}



