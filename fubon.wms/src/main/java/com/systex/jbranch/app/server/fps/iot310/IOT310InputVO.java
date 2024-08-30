package com.systex.jbranch.app.server.fps.iot310;

import java.sql.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT310InputVO extends PagingInputVO{
	
	
	private String INS_KEYNO          ;
	private String INS_KIND           ;
	private String INS_ID             ;
	private String POLICY_NO          ;
	private String REG_TYPE           ;
	private String BRANCH_NBR         ;
	private String CUST_ID            ;
	private String PROPOSER_NAME      ;
	private String INSURED_ID         ;
	private String INSURED_NAME       ;
	private String PPT_TYPE           ;
	private Date APPLY_DATE         ;
	private String REAL_PREMIUM       ;
	private String BATCH_INFO_KEYNO   ;
	private String REF_CON_ID         ;
	private String RECRUIT_ID         ;
	private String STATUS             ;
	private String PPT_DOC            ;
	private String PPT_DOC_OTHER      ;
	private String UNOPEN_ACCT        ;
	private String VERSION            ;
	private String in_OprStatus       ;
	private List<Integer> DOC_SEQ;
	private String DELETE_ID          ;
	private Date DELETE_DATE        ;
	private Date KEYIN_DATE         ;
	private String BEF_SIGN_NO        ;
	private String BEF_SIGN_OPRID     ;
//	private Date BEF_SIGN_DATE      ;
	private String SIGN_NO            ;
	private String SIGN_OPRID         ;
	private Date SIGN_DATE          ;
	private String AFT_SIGN_OPRID     ;
	private Date AFT_SIGN_DATE      ;
	private String REMARK_BANK        ;
	private String REJ_REASON         ;
	private String REJ_REASON_OTH     ;
	public String getINS_KEYNO() {
		return INS_KEYNO;
	}
	public String getINS_KIND() {
		return INS_KIND;
	}
	public String getINS_ID() {
		return INS_ID;
	}
	public String getPOLICY_NO() {
		return POLICY_NO;
	}
	public String getREG_TYPE() {
		return REG_TYPE;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public String getPROPOSER_NAME() {
		return PROPOSER_NAME;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public String getINSURED_NAME() {
		return INSURED_NAME;
	}
	public String getPPT_TYPE() {
		return PPT_TYPE;
	}
	public Date getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public String getREAL_PREMIUM() {
		return REAL_PREMIUM;
	}
	public String getBATCH_INFO_KEYNO() {
		return BATCH_INFO_KEYNO;
	}
	public String getREF_CON_ID() {
		return REF_CON_ID;
	}
	public String getRECRUIT_ID() {
		return RECRUIT_ID;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public String getPPT_DOC() {
		return PPT_DOC;
	}
	public String getIn_OprStatus() {
		return in_OprStatus;
	}
	public void setIn_OprStatus(String in_OprStatus) {
		this.in_OprStatus = in_OprStatus;
	}
	public String getPPT_DOC_OTHER() {
		return PPT_DOC_OTHER;
	}
	public String getUNOPEN_ACCT() {
		return UNOPEN_ACCT;
	}
	public String getVERSION() {
		return VERSION;
	}
	public String getDELETE_ID() {
		return DELETE_ID;
	}
	public Date getDELETE_DATE() {
		return DELETE_DATE;
	}
	public Date getKEYIN_DATE() {
		return KEYIN_DATE;
	}
	public String getBEF_SIGN_NO() {
		return BEF_SIGN_NO;
	}
	public String getBEF_SIGN_OPRID() {
		return BEF_SIGN_OPRID;
	}
//	public Date getBEF_SIGN_DATE() {
//		return BEF_SIGN_DATE;
//	}
	public String getSIGN_NO() {
		return SIGN_NO;
	}
	public String getSIGN_OPRID() {
		return SIGN_OPRID;
	}
	public Date getSIGN_DATE() {
		return SIGN_DATE;
	}
	public String getAFT_SIGN_OPRID() {
		return AFT_SIGN_OPRID;
	}
	public Date getAFT_SIGN_DATE() {
		return AFT_SIGN_DATE;
	}
	public String getREMARK_BANK() {
		return REMARK_BANK;
	}
	public String getREJ_REASON() {
		return REJ_REASON;
	}
	public String getREJ_REASON_OTH() {
		return REJ_REASON_OTH;
	}
	public void setINS_KEYNO(String iNS_KEYNO) {
		INS_KEYNO = iNS_KEYNO;
	}
	public void setINS_KIND(String iNS_KIND) {
		INS_KIND = iNS_KIND;
	}
	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}
	public void setPOLICY_NO(String pOLICY_NO) {
		POLICY_NO = pOLICY_NO;
	}
	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public void setPROPOSER_NAME(String pROPOSER_NAME) {
		PROPOSER_NAME = pROPOSER_NAME;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}

	public List<Integer> getDOC_SEQ() {
		return DOC_SEQ;
	}
	public void setDOC_SEQ(List<Integer> dOC_SEQ) {
		DOC_SEQ = dOC_SEQ;
	}
	public void setINSURED_NAME(String iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}
	public void setPPT_TYPE(String pPT_TYPE) {
		PPT_TYPE = pPT_TYPE;
	}
	public void setAPPLY_DATE(Date aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}
	public void setREAL_PREMIUM(String rEAL_PREMIUM) {
		REAL_PREMIUM = rEAL_PREMIUM;
	}
	public void setBATCH_INFO_KEYNO(String bATCH_INFO_KEYNO) {
		BATCH_INFO_KEYNO = bATCH_INFO_KEYNO;
	}
	public void setREF_CON_ID(String rEF_CON_ID) {
		REF_CON_ID = rEF_CON_ID;
	}
	public void setRECRUIT_ID(String rECRUIT_ID) {
		RECRUIT_ID = rECRUIT_ID;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public void setPPT_DOC(String pPT_DOC) {
		PPT_DOC = pPT_DOC;
	}
	public void setPPT_DOC_OTHER(String pPT_DOC_OTHER) {
		PPT_DOC_OTHER = pPT_DOC_OTHER;
	}
	public void setUNOPEN_ACCT(String uNOPEN_ACCT) {
		UNOPEN_ACCT = uNOPEN_ACCT;
	}
	public void setVERSION(String vERSION) {
		VERSION = vERSION;
	}
	public void setDELETE_ID(String dELETE_ID) {
		DELETE_ID = dELETE_ID;
	}
	public void setDELETE_DATE(Date dELETE_DATE) {
		DELETE_DATE = dELETE_DATE;
	}
	public void setKEYIN_DATE(Date kEYIN_DATE) {
		KEYIN_DATE = kEYIN_DATE;
	}
	public void setBEF_SIGN_NO(String bEF_SIGN_NO) {
		BEF_SIGN_NO = bEF_SIGN_NO;
	}
	public void setBEF_SIGN_OPRID(String bEF_SIGN_OPRID) {
		BEF_SIGN_OPRID = bEF_SIGN_OPRID;
	}
//	public void setBEF_SIGN_DATE(Date bEF_SIGN_DATE) {
//		BEF_SIGN_DATE = bEF_SIGN_DATE;
//	}
	public void setSIGN_NO(String sIGN_NO) {
		SIGN_NO = sIGN_NO;
	}
	public void setSIGN_OPRID(String sIGN_OPRID) {
		SIGN_OPRID = sIGN_OPRID;
	}
	public void setSIGN_DATE(Date sIGN_DATE) {
		SIGN_DATE = sIGN_DATE;
	}
	public void setAFT_SIGN_OPRID(String aFT_SIGN_OPRID) {
		AFT_SIGN_OPRID = aFT_SIGN_OPRID;
	}
	public void setAFT_SIGN_DATE(Date aFT_SIGN_DATE) {
		AFT_SIGN_DATE = aFT_SIGN_DATE;
	}
	public void setREMARK_BANK(String rEMARK_BANK) {
		REMARK_BANK = rEMARK_BANK;
	}
	public void setREJ_REASON(String rEJ_REASON) {
		REJ_REASON = rEJ_REASON;
	}
	public void setREJ_REASON_OTH(String rEJ_REASON_OTH) {
		REJ_REASON_OTH = rEJ_REASON_OTH;
	}
	
	
	

}
