package com.systex.jbranch.common.xmlInfo;

import java.util.List;

public class XMLInfoInputVO {
	private List<?> xmlInfoList; //xml清單資料
	private String tableName;
	private String fieldLabel;
	private String fieldValue;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public List<?> getXmlInfoList() {
		return xmlInfoList;
	}

	public void setXmlInfoList(List<String> xmlInfoList) {
		this.xmlInfoList = xmlInfoList;
	}
}
