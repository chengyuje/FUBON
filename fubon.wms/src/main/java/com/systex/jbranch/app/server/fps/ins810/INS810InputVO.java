package com.systex.jbranch.app.server.fps.ins810;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS810InputVO extends PagingInputVO{
	private Date birthday;
	private String reportID;
	private String CUST_ID;
	private String loginBranch;
	private String type;
	private List<String> loginAOCode;
	private String itemID;	// chkAuth用
	private List<Map<String , Object>> inOutBuyMutiPolicyList;
	
	private Boolean isCallCoverage = false; // 判斷是否要呼叫資訊源 預設 False

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getReportID() {
		return reportID;
	}

	public void setReportID(String reportID) {
		this.reportID = reportID;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getLoginBranch() {
		return loginBranch;
	}

	public List<String> getLoginAOCode() {
		return loginAOCode;
	}

	public void setLoginBranch(String loginBranch) {
		this.loginBranch = loginBranch;
	}

	public void setLoginAOCode(List<String> loginAOCode) {
		this.loginAOCode = loginAOCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public List<Map<String, Object>> getInOutBuyMutiPolicyList() {
		return inOutBuyMutiPolicyList;
	}

	public void setInOutBuyMutiPolicyList(
			List<Map<String, Object>> inOutBuyMutiPolicyList) {
		this.inOutBuyMutiPolicyList = inOutBuyMutiPolicyList;
	}

	public Boolean getIsCallCoverage() {
		return isCallCoverage;
	}

	public void setIsCallCoverage(Boolean isCallCoverage) {
		this.isCallCoverage = isCallCoverage;
	}
	

}
