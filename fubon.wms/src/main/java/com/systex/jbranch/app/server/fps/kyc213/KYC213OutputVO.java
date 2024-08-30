package com.systex.jbranch.app.server.fps.kyc213;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC213OutputVO extends PagingOutputVO{
	
	private List RiskLevelList;

	public List getRiskLevelList() {
		return RiskLevelList;
	}

	public void setRiskLevelList(List riskLevelList) {
		RiskLevelList = riskLevelList;
	}
}
