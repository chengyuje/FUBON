package com.systex.jbranch.app.server.fps.crm3101;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM3101InputVO extends PagingInputVO{
	private String PRJ_ID;                   //專案代號
	private String PRJ_NAME;				 //專案名稱
	private Date sdate;						 //匯入日期(起)
	private Date edate;						 //匯入日期(迄)
	private String PRJ_STATUS;				 //匯入狀態
	private List PRJ_DTL;					 //匯入名單
	private List AOCODE_LST;				 //客戶理專AOCODE 變更紀錄
	private String PRJ_NOTE;                 //專案說明
	private String DESC_01;					 //欄位1說明
	private String DESC_02;					 //欄位2說明
	private String DESC_03;					 //欄位3說明
	private String DESC_04;					 //欄位4說明
	private String DESC_05;					 //欄位5說明
	private String DESC_06;					 //欄位6說明
	private String DESC_07;					 //欄位7說明
	private String DESC_08; 				 //欄位8說明
	private String DESC_09;					 //欄位9說明
	private String DESC_10;					 //欄位10說明
	private String DESC_11;					 //欄位11說明
	private String DESC_12;					 //欄位12說明
	private String DESC_13;					 //欄位13說明
	private String DESC_14;					 //欄位14說明
	private String DESC_15;					 //欄位15說明
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
	
	
	public String getPRJ_ID() {
		return PRJ_ID;
	}
	public void setPRJ_ID(String pRJ_ID) {
		PRJ_ID = pRJ_ID;
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
	public String getDESC_01() {
		return DESC_01;
	}
	public void setDESC_01(String dESC_01) {
		DESC_01 = dESC_01;
	}
	public String getDESC_02() {
		return DESC_02;
	}
	public void setDESC_02(String dESC_02) {
		DESC_02 = dESC_02;
	}
	public String getDESC_03() {
		return DESC_03;
	}
	public void setDESC_03(String dESC_03) {
		DESC_03 = dESC_03;
	}
	public String getDESC_04() {
		return DESC_04;
	}
	public void setDESC_04(String dESC_04) {
		DESC_04 = dESC_04;
	}
	public String getDESC_05() {
		return DESC_05;
	}
	public void setDESC_05(String dESC_05) {
		DESC_05 = dESC_05;
	}
	public String getDESC_06() {
		return DESC_06;
	}
	public void setDESC_06(String dESC_06) {
		DESC_06 = dESC_06;
	}
	public String getDESC_07() {
		return DESC_07;
	}
	public void setDESC_07(String dESC_07) {
		DESC_07 = dESC_07;
	}
	public String getDESC_08() {
		return DESC_08;
	}
	public void setDESC_08(String dESC_08) {
		DESC_08 = dESC_08;
	}
	public String getDESC_09() {
		return DESC_09;
	}
	public void setDESC_09(String dESC_09) {
		DESC_09 = dESC_09;
	}
	public String getDESC_10() {
		return DESC_10;
	}
	public void setDESC_10(String dESC_10) {
		DESC_10 = dESC_10;
	}
	public String getDESC_11() {
		return DESC_11;
	}
	public void setDESC_11(String dESC_11) {
		DESC_11 = dESC_11;
	}
	public String getDESC_12() {
		return DESC_12;
	}
	public void setDESC_12(String dESC_12) {
		DESC_12 = dESC_12;
	}
	public String getDESC_13() {
		return DESC_13;
	}
	public void setDESC_13(String dESC_13) {
		DESC_13 = dESC_13;
	}
	public String getDESC_14() {
		return DESC_14;
	}
	public void setDESC_14(String dESC_14) {
		DESC_14 = dESC_14;
	}
	public String getDESC_15() {
		return DESC_15;
	}
	public void setDESC_15(String dESC_15) {
		DESC_15 = dESC_15;
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
}
