package com.systex.jbranch.app.server.fps.kyc210;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC210InputVO extends PagingInputVO{
	
	private String EXAM_NAME;
	private String EXAM_VERSION;
	private Date LASTUPDATE;
	private String QUEST_TYPE;
	private String STATUS;
	private String Delete_Data;
	
	
	
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
	public String getQUEST_TYPE() {
		return QUEST_TYPE;
	}
	public void setQUEST_TYPE(String qUEST_TYPE) {
		QUEST_TYPE = qUEST_TYPE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	
	

}
