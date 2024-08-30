package com.systex.jbranch.app.server.fps.prd174;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD174OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> INS_ANCDOCList;
	private List errorList;

	public List getErrorList() {
		return errorList;
	}

	public void setErrorList(List errorList) {
		this.errorList = errorList;
	}

	public List<Map<String, Object>> getINS_ANCDOCList() {
		return INS_ANCDOCList;
	}

	public void setINS_ANCDOCList(List<Map<String, Object>> iNS_ANCDOCList) {
		INS_ANCDOCList = iNS_ANCDOCList;
	}
	
	
	
}
