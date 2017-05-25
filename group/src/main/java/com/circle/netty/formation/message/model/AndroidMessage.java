package com.circle.netty.formation.message.model;

/**
 * @author Created by Fomky on 2015/9/22 0022.
 */
public class AndroidMessage {
    public static final String cmd_clfs = "clfs";
    public static final String cmd_company = "company";
    public static final String cmd_himg = "himg";
    public static final String cmd_sys = "sys";
    public static final String cmd_draw = "draw";
    public static final String cmd_complain = "complain";
    public static final String cmd_report = "report";
    public static final String cmd_url = "url";

    public static final String att_cmd = "cmd";
    public static final String att_title = "title";
    public static final String att_context = "context";
    public static final String att_data = "data";

    /**新发布问题*/
    public static final String que_iusse = "27";
    /**关于优惠券*/
    public static final String cmd_red = "11";
    /**答题人 - 未入围*/
    public static final String cmd_que_2 = "12";
    /**提问人 - 没有筛选 已经自动取消*/
    public static final String cmd_que_1 = "13";
    /**发替人 - 演示提醒 推送 无系统消息 */
    public static final String cmd_que_remind_delay = "2";
    /**提问人，题目无人抢答 */
    public static final String cmd_que_0 = "14";
    /**答题人，对方申请延时	T|S */
    public static final String cmd_que_3 = "15";
    /**提问人，对方已评价*/
    public static final String cmd_que_4 = "16";
    /**答题人，对方已评价*/
    public static final String cmd_que_5 = "17";
    /**发题人，题目进入到延时阶段*/
    public static final String cmd_que_6 = "18";
    /**答题人，题目进入到延时阶段*/
    public static final String cmd_que_7 = "19";
    /**答题人，题目完成，进入评价阶段*/
    public static final String cmd_que_8 = "20";
    /**发题人，题目进入到评价阶段*/
    public static final String cmd_que_9 = "21";
    /**答题人，题目已入围，进入到进行阶段*/
    public static final String cmd_que_10 = "22";
    /**发题人，题目进入到进行阶段*/
    public static final String cmd_que_11 = "23";
    /**发题人，题目进入到筛选*/
    public static final String cmd_que_12 = "24";
    /**答题人，题目报名*/
    public static final String cmd_que_13 = "25";
    /**发题人，发布问题有新抢答者*/
    public static final String cmd_que_14 = "26";
    /**发题人 , 对方拒绝延时 */
    public static String cmd_que_15 = "28";
}
