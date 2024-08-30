package com.systex.jbranch.platform.reportserver;

import java.math.BigDecimal;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTVO;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportEngineType;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.factory.ReportFormat;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.util.DateUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.reportserver.service.vo.ExchangeRequestVO;

public class ReportOperator implements Runnable{
	
	public static String SUCCEEDED = "0";
	public static String RUNNING = "2";
	public static String FAILED = "1";
	
	private ReportEngineType reportEngineType = null;
	private ReportFormat reportFormat = null;
	private ReportData reportData = null;
	private String txnCode = null;
	private String reportId = null;
	private ReportIF report = null;
	private String errorMsg = "";
	private DataAccessManager dam = null;
	private ExchangeRequestVO rqVO = null;
	private TBSYSREPORTVO vo = null;
		
	public ReportOperator(ExchangeRequestVO rqVO){
		this.rqVO = rqVO;
		this.reportEngineType = rqVO.getReportEngineType();
		this.reportFormat = rqVO.getReportFormat();
		this.txnCode = rqVO.getTxnCode();
		this.reportId = rqVO.getReportId();
	}
	
	public void setDamVo(DataAccessManager dam, TBSYSREPORTVO vo){
		this.dam = dam;
		this.vo = vo;
		
        ReportData reportData = new ReportData();
        reportData.setFileName(rqVO.getFileName());
        reportData.setMerge(rqVO.isMerge());
        reportData.setPath(rqVO.getPath());
        reportData.addParameter(rqVO.getParameters());
        reportData.setRecords((Map) rqVO.getRecords());
        reportData.setOthers(rqVO.getOtherParam());

		this.reportData = reportData;
	}
	
	public void run() {
		try {

			AbstractReportGenerator generator = ReportFactory.getGenerator(null, reportEngineType, reportFormat);
			
//			if(otherParam instanceof ChartModel){
//				ChartModel cm = (ChartModel) otherParam;
//				BirtReportGenerator birtGen = (BirtReportGenerator) generator;
//				birtGen.setChartModel(txnCode, cm);
//			}
			reportData.setFileName(txnCode + "_" + reportId + "_" + vo.getGEN_ID() + "_" + DateUtil.format("yyyyMMddhhmmssSSS") + "." + generator.getReportExt());
			report=generator.generateReport(txnCode, reportId, reportData);

			vo.setSTATUS(SUCCEEDED);
			vo.setTEMP_PATH(report.getLocation());
			vo.setGEN_TIME(new BigDecimal(report.getSpeendingTime()));

		} catch (Throwable e) {
			errorMsg = StringUtil.getStackTraceAsString(e);
		}finally{
			try {
				if(errorMsg.getBytes().length > 1024){
					errorMsg = new String(errorMsg.getBytes(), 0, 1024);
				}
				vo.setMESSAGE(errorMsg);
				dam.update(vo);
			} catch (DAOException e) {
				errorMsg = StringUtil.getStackTraceAsString(e);
			}
		}
	}
	
	public String getErrorMsg(){
		return errorMsg;
	}
	
	public ReportIF getReport(){
		return this.report;
	}
}
