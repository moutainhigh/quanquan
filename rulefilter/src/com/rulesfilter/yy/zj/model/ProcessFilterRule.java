package com.rulesfilter.yy.zj.model;

import java.io.Serializable;

/**
 * @author zhoujia
 *
 * @date 2015-8-11
 */
public class ProcessFilterRule extends FilterRule implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4642450290755404745L;

	
	private ProcessRule prRule;
	
	private String prRuleKey;


	public String getPrRuleKey() {
		return prRuleKey;
	}


	public void setPrRuleKey(String prRuleKey) {
		this.prRuleKey = prRuleKey;
	}


	public ProcessRule getPrRule() {
		return prRule;
	}


	public void setPrRule(ProcessRule prRule) {
		this.prRule = prRule;
	}
	
	
}
