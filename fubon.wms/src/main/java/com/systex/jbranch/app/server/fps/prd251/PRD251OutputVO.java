package com.systex.jbranch.app.server.fps.prd251;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD251OutputVO extends PagingOutputVO {
	private String bond_name;
	private Boolean canEdit;
	private String errorMsg;
	private List resultList;
	
	
	public String getBond_name() {
		return bond_name;
	}
	public void setBond_name(String bond_name) {
		this.bond_name = bond_name;
	}
	public Boolean getCanEdit() {
		return canEdit;
	}
	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
