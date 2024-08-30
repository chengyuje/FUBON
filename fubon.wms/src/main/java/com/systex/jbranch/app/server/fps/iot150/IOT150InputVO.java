package com.systex.jbranch.app.server.fps.iot150;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT150InputVO extends PagingInputVO{
	private String OP_BATCH_NO;
	private String REG_TYPE;
	private String STATUS;
	private String BRANCH_NBR;
	private String INS_ID;
	private String B_OVER_DAYS;
	private String H_OVER_DAYS;
	private String CUST_ID;
	private Date KEYIN_DATE_F;
	private Date KEYIN_DATE_T;
	private Date APPLY_DATE_F;
	private Date APPLY_DATE_T;
	private String POLICY_NO1;
	private String POLICY_NO2;
	private String POLICY_NO3;
	private String PROD_NAME;
	private String PROD_ALL;
	private String BEF_SIGN_OPRID;
	private Date BEF_SIGN_DATE;
	private String SIGN_OPRID;
	private Date SIGN_DATE;
	private String AFT_SIGN_OPRID;
	private Date AFT_SIGN_DATE;
	private String in_opr;
	private String curr_status;
	private List<Map<String, Object>> IOT_MAINList;
	private String caseId;
	private String BATCH_SETUP_EMPID;
	private Date BATCH_SETUP_DATE;
	
	public List<Map<String, Object>> getIOT_MAINList() {
		return IOT_MAINList;
	}
	public void setIOT_MAINList(List<Map<String, Object>> iOT_MAINList) {
		IOT_MAINList = iOT_MAINList;
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
	public String getOP_BATCH_NO() {
		return OP_BATCH_NO;
	}
	public void setOP_BATCH_NO(String oP_BATCH_NO) {
		OP_BATCH_NO = oP_BATCH_NO;
	}
	public String getREG_TYPE() {
		return REG_TYPE;
	}
	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	public String getINS_ID() {
		return INS_ID;
	}
	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}
	public String getB_OVER_DAYS() {
		return B_OVER_DAYS;
	}
	public void setB_OVER_DAYS(String b_OVER_DAYS) {
		B_OVER_DAYS = b_OVER_DAYS;
	}
	public String getH_OVER_DAYS() {
		return H_OVER_DAYS;
	}
	public void setH_OVER_DAYS(String h_OVER_DAYS) {
		H_OVER_DAYS = h_OVER_DAYS;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public Date getKEYIN_DATE_F() {
		return KEYIN_DATE_F;
	}
	public void setKEYIN_DATE_F(Date kEYIN_DATE_F) {
		KEYIN_DATE_F = kEYIN_DATE_F;
	}
	public Date getKEYIN_DATE_T() {
		return KEYIN_DATE_T;
	}
	public void setKEYIN_DATE_T(Date kEYIN_DATE_T) {
		KEYIN_DATE_T = kEYIN_DATE_T;
	}
	public Date getAPPLY_DATE_F() {
		return APPLY_DATE_F;
	}
	public void setAPPLY_DATE_F(Date aPPLY_DATE_F) {
		APPLY_DATE_F = aPPLY_DATE_F;
	}
	public Date getAPPLY_DATE_T() {
		return APPLY_DATE_T;
	}
	public void setAPPLY_DATE_T(Date aPPLY_DATE_T) {
		APPLY_DATE_T = aPPLY_DATE_T;
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
	public String getPROD_NAME() {
		return PROD_NAME;
	}
	public void setPROD_NAME(String pROD_NAME) {
		PROD_NAME = pROD_NAME;
	}
	public String getPROD_ALL() {
		return PROD_ALL;
	}
	public void setPROD_ALL(String pROD_ALL) {
		PROD_ALL = pROD_ALL;
	}
	public String getBEF_SIGN_OPRID() {
		return BEF_SIGN_OPRID;
	}
	public void setBEF_SIGN_OPRID(String bEF_SIGN_OPRID) {
		BEF_SIGN_OPRID = bEF_SIGN_OPRID;
	}
	public Date getBEF_SIGN_DATE() {
		return BEF_SIGN_DATE;
	}
	public void setBEF_SIGN_DATE(Date bEF_SIGN_DATE) {
		BEF_SIGN_DATE = bEF_SIGN_DATE;
	}
	public String getSIGN_OPRID() {
		return SIGN_OPRID;
	}
	public void setSIGN_OPRID(String sIGN_OPRID) {
		SIGN_OPRID = sIGN_OPRID;
	}
	public Date getSIGN_DATE() {
		return SIGN_DATE;
	}
	public void setSIGN_DATE(Date sIGN_DATE) {
		SIGN_DATE = sIGN_DATE;
	}
	public String getAFT_SIGN_OPRID() {
		return AFT_SIGN_OPRID;
	}
	public void setAFT_SIGN_OPRID(String aFT_SIGN_OPRID) {
		AFT_SIGN_OPRID = aFT_SIGN_OPRID;
	}
	public Date getAFT_SIGN_DATE() {
		return AFT_SIGN_DATE;
	}
	public void setAFT_SIGN_DATE(Date aFT_SIGN_DATE) {
		AFT_SIGN_DATE = aFT_SIGN_DATE;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getBATCH_SETUP_EMPID() {
		return BATCH_SETUP_EMPID;
	}
	public void setBATCH_SETUP_EMPID(String bATCH_SETUP_EMPID) {
		BATCH_SETUP_EMPID = bATCH_SETUP_EMPID;
	}
	public Date getBATCH_SETUP_DATE() {
		return BATCH_SETUP_DATE;
	}
	public void setBATCH_SETUP_DATE(Date bATCH_SETUP_DATE) {
		BATCH_SETUP_DATE = bATCH_SETUP_DATE;
	}
}
