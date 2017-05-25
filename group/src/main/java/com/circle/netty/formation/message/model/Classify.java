package com.circle.netty.formation.message.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 问题分类, 用户认证分类
 * @author Created by cxx on 7/21/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Classify {
    public static final String att_cid = "cid";
    public static final String att_name = "name";
    public static final String att_rgb= "rgb";
    public static final String att_status = "hide";
    public static final String att_sort = "sort";

    private String cid;//分类ID
    private String name;//分类名称
    private int rgb;//颜色
    /*是否显示在外层 0 1*/
    private int status;//是否在外层
    private int sort;
    /**该类每次测评题数*/
    private int qnum = 5;
    /**该类每次通过题数*/
    private int accnum = 4;
    /**该类每天测评次数*/
    private int ednum = 5;
    private int hide;
    public Classify() {
    }

    public Classify(String cid, String name, int rgb, int status) {
        this.cid = cid;
        this.name = name;
        this.rgb = rgb;
        this.status = status;
    }

    public Map createMyVirw() {
        Map<String,Object> hash = new HashMap<>();
        hash.put(att_cid,cid);
        hash.put(att_name,name);
        hash.put(att_rgb,rgb);
        return hash;
    }

    public Map<String,Object> createVirw(){
        Map<String,Object> hash = new HashMap<>();
        hash.put(att_cid,cid);
        hash.put(att_name,name);
        hash.put(att_rgb,rgb);
        hash.put(att_status,status);
        hash.put(att_sort,sort);
        return hash;
    }

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }

    public int getQnum() {
        return qnum;
    }

    public void setQnum(int qnum) {
        this.qnum = qnum;
    }

    public int getAccnum() {
        return accnum;
    }

    public void setAccnum(int accnum) {
        this.accnum = accnum;
    }

    public int getEdnum() {
        return ednum;
    }

    public void setEdnum(int ednum) {
        this.ednum = ednum;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRgb() {
        return rgb;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
