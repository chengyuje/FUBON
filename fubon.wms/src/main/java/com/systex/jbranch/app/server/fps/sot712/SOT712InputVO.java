package com.systex.jbranch.app.server.fps.sot712;

import java.math.BigDecimal;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT712InputVO extends PagingInputVO{
	
	private String tellerID;
	private String recSeq;
	private String custID;
	private String branchNbr;
	private String prodType;
	private String prodID;
	private String tradeType;
	private String tradeSeq;
	private int addDays;
	
	private String profInvestorYN;
	private String isFirstTrade; 
	private String custRemarks;
	private String custProRemark;
	
	private String bargainApplySeq;
	private String bargainStatus;
	private String pro1500;
	private String trustProCorp;
	
	private Boolean under300;
	private String ovsPrivateYN;		//是否為境外私募基金
	
	public String getBargainApplySeq() {
		return bargainApplySeq;
	}

	public void setBargainApplySeq(String bargainApplySeq) {
		this.bargainApplySeq = bargainApplySeq;
	}

	public String getProfInvestorYN() {
		return profInvestorYN;
	}

	public void setProfInvestorYN(String profInvestorYN) {
		this.profInvestorYN = profInvestorYN;
	}

	public String getIsFirstTrade() {
		return isFirstTrade;
	}

	public void setIsFirstTrade(String isFirstTrade) {
		this.isFirstTrade = isFirstTrade;
	}

	public String getCustRemarks() {
		return custRemarks;
	}

	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}

	public String getTradeSeq() {
		return tradeSeq;
	}
	
	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}
	
	public String getTradeType() {
		return tradeType;
	}
	
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	public String getTellerID() {
		return tellerID;
	}
	
	public void setTellerID(String tellerID) {
		this.tellerID = tellerID;
	}
	
	public String getRecSeq() {
		return recSeq;
	}
	
	public void setRecSeq(String recSeq) {
		this.recSeq = recSeq;
	}
	
	public String getCustID() {
		return custID;
	}
	
	public void setCustID(String custID) {
		this.custID = custID;
	}
	
	public String getBranchNbr() {
		return branchNbr;
	}
	
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	
	public String getProdType() {
		return prodType;
	}
	
	public void setProdType(String prodType) {
		this.prodType = prodType;
	}
	
	public String getProdID() {
		return prodID;
	}
	
	public void setProdID(String prodID) {
		this.prodID = prodID;
	}

	public int getAddDays() {
		return addDays;
	}

	public void setAddDays(int addDays) {
		this.addDays = addDays;
	}

	public String getCustProRemark() {
		return custProRemark;
	}

	public void setCustProRemark(String custProRemark) {
		this.custProRemark = custProRemark;
	}

	public String getBargainStatus() {
		return bargainStatus;
	}

	public void setBargainStatus(String bargainStatus) {
		this.bargainStatus = bargainStatus;
	}

	public String getPro1500() {
		return pro1500;
	}

	public void setPro1500(String pro1500) {
		this.pro1500 = pro1500;
	}
	
	public Boolean getUnder300() {
		return under300;
	}

	public void setUnder300(Boolean under300) {
		this.under300 = under300;
	}

	public String getTrustProCorp() {
		return trustProCorp;
	}

	public void setTrustProCorp(String trustProCorp) {
		this.trustProCorp = trustProCorp;
	}

	public String getOvsPrivateYN() {
		return ovsPrivateYN;
	}

	public void setOvsPrivateYN(String ovsPrivateYN) {
		this.ovsPrivateYN = ovsPrivateYN;
	}
	
}
