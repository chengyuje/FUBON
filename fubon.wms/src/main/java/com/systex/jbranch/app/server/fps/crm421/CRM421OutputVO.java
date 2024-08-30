package com.systex.jbranch.app.server.fps.crm421;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM421OutputVO extends PagingOutputVO {
	private List resultList;
	private List custList;
	private List y_profeeList;
	private List highest_lvList;
	private List rcmprodList;
	private List prodList;
	private List tempList;
	private List updateList;
	private String branchNbr;

	private BigDecimal defaultFeeRate;
	private String errorMsg;
	private List applySeqList;

	public List getApplySeqList() {
		return applySeqList;
	}
	public void setApplySeqList(List applySeqList) {
		this.applySeqList = applySeqList;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public BigDecimal getDefaultFeeRate() {
		return defaultFeeRate;
	}
	public void setDefaultFeeRate(BigDecimal defaultFeeRate) {
		this.defaultFeeRate = defaultFeeRate;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getCustList() {
		return custList;
	}
	public void setCustList(List custList) {
		this.custList = custList;
	}
	public List getY_profeeList() {
		return y_profeeList;
	}
	public void setY_profeeList(List y_profeeList) {
		this.y_profeeList = y_profeeList;
	}
	public List getHighest_lvList() {
		return highest_lvList;
	}
	public void setHighest_lvList(List highest_lvList) {
		this.highest_lvList = highest_lvList;
	}
	public List getRcmprodList() {
		return rcmprodList;
	}
	public void setRcmprodList(List rcmprodList) {
		this.rcmprodList = rcmprodList;
	}
	public List getProdList() {
		return prodList;
	}
	public void setProdList(List prodList) {
		this.prodList = prodList;
	}
	public List getTempList() {
		return tempList;
	}
	public void setTempList(List tempList) {
		this.tempList = tempList;
	}
	public List getUpdateList() {
		return updateList;
	}
	public void setUpdateList(List updateList) {
		this.updateList = updateList;
	}
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}

}
