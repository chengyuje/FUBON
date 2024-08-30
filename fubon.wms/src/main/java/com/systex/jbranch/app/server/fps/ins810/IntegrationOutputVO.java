package com.systex.jbranch.app.server.fps.ins810;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IntegrationOutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> IntegrationList;//合併List

	public List<Map<String, Object>> getIntegrationList() {
		return IntegrationList;
	}

	public void setIntegrationList(List<Map<String, Object>> integrationList) {
		IntegrationList = integrationList;
	}
	
	
}
