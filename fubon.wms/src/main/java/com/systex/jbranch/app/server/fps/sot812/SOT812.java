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
 * 
 * @author Lily
 * @date 2016/11/24
 * @spec null
 */
@Component("sot812")
@Scope("request")
public class SOT812 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT812.class);

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
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String tradeType = "";
		String custName = "";
		String tableName = "";
		String acctName="";
//		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select CUST_NAME, TRADE_TYPE ");
			sql.append("from TBSOT_TRADE_MAIN ");
			sql.append("where TRADE_SEQ = :tradeSeq ");
			queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());

			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			tradeType = list.get(0).get("TRADE_TYPE").toString();
			custName = list.get(0).get("CUST_NAME").toString();
			
			if (tradeType.equals("1") || tradeType.equals("5")){
				tableName = "TBSOT_NF_PURCHASE_D";
			}else if (tradeType.equals("2")){
				tableName = "TBSOT_NF_REDEEM_D";
			}else if (tradeType.equals("3")){
				tableName = "TBSOT_NF_TRANSFER_D";
			}else if(tradeType.equals("4")){
				tableName = "TBSOT_NF_CHANGE_D";
			}
			
			if(tradeType.equals("1") || tradeType.equals("2") || tradeType.equals("5")){
				acctName = "TRUST_ACCT";
			}else if(tradeType.equals("3")){
				acctName = "OUT_TRUST_ACCT";
			}else if(tradeType.equals("4")){
				acctName = "B_TRUST_ACCT";
			}
			
			sql = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
			
			sql.append("select M.CUST_ID,D. "+acctName+" as TRUST_ACCT, M.TRUST_TRADE_TYPE, D.CONTRACT_ID  ");		
			sql.append("from "+ tableName+" D ");
			sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
			sql.append("where D.TRADE_SEQ = :tradeSeq ");
			if (!(tradeType.equals("4"))){
				sql.append("and D.BATCH_SEQ = :batch_SEQ");
				queryCondition.setObject("batch_SEQ", inputVO.getBatchSeq());
			}
			queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());

			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> listRecordList = checkList(dam.exeQuery(queryCondition));
			// 組合所有資料
			for (Map<String, Object> map2 : listRecordList) {
				totalList.add(map2);
			}

			if (totalList.size() > 0) {
				if(StringUtils.equals(totalList.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){
					logger.info("走進SOT812_R2邏輯");
					logger.info("CONTRACT_ID = " + totalList.get(0).get("CONTRACT_ID").toString());
					reportID = "R2";
					data.addParameter("CONTRACT_ID", totalList.get(0).get("CONTRACT_ID").toString());
				} else {
					if(tradeType.equals("1")) { //單筆申購
						sql = new StringBuffer();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sql.append("SELECT D.TRADE_DATE, NVL(F.OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN ");
						sql.append(" FROM TBSOT_NF_PURCHASE_D D ");
						sql.append(" INNER JOIN TBPRD_FUND F ON F.PRD_ID = D.PROD_ID ");
						sql.append(" WHERE D.TRADE_SEQ = :tradeSeq AND D.BATCH_SEQ = :batch_SEQ ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("batch_SEQ", inputVO.getBatchSeq());
						queryCondition.setQueryString(sql.toString());
						List<Map<String, Object>> lista = dam.exeQuery(queryCondition);
						
						if(CollectionUtils.isNotEmpty(lista) && StringUtils.equals("Y", ObjectUtils.toString(lista.get(0).get("OVS_PRIVATE_YN")))) {
							//境外私募基金取得預約交易日
							data.addParameter("TRADE_DATE", "預約交易之指定交易日：" + getTradeDateStr((Timestamp)lista.get(0).get("TRADE_DATE")) + "（限為申請日期起次二十個營業日（含）內）");
						} else {
							data.addParameter("TRADE_DATE", "預約交易之指定交易日：民國____年____月____日（限為申請日期起次二十個營業日（含）內）");
						}
					} else if(tradeType.equals("2")) { //贖回
						sql = new StringBuffer();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sql.append("SELECT D.TRADE_DATE, NVL(F.OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN, O.DEADLINE_DATE ");
						sql.append(" FROM TBSOT_NF_REDEEM_D D ");
						sql.append(" INNER JOIN TBPRD_FUND F ON F.PRD_ID = D.RDM_PROD_ID ");
						sql.append(" LEFT JOIN TBPRD_FUND_OVS_PRIVATE O ON O.SEQ_NO = D.OVS_PRIVATE_SEQ ");
						sql.append(" WHERE D.TRADE_SEQ = :tradeSeq AND D.BATCH_SEQ = :batch_SEQ ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("batch_SEQ", inputVO.getBatchSeq());
						queryCondition.setQueryString(sql.toString());
						List<Map<String, Object>> listb = dam.exeQuery(queryCondition);
						
						if(CollectionUtils.isNotEmpty(listb) && StringUtils.equals("Y", ObjectUtils.toString(listb.get(0).get("OVS_PRIVATE_YN")))) {
							//境外私募基金贖回
							reportID = "R3";
							//取得預約交易日
							data.addParameter("TRADE_DATE", "預約贖回之指定交易日：" + getTradeDateStr((Timestamp)listb.get(0).get("TRADE_DATE")) + "（限為申請日期起次九十個營業日（含）內）");
							//交易截止日
							if(listb.get(0).get("DEADLINE_DATE") != null) {
								data.addParameter("DEADLINE_DATE", getTradeDateStr((Timestamp)listb.get(0).get("DEADLINE_DATE")));
							} else {
								data.addParameter("DEADLINE_DATE", "民國____年____月____日");
							}
						} else {
							data.addParameter("TRADE_DATE", "預約交易之指定交易日：民國____年____月____日（限為申請日期起次二十個營業日（含）內）");
						}
					} else { //其他(小額申購、轉換、事件變更)
						data.addParameter("TRADE_DATE", "預約交易之指定交易日：民國____年____月____日（限為申請日期起次二十個營業日（含）內）");
					}
				}
				
				data.addParameter("TRADE_TYPE", tradeType);
				data.addParameter("CUST_NAME", custName);
				if (totalList.get(0).get("CUST_ID") != null && StringUtils.isNotBlank(totalList.get(0).get("CUST_ID").toString())) {
					data.addParameter("CUST_ID", totalList.get(0).get("CUST_ID").toString());
				}
				if (totalList.get(0).get("TRUST_ACCT") != null && StringUtils.isNotBlank(totalList.get(0).get("TRUST_ACCT").toString())) {
					data.addParameter("TRUST_ACCT", totalList.get(0).get("TRUST_ACCT").toString());
				}
				report = gen.generateReport(txnCode, reportID, data);
				url = report.getLocation();	
				url_list.add(url);
//				notifyClientToDownloadFile(url, "SOT812.pdf");
//				notifyClientViewDoc(url, "pdf");
			}
			
			return url_list;
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}

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