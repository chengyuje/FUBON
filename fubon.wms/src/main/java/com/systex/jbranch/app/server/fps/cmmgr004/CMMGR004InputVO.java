package com.systex.jbranch.app.server.fps.cmmgr004;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CMMGR004InputVO extends PagingInputVO{

	public CMMGR004InputVO() {
	}
	
	private String scheduleid;
	private String schedulename;
	private String description;
	
	
	public String getScheduleid() {
		return scheduleid;
	}
	public void setScheduleid(String scheduleid) {
		this.scheduleid = scheduleid;
	}
	public String getSchedulename() {
		return schedulename;
	}
	public void setSchedulename(String schedulename) {
		this.schedulename = schedulename;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
