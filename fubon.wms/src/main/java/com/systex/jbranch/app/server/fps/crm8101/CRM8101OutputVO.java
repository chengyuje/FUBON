package com.systex.jbranch.app.server.fps.crm8101;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM8101OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> custList;
	private List<Map<String, Object>> prdCurrList;
	private List<Map<String, Object>> prdList;
	private List<Map<String, Object>> incomeList;

	public List<Map<String, Object>> getCustList() {
		return custList;
	}

	public void setCustList(List<Map<String, Object>> custList) {
		this.custList = custList;
	}

	public List<Map<String, Object>> getPrdCurrList() {
		return prdCurrList;
	}

	public void setPrdCurrList(List<Map<String, Object>> prdCurrList) {
		this.prdCurrList = prdCurrList;
	}

	public List<Map<String, Object>> getPrdList() {
		return prdList;
	}

	public void setPrdList(List<Map<String, Object>> prdList) {
		this.prdList = prdList;
	}

	public List<Map<String, Object>> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(List<Map<String, Object>> incomeList) {
		this.incomeList = incomeList;
	}

}
