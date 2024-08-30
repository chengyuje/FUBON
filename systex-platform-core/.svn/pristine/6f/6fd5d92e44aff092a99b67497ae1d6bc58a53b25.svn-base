package com.systex.jbranch.platform.common.util;

import java.util.Map;

public class DynamicSystemParameter {

	private String defaultKey;
	private String otherKey;
	private Map paramsMap;
	
	public Object getValue(){
		
		return getValue(defaultKey);
	}
	
	public Object getValue(String key){
		
		Object value = this.paramsMap.get(key);
		return value == null ? this.paramsMap.get(otherKey) : value;
	}

	/**
	 * @return the defaultKey
	 */
	public String getDefaultKey() {
		return defaultKey;
	}

	/**
	 * @param defaultKey the defaultKey to set
	 */
	public void setDefaultKey(String defaultKey) {
		this.defaultKey = defaultKey;
	}

	
	/**
	 * @return the otherKey
	 */
	public String getOtherKey() {
		return otherKey;
	}

	/**
	 * @param otherKey the otherKey to set
	 */
	public void setOtherKey(String otherKey) {
		this.otherKey = otherKey;
	}

	/**
	 * @return the paramsMap
	 */
	public Map getParamsMap() {
		return paramsMap;
	}

	/**
	 * @param paramsMap the paramsMap to set
	 */
	public void setParamsMap(Map paramsMap) {
		this.paramsMap = paramsMap;
	}
	
	
}
