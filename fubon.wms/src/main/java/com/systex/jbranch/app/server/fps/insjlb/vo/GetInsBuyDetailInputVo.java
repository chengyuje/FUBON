package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

public class GetInsBuyDetailInputVo {
	private String custId;
	private String callType;
	private List lstFamily;//家庭戶id清單
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCallType() {
		return callType;
	}
	public void setCallType(String callType) {
		this.callType = callType;
	}
	public List getLstFamily() {
		return lstFamily;
	}
	public void setLstFamily(List lstFamily) {
		this.lstFamily = lstFamily;
	}
	
}
