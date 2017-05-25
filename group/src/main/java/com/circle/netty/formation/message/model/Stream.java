package com.circle.netty.formation.message.model;

import java.math.BigDecimal;

/**
 * 用户流水明细
 * @author Created by fomky on 15-7-17.
 */
public class Stream extends BaseLog<Stream>{
    public static final int TY_1_profit =1;//收益
    public static final int TY_2_tip =2;//消费
    public static final int TY_3_draw =3;//提现
    public static final int TY_4_back =4;//退款
    public static final int TY_5_goincome =5;//违规收入
    public static final int PY_3_ALIPAY =3;
    public static final int PY_4_LAREN =5;
    public static final int PY_2_USER =2;
    public static final int PY_1_SYS =1;
    public static final int STATUS_SUNBMIT =1;
    public static final int STATUS_PAY =2;
    public static final int STATUS_SUCCESS =3;
    public static final int STATUS_FAIL =4;
    public static String table = "CIRCLE.STREAM";

    /** 流水ID */
    private String id;
    private String uid;

    /** 类型 1收益 , 2消费 , 3提现 , 4退款 , 5违规收入 1转入/2消费/3提现/4收益*/
    private Integer stype;
    /** 交易总金额 */
    private BigDecimal money;
    /** 优惠券ID */
    private String disid;
    /** 实际消费金额 */
    private BigDecimal pay;
    /**支付类型 – 1 系统转账 2 用户转账 3 支付宝充值*/
    private Integer ptype;
    /** 支付账号 */
    private String  account;
    /**阿里支付流水号*/


    private String  alipay_no;
    /**流水状态,提交1/审核(付款)2/完成3/失败4 */
    private Integer status;
    /** desc 说明 */
    private String qid;
    /***/
    private String descs;
    /** 提交时间 */
    private long sdate;
    /** 审核时间 */
    private long vdate;
    /** 完成时间 */
    private long fdate;

    public Stream() {
        base=this;
        classs=Stream.class;
    }

    public String id() {
        return id;
    }

    public void id(String id) {
        this.id = id;
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

    public String getDescs() {
        return descs;
    }

    public void setDescs(String desc) {
        this.descs = desc;
    }

    public Integer getStype() {
        return stype;
    }

    public void setStype(Integer stype) {
        this.stype = stype;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getDisid() {
        return disid;
    }

    public void setDisid(String disid) {
        this.disid = disid;
    }

    public BigDecimal getPay() {
        return pay;
    }

    public String getAlipay_no() {
        return alipay_no;
    }

    public void setAlipay_no(String alipay_no) {
        this.alipay_no = alipay_no;
    }

    public void setPay(BigDecimal pay) {
        this.pay = pay;
    }

    public Integer getPtype() {
        return ptype;
    }

    public void setPtype(Integer ptype) {
        this.ptype = ptype;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getSdate() {
        return sdate;
    }

    public void setSdate(long sdate) {
        this.sdate = sdate;
    }

    public long getVdate() {
        return vdate;
    }

    public void setVdate(long vdate) {
        this.vdate = vdate;
    }

    public long getFdate() {
        return fdate;
    }

    public void setFdate(long fdate) {
        this.fdate = fdate;
    }
}
