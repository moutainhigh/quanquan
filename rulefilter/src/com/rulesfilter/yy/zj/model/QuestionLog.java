package com.rulesfilter.yy.zj.model;

import java.io.Serializable;

/**
 * @author zhoujia
 *
 * @date 2015年9月6日
 */
public class QuestionLog implements Serializable{
	
	private static final long serialVersionUID = 620064245842791448L;

	/**开始回答时间点**/
	private Long beginAnswerTime;
	
	/**结束回答时间点***/
	private Long endAnswoerTime;
	
	/**开始筛选时间点**/
	private Long beginSlectTime;
	
	/**结束筛选时间点****/
	private Long endSlectTIme;
	
	/**问题时间**/
	private String questionId;

	public Long getBeginAnswerTime() {
		return beginAnswerTime;
	}

	public void setBeginAnswerTime(Long beginAnswerTime) {
		this.beginAnswerTime = beginAnswerTime;
	}

	public Long getEndAnswoerTime() {
		return endAnswoerTime;
	}

	public void setEndAnswoerTime(Long endAnswoerTime) {
		this.endAnswoerTime = endAnswoerTime;
	}

	public Long getBeginSlectTime() {
		return beginSlectTime;
	}

	public void setBeginSlectTime(Long beginSlectTime) {
		this.beginSlectTime = beginSlectTime;
	}

	public Long getEndSlectTIme() {
		return endSlectTIme;
	}

	public void setEndSlectTIme(Long endSlectTIme) {
		this.endSlectTIme = endSlectTIme;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	
	
}
