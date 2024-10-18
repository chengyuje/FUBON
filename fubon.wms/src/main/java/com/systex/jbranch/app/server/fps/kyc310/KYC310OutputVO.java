package com.systex.jbranch.app.server.fps.kyc310;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC310OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> questionnaireList;
	private List<Map<String, Object>> lastRiskLevel;
	private List<Map<String, Object>> personalIformationList;
	private List<Map<String, Object>> last_firs_ans;
	private Map<String, Map<String, Object>> Comparison;
	private Boolean checkdoquestion;
	private boolean checkCOM_Experience;
	private boolean checkNewCust;
	private boolean deletetwo;
	private String doneKYC;
	private String degrade;		//ＫＹＣ免降等註記(Y/N)
	private Date degradeDate;	//ＫＹＣ免降等註到期日
	private String ansSeqQ10;
	private String ansSeqQ11;
	private String ansSeqQ12;
	
	private String Q3ProdDecrease;	//Q3投資商品上次有本次沒有
	private List<Map<String, Object>> Q3ProdExpChgList;	//Q3投資年期上升2級以上
	private String CRR_MATRIX;	//降等機制矩陣取得的風險屬性
	private String RISK_LOSS_LEVEL;	//可承受風險損失率C值
	
	private Boolean isPSR; // #437 客戶已填過「衍生性金融商品客戶適性評估問卷」
	
	private String seq;
	private Date EXPIRY_DATE;
	
	private String custEduHighSchool;	//高中職以上學歷註記
	
	//高資產客戶註記 High Net Worth Client
	private Date hnwcDueDate; //到期日
	private Date hnwcInvaidDate; //註銷日
	private String hnwcValidYN; //是否為有效高資產客戶
	
	private List<Map<String, Object>> eduList;//學歷參數
	private List<Map<String, Object>> crrList;//職業參數
	private List<Map<String, Object>> marList;//婚姻參數
	private List<Map<String, Object>> chlList;//子女數參數
	private List<Map<String, Object>> heaList;//重大傷病參數
	private List<Map<String, Object>> q3ProdTypeList;//第三題商品類別
	private List<Map<String, Object>> q3ProdExpList;//第三題商品經驗年期
	private List<Map<String, Object>> eduChangeList;//學歷變更原因
	private List<Map<String, Object>> healthChangeList;//重大傷病變更原因
	private List<Map<String, Object>> emailChangeList;//EMAIL變更原因
	
	//#0777
	private boolean haveSameEmail;
	private List<Map<String, Object>> sameEmailData;
	private String legalRegKycLevel; //法定代理人1&2風險屬性取孰低(已過期表示沒有風險屬性，都沒有則為空值)
	
	private Map<String, Object> lastKYCComparisonData;
	private BigDecimal incomeFromCBS;
	
	public String getQ3ProdDecrease() {
		return Q3ProdDecrease;
	}

	public void setQ3ProdDecrease(String q3ProdDecrease) {
		Q3ProdDecrease = q3ProdDecrease;
	}

	public List<Map<String, Object>> getQ3ProdExpChgList() {
		return Q3ProdExpChgList;
	}

	public void setQ3ProdExpChgList(List<Map<String, Object>> q3ProdExpChgList) {
		Q3ProdExpChgList = q3ProdExpChgList;
	}

	public boolean isCheckCOM_Experience() {
		return checkCOM_Experience;
	}

	public void setCheckCOM_Experience(boolean checkCOM_Experience) {
		this.checkCOM_Experience = checkCOM_Experience;
	}

	public boolean isDeletetwo() {
		return deletetwo;
	}

	public void setDeletetwo(boolean deletetwo) {
		this.deletetwo = deletetwo;
	}
	
	public String doneKYC() {
		return doneKYC;
	}

	public void setDoneKYC(String doneKYC) {
		this.doneKYC = doneKYC;
	}
	
	public boolean isCheckNewCust() {
		return checkNewCust;
	}

	public void setCheckNewCust(boolean checkNewCust) {
		this.checkNewCust = checkNewCust;
	}

	public Boolean getCheckdoquestion() {
		return checkdoquestion;
	}

	public void setCheckdoquestion(Boolean checkdoquestion) {
		this.checkdoquestion = checkdoquestion;
	}

	public Map<String, Map<String, Object>> getComparison() {
		return Comparison;
	}

	public void setComparison(Map<String, Map<String, Object>> comparison) {
		Comparison = comparison;
	}

	public List<Map<String, Object>> getLast_firs_ans() {
		return last_firs_ans;
	}

	public void setLast_firs_ans(List<Map<String, Object>> last_firs_ans) {
		this.last_firs_ans = last_firs_ans;
	}

	public List<Map<String, Object>> getLastRiskLevel() {
		return lastRiskLevel;
	}

	public void setLastRiskLevel(List<Map<String, Object>> lastRiskLevel) {
		this.lastRiskLevel = lastRiskLevel;
	}

	public List<Map<String, Object>> getPersonalIformationList() {
		return personalIformationList;
	}

	public void setPersonalIformationList(
			List<Map<String, Object>> personalIformationList) {
		this.personalIformationList = personalIformationList;
	}

	public List<Map<String, Object>> getQuestionnaireList() {
		return questionnaireList;
	}

	public void setQuestionnaireList(List<Map<String, Object>> questionnaireList) {
		this.questionnaireList = questionnaireList;
	}

	public String isDegrade() {
		return degrade;
	}

	public void setDegrade(String degrade) {
		this.degrade = degrade;
	}
	
	public Date getDegradeDate() {
		return degradeDate;
	}

	public void setDegradeDate(Date degradeDate) {
		this.degradeDate = degradeDate;
	}

	public String getCRR_MATRIX() {
		return CRR_MATRIX;
	}

	public void setCRR_MATRIX(String cRR_MATRIX) {
		CRR_MATRIX = cRR_MATRIX;
	}

	public String getRISK_LOSS_LEVEL() {
		return RISK_LOSS_LEVEL;
	}

	public void setRISK_LOSS_LEVEL(String rISK_LOSS_LEVEL) {
		RISK_LOSS_LEVEL = rISK_LOSS_LEVEL;
	}

	public String getAnsSeqQ10() {
		return ansSeqQ10;
	}

	public void setAnsSeqQ10(String ansSeqQ10) {
		this.ansSeqQ10 = ansSeqQ10;
	}

	public String getAnsSeqQ11() {
		return ansSeqQ11;
	}

	public void setAnsSeqQ11(String ansSeqQ11) {
		this.ansSeqQ11 = ansSeqQ11;
	}

	public String getAnsSeqQ12() {
		return ansSeqQ12;
	}

	public void setAnsSeqQ12(String ansSeqQ12) {
		this.ansSeqQ12 = ansSeqQ12;
	}

	public Boolean getIsPSR() {
		return isPSR;
	}

	public void setIsPSR(Boolean isPSR) {
		this.isPSR = isPSR;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public Date getEXPIRY_DATE() {
		return EXPIRY_DATE;
	}

	public void setEXPIRY_DATE(Date eXPIRY_DATE) {
		EXPIRY_DATE = eXPIRY_DATE;
	}

	public String getCustEduHighSchool() {
		return custEduHighSchool;
	}

	public void setCustEduHighSchool(String custEduHighSchool) {
		this.custEduHighSchool = custEduHighSchool;
	}

	public boolean isHaveSameEmail() {
		return haveSameEmail;
	}

	public void setHaveSameEmail(boolean haveSameEmail) {
		this.haveSameEmail = haveSameEmail;
	}

	public List<Map<String, Object>> getSameEmailData() {
		return sameEmailData;
	}

	public void setSameEmailData(List<Map<String, Object>> sameEmailData) {
		this.sameEmailData = sameEmailData;
	}

	public List<Map<String, Object>> getEduList() {
		return eduList;
	}

	public void setEduList(List<Map<String, Object>> eduList) {
		this.eduList = eduList;
	}

	public List<Map<String, Object>> getCrrList() {
		return crrList;
	}

	public void setCrrList(List<Map<String, Object>> crrList) {
		this.crrList = crrList;
	}

	public List<Map<String, Object>> getMarList() {
		return marList;
	}

	public void setMarList(List<Map<String, Object>> marList) {
		this.marList = marList;
	}

	public List<Map<String, Object>> getChlList() {
		return chlList;
	}

	public void setChlList(List<Map<String, Object>> chlList) {
		this.chlList = chlList;
	}

	public List<Map<String, Object>> getHeaList() {
		return heaList;
	}

	public void setHeaList(List<Map<String, Object>> heaList) {
		this.heaList = heaList;
	}

	public List<Map<String, Object>> getQ3ProdTypeList() {
		return q3ProdTypeList;
	}

	public void setQ3ProdTypeList(List<Map<String, Object>> q3ProdTypeList) {
		this.q3ProdTypeList = q3ProdTypeList;
	}

	public List<Map<String, Object>> getQ3ProdExpList() {
		return q3ProdExpList;
	}

	public void setQ3ProdExpList(List<Map<String, Object>> q3ProdExpList) {
		this.q3ProdExpList = q3ProdExpList;
	}

	public List<Map<String, Object>> getEduChangeList() {
		return eduChangeList;
	}

	public void setEduChangeList(List<Map<String, Object>> eduChangeList) {
		this.eduChangeList = eduChangeList;
	}

	public List<Map<String, Object>> getHealthChangeList() {
		return healthChangeList;
	}

	public void setHealthChangeList(List<Map<String, Object>> healthChangeList) {
		this.healthChangeList = healthChangeList;
	}

	public List<Map<String, Object>> getEmailChangeList() {
		return emailChangeList;
	}

	public void setEmailChangeList(List<Map<String, Object>> emailChangeList) {
		this.emailChangeList = emailChangeList;
	}

	public Date getHnwcDueDate() {
		return hnwcDueDate;
	}

	public void setHnwcDueDate(Date hnwcDueDate) {
		this.hnwcDueDate = hnwcDueDate;
	}

	public Date getHnwcInvaidDate() {
		return hnwcInvaidDate;
	}

	public void setHnwcInvaidDate(Date hnwcInvaidDate) {
		this.hnwcInvaidDate = hnwcInvaidDate;
	}

	public String getHnwcValidYN() {
		return hnwcValidYN;
	}

	public void setHnwcValidYN(String hnwcValidYN) {
		this.hnwcValidYN = hnwcValidYN;
	}

	public String getLegalRegKycLevel() {
		return legalRegKycLevel;
	}

	public void setLegalRegKycLevel(String legalRegKycLevel) {
		this.legalRegKycLevel = legalRegKycLevel;
	}

	public Map<String, Object> getLastKYCComparisonData() {
		return lastKYCComparisonData;
	}

	public void setLastKYCComparisonData(Map<String, Object> lastKYCComparisonData) {
		this.lastKYCComparisonData = lastKYCComparisonData;
	}

	public BigDecimal getIncomeFromCBS() {
		return incomeFromCBS;
	}

	public void setIncomeFromCBS(BigDecimal incomeFromCBS) {
		this.incomeFromCBS = incomeFromCBS;
	}

}
