package com.systex.jbranch.app.server.fps.crm1242;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM1242InputVO extends PagingInputVO{
	private List<Map<String, Object>> list ;
	
	private String branch_nbr;
	private Date meetingDate;
	private String FA_FLAG;
	private String IA_FLAG;
	private String CUST_ID;
	private String AO_CODE;
	private String SEQ;
	private String SEQ2;
	private String meetingTimeS;
	private String meetingTimeE;
	private String meetingType; // M日期約訪時間起
	private String visit_purpose;
	private String visit_purpose_other;
	private String key_issue;
	private String MODE;
	private List<String> AOlist ;
	private String LoginID;
	private String priID;
	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public Date getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(Date meetingDate) {
		this.meetingDate = meetingDate;
	}

	public String getMeetingTimeS() {
		return meetingTimeS;
	}

	public void setMeetingTimeS(String meetingTimeS) {
		this.meetingTimeS = meetingTimeS;
	}

	public String getFA_FLAG() {
		return FA_FLAG;
	}

	public void setFA_FLAG(String fA_FLAG) {
		FA_FLAG = fA_FLAG;
	}

	public String getIA_FLAG() {
		return IA_FLAG;
	}

	public void setIA_FLAG(String iA_FLAG) {
		IA_FLAG = iA_FLAG;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String CUST_ID) {
		this.CUST_ID = CUST_ID;
	}

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String SEQ) {
		this.SEQ = SEQ;
	}

	public String getMeetingTimeE() {
		return meetingTimeE;
	}

	public void setMeetingTimeE(String meetingTimeE) {
		this.meetingTimeE = meetingTimeE;
	}

	public String getVisit_purpose() {
		return visit_purpose;
	}

	public void setVisit_purpose(String visit_purpose) {
		this.visit_purpose = visit_purpose;
	}

	public String getVisit_purpose_other() {
		return visit_purpose_other;
	}

	public void setVisit_purpose_other(String visit_purpose_other) {
		this.visit_purpose_other = visit_purpose_other;
	}

	public String getKey_issue() {
		return key_issue;
	}

	public void setKey_issue(String key_issue) {
		this.key_issue = key_issue;
	}

	public String getAO_CODE() {
		return AO_CODE;
	}

	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}

	public String getMeetingType() {
		return meetingType;
	}

	public void setMeetingType(String meetingType) {
		this.meetingType = meetingType;
	}

	public String getMODE() {
		return MODE;
	}

	public void setMODE(String mODE) {
		MODE = mODE;
	}

	public String getSEQ2() {
		return SEQ2;
	}

	public void setSEQ2(String sEQ2) {
		SEQ2 = sEQ2;
	}

	public List<String> getAOlist() {
		return AOlist;
	}

	public void setAOlist(List<String> aOlist) {
		AOlist = aOlist;
	}

	public String getLoginID() {
		return LoginID;
	}

	public void setLoginID(String loginID) {
		LoginID = loginID;
	}

	public String getPriID() {
		return priID;
	}

	public void setPriID(String priID) {
		this.priID = priID;
	}
	
	
	
	
}
