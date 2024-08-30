package com.systex.jbranch.app.server.fps.cmmgr002;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR002OutputVO extends PagingOutputVO {
	private List<?> resultList;
	private List<?> groupList;
	private List<?> roleList;

	public List<?> getResultList() {
		return resultList;
	}
	public void setResultList(List<?> resultList) {
		this.resultList = resultList;
	}
	public List<?> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<?> groupList) {
		this.groupList = groupList;
	}
	public List<?> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<?> roleList) {
		this.roleList = roleList;
	}
	
}
