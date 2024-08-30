package com.systex.jbranch.app.server.fps.ins112;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS112OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> COMList;
	private List<Map<String, Object>> queryList;

	public List<Map<String, Object>> getCOMList() {
		return COMList;
	}

	public void setCOMList(List<Map<String, Object>> cOMList) {
		COMList = cOMList;
	}

	public List<Map<String, Object>> getQueryList() {
		return queryList;
	}

	public void setQueryList(List<Map<String, Object>> queryList) {
		this.queryList = queryList;
	}
	
	

}
