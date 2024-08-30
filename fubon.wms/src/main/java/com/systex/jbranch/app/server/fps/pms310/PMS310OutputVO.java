package com.systex.jbranch.app.server.fps.pms310;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS310OutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;
	private String NBR_state;
	private String BRANCH_NBR;
	private String PS_state;
	private String PSEmpId;
	private String YEARMON;
	

	public String getYEARMON() {
		return YEARMON;
	}

	public void setYEARMON(String yEARMON) {
		YEARMON = yEARMON;
	}

	public String getPS_state() {
		return PS_state;
	}

	public void setPS_state(String pS_state) {
		PS_state = pS_state;
	}

	public String getPSEmpId() {
		return PSEmpId;
	}

	public void setPSEmpId(String pSEmpId) {
		PSEmpId = pSEmpId;
	}

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
}
