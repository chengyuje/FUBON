package com.systex.jbranch.app.server.fps.crm990;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM990OutputVO extends PagingOutputVO{
	private List<Map<String,Object>> resultList;
	private String complain_list_id;
	private String privilege_id;
	private boolean hasNextStep;
	private boolean hasFowordStep;
	private String pdfUrl;

	public List<Map<String,Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}

	public String getComplain_list_id() {
		return complain_list_id;
	}

	public void setComplain_list_id(String complain_list_id) {
		this.complain_list_id = complain_list_id;
	}

	public String getPrivilege_id() {
		return privilege_id;
	}

	public void setPrivilege_id(String privilege_id) {
		this.privilege_id = privilege_id;
	}

	public boolean isHasNextStep() {
		return hasNextStep;
	}

	public void setHasNextStep(boolean hasNextStep) {
		this.hasNextStep = hasNextStep;
	}

	public boolean isHasFowordStep() {
		return hasFowordStep;
	}

	public void setHasFowordStep(boolean hasFowordStep) {
		this.hasFowordStep = hasFowordStep;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}
	
}
	
