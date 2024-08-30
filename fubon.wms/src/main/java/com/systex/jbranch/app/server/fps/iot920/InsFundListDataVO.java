package com.systex.jbranch.app.server.fps.iot920;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class InsFundListDataVO extends PagingOutputVO{
	
	private List<Map<String, Object>> INVESTList;

	public List<Map<String, Object>> getINVESTList() {
		return INVESTList;
	}

	public void setINVESTList(List<Map<String, Object>> iNVESTList) {
		INVESTList = iNVESTList;
	}
	
	
}
