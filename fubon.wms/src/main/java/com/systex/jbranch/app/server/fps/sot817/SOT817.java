package com.systex.jbranch.app.server.fps.sot817;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Allen
 * @date 2018/09/17
 * @spec null
 */
@Component("sot817")
@Scope("request")
public class SOT817 extends SotPdf {
	private Logger logger = LoggerFactory.getLogger(SOT817.class);

	@Override
	public List<String> printReport() throws JBranchException {
		List<String> url_list = new ArrayList<String>();
		String url = null;
		String txnCode = "SOT817";
		String reportID = "R1";
		ReportIF report = null;

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();

		try {

			data.addRecordList("Script Mult Data Set", totalList);

			report = gen.generateReport(txnCode, reportID, data);
			url = report.getLocation();
			url_list.add(url);

			return url_list;

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
}