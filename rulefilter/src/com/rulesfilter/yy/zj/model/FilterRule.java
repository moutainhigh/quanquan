package com.rulesfilter.yy.zj.model;
/**
 * @author zhoujia
 *
 * @date 2015-8-11
 */
public class FilterRule {

	
	/**id**/
	private String filgerRuleId;
	
	/**每几天**/
	private Double everyDay;
	
	/**第几次出现*/
	private Integer times;
	
	/**执行的状态**/
	private String status;
	
	/**状态持续几天**/
	private Double longDays;
	
	
	/**
	 * 1 是， 0 否  对应那些规则不可见 account  = 1 代表 账号不可见 0代表没有这个规则
	 */
	
	private String account;
	
	private String IMEI;
	
	private String IP;
	
	public static final String account_yes = "1";
	public static final String account_no = "0";
	public static final String IMEI_yes = "1";
	public static final String IMEI_no = "0";
	public static final String IP_yes = "1";
	public static final String IP_no = "0";
	public Double getEveryDay() {
		return everyDay;
	}

	public void setEveryDay(Double everyDay) {
		this.everyDay = everyDay;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getLongDays() {
		return longDays;
	}

	public void setLongDays(Double longDays) {
		this.longDays = longDays;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getFilgerRuleId() {
		return filgerRuleId.trim();
	}

	public void setFilgerRuleId(String filgerRuleId) {
		this.filgerRuleId = filgerRuleId;
	}
	
	/**
	 * id相等即视为相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FilterRule){
			FilterRule f = (FilterRule)obj;
			return this.filgerRuleId.equals(f.getFilgerRuleId());
		}
		return false;
	}
	
}
