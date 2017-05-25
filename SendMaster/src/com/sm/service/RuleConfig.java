package com.sm.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sm.model.Rules;
import com.sm.util.FileUtil;

/**
 * @author zhoujia
 *
 * @date 2015年7月16日
 * 
 * 初始化规则
 */
public class RuleConfig {
	private Logger logger = LoggerFactory.getLogger(RuleConfig.class);
	String ruleFilePath;
	String ruleType;
	public RuleConfig(String ruleFilePath,String ruleType,int bingxing) {
		this.ruleFilePath = ruleFilePath;
		this.ruleType = ruleType;
	}
	/**
	 * // 解析      0 0 18 * * * , no-login-time:21*24,50       字符串为 rule对象
	 * 目前为解析配置文件，也可能是从数据库中读取，当数据库中添加数据或者配置文件变动时，从新加载此方法
	 * @return
	 */
	public List<Rules> initRules(){
		List<String> rules = FileUtil.readFileByLines(ruleFilePath);
		List<Rules> ruleList = new ArrayList<Rules>();
		try {
			for (String ruleStr : rules) {
				 Rules rule = new Rules();
				 rule.setCycleTime(ruleStr.split(",")[0]);
				 String nologintime = ruleStr.split(",")[1].split(":")[1];
				 int noLoginTime = 0;
				 if (nologintime.indexOf("*") != -1){
					 noLoginTime = Integer.parseInt(nologintime.split("\\*")[0]) * Integer.parseInt(nologintime.split("\\*")[1]);
				 }else{
					 noLoginTime = Integer.parseInt(nologintime);
				 }
				 //rule.setNologinTime(noLoginTime);
				 //rule.setMoney(Double.parseDouble(ruleStr.split(",")[2]));
				 
				 ruleList.add(rule);
			}
			
		} catch (NumberFormatException e) {
			logger.error("解析规则出错：",e);
		}
		
		return ruleList;
	}
	
	
	
}
