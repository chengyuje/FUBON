package com.systex.jbranch.app.server.fps.pms203;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS203OutputVO extends PagingOutputVO{
	private List resultList;
	private List totalList;
	private String targetType;
	private String NBR_state;
	private String BRANCH_NBR;
	

	public String getNBR_state() {
		return NBR_state;
	}
	public void setNBR_state(String nBR_state) {
		NBR_state = nBR_state;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
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
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	
}
