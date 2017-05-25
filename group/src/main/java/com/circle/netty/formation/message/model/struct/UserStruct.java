package com.circle.netty.formation.message.model.struct;

import com.circle.core.util.Verification;
import com.circle.netty.formation.message.model.User;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.types.PInteger;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户数据转换
 *
 * @author Created by clicoy on 15-6-11.
 */
public class UserStruct {
    /**
     * 用户表
     */
    public static final String table = "CIRCLE.USER";
    public static final String table_EL = "ELC_TAB_REALUSER";
    public static final byte[] table_byte = Bytes.toBytes(table);
    public static final byte[] family_info = Bytes.toBytes("0");
    public static final String pass = "pass";
    public static final byte[] pass_byte = Bytes.toBytes(pass.toUpperCase());
    public static final String mobile_mob = "mob";
    public static final String mobile = "mobile";
    public static final byte[] mobile_byte = Bytes.toBytes(mobile.toUpperCase());
    public static final String age = "age";
    public static final byte[] age_byte = Bytes.toBytes(age.toUpperCase());
    public static final String USR = "usr";
    public static final String uid = "uid";
    public static final String uurl = "uurl";//头像
    public static final byte[] uurl_byte = Bytes.toBytes(uurl.toUpperCase());
    public static final String curl = "curl";//证件
    public static final byte[] curl_byte = Bytes.toBytes(curl.toUpperCase());
    public static final String purl = "purl";//工牌
    public static final byte[] purl_byte = Bytes.toBytes(purl.toUpperCase());
    public static final String card = "card";//性别
    public static final byte[] card_byte = Bytes.toBytes(card.toUpperCase());
    public static final String sex = "sex";//性别
    public static final byte[] sex_byte = Bytes.toBytes(sex.toUpperCase());
    public static final String nn = "nn";//昵称
    public static final String rname = "name";//真实姓名
    public static final byte[] rname_byte = Bytes.toBytes(rname.toUpperCase());
    public static final String cdate = "cdate";
    public static final byte[] cdate_byte = Bytes.toBytes(cdate.toUpperCase());
    public static final String sdate = "sdate";
    public static final byte[] sdate_byte = Bytes.toBytes(sdate.toUpperCase());
    public static final String qscore = "qscore";
    public static final byte[] qscore_byte = Bytes.toBytes(qscore.toUpperCase());
    public static final String qnum = "qnum";
    public static final byte[] qnum_byte = Bytes.toBytes(qnum.toUpperCase());
    public static final String iscom = "iscom";
    public static final byte[] iscom_byte = Bytes.toBytes(iscom.toUpperCase());
    public static final String comdate = "comdate";
    public static final byte[] comdate_byte = Bytes.toBytes(comdate.toUpperCase());
    public static final String isava = "isava";
    public static final byte[] isava_byte = Bytes.toBytes(isava.toUpperCase());
    public static final String avadate = "avadate";
    public static final byte[] avadate_byte = Bytes.toBytes(avadate.toUpperCase());
    public static final String ascore = "ascore";
    public static final byte[] ascore_byte = Bytes.toBytes(ascore.toUpperCase());
    public static final String anum = "anum";
    public static final byte[] anum_byte = Bytes.toBytes(anum.toUpperCase());
    public static final String qq = "qq";
    public static final byte[] qq_byte = Bytes.toBytes(qq.toUpperCase());
    public static final String weixin = "weixin";
    public static final byte[] weixin_byte = Bytes.toBytes(weixin.toUpperCase());
    public static final String passwd = "pwd";
    public static final String remark = "remark";
    public static final byte[] remark_byte = Bytes.toBytes(remark.toUpperCase());
    public static final String company = "company";
    public static final byte[] company_byte = Bytes.toBytes(company.toUpperCase());
    public static final String city = "city";
    public static final byte[] city_byte = Bytes.toBytes(city.toUpperCase());
    public static final String email = "email";
    public static final byte[] email_byte = Bytes.toBytes(email.toUpperCase());
    public static final String channel = "channel";
    public static final String system = "system";
    public static final String larenGroupId = "larenGroupId";
    public static final byte[] system_byte = Bytes.toBytes(system.toUpperCase());
    public static final String deep = "deep";
    public static final String att = "att";
    public static final String speed = "speed";
    public static final byte[] deep_byte = Bytes.toBytes(deep.toUpperCase());
    public static final byte[] att_byte = Bytes.toBytes(att.toUpperCase());
    public static final byte[] speed_byte = Bytes.toBytes(speed.toUpperCase());
    public static final String attct = "attct";
    public static final String cid = "cid";
    public static final String incr = "incr";
    public static final byte[] channel_byte = Bytes.toBytes(channel.toUpperCase());
    public static final byte[] cid_byte = Bytes.toBytes(cid.toUpperCase());
    public static final byte[] incr_byte = Bytes.toBytes(incr.toUpperCase());
    public static final String name = "name";
    public static final String complanin = "complanin";
    public static final String aliacc = "aliacc";
    public static final byte[] aliacc_byte = Bytes.toBytes(aliacc.toUpperCase());
    public static final String alimob = "alimob";
    public static final byte[] alimob_byte = Bytes.toBytes(alimob.toUpperCase());
    public static final String drawpwd = "drawpwd";
    public static final byte[] drawpwd_byte = Bytes.toBytes(drawpwd.toUpperCase());
    public static final String auth = "auth";
    public static final byte[] auth_byte = Bytes.toBytes(auth.toUpperCase());
    public static final String uploadfiles = "uploadfiles";
    public static final String access = "access";
    public static final String laren = "laren";
    public static final byte[] laren_byte = Bytes.toBytes(laren.toUpperCase());
    public static final String device = "device";
    public static final byte[] device_byte = Bytes.toBytes(device.toUpperCase());
    public static final String ldate = "ldate";
    public static final String laren_num = "laren_num";
    public static final String staticGroupId = "staticGroupId";
    public static final String groupId = "groupId";
    public static final String DEF_PUB_DAY = "0|1|2|3|4|5|6";//默认推送日期
    public static final String day = "day";
    public static final String dname = "dname";/*支付宝收款人*/
    public static final byte[] dname_byte = Bytes.toBytes(dname.toUpperCase());
    public static final String passwd_all = "passwd";
    public static final String posxy = "posxy";
    public static final byte[] posxy_byte = Bytes.toBytes(posxy.toUpperCase());
    public static final String rcity = "rcity";
    public static final byte[] rcity_byte = Bytes.toBytes(rcity.toUpperCase());
    public static final String hasreg = "hasreg";
    public static final byte[] hasreg_byte = Bytes.toBytes(hasreg.toUpperCase());
    public static final String imei = "imei";
    public static final byte[] imei_byte = Bytes.toBytes(imei.toUpperCase());

