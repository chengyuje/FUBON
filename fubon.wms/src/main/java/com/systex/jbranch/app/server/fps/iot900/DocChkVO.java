package com.systex.jbranch.app.server.fps.iot900;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class DocChkVO extends PagingOutputVO{
	
	private List<Map<String, Object>> INList;
	private List<Map<String, Object>> OUTList;
	
	
	public List<Map<String, Object>> getINList() {
		return INList;
	}
	public void setINList(List<Map<String, Object>> iNList) {
		INList = iNList;
	}
	public List<Map<String, Object>> getOUTList() {
		return OUTList;
	}
	public void setOUTList(List<Map<String, Object>> oUTList) {
		OUTList = oUTList;
	}
	
	

}
