package com.systex.jbranch.app.server.fps.kyc510;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC510InputVO extends PagingInputVO{
	
	private String EXAM_NAME;
	private String EXAM_VERSION;
	private Date LASTUPDATE;
	private String PRO_TYPE;
	private String STATUS;
	private String Delete_Data;
	
	//edit
	private String RL_VERSION;
	private List<Map<String,Object>> RISK_LEVEL;
	private List<String> DEL_CUST_RL_ID;
	private Date ACTIVE_DATE;
	private Date EXPIRY_DATE;
	private List<Map<String,Object>> update_preview_data;
	private List<Map<String,Object>> DEL_QUESTION;
	private String inExam_Version;
	private String inQuest_Version;
	private Boolean copy;

	//Questions
	private String QUESTION_DESC;
	private Date sDate;
	private Date eDate;
	private String QUESTION_VERSION;
	private List<Map<String,Object>> selectData;
	private List<Map<String,Object>> preview_data;
	private String CORRECT_ANS;

	
	public String getDelete_Data() {
		return Delete_Data;
	}
	public void setDelete_Data(String delete_Data) {
		Delete_Data = delete_Data;
	}
	public String getEXAM_NAME() {
		return EXAM_NAME;
	}
	public void setEXAM_NAME(String eXAM_NAME) {
		EXAM_NAME = eXAM_NAME;
	}
	public String getEXAM_VERSION() {
		return EXAM_VERSION;
	}
	public void setEXAM_VERSION(String eXAM_VERSION) {
		EXAM_VERSION = eXAM_VERSION;
	}
	public Date getLASTUPDATE() {
		return LASTUPDATE;
	}
	public void setLASTUPDATE(Date lASTUPDATE) {
		LASTUPDATE = lASTUPDATE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getRL_VERSION() {
		return RL_VERSION;
	}
	public void setRL_VERSION(String rL_VERSION) {
		RL_VERSION = rL_VERSION;
	}
	public List<Map<String, Object>> getRISK_LEVEL() {
		return RISK_LEVEL;
	}
	public void setRISK_LEVEL(List<Map<String, Object>> rISK_LEVEL) {
		RISK_LEVEL = rISK_LEVEL;
	}
	public List<String> getDEL_CUST_RL_ID() {
		return DEL_CUST_RL_ID;
	}
	public void setDEL_CUST_RL_ID(List<String> dEL_CUST_RL_ID) {
		DEL_CUST_RL_ID = dEL_CUST_RL_ID;
	}
	public Date getACTIVE_DATE() {
		return ACTIVE_DATE;
	}
	public void setACTIVE_DATE(Date ACTIVE_DATE) {
		this.ACTIVE_DATE = ACTIVE_DATE;
	}
	public List<Map<String, Object>> getUpdate_preview_data() {
		return update_preview_data;
	}
	public void setUpdate_preview_data(List<Map<String, Object>> update_preview_data) {
		this.update_preview_data = update_preview_data;
	}
	public List<Map<String, Object>> getDEL_QUESTION() {
		return DEL_QUESTION;
	}
	public void setDEL_QUESTION(List<Map<String, Object>> dEL_QUESTION) {
		DEL_QUESTION = dEL_QUESTION;
	}
	public String getInExam_Version() {
		return inExam_Version;
	}
	public void setInExam_Version(String inExam_Version) {
		this.inExam_Version = inExam_Version;
	}
	public String getInQuest_Version() {
		return inQuest_Version;
	}
	public void setInQuest_Version(String inQuest_Version) {
		this.inQuest_Version = inQuest_Version;
	}
	public Boolean getCopy() {
		return copy;
	}
	public void setCopy(Boolean copy) {
		this.copy = copy;
	}
	public String getQUESTION_DESC() {
		return QUESTION_DESC;
	}
	public void setQUESTION_DESC(String qUESTION_DESC) {
		QUESTION_DESC = qUESTION_DESC;
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
	public String getQUESTION_VERSION() {
		return QUESTION_VERSION;
	}
	public void setQUESTION_VERSION(String qUESTION_VERSION) {
		QUESTION_VERSION = qUESTION_VERSION;
	}
	public List<Map<String, Object>> getSelectData() {
		return selectData;
	}
	public void setSelectData(List<Map<String, Object>> selectData) {
		this.selectData = selectData;
	}
	public List<Map<String, Object>> getPreview_data() {
		return preview_data;
	}
	public void setPreview_data(List<Map<String, Object>> preview_data) {
		this.preview_data = preview_data;
	}
	public String getPRO_TYPE() {
		return PRO_TYPE;
	}
	public void setPRO_TYPE(String pRO_TYPE) {
		PRO_TYPE = pRO_TYPE;
	}
	public String getCORRECT_ANS() {
		return CORRECT_ANS;
	}
	public void setCORRECT_ANS(String cORRECT_ANS) {
		CORRECT_ANS = cORRECT_ANS;
	}
	public Date getEXPIRY_DATE() {
		return EXPIRY_DATE;
	}
	public void setEXPIRY_DATE(Date eXPIRY_DATE) {
		EXPIRY_DATE = eXPIRY_DATE;
	}

}
