package com.systex.jbranch.app.server.fps.appvo.pii;

import java.math.BigDecimal;
import java.util.List;

public class LoadFPModuleBHCalInputVO {

	private Object fpcal130InputVO;
	private List fpcal131LoanCashFlowList;
	private BigDecimal tolAmt; // 歸還總額
	private BigDecimal tolInterestAmt; // 歸還總利息金額
	
	public LoadFPModuleBHCalInputVO() {
		super();
	}
	public Object getFpcal130InputVO() {
		return fpcal130InputVO;
	}
	public void setFpcal130InputVO(Object fpcal130InputVO) {
		this.fpcal130InputVO = fpcal130InputVO;
	}
	public List getFpcal131LoanCashFlowList() {
		return fpcal131LoanCashFlowList;
	}
	public void setFpcal131LoanCashFlowList(List fpcal131LoanCashFlowList) {
		this.fpcal131LoanCashFlowList = fpcal131LoanCashFlowList;
	}
	public BigDecimal getTolAmt() {
		return tolAmt;
	}
	public void setTolAmt(BigDecimal tolAmt) {
		this.tolAmt = tolAmt;
	}
	public BigDecimal getTolInterestAmt() {
		return tolInterestAmt;
	}
	public void setTolInterestAmt(BigDecimal tolInterestAmt) {
		this.tolInterestAmt = tolInterestAmt;
	}
	
}
