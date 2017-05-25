package com.sendtask.common.utils;

import java.util.HashMap;
import java.util.Map;

public class ReturnUtil {
	public static final String ERROR = "100";
	public static final String SUCCESS = "200";

	public static Map<String, String> result(String message, String code) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("message", message);
		result.put("code", code);
		return result;
	}
}
