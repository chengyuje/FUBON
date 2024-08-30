package com.systex.jbranch.app.server.fps.cam121;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM121InputVO extends PagingInputVO{
	
	private String questionVersion;
	private String questionDesc;
	private String questionType;
	private String moduleCategory;
	private Boolean ansOtherFlag;
	private Boolean ansMemoFlag;
	
	private String answerSEQ;
	private String answerDesc;
	
	private Date sDate;
	private Date eDate;

	public String getModuleCategory() {
		return moduleCategory;
	}

	public void setModuleCategory(String moduleCategory) {
		this.moduleCategory = moduleCategory;
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

	public String getQuestionDesc() {
		return questionDesc;
	}

	public void setQuestionDesc(String questionDesc) {
		this.questionDesc = questionDesc;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Boolean getAnsOtherFlag() {
		return ansOtherFlag;
	}

	public void setAnsOtherFlag(Boolean ansOtherFlag) {
		this.ansOtherFlag = ansOtherFlag;
	}

	public Boolean getAnsMemoFlag() {
		return ansMemoFlag;
	}

	public void setAnsMemoFlag(Boolean ansMemoFlag) {
		this.ansMemoFlag = ansMemoFlag;
	}

	public String getAnswerSEQ() {
		return answerSEQ;
	}

	public void setAnswerSEQ(String answerSEQ) {
		this.answerSEQ = answerSEQ;
	}

	public String getAnswerDesc() {
		return answerDesc;
	}

	public void setAnswerDesc(String answerDesc) {
		this.answerDesc = answerDesc;
	}

	public String getQuestionVersion() {
		return questionVersion;
	}

	public void setQuestionVersion(String questionVersion) {
		this.questionVersion = questionVersion;
	}

}
