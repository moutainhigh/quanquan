package com.rulesfilter.yy.zj.subscrib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.circle.core.elastic.CElastic;
import com.rulesfilter.yy.zj.model.Question;
import com.rulesfilter.yy.zj.service.RuleFilterService;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.index.get.GetField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.incr.ReConnectPublish;
import com.rulesfilter.yy.zj.model.AnsLog;
import com.rulesfilter.yy.zj.model.FilterRule;
import com.rulesfilter.yy.zj.model.User;
import com.rulesfilter.yy.zj.thread.FilterThread;
import com.rulesfilter.yy.zj.utils.ParamStatic;
import com.rulesfilter.yy.zj.utils.PareseQLogToAnsLog;

import redis.clients.jedis.JedisPubSub;

/**
 * @author zhoujia
 *
 * @date 2015年8月12日
 */
public class FilterSubpub extends ReConnectPublish {
	private static Logger logger = LoggerFactory.getLogger(FilterSubpub.class);
	
	public static List<FilterSubpub> hasActiveFilterRules = new ArrayList<FilterSubpub>();
	
	private FilterRule pfRule;
	
	public FilterSubpub(FilterRule pfRule) {
		try {
			this.delay_check=10000;
			this.delay_connect = 10000;
			this.delay_wait = 50000;

			this.pfRule = pfRule;
			boolean s = false;
			for (FilterSubpub filterSubpub : hasActiveFilterRules) {
				if(this.pfRule.getFilgerRuleId().equals(filterSubpub.getPfRule().getFilgerRuleId())){
					s = true;
					break;
				}

			}
			if(!s){ // 如果这个规则没有启动，则加入启动队列
				logger.info("创建 FilterSubpub ， id = " + pfRule.getFilgerRuleId() + " 并加入 hasActiveFilterRules");
				hasActiveFilterRules.add(this);
				logger.info("打印 hasActiveFilterRules");
				for (FilterSubpub filterSubpub : hasActiveFilterRules) {
					logger.info("已经加入的id = = = = " + filterSubpub.getPfRule().getFilgerRuleId());
				}
			}
		}catch (Exception e){
			logger.error("FilterSubpub 初始化异常：", e);
		}

	}
	
	
	@Override
	public void onMessage(String channel, String message) {
		boolean checkreconnect = checkreconnect(channel, message);
		if(!checkreconnect){
			if(hasActiveFilterRules.contains(this)){//如果启动
				logger.info("收到用户答题日志,过程规则处理开始。 message =" + message); 
				try {
					message = PareseQLogToAnsLog.paster(message,pfRule.getFilgerRuleId());
					AnsLog alog = (AnsLog)Json.jsonParser(message, AnsLog.class);
					User aUser = new User();
					User qUser = new User();

					logger.info("hbase log 写入监听   ===============" + alog.getUserId());
					if(alog.getUserId() != null && !"".equals(alog.getUserId())){ // 等于空说明没有答题人
						Get get = new Get(Bytes.toBytes(alog.getUserId()));
						Result aUserResult = CHbase.bean().get(ParamStatic.Hbase_User_Name, get);
						aUser = aUser.create(aUserResult); 
					}else{//如果没有抢题，这个记录则不保存
						return;
					}
					
					
					Get get = new Get(Bytes.toBytes(alog.getQueUserId()));
					Result qUserResult = CHbase.bean().get(ParamStatic.Hbase_User_Name, get);
					qUser = qUser.create(qUserResult);

					RuleFilterService service = new RuleFilterService();
					int rob = service.getIsRot(alog.getQid()); //是否是机器人

					logger.info("过程规则 : 回答人白名单状态 == " +aUser.getAccess()  + " \n 提问人白名单状态：" +  qUser.getAccess() + " 是否是机器人问题 "+ rob);
					if(!(aUser.getAccess() == ParamStatic.user_access_whiteList || qUser.getAccess() == ParamStatic.user_access_whiteList ) && rob == 0){
						logger.info("---------------------启动过程规则线程--------------------");
						//起线程去处理这个日志
						FilterThread ft = new FilterThread(pfRule, message);
						ft.start();
					}
				} catch (Exception e) {
					logger.error("hbase 查询问题异常 ", e);
				}
			}else{
				logger.info(this.getPfRule().getFilgerRuleId() + " 这个过程规则没有启动  请检查");
			}
			
		}
		
	}


	public FilterRule getPfRule() {
		return pfRule;
	}


	public void setPfRule(FilterRule pfRule) {
		this.pfRule = pfRule;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FilterSubpub){
			obj = (FilterSubpub)obj;
			if(this.getPfRule().getFilgerRuleId().equals(((FilterSubpub) obj).getPfRule().getFilgerRuleId())){
				return true;
			}else {
				return false;
			}
		}
		return false;
	}
	
	

}
