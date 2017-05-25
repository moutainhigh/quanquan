package com.sendtask.usergroup.zhoujia.kafka;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import com.sendtask.usergroup.zhoujia.dao.TaskDao;
import com.sendtask.usergroup.zhoujia.model.Group;
import com.sendtask.usergroup.zhoujia.utils.Config;

/**
 * @author zhoujia
 *
 * @date 2015年7月28日
 */
public class NewUserstart {
	private static Logger logger = LoggerFactory.getLogger(NewUserstart.class);
	private Config config;
    private ExecutorService executor;
    private String topic;
    private String group;
    private int pnum;
    private ConsumerConfig conf;
    public Map<Integer, UserGroupConsumer> smss = new HashMap<>();
    private ConsumerConnector consumer;
    private Group userGroup;
    private TaskDao taskDao = new TaskDao();
    
    public static Map<Integer, Map<Integer, String>> childGroupMap = new HashMap<Integer,  Map<Integer, String>>();
//    static{
//    	
//    	Map<Integer, String> qudao = new HashMap<Integer, String>();
//    	qudao.put(1, "App Store");
//    	qudao.put(2, "Google Play");
//    	
//    	Map<Integer, String> systemMap = new HashMap<Integer, String>();
//    	systemMap.put(1, "iOS");
//    	systemMap.put(1, "android");
//    	
//    	Map<Integer, String> sexMap = new HashMap<Integer, String>();
//    	sexMap.put(0, "男");
//    	sexMap.put(1, "女");
//    	
//    	Map<Integer, String> cityMap = new HashMap<Integer, String>();
//    	cityMap.put(1, "北京");
//    	cityMap.put(2, "上海");
//    	cityMap.put(3, "天津");
//    	cityMap.put(4, "郑州");
//    	cityMap.put(5, "广州");
//    	//。。。以上数据未完整
//    	childGroupMap.put(1, qudao);
//    	childGroupMap.put(2, systemMap);
//    	childGroupMap.put(3, sexMap);
//    	childGroupMap.put(4, cityMap);
//    	
//    }

    public Group getGroup(){
    	return this.userGroup;
    }

    public NewUserstart(Config config,Group group,String topic) {
    	try {//初始化分组详细信息数据
			Map<Integer, String> qudao = taskDao.groupDetailDataInit(1);
			Map<Integer, String> systemMap = taskDao.groupDetailDataInit(2);
			Map<Integer, String> sexMap = taskDao.groupDetailDataInit(3);
			Map<Integer, String> cityMap = taskDao.groupDetailDataInit(4);
			taskDao.closeConn();
	    	childGroupMap.put(1, qudao);
	    	childGroupMap.put(2, systemMap);
	    	childGroupMap.put(3, sexMap);
	    	childGroupMap.put(4, cityMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
        initialConfig(config,group,topic);//初始化配置文件
        initailTheard();//初始化线程
    }

    private void initailTheard() {
        executor = Executors.newFixedThreadPool(pnum); 
        consumer = Consumer.createJavaConsumerConnector(conf);
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, pnum);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        int threadNumber = 0;
        for (KafkaStream<byte[], byte[]> stream : streams) {
            UserGroupConsumer sms = new UserGroupConsumer(stream,threadNumber,this);
            executor.submit(sms);
            smss.put(threadNumber,sms);
            threadNumber++;
        }
    }

    private void initialConfig(Config config,Group group, String topicName) {
        this.config = config;
        pnum = this.config.getAsInteger("kafka.topic.usergroup.partion");
        topic = topicName;
        this.group = this.config.getAsString("group.id");
        conf = new ConsumerConfig(config.getProperties());
        this.userGroup = group;
    }
}
