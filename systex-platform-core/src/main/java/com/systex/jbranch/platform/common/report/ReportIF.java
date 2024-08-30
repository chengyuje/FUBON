package com.systex.jbranch.platform.common.report;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface ReportIF {
	/**
     * @return 產出報表的路徑及檔名
     */  
	public String getLocation();
	/**
     * @return 產出報表的檔名
     */  
	public String getReportName();
	/**
     * @return 產出報表的建立時間
     */  
	public String getEstablishTime();
	/**
     * @return 產出報表所花費的時間
     */  
	public double getSpeendingTime();
	
	public String getReportTitle();
}
