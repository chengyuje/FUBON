package com.systex.jbranch.app.server.fps.prd178;

import java.util.List;

public class PRD178OutputVO {

	private List resultList;
	private List projectBigClassList;
	private List projectMidClassList;
	private List projectSmallClassList;
	private List companyNameList;

	private List<String> PROD_MAIN_notExist; //對應TBJSB_INS_PROD_MAIN不存在
	private List<String> PROD_LIFEITEM_notExist; //對應TBJSB_INS_PROD_LIFEITEM不存在
	private List<String> typeError; // 數值不符合資料庫欄位型態（紀錄表格位置）
	private List<String> requiredError; // 若為新增，缺少必要欄位（紀錄表格位置）
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getProjectBigClassList() {
		return projectBigClassList;
	}
	public void setProjectBigClassList(List projectBigClassList) {
		this.projectBigClassList = projectBigClassList;
	}
	public List getProjectMidClassList() {
		return projectMidClassList;
	}
	public void setProjectMidClassList(List projectMidClassList) {
		this.projectMidClassList = projectMidClassList;
	}
	public List getProjectSmallClassList() {
		return projectSmallClassList;
	}
	public void setProjectSmallClassList(List projectSmallClassList) {
		this.projectSmallClassList = projectSmallClassList;
	}
	public List getCompanyNameList() {
		return companyNameList;
	}
	public void setCompanyNameList(List companyNameList) {
		this.companyNameList = companyNameList;
	}
	public List<String> getPROD_MAIN_notExist() {
		return PROD_MAIN_notExist;
	}
	public void setPROD_MAIN_notExist(List<String> pROD_MAIN_notExist) {
		PROD_MAIN_notExist = pROD_MAIN_notExist;
	}
	public List<String> getPROD_LIFEITEM_notExist() {
		return PROD_LIFEITEM_notExist;
	}
	public void setPROD_LIFEITEM_notExist(List<String> pROD_LIFEITEM_notExist) {
		PROD_LIFEITEM_notExist = pROD_LIFEITEM_notExist;
	}
	public List<String> getTypeError() {
		return typeError;
	}
	public void setTypeError(List<String> typeError) {
		this.typeError = typeError;
	}
	public List<String> getRequiredError() {
		return requiredError;
	}
	public void setRequiredError(List<String> requiredError) {
		this.requiredError = requiredError;
	}

	

}
