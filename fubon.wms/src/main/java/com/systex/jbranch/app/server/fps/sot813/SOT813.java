package com.systex.jbranch.app.server.fps.sot813;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;

/**
 * MENU
 * 
 * @author Lily
 * @date 2016/11/24
 * @spec null
 */
@Component("sot813")
@Scope("request")
public class SOT813 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT813.class);
	
	@Override
	public List<String> printReport() throws JBranchException {
		List<String> url_list = new ArrayList<String>();
		String url = null;
		String txnCode = "SOT813";
		String reportID = "R1";
		ReportIF report = null;

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf

		PRDFitInputVO inputVO = getInputVO();
		
		try {
			if (inputVO.getCaseCode() == 1) { //case1下單
				dam = this.getDataAccessManager();
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("select CUST_ID ");
				sql.append("from TBSOT_TRADE_MAIN ");
				sql.append("where TRADE_SEQ = :tradeSeq ");
				queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
	
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				//System.out.println(list.get(0).get("TRADE_TYPE"));
	
				if (list.get(0).get("CUST_ID") != null && StringUtils.isNotBlank(list.get(0).get("CUST_ID").toString())) {
					data.addParameter("CUST_ID", list.get(0).get("CUST_ID").toString());
				}
			} else if (inputVO.getCaseCode() == 2) { //case2 適配 
				data.addParameter("CUST_ID", inputVO.getCustId());
			}	
			report = gen.generateReport(txnCode, reportID, data);
			url = report.getLocation();
			url_list.add(url);
//			notifyClientToDownloadFile(url, "SOT813.pdf");
//			notifyClientViewDoc(url, "pdf");
			return url_list;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

}