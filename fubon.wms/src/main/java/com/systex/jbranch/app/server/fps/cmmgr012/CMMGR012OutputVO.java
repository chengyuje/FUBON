package com.systex.jbranch.app.server.fps.cmmgr012;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR012OutputVO extends PagingOutputVO {
	
	public CMMGR012OutputVO()
	{
	}
	
	private List<Map<String, Object>> dataList;
	private List<Map<String, Object>> functionList;
	private List<Map<String, Object>> roleList;


	public List<Map<String, Object>> getDataList() {
		return dataList;
	}
	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}
	public List<Map<String, Object>> getFunctionList() {
		return functionList;
	}
	public void setFunctionList(List<Map<String, Object>> functionList) {
		this.functionList = functionList;
	}
	public List<Map<String, Object>> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<Map<String, Object>> roleList) {
		this.roleList = roleList;
	}
}
