package com.systex.jbranch.app.server.fps.pms201;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS201InputVO extends PagingInputVO{
	
	private String eTime;
	private String ao_code;
	private String branch_nbr;
	
	private String empID;
	private String selfNote;
	
	private String conDegree;
	private String targetType;
	
	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getConDegree() {
		return conDegree;
	}

	public void setConDegree(String conDegree) {
		this.conDegree = conDegree;
	}

	public String getSelfNote() {
		return selfNote;
	}

	public void setSelfNote(String selfNote) {
		this.selfNote = selfNote;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

//	private String AO_CODE;    //理專
//	private String EMP_ID;     //理專員編
//	private String EMP_NAME;   //理專信明
//	private String CERT_FLAG;
//	private String SALE_FLAG;  
//	private String VISIT_FLAG;    
//	private String TEL_FLAG;    
//	private String RP_FLAG; 
//	private String FINTRVW_RATE; 
//	private String FCNTCT_CNT;
//
//	private String FPRD_RATE;	
//	private String FTXN_RATE;	  
//	private String FROA_INT_RATE;
//	private String FROA_NON_RATE;
//	private String SELF_NOTE;
//	private String SUP_NOTE;
//	private String AO_JOB_RANK;
//	private String SALES_SUP_EMP_NAME;
	
	public String geteTime() {
		return eTime;
	}
	
	public void seteTime(String eTime) {
		this.eTime = eTime;
	}
//	public String getSALES_SUP_EMP_NAME() {
//		return SALES_SUP_EMP_NAME;
//	}
//
//	public void setSALES_SUP_EMP_NAME(String sALES_SUP_EMP_NAME) {
//		SALES_SUP_EMP_NAME = sALES_SUP_EMP_NAME;
//	}
//

//
//	public String getEMP_NAME() {
//		return EMP_NAME;
//	}
//	
//	public void setEMP_NAME(String eMP_NAME) {
//		EMP_NAME = eMP_NAME;
//	}
//
//	public String getAO_CODE() {
//		return AO_CODE;
//	}
//
//	public String getEMP_ID() {
//		return EMP_ID;
//	}
//
//	public String getCERT_FLAG() {
//		return CERT_FLAG;
//	}
//
//	public String getSALE_FLAG() {
//		return SALE_FLAG;
//	}
//
//	public String getVISIT_FLAG() {
//		return VISIT_FLAG;
//	}
//
//	public String getTEL_FLAG() {
//		return TEL_FLAG;
//	}
//
//	public String getRP_FLAG() {
//		return RP_FLAG;
//	}
//
//	public String getFINTRVW_RATE() {
//		return FINTRVW_RATE;
//	}
//
//	public String getFCNTCT_CNT() {
//		return FCNTCT_CNT;
//	}
//
//	public String getFPRD_RATE() {
//		return FPRD_RATE;
//	}
//
//	public String getFTXN_RATE() {
//		return FTXN_RATE;
//	}
//
//	public String getFROA_INT_RATE() {
//		return FROA_INT_RATE;
//	}
//
//	public String getFROA_NON_RATE() {
//		return FROA_NON_RATE;
//	}
//
//	public void setEMP_ID(String eMP_ID) {
//		EMP_ID = eMP_ID;
//	}
//
//	public void setCERT_FLAG(String cERT_FLAG) {
//		CERT_FLAG = cERT_FLAG;
//	}
//
//	public void setSALE_FLAG(String sALE_FLAG) {
//		SALE_FLAG = sALE_FLAG;
//	}
//
//	public void setVISIT_FLAG(String vISIT_FLAG) {
//		VISIT_FLAG = vISIT_FLAG;
//	}
//
//	public void setTEL_FLAG(String tEL_FLAG) {
//		TEL_FLAG = tEL_FLAG;
//	}
//
//	public void setRP_FLAG(String rP_FLAG) {
//		RP_FLAG = rP_FLAG;
//	}
//
//	public void setFINTRVW_RATE(String fINTRVW_RATE) {
//		FINTRVW_RATE = fINTRVW_RATE;
//	}
//
//	public void setFCNTCT_CNT(String fCNTCT_CNT) {
//		FCNTCT_CNT = fCNTCT_CNT;
//	}
//
//	public void setFPRD_RATE(String fPRD_RATE) {
//		FPRD_RATE = fPRD_RATE;
//	}
//
//	public void setFTXN_RATE(String fTXN_RATE) {
//		FTXN_RATE = fTXN_RATE;
//	}
//
//	public void setFROA_INT_RATE(String fROA_INT_RATE) {
//		FROA_INT_RATE = fROA_INT_RATE;
//	}
//
//	public void setFROA_NON_RATE(String fROA_NON_RATE) {
//		FROA_NON_RATE = fROA_NON_RATE;
//	}
//
//	public void setAO_CODE(String aO_CODE) {
//		AO_CODE = aO_CODE;
//	}
//
//	public String getSELF_NOTE() {
//		return SELF_NOTE;
//	}
//
//	public String getSUP_NOTE() {
//		return SUP_NOTE;
//	}
//
//	public void setSELF_NOTE(String sELF_NOTE) {
//		SELF_NOTE = sELF_NOTE;
//	}
//
//	public void setSUP_NOTE(String sUP_NOTE) {
//		SUP_NOTE = sUP_NOTE;
//	}
//
//	public String getAO_JOB_RANK() {
//		return AO_JOB_RANK;
//	}
//
//	public void setAO_JOB_RANK(String aO_JOB_RANK) {
//		AO_JOB_RANK = aO_JOB_RANK;
//	} 
//	
//	
//

}
