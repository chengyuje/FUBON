package com.systex.jbranch.app.server.fps.cam200;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM200InputVO extends PagingInputVO{
	
	private String custID;
	private String custName;
	private String campID;
	private String stepID;
	private String docID;
	private String leadType;
	
	private String onefileName;
	private byte[] attach;
	
	private String[] sfaLeadIDList;
	private String[] leadStatusList;
	
	private String cmuType;
	private String vstRecTextFormat;
	private String visitMemo;
	
	private String closedCmuType;
	private String closedVstRecTextFormat;
	private String closedVisitMemo;
	
	private String responseCode;
	
	//問卷
	private String examVersion;
	private String questionVersion;
	private List<Map<String, String>> questionnaireList;
	
	private Date visitDate;
	private Date visitTime;
	private String visitCreply;
	private Date closedVisitDate;
	private Date closedVisitTime;
	private String closedVisitCreply;
	
	// == 0000415: WMS-CR-20200908-01_自動產製線上留資名單報表及留資客戶承作消金商品導入消金Pipeline功能
	private String estPrd;
	private BigDecimal estAmt;
	private String planEmpID;
	
	private String branchID;
	
	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public String getEstPrd() {
		return estPrd;
	}

	public void setEstPrd(String estPrd) {
		this.estPrd = estPrd;
	}

	public BigDecimal getEstAmt() {
		return estAmt;
	}

	public void setEstAmt(BigDecimal estAmt) {
		this.estAmt = estAmt;
	}

	public String getPlanEmpID() {
		return planEmpID;
	}

	public void setPlanEmpID(String planEmpID) {
		this.planEmpID = planEmpID;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public Date getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}

	public String getVisitCreply() {
		return visitCreply;
	}

	public void setVisitCreply(String visitCreply) {
		this.visitCreply = visitCreply;
	}

	public Date getClosedVisitDate() {
		return closedVisitDate;
	}

	public void setClosedVisitDate(Date closedVisitDate) {
		this.closedVisitDate = closedVisitDate;
	}

	public Date getClosedVisitTime() {
		return closedVisitTime;
	}

	public void setClosedVisitTime(Date closedVisitTime) {
		this.closedVisitTime = closedVisitTime;
	}

	public String getClosedVisitCreply() {
		return closedVisitCreply;
	}

	public void setClosedVisitCreply(String closedVisitCreply) {
		this.closedVisitCreply = closedVisitCreply;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getLeadType() {
		return leadType;
	}

	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}

	public List<Map<String, String>> getQuestionnaireList() {
		return questionnaireList;
	}

	public void setQuestionnaireList(List<Map<String, String>> questionnaireList) {
		this.questionnaireList = questionnaireList;
	}

	public String getQuestionVersion() {
		return questionVersion;
	}

	public void setQuestionVersion(String questionVersion) {
		this.questionVersion = questionVersion;
	}

	public String getExamVersion() {
		return examVersion;
	}

	public void setExamVersion(String examVersion) {
		this.examVersion = examVersion;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getClosedCmuType() {
		return closedCmuType;
	}

	public void setClosedCmuType(String closedCmuType) {
		this.closedCmuType = closedCmuType;
	}

	public String getClosedVstRecTextFormat() {
		return closedVstRecTextFormat;
	}

	public void setClosedVstRecTextFormat(String closedVstRecTextFormat) {
		this.closedVstRecTextFormat = closedVstRecTextFormat;
	}

	public String getClosedVisitMemo() {
		return closedVisitMemo;
	}

	public void setClosedVisitMemo(String closedVisitMemo) {
		this.closedVisitMemo = closedVisitMemo;
	}

	public String getCmuType() {
		return cmuType;
	}

	public String[] getSfaLeadIDList() {
		return sfaLeadIDList;
	}

	public void setSfaLeadIDList(String[] sfaLeadIDList) {
		this.sfaLeadIDList = sfaLeadIDList;
	}

	public void setCmuType(String cmuType) {
		this.cmuType = cmuType;
	}

	public String getVstRecTextFormat() {
		return vstRecTextFormat;
	}

	public void setVstRecTextFormat(String vstRecTextFormat) {
		this.vstRecTextFormat = vstRecTextFormat;
	}

	public String getVisitMemo() {
		return visitMemo;
	}

	public void setVisitMemo(String visitMemo) {
		this.visitMemo = visitMemo;
	}

	public String[] getLeadStatusList() {
		return leadStatusList;
	}

	public void setLeadStatusList(String[] leadStatusList) {
		this.leadStatusList = leadStatusList;
	}

	public String getOnefileName() {
		return onefileName;
	}

	public void setOnefileName(String onefileName) {
		this.onefileName = onefileName;
	}

	public byte[] getAttach() {
		return attach;
	}

	public void setAttach(byte[] attach) {
		this.attach = attach;
	}

	public String getDocID() {
		return docID;
	}

	public void setDocID(String docID) {
		this.docID = docID;
	}

	public String getStepID() {
		return stepID;
	}

	public void setStepID(String stepID) {
		this.stepID = stepID;
	}

	public String getCampID() {
		return campID;
	}

	public void setCampID(String campID) {
		this.campID = campID;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}
	
}
