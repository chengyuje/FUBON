package com.systex.jbranch.app.server.fps.pms112;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS112InputVO extends PagingInputVO {

	private String sDate;
	private String eDate;

	private String leadType;
	List<Map<String, String>> rep_qryList;

	public List<Map<String, String>> getRep_qryList() {
		return rep_qryList;
	}

	public void setRep_qryList(List<Map<String, String>> rep_qryList) {
		this.rep_qryList = rep_qryList;
	}

	public String getLeadType() {
		return leadType;
	}

	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}

	public String getsDate() {
		return sDate;
	}

	public void setsDate(String sDate) {
		this.sDate = sDate;
	}

	public String geteDate() {
		return eDate;
	}

	public void seteDate(String eDate) {
		this.eDate = eDate;
	}

}
