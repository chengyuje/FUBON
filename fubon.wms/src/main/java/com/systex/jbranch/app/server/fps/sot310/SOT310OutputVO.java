package com.systex.jbranch.app.server.fps.sot310;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT310OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> resultList;
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
	private String piRemark;	//專業投資人註記
	private String custRemarks;		//客戶註記
	private String isOBU;			//是否為OBU客戶
	private String isAgreeProdAdv;	//同意投資商品諮詢服務
	private Date bargainDueDate;	//期間議價效期
	private String plNotifyWays;	//停損停利通知方式
	private BigDecimal takeProfitPerc;	//停利點
	private BigDecimal stopLossPerc;	//停損點
	private Date w8benEffDate;	//W8ben有效日期
	private String w8BenEffYN;	//W8ben簽署是否有效
	private String fatcaType;
	private String custQValue;
	private boolean isCustStakeholder;
	private String custProType;	//專投種類
	
	private List<Map<String, Object>> debitAcct;
	private List<Map<String, Object>> trustAcct;
	private List<Map<String, Object>> creditAcct;
	
	private String noSale;	//禁銷客戶
	private String deathFlag;	//死亡戶
	private String isInterdict;	//禁治產
	private String rejectProdFlag; //拒銷客戶
	
	private String isFirstTrade;
	private String ageUnder70Flag;
	private String eduJrFlag;
	private String healthFlag;
	private Integer custAge; //客戶年齡  (FOR 未成年)
	
	private List<Map<String, Object>> dueDate;
	
	//取得商品資訊-海外債投組風控集中度
	private BigDecimal nvlAMT;			//前一日投資AUM
	private BigDecimal sumITEM;         //在途申購金額+庫存金額  
	private BigDecimal bondVal;			//票面價值
	private Boolean limitPrice;			//是否可限價
	
	//長效單起迄日邏輯：最短2日最長5日
	private Date minGtcStartDate;		// 長效單：委託起日在『次營業日 ～ 次營業日+19個營業日』
	private Date maxGtcStartDate;		// (次營業日～次營業日+19個營業日)
	private Date minGtcEndDate;			// 長效單：委託迄日在『次營業日+1個營業日 ～ 次營業日+19個營業日+4個營業日』
	private Date maxGtcEndDate;			// (第2個營業日～次營業日+19個營業日+4個營業日)
	
	//FOR CBS測試日期修改
	private boolean isKycDueDateUseful;
	private boolean isPiDueDateUseful; 	//FOR CBS TEST日期
	
	private String isWebSale;			//商品是否可快速申購
	private BigDecimal buyRate;			//商品計價幣別前日買匯匯率
	private String hnwcYN; 				//高資產客戶註記
	private String hnwcServiceYN; 		//可提供高資產商品或服務
	private String hnwcBuy; 			//商品限高資產客戶申購註記
	private String flagNumber; 			//90天內是否有貸款紀錄 Y/N
	
	private String sotYN;
	private String overCentRateResult; //集中度檢核結果
	
	//#2304_商品適配及申購新增KYC快到期提醒
    private boolean kycDueDateLessOneMonth; //KYC校期小於等於1個月(30天)
	
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String getIsWebSale() {
		return isWebSale;
	}

	public void setIsWebSale(String isWebSale) {
		this.isWebSale = isWebSale;
	}
	
	public BigDecimal getBuyRate() {
		return buyRate;
	}

	public void setBuyRate(BigDecimal buyRate) {
		this.buyRate = buyRate;
	}

	public boolean isPiDueDateUseful() {
		return isPiDueDateUseful;
	}

	public void setPiDueDateUseful(boolean isPiDueDateUseful) {
		this.isPiDueDateUseful = isPiDueDateUseful;
	}
	
	
	public boolean isKycDueDateUseful() {
		return isKycDueDateUseful;
	}

	public void setKycDueDateUseful(boolean isKycDueDateUseful) {
		this.isKycDueDateUseful = isKycDueDateUseful;
	}
	
	public BigDecimal getBondVal() {
		return bondVal;
	}

	public void setBondVal(BigDecimal bondVal) {
		this.bondVal = bondVal;
	}

	public String getCustProType() {
		return custProType;
	}

	public void setCustProType(String custProType) {
		this.custProType = custProType;
	}

	public boolean getIsCustStakeholder() {
		return isCustStakeholder;
	}

	public void setIsCustStakekholder(boolean isCustStakeholder) {
		this.isCustStakeholder = isCustStakeholder;
	}

	public String getCustQValue() {
		return custQValue;
	}

	public void setCustQValue(String custQValue) {
		this.custQValue = custQValue;
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

	public String getRejectProdFlag() {
		return rejectProdFlag;
	}

	public void setRejectProdFlag(String rejectProdFlag) {
		this.rejectProdFlag = rejectProdFlag;
	}

	public String getDeathFlag() {
		return deathFlag;
	}

	public void setDeathFlag(String deathFlag) {
		this.deathFlag = deathFlag;
	}

	public String getIsInterdict() {
		return isInterdict;
	}

	public void setIsInterdict(String isInterdict) {
		this.isInterdict = isInterdict;
	}

	public String getNoSale() {
		return noSale;
	}

	public void setNoSale(String noSale) {
		this.noSale = noSale;
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

	public Integer getCustAge() {
		return custAge;
	}

	public void setCustAge(Integer custAge) {
		this.custAge = custAge;
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

	public String getPiRemark() {
		return piRemark;
	}

	public void setPiRemark(String piRemark) {
		this.piRemark = piRemark;
	}

	public Date getBargainDueDate() {
		return bargainDueDate;
	}

	public void setBargainDueDate(Date bargainDueDate) {
		this.bargainDueDate = bargainDueDate;
	}
	
	public BigDecimal getNvlAMT() {
		return nvlAMT;
	}

	public void setNvlAMT(BigDecimal nvlAMT) {
		this.nvlAMT = nvlAMT;
	}

	public BigDecimal getSumITEM() {
		return sumITEM;
	}

	public void setSumITEM(BigDecimal sumITEM) {
		this.sumITEM = sumITEM;
	}

	public Boolean getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(Boolean limitPrice) {
		this.limitPrice = limitPrice;
	}

	public void setCustStakeholder(boolean isCustStakeholder) {
		this.isCustStakeholder = isCustStakeholder;
	}
	
	public Date getMinGtcStartDate() {
		return minGtcStartDate;
	}

	public void setMinGtcStartDate(Date minGtcStartDate) {
		this.minGtcStartDate = minGtcStartDate;
	}

	public Date getMaxGtcStartDate() {
		return maxGtcStartDate;
	}

	public void setMaxGtcStartDate(Date maxGtcStartDate) {
		this.maxGtcStartDate = maxGtcStartDate;
	}

	public Date getMinGtcEndDate() {
		return minGtcEndDate;
	}

	public void setMinGtcEndDate(Date minGtcEndDate) {
		this.minGtcEndDate = minGtcEndDate;
	}

	public Date getMaxGtcEndDate() {
		return maxGtcEndDate;
	}

	public void setMaxGtcEndDate(Date maxGtcEndDate) {
		this.maxGtcEndDate = maxGtcEndDate;
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

	public String getSotYN() {
		return sotYN;
	}

	public void setSotYN(String sotYN) {
		this.sotYN = sotYN;
	}

	public String getOverCentRateResult() {
		return overCentRateResult;
	}

	public void setOverCentRateResult(String overCentRateResult) {
		this.overCentRateResult = overCentRateResult;
	}

	public boolean isKycDueDateLessOneMonth() {
		return kycDueDateLessOneMonth;
	}

	public void setKycDueDateLessOneMonth(boolean kycDueDateLessOneMonth) {
		this.kycDueDateLessOneMonth = kycDueDateLessOneMonth;
	}
	
}
