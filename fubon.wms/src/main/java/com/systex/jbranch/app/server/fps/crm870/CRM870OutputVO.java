package com.systex.jbranch.app.server.fps.crm870;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM870OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> txnList;
	private List<Map<String, Object>> divList;
	private List<Map<String, Object>> maturityList;

	public List<Map<String, Object>> getMaturityList() {
		return maturityList;
	}

	public void setMaturityList(List<Map<String, Object>> maturityList) {
		this.maturityList = maturityList;
	}

	public List<Map<String, Object>> getTxnList() {
		return txnList;
	}

	public void setTxnList(List<Map<String, Object>> txnList) {
		this.txnList = txnList;
	}

	public List<Map<String, Object>> getDivList() {
		return divList;
	}

	public void setDivList(List<Map<String, Object>> divList) {
		this.divList = divList;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

}
