package com.sendtask.usergroup.zhoujia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.circle.core.elastic.Json;
import com.sendtask.usergroup.zhoujia.model.Group;
import com.sendtask.usergroup.zhoujia.model.User;
import com.sendtask.usergroup.zhoujia.utils.DBUtils;



public class UserDao {
	Connection con = null;

	public UserDao() {
		con = DBUtils.getConnetction();
	}

	/***
	 * 查询 hour 小时未登录的用户
	 * 
	 * @param hour
	 * @return
	 * @throws SQLException
	 */
	public List<User> findUserByNologinTime(Integer hour) throws SQLException {

		long time = System.currentTimeMillis() - hour * 60 * 60 * 1000;

		PreparedStatement pstmt = con
				.prepareStatement("select * from user where lstime > ?");
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
			user.setCdate(Long.parseLong(rs.getString(11)));
			user.setSdate(rs.getLong(12));
			user.setLstime(rs.getLong(13));
			userList.add(user);
		}
		rs.close();

		pstmt.close();

		return userList;
	}

	
	public Group findUserGroup(Integer id) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement("select * from user_group where group_id = ?");
		pstmt.setLong(1, id);
		ResultSet rs = pstmt.executeQuery();
		Group group = new Group();
		while (rs.next()) {
			group.setGroupId(rs.getInt(1));
			group.setGroupType(rs.getInt(2));
			group.setParentId(rs.getInt(3));
			group.setGroup(rs.getInt(4));
			group.setGroupValue(rs.getString(5));
		}
		rs.close();
		pstmt.close();
		return group;
	}
	
	/**
	 * 查询分组
	 * @param parentId
	 * @return
	 * @throws SQLException
	 */
	public List<Group> findChildrenGroup(Integer parentId) throws SQLException{
		PreparedStatement pstmt = con.prepareStatement("select * from user_group where parent_id = ?");
		pstmt.setLong(1,parentId);
		ResultSet rs = pstmt.executeQuery();
		ArrayList<Group> children = new ArrayList<Group>();
		Group group = null;
		while (rs.next()) {
			group = new Group();
			group.setGroupId(rs.getInt(1));
			group.setGroupType(rs.getInt(2));
			group.setParentId(rs.getInt(3));
			group.setGroup(rs.getInt(4));
			group.setGroupValue(rs.getString(5));
			children.add(group);
		}
		rs.close();
		pstmt.close();

		return children;
	}
	
	
	/**
	 * 
	 * @param rootGroup
	 * @throws SQLException
	 */
	public Group getGroups(Group rootGroup) throws SQLException{
		List<Group> findUserGroup = findChildrenGroup(rootGroup.getGroupId());
		if(findUserGroup.size()>0){
			rootGroup.setChildren(findUserGroup);
			for (Group group : findUserGroup) {
				getGroups(group);
			}
		}
		return rootGroup;
	}
	
	
	
	
	public static void main(String[] args) throws SQLException {
		UserDao dao = new UserDao();
		Group rootGroup = new Group();
		rootGroup.setGroupId(1);
		Group groups = dao.getGroups(rootGroup);
		System.out.println(groups);
		
		System.out.println(Json.json(groups));
		
		//String json = "{\"groupId\":1,\"groupType\":null,\"parentId\":null,\"groupValue\":null,\"group\":null,\"children\":[{\"groupId\":2,\"groupType\":1,\"parentId\":1,\"groupValue\":\"0\",\"group\":\"3\",\"children\":null},{\"groupId\":3,\"groupType\":1,\"parentId\":1,\"groupValue\":\"2\",\"group\":\"1\",\"children\":[{\"groupId\":4,\"groupType\":2,\"parentId\":3,\"groupValue\":\"1\",\"group\":\"4\",\"children\":null}]}]}"
		String json = "{\"groupId\":1,\"groupType\":null,\"parentId\":null,\"groupValue\":null,\"group\":null,\"children\":[{\"groupId\":2,\"groupType\":1,\"parentId\":1,\"groupValue\":\"0\",\"group\":\"3\",\"children\":null},{\"groupId\":3,\"groupType\":1,\"parentId\":1,\"groupValue\":\"2\",\"group\":\"1\",\"children\":[{\"groupId\":4,\"groupType\":2,\"parentId\":3,\"groupValue\":\"1\",\"group\":\"4\",\"children\":null}]}]}";
		Object jsonParser = Json.jsonParser(json, Group.class);
		System.out.println(jsonParser);
		
	}
	
	
	
	
}
