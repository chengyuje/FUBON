package com.systex.jbranch.app.server.fps.iot920;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PPTIDDataVO extends PagingOutputVO{
	
	private List<Map<String, Object>> PPTIDData;

	public List<Map<String, Object>> getPPTIDData() {
		return PPTIDData;
	}

	public void setPPTIDData(List<Map<String, Object>> pPTIDData) {
		PPTIDData = pPTIDData;
	}
	
	

}
