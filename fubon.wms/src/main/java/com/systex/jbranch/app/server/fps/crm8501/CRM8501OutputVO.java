package com.systex.jbranch.app.server.fps.crm8501;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM8501OutputVO extends PagingInputVO {
	private List custNoteList;				//客戶註記清單
	private String dosSeq;                  //資況表的列印序號
	private String url;						//給CRM341下載用的
	private String fileNmae;				//給CRM341下載用的
    private String errorMsg;
	private boolean showMsg;
	
	
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public boolean isShowMsg() {
		return showMsg;
	}
	public void setShowMsg(boolean showMsg) {
		this.showMsg = showMsg;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileNmae() {
		return fileNmae;
	}
	public void setFileNmae(String fileNmae) {
		this.fileNmae = fileNmae;
	}
	public List getCustNoteList() {
		return custNoteList;
	}
	public void setCustNoteList(List custNoteList) {
		this.custNoteList = custNoteList;
	}
	public String getDosSeq() {
		return dosSeq;
	}
	public void setDosSeq(String dosSeq) {
		this.dosSeq = dosSeq;
	}
	
	
}
