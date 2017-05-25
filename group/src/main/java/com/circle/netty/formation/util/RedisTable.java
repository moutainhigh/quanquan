package com.circle.netty.formation.util;

/**
 * @author Created by cxx on 15-8-12.
 */
public class RedisTable {
    public static final String MOB_CODE = "MCD|";//手机验证码
    public static final String PARAM_PUB_ROLES = "PublishRoles-parameters";//发布问题权限参数
    public static final String PARAM_PUB_ROLES_NUM = "PBR|";//发布问题权限参数
    public static final String USER_G_MOB = "G|";//用户ID存手机
    public static final String USER_TOK = "TOK|";//用户登录标识
    public static final String USER_UID = "UID|";//用户ID标识,value=mobile_sha1
    /**总共 Money 优惠券 和 现金券 */
    public static final String TOTLECM = "TOTLECM|";
    public static final String TOTLECM_COUPON = "COUPON|";
    public static final String TOTLECM_MONEY = "MONEY|";
    public static final String PARAM_TIMEAXIS = "timeaxis-parameters";//时间轴参数设置
    public static final String PARAM_SMS_VERIFICATION = "SMS-verification-code-parameters";//短信验证码参数
    public static final String PARAM_QUESTION = "question-parameters";//提问参数
    public static final String PARAM_PUBLISH = "publish-parameters";//发布问题参数
    public static final String PARAM_APPRAISE = "appraise-parameters";//评价参数
    public static final String PARAM_REFRESH = "refresh-list-parameters";//刷新列表参数
    public static final String PARAM_SCREENING = "screening-parameters";//筛选及提醒参数
    public static final String USER_MOB_TOK = "MTOK|";
    public static final String DEVICE_MCD = "MCD|";//手机发短信,次数记录 .
    public static final String PRICETAGS = "price-tags";//价格标签
    public static final String T = "T|"; //用户TTL 每天发题权限 - hash
    public static final String PUB_QUES = "publish";//每天发布问题

    public static final String A = "A|";//捎句话
    public static final String QRP = "QRP|";//抢答分包
    public static final String TI = "TI|";//抢答锁定
    public static final String QTD = "QTD|";//抢答锁定 QTD|qid
    public static final String CLASSIFY = "CLF|";
    public static final String SA = "SA|";
    public static final String SQ = "SQ|";
    public static final String QFU = "QFU|"; //刷新到该题目的人
    public static final String USER_INCR_SORT = "USER_INCR_SORT";
    public static final String QPL = "QPL|";//推送列表
    public static final String CPNL = "CPNL|";
    public static final String CPNH = "CPNH|";
    // Modified 2015年09月10日16:38:18 chenxx
    public static final String STREAM_ALL = "S_ALL|";//用户流水--,全部
    public static final String STREAM_PROFIT = "S_PROFIT|";//收益--流水
    public static final String STREAM_DRAW = "S_DRAW|";//提现 --流水
    public static final String STREAM_TIP = "S_TIP|";//消费--流水
    public static final String STREAM_BACK = "S_BACK|";//退款--流水
    public static final String STREAM_GOINCOME = "S_GOINCOME|";//违规收入-流水


