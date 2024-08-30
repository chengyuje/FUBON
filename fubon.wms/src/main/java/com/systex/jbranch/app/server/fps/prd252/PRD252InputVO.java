package com.systex.jbranch.app.server.fps.prd252;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD252InputVO extends PagingInputVO {

	private String fileName;
	private String fileRealName;
	private String prdNo;
	private String prdId;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setPrdNo(String prdNo) {
		this.prdNo = prdNo;
	}
	public String getPrdNo() {
		return prdNo;
	}
	public void setPrdId(String prdId) {
		this.prdId = prdId;
	}
	public String getPrdId() {
		return prdId;
	}
}
