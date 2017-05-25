package com.rulesfilter.yy.zj.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 问题model
 * @author chenxx 2015年07月20日11:23:15
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Question extends BaseLog<Question>{
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
    @JsonIgnore
    private int color = 0;
    /**发题人手机号*/
    private String qphone = "";
    /**赏金*/
    private BigDecimal cash;
    /**优惠金额*/
    @JsonIgnore
    private BigDecimal discount;
    /**优惠券id*/
    @JsonIgnore
    private String disid = "";
    /**答题人id*/
    @JsonIgnore
    private String auid = "";
    /**答题人手机号*/
    @JsonIgnore
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
    @JsonIgnore
    private String posxy2 = "";
    /**答问题人ip*/
    @JsonIgnore
    private String ip2 = "";
    /**答题人手机标识码*/
    @JsonIgnore
    private String pkey2 = "";
    /**创建时间*/
    private long cdate;
    /**finish time*/
    private long fdate;

    /**是否是机器人发的题目**/
    private int rob;

    private int slen;
    
    public Question() {
    	base = this;
        classs = Question.class;
	}

    public int getRob() {
        return rob;
    }

    public void setRob(int rob) {
        this.rob = rob;
    }

    public long getFdate() {
        return fdate;
    }

    public void setFdate(long fdate) {
        this.fdate = fdate;
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

    public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
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
}
