package com.systex.jbranch.app.server.fps.sot150;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT150InputVO extends PagingInputVO {
	
	// 共同
	private String tradeSEQ;
	private String prodType;
	private String tradeType;
	private String stockCode;
	
	private String isFirstTrade;
	private String ageUnder70Flag;
	private String eduJrFlag;
	private String healthFlag;
	private String noSale;
	private String fatcaType;
	
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
	private String isOBU;
	private String isAgreeProdAdv;
	private Date bargainDueDate;
	private String tradeStatus;
	private String isBargainNeeded;
	private String bargainFeeFlag;
	private String isRecNeeded;
	private String recSEQ;
	private Date sendDate;
	private String custProType; // add by ocean 2016-10-27 專業投資人類型
	private String piRemark;
	private String hnwcYN;
	private String hnwcServiceYN;
	
	// 明細使用
	private BigDecimal carSEQ;
	private String tradeDateType;               //交易日期類別
	private String tradeDate;                   //交易日期 (電文需要)
	private String reservationTradeDate;        //預約交易日
	private String tradeSubType; 				//信託型態
	private String tradeSubTypeD; 				//詳細信託型態
	private String certificateID; 				//憑證編號
	private String bProdID; 					//變更前基金代號
	private String bProdName; 					//變更前基金名稱
	private String bProdCurr; 					//變更前產品計價幣別
	private String bProdRiskLV; 				//變更前產品風險等級
	private String bTrustCurr; 					//變更前產品信託幣別
	private String bTrustCurrType; 				//轉出標的信託幣別類別
	private BigDecimal bTrustAmt; 				//變更前信託金額
	private BigDecimal bPurchaseAmtL; 			//變更前每月扣款金額_低(定期不定額)
	private BigDecimal bPurchaseAmtM;			//變更前每月扣款金額_中(定期不定額)；變更前扣款金額(定期定額)
	private BigDecimal bPurchaseAmtH;			//變更前每月扣款金額_高(定期不定額)
	private BigDecimal bPurchaseAmtExchL; 		//變更前每月扣款金額換匯_低(定期不定額)
	private BigDecimal bPurchaseAmtExchM;		//變更前每月扣款金額換匯_中(定期不定額)；變更前扣款金額(定期定額)
	private BigDecimal bPurchaseAmtExchH;		//變更前每月扣款金額換匯_高(定期不定額)
	private String bExchCurr;                   //變更前每月扣款金額換匯 幣別 (換匯電文回傳)
	private String bChargeDate1; 				//變更前每月扣款日期1
	private String bChargeDate2; 				//變更前每月扣款日期2
	private String bChargeDate3; 				//變更前每月扣款日期3
	private String bChargeDate4; 				//變更前每月扣款日期4
	private String bChargeDate5; 				//變更前每月扣款日期5
	private String bChargeDate6; 				//變更前每月扣款日期6
	private String bDebitAcct; 					//變更前扣款帳號
	private String bCreditAcct; 				//變更前收益入帳帳號
	private String bTrustAcct; 					//變更前信託帳號
	private String bCertificateStatus;			//變更前憑證狀態
	
	private BigDecimal bEquivalentAmtL;         //變更前,從庫存帶入
	private BigDecimal bEquivalentAmtM;         //變更前,從庫存帶入
	private BigDecimal bEquivalentAmtH;         //變更前,從庫存帶入
	private String bNotVertify;                 //變更前標的未核備(TBPRD_FUNDINFO.FUS40)
	
	private String bChargeDateList;
	private String chargeDateList;
	
	private String prospectusType;  //當異動標的    公開說明書選項   1.已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得 2.已取得，本次毋須再交付
	private String fProdID; 					//變更後基金代號
	private String fProdName; 					//變更後基金名稱
	private String fProdCurr; 					//變更後產品計價幣別
	private String fProdRiskLV; 				//變更後產品風險等級
	private String fTrustCurr; 					//變更後產品信託幣別
	private BigDecimal fPurchaseAmtL; 			//變更後每月扣款金額_低(定期不定額)
	private BigDecimal fPurchaseAmtM;			//變更後每月扣款金額_中(定期不定額)；變更後扣款金額(定期定額)
	private BigDecimal fPurchaseAmtH;			//變更後每月扣款金額_高(定期不定額)
	private String fChargeDate1;				//變更後每月扣款日期1
	private String fChargeDate2;				//變更後每月扣款日期2
	private String fChargeDate3;				//變更後每月扣款日期3
	private String fChargeDate4;				//變更後每月扣款日期4
	private String fChargeDate5;				//變更後每月扣款日期5
	private String fChargeDate6;				//變更後每月扣款日期6
	private String fDebitAcct; 					//變更後扣款帳號
	private String fCreditAcct; 				//變更後收益入帳帳號
	private String fCertificateStatus; 			//變更後憑證狀態
	private Date fHoldStartDate; 				//變更後暫停扣款起日
	private Date fHoldEndDate; 					//變更後暫停扣款迄日
	private Date fResumeDate; 					//變更後恢復正常扣款起日
	private BigDecimal fFeeL;					//變更後每次扣款手續費_低
	private BigDecimal fFeeM;					//變更後每次扣款手續費_中
	private BigDecimal fFeeH;					//變更後每次扣款手續費_高
	
	private String narratorID;
	private String narratorName;
	private String needHnwcRiskValueYN; //需要做越級適配檢核：有變更標的、有恢復扣款、有變更金額且增加扣款金額、有變更扣款日期且有增加次數
	
	public String getFatcaType() {
		return fatcaType;
	}

	public void setFatcaType(String fatcaType) {
		this.fatcaType = fatcaType;
	}

	public String getNoSale() {
		return noSale;
	}

	public void setNoSale(String noSale) {
		this.noSale = noSale;
	}

	public BigDecimal getbEquivalentAmtL() {
		return bEquivalentAmtL;
	}

	public void setbEquivalentAmtL(BigDecimal bEquivalentAmtL) {
		this.bEquivalentAmtL = bEquivalentAmtL;
	}

	public BigDecimal getbEquivalentAmtM() {
		return bEquivalentAmtM;
	}

	public void setbEquivalentAmtM(BigDecimal bEquivalentAmtM) {
		this.bEquivalentAmtM = bEquivalentAmtM;
	}

	public BigDecimal getbEquivalentAmtH() {
		return bEquivalentAmtH;
	}

	public void setbEquivalentAmtH(BigDecimal bEquivalentAmtH) {
		this.bEquivalentAmtH = bEquivalentAmtH;
	}

	public String getbChargeDateList() {
		return bChargeDateList;
	}

	public void setbChargeDateList(String bChargeDateList) {
		this.bChargeDateList = bChargeDateList;
	}

	public String getChargeDateList() {
		return chargeDateList;
	}

	public void setChargeDateList(String chargeDateList) {
		this.chargeDateList = chargeDateList;
	}

	public String getIsFirstTrade() {
		return isFirstTrade;
	}

	public void setIsFirstTrade(String isFirstTrade) {
		this.isFirstTrade = isFirstTrade;
	}

	public String getAgeUnder70Flag() {
		return ageUnder70Flag;
	}

	public void setAgeUnder70Flag(String ageUnder70Flag) {
		this.ageUnder70Flag = ageUnder70Flag;
	}

	public String getEduJrFlag() {
		return eduJrFlag;
	}

	public void setEduJrFlag(String eduJrFlag) {
		this.eduJrFlag = eduJrFlag;
	}

	public String getHealthFlag() {
		return healthFlag;
	}

	public void setHealthFlag(String healthFlag) {
		this.healthFlag = healthFlag;
	}

	public String getCustProType() {
		return custProType;
	}

	public void setCustProType(String custProType) {
		this.custProType = custProType;
	}

	public String getPiRemark() {
		return piRemark;
	}

	public void setPiRemark(String piRemark) {
		this.piRemark = piRemark;
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

	public String getTradeSubType() {
		return tradeSubType;
	}

	public void setTradeSubType(String tradeSubType) {
		this.tradeSubType = tradeSubType;
	}

	public String getCertificateID() {
		return certificateID;
	}

	public void setCertificateID(String certificateID) {
		this.certificateID = certificateID;
	}

	public String getbProdID() {
		return bProdID;
	}

	public void setbProdID(String bProdID) {
		this.bProdID = bProdID;
	}

	public String getbProdName() {
		return bProdName;
	}

	public void setbProdName(String bProdName) {
		this.bProdName = bProdName;
	}

	public String getbProdCurr() {
		return bProdCurr;
	}

	public void setbProdCurr(String bProdCurr) {
		this.bProdCurr = bProdCurr;
	}

	public String getbProdRiskLV() {
		return bProdRiskLV;
	}

	public void setbProdRiskLV(String bProdRiskLV) {
		this.bProdRiskLV = bProdRiskLV;
	}

	public String getbTrustCurr() {
		return bTrustCurr;
	}

	public void setbTrustCurr(String bTrustCurr) {
		this.bTrustCurr = bTrustCurr;
	}

	public String getbTrustCurrType() {
		return bTrustCurrType;
	}

	public void setbTrustCurrType(String bTrustCurrType) {
		this.bTrustCurrType = bTrustCurrType;
	}

	public BigDecimal getbTrustAmt() {
		return bTrustAmt;
	}

	public void setbTrustAmt(BigDecimal bTrustAmt) {
		this.bTrustAmt = bTrustAmt;
	}

	public BigDecimal getbPurchaseAmtL() {
		return bPurchaseAmtL;
	}

	public void setbPurchaseAmtL(BigDecimal bPurchaseAmtL) {
		this.bPurchaseAmtL = bPurchaseAmtL;
	}

	public BigDecimal getbPurchaseAmtM() {
		return bPurchaseAmtM;
	}

	public void setbPurchaseAmtM(BigDecimal bPurchaseAmtM) {
		this.bPurchaseAmtM = bPurchaseAmtM;
	}

	public BigDecimal getbPurchaseAmtH() {
		return bPurchaseAmtH;
	}

	public void setbPurchaseAmtH(BigDecimal bPurchaseAmtH) {
		this.bPurchaseAmtH = bPurchaseAmtH;
	}

	public String getbChargeDate1() {
		return bChargeDate1;
	}

	public void setbChargeDate1(String bChargeDate1) {
		this.bChargeDate1 = bChargeDate1;
	}

	public String getbChargeDate2() {
		return bChargeDate2;
	}

	public void setbChargeDate2(String bChargeDate2) {
		this.bChargeDate2 = bChargeDate2;
	}

	public String getbChargeDate3() {
		return bChargeDate3;
	}

	public void setbChargeDate3(String bChargeDate3) {
		this.bChargeDate3 = bChargeDate3;
	}

	public String getbChargeDate4() {
		return bChargeDate4;
	}

	public void setbChargeDate4(String bChargeDate4) {
		this.bChargeDate4 = bChargeDate4;
	}

	public String getbChargeDate5() {
		return bChargeDate5;
	}

	public void setbChargeDate5(String bChargeDate5) {
		this.bChargeDate5 = bChargeDate5;
	}

	public String getbChargeDate6() {
		return bChargeDate6;
	}

	public void setbChargeDate6(String bChargeDate6) {
		this.bChargeDate6 = bChargeDate6;
	}

	public String getbDebitAcct() {
		return bDebitAcct;
	}

	public void setbDebitAcct(String bDebitAcct) {
		this.bDebitAcct = bDebitAcct;
	}

	public String getbCreditAcct() {
		return bCreditAcct;
	}

	public void setbCreditAcct(String bCreditAcct) {
		this.bCreditAcct = bCreditAcct;
	}

	public String getbTrustAcct() {
		return bTrustAcct;
	}

	public void setbTrustAcct(String bTrustAcct) {
		this.bTrustAcct = bTrustAcct;
	}

	public String getbCertificateStatus() {
		return bCertificateStatus;
	}

	public void setbCertificateStatus(String bCertificateStatus) {
		this.bCertificateStatus = bCertificateStatus;
	}

	public String getfProdID() {
		return fProdID;
	}

	public void setfProdID(String fProdID) {
		this.fProdID = fProdID;
	}

	public String getfProdName() {
		return fProdName;
	}

	public void setfProdName(String fProdName) {
		this.fProdName = fProdName;
	}

	public String getfProdCurr() {
		return fProdCurr;
	}

	public void setfProdCurr(String fProdCurr) {
		this.fProdCurr = fProdCurr;
	}

	public String getfProdRiskLV() {
		return fProdRiskLV;
	}

	public void setfProdRiskLV(String fProdRiskLV) {
		this.fProdRiskLV = fProdRiskLV;
	}

	public String getfTrustCurr() {
		return fTrustCurr;
	}

	public void setfTrustCurr(String fTrustCurr) {
		this.fTrustCurr = fTrustCurr;
	}

	public BigDecimal getfPurchaseAmtL() {
		return fPurchaseAmtL;
	}

	public void setfPurchaseAmtL(BigDecimal fPurchaseAmtL) {
		this.fPurchaseAmtL = fPurchaseAmtL;
	}

	public BigDecimal getfPurchaseAmtM() {
		return fPurchaseAmtM;
	}

	public void setfPurchaseAmtM(BigDecimal fPurchaseAmtM) {
		this.fPurchaseAmtM = fPurchaseAmtM;
	}

	public BigDecimal getfPurchaseAmtH() {
		return fPurchaseAmtH;
	}

	public void setfPurchaseAmtH(BigDecimal fPurchaseAmtH) {
		this.fPurchaseAmtH = fPurchaseAmtH;
	}

	public String getfChargeDate1() {
		return fChargeDate1;
	}

	public void setfChargeDate1(String fChargeDate1) {
		this.fChargeDate1 = fChargeDate1;
	}

	public String getfChargeDate2() {
		return fChargeDate2;
	}

	public void setfChargeDate2(String fChargeDate2) {
		this.fChargeDate2 = fChargeDate2;
	}

	public String getfChargeDate3() {
		return fChargeDate3;
	}

	public void setfChargeDate3(String fChargeDate3) {
		this.fChargeDate3 = fChargeDate3;
	}

	public String getfChargeDate4() {
		return fChargeDate4;
	}

	public void setfChargeDate4(String fChargeDate4) {
		this.fChargeDate4 = fChargeDate4;
	}

	public String getfChargeDate5() {
		return fChargeDate5;
	}

	public void setfChargeDate5(String fChargeDate5) {
		this.fChargeDate5 = fChargeDate5;
	}

	public String getfChargeDate6() {
		return fChargeDate6;
	}

	public void setfChargeDate6(String fChargeDate6) {
		this.fChargeDate6 = fChargeDate6;
	}

	public String getfDebitAcct() {
		return fDebitAcct;
	}

	public void setfDebitAcct(String fDebitAcct) {
		this.fDebitAcct = fDebitAcct;
	}

	public String getfCreditAcct() {
		return fCreditAcct;
	}

	public void setfCreditAcct(String fCreditAcct) {
		this.fCreditAcct = fCreditAcct;
	}

	public String getfCertificateStatus() {
		return fCertificateStatus;
	}

	public void setfCertificateStatus(String fCertificateStatus) {
		this.fCertificateStatus = fCertificateStatus;
	}

	public Date getfHoldStartDate() {
		return fHoldStartDate;
	}

	public void setfHoldStartDate(Date fHoldStartDate) {
		this.fHoldStartDate = fHoldStartDate;
	}

	public Date getfHoldEndDate() {
		return fHoldEndDate;
	}

	public void setfHoldEndDate(Date fHoldEndDate) {
		this.fHoldEndDate = fHoldEndDate;
	}

	public Date getfResumeDate() {
		return fResumeDate;
	}

	public void setfResumeDate(Date fResumeDate) {
		this.fResumeDate = fResumeDate;
	}

	public BigDecimal getfFeeL() {
		return fFeeL;
	}

	public void setfFeeL(BigDecimal fFeeL) {
		this.fFeeL = fFeeL;
	}

	public BigDecimal getfFeeM() {
		return fFeeM;
	}

	public void setfFeeM(BigDecimal fFeeM) {
		this.fFeeM = fFeeM;
	}

	public BigDecimal getfFeeH() {
		return fFeeH;
	}

	public void setfFeeH(BigDecimal fFeeH) {
		this.fFeeH = fFeeH;
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

	public String getbNotVertify() {
		return bNotVertify;
	}

	public void setbNotVertify(String bNotVertify) {
		this.bNotVertify = bNotVertify;
	}

	public String getTradeDateType() {
		return tradeDateType;
	}

	public void setTradeDateType(String tradeDateType) {
		this.tradeDateType = tradeDateType;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getProspectusType() {
		return prospectusType;
	}

	public void setProspectusType(String prospectusType) {
		this.prospectusType = prospectusType;
	}

	public BigDecimal getbPurchaseAmtExchL() {
		return bPurchaseAmtExchL;
	}

	public void setbPurchaseAmtExchL(BigDecimal bPurchaseAmtExchL) {
		this.bPurchaseAmtExchL = bPurchaseAmtExchL;
	}

	public BigDecimal getbPurchaseAmtExchM() {
		return bPurchaseAmtExchM;
	}

	public void setbPurchaseAmtExchM(BigDecimal bPurchaseAmtExchM) {
		this.bPurchaseAmtExchM = bPurchaseAmtExchM;
	}

	public BigDecimal getbPurchaseAmtExchH() {
		return bPurchaseAmtExchH;
	}

	public void setbPurchaseAmtExchH(BigDecimal bPurchaseAmtExchH) {
		this.bPurchaseAmtExchH = bPurchaseAmtExchH;
	}

	public String getbExchCurr() {
		return bExchCurr;
	}

	public void setbExchCurr(String bExchCurr) {
		this.bExchCurr = bExchCurr;
	}

	public String getReservationTradeDate() {
		return reservationTradeDate;
	}

	public void setReservationTradeDate(String reservationTradeDate) {
		this.reservationTradeDate = reservationTradeDate;
	}

	public String getTradeSubTypeD() {
		return tradeSubTypeD;
	}

	public void setTradeSubTypeD(String tradeSubTypeD) {
		this.tradeSubTypeD = tradeSubTypeD;
	}

	public String getNeedHnwcRiskValueYN() {
		return needHnwcRiskValueYN;
	}

	public void setNeedHnwcRiskValueYN(String needHnwcRiskValueYN) {
		this.needHnwcRiskValueYN = needHnwcRiskValueYN;
	}
	
}
