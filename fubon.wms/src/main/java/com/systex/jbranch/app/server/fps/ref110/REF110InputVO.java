package com.systex.jbranch.app.server.fps.ref110;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class REF110InputVO extends PagingInputVO{
	
	private String seq;
	private String dataDate;
	private String regionID;
	private String branchAreaID;
	private String branchID;
	private String refProd;
	private String empID;
	private String empName;
	private String empRoleName;
	private String custID;
	private String custName;
	private String custIDSEQ;
	private String custAoCode;
	private String refEmpID;
	private String refEmpName;
	private String refEmpRoleName;
	private String contRslt;
	private String nonGrantReason;
	private String comments;
	
	public String getCustAoCode() {
		return custAoCode;
	}

	public void setCustAoCode(String custAoCode) {
		this.custAoCode = custAoCode;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getContRslt() {
		return contRslt;
	}

	public void setContRslt(String contRslt) {
		this.contRslt = contRslt;
	}

	public String getNonGrantReason() {
		return nonGrantReason;
	}

	public void setNonGrantReason(String nonGrantReason) {
		this.nonGrantReason = nonGrantReason;
	}

	public String getCustIDSEQ() {
		return custIDSEQ;
	}

	public void setCustIDSEQ(String custIDSEQ) {
		this.custIDSEQ = custIDSEQ;
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

	public String getRefProd() {
		return refProd;
	}

	public void setRefProd(String refProd) {
		this.refProd = refProd;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpRoleName() {
		return empRoleName;
	}

	public void setEmpRoleName(String empRoleName) {
		this.empRoleName = empRoleName;
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

	public String getRefEmpID() {
		return refEmpID;
	}

	public void setRefEmpID(String refEmpID) {
		this.refEmpID = refEmpID;
	}

	public String getRefEmpName() {
		return refEmpName;
	}

	public void setRefEmpName(String refEmpName) {
		this.refEmpName = refEmpName;
	}

	public String getRefEmpRoleName() {
		return refEmpRoleName;
	}

	public void setRefEmpRoleName(String refEmpRoleName) {
		this.refEmpRoleName = refEmpRoleName;
	}

	public String getSeq() {
		return seq;
	}
	
	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}
	
}
