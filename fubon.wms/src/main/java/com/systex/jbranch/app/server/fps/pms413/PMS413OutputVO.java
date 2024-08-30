package com.systex.jbranch.app.server.fps.pms413;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS413OutputVO extends PagingOutputVO{
	private List resultList;
	private List totalList;
	private List empName;
    private Boolean x;
	
	public Boolean getX() {
		return x;
	}
	public void setX(Boolean x) {
		this.x = x;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getTotalList() {
		return totalList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
	public List getEmpName() {
		return empName;
	}
	public void setEmpName(List empName) {
		this.empName = empName;
	}
}
