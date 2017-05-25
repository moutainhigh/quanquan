package com.rulesfilter.yy.zj.subscrib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.circle.core.elastic.CElastic;
import com.rulesfilter.yy.zj.model.*;
import com.rulesfilter.yy.zj.service.RuleFilterService;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.elasticsearch.action.get.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.incr.ReConnectPublish;
//import com.rulesfilter.yy.zj.thread.FilterThread;
import com.rulesfilter.yy.zj.thread.PointThread;
import com.rulesfilter.yy.zj.utils.ParamStatic;
import com.rulesfilter.yy.zj.utils.PareseQLogToAnsLog;

import redis.clients.jedis.JedisPubSub;

/**
 * @author zhoujia
 *
 * @date 2015年8月13日
 */
public class PTPSubpub extends ReConnectPublish{
	
	public static List<PTPSubpub> hasActivePtPRule = new ArrayList<PTPSubpub>();
	
	private static Logger logger = LoggerFactory.getLogger(PTPSubpub.class);
	private FilterRule ptpRule;
	
	public PTPSubpub(FilterRule ptpRule) {
		try {
			this.delay_check=1000;
			this.delay_connect = 1000;
			this.delay_wait = 5000;

			this.ptpRule = ptpRule;
			boolean s = false;
			for (PTPSubpub ptpSubpub : hasActivePtPRule) {
				if(this.ptpRule.getFilgerRuleId().equals(ptpSubpub.getPtpRule().getFilgerRuleId())){
					s = true;
					break;
				}
			}
			if(!s){//如果没有启动，则加入启动队列
				logger.info("创建 PTPSubpub ， id=" + ptpRule.getFilgerRuleId()+ " 加入 hasActivePtPRule 中");
				hasActivePtPRule.add(this);
			}
		}catch (Exception e){
			logger.error("PTPSubpub 初始化异常", e);
		}


	}
	
	/***
	 * 处理点对点规则
	 */
	@Override
	public void onMessage(String channel, String message) {
		boolean checkreconnect = checkreconnect(channel, message);
		if(!checkreconnect){
			if(hasActivePtPRule.contains(this)){//如果这个监听是启动的，加上这个确保删除的规则不再生效
				logger.info("收到用户答题日志,点对点规则处理开始。 message = "+message); 
				try {
					message = PareseQLogToAnsLog.paster(message,ptpRule.getFilgerRuleId());
					AnsLog alog = (AnsLog)Json.jsonParser(message, AnsLog.class);
					User aUser = new User();
					User qUser = new User();
					//Get get = new Get(Bytes.toBytes(alog.getQid()));
					//Result result = CHbase.bean().get(ParamStatic.Hbase_Question_Name, get);
					if(alog.getUserId() != null && !"".equals(alog.getUserId())){
						Get get = new Get(Bytes.toBytes(alog.getUserId()));
						Result aUserResult = CHbase.bean().get(ParamStatic.Hbase_User_Name, get);
						aUser = aUser.create(aUserResult); 
					}else{
						logger.info("回答人为空，此题没有人抢答");
						return ;
					}
					if(alog.getQueUserId() == null || "".equals(alog.getQueUserId())){
						logger.error("提问人id为空，数据错误");
						return;
					}
					Get get = new Get(Bytes.toBytes(alog.getQueUserId()));
					Result qUserResult = CHbase.bean().get(ParamStatic.Hbase_User_Name, get);
					qUser = qUser.create(qUserResult);
					RuleFilterService service = new RuleFilterService();
					int rob = service.getIsRot(alog.getQid()); //是否是机器人


					logger.info("点对点规则 : 回答人白名单状态 == " +aUser.getAccess()  + " \n 提问人白名单状态：" + qUser.getAccess() + " 是否是机器人问题 "+ rob);
					if(!(aUser.getAccess() == ParamStatic.user_access_whiteList || qUser.getAccess() == ParamStatic.user_access_whiteList ) && rob == 0){
						logger.info("---------------------启动点对点规则过滤-----------------------");
						//起线程去处理这个日志
						PointThread pt = new PointThread((PointToPointRule)ptpRule, message);
						pt.start();
					}
					
				} catch (Exception e) {
					logger.error("hbase 查询问题异常 ", e);
				}
			}else{
				logger.info(this.getPtpRule().getFilgerRuleId() + " 这个点对点规则没有启动  请检查 ");
			}
		}
		
	}

	public FilterRule getPtpRule() {
		return ptpRule;
	}

	public void setPtpRule(FilterRule ptpRule) {
		this.ptpRule = ptpRule;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PTPSubpub){
			obj = (PTPSubpub)obj;
			if(this.getPtpRule().getFilgerRuleId().equals(((PTPSubpub) obj).getPtpRule().getFilgerRuleId())){
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

}
