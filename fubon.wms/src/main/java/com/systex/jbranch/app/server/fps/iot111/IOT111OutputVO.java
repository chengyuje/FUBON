package com.systex.jbranch.app.server.fps.iot111;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT111OutputVO extends PagingOutputVO {
	private List prematchList;
	private List<Map<String, Object>> resultList;

	public List getPrematchList() {
		return prematchList;
	}

	public void setPrematchList(List prematchList) {
		this.prematchList = prematchList;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	
}