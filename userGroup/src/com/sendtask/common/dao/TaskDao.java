package com.sendtask.common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.circle.core.elastic.Json;
import com.sendtask.common.model.Rules;
import com.sendtask.common.model.SMTask;
import com.sendtask.common.utils.DBUtils;

public class TaskDao {
	Connection con = null;

	public TaskDao(String configPath) {
		con = DBUtils.getConnetction(configPath);
	}

	public SMTask getTaskById(String taskID) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement("select * from task where task_id = ?");
		pstmt.setString(1, taskID);
		ResultSet rs = pstmt.executeQuery();
		SMTask smTask = null;
		if (rs.next()) {
			smTask = new SMTask();
			smTask.setTaskID(taskID);
			smTask.setDataType(rs.getString("data_type"));
			smTask.setJarName(rs.getString("jarname"));
			smTask.setSlaveStr(rs.getString("slave_str"));
			Rules rule = (Rules) Json.jsonParser(rs.getString("rule"), Rules.class);
			smTask.setRule(rule);
			smTask.setSlaveNum(rs.getInt("slave_num"));
		}

		rs.close();

		pstmt.close();

		return smTask;
	}

}
