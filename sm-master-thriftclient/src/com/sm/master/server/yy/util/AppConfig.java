package com.sm.master.server.yy.util;

/**
 * all types  : Java IOS Andorid WEB Hadoop .NET
 * Created by Fomky on 2015/3/5 0005.
 */
public class AppConfig {
	public static final long MSG_MAX_NUMBER = 200;

    /**
     * 请求内容最大长度
     */
    public static int MAX_CONTENT_LENGTH=1024000;

    /**
     * 最大工作线程
     */
    public static int MAX_WORKER_THREAD;
    /**
     * 最小工作线程
     */
    public static int MIN_WORKER_THREAD;
    
    public static Integer PORT;

    public static void inital(Cfs cfs) {
//    	USER_SIGIN_OUTTIME=cfs.getAsInteger("USER_SIGIN_OUTTIME");
//    	USER_SIGIN_TIMES_USE_CODE=cfs.getAsInteger("USER_SIGIN_TIMES_USE_CODE");
//    	USER_SIGIN_IN_TIMES=cfs.getAsInteger("USER_SIGIN_IN_TIMES");
//    	FILE_ROOT = cfs.getAsString("file_root");
//    	FILE_IMAGE = cfs.getAsString("file_image");
//    	FILE_MSG = cfs.getAsString("file_msg");
//    	DATA_ROOT = cfs.getAsString("data_root");
    	PORT = cfs.getAsInteger("port");
    	MAX_WORKER_THREAD = cfs.getAsInteger("maxworker");
    	MIN_WORKER_THREAD = cfs.getAsInteger("minworker");
    }
}
