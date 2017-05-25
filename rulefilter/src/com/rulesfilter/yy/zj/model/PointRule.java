package com.rulesfilter.yy.zj.model;
/**
 * @author zhoujia
 *
 * @date 2015-8-11
 */
public class PointRule {
	
	/**id**/
	private String pointRuleId;
	
	/**匹配规则  账号**/
	private String ruleAccount;
	/**匹配规则  IMEI**/
	private String ruleIMEI;
	/**匹配规则  IP**/
	private String ruleIP;
	public String getRuleAccount() {
		return ruleAccount;
	}
	public void setRuleAccount(String ruleAccount) {
		this.ruleAccount = ruleAccount;
	}
	public String getRuleIMEI() {
		return ruleIMEI;
	}
	public void setRuleIMEI(String ruleIMEI) {
		this.ruleIMEI = ruleIMEI;
	}
	public String getRuleIP() {
		return ruleIP;
	}
	public void setRuleIP(String ruleIP) {
		this.ruleIP = ruleIP;
	}
	public String getPointRuleId() {
		return pointRuleId;
	}
	public void setPointRuleId(String pointRuleId) {
		this.pointRuleId = pointRuleId;
	}
	
	

}
