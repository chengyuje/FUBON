package com.systex.jbranch.app.server.fps.sot819;

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
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;

/**
 * #1695
 * 貸款風險預告書
 * 
 * @author Sam Tu
 * @date 2023/09/07
 * @spec 

 */
@Component("sot819")
@Scope("request")
public class SOT819 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT819.class);

	@Override
	public List<String> printReport() throws JBranchException {
		List<String> url_list = new ArrayList<String>();
		String url = null;
		String txnCode = "SOT819";
		String reportID = "R1";
		ReportIF report = null;

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();

		PRDFitInputVO inputVO = getInputVO();
		
		try {
			if(StringUtils.isBlank(inputVO.getTradeSeq())) {
				reportID = "R1";
			} else if (StringUtils.equals(getTrustTS(inputVO), "M")) {
				reportID = "R2";
			} else {
				reportID = "R1";
			}
			
			//第一份
			data.addParameter("CUST_ID", inputVO.getCustId());
			report = gen.generateReport(txnCode, reportID, data);
			url = report.getLocation();
			url_list.add(url);
			
			//第二份
			data.addParameter("CUST_ID", inputVO.getCustId());
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

	private String getTrustTS(PRDFitInputVO inputVO) throws DAOException, JBranchException {
		StringBuffer sql = new StringBuffer();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append(" select TRUST_TRADE_TYPE from TBSOT_TRADE_MAIN WHERE 1=1 ");
		sql.append(" AND TRADE_SEQ = :TRADE_SEQ ");
		queryCondition.setObject("TRADE_SEQ", inputVO.getTradeSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> data_list = dam.exeQuery(queryCondition);
		
		String result = "S";
		try {
			result = (String) data_list.get(0).get("TRUST_TRADE_TYPE");
		} catch (Exception e) {
	
		}
		
		return result;
	}
}