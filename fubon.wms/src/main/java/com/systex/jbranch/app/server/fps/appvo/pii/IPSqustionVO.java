package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class IPSqustionVO implements Serializable {
	
	private String qusNo;			//題目編號
	private String question;		//題目內容
	private String type;			//題目類型(S:單筆M:多筆O: 表格F: 單選+輸入欄位)
	public String getQusNo() {
		return qusNo;
	}
	public void setQusNo(String qusNo) {
		this.qusNo = qusNo;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	
}
