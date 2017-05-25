package com.circle.netty.formation.message.model;

/**
 * 定义消息
 * @author Created by cxx on 15-8-11.
 */
public class Msg {
    /**问题-貌似是主页的*/
    /**问题详情*/
    public static final String type_quedetail = "questiondetail";
    /**发布问题*/
    public static final String type_issue = "issue";
    /**支付提醒*/
    public static final String type_pay = "pay";
    /**我的问题*/
    public static final String type_myque = "myque";
    /**我的回答*/
    public static final String type_myans = "myans";
    /** 有新问题 */
    public static final String type_new = "newque";
    /**用户延时申请*/
    public static String type_delay_request = "delayreq";
    /** 延时提醒 */
    public static String type_delay_remind = "delayremind";
    /** 取消订单 */
    public static String type_cancel = "cancael";
    /**画板*/
    public static String type_board ="board";
    public static String type_sysmsg = "sysmsg";
    public static String type_cancael  ="cancael";
    /**命令 */
    private String type;
    /**数据 */
    private Object list;
    /***/
    private int status;
    private int mtype;

    public int getStatus() {
        return status;
    }

    public int getMtype() {
        return mtype;
    }

    public void setMtype(int mtype) {
        this.mtype = mtype;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getList() {
        return list;
    }

    public void setList(Object list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "type='" + type + '\'' +
                ", list=" + list +
                '}';
    }
}
