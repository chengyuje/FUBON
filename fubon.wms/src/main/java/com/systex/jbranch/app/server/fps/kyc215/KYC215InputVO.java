package com.systex.jbranch.app.server.fps.kyc215;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC215InputVO extends PagingInputVO{
	
	private String RS_VERSION;
	private List C_VAL;
	private List W_VAL;
	private List<Map<String,Object>> CUST_RL_ID;	


	public List<Map<String, Object>> getCUST_RL_ID() {
		return CUST_RL_ID;
	}

	public void setCUST_RL_ID(List<Map<String, Object>> cUST_RL_ID) {
		CUST_RL_ID = cUST_RL_ID;
	}

	public List getC_VAL() {
		return C_VAL;
	}

	public void setC_VAL(List c_VAL) {
		C_VAL = c_VAL;
	}

	public List getW_VAL() {
		return W_VAL;
	}

	public void setW_VAL(List w_VAL) {
		W_VAL = w_VAL;
	}

	public String getRS_VERSION() {
		return RS_VERSION;
	}

	public void setRS_VERSION(String rS_VERSION) {
		RS_VERSION = rS_VERSION;
	}
	
}
