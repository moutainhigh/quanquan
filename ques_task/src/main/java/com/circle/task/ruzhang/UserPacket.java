package com.circle.task.ruzhang;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 钱包
 * @author chenxx 2015年07月21日14:32:53
 */
public class UserPacket extends BaseLog<UserPacket>{
    public static final String table = "CIRCLE.PACKET";
    private String uid;//用户ID
    private BigDecimal totle;//累计收入
    private BigDecimal cash;//余额
    private BigDecimal get;//已经提现
    private BigDecimal precash;//即将入帐
    @JsonIgnore
    private int gettimes;//提现次数

    public UserPacket() {
        base=this;
        classs = UserPacket.class;
    }

    public Object createView() {
        Map view = new HashMap();
        view.put("totle",totle.doubleValue());
        view.put("cash",cash.doubleValue());
        view.put("get",get.doubleValue());
        view.put("precash",precash.doubleValue());
        return view;
    }

    public int getGettimes() {
        return gettimes;
    }

    public void setGettimes(int gettimes) {
        this.gettimes = gettimes;
    }

    public String uid() {
        return uid;
    }

    public void uid(String uid) {
        this.uid = uid;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public BigDecimal getGet() {
        return get;
    }

    public void setGet(BigDecimal get) {
        this.get = get;
    }

    public BigDecimal getPrecash() {
        return precash;
    }

    public void setPrecash(BigDecimal precash) {
        this.precash = precash;
    }

    public BigDecimal getTotle() {
        return totle;
    }

    public void setTotle(BigDecimal totle) {
        this.totle = totle;
    }
}
