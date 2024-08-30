package com.systex.jbranch.platform.common.report.engine;

import org.eclipse.birt.report.engine.api.IReportEngine;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface ReportServiceIF {
	
	public void initial()throws JBranchException;
	public void release();

}
