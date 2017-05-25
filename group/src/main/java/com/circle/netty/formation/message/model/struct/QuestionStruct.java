package com.circle.netty.formation.message.model.struct;

import com.circle.core.util.Verification;
import com.circle.netty.formation.message.model.Question;
import com.circle.netty.formation.message.model.User;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 问题, 数据结构类
 *
 * @author chenxx 2015年07月20日11:32:27
 */
public class QuestionStruct {
    public static final int app_no = 0;
    public static final int app_que = 1;
    public static final int app_ans = 2;
    public static final int app_all = 3;
    public static String hide = "hide";
    public static String app = "app";
    public static final String red = "red";
    public static byte[] red_byte = Bytes.toBytes(red.toUpperCase());
    public static byte[] app_byte = Bytes.toBytes(app.toUpperCase());
    public static final byte[] family_time = Bytes.toBytes("3");
    public static final byte[] family_rob = Bytes.toBytes("4");

    public static final String table = "CIRCLE.QUESTION";
    public static final String table_el = "art_que";
    /*题目类型*/
    public static final String type = "type";
    public static final byte[] type_byte = Bytes.toBytes(type.toUpperCase());
    /*赏金*/
    public static final String cash = "cash";
    public static final byte[] cash_byte = Bytes.toBytes(cash.toUpperCase());
    /*标题*/
    public static final String title = "title";
    public static final byte[] title_byte = Bytes.toBytes(title.toUpperCase());
    /*描述*/
    public static final String context = "context";
    public static final byte[] context_byte = Bytes.toBytes(context.toUpperCase());
    /* 时间 */
    public static final String time = "time";
    public static final byte[] time_byte = Bytes.toBytes(time.toUpperCase());
    /*0完成1未完成*/
    public static final String status = "status";
    public static final byte[] status_byte = Bytes.toBytes(status.toUpperCase());
    /*发题人id*/
    public static final String quid = "quid";
    public static final byte[] quid_byte = Bytes.toBytes(quid.toUpperCase());
    /*发题人手机号*/
    public static final String qphone = "qphone";
    public static final byte[] qphone_byte = Bytes.toBytes(qphone.toUpperCase());
    /*图片url*/
    public static final String purl = "purl";
    public static final byte[] purl_byte = Bytes.toBytes(purl.toUpperCase());
    /*声音url*/
    public static final String surl = "surl";
    public static final byte[] surl_byte = Bytes.toBytes(surl.toUpperCase());
    /*时间线配置（ |分割）*/
    public static final String conf = "conf";
    public static final byte[] conf_byte = Bytes.toBytes(conf.toUpperCase());
    /*权限配置（|分割）*/
    public static final String pow = "pow";
    public static final byte[] pow_byte = Bytes.toBytes(pow.toUpperCase());
    /*优惠金额*/
    public static final String discount = "discount";
    public static final byte[] discount_byte = Bytes.toBytes(discount.toUpperCase());
    /*优惠券id*/
    public static final String disid = "disid";
    public static final byte[] disid_byte = Bytes.toBytes(disid.toUpperCase());
    /*答题人id*/
    public static final String auid = "auid";
    public static final byte[] auid_byte = Bytes.toBytes(auid.toUpperCase());
    /*答题人手机号*/
    public static final String aphone = "aphone";
    public static final byte[] aphone_byte = Bytes.toBytes(aphone.toUpperCase());
    /*x,y坐标*/
    public static final String posxy = "posxy";
    public static final byte[] posxy_byte = Bytes.toBytes(posxy.toUpperCase());
    /*发问题人ip*/
    public static final String ip = "ip";
    public static final byte[] ip_byte = Bytes.toBytes(ip.toUpperCase());
    /*手机标识码*/
    public static final String pkey = "pkey";
    public static final byte[] pkey_byte = Bytes.toBytes(pkey.toUpperCase());
    /*答题人x,y坐标*/
    public static final String posxy2 = "posxy2";
    public static final byte[] posxy2_byte = Bytes.toBytes(posxy2.toUpperCase());
    /*答问题人ip*/
    public static final String ip2 = "ip2";
    public static final byte[] ip2_byte = Bytes.toBytes(ip2.toUpperCase());
    /*答题人手机标识码*/
    public static final String pkey2 = "pkey2";
    public static final byte[] pkey2_byte = Bytes.toBytes(pkey2.toUpperCase());
    private static final String qpurl = "qpurl";//发题人头像
    //private static final byte[] qpurl_byte = Bytes.toBytes(qpurl);//发题人头像
    private static final String qmark = "qmark";//发题人认证
    //private static final byte[] qmark_byte = Bytes.toBytes(qmark);//发题人认证
    public static final byte[] family = Bytes.toBytes("0");
    public static final String cdate = "cdate";
    public static final byte[] cdate_byte = Bytes.toBytes(cdate);
    public static final String isver = "isver";
    /**finish time*/
    public static final String fdate = "fdate";
    public static final byte[] fdate_byte = Bytes.toBytes(fdate.toUpperCase());
    /**读题阶段*/
    public static final int stu_read = 0;
    /**抢答阶段*/
    public static final int stu_rob = 1;
    /**筛选阶段*/
    public static final int stu_chose = 2;
    /**回答阶段*/
    public static final int stu_answer = 3;
    /**延时阶段*/
    public static final int stu_delay = 4;
    /**完成题目-但是未评价*/
    public static final int stu_finish = 5;
    /**已经评价完成*/
    public static final int stu_success = 6;
    /**取消订单*/
    public static final int stu_cancel = 7;
    public static final int stu_do_cancel = 8;
    public static final int stu_delay_remind = 9;
    public static final String ispay = "ispay";
    public static final byte[] ispay_byte = Bytes.toBytes(ispay);
    public static final int stu_add_rob = 10;
    public static final String qid = "qid";
    public static final String lock = "lock";
    public static byte[] lock_byte = Bytes.toBytes(lock.toUpperCase());
    public static final String robnum = "robnum";
    public static byte[] robnum_byte = Bytes.toBytes(robnum.toUpperCase());
    public static final String appsdate = "appsdate";
    public static final byte[] appsdate_byte = Bytes.toBytes(appsdate.toUpperCase());

