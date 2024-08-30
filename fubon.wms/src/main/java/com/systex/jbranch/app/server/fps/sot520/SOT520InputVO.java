package com.systex.jbranch.app.server.fps.sot520;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT520InputVO extends PagingInputVO {
	
	// 共同
	private String tradeSEQ;
	private String prodType;
	private String tradeType;
//	private String stockCode;
	
	// 主檔使用
	private String custID;
	private String custName;
	private String agentID;
	private String agentName;
	private String kycLV;
	private Date kycDueDate;
	private String profInvestorYN;
	private String piRemark; 		//專業投資人註記
	private Date piDueDate;
	private String custRemarks;
	private String isOBU;
	private String isAgreeProdAdv;
	private Date bargainDueDate;
	private String tradeStatus;
	private String isBargainNeeded;
	private String bargainFeeFlag;
	private String isRecNeeded;
	private String recSEQ;
	private Date sendDate;
	
	// 明細使用
	private BigDecimal carSEQ;
	
	private String prodID; 				//商品代號
	private String prodName;			//商品名稱
	private String prodCurr;			//計價幣別
	private String prodRiskLV;			//產品風險等級
	private BigDecimal prodMinBuyAmt;	//最低申購面額
	private BigDecimal prodMinGrdAmt;	//累計申購面額
	private String trustCurrType;		//信託幣別類型
	private String trustCurr;			//信託幣別
	private BigDecimal trustUnit;		//庫存張數
	private String marketType;			//債券市場種類
	private BigDecimal refVal;			//參考報價
	private Date refValDate;			//參考報價日期
	private BigDecimal purchaseAmt;		//申購金額/庫存金額
	private String entrustType;			//委託價格類型/贖回方式
	private BigDecimal entrustAmt;		//委託價格/贖回價格
	private BigDecimal trustAmt;		//信託本金
	private BigDecimal defaultFeeRate;	//表定手續費率
	private BigDecimal advFeeRate;		//事先申請手續費率
	private String bargainApplySEQ;		//議價編號
	private BigDecimal feeRate;			//手續費率/信託管理費率
	private BigDecimal fee;				//手續費金額/預估信託管理費
	private BigDecimal feeDiscount;		//手續費折數
	private BigDecimal payableFee;		//應付前手息/應收前手息
	private BigDecimal totAmt;			//總扣款金額/預估贖回入帳金額
	private String debitAcct;			//扣款帳號
	private String trustAcct;			//信託帳號
	private String creditAcct;			//收益入帳帳號/贖回款入帳帳號
	private Date tradeDate;				//交易日期
	private BigDecimal limitedPrice;
	private BigDecimal bestFeeRate;		//最優手續費率
	private String certificateID;		//憑證編號
	private BigDecimal bondVal;			    //票面價值
	private BigDecimal redeemAmt;
	private String redeemType;
	private String narratorID;
	private String narratorName;

	private String trustTS;
	private String contractID;			//契約編號
	private String trustPeopNum;		//是否為多委託人契約

	//0000275: 金錢信託受監護受輔助宣告交易控管調整
	private String GUARDIANSHIP_FLAG; //受監護輔助 空白：無監護輔助 1.監護宣告 2輔助宣告
	
	public BigDecimal getBondVal() {
		return bondVal;
	}

	public void setBondVal(BigDecimal bondVal) {
		this.bondVal = bondVal;
	}

	public String getRedeemType() {
		return redeemType;
	}

	public void setRedeemType(String redeemType) {
		this.redeemType = redeemType;
	}

	public BigDecimal getRedeemAmt() {
		return redeemAmt;
	}

	public void setRedeemAmt(BigDecimal redeemAmt) {
		this.redeemAmt = redeemAmt;
	}

	public String getCertificateID() {
		return certificateID;
	}

	public void setCertificateID(String certificateID) {
		this.certificateID = certificateID;
	}

	public BigDecimal getLimitedPrice() {
		return limitedPrice;
	}

	public void setLimitedPrice(BigDecimal limitedPrice) {
		this.limitedPrice = limitedPrice;
	}

	public BigDecimal getBestFeeRate() {
		return bestFeeRate;
	}

	public void setBestFeeRate(BigDecimal bestFeeRate) {
		this.bestFeeRate = bestFeeRate;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getTradeSEQ() {
		return tradeSEQ;
	}
	
	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
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
	
	public String getAgentID() {
		return agentID;
	}
	
	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}
	
	public String getAgentName() {
		return agentName;
	}
	
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	public String getKycLV() {
		return kycLV;
	}
	
	public void setKycLV(String kycLV) {
		this.kycLV = kycLV;
	}
	
	public Date getKycDueDate() {
		return kycDueDate;
	}
	
	public void setKycDueDate(Date kycDueDate) {
		this.kycDueDate = kycDueDate;
	}
	
	public String getProfInvestorYN() {
		return profInvestorYN;
	}
	
	public void setProfInvestorYN(String profInvestorYN) {
		this.profInvestorYN = profInvestorYN;
	}
	
	public String getPiRemark() {
		return piRemark;
	}

	public void setPiRemark(String piRemark) {
		this.piRemark = piRemark;
	}
	
	public Date getPiDueDate() {
		return piDueDate;
	}
	
	public void setPiDueDate(Date piDueDate) {
		this.piDueDate = piDueDate;
	}
	
	public String getCustRemarks() {
		return custRemarks;
	}
	
	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}
	
	public String getIsOBU() {
		return isOBU;
	}
	
	public void setIsOBU(String isOBU) {
		this.isOBU = isOBU;
	}
	
	public String getIsAgreeProdAdv() {
		return isAgreeProdAdv;
	}
	
	public void setIsAgreeProdAdv(String isAgreeProdAdv) {
		this.isAgreeProdAdv = isAgreeProdAdv;
	}
	
	public Date getBargainDueDate() {
		return bargainDueDate;
	}
	
	public void setBargainDueDate(Date bargainDueDate) {
		this.bargainDueDate = bargainDueDate;
	}
	
	public String getTradeStatus() {
		return tradeStatus;
	}
	
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	
	public String getIsBargainNeeded() {
		return isBargainNeeded;
	}
	
	public void setIsBargainNeeded(String isBargainNeeded) {
		this.isBargainNeeded = isBargainNeeded;
	}
	
	public String getBargainFeeFlag() {
		return bargainFeeFlag;
	}
	
	public void setBargainFeeFlag(String bargainFeeFlag) {
		this.bargainFeeFlag = bargainFeeFlag;
	}
	
	public String getIsRecNeeded() {
		return isRecNeeded;
	}
	
	public void setIsRecNeeded(String isRecNeeded) {
		this.isRecNeeded = isRecNeeded;
	}
	
	public String getRecSEQ() {
		return recSEQ;
	}
	
	public void setRecSEQ(String recSEQ) {
		this.recSEQ = recSEQ;
	}
	
	public Date getSendDate() {
		return sendDate;
	}
	
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	
	public BigDecimal getCarSEQ() {
		return carSEQ;
	}
	
	public void setCarSEQ(BigDecimal carSEQ) {
		this.carSEQ = carSEQ;
	}
	
	public String getProdID() {
		return prodID;
	}
	
	public void setProdID(String prodID) {
		this.prodID = prodID;
	}
	
	public String getProdName() {
		return prodName;
	}
	
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	
	public String getProdCurr() {
		return prodCurr;
	}
	
	public void setProdCurr(String prodCurr) {
		this.prodCurr = prodCurr;
	}
	
	public String getProdRiskLV() {
		return prodRiskLV;
	}
	
	public void setProdRiskLV(String prodRiskLV) {
		this.prodRiskLV = prodRiskLV;
	}
	
	public BigDecimal getProdMinBuyAmt() {
		return prodMinBuyAmt;
	}
	
	public void setProdMinBuyAmt(BigDecimal prodMinBuyAmt) {
		this.prodMinBuyAmt = prodMinBuyAmt;
	}
	
	public BigDecimal getProdMinGrdAmt() {
		return prodMinGrdAmt;
	}
	
	public void setProdMinGrdAmt(BigDecimal prodMinGrdAmt) {
		this.prodMinGrdAmt = prodMinGrdAmt;
	}
	
	public String getTrustCurrType() {
		return trustCurrType;
	}
	
	public void setTrustCurrType(String trustCurrType) {
		this.trustCurrType = trustCurrType;
	}
	
	public String getTrustCurr() {
		return trustCurr;
	}
	
	public void setTrustCurr(String trustCurr) {
		this.trustCurr = trustCurr;
	}
	
	public BigDecimal getTrustUnit() {
		return trustUnit;
	}
	
	public void setTrustUnit(BigDecimal trustUnit) {
		this.trustUnit = trustUnit;
	}
	
	public String getMarketType() {
		return marketType;
	}
	
	public void setMarketType(String marketType) {
		this.marketType = marketType;
	}
	
	public BigDecimal getRefVal() {
		return refVal;
	}
	
	public void setRefVal(BigDecimal refVal) {
		this.refVal = refVal;
	}
	
	public Date getRefValDate() {
		return refValDate;
	}
	
	public void setRefValDate(Date refValDate) {
		this.refValDate = refValDate;
	}
	
	public BigDecimal getPurchaseAmt() {
		return purchaseAmt;
	}
	
	public void setPurchaseAmt(BigDecimal purchaseAmt) {
		this.purchaseAmt = purchaseAmt;
	}
	
	public String getEntrustType() {
		return entrustType;
	}
	
	public void setEntrustType(String entrustType) {
		this.entrustType = entrustType;
	}
	
	public BigDecimal getEntrustAmt() {
		return entrustAmt;
	}
	
	public void setEntrustAmt(BigDecimal entrustAmt) {
		this.entrustAmt = entrustAmt;
	}
	
	public BigDecimal getTrustAmt() {
		return trustAmt;
	}
	
	public void setTrustAmt(BigDecimal trustAmt) {
		this.trustAmt = trustAmt;
	}
	
	public BigDecimal getDefaultFeeRate() {
		return defaultFeeRate;
	}
	
	public void setDefaultFeeRate(BigDecimal defaultFeeRate) {
		this.defaultFeeRate = defaultFeeRate;
	}
	
	public BigDecimal getAdvFeeRate() {
		return advFeeRate;
	}
	
	public void setAdvFeeRate(BigDecimal advFeeRate) {
		this.advFeeRate = advFeeRate;
	}
	
	public String getBargainApplySEQ() {
		return bargainApplySEQ;
	}
	
	public void setBargainApplySEQ(String bargainApplySEQ) {
		this.bargainApplySEQ = bargainApplySEQ;
	}
	
	public BigDecimal getFeeRate() {
		return feeRate;
	}
	
	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}
	
	public BigDecimal getFee() {
		return fee;
	}
	
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	
	public BigDecimal getFeeDiscount() {
		return feeDiscount;
	}
	
	public void setFeeDiscount(BigDecimal feeDiscount) {
		this.feeDiscount = feeDiscount;
	}
	
	public BigDecimal getPayableFee() {
		return payableFee;
	}
	
	public void setPayableFee(BigDecimal payableFee) {
		this.payableFee = payableFee;
	}
	
	public BigDecimal getTotAmt() {
		return totAmt;
	}
	
	public void setTotAmt(BigDecimal totAmt) {
		this.totAmt = totAmt;
	}
	
	public String getDebitAcct() {
		return debitAcct;
	}
	
	public void setDebitAcct(String debitAcct) {
		this.debitAcct = debitAcct;
	}
	
	public String getTrustAcct() {
		return trustAcct;
	}
	
	public void setTrustAcct(String trustAcct) {
		this.trustAcct = trustAcct;
	}
	
	public String getCreditAcct() {
		return creditAcct;
	}
	
	public void setCreditAcct(String creditAcct) {
		this.creditAcct = creditAcct;
	}
	
	public Date getTradeDate() {
		return tradeDate;
	}
	
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	
	public String getNarratorID() {
		return narratorID;
	}
	
	public void setNarratorID(String narratorID) {
		this.narratorID = narratorID;
	}
	
	public String getNarratorName() {
		return narratorName;
	}
	
	public void setNarratorName(String narratorName) {
		this.narratorName = narratorName;
	}

	public String getTrustTS() {
		return trustTS;
	}

	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}

	public String getContractID() {
		return contractID;
	}

	public void setContractID(String contractID) {
		this.contractID = contractID;
	}

	public String getTrustPeopNum() {
		return trustPeopNum;
	}

	public void setTrustPeopNum(String trustPeopNum) {
		this.trustPeopNum = trustPeopNum;
	}

	public String getGUARDIANSHIP_FLAG() {
		return GUARDIANSHIP_FLAG;
	}

	public void setGUARDIANSHIP_FLAG(String GUARDIANSHIP_FLAG) {
		this.GUARDIANSHIP_FLAG = GUARDIANSHIP_FLAG;
	}
}
