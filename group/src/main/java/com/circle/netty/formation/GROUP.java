package com.circle.netty.formation;

import com.circle.core.elastic.CElastic;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.circle.netty.formation.util.AppConfig;
import com.circle.netty.formation.util.ProducerPool;
import com.circle.netty.formation.util.SensitivewordFilter;
import com.circle.netty.http.HttpServer;
import com.circle.netty.http.Urls;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Created by cxx on 15-8-7.
 */
public class GROUP {
    private static Logger logger = LoggerFactory.getLogger(GROUP.class);
    public static ApplicationContext context;
    public static final String DEF_CONFIG = "config";
    public static ZooKeeper zooKeeper;
    public static ExecutorService executor;
    public static String pid;
    public static String config_path;
    public static String hbase_path = "hbase-site.xml";
    public static String log4j_path = "log4j.properties";
    public static String producer_path = "producer.properties";

    /**
     * 关于 在Windows 中 IO 异常 Could not locate executable null\bin\winutils.exe in the Hadoop binaries.
     * 可以不用理会 , 不影响正常使用.
     */
    public void initial(Config conf, boolean asyn) throws Exception {
        PropertyConfigurator.configure(log4j_path);
        AppConfig.inital(conf);
        //初始化redis连接
        Redis.initialShard(conf);
        Redis.initial(conf);
        //初始化Elastic 连接
        CElastic.inital(conf);
        //初始化Hbase连接
        CHbase.instance(hbase_path);
        //加载敏感词,初始化到内存中
        SensitivewordFilter.init(config_path + "words.dlt");
        //初始化线程池
//        executor = Executors.newFixedThreadPool(Verification.getInt(40000,conf.getAsString("server.tuisong.thread.size")));
        executor = Executors.newCachedThreadPool();
        //初始化那个接口数据
        Urls.create("uris_formation.xml");
        AppConfig.producerPool = new ProducerPool<>(producer_path);
        context = new ClassPathXmlApplicationContext("spring-core.xml");
        HttpServer httpServer = (HttpServer) context.getBean("server");
        //Asyn
        pid = String.valueOf(HttpServer.getPid());
        httpServer.runHttp(AppConfig.SERVER_PORT, asyn, context, AppConfig.pid_path);
    }

    public static void main(String[] args) throws Exception {
        //注册关闭信号
        SignalHandler handler = new SignalHandler() {
            @Override
            public void handle(Signal signal) {
                //关闭进程 -- 程序关闭流程 实现
                HttpServer.server.close();
                logger.info("关闭服务");
                System.exit(0);
            }
        };
        Signal.handle(new Signal("TERM"), handler);//kill 15
        GROUP exe = new GROUP();
        //初始化配置文件
        String url_path = DEF_CONFIG;
        if (args.length == 1) {
            url_path = args[0];
        }
        logger.info("Config path : " + url_path);
        Config conf = new Config(url_path+"/app.properties");
        config_path = url_path + File.separator;
        String hbasepath = conf.getAsString("hbase-site.xml");
        if (StringUtils.isNotEmpty(hbasepath)) {
            hbase_path = config_path + hbasepath;
        }
        String kafkapath = conf.getAsString("kafka.producer.properties");
        if (StringUtils.isNotEmpty(kafkapath)) {
            producer_path = config_path + kafkapath;
        }
        String log4jpath = conf.getAsString("server.log4j.properties");
        if (StringUtils.isNotEmpty(log4jpath)) {
            log4j_path = config_path + log4jpath;
        }
        exe.initial(conf, false);
    }
}
