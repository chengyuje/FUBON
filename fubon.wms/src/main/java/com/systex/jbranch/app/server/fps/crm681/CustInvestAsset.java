package com.systex.jbranch.app.server.fps.crm681;

import java.math.BigDecimal;
import java.util.Date;

public class CustInvestAsset {
	
	private String BUSINESS_CODE; 				// 業務別
	private String CURRENCY; 					// 幣別
	private BigDecimal TOTAL_SUM; 				// 總餘額
	private BigDecimal TOTAL_SUM_TWD; 			// 總餘額(台幣)

	private BigDecimal TOTAL_INVEST;			// 投資成本(原幣)
	private BigDecimal TOTAL_INVEST_TWD;		// 投資成本(台幣)
	private BigDecimal TOTAL_BENEFIT_AMT1;		// 損益金額(不含息)(原幣)
	private BigDecimal TOTAL_BENEFIT_AMT1_TWD;	// 損益金額(不含息)(台幣)
	private BigDecimal TOTAL_BENEFIT_AMT2;		// 損益金額(含息)(原幣)
	private BigDecimal TOTAL_BENEFIT_AMT2_TWD;	// 損益金額(含息)(台幣)
	
	public BigDecimal getTOTAL_INVEST() {
		return TOTAL_INVEST;
	}

	public void setTOTAL_INVEST(BigDecimal tOTAL_INVEST) {
		TOTAL_INVEST = tOTAL_INVEST;
	}

	public BigDecimal getTOTAL_INVEST_TWD() {
		return TOTAL_INVEST_TWD;
	}

	public void setTOTAL_INVEST_TWD(BigDecimal tOTAL_INVEST_TWD) {
		TOTAL_INVEST_TWD = tOTAL_INVEST_TWD;
	}

	public BigDecimal getTOTAL_BENEFIT_AMT1() {
		return TOTAL_BENEFIT_AMT1;
	}

	public void setTOTAL_BENEFIT_AMT1(BigDecimal tOTAL_BENEFIT_AMT1) {
		TOTAL_BENEFIT_AMT1 = tOTAL_BENEFIT_AMT1;
	}

	public BigDecimal getTOTAL_BENEFIT_AMT1_TWD() {
		return TOTAL_BENEFIT_AMT1_TWD;
	}

	public void setTOTAL_BENEFIT_AMT1_TWD(BigDecimal tOTAL_BENEFIT_AMT1_TWD) {
		TOTAL_BENEFIT_AMT1_TWD = tOTAL_BENEFIT_AMT1_TWD;
	}

	public BigDecimal getTOTAL_BENEFIT_AMT2() {
		return TOTAL_BENEFIT_AMT2;
	}

	public void setTOTAL_BENEFIT_AMT2(BigDecimal tOTAL_BENEFIT_AMT2) {
		TOTAL_BENEFIT_AMT2 = tOTAL_BENEFIT_AMT2;
	}

	public BigDecimal getTOTAL_BENEFIT_AMT2_TWD() {
		return TOTAL_BENEFIT_AMT2_TWD;
	}

	public void setTOTAL_BENEFIT_AMT2_TWD(BigDecimal tOTAL_BENEFIT_AMT2_TWD) {
		TOTAL_BENEFIT_AMT2_TWD = tOTAL_BENEFIT_AMT2_TWD;
	}

	public String getBUSINESS_CODE() {
		return BUSINESS_CODE;
	}

	public void setBUSINESS_CODE(String bUSINESS_CODE) {
		BUSINESS_CODE = bUSINESS_CODE;
	}

	public String getCURRENCY() {
		return CURRENCY;
	}

	public void setCURRENCY(String cURRENCY) {
		CURRENCY = cURRENCY;
	}

	public BigDecimal getTOTAL_SUM() {
		return TOTAL_SUM;
	}

	public void setTOTAL_SUM(BigDecimal tOTAL_SUM) {
		TOTAL_SUM = tOTAL_SUM;
	}

	public BigDecimal getTOTAL_SUM_TWD() {
		return TOTAL_SUM_TWD;
	}

	public void setTOTAL_SUM_TWD(BigDecimal tOTAL_SUM_TWD) {
		TOTAL_SUM_TWD = tOTAL_SUM_TWD;
	}

}
