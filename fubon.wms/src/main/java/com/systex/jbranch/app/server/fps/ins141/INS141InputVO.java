package com.systex.jbranch.app.server.fps.ins141;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS141InputVO extends PagingInputVO{
	
	private List<String> custList;

	public List<String> getCustList() {
		return custList;
	}

	public void setCustList(List<String> custList) {
		this.custList = custList;
	}
	
	

}
