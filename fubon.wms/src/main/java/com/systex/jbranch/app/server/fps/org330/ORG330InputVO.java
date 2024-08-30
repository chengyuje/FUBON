package com.systex.jbranch.app.server.fps.org330;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG330InputVO extends PagingInputVO {
	
	private String ROLE_ID; // 角色
	private String ROLE_NAME; // 角色名稱
	private String JOB_TITLE_NAME; // 人資職稱
	private String IS_AO; // 是否為AO
	
	private String SEQNO;
	private String REVIEW_STATUS; // 覆核狀態
	private String ACT_TYPE; // 執行動作
	
	private String SYS_ROLE; // 可視範圍
	
	private String AGENT_ROLE; // 可代理該角色的角色
	
	public String getACT_TYPE() {
		return ACT_TYPE;
	}

	public String getSYS_ROLE() {
		return SYS_ROLE;
	}

	public void setSYS_ROLE(String sYS_ROLE) {
		SYS_ROLE = sYS_ROLE;
	}

	public void setACT_TYPE(String aCT_TYPE) {
		ACT_TYPE = aCT_TYPE;
	}

	public String getSEQNO() {
		return SEQNO;
	}

	public void setSEQNO(String sEQNO) {
		SEQNO = sEQNO;
	}

	public String getREVIEW_STATUS() {
		return REVIEW_STATUS;
	}

	public void setREVIEW_STATUS(String rEVIEW_STATUS) {
		REVIEW_STATUS = rEVIEW_STATUS;
	}

	public String getROLE_ID() {
		return ROLE_ID;
	}
	
	public void setROLE_ID(String rOLE_ID) {
		ROLE_ID = rOLE_ID;
	}
	
	public String getROLE_NAME() {
		return ROLE_NAME;
	}
	
	public String getJOB_TITLE_NAME() {
		return JOB_TITLE_NAME;
	}
	
	public void setJOB_TITLE_NAME(String jOB_TITLE_NAME) {
		JOB_TITLE_NAME = jOB_TITLE_NAME;
	}
	
	public String getIS_AO() {
		return IS_AO;
	}
	
	public void setIS_AO(String iS_AO) {
		IS_AO = iS_AO;
	}
	
	public void setROLE_NAME(String rOLE_NAME) {
		ROLE_NAME = rOLE_NAME;
	}

	public String getAGENT_ROLE() {
		return AGENT_ROLE;
	}

	public void setAGENT_ROLE(String aGENT_ROLE) {
		AGENT_ROLE = aGENT_ROLE;
	}
	
}