    public static final String FMSG_RQS = "FMSG|RQS";//#提醒发题人要进行筛选 FMSG_RQS
    public static final String FMSG_CHD = "FMSG|CHD";//#投诉处理完毕 FMSG_CHD
    public static final String FMSG_RAN = "FMSG|RAN";//#提醒答题人已入围 FMSG_RAN'
    public static final String FMSG_GRP = "FMSG|GRP";//获得红包 FMSG_GRP
    public static final String FMSG_RANN = "FMSG|RANN";//#提醒答题人未入围 FMSG_RANN
    public static final String FMSG_BLD = "FMSG|BLD";//#被评价 FMSG_BLD
    public static final String FMSG_PEM = "FMSG|PEM";//#提现输入密码错误超过5次 FMSG_PEM
    public static final String FMSG_CAT = "FMSG|CAT";//#佣金到账 FMSG_CAT
    public static final String FMSG_TPH = "FMSG|TPH";//#任务推送 FMSG_'TPH
    public static final String FMSG_ADY = "FMSG|ADY";//#对方申请延时 FMSG_ADY
    public static final String FMSG_HTH = "FMSG|HTH";//#头像验证通过 FMSG_HTH
    public static final String FMSG_HNT = "FMSG|HNT";//#头像验证未通过 FMSG_HNT
    public static final String FMSG_CTH = "FMSG|CTH";//#公司验证通过 FMSG_CTH
    public static final String FMSG_CNT = "FMSG|CNT";//#公司验证未通过 FMSG_CNT
    public static final String FMSG_QNA = "FMSG|QNA";//#题目无人抢答，已取消 FMSG_QNA
    public static final String FMSG_TYPE_SYS = "SYS";//系统消息
    public static final String FMSG_TYPE_MSG = "MSG";//短信消息
    public static final String FMSG_TYPE_PUSH = "PUSH";//推送消息
    public static final String FMSG_TYPE_VOC = "VOC";//语音消息
    public static final String FMSG_TYPE_TASK = "TASK";//任务消息

    public static final String defcoupon = "kafka-defcoupon-topic";//默认券
    public static final String pullcoupon = "kafka-pullcoupon-topic";//拉人券
    public static final String SMSG = "SMSG|";/*系统消息*/

    public static final String LAREN = "LAREN|";
    public static final String ROBL = "ROBL|"; //抢答排队列表
    public static final String ROBH = "ROBH|"; //抢答排队列表,捎句话
    public static final String MYANS = "MYANS|";
    public static final String ontime = "ONTIME|";
    public static final String LRL = "LRL|";//拉人券 列表
    public static final String larenGroupKeyPerfix = "larenGroup|";//拉人券限额
    public static final String COUPON_U = "COUPON|U|";//优惠券总包使用 -1
    public static final String CACEL = "CACEL|";//
    public static final String QTM = "QTM|";//记录时间
    public static final String QDEL = "QDEL|";
    public static final String FRESHFILTERRULE_KEY = "FRESHFILTERRULE|KEY";
    public static final String classfiy_all = "classfiy_all";//所有分类列表

    //排行榜
    //每日排行榜
    public static final String RANGE_QUES_TIMES = "rangeQuesTimes|";//总提问次数 +1
    public static final String RANGE_ANSQUES_TIMES = "rangeAnsQuesTimes|";//总答题次数 +1
    public static final String RANGE_QUES_DAY_TIMES = "rangeQuesDayTimes|";//每天提问次数 rangeQuesDayTimes|20160325|UID +1
    public static final String RANGE_ANSQUES_DAY_TIMES = "rangeAnsQuesDayTimes|";//每天答题次数 +1
    public static final String RANGE_MARK_TIMES = "rangeMarkTimes|";//今天被评价次数
    public static final String RANGE_MARK_SCORE = "rangeMarkScore|";//今天被评价分数
    public static final String RANGE_MARK_OHTER_TIMES = "rangeMarkOhterTimes|";//评价别人次数
    public static final String RANGE_MARK_OTHER_SCORE = "rangeMarkOhterScore|";//评价别人分数
    public static final String LAST_ANS_TIME = "LAST_ANS_TIME|";    //最后一次答题时间 +
    public static final String LAST_COMMOT_TIME = "LAST_COMMOT_TIME|"; //最后一次被评价时间
    public static final String LAST_QUE_TIME = "LAST_QUE_TIME|";    //最后一次提问时间 +
    public static final String LAST_COMMOT_OTHTER_TIME = "LAST_COMMOT_OTHTER_TIME|"; //最后一次评价时间

