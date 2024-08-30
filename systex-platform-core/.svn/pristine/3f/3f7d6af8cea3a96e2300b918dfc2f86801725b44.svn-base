package com.systex.jbranch.platform.common.report.generator.linemode;


public interface StoreIF {
	String saveReportMaster(String txnCode, String reportName,String devId, int totalPageNum, String rePrint) throws Exception;
	String saveReportMaster(String txnCode, String reportName, String devId, int totalPageNum, String rePrint, Object others) throws Exception;
	void updateReportMasterToFinshed(String reportId) throws Exception;
	@Deprecated
	void saveReportDetail(String json,int rptSeqNo, int pageNumber) throws Exception;
	void saveReportDetail(String json,String rptSeqNo, int pageNumber) throws Exception;
}
