package com.systex.jbranch.app.server.fps.crm512;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM512OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> qusBankList;
	private List<Map<String, Object>> custHisByQusList;
	private boolean isHighAge;

	public List<Map<String, Object>> getCustHisByQusList() {
		return custHisByQusList;
	}

	public void setCustHisByQusList(List<Map<String, Object>> custHisByQusList) {
		this.custHisByQusList = custHisByQusList;
	}

	public List<Map<String, Object>> getQusBankList() {
		return qusBankList;
	}

	public void setQusBankList(List<Map<String, Object>> qusBankList) {
		this.qusBankList = qusBankList;
	}

	public boolean isHighAge() {
		return isHighAge;
	}

	public void setHighAge(boolean isHighAge) {
		this.isHighAge = isHighAge;
	}

}
