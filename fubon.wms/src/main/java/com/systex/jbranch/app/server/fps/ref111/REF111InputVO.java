package com.systex.jbranch.app.server.fps.ref111;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class REF111InputVO extends PagingInputVO{
	private String dateYearMonth;
	private String queryType;
	
	private List<Map<String, Object>> list;

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public String getDateYearMonth() {
		return dateYearMonth;
	}

	public void setDateYearMonth(String dateYearMonth) {
		this.dateYearMonth = dateYearMonth;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
}
