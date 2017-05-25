package com.sendtask.common.utils;

import java.util.List;

public class StringUtil {
	/**
	 * 将List 转化为指定分隔符的字符串
	 * @param list
	 * @param separator
	 * @return
	 */
	public static String listToString(List list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size() - 1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
	
	public static boolean isNullStr(String str){
		if(str==null){
			return true;
		}else{
			if(str.length()==0||str.trim().length()==0){
				return true;
			}else{
				return false;
			}
		}
	}

}
