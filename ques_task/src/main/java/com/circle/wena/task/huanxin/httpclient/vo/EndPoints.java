package com.circle.wena.task.huanxin.httpclient.vo;

import com.circle.wena.task.huanxin.comm.Constants;
import com.circle.wena.task.huanxin.httpclient.utils.HTTPClientUtils;

import java.net.URL;


/**
 * HTTPClient EndPoints
 * 
 * @author Lynch 2014-09-15
 *
 */
public class EndPoints {

	public static URL TOKEN_APP_URL;

	public static URL USERS_URL;

	public static URL MESSAGES_URL;

	public static URL CHATGROUPS_URL;

	public static URL CHATFILES_URL;

	public static void instal(){
		TOKEN_APP_URL = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/token");
		USERS_URL = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/users");
		MESSAGES_URL = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/messages");
		CHATGROUPS_URL = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups");
		CHATFILES_URL = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatfiles");
	}

}
