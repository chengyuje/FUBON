package com.systex.jbranch.app.server.fps.importfile;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IMPORTFILEOutputVO extends PagingOutputVO {
	private List<Map<String, Object>> NameList;
	private List<Map<String, Object>> ResultList;

	public List<Map<String, Object>> getNameList() {
		return NameList;
	}
	public void setNameList(List<Map<String, Object>> nameList) {
		NameList = nameList;
	}
	public List<Map<String, Object>> getResultList() {
		return ResultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		ResultList = resultList;
	}



}
