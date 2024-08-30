package com.systex.jbranch.platform.common.report.dispacther;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportEngineType;
import com.systex.jbranch.platform.common.report.factory.ReportFormat;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;


public abstract class AbstractDispacter implements Dispacther{

	protected ReportEngineType reportEngineType;
	protected ReportFormat reportFormat;
	
	public abstract AbstractReportGenerator getGenerator() throws JBranchException;
	
	public ReportEngineType getReportEngineType() {
		
		return reportEngineType;
	}
	
	public void setReportEngineType(ReportEngineType reportEngineType) {
		this.reportEngineType = reportEngineType;
	}
	
	public ReportFormat getReportFormat() {
		return reportFormat;
	}
	
	public void setReportFormat(ReportFormat reportFormat) {
		this.reportFormat = reportFormat;
	}
	
}
