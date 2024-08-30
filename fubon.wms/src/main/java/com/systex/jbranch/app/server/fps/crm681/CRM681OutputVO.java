package com.systex.jbranch.app.server.fps.crm681;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM681OutputVO extends PagingOutputVO {

	private BigDecimal deposit;
	private BigDecimal investment;
	private BigDecimal insurance;
	private BigDecimal fuTotal; //貸款總金額
	List<CustInvestAsset> assetList; //客戶投資類產品明細

	private List resultList;
	private List resultList2;
	private List resultList3;
	private List resultList4;
	private List resultList5;
	private List resultList6;
	private List resultList7;
	private List resultList8;
	private List resultList9;

	private List secCustData;
	private List secCustDataDSN;

	private String secCustCrossSelling;

	public List getSecCustDataDSN() {
		return secCustDataDSN;
	}

	public void setSecCustDataDSN(List secCustDataDSN) {
		this.secCustDataDSN = secCustDataDSN;
	}

	public String getSecCustCrossSelling() {
		return secCustCrossSelling;
	}

	public void setSecCustCrossSelling(String secCustCrossSelling) {
		this.secCustCrossSelling = secCustCrossSelling;
	}

	public List getSecCustData() {
		return secCustData;
	}

	public void setSecCustData(List secCustData) {
		this.secCustData = secCustData;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public List getResultList3() {
		return resultList3;
	}

	public void setResultList3(List resultList3) {
		this.resultList3 = resultList3;
	}

	public List getResultList4() {
		return resultList4;
	}

	public void setResultList4(List resultList4) {
		this.resultList4 = resultList4;
	}

	public List getResultList5() {
		return resultList5;
	}

	public void setResultList5(List resultList5) {
		this.resultList5 = resultList5;
	}

	public List getResultList6() {
		return resultList6;
	}

	public void setResultList6(List resultList6) {
		this.resultList6 = resultList6;
	}

	public List getResultList7() {
		return resultList7;
	}

	public void setResultList7(List resultList7) {
		this.resultList7 = resultList7;
	}

	public List getResultList8() {
		return resultList8;
	}

	public void setResultList8(List resultList8) {
		this.resultList8 = resultList8;
	}

	public List getResultList9() {
		return resultList9;
	}

	public void setResultList9(List resultList9) {
		this.resultList9 = resultList9;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public BigDecimal getInvestment() {
		return investment;
	}

	public void setInvestment(BigDecimal investment) {
		this.investment = investment;
	}

	public BigDecimal getInsurance() {
		return insurance;
	}

	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}

	public List<CustInvestAsset> getAssetList() {
		return assetList;
	}

	public void setAssetList(List<CustInvestAsset> assetList) {
		this.assetList = assetList;
	}

	public BigDecimal getFuTotal() {
		return fuTotal;
	}

	public void setFuTotal(BigDecimal fuTotal) {
		this.fuTotal = fuTotal;
	}

}
