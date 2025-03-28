package com.systex.jbranch.app.server.fps.prd235;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD235InputVO extends PagingInputVO{
	private BigDecimal SEQ_NO;
	private String PRD_ID;
	private String FUND_NAME;
	private String TRADE_TYPE;
	private String PRD_YEAR;
	private String PRD_CATEGORY;
	private Timestamp START_DATE;
	private Timestamp END_DATE;
	private Timestamp DEADLINE_DATE;
	private Timestamp TRADE_DATE;
	private String saveType;
	private BigDecimal MIN_PURCHASE_AMT;
	private String fileName;
	private String fileRealName;
	private String validDateYN; //在交易期間中的資料
	private String PRD_SEQ_NO;
	private BigDecimal ADJ_UNIT_NUM;
	private String STATUS;
	private BigDecimal NEW_RDM_TOTAL_UNITS;
	private List<Map<String, Object>> adjList;
	private BigDecimal DIV_SEQ_NO;
	private String DIVIDEND_CATEGORY;
	
	public BigDecimal getSEQ_NO() {
		return SEQ_NO;
	}
	public void setSEQ_NO(BigDecimal sEQ_NO) {
		SEQ_NO = sEQ_NO;
	}
	public String getPRD_ID() {
		return PRD_ID;
	}
	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}
	public String getFUND_NAME() {
		return FUND_NAME;
	}
	public void setFUND_NAME(String fUND_NAME) {
		FUND_NAME = fUND_NAME;
	}
	public String getTRADE_TYPE() {
		return TRADE_TYPE;
	}
	public void setTRADE_TYPE(String tRADE_TYPE) {
		TRADE_TYPE = tRADE_TYPE;
	}
	public String getPRD_YEAR() {
		return PRD_YEAR;
	}
	public void setPRD_YEAR(String pRD_YEAR) {
		PRD_YEAR = pRD_YEAR;
	}
	public String getPRD_CATEGORY() {
		return PRD_CATEGORY;
	}
	public void setPRD_CATEGORY(String pRD_CATEGORY) {
		PRD_CATEGORY = pRD_CATEGORY;
	}
	public Timestamp getSTART_DATE() {
		return START_DATE;
	}
	public void setSTART_DATE(Timestamp sTART_DATE) {
		START_DATE = sTART_DATE;
	}
	public Timestamp getEND_DATE() {
		return END_DATE;
	}
	public void setEND_DATE(Timestamp eND_DATE) {
		END_DATE = eND_DATE;
	}
	public Timestamp getDEADLINE_DATE() {
		return DEADLINE_DATE;
	}
	public void setDEADLINE_DATE(Timestamp dEADLINE_DATE) {
		DEADLINE_DATE = dEADLINE_DATE;
	}
	public Timestamp getTRADE_DATE() {
		return TRADE_DATE;
	}
	public void setTRADE_DATE(Timestamp tRADE_DATE) {
		TRADE_DATE = tRADE_DATE;
	}
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	public BigDecimal getMIN_PURCHASE_AMT() {
		return MIN_PURCHASE_AMT;
	}
	public void setMIN_PURCHASE_AMT(BigDecimal mIN_PURCHASE_AMT) {
		MIN_PURCHASE_AMT = mIN_PURCHASE_AMT;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	public String getValidDateYN() {
		return validDateYN;
	}
	public void setValidDateYN(String validDateYN) {
		this.validDateYN = validDateYN;
	}
	public String getPRD_SEQ_NO() {
		return PRD_SEQ_NO;
	}
	public void setPRD_SEQ_NO(String pRD_SEQ_NO) {
		PRD_SEQ_NO = pRD_SEQ_NO;
	}
	public BigDecimal getADJ_UNIT_NUM() {
		return ADJ_UNIT_NUM;
	}
	public void setADJ_UNIT_NUM(BigDecimal aDJ_UNIT_NUM) {
		ADJ_UNIT_NUM = aDJ_UNIT_NUM;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public BigDecimal getNEW_RDM_TOTAL_UNITS() {
		return NEW_RDM_TOTAL_UNITS;
	}
	public void setNEW_RDM_TOTAL_UNITS(BigDecimal nEW_RDM_TOTAL_UNITS) {
		NEW_RDM_TOTAL_UNITS = nEW_RDM_TOTAL_UNITS;
	}
	public List<Map<String, Object>> getAdjList() {
		return adjList;
	}
	public void setAdjList(List<Map<String, Object>> adjList) {
		this.adjList = adjList;
	}
	public BigDecimal getDIV_SEQ_NO() {
		return DIV_SEQ_NO;
	}
	public void setDIV_SEQ_NO(BigDecimal dIV_SEQ_NO) {
		DIV_SEQ_NO = dIV_SEQ_NO;
	}
	public String getDIVIDEND_CATEGORY() {
		return DIVIDEND_CATEGORY;
	}
	public void setDIVIDEND_CATEGORY(String dIVIDEND_CATEGORY) {
		DIVIDEND_CATEGORY = dIVIDEND_CATEGORY;
	}
	
}
