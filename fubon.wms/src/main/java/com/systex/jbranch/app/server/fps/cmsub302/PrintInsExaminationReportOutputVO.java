package com.systex.jbranch.app.server.fps.cmsub302;

import java.util.List;

public class PrintInsExaminationReportOutputVO {

	private Boolean showLog;
	private List lstLogTable;
	private List lstFamily;
	
	public List getLstFamily() {
		return lstFamily;
	}
	public void setLstFamily(List lstFamily) {
		this.lstFamily = lstFamily;
	}
	public Boolean getShowLog() {
		return showLog;
	}
	public void setShowLog(Boolean showLog) {
		this.showLog = showLog;
	}
	public List getLstLogTable() {
		return lstLogTable;
	}
	public void setLstLogTable(List lstLogTable) {
		this.lstLogTable = lstLogTable;
	}
}
