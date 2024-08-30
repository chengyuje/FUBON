package com.systex.jbranch.platform.common.security.privilege.vo;

import java.util.HashSet;
import java.util.Set;

public class ItemDTO {
	private String itemId;
	//private String itemName;
	private Set<String> functionList = new HashSet<String>();
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
//	public String getItemName() {
//		return itemName;
//	}
//	public void setItemName(String itemName) {
//		this.itemName = itemName;
//	}
	public Set<String> getFunctionSet() {
		return functionList;
	}
	public void setFunctionSet(Set<String> functionList) {
		this.functionList = functionList;
	}
	
	
}
