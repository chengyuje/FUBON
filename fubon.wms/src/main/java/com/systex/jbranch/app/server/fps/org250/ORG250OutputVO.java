package com.systex.jbranch.app.server.fps.org250;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG250OutputVO extends PagingOutputVO {
	private List agentLst = null;
	private List alertLst = null;
	public List getAgentLst() {
		return agentLst;
	}

	public void setAgentLst(List agentLst) {
		this.agentLst = agentLst;
	}

	public List getAlertLst() {
		return alertLst;
	}

	public void setAlertLst(List alertLst) {
		this.alertLst = alertLst;
	}

		
}
