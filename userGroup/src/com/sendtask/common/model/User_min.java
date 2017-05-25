package com.sendtask.common.model;

public class User_min {
	/**
	 * 用户唯一ID
	 */
	private String uid;
	/**
	 * 性别
	 */
	private int sex;
	/**
	 * 所在城市
	 */
	private String city;
	/**
	 * 创建时间
	 */
	private Long cdate;
	/**
	 * 渠道
	 */
	private String cha;
	/**
	 * 系统
	 */
	private String sys;

	private String from;

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(int sex) {
		this.sex = sex;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the cdate
	 */
	public Long getCdate() {
		return cdate;
	}

	/**
	 * @param cdate
	 *            the cdate to set
	 */
	public void setCdate(Long cdate) {
		this.cdate = cdate;
	}

	/**
	 * @return the cha
	 */
	public String getCha() {
		return cha;
	}

	/**
	 * @param cha
	 *            the cha to set
	 */
	public void setCha(String cha) {
		this.cha = cha;
	}

	/**
	 * @return the sys
	 */
	public String getSys() {
		return sys;
	}

	/**
	 * @param sys
	 *            the sys to set
	 */
	public void setSys(String sys) {
		this.sys = sys;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
