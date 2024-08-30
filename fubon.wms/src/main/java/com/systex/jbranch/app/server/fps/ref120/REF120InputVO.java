package com.systex.jbranch.app.server.fps.ref120;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class REF120InputVO extends PagingInputVO{
	
	private String regionID;
	private String branchAreaID;
	private String branchID;
	private String seq;
	private String salesPerson;
	private String salesName;
	private String salesRole;
	private String userID;
	private String userName;
	private String userRole;
	private String custID;
	private String custName;
	private Date sDate;
	private Date eDate;
	private String refProd;
	private String refInsContRslt;
	private String refLoanContRslt;
	
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
	
	public String getSeq() {
		return seq;
	}
	
	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public String getSalesPerson() {
		return salesPerson;
	}
	
	public void setSalesPerson(String salesPerson) {
		this.salesPerson = salesPerson;
	}
	
	public String getSalesName() {
		return salesName;
	}
	
	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}
	
	public String getSalesRole() {
		return salesRole;
	}
	
	public void setSalesRole(String salesRole) {
		this.salesRole = salesRole;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserRole() {
		return userRole;
	}
	
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	
	public String getCustID() {
		return custID;
	}
	
	public void setCustID(String custID) {
		this.custID = custID;
	}
	
	public String getCustName() {
		return custName;
	}
	
	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public Date getsDate() {
		return sDate;
	}
	
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	
	public Date geteDate() {
		return eDate;
	}
	
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	
	public String getRefProd() {
		return refProd;
	}
	
	public void setRefProd(String refProd) {
		this.refProd = refProd;
	}
	
	public String getRefInsContRslt() {
		return refInsContRslt;
	}
	
	public void setRefInsContRslt(String refInsContRslt) {
		this.refInsContRslt = refInsContRslt;
	}
	
	public String getRefLoanContRslt() {
		return refLoanContRslt;
	}
	
	public void setRefLoanContRslt(String refLoanContRslt) {
		this.refLoanContRslt = refLoanContRslt;
	}
	
}
