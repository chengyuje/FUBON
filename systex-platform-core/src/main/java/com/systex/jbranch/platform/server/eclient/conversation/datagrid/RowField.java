package com.systex.jbranch.platform.server.eclient.conversation.datagrid;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RowField
{
	private Map<String, String> colDataMap = new HashMap<String, String>();
	
	/**
	 * 設定單一列中某一行資料
	 * @param columnId	欄位名稱
	 * @param columnData	欄位資料
	 */
	public void setColData(String columnId, String columnData)
	{
		colDataMap.put(columnId, columnData);		
	}
	/**
	 * 移除單一列中某一行資料
	 * @param columnId 欄位名稱
	 */
	public void removeColData(String columnId)
	{
		if (colDataMap.containsKey(columnId)) 
		{
			colDataMap.remove(columnId);
		}
	}
	
	@Override
	public String toString()
	{
		final String strDataFormat = "<col id='%s' data='%s' />";
		String strData = "";
		
		Set<String> keys = colDataMap.keySet();		
		for(String key: keys) {
			strData += String.format(strDataFormat, key, colDataMap.get(key));
		}
		
		return strData;
	}	
}