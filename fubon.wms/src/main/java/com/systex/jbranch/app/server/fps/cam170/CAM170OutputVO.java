package com.systex.jbranch.app.server.fps.cam170;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM170OutputVO extends PagingOutputVO{
	
	private List campaignList;
	private List questionList;
	private List answerList;
	private List otherAnswerList;
	
	private List reportList;

	public List getReportList() {
		return reportList;
	}

	public void setReportList(List reportList) {
		this.reportList = reportList;
	}

	public List getOtherAnswerList() {
		return otherAnswerList;
	}

	public void setOtherAnswerList(List otherAnswerList) {
		this.otherAnswerList = otherAnswerList;
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

	public List getCampaignList() {
		return campaignList;
	}

	public void setCampaignList(List campaignList) {
		this.campaignList = campaignList;
	}

}
