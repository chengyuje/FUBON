package com.systex.jbranch.app.server.fps.cam230;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM230InputVO extends PagingInputVO{
	
	private String campID;
	private Integer assignRow;

	public String getCampID() {
		return campID;
	}

	public Integer getAssignRow() {
		return assignRow;
	}

	public void setAssignRow(Integer assignRow) {
		this.assignRow = assignRow;
	}

	public void setCampID(String campID) {
		this.campID = campID;
	}
	
}
