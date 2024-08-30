package com.systex.jbranch.app.server.fps.sot225;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT225InputVO extends PagingInputVO {
	
	private String custID;

	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
}
