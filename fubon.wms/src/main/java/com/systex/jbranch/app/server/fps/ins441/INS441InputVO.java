package com.systex.jbranch.app.server.fps.ins441;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS441InputVO extends PagingInputVO{
	
	private String type;			// 下拉選單取值用 - 選取類型
	private String custID;
	private String compID;
	private List<Map<String, Object>> insList;
	private List<Map<String, Object>> othList;
	private String keyNO;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCompID() {
		return compID;
	}
	public void setCompID(String compID) {
		this.compID = compID;
	}
	public List<Map<String, Object>> getInsList() {
		return insList;
	}
	public void setInsList(List<Map<String, Object>> insList) {
		this.insList = insList;
	}
	public String getKeyNO() {
		return keyNO;
	}
	public void setKeyNO(String keyNO) {
		this.keyNO = keyNO;
	}
	public List<Map<String, Object>> getOthList() {
		return othList;
	}
	public void setOthList(List<Map<String, Object>> othList) {
		this.othList = othList;
	}
}
