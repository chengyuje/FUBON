package com.systex.jbranch.app.server.fps.ins110;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS110OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> DataList;

	public List<Map<String, Object>> getDataList() {
		return DataList;
	}

	public void setDataList(List<Map<String, Object>> dataList) {
		DataList = dataList;
	}
	
	
	
}
