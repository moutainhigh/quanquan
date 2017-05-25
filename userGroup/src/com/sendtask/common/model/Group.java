package com.sendtask.common.model;

import java.util.List;

/**
 * @author zhoujia
 *
 * @date 2015年7月30日
 */
public class Group {

	/**id**/
	private Integer groupId;
	
	/**树的深度**/
	private Integer groupType;
	
	/***父节点id**/
	private Integer parentId;
	
	/**value值**/
	private String groupValue;
	
	/**group类型**/
	private Integer group;
	
	/**子节点**/
	private List<Group> children;

	
	
	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getGroupValue() {
		return groupValue;
	}

	public void setGroupValue(String groupValue) {
		this.groupValue = groupValue;
	}


	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public List<Group> getChildren() {
		return children;
	}

	public void setChildren(List<Group> children) {
		this.children = children;
	}
	
	
	
}
