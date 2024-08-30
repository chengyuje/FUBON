package com.systex.jbranch.app.server.fps.ins111;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS111_IS_MAIN_NInputVO extends PagingInputVO{
	
	private String KEY_NO;			//行外保單序號
	private String PRD_KEY_NO;		//主附約產品編號
	private String INSURED_ID;		//被保人ID
	private String PRD_ID;			//產品代碼
	private String PRD_NAME;		//產品名稱
	private String IS_MAIN_TYPE;	//主/附約(辨別用)	
	private String IS_MAIN;			//主/附約(畫面上)
	private String INSURED_NAME;	//被保險人姓名
	private String GENDER;			//被保人性別
	private Date INSURED_BIRTHDAY;	//被保人生日
	private Date EFFECTED_DATE;		//保單生效日
	private String PAY_TYPE;		//繳別
	private String PAYMENTYEAR_SEL;	//繳費年期
	private String KIND_SEL;		//屬性歸類(種類)
	private String LOCAL_INSUREDAMT;//保額
	private String INSYEARFEE;		//保費
	private String INSURED_OBJECT;	//被保險對象
	private String COM_ID;			//保險公司
	private String TYPE;			//狀態
	
	private String COVERYEAR_SEL;	//保障年期
	
	private String UPQTY_SEL;
	private String UPTYPE;
	private String INSUREDAMT;
	private String BENEFICIARY_YN;	//要保人是否為身故受益人
	
	private String labelTITLE_Y;
	private String labelTITLE_A;
	private String labelTITLE_O;
	private String labelTITLE_K;

	private List<Map<String, Object>> IS_MAIN_NList;//附約清單
	
	
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getKEY_NO() {
		return KEY_NO;
	}
	public void setKEY_NO(String kEY_NO) {
		KEY_NO = kEY_NO;
	}
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}
	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}
	public String getPRD_KEY_NO() {
		return PRD_KEY_NO;
	}
	public void setPRD_KEY_NO(String pRD_KEY_NO) {
		PRD_KEY_NO = pRD_KEY_NO;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public String getPRD_ID() {
		return PRD_ID;
	}
	public String getIS_MAIN_TYPE() {
		return IS_MAIN_TYPE;
	}
	public String getIS_MAIN() {
		return IS_MAIN;
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
	public String getPAYMENTYEAR_SEL() {
		return PAYMENTYEAR_SEL;
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
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}
	public void setIS_MAIN_TYPE(String iS_MAIN_TYPE) {
		IS_MAIN_TYPE = iS_MAIN_TYPE;
	}
	public void setIS_MAIN(String iS_MAIN) {
		IS_MAIN = iS_MAIN;
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
	public void setPAYMENTYEAR_SEL(String pAYMENTYEAR_SEL) {
		PAYMENTYEAR_SEL = pAYMENTYEAR_SEL;
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
	public List<Map<String, Object>> getIS_MAIN_NList() {
		return IS_MAIN_NList;
	}
	public void setIS_MAIN_NList(List<Map<String, Object>> iS_MAIN_NList) {
		IS_MAIN_NList = iS_MAIN_NList;
	}
	public String getPRD_NAME() {
		return PRD_NAME;
	}
	public void setPRD_NAME(String pRD_NAME) {
		PRD_NAME = pRD_NAME;
	}
	public String getGENDER() {
		return GENDER;
	}
	public void setGENDER(String gENDER) {
		GENDER = gENDER;
	}
	
	public String getCOVERYEAR_SEL() {
		return COVERYEAR_SEL;
	}
	
	public void setCOVERYEAR_SEL(String cOVERYEAR_SEL) {
		COVERYEAR_SEL = cOVERYEAR_SEL;
	}
	public String getUPQTY_SEL() {
		return UPQTY_SEL;
	}
	public void setUPQTY_SEL(String uPQTY_SEL) {
		UPQTY_SEL = uPQTY_SEL;
	}
	
	public String getUPTYPE() {
		return UPTYPE;
	}
	public void setUPTYPE(String uPTYPE) {
		UPTYPE = uPTYPE;
	}
	
	public String getINSUREDAMT() {
		return INSUREDAMT;
	}
	public void setINSUREDAMT(String iNSUREDAMT) {
		INSUREDAMT = iNSUREDAMT;
	}
	public String getLabelTITLE_Y() {
		return labelTITLE_Y;
	}
	public void setLabelTITLE_Y(String labelTITLE_Y) {
		this.labelTITLE_Y = labelTITLE_Y;
	}
	public String getLabelTITLE_A() {
		return labelTITLE_A;
	}
	public void setLabelTITLE_A(String labelTITLE_A) {
		this.labelTITLE_A = labelTITLE_A;
	}
	public String getLabelTITLE_O() {
		return labelTITLE_O;
	}
	public void setLabelTITLE_O(String labelTITLE_O) {
		this.labelTITLE_O = labelTITLE_O;
	}
	public String getLabelTITLE_K() {
		return labelTITLE_K;
	}
	public void setLabelTITLE_K(String labelTITLE_K) {
		this.labelTITLE_K = labelTITLE_K;
	}
	public String getBENEFICIARY_YN() {
		return BENEFICIARY_YN;
	}
	public void setBENEFICIARY_YN(String bENEFICIARY_YN) {
		BENEFICIARY_YN = bENEFICIARY_YN;
	}


}
