package com.systex.jbranch.app.server.fps.sot150;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT150OutputVO extends PagingOutputVO {
	
	private String tradeSEQ;
//	private String pType;
	
	private List<Map<String, Object>> prodDTL;
	
	private List<Map<String, Object>> carList;	//購物車
	private List<Map<String, Object>> mainList;
	
	private String errorMsg;
	private String warningMsg;
	
	//客戶
	private String custName;
	private String kycLV;			//KYC等級
	private Date kycDueDate;		//KYC效期
	private String profInvestorYN;	//是否為專業投資人
	private Date piDueDate;		//專業投資人效期
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
	private String custProType;
	private String noSale;
	private String piRemark;
	private String hnwcYN; //高資產客戶註記
	private String hnwcServiceYN; //可提供高資產商品或服務
	
	private List<Map<String, Object>> debitAcct;
	private List<Map<String, Object>> trustAcct;
	private List<Map<String, Object>> creditAcct;
	
	private String isFirstTrade;
	private String ageUnder70Flag;
	private String eduJrFlag;
	private String healthFlag;
	
	private String fitnessMessage;//以逗號分隔錯誤碼
	private String fundInfoSelling;// N / Y:ehl_01_SOT702_016(XXXX)為本行建議售出之標的，請提醒客戶並確認其申購意願，謝謝！
//	private List<Map<String, Object>> dueDate;
	private String nfs100YN; //是否為新興市場之非投資等級債券型基金

	
	public String getW8BenEffYN() {
		return w8BenEffYN;
	}

	public String getWarningMsg() {
		return warningMsg;
	}

	public void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}

	public String getNoSale() {
		return noSale;
	}

	public void setNoSale(String noSale) {
		this.noSale = noSale;
	}

	public String getCustProType() {
		return custProType;
	}

	public void setCustProType(String custProType) {
		this.custProType = custProType;
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

//	public List<Map<String, Object>> getDueDate() {
//		return dueDate;
//	}
//
//	public void setDueDate(List<Map<String, Object>> dueDate) {
//		this.dueDate = dueDate;
//	}

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

//	public String getpType() {
//		return pType;
//	}
//
//	public void setpType(String pType) {
//		this.pType = pType;
//	}

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

	public String getFitnessMessage() {
		return fitnessMessage;
	}

	public void setFitnessMessage(String fitnessMessage) {
		this.fitnessMessage = fitnessMessage;
	}

	public String getFundInfoSelling() {
		return fundInfoSelling;
	}

	public void setFundInfoSelling(String fundInfoSelling) {
		this.fundInfoSelling = fundInfoSelling;
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

	public String getNfs100YN() {
		return nfs100YN;
	}

	public void setNfs100YN(String nfs100yn) {
		nfs100YN = nfs100yn;
	}
	
	
}
