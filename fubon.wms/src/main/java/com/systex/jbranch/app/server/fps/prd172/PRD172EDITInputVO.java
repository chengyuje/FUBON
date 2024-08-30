package com.systex.jbranch.app.server.fps.prd172;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD172EDITInputVO extends PagingInputVO{
	
	
	private String SEQ;
	private String DOC_NAME  ;
	private String DOC_SEQ   ;
	private String DOC_TYPE  ;
	private String DOC_LEVEL ;
	private String SIGN_INC  ;
	private String OTH_TYPE;
	private String REG_TYPE;
	
	
	public String getREG_TYPE() {
		return REG_TYPE;
	}
	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}
	public String getDOC_NAME() {
		return DOC_NAME;
	}
	public String getDOC_SEQ() {
		return DOC_SEQ;
	}
	public String getDOC_TYPE() {
		return DOC_TYPE;
	}
	public String getDOC_LEVEL() {
		return DOC_LEVEL;
	}
	public String getSIGN_INC() {
		return SIGN_INC;
	}
	public void setDOC_NAME(String dOC_NAME) {
		DOC_NAME = dOC_NAME;
	}
	public void setDOC_SEQ(String dOC_SEQ) {
		DOC_SEQ = dOC_SEQ;
	}
	public void setDOC_TYPE(String dOC_TYPE) {
		DOC_TYPE = dOC_TYPE;
	}
	public void setDOC_LEVEL(String dOC_LEVEL) {
		DOC_LEVEL = dOC_LEVEL;
	}
	public void setSIGN_INC(String sIGN_INC) {
		SIGN_INC = sIGN_INC;
	}
	public String getSEQ() {
		return SEQ;
	}
	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}
	public String getOTH_TYPE() {
		return OTH_TYPE;
	}
	public void setOTH_TYPE(String oTH_TYPE) {
		OTH_TYPE = oTH_TYPE;
	}
	
	

}
