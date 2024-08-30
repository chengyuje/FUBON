package com.systex.jbranch.app.server.fps.iot131;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT131OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> MatchList;

	public List<Map<String, Object>> getMatchList() {
		return MatchList;
	}

	public void setMatchList(List<Map<String, Object>> matchList) {
		MatchList = matchList;
	}
	
	
}
