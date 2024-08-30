package com.systex.jbranch.platform.common.report.dispacther;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportEngineType;
import com.systex.jbranch.platform.common.report.factory.ReportFormat;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;

public interface Dispacther {

	public AbstractReportGenerator getGenerator() throws JBranchException;
	
	public ReportEngineType getReportEngineType();

	public void setReportEngineType(ReportEngineType reportEngine);

	public ReportFormat getReportFormat();

	public void setReportFormat(ReportFormat reportGenerator);
}
