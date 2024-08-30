package com.systex.jbranch.app.server.fps.cmmgr009;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR009OutputVO extends PagingOutputVO {
	
	public CMMGR009OutputVO()
	{
	}
	
	private List<Map<String, Object>> dataList;


	public List<Map<String, Object>> getDataList() {
		return dataList;
	}
	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}
}
