package com.systex.jbranch.app.server.fps.iot410;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT410InputVO extends PagingInputVO {
	private String PREMATCH_SEQ;
	private String STATUS;
	private String CUST_ID;
	private String INSURED_ID;
	private String PAYER_ID;
	private String C_PREMIUM_TRANSSEQ;
	private String I_PREMIUM_TRANSSEQ;
	private String P_PREMIUM_TRANSSEQ;
	private String TOT_CALL_CNT;
	private String TODAY_CALL_CNT;
	private String FAIL_CALL_CNT;
	private String FAIL_REASON;
	private String FAIL_TYPE1;
	private String FAIL_TYPE2;
	private String CALL_MEMO;
	
	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}

	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getINSURED_ID() {
		return INSURED_ID;
	}

	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}

	public String getPAYER_ID() {
		return PAYER_ID;
	}

	public void setPAYER_ID(String pAYER_ID) {
		PAYER_ID = pAYER_ID;
	}

	public String getC_PREMIUM_TRANSSEQ() {
		return C_PREMIUM_TRANSSEQ;
	}

	public void setC_PREMIUM_TRANSSEQ(String c_PREMIUM_TRANSSEQ) {
		C_PREMIUM_TRANSSEQ = c_PREMIUM_TRANSSEQ;
	}

	public String getI_PREMIUM_TRANSSEQ() {
		return I_PREMIUM_TRANSSEQ;
	}

	public void setI_PREMIUM_TRANSSEQ(String i_PREMIUM_TRANSSEQ) {
		I_PREMIUM_TRANSSEQ = i_PREMIUM_TRANSSEQ;
	}

	public String getP_PREMIUM_TRANSSEQ() {
		return P_PREMIUM_TRANSSEQ;
	}

	public void setP_PREMIUM_TRANSSEQ(String p_PREMIUM_TRANSSEQ) {
		P_PREMIUM_TRANSSEQ = p_PREMIUM_TRANSSEQ;
	}

	public String getTOT_CALL_CNT() {
		return TOT_CALL_CNT;
	}

	public void setTOT_CALL_CNT(String tOT_CALL_CNT) {
		TOT_CALL_CNT = tOT_CALL_CNT;
	}

	public String getTODAY_CALL_CNT() {
		return TODAY_CALL_CNT;
	}

	public void setTODAY_CALL_CNT(String tODAY_CALL_CNT) {
		TODAY_CALL_CNT = tODAY_CALL_CNT;
	}

	public String getFAIL_CALL_CNT() {
		return FAIL_CALL_CNT;
	}

	public void setFAIL_CALL_CNT(String fAIL_CALL_CNT) {
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
	
}
