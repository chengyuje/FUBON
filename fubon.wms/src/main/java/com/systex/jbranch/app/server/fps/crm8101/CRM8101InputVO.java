package com.systex.jbranch.app.server.fps.crm8101;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM8101InputVO extends PagingInputVO {

	private String custID;

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}
}
