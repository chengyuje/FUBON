package com.systex.jbranch.app.server.fps.i18n;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class I18NOutputVO extends PagingOutputVO {

	private List resultList;
	private List localList;
	private List typeList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getLocalList() {
		return localList;
	}
	public void setLocalList(List localList) {
		this.localList = localList;
	}
	public List getTypeList() {
		return typeList;
	}
	public void setTypeList(List typeList) {
		this.typeList = typeList;
	}

}