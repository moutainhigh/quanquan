package com.circle.netty.formation.message.model;

/**
 * 评价提问人 ,
 * @author cxx on 2015年07月20日11:18:29
 */
public class Qcheck extends BaseLog<Qcheck>{
    private String id;
    /* 评价人ID */
    private String auid;
    /* 评价问题 */
    private String qid;
    /* 评价总分 */
    private double score;
    /* 评价内容 */
    private String context;
    /* 评价时间 */
    private long time;
    /*提问人*/
    private String uid;

    public Qcheck(){
        base=this;
        classs=Qcheck.class;
    }

    public String id() {
        return id;
    }

    public void id(String id) {
        this.id = id;
    }

    public String getAuid() {
        return auid;
    }

    public void setAuid(String auid) {
        this.auid = auid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }


    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
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
