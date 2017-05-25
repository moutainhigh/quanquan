package com.sm.master.server.yy.exec;

import com.lexjishu.thrift.yy.StatisticsService;
import com.lexjishu.thrift.yy.UserInfoService;
import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sm.master.server.yy.util.Cfs;
import com.sm.master.server.yy.util.DateUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Thrift 数据平台服务 启动类
 * 
 */
public class AppExe {
	private static Logger logger = LoggerFactory.getLogger(AppExe.class);

//	/**
//	 * 启动thrift 服务
//	 */
//	public void startThriftServers(int port) {
//		try {
//
//			SystemServiceImpl systemServiceImpl = new SystemServiceImpl();
//			TProcessor systemService = new SystemService.Processor<SystemServiceImpl>(systemServiceImpl);
//			//添加多个Service
//			TMultiplexedProcessor processors = new TMultiplexedProcessor();
//
//			TServerTransport serverTransport = new TServerSocket(port);
//			Args args = new Args(serverTransport);
//			TProtocolFactory factory = new TCompactProtocol.Factory();
//			args.protocolFactory(factory);
//			TFramedTransport.Factory tFactory =new TFramedTransport.Factory();
//			args.transportFactory(tFactory);
//			args.maxWorkerThreads(AppConfig.MAX_WORKER_THREAD);
//			args.maxWorkerThreads(AppConfig.MIN_WORKER_THREAD);
//			TProcessorFactory protocolFactory = new TProcessorFactory(processors);
//			args.processorFactory(protocolFactory);
//			TServer server = new TThreadPoolServer(args);
//			processors.registerProcessor("systemService", systemService);
//			logger.info(DateUtil.getNowFullDate()+": Starting the simple server... PORT=" + port );
//			server.serve();
//		} catch (Exception e) {
//			logger.error("start error",e);
//		} catch (TTransportException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 生成全部问题详情数据
	 * thrift client
	 * @param date
     */
	public void thriftClient(String date){

		try {
			Cfs cfs = new Cfs("config/app.properties");
			TTransport transport = new TFramedTransport(new TSocket(cfs.getAsString("IP"), cfs.getAsInteger("port")));
			TProtocol protocol = new TCompactProtocol(transport);

			TMultiplexedProtocol statisticsService = new TMultiplexedProtocol(protocol, "statisticsService");

			StatisticsService.Client client = new StatisticsService.Client(statisticsService);

			transport.open();
			client.questionDetail(date);
			transport.close();
		} catch (TException | IOException e) {
			e.printStackTrace();
		}

	}

