package com.systex.jbranch.platform.common.security.privilege.vo;

import java.util.HashMap;
import java.util.Map;

public class ModuleDTO {
	private String moduleId;
	//private String moduleName;
	
	private Map<String, ItemDTO> itemMap = new HashMap<String, ItemDTO>();
	
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
//	public String getModuleName() {
//		return moduleName;
//	}
//	public void setModuleName(String moduleName) {
//		this.moduleName = moduleName;
//	}
	public Map<String, ItemDTO> getItemMap() {
		return itemMap;
	}
	public void setItemMap(Map<String, ItemDTO> itemMap) {
		this.itemMap = itemMap;
	}
}
