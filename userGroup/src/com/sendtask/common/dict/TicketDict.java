package com.sendtask.common.dict;

/**
 * 优惠券字典
 * 
 * @author qiuxy
 *
 */
public class TicketDict {
	/**
	 * 领取券后 继续对比券
	 */
	public static final Integer TICKET_AFTERGET_YES = 1;
	/**
	 * 领取券后 不继续对比券
	 */
	public static final Integer TICKET_AFTERGET_NO = 2;

	/**
	 * 红包券缩写
	 */
	public static final String TICKET_NAME_RED = "RED";
	/**
	 * 默认券缩写
	 */
	public static final String TICKET_NAME_DEF = "DEF";
	/**
	 * 直投券缩写
	 */
	public static final String TICKET_NAME_DCT = "DCT";
	/**
	 * 拉人券缩写
	 */
	public static final String TICKET_NAME_PULL = "PULL";
	/**
	 * 兑换码券缩写
	 */
	public static final String TICKET_NAME_EXC = "EXC";

	/**
	 * 红包规则redis
	 */
	public static final String REDTICKET_REDIS_RULE = "RED";
	/**
	 * 红包规则版本redis
	 */
	public static final String REDTICKET_REDIS_VERSION = "REDR";
	/**
	 * 单个红包规则redis
	 */
	public static final String REDTICKET_REDIS_ONE_RULE = "REDT|";
	/**
	 * 单个红包计数redis
	 */
	public static final String REDTICKET_REDIS__ONE_COUNT = "RED|";
	/**
	 * 默认券redis
	 */
	public static final String DEFTICKET_REDIS_RULE = "DEF";
	/**
	 * 默认券规则redis
	 */
	public static final String DEFTICKET_REDIS_VERSION = "DEFR";
	/**
	 * 直投券redis
	 */
	public static final String DCTTICKET_REDIS_RULE = "DCT|";
	/**
	 * 拉人券redis
	 */
	public static final String PULLTICKET_REDIS_RULE = "PULL";
	/**
	 * 拉人券规则redis
	 */
	public static final String PULLTICKET_REDIS_VERSION = "PULLR";
	/**
	 * 兑换码券redis
	 */
	public static final String EXCTICKET_REDIS_RULE = "EXC|";

	/**
	 * 优惠券状态# 待发放1
	 */
	public static final Integer TICKET_STATUS_SENDWAIT = 1;
	/**
	 * 优惠券状态# 发放中2
	 */
	public static final Integer TICKET_STATUS_SENDING = 2;
	/**
	 * 优惠券状态# 已发完3
	 */
	public static final Integer TICKET_STATUS_SENDOVER = 3;
	/**
	 * 优惠券状态# 已停用4
	 */
	public static final Integer TICKET_STATUS_SENDSTOP = 4;

	////////////////////////////////////////////////////
	// 停发用
	////////////////////////////////////////////////////
	/**
	 * 默认券
	 */
	public static final String STOP_REDIS_DEF = "STOP|DEFTICKET1";
	public static final String STOP_REDIS_SUB_DEF = "STOPSUB|DEFTICKET1";
	/**
	 * 拉人券
	 */
	public static final String STOP_REDIS_PULL = "STOP|PULLTICKET";
	public static final String STOP_REDIS_SUB_PULL = "STOPSUB|PULLTICKET";
	/**
	 * 直投券
	 */
	public static final String STOP_REDIS_DCT = "STOP|DCTTICKET|";
	public static final String STOP_REDIS_SUB_DCT = "STOPSUB|DCTTICKET|";
	/**
	 * 红包券和兑换码券(其它不是单独有计划任务的券)
	 */
	public static final String STOP_REDIS_OTHER = "STOP|OTHERTICKET";
	public static final String STOP_REDIS_SUB_OTHER = "STOPSUB|OTHERTICKET";

	/**
	 * 系统消息停发
	 */
	public static final String STOP_REDIS_SYSMSG = "STOP|SYSMSG|";
	public static final String STOP_REDIS_SUB_SYSMSG = "STOPSUB|SYSMSG|";

	/**
	 * kafka用的存topic名的redis key
	 */
	public static final String KAFKA_REDIS_TOPIC_DEF = "kafka-defcoupon-topic";
	public static final String KAFKA_REDIS_TOPIC_PULL = "kafka-pullcoupon-topic";
}
