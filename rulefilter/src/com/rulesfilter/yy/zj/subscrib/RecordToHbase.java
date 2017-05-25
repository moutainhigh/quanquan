package com.rulesfilter.yy.zj.subscrib;

import java.io.IOException;
import java.math.BigDecimal;

import com.circle.core.elastic.CElastic;
import com.rulesfilter.yy.zj.service.RuleFilterService;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.types.PDecimal;
import org.apache.phoenix.schema.types.PLong;
import org.elasticsearch.action.get.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.incr.ReConnectPublish;
import com.rulesfilter.yy.zj.model.AnsLog;
import com.rulesfilter.yy.zj.utils.PareseQLogToAnsLog;

import redis.clients.jedis.JedisPubSub;

/**
 * 
 * 收到一条日志就存入Hbase中
 * 
 * @author zhoujia
 *
 * @date 2015年8月14日
 */
public class RecordToHbase extends ReConnectPublish{
	private static Logger logger = LoggerFactory.getLogger(RecordToHbase.class);
	private CHbase bean;
	public RecordToHbase() {
		bean = CHbase.bean();
		this.delay_check=1000;
		this.delay_connect = 1000;
		this.delay_wait = 5000;
	}

	@Override
	public void onMessage(String channel, String message) {
		boolean checkreconnect = checkreconnect(channel, message);
		if(!checkreconnect){

			message = PareseQLogToAnsLog.paster(message,"noId");
			try {
				AnsLog log = (AnsLog)Json.jsonParser(message,AnsLog.class);
				RuleFilterService service = new RuleFilterService();
				int rob = service.getIsRot(log.getQid()); //是否是机器人
				if(rob == 0){ // 等于0为真题，1是机器人的题目
					Put put = new Put(Bytes.toBytes(log.getLogId()));
					logger.info("收到答题记录信息，写入hbase = = = 题目id ==" + log.getQid()+ " " + log.getLogId() ) ;
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("userId".toUpperCase()),Bytes.toBytes(log.getUserId()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("time".toUpperCase()),PLong.INSTANCE.toBytes(log.getTime()));
					//put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("time"),PLong.INSTANCE.toBytes(log.getTime()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerPhone".toUpperCase()),Bytes.toBytes(log.getAnswerPhone()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerIMEI".toUpperCase()),Bytes.toBytes(log.getAnswerIMEI()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("answerIP".toUpperCase()),Bytes.toBytes(log.getAnswerIP()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("questionPhone".toUpperCase()),Bytes.toBytes(log.getQuestionPhone()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("questionIMEI".toUpperCase()),Bytes.toBytes(log.getQuestionIMEI()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("questionIP".toUpperCase()),Bytes.toBytes(log.getQuestionIP()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("qid".toUpperCase()),Bytes.toBytes(log.getQid()));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("cash".toUpperCase()),PDecimal.INSTANCE.toBytes(new BigDecimal(log.getCash())));
					put.addColumn(Bytes.toBytes(AnsLog.hbase_family_1),Bytes.toBytes("hit".toUpperCase()),Bytes.toBytes(log.getHit()));//是否被命中
					bean.put(AnsLog.hbase_table, put);
					logger.info("保存完毕。。。。。。");
				}
			} catch (Exception e) {
				logger.error("保存hbase异常",e);
			}
		}
		
		
	}
}
