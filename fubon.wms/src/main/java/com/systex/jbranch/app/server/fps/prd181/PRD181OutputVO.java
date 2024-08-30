package com.systex.jbranch.app.server.fps.prd181;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD181OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> DicountList ;

	public List<Map<String, Object>> getDicountList() {
		return DicountList;
	}

	public void setDicountList(List<Map<String, Object>> dicountList) {
		DicountList = dicountList;
	}
	


	
	
}
