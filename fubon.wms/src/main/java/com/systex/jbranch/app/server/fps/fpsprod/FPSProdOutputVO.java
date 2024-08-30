package com.systex.jbranch.app.server.fps.fpsprod;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FPSProdOutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> prodList;

	public List<Map<String, Object>> getProdList() {
		return prodList;
	}

	public void setProdList(List<Map<String, Object>> prodList) {
		this.prodList = prodList;
	}	

}
