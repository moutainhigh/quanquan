package com.sendtask.common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.sendtask.common.model.SMTask;
import com.sendtask.common.utils.DBUtils;

public class QuestionTypeDao {
 Connection con = null;

	public QuestionTypeDao(String configPath) {
		con = DBUtils.getConnetction(configPath);
	}

	public void saveQuesType(String id, String name, String colour, int cpnum,
			int cpques, int tnum, int hide, int show, int sortid)
			throws SQLException {
		PreparedStatement pstmt = con
				.prepareStatement("INSERT INTO `yy_questiontype` "
						+ "(`id`, `name`, `colour`, `cpnum`, `cpques`, `tnum`, `hide`, `show`, `sortid`) "
						+ "VALUES (?,?,?,?,?,?,?,?,?)");
		pstmt.setString(1, id);
		pstmt.setString(2, name);
		pstmt.setString(3, colour);
		pstmt.setInt(4, cpnum);
		pstmt.setInt(5, cpques);
		pstmt.setInt(6, tnum);
		pstmt.setInt(7, hide);
		pstmt.setInt(8, show);
		pstmt.setInt(9, sortid);
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	public String getQuesTypeByName(String name) throws SQLException {
		String qtid = null;
		PreparedStatement pstmt = con
				.prepareStatement("select id from yy_questiontype where name = ?");
		pstmt.setString(1, name);
		ResultSet rs = pstmt.executeQuery();
		SMTask smTask = null;
		if (rs.next()) {
			qtid = rs.getString("id");
		}

		rs.close();

		pstmt.close();

		return qtid;
	}

}
