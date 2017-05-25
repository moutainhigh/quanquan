package com.sendtask.usergroup.zhoujia.model;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.types.PDecimal;
import org.apache.phoenix.schema.types.PInteger;

/**
 * @author zhoujia
 *
 * @date 2015年7月29日
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class User implements Serializable{
    private static final long serialVersionUID = -1375633317722913179L;
    
    public static String elc_table_user = "ELC_TAB_REALUSER";
    public static String elc_index_user = "faq";
    /**
     * 用户唯一ID
     */
    private String uid;
    /**
     * 密码
     */
    private String pass;
    /**
     * 昵称
     */
    private String name;
    /**
     * 性别 , 0 男 , 1 女
     */
    private int sex;
    /**
     * 头像
     */
    private String uurl;
    /**
     * 公司
     */
    private String company;
    private int iscom;//是否通过公司认证 0 否 1 是
    private int isava;//是否头像认证 0 否 1 是
    /**
     * 年龄
     */
	private long age;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 个人说明
     */
    private String remark;

    /**
     * 手机
     */
    private String mobile;
    /**
     * 创建时间
     */
    private Long cdate;
    /**
     * 禁言时间
     */
    private long sdate;
    /**
     * lastsigntime 最后登录时间
     */
    private long lstime;
    
    /**
     * 回答评价总分
     */
    private float ascore;
    private int anum;//回答次数

    /**
     * 提问评价总分
     */
    private float qscore;
    private int qnum;//提问次数
    private String weixin; //微信
    private String qq;  //qq号码
    private String email; // 邮箱
    
    
    /**渠道来源**/
    private String channel;
    
    /**系统选择**/
    private String system;
    
    /**总支出**/
    private Double totalPay;
    
    /**总收入**/
    private Double totalIncome;
    
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
    
    /**
     * 分组id
     */
    //private List<Integer> groupId;
	private String groupId = "";
	/** 固定分组 **/
	private String staticGroupId = "";
	/** 拉人分组 ***/
	private String larenGroupId = "";
	
	private String imei;
	
	private String ip;
	
	private String rname;

	@JsonIgnore
	private Integer robot;


    
	public Object getAttr(int key){
    	switch (key) {
		case 1: //渠道
			return channel;
		case 2: //系统
			return system;
		case 3: //性别
			return sex;
		case 4: //城市
			return city;
		case 5: //回答评价
			return ascore;
		case 6: //提问评价
			return qscore;
		case 7: //注册天数
			long day = System.currentTimeMillis()-cdate;
			return day/(24*60*60*1000);
		case 8: //注册时间
			return cdate;

		case 9: //发题次数

			return Redis.shard.get("rangeQuesTimes|"+this.uid);
		case 10: //回答次数
			return Redis.shard.get("rangeAnsQuesTimes|"+this.uid);
		case 11: //总支出
			return totalPay;
		case 12: //提现总额
			CHbase bean = CHbase.bean();
			Get getGet = new Get(Bytes.toBytes(this.uid));
			try {
				Result result = bean.get("CIRCLE.PACKET", getGet);
				byte[] getValue = result.getValue(Bytes.toBytes("0"), PDecimal.INSTANCE.toBytes("get".toUpperCase()));
				BigDecimal getMoney = (BigDecimal)PDecimal.INSTANCE.toObject(getValue==null?"0".getBytes():getValue);
				return getMoney.doubleValue();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		case 13: //体现次数
			CHbase bean1 = CHbase.bean();
			Get getTimes = new Get(Bytes.toBytes(this.uid));
			try {
				Result result = bean1.get("CIRCLE.PACKET", getTimes);
				byte[] getValue = result.getValue(Bytes.toBytes("0"), PInteger.INSTANCE.toBytes("gettimes".toUpperCase()));
				//Integer gettimes = (Integer)PInteger.INSTANCE.toObject(getValue==null?"0".getBytes():getValue);
				return (Integer)PInteger.INSTANCE.toObject(getValue==null?"0".getBytes():getValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		case 14: //当前余额
			CHbase bean2 = CHbase.bean();
			Get getCash = new Get(Bytes.toBytes(this.uid));
			try {
				Result result = bean2.get("CIRCLE.PACKET", getCash);
				byte[] getValue = result.getValue(Bytes.toBytes("0"), PDecimal.INSTANCE.toBytes("cash".toUpperCase()));
				BigDecimal getMoney = (BigDecimal)PDecimal.INSTANCE.toObject(getValue==null?"0".getBytes():getValue);
				return getMoney.doubleValue();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		case 15: //未登录天数
			long nologin = System.currentTimeMillis()-this.lstime;
			return nologin/(24*60*60*1000);
		case 16: //未回答天数
			long noAns = System.currentTimeMillis() - this.lastAnsTime;
			return noAns/(24*60*60*1000);
		case 17: //未提问天数
			long noQue = System.currentTimeMillis() - this.lastQuesTime;
			return noQue/(24*60*60*1000);

		default:
			return null;
			//break;
		}
    }
    private String card;
    private String device;
    
	/** 证件url */
	private String curl;
	/** 工牌url */
	private String purl;
	
	private String auth;
	private Long comdate;
	private Long avadate;
	private float att;
	private float deep;
	private float speed;
	private String cid;
	private long incr;


	/** 被投诉次数 */
	@JsonIgnore
	private long complanin;

	/** 支付宝账号 */
	private String aliacc;
	/** 支付宝绑定手机 */
	private String alimob;
	/** 体现密码 */
	private String drawpwd;

	/** 提醒设置 价格区间 */
	private String price;
	/** 提醒设置 推送 */
	private String pub;
	/** 提醒设置 星期 */
	private String day;
	/** 提醒设置 时间段 */
	private String time;
	/** 0 正常用户 , 1 白名单用户 , 3 黑名单用户 ,意为:是否能进入本系统 5-禁抢 7-不可见 */
	private int access;
	@JsonIgnore
	private String laren;
	@JsonIgnore
	private int laren_num;
	@JsonIgnore
	private String dname = "";
	/** 分组id */

	/** 推送声音开关 0 开(默认) 1 关 */
	private int sound;
	/** 坐标 */
	private String posxy;
	/** 所在城市 */
	private String rcity;

	private String nn;

	/**
	 * 累计收入
	 */
	private BigDecimal income;

	public Integer getRobot() {
		return robot;
	}

	public void setRobot(Integer robot) {
		this.robot = robot;
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


	public String getCid() {
		return cid;
	}


	public void setCid(String cid) {
		this.cid = cid;
	}


	public long getIncr() {
		return incr;
	}


	public void setIncr(long incr) {
		this.incr = incr;
	}


	public long getComplanin() {
		return complanin;
	}


	public void setComplanin(long complanin) {
		this.complanin = complanin;
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


	public int getAccess() {
		return access;
	}


	public void setAccess(int access) {
		this.access = access;
	}


	public String getLaren() {
		return laren;
	}


	public void setLaren(String laren) {
		this.laren = laren;
	}


	public int getLaren_num() {
		return laren_num;
	}


	public void setLaren_num(int laren_num) {
		this.laren_num = laren_num;
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


	public String getNn() {
		return nn;
	}


	public void setNn(String nn) {
		this.nn = nn;
	}


	public BigDecimal getIncome() {
		return income;
	}


	public void setIncome(BigDecimal income) {
		this.income = income;
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


	public String getAuth() {
		return auth;
	}


	public void setAuth(String auth) {
		this.auth = auth;
	}


	public String getCurl() {
		return curl;
	}


	public void setCurl(String curl) {
		this.curl = curl;
	}


	public String getPurl() {
		return purl;
	}


	public void setPurl(String purl) {
		this.purl = purl;
	}


	public String getDevice() {
		return device;
	}


	public void setDevice(String device) {
		this.device = device;
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


	public String getIp() {
		return ip;
	}



	public void setIp(String ip) {
		this.ip = ip;
	}



	public String getGroupId() {
		return groupId;
	}



	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	

	public String getImei() {
		return imei;
	}



	public void setImei(String imei) {
		this.imei = imei;
	}



	public String getStaticGroupId() {
		return staticGroupId;
	}



	public void setStaticGroupId(String staticGroupId) {
		this.staticGroupId = staticGroupId;
	}



	public String getLarenGroupId() {
		return larenGroupId;
	}



	public void setLarenGroupId(String larenGroupId) {
		this.larenGroupId = larenGroupId;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
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



}
