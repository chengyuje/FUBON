package com.systex.jbranch.app.server.fps.cam170;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM170InputVO extends PagingInputVO{
	
	private String campID;
	private Date importSDate;
	private Date importEDate;
	private String campName;
	private Date sDate;
	private Date eDate;
	private Date eDate2;
	
	private String examVersion;
	private String questionVersion;
	private String answerSEQ;
	
	public String getAnswerSEQ() {
		return answerSEQ;
	}

	public void setAnswerSEQ(String answerSEQ) {
		this.answerSEQ = answerSEQ;
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

	public String getCampID() {
		return campID;
	}
	
	public void setCampID(String campID) {
		this.campID = campID;
	}
	
	public Date getImportSDate() {
		return importSDate;
	}
	
	public void setImportSDate(Date importSDate) {
		this.importSDate = importSDate;
	}
	
	public Date getImportEDate() {
		return importEDate;
	}
	
	public void setImportEDate(Date importEDate) {
		this.importEDate = importEDate;
	}
	
	public String getCampName() {
		return campName;
	}
	
	public void setCampName(String campName) {
		this.campName = campName;
	}
	
	public Date getsDate() {
		return sDate;
	}
	
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	
	public Date geteDate() {
		return eDate;
	}
	
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	
	public Date geteDate2() {
		return eDate2;
	}
	
	public void seteDate2(Date eDate2) {
		this.eDate2 = eDate2;
	}
}
