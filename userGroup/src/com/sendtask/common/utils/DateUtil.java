package com.sendtask.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String getNowFullDate(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		String now=sdf.format(date);
		return now;
	}
}
