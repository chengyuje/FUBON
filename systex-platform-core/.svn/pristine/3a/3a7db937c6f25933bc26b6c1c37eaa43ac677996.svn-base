package com.systex.jbranch.platform.server.eclient.conversation.datagrid;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TOATable {
	private String tableId;
	private Map<String, RowField> rowDataMap = new HashMap<String, RowField>(); 
	
	/**
	 * 資料所附屬的 Table 名稱，與 Client 端畫面的欄位元件名稱相符
	 * @param tableId 交易畫面元件名稱
	 */
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}	
	/**
	 * 設定某列資料
	 * @param nRow 指定列數，需大於等於零
	 * @return 指定列物件的參照
	 */
	public RowField getRow(int nRow) {
		if (nRow >= 0) {
			String sRowKey = String.format("%d", nRow);
			if (rowDataMap.containsKey(sRowKey)) {
				return rowDataMap.get(sRowKey);
			} else {
				RowField obj = new RowField();
				rowDataMap.put(sRowKey, obj);
				return obj;
			}		
		} 
		return null;
	}
	
	/**
	 * 
	 * @return 指定列物件的參照
	 */
	public RowField addRow()
	{
		int nRowCount = rowDataMap.size();
		return getRow(nRowCount);
	}
	
	@Override
	public String toString()
	{
		final String strDataFormat = "<row id='%s'>%s</row>";
		String outputData = String.format("<table id='%s'> " +
					"<rowCnt data='%d' />", 
					this.tableId, this.rowDataMap.size()); 
					 
		String strRowData = "<screen>";		
		Set<String> keys = rowDataMap.keySet();		
		for(String key: keys) {
			strRowData += String.format(strDataFormat, key, rowDataMap.get(key));
		}
		strRowData += "</screen>";
		return outputData + strRowData + "</table>";
	}
}
