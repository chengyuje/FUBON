package com.systex.jbranch.app.server.fps.prd174;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD174EDITInputVO extends PagingInputVO{
	
	private String Q_ID;
	private String Q_NAME;
	private String Q_TYPE;
	private Date EFFECT_DATE;
	private Date EXPIRY_DATE;
	private String TEXT_STYLE_B;
	private String TEXT_STYLE_I;
	private String TEXT_STYLE_U;
	private String TEXT_STYLE_A;	
	
	public String getQ_ID() {
		return Q_ID;
	}
	public void setQ_ID(String q_ID) {
		Q_ID = q_ID;
	}
	public String getQ_NAME() {
		return Q_NAME;
	}
	public void setQ_NAME(String q_NAME) {
		Q_NAME = q_NAME;
	}
	public String getQ_TYPE() {
		return Q_TYPE;
	}
	public void setQ_TYPE(String q_TYPE) {
		Q_TYPE = q_TYPE;
	}
	public Date getEFFECT_DATE() {
		return EFFECT_DATE;
	}
	public void setEFFECT_DATE(Date eFFECT_DATE) {
		EFFECT_DATE = eFFECT_DATE;
	}
	public Date getEXPIRY_DATE() {
		return EXPIRY_DATE;
	}
	public void setEXPIRY_DATE(Date eXPIRY_DATE) {
		EXPIRY_DATE = eXPIRY_DATE;
	}
	public String getTEXT_STYLE_B() {
		return TEXT_STYLE_B;
	}

	public void setTEXT_STYLE_B(String tEXT_STYLE_B) {
		TEXT_STYLE_B = tEXT_STYLE_B;
	}

	public String getTEXT_STYLE_I() {
		return TEXT_STYLE_I;
	}

	public void setTEXT_STYLE_I(String tEXT_STYLE_I) {
		TEXT_STYLE_I = tEXT_STYLE_I;
	}

	public String getTEXT_STYLE_U() {
		return TEXT_STYLE_U;
	}

	public void setTEXT_STYLE_U(String tEXT_STYLE_U) {
		TEXT_STYLE_U = tEXT_STYLE_U;
	}

	public String getTEXT_STYLE_A() {
		return TEXT_STYLE_A;
	}

	public void setTEXT_STYLE_A(String tEXT_STYLE_A) {
		TEXT_STYLE_A = tEXT_STYLE_A;
	}
	

}
