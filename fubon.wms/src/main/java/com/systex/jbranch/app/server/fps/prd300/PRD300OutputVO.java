package com.systex.jbranch.app.server.fps.prd300;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class PRD300OutputVO extends PagingOutputVO {
	private String name;
	private String riskcate_id;
	private String currency;
	private List resultList;
	private Boolean canEdit;
	private String inv_level;			//投資策略等級
	
	public Boolean getCanEdit() {
		return canEdit;
	}
	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public String getRiskcate_id() {
		return riskcate_id;
	}
	public void setRiskcate_id(String riskcate_id) {
		this.riskcate_id = riskcate_id;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getInv_level() {
		return inv_level;
	}
	public void setInv_level(String inv_level) {
		this.inv_level = inv_level;
	}
	
}
