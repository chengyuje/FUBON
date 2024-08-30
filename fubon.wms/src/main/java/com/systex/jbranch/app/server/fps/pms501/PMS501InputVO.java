package com.systex.jbranch.app.server.fps.pms501;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS501InputVO extends PagingInputVO {

	private String FILE_NAME;
	private String ACTUAL_FILE_NAME;
	
	private String selectType;
	
	private String dtccc;
	
	private List<Map<String, Object>> exportList;

	public String getSelectType() {
		return selectType;
	}

	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}

	public String getFILE_NAME() {
		return FILE_NAME;
	}

	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}

	public String getDtccc() {
		return dtccc;
	}

	public void setDtccc(String dtccc) {
		this.dtccc = dtccc;
	}

	public List<Map<String, Object>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, Object>> exportList) {
		this.exportList = exportList;
	}

	public String getACTUAL_FILE_NAME() {
		return ACTUAL_FILE_NAME;
	}

	public void setACTUAL_FILE_NAME(String aCTUAL_FILE_NAME) {
		ACTUAL_FILE_NAME = aCTUAL_FILE_NAME;
	}

}
