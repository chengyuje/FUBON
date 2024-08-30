package com.systex.jbranch.app.server.fps.iot110;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT110InputVO extends PagingInputVO {

	private String empId;			//登入者員編
//	private String represet;		//是否為代理人　是：Y　否：N
//	private String repName;
//	private String salesPerson;
//	private String insCode;
	private String PRODUCT_TYPE;
	private String CURR_CD;
	private String AB_EXCH_RATE;	//非常態交易匯率
	private String UNDER_YN;		//弱勢戶
	private String PRO_YN;			//專業投資人
//	private String FirstBuy_YN;		//是否為首購
//	private String insType;

	private String PREMATCH_SEQ;
	private String REG_TYPE;
	private String OTH_TYPE;
	private String INS_ID;
	private String CASE_ID;
	private String CUST_ID;
	private String PROPOSER_NAME;
	private Timestamp PROPOSER_BIRTH;
	private String PROPOSER_CM_FLAG;
	private String REPRESET_ID;
	private String REPRESET_NAME;
	private String REPRESET_CM_FLAG;
	private String CUST_RISK;
	private Timestamp CUST_RISK_DUE;
	private String AO_CODE;
	private String RLT_BT_PROREP;
	private String INSURED_ID;
	private String INSURED_NAME;
	private String INSURED_CM_FLAG;
	private String PAYER_ID;
	private String PAYER_NAME;
	private String PAYER_CM_FLAG;
	private String RLT_BT_PROPAY;
	private BigDecimal INSPRD_KEYNO;
	private String INSPRD_ID;
	private String PAY_TYPE;
	private BigDecimal REAL_PREMIUM;
	private BigDecimal BASE_PREMIUM;
	private String MOP2;
	private String LOAN_SOURCE_YN;
	private String LOAN_SOURCE2_YN;
	private Timestamp APPLY_DATE;
	private Timestamp MATCH_DATE;
	private String RECRUIT_ID;
	private String POLICY_NO1;
	private String POLICY_NO2;
	private String POLICY_NO3;
	private String AML;
	private String PRECHECK;
	private BigDecimal PROPOSER_INCOME1;
	private BigDecimal PROPOSER_INCOME2;
	private BigDecimal PROPOSER_INCOME3;
	private BigDecimal INSURED_INCOME1;
	private BigDecimal INSURED_INCOME2;
	private BigDecimal INSURED_INCOME3;
	private String LOAN_CHK1_YN;
	private String LOAN_CHK2_YN;
	private Timestamp LOAN_CHK2_DATE;
	private String LOAN_CHK3_YN;
	private String LOAN_CHK4_YN;
	private String CD_CHK_YN;
	private String C_LOAN_CHK1_YN;
	private String C_LOAN_CHK2_YN;
	private Timestamp C_LOAN_CHK2_DATE;
	private String C_LOAN_CHK3_YN;
	private String C_LOAN_CHK4_YN;
	private String C_CD_CHK_YN;
	private String I_LOAN_CHK1_YN;
	private String I_LOAN_CHK2_YN;
	private Timestamp I_LOAN_CHK2_DATE;
	private String I_LOAN_CHK3_YN;
	private String I_LOAN_CHK4_YN;
	private String I_CD_CHK_YN;
	private String INCOME_REMARK;
	private String LOAN_SOURCE_REMARK;
	private String CHG_CUST_ID;
	private String CHG_PROPOSER_NAME;
	private Timestamp CHG_PROPOSER_BIRTH;
	private String STATUS;

	private String CONTRACT_END_YN;
	private String S_INFITEM_LOAN_YN;
	private String PROPOSER_WORK;
	private String INSURED_WORK;
	private Timestamp C_LOAN_APPLY_DATE;
	private Timestamp C_PREM_DATE;
	private Timestamp I_LOAN_APPLY_DATE;
	private Timestamp P_LOAN_APPLY_DATE;
	private String C_LOAN_APPLY_YN;
	private String I_LOAN_APPLY_YN;
	private String P_LOAN_APPLY_YN;

	private String AB_SENIOR_YN;
	private String C_SALE_SENIOR_YN;
	private String I_SALE_SENIOR_YN;
	private String P_SALE_SENIOR_YN;
	private String MAPPVIDEO_YN; //是否為電子要保書視訊投保(Y/N)
	private String MAPP_CHKLIST_TYPE; //視訊投保檢核類型
	private String AGE_UNDER20_YN; //要保人年齡小於20歲
	private Timestamp INSURED_BIRTH;
	private Timestamp PAYER_BIRTH;
	private String INV_SCORE;
	private String C_KYC_INCOME;
	private String I_KYC_INCOME;
	private String SENIOR_AUTH_OPT;
	private String SENIOR_AUTH_OPT2;
	private String SENIOR_AUTH_OPT3;
	private String SENIOR_AUTH_OPT4;
	private String C_SALE_SENIOR_TRANSSEQ;
	private String I_SALE_SENIOR_TRANSSEQ;
	private String P_SALE_SENIOR_TRANSSEQ;
	private String INV_SCORE_CHK;
	private BigDecimal CUST_DEBIT;
	private BigDecimal INSURED_DEBIT;
	private String MAPPVIDEO_AGENTMEMO;

	private String copyInsId;
	private String printSourceRecListYN;
	private String NEED_MATCH;
	private String PRD_RISK;
	private String C_SENIOR_PVAL;
	private String DIGITAL_AGREESIGN_YN;
	private List<Map<String, Object>> INVESTList;
	private String FB_COM_YN;
	private BigDecimal COMPANY_NUM;
	private String AB_YN;
	private Timestamp C_CD_DUE_DATE;
	private Timestamp I_CD_DUE_DATE;
	private Timestamp P_CD_DUE_DATE;
	private String CANCEL_CONTRACT_YN;
	private String DATA_SHR_YN;
	private String SENIOR_OVER_PVAL;
	private String WMSHAIA_SEQ;
	private BigDecimal OVER_PVAL_AMT;
	private BigDecimal OVER_PVAL_MAX_AMT;
	private String CUST_OVER_70;
	private CustHighNetWorthDataVO hnwcDataVO;
	private boolean isNeedCalHnwcRiskValue; //非投資型但須適配商品是否需要越級試算
	private String SENIOR_AUTH_REMARKS;
	private String SENIOR_AUTH_ID;
	private String C_PREMIUM_TRANSSEQ_YN;
	private String I_PREMIUM_TRANSSEQ_YN;
	private String P_PREMIUM_TRANSSEQ_YN;
	private String C_REVOLVING_LOAN_YN;
	private String I_REVOLVING_LOAN_YN;
	private String P_REVOLVING_LOAN_YN;

	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}
	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
