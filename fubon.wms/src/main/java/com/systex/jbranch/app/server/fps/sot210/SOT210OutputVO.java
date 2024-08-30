package com.systex.jbranch.app.server.fps.sot210;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT210OutputVO extends PagingOutputVO {
	
	private String tradeSEQ;
	private String pType;
	
	private List<Map<String, Object>> prodDTL;
	
	private List<Map<String, Object>> carList;	//購物車
	private List<Map<String, Object>> mainList;
	
	private String warningMsg;
	private String errorMsg;
	
	//客戶
	private String custName;
	private String kycLV;			//KYC等級
	private Date kycDueDate;		//KYC效期
	private String profInvestorYN;	//是否為專業投資人
	private Date piDueDate;		//專業投資人效期
	private String custRemarks;		//客戶註記
	private String piRemark;		//專投註記
	private String isOBU;			//是否為OBU客戶
	private String isAgreeProdAdv;	//同意投資商品諮詢服務
	private Date bargainDueDate;	//期間議價效期
	private String plNotifyWays;	//停損停利通知方式
	private BigDecimal takeProfitPerc;	//停利點
	private BigDecimal stopLossPerc;	//停損點
	private Date w8benEffDate;	//W8ben有效日期
	private String w8BenEffYN;	//W8ben簽署是否有效
	private Date w8benStartDate;	//W8ben簽署起日
	private Date w8benEndDate;	//W8ben簽署迄日
	private String fatcaType;
	private String custProType;	//專投種類
	private String investType;	//特定客戶
	private String investDue;	//特定客戶申請日距今是否滿兩週
	private String noSale;          //FC032675商品禁銷註記
	private String rejectProdFlag;	//FC032675客戶拒銷註記
	private String isInterdict;    //禁治產註記
	private String deathFlag;		//死亡戶註記
	
	private List<Map<String, Object>> debitAcct;
	private List<Map<String, Object>> trustAcct;
	private List<Map<String, Object>> creditAcct;
	
	private String isFirstTrade;
	private String ageUnder70Flag;
	private String eduJrFlag;
	private String healthFlag;
	
	private List<Map<String, Object>> dueDate;			//getTradeDueDate() 取dueDateList
	private List<Map<String, Object>> entrustTypeList;  //交易指示到期日可點選範圍
	private boolean recNeeded;
	
	private Integer custAge; //客戶年齡  (FOR 未成年)
	private String signRiskYn;	//已簽署風險預告書
	private String custStakeholder;	//客戶利害關係人註記
	
	private Boolean earnAcctError = false;	//ETF 客戶首購收益帳號不同錯誤
	private String transEarnAcct;	//ETF 電文提供收益帳號
	
	//FOR CBS測試日期修改
	private boolean isKycDueDateUseful;
	
	private String flagNumber; 		//90天內是否有貸款紀錄 Y/N
	
	private String hnwcYN; //高資產客戶註記
	private String hnwcServiceYN; //可提供高資產商品或服務
	
	public boolean isKycDueDateUseful() {
		return isKycDueDateUseful;
	}

	public void setKycDueDateUseful(boolean isKycDueDateUseful) {
		this.isKycDueDateUseful = isKycDueDateUseful;
	}
	
	public Date getW8benStartDate() {
		return w8benStartDate;
	}

	public void setW8benStartDate(Date w8benStartDate) {
		this.w8benStartDate = w8benStartDate;
	}

	public Date getW8benEndDate() {
		return w8benEndDate;
	}

	public void setW8benEndDate(Date w8benEndDate) {
		this.w8benEndDate = w8benEndDate;
	}

	public boolean getRecNeeded() {
		return recNeeded;
	}

	public void setRecNeeded(boolean recNeeded) {
		this.recNeeded = recNeeded;
	}

	public List<Map<String, Object>> getEntrustTypeList() {
		return entrustTypeList;
	}

	public void setEntrustTypeList(List<Map<String, Object>> entrustTypeList) {
		this.entrustTypeList = entrustTypeList;
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

	public String getW8BenEffYN() {
		return w8BenEffYN;
	}

	public void setW8BenEffYN(String w8BenEffYN) {
		this.w8BenEffYN = w8BenEffYN;
	}

	public String getFatcaType() {
		return fatcaType;
	}

	public void setFatcaType(String fatcaType) {
		this.fatcaType = fatcaType;
	}

	public Date getW8benEffDate() {
		return w8benEffDate;
	}

	public void setW8benEffDate(Date w8benEffDate) {
		this.w8benEffDate = w8benEffDate;
	}

	public List<Map<String, Object>> getDueDate() {
		return dueDate;
	}

	public void setDueDate(List<Map<String, Object>> dueDate) {
		this.dueDate = dueDate;
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

	public List<Map<String, Object>> getDebitAcct() {
		return debitAcct;
	}

	public void setDebitAcct(List<Map<String, Object>> debitAcct) {
		this.debitAcct = debitAcct;
	}

	public List<Map<String, Object>> getTrustAcct() {
		return trustAcct;
	}

	public void setTrustAcct(List<Map<String, Object>> trustAcct) {
		this.trustAcct = trustAcct;
	}

	public List<Map<String, Object>> getCreditAcct() {
		return creditAcct;
	}

	public void setCreditAcct(List<Map<String, Object>> creditAcct) {
		this.creditAcct = creditAcct;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getKycLV() {
		return kycLV;
	}

	public void setKycLV(String kycLV) {
		this.kycLV = kycLV;
	}

	public String getProfInvestorYN() {
		return profInvestorYN;
	}

	public void setProfInvestorYN(String profInvestorYN) {
		this.profInvestorYN = profInvestorYN;
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

	public String getPlNotifyWays() {
		return plNotifyWays;
	}

	public void setPlNotifyWays(String plNotifyWays) {
		this.plNotifyWays = plNotifyWays;
	}

	public BigDecimal getTakeProfitPerc() {
		return takeProfitPerc;
	}

	public void setTakeProfitPerc(BigDecimal takeProfitPerc) {
		this.takeProfitPerc = takeProfitPerc;
	}

	public BigDecimal getStopLossPerc() {
		return stopLossPerc;
	}

	public void setStopLossPerc(BigDecimal stopLossPerc) {
		this.stopLossPerc = stopLossPerc;
	}

	public String getWarningMsg() {
		return warningMsg;
	}

	public void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<Map<String, Object>> getMainList() {
		return mainList;
	}

	public void setMainList(List<Map<String, Object>> mainList) {
		this.mainList = mainList;
	}

	public String getTradeSEQ() {
		return tradeSEQ;
	}
	
	public List<Map<String, Object>> getCarList() {
		return carList;
	}

	public void setCarList(List<Map<String, Object>> carList) {
		this.carList = carList;
	}

	public String getpType() {
		return pType;
	}

	public void setpType(String pType) {
		this.pType = pType;
	}

	public List<Map<String, Object>> getProdDTL() {
		return prodDTL;
	}
	
	public void setProdDTL(List<Map<String, Object>> prodDTL) {
		this.prodDTL = prodDTL;
	}
	
	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
	}

	public Date getKycDueDate() {
		return kycDueDate;
	}

	public void setKycDueDate(Date kycDueDate) {
		this.kycDueDate = kycDueDate;
	}

	public Date getPiDueDate() {
		return piDueDate;
	}

	public void setPiDueDate(Date piDueDate) {
		this.piDueDate = piDueDate;
	}

	public Date getBargainDueDate() {
		return bargainDueDate;
	}

	public void setBargainDueDate(Date bargainDueDate) {
		this.bargainDueDate = bargainDueDate;
	}

	public String getNoSale() {
		return noSale;
	}

	public void setNoSale(String noSale) {
		this.noSale = noSale;
	}

	public String getRejectProdFlag() {
		return rejectProdFlag;
	}

	public void setRejectProdFlag(String rejectProdFlag) {
		this.rejectProdFlag = rejectProdFlag;
	}

	public Integer getCustAge() {
		return custAge;
	}

	public void setCustAge(Integer custAge) {
		this.custAge = custAge;
	}

	public String getSignRiskYn() {
		return signRiskYn;
	}

	public void setSignRiskYn(String signRiskYn) {
		this.signRiskYn = signRiskYn;
	}

	public String getCustStakeholder() {
		return custStakeholder;
	}

	public void setCustStakeholder(String custStakeholder) {
		this.custStakeholder = custStakeholder;
	}

	public Boolean getEarnAcctError() {
		return earnAcctError;
	}

	public void setEarnAcctError(Boolean earnAcctError) {
		this.earnAcctError = earnAcctError;
	}

	public String getTransEarnAcct() {
		return transEarnAcct;
	}

	public void setTransEarnAcct(String transEarnAcct) {
		this.transEarnAcct = transEarnAcct;
	}
	
	public String getIsInterdict() {
		return isInterdict;
	}

	public void setIsInterdict(String isInterdict) {
		this.isInterdict = isInterdict;
	}

	public String getDeathFlag() {
		return deathFlag;
	}

	public void setDeathFlag(String deathFlag) {
		this.deathFlag = deathFlag;
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
