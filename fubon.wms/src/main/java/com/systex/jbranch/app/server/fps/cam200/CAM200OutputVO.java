package com.systex.jbranch.app.server.fps.cam200;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM200OutputVO extends PagingOutputVO {
	
	private List beContactList;
	private List closedList;
	private List expiredList;
	private List resultList;
	private List fileList;
	
	// 問卷
	private List questionList;
	private List answerList;
	
	private List psaoSalesList;

	public List getPsaoSalesList() {
		return psaoSalesList;
	}

	public void setPsaoSalesList(List psaoSalesList) {
		this.psaoSalesList = psaoSalesList;
	}

	public List getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List answerList) {
		this.answerList = answerList;
	}

	public List getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List questionList) {
		this.questionList = questionList;
	}

	public List getFileList() {
		return fileList;
	}

	public void setFileList(List fileList) {
		this.fileList = fileList;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getBeContactList() {
		return beContactList;
	}
	
	public void setBeContactList(List beContactList) {
		this.beContactList = beContactList;
	}
	
	public List getClosedList() {
		return closedList;
	}
	
	public void setClosedList(List closedList) {
		this.closedList = closedList;
	}
	
	public List getExpiredList() {
		return expiredList;
	}
	
	public void setExpiredList(List expiredList) {
		this.expiredList = expiredList;
	}
	
}
