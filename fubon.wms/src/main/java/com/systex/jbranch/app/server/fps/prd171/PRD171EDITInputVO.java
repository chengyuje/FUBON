package com.systex.jbranch.app.server.fps.prd171;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD171EDITInputVO extends PagingInputVO {

	private String TYPE_TABLE; // 來源類型
	private String SEQ;
	private String DOC_NAME;
	private String DOC_SEQ;
	private String DOC_TYPE;
	private String DOC_LEVEL;
	private String SIGN_INC;
	private String OTH_TYPE;

	private BigDecimal COMPANY_NUM;

	// 1table --
	private String INSPRD_KEYNO;
	private String INSPRD_TYPE;
	private String INSPRD_CLASS;
	private String INSPRD_STYLE;
	private String INSPRD_ID;
	private String INSPRD_NAME;
	private String INSPRD_ANNUAL;
	private String MAIN_RIDER;
	private String PAY_TYPE;
	private String CURR_CD;
	private String FEE_STATE;
	private String PRD_RATE;
	private String CNR_RATE;
	private String COEFFICIENT;
	private String NEED_MATCH;
	private String CERT_TYPE;
	private String TRAINING_TYPE;
	private String EXCH_RATE;
	private String AB_EXCH_RATE;
	private String EFFECT_DATE;
	private String EXPIRY_DATE;

	// 2table
	private String KEY_NO;

	private String SPECIAL_CONDITION;

	private String APPROVER; // 複核人員

	private String CHOICE; // 是否勾選
	// 3table

	private String Q_ID;
	private String Q_SEQ;
	private String temp_Q_ID;
	private List DeleteList;
	private List<Map<String, Object>> DILOGList;
	private Date APP_DATE;

	// 4table

	private String PERIOD;
	private String EMP_BONUS_RATE;
	private String PRD_BONUS_RATE;
	private String OSEA_BONUS_RATE;
	private String HIGH_CPCT_BONUS_RATE;
	private String YEAR_END_BONUS;
	private String LINKED_NAME;
	private BigDecimal KYC_SCORE;
	private String TARGET_CURR;
	private String INT_TYPE;
	private String TRANSFER_FLG;

	// 5table

	private String ANNUAL;
	private String TYPE;
	private String COMM_RATE;
	private String CNR_YIELD;//CNR分配率
	private String CNR_MULTIPLE;//CNR加減碼
	private Date MULTIPLE_SDATE;//CNR加碼區間起日
	private Date MULTIPLE_EDATE;//CNR加碼區間迄日

	private String FUND_ID;
	private String INKED_NAME;
	private String LIPPER_ID;
	private String PRD_RISK;
	private String COM_PRD_RISK;
	private String TARGET_ID;

	private String copy_insprd_id;
	private String YN_Copy;
	private List<Map<String, Object>> DOCCHKList;
	private String DIVIDEND_YN;


	public String getCHOICE() {
		return CHOICE;
	}

	public void setCHOICE(String cHOICE) {
		CHOICE = cHOICE;
	}

	public List<Map<String, Object>> getDOCCHKList() {
		return DOCCHKList;
	}

	public void setDOCCHKList(List<Map<String, Object>> dOCCHKList) {
		DOCCHKList = dOCCHKList;
	}

	public String getCopy_insprd_id() {
		return copy_insprd_id;
	}

	public void setCopy_insprd_id(String copy_insprd_id) {
		this.copy_insprd_id = copy_insprd_id;
	}

	public String getYN_Copy() {
		return YN_Copy;
	}

	public void setYN_Copy(String yN_Copy) {
		YN_Copy = yN_Copy;
	}

	public String getCNR_YIELD() {
		return CNR_YIELD;
	}

	public String getCNR_MULTIPLE() {
		return CNR_MULTIPLE;
	}

	public Date getMULTIPLE_SDATE() {
		return MULTIPLE_SDATE;
	}

	public Date getMULTIPLE_EDATE() {
		return MULTIPLE_EDATE;
	}

	public void setCNR_YIELD(String cNR_YIELD) {
		CNR_YIELD = cNR_YIELD;
	}

	public void setCNR_MULTIPLE(String cNR_MULTIPLE) {
		CNR_MULTIPLE = cNR_MULTIPLE;
	}

	public void setMULTIPLE_SDATE(Date mULTIPLE_SDATE) {
		MULTIPLE_SDATE = mULTIPLE_SDATE;
	}

	public void setMULTIPLE_EDATE(Date mULTIPLE_EDATE) {
		MULTIPLE_EDATE = mULTIPLE_EDATE;
	}

	public List<Map<String, Object>> getDILOGList() {
		return DILOGList;
	}

	public void setDILOGList(List<Map<String, Object>> dILOGList) {
		DILOGList = dILOGList;
	}

	public List getDeleteList() {
		return DeleteList;
	}

	public void setDeleteList(List deleteList) {
		DeleteList = deleteList;
	}

	public String getTemp_Q_ID() {
		return temp_Q_ID;
	}

	public void setTemp_Q_ID(String temp_Q_ID) {
		this.temp_Q_ID = temp_Q_ID;
	}

	public String getFUND_ID() {
		return FUND_ID;
	}

	public void setFUND_ID(String fUND_ID) {
		FUND_ID = fUND_ID;
	}

	public String getDOC_NAME() {
		return DOC_NAME;
	}

	public String getDOC_SEQ() {
		return DOC_SEQ;
	}

	public String getDOC_TYPE() {
		return DOC_TYPE;
	}

	public String getDOC_LEVEL() {
		return DOC_LEVEL;
	}

	public String getSIGN_INC() {
		return SIGN_INC;
	}

	public void setDOC_NAME(String dOC_NAME) {
		DOC_NAME = dOC_NAME;
	}

	public void setDOC_SEQ(String dOC_SEQ) {
		DOC_SEQ = dOC_SEQ;
	}

	public void setDOC_TYPE(String dOC_TYPE) {
		DOC_TYPE = dOC_TYPE;
	}

	public void setDOC_LEVEL(String dOC_LEVEL) {
		DOC_LEVEL = dOC_LEVEL;
	}

	public void setSIGN_INC(String sIGN_INC) {
		SIGN_INC = sIGN_INC;
	}

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}

	public String getOTH_TYPE() {
		return OTH_TYPE;
	}

	public void setOTH_TYPE(String oTH_TYPE) {
		OTH_TYPE = oTH_TYPE;
	}

	public String getTYPE_TABLE() {
		return TYPE_TABLE;
	}

	public void setTYPE_TABLE(String tYPE_TABLE) {
		TYPE_TABLE = tYPE_TABLE;
	}

	public String getINSPRD_KEYNO() {
		return INSPRD_KEYNO;
	}

	public String getINSPRD_TYPE() {
		return INSPRD_TYPE;
	}

	public String getINSPRD_CLASS() {
		return INSPRD_CLASS;
	}

	public String getINSPRD_STYLE() {
		return INSPRD_STYLE;
	}

	public String getINSPRD_ID() {
		return INSPRD_ID;
	}

	public String getINSPRD_NAME() {
		return INSPRD_NAME;
	}

	public String getINSPRD_ANNUAL() {
		return INSPRD_ANNUAL;
	}

	public String getMAIN_RIDER() {
		return MAIN_RIDER;
	}

	public String getPAY_TYPE() {
		return PAY_TYPE;
	}

	public String getCURR_CD() {
		return CURR_CD;
	}

	public String getFEE_STATE() {
		return FEE_STATE;
	}

	public String getPRD_RATE() {
		return PRD_RATE;
	}

	public String getCNR_RATE() {
		return CNR_RATE;
	}

	public String getCOEFFICIENT() {
		return COEFFICIENT;
	}

	public String getNEED_MATCH() {
		return NEED_MATCH;
	}

	public String getCERT_TYPE() {
		return CERT_TYPE;
	}

	public String getTRAINING_TYPE() {
		return TRAINING_TYPE;
	}

	public String getEXCH_RATE() {
		return EXCH_RATE;
	}

	public String getAB_EXCH_RATE() {
		return AB_EXCH_RATE;
	}

	public String getKEY_NO() {
		return KEY_NO;
	}

	public String getSPECIAL_CONDITION() {
		return SPECIAL_CONDITION;
	}

	public String getAPPROVER() {
		return APPROVER;
	}

	public String getQ_ID() {
		return Q_ID;
	}

	public String getQ_SEQ() {
		return Q_SEQ;
	}

	public Date getAPP_DATE() {
		return APP_DATE;
	}

	public String getPERIOD() {
		return PERIOD;
	}

	public String getLINKED_NAME() {
		return LINKED_NAME;
	}

	public void setLINKED_NAME(String lINKED_NAME) {
		LINKED_NAME = lINKED_NAME;
	}

	public String getEMP_BONUS_RATE() {
		return EMP_BONUS_RATE;
	}

	public String getPRD_BONUS_RATE() {
		return PRD_BONUS_RATE;
	}

	public String getOSEA_BONUS_RATE() {
		return OSEA_BONUS_RATE;
	}

	public String getHIGH_CPCT_BONUS_RATE() {
		return HIGH_CPCT_BONUS_RATE;
	}

	public String getYEAR_END_BONUS() {
		return YEAR_END_BONUS;
	}

	public String getANNUAL() {
		return ANNUAL;
	}

	public String getTYPE() {
		return TYPE;
	}

	public String getCOMM_RATE() {
		return COMM_RATE;
	}

	public void setINSPRD_KEYNO(String iNSPRD_KEYNO) {
		INSPRD_KEYNO = iNSPRD_KEYNO;
	}

	public void setINSPRD_TYPE(String iNSPRD_TYPE) {
		INSPRD_TYPE = iNSPRD_TYPE;
	}

	public void setINSPRD_CLASS(String iNSPRD_CLASS) {
		INSPRD_CLASS = iNSPRD_CLASS;
	}

	public void setINSPRD_STYLE(String iNSPRD_STYLE) {
		INSPRD_STYLE = iNSPRD_STYLE;
	}

	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}

	public void setINSPRD_NAME(String iNSPRD_NAME) {
		INSPRD_NAME = iNSPRD_NAME;
	}

	public void setINSPRD_ANNUAL(String iNSPRD_ANNUAL) {
		INSPRD_ANNUAL = iNSPRD_ANNUAL;
	}

	public void setMAIN_RIDER(String mAIN_RIDER) {
		MAIN_RIDER = mAIN_RIDER;
	}

	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}

	public void setCURR_CD(String cURR_CD) {
		CURR_CD = cURR_CD;
	}

	public void setFEE_STATE(String fEE_STATE) {
		FEE_STATE = fEE_STATE;
	}

	public void setPRD_RATE(String pRD_RATE) {
		PRD_RATE = pRD_RATE;
	}

	public void setCNR_RATE(String cNR_RATE) {
		CNR_RATE = cNR_RATE;
	}

	public void setCOEFFICIENT(String cOEFFICIENT) {
		COEFFICIENT = cOEFFICIENT;
	}

	public void setNEED_MATCH(String nEED_MATCH) {
		NEED_MATCH = nEED_MATCH;
	}

	public String getEFFECT_DATE() {
		return EFFECT_DATE;
	}

	public String getEXPIRY_DATE() {
		return EXPIRY_DATE;
	}

	public void setEFFECT_DATE(String eFFECT_DATE) {
		EFFECT_DATE = eFFECT_DATE;
	}

	public void setEXPIRY_DATE(String eXPIRY_DATE) {
		EXPIRY_DATE = eXPIRY_DATE;
	}

	public void setCERT_TYPE(String cERT_TYPE) {
		CERT_TYPE = cERT_TYPE;
	}

	public void setTRAINING_TYPE(String tRAINING_TYPE) {
		TRAINING_TYPE = tRAINING_TYPE;
	}

	public void setEXCH_RATE(String eXCH_RATE) {
		EXCH_RATE = eXCH_RATE;
	}

	public void setAB_EXCH_RATE(String aB_EXCH_RATE) {
		AB_EXCH_RATE = aB_EXCH_RATE;
	}

	public void setKEY_NO(String kEY_NO) {
		KEY_NO = kEY_NO;
	}

	public void setSPECIAL_CONDITION(String sPECIAL_CONDITION) {
		SPECIAL_CONDITION = sPECIAL_CONDITION;
	}

	public void setAPPROVER(String aPPROVER) {
		APPROVER = aPPROVER;
	}

	public void setQ_ID(String q_ID) {
		Q_ID = q_ID;
	}

	public void setQ_SEQ(String q_SEQ) {
		Q_SEQ = q_SEQ;
	}

	public void setAPP_DATE(Date aPP_DATE) {
		APP_DATE = aPP_DATE;
	}

	public void setPERIOD(String pERIOD) {
		PERIOD = pERIOD;
	}

	public void setEMP_BONUS_RATE(String eMP_BONUS_RATE) {
		EMP_BONUS_RATE = eMP_BONUS_RATE;
	}

	public void setPRD_BONUS_RATE(String pRD_BONUS_RATE) {
		PRD_BONUS_RATE = pRD_BONUS_RATE;
	}

	public void setOSEA_BONUS_RATE(String oSEA_BONUS_RATE) {
		OSEA_BONUS_RATE = oSEA_BONUS_RATE;
	}

	public void setHIGH_CPCT_BONUS_RATE(String hIGH_CPCT_BONUS_RATE) {
		HIGH_CPCT_BONUS_RATE = hIGH_CPCT_BONUS_RATE;
	}

	public void setYEAR_END_BONUS(String yEAR_END_BONUS) {
		YEAR_END_BONUS = yEAR_END_BONUS;
	}

	public void setANNUAL(String aNNUAL) {
		ANNUAL = aNNUAL;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public void setCOMM_RATE(String cOMM_RATE) {
		COMM_RATE = cOMM_RATE;
	}

	public String getINKED_NAME() {
		return INKED_NAME;
	}

	public String getLIPPER_ID() {
		return LIPPER_ID;
	}

	public String getPRD_RISK() {
		return PRD_RISK;
	}

	public String getTARGET_ID() {
		return TARGET_ID;
	}

	public void setINKED_NAME(String iNKED_NAME) {
		INKED_NAME = iNKED_NAME;
	}

	public void setLIPPER_ID(String lIPPER_ID) {
		LIPPER_ID = lIPPER_ID;
	}

	public void setPRD_RISK(String pRD_RISK) {
		PRD_RISK = pRD_RISK;
	}

	public void setTARGET_ID(String tARGET_ID) {
		TARGET_ID = tARGET_ID;
	}

	public BigDecimal getCOMPANY_NUM() {
		return COMPANY_NUM;
	}

	public void setCOMPANY_NUM(BigDecimal COMPANY_NUM) {
		this.COMPANY_NUM = COMPANY_NUM;
	}

	public BigDecimal getKYC_SCORE() {
		return KYC_SCORE;
	}

	public void setKYC_SCORE(BigDecimal KYC_SCORE) {
		this.KYC_SCORE = KYC_SCORE;
	}

	public String getTARGET_CURR() {
		return TARGET_CURR;
	}

	public void setTARGET_CURR(String TARGET_CURR) {
		this.TARGET_CURR = TARGET_CURR;
	}

	public String getINT_TYPE() {
		return INT_TYPE;
	}

	public void setINT_TYPE(String INT_TYPE) {
		this.INT_TYPE = INT_TYPE;
	}

	public String getTRANSFER_FLG() {
		return TRANSFER_FLG;
	}

	public void setTRANSFER_FLG(String TRANSFER_FLG) {
		this.TRANSFER_FLG = TRANSFER_FLG;
	}

	public String getCOM_PRD_RISK() {
		return COM_PRD_RISK;
	}

	public void setCOM_PRD_RISK(String cOM_PRD_RISK) {
		COM_PRD_RISK = cOM_PRD_RISK;
	}

	public String getDIVIDEND_YN() {
		return DIVIDEND_YN;
	}

	public void setDIVIDEND_YN(String dIVIDEND_YN) {
		DIVIDEND_YN = dIVIDEND_YN;
	}
	
}
