package com.systex.jbranch.app.server.fps.cam120;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM120InputVO extends PagingInputVO{
	private String id;
	private String campId;
	private String campName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCampId() {
		return campId;
	}
	public void setCampId(String campId) {
		this.campId = campId;
	}
	public String getCampName() {
		return campName;
	}
	public void setCampName(String campName) {
		this.campName = campName;
	}
}