	/***
	 * 生成指定日期的问题详情
	 * @param date
     */
	public void thriftClientEarly(String date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(DateUtil.formatStringToDate(date).getTime()));
		try {
			Cfs cfs = new Cfs("config/app.properties");
			TTransport transport = new TFramedTransport(new TSocket(cfs.getAsString("IP"), cfs.getAsInteger("port")));
			TProtocol protocol = new TCompactProtocol(transport);

			TMultiplexedProtocol statisticsService = new TMultiplexedProtocol(protocol, "statisticsService");

			StatisticsService.Client client = new StatisticsService.Client(statisticsService);
			transport.open();

			while (true){ // 循环计算从开始日期的每天的数据，知道今天为止，计算量较大，请耐心等待
				client.questionDetail(DateUtil.formatDate(cal.getTime()));
				cal.add(Calendar.DAY_OF_MONTH, 1);
				Calendar newCal = Calendar.getInstance();
				newCal.setTime(new Date());
				logger.info("----------------" + DateUtil.formatDate(cal.getTime()) + "的数据-------------");
				if(cal.after(newCal))
					break;
			}

			transport.close();
		} catch (TException | IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 生成所有用户的答题数，问题数，答题评价平均分， 问题评价平均分
	 */
	public void thriftClientCreateUserCountData(){
		try {
			Cfs cfs = new Cfs("config/app.properties");
			TTransport transport = new TFramedTransport(new TSocket(cfs.getAsString("IP"), cfs.getAsInteger("port")));
			TProtocol protocol = new TCompactProtocol(transport);

			TMultiplexedProtocol userInfoService = new TMultiplexedProtocol(protocol, "userInfoService");

			UserInfoService.Client client = new UserInfoService.Client(userInfoService);

			transport.open();
			client.countAllUserData();
			transport.close();
		} catch (TException | IOException e) {
			e.printStackTrace();
		}
	}


	/***
	 * 拷贝真实的用户和问题到查询表
	 */
	public void thriftClientCopyUserAndQuestion(String day){
		try {
			Cfs cfs = new Cfs("config/app.properties");
			TTransport transport = new TFramedTransport(new TSocket(cfs.getAsString("IP"), cfs.getAsInteger("port")));
			TProtocol protocol = new TCompactProtocol(transport);
			TMultiplexedProtocol userInfoService = new TMultiplexedProtocol(protocol, "userInfoService");
			UserInfoService.Client client = new UserInfoService.Client(userInfoService);
			transport.open();
			client.transportDate(day);
			transport.close();
		} catch (TException | IOException e) {
			e.printStackTrace();
		}
	}


	/***
	 * 更新elastic中random的sort
	 */
	public void thriftClientUpdateSort(){
		try {
			Cfs cfs = new Cfs("config/app.properties");
			TTransport transport = new TFramedTransport(new TSocket(cfs.getAsString("IP"), cfs.getAsInteger("port")));
			TProtocol protocol = new TCompactProtocol(transport);
			TMultiplexedProtocol userInfoService = new TMultiplexedProtocol(protocol, "userInfoService");
			UserInfoService.Client client = new UserInfoService.Client(userInfoService);
			transport.open();
			client.updateUserRandomSort();
			transport.close();
		} catch (TException | IOException e) {
			e.printStackTrace();
		}
	}



	
	public static void main(String[] args) throws Exception {
//		args = new String[2];
//		args[0] = "thriftClientEarly";
//		args[1] = "2016-04-06 00:00:00";
		PropertyConfigurator.configure("config/log4j.properties");
		logger.info("传人参数 = = =" + args);

		if(args != null && args.length >0){ //请求某一个方法
			execMethod( args);

		}else{// 默认每天的定时任务
			logger.info("这个是默认定时任务");
			String[] args0 = new String[1];
			args0[0] = "thriftClient";
			execMethod(args0);
			args0[0] = "thriftClientCreateUserCountData";
			execMethod(args0);
		}

	}


	private static void execMethod(String[] args){
		AppExe appExe = new AppExe();
		switch (args[0]){
			case "thriftClientCreateUserCountData"://
				logger.info("统计用户的答题数，发题数，答题评价分数和发题评价分数");
				appExe.thriftClientCreateUserCountData();
				break;
			case "thriftClientEarly"://传人起始时间，从起始时间开始计算每天的数据 yyyy-MM-dd HH:mm:ss
				logger.info("计算历史数据");
				appExe.thriftClientEarly(args[1]);
				break;
			case "thriftClientCopyUserAndQuestion": // 定时任务参数为 ： thriftClientCopyUserAndQuestion  20160407
				logger.info("考呗用户数据和问题数据");
				appExe.thriftClientCopyUserAndQuestion(args[1]);
				break;
			case "thriftClientUpdateSort":// 不需要传参数
				logger.info("每天定时更新elastic中的sort");
				appExe.thriftClientUpdateSort();
				break;
			case "thriftClient":
				Date date = new Date(System.currentTimeMillis() - 24*3600*1000);//今天生成昨天的统计情况
				String yyyyMMdd = DateUtil.formatDate(date, "yyyyMMdd");

				logger.info("首先拷贝 - 用户数据和问题数据");
				appExe.thriftClientCopyUserAndQuestion(yyyyMMdd);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String format = sdf.format(date);
				logger.info("再次 - 计算当天数据:"+format);
				appExe.thriftClient(format);
				break;

		}

	}


}
