package com.systex.jbranch.app.server.fps.kyc211;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC211OutputVO extends PagingOutputVO{	
	
	private List questionList;

	public List getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List questionList) {
		this.questionList = questionList;
	}
	
	
}
