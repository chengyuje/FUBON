package com.systex.jbranch.app.server.fps.prd175;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD175InputVO extends PagingInputVO{
	
	
	
	private String BRANCH_NBR ;
	private String EMP_NAME   ;
	private String EMP_ID     ;
	private Date REG_DATE   ;
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public String getEMP_NAME() {
		return EMP_NAME;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public Date getREG_DATE() {
		return REG_DATE;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public void setREG_DATE(Date rEG_DATE) {
		REG_DATE = rEG_DATE;
	}

	
	

	
	
}
