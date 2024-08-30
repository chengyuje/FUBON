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
 * MENU
 * 
 * @author Lily
 * @date 2016/11/28
 * @spec null
 */
@Component("sot814")
@Scope("request")
public class SOT814 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT814.class);

	@Override
	public List<String> printReport() throws JBranchException {
		List<String> urlList = new ArrayList<String>();
		String txnCode = "SOT814";
		String reportID = "R1";
		ReportIF report = null;

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf

		PRDFitInputVO inputVO = getInputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
//		try {
			if (inputVO.getCaseCode() == 1) { //case1下單
				//境外私募基金REPORT_ID = "R2"
				StringBuffer sql = new StringBuffer();
				sql.append("select NVL(F.OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN ");
				sql.append("from TBSOT_TRADE_MAIN M ");
				sql.append("left join TBSOT_NF_PURCHASE_D D ON D.TRADE_SEQ = M.TRADE_SEQ ");
				sql.append("left join TBPRD_FUND F on F.PRD_ID = D.PROD_ID ");
				sql.append("where M.TRADE_SEQ = :tradeSeq ");
				queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());	
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> ovsList = dam.exeQuery(queryCondition);
				if(CollectionUtils.isNotEmpty(ovsList) && StringUtils.equals("Y", ovsList.get(0).get("OVS_PRIVATE_YN").toString())) {
					reportID = "R2";
				}
				
				String tradeType = "";
				String tableName = "";
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("select TRADE_TYPE ");
				sql.append("from TBSOT_TRADE_MAIN ");
				sql.append("where TRADE_SEQ = :tradeSeq ");
				queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
	
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				tradeType = list.get(0).get("TRADE_TYPE").toString();
				
				
				if (tradeType.equals("1") || tradeType.equals("5")){
					tableName = "TBSOT_NF_PURCHASE_D";
				}else if (tradeType.equals("2")){
					tableName = "TBSOT_NF_REDEEM_D";
				}else if (tradeType.equals("3")){
					tableName = "TBSOT_NF_TRANSFER_D";
				}else if (tradeType.equals("4")){
				    tableName = "TBSOT_NF_CHANGE_D";
				}
	
				StringBuffer sql_batchRS = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
				sql_batchRS.append("select TRUNC(TRADE_DATE) as TRADE_DATE ");
				sql_batchRS.append("from "+tableName+" ");
				sql_batchRS.append("where TRADE_SEQ = :tradeSeq ");
				sql_batchRS.append("group by TRUNC(TRADE_DATE) order by TRUNC(TRADE_DATE) ");
				
				queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
				queryCondition.setQueryString(sql_batchRS.toString());
				List<Map<String, Object>> batchRSList = dam.exeQuery(queryCondition);
				String custID3 = "";
				String custName3 = "";
				
				for(Map<String, Object> batchRS :batchRSList){
					String tradeDate = batchRS.get("TRADE_DATE").toString().substring(0, 10);
					sql = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> listRecordList = new ArrayList<Map<String, Object>>();
					if (tradeType.equals("1") || tradeType.equals("5")) {
						sql.append("select  M.CUST_ID, M.CUST_NAME, D.PROD_ID, D.PROD_NAME, D.PROD_CURR, F.*, N.WARNING, N.IS_BACKEND, ");
						sql.append(" 		CASE WHEN PARAM.PARAM_NAME IS NULL THEN N.TRUST_COM  ");
						sql.append("			 WHEN SUBSTR(TRIM(N.TRUST_COM),-2,2) IN ('投信','投顧') ");
						sql.append("			 THEN SUBSTR(TRIM(N.TRUST_COM), 1, LENGTH(TRIM(N.TRUST_COM))-2) || PARAM.PARAM_NAME ");
						sql.append("		ELSE TRIM(N.TRUST_COM) || PARAM.PARAM_NAME END AS TRUST_COM ,");
						sql.append("		CASE WHEN PARAM.PARAM_NAME = '投信' THEN 'INV' ");
						sql.append("		WHEN PARAM.PARAM_NAME = '總代理人' THEN 'AGN' ");
						sql.append(" 		WHEN PARAM.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE ");
						sql.append("from TBSOT_NF_PURCHASE_D D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("left outer join TBPRD_FUND_BONUSINFO F on F.PRD_ID = D.PROD_ID ");
						sql.append("left outer join TBPRD_FUND N on N.PRD_ID = D.PROD_ID ");
						sql.append("left join TBSYSPARAMETER PARAM on SUBSTR(D.PROD_ID,0,2) = PARAM.PARAM_CODE AND PARAM.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and to_char(TRADE_DATE,'YYYY-MM-DD') = :trade_date ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("trade_date", tradeDate);
					} else if (tradeType.equals("2")) {
						sql.append("select  M.CUST_ID, M.CUST_NAME, D.PCH_PROD_ID as PROD_ID, D.PCH_PROD_NAME as PROD_NAME, D.PCH_PROD_CURR as PROD_CURR, F.*, N.WARNING, N.IS_BACKEND, ");
						sql.append(" 		CASE WHEN PARAM.PARAM_NAME IS NULL THEN N.TRUST_COM  ");
						sql.append("		     WHEN SUBSTR(TRIM(N.TRUST_COM),-2,2) IN ('投信','投顧') ");
						sql.append("		     THEN SUBSTR(TRIM(N.TRUST_COM), 1, LENGTH(TRIM(N.TRUST_COM))-2) || PARAM.PARAM_NAME ");
						sql.append("		ELSE TRIM(N.TRUST_COM) || PARAM.PARAM_NAME END AS TRUST_COM,");
						sql.append("		CASE WHEN PARAM.PARAM_NAME = '投信' THEN 'INV' ");
						sql.append("		WHEN PARAM.PARAM_NAME = '總代理人' THEN 'AGN' ");
						sql.append(" 		WHEN PARAM.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE ");
						sql.append("from TBSOT_NF_REDEEM_D D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("left outer join TBPRD_FUND_BONUSINFO F on F.PRD_ID = D.PCH_PROD_ID ");
						sql.append("left outer join TBPRD_FUND N on N.PRD_ID = D.PCH_PROD_ID ");
						sql.append("left join TBSYSPARAMETER PARAM on SUBSTR(D.PCH_PROD_ID,0,2) = PARAM.PARAM_CODE AND PARAM.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and D.IS_RE_PURCHASE = 'Y' ");
						sql.append("and to_char(TRADE_DATE,'YYYY-MM-DD') = :trade_date ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("trade_date", tradeDate);
					} else if(tradeType.equals("3")){
						sql.append("select  M.CUST_ID, M.CUST_NAME, D.IN_PROD_ID_1 as PROD_ID1, D.IN_PROD_NAME_1 as PROD_NAME1, ");
						sql.append("		D.IN_PROD_CURR_1 as PROD_CURR1, D.IN_PROD_ID_2 as PROD_ID2, D.IN_PROD_NAME_2 as PROD_NAME2, ");
						sql.append("		D.IN_PROD_CURR_2 as PROD_CURR2, D.IN_PROD_ID_3 as PROD_ID3, D.IN_PROD_NAME_3 as PROD_NAME3, ");
						sql.append("		D.IN_PROD_CURR_3 as PROD_CURR3, N.WARNING, N.IS_BACKEND IS_BACKEND1, O.IS_BACKEND IS_BACKEND2, P.IS_BACKEND IS_BACKEND3, ");
						
						sql.append(" 		CASE WHEN P1.PARAM_NAME IS NULL THEN N.TRUST_COM  ");
						sql.append("			 WHEN SUBSTR(TRIM(N.TRUST_COM),-2,2) IN ('投信','投顧') ");
						sql.append("			 THEN SUBSTR(TRIM(N.TRUST_COM), 1, LENGTH(TRIM(N.TRUST_COM))-2) || P1.PARAM_NAME ");
						sql.append("		ELSE TRIM(N.TRUST_COM) || P1.PARAM_NAME END AS TRUST_COM,");
						sql.append("		CASE WHEN P1.PARAM_NAME = '投信' THEN 'INV' ");
						sql.append("		WHEN P1.PARAM_NAME = '總代理人' THEN 'AGN' ");
						sql.append(" 		WHEN P1.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE, ");
						
						sql.append(" 		CASE WHEN P2.PARAM_NAME IS NULL THEN O.TRUST_COM  ");
						sql.append("			 WHEN SUBSTR(TRIM(O.TRUST_COM),-2,2) IN ('投信','投顧') ");
						sql.append("			 THEN SUBSTR(TRIM(O.TRUST_COM), 1, LENGTH(TRIM(O.TRUST_COM))-2) || P2.PARAM_NAME ");
						sql.append("		ELSE TRIM(O.TRUST_COM) || P2.PARAM_NAME END AS TRUST_COM2,");
						sql.append("		CASE WHEN P2.PARAM_NAME = '投信' THEN 'INV' ");
						sql.append("		WHEN P2.PARAM_NAME = '總代理人' THEN 'AGN' ");
						sql.append(" 		WHEN P2.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE2, ");
						
						sql.append(" 		CASE WHEN P3.PARAM_NAME IS NULL THEN P.TRUST_COM  ");
						sql.append("			 WHEN SUBSTR(TRIM(P.TRUST_COM),-2,2) IN ('投信','投顧') ");
						sql.append("			 THEN SUBSTR(TRIM(P.TRUST_COM), 1, LENGTH(TRIM(P.TRUST_COM))-2) || P3.PARAM_NAME ");
						sql.append("		ELSE TRIM(P.TRUST_COM) || P3.PARAM_NAME END AS TRUST_COM3,");
						sql.append("		CASE WHEN P3.PARAM_NAME = '投信' THEN 'INV' ");
						sql.append("		WHEN P3.PARAM_NAME = '總代理人' THEN 'AGN' ");
						sql.append(" 		WHEN P3.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE3 ");
						
						sql.append("from TBSOT_NF_TRANSFER_D D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("left outer join TBPRD_FUND N on N.PRD_ID = D.IN_PROD_ID_1 ");
						sql.append("left outer join TBPRD_FUND O on O.PRD_ID = D.IN_PROD_ID_2 ");
						sql.append("left outer join TBPRD_FUND P on P.PRD_ID = D.IN_PROD_ID_3 ");
						sql.append("left join TBSYSPARAMETER P1 on SUBSTR(D.IN_PROD_ID_1,0,2) = P1.PARAM_CODE AND P1.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
						sql.append("left join TBSYSPARAMETER P2 on SUBSTR(D.IN_PROD_ID_2,0,2) = P2.PARAM_CODE AND P2.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
						sql.append("left join TBSYSPARAMETER P3 on SUBSTR(D.IN_PROD_ID_3,0,2) = P3.PARAM_CODE AND P3.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and to_char(TRADE_DATE,'YYYY-MM-DD') = :trade_date ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("trade_date", tradeDate);
					}else if (tradeType.equals("4")){
						sql.append("select  M.CUST_ID, M.CUST_NAME, D.F_PROD_ID as PROD_ID, D.F_PROD_NAME as PROD_NAME, D.F_PROD_CURR as PROD_CURR, F.*, N.WARNING, N.IS_BACKEND, ");
						sql.append(" 		CASE WHEN PARAM.PARAM_NAME IS NULL THEN N.TRUST_COM  ");
						sql.append("			 WHEN SUBSTR(TRIM(N.TRUST_COM),-2,2) IN ('投信','投顧') ");
						sql.append("			 THEN SUBSTR(TRIM(N.TRUST_COM), 1, LENGTH(TRIM(N.TRUST_COM))-2) || PARAM.PARAM_NAME ");
						sql.append("		ELSE TRIM(N.TRUST_COM) || PARAM.PARAM_NAME END AS TRUST_COM,");
						sql.append("		CASE WHEN PARAM.PARAM_NAME = '投信' THEN 'INV' ");
						sql.append("		WHEN PARAM.PARAM_NAME = '總代理人' THEN 'AGN' ");
						sql.append(" 		WHEN PARAM.PARAM_NAME = '境外基金機構' THEN 'FRN' END AS TRUST_COM_TYPE ");
						sql.append("from TBSOT_NF_CHANGE_D D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("left outer join TBPRD_FUND_BONUSINFO F on F.PRD_ID = D.F_PROD_ID ");
						sql.append("left outer join TBPRD_FUND N on N.PRD_ID = D.F_PROD_ID ");
						sql.append("left join TBSYSPARAMETER PARAM on SUBSTR(D.F_PROD_ID,0,2) = PARAM.PARAM_CODE AND PARAM.PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and to_char(TRADE_DATE,'YYYY-MM-DD') = :trade_date ");
						sql.append("and (instr(D.CHANGE_TYPE, 'AX') > 0  or instr(D.CHANGE_TYPE, 'X7') > 0)");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("trade_date", tradeDate);
						
					}
					String prodID1 = "";
					String prodID2 = "";
					String prodID3 = "";
					
					if (tradeType.equals("3")){
						queryCondition.setQueryString(sql.toString());
						listRecordList = dam.exeQuery(queryCondition);
						HashMap<String, Object> addMap = new HashMap<String, Object>();
						custID3=getString(listRecordList.get(0).get("CUST_ID"));
						custName3= getString(listRecordList.get(0).get("CUST_NAME"));
						for (Map<String, Object> map2 : listRecordList) {
							if(StringUtils.isNotEmpty(getString(map2.get("PROD_ID1")))){
								addMap = new HashMap<String, Object>();
								prodID1 =map2.get("PROD_ID1").toString();
							}
							
							if(StringUtils.isNotEmpty(getString(map2.get("PROD_ID2")))){
								prodID2 ="','"+map2.get("PROD_ID2").toString();
							}
							if(StringUtils.isNotEmpty(getString(map2.get("PROD_ID3")))){
								prodID3 ="','"+map2.get("PROD_ID3").toString();
							}
	
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sql = new StringBuffer();
							sql.append("SELECT * FROM TBPRD_FUND_BONUSINFO WHERE PRD_ID in ('"+prodID1+prodID2+prodID3+"') ");
							queryCondition.setQueryString(sql.toString());
							List<Map<String, Object>> otherCodeList = dam.exeQuery(queryCondition);
							prodID1 = "";
							prodID2 = "";
							prodID3 = "";
							for (Map<String, Object> map3 : otherCodeList) {
								addMap = new HashMap<String, Object>();
								if(map2.get("PROD_ID1").equals(map3.get("PRD_ID"))){
									addMap.put("PROD_ID", map2.get("PROD_ID1"));
									addMap.put("PROD_NAME", map2.get("PROD_NAME1"));
									if(StringUtils.isNotEmpty(getString(map2.get("TRUST_COM")))){
										addMap.put("TRUST_COM", getString(map2.get("TRUST_COM")).trim());
									}
									if(StringUtils.isNotEmpty(getString(map2.get("TRUST_COM_TYPE")))){
										addMap.put("TRUST_COM_TYPE", getString(map2.get("TRUST_COM_TYPE")).trim());
									}
									if(StringUtils.isNotEmpty(getString(map2.get("PROD_CURR1")))){
										addMap.put("PROD_CURR", getString(map2.get("PROD_CURR1")));
									}
									if(StringUtils.isNotEmpty(getString(map2.get("IS_BACKEND1")))){
										addMap.put("IS_BACKEND", getString(map2.get("IS_BACKEND1")));
									}
									addMap.putAll(map3);
								}else if(map2.get("PROD_ID2").equals(map3.get("PRD_ID"))){
									addMap.put("PROD_ID", map2.get("PROD_ID2"));
									addMap.put("PROD_NAME", map2.get("PROD_NAME2"));
									if(StringUtils.isNotEmpty(getString(map2.get("TRUST_COM2")))){
										addMap.put("TRUST_COM", getString(map2.get("TRUST_COM2")).trim());
									}
									if(StringUtils.isNotEmpty(getString(map2.get("TRUST_COM_TYPE2")))){
										addMap.put("TRUST_COM_TYPE", getString(map2.get("TRUST_COM_TYPE2")).trim());
									}
									if(StringUtils.isNotEmpty(getString(map2.get("PROD_CURR2")))){
										addMap.put("PROD_CURR", getString(map2.get("PROD_CURR2")));
									}
									if(StringUtils.isNotEmpty(getString(map2.get("IS_BACKEND2")))){
										addMap.put("IS_BACKEND", getString(map2.get("IS_BACKEND2")));
									}
									addMap.putAll(map3);
								}else if(map2.get("PROD_ID3").equals(map3.get("PRD_ID"))){
									addMap.put("PROD_ID", map2.get("PROD_ID3"));
									addMap.put("PROD_NAME", map2.get("PROD_NAME3"));
									if(StringUtils.isNotEmpty(getString(map2.get("TRUST_COM3")))){
										addMap.put("TRUST_COM", getString(map2.get("TRUST_COM3")).trim());
									}
									if(StringUtils.isNotEmpty(getString(map2.get("TRUST_COM_TYPE3")))){
										addMap.put("TRUST_COM_TYPE", getString(map2.get("TRUST_COM_TYPE3")).trim());
									}
									if(StringUtils.isNotEmpty(getString(map2.get("PROD_CURR2")))){
										addMap.put("PROD_CURR", getString(map2.get("PROD_CURR2")));
									}
									if(StringUtils.isNotEmpty(getString(map2.get("IS_BACKEND3")))){
										addMap.put("IS_BACKEND", getString(map2.get("IS_BACKEND3")));
									}
									addMap.putAll(map3);
								}
								
	
								if(StringUtils.isNotEmpty(getString(map3.get("FEE")))){
									addMap.put("FEE", getString(map3.get("FEE")));
								}else if(map3.get("FEE") == null){
									addMap.put("FEE", 0);
								}
	
								if(StringUtils.isNotEmpty(getString(map3.get("FEE1")))){
									addMap.put("FEE1", getString(map3.get("FEE1")));
								}else if (map3.get("FEE1") == null){
									addMap.put("FEE1", 0);
								}
								if(StringUtils.isNotEmpty(getString(map3.get("FEE2")))){
									addMap.put("FEE2", getString(map3.get("FEE2")));
								}else if (map3.get("FEE2") == null){
									addMap.put("FEE2", 0);
								}
								if(StringUtils.isNotEmpty(getString(map3.get("FEE3")))){
									addMap.put("FEE3", getString(map3.get("FEE3")));
								}else if (map3.get("FEE3") == null){
									addMap.put("FEE3", 0);
								}
								if(StringUtils.isNotEmpty(getString(map3.get("M_FEE")))){
									addMap.put("M_FEE", getString(map3.get("M_FEE")));
								}else if (map3.get("M_FEE") == null){
									addMap.put("M_FEE", 0);
								}

								addMap.put("DISTRIBUTION_FEE", ObjectUtils.defaultIfNull(map3.get("DISTRIBUTION_FEE"), 0));

								if(StringUtils.isNotEmpty(getString(map3.get("M_FEE_RATE")))){
									addMap.put("M_FEE_RATE", getString(map3.get("M_FEE_RATE")));
								}else if (map3.get("M_FEE_RATE") == null){
									addMap.put("M_FEE_RATE", 0);
								}
								if(StringUtils.isNotEmpty(getString(map3.get("REWARD")))){
									addMap.put("REWARD", getString(map3.get("REWARD")));
								}else if (map3.get("REWARD") == null){
									addMap.put("REWARD", 0);
								}
								if(StringUtils.isNotEmpty(getString(map3.get("REWARD_DF")))){
									addMap.put("REWARD_DF", getString(map3.get("REWARD_DF")));
								}else if (map3.get("REWARD_DF") == null){
									addMap.put("REWARD_DF", 0);
								}
								if(StringUtils.isNotEmpty(getString(map3.get("TRAIN")))){
									addMap.put("TRAIN", getString(map3.get("TRAIN")));
								}else if (map3.get("TRAIN") == null){
									addMap.put("TRAIN", 0);
								}
								if(StringUtils.isNotEmpty(getString(map3.get("OTHER_REWARD_INV")))){
									addMap.put("OTHER_REWARD_INV", getString(map3.get("OTHER_REWARD_INV")));
								}else if (map3.get("OTHER_REWARD_INV") == null){
									addMap.put("OTHER_REWARD_INV", 0);
								}
								
								if(StringUtils.isNotEmpty(getString(map3.get("OTHER_REWARD_AGN")))){
									addMap.put("OTHER_REWARD_AGN", getString(map3.get("OTHER_REWARD_AGN")));
								}else if (map3.get("OTHER_REWARD_AGN") == null){
									addMap.put("OTHER_REWARD_AGN", 0);
								}
								
								if(StringUtils.isNotEmpty(getString(map3.get("OTHER_REWARD_FRN")))){
									addMap.put("OTHER_REWARD_FRN", getString(map3.get("OTHER_REWARD_FRN")));
								}else if (map3.get("OTHER_REWARD_FRN") == null){
									addMap.put("OTHER_REWARD_FRN", 0);
								}
								
								if(StringUtils.isNotEmpty(getString(map2.get("TEUST_COM1")))){
									addMap.put("TEUST_COM", getString(map2.get("TEUST_COM1")));
								}
								
								if(StringUtils.isNotEmpty(getString(map2.get("IS_BACKEND1")))){
									addMap.put("IS_BACKEND", getString(map2.get("IS_BACKEND1")));
								}
								
								if (StringUtils.isNotBlank(getString(map2.get("WARNING")))) {
									XmlInfo xmlInfo = new XmlInfo();
									Map<String, String> map = xmlInfo.doGetVariable("PRD.FUND_ALERT", FormatHelper.FORMAT_3);
									String WARNING = map.get(getString(map2.get("WARNING")));
									addMap.put("WARNING", WARNING);
								} else {
									addMap.put("WARNING", "");
								}
								
								totalList.add(addMap);
							}
	
						}
	
					}else{
						queryCondition.setQueryString(sql.toString());
						listRecordList = dam.exeQuery(queryCondition);
					
					
						for(Map<String, Object> record :listRecordList){
							if(!record.isEmpty()){
								if (record.get("FEE") == null){
									record.put("FEE", 0);
								}
								if (record.get("FEE1") == null){
									record.put("FEE1", 0);
								}
								if (record.get("FEE2") == null){
									record.put("FEE2", 0);
								}
								if (record.get("FEE3") == null){
									record.put("FEE3", 0);
								}
								if (record.get("M_FEE") == null){
									record.put("M_FEE", 0);
								}

								if (record.get("DISTRIBUTION_FEE") == null){
									record.put("DISTRIBUTION_FEE", 0);
								}

								if (record.get("M_FEE_RATE") == null){
									record.put("M_FEE_RATE", 0);
								}
								
								if (record.get("REWARD") == null){
									record.put("REWARD", 0);
								} else {
									record.put("REWARD", getString(record.get("REWARD")));
								}
								
								if (record.get("REWARD_DF") == null){
									record.put("REWARD_DF", 0);
								}
								if (record.get("TRAIN") == null){
									record.put("TRAIN", 0);
								}
								
								if (record.get("TRUST_COM") == null){
									record.put("TRUST_COM", " ");
								} else {
									record.put("TRUST_COM", record.get("TRUST_COM").toString().trim());
								}
								
								if (record.get("OTHER_REWARD_INV") == null){
									record.put("OTHER_REWARD_INV", 0);
								}
								
								if (record.get("OTHER_REWARD_AGN") == null){
									record.put("OTHER_REWARD_AGN", 0);
								}
								
								if (record.get("OTHER_REWARD_FRN") == null){
									record.put("OTHER_REWARD_FRN", 0);
								}
								
								if (record.get("IS_BACKEND") == null){
									record.put("IS_BACKEND", "N");
								}
								
								if (StringUtils.isNotBlank(getString(record.get("WARNING")))) {
									XmlInfo xmlInfo = new XmlInfo();
									Map<String, String> map = xmlInfo.doGetVariable("PRD.FUND_ALERT", FormatHelper.FORMAT_3);
									String WARNING = map.get(getString(record.get("WARNING")));
									record.put("WARNING", WARNING);
								}
							}else{
								listRecordList = new ArrayList<Map<String,Object>>();
								Map<String, Object> map = new HashMap<String,Object>();
								map.put("FEE", 0);
								map.put("FEE1", 0);
								map.put("FEE2", 0);
								map.put("FEE3", 0);
								map.put("M_FEE", 0);
								map.put("DISTRIBUTION_FEE", 0);
								map.put("M_FEE_RATE", 0);
								map.put("REWARD", 0);
								map.put("REWARD_DF", 0);
								map.put("TRAIN", 0);
								map.put("TRUST_COM", "");
								map.put("TRUST_COM_TYPE", "");
								map.put("OTHER_REWARD_INV", 0);
								map.put("OTHER_REWARD_AGN", 0);
								map.put("OTHER_REWARD_FRN", 0);
								map.put("CUST_ID", "");
								map.put("CUST_NAME", "");
								map.put("IS_BACKEND", "N");
								map.put("WARNING", "");
								listRecordList.add(map);
							}
						}
						totalList.addAll(listRecordList);
					}
	
	
					if (totalList.size() > 0) {
						data.addRecordList("Script Mult Data Set", totalList);
						String custID = "";
						String custName = "";
						if (tradeType.equals("3")){
							custID = custID3;
							custName = custName3;
						}else{
							custID = (String) totalList.get(0).get("CUST_ID");
							custName = (String) totalList.get(0).get("CUST_NAME");
						}
						
						if (StringUtils.isNotBlank(custID)) {
							data.addParameter("CUST_ID", custID);
						}else{
							data.addParameter("CUST_ID", " ");
						}
						if (StringUtils.isNotBlank(custName)) {
							data.addParameter("CUST_NAME", custName);
						}						
		//				notifyClientToDownloadFile(url, "SOT814.pdf");
		//				notifyClientViewDoc(url, "pdf");
					}
					
					//費用率&報酬率
					StringBuffer sql_fee = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
					sql_fee.append("select A.*, (C.PRD_ID || ' ' || C.FUND_CNAME_A || '-' || C.CURRENCY_STD_ID) as FUND_CNAME_A, ");
					sql_fee.append("		to_char(A.CREATETIME, 'yyyy/mm/dd') as FEE_DATA_DATE, C.OVS_PRIVATE_YN ");
					sql_fee.append("from TBPRD_FUND_BONUSINFO_RATES A ");
					sql_fee.append("left outer join TBPRD_FUND C on C.PRD_ID = A.PRD_ID ");
					sql_fee.append("left outer join TBPRD_NFS062_SG D on D.S6201 = A.PRD_ID ");
					
					if (tradeType.equals("1") || tradeType.equals("5")) {						
						sql_fee.append("where A.PRD_ID in (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 in ");
						sql_fee.append("					 (select PROD_ID from TBSOT_NF_PURCHASE_D where TRADE_SEQ = :tradeSeq))) ");
					} else if (tradeType.equals("2")) {
						sql_fee.append("where A.PRD_ID in (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 in ");
						sql_fee.append("					 (select PCH_PROD_ID from TBSOT_NF_REDEEM_D where TRADE_SEQ = :tradeSeq))) ");
					} else if (tradeType.equals("3")) {
						sql_fee.append("where (A.PRD_ID in (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 in ");
						sql_fee.append("					(select IN_PROD_ID_1 from TBSOT_NF_TRANSFER_D where TRADE_SEQ = :tradeSeq))) ");
						sql_fee.append("   or  A.PRD_ID in (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 in ");
						sql_fee.append("   					(select IN_PROD_ID_2 from TBSOT_NF_TRANSFER_D where TRADE_SEQ = :tradeSeq))) ");
						sql_fee.append("   or  A.PRD_ID in (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 in ");
						sql_fee.append("   					(select IN_PROD_ID_3 from TBSOT_NF_TRANSFER_D where TRADE_SEQ = :tradeSeq)))) ");
					} else if (tradeType.equals("4")) {
						sql_fee.append("where A.PRD_ID in (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 in ");
						sql_fee.append("					(select F_PROD_ID from TBSOT_NF_CHANGE_D where TRADE_SEQ = :tradeSeq and (instr(CHANGE_TYPE, 'AX') > 0  or instr(CHANGE_TYPE, 'X7') > 0)))) ");
					}
					sql_fee.append(" AND C.IS_SALE = 1 ");
					sql_fee.append(" order by D.S6202, A.PRD_ID ");
					queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
					queryCondition.setQueryString(sql_fee.toString());
					List<Map<String, Object>> feeList = dam.exeQuery(queryCondition);
					
					//取得西元年
					Calendar cal = Calendar.getInstance();
					int calYear = cal.get(Calendar.YEAR);
					int currentYear = calYear;
					
					sql_fee = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql_fee.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FUND_FEE_YEAR' AND PARAM_CODE = :paramCode ");
					String paramCode = (CollectionUtils.isNotEmpty(feeList) && StringUtils.equals("Y", ObjectUtils.toString(feeList.get(0).get("OVS_PRIVATE_YN")))) ? ObjectUtils.toString(feeList.get(0).get("PRD_ID")) : "1";
					queryCondition.setObject("paramCode", paramCode);
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
					if (totalList.size() > 0) {
						report = gen.generateReport(txnCode, reportID, data);
						urlList.add(report.getLocation());
					}
				}
			} else if (inputVO.getCaseCode() == 2) { //case2適配
				//境外私募基金REPORT_ID = "R2"
				StringBuffer sql = new StringBuffer();
				sql.append("select NVL(OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN ");
				sql.append("from TBPRD_FUND ");
				sql.append("WHERE PRD_ID = :prod_id ");
				queryCondition.setObject("prod_id", inputVO.getPrdId());	
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> ovsList = dam.exeQuery(queryCondition);
				if(CollectionUtils.isNotEmpty(ovsList) && StringUtils.equals("Y", ovsList.get(0).get("OVS_PRIVATE_YN").toString())) {
					reportID = "R2";
				}
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT  INFO.*, N.WARNING, N.IS_BACKEND, ");
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
				queryCondition.setObject("prod_id", inputVO.getPrdId());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> fitList = dam.exeQuery(queryCondition);
				
				List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
				HashMap<String, Object> addMap = new HashMap<String, Object>();
				
				addMap.put("PROD_ID", inputVO.getPrdId());
				addMap.put("PROD_NAME", inputVO.getPrdName());
				addMap.put("PROD_CURR", inputVO.getCurrency());
				
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("TRUST_COM")))){
					addMap.put("TRUST_COM", getString(fitList.get(0).get("TRUST_COM")));
				}
				
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("TRUST_COM_TYPE")))){
					addMap.put("TRUST_COM_TYPE", getString(fitList.get(0).get("TRUST_COM_TYPE")));
				}
				
				addMap.putAll(fitList.get(0));
			
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("FEE")))){
					addMap.put("FEE", getString(fitList.get(0).get("FEE")));
				}else if(fitList.get(0).get("FEE") == null){
					addMap.put("FEE", 0);
				}

				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("FEE1")))){
					addMap.put("FEE1", getString(fitList.get(0).get("FEE1")));
				}else if (fitList.get(0).get("FEE1") == null){
					addMap.put("FEE1", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("FEE2")))){
					addMap.put("FEE2", getString(fitList.get(0).get("FEE2")));
				}else if (fitList.get(0).get("FEE2") == null){
					addMap.put("FEE2", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("FEE3")))){
					addMap.put("FEE3", getString(fitList.get(0).get("FEE3")));
				}else if (fitList.get(0).get("FEE3") == null){
					addMap.put("FEE3", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("M_FEE")))){
					addMap.put("M_FEE", getString(fitList.get(0).get("M_FEE")));
				}else if (fitList.get(0).get("M_FEE") == null){
					addMap.put("M_FEE", 0);
				}

				addMap.put("DISTRIBUTION_FEE", ObjectUtils.defaultIfNull(fitList.get(0).get("DISTRIBUTION_FEE"), 0));

				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("M_FEE_RATE")))){
					addMap.put("M_FEE_RATE", getString(fitList.get(0).get("M_FEE_RATE")));
				}else if (fitList.get(0).get("M_FEE_RATE") == null){
					addMap.put("M_FEE_RATE", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("REWARD")))){
					addMap.put("REWARD", getString(fitList.get(0).get("REWARD")));
				}else if (fitList.get(0).get("REWARD") == null){
					addMap.put("REWARD", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("REWARD_DF")))){
					addMap.put("REWARD_DF", getString(fitList.get(0).get("REWARD_DF")));
				}else if (fitList.get(0).get("REWARD_DF") == null){
					addMap.put("REWARD_DF", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("TRAIN")))){
					addMap.put("TRAIN", getString(fitList.get(0).get("TRAIN")));
				}else if (fitList.get(0).get("TRAIN") == null){
					addMap.put("TRAIN", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("IS_BACKEND")))){
					addMap.put("IS_BACKEND", getString(fitList.get(0).get("IS_BACKEND")));
				}else if (fitList.get(0).get("IS_BACKEND") == null){
					addMap.put("IS_BACKEND", "N");
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("OTHER_REWARD_INV")))){
					addMap.put("OTHER_REWARD_INV", getString(fitList.get(0).get("OTHER_REWARD_INV")));
				}else if (fitList.get(0).get("OTHER_REWARD_INV") == null){
					addMap.put("OTHER_REWARD_INV", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("OTHER_REWARD_AGN")))){
					addMap.put("OTHER_REWARD_AGN", getString(fitList.get(0).get("OTHER_REWARD_AGN")));
				}else if (fitList.get(0).get("OTHER_REWARD_AGN") == null){
					addMap.put("OTHER_REWARD_AGN", 0);
				}
				if(StringUtils.isNotEmpty(getString(fitList.get(0).get("OTHER_REWARD_FRN")))){
					addMap.put("OTHER_REWARD_FRN", getString(fitList.get(0).get("OTHER_REWARD_FRN")));
				}else if (fitList.get(0).get("OTHER_REWARD_FRN") == null){
					addMap.put("OTHER_REWARD_FRN", 0);
				}
				if (StringUtils.isNotBlank(getString(fitList.get(0).get("WARNING")))) {
					XmlInfo xmlInfo = new XmlInfo();
					Map<String, String> map = xmlInfo.doGetVariable("PRD.FUND_ALERT", FormatHelper.FORMAT_3);
					String WARNING = map.get(getString(fitList.get(0).get("WARNING")));
					addMap.put("WARNING", WARNING);
				} else {
					addMap.put("WARNING", "");
				}

				totalList.add(addMap);
				
				if (totalList.size() > 0) {
					data.addRecordList("Script Mult Data Set", totalList);
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer custSql = new StringBuffer();
					custSql.append("SELECT CUST_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id");
					queryCondition.setObject("cust_id", inputVO.getCustId());
					queryCondition.setQueryString(custSql.toString());
					List<Map<String, Object>> custList = dam.exeQuery(queryCondition);
					
					String custID = inputVO.getCustId();
					String custName = "";
					if (!"".equals(getString(custList.get(0).get("CUST_NAME")))){
						custName = (String) custList.get(0).get("CUST_NAME");
					}
					
					data.addParameter("CUST_ID", custID);
					if (StringUtils.isNotBlank(custName)) {
						data.addParameter("CUST_NAME", custName);
					}
					
				}
				
				//費用率&報酬率
				StringBuffer sql_fee = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
				sql_fee.append("select A.*, (C.PRD_ID || ' ' || C.FUND_CNAME_A || '-' || C.CURRENCY_STD_ID) as FUND_CNAME_A, ");
				sql_fee.append("		to_char(A.CREATETIME, 'yyyy/mm/dd') as FEE_DATA_DATE, C.OVS_PRIVATE_YN ");
				sql_fee.append("from TBPRD_FUND_BONUSINFO_RATES A ");
				sql_fee.append("left outer join TBPRD_FUND C on C.PRD_ID = A.PRD_ID ");
				sql_fee.append("where A.PRD_ID in (select S6201 from TBPRD_NFS062_SG where S6202 in (select S6202 from TBPRD_NFS062_SG where S6201 = :prod_id)) ");
				sql_fee.append(" AND C.IS_SALE = 1 ");
				sql_fee.append(" order by A.PRD_ID ");
				queryCondition.setObject("prod_id", inputVO.getPrdId());
				queryCondition.setQueryString(sql_fee.toString());
				List<Map<String, Object>> feeList = dam.exeQuery(queryCondition);
				
				//取得西元年
				Calendar cal = Calendar.getInstance();
				int calYear = cal.get(Calendar.YEAR);
				int currentYear = calYear;
				
				sql_fee = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql_fee.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FUND_FEE_YEAR' AND PARAM_CODE = :paramCode ");
				String paramCode = (CollectionUtils.isNotEmpty(feeList) && StringUtils.equals("Y", ObjectUtils.toString(feeList.get(0).get("OVS_PRIVATE_YN")))) ? ObjectUtils.toString(feeList.get(0).get("PRD_ID")) : "1";
				queryCondition.setObject("paramCode", paramCode);
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
				
				if (totalList.size() > 0) {
					report = gen.generateReport(txnCode, reportID, data);
					urlList.add(report.getLocation());
				}
			}
			return urlList;
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}

	}
	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}

}