    /*分辨率*/
    public static final String resolution = "resolution";
    public static final byte[] resolution_byte = Bytes.toBytes(resolution.toUpperCase());
    /*运营商*/
    public static final String operator = "operator";
    public static final byte[] operator_byte = Bytes.toBytes(operator.toUpperCase());
    /*未知运营商*/
    public static final int operator_unknow = 0;
    /*电信*/
    public static final int operator_telecom = 1;
    //中国移动
    public static final int operator_CMCC = 2;
    //中国联通
    public static final int operator_China_Unicom = 3;
    //其他
    public static final int operator_other = 4;
    /*用户手机联网方式*/
    public static final String network = "network";
    public static final byte[] network_byte = Bytes.toBytes(network.toUpperCase());
    public static final int network_unknown = 0;
    public static final int network_2g = 1;
    public static final int network_3g = 2;
    public static final int network_4g = 3;
    public static final int network_5g = 4;
    public static final int network_wifi = 5;
    /* 智能手机型号 */
    public static final String smartphones = "smartphones";
    public static final byte[] smartphones_byte = Bytes.toBytes(smartphones.toUpperCase());
    public static final String version = "version";
    public static final byte[] version_byte = Bytes.toBytes(version.toUpperCase());
    public static final String login = "login";//The status of sign
    public static final String ip = "ip";
    public static final byte[] ip_byte = Bytes.toBytes(ip.toUpperCase());
    public static final String sound = "sound";
    public static final String pub = "pub";
    public static final String usr = "usr";
    public static final String randomSort = "random_sort";

