package com.rulesfilter.yy.zj.model;
/**
 * @author zhoujia
 *
 * @date 2015-8-11
 */
public class ProcessRule {
	
	
	/**匹配规则 回答时间 -- 秒**/
	private Integer ruleAnswerTime;
	
	/**匹配规则 回答字符数**/
	private Integer ruleContentNum;
	
	/***匹配规则 筛选时间-- 秒**/
	private Integer ruleSelectTime;

	public Integer getRuleAnswerTime() {
		return ruleAnswerTime;
	}

	public void setRuleAnswerTime(Integer ruleAnswerTime) {
		this.ruleAnswerTime = ruleAnswerTime;
	}

	public Integer getRuleContentNum() {
		return ruleContentNum;
	}

	public void setRuleContentNum(Integer ruleContentNum) {
		this.ruleContentNum = ruleContentNum;
	}

	public Integer getRuleSelectTime() {
		return ruleSelectTime;
	}

	public void setRuleSelectTime(Integer ruleSelectTime) {
		this.ruleSelectTime = ruleSelectTime;
	}
	
	
}
