package com.systex.jbranch.app.server.fps.crm829;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM829OutputVO extends PagingOutputVO{
	private List resultList;				// 明細資料
	private List sellList;					// 贖回在途資料
	private int totalPlanNo;				// 投資計畫總數
	private BigDecimal totalInvestmentTwd;	// 總投資金額(台幣)
	private BigDecimal totalMarketValueTwd;	// 參考總市值(台幣)
	private String errorCode;
	private String errorMsg;
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getSellList() {
		return sellList;
	}
	public void setSellList(List sellList) {
		this.sellList = sellList;
	}
	public int getTotalPlanNo() {
		return totalPlanNo;
	}
	public void setTotalPlanNo(int totalPlanNo) {
		this.totalPlanNo = totalPlanNo;
	}
	public BigDecimal getTotalInvestmentTwd() {
		return totalInvestmentTwd;
	}
	public void setTotalInvestmentTwd(BigDecimal totalInvestmentTwd) {
		this.totalInvestmentTwd = totalInvestmentTwd;
	}
	public BigDecimal getTotalMarketValueTwd() {
		return totalMarketValueTwd;
	}
	public void setTotalMarketValueTwd(BigDecimal totalMarketValueTwd) {
		this.totalMarketValueTwd = totalMarketValueTwd;
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
