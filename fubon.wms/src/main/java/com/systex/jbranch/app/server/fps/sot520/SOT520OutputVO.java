package com.systex.jbranch.app.server.fps.sot520;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT520OutputVO extends PagingOutputVO {
	
	private String tradeSEQ;
	private String pType;
	
	private List<Map<String, Object>> prodDTL;
	
	private List<Map<String, Object>> carList;	//購物車
	private List<Map<String, Object>> mainList;
	private List<String> warningCode;
	private String errorMsg;
	
	//客戶
	private String custID;
	private String custName;
	private String kycLevel;			//KYC等級
	private Date kycDueDate;		//KYC效期
	private String profInvestorYN;	//是否為專業投資人
	private String piRemark;        //專業投資人註記
	private Date piDueDate;		//專業投資人效期
	private String custRemarks;		//客戶註記
	private String outFlag;			//是否為OBU客戶
	private String isAgreeProdAdv;	//同意投資商品諮詢服務
	private Date bargainDueDate;	//期間議價效期
	private String plNotifyWays;	//停損停利通知方式
	private BigDecimal takeProfitPerc;	//停利點
	private BigDecimal stopLossPerc;	//停損點
	private Date w8benEffDate;	//W8ben有效日期
	private String  noSale;         //商品禁銷註記
	private Boolean isInterdict;    //禁治產註記
	private String  deathFlag;		//死亡戶註記
	private String  rejectProdFlag; //商品拒銷註記
	private List<Map<String, Object>> debitAcct;
	private List<Map<String, Object>> trustAcct;
	private List<Map<String, Object>> creditAcct;
	
	private String isFirstTrade;
	private String ageUnder70Flag;
	private String eduJrFlag;
	private String healthFlag;
	private Integer custAge; //客戶年齡  (FOR 未成年)
	
	private List<Map<String, Object>> dueDate;

	public List<String> getWarningCode() {
		return warningCode;
	}

	public void setWarningCode(List<String> warningCode) {
		this.warningCode = warningCode;
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

	public Boolean getIsInterdict() {
		return isInterdict;
	}

	public void setIsInterdict(Boolean isInterdict) {
		this.isInterdict = isInterdict;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
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

	public String getKycLevel() {
		return kycLevel;
	}

	public void setKycLevel(String kycLevel) {
		this.kycLevel = kycLevel;
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

	public String getCustRemarks() {
		return custRemarks;
	}

	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}

	public String getOutFlag() {
		return outFlag;
	}

	public void setOutFlag(String outFlag) {
		this.outFlag = outFlag;
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
	
	public Integer getCustAge() {
		return custAge;
	}

	public void setCustAge(Integer custAge) {
		this.custAge = custAge;
	}
	
	
}
