package com.systex.jbranch.common.xmlInfo;

import java.util.List;
import java.util.Map;

public class XMLInfoOutputVO {
	private List<Map<String, Object>> xmlInfoList; //取回的xml清單資料

	public List<Map<String, Object>> getXmlInfoList() {
		return xmlInfoList;
	}

	public void setXmlInfoList(List<Map<String, Object>> xmlInfoList) {
		this.xmlInfoList = xmlInfoList;
	}


	
}