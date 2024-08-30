package com.systex.jbranch.app.server.fps.sot210;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT210InputVO extends PagingInputVO {
	
	// 共同
	private String tradeSEQ;
	private String prodType;
	private String tradeType;
	private String stockCode;
	
	// 主檔使用
	private String custID;
	private String custName;
	private String agentID;
	private String agentName;
	private String kycLV;
	private Date kycDueDate;
	private String profInvestorYN;
	private Date piDueDate;
	private String custRemarks;
	private String piRemark;
	private String isOBU;
	private String isAgreeProdAdv;
	private Date bargainDueDate;
	private String tradeStatus;
	private String isBargainNeeded;
	private String bargainFeeFlag;
	private String isRecNeeded;
	private String recSEQ;
	private Date sendDate;
	private String fatcaType;
	private String custProType;
	private String investType;	//特定客戶
	private String investDue;	//特定客戶申請日距今是否滿兩週
	private String hnwcYN;
	private String hnwcServiceYN;
	
	// 明細使用
	private BigDecimal carSEQ;
	
	private String prodID; 				//商品代號
	private String prodName;			//商品名稱
	private String prodCurr;			//計價幣別
	private String prodRiskLV;			//產品風險等級
	private BigDecimal prodMinBuyAmt; 	//最低申購金額
	private BigDecimal prodMinBuyUnit;	//最低買進單位
	private BigDecimal prodMinGrdUnit;	//最低累進單位
	private BigDecimal unitNum;			//買進股數
	private String entrustType;			//買進價格指示
	
	private BigDecimal entrustAmt;		//買進價格 約定限價
	private BigDecimal entrustAmt1;		//買進價格 限價-元
	private BigDecimal entrustAmt2;		//買進價格 限價-XXXX.XX元以下買進
	
	private BigDecimal closingPrice;	//收盤價
	private Date closingPriceDate;		//收盤價日期
	private BigDecimal entrustDiscount;	//限價折扣率
	private String dueDateShow;
	private String dueDate;				//交易指示到期日
	private BigDecimal defaultFeeRate;	//表定手續費率
	private String feeRateType;			//手續費優惠方式
	private String brgReason;			//議價原因
	private String bargainStatus;		//議價狀態
	private String bargainApplySEQ;		//議價編號
	private BigDecimal feeRate;			//手續 費率
	private BigDecimal fee;				//手續費金額
	private BigDecimal feeDiscount;		//手續費折數
	private String debitAcct;			//扣款帳號
	private String trustAcct;			//信託帳號
	private String creditAcct;			//收益入帳帳號
	private Date tradeDate;				//交易日期
	private BigDecimal stopLossPerc;	//停損點
	private BigDecimal takeProfitPerc;	//停利點
	private String plNotifyWays;		//停損停利通知方式
	private String countryCode;			//交易市場別
	private String narratorID;
	private String narratorName;
	
	private String trustTS;				//S=特金/M=金錢信託
	private String contractID;			//契約編號
	private String trustPeopNum;		//是否為多委託人契約
	
	//0000275: 金錢信託受監護受輔助宣告交易控管調整
	private String GUARDIANSHIP_FLAG; //受監護輔助 空白：無監護輔助 1.監護宣告 2輔助宣告
	
	private String flagNumber;
	
	public String getFatcaType() {
		return fatcaType;
	}

	public void setFatcaType(String fatcaType) {
		this.fatcaType = fatcaType;
	}

	public String getCustProType() {
		return custProType;
	}

	public void setCustProType(String custProType) {
		this.custProType = custProType;
	}

	public String getInvestType() {
		return investType;
	}

	public void setInvestType(String investType) {
		this.investType = investType;
	}

	public String getInvestDue() {
		return investDue;
	}

	public void setInvestDue(String investDue) {
		this.investDue = investDue;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
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

	public BigDecimal getCarSEQ() {
		return carSEQ;
	}

	public void setCarSEQ(BigDecimal carSEQ) {
		this.carSEQ = carSEQ;
	}

	public BigDecimal getEntrustAmt1() {
		return entrustAmt1;
	}

	public void setEntrustAmt1(BigDecimal entrustAmt1) {
		this.entrustAmt1 = entrustAmt1;
	}

	public BigDecimal getEntrustAmt2() {
		return entrustAmt2;
	}

	public void setEntrustAmt2(BigDecimal entrustAmt2) {
		this.entrustAmt2 = entrustAmt2;
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

	public BigDecimal getProdMinBuyUnit() {
		return prodMinBuyUnit;
	}

	public void setProdMinBuyUnit(BigDecimal prodMinBuyUnit) {
		this.prodMinBuyUnit = prodMinBuyUnit;
	}

	public BigDecimal getProdMinGrdUnit() {
		return prodMinGrdUnit;
	}

	public void setProdMinGrdUnit(BigDecimal prodMinGrdUnit) {
		this.prodMinGrdUnit = prodMinGrdUnit;
	}

	public BigDecimal getUnitNum() {
		return unitNum;
	}

	public void setUnitNum(BigDecimal unitNum) {
		this.unitNum = unitNum;
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

	public BigDecimal getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(BigDecimal closingPrice) {
		this.closingPrice = closingPrice;
	}

	public Date getClosingPriceDate() {
		return closingPriceDate;
	}

	public void setClosingPriceDate(Date closingPriceDate) {
		this.closingPriceDate = closingPriceDate;
	}

	public BigDecimal getEntrustDiscount() {
		return entrustDiscount;
	}

	public void setEntrustDiscount(BigDecimal entrustDiscount) {
		this.entrustDiscount = entrustDiscount;
	}

	public String getDueDateShow() {
		return dueDateShow;
	}

	public void setDueDateShow(String dueDateShow) {
		this.dueDateShow = dueDateShow;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getDefaultFeeRate() {
		return defaultFeeRate;
	}

	public void setDefaultFeeRate(BigDecimal defaultFeeRate) {
		this.defaultFeeRate = defaultFeeRate;
	}

	public String getFeeRateType() {
		return feeRateType;
	}

	public void setFeeRateType(String feeRateType) {
		this.feeRateType = feeRateType;
	}

	public String getBrgReason() {
		return brgReason;
	}

	public void setBrgReason(String brgReason) {
		this.brgReason = brgReason;
	}

	public String getBargainStatus() {
		return bargainStatus;
	}

	public void setBargainStatus(String bargainStatus) {
		this.bargainStatus = bargainStatus;
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

	public BigDecimal getStopLossPerc() {
		return stopLossPerc;
	}

	public void setStopLossPerc(BigDecimal stopLossPerc) {
		this.stopLossPerc = stopLossPerc;
	}

	public BigDecimal getTakeProfitPerc() {
		return takeProfitPerc;
	}

	public void setTakeProfitPerc(BigDecimal takeProfitPerc) {
		this.takeProfitPerc = takeProfitPerc;
	}

	public String getPlNotifyWays() {
		return plNotifyWays;
	}

	public void setPlNotifyWays(String plNotifyWays) {
		this.plNotifyWays = plNotifyWays;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getProdID() {
		return prodID;
	}

	public void setProdID(String prodID) {
		this.prodID = prodID;
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

	public String getPiRemark() {
		return piRemark;
	}

	public void setPiRemark(String piRemark) {
		this.piRemark = piRemark;
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

	public void setGUARDIANSHIP_FLAG(String gUARDIANSHIP_FLAG) {
		GUARDIANSHIP_FLAG = gUARDIANSHIP_FLAG;
	}

	public String getFlagNumber() {
		return flagNumber;
	}

	public void setFlagNumber(String flagNumber) {
		this.flagNumber = flagNumber;
	}

	public String getHnwcYN() {
		return hnwcYN;
	}

	public void setHnwcYN(String hnwcYN) {
		this.hnwcYN = hnwcYN;
	}

	public String getHnwcServiceYN() {
		return hnwcServiceYN;
	}

	public void setHnwcServiceYN(String hnwcServiceYN) {
		this.hnwcServiceYN = hnwcServiceYN;
	}
	
}
