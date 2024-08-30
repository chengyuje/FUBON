package com.systex.jbranch.app.server.fps.kyc510;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC510OutputVO extends PagingOutputVO{
	private List questionList;
	private List qstQustionLst;

	public List getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List questionList) {
		this.questionList = questionList;
	}

	public List getQstQustionLst() {
		return qstQustionLst;
	}

	public void setQstQustionLst(List qstQustionLst) {
		this.qstQustionLst = qstQustionLst;
	}
	
	
}
