package com.systex.jbranch.app.server.fps.sot410;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT410InputVO extends PagingInputVO{
	private String prodType;				//商品類別
	private String tradeType;				//交易種類
	private String fatcaType;
	private String prodID;					//商品代號
	private String custQValue;
	private boolean isCustStakeholder;
	
	// 主檔使用
	private String tradeSEQ;				//交易序號
	private String custID;					//客戶ID
	private String custName;				//客戶姓名
	private String kycLv;					//kyc等級
	private Date kycDueDate;				//kcy效期
	private String profInvestorYN;			//是否為專業投資人
	private Date piDueDate;					//專業投資人效期
	private String custRemarks;				//客戶註記
	private String isOBU;					//是否為OBU客戶
	private String isagreeProdAdv;			//同意投資商品諮詢服務
	private String piRemark; //專業投資人註記
	private Date bagainDueDate;				//期間議價效期
	private String tradeStatus;				//交易狀態
	private String isBargainNeeded;			//是否需要議價
//	private String bargainFeeFlag;			//議價狀態
	private String isRecNeeded;				//是否需要錄音
	private String recSEQ;					//錄音序號
	private Date sendDate;					//下單傳送日期
	private String hnwcYN;
	private String hnwcServiceYN;
	
	//SI明細
	private BigDecimal seqNo;				//流水號
	private String batchSeq;				//下單批號(風控才會用到)
	private String tradeSubType;			//交易類型
	private String prodName;			 	//商品名稱
	private String prodCurr;				//計價幣別
	private String prodRiskLv;				//產品風險等級
	private BigDecimal prodMinBuyAmt;		//最低申購面額
	private BigDecimal prodMinGrdAmt;		//累計申購面額
	private BigDecimal purchaseAmt;			//申購金額 / 庫存金額
	private BigDecimal refVal;				//參考報價
	private Date refValDate;				//參考報價日期
	private String debitAcct;				//扣款帳號 / 贖回款入帳帳號
	private String prodAcct;				//組合式商品帳號
	private Date tradeDate;					//交易日期
	private String narratorID;				//解說專員員編
	private String narratorName;			//解說專員姓名
	private String bossID;					//主管員編
	private String pro1500;					//專業自然人註記
	private String trustProCorp;
	private String authID;
	private String hnwcBuy; 			//商品限高資產客戶申購註記
	private String overCentRateYN;
	private String flagNumber; 				//90天內是否有貸款紀錄 Y/N
	
	public String getCustQValue() {
		return custQValue;
	}

	public void setCustQValue(String custQValue) {
		this.custQValue = custQValue;
	}

	public boolean getIsCustStakeholder() {
		return isCustStakeholder;
	}

	public void setIsCustStakeholder(boolean isCustStakeholder) {
		this.isCustStakeholder = isCustStakeholder;
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
	public String getProdID() {
		return prodID;
	}
	public void setProdID(String prodID) {
		this.prodID = prodID;
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
	public String getKycLv() {
		return kycLv;
	}
	public void setKycLv(String kycLv) {
		this.kycLv = kycLv;
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
	public String getIsOBU() {
		return isOBU;
	} 
	public void setIsOBU(String isOBU) {
		this.isOBU = isOBU;
	}

	public String getIsagreeProdAdv() {
		return isagreeProdAdv;
	}
	public void setIsagreeProdAdv(String isagreeProdAdv) {
		this.isagreeProdAdv = isagreeProdAdv;
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
//	public String getBargainFeeFlag() {
//		return bargainFeeFlag;
//	}
//	public void setBargainFeeFlag(String bargainFeeFlag) {
//		this.bargainFeeFlag = bargainFeeFlag;
//	}
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
	public String getNarratorID() {
		return narratorID;
	}
	public void setNarratorID(String narratorID) {
		this.narratorID = narratorID;
	}
	public String getTradeSEQ() {
		return tradeSEQ;
	}
	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
	}
	public Date getBagainDueDate() {
		return bagainDueDate;
	}
	public void setBagainDueDate(Date bagainDueDate) {
		this.bagainDueDate = bagainDueDate;
	}
	public BigDecimal getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(BigDecimal seqNo) {
		this.seqNo = seqNo;
	}
	public String getBatchSeq() {
		return batchSeq;
	}
	public void setBatchSeq(String batchSeq) {
		this.batchSeq = batchSeq;
	}
	public String getTradeSubType() {
		return tradeSubType;
	}
	public void setTradeSubType(String tradeSubType) {
		this.tradeSubType = tradeSubType;
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
	public String getProdRiskLv() {
		return prodRiskLv;
	}
	public void setProdRiskLv(String prodRiskLv) {
		this.prodRiskLv = prodRiskLv;
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

	public BigDecimal getPurchaseAmt() {
		return purchaseAmt;
	}
	public void setPurchaseAmt(BigDecimal purchaseAmt) {
		this.purchaseAmt = purchaseAmt;
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
	public String getDebitAcct() {
		return debitAcct;
	}
	public void setDebitAcct(String debitAcct) {
		this.debitAcct = debitAcct;
	}
	public String getProdAcct() {
		return prodAcct;
	}
	public void setProdAcct(String prodAcct) {
		this.prodAcct = prodAcct;
	}
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getNarratorName() {
		return narratorName;
	}
	public void setNarratorName(String narratorName) {
		this.narratorName = narratorName;
	}
	public String getFatcaType() {
		return fatcaType;
	}
	public void setFatcaType(String fatcaType) {
		this.fatcaType = fatcaType;
	}

	public String getPiRemark() {
		return piRemark;
	}

	public void setPiRemark(String piRemark) {
		this.piRemark = piRemark;
	}

	public String getBossID() {
		return bossID;
	}

	public void setBossID(String bossID) {
		this.bossID = bossID;
	}

	public String getPro1500() {
		return pro1500;
	}

	public void setPro1500(String pro1500) {
		this.pro1500 = pro1500;
	}

	public String getTrustProCorp() {
		return trustProCorp;
	}

	public void setTrustProCorp(String trustProCorp) {
		this.trustProCorp = trustProCorp;
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

	public String getAuthID() {
		return authID;
	}

	public void setAuthID(String authID) {
		this.authID = authID;
	}

	public String getOverCentRateYN() {
		return overCentRateYN;
	}

	public void setOverCentRateYN(String overCentRateYN) {
		this.overCentRateYN = overCentRateYN;
	}

	public String getHnwcBuy() {
		return hnwcBuy;
	}

	public void setHnwcBuy(String hnwcBuy) {
		this.hnwcBuy = hnwcBuy;
	}

	public String getFlagNumber() {
		return flagNumber;
	}

	public void setFlagNumber(String flagNumber) {
		this.flagNumber = flagNumber;
	}
	
}
