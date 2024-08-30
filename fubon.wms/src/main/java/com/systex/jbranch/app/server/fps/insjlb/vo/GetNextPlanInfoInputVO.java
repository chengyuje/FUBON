package com.systex.jbranch.app.server.fps.insjlb.vo;

public class GetNextPlanInfoInputVO {

	private String custId; 	// 客戶ID(被保人ID)
	private String qid; 	// 問卷ID
	private String seq; 	// 規劃序號
	
	public GetNextPlanInfoInputVO() {
		super();
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	
}
