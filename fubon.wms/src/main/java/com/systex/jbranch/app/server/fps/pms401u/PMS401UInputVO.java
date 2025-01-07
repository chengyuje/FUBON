package com.systex.jbranch.app.server.fps.pms401u;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS401UInputVO extends PagingInputVO {

	private String itemID;
	private String orgType;
	private String uhrmRC;
	private String uhrmOP;

	public String getUhrmRC() {
		return uhrmRC;
	}

	public void setUhrmRC(String uhrmRC) {
		this.uhrmRC = uhrmRC;
	}

	public String getUhrmOP() {
		return uhrmOP;
	}

	public void setUhrmOP(String uhrmOP) {
		this.uhrmOP = uhrmOP;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
}
