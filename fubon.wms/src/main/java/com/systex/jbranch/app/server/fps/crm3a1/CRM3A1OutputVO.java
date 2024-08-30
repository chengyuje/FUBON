package com.systex.jbranch.app.server.fps.crm3a1;

import java.util.List;

public class CRM3A1OutputVO {
	
	private List resultList;
	private List PRJIDList;
	private List downloadList;
	private String fileRealName;
	private String fileType;
	
	

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getPRJIDList() {
		return PRJIDList;
	}

	public void setPRJIDList(List pRJIDList) {
		PRJIDList = pRJIDList;
	}

	public List getDownloadList() {
		return downloadList;
	}

	public void setDownloadList(List downloadList) {
		this.downloadList = downloadList;
	}

	public String getFileRealName() {
		return fileRealName;
	}

	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}



	


}
