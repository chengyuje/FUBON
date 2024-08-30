package com.systex.jbranch.app.server.fps.crm132;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM132InputVO extends PagingInputVO { 
	
	private String memLoginFlag;
	private String pri_id;

	public String getPri_id() {
		return pri_id;
	}

	public void setPri_id(String pri_id) {
		this.pri_id = pri_id;
	}

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

}
