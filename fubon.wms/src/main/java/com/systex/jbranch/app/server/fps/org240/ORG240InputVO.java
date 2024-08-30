package com.systex.jbranch.app.server.fps.org240;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG240InputVO extends PagingInputVO {
	
	private String SUPT_SALES_TEAM_ID;
	private String FILE_NAME;
	private String ACTUAL_FILE_NAME;
	private List<Map<String, String>> SUPT_SALES_TEAM_LST;

	public String getSUPT_SALES_TEAM_ID() {
		return SUPT_SALES_TEAM_ID;
	}

	public void setSUPT_SALES_TEAM_ID(String sUPT_SALES_TEAM_ID) {
		SUPT_SALES_TEAM_ID = sUPT_SALES_TEAM_ID;
	}

	public String getFILE_NAME() {
		return FILE_NAME;
	}

	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}

	public String getACTUAL_FILE_NAME() {
		return ACTUAL_FILE_NAME;
	}

	public void setACTUAL_FILE_NAME(String aCTUAL_FILE_NAME) {
		ACTUAL_FILE_NAME = aCTUAL_FILE_NAME;
	}

	public List<Map<String, String>> getSUPT_SALES_TEAM_LST() {
		return SUPT_SALES_TEAM_LST;
	}

	public void setSUPT_SALES_TEAM_LST(List<Map<String, String>> sUPT_SALES_TEAM_LST) {
		SUPT_SALES_TEAM_LST = sUPT_SALES_TEAM_LST;
	}
	
}
