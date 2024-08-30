package com.systex.jbranch.app.server.fps.iot111;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT111InputVO extends PagingInputVO {
	private String REG_TYPE;
	private String CUST_ID;
	private String STATUS;
	private String PREMATCH_SEQ;
	private String RECRUIT_ID;
	private String INSPRD_ID;
	private String CASE_ID;
	private Date APPLY_DATE_F;
	private Date APPLY_DATE_E;
	private List prematchList;
	private String fileName;
	private String fileRealName;
	private List<Map<String, Object>> resultList;
	private String INS_ID;
	
	public String getREG_TYPE() {
		return REG_TYPE;
	}
	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}
	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}
	public String getRECRUIT_ID() {
		return RECRUIT_ID;
	}
	public void setRECRUIT_ID(String rECRUIT_ID) {
		RECRUIT_ID = rECRUIT_ID;
	}
	public String getINSPRD_ID() {
		return INSPRD_ID;
	}
	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}
	public String getCASE_ID() {
		return CASE_ID;
	}
	public void setCASE_ID(String cASE_ID) {
		CASE_ID = cASE_ID;
	}
	public Date getAPPLY_DATE_F() {
		return APPLY_DATE_F;
	}
	public void setAPPLY_DATE_F(Date aPPLY_DATE_F) {
		APPLY_DATE_F = aPPLY_DATE_F;
	}
	public Date getAPPLY_DATE_E() {
		return APPLY_DATE_E;
	}
	public void setAPPLY_DATE_E(Date aPPLY_DATE_E) {
		APPLY_DATE_E = aPPLY_DATE_E;
	}
	public List getPrematchList() {
		return prematchList;
	}
	public void setPrematchList(List prematchList) {
		this.prematchList = prematchList;
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
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public String getINS_ID() {
		return INS_ID;
	}
	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}	
	
}
