package com.systex.jbranch.app.server.fps.kyc311;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC311InputVO extends PagingInputVO{
	
	private String CUST_ID;
	private String DAY;
	private String cust_name;
	private Date test_date;
	private String SEQ;
	private String NIGHT;
	private String TEL_NO;
	private String FAX;
	private String birthday;//出生日期
	private String GENDER;//性別
	private String EDUCATION_Item;//教育程度選項
	private String EDUCATION;//教育程度
	private String CAREER;//職業
	private String MARRAGE;//婚姻
	private String CHILD_NO_Item;//子女數選項
	private String CHILD_NO;//子女數
	private String CUST_ADDR_1;//通訊地址
	private String EMAIL_ADDR;//E-mail
	private String CUST_RISK_AFR;//風險等級
	private String RISK_TYPE;//風險類型
	private String RISK_TYPE_ENG;//風險類型英文
	private String CUST_RISK_BEF;//前次風險等級
	private String RISK_TYPE_BEF;//前次風險類型
	private String RISK_TYPE_BEF_ENG;//前次風險類型英文

	private String down_risk_level;
	private String EMP_ID;//主管帳號
	private String OUT_ACCESS;
	private String SICK_TYPE;//重大傷病證明
	private String RPRS_ID;//負責人ID
	private String RPRS_NAME;//負責人姓名
	private String DEL_TYPE;
	private String EMP_PASSWORD;//主管pwd

	private boolean branch;//判斷是否為網銀
	private List<Map<String, Object>> RISKList;
	private List<Map<String, Object>> questionList;
	private List<Map<String, Object>> questionList_change;
	private Map<String, Object> basic_information;
	private List<String> SupervisorerrorMsg;
	
	//add by Brian
	private String SCHOOL; //就讀學校
	private String EDU_CHANGE; //15歲以下，選擇高中職以上學歷的原因;20歲以上，從國中學歷改為高中職以上學歷的原因
	private String HEALTH_CHANGE; //有重大傷變證明變更為無
	private String REC_SEQ; //錄音序號
	
	private String CUST_DATA_390;	//冷靜期解除電文傳送主機資料
	private Date COOLING_EFF_DATE;	//冷靜期解除生效日
	
	private boolean COOLING; //是否為冷靜期
	
	//上送067157要有分行
	private String BRANCH;
	
	//因應KYC冷靜期電文增加變數
	private String KYC_TEST_DATE;
	private String EXPIRY_DATE;
	
	//WMS-CR-20201126-01_KYC到期等於專投到期日或免降等到期日孰低者
	private String CUST_PRO_DATE;	//專投到期日
	private String DEGRADE_DATE;	//免降等到期日
	private String PDF_KYC_FLAG;	//PDF用
	
	//#0777
	private String CUST_EMAIL_BEFORE; 
	private String SAMEEMAIL_REASON;//全行重覆信箱理由
	private String SAMEEMAIL_CHOOSE;//全行重覆信箱原因選擇
	private String HNWC_DUE_DATE;	//客戶高資產註記到期日(High Net Worth Client Data)
	private String HNWC_INVALID_DATE;	//客戶高資產註記註銷日(High Net Worth Client Data)
	
	
	public boolean isCOOLING() {
		return COOLING;
	}

	public void setCOOLING(boolean cOOLING) {
		COOLING = cOOLING;
	}
	
	public String getCHILD_NO_Item() {
		return CHILD_NO_Item;
	}

	public void setCHILD_NO_Item(String cHILD_NO_Item) {
		CHILD_NO_Item = cHILD_NO_Item;
	}

	public String getEDUCATION_Item() {
		return EDUCATION_Item;
	}

	public void setEDUCATION_Item(String eDUCATION_Item) {
		EDUCATION_Item = eDUCATION_Item;
	}

	public List<String> getSupervisorerrorMsg() {
		return SupervisorerrorMsg;
	}

	public void setSupervisorerrorMsg(List<String> supervisorerrorMsg) {
		SupervisorerrorMsg = supervisorerrorMsg;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public boolean isBranch() {
		return branch;
	}

	public void setBranch(boolean branch) {
		this.branch = branch;
	}

	public String getDown_risk_level() {
		return down_risk_level;
	}

	public String getOUT_ACCESS() {
		return OUT_ACCESS;
	}

	public void setDown_risk_level(String down_risk_level) {
		this.down_risk_level = down_risk_level;
	}

	public void setOUT_ACCESS(String oUT_ACCESS) {
		OUT_ACCESS = oUT_ACCESS;
	}

	public String getEMAIL_ADDR() {
		return EMAIL_ADDR;
	}

	public void setEMAIL_ADDR(String eMAIL_ADDR) {
		EMAIL_ADDR = eMAIL_ADDR;
	}

	public List<Map<String, Object>> getRISKList() {
		return RISKList;
	}

	public void setRISKList(List<Map<String, Object>> rISKList) {
		RISKList = rISKList;
	}

	public String getRISK_TYPE() {
		return RISK_TYPE;
	}

	public void setRISK_TYPE(String rISK_TYPE) {
		RISK_TYPE = rISK_TYPE;
	}

	public String getCUST_RISK_BEF() {
		return CUST_RISK_BEF;
	}

	public void setCUST_RISK_BEF(String cUST_RISK_BEF) {
		CUST_RISK_BEF = cUST_RISK_BEF;
	}

	public String getRISK_TYPE_BEF() {
		return RISK_TYPE_BEF;
	}

	public void setRISK_TYPE_BEF(String rISK_TYPE_BEF) {
		RISK_TYPE_BEF = rISK_TYPE_BEF;
	}
	
	public List<Map<String, Object>> getQuestionList_change() {
		return questionList_change;
	}

	public void setQuestionList_change(List<Map<String, Object>> questionList_change) {
		this.questionList_change = questionList_change;
	}

	public Map<String, Object> getBasic_information() {
		return basic_information;
	}

	public void setBasic_information(Map<String, Object> basic_information) {
		this.basic_information = basic_information;
	}

	public List<Map<String, Object>> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List<Map<String, Object>> questionList) {
		this.questionList = questionList;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public Date getTest_date() {
		return test_date;
	}

	public void setTest_date(Date test_date) {
		this.test_date = test_date;
	}

	public String getDAY() {
		return DAY;
	}

	public void setDAY(String dAY) {
		DAY = dAY;
	}

	public String getCUST_ADDR_1() {
		return CUST_ADDR_1;
	}

	public void setCUST_ADDR_1(String cUST_ADDR_1) {
		CUST_ADDR_1 = cUST_ADDR_1;
	}

	public String getCUST_RISK_AFR() {
		return CUST_RISK_AFR;
	}

	public void setCUST_RISK_AFR(String cUST_RISK_AFR) {
		CUST_RISK_AFR = cUST_RISK_AFR;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGENDER() {
		return GENDER;
	}

	public void setGENDER(String gENDER) {
		GENDER = gENDER;
	}

	public String getEDUCATION() {
		return EDUCATION;
	}

	public void setEDUCATION(String eDUCATION) {
		EDUCATION = eDUCATION;
	}

	public String getCAREER() {
		return CAREER;
	}

	public void setCAREER(String cAREER) {
		CAREER = cAREER;
	}

	public String getMARRAGE() {
		return MARRAGE;
	}

	public void setMARRAGE(String mARRAGE) {
		MARRAGE = mARRAGE;
	}

	public String getCHILD_NO() {
		return CHILD_NO;
	}

	public void setCHILD_NO(String cHILD_NO) {
		CHILD_NO = cHILD_NO;
	}

	public String getSICK_TYPE() {
		return SICK_TYPE;
	}

	public void setSICK_TYPE(String sICK_TYPE) {
		SICK_TYPE = sICK_TYPE;
	}

	public String getNIGHT() {
		return NIGHT;
	}

	public void setNIGHT(String nIGHT) {
		NIGHT = nIGHT;
	}

	public String getTEL_NO() {
		return TEL_NO;
	}

	public void setTEL_NO(String tEL_NO) {
		TEL_NO = tEL_NO;
	}

	public String getFAX() {
		return FAX;
	}

	public void setFAX(String fAX) {
		FAX = fAX;
	}

	public String getRPRS_ID() {
		return RPRS_ID;
	}

	public void setRPRS_ID(String rPRS_ID) {
		RPRS_ID = rPRS_ID;
	}

	public String getRPRS_NAME() {
		return RPRS_NAME;
	}

	public void setRPRS_NAME(String rPRS_NAME) {
		RPRS_NAME = rPRS_NAME;
	}

	public String getDEL_TYPE() {
		return DEL_TYPE;
	}

	public void setDEL_TYPE(String dEL_TYPE) {
		DEL_TYPE = dEL_TYPE;
	}

	public String getEMP_PASSWORD() {
		return EMP_PASSWORD;
	}

	public void setEMP_PASSWORD(String eMP_PASSWORD) {
		EMP_PASSWORD = eMP_PASSWORD;
	}

	public String getSCHOOL() {
		return SCHOOL;
	}

	public void setSCHOOL(String sCHOOL) {
		SCHOOL = sCHOOL;
	}

	public String getEDU_CHANGE() {
		return EDU_CHANGE;
	}

	public void setEDU_CHANGE(String eDU_CHANGE) {
		EDU_CHANGE = eDU_CHANGE;
	}

	public String getHEALTH_CHANGE() {
		return HEALTH_CHANGE;
	}

	public void setHEALTH_CHANGE(String hEALTH_CHANGE) {
		HEALTH_CHANGE = hEALTH_CHANGE;
	}

	public String getREC_SEQ() {
		return REC_SEQ;
	}

	public void setREC_SEQ(String rEC_SEQ) {
		REC_SEQ = rEC_SEQ;
	}

	public String getCUST_DATA_390() {
		return CUST_DATA_390;
	}

	public void setCUST_DATA_390(String cUST_DATA_390) {
		CUST_DATA_390 = cUST_DATA_390;
	}

	public Date getCOOLING_EFF_DATE() {
		return COOLING_EFF_DATE;
	}

	public void setCOOLING_EFF_DATE(Date cOOLING_EFF_DATE) {
		COOLING_EFF_DATE = cOOLING_EFF_DATE;
	}

	public String getBRANCH() {
		return BRANCH;
	}

	public void setBRANCH(String bRANCH) {
		BRANCH = bRANCH;
	}

	public String getKYC_TEST_DATE() {
		return KYC_TEST_DATE;
	}

	public void setKYC_TEST_DATE(String kYC_TEST_DATE) {
		KYC_TEST_DATE = kYC_TEST_DATE;
	}

	public String getEXPIRY_DATE() {
		return EXPIRY_DATE;
	}

	public void setEXPIRY_DATE(String eXPIRY_DATE) {
		EXPIRY_DATE = eXPIRY_DATE;
	}

	public String getCUST_PRO_DATE() {
		return CUST_PRO_DATE;
	}

	public void setCUST_PRO_DATE(String cUST_PRO_DATE) {
		CUST_PRO_DATE = cUST_PRO_DATE;
	}

	public String getDEGRADE_DATE() {
		return DEGRADE_DATE;
	}

	public void setDEGRADE_DATE(String dEGRADE_DATE) {
		DEGRADE_DATE = dEGRADE_DATE;
	}

	public String getPDF_KYC_FLAG() {
		return PDF_KYC_FLAG;
	}

	public void setPDF_KYC_FLAG(String pDF_KYC_FLAG) {
		PDF_KYC_FLAG = pDF_KYC_FLAG;
	}

	public String getCUST_EMAIL_BEFORE() {
		return CUST_EMAIL_BEFORE;
	}

	public void setCUST_EMAIL_BEFORE(String cUST_EMAIL_BEFORE) {
		CUST_EMAIL_BEFORE = cUST_EMAIL_BEFORE;
	}

	public String getSAMEEMAIL_REASON() {
		return SAMEEMAIL_REASON;
	}

	public void setSAMEEMAIL_REASON(String sAMEEMAIL_REASON) {
		SAMEEMAIL_REASON = sAMEEMAIL_REASON;
	}

	public String getSAMEEMAIL_CHOOSE() {
		return SAMEEMAIL_CHOOSE;
	}

	public void setSAMEEMAIL_CHOOSE(String sAMEEMAIL_CHOOSE) {
		SAMEEMAIL_CHOOSE = sAMEEMAIL_CHOOSE;
	}

	public String getRISK_TYPE_ENG() {
		return RISK_TYPE_ENG;
	}

	public void setRISK_TYPE_ENG(String rISK_TYPE_ENG) {
		RISK_TYPE_ENG = rISK_TYPE_ENG;
	}

	public String getRISK_TYPE_BEF_ENG() {
		return RISK_TYPE_BEF_ENG;
	}

	public void setRISK_TYPE_BEF_ENG(String rISK_TYPE_BEF_ENG) {
		RISK_TYPE_BEF_ENG = rISK_TYPE_BEF_ENG;
	}

	public String getHNWC_DUE_DATE() {
		return HNWC_DUE_DATE;
	}

	public void setHNWC_DUE_DATE(String hNWC_DUE_DATE) {
		HNWC_DUE_DATE = hNWC_DUE_DATE;
	}

	public String getHNWC_INVALID_DATE() {
		return HNWC_INVALID_DATE;
	}

	public void setHNWC_INVALID_DATE(String hNWC_INVALID_DATE) {
		HNWC_INVALID_DATE = hNWC_INVALID_DATE;
	}

}
