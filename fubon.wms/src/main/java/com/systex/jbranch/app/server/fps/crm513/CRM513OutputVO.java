package com.systex.jbranch.app.server.fps.crm513;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM513OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> tradeList;

	public List<Map<String, Object>> getTradeList() {
		return tradeList;
	}

	public void setTradeList(List<Map<String, Object>> tradeList) {
		this.tradeList = tradeList;
	}
	
}
