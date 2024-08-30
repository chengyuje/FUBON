package com.systex.jbranch.app.server.fps.iot150;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT150OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> IOT_MAIN;
	private List<Map<String, Object>> next_statusList;
	private List<Map<String, Object>> csvList; 
	
	public List<Map<String, Object>> getCsvList() {
		return csvList;
	}

	public void setCsvList(List<Map<String, Object>> csvList) {
		this.csvList = csvList;
	}

	public List<Map<String, Object>> getNext_statusList() {
		return next_statusList;
	}

	public void setNext_statusList(List<Map<String, Object>> next_statusList) {
		this.next_statusList = next_statusList;
	}

	public List<Map<String, Object>> getIOT_MAIN() {
		return IOT_MAIN;
	}

	public void setIOT_MAIN(List<Map<String, Object>> iOT_MAIN) {
		IOT_MAIN = iOT_MAIN;
	}

	
}
