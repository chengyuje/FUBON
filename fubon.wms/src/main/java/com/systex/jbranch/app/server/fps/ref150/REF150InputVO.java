package com.systex.jbranch.app.server.fps.ref150;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class REF150InputVO extends PagingInputVO{
	
	private String regionID;
	private String branchAreaID;
	private String branchID;
	
	private String txnDate;
	private String userID;
	private String refProd;
	
	private List EXPORT_LST;
	
	public List getEXPORT_LST() {
		return EXPORT_LST;
	}

	public void setEXPORT_LST(List eXPORT_LST) {
		EXPORT_LST = eXPORT_LST;
	}

	public String getRegionID() {
		return regionID;
	}

	public void setRegionID(String regionID) {
		this.regionID = regionID;
	}

	public String getBranchAreaID() {
		return branchAreaID;
	}

	public void setBranchAreaID(String branchAreaID) {
		this.branchAreaID = branchAreaID;
	}

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public String getTxnDate() {
		return txnDate;
	}
	
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getRefProd() {
		return refProd;
	}
	
	public void setRefProd(String refProd) {
		this.refProd = refProd;
	}
}
