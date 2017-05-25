package com.circle.netty.formation.message.model;

/**
 * 评价答题人
 * @author Created by cxx on 15-7-16.
 */
public class Acheck extends BaseLog<Acheck>{
    private String id;
    /* 答题人ID */
    private String uid;
    /* 问题ID */
    private String qid;
    /*解决问题的深度*/
    private double deep;
    /* 解决问题的态度 */
    private double att;
    /* 响应问题的速度 */
    private double speed;
    /* 总评分 */
    private double score;
    /* 评价内容 */
    private String context;
    /*提问人*/
    private String quid;
    /* 评价时间 */
    private long time;
    public Acheck(){
        base=this;
        classs=Acheck.class;
    }
    public String getQid() {
        return qid;
    }

    public String getQuid() {
        return quid;
    }

    public void setQuid(String quid) {
        this.quid = quid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public double getDeep() {
        return deep;
    }

    public void setDeep(double deep) {
        this.deep = deep;
    }

    public double getAtt() {
        return att;
    }

    public void setAtt(double att) {
        this.att = att;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String id() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void id(String rowkey) {
        this.id = rowkey;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