    public static final String RANGE_QUES_DAY_TIMES_WEEK = "rangeQuesDayTimesWeek|";//每周提问次数 rangeQuesDayTimes|week|UID +1
    public static final String RANGE_ANSQUES_DAY_TIMES_WEEK = "rangeAnsQuesDayTimesWeek|";//每周答题次数 +1
    public static final String RANGE_MARK_TIMES_WEEK = "rangeMarkTimesWeek|";//今周被评价次数
    public static final String RANGE_MARK_SCORE_WEEK = "rangeMarkScoreWeek|";//今周被评价分数
    public static final String RANGE_MARK_OHTER_TIMES_WEEK = "rangeMarkOhterTimesWeek|";//周评价别人次数
    public static final String RANGE_MARK_OTHER_SCORE_WEEK = "rangeMarkOhterScoreWeek|";//周评价别人分数
    public static final String LAST_ANS_TIME_WEEK = "LAST_ANS_TIME_WEEK|";    //本周最后一次答题时间 +
    public static final String LAST_COMMOT_TIME_WEEK = "LAST_COMMOT_TIME_WEEK|"; //本周最后一次被评价时间
    public static final String LAST_QUE_TIME_WEEK = "LAST_QUE_TIME_WEEK|";    //本周最后一次提问时间 +
    public static final String LAST_COMMOT_OTHTER_TIME_WEEK = "LAST_COMMOT_OTHTER_TIME_WEEK|"; //本周最后一次评价时间


    //排行榜规则
    public static String rankRulesKey = "RANKRULES";
    //排行榜规则文本
    public static String rankRulesText = "RANKRULESTEXT";
    //最强导师
    public static String tutor = "TUTOR";
    //奋斗学霸
    public static String superscholar = "SUPER";
    //究极妹子
    public static String maze = "MAZE";
    //周排行前缀
    public static String WEEKRANKING = "WEEKRANK|";
    //日排行前缀
    public static String TODAYRANKING = "TODAYRANK|";

    //周排行前缀 查我的用
    public static String WEEKUSERRANKING = "WEEKUSERRANK|";
    //日排行前缀 查我的用
    public static String TODAYUSERRANKING = "TODAYUSERRANK|";

    public static final String RANKOOQUE = "RANKOOQUE";//排行榜OO问题
    public static final String RANKOOANS = "RANKOOANS";//排行榜OO回答




    public static final String NOSEE = "NOSEE|";//用户过滤条件_ 不可见 _  ACC _ IMEI   _ IP 三种不可见
    public static final String NOSEE_IP = "IP";//用户过滤条件_ 不可见 _  ACC _ IMEI   _ IP 三种不可见
    public static final String NOSEE_IMEI = "IMEI";//用户过滤条件_ 不可见 _  ACC _ IMEI   _ IP 三种不可见
    public static final String NOSEE_ACC = "ACC";//用户过滤条件_ 不可见 _  ACC _ IMEI   _ IP 三种不可见
    public static final String ISINAPP = "ISINAPP|";
    public static final Object QRINCR = "QRINCR";

    public static final String MUST_SEND_USERS = "must_send_users";



    public static final class config_timer {
        public static final String QUESTION_CONFIG_TIMER = "QUESTION_CONFIG_TIMER_";
        public static final String FATI = "FATI";
    }

    public static final String cache_http_sys_config = "cache_http_sys_config";

    public static final class SYS_CONFIG {
        public static final String act = "act";
        /**
         * 1. All status open
         * 2. only status between 0 to 2 .
         */
        public static final String act_ALL = "1|2";
        /**
         * 必须状态在抢答和读题状态下提示
         */
        public static final String act_2 = "2";
        public static final String SFS = "sfs";
        /*********************************************************
         * 推送价格,设置,
         * @Date 2015年12月30日14:43:45
         * @author Modified by chenxx
         *********************************************************/
        public static final String pubsp = "pubsp";
        public static final String SFE = "sfe";
        public static final String delay = "delay";
        /*提问或者答题数量必须大于这个数字才不会提示*/
        public static final String pub_remind = "pub_remind";
        /*当 当前题目列表中包含 N 题目,才会提示 强制发题*/
        public static final String isst = "isst";
        //当前题目数量
        public static final String isstn = "isstn";
    }

    /**
     * 红点 - 小小的红点 - 我的问题 - 我的回答, hash 总key
     */
    public static final String QUEN = "QUEN|";
    /**
     * 红点 - 我的回答
     */
    public static final String QUENA = "QUENA|";
    /**
     * 红点 - 我的问题
     */
    public static final String QUENQ = "QUENQ|";
}
