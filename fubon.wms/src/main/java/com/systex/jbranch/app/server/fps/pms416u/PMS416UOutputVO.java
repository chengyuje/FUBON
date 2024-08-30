package com.systex.jbranch.app.server.fps.pms416u;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS416UOutputVO extends PagingOutputVO{
	private List resultList;  //分頁用
	private List totalList;	  //csv用
	private List UHRMList;	  //UHRM人員清單
	
	public List getUHRMList() {
		return UHRMList;
	}
	public void setUHRMList(List uHRMList) {
		UHRMList = uHRMList;
	}
	public List getTotalList() {
		return totalList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
