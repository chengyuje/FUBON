package com.systex.jbranch.app.server.fps.crm82A;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM82AOutputVO extends PagingOutputVO{
	private List resultList;				//明細資料
	private BigDecimal totalInvestmentTwd;	//總投資金額(台幣)
	private BigDecimal totalMarketValueTwd;	//參考總市值(台幣)
	private BigDecimal totalROI;			//總含息報酬率
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
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
	public BigDecimal getTotalROI() {
		return totalROI;
	}
	public void setTotalROI(BigDecimal totalROI) {
		this.totalROI = totalROI;
	}
	
}
