package com.circle.netty.formation.message.model;

import java.math.BigDecimal;

/**
 * 拉人获得现金奖励 - >
 *
 * @author Created by cxx15 on 2016/1/4.
 */
public class LCash extends BaseLog<LCash> {
    public static final String table = "CIRCLE.LCASH";
    private BigDecimal cash;
    private String parentId;
    private long cdate;
    private int status;
    private String uid;
    private String fuid;
    private String qid;//题目ID
    private long qfdate;//题目完成时间
    private long vdate;//审核完成时间
    private int vstatus;//审核状态

    public LCash() {
        base = this;
        classs = LCash.class;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public long getCdate() {
        return cdate;
    }

    public void setCdate(long cdate) {
        this.cdate = cdate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public long getQfdate() {
        return qfdate;
    }

    public void setQfdate(long qfdate) {
        this.qfdate = qfdate;
    }

    public long getVdate() {
        return vdate;
    }

    public void setVdate(long vdate) {
        this.vdate = vdate;
    }

    public int getVstatus() {
        return vstatus;
    }

    public void setVstatus(int vstatus) {
        this.vstatus = vstatus;
    }
}
