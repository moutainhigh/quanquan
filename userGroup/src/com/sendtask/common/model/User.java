package com.sendtask.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhoujia
 *
 * @date 2015年7月29日
 */
public class User implements Serializable{
    private static final long serialVersionUID = -1375633317722913179L;
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
    private int age;
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
    private List<Integer> groupId;
    
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
			return qnum;
		case 10: //回答次数
			return anum;
		case 11: //总支出
			return totalPay;
		case 12: //提现总额
			return totalGetMoney;
		case 13: //体现次数
			return totalGetMoneyNum;
		case 14: //当前余额
			return nowMoney;
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
    
	public List<Integer> getGroupId() {
		return groupId;
	}

	public void setGroupId(List<Integer> groupId) {
		this.groupId = groupId;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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
