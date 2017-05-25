package com.circle.netty.formation.message.model;

import com.circle.core.util.Verification;
import com.circle.netty.http.JsonParams;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 优惠券
 *
 * @author Created by cxx on 15-7-17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Coupon extends BaseLog<Coupon>{
    public static final String table = "CIRCLE.COUPON";
    /**
     * 红包
     */
    public static final int type_red = 1;
    /**
     * 默认
     */
    public static final int type_def = 2;
    /**
     * 直投
     */
    public static final int type_direct = 3;
    /**
     * 兑换码
     */
    public static final int type_exchange = 4;
    /**
     * 拉人券
     */
    public static final int type_pull = 5;
    /**
     * 固定金额
     */
    public static final int ctype_fixed = 0;
    /**
     * 冲抵金额
     */
    public static final int ctype_offset = 1;
    /**
     * 有效天数
     */
    public static final int utype_days = 1;
    
    /**
     * 未使用
     */
    public static final int status_nouse = 1;
    /**
     * 已使用
     */
    public static final int status_used = 2;
    /**
     * 已过期
     */
    public static final int status_over = 3;
    /**
     * 废弃
     */
    public static final int status_abandon = 4;

    /**
     * 日期区间
     */
    public static final int utype_date = 2;
    public static final String att_disid = "disid";
    public static final String att_discount = "discount";
    public static final String att_desc = "descs";
    public static final String att_end = "end";
    public static final String att_title = "title";
    public static final String att_ctype = "ctype";
    public static final String att_percent = "percent";
    public static final String att_status = "status";

    private String disid = "";//优惠券ID
    private int type;//优惠券类型 1 红包 2 默认 3 直投 4 兑换码 5 拉人
    private int ctype;//金额 使用条件类型- 0 固定金额 1 冲抵金额
    private BigDecimal discount = new BigDecimal(0);//优惠价格
    private BigDecimal percent = new BigDecimal(0);//百分比-使用条件/冲抵比例
    private String descs = "";//说明
    private String title = "";//coupon name
    private String parent = "";//父包ID
    private int status;//状态 1 未使用 2 已经使用 3 已过期
    private int utype;//日期计算方式
    private int day;//有效期 天数
    private long begin;//开始计时-日期
    private long cdate;//领取时间
    private long endtime;//有效期结束
    private String classifys;//可以使用的分类 其他分类不能使用 , 如果有 填写 分类ID , 如果多个 使用 | 隔开
    private String error;
    private String uid;
    private String qid;
    public double sort = -1;

    public static final byte[] family = Bytes.toBytes("info");
    private long utime;//使用时间

    /**
     * 构造方法
     */
    public Coupon() {
        base=this;
        classs=Coupon.class;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static Coupon create(Result result, String disid) {
        if(disid==null) return null;
        byte[] bytes = result.getValue(family, Bytes.toBytes(disid));
        if (bytes == null) return null;
        String value = Bytes.toString(bytes);
        return create(disid, value);
    }

    public Map<String,Object> createView() {
        Map map = new HashMap();
        map.put(att_disid, disid);
        map.put(att_discount, discount);
        map.put(att_title, title==null?"提问优惠券":title);
        map.put(att_ctype, ctype);
        map.put(att_percent, percent);
        map.put(att_desc, descs==null?"":descs);
        map.put(att_end, endtime);
//        map.put(att_end, endtime);
        if(endtime<System.currentTimeMillis()&&status==status_nouse){
            map.put(att_status, status_over);
        }else {
            map.put(att_status, status);
        }
        return map;
    }

    public static Coupon create(String disid, String value) {
        if (StringUtils.isEmpty(value)) return null;
        String[] values = value.split(JsonParams.SPLIT_BACK);
        if (values.length < 11) return null;
        Coupon coupon = new Coupon();
        coupon.disid(disid);
        coupon.setStatus(Verification.getInt(0, values[0]));
        coupon.setType(Verification.getInt(0, values[1]));
        coupon.setCtype(Verification.getInt(0, values[2]));
        coupon.setUtype(Verification.getInt(0, values[3]));
        coupon.setDay(Verification.getInt(0, values[4]));
        coupon.setBegin(Verification.getLong(0, values[5]));
        coupon.setCdate(Verification.getLong(0, values[6]));
        coupon.setEndtime(Verification.getLong(0, values[7]));
        coupon.setUtime(Verification.getLong(0, values[8]));
        coupon.setDiscount(new BigDecimal(values[9]));
        coupon.setPercent(new BigDecimal(values[10]));
        if (values.length > 11)
            coupon.disid(values[11]);
        if (values.length > 12)
            coupon.setParent(values[12]);
        if (values.length > 13)
            coupon.setTitle(values[13]);
        if (values.length > 14)
            coupon.setDescs(values[14]);
        if (values.length > 15)
            coupon.setClassifys(values[15]);
        return coupon;
    }

    public String getQid() {
        return qid;
    }
    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getClassifys() {
        return classifys;
    }

    public void setClassifys(String classifys) {
        this.classifys = classifys;
    }

    public String error() {
        return error;
    }

    public void error(String error) {
        this.error = error;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Put put(User user) {
        Put put = new Put(Bytes.toBytes(user.uid()));
        put.addColumn(family, Bytes.toBytes(disid), Bytes.toBytes(createValue()));
        return put;
    }

    public String createValue() {
        return String.valueOf(status) +
                JsonParams.SPLIT + type +
                JsonParams.SPLIT + ctype +
                JsonParams.SPLIT + utype +
                JsonParams.SPLIT + day +
                JsonParams.SPLIT + begin +
                JsonParams.SPLIT + cdate +
                JsonParams.SPLIT + endtime +
                JsonParams.SPLIT + utime +
                JsonParams.SPLIT + discount +
                JsonParams.SPLIT + percent +
                JsonParams.SPLIT + disid +
                JsonParams.SPLIT + parent +
                JsonParams.SPLIT + title +
                JsonParams.SPLIT + descs +
                JsonParams.SPLIT + classifys;
    }


    public int getUtype() {
        return utype;
    }

    public void setUtype(int utype) {
        this.utype = utype;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public String disid() {
        return disid;
    }

    public void disid(String disid) {
        this.disid = disid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCtype() {
        return ctype;
    }

    public void setCtype(int ctype) {
        this.ctype = ctype;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getCdate() {
        return cdate;
    }

    public void setCdate(long cdate) {
        this.cdate = cdate;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public long getUtime() {
        return utime;
    }

    public void setUtime(long utime) {
        this.utime = utime;
    }

    public BigDecimal compute(BigDecimal cash) {

        return new BigDecimal(0);
    }


}
