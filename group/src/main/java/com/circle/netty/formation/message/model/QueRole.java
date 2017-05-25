package com.circle.netty.formation.message.model;

import com.circle.core.util.Verification;
import com.circle.netty.http.JsonParams;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 发布问题权限表<br>
 * 金额开始|金额结束|报名人数|推送人数|可刷新人数|高亮|选择时间|回答时间|公司认证|特权文字
 *
 * @author Created by cxx on 15-7-27.
 */
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
public class QueRole {
    public static final String AMTRANGE_START = "amtrange_start";// [开始]  -> 金额范围|
    public static final String AMTRANGE_END = "amtrange_end";//[结束] - > 金额范围
    public static final String REGNUM = "regnum";// 报名人数
    public static final String PUBNUMBER = "pubnumber";//推送人数
    public static final String REFRESH = "refresh";// 可刷新人数
    public static final String HIGHILIGHT = "highilight";// 是否高亮  0 不高亮 1 高亮
    public static final String CHOSETIME = "chosetime";// 选择时间(分钟)
    public static final String ANSTIME = "anstime";// 答题时间 (分钟)
    public static final String ISCOMPAN = "iscompan";// 限公司认证 0 不限制 1 限制
    public static final String DESC = "desc";// 特区文字

    private String key;
    private Integer amtrange_start;// [开始]  -> 金额范围|
    private Integer amtrange_end;//[结束] - > 金额范围
    private Integer regnum;// 报名人数
    private Integer pubnumber;//推送人数
    private Integer refresh;// 可刷新人数
    private Integer highilight;// 是否高亮  0 不高亮 1 高亮
    private Integer chosetime;// 选择时间(分钟)
    private Integer anstime;// 答题时间 (分钟)
    private Integer iscompan;// 限公司认证 0 不限制 1 限制
    private String desc;//特区文字

    public static QueRole create(String s) {
        String[] strs = s.split(JsonParams.SPLIT_BACK);
        if(strs.length==10){
            QueRole queRole = new QueRole();
            queRole.setAmtrange_start(Verification.getInt(0, strs[0]));
            queRole.setAmtrange_end(Verification.getInt(0, strs[1]));
            queRole.setRegnum(Verification.getInt(0, strs[2]));
            queRole.setPubnumber(Verification.getInt(0, strs[3]));
            queRole.setRefresh(Verification.getInt(0, strs[4]));
            queRole.setHighilight(Verification.getInt(0, strs[5]));
            queRole.setChosetime(Verification.getInt(0, strs[6]));
            queRole.setAnstime(Verification.getInt(0, strs[7]));
            queRole.setIscompan(Verification.getInt(0, strs[8]));
            queRole.setDesc(strs[9]);
            return queRole;
        }
        return null;
    }
    @Override
    public String toString() {
        return amtrange_start + JsonParams.SPLIT + amtrange_end + JsonParams.SPLIT + regnum
                + JsonParams.SPLIT + pubnumber + JsonParams.SPLIT + refresh + JsonParams.SPLIT
                + highilight + JsonParams.SPLIT + chosetime + JsonParams.SPLIT + anstime +
                JsonParams.SPLIT + iscompan + JsonParams.SPLIT + desc;
    }
    public String toString1() {
        return amtrange_start + JsonParams.SPLIT + amtrange_end + JsonParams.SPLIT + regnum
                + JsonParams.SPLIT + pubnumber + JsonParams.SPLIT + refresh + JsonParams.SPLIT
                + highilight + JsonParams.SPLIT + chosetime + JsonParams.SPLIT + anstime +
                JsonParams.SPLIT + iscompan + JsonParams.SPLIT + " ";
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getAmtrange_start() {
        return amtrange_start;
    }

    public void setAmtrange_start(Integer amtrange_start) {
        this.amtrange_start = amtrange_start;
    }

    public Integer getAmtrange_end() {
        return amtrange_end;
    }

    public void setAmtrange_end(Integer amtrange_end) {
        this.amtrange_end = amtrange_end;
    }

    public Integer getRegnum() {
        return regnum;
    }

    public void setRegnum(Integer regnum) {
        this.regnum = regnum;
    }

    public Integer getPubnumber() {
        return pubnumber;
    }

    public void setPubnumber(Integer pubnumber) {
        this.pubnumber = pubnumber;
    }

    public Integer getRefresh() {
        return refresh;
    }

    public void setRefresh(Integer refresh) {
        this.refresh = refresh;
    }

    public Integer getHighilight() {
        return highilight;
    }

    public void setHighilight(Integer highilight) {
        this.highilight = highilight;
    }

    public Integer getChosetime() {
        return chosetime;
    }

    public void setChosetime(Integer chosetime) {
        this.chosetime = chosetime;
    }

    public Integer getAnstime() {
        return anstime;
    }

    public void setAnstime(Integer anstime) {
        this.anstime = anstime;
    }

    public Integer getIscompan() {
        return iscompan;
    }

    public void setIscompan(Integer iscompan) {
        this.iscompan = iscompan;
    }


}
