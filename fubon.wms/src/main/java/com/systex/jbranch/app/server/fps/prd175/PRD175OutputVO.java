package com.systex.jbranch.app.server.fps.prd175;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD175OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> INS_ANCDOCList;
	private List<Map<String, Object>> DILOGList;

	public List<Map<String, Object>> getINS_ANCDOCList() {
		return INS_ANCDOCList;
	}
	public void setINS_ANCDOCList(List<Map<String, Object>> iNS_ANCDOCList) {
		INS_ANCDOCList = iNS_ANCDOCList;
	}
	public List<Map<String, Object>> getDILOGList() {
		return DILOGList;
	}
	public void setDILOGList(List<Map<String, Object>> dILOGList) {
		DILOGList = dILOGList;
	}
	
	
	
}
