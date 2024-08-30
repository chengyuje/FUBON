package com.systex.jbranch.platform.common.report.generator;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;

public interface ReportGeneratorIF {

	public ReportIF generateReport(String reportID,ReportDataIF data) throws  JBranchException;
	public ReportIF generateReport(String reportID) throws JBranchException;
	
	public ReportIF generateReport(String txnCode,String reportID,ReportDataIF data) throws  JBranchException;
	public ReportIF generateReport(String txnCode,String reportID) throws  JBranchException;
	
	public ReportIF generateReport() throws  JBranchException;
	public ReportIF generateReport(ReportDataIF data) throws  JBranchException;
	
	public ReportIF mergeFiles(ReportIF[] dataArray) throws JBranchException;
}
