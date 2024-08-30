package com.systex.jbranch.app.server.fps.sot140;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author efun
 *
 */
public class SOT140OutputVO extends PagingOutputVO {
	
 
	private List<Map<String, Object>> carList;	//購物車
	private List<Map<String, Object>> mainList;
	
	private String tradeSEQ;		//交易序號
	private String custName;
	private String kycLevel;		//KYC等級
	private Date kycDueDate;		//KYC效期
	private String custRemarks;		//客戶註記
	private String isOBU;			//是否為OBU客戶
	private String isAgreeProdAdv;	//同意投資商品諮詢服務註記
	private String piRemark;        //專業投資人註記
	private Date piDueDate;		//專業投資人效期
	private String hnwcYN; //高資產客戶註記
	private String hnwcServiceYN; //可提供高資產商品或服務
	
	//適配
	private List<Map<String, Object>> prodDTL;
	private String warningMsg;
	private String errorMsg;
	private String profInvestorYN;
	private String fatcaType; 
	private String custProType;
	private String fitnessMessage;//以逗號分隔錯誤碼
	private String fundInfoSelling;// N / Y:ehl_01_SOT702_016(XXXX)為本行建議售出之標的，請提醒客戶並確認其申購意願，謝謝！
	
	private String isFirstTrade;   //是否首購
	private String ageUnder70Flag; //年齡小於70
	private String eduJrFlag;      //教育程度國中以上(不含國中)
	private String healthFlag;     //未領有全民健保重大傷病證明
	
	/*
	 * 判斷是否為短期交易
	 * add by Brian
	 */
	private String SHORT_1; 
	private String SHORT_2; 
	
	private List<Map<String, Object>> debitAcct;	//手續費扣款帳號
	
	//FOR CBS測試日期修改
	private boolean isKycDueDateUseful;
	
	private boolean sameCDSCMonth;
		
	public boolean isSameCDSCMonth() {
		return sameCDSCMonth;
	}

	public void setSameCDSCMonth(boolean sameCDSCMonth) {
		this.sameCDSCMonth = sameCDSCMonth;
	}

	public boolean isKycDueDateUseful() {
		return isKycDueDateUseful;
	}

	public void setKycDueDateUseful(boolean isKycDueDateUseful) {
		this.isKycDueDateUseful = isKycDueDateUseful;
	}
	
	
	public String getTradeSEQ() {
		return tradeSEQ;
	}
	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
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
	public Date getKycDueDate() {
		return kycDueDate;
	}
	public void setKycDueDate(Date kycDueDate) {
		this.kycDueDate = kycDueDate;
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
	public List<Map<String, Object>> getDebitAcct() {
		return debitAcct;
	}
	public void setDebitAcct(List<Map<String, Object>> debitAcct) {
		this.debitAcct = debitAcct;
	}
	public List<Map<String, Object>> getProdDTL() {
		return prodDTL;
	}
	public void setProdDTL(List<Map<String, Object>> prodDTL) {
		this.prodDTL = prodDTL;
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
	public String getProfInvestorYN() {
		return profInvestorYN;
	}
	public void setProfInvestorYN(String profInvestorYN) {
		this.profInvestorYN = profInvestorYN;
	}
	public String getFatcaType() {
		return fatcaType;
	}
	public void setFatcaType(String fatcaType) {
		this.fatcaType = fatcaType;
	}
	public String getAgeUnder70Flag() {
		return ageUnder70Flag;
	}
	public void setAgeUnder70Flag(String ageUnder70Flag) {
		this.ageUnder70Flag = ageUnder70Flag;
	}
	public String getCustProType() {
		return custProType;
	}
	public void setCustProType(String custProType) {
		this.custProType = custProType;
	}
	public String getIsFirstTrade() {
		return isFirstTrade;
	}
	public void setIsFirstTrade(String isFirstTrade) {
		this.isFirstTrade = isFirstTrade;
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
	public List<Map<String, Object>> getCarList() {
		return carList;
	}
	public void setCarList(List<Map<String, Object>> carList) {
		this.carList = carList;
	}
	public List<Map<String, Object>> getMainList() {
		return mainList;
	}
	public void setMainList(List<Map<String, Object>> mainList) {
		this.mainList = mainList;
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
	public String getSHORT_1() {
		return SHORT_1;
	}
	public void setSHORT_1(String sHORT_1) {
		SHORT_1 = sHORT_1;
	}
	public String getSHORT_2() {
		return SHORT_2;
	}
	public void setSHORT_2(String sHORT_2) {
		SHORT_2 = sHORT_2;
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
