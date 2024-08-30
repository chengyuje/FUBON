package com.systex.jbranch.app.server.fps.iot400;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT400InputVO extends PagingInputVO {
	private String PREMATCH_SEQ;
	private String CASE_ID;
	private String fileName;
	private String fileRealName;
	private int SEQ;
	
	private String STATUS;
//	private String CALL_TYPE;
	private String C_CALL_TYPE;
	private String I_CALL_TYPE;
	private String P_CALL_TYPE;
	private String INSPRD_TYPE;
	private String C_NEED_CALL_YN;
	private String CUST_ID;
	private String PROPOSER_NAME;
	private Date PROPOSER_BIRTH;
	private String REPRESET_ID;
	private String REPRESET_NAME;
	private Date REPRESET_BIRTH;
	private String C_TEL_NO;
	private String C_MOBILE;
	private String C_TIME;
	private String I_NEED_CALL_YN;
	private String INSURED_ID;
	private String INSURED_NAME;
	private Date INSURED_BIRTH;
	private String I_REPRESET_ID;
	private String I_REPRESET_NAME;
	private Date I_REPRESET_BIRTH;
	private String I_TEL_NO;
	private String I_MOBILE;
	private String I_TIME;
	private String P_NEED_CALL_YN;
	private String PAYER_ID;
	private String PAYER_NAME;
	private Date PAYER_BIRTH;
	private String P_REPRESET_ID;
	private String P_REPRESET_NAME;
	private Date P_REPRESET_BIRTH;
	private String P_TEL_NO;
	private String P_MOBILE;
	private String P_TIME;
	private String SPECIAL_MEMO;
	private int TOT_CALL_CNT;
	private int TODAY_CALL_CNT;
	private int FAIL_CALL_CNT;
	private String FAIL_REASON;
	private String FAIL_TYPE1;
	private String FAIL_TYPE2;
	private String CALL_MEMO;
	private String REJECT_REASON;

	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}

	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}

	public String getCASE_ID() {
		return CASE_ID;
	}

	public void setCASE_ID(String cASE_ID) {
		CASE_ID = cASE_ID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileRealName() {
		return fileRealName;
	}

	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}

	public int getSEQ() {
		return SEQ;
	}

	public void setSEQ(int sEQ) {
		SEQ = sEQ;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getC_CALL_TYPE() {
		return C_CALL_TYPE;
	}

	public void setC_CALL_TYPE(String c_CALL_TYPE) {
		C_CALL_TYPE = c_CALL_TYPE;
	}

	public String getI_CALL_TYPE() {
		return I_CALL_TYPE;
	}

	public void setI_CALL_TYPE(String i_CALL_TYPE) {
		I_CALL_TYPE = i_CALL_TYPE;
	}

	public String getP_CALL_TYPE() {
		return P_CALL_TYPE;
	}

	public void setP_CALL_TYPE(String p_CALL_TYPE) {
		P_CALL_TYPE = p_CALL_TYPE;
	}

	public String getINSPRD_TYPE() {
		return INSPRD_TYPE;
	}

	public void setINSPRD_TYPE(String iNSPRD_TYPE) {
		INSPRD_TYPE = iNSPRD_TYPE;
	}

	public String getC_NEED_CALL_YN() {
		return C_NEED_CALL_YN;
	}

	public void setC_NEED_CALL_YN(String c_NEED_CALL_YN) {
		C_NEED_CALL_YN = c_NEED_CALL_YN;
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

	public Date getPROPOSER_BIRTH() {
		return PROPOSER_BIRTH;
	}

	public void setPROPOSER_BIRTH(Date pROPOSER_BIRTH) {
		PROPOSER_BIRTH = pROPOSER_BIRTH;
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

	public Date getREPRESET_BIRTH() {
		return REPRESET_BIRTH;
	}

	public void setREPRESET_BIRTH(Date rEPRESET_BIRTH) {
		REPRESET_BIRTH = rEPRESET_BIRTH;
	}

	public String getC_TEL_NO() {
		return C_TEL_NO;
	}

	public void setC_TEL_NO(String c_TEL_NO) {
		C_TEL_NO = c_TEL_NO;
	}

	public String getC_MOBILE() {
		return C_MOBILE;
	}

	public void setC_MOBILE(String c_MOBILE) {
		C_MOBILE = c_MOBILE;
	}

	public String getC_TIME() {
		return C_TIME;
	}

	public void setC_TIME(String c_TIME) {
		C_TIME = c_TIME;
	}

	public String getI_NEED_CALL_YN() {
		return I_NEED_CALL_YN;
	}

	public void setI_NEED_CALL_YN(String i_NEED_CALL_YN) {
		I_NEED_CALL_YN = i_NEED_CALL_YN;
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

	public Date getINSURED_BIRTH() {
		return INSURED_BIRTH;
	}

	public void setINSURED_BIRTH(Date iNSURED_BIRTH) {
		INSURED_BIRTH = iNSURED_BIRTH;
	}

	public String getI_REPRESET_ID() {
		return I_REPRESET_ID;
	}

	public void setI_REPRESET_ID(String i_REPRESET_ID) {
		I_REPRESET_ID = i_REPRESET_ID;
	}

	public String getI_REPRESET_NAME() {
		return I_REPRESET_NAME;
	}

	public void setI_REPRESET_NAME(String i_REPRESET_NAME) {
		I_REPRESET_NAME = i_REPRESET_NAME;
	}

	public Date getI_REPRESET_BIRTH() {
		return I_REPRESET_BIRTH;
	}

	public void setI_REPRESET_BIRTH(Date i_REPRESET_BIRTH) {
		I_REPRESET_BIRTH = i_REPRESET_BIRTH;
	}

	public String getI_TEL_NO() {
		return I_TEL_NO;
	}

	public void setI_TEL_NO(String i_TEL_NO) {
		I_TEL_NO = i_TEL_NO;
	}

	public String getI_MOBILE() {
		return I_MOBILE;
	}

	public void setI_MOBILE(String i_MOBILE) {
		I_MOBILE = i_MOBILE;
	}

	public String getI_TIME() {
		return I_TIME;
	}

	public void setI_TIME(String i_TIME) {
		I_TIME = i_TIME;
	}

	public String getP_NEED_CALL_YN() {
		return P_NEED_CALL_YN;
	}

	public void setP_NEED_CALL_YN(String p_NEED_CALL_YN) {
		P_NEED_CALL_YN = p_NEED_CALL_YN;
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

	public Date getPAYER_BIRTH() {
		return PAYER_BIRTH;
	}

	public void setPAYER_BIRTH(Date pAYER_BIRTH) {
		PAYER_BIRTH = pAYER_BIRTH;
	}

	public String getP_REPRESET_ID() {
		return P_REPRESET_ID;
	}

	public void setP_REPRESET_ID(String p_REPRESET_ID) {
		P_REPRESET_ID = p_REPRESET_ID;
	}

	public String getP_REPRESET_NAME() {
		return P_REPRESET_NAME;
	}

	public void setP_REPRESET_NAME(String p_REPRESET_NAME) {
		P_REPRESET_NAME = p_REPRESET_NAME;
	}

	public Date getP_REPRESET_BIRTH() {
		return P_REPRESET_BIRTH;
	}

	public void setP_REPRESET_BIRTH(Date p_REPRESET_BIRTH) {
		P_REPRESET_BIRTH = p_REPRESET_BIRTH;
	}

	public String getP_TEL_NO() {
		return P_TEL_NO;
	}

	public void setP_TEL_NO(String p_TEL_NO) {
		P_TEL_NO = p_TEL_NO;
	}

	public String getP_MOBILE() {
		return P_MOBILE;
	}

	public void setP_MOBILE(String p_MOBILE) {
		P_MOBILE = p_MOBILE;
	}

	public String getP_TIME() {
		return P_TIME;
	}

	public void setP_TIME(String p_TIME) {
		P_TIME = p_TIME;
	}

	public String getSPECIAL_MEMO() {
		return SPECIAL_MEMO;
	}

	public void setSPECIAL_MEMO(String sPECIAL_MEMO) {
		SPECIAL_MEMO = sPECIAL_MEMO;
	}

	public int getTOT_CALL_CNT() {
		return TOT_CALL_CNT;
	}

	public void setTOT_CALL_CNT(int tOT_CALL_CNT) {
		TOT_CALL_CNT = tOT_CALL_CNT;
	}

	public int getTODAY_CALL_CNT() {
		return TODAY_CALL_CNT;
	}

	public void setTODAY_CALL_CNT(int tODAY_CALL_CNT) {
		TODAY_CALL_CNT = tODAY_CALL_CNT;
	}

	public int getFAIL_CALL_CNT() {
		return FAIL_CALL_CNT;
	}

	public void setFAIL_CALL_CNT(int fAIL_CALL_CNT) {
		FAIL_CALL_CNT = fAIL_CALL_CNT;
	}

	public String getFAIL_REASON() {
		return FAIL_REASON;
	}

	public void setFAIL_REASON(String fAIL_REASON) {
		FAIL_REASON = fAIL_REASON;
	}

	public String getFAIL_TYPE1() {
		return FAIL_TYPE1;
	}

	public void setFAIL_TYPE1(String fAIL_TYPE1) {
		FAIL_TYPE1 = fAIL_TYPE1;
	}

	public String getFAIL_TYPE2() {
		return FAIL_TYPE2;
	}

	public void setFAIL_TYPE2(String fAIL_TYPE2) {
		FAIL_TYPE2 = fAIL_TYPE2;
	}

	public String getCALL_MEMO() {
		return CALL_MEMO;
	}

	public void setCALL_MEMO(String cALL_MEMO) {
		CALL_MEMO = cALL_MEMO;
	}

	public String getREJECT_REASON() {
		return REJECT_REASON;
	}

	public void setREJECT_REASON(String rEJECT_REASON) {
		REJECT_REASON = rEJECT_REASON;
	}
	
}
