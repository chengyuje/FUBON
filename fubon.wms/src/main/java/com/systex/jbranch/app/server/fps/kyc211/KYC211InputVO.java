package com.systex.jbranch.app.server.fps.kyc211;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC211InputVO extends PagingInputVO{
	
	private String EXAM_VERSION;
	private String QUEST_TYPE;
	private String EXAM_NAME;
	private String RL_VERSION;
	private List<Map<String,Object>> RISK_LEVEL;
	private List<String> DEL_CUST_RL_ID;
	private Date ACTIVE_DATE;
	private List<Map<String,Object>> update_preview_data;
	private List<Map<String,Object>> DEL_QUESTION;
	private String inExam_Version;
	private String inQuest_Version;
	private Boolean copy;
	private String RS_VERSION;
	private List C_VAL;
	private List W_VAL;
	private List<Map<String,Object>> CUST_RL_ID;
	private BigDecimal INT_SCORE;
	private String RLR_VERSION;
	private List<Map<String,Object>> LRATE_RISK_LEVEL;
	private List<String> LRATE_DEL_CUST_RL_ID;
	
	
	public String getRLR_VERSION() {
		return RLR_VERSION;
	}

	public void setRLR_VERSION(String rLR_VERSION) {
		RLR_VERSION = rLR_VERSION;
	}

	public List<Map<String, Object>> getLRATE_RISK_LEVEL() {
		return LRATE_RISK_LEVEL;
	}

	public void setLRATE_RISK_LEVEL(List<Map<String, Object>> lRATE_RISK_LEVEL) {
		LRATE_RISK_LEVEL = lRATE_RISK_LEVEL;
	}

	public List<String> getLRATE_DEL_CUST_RL_ID() {
		return LRATE_DEL_CUST_RL_ID;
	}

	public void setLRATE_DEL_CUST_RL_ID(List<String> lRATE_DEL_CUST_RL_ID) {
		LRATE_DEL_CUST_RL_ID = lRATE_DEL_CUST_RL_ID;
	}

	public BigDecimal getINT_SCORE() {
		return INT_SCORE;
	}

	public void setINT_SCORE(BigDecimal iNT_SCORE) {
		INT_SCORE = iNT_SCORE;
	}

	public List<Map<String, Object>> getCUST_RL_ID() {
		return CUST_RL_ID;
	}

	public void setCUST_RL_ID(List<Map<String, Object>> cUST_RL_ID) {
		CUST_RL_ID = cUST_RL_ID;
	}

	public List getC_VAL() {
		return C_VAL;
	}

	public void setC_VAL(List c_VAL) {
		C_VAL = c_VAL;
	}

	public List getW_VAL() {
		return W_VAL;
	}

	public void setW_VAL(List w_VAL) {
		W_VAL = w_VAL;
	}

	public String getRS_VERSION() {
		return RS_VERSION;
	}

	public void setRS_VERSION(String rS_VERSION) {
		RS_VERSION = rS_VERSION;
	}
	
	public List<Map<String, Object>> getDEL_QUESTION() {
		return DEL_QUESTION;
	}

	public void setDEL_QUESTION(List<Map<String, Object>> dEL_QUESTION) {
		DEL_QUESTION = dEL_QUESTION;
	}

	public List<String> getDEL_CUST_RL_ID() {
		return DEL_CUST_RL_ID;
	}

	public void setDEL_CUST_RL_ID(List<String> dEL_CUST_RL_ID) {
		DEL_CUST_RL_ID = dEL_CUST_RL_ID;
	}

	public List<Map<String, Object>> getRISK_LEVEL() {
		return RISK_LEVEL;
	}

	public void setRISK_LEVEL(List<Map<String, Object>> rISK_LEVEL) {
		RISK_LEVEL = rISK_LEVEL;
	}

	public Boolean getCopy() {
		return copy;
	}

	public void setCopy(Boolean copy) {
		this.copy = copy;
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

	public String getEXAM_VERSION() {
		return EXAM_VERSION;
	}

	public void setEXAM_VERSION(String eXAM_VERSION) {
		EXAM_VERSION = eXAM_VERSION;
	}

	public String getQUEST_TYPE() {
		return QUEST_TYPE;
	}

	public void setQUEST_TYPE(String qUEST_TYPE) {
		QUEST_TYPE = qUEST_TYPE;
	}

	public String getEXAM_NAME() {
		return EXAM_NAME;
	}

	public void setEXAM_NAME(String eXAM_NAME) {
		EXAM_NAME = eXAM_NAME;
	}

	public String getRL_VERSION() {
		return RL_VERSION;
	}

	public void setRL_VERSION(String rL_VERSION) {
		RL_VERSION = rL_VERSION;
	}

	public Date getACTIVE_DATE() {
		return ACTIVE_DATE;
	}

	public void setACTIVE_DATE(Date aCTIVE_DATE) {
		ACTIVE_DATE = aCTIVE_DATE;
	}

	public List<Map<String, Object>> getUpdate_preview_data() {
		return update_preview_data;
	}

	public void setUpdate_preview_data(List<Map<String, Object>> update_preview_data) {
		this.update_preview_data = update_preview_data;
	}
	
	
}
