package com.systex.jbranch.platform.server.eclient.conversation.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DsField {
	private String fieldId;
	private Map<String, String> fieldMap = new HashMap<String, String>();
	
	/**
	 * 欲替換欄位名稱
	 * @param fieldId
	 */
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	/**
	 * 
	 * @param sValue 對應資料值
	 * @param sDesc	 對應資料說明
	 */
	public void addValue(String sValue, String sDesc)
	{
		fieldMap.put(sValue, sDesc);
	}
	
	@Override
	public String toString()
	{
		final String strDataFormat = "<data value='%s' desc='%s' />";
		String outputData = "<ds id='" + this.fieldId + "'>";
		
		Set<String> keys = fieldMap.keySet();		
		for(String key: keys) {
			outputData += String.format(strDataFormat, key, fieldMap.get(key));
		}		
		
		outputData += "</ds>";
		return outputData;
	}
}
