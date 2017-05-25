package com.circle.netty.formation.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiuxy on 2016/3/22.
 */
public class UserRank extends BaseLog<UserRank> {
    private String uid;//
    @JsonIgnore
    private String name;//昵称
    @JsonIgnore
    private String uurl;//头像
    @JsonIgnore
    private int sex;//性别
    //下面是存redis的现拿
    private int qnum;//今日问题数量
    private float score;// 评价分数
    private int space;//落后上一名 N 名次
    private long rank;//当前排名
    private long orank;//上次排名
    private int coin;//3/2/1 金/银/铜 显示是啥牌的=。=,日榜 是0，周榜算的

    public Map<String, Object> view() {
        Map<String, Object> view = new HashMap<>();
        view.put("name", name == null ? "" : name);
        view.put("uurl", uurl == null ? "" : uurl);
        view.put("sex", sex);
        view.put("qnum", qnum);
        view.put("score", score);
        view.put("space", space);
        view.put("rank", rank);
        view.put("orank", orank);
        view.put("coin", coin);
        return view;
    }

    public UserRank() {
    }

    public UserRank(String uid, int qnum, float score, int coin, long orank, long rank, int space) {
        this.qnum = qnum;
        this.uid = uid;
        this.score = score;
        this.coin = coin;
        this.orank = orank;
        this.rank = rank;
        this.space = space;
    }

    public UserRank(String name, String uurl, int sex, int qnum, float score, int space, long rank, long orank, int coin) {
        this.name = name;
        this.uurl = uurl;
        this.sex = sex;
        this.qnum = qnum;
        this.score = score;
        this.space = space;
        this.rank = rank;
        this.orank = orank;
        this.coin = coin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUurl() {
        return uurl;
    }

    public void setUurl(String uurl) {
        this.uurl = uurl;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getQnum() {
        return qnum;
    }

    public void setQnum(int qnum) {
        this.qnum = qnum;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public long getOrank() {
        return orank;
    }

    public void setOrank(long orank) {
        this.orank = orank;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}
