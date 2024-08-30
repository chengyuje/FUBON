package com.systex.jbranch.app.server.fps.sot410;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT410OutputVO extends PagingOutputVO {
	// 客戶
	private String tradeSEQ; // 交易序號
	private String custID;
	private String custName;
	private String kycLv; // KYC等級
	private Date kycDueDate; // KYC效期
	private String custRemarks; // 客戶註記
	private String isOBU; // 是否為OBU客戶
	private String profInvestorYN; // 是否為專業投資人
	private String isAgreeProdAdv; // 同意投資商品諮詢服務
	private String piRemark; // 專業投資人註記
	private Date piDueDate; // 專業投資人效期
	private String trustAcct; // 信託帳號
	private String noSale; // 禁銷客戶
	private String deathFlag; // 死亡戶
	private String isInterdict; // 禁治產
	private String rejectProdFlag; // 拒銷客戶
	private String custQValue;
	private boolean isCustStakeholder;
	private String custProFlag;
	private String custStakeholder;

	private String narratorID; // 解說專員原編
	private String narratorName; // 解說專員姓名

	private List<Map<String, Object>> prodDTL; // 商品代號
	private List<Map<String, Object>> debitList;// 扣款帳號
	private List<Map<String, Object>> prodAcctList;// 組合式商品帳號
	private String tellerID;

	private List<Map<String, Object>> carList; // SI明細
	private List<Map<String, Object>> mainList; // 主檔明細

	// 非常規交易
	private String ageUnder70Flag; // 弱勢客群:70歲以上
	private String eduJrFlag; // 弱勢客群：教育程度為國中(含)以下
	private String healthFlag; // 弱勢客群：有重大傷病證明
	private String isFirstTrade; // 首次申購
	private Integer custAge; // 客戶年齡 (FOR 未成年)

	// 取得商品資訊-SN投組風控集中度
	private BigDecimal sumBDS; // 此單申購金額
	private BigDecimal sumAJS; // 此單已申購金額
	private BigDecimal nvlAMT; // 前一日投資AUM
	private BigDecimal sumITEM; // SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額

	private String fitnessMessage;// 以逗號分隔錯誤碼

	private String warningMsg;
	private String errorMsg;

	private String proCorpInv; // 專業機構投資人
	private String highYieldCorp; // 高淨值法人
	private String siProCorp; // 衍商資格專業法人
	private String pro3000; // 專業自然人提供3000萬財力證明
	private String pro1500; // 專業自然人提供1500萬財力證明
	private String trustProCorp;
	private String KYCResult;

	// FOR CBS TEST日期
	private boolean isKycDueDateUseful;
	private boolean isPiDueDateUseful; // FOR CBS TEST日期

	private String hnwcYN; //高資產客戶註記
	private String hnwcServiceYN; //可提供高資產商品或服務
	private String hnwcBuy; //商品限高資產客戶申購註記
	private String flagNumber; //90天內是否有貸款紀錄 Y/N
	
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

	public String getKYCResult() {
		return KYCResult;
	}

	public void setKYCResult(String KYCResult) {
		this.KYCResult = KYCResult;
	}

	public String getProCorpInv() {
		return proCorpInv;
	}

	public void setProCorpInv(String proCorpInv) {
		this.proCorpInv = proCorpInv;
	}

	public String getHighYieldCorp() {
		return highYieldCorp;
	}

	public void setHighYieldCorp(String highYieldCorp) {
		this.highYieldCorp = highYieldCorp;
	}

	public String getSiProCorp() {
		return siProCorp;
	}

	public void setSiProCorp(String siProCorp) {
		this.siProCorp = siProCorp;
	}

	public String getPro3000() {
		return pro3000;
	}

	public void setPro3000(String pro3000) {
		this.pro3000 = pro3000;
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

	public void setCustStakeholder(boolean isCustStakeholder) {
		this.isCustStakeholder = isCustStakeholder;
	}

	public boolean getIsCustStakeholder() {
		return isCustStakeholder;
	}

	public void setIsCustStakeholder(boolean isCustStakeholder) {
		this.isCustStakeholder = isCustStakeholder;
	}

	public String getCustProFlag() {
		return custProFlag;
	}

	public void setCustProFlag(String custProFlag) {
		this.custProFlag = custProFlag;
	}

	public String getCustQValue() {
		return custQValue;
	}

	public void setCustQValue(String custQValue) {
		this.custQValue = custQValue;
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

	public List<Map<String, Object>> getProdDTL() {
		return prodDTL;
	}

	public void setProdDTL(List<Map<String, Object>> prodDTL) {
		this.prodDTL = prodDTL;
	}

	public String getTellerID() {
		return tellerID;
	}

	public void setTellerID(String tellerID) {
		this.tellerID = tellerID;
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

	public String getTradeSEQ() {
		return tradeSEQ;
	}

	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
	}

	public String getTrustAcct() {
		return trustAcct;
	}

	public void setTrustAcct(String trustAcct) {
		this.trustAcct = trustAcct;
	}

	public String getNoSale() {
		return noSale;
	}

	public void setNoSale(String noSale) {
		this.noSale = noSale;
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

	public String getRejectProdFlag() {
		return rejectProdFlag;
	}

	public void setRejectProdFlag(String rejectProdFlag) {
		this.rejectProdFlag = rejectProdFlag;
	}

	public List<Map<String, Object>> getDebitList() {
		return debitList;
	}

	public void setDebitList(List<Map<String, Object>> debitList) {
		this.debitList = debitList;
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

	public String getIsFirstTrade() {
		return isFirstTrade;
	}

	public void setIsFirstTrade(String isFirstTrade) {
		this.isFirstTrade = isFirstTrade;
	}

	public BigDecimal getSumBDS() {
		return sumBDS;
	}

	public void setSumBDS(BigDecimal sumBDS) {
		this.sumBDS = sumBDS;
	}

	public BigDecimal getSumAJS() {
		return sumAJS;
	}

	public void setSumAJS(BigDecimal sumAJS) {
		this.sumAJS = sumAJS;
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

	public List<Map<String, Object>> getProdAcctList() {
		return prodAcctList;
	}

	public void setProdAcctList(List<Map<String, Object>> prodAcctList) {
		this.prodAcctList = prodAcctList;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getPiRemark() {
		return piRemark;
	}

	public void setPiRemark(String piRemark) {
		this.piRemark = piRemark;
	}

	public String getIsAgreeProdAdv() {
		return isAgreeProdAdv;
	}

	public void setIsAgreeProdAdv(String isAgreeProdAdv) {
		this.isAgreeProdAdv = isAgreeProdAdv;
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

	public String getFitnessMessage() {
		return fitnessMessage;
	}

	public void setFitnessMessage(String fitnessMessage) {
		this.fitnessMessage = fitnessMessage;
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

	public Integer getCustAge() {
		return custAge;
	}

	public void setCustAge(Integer custAge) {
		this.custAge = custAge;
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

	public String getCustStakeholder() {
		return custStakeholder;
	}

	public void setCustStakeholder(String custStakeholder) {
		this.custStakeholder = custStakeholder;
	}
}
