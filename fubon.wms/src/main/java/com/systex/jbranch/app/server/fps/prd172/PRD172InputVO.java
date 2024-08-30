package com.systex.jbranch.app.server.fps.prd172;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD172InputVO extends PagingInputVO{
	
	private String Q_TYPE;
	private String Q_ID;
	private String OTH_TYPE;
	private String REG_TYPE;
	
	
	
	public String getREG_TYPE() {
		return REG_TYPE;
	}

	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}

	public String getQ_ID() {
		return Q_ID;
	}

	public void setQ_ID(String q_ID) {
		Q_ID = q_ID;
	}

	public String getQ_TYPE() {
		return Q_TYPE;
	}

	public void setQ_TYPE(String q_TYPE) {
		Q_TYPE = q_TYPE;
	}

	public String getOTH_TYPE() {
		return OTH_TYPE;
	}

	public void setOTH_TYPE(String oTH_TYPE) {
		OTH_TYPE = oTH_TYPE;
	}
	
	
	
}
