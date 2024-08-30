package com.systex.jbranch.app.server.fps.sot315;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT315OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> contractList;

	public List<Map<String, Object>> getContractList() {
		return contractList;
	}

	public void setContractList(List<Map<String, Object>> contractList) {
		this.contractList = contractList;
	}

}
