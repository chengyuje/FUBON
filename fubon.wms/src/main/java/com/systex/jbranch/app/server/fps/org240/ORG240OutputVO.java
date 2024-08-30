package com.systex.jbranch.app.server.fps.org240;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG240OutputVO extends PagingOutputVO {
	
	private List suptSalesTeamLst;

	public List getSuptSalesTeamLst() {
		return suptSalesTeamLst;
	}

	public void setSuptSalesTeamLst(List suptSalesTeamLst) {
		this.suptSalesTeamLst = suptSalesTeamLst;
	}
	
}
