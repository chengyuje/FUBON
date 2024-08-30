package com.systex.jbranch.app.server.fps.org250;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG250InputVO extends PagingInputVO {
	
	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
	private String EMP_ID;
	private String EMP_NAME;
	private String FILE_NAME;
	private String ACTUAL_FILE_NAME;
	private List<Map<String, String>> AGENT_LST;
	
	private String SEQNO;
	private String AGENT_ID_1;
	private String AGENT_ID_2;
	private String AGENT_ID_3;
	private String ACT_TYPE;
	private String REVIEW_STATUS;
	
	private List exportList;
	
	public List getExportList() {
		return exportList;
	}

	public void setExportList(List exportList) {
		this.exportList = exportList;
	}

	public String getRegion_center_id() {
		return region_center_id;
	}
	
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	
	public String getBranch_area_id() {
		return branch_area_id;
	}
	
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	
	public String getBranch_nbr() {
		return branch_nbr;
	}
	
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	
	public String getEMP_ID() {
		return EMP_ID;
	}
	
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	
	public String getEMP_NAME() {
		return EMP_NAME;
	}
	
	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
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
	
	public List<Map<String, String>> getAGENT_LST() {
		return AGENT_LST;
	}
	
	public void setAGENT_LST(List<Map<String, String>> aGENT_LST) {
		AGENT_LST = aGENT_LST;
	}
	
	public String getSEQNO() {
		return SEQNO;
	}
	
	public void setSEQNO(String sEQNO) {
		SEQNO = sEQNO;
	}
	
	public String getAGENT_ID_1() {
		return AGENT_ID_1;
	}
	
	public void setAGENT_ID_1(String aGENT_ID_1) {
		AGENT_ID_1 = aGENT_ID_1;
	}
	
	public String getAGENT_ID_2() {
		return AGENT_ID_2;
	}
	
	public void setAGENT_ID_2(String aGENT_ID_2) {
		AGENT_ID_2 = aGENT_ID_2;
	}
	
	public String getAGENT_ID_3() {
		return AGENT_ID_3;
	}
	
	public void setAGENT_ID_3(String aGENT_ID_3) {
		AGENT_ID_3 = aGENT_ID_3;
	}
	
	public String getACT_TYPE() {
		return ACT_TYPE;
	}
	
	public void setACT_TYPE(String aCT_TYPE) {
		ACT_TYPE = aCT_TYPE;
	}
	
	public String getREVIEW_STATUS() {
		return REVIEW_STATUS;
	}
	
	public void setREVIEW_STATUS(String rEVIEW_STATUS) {
		REVIEW_STATUS = rEVIEW_STATUS;
	}
	
}