    public static User create(Map<String, String> hash) {
        if (hash == null) return null;
        User user = new User();
        user.uid(hash.get(uid));
        user.setMobile(hash.get(mobile));
        user.setPass(hash.get(pass));
        user.setName(hash.get(nn));
        user.setQnum(Verification.getInt(0, hash.get(qnum)));
        user.setQscore(Verification.getFloat(0, hash.get(qscore)));
        user.setAge(Verification.getLong(0, hash.get(age)));
        user.setAnum(Verification.getInt(0, hash.get(anum)));
        user.setAuth(hash.get(auth));
        user.setDevice(hash.get(device));
        user.setAscore(Verification.getFloat(0, hash.get(ascore)));
        user.setIsava(Verification.getInt(0, hash.get(isava)));
        user.setIscom(Verification.getInt(0, hash.get(iscom)));
        user.setSex(Verification.getInt(0, hash.get(sex)));
        user.setCdate(Verification.numberReg(hash.get(cdate)) ? Long.valueOf(hash.get(cdate)) : 0L);
        user.setSdate(Verification.numberReg(hash.get(sdate)) ? Long.valueOf(hash.get(sdate)) : 0L);
        user.setCity(hash.get(city) == null ? "" : hash.get(city));
        user.setCompany(hash.get(company) == null ? "" : hash.get(company));
        user.setRemark(hash.get(remark) == null ? "" : hash.get(remark));
        user.setWeixin(hash.get(weixin) == null ? "" : hash.get(weixin));
        user.setQq(hash.get(qq) == null ? "" : hash.get(qq));
        user.setEmail(hash.get(email) == null ? "" : hash.get(email));
        user.setUurl(hash.get(uurl) == null ? "" : hash.get(uurl));
        user.setCurl(hash.get(curl) == null ? "" : hash.get(curl));
        user.setPurl(hash.get(purl) == null ? "" : hash.get(purl));
        user.setCard(hash.get(card) == null ? "" : hash.get(card));
        user.setRname(hash.get(rname) == null ? "" : hash.get(rname));
        user.setDeep(Verification.getFloat(0, hash.get(deep)));
        user.setAtt(Verification.getFloat(0, hash.get(att)));
        user.setSpeed(Verification.getFloat(0, hash.get(speed)));
        user.setCid(hash.get(cid));
        user.setAccess(Verification.getInt(0, hash.get(access)));
        user.setLaren(hash.get(laren));
        user.setDrawpwd(hash.get(drawpwd));
        user.setChannel(hash.get(channel));
        user.setSystem(hash.get(system));
        user.setAliacc(hash.get(aliacc));
        user.setAlimob(hash.get(alimob));
        user.setStaticGroupId(hash.get(staticGroupId) == null ? "" : hash.get(staticGroupId));
        user.setLarenGroupId(hash.get(larenGroupId) == null ? "" : hash.get(larenGroupId));
        user.setGroupId(hash.get(groupId) == null ? "" : hash.get(groupId));
        user.setLaren_num(Verification.getInt(0, hash.get(laren_num)));
        user.setDname(hash.get(dname) == null ? "" : hash.get(dname));
        user.setRcity(hash.get(rcity) == null ? "" : hash.get(rcity));
        user.setPosxy(hash.get(posxy) == null ? "" : hash.get(posxy));
        user.setImei(hash.get(imei) == null ? "" : hash.get(imei));
        user.setIp(hash.get(ip));
        return user;
    }

    public static final byte[] nn_byte = Bytes.toBytes(name.toUpperCase());

    public static Map<String, String> createMap(User user) {
        Map<String, String> hash = new HashMap<>();
        hash.put(uid, user.uid());
        hash.put(mobile, user.getMobile());
        hash.put(pass, user.getPass());
        hash.put(nn, user.getName());
        hash.put(qnum, String.valueOf(user.getQnum()));
        hash.put(qscore, String.valueOf(user.getQscore()));
        hash.put(age, String.valueOf(user.getAge()));
        hash.put(anum, String.valueOf(user.getAnum()));
        hash.put(ascore, String.valueOf(user.getAscore()));
        hash.put(isava, String.valueOf(user.getIsava()));
        hash.put(iscom, String.valueOf(user.getIscom()));
        hash.put(sex, String.valueOf(user.getSex()));
        hash.put(cdate, String.valueOf(user.getCdate()));
        hash.put(sdate, String.valueOf(user.getSdate()));
        hash.put(city, user.getCity());
        hash.put(company, user.getCompany());
        hash.put(remark, user.getRemark());
        hash.put(weixin, user.getWeixin());
        hash.put(qq, user.getQq());
        hash.put(email, user.getEmail());
        hash.put(uurl, user.getUurl());
        hash.put(curl, user.getCurl());
        hash.put(purl, user.getPurl());
        hash.put(card, user.getCard());
        hash.put(rname, user.getRname());
        hash.put(deep, String.valueOf(user.getDeep()));
        hash.put(att, String.valueOf(user.getAtt()));
        hash.put(speed, String.valueOf(user.getSpeed()));
        hash.put(laren, user.getLaren());
        return hash;
    }

