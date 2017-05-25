package com.circle.netty.formation.message.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 排行榜栏目+规则
 *
 * @author Created by qiuxy on 2016/3/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RankRules extends BaseLog<RankRules> {
    private String id;
    private String title;
    private int showNum;
    private int showMy;
    private int showWeek;
    private int showToday;
    private String medalGold;
    private String medalSilver;
    private String medalCopper;
    private float equalSilver;
    private float equalCopper;
    private int rankGold;
    private int rankSilver;
    private int rankCopper;
    private String remarks;
    private int sort;


    private int rulesAnsNum; //回答次数 45
    private int rulesAAppNum;//回答评价次数
    private int rulesAAppScore;//回答评价分数
    private int rulesAnsNumTime;//回答到到次数时间
    private int rulesAAppScoreTime;//回答到评价分数的时间
    private int rulesQueNum;//提问次数
    private int rulesQAppNum;//提问评价次数
    private int rulesQAppScore;// 提问评价分数
    private int rulesQueNumTime;//提问到次数时间
    private int rulesQAppScoreTime;

    public RankRules() {
        base = this;
        classs = RankRules.class;
    }

    public RankRules(String id, String title, int showNum, int showMy, int showWeek, int showToday, String medalGold, String medalSilver, String medalCopper, float equalSilver, float equalCopper, int rankGold, int rankSilver, int rankCopper, String remarks, int sort, int rulesAnsNum, int rulesAAppNum, int rulesAAppScore, int rulesAnsNumTime, int rulesAAppScoreTime, int rulesQueNum, int rulesQAppNum, int rulesQAppScore, int rulesQueNumTime, int rulesQAppScoreTime) {
        base = this;
        classs = RankRules.class;
        this.id = id;
        this.title = title;
        this.showNum = showNum;
        this.showMy = showMy;
        this.showWeek = showWeek;
        this.showToday = showToday;
        this.medalGold = medalGold;
        this.medalSilver = medalSilver;
        this.medalCopper = medalCopper;
        this.equalSilver = equalSilver;
        this.equalCopper = equalCopper;
        this.rankGold = rankGold;
        this.rankSilver = rankSilver;
        this.rankCopper = rankCopper;
        this.remarks = remarks;
        this.sort = sort;
        this.rulesAnsNum = rulesAnsNum;
        this.rulesAAppNum = rulesAAppNum;
        this.rulesAAppScore = rulesAAppScore;
        this.rulesAnsNumTime = rulesAnsNumTime;
        this.rulesAAppScoreTime = rulesAAppScoreTime;
        this.rulesQueNum = rulesQueNum;
        this.rulesQAppNum = rulesQAppNum;
        this.rulesQAppScore = rulesQAppScore;
        this.rulesQueNumTime = rulesQueNumTime;
        this.rulesQAppScoreTime = rulesQAppScoreTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getShowNum() {
        return showNum;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public int getShowMy() {
        return showMy;
    }

    public void setShowMy(int showMy) {
        this.showMy = showMy;
    }

    public int getShowWeek() {
        return showWeek;
    }

    public void setShowWeek(int showWeek) {
        this.showWeek = showWeek;
    }

    public int getShowToday() {
        return showToday;
    }

    public void setShowToday(int showToday) {
        this.showToday = showToday;
    }

    public String getMedalGold() {
        return medalGold;
    }

    public void setMedalGold(String medalGold) {
        this.medalGold = medalGold;
    }

    public String getMedalSilver() {
        return medalSilver;
    }

    public void setMedalSilver(String medalSilver) {
        this.medalSilver = medalSilver;
    }

    public String getMedalCopper() {
        return medalCopper;
    }

    public void setMedalCopper(String medalCopper) {
        this.medalCopper = medalCopper;
    }

    public float getEqualSilver() {
        return equalSilver;
    }

    public void setEqualSilver(float equalSilver) {
        this.equalSilver = equalSilver;
    }

    public float getEqualCopper() {
        return equalCopper;
    }

    public void setEqualCopper(float equalCopper) {
        this.equalCopper = equalCopper;
    }

    public int getRankGold() {
        return rankGold;
    }

    public void setRankGold(int rankGold) {
        this.rankGold = rankGold;
    }

    public int getRankSilver() {
        return rankSilver;
    }

    public void setRankSilver(int rankSilver) {
        this.rankSilver = rankSilver;
    }

    public int getRankCopper() {
        return rankCopper;
    }

    public void setRankCopper(int rankCopper) {
        this.rankCopper = rankCopper;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getRulesAnsNum() {
        return rulesAnsNum;
    }

    public void setRulesAnsNum(int rulesAnsNum) {
        this.rulesAnsNum = rulesAnsNum;
    }

    public int getRulesAAppNum() {
        return rulesAAppNum;
    }

    public void setRulesAAppNum(int rulesAAppNum) {
        this.rulesAAppNum = rulesAAppNum;
    }

    public int getRulesAAppScore() {
        return rulesAAppScore;
    }

    public void setRulesAAppScore(int rulesAAppScore) {
        this.rulesAAppScore = rulesAAppScore;
    }

    public int getRulesAnsNumTime() {
        return rulesAnsNumTime;
    }

    public void setRulesAnsNumTime(int rulesAnsNumTime) {
        this.rulesAnsNumTime = rulesAnsNumTime;
    }

    public int getRulesAAppScoreTime() {
        return rulesAAppScoreTime;
    }

    public void setRulesAAppScoreTime(int rulesAAppScoreTime) {
        this.rulesAAppScoreTime = rulesAAppScoreTime;
    }

    public int getRulesQueNum() {
        return rulesQueNum;
    }

    public void setRulesQueNum(int rulesQueNum) {
        this.rulesQueNum = rulesQueNum;
    }

    public int getRulesQAppNum() {
        return rulesQAppNum;
    }

    public void setRulesQAppNum(int rulesQAppNum) {
        this.rulesQAppNum = rulesQAppNum;
    }

    public int getRulesQAppScore() {
        return rulesQAppScore;
    }

    public void setRulesQAppScore(int rulesQAppScore) {
        this.rulesQAppScore = rulesQAppScore;
    }

    public int getRulesQueNumTime() {
        return rulesQueNumTime;
    }

    public void setRulesQueNumTime(int rulesQueNumTime) {
        this.rulesQueNumTime = rulesQueNumTime;
    }

    public int getRulesQAppScoreTime() {
        return rulesQAppScoreTime;
    }

    public void setRulesQAppScoreTime(int rulesQAppScoreTime) {
        this.rulesQAppScoreTime = rulesQAppScoreTime;
    }
}
