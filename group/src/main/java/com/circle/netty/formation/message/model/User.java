package com.circle.netty.formation.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author Created by clicoy on 15-5-10.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class User extends BaseLog<User> implements Serializable {
    private static final long serialVersionUID = -1375633317722913179L;
    /** 用户唯一ID */
    @JsonIgnore
    private String uid;
    /**物理机标识*/
    private String imei;
    /**用户IP*/
    private String ip;
    /** 密码 */
    private String pass;
    /**昵称 */
    private String name;
    /**真实姓名*/
    private String rname;
    /**身份证号码*/
    private String card;
    /** 性别 , 0 男 , 1 女 */
    private int sex;
    private String device;
    /** 头像 */
    private String uurl;
    /** 证件url */
    private String curl;
    /** 工牌url */
    private String purl;
    /** 公司 */
    private String company;
    /**authentication types use "|" add more .*/
    private String auth;
    /**是否通过公司认证 0 否 1 是 2 驳回 */
    private int iscom;
    /** 公司验证申请时间 */
    private Long comdate;
    /**是否头像认证 0 否 1 是 2 驳回 */
    private int isava;
    /** 头像验证申请时间 */
    private Long avadate;
    /** 年龄 */
    private long age;
    /** 所在城市 */
    private String city;
    /** 个人说明  */
    private String remark;
    /**手机*/
    private String mobile;
    /**创建时间*/
    private Long cdate;
    /**禁言时间 */
    private long sdate;
    /** lastsigntime 最后登录时间 */
    private long lstime;
    /**回答评价总分*/
    private float ascore;
    private float att;
    /***/
    private float deep;
    private float speed;
    private String cid;
    /** 随机分段使用 - 主要用户阀体推送 */
    private long incr;
    /**回答次数 */
    private int anum;
    /**提问评价总分 */
    private float qscore;
    private int qnum;//提问次数
    private String weixin; //微信
    private String qq;  //qq号码
    private String email; // 邮箱
    /**系统选择**/
    private String system = "";
    /**渠道来源**/
    private String channel="";
    /**总支出**/
    private Double totalPay;
    /**总收入**/
    private Double totalIncome;
    /**被投诉次数*/
    @JsonIgnore
    private long complanin;

    /**支付宝账号*/
    private String aliacc;
    /**支付宝绑定手机*/
    private String alimob;
    /**体现密码*/
    private String drawpwd;

    /**提现总额**/
    private Double totalGetMoney;

    /**提现次数**/
    private Integer totalGetMoneyNum;

    /***当前余额**/
    private Double nowMoney;

    /**最后回答时间**/
    private Long lastAnsTime;

    /**最后提问时间**/
    private Long lastQuesTime;
    /**提醒设置 价格区间*/
    private String price;
    /**提醒设置 推送*/
    private String pub;
    /**提醒设置 星期*/
    private String day;
    /**提醒设置 时间段*/
    private String time;
    /** 0 正常用户 , 1 白名单用户 , 3 黑名单用户 ,意为:是否能进入本系统   5-禁抢     7-不可见*/
    private int access;
    @JsonIgnore
    private String laren;
    @JsonIgnore
    private int laren_num;
    @JsonIgnore
    private String dname = "";
    /** 分组id */

    private String groupId = "";
    /**固定分组**/
    private String staticGroupId = "";
    /**拉人分组***/
    private String larenGroupId = "";
    /**推送声音开关 0 开(默认)  1 关*/
    private int sound;
    /**坐标*/
    private String posxy;
    /**所在城市*/
    private String rcity;

    private int robot;

    public User() {
        base = this;
        classs = User.class;
    }

    public int getRobot() {
        return robot;
    }

    public void setRobot(int robot) {
        this.robot = robot;
    }

    public String getPosxy() {
        return posxy;
    }

    public void setPosxy(String posxy) {
        this.posxy = posxy;
    }

    public String getRcity() {
        return rcity;
    }

    public void setRcity(String rcity) {
        this.rcity = rcity;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getLaren_num() {
        return laren_num;
    }

    public String getLarenGroupId() {
        return larenGroupId;
    }

    public void setLarenGroupId(String larenGroupId) {
        this.larenGroupId = larenGroupId;
    }

    public String getStaticGroupId() {
        return staticGroupId;
    }

    public void setStaticGroupId(String staticGroupId) {
        this.staticGroupId = staticGroupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setLaren_num(int laren_num) {
        this.laren_num = laren_num;
    }

    public String getLaren() {
        return laren;
    }

    public void setLaren(String laren) {
        this.laren = laren;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAliacc() {
        return aliacc;
    }

    public void setAliacc(String aliacc) {
        this.aliacc = aliacc;
    }

    public String getAlimob() {
        return alimob;
    }

    public void setAlimob(String alimob) {
        this.alimob = alimob;
    }

    public String getDrawpwd() {
        return drawpwd;
    }

    public void setDrawpwd(String drawpwd) {
        this.drawpwd = drawpwd;
    }

    public long getComplanin() {
        return complanin;
    }

    public void setComplanin(long complanin) {
        this.complanin = complanin;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getIncr() {
        return incr;
    }

    public void setIncr(long incr) {
        this.incr = incr;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public float getAtt() {
        return att;
    }

    public void setAtt(float att) {
        this.att = att;
    }

    public float getDeep() {
        return deep;
    }

    public void setDeep(float deep) {
        this.deep = deep;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public Double getTotalPay() {
		return totalPay;
	}

	public void setTotalPay(Double totalPay) {
		this.totalPay = totalPay;
	}

	public Double getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(Double totalIncome) {
		this.totalIncome = totalIncome;
	}

	public Double getTotalGetMoney() {
		return totalGetMoney;
	}

	public void setTotalGetMoney(Double totalGetMoney) {
		this.totalGetMoney = totalGetMoney;
	}

	public Integer getTotalGetMoneyNum() {
		return totalGetMoneyNum;
	}

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        this.curl = curl;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public void setTotalGetMoneyNum(Integer totalGetMoneyNum) {
		this.totalGetMoneyNum = totalGetMoneyNum;
	}

	public Double getNowMoney() {
		return nowMoney;
	}

	public void setNowMoney(Double nowMoney) {
		this.nowMoney = nowMoney;
	}

	public Long getLastAnsTime() {
		return lastAnsTime;
	}

	public void setLastAnsTime(Long lastAnsTime) {
		this.lastAnsTime = lastAnsTime;
	}

	public Long getLastQuesTime() {
		return lastQuesTime;
	}

	public void setLastQuesTime(Long lastQuesTime) {
		this.lastQuesTime = lastQuesTime;
	}

	public int getAnum() {
        return anum;
    }

    public void setAnum(int anum) {
        this.anum = anum;
    }

    public int getQnum() {
        return qnum;
    }

    public void setQnum(int qnum) {
        this.qnum = qnum;
    }

    public int getIscom() {
        return iscom;
    }

    public void setIscom(int iscom) {
        this.iscom = iscom;
    }

    public int getIsava() {
        return isava;
    }

    public void setIsava(int isava) {
        this.isava = isava;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getLstime() {
        return lstime;
    }

    public void setLstime(long lstime) {
        this.lstime = lstime;
    }

    public float getAscore() {
        return ascore;
    }

    public void setAscore(float ascore) {
        this.ascore = ascore;
    }

    public float getQscore() {
        return qscore;
    }

    public void setQscore(float qscore) {
        this.qscore = qscore;
    }

    public String uid() {
        return uid;
    }

    public void uid(String uid) {
        this.uid = uid;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUurl() {
        return uurl;
    }

    public void setUurl(String uurl) {
        this.uurl = uurl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getCdate() {
        return cdate;
    }

    public void setCdate(Long cdate) {
        this.cdate = cdate;
    }

    public long getSdate() {
        return sdate;
    }

    public void setSdate(long sdate) {
        this.sdate = sdate;
    }

    public Long getAvadate() {
        return avadate;
    }

    public void setAvadate(Long avadate) {
        this.avadate = avadate;
    }

    public Long getComdate() {
        return comdate;
    }

    public void setComdate(Long comdate) {
        this.comdate = comdate;
    }
}
