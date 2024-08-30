package com.systex.jbranch.app.server.fps.ins111;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS111InputVO extends PagingInputVO{
	
	private String KEYNO;			//行外保單序號
	private String INSSEQ;			//保單自訂序號
	private String INSURED_ID;		//被保人ID
	private String PRD_KEY_NO;		//產品主鍵
	private String PRD_ID;			//產品代碼
	private String PRD_ID_N;		//附約產品代碼
	private String IS_MAIN_TYPE;	//主/附約(辨別用)	
	private String IS_MAIN;			//主/附約(畫面上)
	private String POLICY_NBR;		//保單編號
	private String AO_CODE;			//理專代號
	private String INSURED_NAME;	//被保險人姓名
	private Date INSURED_BIRTHDAY;	//被保人生日
	private Date EFFECTED_DATE;		//保單生效日
	private String INSURED_AGE;		//被保險人的投保年齡
	private String PAYMENTYEAR_SEL;	//繳費年期
	private String COVERYEAR_SEL;	//保障年期
	private String KIND_SEL;		//屬性歸類(種類)
	private String LOCAL_INSUREDAMT;//保額
	private String INSYEARFEE;		//保費
	private String INSURED_OBJECT;	//被保險對象
	private String COM_ID;			//保險公司
	private String CURR_CD;			//產品幣別               
	private String PAY_TYPE;		//繳別
	
	private String UPTYPE;			//單位或計畫             
	private String UPQTY_SEL;		//單位或計畫數           
	private String INSUREDAMT;		//原幣保險金額(保額)   
	private String GENDER;			//性別
	private String BENEFICIARY_YN;	//要保人是否為身故受益人
	
	private List<Map<String, Object>> IS_MAIN_NList;	//附約列表
	
	
	public String getCURR_CD() {
		return CURR_CD;
	}
	public void setCURR_CD(String cURR_CD) {
		CURR_CD = cURR_CD;
	}
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}
	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}
	public String getINSSEQ() {
		return INSSEQ;
	}
	public void setINSSEQ(String iNSSEQ) {
		INSSEQ = iNSSEQ;
	}
	public String getPRD_KEY_NO() {
		return PRD_KEY_NO;
	}
	public void setPRD_KEY_NO(String pRD_KEY_NO) {
		PRD_KEY_NO = pRD_KEY_NO;
	}
	public List<Map<String, Object>> getIS_MAIN_NList() {
		return IS_MAIN_NList;
	}
	public void setIS_MAIN_NList(List<Map<String, Object>> iS_MAIN_NList) {
		IS_MAIN_NList = iS_MAIN_NList;
	}
	public String getKEYNO() {
		return KEYNO;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public String getPRD_ID() {
		return PRD_ID;
	}
	public String getPRD_ID_N() {
		return PRD_ID_N;
	}
	public String getIS_MAIN_TYPE() {
		return IS_MAIN_TYPE;
	}
	public String getIS_MAIN() {
		return IS_MAIN;
	}
	public String getPOLICY_NBR() {
		return POLICY_NBR;
	}
	public String getAO_CODE() {
		return AO_CODE;
	}
	public String getINSURED_NAME() {
		return INSURED_NAME;
	}
	public Date getINSURED_BIRTHDAY() {
		return INSURED_BIRTHDAY;
	}
	public Date getEFFECTED_DATE() {
		return EFFECTED_DATE;
	}
	public String getINSURED_AGE() {
		return INSURED_AGE;
	}
	public String getPAYMENTYEAR_SEL() {
		return PAYMENTYEAR_SEL;
	}
	public String getCOVERYEAR_SEL() {
		return COVERYEAR_SEL;
	}
	public String getKIND_SEL() {
		return KIND_SEL;
	}
	public String getLOCAL_INSUREDAMT() {
		return LOCAL_INSUREDAMT;
	}
	public String getINSYEARFEE() {
		return INSYEARFEE;
	}
	public String getINSURED_OBJECT() {
		return INSURED_OBJECT;
	}
	public String getCOM_ID() {
		return COM_ID;
	}
	public void setKEYNO(String kEYNO) {
		KEYNO = kEYNO;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}
	public void setPRD_ID_N(String pRD_ID_N) {
		PRD_ID_N = pRD_ID_N;
	}
	public void setIS_MAIN_TYPE(String iS_MAIN_TYPE) {
		IS_MAIN_TYPE = iS_MAIN_TYPE;
	}
	public void setIS_MAIN(String iS_MAIN) {
		IS_MAIN = iS_MAIN;
	}
	public void setPOLICY_NBR(String POLICY_NBR) {
		POLICY_NBR = POLICY_NBR;
	}
	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}
	public void setINSURED_NAME(String iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}
	public void setINSURED_BIRTHDAY(Date iNSURED_BIRTHDAY) {
		INSURED_BIRTHDAY = iNSURED_BIRTHDAY;
	}
	public void setEFFECTED_DATE(Date eFFECTED_DATE) {
		EFFECTED_DATE = eFFECTED_DATE;
	}
	public void setINSURED_AGE(String iNSURED_AGE) {
		INSURED_AGE = iNSURED_AGE;
	}
	public void setPAYMENTYEAR_SEL(String pAYMENTYEAR_SEL) {
		PAYMENTYEAR_SEL = pAYMENTYEAR_SEL;
	}
	public void setCOVERYEAR_SEL(String cOVERYEAR_SEL) {
		COVERYEAR_SEL = cOVERYEAR_SEL;
	}
	public void setKIND_SEL(String kIND_SEL) {
		KIND_SEL = kIND_SEL;
	}
	public void setLOCAL_INSUREDAMT(String lOCAL_INSUREDAMT) {
		LOCAL_INSUREDAMT = lOCAL_INSUREDAMT;
	}
	public void setINSYEARFEE(String iNSYEARFEE) {
		INSYEARFEE = iNSYEARFEE;
	}
	public void setINSURED_OBJECT(String iNSURED_OBJECT) {
		INSURED_OBJECT = iNSURED_OBJECT;
	}
	public void setCOM_ID(String cOM_ID) {
		COM_ID = cOM_ID;
	}
	
	public String getUPTYPE() {
		return UPTYPE;
	}
	public void setUPTYPE(String uPTYPE) {
		UPTYPE = uPTYPE;
	}
	
	public String getUPQTY_SEL() {
		return UPQTY_SEL;
	}
	public void setUPQTY_SEL(String uPQTY_SEL) {
		UPQTY_SEL = uPQTY_SEL;
	}
	
	public String getINSUREDAMT() {
		return INSUREDAMT;
	}
	public void setINSUREDAMT(String iNSUREDAMT) {
		INSUREDAMT = iNSUREDAMT;
	}
	public String getGENDER() {
		return GENDER;
	}
	public void setGENDER(String gENDER) {
		GENDER = gENDER;
	}
	public String getBENEFICIARY_YN() {
		return BENEFICIARY_YN;
	}
	public void setBENEFICIARY_YN(String bENEFICIARY_YN) {
		BENEFICIARY_YN = bENEFICIARY_YN;
	}
	
	
}
