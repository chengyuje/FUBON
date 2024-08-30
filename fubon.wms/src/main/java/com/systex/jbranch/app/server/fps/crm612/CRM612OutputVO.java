package com.systex.jbranch.app.server.fps.crm612;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM612OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> resultList_assets;
	private List<Map<String, Object>> resultList_group;
	private List<Map<String, Object>> resultList_cust;
	private List<Map<String, Object>> resultList_kyc;
	private List<Map<String, Object>> resultList_voc;

	public List<Map<String, Object>> getResultList_voc() {
		return resultList_voc;
	}

	public void setResultList_voc(List<Map<String, Object>> resultList_voc) {
		this.resultList_voc = resultList_voc;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getResultList_assets() {
		return resultList_assets;
	}

	public void setResultList_assets(List<Map<String, Object>> resultList_assets) {
		this.resultList_assets = resultList_assets;
	}

	public List<Map<String, Object>> getResultList_group() {
		return resultList_group;
	}

	public void setResultList_group(List<Map<String, Object>> resultList_group) {
		this.resultList_group = resultList_group;
	}

	public List<Map<String, Object>> getResultList_cust() {
		return resultList_cust;
	}

	public void setResultList_cust(List<Map<String, Object>> resultList_cust) {
		this.resultList_cust = resultList_cust;
	}

	public List<Map<String, Object>> getResultList_kyc() {
		return resultList_kyc;
	}

	public void setResultList_kyc(List<Map<String, Object>> resultList_kyc) {
		this.resultList_kyc = resultList_kyc;
	}

}
