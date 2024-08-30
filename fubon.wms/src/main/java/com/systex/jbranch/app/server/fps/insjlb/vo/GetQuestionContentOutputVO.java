package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.math.BigDecimal;

public class GetQuestionContentOutputVO {

	private String custId; 				// 客戶編號
	private String questionnaireId; 	// 問卷編號
	private String qplanName; 			// 問卷名稱
	private String answer1; 			// 保險需求問卷之問題一
	private String answer2; 			// 保險需求問卷之問題二
	private String manswer1; 			// 醫療需求問卷之問題一
	private String manswer2; 			// 醫療需求問卷之問題二
	private String manswer3; 			// 醫療需求問卷之問題三
	private String manswer4; 			// 醫療需求問卷之問題四
	private String manswer5; 			// 醫療需求問卷之問題五
	private String manswer6; 			// 醫療需求問卷之問題六
	private BigDecimal sickScore; 		// 醫療規劃分數
	private BigDecimal cancerScore; 	// 癌症規劃分數
	private BigDecimal dreadSickScore; 	// 重症規劃分數
	private String haveMQuestion; 		// 是否包含醫療問卷
	private String qresult; 			// 問卷結果
	private String planMType2; 			// 醫療規劃下一步驟(2)
	private String planMType3; 			// 醫療規劃下一步驟(3)
	private String note; 				// 問卷備註
	
	public GetQuestionContentOutputVO() {
		super();
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getQuestionnaireId() {
		return questionnaireId;
	}
	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}
	public String getQplanName() {
		return qplanName;
	}
	public void setQplanName(String qplanName) {
		this.qplanName = qplanName;
	}
	public String getAnswer1() {
		return answer1;
	}
	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}
	public String getAnswer2() {
		return answer2;
	}
	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}
	public BigDecimal getSickScore() {
		return sickScore;
	}
	public void setSickScore(BigDecimal sickScore) {
		this.sickScore = sickScore;
	}
	public BigDecimal getCancerScore() {
		return cancerScore;
	}
	public void setCancerScore(BigDecimal cancerScore) {
		this.cancerScore = cancerScore;
	}
	public BigDecimal getDreadSickScore() {
		return dreadSickScore;
	}
	public void setDreadSickScore(BigDecimal dreadSickScore) {
		this.dreadSickScore = dreadSickScore;
	}
	public String getHaveMQuestion() {
		return haveMQuestion;
	}
	public void setHaveMQuestion(String haveMQuestion) {
		this.haveMQuestion = haveMQuestion;
	}
	public String getPlanMType2() {
		return planMType2;
	}
	public void setPlanMType2(String planMType2) {
		this.planMType2 = planMType2;
	}
	public String getPlanMType3() {
		return planMType3;
	}
	public void setPlanMType3(String planMType3) {
		this.planMType3 = planMType3;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getManswer1() {
		return manswer1;
	}
	public void setManswer1(String manswer1) {
		this.manswer1 = manswer1;
	}
	public String getManswer2() {
		return manswer2;
	}
	public void setManswer2(String manswer2) {
		this.manswer2 = manswer2;
	}
	public String getManswer3() {
		return manswer3;
	}
	public void setManswer3(String manswer3) {
		this.manswer3 = manswer3;
	}
	public String getManswer4() {
		return manswer4;
	}
	public void setManswer4(String manswer4) {
		this.manswer4 = manswer4;
	}
	public String getManswer5() {
		return manswer5;
	}
	public void setManswer5(String manswer5) {
		this.manswer5 = manswer5;
	}
	public String getManswer6() {
		return manswer6;
	}
	public void setManswer6(String manswer6) {
		this.manswer6 = manswer6;
	}
	public String getQresult() {
		return qresult;
	}
	public void setQresult(String qresult) {
		this.qresult = qresult;
	}
}
