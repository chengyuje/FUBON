package com.systex.jbranch.app.server.fps.iot920;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FirstBuyInputVO extends PagingInputVO{
	
	private String INSPRD_TYPE;
	private String CURR_CD;
	private String CUST_ID;
	
	
	public String getINSPRD_TYPE() {
		return INSPRD_TYPE;
	}
	public String getCURR_CD() {
		return CURR_CD;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setINSPRD_TYPE(String iNSPRD_TYPE) {
		INSPRD_TYPE = iNSPRD_TYPE;
	}
	public void setCURR_CD(String cURR_CD) {
		CURR_CD = cURR_CD;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	
	
}
