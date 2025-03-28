package com.systex.jbranch.app.server.fps.sot814;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

/**
 * 動態鎖利基金通路報酬表單列印
 */
@Component("sot814dyna")
@Scope("request")
public class SOT814DYNA extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT814DYNA.class);
	
	@Override
	public List<String> printReport() throws JBranchException {
		PRDFitInputVO inputVO = getInputVO();
		List<String> urlList = new ArrayList<String>();
		if (inputVO.getCaseCode() == 2) { //case2適配
			//母基金通路報酬
			urlList.addAll(genReport_FIT(inputVO, "M", inputVO.getPrdId(), false));
			//子基金通路報酬1
			urlList.addAll(genReport_FIT(inputVO, "C", inputVO.getPrdIdC1(), StringUtils.isBlank(inputVO.getPrdIdC2())));
			//子基金通路報酬2
			if(StringUtils.isNotBlank(inputVO.getPrdIdC2())) {
				urlList.addAll(genReport_FIT(inputVO, "C", inputVO.getPrdIdC2(), StringUtils.isBlank(inputVO.getPrdIdC3())));
			}
			//子基金通路報酬3
			if(StringUtils.isNotBlank(inputVO.getPrdIdC3())) {
				urlList.addAll(genReport_FIT(inputVO, "C", inputVO.getPrdIdC3(), true));
			}
		} else { //下單
			if(inputVO.getTradeType() == 1 || inputVO.getTradeType() == 5 || inputVO.getTradeType() == 3) { 
				//母基金通路報酬
				//單筆申購或母基金加碼或轉換
				urlList.addAll(genReportM(inputVO));
			}
			//子基金通路報酬
			//單筆申購 & 事件變更有新增子基金
			//母基金加碼沒有子基金，不需子基金通路報酬
			if(inputVO.getTradeType() == 1 || inputVO.getTradeType() == 4) {
				urlList.addAll(genReportC(inputVO));
			}
		}
		
		return urlList;
	}
	
	/*** 
	 * 母基金通路報酬
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<String> genReportM(PRDFitInputVO inputVO) throws DAOException, JBranchException {
		String txnCode = "SOT814";
		String reportID = "R3"; //母基金通路報酬
		List<String> urlList = new ArrayList<String>();
		ReportIF report = null;
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		String tableName = (inputVO.getTradeType() == 3 ? "TBSOT_NF_TRANSFER_DYNA" : (inputVO.getTradeType() == 5 ? "TBSOT_NF_RAISE_AMT_DYNA" : "TBSOT_NF_PURCHASE_DYNA")); //表格名稱
		
		String prodIdPrefix = "";
		if(inputVO.getTradeType() == 3) {
			prodIdPrefix = getTransferPrefix(inputVO.getTradeSeq());
		}
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//case1下單:申購母基金
		sql.append("SELECT  M.CUST_ID, M.CUST_NAME, F.*, N.WARNING, N.IS_BACKEND, ");
		sql.append("	D." + prodIdPrefix + "PROD_ID AS PROD_ID, D." + prodIdPrefix + "PROD_NAME AS PROD_NAME, D." + prodIdPrefix + "PROD_CURR AS PROD_CURR, ");
		sql.append(" 		CASE WHEN PARAM.PARAM_NAME IS NULL THEN N.TRUST_COM  ");
		sql.append("			 WHEN SUBSTR(TRIM(N.TRUST_COM),-2,2) IN ('投信','投顧') ");
		sql.append("			 THEN SUBSTR(TRIM(N.TRUST_COM), 1, LENGTH(TRIM(N.TRUST_COM))-2) || PARAM.PARAM_NAME ");
		sql.append("		ELSE TRIM(N.TRUST_COM) || PARAM.PARAM_NAME END AS TRUST_COM ,");
		sql.append("		CASE WHEN PARAM.PARAM_NAME = '投信' THEN 'INV' ");
		sql.append("		WHEN PARAM.PARAM_NAME = '總代理人' THEN 'AGN' ");
		sql.append(" 		WHEN PARAM.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE ");
		sql.append(" FROM " + tableName + " D ");
		sql.append(" INNER JOIN TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
		sql.append(" LEFT JOIN TBPRD_FUND_BONUSINFO F on F.PRD_ID = D." + prodIdPrefix + "PROD_ID ");
		sql.append(" LEFT JOIN TBPRD_FUND N on N.PRD_ID = D." + prodIdPrefix + "PROD_ID ");
		sql.append(" LEFT JOIN TBSYSPARAMETER PARAM on SUBSTR(D." + prodIdPrefix + "PROD_ID,0,2) = PARAM.PARAM_CODE AND PARAM.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
		sql.append(" WHERE D.TRADE_SEQ = :tradeSeq ");
		queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> listRecordList = dam.exeQuery(queryCondition);
		
		Map<String, Object> record = listRecordList.get(0);
		if (record.get("FEE") == null) record.put("FEE", 0);
		if (record.get("FEE1") == null) record.put("FEE1", 0);
		if (record.get("FEE2") == null) record.put("FEE2", 0);
		if (record.get("FEE3") == null) record.put("FEE3", 0);
		if (record.get("M_FEE") == null) record.put("M_FEE", 0);
		if (record.get("DISTRIBUTION_FEE") == null) record.put("DISTRIBUTION_FEE", 0);
		if (record.get("M_FEE_RATE") == null) record.put("M_FEE_RATE", 0);
		if (record.get("OTHER_REWARD_INV") == null) record.put("OTHER_REWARD_INV", 0);
		if (record.get("OTHER_REWARD_AGN") == null) record.put("OTHER_REWARD_AGN", 0);
		if (record.get("OTHER_REWARD_FRN") == null) record.put("OTHER_REWARD_FRN", 0);
		if (record.get("IS_BACKEND") == null) record.put("IS_BACKEND", "N");
		if (record.get("REWARD_DF") == null) record.put("REWARD_DF", 0);
		if (record.get("TRAIN") == null) record.put("TRAIN", 0);
		record.put("TRUST_COM", record.get("TRUST_COM") == null ? " " : record.get("TRUST_COM").toString().trim());
		record.put("REWARD", record.get("REWARD") == null ? 0 : getString(record.get("REWARD")));
		if (StringUtils.isNotBlank(getString(record.get("WARNING")))) {
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> map = xmlInfo.doGetVariable("PRD.FUND_C_ALERT", FormatHelper.FORMAT_3);
			String WARNING = map.get(getString(record.get("WARNING")));
			record.put("WARNING", WARNING);
		}
		
		data.addRecordList("Script Mult Data Set", listRecordList);
		data.addParameter("CUST_ID", (String) listRecordList.get(0).get("CUST_ID"));
		data.addParameter("CUST_NAME", (String) listRecordList.get(0).get("CUST_NAME"));
		//母基金加碼，沒有子基金，需顯示簽名欄
		data.addParameter("SHOW_SIGNATURE", (inputVO.getTradeType() == 1 ? "N" : "Y"));
		
		//費用率&報酬率
		StringBuffer sql_fee = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sql_fee.append("select A.*, (C.PRD_ID || ' ' || C.FUND_CNAME_A || '-' || C.CURRENCY_STD_ID) as FUND_CNAME_A, ");
		sql_fee.append("		to_char(A.CREATETIME, 'yyyy/mm/dd') as FEE_DATA_DATE ");
		sql_fee.append(" FROM TBPRD_FUND_BONUSINFO_RATES A ");
		sql_fee.append(" LEFT JOIN TBPRD_FUND C on C.PRD_ID = A.PRD_ID ");
		sql_fee.append(" LEFT JOIN TBPRD_NFS062_SG D on D.S6201 = A.PRD_ID ");
		sql_fee.append(" WHERE A.PRD_ID IN (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 in ");
		sql_fee.append("	 (SELECT " + prodIdPrefix + "PROD_ID FROM " + tableName + " WHERE TRADE_SEQ = :tradeSeq))) ");
		sql_fee.append(" AND C.IS_SALE = 1 ");
		sql_fee.append(" ORDER BY D.S6202, A.PRD_ID ");
		queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
		queryCondition.setQueryString(sql_fee.toString());
		List<Map<String, Object>> feeList = dam.exeQuery(queryCondition);
		
		//取得西元年
		Calendar cal = Calendar.getInstance();
		int calYear = cal.get(Calendar.YEAR);
		int currentYear = calYear;
		
		sql_fee = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		sql_fee.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FUND_FEE_YEAR' AND PARAM_CODE = '1' ");
		queryCondition.setQueryString(sql_fee.toString());
		List<Map<String, Object>> yearList = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(yearList)) {
			String paramName = ObjectUtils.toString(yearList.get(0).get("PARAM_NAME"));
			try {
				currentYear = StringUtils.isNotBlank(paramName) ? Integer.parseInt(paramName) + 1 : calYear;
			} catch (Exception e) {
				currentYear = calYear;
			}
		}
		
		data.addParameter("CURRENT_YEAR", currentYear);
		
		if(CollectionUtils.isNotEmpty(feeList)) {
			data.addRecordList("FEELIST", feeList);
			//報表日期
			data.addParameter("FEE_DATA_DATE", "資料日期：" + (String) feeList.get(0).get("FEE_DATA_DATE"));
		}
		
		//generateReport
		report = gen.generateReport(txnCode, reportID, data);
		urlList.add(report.getLocation());
		
		return urlList;
	}
	
	/***
	 * 子基金通路報酬
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<String> genReportC(PRDFitInputVO inputVO) throws DAOException, JBranchException {
		String txnCode = "SOT814";
		String reportID = "R4"; //子基金通路報酬
		List<String> urlList = new ArrayList<String>();
		ReportIF report = null;
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		int maxCount = (inputVO.getTradeType() == 1 ? 3 : 5); //單筆申購最多3筆字基金 & 事件變更有新增子基金
		String prefixC = (inputVO.getTradeType() == 1 ? "" : "F_"); //子基金前綴字：單筆申購沒有 & 事件變更新增子基金為F_
		String tableName = (inputVO.getTradeType() == 1 ? "TBSOT_NF_PURCHASE_DYNA" : "TBSOT_NF_CHANGE_DYNA"); //表格名稱
		
		for(int i = 1; i <= maxCount; i++) {
			String cIdx = "_C" + String.valueOf(i); //子基金代號 _C1 ~ _CmaxCount
			String cNextIdx = cIdx;
			if(i < maxCount) cNextIdx = "_C" + String.valueOf(i+1);
		
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			//case1下單:申購子基金	
			sql.append("SELECT  M.CUST_ID, M.CUST_NAME, D." + prefixC + "PROD_ID" + cIdx + " AS PROD_ID, D." + prefixC + "PROD_NAME" + cIdx + " AS PROD_NAME, ");
			sql.append(" D.PROD_CURR, F.*, N.WARNING, N.IS_BACKEND, D." + prefixC + "PROD_ID" + cNextIdx + " AS NEXT_PROD_ID, ");
			sql.append(" 		CASE WHEN PARAM.PARAM_NAME IS NULL THEN N.TRUST_COM  ");
			sql.append("			 WHEN SUBSTR(TRIM(N.TRUST_COM),-2,2) IN ('投信','投顧') ");
			sql.append("			 THEN SUBSTR(TRIM(N.TRUST_COM), 1, LENGTH(TRIM(N.TRUST_COM))-2) || PARAM.PARAM_NAME ");
			sql.append("		ELSE TRIM(N.TRUST_COM) || PARAM.PARAM_NAME END AS TRUST_COM ,");
			sql.append("		CASE WHEN PARAM.PARAM_NAME = '投信' THEN 'INV' ");
			sql.append("		WHEN PARAM.PARAM_NAME = '總代理人' THEN 'AGN' ");
			sql.append(" 		WHEN PARAM.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE ");
			sql.append(" FROM " + tableName + " D ");
			sql.append(" INNER JOIN TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
			sql.append(" LEFT JOIN TBPRD_FUND_BONUSINFO F on F.PRD_ID = D." + prefixC + "PROD_ID" + cIdx + " ");
			sql.append(" LEFT JOIN TBPRD_FUND N on N.PRD_ID = D." + prefixC + "PROD_ID" + cIdx + " ");
			sql.append(" LEFT JOIN TBSYSPARAMETER PARAM on SUBSTR(D." + prefixC + "PROD_ID" + cIdx + ",0,2) = PARAM.PARAM_CODE AND PARAM.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
			sql.append(" WHERE D.TRADE_SEQ = :tradeSeq ");
			queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> listRecordList = dam.exeQuery(queryCondition);
			
			if(CollectionUtils.isNotEmpty(listRecordList) && StringUtils.isNotBlank(ObjectUtils.toString(listRecordList.get(0).get("PROD_ID")))) {
				Map<String, Object> record = listRecordList.get(0);
				if (record.get("FEE") == null) record.put("FEE", 0);
				if (record.get("FEE1") == null) record.put("FEE1", 0);
				if (record.get("FEE2") == null) record.put("FEE2", 0);
				if (record.get("FEE3") == null) record.put("FEE3", 0);
				if (record.get("M_FEE") == null) record.put("M_FEE", 0);
				if (record.get("DISTRIBUTION_FEE") == null) record.put("DISTRIBUTION_FEE", 0);
				if (record.get("M_FEE_RATE") == null) record.put("M_FEE_RATE", 0);
				if (record.get("OTHER_REWARD_INV") == null) record.put("OTHER_REWARD_INV", 0);
				if (record.get("OTHER_REWARD_AGN") == null) record.put("OTHER_REWARD_AGN", 0);
				if (record.get("OTHER_REWARD_FRN") == null) record.put("OTHER_REWARD_FRN", 0);
				if (record.get("IS_BACKEND") == null) record.put("IS_BACKEND", "N");
				if (record.get("REWARD_DF") == null) record.put("REWARD_DF", 0);
				if (record.get("TRAIN") == null) record.put("TRAIN", 0);
				record.put("REWARD", record.get("REWARD") == null ? 0 : getString(record.get("REWARD")));
				record.put("TRUST_COM", record.get("TRUST_COM") == null ? " " : record.get("TRUST_COM").toString().trim());
				if (StringUtils.isNotBlank(getString(record.get("WARNING")))) {
					XmlInfo xmlInfo = new XmlInfo();
					Map<String, String> map = xmlInfo.doGetVariable("PRD.FUND_C_ALERT", FormatHelper.FORMAT_3);
					String WARNING = map.get(getString(record.get("WARNING")));
					record.put("WARNING", WARNING);
				}
				
				data.addRecordList("Script Mult Data Set", listRecordList);
				data.addParameter("CUST_ID", (String) listRecordList.get(0).get("CUST_ID"));
				data.addParameter("CUST_NAME", (String) listRecordList.get(0).get("CUST_NAME"));
				//最後一個子基金才顯示簽名欄
				data.addParameter("SHOW_SIGNATURE", (i == maxCount || StringUtils.isBlank(ObjectUtils.toString(listRecordList.get(0).get("NEXT_PROD_ID")))) ? "Y" : "N");
				
				//費用率&報酬率
				StringBuffer sql_fee = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
				sql_fee.append("select A.*, (C.PRD_ID || ' ' || C.FUND_CNAME_A || '-' || C.CURRENCY_STD_ID) as FUND_CNAME_A, ");
				sql_fee.append("		to_char(A.CREATETIME, 'yyyy/mm/dd') as FEE_DATA_DATE ");
				sql_fee.append(" FROM TBPRD_FUND_BONUSINFO_RATES A ");
				sql_fee.append(" LEFT JOIN TBPRD_FUND C on C.PRD_ID = A.PRD_ID ");
				sql_fee.append(" LEFT JOIN TBPRD_NFS062_SG D on D.S6201 = A.PRD_ID ");
				sql_fee.append(" WHERE A.PRD_ID IN (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 in ");
				sql_fee.append("	 (SELECT " + prefixC + "PROD_ID" + cIdx + " FROM " + tableName + " WHERE TRADE_SEQ = :tradeSeq))) ");
				sql_fee.append(" AND C.IS_SALE = 1 ");
				sql_fee.append(" ORDER BY D.S6202, A.PRD_ID ");
				queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
				queryCondition.setQueryString(sql_fee.toString());
				List<Map<String, Object>> feeList = dam.exeQuery(queryCondition);
				
				//取得西元年
				Calendar cal = Calendar.getInstance();
				int calYear = cal.get(Calendar.YEAR);
				int currentYear = calYear;
				
				sql_fee = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
				sql_fee.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FUND_FEE_YEAR' AND PARAM_CODE = '1' ");
				queryCondition.setQueryString(sql_fee.toString());
				List<Map<String, Object>> yearList = dam.exeQuery(queryCondition);
				
				if(CollectionUtils.isNotEmpty(yearList)) {
					String paramName = ObjectUtils.toString(yearList.get(0).get("PARAM_NAME"));
					try {
						currentYear = StringUtils.isNotBlank(paramName) ? Integer.parseInt(paramName) + 1 : calYear;
					} catch (Exception e) {
						currentYear = calYear;
					}
				}
				
				data.addParameter("CURRENT_YEAR", currentYear);
				
				if(CollectionUtils.isNotEmpty(feeList)) {
					data.addRecordList("FEELIST", feeList);
					//報表日期
					data.addParameter("FEE_DATA_DATE", "資料日期：" + (String) feeList.get(0).get("FEE_DATA_DATE"));
				}
				
				//generateReport
				report = gen.generateReport(txnCode, reportID, data);
				urlList.add(report.getLocation());
			}
		}
		
		return urlList;
	}
	
	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}

	/***
	 * 基金通路報酬_適配
	 * @param inputVO
	 * @param type: M:母基金 C:子基金
	 * @param prodId: 商品代碼
	 * @param isFinalPage: 是否為最後一個子基金，最後一個子基金才顯示簽名欄
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<String> genReport_FIT(PRDFitInputVO inputVO, String type, String prodId, boolean isFinalPage) throws DAOException, JBranchException {
		String txnCode = "SOT814";
		String reportID = StringUtils.equals("M", type) ? "R3" : "R4"; //R3:母基金通路報酬 R4:子基金通路報酬
		List<String> urlList = new ArrayList<String>();
		ReportIF report = null;
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//動態鎖利基金適配
		sql.append("SELECT  INFO.*, N.WARNING, N.IS_BACKEND, N.PRD_ID AS PROD_ID, N.FUND_CNAME AS PROD_NAME, ");
		sql.append(" 		CASE WHEN PARAM.PARAM_NAME IS NULL THEN N.TRUST_COM  ");
		sql.append("			 WHEN SUBSTR(TRIM(N.TRUST_COM),-2,2) IN ('投信','投顧') ");
		sql.append("			 THEN SUBSTR(TRIM(N.TRUST_COM), 1, LENGTH(TRIM(N.TRUST_COM))-2) || PARAM.PARAM_NAME ");
		sql.append("		ELSE TRIM(N.TRUST_COM) || PARAM.PARAM_NAME END AS TRUST_COM,");
		sql.append("		CASE WHEN PARAM.PARAM_NAME = '投信' THEN 'INV' ");
		sql.append("		WHEN PARAM.PARAM_NAME = '總代理人' THEN 'AGN' ");
		sql.append(" 		WHEN PARAM.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE ");
		sql.append("FROM TBPRD_FUND_BONUSINFO INFO ");
		sql.append("LEFT JOIN TBPRD_FUND N ON INFO.PRD_ID = N.PRD_ID ");
		sql.append("LEFT JOIN TBSYSPARAMETER PARAM on SUBSTR(INFO.PRD_ID,0,2) = PARAM.PARAM_CODE AND PARAM.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
		sql.append("WHERE INFO.PRD_ID = :prod_id ");
		queryCondition.setObject("prod_id", prodId);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> listRecordList = dam.exeQuery(queryCondition);
		
		Map<String, Object> record = listRecordList.get(0);
		if (record.get("FEE") == null) record.put("FEE", 0);
		if (record.get("FEE1") == null) record.put("FEE1", 0);
		if (record.get("FEE2") == null) record.put("FEE2", 0);
		if (record.get("FEE3") == null) record.put("FEE3", 0);
		if (record.get("M_FEE") == null) record.put("M_FEE", 0);
		if (record.get("DISTRIBUTION_FEE") == null) record.put("DISTRIBUTION_FEE", 0);
		if (record.get("M_FEE_RATE") == null) record.put("M_FEE_RATE", 0);
		if (record.get("OTHER_REWARD_INV") == null) record.put("OTHER_REWARD_INV", 0);
		if (record.get("OTHER_REWARD_AGN") == null) record.put("OTHER_REWARD_AGN", 0);
		if (record.get("OTHER_REWARD_FRN") == null) record.put("OTHER_REWARD_FRN", 0);
		if (record.get("IS_BACKEND") == null) record.put("IS_BACKEND", "N");
		if (record.get("REWARD_DF") == null) record.put("REWARD_DF", 0);
		if (record.get("TRAIN") == null) record.put("TRAIN", 0);
		record.put("TRUST_COM", record.get("TRUST_COM") == null ? " " : record.get("TRUST_COM").toString().trim());
		record.put("REWARD", record.get("REWARD") == null ? 0 : getString(record.get("REWARD")));
		if (StringUtils.isNotBlank(getString(record.get("WARNING")))) {
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> map = xmlInfo.doGetVariable("PRD.FUND_C_ALERT", FormatHelper.FORMAT_3);
			String WARNING = map.get(getString(record.get("WARNING")));
			record.put("WARNING", WARNING);
		}
		
		data.addRecordList("Script Mult Data Set", listRecordList);
		data.addParameter("CUST_ID", inputVO.getCustId());
		data.addParameter("CUST_NAME", inputVO.getCustName());
		//最後一個子基金才顯示簽名欄
		data.addParameter("SHOW_SIGNATURE", isFinalPage ? "Y" : "N");
		
		//費用率&報酬率
		StringBuffer sql_fee = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sql_fee.append("select A.*, (C.PRD_ID || ' ' || C.FUND_CNAME_A || '-' || C.CURRENCY_STD_ID) as FUND_CNAME_A, ");
		sql_fee.append("		to_char(A.CREATETIME, 'yyyy/mm/dd') as FEE_DATA_DATE ");
		sql_fee.append(" FROM TBPRD_FUND_BONUSINFO_RATES A ");
		sql_fee.append(" LEFT JOIN TBPRD_FUND C on C.PRD_ID = A.PRD_ID ");
		sql_fee.append(" LEFT JOIN TBPRD_NFS062_SG D on D.S6201 = A.PRD_ID ");
		sql_fee.append(" WHERE A.PRD_ID IN (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 = :prodId)) ");
		sql_fee.append(" AND C.IS_SALE = 1 ");
		sql_fee.append(" ORDER BY D.S6202, A.PRD_ID ");
		queryCondition.setObject("prodId", prodId);
		queryCondition.setQueryString(sql_fee.toString());
		List<Map<String, Object>> feeList = dam.exeQuery(queryCondition);
		
		//取得西元年
		Calendar cal = Calendar.getInstance();
		int calYear = cal.get(Calendar.YEAR);
		int currentYear = calYear;
		
		sql_fee = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		sql_fee.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FUND_FEE_YEAR' AND PARAM_CODE = '1' ");
		queryCondition.setQueryString(sql_fee.toString());
		List<Map<String, Object>> yearList = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(yearList)) {
			String paramName = ObjectUtils.toString(yearList.get(0).get("PARAM_NAME"));
			try {
				currentYear = StringUtils.isNotBlank(paramName) ? Integer.parseInt(paramName) + 1 : calYear;
			} catch (Exception e) {
				currentYear = calYear;
			}
		}
		
		data.addParameter("CURRENT_YEAR", currentYear);
		
		if(CollectionUtils.isNotEmpty(feeList)) {
			data.addRecordList("FEELIST", feeList);
			//報表日期
			data.addParameter("FEE_DATA_DATE", "資料日期：" + (String) feeList.get(0).get("FEE_DATA_DATE"));
		}
		
		//generateReport
		report = gen.generateReport(txnCode, reportID, data);
		urlList.add(report.getLocation());
		
		return urlList;
	}
	
	/***
	 * 轉換：母基金轉換，則取IN_PROD_ID
	 * 其他都是PROD_ID
	 * @param tradeSeq
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getTransferPrefix(String tradeSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TRANSFER_TYPE FROM TBSOT_NF_TRANSFER_DYNA ");
		sql.append(" WHERE TRADE_SEQ = :tradeSeq ");
		queryCondition.setObject("tradeSeq", tradeSeq);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isEmpty(list) ? "" : StringUtils.equals("1", list.get(0).get("TRANSFER_TYPE").toString()) ? "IN_" : "";
	}
	
}