package com.systex.jbranch.app.server.fps.iot920;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class chk_CTInputVO extends PagingInputVO{
	
	private String EMP_ID;
	private String CERT_TYPE;
	private String TRAINING_TYPE;
	private Date APPLY_DATE;
	private Date DOC_KEYIN_DATE;
	
	
	public Date getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public void setAPPLY_DATE(Date aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public String getCERT_TYPE() {
		return CERT_TYPE;
	}
	public String getTRAINING_TYPE() {
		return TRAINING_TYPE;
	}
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public void setCERT_TYPE(String cERT_TYPE) {
		CERT_TYPE = cERT_TYPE;
	}
	public void setTRAINING_TYPE(String tRAINING_TYPE) {
		TRAINING_TYPE = tRAINING_TYPE;
	}
	public Date getDOC_KEYIN_DATE() {
		return DOC_KEYIN_DATE;
	}
	public void setDOC_KEYIN_DATE(Date dOC_KEYIN_DATE) {
		DOC_KEYIN_DATE = dOC_KEYIN_DATE;
	}
	
	
}
