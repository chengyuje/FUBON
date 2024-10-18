package com.systex.jbranch.app.server.fps.pms432;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS432InputVO extends PagingInputVO {

	private String checkInterval; // 查核區間
	private String similarInfo;   // 相似項目
	private String compareSource; // 比對檔案來源
	private String compareResult; // 比對結果
	private String custID;		  // 客戶ID
	private String empCustID;	  // 理專ID
	private String compareType;	  // 維護註記 相符:1 集中度:2
	private String checkedResult; // 查核結果
	
	private String fileName;
	private String fileRealName;

	private List<Map<String, Object>> list;
	private boolean uploadMark; // 上傳

	public String getCheckInterval() {
		return checkInterval;
	}

	public void setCheckInterval(String checkInterval) {
		this.checkInterval = checkInterval;
	}

	public String getSimilarInfo() {
		return similarInfo;
	}

	public void setSimilarInfo(String similarInfo) {
		this.similarInfo = similarInfo;
	}

	public String getCompareSource() {
		return compareSource;
	}

	public void setCompareSource(String compareSource) {
		this.compareSource = compareSource;
	}

	public String getCompareResult() {
		return compareResult;
	}

	public void setCompareResult(String compareResult) {
		this.compareResult = compareResult;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getEmpCustID() {
		return empCustID;
	}

	public void setEmpCustID(String empCustID) {
		this.empCustID = empCustID;
	}
	
	public String getCompareType() {
		return compareType;
	}

	public void setCompareType(String compareType) {
		this.compareType = compareType;
	}

	public String getCheckedResult() {
		return checkedResult;
	}

	public void setCheckedResult(String checkedResult) {
		this.checkedResult = checkedResult;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
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

	public boolean getUploadMark() {
		return uploadMark;
	}

	public void setUploadMark(boolean uploadMark) {
		this.uploadMark = uploadMark;
	}

}
