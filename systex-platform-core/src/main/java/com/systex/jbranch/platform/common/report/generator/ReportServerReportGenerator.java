package com.systex.jbranch.platform.common.report.generator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.activation.DataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.FlexReport;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportEngineType;
import com.systex.jbranch.platform.common.report.factory.ReportFormat;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.reportserver.service.ReportService;
import com.systex.jbranch.platform.reportserver.service.vo.ExchangeRequestVO;
import com.systex.jbranch.platform.reportserver.service.vo.ExchangeResponseVO;

public class ReportServerReportGenerator extends AbstractReportGenerator {


	private Logger logger = LoggerFactory.getLogger(ReportServerReportGenerator.class);
	private boolean async = false;
	private ReportEngineType reportEngineType;
	private ReportFormat reportFormat;
	
	public ReportIF generateReport(String txnCode, String reportID,
			ReportDataIF data) throws JBranchException {
		ReportService service = (ReportService) PlatformContext.getBean("reportServiceClient");
		ExchangeRequestVO vo = new ExchangeRequestVO();

		ReportData rd = (ReportData) data;
		vo.setTxnCode(txnCode);
		vo.setReportId(reportID);
		vo.setAsync(async);
		vo.setReportEngineType(reportEngineType);
		vo.setReportFormat(reportFormat);
		vo.setFileName(rd.getFileName());
		vo.setMerge(rd.isMerge());
		vo.setParameters((HashMap) rd.getParameters());
		vo.setPath(rd.getPath());
		vo.setRecords((HashMap) rd.getRecordListAll());
		vo.setOtherParam(rd.getOthers());
//		Object otherParam = getOtherParam(txnCode, reportID, data);
//		vo.setOtherParam(otherParam);
		logger.debug("send report server...");
		logger.debug("ReportGenerator async..." + async);
		ExchangeResponseVO rsVO = service.generate(vo);
		logger.debug("end,location="+rsVO.getLocation());

		return getReportIF(rsVO);
	}

	@Override
	public String getReportExt() {

		return null;
	}

	private ReportIF getReportIF(ExchangeResponseVO rsVO) throws JBranchException{
		ReportIF fr = new FlexReport();
		ObjectUtil.copyProperties(fr, rsVO);
		ReportIF reportIF = fr;

		if(rsVO.getReportData() != null){
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				byte[] buffer = new byte[512];
				DataHandler reportData = rsVO.getReportData();
				if(reportData != null){
					is = reportData.getInputStream();
					fos = new FileOutputStream(DataManager.getRealPath() + reportIF.getLocation());
					int length = 0;
					while((length = is.read(buffer)) != -1 ){
						fos.write(buffer, 0, length);
					}
					fos.flush();
				}
			} catch (Exception e) {
				throw new JBranchException(e.getMessage(), e);
			}finally{
				if(is != null){
					try {
						is.close();
					} catch (IOException e) {
						//ignore
					}
				}
				if(fos != null){
					try {
						fos.close();
					} catch (IOException e) {
						//ignore
					}
				}
			}
		}
		return reportIF;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

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
