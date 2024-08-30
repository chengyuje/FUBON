package com.systex.jbranch.app.server.fps.kyc310;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC310InputVO extends PagingInputVO{
	
	private String CUST_ID;
	private String cust_name;
	private String COMMENTARY_STAFF;//解說人員
	private String QUESTION_TYPE;	//02 = 自然人，03 = 法人
	private String chk_type;	//操作行為：print = 列印空白表單，submit = 填寫完成
	private String birthday;	//出生日期
	private Boolean gender_W;	//女
	private Boolean gender_M;	//男
	private String EDUCATION;	//教育程度
	private List<Map<String, Object>> EDUCATIONList;//教育程度選項
	private String CAREER;		//職業
	private List<Map<String, Object>> CAREERList;//職業選項
	private String MARRAGE;		//婚姻
	private List<Map<String, Object>> MARRAGEList;//婚姻選項
	private String CHILD_NO;	//子女人數
	private List<Map<String, Object>> CHILD_NOList;//子女人數選項
	private String DAY;			//日連絡電話
	private String DAY_COD;		//日連絡電話COD
	private String NIGHT;		//夜連絡電話
	private String NIGHT_COD;	//夜連絡電話COD
	private String TEL_NO;		//行動
	private String FAX_TYPE;	//傳真
	private String FAX;			//傳真號碼
	private String CUST_ADDR_1; //通訊地址
	private String EMAIL_ADDR;	//電子郵件
	private String SICK_TYPE;	//重大傷病
	private List<Map<String, Object>> SICK_TYPEList;//重大傷病選項
	private String EXAM_VERSION;
	private String TEST_BEF_DATE;
	private String CUST_RISK_AFR;
	private String RISKRANGE;
	private String CUST_RISK_BEF;
	private String CUST_EDUCTION_BEFORE;
	private String CUST_CAREER_BEFORE;
	private String CUST_MARRIAGE_BEFORE;
	private String CUST_CHILDREN_BEFORE;
	private String CUST_HEALTH_BEFORE;
	private String RPRS_NAME;	 //公司負責人姓名
	private String RPRS_ID;		//公司負責人ID
	private String Y_N_update;//是否更新
	private List ANSWER_2;
	private List<Map<String, Object>> quest_list;
	private List<Map<String, Object>> before_persional;
	private Boolean FromElsePrintBlank;
	private String degrade;//主管特簽降等註記
	//add by Brian 
	private Map<String, Object> basic_information;
	private String CUST_SCHOOL; //就讀學校
    private List<Map<String, Object>> EDU_CHOOSEList; //15歲以下，選擇高中職以上學歷的原因選項
	private List<Map<String, Object>> EDU_CHANGEList; //20歲以上，從國中學歷改為高中職以上學歷的原因選項
	private List<Map<String, Object>> HEALTH_CHANGEList; //有重大傷變證明變更為無選項
	private String SCHOOL; //就讀學校
	private String EDU_CHANGE; //15歲以下，選擇高中職以上學歷的原因;20歲以上，從國中學歷改為高中職以上學歷的原因;18歲(含)以上客戶，學歷由國中以下異動為高中職以上，需留存學校名稱及原因
	private String HEALTH_CHANGE; //有重大傷變證明變更為無
	private String DAY_TYPE; //是否勾選日間電話
	private String NIGHT_TYPE; //是否勾選夜間電話
	private String TEL_NO_TYPE; //是否勾選行動電話
	private String TELNO_COD; //行動COD
	private String FAX_COD; //傳真COD
	
	private String CRR_ORI;	//原始總分取得的風險屬性
	private String CRR_MATRIX;	//降等機制矩陣取得的風險屬性
	private BigDecimal SCORE_ORI_TOT;	//原始總分
	private BigDecimal SCORE_C;	//風險偏好總分
	private BigDecimal SCORE_W;	//風險承受能力總分
	private BigDecimal SCORE_CW_TOT;	//降等機制矩陣總分
	private BigDecimal RISK_LOSS_RATE;	//可承受風險損失率(%)
	private String RISK_LOSS_LEVEL;	//可承受風險損失率C值
	private String RS_VERSION;	//降等機制矩陣版號
	private String RLR_VERSION;	//可承受風險損失率版號
	private Date EXPIRY_DATE;	
	private Date DEGRADE_DATE;	//免降等註記到期日
	private Date CUST_PRO_DATE;	//專業投資人到期日
	
	private String PDF_KYC_FLAG;
	
	private String CUST_EMAIL_BEFORE; 
	private String SAMEEMAIL_REASON;//全行重覆信箱理由
	private String SAMEEMAIL_CHOOSE;//全行重覆信箱原因選擇
	private List<Map<String, Object>> EMAIL_CHANGEList; //全行重覆信箱原因
	private Date HNWC_DUE_DATE;	//客戶高資產註記到期日(High Net Worth Client Data)
	private Date HNWC_INVALID_DATE;	//客戶高資產註記註銷日(High Net Worth Client Data)
	
	public List<Map<String, Object>> getMARRAGEList() {
		return MARRAGEList;
	}
	public List<Map<String, Object>> getCHILD_NOList() {
		return CHILD_NOList;
	}
	public List<Map<String, Object>> getSICK_TYPEList() {
		return SICK_TYPEList;
	}
	public void setMARRAGEList(List<Map<String, Object>> mARRAGEList) {
		MARRAGEList = mARRAGEList;
	}
	public void setCHILD_NOList(List<Map<String, Object>> cHILD_NOList) {
		CHILD_NOList = cHILD_NOList;
	}
	public void setSICK_TYPEList(List<Map<String, Object>> sICK_TYPEList) {
		SICK_TYPEList = sICK_TYPEList;
	}
	public List<Map<String, Object>> getCAREERList() {
		return CAREERList;
	}
	public void setCAREERList(List<Map<String, Object>> cAREERList) {
		CAREERList = cAREERList;
	}
	public List<Map<String, Object>> getEDUCATIONList() {
		return EDUCATIONList;
	}
	public void setEDUCATIONList(List<Map<String, Object>> eDUCATIONList) {
		EDUCATIONList = eDUCATIONList;
	}
	public Boolean getFromElsePrintBlank() {
		return FromElsePrintBlank;
	}
	public void setFromElsePrintBlank(Boolean fromElsePrintBlank) {
		FromElsePrintBlank = fromElsePrintBlank;
	}
	public String getY_N_update() {
		return Y_N_update;
	}
	public void setY_N_update(String y_N_update) {
		Y_N_update = y_N_update;
	}
	public String getFAX() {
		return FAX;
	}
	public void setFAX(String fAX) {
		FAX = fAX;
	}
	public String getDAY_COD() {
		return DAY_COD;
	}
	public String getNIGHT_COD() {
		return NIGHT_COD;
	}
	public void setDAY_COD(String dAY_COD) {
		DAY_COD = dAY_COD;
	}
	public void setNIGHT_COD(String nIGHT_COD) {
		NIGHT_COD = nIGHT_COD;
	}
	public List<Map<String, Object>> getBefore_persional() {
		return before_persional;
	}
	public void setBefore_persional(List<Map<String, Object>> before_persional) {
		this.before_persional = before_persional;
	}
	public String getRPRS_NAME() {
		return RPRS_NAME;
	}
	public String getRPRS_ID() {
		return RPRS_ID;
	}
	public void setRPRS_NAME(String rPRS_NAME) {
		RPRS_NAME = rPRS_NAME;
	}
	public void setRPRS_ID(String rPRS_ID) {
		RPRS_ID = rPRS_ID;
	}
	public List<Map<String, Object>> getQuest_list() {
		return quest_list;
	}
	public void setQuest_list(List<Map<String, Object>> quest_list) {
		this.quest_list = quest_list;
	}
	public String getCUST_RISK_BEF() {
		return CUST_RISK_BEF;
	}
	public void setCUST_RISK_BEF(String cUST_RISK_BEF) {
		CUST_RISK_BEF = cUST_RISK_BEF;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getEXAM_VERSION() {
		return EXAM_VERSION;
	}
	public void setEXAM_VERSION(String eXAM_VERSION) {
		EXAM_VERSION = eXAM_VERSION;
	}

	public String getTEST_BEF_DATE() {
		return TEST_BEF_DATE;
	}
	public void setTEST_BEF_DATE(String tEST_BEF_DATE) {
		TEST_BEF_DATE = tEST_BEF_DATE;
	}
	public String getCUST_RISK_AFR() {
		return CUST_RISK_AFR;
	}
	public void setCUST_RISK_AFR(String cUST_RISK_AFR) {
		CUST_RISK_AFR = cUST_RISK_AFR;
	}
	public String getRISKRANGE() {
		return RISKRANGE;
	}
	public void setRISKRANGE(String rISKRANGE) {
		RISKRANGE = rISKRANGE;
	}
	public String getCUST_EDUCTION_BEFORE() {
		return CUST_EDUCTION_BEFORE;
	}
	public void setCUST_EDUCTION_BEFORE(String cUST_EDUCTION_BEFORE) {
		CUST_EDUCTION_BEFORE = cUST_EDUCTION_BEFORE;
	}
	public String getCUST_CAREER_BEFORE() {
		return CUST_CAREER_BEFORE;
	}
	public void setCUST_CAREER_BEFORE(String cUST_CAREER_BEFORE) {
		CUST_CAREER_BEFORE = cUST_CAREER_BEFORE;
	}
	public String getCUST_MARRIAGE_BEFORE() {
		return CUST_MARRIAGE_BEFORE;
	}
	public void setCUST_MARRIAGE_BEFORE(String cUST_MARRIAGE_BEFORE) {
		CUST_MARRIAGE_BEFORE = cUST_MARRIAGE_BEFORE;
	}
	public String getCUST_CHILDREN_BEFORE() {
		return CUST_CHILDREN_BEFORE;
	}
	public void setCUST_CHILDREN_BEFORE(String cUST_CHILDREN_BEFORE) {
		CUST_CHILDREN_BEFORE = cUST_CHILDREN_BEFORE;
	}
	public String getCUST_HEALTH_BEFORE() {
		return CUST_HEALTH_BEFORE;
	}
	public void setCUST_HEALTH_BEFORE(String cUST_HEALTH_BEFORE) {
		CUST_HEALTH_BEFORE = cUST_HEALTH_BEFORE;
	}

	public List getANSWER_2() {
		return ANSWER_2;
	}
	public void setANSWER_2(List aNSWER_2) {
		ANSWER_2 = aNSWER_2;
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
	public String getChk_type() {
		return chk_type;
	}
	public void setChk_type(String chk_type) {
		this.chk_type = chk_type;
	}
	public String getCUST_ID() {
		return CUST_ID.toUpperCase();
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID.toUpperCase();
	}
	public String getCOMMENTARY_STAFF() {
		return COMMENTARY_STAFF;
	}
	public void setCOMMENTARY_STAFF(String cOMMENTARY_STAFF) {
		COMMENTARY_STAFF = cOMMENTARY_STAFF;
	}
	public String getQUESTION_TYPE() {
		return QUESTION_TYPE;
	}
	public void setQUESTION_TYPE(String qUESTION_TYPE) {
		QUESTION_TYPE = qUESTION_TYPE;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getDAY() {
		return DAY;
	}
	public void setDAY(String dAY) {
		DAY = dAY;
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
	public String getFAX_TYPE() {
		return FAX_TYPE;
	}
	public void setFAX_TYPE(String fAX_TYPE) {
		FAX_TYPE = fAX_TYPE;
	}
	public String getCUST_ADDR_1() {
		return CUST_ADDR_1;
	}
	public void setCUST_ADDR_1(String cUST_ADDR_1) {
		CUST_ADDR_1 = cUST_ADDR_1;
	}
	public String getEMAIL_ADDR() {
		return EMAIL_ADDR;
	}
	public void setEMAIL_ADDR(String eMAIL_ADDR) {
		EMAIL_ADDR = eMAIL_ADDR;
	}
	public Boolean getGender_W() {
		return gender_W;
	}
	public void setGender_W(Boolean gender_W) {
		this.gender_W = gender_W;
	}
	public Boolean getGender_M() {
		return gender_M;
	}
	public void setGender_M(Boolean gender_M) {
		this.gender_M = gender_M;
	}
	public String getDegrade() {
		return degrade;
	}
	public void setDegrade(String degrade) {
		this.degrade = degrade;
	}
	public Map<String, Object> getBasic_information() {
		return basic_information;
	}
	public void setBasic_information(Map<String, Object> basic_information) {
		this.basic_information = basic_information;
	}
	public String getCUST_SCHOOL() {
		return CUST_SCHOOL;
	}
	public void setCUST_SCHOOL(String cUST_SCHOOL) {
		CUST_SCHOOL = cUST_SCHOOL;
	}
	public List<Map<String, Object>> getEDU_CHOOSEList() {
		return EDU_CHOOSEList;
	}
	public void setEDU_CHOOSEList(List<Map<String, Object>> eDU_CHOOSEList) {
		EDU_CHOOSEList = eDU_CHOOSEList;
	}
	public List<Map<String, Object>> getEDU_CHANGEList() {
		return EDU_CHANGEList;
	}
	public void setEDU_CHANGEList(List<Map<String, Object>> eDU_CHANGEList) {
		EDU_CHANGEList = eDU_CHANGEList;
	}
	public List<Map<String, Object>> getHEALTH_CHANGEList() {
		return HEALTH_CHANGEList;
	}
	public void setHEALTH_CHANGEList(List<Map<String, Object>> hEALTH_CHANGEList) {
		HEALTH_CHANGEList = hEALTH_CHANGEList;
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
	public String getDAY_TYPE() {
		return DAY_TYPE;
	}
	public void setDAY_TYPE(String dAY_TYPE) {
		DAY_TYPE = dAY_TYPE;
	}
	public String getNIGHT_TYPE() {
		return NIGHT_TYPE;
	}
	public void setNIGHT_TYPE(String nIGHT_TYPE) {
		NIGHT_TYPE = nIGHT_TYPE;
	}
	public String getTEL_NO_TYPE() {
		return TEL_NO_TYPE;
	}
	public void setTEL_NO_TYPE(String tEL_NO_TYPE) {
		TEL_NO_TYPE = tEL_NO_TYPE;
	}
	public String getTELNO_COD() {
		return TELNO_COD;
	}
	public void setTELNO_COD(String tELNO_COD) {
		TELNO_COD = tELNO_COD;
	}
	public String getFAX_COD() {
		return FAX_COD;
	}
	public void setFAX_COD(String fAX_COD) {
		FAX_COD = fAX_COD;
	}
	public String getCRR_ORI() {
		return CRR_ORI;
	}
	public void setCRR_ORI(String cRR_ORI) {
		CRR_ORI = cRR_ORI;
	}
	public String getCRR_MATRIX() {
		return CRR_MATRIX;
	}
	public void setCRR_MATRIX(String cRR_MATRIX) {
		CRR_MATRIX = cRR_MATRIX;
	}
	public BigDecimal getSCORE_ORI_TOT() {
		return SCORE_ORI_TOT;
	}
	public void setSCORE_ORI_TOT(BigDecimal sCORE_ORI_TOT) {
		SCORE_ORI_TOT = sCORE_ORI_TOT;
	}
	public BigDecimal getSCORE_C() {
		return SCORE_C;
	}
	public void setSCORE_C(BigDecimal sCORE_C) {
		SCORE_C = sCORE_C;
	}
	public BigDecimal getSCORE_W() {
		return SCORE_W;
	}
	public void setSCORE_W(BigDecimal sCORE_W) {
		SCORE_W = sCORE_W;
	}
	public BigDecimal getSCORE_CW_TOT() {
		return SCORE_CW_TOT;
	}
	public void setSCORE_CW_TOT(BigDecimal sCORE_CW_TOT) {
		SCORE_CW_TOT = sCORE_CW_TOT;
	}
	public BigDecimal getRISK_LOSS_RATE() {
		return RISK_LOSS_RATE;
	}
	public void setRISK_LOSS_RATE(BigDecimal rISK_LOSS_RATE) {
		RISK_LOSS_RATE = rISK_LOSS_RATE;
	}
	public String getRISK_LOSS_LEVEL() {
		return RISK_LOSS_LEVEL;
	}
	public void setRISK_LOSS_LEVEL(String rISK_LOSS_LEVEL) {
		RISK_LOSS_LEVEL = rISK_LOSS_LEVEL;
	}
	public String getRS_VERSION() {
		return RS_VERSION;
	}
	public void setRS_VERSION(String rS_VERSION) {
		RS_VERSION = rS_VERSION;
	}
	public String getRLR_VERSION() {
		return RLR_VERSION;
	}
	public void setRLR_VERSION(String rLR_VERSION) {
		RLR_VERSION = rLR_VERSION;
	}
	public Date getEXPIRY_DATE() {
		return EXPIRY_DATE;
	}
	public void setEXPIRY_DATE(Date eXPIRY_DATE) {
		EXPIRY_DATE = eXPIRY_DATE;
	}
	public Date getDEGRADE_DATE() {
		return DEGRADE_DATE;
	}
	public void setDEGRADE_DATE(Date dEGRADE_DATE) {
		DEGRADE_DATE = dEGRADE_DATE;
	}
	public Date getCUST_PRO_DATE() {
		return CUST_PRO_DATE;
	}
	public void setCUST_PRO_DATE(Date cUST_PRO_DATE) {
		CUST_PRO_DATE = cUST_PRO_DATE;
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
	public List<Map<String, Object>> getEMAIL_CHANGEList() {
		return EMAIL_CHANGEList;
	}
	public void setEMAIL_CHANGEList(List<Map<String, Object>> eMAIL_CHANGEList) {
		EMAIL_CHANGEList = eMAIL_CHANGEList;
	}
	public Date getHNWC_DUE_DATE() {
		return HNWC_DUE_DATE;
	}
	public void setHNWC_DUE_DATE(Date hNWC_DUE_DATE) {
		HNWC_DUE_DATE = hNWC_DUE_DATE;
	}
	public Date getHNWC_INVALID_DATE() {
		return HNWC_INVALID_DATE;
	}
	public void setHNWC_INVALID_DATE(Date hNWC_INVALID_DATE) {
		HNWC_INVALID_DATE = hNWC_INVALID_DATE;
	}
	
}
