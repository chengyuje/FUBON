package com.systex.jbranch.app.server.fps.ins940;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS940InputVO extends PagingInputVO {
	private BigDecimal para_no;
	private List<Map<String, Object>> diseaseList;
	private List<Map<String, Object>> ltcareList;
	private List<Map<String, Object>> suggestList;
	private String cal_desc;
	private String status;
	
	private BigDecimal file_seq;
	private String fileName;
	private String fileRealName;
	
	
	public BigDecimal getPara_no() {
		return para_no;
	}
	public void setPara_no(BigDecimal para_no) {
		this.para_no = para_no;
	}
	public List<Map<String, Object>> getDiseaseList() {
		return diseaseList;
	}
	public void setDiseaseList(List<Map<String, Object>> diseaseList) {
		this.diseaseList = diseaseList;
	}
	public List<Map<String, Object>> getLtcareList() {
		return ltcareList;
	}
	public void setLtcareList(List<Map<String, Object>> ltcareList) {
		this.ltcareList = ltcareList;
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
}