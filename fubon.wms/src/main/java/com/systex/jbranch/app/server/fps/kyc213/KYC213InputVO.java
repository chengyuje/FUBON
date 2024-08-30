package com.systex.jbranch.app.server.fps.kyc213;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC213InputVO extends PagingInputVO{
	
	private String RL_VERSION;
	private List<Map<String,Object>> RISK_LEVEL;
	private List DEL_CUST_RL_ID;
	
	

	public List<Map<String, Object>> getRISK_LEVEL() {
		return RISK_LEVEL;
	}

	public void setRISK_LEVEL(List<Map<String, Object>> rISK_LEVEL) {
		RISK_LEVEL = rISK_LEVEL;
	}

	public List getDEL_CUST_RL_ID() {
		return DEL_CUST_RL_ID;
	}

	public void setDEL_CUST_RL_ID(List dEL_CUST_RL_ID) {
		DEL_CUST_RL_ID = dEL_CUST_RL_ID;
	}

	public String getRL_VERSION() {
		return RL_VERSION;
	}

	public void setRL_VERSION(String rL_VERSION) {
		RL_VERSION = rL_VERSION;
	}
	
	

}
