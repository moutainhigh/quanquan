package com.sendtask.common.dict;

/**
 * 广告&消息&投诉字典
 * 
 * @author qiuxy
 */
public class AMCDict {
	/**
	 * 广告redis
	 */
	public static final String ADVERT_REDIS_PREFIX = "AD|";
	public static final String ADVERT_REDIS_VERSION = "ADR";

	/**
	 * 消息redis
	 */
	public static final String MESSAGE_REDIS_RULE = "SYSMESSAGE";
	public static final String MESSAGE_REDIS_COMMON = "MSGCOMMON";
	public static final String MESSAGE_REDIS_PREFIX = "MSG|";
	public static final String MESSAGE_REDIS_SUB = "MESSAGESUB";

	/**
	 * 新增
	 */
	public static final Integer MESSAGE_TASK_INSERT = 1;
	/**
	 * 修改
	 */
	public static final Integer MESSAGE_TASK_UPDATE = 2;
	/**
	 * 消息类型 全部
	 */
	public static final int MESSAGE_TYPE_ALL = 0;
	/**
	 * 消息类型 推送
	 */
	public static final int MESSAGE_TYPE_PUSH = 1;
	/**
	 * 消息类型 系统
	 */
	public static final int MESSAGE_TYPE_SYS = 2;

	/**
	 * 固定消息
	 */
	public static final String FIXEDMESSAGE_REDIS_PREFIX = "FMSG|";// 前缀

	/**
	 * 投诉redis&hbase
	 */
	public static final String COMPLAINT_REDIS_QUESTION_CONTENT = "COMPCONTENT|QUESTION";
	public static final String COMPLAINT_NETTY_QUESTION_CONTENT_S = "COMPQ";
	public static final String COMPLAINT_REDIS_ANSWER_CONTENT = "COMPCONTENT|ANSWER";
	public static final String COMPLAINT_NETTY_ANSWER_CONTENT_S = "COMPA";
	public static final String COMPLAINT_REDIS_COMMON_REPORT = "COMPREPORT ";
	public static final String COMPLAINT_HBASE_TABLE = "art_complanint";
//	public static final byte[] COMPLAINT_HBASE_FAMILY = Bytes.toBytes("info");

	/** 投诉时间 */
	public static final String date_str = "date";
	/** 投诉人ID */
	public static final String fuid_str = "fuid";
	/** 被投诉人 */
	public static final String tuid_str = "tuid";
	/** 问题ID */
	public static final String qid_str = "qid";
	/** 投诉理由 */
	public static final String reason_str = "reason";
	/** 图片 */
	public static final String img_str = "img";
	/** 描述 */
	public static final String context_str = "context";
	/** 处理方式 */
	public static final String identity_str = "identity";
	public static final String status_str = "status";

	// 处理三种状态
	public static final int COMPLAINT_STATUS_IGNORE = 1;// 忽略
	public static final int COMPLAINT_STATUS_APPEASE = 2;// 安抚
	public static final int COMPLAINT_STATUS_HANDLE = 3;// 处理

}
