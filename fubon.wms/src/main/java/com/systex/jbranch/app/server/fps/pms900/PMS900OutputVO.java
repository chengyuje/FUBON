package com.systex.jbranch.app.server.fps.pms900;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS900OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;	//Q3投資年期上升2級以上
	private String[] line_name_array;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String[] getLine_name_array() {
		return line_name_array;
	}

	public void setLine_name_array(String[] line_name_array) {
		this.line_name_array = line_name_array;
	}

}
