package com.systex.jbranch.app.server.fps.sot413;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT413InputVO extends PagingInputVO{
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
	private String recCode;					//錄音代碼
	private Date sendDate;					//下單傳送日期
	private String hnwcYN;
	private String hnwcServiceYN;
	
	//FCI明細
	private BigDecimal seqNo;				//流水號
	private String batchSeq;				//下單批號(風控才會用到)
	private String tradeSubType;			//交易類型
	private String prodName;			 	//商品名稱
	private String prodCurr;				//計價幣別
	private String prodRiskLv;				//產品風險等級
	private BigDecimal prodMinBuyAmt;		//最低申購面額
	private BigDecimal prodMinGrdAmt;		//累計申購面額
	private BigDecimal purchaseAmt;			//申購金額 / 庫存金額
	private String debitAcct;				//扣款帳號 / 贖回款入帳帳號
	private String prodAcct;				//組合式商品帳號
	private String monPeriod;				//天期
	private BigDecimal rmProfee;			//理專收益率
	private String targetCurrId;			//連結標的
	private BigDecimal strikePrice;			//履約價
	private BigDecimal ftpRate;				//FTP RATE
	private String targetName;				//連結標的中文
	private Date tradeDate;					//申購日
	private Date valueDate; 				//扣款(起息)日
	private Date spotDate;					//比價日	
	private Date expireDate;				//到期(入帳)日
	private BigDecimal intDates;			//產品天期
	private BigDecimal prdProfeeRate;		//預估年化收益率(到期比價匯率大於等於履約價)
	private BigDecimal lessProfeeRate; 		//預估年化收益率(到期比價匯率小於履約價)
	private BigDecimal prdProfeeAmt;		//到期比價匯率大於等於履約價
	private BigDecimal lessProfeeAmt; 		//到期比價匯率小於履約價
	private BigDecimal traderCharge; 		//交易員CHARGE
	private String narratorID;				//解說專員員編
	private String narratorName;			//解說專員姓名
	private String bossID;					//主管員編
	private String pro1500;					//專業自然人註記
	private String trustProCorp;
	private String authID;
	private String currCode;//幣別代碼
	private String monType;
	private String isPrintSot816;
	private String isPrintSot817;
	
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

	public String getRecCode() {
		return recCode;
	}

	public void setRecCode(String recCode) {
		this.recCode = recCode;
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
	public String getMonPeriod() {
		return monPeriod;
	}

	public void setMonPeriod(String monPeriod) {
		this.monPeriod = monPeriod;
	}

	public BigDecimal getRmProfee() {
		return rmProfee;
	}

	public void setRmProfee(BigDecimal rmProfee) {
		this.rmProfee = rmProfee;
	}

	public String getTargetCurrId() {
		return targetCurrId;
	}

	public void setTargetCurrId(String targetCurrId) {
		this.targetCurrId = targetCurrId;
	}

	public BigDecimal getStrikePrice() {
		return strikePrice;
	}

	public void setStrikePrice(BigDecimal strikePrice) {
		this.strikePrice = strikePrice;
	}

	public BigDecimal getFtpRate() {
		return ftpRate;
	}

	public void setFtpRate(BigDecimal ftpRate) {
		this.ftpRate = ftpRate;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getSpotDate() {
		return spotDate;
	}

	public void setSpotDate(Date spotDate) {
		this.spotDate = spotDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public BigDecimal getIntDates() {
		return intDates;
	}

	public void setIntDates(BigDecimal intDates) {
		this.intDates = intDates;
	}

	public BigDecimal getPrdProfeeRate() {
		return prdProfeeRate;
	}

	public void setPrdProfeeRate(BigDecimal prdProfeeRate) {
		this.prdProfeeRate = prdProfeeRate;
	}

	public BigDecimal getLessProfeeRate() {
		return lessProfeeRate;
	}

	public void setLessProfeeRate(BigDecimal lessProfeeRate) {
		this.lessProfeeRate = lessProfeeRate;
	}

	public BigDecimal getPrdProfeeAmt() {
		return prdProfeeAmt;
	}

	public void setPrdProfeeAmt(BigDecimal prdProfeeAmt) {
		this.prdProfeeAmt = prdProfeeAmt;
	}

	public BigDecimal getLessProfeeAmt() {
		return lessProfeeAmt;
	}

	public void setLessProfeeAmt(BigDecimal lessProfeeAmt) {
		this.lessProfeeAmt = lessProfeeAmt;
	}

	public BigDecimal getTraderCharge() {
		return traderCharge;
	}

	public void setTraderCharge(BigDecimal traderCharge) {
		this.traderCharge = traderCharge;
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

	public String getCurrCode() {
		return currCode;
	}

	public void setCurrCode(String currCode) {
		this.currCode = currCode;
	}

	public String getMonType() {
		return monType;
	}

	public void setMonType(String monType) {
		this.monType = monType;
	}

	public String getIsPrintSot816() {
		return isPrintSot816;
	}

	public void setIsPrintSot816(String isPrintSot816) {
		this.isPrintSot816 = isPrintSot816;
	}

	public String getIsPrintSot817() {
		return isPrintSot817;
	}

	public void setIsPrintSot817(String isPrintSot817) {
		this.isPrintSot817 = isPrintSot817;
	}
	
}
