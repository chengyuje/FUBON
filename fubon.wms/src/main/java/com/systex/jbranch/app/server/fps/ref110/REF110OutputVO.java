package com.systex.jbranch.app.server.fps.ref110;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class REF110OutputVO extends PagingOutputVO{
	
	private String seq;
	private String dataDate;
	private String regionID;
	private String regionName;
	private String branchAreaID;
	private String branchAreaName;
	private String branchID;
	private String branchName;
	private String refProd;
	
	private String empID;
	private String empName;
	private String empJobTitleName;
	private String empRoleName;
	
	private String custID;
	private String custName;
	private String custBraNbr;
	private String custAoCode;
	private String custAoType;
	private String braNbr; //客戶歸屬行
	private String aoCode; //客戶的ao
	private String yesterdayNoAO; //客戶前一日是否有ao
	private String salerecCounts; //30日內是否有轉介
	private String recYN;
	
	private String refJRMEmpID;
	private String refEmpID;
	private String refEmpName;
	private String refAoCode;
	private String refEmpJobTitleName;
	private String refEmpRoleName;
	private String refEmpRegionID;
	private String refEmpBranchAreaID;
	private String refEmpBranchID;
	
	private String salerecRefEmpRoleName;
	
	private String contRslt;
	private String nonGrantReason;
	private String comments;
	
	private String errorMsg;
	
	//
	private String uhrmYN;
	
	private String viceCust;
	private String maintainCust;
	private String massCust;
	
	public String getRefJRMEmpID() {
		return refJRMEmpID;
	}

	public void setRefJRMEmpID(String refJRMEmpID) {
		this.refJRMEmpID = refJRMEmpID;
	}

	public String getViceCust() {
		return viceCust;
	}

	public void setViceCust(String viceCust) {
		this.viceCust = viceCust;
	}

	public String getMaintainCust() {
		return maintainCust;
	}

	public void setMaintainCust(String maintainCust) {
		this.maintainCust = maintainCust;
	}

	public String getMassCust() {
		return massCust;
	}

	public void setMassCust(String massCust) {
		this.massCust = massCust;
	}

	public String getCustAoType() {
		return custAoType;
	}

	public void setCustAoType(String custAoType) {
		this.custAoType = custAoType;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getUhrmYN() {
		return uhrmYN;
	}

	public void setUhrmYN(String uhrmYN) {
		this.uhrmYN = uhrmYN;
	}

	public String getBranchAreaName() {
		return branchAreaName;
	}

	public void setBranchAreaName(String branchAreaName) {
		this.branchAreaName = branchAreaName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getRefEmpRegionID() {
		return refEmpRegionID;
	}

	public void setRefEmpRegionID(String refEmpRegionID) {
		this.refEmpRegionID = refEmpRegionID;
	}

	public String getRefEmpBranchAreaID() {
		return refEmpBranchAreaID;
	}

	public void setRefEmpBranchAreaID(String refEmpBranchAreaID) {
		this.refEmpBranchAreaID = refEmpBranchAreaID;
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

	public String getSalerecRefEmpRoleName() {
		return salerecRefEmpRoleName;
	}

	public void setSalerecRefEmpRoleName(String salerecRefEmpRoleName) {
		this.salerecRefEmpRoleName = salerecRefEmpRoleName;
	}

	public String getRecYN() {
		return recYN;
	}

	public void setRecYN(String recYN) {
		this.recYN = recYN;
	}

	public String getCustAoCode() {
		return custAoCode;
	}

	public void setCustAoCode(String custAoCode) {
		this.custAoCode = custAoCode;
	}

	public String getCustBraNbr() {
		return custBraNbr;
	}

	public void setCustBraNbr(String custBraNbr) {
		this.custBraNbr = custBraNbr;
	}

	public String getRefAoCode() {
		return refAoCode;
	}

	public void setRefAoCode(String refAoCode) {
		this.refAoCode = refAoCode;
	}

	public String getRefEmpBranchID() {
		return refEmpBranchID;
	}

	public void setRefEmpBranchID(String refEmpBranchID) {
		this.refEmpBranchID = refEmpBranchID;
	}

	public String getSalerecCounts() {
		return salerecCounts;
	}

	public void setSalerecCounts(String salerecCounts) {
		this.salerecCounts = salerecCounts;
	}

	public String getBraNbr() {
		return braNbr;
	}

	public void setBraNbr(String braNbr) {
		this.braNbr = braNbr;
	}

	public String getAoCode() {
		return aoCode;
	}

	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}

	public String getYesterdayNoAO() {
		return yesterdayNoAO;
	}

	public void setYesterdayNoAO(String yesterdayNoAO) {
		this.yesterdayNoAO = yesterdayNoAO;
	}

	public String getEmpJobTitleName() {
		return empJobTitleName;
	}

	public void setEmpJobTitleName(String empJobTitleName) {
		this.empJobTitleName = empJobTitleName;
	}

	public String getRefEmpJobTitleName() {
		return refEmpJobTitleName;
	}

	public void setRefEmpJobTitleName(String refEmpJobTitleName) {
		this.refEmpJobTitleName = refEmpJobTitleName;
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

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
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

	public String getDataDate() {
		return dataDate;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
