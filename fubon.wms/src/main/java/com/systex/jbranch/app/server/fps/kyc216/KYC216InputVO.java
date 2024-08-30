package com.systex.jbranch.app.server.fps.kyc216;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC216InputVO extends PagingInputVO{
	
	private String RLR_VERSION;
	private List<Map<String,Object>> LRATE_RISK_LEVEL;
	private List LRATE_DEL_CUST_RL_ID;
	
	
	public List<Map<String, Object>> getLRATE_RISK_LEVEL() {
		return LRATE_RISK_LEVEL;
	}

	public void setLRATE_RISK_LEVEL(List<Map<String, Object>> lRATE_RISK_LEVEL) {
		LRATE_RISK_LEVEL = lRATE_RISK_LEVEL;
	}

	public List getLRATE_DEL_CUST_RL_ID() {
		return LRATE_DEL_CUST_RL_ID;
	}

	public void setLRATE_DEL_CUST_RL_ID(List lRATE_DEL_CUST_RL_ID) {
		LRATE_DEL_CUST_RL_ID = lRATE_DEL_CUST_RL_ID;
	}

	public void setRLR_VERSION(String rLR_VERSION) {
		RLR_VERSION = rLR_VERSION;
	}

	public String getRLR_VERSION() {
		return RLR_VERSION;
	}
	
	

}
