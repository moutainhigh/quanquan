package com.sm.master.server.yy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String getNowFullDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return sdf.format(date);
	}

	public static Date formatStringToDate(String dateStr) {
		if(dateStr == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date formatStringToDate(String dateStr,String type) {
		if(type==null){
			return formatStringToDate(dateStr);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat(type);
			try {
				return sdf.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}

	}
	
	public static String formatLongToStr(Long longTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(longTime);
		return sdf.format(date);
	}

	public static String formatLongToStr(Long longTime, String type){
		if(type==null){
			return formatLongToStr( longTime);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat(type);
			Date date = new Date(longTime);
			return sdf.format(date);
		}

	}

	public static String formatDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	public static String formatDate(Date date, String format){
		if(format==null){
			return formatDate(date);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}
	}
}
