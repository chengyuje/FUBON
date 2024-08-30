package com.systex.jbranch.platform.common.report.dispacther;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportEngine;
import com.systex.jbranch.platform.common.report.factory.ReportEngineFactory;
import com.systex.jbranch.platform.common.report.factory.ReportEngineType;
import com.systex.jbranch.platform.common.report.factory.ReportFormat;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.util.PlatformContext;

public class LocalDispacther extends AbstractDispacter {

	private static final String REPORT_ENGINE_FACTORY = "reportEngineFactory";

	public AbstractReportGenerator getGenerator() throws JBranchException {
		
		ReportEngineFactory engineFactory = (ReportEngineFactory) PlatformContext.getBean(REPORT_ENGINE_FACTORY);
		ReportEngineType reportEngineType = this.getReportEngineType();
		ReportFormat reportFormat = this.getReportFormat();
		ReportEngine engine = null;
		AbstractReportGenerator generator;
		if(reportEngineType == null){
			engine = engineFactory.getEngine();
		}else{
			engine = engineFactory.getEngine(reportEngineType);
		}
		if(reportFormat == null){
			generator = engine.getGenerator();
		}else{
			generator = engine.getGenerator(reportFormat);
		}
		return generator;
	}
}
