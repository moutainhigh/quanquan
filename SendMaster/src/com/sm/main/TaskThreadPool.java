package com.sm.main;

//import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhoujia
 *
 * @date 2015年7月24日12:28:36
 */
public class TaskThreadPool {
//	public static final int ABQSIZE=5;
//	public static final int ABQSIZE_INNER=10;
//	public static final int QUESIZE=2000;
	public static final int POOLSIZE=1000;
	public static ExecutorService taskEs = Executors.newFixedThreadPool(POOLSIZE);/////
	
//	//一共 ABQSIZE 个队列，生产者向 ABQSIZE 个队列轮循发送任务，每个队列ABQSIZE_INNER个线程消费
//	public static ArrayBlockingQueue<String> taskabq []= new ArrayBlockingQueue[ABQSIZE];
//	static{
//		for(int i=0;i<ABQSIZE;i++){
//			taskabq[i]=new ArrayBlockingQueue<String>(QUESIZE);
//		}
//	}
	
	
	public static void runThread(String param){
		GetTask gt = new GetTask(param);
		taskEs.execute(gt);
		//fjEs.shutdown();
	}
	
	/****
	 * 关闭连接池
	 */
	public static void shutdown(){
		if(taskEs != null && !taskEs.isShutdown()){
			taskEs.shutdown();
		}
	}
	
	
//	public  void runThreadPool(){
//		GetTask gt = null;
//		for(int j=0;j<ABQSIZE_INNER;j++){
//			for (int i = 0; i <ABQSIZE; i++) {
//				gt = new GetTask(i,"Queue "+i+" : Thread : "+j);
//				taskEs.execute(gt);
//			}
//		}
//		//fjEs.shutdown();
//	}
	
}
