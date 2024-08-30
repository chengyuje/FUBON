package com.systex.jbranch.app.server.fps.sot1620;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT1620OutputVO extends PagingOutputVO {
	private List<Map<String,Object>> resultList;
	private String tradeSEQ;
//	private List<Map<String, Object>> custDTL;
//	private List<Map<String, Object>> cartList;
	private List<Map<String, Object>> prodDTL;
	private List<Map<String, Object>> feeTypeList; 		//手續費優惠方式
	private List<Map<String, Object>> carList;			//購物車  //TBSOT_NF_PURCHASE_D
	private List<Map<String, Object>> mainList;  		//TBSOT_TRADE_MAIN
	private String warningMsg;
	private String errorMsg;
	private String fitnessMessage;						//以逗號分隔錯誤碼
	
	//客戶
	private String custName;
	private String kycLV;				//KYC等級
	private Date kycDueDate;			//KYC效期
	private String profInvestorYN;		//是否為專業投資人
	private Date piDueDate;				//專業投資人效期
	private String custRemarks;			//客戶註記
	private String isOBU;				//是否為OBU客戶
	private String isAgreeProdAdv;		//同意投資商品諮詢服務
	private String piRemark;        	//專業投資人註記
	private String noSale;          	//FC032675商品禁銷註記
	private String rejectProdFlag;		//FC032675客戶拒銷註記
	private Date bargainDueDate;		//期間議價效期
	private String plNotifyWays;		//停損停利通知方式
	private BigDecimal takeProfitPerc;	//停利點
	private BigDecimal stopLossPerc;	//停損點
	private Date w8benEffDate;			//W8ben有效日期 
	private String custProType; 		// 專業投資人類型
	private List<Map<String, Object>> debitAcct;
	private List<Map<String, Object>> trustAcct;
	private List<Map<String, Object>> creditAcct;
	private String fundInfoSelling;		// N / Y:ehl_01_SOT702_016(XXXX)為本行建議售出之標的，請提醒客戶並確認其申購意願，謝謝！
	private String isFirstTrade; 		//是否首購
	private String ageUnder70Flag;		//年齡小於70
	private String eduJrFlag;     		//教育程度國中以上(不含國中)
	private String healthFlag;    		//未領有全民健保重大傷病證明
	private String feeTypeIndex;		// 手續費優惠方式Index for 檢索
	private Map brgApplySingle;		 	//CRM421事先議價設定 
	private String isBackendInCart;		//購物車中是否有後收型基金
	private String isBanker; 			//是否行員
	private boolean reserve;
	private String hnwcYN;
	private String hnwcServiceYN;
	private String flagNumber;  		//90天內是否有貸款紀錄 Y/N
	
	//FOR CBS測試日期修改
	private boolean isKycDueDateUseful;
	
	private String deathFlag;	//死亡戶
	private String isInterdict;	//禁治產
	
	public boolean isKycDueDateUseful() {
		return isKycDueDateUseful;
	}

	public void setKycDueDateUseful(boolean isKycDueDateUseful) {
		this.isKycDueDateUseful = isKycDueDateUseful;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	
	public String getTradeSEQ() {
		return tradeSEQ;
	}
	
	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
	}
	 
	public List<Map<String, Object>> getProdDTL() {
		return prodDTL;
	}
	
	public void setProdDTL(List<Map<String, Object>> prodDTL) {
		this.prodDTL = prodDTL;
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
	public Date getW8benEffDate() {
		return w8benEffDate;
	}
	public void setW8benEffDate(Date w8benEffDate) {
		this.w8benEffDate = w8benEffDate;
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
	public List<Map<String, Object>> getFeeTypeList() {
		return feeTypeList;
	}
	public void setFeeTypeList(List<Map<String, Object>> feeTypeList) {
		this.feeTypeList = feeTypeList;
	}
	public Map getBrgApplySingle() {
		return brgApplySingle;
	}
	public void setBrgApplySingle(Map brgApplySingle) {
		this.brgApplySingle = brgApplySingle;
	}
	
	public String getCustProType() {
		return custProType;
	}
	public void setCustProType(String custProType) {
		this.custProType = custProType;
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
	public String getNoSale() {
		return noSale;
	}
	public void setNoSale(String noSale) {
		this.noSale = noSale;
	}
	public String getFeeTypeIndex() {
		return feeTypeIndex;
	}
	public void setFeeTypeIndex(String feeTypeIndex) {
		this.feeTypeIndex = feeTypeIndex;
	}
	public String getRejectProdFlag() {
		return rejectProdFlag;
	}
	public void setRejectProdFlag(String rejectProdFlag) {
		this.rejectProdFlag = rejectProdFlag;
	}
	public String getIsBackendInCart() {
		return isBackendInCart;
	}
	public void setIsBackendInCart(String isBackendInCart) {
		this.isBackendInCart = isBackendInCart;
	}
	public String getIsBanker() {
		return isBanker;
	}
	public void setIsBanker(String isBanker) {
		this.isBanker = isBanker;
	}
	public boolean getReserve() {
		return reserve;
	}
	public void setReserve(Boolean reserve) {
		this.reserve = reserve;
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

	public String getFlagNumber() {
		return flagNumber;
	}

	public void setFlagNumber(String flagNumber) {
		this.flagNumber = flagNumber;
	}
	
}