    public static Put createPut(Question que) {
        Put put = new Put(Bytes.toBytes(que.qid()));
        if (que.getType() == null)
            return null;
        put.addColumn(family, type_byte, Bytes.toBytes(que.getType()));
        if (que.getTitle() == null)
            return null;
        put.addColumn(family, title_byte, Bytes.toBytes(que.getTitle()));
        if (que.getContext() != null)
            put.addColumn(family, context_byte, Bytes.toBytes(que.getContext()));
        if (que.getPurl() != null)
            put.addColumn(family, purl_byte, Bytes.toBytes(que.getPurl()));
        if (que.getSurl() != null)
            put.addColumn(family, surl_byte, Bytes.toBytes(que.getSurl()));
        put.addColumn(family, cash_byte, Bytes.toBytes(que.getCash()));
        if (que.getDisid() != null)
            put.addColumn(family, disid_byte, Bytes.toBytes(que.getDisid()));
        put.addColumn(family, discount_byte, Bytes.toBytes(que.getDiscount()));
        if (que.getConf() != null)
            put.addColumn(family, conf_byte, Bytes.toBytes(que.getConf()));
        if (que.getPow() != null)
            put.addColumn(family, pow_byte, Bytes.toBytes(que.getPow()));
        if (que.getQuid() != null)
            put.addColumn(family, quid_byte, Bytes.toBytes(que.getQuid()));
        if (que.getQphone() != null)
            put.addColumn(family, qphone_byte, Bytes.toBytes(que.getQphone()));
        if (que.getAuid() != null)
            put.addColumn(family, auid_byte, Bytes.toBytes(que.getAuid()));
        if (que.getAphone() != null)
            put.addColumn(family, aphone_byte, Bytes.toBytes(que.getAphone()));
        if (que.getIp() != null)
            put.addColumn(family, ip_byte, Bytes.toBytes(que.getIp()));
        if (que.getIp2() != null)
            put.add(family, ip2_byte, Bytes.toBytes(que.getIp2()));
        if (que.getPosxy() != null)
            put.addColumn(family, posxy_byte, Bytes.toBytes(que.getPosxy()));
        if (que.getPosxy2() != null)
            put.addColumn(family, posxy2_byte, Bytes.toBytes(que.getPosxy2()));
        put.addColumn(family, status_byte, Bytes.toBytes(que.getStatus()));
        put.addColumn(family, cdate_byte, Bytes.toBytes(que.getCdate()));//天剑创建时间
        if (que.getPkey() != null)
            put.addColumn(family, pkey_byte, Bytes.toBytes(que.getPkey()));
        if (que.getPkey2() != null)
            put.addColumn(family, pkey2_byte, Bytes.toBytes(que.getPkey2()));
        return put;
    }

    public static Question create(Map<String, String> hash) {
        Question que = new Question();
        que.setTitle(hash.get(title));
        que.setContext(hash.get(context));
        que.setPurl(hash.get(purl));
        que.setSurl(hash.get(surl));
        que.setType(hash.get(type));
        que.setConf(hash.get(conf));
        que.setPow(hash.get(pow));
        que.setQuid(hash.get(quid));
        que.setQphone(hash.get(qphone));
        que.setCash(new BigDecimal(Verification.getDoule(0, hash.get(cash))));
        que.setPurl(hash.get(qpurl));
        //que.set(hash.get(qmark));
        return que;
    }
}
