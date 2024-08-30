package com.systex.jbranch.app.server.fps.cmmgr008;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR008OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> dataList;
	private List<Map<String, Object>> RoleList;

	public List<Map<String, Object>> getDataList() {
		return dataList;
	}
	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}
	public List<Map<String, Object>> getRoleList() {
		return RoleList;
	}
	public void setRoleList(List<Map<String, Object>> roleList) {
		RoleList = roleList;
	}
}