    public static User create(Result result) {
        if (result == null || result.isEmpty()) return null;
        User user = new User();
        byte[] bytes = result.getRow();
        user.uid(Bytes.toString(bytes));
        bytes = result.getValue(family_info, mobile_byte);
        if (bytes != null)
            user.setMobile(Bytes.toString(bytes));
        bytes = result.getValue(family_info, nn_byte);
        if (bytes != null)
            user.setName(Bytes.toString(bytes));
        bytes = result.getValue(family_info, uurl_byte);
        if (bytes != null)
            user.setUurl(Bytes.toString(bytes));
        bytes = result.getValue(family_info, sex_byte);
        if (bytes != null)
            user.setSex(Bytes.toInt(bytes));
        bytes = result.getValue(family_info, age_byte);
        if (bytes != null)
            user.setAge(Bytes.toInt(bytes));
        bytes = result.getValue(family_info, pass_byte);
        if (bytes != null)
            user.setPass(Bytes.toString(bytes));
        bytes = result.getValue(family_info, qq_byte);
        if (bytes != null)
            user.setQq(Bytes.toString(bytes));
        bytes = result.getValue(family_info, city_byte);
        if (bytes != null)
            user.setCity(Bytes.toString(bytes));
        bytes = result.getValue(family_info, company_byte);
        if (bytes != null)
            user.setCompany(Bytes.toString(bytes));
        bytes = result.getValue(family_info, weixin_byte);
        if (bytes != null)
            user.setWeixin(Bytes.toString(bytes));
        bytes = result.getValue(family_info, email_byte);
        if (bytes != null)
            user.setEmail(Bytes.toString(bytes));
        bytes = result.getValue(family_info, remark_byte);
        if (bytes != null)
            user.setRemark(Bytes.toString(bytes));
        bytes = result.getValue(family_info, system_byte);
        if (bytes != null)
            user.setSystem(Bytes.toString(bytes));
        bytes = result.getValue(family_info, qnum_byte);
        if (bytes != null)
            user.setQnum(Bytes.toInt(bytes));
        bytes = result.getValue(family_info, qscore_byte);
        if (bytes != null)
            user.setQscore(Bytes.toFloat(bytes));
        bytes = result.getValue(family_info, anum_byte);
        if (bytes != null)
            user.setAnum(Bytes.toInt(bytes));
        bytes = result.getValue(family_info, ascore_byte);
        if (bytes != null)
            user.setAscore(Bytes.toFloat(bytes));
        bytes = result.getValue(family_info, isava_byte);
        if (bytes != null)
            user.setIsava(Bytes.toInt(bytes));
        bytes = result.getValue(family_info, iscom_byte);
        if (bytes != null)
            user.setIscom(Bytes.toInt(bytes));
        bytes = result.getValue(family_info, cdate_byte);
        if (bytes != null)
            user.setCdate(Bytes.toLong(bytes));
        bytes = result.getValue(family_info, sdate_byte);
        if (bytes != null)
            user.setSdate(Bytes.toLong(bytes));
        bytes = result.getValue(family_info, curl_byte);
        if (bytes != null)
            user.setCurl(Bytes.toString(bytes));
        bytes = result.getValue(family_info, purl_byte);
        if (bytes != null)
            user.setPurl(Bytes.toString(bytes));
        bytes = result.getValue(family_info, card_byte);
        if (bytes != null)
            user.setCard(Bytes.toString(bytes));
        bytes = result.getValue(family_info, rname_byte);
        if (bytes != null)
            user.setRname(Bytes.toString(bytes));
        bytes = result.getValue(family_info, deep_byte);
        if (bytes != null)
            user.setRname(Bytes.toString(bytes));
        bytes = result.getValue(family_info, att_byte);
        if (bytes != null)
            user.setRname(Bytes.toString(bytes));
        bytes = result.getValue(family_info, speed_byte);
        if (bytes != null)
            user.setRname(Bytes.toString(bytes));
        return user;
    }
}
