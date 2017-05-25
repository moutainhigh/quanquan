package com.circle.netty.formation.message.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author  zhoujia 
 * @date 创建时间：2015年8月18日 下午3:20:22   
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FreshFilterRule {
	public static final String IP_NO = "1";
	public static final String IP_YES = "0";
	public static final int a_yes = 0;
	public static final int a_no = 1;

	/**距离多少米不可刷新出题目， 为0没有限制*/
	private int distance = 0;

	/**ip相同不可刷新题目， 0是 1否**/
	private String ipEqual = IP_NO;

	/**几小时之内回答过问题的帐号之间不可见  0没有限制**/
	private int accountTimeNoSee = 0;

	/**几小时内回答过问题的手机之间不可见  0 没有限制***/
	private int phoneTimeNoSee = 0;
	/** device 相同 不可见 //一个手机登录过的[帐号]之间不可见 		0是   1否**/
	private int a = 1;

	/** 一个帐号登录过的[手机]之间不可见		0是   1否**/
	private int b = 1;

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getIpEqual() {
		return ipEqual;
	}

	public void setIpEqual(String ipEqual) {
		this.ipEqual = ipEqual;
	}

	public int getAccountTimeNoSee() {
		return accountTimeNoSee;
	}

	public void setAccountTimeNoSee(int accountTimeNoSee) {
		this.accountTimeNoSee = accountTimeNoSee;
	}

	public int getPhoneTimeNoSee() {
		return phoneTimeNoSee;
	}

	public void setPhoneTimeNoSee(int phoneTimeNoSee) {
		this.phoneTimeNoSee = phoneTimeNoSee;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}
	
	
}
