package com.systex.jbranch.app.server.fps.insjlb.vo;

public class GetQuestionContentInputVO {

	private String custId; 	// 客戶ID(被保人ID)
	private String qid; 	// 問卷ID
	
	public GetQuestionContentInputVO() {
		super();
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	
	
}
