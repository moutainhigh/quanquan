package com.sendtask.netty.dict;

/**
 * netty接口名
 * 
 * @author qiuxy
 *
 */
public class NettyIfaceDict {
	/**
	 * 对单用户发送透传消息
	 */
	public final static String URL_NETTY_TC_ONE = "/message/transcid";
	/**
	 * 对整个APP发送透传消息
	 */
	public final static String URL_NETTY_TC_APP = "/message/transapp";
	/**
	 * 对单个人发送推送
	 */
	public final static String URL_NETTY_TS_ONE = "/message/pushcid";
	/**
	 * 对整个APP的用户发发送推送消息
	 */
	public final static String URL_NETTY_TS_APP = "/message/pushapp";
	/**
	 * 给用户添加优惠券
	 */
	public final static String URL_NETTY_YHQ_ONE = "/message/addcoupon";
	/**
	 * 发系统消息
	 */
	public final static String URL_NETTY_SYSTEM_MSG = "/message/addsysmsg";
}
