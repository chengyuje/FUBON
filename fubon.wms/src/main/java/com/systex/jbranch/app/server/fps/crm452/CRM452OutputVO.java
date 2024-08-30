package com.systex.jbranch.app.server.fps.crm452;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM452OutputVO extends PagingOutputVO {
	
	private List applyList;

	public List getApplyList() {
		return applyList;
	}

	public void setApplyList(List applyList) {
		this.applyList = applyList;
	}
	
}