//	public String getRepreset() {
//		return represet;
//	}
//	public void setRepreset(String represet) {
//		this.represet = represet;
//	}
//	public String getRepName() {
//		return repName;
//	}
//	public void setRepName(String repName) {
//		this.repName = repName;
//	}
//	public String getSalesPerson() {
//		return salesPerson;
//	}
//	public void setSalesPerson(String salesPerson) {
//		this.salesPerson = salesPerson;
//	}
//	public String getInsCode() {
//		return insCode;
//	}
//	public void setInsCode(String insCode) {
//		this.insCode = insCode;
//	}
	public String getPRODUCT_TYPE() {
		return PRODUCT_TYPE;
	}
	public void setPRODUCT_TYPE(String pRODUCT_TYPE) {
		PRODUCT_TYPE = pRODUCT_TYPE;
	}
	public String getCURR_CD() {
		return CURR_CD;
	}
	public void setCURR_CD(String cURR_CD) {
		CURR_CD = cURR_CD;
	}
	public String getAB_EXCH_RATE() {
		return AB_EXCH_RATE;
	}
	public void setAB_EXCH_RATE(String aB_EXCH_RATE) {
		AB_EXCH_RATE = aB_EXCH_RATE;
	}
	public String getUNDER_YN() {
		return UNDER_YN;
	}
	public void setUNDER_YN(String uNDER_YN) {
		UNDER_YN = uNDER_YN;
	}
	public String getPRO_YN() {
		return PRO_YN;
	}
	public void setPRO_YN(String pRO_YN) {
		PRO_YN = pRO_YN;
	}
