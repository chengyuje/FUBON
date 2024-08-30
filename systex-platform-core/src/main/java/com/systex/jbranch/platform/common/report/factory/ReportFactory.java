package com.systex.jbranch.platform.common.report.factory;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.dispacther.Dispacther;
import com.systex.jbranch.platform.common.report.dispacther.DispactherFactory;
import com.systex.jbranch.platform.common.report.dispacther.DispactherType;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.util.PlatformContext;

public class ReportFactory {

	private static final String REPORT_DISPACTHER = "reportDispacther";

	/**
	 * 使用預設種類產生報表。
	 * 
	 * @return AbstractReportGenerator
	 * @throws JBranchException
	 */
	public static AbstractReportGenerator getGenerator() throws JBranchException{
		
		return getGenerator(null, null, null);
	}
	
	/**
	 * 依指定的報表種類，產生報表。
	 * 
	 * @param reportEngineBean 報表種類
	 * @return AbstractReportGenerator
	 * @throws JBranchException
	 */
	public static AbstractReportGenerator getGenerator(ReportEngineType reportEngineBean) throws JBranchException{
		
		return getGenerator(null, reportEngineBean, null);
	}
	
	/**
	 * 依指定的報表種類、報表格式產生報表。
	 * 
	 * @param reportEngineBean 報表種類
	 * @param reportGeneratorBean 報表格式
	 * @return
	 * @throws JBranchException
	 */
	public static AbstractReportGenerator getGenerator(ReportEngineType reportEngineBean, ReportFormat reportGeneratorBean) throws JBranchException{
		
		return getGenerator(null, reportEngineBean, reportGeneratorBean);
	}
	
	/**
	 * 依指定的產生位置、報表種類、報表格式產生報表。
	 * 
	 * @param reportDispacther 報表產生位置
	 * @param reportEngineBean 報表種類
	 * @param reportGeneratorBean 報表格式
	 * @return
	 * @throws JBranchException
	 */
	public static AbstractReportGenerator getGenerator(DispactherType reportDispacther, ReportEngineType reportEngineBean, ReportFormat reportGeneratorBean) throws JBranchException{
		DispactherFactory factory = (DispactherFactory) PlatformContext.getBean(REPORT_DISPACTHER);
		Dispacther dispacther = null;
		if(reportDispacther == null){
			dispacther = factory.getDispatcher();
		}else{
			dispacther = factory.getDispatcher(reportDispacther);
		}
		
		dispacther.setReportEngineType(reportEngineBean);
		dispacther.setReportFormat(reportGeneratorBean);

		return dispacther.getGenerator();
	}
}
