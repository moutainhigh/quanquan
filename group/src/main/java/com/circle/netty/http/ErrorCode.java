package com.circle.netty.http;

import io.netty.channel.ChannelHandlerContext;

public class ErrorCode {

    /**
     * 系统错误
     */
    public static final String SYS_ERR = "SYS_ERR";
    public static final String ERR_404 = "ERR_404";
    public static final String OK = "OK";
    public static final String STATUS_ERR = "STATUS_ERR";
    public static final String NO_GROUPS = "NO_GROUPS";
    public static final String UID_NULL = "UID_NULL";
    public static final String USER_NOT_EXISTS = "USER_NOT_EXISTS";
    public static final String SIZE_ERR = "SIZE_ERR";
    public static final String QUE_NOT_EXISTS = "question not exists";
    public static final String USER_NO_CID = "USER_NO_CID";//用户没有CID
    public static final String TYPE_ERR = "TYPE_ERR";
    public static final String REDY_DEAL = "REDY_DEAL";//已经处理过
    public static final String DISID_NULL = "DISID_NULL";/*优惠券ID不能为空*/
    public static final String CASH_NULL = "CASH_NULL";
    public static final String FUSER_NOT_EXISTS = "FUSER_NOT_EXISTS";
}
