package com.systex.jbranch.app.server.fps.iot910;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT910InputVO extends PagingInputVO{

	private String INSPRD_ID;
	private boolean INS_RIDER_DLT;
	private String FB_COM_YN;
	private String COMPANY_NUM;

	public String getINSPRD_ID() {
		return INSPRD_ID;
	}

	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}

	public boolean isINS_RIDER_DLT() {
		return INS_RIDER_DLT;
	}

	public void setINS_RIDER_DLT(boolean iNS_RIDER_DLT) {
		INS_RIDER_DLT = iNS_RIDER_DLT;
	}

	public String getFB_COM_YN() {
		return FB_COM_YN;
	}

	public void setFB_COM_YN(String fB_COM_YN) {
		FB_COM_YN = fB_COM_YN;
	}

	public String getCOMPANY_NUM() {
		return COMPANY_NUM;
	}

	public void setCOMPANY_NUM(String cOMPANY_NUM) {
		COMPANY_NUM = cOMPANY_NUM;
	}
	
}
