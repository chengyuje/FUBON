package com.systex.jbranch.app.server.fps.prd178;

import java.util.Date;

public class PRD178InputVO {

	private String PROJECTNAME; // 專案名稱
	private String PROJECTID; // 專案代碼
	private Date sCreDate;
	private Date endDate;

	private String fileName;

	// 更新欄位用
	private String deleteProjectID; // 專案代碼
	private String CNAME; // 保險公司
	private String DEPARTMENTTYPE; // 通路型態
	private String PROJECTTYPE2; // 專案大分類
	private String PROJECTTYPE; // 專案中分類
	private String PROJECTTYPE1; // 專案小分類
	private String PERMER; // 保費固定
	private Date VALIDFROM; // 開始銷售日
	private Date VALIDTHRU; // 停止銷售日
	private String INSCOPRODUCTNAME; // 保險公司產品名稱
	private String INSCOPRODUCTID; // 保險公司產品代號
	private String MEMO; // 備註
	private String MPRODUCTID; // 產品代號
	private String MPRODUCTNAME; // 產品名稱
	private String PAYANDPROTECT; // 繳費/保障方式
	private String COVERAGEDUEPREMIUM; // 單位數
	private String CONTCOVERAGEDUEPREMIUM; // 保費
	private boolean updateTBJSB_INS_PROD_PROJECT;
	private boolean updateTBJSB_INS_PROD_MAIN;
	private boolean updateTBJSB_INS_PROD_PROJECT_CONT;
	private boolean updateTBJSB_INS_PROD_LIFEITEM;

	public String getPROJECTNAME() {
		return PROJECTNAME;
	}

	public void setPROJECTNAME(String pROJECTNAME) {
		PROJECTNAME = pROJECTNAME;
	}

	public String getPROJECTID() {
		return PROJECTID;
	}

	public void setPROJECTID(String pROJECTID) {
		PROJECTID = pROJECTID;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCNAME() {
		return CNAME;
	}

	public void setCNAME(String cNAME) {
		CNAME = cNAME;
	}

	public String getDEPARTMENTTYPE() {
		return DEPARTMENTTYPE;
	}

	public void setDEPARTMENTTYPE(String dEPARTMENTTYPE) {
		DEPARTMENTTYPE = dEPARTMENTTYPE;
	}

	public String getPROJECTTYPE2() {
		return PROJECTTYPE2;
	}

	public void setPROJECTTYPE2(String pROJECTTYPE2) {
		PROJECTTYPE2 = pROJECTTYPE2;
	}

	public String getPROJECTTYPE() {
		return PROJECTTYPE;
	}

	public void setPROJECTTYPE(String pROJECTTYPE) {
		PROJECTTYPE = pROJECTTYPE;
	}

	public String getPROJECTTYPE1() {
		return PROJECTTYPE1;
	}

	public void setPROJECTTYPE1(String pROJECTTYPE1) {
		PROJECTTYPE1 = pROJECTTYPE1;
	}

	public String getPERMER() {
		return PERMER;
	}

	public void setPERMER(String pERMER) {
		PERMER = pERMER;
	}

	public Date getVALIDFROM() {
		return VALIDFROM;
	}

	public void setVALIDFROM(Date vALIDFROM) {
		VALIDFROM = vALIDFROM;
	}

	public Date getVALIDTHRU() {
		return VALIDTHRU;
	}

	public void setVALIDTHRU(Date vALIDTHRU) {
		VALIDTHRU = vALIDTHRU;
	}

	public String getINSCOPRODUCTNAME() {
		return INSCOPRODUCTNAME;
	}

	public void setINSCOPRODUCTNAME(String iNSCOPRODUCTNAME) {
		INSCOPRODUCTNAME = iNSCOPRODUCTNAME;
	}

	public String getINSCOPRODUCTID() {
		return INSCOPRODUCTID;
	}

	public void setINSCOPRODUCTID(String iNSCOPRODUCTID) {
		INSCOPRODUCTID = iNSCOPRODUCTID;
	}

	public String getMEMO() {
		return MEMO;
	}

	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}

	public String getMPRODUCTID() {
		return MPRODUCTID;
	}

	public void setMPRODUCTID(String mPRODUCTID) {
		MPRODUCTID = mPRODUCTID;
	}

	public String getMPRODUCTNAME() {
		return MPRODUCTNAME;
	}

	public void setMPRODUCTNAME(String mPRODUCTNAME) {
		MPRODUCTNAME = mPRODUCTNAME;
	}



	public String getPAYANDPROTECT() {
		return PAYANDPROTECT;
	}

	public void setPAYANDPROTECT(String pAYANDPROTECT) {
		PAYANDPROTECT = pAYANDPROTECT;
	}

	public String getCOVERAGEDUEPREMIUM() {
		return COVERAGEDUEPREMIUM;
	}

	public void setCOVERAGEDUEPREMIUM(String cOVERAGEDUEPREMIUM) {
		COVERAGEDUEPREMIUM = cOVERAGEDUEPREMIUM;
	}

	public String getCONTCOVERAGEDUEPREMIUM() {
		return CONTCOVERAGEDUEPREMIUM;
	}

	public void setCONTCOVERAGEDUEPREMIUM(String cONTCOVERAGEDUEPREMIUM) {
		CONTCOVERAGEDUEPREMIUM = cONTCOVERAGEDUEPREMIUM;
	}

	public boolean isUpdateTBJSB_INS_PROD_PROJECT() {
		return updateTBJSB_INS_PROD_PROJECT;
	}

	public void setUpdateTBJSB_INS_PROD_PROJECT(boolean updateTBJSB_INS_PROD_PROJECT) {
		this.updateTBJSB_INS_PROD_PROJECT = updateTBJSB_INS_PROD_PROJECT;
	}

	public boolean isUpdateTBJSB_INS_PROD_MAIN() {
		return updateTBJSB_INS_PROD_MAIN;
	}

	public void setUpdateTBJSB_INS_PROD_MAIN(boolean updateTBJSB_INS_PROD_MAIN) {
		this.updateTBJSB_INS_PROD_MAIN = updateTBJSB_INS_PROD_MAIN;
	}

	public boolean isUpdateTBJSB_INS_PROD_PROJECT_CONT() {
		return updateTBJSB_INS_PROD_PROJECT_CONT;
	}

	public void setUpdateTBJSB_INS_PROD_PROJECT_CONT(boolean updateTBJSB_INS_PROD_PROJECT_CONT) {
		this.updateTBJSB_INS_PROD_PROJECT_CONT = updateTBJSB_INS_PROD_PROJECT_CONT;
	}

	public boolean isUpdateTBJSB_INS_PROD_LIFEITEM() {
		return updateTBJSB_INS_PROD_LIFEITEM;
	}

	public void setUpdateTBJSB_INS_PROD_LIFEITEM(boolean updateTBJSB_INS_PROD_LIFEITEM) {
		this.updateTBJSB_INS_PROD_LIFEITEM = updateTBJSB_INS_PROD_LIFEITEM;
	}

	public String getDeleteProjectID() {
		return deleteProjectID;
	}

	public void setDeleteProjectID(String deleteProjectID) {
		this.deleteProjectID = deleteProjectID;
	}

}
