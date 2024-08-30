package com.systex.jbranch.app.server.fps.pms131;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS131OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> bonusRateList;

	public List<Map<String, Object>> getBonusRateList() {
		return bonusRateList;
	}

	public void setBonusRateList(List<Map<String, Object>> bonusRateList) {
		this.bonusRateList = bonusRateList;
	}
}
