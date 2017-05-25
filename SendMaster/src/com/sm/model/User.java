package com.sm.model;


/** 
 * @author  zhoujia 
 * @date 创建时间：2015年7月16日 下午2:06:24   
 */
public class User {
	
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
    private String cdate;
    /**
     * 禁言时间
     */
    private long sdate;
    
    /**
     * lastsigntime 最后登录时间
     */
    private long lstime;
    
    @Override
    public String toString() {
    	return uid + "," + name + "," + sex + "," + age;
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

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public long getSdate() {
        return sdate;
    }

    public void setSdate(long sdate) {
        this.sdate = sdate;
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
	
	

}
