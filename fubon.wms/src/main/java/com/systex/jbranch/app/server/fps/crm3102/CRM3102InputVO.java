package com.systex.jbranch.app.server.fps.crm3102;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM3102InputVO extends PagingInputVO{
	private String PRJ_ID;                   //專案代號
	private String PRJ_TYPE;				 //專案類型
	private String PRJ_SUB_TYPE;			 //加強管控專案類型 1:一般理專 2:高端理專
	private String PRJ_NAME;				 //專案名稱
	private Date sdate;						 //匯入日期(起)
	private Date edate;						 //匯入日期(迄)
	private String PRJ_STATUS;				 //匯入狀態
	private List PRJ_DTL;					 //匯入名單
	private List AOCODE_LST;				 //客戶理專AOCODE 變更紀錄
	private String PRJ_NOTE;                 //專案說明
	private String IMP_FILE_NAME;			 //檔案名稱
	private String set_name;
	private String fileName;
	private String realfileName;
	private String acttype;
	private String comboflag;
	private List<Map<String, Object>> list;
	
	private String list_name;
	private String fileName2;
	private String realfileName2;
	private BigDecimal seq;
	private Date PRJ_EXE_DATE;
	private Date STATEMENT_DATE;
	private String EMP_ID;
	private String CUST_ID;
	private String BRANCH_NBR;
	
	public String getPRJ_ID() {
		return PRJ_ID;
	}

	public void setPRJ_ID(String pRJ_ID) {
		PRJ_ID = pRJ_ID;
	}

	public String getPRJ_TYPE() {
		return PRJ_TYPE;
	}

	public void setPRJ_TYPE(String pRJ_TYPE) {
		PRJ_TYPE = pRJ_TYPE;
	}

	public String getPRJ_SUB_TYPE() {
		return PRJ_SUB_TYPE;
	}

	public void setPRJ_SUB_TYPE(String pRJ_SUB_TYPE) {
		PRJ_SUB_TYPE = pRJ_SUB_TYPE;
	}

	public String getPRJ_NAME() {
		return PRJ_NAME;
	}

	public void setPRJ_NAME(String pRJ_NAME) {
		PRJ_NAME = pRJ_NAME;
	}

	public Date getSdate() {
		return sdate;
	}

	public void setSdate(Date sdate) {
		this.sdate = sdate;
	}

	public Date getEdate() {
		return edate;
	}

	public void setEdate(Date edate) {
		this.edate = edate;
	}

	public String getPRJ_STATUS() {
		return PRJ_STATUS;
	}

	public void setPRJ_STATUS(String pRJ_STATUS) {
		PRJ_STATUS = pRJ_STATUS;
	}

	public List getPRJ_DTL() {
		return PRJ_DTL;
	}

	public void setPRJ_DTL(List pRJ_DTL) {
		PRJ_DTL = pRJ_DTL;
	}

	public List getAOCODE_LST() {
		return AOCODE_LST;
	}

	public void setAOCODE_LST(List aOCODE_LST) {
		AOCODE_LST = aOCODE_LST;
	}

	public String getPRJ_NOTE() {
		return PRJ_NOTE;
	}

	public void setPRJ_NOTE(String pRJ_NOTE) {
		PRJ_NOTE = pRJ_NOTE;
	}

	public String getIMP_FILE_NAME() {
		return IMP_FILE_NAME;
	}

	public void setIMP_FILE_NAME(String iMP_FILE_NAME) {
		IMP_FILE_NAME = iMP_FILE_NAME;
	}

	public String getSet_name() {
		return set_name;
	}

	public void setSet_name(String set_name) {
		this.set_name = set_name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRealfileName() {
		return realfileName;
	}

	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}

	public String getActtype() {
		return acttype;
	}

	public void setActtype(String acttype) {
		this.acttype = acttype;
	}

	public String getComboflag() {
		return comboflag;
	}

	public void setComboflag(String comboflag) {
		this.comboflag = comboflag;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public String getList_name() {
		return list_name;
	}

	public void setList_name(String list_name) {
		this.list_name = list_name;
	}

	public String getFileName2() {
		return fileName2;
	}

	public void setFileName2(String fileName2) {
		this.fileName2 = fileName2;
	}

	public String getRealfileName2() {
		return realfileName2;
	}

	public void setRealfileName2(String realfileName2) {
		this.realfileName2 = realfileName2;
	}

	public BigDecimal getSeq() {
		return seq;
	}

	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}

	public Date getPRJ_EXE_DATE() {
		return PRJ_EXE_DATE;
	}

	public void setPRJ_EXE_DATE(Date pRJ_EXE_DATE) {
		PRJ_EXE_DATE = pRJ_EXE_DATE;
	}

	public Date getSTATEMENT_DATE() {
		return STATEMENT_DATE;
	}

	public void setSTATEMENT_DATE(Date sTATEMENT_DATE) {
		STATEMENT_DATE = sTATEMENT_DATE;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	
}
