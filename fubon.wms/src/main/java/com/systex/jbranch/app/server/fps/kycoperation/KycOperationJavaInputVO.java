package com.systex.jbranch.app.server.fps.kycoperation;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KycOperationJavaInputVO extends PagingInputVO {
	private String branch;
	private String userID;
	private String custID;
	private String examID;
	private String generalQuestion;
	private String generalAns;
	private String IP;
	
	
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getExamID() {
		return examID;
	}
	public void setExamID(String examID) {
		this.examID = examID;
	}
	public String getGeneralQuestion() {
		return generalQuestion;
	}
	public void setGeneralQuestion(String generalQuestion) {
		this.generalQuestion = generalQuestion;
	}
	public String getGeneralAns() {
		return generalAns;
	}
	public void setGeneralAns(String generalAns) {
		this.generalAns = generalAns;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
}
