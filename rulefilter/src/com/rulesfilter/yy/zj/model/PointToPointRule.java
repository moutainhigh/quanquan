package com.rulesfilter.yy.zj.model;

import java.io.Serializable;

/**
 * @author zhoujia
 *
 * @date 2015-8-11
 */
public class PointToPointRule extends FilterRule implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2835021605983465386L;


	private PointRule prule;
	
	private String pruleKey;

	public String getPruleKey() {
		return pruleKey;
	}


	public void setPruleKey(String pruleKey) {
		this.pruleKey = pruleKey;
	}


	public PointRule getPrule() {
		return prule;
	}


	public void setPrule(PointRule prule) {
		this.prule = prule;
	}
	
	
	
	
}
