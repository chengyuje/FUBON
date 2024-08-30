package com.systex.jbranch.app.server.fps.ins960;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS960InputVO extends PagingInputVO {
	private BigDecimal para_no;
	private String ins_type;
	private List<Map<String, Object>> suggestList;
	private String cal_desc;
	private String status;
	//add by Brian
	private String para_type;
	
	private BigDecimal file_seq;
	private String fileName;
	private String fileRealName;
	// OLD CODE
	private String KEY_NO;
	private String PRD_ID;
	private String INSPRD_ANNUAL;
	
	private String INSDATA_KEYNO;
	
	private List<Map<String, Object>> checkData;
	
	
	public BigDecimal getPara_no() {
		return para_no;
	}
	public void setPara_no(BigDecimal para_no) {
		this.para_no = para_no;
	}
	public String getIns_type() {
		return ins_type;
	}
	public void setIns_type(String ins_type) {
		this.ins_type = ins_type;
	}
	public List<Map<String, Object>> getSuggestList() {
		return suggestList;
	}
	public void setSuggestList(List<Map<String, Object>> suggestList) {
		this.suggestList = suggestList;
	}
	public String getCal_desc() {
		return cal_desc;
	}
	public void setCal_desc(String cal_desc) {
		this.cal_desc = cal_desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getFile_seq() {
		return file_seq;
	}
	public void setFile_seq(BigDecimal file_seq) {
		this.file_seq = file_seq;
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
	public String getKEY_NO() {
		return KEY_NO;
	}
	public void setKEY_NO(String kEY_NO) {
		KEY_NO = kEY_NO;
	}
	public String getPRD_ID() {
		return PRD_ID;
	}
	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}
	public String getINSPRD_ANNUAL() {
		return INSPRD_ANNUAL;
	}
	public void setINSPRD_ANNUAL(String iNSPRD_ANNUAL) {
		INSPRD_ANNUAL = iNSPRD_ANNUAL;
	}
	public String getPara_type() {
		return para_type;
	}
	public void setPara_type(String para_type) {
		this.para_type = para_type;
	}
	public String getINSDATA_KEYNO() {
		return INSDATA_KEYNO;
	}
	public void setINSDATA_KEYNO(String iNSDATA_KEYNO) {
		INSDATA_KEYNO = iNSDATA_KEYNO;
	}
	public List<Map<String, Object>> getCheckData() {
		return checkData;
	}
	public void setCheckData(List<Map<String, Object>> checkData) {
		this.checkData = checkData;
	}
	
	
	
}