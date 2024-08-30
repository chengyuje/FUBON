package com.systex.jbranch.app.server.fps.org210;

import java.util.List;
import java.util.Map;

public class ORG210InputVO {

	private String DEPT_ID        = "";
	private String DEPT_NAME      = "";
	private String ROLE_ID        = ""; 
	private String EMP_ID         = "";
	private String EMP_NAME       = "";
	private String AO_CODE        = "";
	private String ORG_TYPE       = "";
	private String PARENT_DEPT_ID = "";
	private String DEPT_DEGREE    = "";
	private String DEPT_GROUP     = "";
	private List<Map<String, String>> EMP_GROUP_LST = null;
	private List<Map<String, String>> DEL_MEMBER_LST = null;
	private List<Map<String, Object>> ADD_MEMBER_LST = null;
	
	private List exportList;
	
	public List getExportList() {
		return exportList;
	}
	public void setExportList(List exportList) {
		this.exportList = exportList;
	}
	public String getDEPT_ID() {
		return DEPT_ID;
	}
	public void setDEPT_ID(String dEPT_ID) {
		DEPT_ID = dEPT_ID;
	}
	public String getDEPT_NAME() {
		return DEPT_NAME;
	}
	public void setDEPT_NAME(String dEPT_NAME) {
		DEPT_NAME = dEPT_NAME;
	}
	public String getROLE_ID() {
		return ROLE_ID;
	}
	public void setROLE_ID(String rOLE_ID) {
		ROLE_ID = rOLE_ID;
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
	public String getAO_CODE() {
		return AO_CODE;
	}
	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}
	public String getORG_TYPE() {
		return ORG_TYPE;
	}
	public void setORG_TYPE(String oRG_TYPE) {
		ORG_TYPE = oRG_TYPE;
	}
	public String getPARENT_DEPT_ID() {
		return PARENT_DEPT_ID;
	}
	public void setPARENT_DEPT_ID(String pARENT_DEPT_ID) {
		PARENT_DEPT_ID = pARENT_DEPT_ID;
	}
	public String getDEPT_DEGREE() {
		return DEPT_DEGREE;
	}
	public void setDEPT_DEGREE(String dEPT_DEGREE) {
		DEPT_DEGREE = dEPT_DEGREE;
	}
	public String getDEPT_GROUP() {
		return DEPT_GROUP;
	}
	public void setDEPT_GROUP(String dEPT_GROUP) {
		DEPT_GROUP = dEPT_GROUP;
	}
	public List<Map<String, String>> getEMP_GROUP_LST() {
		return EMP_GROUP_LST;
	}
	public void setEMP_GROUP_LST(List<Map<String, String>> eMP_GROUP_LST) {
		EMP_GROUP_LST = eMP_GROUP_LST;
	}
	public List<Map<String, String>> getDEL_MEMBER_LST() {
		return DEL_MEMBER_LST;
	}
	public void setDEL_MEMBER_LST(List<Map<String, String>> dEL_MEMBER_LST) {
		DEL_MEMBER_LST = dEL_MEMBER_LST;
	}
	public List<Map<String, Object>> getADD_MEMBER_LST() {
		return ADD_MEMBER_LST;
	}
	public void setADD_MEMBER_LST(List<Map<String, Object>> aDD_MEMBER_LST) {
		ADD_MEMBER_LST = aDD_MEMBER_LST;
	}
	
}
