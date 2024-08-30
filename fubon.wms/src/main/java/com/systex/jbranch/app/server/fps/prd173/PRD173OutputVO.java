package com.systex.jbranch.app.server.fps.prd173;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD173OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> FXD_DicountList ;

	public List<Map<String, Object>> getFXD_DicountList() {
		return FXD_DicountList;
	}

	public void setFXD_DicountList(List<Map<String, Object>> fXD_DicountList) {
		FXD_DicountList = fXD_DicountList;
	}
	
	
}
