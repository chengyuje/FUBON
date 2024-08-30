package com.systex.jbranch.app.server.fps.iot920;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class InsFundListInputVO extends PagingInputVO{
	
	private String INSPRD_ID;
	private String PRD_RISK;
	private String custID;
	private List<Map<String, Object>> INVESTList;
	private String C_SENIOR_PVAL;
	private String SENIOR_OVER_PVAL; //高齡投組適配選擇越級P值
	
	
	public List<Map<String, Object>> getINVESTList() {
		return INVESTList;
	}

	public void setINVESTList(List<Map<String, Object>> iNVESTList) {
		INVESTList = iNVESTList;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getINSPRD_ID() {
		return INSPRD_ID;
	}

	public String getPRD_RISK() {
		return PRD_RISK;
	}

	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}

	public void setPRD_RISK(String pRD_RISK) {
		PRD_RISK = pRD_RISK;
	}

	public String getC_SENIOR_PVAL() {
		return C_SENIOR_PVAL;
	}

	public void setC_SENIOR_PVAL(String c_SENIOR_PVAL) {
		C_SENIOR_PVAL = c_SENIOR_PVAL;
	}

	public String getSENIOR_OVER_PVAL() {
		return SENIOR_OVER_PVAL;
	}

	public void setSENIOR_OVER_PVAL(String sENIOR_OVER_PVAL) {
		SENIOR_OVER_PVAL = sENIOR_OVER_PVAL;
	}
	
	
}
