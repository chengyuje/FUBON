package com.systex.jbranch.app.server.fps.prd235;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD235OutputVO extends PagingOutputVO {
	private List resultList;	
	private String prdName;
	private BigDecimal MIN_PURCHASE_AMT;
	private BigDecimal rdmTotalUnits;
	private String errorCode;
	private String errorMsg;
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public String getPrdName() {
		return prdName;
	}
	public void setPrdName(String prdName) {
		this.prdName = prdName;
	}
	public BigDecimal getMIN_PURCHASE_AMT() {
		return MIN_PURCHASE_AMT;
	}
	public void setMIN_PURCHASE_AMT(BigDecimal mIN_PURCHASE_AMT) {
		MIN_PURCHASE_AMT = mIN_PURCHASE_AMT;
	}
	public BigDecimal getRdmTotalUnits() {
		return rdmTotalUnits;
	}
	public void setRdmTotalUnits(BigDecimal rdmTotalUnits) {
		this.rdmTotalUnits = rdmTotalUnits;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
