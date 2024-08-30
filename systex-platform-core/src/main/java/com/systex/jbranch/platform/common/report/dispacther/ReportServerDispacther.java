package com.systex.jbranch.platform.common.report.dispacther;

import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.report.generator.ReportServerReportGenerator;

public class ReportServerDispacther extends AbstractDispacter {

	public AbstractReportGenerator getGenerator() {
		ReportServerReportGenerator generator = new ReportServerReportGenerator();
		generator.setReportEngineType(this.getReportEngineType());
		generator.setReportFormat(this.getReportFormat());
		return generator;
	}

}
