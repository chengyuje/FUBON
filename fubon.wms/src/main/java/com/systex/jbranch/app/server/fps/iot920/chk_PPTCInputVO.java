package com.systex.jbranch.app.server.fps.iot920;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class chk_PPTCInputVO extends PagingInputVO{
	
	private String EMP_ID;
	private String INSPRD_ID;
	private Date APPLY_DATE;
	
	
	public Date getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public void setAPPLY_DATE(Date aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public String getINSPRD_ID() {
		return INSPRD_ID;
	}
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}
	
	
}
