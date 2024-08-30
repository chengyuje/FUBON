package com.systex.jbranch.app.server.fps.prd181;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD181InputVO extends PagingInputVO{
	

	
	private String  INSPRD_KEYNO  ;
	private String  INSPRD_ID     ;
	private String  INSPRD_NAME   ;
	private String  CERT_03       ;
	private String  CERT_05       ;
	
	
	public String getINSPRD_KEYNO() {
		return INSPRD_KEYNO;
	}
	public String getINSPRD_ID() {
		return INSPRD_ID;
	}
	public String getINSPRD_NAME() {
		return INSPRD_NAME;
	}
	public String getCERT_03() {
		return CERT_03;
	}
	public String getCERT_05() {
		return CERT_05;
	}
	public void setINSPRD_KEYNO(String iNSPRD_KEYNO) {
		INSPRD_KEYNO = iNSPRD_KEYNO;
	}
	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}
	public void setINSPRD_NAME(String iNSPRD_NAME) {
		INSPRD_NAME = iNSPRD_NAME;
	}
	public void setCERT_03(String cERT_03) {
		CERT_03 = cERT_03;
	}
	public void setCERT_05(String cERT_05) {
		CERT_05 = cERT_05;
	}
	
	

}
