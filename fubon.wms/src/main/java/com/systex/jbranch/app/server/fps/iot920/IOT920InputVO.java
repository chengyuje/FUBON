package com.systex.jbranch.app.server.fps.iot920;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT920InputVO extends PagingInputVO{
	
	private String curr_status;
	private String in_opr;
	private String SIGN_INC;
	private String INS_KIND;
	private String CUST_ID;
	private String RECRUIT_ID;
	private String INSURED_ID;
	private String REPRESET_ID;
	private String PAYER_ID;
	private String REF_CON_ID;
	private String in_column;
	private String REG_TYPE;
	private Date   APPLY_DATE;            //要保書填寫申請日
	private String CHG_CUST_ID;
	private String PREMATCH_SEQ;
	private String MAPPVIDEO_YN; //是否為電子要保書視訊投保(Y/N)
	private String MAPP_CHKLIST_TYPE; //視訊投保檢核類型
	
	
	public String getREG_TYPE() {
		return REG_TYPE;
	}
	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}
	public String getIn_column() {
		return in_column;
	}
	public void setIn_column(String in_column) {
		this.in_column = in_column;
	}
	public String getRECRUIT_ID() {
		return RECRUIT_ID;
	}
	public void setRECRUIT_ID(String rECRUIT_ID) {
		RECRUIT_ID = rECRUIT_ID;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public String getREPRESET_ID() {
		return REPRESET_ID;
	}
	public void setREPRESET_ID(String rEPRESET_ID) {
		REPRESET_ID = rEPRESET_ID;
	}
	public String getPAYER_ID() {
		return PAYER_ID;
	}
	public void setPAYER_ID(String pAYER_ID) {
		PAYER_ID = pAYER_ID;
	}
	public String getREF_CON_ID() {
		return REF_CON_ID;
	}
	public void setREF_CON_ID(String rEF_CON_ID) {
		REF_CON_ID = rEF_CON_ID;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getCurr_status() {
		return curr_status;
	}
	public void setCurr_status(String curr_status) {
		this.curr_status = curr_status;
	}
	public String getIn_opr() {
		return in_opr;
	}
	public void setIn_opr(String in_opr) {
		this.in_opr = in_opr;
	}
	public String getSIGN_INC() {
		return SIGN_INC;
	}
	public void setSIGN_INC(String sIGN_INC) {
		SIGN_INC = sIGN_INC;
	}
	public String getINS_KIND() {
		return INS_KIND;
	}
	public void setINS_KIND(String iNS_KIND) {
		INS_KIND = iNS_KIND;
	}
	public Date getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public void setAPPLY_DATE(Date aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}
	public String getCHG_CUST_ID() {
		return CHG_CUST_ID;
	}
	public void setCHG_CUST_ID(String cHG_CUST_ID) {
		CHG_CUST_ID = cHG_CUST_ID;
	}
	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}
	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
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
	
	
}
