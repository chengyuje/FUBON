package com.systex.jbranch.app.server.fps.prd172;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD172OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> INS_ANCDOCList;

	public List<Map<String, Object>> getINS_ANCDOCList() {
		return INS_ANCDOCList;
	}

	public void setINS_ANCDOCList(List<Map<String, Object>> iNS_ANCDOCList) {
		INS_ANCDOCList = iNS_ANCDOCList;
	}
	
	
	
}
