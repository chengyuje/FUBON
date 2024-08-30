package com.systex.jbranch.app.server.fps.org410;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG410OutputVO extends PagingOutputVO {
	
	private List empChangeLst;
	private List<Map<String, String>> EXPORT_LST;
	private List aoJobRankList;

	public List getAoJobRankList() {
		return aoJobRankList;
	}

	public void setAoJobRankList(List aoJobRankList) {
		this.aoJobRankList = aoJobRankList;
	}

	public List getEmpChangeLst() {
		return empChangeLst;
	}

	public void setEmpChangeLst(List empChangeLst) {
		this.empChangeLst = empChangeLst;
	}

	public List<Map<String, String>> getEXPORT_LST() {
		return EXPORT_LST;
	}

	public void setEXPORT_LST(List<Map<String, String>> eXPORT_LST) {
		EXPORT_LST = eXPORT_LST;
	}

}
