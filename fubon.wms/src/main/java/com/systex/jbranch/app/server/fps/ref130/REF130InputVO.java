package com.systex.jbranch.app.server.fps.ref130;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class REF130InputVO extends PagingInputVO{
	
	private String regionID;
	private String branchAreaID;
	private String branchID;
	
	private String txnDate;
	private String salesPerson;
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
	
	public String getSalesPerson() {
		return salesPerson;
	}
	
	public void setSalesPerson(String salesPerson) {
		this.salesPerson = salesPerson;
	}
	
	public String getRefProd() {
		return refProd;
	}
	
	public void setRefProd(String refProd) {
		this.refProd = refProd;
	}
}
