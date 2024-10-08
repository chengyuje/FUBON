package com.systex.jbranch.app.server.fps.iot130;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT130InputVO extends PagingInputVO{

	
	private String OTH_TYPE;
	private Date APPLY_DATE;
	private String CUST_ID;
	private String PROPOSER_NAME;
	private Date PROPOSER_BIRDAY;
	private String INSURED_ID;
	private String INSURED_NAME;
	private String RECRUIT_ID;
	private String REPRESET_ID;
	private String REPRESET_NAME;
	private String REPRESET_CUST;
	private String AO_CODE;
	private String PROPOSER_CM_FLAG;
	private String INSURED_CM_FLAG;
	private String REPRESET_CM_FLAG;
	private String reg_type;
	private String POLICY_NO1;
	private String POLICY_NO2;
	private String POLICY_NO3;
	private String INSPRD_ID;
	private String INSPRD_KEYNO;
	private String CNCT_NAME;
	private String TERMINATED_INC;
	private String STATUS;
	private String OPR_STATUS;
	private String INS_KEYNO;
	private String FX_PROD;
	private String EX_CUST_ID;
	private String EX_PROPOSER_NAME;
	private Date CONTRAT_DATE;
	private String REMARK_INS;
	private String MATCH_DATE;
	private String PROPOSER_RISK;
	private boolean editOTH_TYPE;
	private List<Map<String, Object>> MatchList;
	private List<Map<String, Object>> inList;
	private List<Map<String, Object>> outList;
	private String PREMATCH_SEQ;
	private Date GUILD_RPT_DATE;
	private String LOAN_PRD_YN;
	private String QC_IMMI;
	private String NOT_PASS_REASON;
	private String PREMIUM_TRANSSEQ;
	private String QC_PROPOSER_CHG;
	private String PREMIUM_USAGE;
	private String PAY_WAY;
	private String PAYER_ID;
	private String RLT_BT_PROPAY;
	private Date DOC_KEYIN_DATE;
	private String LOAN_SOURCE_YN;
	private String VALID_CHG_CUST_YN;
	private String FB_COM_YN;
	private BigDecimal COMPANY_NUM;
	private String REVISE_CONFIRM_YN;
	private String OTH_FUND_PURPOSE_1;
	private String OTH_FUND_PURPOSE_2;
	private String OTH_FUND_PURPOSE_3;
	private String OTH_FUND_PURPOSE_4;
	private String OTH_FUND_PURPOSE_5;
	private String OTH_FUND_PURPOSE_6;
	private String OTH_FUND_PURPOSE_RMK_1;
	private String OTH_FUND_PURPOSE_RMK_2;
	
	public boolean isEditOTH_TYPE() {
		return editOTH_TYPE;
	}

	public void setEditOTH_TYPE(boolean editOTH_TYPE) {
		this.editOTH_TYPE = editOTH_TYPE;
	}

	public List<Map<String, Object>> getMatchList() {
		return MatchList;
	}

	public void setMatchList(List<Map<String, Object>> matchList) {
		MatchList = matchList;
	}

	public String getMATCH_DATE() {
		return MATCH_DATE;
	}

	public void setMATCH_DATE(String mATCH_DATE) {
		MATCH_DATE = mATCH_DATE;
	}

	public String getPROPOSER_RISK() {
		return PROPOSER_RISK;
	}

	public void setPROPOSER_RISK(String pROPOSER_RISK) {
		PROPOSER_RISK = pROPOSER_RISK;
	}
	public String getREMARK_INS() {
		return REMARK_INS;
	}
	public void setREMARK_INS(String rEMARK_INS) {
		REMARK_INS = rEMARK_INS;
	}
	public String getFX_PROD() {
		return FX_PROD;
	}
	public String getEX_CUST_ID() {
		return EX_CUST_ID;
	}
	public String getEX_PROPOSER_NAME() {
		return EX_PROPOSER_NAME;
	}
	public Date getCONTRAT_DATE() {
		return CONTRAT_DATE;
	}
	public void setFX_PROD(String fX_PROD) {
		FX_PROD = fX_PROD;
	}
	public void setEX_CUST_ID(String eX_CUST_ID) {
		EX_CUST_ID = eX_CUST_ID;
	}
	public void setEX_PROPOSER_NAME(String eX_PROPOSER_NAME) {
		EX_PROPOSER_NAME = eX_PROPOSER_NAME;
	}
	public void setCONTRAT_DATE(Date cONTRAT_DATE) {
		CONTRAT_DATE = cONTRAT_DATE;
	}
	public String getINS_KEYNO() {
		return INS_KEYNO;
	}
	public void setINS_KEYNO(String iNS_KEYNO) {
		INS_KEYNO = iNS_KEYNO;
	}
	public String getINSPRD_KEYNO() {
		return INSPRD_KEYNO;
	}
	public void setINSPRD_KEYNO(String iNSPRD_KEYNO) {
		INSPRD_KEYNO = iNSPRD_KEYNO;
	}
	public String getCNCT_NAME() {
		return CNCT_NAME;
	}
	public void setCNCT_NAME(String cNCT_NAME) {
		CNCT_NAME = cNCT_NAME;
	}
	public String getAO_CODE() {
		return AO_CODE;
	}
	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}
	public String getPROPOSER_CM_FLAG() {
		return PROPOSER_CM_FLAG;
	}
	public void setPROPOSER_CM_FLAG(String pROPOSER_CM_FLAG) {
		PROPOSER_CM_FLAG = pROPOSER_CM_FLAG;
	}
	public String getINSURED_CM_FLAG() {
		return INSURED_CM_FLAG;
	}
	public void setINSURED_CM_FLAG(String iNSURED_CM_FLAG) {
		INSURED_CM_FLAG = iNSURED_CM_FLAG;
	}
	public String getREPRESET_CM_FLAG() {
		return REPRESET_CM_FLAG;
	}
	public void setREPRESET_CM_FLAG(String rEPRESET_CM_FLAG) {
		REPRESET_CM_FLAG = rEPRESET_CM_FLAG;
	}
	public String getINSURED_NAME() {
		return INSURED_NAME;
	}
	public void setINSURED_NAME(String iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}
	public String getOPR_STATUS() {
		return OPR_STATUS;
	}
	public void setOPR_STATUS(String oPR_STATUS) {
		OPR_STATUS = oPR_STATUS;
	}
	public List<Map<String, Object>> getInList() {
		return inList;
	}
	public void setInList(List<Map<String, Object>> inList) {
		this.inList = inList;
	}
	public List<Map<String, Object>> getOutList() {
		return outList;
	}
	public void setOutList(List<Map<String, Object>> outList) {
		this.outList = outList;
	}
	public String getOTH_TYPE() {
		return OTH_TYPE;
	}
	public void setOTH_TYPE(String oTH_TYPE) {
		OTH_TYPE = oTH_TYPE;
	}
	public Date getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public void setAPPLY_DATE(Date aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
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
	public Date getPROPOSER_BIRDAY() {
		return PROPOSER_BIRDAY;
	}
	public void setPROPOSER_BIRDAY(Date pROPOSER_BIRDAY) {
		PROPOSER_BIRDAY = pROPOSER_BIRDAY;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public String getRECRUIT_ID() {
		return RECRUIT_ID;
	}
	public void setRECRUIT_ID(String rECRUIT_ID) {
		RECRUIT_ID = rECRUIT_ID;
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
	public String getREPRESET_CUST() {
		return REPRESET_CUST;
	}
	public void setREPRESET_CUST(String rEPRESET_CUST) {
		REPRESET_CUST = rEPRESET_CUST;
	}
	public String getReg_type() {
		return reg_type;
	}
	public void setReg_type(String reg_type) {
		this.reg_type = reg_type;
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

	public String getINSPRD_ID() {
		return INSPRD_ID;
	}
	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}
	public String getTERMINATED_INC() {
		return TERMINATED_INC;
	}
	public void setTERMINATED_INC(String tERMINATED_INC) {
		TERMINATED_INC = tERMINATED_INC;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}

	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}

	public Date getGUILD_RPT_DATE() {
		return GUILD_RPT_DATE;
	}

	public void setGUILD_RPT_DATE(Date gUILD_RPT_DATE) {
		GUILD_RPT_DATE = gUILD_RPT_DATE;
	}

	public String getLOAN_PRD_YN() {
		return LOAN_PRD_YN;
	}

	public void setLOAN_PRD_YN(String lOAN_PRD_YN) {
		LOAN_PRD_YN = lOAN_PRD_YN;
	}

	public String getQC_IMMI() {
		return QC_IMMI;
	}

	public void setQC_IMMI(String qC_IMMI) {
		QC_IMMI = qC_IMMI;
	}

	public String getNOT_PASS_REASON() {
		return NOT_PASS_REASON;
	}

	public void setNOT_PASS_REASON(String nOT_PASS_REASON) {
		NOT_PASS_REASON = nOT_PASS_REASON;
	}

	public String getPREMIUM_TRANSSEQ() {
		return PREMIUM_TRANSSEQ;
	}

	public void setPREMIUM_TRANSSEQ(String pREMIUM_TRANSSEQ) {
		PREMIUM_TRANSSEQ = pREMIUM_TRANSSEQ;
	}

	public String getQC_PROPOSER_CHG() {
		return QC_PROPOSER_CHG;
	}

	public void setQC_PROPOSER_CHG(String qC_PROPOSER_CHG) {
		QC_PROPOSER_CHG = qC_PROPOSER_CHG;
	}

	public String getPREMIUM_USAGE() {
		return PREMIUM_USAGE;
	}

	public void setPREMIUM_USAGE(String pREMIUM_USAGE) {
		PREMIUM_USAGE = pREMIUM_USAGE;
	}

	public String getPAY_WAY() {
		return PAY_WAY;
	}

	public void setPAY_WAY(String pAY_WAY) {
		PAY_WAY = pAY_WAY;
	}

	public String getPAYER_ID() {
		return PAYER_ID;
	}

	public void setPAYER_ID(String pAYER_ID) {
		PAYER_ID = pAYER_ID;
	}

	public String getRLT_BT_PROPAY() {
		return RLT_BT_PROPAY;
	}

	public void setRLT_BT_PROPAY(String rLT_BT_PROPAY) {
		RLT_BT_PROPAY = rLT_BT_PROPAY;
	}

	public Date getDOC_KEYIN_DATE() {
		return DOC_KEYIN_DATE;
	}

	public void setDOC_KEYIN_DATE(Date dOC_KEYIN_DATE) {
		DOC_KEYIN_DATE = dOC_KEYIN_DATE;
	}

	public String getLOAN_SOURCE_YN() {
		return LOAN_SOURCE_YN;
	}

	public void setLOAN_SOURCE_YN(String lOAN_SOURCE_YN) {
		LOAN_SOURCE_YN = lOAN_SOURCE_YN;
	}

	public String getVALID_CHG_CUST_YN() {
		return VALID_CHG_CUST_YN;
	}

	public void setVALID_CHG_CUST_YN(String vALID_CHG_CUST_YN) {
		VALID_CHG_CUST_YN = vALID_CHG_CUST_YN;
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

	public String getREVISE_CONFIRM_YN() {
		return REVISE_CONFIRM_YN;
	}

	public void setREVISE_CONFIRM_YN(String rEVISE_CONFIRM_YN) {
		REVISE_CONFIRM_YN = rEVISE_CONFIRM_YN;
	}

	public String getOTH_FUND_PURPOSE_1() {
		return OTH_FUND_PURPOSE_1;
	}

	public void setOTH_FUND_PURPOSE_1(String oTH_FUND_PURPOSE_1) {
		OTH_FUND_PURPOSE_1 = oTH_FUND_PURPOSE_1;
	}

	public String getOTH_FUND_PURPOSE_2() {
		return OTH_FUND_PURPOSE_2;
	}

	public void setOTH_FUND_PURPOSE_2(String oTH_FUND_PURPOSE_2) {
		OTH_FUND_PURPOSE_2 = oTH_FUND_PURPOSE_2;
	}

	public String getOTH_FUND_PURPOSE_3() {
		return OTH_FUND_PURPOSE_3;
	}

	public void setOTH_FUND_PURPOSE_3(String oTH_FUND_PURPOSE_3) {
		OTH_FUND_PURPOSE_3 = oTH_FUND_PURPOSE_3;
	}

	public String getOTH_FUND_PURPOSE_4() {
		return OTH_FUND_PURPOSE_4;
	}

	public void setOTH_FUND_PURPOSE_4(String oTH_FUND_PURPOSE_4) {
		OTH_FUND_PURPOSE_4 = oTH_FUND_PURPOSE_4;
	}

	public String getOTH_FUND_PURPOSE_5() {
		return OTH_FUND_PURPOSE_5;
	}

	public void setOTH_FUND_PURPOSE_5(String oTH_FUND_PURPOSE_5) {
		OTH_FUND_PURPOSE_5 = oTH_FUND_PURPOSE_5;
	}

	public String getOTH_FUND_PURPOSE_6() {
		return OTH_FUND_PURPOSE_6;
	}

	public void setOTH_FUND_PURPOSE_6(String oTH_FUND_PURPOSE_6) {
		OTH_FUND_PURPOSE_6 = oTH_FUND_PURPOSE_6;
	}

	public String getOTH_FUND_PURPOSE_RMK_1() {
		return OTH_FUND_PURPOSE_RMK_1;
	}

	public void setOTH_FUND_PURPOSE_RMK_1(String oTH_FUND_PURPOSE_RMK_1) {
		OTH_FUND_PURPOSE_RMK_1 = oTH_FUND_PURPOSE_RMK_1;
	}

	public String getOTH_FUND_PURPOSE_RMK_2() {
		return OTH_FUND_PURPOSE_RMK_2;
	}

	public void setOTH_FUND_PURPOSE_RMK_2(String oTH_FUND_PURPOSE_RMK_2) {
		OTH_FUND_PURPOSE_RMK_2 = oTH_FUND_PURPOSE_RMK_2;
	}
	
}