//	public String getFirstBuy_YN() {
//		return FirstBuy_YN;
//	}
//	public void setFirstBuy_YN(String firstBuy_YN) {
//		FirstBuy_YN = firstBuy_YN;
//	}
//	public String getInsType() {
//		return insType;
//	}
//	public void setInsType(String insType) {
//		this.insType = insType;
//	}
	public String getREG_TYPE() {
		return REG_TYPE;
	}
	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}
	public String getOTH_TYPE() {
		return OTH_TYPE;
	}
	public void setOTH_TYPE(String oTH_TYPE) {
		OTH_TYPE = oTH_TYPE;
	}
	public String getINS_ID() {
		return INS_ID;
	}
	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}
	public String getCASE_ID() {
		return CASE_ID;
	}
	public void setCASE_ID(String cASE_ID) {
		CASE_ID = cASE_ID;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getPROPOSER_NAME() {
		return PROPOSER_NAME;
	}
	public void setPROPOSER_NAME(String pROPOSER_NAME) {
		PROPOSER_NAME = pROPOSER_NAME;
	}
	public Timestamp getPROPOSER_BIRTH() {
		return PROPOSER_BIRTH;
	}
	public void setPROPOSER_BIRTH(Timestamp pROPOSER_BIRTH) {
		PROPOSER_BIRTH = pROPOSER_BIRTH;
	}
	public String getPROPOSER_CM_FLAG() {
		return PROPOSER_CM_FLAG;
	}
	public void setPROPOSER_CM_FLAG(String pROPOSER_CM_FLAG) {
		PROPOSER_CM_FLAG = pROPOSER_CM_FLAG;
	}
	public String getREPRESET_ID() {
		return REPRESET_ID;
	}
	public void setREPRESET_ID(String rEPRESET_ID) {
		REPRESET_ID = rEPRESET_ID;
	}
	public String getREPRESET_NAME() {
		return REPRESET_NAME;
	}
	public void setREPRESET_NAME(String rEPRESET_NAME) {
		REPRESET_NAME = rEPRESET_NAME;
	}
	public String getREPRESET_CM_FLAG() {
		return REPRESET_CM_FLAG;
	}
	public void setREPRESET_CM_FLAG(String rEPRESET_CM_FLAG) {
		REPRESET_CM_FLAG = rEPRESET_CM_FLAG;
	}
	public String getCUST_RISK() {
		return CUST_RISK;
	}
	public void setCUST_RISK(String cUST_RISK) {
		CUST_RISK = cUST_RISK;
	}
	public Timestamp getCUST_RISK_DUE() {
		return CUST_RISK_DUE;
	}
	public void setCUST_RISK_DUE(Timestamp cUST_RISK_DUE) {
		CUST_RISK_DUE = cUST_RISK_DUE;
	}
	public String getRLT_BT_PROREP() {
		return RLT_BT_PROREP;
	}
	public void setRLT_BT_PROREP(String rLT_BT_PROREP) {
		RLT_BT_PROREP = rLT_BT_PROREP;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public String getINSURED_NAME() {
		return INSURED_NAME;
	}
	public void setINSURED_NAME(String iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}
	public String getINSURED_CM_FLAG() {
		return INSURED_CM_FLAG;
	}
	public void setINSURED_CM_FLAG(String iNSURED_CM_FLAG) {
		INSURED_CM_FLAG = iNSURED_CM_FLAG;
	}
	public String getPAYER_ID() {
		return PAYER_ID;
	}
	public void setPAYER_ID(String pAYER_ID) {
		PAYER_ID = pAYER_ID;
	}
	public String getPAYER_NAME() {
		return PAYER_NAME;
	}
	public void setPAYER_NAME(String pAYER_NAME) {
		PAYER_NAME = pAYER_NAME;
	}
	public String getPAYER_CM_FLAG() {
		return PAYER_CM_FLAG;
	}
	public void setPAYER_CM_FLAG(String pAYER_CM_FLAG) {
		PAYER_CM_FLAG = pAYER_CM_FLAG;
	}
	public String getRLT_BT_PROPAY() {
		return RLT_BT_PROPAY;
	}
	public void setRLT_BT_PROPAY(String rLT_BT_PROPAY) {
		RLT_BT_PROPAY = rLT_BT_PROPAY;
	}
	public BigDecimal getINSPRD_KEYNO() {
		return INSPRD_KEYNO;
	}
	public void setINSPRD_KEYNO(BigDecimal iNSPRD_KEYNO) {
		INSPRD_KEYNO = iNSPRD_KEYNO;
	}
	public String getINSPRD_ID() {
		return INSPRD_ID;
	}
	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}
	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}
	public BigDecimal getREAL_PREMIUM() {
		return REAL_PREMIUM;
	}
	public void setREAL_PREMIUM(BigDecimal rEAL_PREMIUM) {
		REAL_PREMIUM = rEAL_PREMIUM;
	}
	public BigDecimal getBASE_PREMIUM() {
		return BASE_PREMIUM;
	}
	public void setBASE_PREMIUM(BigDecimal bASE_PREMIUM) {
		BASE_PREMIUM = bASE_PREMIUM;
	}
	public String getMOP2() {
		return MOP2;
	}
	public void setMOP2(String mOP2) {
		MOP2 = mOP2;
	}
	public String getLOAN_SOURCE_YN() {
		return LOAN_SOURCE_YN;
	}
	public void setLOAN_SOURCE_YN(String lOAN_SOURCE_YN) {
		LOAN_SOURCE_YN = lOAN_SOURCE_YN;
	}
	public Timestamp getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public void setAPPLY_DATE(Timestamp aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}
	public Timestamp getMATCH_DATE() {
		return MATCH_DATE;
	}
	public void setMATCH_DATE(Timestamp mATCH_DATE) {
		MATCH_DATE = mATCH_DATE;
	}
	public String getPOLICY_NO1() {
		return POLICY_NO1;
	}
	public void setPOLICY_NO1(String pOLICY_NO1) {
		POLICY_NO1 = pOLICY_NO1;
	}
	public String getPOLICY_NO2() {
		return POLICY_NO2;
	}
	public void setPOLICY_NO2(String pOLICY_NO2) {
		POLICY_NO2 = pOLICY_NO2;
	}
	public String getPOLICY_NO3() {
		return POLICY_NO3;
	}
	public void setPOLICY_NO3(String pOLICY_NO3) {
		POLICY_NO3 = pOLICY_NO3;
	}
	public String getAML() {
		return AML;
	}
	public void setAML(String aML) {
		AML = aML;
	}
	public String getPRECHECK() {
		return PRECHECK;
	}
	public void setPRECHECK(String pRECHECK) {
		PRECHECK = pRECHECK;
	}
	public BigDecimal getPROPOSER_INCOME1() {
		return PROPOSER_INCOME1;
	}
	public void setPROPOSER_INCOME1(BigDecimal pROPOSER_INCOME1) {
		PROPOSER_INCOME1 = pROPOSER_INCOME1;
	}
	public BigDecimal getPROPOSER_INCOME2() {
		return PROPOSER_INCOME2;
	}
	public void setPROPOSER_INCOME2(BigDecimal pROPOSER_INCOME2) {
		PROPOSER_INCOME2 = pROPOSER_INCOME2;
	}
	public BigDecimal getPROPOSER_INCOME3() {
		return PROPOSER_INCOME3;
	}
	public void setPROPOSER_INCOME3(BigDecimal pROPOSER_INCOME3) {
		PROPOSER_INCOME3 = pROPOSER_INCOME3;
	}
	public BigDecimal getINSURED_INCOME1() {
		return INSURED_INCOME1;
	}
	public void setINSURED_INCOME1(BigDecimal iNSURED_INCOME1) {
		INSURED_INCOME1 = iNSURED_INCOME1;
	}
	public BigDecimal getINSURED_INCOME2() {
		return INSURED_INCOME2;
	}
	public void setINSURED_INCOME2(BigDecimal iNSURED_INCOME2) {
		INSURED_INCOME2 = iNSURED_INCOME2;
	}
	public BigDecimal getINSURED_INCOME3() {
		return INSURED_INCOME3;
	}
	public void setINSURED_INCOME3(BigDecimal iNSURED_INCOME3) {
		INSURED_INCOME3 = iNSURED_INCOME3;
	}
	public String getLOAN_CHK1_YN() {
		return LOAN_CHK1_YN;
	}
	public void setLOAN_CHK1_YN(String lOAN_CHK1_YN) {
		LOAN_CHK1_YN = lOAN_CHK1_YN;
	}
	public String getLOAN_CHK2_YN() {
		return LOAN_CHK2_YN;
	}
	public void setLOAN_CHK2_YN(String lOAN_CHK2_YN) {
		LOAN_CHK2_YN = lOAN_CHK2_YN;
	}
	public String getCD_CHK_YN() {
		return CD_CHK_YN;
	}
	public void setCD_CHK_YN(String cD_CHK_YN) {
		CD_CHK_YN = cD_CHK_YN;
	}
	public String getLOAN_CHK3_YN() {
		return LOAN_CHK3_YN;
	}
	public void setLOAN_CHK3_YN(String lOAN_CHK3_YN) {
		LOAN_CHK3_YN = lOAN_CHK3_YN;
	}
	public String getC_LOAN_CHK1_YN() {
		return C_LOAN_CHK1_YN;
	}
	public void setC_LOAN_CHK1_YN(String c_LOAN_CHK1_YN) {
		C_LOAN_CHK1_YN = c_LOAN_CHK1_YN;
	}
	public String getC_LOAN_CHK2_YN() {
		return C_LOAN_CHK2_YN;
	}
	public void setC_LOAN_CHK2_YN(String c_LOAN_CHK2_YN) {
		C_LOAN_CHK2_YN = c_LOAN_CHK2_YN;
	}
	public String getC_LOAN_CHK3_YN() {
		return C_LOAN_CHK3_YN;
	}
	public void setC_LOAN_CHK3_YN(String c_LOAN_CHK3_YN) {
		C_LOAN_CHK3_YN = c_LOAN_CHK3_YN;
	}
	public String getC_CD_CHK_YN() {
		return C_CD_CHK_YN;
	}
	public void setC_CD_CHK_YN(String c_CD_CHK_YN) {
		C_CD_CHK_YN = c_CD_CHK_YN;
	}
	public String getI_LOAN_CHK1_YN() {
		return I_LOAN_CHK1_YN;
	}
	public void setI_LOAN_CHK1_YN(String i_LOAN_CHK1_YN) {
		I_LOAN_CHK1_YN = i_LOAN_CHK1_YN;
	}
	public String getI_LOAN_CHK2_YN() {
		return I_LOAN_CHK2_YN;
	}
	public void setI_LOAN_CHK2_YN(String i_LOAN_CHK2_YN) {
		I_LOAN_CHK2_YN = i_LOAN_CHK2_YN;
	}
	public String getI_LOAN_CHK3_YN() {
		return I_LOAN_CHK3_YN;
	}
	public void setI_LOAN_CHK3_YN(String i_LOAN_CHK3_YN) {
		I_LOAN_CHK3_YN = i_LOAN_CHK3_YN;
	}
	public String getI_CD_CHK_YN() {
		return I_CD_CHK_YN;
	}
	public void setI_CD_CHK_YN(String i_CD_CHK_YN) {
		I_CD_CHK_YN = i_CD_CHK_YN;
	}
	public String getINCOME_REMARK() {
		return INCOME_REMARK;
	}
	public void setINCOME_REMARK(String iNCOME_REMARK) {
		INCOME_REMARK = iNCOME_REMARK;
	}
	public String getLOAN_SOURCE_REMARK() {
		return LOAN_SOURCE_REMARK;
	}
	public void setLOAN_SOURCE_REMARK(String lOAN_SOURCE_REMARK) {
		LOAN_SOURCE_REMARK = lOAN_SOURCE_REMARK;
	}
	public String getCHG_CUST_ID() {
		return CHG_CUST_ID;
	}
	public void setCHG_CUST_ID(String cHG_CUST_ID) {
		CHG_CUST_ID = cHG_CUST_ID;
	}
	public String getCHG_PROPOSER_NAME() {
		return CHG_PROPOSER_NAME;
	}
	public void setCHG_PROPOSER_NAME(String cHG_PROPOSER_NAME) {
		CHG_PROPOSER_NAME = cHG_PROPOSER_NAME;
	}
	public Timestamp getCHG_PROPOSER_BIRTH() {
		return CHG_PROPOSER_BIRTH;
	}
	public void setCHG_PROPOSER_BIRTH(Timestamp cHG_PROPOSER_BIRTH) {
		CHG_PROPOSER_BIRTH = cHG_PROPOSER_BIRTH;
	}
	public String getRECRUIT_ID() {
		return RECRUIT_ID;
	}
	public void setRECRUIT_ID(String rECRUIT_ID) {
		RECRUIT_ID = rECRUIT_ID;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getCopyInsId() {
		return copyInsId;
	}
	public void setCopyInsId(String copyInsId) {
		this.copyInsId = copyInsId;
	}
	public String getAO_CODE() {
		return AO_CODE;
	}
	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}
	public String getCONTRACT_END_YN() {
		return CONTRACT_END_YN;
	}
	public void setCONTRACT_END_YN(String cONTRACT_END_YN) {
		CONTRACT_END_YN = cONTRACT_END_YN;
	}
	public String getS_INFITEM_LOAN_YN() {
		return S_INFITEM_LOAN_YN;
	}
	public void setS_INFITEM_LOAN_YN(String s_INFITEM_LOAN_YN) {
		S_INFITEM_LOAN_YN = s_INFITEM_LOAN_YN;
	}
	public String getPROPOSER_WORK() {
		return PROPOSER_WORK;
	}
	public void setPROPOSER_WORK(String pROPOSER_WORK) {
		PROPOSER_WORK = pROPOSER_WORK;
	}
	public String getINSURED_WORK() {
		return INSURED_WORK;
	}
	public void setINSURED_WORK(String iNSURED_WORK) {
		INSURED_WORK = iNSURED_WORK;
	}
	public Timestamp getC_LOAN_APPLY_DATE() {
		return C_LOAN_APPLY_DATE;
	}
	public void setC_LOAN_APPLY_DATE(Timestamp c_LOAN_APPLY_DATE) {
		C_LOAN_APPLY_DATE = c_LOAN_APPLY_DATE;
	}
	public Timestamp getC_PREM_DATE() {
		return C_PREM_DATE;
	}
	public void setC_PREM_DATE(Timestamp c_PREM_DATE) {
		C_PREM_DATE = c_PREM_DATE;
	}
	public Timestamp getI_LOAN_APPLY_DATE() {
		return I_LOAN_APPLY_DATE;
	}
	public void setI_LOAN_APPLY_DATE(Timestamp i_LOAN_APPLY_DATE) {
		I_LOAN_APPLY_DATE = i_LOAN_APPLY_DATE;
	}
	public Timestamp getP_LOAN_APPLY_DATE() {
		return P_LOAN_APPLY_DATE;
	}
	public void setP_LOAN_APPLY_DATE(Timestamp p_LOAN_APPLY_DATE) {
		P_LOAN_APPLY_DATE = p_LOAN_APPLY_DATE;
	}
	public String getC_LOAN_APPLY_YN() {
		return C_LOAN_APPLY_YN;
	}
	public void setC_LOAN_APPLY_YN(String c_LOAN_APPLY_YN) {
		C_LOAN_APPLY_YN = c_LOAN_APPLY_YN;
	}
	public String getI_LOAN_APPLY_YN() {
		return I_LOAN_APPLY_YN;
	}
	public void setI_LOAN_APPLY_YN(String i_LOAN_APPLY_YN) {
		I_LOAN_APPLY_YN = i_LOAN_APPLY_YN;
	}
	public String getP_LOAN_APPLY_YN() {
		return P_LOAN_APPLY_YN;
	}
	public void setP_LOAN_APPLY_YN(String p_LOAN_APPLY_YN) {
		P_LOAN_APPLY_YN = p_LOAN_APPLY_YN;
	}
	public String getLOAN_SOURCE2_YN() {
		return LOAN_SOURCE2_YN;
	}
	public void setLOAN_SOURCE2_YN(String lOAN_SOURCE2_YN) {
		LOAN_SOURCE2_YN = lOAN_SOURCE2_YN;
	}
	public String getAB_SENIOR_YN() {
		return AB_SENIOR_YN;
	}
	public void setAB_SENIOR_YN(String aB_SENIOR_YN) {
		AB_SENIOR_YN = aB_SENIOR_YN;
	}
	public String getC_SALE_SENIOR_YN() {
		return C_SALE_SENIOR_YN;
	}
	public void setC_SALE_SENIOR_YN(String c_SALE_SENIOR_YN) {
		C_SALE_SENIOR_YN = c_SALE_SENIOR_YN;
	}
	public String getI_SALE_SENIOR_YN() {
		return I_SALE_SENIOR_YN;
	}
	public void setI_SALE_SENIOR_YN(String i_SALE_SENIOR_YN) {
		I_SALE_SENIOR_YN = i_SALE_SENIOR_YN;
	}
	public String getP_SALE_SENIOR_YN() {
		return P_SALE_SENIOR_YN;
	}
	public void setP_SALE_SENIOR_YN(String p_SALE_SENIOR_YN) {
		P_SALE_SENIOR_YN = p_SALE_SENIOR_YN;
	}
	public String getLOAN_CHK4_YN() {
		return LOAN_CHK4_YN;
	}
	public void setLOAN_CHK4_YN(String lOAN_CHK4_YN) {
		LOAN_CHK4_YN = lOAN_CHK4_YN;
	}
	public String getC_LOAN_CHK4_YN() {
		return C_LOAN_CHK4_YN;
	}
	public void setC_LOAN_CHK4_YN(String c_LOAN_CHK4_YN) {
		C_LOAN_CHK4_YN = c_LOAN_CHK4_YN;
	}
	public String getI_LOAN_CHK4_YN() {
		return I_LOAN_CHK4_YN;
	}
	public void setI_LOAN_CHK4_YN(String i_LOAN_CHK4_YN) {
		I_LOAN_CHK4_YN = i_LOAN_CHK4_YN;
	}
	public Timestamp getLOAN_CHK2_DATE() {
		return LOAN_CHK2_DATE;
	}
	public void setLOAN_CHK2_DATE(Timestamp lOAN_CHK2_DATE) {
		LOAN_CHK2_DATE = lOAN_CHK2_DATE;
	}
	public Timestamp getC_LOAN_CHK2_DATE() {
		return C_LOAN_CHK2_DATE;
	}
	public void setC_LOAN_CHK2_DATE(Timestamp c_LOAN_CHK2_DATE) {
		C_LOAN_CHK2_DATE = c_LOAN_CHK2_DATE;
	}
	public Timestamp getI_LOAN_CHK2_DATE() {
		return I_LOAN_CHK2_DATE;
	}
	public void setI_LOAN_CHK2_DATE(Timestamp i_LOAN_CHK2_DATE) {
		I_LOAN_CHK2_DATE = i_LOAN_CHK2_DATE;
	}
	public String getMAPPVIDEO_YN() {
		return MAPPVIDEO_YN;
	}
	public void setMAPPVIDEO_YN(String mAPPVIDEO_YN) {
		MAPPVIDEO_YN = mAPPVIDEO_YN;
	}
	public String getMAPP_CHKLIST_TYPE() {
		return MAPP_CHKLIST_TYPE;
	}
	public void setMAPP_CHKLIST_TYPE(String mAPP_CHKLIST_TYPE) {
		MAPP_CHKLIST_TYPE = mAPP_CHKLIST_TYPE;
	}
	public String getAGE_UNDER20_YN() {
		return AGE_UNDER20_YN;
	}
	public void setAGE_UNDER20_YN(String aGE_UNDER20_YN) {
		AGE_UNDER20_YN = aGE_UNDER20_YN;
	}
	public Timestamp getINSURED_BIRTH() {
		return INSURED_BIRTH;
	}
	public void setINSURED_BIRTH(Timestamp iNSURED_BIRTH) {
		INSURED_BIRTH = iNSURED_BIRTH;
	}
	public Timestamp getPAYER_BIRTH() {
		return PAYER_BIRTH;
	}
	public void setPAYER_BIRTH(Timestamp pAYER_BIRTH) {
		PAYER_BIRTH = pAYER_BIRTH;
	}
	public String getINV_SCORE() {
		return INV_SCORE;
	}
	public void setINV_SCORE(String iNV_SCORE) {
		INV_SCORE = iNV_SCORE;
	}
	public String getC_KYC_INCOME() {
		return C_KYC_INCOME;
	}
	public void setC_KYC_INCOME(String c_KYC_INCOME) {
		C_KYC_INCOME = c_KYC_INCOME;
	}
	public String getI_KYC_INCOME() {
		return I_KYC_INCOME;
	}
	public void setI_KYC_INCOME(String i_KYC_INCOME) {
		I_KYC_INCOME = i_KYC_INCOME;
	}
	public String getSENIOR_AUTH_OPT() {
		return SENIOR_AUTH_OPT;
	}
	public void setSENIOR_AUTH_OPT(String sENIOR_AUTH_OPT) {
		SENIOR_AUTH_OPT = sENIOR_AUTH_OPT;
	}
	public String getSENIOR_AUTH_OPT2() {
		return SENIOR_AUTH_OPT2;
	}
	public void setSENIOR_AUTH_OPT2(String sENIOR_AUTH_OPT2) {
		SENIOR_AUTH_OPT2 = sENIOR_AUTH_OPT2;
	}
	public String getSENIOR_AUTH_OPT3() {
		return SENIOR_AUTH_OPT3;
	}
	public void setSENIOR_AUTH_OPT3(String sENIOR_AUTH_OPT3) {
		SENIOR_AUTH_OPT3 = sENIOR_AUTH_OPT3;
	}
	public String getSENIOR_AUTH_OPT4() {
		return SENIOR_AUTH_OPT4;
	}
	public void setSENIOR_AUTH_OPT4(String sENIOR_AUTH_OPT4) {
		SENIOR_AUTH_OPT4 = sENIOR_AUTH_OPT4;
	}
	public String getC_SALE_SENIOR_TRANSSEQ() {
		return C_SALE_SENIOR_TRANSSEQ;
	}
	public void setC_SALE_SENIOR_TRANSSEQ(String c_SALE_SENIOR_TRANSSEQ) {
		C_SALE_SENIOR_TRANSSEQ = c_SALE_SENIOR_TRANSSEQ;
	}
	public String getI_SALE_SENIOR_TRANSSEQ() {
		return I_SALE_SENIOR_TRANSSEQ;
	}
	public void setI_SALE_SENIOR_TRANSSEQ(String i_SALE_SENIOR_TRANSSEQ) {
		I_SALE_SENIOR_TRANSSEQ = i_SALE_SENIOR_TRANSSEQ;
	}
	public String getP_SALE_SENIOR_TRANSSEQ() {
		return P_SALE_SENIOR_TRANSSEQ;
	}
	public void setP_SALE_SENIOR_TRANSSEQ(String p_SALE_SENIOR_TRANSSEQ) {
		P_SALE_SENIOR_TRANSSEQ = p_SALE_SENIOR_TRANSSEQ;
	}
	public String getINV_SCORE_CHK() {
		return INV_SCORE_CHK;
	}
	public void setINV_SCORE_CHK(String iNV_SCORE_CHK) {
		INV_SCORE_CHK = iNV_SCORE_CHK;
	}
	public BigDecimal getCUST_DEBIT() {
		return CUST_DEBIT;
	}
	public void setCUST_DEBIT(BigDecimal cUST_DEBIT) {
		CUST_DEBIT = cUST_DEBIT;
	}
	public BigDecimal getINSURED_DEBIT() {
		return INSURED_DEBIT;
	}
	public void setINSURED_DEBIT(BigDecimal iNSURED_DEBIT) {
		INSURED_DEBIT = iNSURED_DEBIT;
	}
	public String getMAPPVIDEO_AGENTMEMO() {
		return MAPPVIDEO_AGENTMEMO;
	}
	public void setMAPPVIDEO_AGENTMEMO(String mAPPVIDEO_AGENTMEMO) {
		MAPPVIDEO_AGENTMEMO = mAPPVIDEO_AGENTMEMO;
	}
	public String getPrintSourceRecListYN() {
		return printSourceRecListYN;
	}
	public void setPrintSourceRecListYN(String printSourceRecListYN) {
		this.printSourceRecListYN = printSourceRecListYN;
	}
	public String getNEED_MATCH() {
		return NEED_MATCH;
	}
	public void setNEED_MATCH(String nEED_MATCH) {
		NEED_MATCH = nEED_MATCH;
	}
	public String getPRD_RISK() {
		return PRD_RISK;
	}
	public void setPRD_RISK(String pRD_RISK) {
		PRD_RISK = pRD_RISK;
	}
	public String getC_SENIOR_PVAL() {
		return C_SENIOR_PVAL;
	}
	public void setC_SENIOR_PVAL(String c_SENIOR_PVAL) {
		C_SENIOR_PVAL = c_SENIOR_PVAL;
	}
	public String getDIGITAL_AGREESIGN_YN() {
		return DIGITAL_AGREESIGN_YN;
	}
	public void setDIGITAL_AGREESIGN_YN(String dIGITAL_AGREESIGN_YN) {
		DIGITAL_AGREESIGN_YN = dIGITAL_AGREESIGN_YN;
	}
	public List<Map<String, Object>> getINVESTList() {
		return INVESTList;
	}
	public void setINVESTList(List<Map<String, Object>> iNVESTList) {
		INVESTList = iNVESTList;
	}
	public String getFB_COM_YN() {
		return FB_COM_YN;
	}
	public void setFB_COM_YN(String fB_COM_YN) {
		FB_COM_YN = fB_COM_YN;
	}
	public BigDecimal getCOMPANY_NUM() {
		return COMPANY_NUM;
	}
	public void setCOMPANY_NUM(BigDecimal cOMPANY_NUM) {
		COMPANY_NUM = cOMPANY_NUM;
	}
	public String getAB_YN() {
		return AB_YN;
	}
	public void setAB_YN(String aB_YN) {
		AB_YN = aB_YN;
	}
	public Timestamp getC_CD_DUE_DATE() {
		return C_CD_DUE_DATE;
	}
	public void setC_CD_DUE_DATE(Timestamp c_CD_DUE_DATE) {
		C_CD_DUE_DATE = c_CD_DUE_DATE;
	}
	public Timestamp getI_CD_DUE_DATE() {
		return I_CD_DUE_DATE;
	}
	public void setI_CD_DUE_DATE(Timestamp i_CD_DUE_DATE) {
		I_CD_DUE_DATE = i_CD_DUE_DATE;
	}
	public Timestamp getP_CD_DUE_DATE() {
		return P_CD_DUE_DATE;
	}
	public void setP_CD_DUE_DATE(Timestamp p_CD_DUE_DATE) {
		P_CD_DUE_DATE = p_CD_DUE_DATE;
	}
	public String getCANCEL_CONTRACT_YN() {
		return CANCEL_CONTRACT_YN;
	}
	public void setCANCEL_CONTRACT_YN(String cANCEL_CONTRACT_YN) {
		CANCEL_CONTRACT_YN = cANCEL_CONTRACT_YN;
	}
	public String getDATA_SHR_YN() {
		return DATA_SHR_YN;
	}
	public void setDATA_SHR_YN(String dATA_SHR_YN) {
		DATA_SHR_YN = dATA_SHR_YN;
	}
	public String getSENIOR_OVER_PVAL() {
		return SENIOR_OVER_PVAL;
	}
	public void setSENIOR_OVER_PVAL(String sENIOR_OVER_PVAL) {
		SENIOR_OVER_PVAL = sENIOR_OVER_PVAL;
	}
	public String getWMSHAIA_SEQ() {
		return WMSHAIA_SEQ;
	}
	public void setWMSHAIA_SEQ(String wMSHAIA_SEQ) {
		WMSHAIA_SEQ = wMSHAIA_SEQ;
	}
	public BigDecimal getOVER_PVAL_AMT() {
		return OVER_PVAL_AMT;
	}
	public void setOVER_PVAL_AMT(BigDecimal oVER_PVAL_AMT) {
		OVER_PVAL_AMT = oVER_PVAL_AMT;
	}
	public BigDecimal getOVER_PVAL_MAX_AMT() {
		return OVER_PVAL_MAX_AMT;
	}
	public void setOVER_PVAL_MAX_AMT(BigDecimal oVER_PVAL_MAX_AMT) {
		OVER_PVAL_MAX_AMT = oVER_PVAL_MAX_AMT;
	}
	public String getCUST_OVER_70() {
		return CUST_OVER_70;
	}
	public void setCUST_OVER_70(String cUST_OVER_70) {
		CUST_OVER_70 = cUST_OVER_70;
	}
	public CustHighNetWorthDataVO getHnwcDataVO() {
		return hnwcDataVO;
	}
	public void setHnwcDataVO(CustHighNetWorthDataVO hnwcDataVO) {
		this.hnwcDataVO = hnwcDataVO;
	}
	public boolean isNeedCalHnwcRiskValue() {
		return isNeedCalHnwcRiskValue;
	}
	public void setNeedCalHnwcRiskValue(boolean isNeedCalHnwcRiskValue) {
		this.isNeedCalHnwcRiskValue = isNeedCalHnwcRiskValue;
	}
	public String getSENIOR_AUTH_REMARKS() {
		return SENIOR_AUTH_REMARKS;
	}
	public void setSENIOR_AUTH_REMARKS(String sENIOR_AUTH_REMARKS) {
		SENIOR_AUTH_REMARKS = sENIOR_AUTH_REMARKS;
	}
	public String getSENIOR_AUTH_ID() {
		return SENIOR_AUTH_ID;
	}
	public void setSENIOR_AUTH_ID(String sENIOR_AUTH_ID) {
		SENIOR_AUTH_ID = sENIOR_AUTH_ID;
	}
	public String getC_PREMIUM_TRANSSEQ_YN() {
		return C_PREMIUM_TRANSSEQ_YN;
	}
	public void setC_PREMIUM_TRANSSEQ_YN(String c_PREMIUM_TRANSSEQ_YN) {
		C_PREMIUM_TRANSSEQ_YN = c_PREMIUM_TRANSSEQ_YN;
	}
	public String getI_PREMIUM_TRANSSEQ_YN() {
		return I_PREMIUM_TRANSSEQ_YN;
	}
	public void setI_PREMIUM_TRANSSEQ_YN(String i_PREMIUM_TRANSSEQ_YN) {
		I_PREMIUM_TRANSSEQ_YN = i_PREMIUM_TRANSSEQ_YN;
	}
	public String getP_PREMIUM_TRANSSEQ_YN() {
		return P_PREMIUM_TRANSSEQ_YN;
	}
	public void setP_PREMIUM_TRANSSEQ_YN(String p_PREMIUM_TRANSSEQ_YN) {
		P_PREMIUM_TRANSSEQ_YN = p_PREMIUM_TRANSSEQ_YN;
	}
	public String getC_REVOLVING_LOAN_YN() {
		return C_REVOLVING_LOAN_YN;
	}
	public void setC_REVOLVING_LOAN_YN(String c_REVOLVING_LOAN_YN) {
		C_REVOLVING_LOAN_YN = c_REVOLVING_LOAN_YN;
	}
	public String getI_REVOLVING_LOAN_YN() {
		return I_REVOLVING_LOAN_YN;
	}
	public void setI_REVOLVING_LOAN_YN(String i_REVOLVING_LOAN_YN) {
		I_REVOLVING_LOAN_YN = i_REVOLVING_LOAN_YN;
	}
	public String getP_REVOLVING_LOAN_YN() {
		return P_REVOLVING_LOAN_YN;
	}
	public void setP_REVOLVING_LOAN_YN(String p_REVOLVING_LOAN_YN) {
		P_REVOLVING_LOAN_YN = p_REVOLVING_LOAN_YN;
	}

}
