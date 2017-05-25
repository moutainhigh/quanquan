package com.circle.netty.formation.message.model;

import com.circle.core.util.Verification;
import com.circle.netty.http.JsonParams;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang.StringUtils;

/**
 * 问题时间轴参数
 *
 * @author Created by cxx on 8/4/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class QueTimeAxis {
    private int read = 600;//读题时间(秒)
    private int robcant = 60;//抢单,且不可取消(秒)
    private int robcan = 240;//抢单, 可取消时间(秒)
    private int robCount = 300;//抢单总时间(秒)
    private int remain = 1;//提醒延时时间(分钟)
    private int delay = 10;//延时时间(分钟)
    private int apprise = 12;//评价时间(小时)
    private int screen = 12;//筛选时间(分钟)--权限
    private int answer = 30;//回答时间(分钟)--权限
    private int delaytimes = 1;//申请延时次数
    private int delayrtime = 3;//申请延时时候被拒绝后可申请次数
    private int delayrSpace = 5;//延时提醒间隔时间

    private int cont;//总时间时间(分钟)--权限

    @Override
    public String toString() {
        return read +
                JsonParams.SPLIT + robcant +
                JsonParams.SPLIT + robcan +
                JsonParams.SPLIT + robCount +
                JsonParams.SPLIT + remain +
                JsonParams.SPLIT + delay +
                JsonParams.SPLIT + apprise +
                JsonParams.SPLIT + screen +
                JsonParams.SPLIT + answer +
                JsonParams.SPLIT + getCont() +
                JsonParams.SPLIT + delaytimes +
                JsonParams.SPLIT + delayrtime +
                JsonParams.SPLIT + delayrSpace;
    }

    public QueTimeAxis create(String conf) {
        if (StringUtils.isEmpty(conf)) return this;
        String[] split = conf.split(JsonParams.SPLIT_BACK);
        if (split.length < 10) return this;
        this.setRead(Verification.getInt(read, split[0]));
        this.setRobcant(Verification.getInt(robcant, split[1]));
        this.setRobcan(Verification.getInt(robcan, split[2]));
        this.setRobCount(Verification.getInt(robCount, split[3]));
        this.setRemain(Verification.getInt(remain, split[4]));
        this.setDelay(Verification.getInt(delay, split[5]));
        this.setApprise(Verification.getInt(apprise, split[6]));
        this.setScreen(Verification.getInt(screen, split[7]));
        this.setAnswer(Verification.getInt(answer, split[8]));
        this.setCont(Verification.getInt(getCont(), split[9]));
        if (split.length > 10){
            this.setDelaytimes(Verification.getInt(delaytimes, split[10]));
        }
        if (split.length > 11){
            this.setDelayrtime(Verification.getInt(delayrtime, split[11]));
        }
        if (split.length > 12){
            this.setDelayrSpace(Verification.getInt(delayrSpace, split[12]));
        }
        return this;
    }

    public int getCont() {
        return cont = read + robCount + remain * 60 + delay * 60 + apprise * 60 + screen * 60 + answer * 60;
    }

    public int getDelaytimes() {
        return delaytimes;
    }

    public void setDelaytimes(int delaytimes) {
        this.delaytimes = delaytimes;
    }

    public int getDelayrtime() {
        return delayrtime;
    }

    public void setDelayrtime(int delayrtime) {
        this.delayrtime = delayrtime;
    }

    public int getDelayrSpace() {
        return delayrSpace;
    }

    public void setDelayrSpace(int delayrSpace) {
        this.delayrSpace = delayrSpace;
    }

    public void setCont(int cont) {
        this.cont = cont;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getRobcant() {
        return robcant;
    }

    public void setRobcant(int robcant) {
        this.robcant = robcant;
    }

    public int getRobcan() {
        return robcan;
    }

    public void setRobcan(int robcan) {
        this.robcan = robcan;
    }

    public int getRobCount() {
        return robCount;
    }

    public void setRobCount(int robCount) {
        this.robCount = robCount;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public int getApprise() {
        return apprise;
    }

    public void setApprise(int apprise) {
        this.apprise = apprise;
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }


}
