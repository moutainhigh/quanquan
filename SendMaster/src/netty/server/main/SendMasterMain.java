package netty.server.main;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import netty.server.AppConfig;
import netty.server.HttpServer;
import netty.server.Urls;

import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.sm.service.ZookeeperService;
import com.sm.util.FileUtil;
import com.sm.util.StaticParam;
import com.sm.util.SystemUtils;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class SendMasterMain {
	private static Logger logger = LoggerFactory.getLogger(SendMasterMain.class);
    public static ApplicationContext context;
    public static HttpServer httpServer;
    public static String def_config_path = "app.properties";
    /**
     * 关于 在Windows 中 IO 异常 Could not locate executable null\bin\winutils.exe in the Hadoop binaries.
     * 可以不用理会 , 不影响正常使用.
     * @throws Exception
     */
    public static String pid;
    
    private void initial(String pid , String pidPath) throws Exception {
    	AtomicReference<Config> conf = new AtomicReference<>(new Config(StaticParam.CONFIG_URL + def_config_path));
        AppConfig.inital(conf.get());
        
        //初始化redis连接
        Redis.initial(conf.get());
        //创建shard链接
        Redis.initialShard(conf.get());
        //初始化Elastic 连接
      //  CElastic.inital(conf.get());
        //初始化Hbase连接
      //  CHbase.instance();
        //初始化那个接口数据
        Urls.create("uris.xml");
//        //初始化环信配置,获取环信token
//        //添加定期缓存 环信Token
        //MsgService.create();
        //初始化kafka 链接
//        Config config = new Config("producer.properties");
//        Producers.initial(config);
        //context = new AnnotationConfigApplicationContext("com.circle.programmer.netty");
        //写pid
        pid = String.valueOf(pid);
        context = new ClassPathXmlApplicationContext("spring-core.xml");
        httpServer = (HttpServer) context.getBean("server");
        logger.info("master 关键位置： 端口" + AppConfig.SERVER_PORT + "   ===== pidPath="+pidPath);
        httpServer.runHttp(AppConfig.SERVER_PORT,false,context,pidPath);
    }
    public static void main(String[] args) throws Exception {
//    	args = new String[1];
//    	args[0]="config/";
    	logger.info("================启动master====================");
    	if(args == null){
    		throw new Exception("配置文件路径为空，请输入配置配置文件路径：");
    	}
    	String confUrl = args[0];
    	StaticParam.CONFIG_URL = confUrl;
    	PropertyConfigurator.configure(StaticParam.CONFIG_URL+ StaticParam.log4jConfigFileName);
    	//记录pid，运维使用
    	int pid = SystemUtils.getPid();
    	Config conf = new Config(StaticParam.CONFIG_URL+StaticParam.configFileName);
    	String pudFilePath = conf.getAsString("pidfile");
    	FileUtil.crateContentFile(pudFilePath,String.valueOf(pid));
    	
    	AppConfig.inital(new Config(StaticParam.CONFIG_URL+def_config_path));
    	
    	ZookeeperService zkService = new ZookeeperService();
		//创建root根目录
		zkService.writeRootPath();
		logger.info("zookeeper 根目录创建成功");
		//监控子节点
		zkService.watcherSlave();
		logger.info("监控子节点 成功");
		//创建数据目录
		zkService.createDataPath();
		logger.info("数据节点创建 成功");
		//初始化线程池
//		TaskThreadPool threadPool = new TaskThreadPool();
//		threadPool.runThreadPool();
		
		logger.info("master 成功启动： 端口" + AppConfig.SERVER_PORT);
		logger.info("===============启动master结束===================");
        SendMasterMain exe = new SendMasterMain();
        f();
        exe.initial(String.valueOf(pid),pudFilePath);
    }
    
    public static void f(){
    	SignalHandler handler = new SignalHandler() {
    	    @Override
    	    public void handle(Signal signal) {
    	        //关闭进程 -- 程序关闭流程 实现
    	    	logger.info("程序开始关闭。。。。。。。。。。。。。。。。。。");
    	    	HttpServer.server.close();
    	    	System.exit(0);
    	    }
    	};
    	//注册关闭信号
    	Signal.handle(new Signal("TERM"),handler);//kill 15
    	//Signal.handle(new Signal("KILL"), handler);//相当于kill -9
    	Signal.handle(new Signal("INT"), handler);//相当于Ctrl+C
    }
    
}


