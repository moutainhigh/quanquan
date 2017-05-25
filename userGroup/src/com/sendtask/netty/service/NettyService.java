package com.sendtask.netty.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.util.Config;
import com.circle.imhxin.httpclient.BaseClient;
import com.sendtask.netty.dict.NettyIfaceDict;

public class NettyService {
	private static Logger logger = LoggerFactory.getLogger(NettyService.class);

	private static String nettyServer;

	public static void init(String configPath) throws IOException {
		Config config = new Config(configPath + "conf.properties");
		nettyServer = config.getAsString("group.lvs.server");
	}

	/**
	 * 对单个人发送推送
	 * 
	 * @throws Exception
	 */
	public void pushcid(String uid, String context) throws Exception {
		BaseClient httpClient = new BaseClient();
		Map<String, String> param = new HashMap<String, String>();
		param.put("uid", uid);
		param.put("context", context);
		param.put("type", "0");
		param.put("data", "");

		logger.info("准备推送给uid[“+uid+”]======context=" + context);
		try {
			httpClient.post("http://" + nettyServer + NettyIfaceDict.URL_NETTY_TS_ONE, param);
		} catch (IOException e) {
			logger.error("发送http请求异常,url=" + nettyServer + "\n uid=" + uid + "\n context=" + context, e);
		}
	}

	/**
	 * 给用户添加优惠券
	 * 
	 * @param uid
	 * @param disid
	 *            优惠券ID
	 * @param json
	 *            优惠券内容 coupon串
	 * @param isend
	 *            是否发送默认推送 （默认0 发送消息，其不发送）[是否发系统消息0是发]
	 * @param cmd
	 *            是否推送 0 （默认 推送） 其他值 不推送 [是否有推送0是有]
	 * @param context
	 *            消息内容
	 * @param from
	 *            拉人券 来源人
	 * @throws Exception
	 */

	public void addcoupon(String uid, String disid, String json, String isend, String cmd, String context, String from)
			throws Exception {
		BaseClient httpClient = new BaseClient();
		Map<String, String> param = new HashMap<String, String>();
		param.put("uid", uid);
		param.put("disid", disid);
		param.put("json", json);
		param.put("isend", isend);
		param.put("cmd", cmd);
		if ("0".equals(isend)) {// 不发系统消息和推送
			param.put("context", context);
		}
		if (from != null) {
			param.put("from", from);
		}

		// String url = NettyUrlService.getRandomNettyUrl();
		try {
			httpClient.post("http://" + nettyServer + NettyIfaceDict.URL_NETTY_YHQ_ONE, param);
		} catch (IOException e) {
			logger.error(
					"发送http请求异常,url=" + nettyServer + "\n context=" + context + "\n disid=" + disid + "\n uid=" + uid,
					e);
		}
	}

	/**
	 * 调接口发消息
	 * 
	 * @param type
	 *            0是有推送 1是没推送
	 */
	public void sendMsg(String uid, String json, String type) {
		BaseClient httpClient = new BaseClient();
		Map<String, String> param = new HashMap<String, String>();
		param.put("uid", uid);
		param.put("json", json);
		param.put("type", type);
		String date = new Date().getTime() + "";
		param.put("time", date);
		try {
			httpClient.post("http://" +nettyServer + NettyIfaceDict.URL_NETTY_SYSTEM_MSG, param);
		} catch (IOException e) {
			logger.error("调用发消息接口报错===url=" + nettyServer + NettyIfaceDict.URL_NETTY_SYSTEM_MSG + ",uid=" + uid
					+ ",json=" + json + ",time=" + date, e);
		}
	}

	//////////////////////////////////////////////////
	///////// 以下待测 目前也没用

	/**
	 * 对单用户发送透传消息
	 * 
	 * @param type
	 * @param json
	 * @param cid
	 * @throws Exception
	 */
	public void transcid(String type, String json, String cid) throws Exception {
		BaseClient httpClient = new BaseClient();
		Map<String, String> param = new HashMap<String, String>();
		param.put("type", type);
		param.put("json", json);
		param.put("cid", cid);
		// String url = NettyUrlService.getRandomNettyUrl();
		try {
			httpClient.post("http://" + nettyServer + NettyIfaceDict.URL_NETTY_TC_ONE, param);
		} catch (IOException e) {
			logger.error("发送http请求异常,url=" + nettyServer + "\n type=" + type + "\n json=" + json + "\n cid=" + cid, e);
		}
	}

	/**
	 * 对整个APP发送透传消息
	 * 
	 * @param type
	 * @param json
	 * @param tag
	 * @param phone
	 * @param province
	 * @throws Exception
	 */
	public void transapp(String type, String json, String tag, String phone, String province) throws Exception {
		BaseClient httpClient = new BaseClient();
		Map<String, String> param = new HashMap<String, String>();
		param.put("type", type);
		param.put("json", json);
		if (tag != null) {
			param.put("tag", tag);
		}
		if (phone != null) {
			param.put("phone", phone);
		}
		if (province != null) {
			param.put("province", province);
		}
		// String url = NettyUrlService.getRandomNettyUrl();
		try {
			httpClient.post("http://" + nettyServer + NettyIfaceDict.URL_NETTY_TC_APP, param);
		} catch (IOException e) {
			logger.error("发送http请求异常,url=" + nettyServer + "\n type=" + type + "\n json=" + json + "\n tag=" + tag
					+ "\n phone=" + phone + "\n province=" + province, e);
		}
	}

	/**
	 * 对整个APP的用户发发送推送消息
	 * 
	 * @param sys
	 * @param title
	 * @param context
	 * @param type
	 * @param json
	 * @param phone
	 * @param province
	 * @throws Exception
	 */
	public void pushapp(String sys, String title, String context, String type, String json, String phone,
			String province) throws Exception {
		BaseClient httpClient = new BaseClient();
		Map<String, String> param = new HashMap<String, String>();
		param.put("sys", sys);
		param.put("title", title);
		param.put("context", context);
		param.put("type", type);
		param.put("json", json);
		if (phone != null) {
			param.put("phone", phone);
		}
		if (province != null) {
			param.put("province", province);
		}
		// String url = NettyUrlService.getRandomNettyUrl();
		try {
			httpClient.post("http://" + nettyServer + NettyIfaceDict.URL_NETTY_TS_APP, param);
		} catch (IOException e) {
			logger.error(
					"发送http请求异常,url=" + nettyServer + "\n type=" + type + "\n json=" + json + "\n sys=" + sys
							+ "\n phone=" + phone + "\n province=" + province + "\n sys=" + sys + "\n title=" + title,
					e);
		}
	}

}
