package com.systex.jbranch.app.server.fps.org410;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG410InputVO extends PagingInputVO {
	
	private String CHANGE_TYPE;
	private String EMP_ID;
	private String EMP_NAME;
	private String HIS_BRANCH_AREA_ID;
	private String BRANCH_AREA_ID;
	private String HIS_BRANCH_ID;
	private String BRANCH_ID;
	private String HIS_JOB_TITLE_NAME;
	private String JOB_TITLE_NAME;
	private String HIS_JOB_RANK;
	private String JOB_RANK;
	private String START_TIME;
	private String END_TIME;
	private List<Map<String, String>> EXPORT_LST;
	
	public String getCHANGE_TYPE() {
		return CHANGE_TYPE;
	}
	
	public void setCHANGE_TYPE(String cHANGE_TYPE) {
		CHANGE_TYPE = cHANGE_TYPE;
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
	
	public String getHIS_BRANCH_AREA_ID() {
		return HIS_BRANCH_AREA_ID;
	}
	
	public void setHIS_BRANCH_AREA_ID(String hIS_BRANCH_AREA_ID) {
		HIS_BRANCH_AREA_ID = hIS_BRANCH_AREA_ID;
	}
	
	public String getBRANCH_AREA_ID() {
		return BRANCH_AREA_ID;
	}
	
	public void setBRANCH_AREA_ID(String bRANCH_AREA_ID) {
		BRANCH_AREA_ID = bRANCH_AREA_ID;
	}
	
	public String getHIS_BRANCH_ID() {
		return HIS_BRANCH_ID;
	}
	
	public void setHIS_BRANCH_ID(String hIS_BRANCH_ID) {
		HIS_BRANCH_ID = hIS_BRANCH_ID;
	}
	
	public String getBRANCH_ID() {
		return BRANCH_ID;
	}
	
	public void setBRANCH_ID(String bRANCH_ID) {
		BRANCH_ID = bRANCH_ID;
	}
	
	public String getHIS_JOB_TITLE_NAME() {
		return HIS_JOB_TITLE_NAME;
	}
	
	public void setHIS_JOB_TITLE_NAME(String hIS_JOB_TITLE_NAME) {
		HIS_JOB_TITLE_NAME = hIS_JOB_TITLE_NAME;
	}
	
	public String getJOB_TITLE_NAME() {
		return JOB_TITLE_NAME;
	}
	
	public void setJOB_TITLE_NAME(String jOB_TITLE_NAME) {
		JOB_TITLE_NAME = jOB_TITLE_NAME;
	}
	
	public String getHIS_JOB_RANK() {
		return HIS_JOB_RANK;
	}
	
	public void setHIS_JOB_RANK(String hIS_JOB_RANK) {
		HIS_JOB_RANK = hIS_JOB_RANK;
	}
	
	public String getJOB_RANK() {
		return JOB_RANK;
	}
	
	public void setJOB_RANK(String jOB_RANK) {
		JOB_RANK = jOB_RANK;
	}
	
	public String getSTART_TIME() {
		return START_TIME;
	}
	
	public void setSTART_TIME(String sTART_TIME) {
		START_TIME = sTART_TIME;
	}
	
	public String getEND_TIME() {
		return END_TIME;
	}
	
	public void setEND_TIME(String eND_TIME) {
		END_TIME = eND_TIME;
	}
	
	public List<Map<String, String>> getEXPORT_LST() {
		return EXPORT_LST;
	}
	
	public void setEXPORT_LST(List<Map<String, String>> eXPORT_LST) {
		EXPORT_LST = eXPORT_LST;
	}
	
}
