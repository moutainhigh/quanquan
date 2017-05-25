package com.sendtask.common.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程池
 * 
 * @author zhoujia
 * @rewrite qiuxy
 */
public class TaskThreadPool {
	public static ExecutorService taskEs;// slave起脚本用
	public static ExecutorService scheduleEs;// schedule订阅通道收到消息用
	public static ExecutorService spareEs;// 备机订阅通道收到消息用
	public static ExecutorService stopEs;// 停机订阅通道收到消息用

	// 一共 ABQSIZE 个队列，生产者向 ABQSIZE 个队列轮循发送任务，每个队列ABQSIZE_INNER个线程消费
	// public static final int ABQSIZE = 5;
	// public static final int ABQSIZE_INNER = 10;
	// public static final int QUESIZE = 2000;
	// public static ArrayBlockingQueue<String> taskabq[] = new
	// ArrayBlockingQueue[ABQSIZE];

	// static {
	// for (int i = 0; i < ABQSIZE; i++) {
	// taskabq[i] = new ArrayBlockingQueue<String>(QUESIZE);
	// }
	// }
	private static Logger logger = LoggerFactory.getLogger(TaskThreadPool.class);
	/**
	 * 初始化线程池
	 * 
	 * @param threadNum
	 */
	public void initThreadPool(int threadNum) {
		logger.info("初始化线程池aaaaa===="+threadNum);
		taskEs = Executors.newFixedThreadPool(threadNum);/////
		scheduleEs = Executors.newFixedThreadPool(threadNum);/////
		spareEs = Executors.newFixedThreadPool(threadNum);/////
		stopEs = Executors.newFixedThreadPool(threadNum);/////
		logger.info("初始化线程池bbbbb====");
	}
}
