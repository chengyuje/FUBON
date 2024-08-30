package com.systex.jbranch.app.server.fps.iot400;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT400OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> fileList;
	private int SEQ;
	private Boolean showMsg;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getFileList() {
		return fileList;
	}

	public void setFileList(List<Map<String, Object>> fileList) {
		this.fileList = fileList;
	}

	public int getSEQ() {
		return SEQ;
	}

	public void setSEQ(int sEQ) {
		SEQ = sEQ;
	}

	public Boolean getShowMsg() {
		return showMsg;
	}

	public void setShowMsg(Boolean showMsg) {
		this.showMsg = showMsg;
	}
	
}
