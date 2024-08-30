package com.systex.jbranch.app.server.fps.pqc101;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PQC101OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> basicList;
	private List<Map<String, Object>> orgList;

	public List<Map<String, Object>> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<Map<String, Object>> orgList) {
		this.orgList = orgList;
	}

	public List<Map<String, Object>> getBasicList() {
		return basicList;
	}

	public void setBasicList(List<Map<String, Object>> basicList) {
		this.basicList = basicList;
	}

}
