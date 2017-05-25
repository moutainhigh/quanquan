package com.rulesfilter.yy.zj.main;


import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import com.circle.core.util.Config;
import com.rulesfilter.yy.zj.service.RuleFilterService;
import com.rulesfilter.yy.zj.utils.FileUtil;
import com.rulesfilter.yy.zj.utils.ParamStatic;
import com.rulesfilter.yy.zj.utils.SystemUtils;

/**
 * @author zhoujia
 *
 * @date 2015年8月29日
 */
public class RuleFilter {
	private static Logger logger = LoggerFactory.getLogger(RuleFilter.class);
	
	public static void main(String[] args) {
//		args = new String[1];
//		args[0] = "config/";
		if(args == null || args.length == 0){
			logger.error("配置文件路径为空");
			return;
		}
		if(!args[0].endsWith("/")){//配置文件路径必须已/结尾
			logger.error("the config path endwith '/'");
			return ;
		}
		ParamStatic.configPath = args[0];
		PropertyConfigurator.configure(args[0]+ParamStatic.log4jConfigFile);
		RuleFilterService ruleFilterService = new RuleFilterService();
		//初始化
		ruleFilterService.init(args[0]);
		
		try {
			int pid = SystemUtils.getPid();
			Config conf = new Config(args[0]+ParamStatic.configFileName);
			String pudFilePath = conf.getAsString("pidfile");
			FileUtil.crateContentFile(pudFilePath,String.valueOf(pid));
		} catch (IOException e) {
			logger.error("写pid文件报错，请检查文件路径和权限");
		}
		
		logger.info("过滤服务启动完毕");
		linuxMessageLister();
	}
	/***
	 * 
	 */
    public static void linuxMessageLister(){
    	SignalHandler handler = new SignalHandler() {
    	    @Override
    	    public void handle(Signal signal) {
    	      //关闭进程 -- 程序关闭流程 实现
    	      //HttpServer.server.close();
    	    	logger.info("关闭程序。。。");
    	    	System.exit(0);
    	    }
    	};
    	//注册关闭信号
    	Signal.handle(new Signal("TERM"),handler);//kill 15
    	//Signal.handle(new Signal("KILL"), handler);//相当于kill -9
    	Signal.handle(new Signal("INT"), handler);//相当于Ctrl+C
    }
}
