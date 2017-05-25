package com.circle.netty.formation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author Created by Administrator on 2015/10/17.
 */
public class LogUtils {
    private Logger logger = LoggerFactory.getLogger("<wenaaa>");

    public static final int DEBUG = 9;
    public static final int INFO = 8;
    public static final int WARN = 7;
    public static final int ERROR = 6;
    private static int leavel = 1;
    private static LogUtils instance;

    private LogUtils() {
    }

    public static void initail(int leavel) {
        LogUtils.leavel = leavel;
        instance = new LogUtils();
    }

    public static LogUtils getInstance() {
        if(instance==null) {
            initail(DEBUG);
        }
        return instance;
    }

    public void debug(Object object, String... message) {
        if (object != null && message != null&&leavel==DEBUG)
            logger.debug(object.getClass() + Arrays.toString(message));
    }

    public void info(Object object, String... message) {
        if (object != null && message != null&&leavel>=INFO)
            logger.info(object.getClass() + Arrays.toString(message));
    }

    public void warn(Object object, String... message) {
        if (object != null && message != null&&leavel>=WARN)
            logger.warn(object.getClass() + Arrays.toString(message));
    }

    public void error(Object object, Exception e) {
        if (object != null && e != null&&leavel>=ERROR)
            logger.error(object.getClass() + e.getMessage());
    }

    public void errorall(Object object, Exception e) {
        if (object != null && e != null&&leavel>=ERROR)
            logger.error(object.getClass() + e.getMessage(), e);
    }
}
