package com.circle.netty.formation.message.model;

/**
 * Created by qiuxy on 2016/3/22.
 */
public class RankRulesStruct {
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

    /**
    * 日榜 排行榜列表 TODAYRANKING + date(20160323一天过期) +|+tutor/superscholar/maze     uid
     * 周榜 排行榜列表 WEEKRANKING + date(第几周) +|+ tutor/superscholar/maze      uid
     * 日榜 个人排行榜数据 TODAYUSERRANKING +  date(20160323一天过期)   +|+ uid   UserRank
     * 周榜 个人排行榜数据 WEEKUSERRANKING + date(第几周)  +|+ uid   UserRank
     */


    public static String rulesAnsNum="rulesAnsNum"; //回答次数 45
    public static String rulesAAppNum="rulesAAppNum";//回答评价次数
    public static String rulesAAppScore="rulesAAppScore";//回答评价分数
    public static String rulesAnsNumTime="rulesAnsNumTime";//回答到到次数时间
    public static String rulesAAppScoreTime="rulesAAppScoreTime";//回答到评价分数的时间
    public static String rulesQueNum="rulesQueNum";//提问次数
    public static String rulesQAppNum="rulesQAppNum";//提问评价次数
    public static String rulesQAppScore="rulesQAppScore";// 提问评价分数
    public static String rulesQueNumTime="rulesQueNumTime";//提问到次数时间
    public static String rulesQAppScoreTime="rulesQAppScoreTime";

}