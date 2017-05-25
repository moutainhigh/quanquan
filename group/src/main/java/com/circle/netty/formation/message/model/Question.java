package com.circle.netty.formation.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * 问题model
 * @author chenxx 2015年07月20日11:23:15
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Question extends BaseLog<Question>{
    public static final String table = "CIRCLE.QUESTION";
    @JsonIgnore
    private String qid = "";
    /**题目标题*/
    private String title = "";
    /**描述*/
    private String context = "";
    /**图片url*/
    private String purl = "";
    /**声音url*/
    private String surl = "";
    /**题目类型*/
    private String type = "";
    /**时间线配置（ |分割）*/
    private String conf = "";
    /**权限配置（|分割）*/
    private String pow = "";
    /**发题人id*/
    private String quid = "";
    @JsonIgnore
    private int robnum;
    private int robtot;
    private int color = 0;
    /**发题人手机号*/
    private String qphone = "";
    /**赏金*/
    private BigDecimal cash;
    /**优惠金额*/
    private BigDecimal discount;
    /**优惠券id*/
    private String disid = "";
    /**答题人id*/
    private String auid = "";
    /**语音长度*/
    private long slen;
    /**答题人手机号*/
    private String aphone = "";
    /**0完成1未完成*/
    private int status;
    /**x,y坐标*/
    private String posxy = "";
    /**发问题人ip*/
    private String ip = "";
    /**手机标识码*/
    private String pkey = "";
    /**答题人x,y坐标*/
    private String posxy2 = "";
    /**答问题人ip*/
    private String ip2 = "";
    /**答题人手机标识码 1*/
    private String pkey2 = "";
    /**答题人所在城市*/
    private String city2 = "";
    /**提问人所在城市*/
    private String city = "";
    /**是否需要公司认证 0 不需要  1 需要*/
    @JsonIgnore
    private int iscompany;
    /**创建时间*/
    private long cdate;
    /**finish time*/
    private long fdate;
    /**是否隐藏描述*/
    private int hcon;
    /**是否隐藏 声音*/
    private int hsou;
    /**是否隐藏 图片*/
    private int hpic;
    /**延时次数*/
    private int delay;
    private int hide;
    /** 是否刷单 0  否 ,1 是*/
    private int sdan;
    /**评价 0 未评价 , 1 提问者评价 2 回答者评价完成 3 评价完成 4 自动评价*/
    private int app = 0;
    /**是否支付-用户判定支付状态, 0 : 完成未支付 1:支付完成*/
    private int ispay;
    /**锁定 , 被投诉后锁定题目金额,不计算到自动到账 0(默认)无异常, 1 举报异常 ,2  入账到 答题人*/
    private int lock;
    /**投诉 0 未投诉 1 答题人已经投诉 2 提问人已经投诉 3 双方均投诉*/
    @JsonIgnore
    private int iscom;
    private String msg;
    /**隐藏描述 0 不隐藏(默认) 1 隐藏*/
    private int hidedesc;
    /**隐藏声音*/
    private int hidesound;
    /**隐藏图片*/
    private int hideimg;
    /**重发次数*/
    @JsonIgnore
    private int reissue;
    /**是否已经转发了红包 0 没有 1 已经转发了*/
    @JsonIgnore
    private int red;
    /**是否可刷新到 - 0 可刷新到 ， 1 不可刷新到*/
    @JsonIgnore
    private int refurbish;

    public int rob;

    public int getRob() {
        return rob;
    }

    public void setRob(int rob) {
        this.rob = rob;
    }

    public int getRefurbish() {
        return refurbish;
    }

    public void setRefurbish(int refurbish) {
        this.refurbish = refurbish;
    }

    public int getIscom() {
        return iscom;
    }

    public void setIscom(int iscom) {
        this.iscom = iscom;
    }

    public String getCity2() {
        return city2;
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getReissue() {
        return reissue;
    }

    public void setReissue(int reissue) {
        this.reissue = reissue;
    }

    public int getHidedesc() {
        return hidedesc;
    }

    public void setHidedesc(int hidedesc) {
        this.hidedesc = hidedesc;
    }

    public int getHidesound() {
        return hidesound;
    }

    public void setHidesound(int hidesound) {
        this.hidesound = hidesound;
    }

    public int getHideimg() {
        return hideimg;
    }

    public void setHideimg(int hideimg) {
        this.hideimg = hideimg;
    }

    public int getIscompany() {
        return iscompany;
    }

    public void setIscompany(int iscompany) {
        this.iscompany = iscompany;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public long getSlen() {
        return slen;
    }

    public void setSlen(long slen) {
        this.slen = slen;
    }

    public void setFdate(long fdate) {
        this.fdate = fdate;
    }

    public long getFdate() {
        return fdate;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Question() {
        base = this;
        classs = Question.class;
    }

    public int getIspay() {
        return ispay;
    }

    public void setIspay(int ispay) {
        this.ispay = ispay;
    }

    public int getApp() {
        return app;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }

    public int getSdan() {
        return sdan;
    }

    public void setSdan(int sdan) {
        this.sdan = sdan;
    }

    public int getHcon() {
        return hcon;
    }

    public void setHcon(int hcon) {
        this.hcon = hcon;
    }

    public int getHsou() {
        return hsou;
    }

    public void setHsou(int hsou) {
        this.hsou = hsou;
    }

    public int getHpic() {
        return hpic;
    }

    public void setHpic(int hpic) {
        this.hpic = hpic;
    }

    public long getCdate() {
        return cdate;
    }

    public void setCdate(long cdate) {
        this.cdate = cdate;
    }

    public int getRobnum() {
        return robnum;
    }

    public void setRobnum(int robnum) {
        this.robnum = robnum;
    }

    public int getRobtot() {
        return robtot;
    }

    public void setRobtot(int robtot) {
        this.robtot = robtot;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosxy() {
        return posxy;
    }

    public void setPosxy(String posxy) {
        this.posxy = posxy;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPkey() {
        return pkey;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }

    public String getPosxy2() {
        return posxy2;
    }

    public void setPosxy2(String posxy2) {
        this.posxy2 = posxy2;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getPkey2() {
        return pkey2;
    }

    public void setPkey2(String pkey2) {
        this.pkey2 = pkey2;
    }

    public String qid() {
        return qid;
    }

    public void qid(String qid) {
        this.qid = qid;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getSurl() {
        return surl;
    }

    public void setSurl(String surl) {
        this.surl = surl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public String getPow() {
        return pow;
    }

    public void setPow(String pow) {
        this.pow = pow;
    }

    public String getQuid() {
        return quid;
    }

    public void setQuid(String quid) {
        this.quid = quid;
    }

    public String getQphone() {
        return qphone;
    }

    public void setQphone(String qphone) {
        this.qphone = qphone;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getDisid() {
        return disid;
    }

    public void setDisid(String disid) {
        this.disid = disid;
    }

    public String getAuid() {
        return auid;
    }

    public void setAuid(String auid) {
        this.auid = auid;
    }

    public String getAphone() {
        return aphone;
    }

    public void setAphone(String aphone) {
        this.aphone = aphone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Question{" +
                "qid='" + qid + '\'' +
                ", title='" + title + '\'' +
                ", context='" + context + '\'' +
                ", purl='" + purl + '\'' +
                ", surl='" + surl + '\'' +
                ", type='" + type + '\'' +
                ", conf='" + conf + '\'' +
                ", pow='" + pow + '\'' +
                ", quid='" + quid + '\'' +
                '}';
    }
}
