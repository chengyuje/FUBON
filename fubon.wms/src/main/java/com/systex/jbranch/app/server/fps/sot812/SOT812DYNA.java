package com.systex.jbranch.app.server.fps.sot812;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
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
 * 動態鎖利預約交易申請書
 */
@Component("sot812dyna")
@Scope("request")
public class SOT812DYNA extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT812DYNA.class);

	@Override
	public List<String> printReport() throws Exception {
		List<String> url_list = new ArrayList<String>();
		String url = null;
		String txnCode = "SOT812";
		String reportID = "R1";
		ReportIF report = null;

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf

		PRDFitInputVO inputVO = getInputVO();
		dam = this.getDataAccessManager();
		String tableName = "";
		if(inputVO.getTradeType() == 1) {
			tableName = "TBSOT_NF_PURCHASE_DYNA";
		} else if(inputVO.getTradeType() == 2) {
			tableName = "TBSOT_NF_REDEEM_DYNA";
		} else if(inputVO.getTradeType() == 3) {
			tableName = "TBSOT_NF_TRANSFER_DYNA";
		} else if(inputVO.getTradeType() == 4) {
			tableName = "TBSOT_NF_CHANGE_DYNA";
		} else if(inputVO.getTradeType() == 5) {
			tableName = "TBSOT_NF_RAISE_AMT_DYNA";
		}
		
		StringBuffer sql = new StringBuffer();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
		
		sql.append("select M.CUST_ID, M.CUST_NAME, M.TRADE_TYPE, D.TRUST_ACCT ");		
		sql.append(" from " + tableName + " D ");
		sql.append(" inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
		sql.append(" where D.TRADE_SEQ = :tradeSeq ");
		queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> rList = checkList(dam.exeQuery(queryCondition));
		
		data.addParameter("TRADE_DATE", "預約交易之指定交易日：民國____年____月____日（限為申請日期起次二十個營業日（含）內）");
		data.addParameter("TRADE_TYPE", ObjectUtils.toString(rList.get(0).get("TRADE_TYPE")));
		data.addParameter("CUST_NAME", ObjectUtils.toString(rList.get(0).get("CUST_NAME")));
		data.addParameter("CUST_ID", ObjectUtils.toString(rList.get(0).get("CUST_ID")));
		data.addParameter("TRUST_ACCT", ObjectUtils.toString(rList.get(0).get("TRUST_ACCT")));
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();	
		url_list.add(url);
		
		return url_list;
	}

	@SuppressWarnings("deprecation")
	private String getTradeDateStr(Timestamp tDate) {
		String getMonth = "";
		String getDate = "";
		
		getMonth = (tDate.getMonth() + 1) + "";
		getDate = tDate.getDate() + "";
		if (getMonth.length() < 2) getMonth = "0" + getMonth;
		if (getDate.length() < 2) getDate = "0" + getDate;

		return "民國  "+ (tDate.getYear()-11) + "  年  " + getMonth + "  月  " + getDate + "  日";
	}
}