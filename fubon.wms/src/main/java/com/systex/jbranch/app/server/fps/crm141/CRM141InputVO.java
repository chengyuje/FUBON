package com.systex.jbranch.app.server.fps.crm141;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM141InputVO extends PagingInputVO {
	
	private String pri_id;
	private String memLoginFlag;


	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public String getPri_id() {
		return pri_id;
	}

	public void setPri_id(String pri_id) {
		this.pri_id = pri_id;
	}

}
