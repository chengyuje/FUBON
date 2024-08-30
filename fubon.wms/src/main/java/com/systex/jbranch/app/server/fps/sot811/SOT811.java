package com.systex.jbranch.app.server.fps.sot811;

import java.util.ArrayList;
import java.util.HashMap;
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
 * @date 2016/11/15
 * @spec null
 */
@Component("sot811")
@Scope("request")
public class SOT811 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT811.class);

	@Override
	public List<String> printReport() throws JBranchException {
		List<String> url_list = new ArrayList<String>();
		String url = null;
		String txnCode = "SOT811";
		String reportID = "R1";
		ReportIF report = null;

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();

		PRDFitInputVO inputVO = getInputVO();
		
		try {
			if (inputVO.getCaseCode() == 1) { //case1下單
				dam = this.getDataAccessManager();
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				String tradeType = "";
				String prodType ="";
				String tableName = "";
				
				StringBuffer sql = new StringBuffer();
				sql.append("select TRADE_TYPE, PROD_TYPE ");
				sql.append("from TBSOT_TRADE_MAIN ");
				sql.append("where TRADE_SEQ = :tradeSeq ");
				queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
	
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
	//			System.out.println("TRADE_TYPE-----="+list.get(0).get("TRADE_TYPE"));
	//			System.out.println("PROD_TYPE-----="+list.get(0).get("PROD_TYPE"));
				tradeType = list.get(0).get("TRADE_TYPE").toString();
				prodType = list.get(0).get("PROD_TYPE").toString();
				
				if ("2".equals(prodType)) {
					tableName = "TBSOT_ETF_TRADE_D";
				} else if ("3".equals(prodType)) {
					tableName = "TBSOT_BN_TRADE_D";
				} else if ("4".equals(prodType)) {
					tableName = "TBSOT_SI_TRADE_D";
				} else if ("5".equals(prodType)) {
					tableName = "TBSOT_SN_TRADE_D";
				}
				
				StringBuffer sql_batchRS = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				if ("1".equals(prodType) && tradeType.equals("4")){
					sql_batchRS.append("select BATCH_SEQ from TBSOT_NF_CHANGE_BATCH ");
				}else if (!("1".equals(prodType) && tradeType.equals("4"))){
					sql_batchRS.append("select BATCH_SEQ, count(1) as BATCH_COUNT ");
					if ("1".equals(prodType) && (tradeType.equals("1") || tradeType.equals("5"))) {
						sql_batchRS.append("from TBSOT_NF_PURCHASE_D ");
					} else if ("1".equals(prodType) && tradeType.equals("2")) {
						sql_batchRS.append("from TBSOT_NF_REDEEM_D ");
					} else if ("1".equals(prodType) && tradeType.equals("3")) {
						sql_batchRS.append("from TBSOT_NF_TRANSFER_D ");
					} else if ("2".equals(prodType)) {
						sql_batchRS.append("from TBSOT_ETF_TRADE_D ");
					} else if ("3".equals(prodType)) {
						sql_batchRS.append("from TBSOT_BN_TRADE_D ");
					} else if ("4".equals(prodType)) {
						sql_batchRS.append("from TBSOT_SI_TRADE_D ");
					} else if ("5".equals(prodType)) {
						sql_batchRS.append("from TBSOT_SN_TRADE_D ");
					}		
				}
				sql_batchRS.append("where TRADE_SEQ = :tradeSeq ");
				if(StringUtils.isNotEmpty(inputVO.getBatchSeq())){
					sql_batchRS.append("and BATCH_SEQ = :batchSeq ");
					queryCondition.setObject("batchSeq", inputVO.getBatchSeq());
				}
				sql_batchRS.append("group by BATCH_SEQ order by BATCH_SEQ ");
				
				queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
				queryCondition.setQueryString(sql_batchRS.toString());
				List<Map<String, Object>> batchRSList = dam.exeQuery(queryCondition);
				sql = new StringBuffer();
	
				List<Map<String, Object>> l = new ArrayList();
				for (Map<String, Object> map : batchRSList) {
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					if ("1".equals(prodType) && (tradeType.equals("1") || tradeType.equals("5"))) {
						sql.append("select M.CUST_ID, D.PROD_ID, D.PROD_NAME ");
						sql.append("from TBSOT_NF_PURCHASE_D D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and D.BATCH_SEQ = :batch_SEQ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("batch_SEQ", map.get("BATCH_SEQ"));
					} else if("1".equals(prodType) && tradeType.equals("2")) {
						sql.append("select M.CUST_ID, D.PCH_PROD_ID as PROD_ID, D.PCH_PROD_NAME as PROD_NAME ");
						sql.append("from TBSOT_NF_REDEEM_D D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and D.BATCH_SEQ = :batch_SEQ ");
						sql.append("and D.IS_RE_PURCHASE = 'Y' ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("batch_SEQ", map.get("BATCH_SEQ"));
	
					}else if("1".equals(prodType) && tradeType.equals("3")){
						sql.append("select M.CUST_ID,D.IN_PROD_ID_1 as PROD_ID1,D.IN_PROD_NAME_1 as PROD_NAME1,D.IN_PROD_ID_2 as PROD_ID2, ");
						sql.append("D.IN_PROD_NAME_2 as PROD_NAME2,D.IN_PROD_ID_3 as PROD_ID3,D.IN_PROD_NAME_3 as PROD_NAME3 ");
						sql.append("from TBSOT_NF_TRANSFER_D D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and D.BATCH_SEQ = :batch_SEQ ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("batch_SEQ", map.get("BATCH_SEQ"));
					}else if("1".equals(prodType) && tradeType.equals("4")){
						sql.append("select M.CUST_ID,D.F_PROD_ID as PROD_ID,D.F_PROD_NAME as PROD_NAME ");
						sql.append("from TBSOT_NF_CHANGE_D D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("inner join TBSOT_NF_CHANGE_BATCH B on B.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and B.BATCH_SEQ = :batch_SEQ ");
						sql.append("and B.CHANGE_TYPE in ('AX','X7') ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("batch_SEQ", map.get("BATCH_SEQ"));
					} else {
						sql.append("select M.CUST_ID, D.PROD_ID, D.PROD_NAME ");
						sql.append("from "+tableName+" D ");
						sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
						sql.append("where D.TRADE_SEQ = :tradeSeq ");
						sql.append("and D.BATCH_SEQ = :batch_SEQ");
						queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
						queryCondition.setObject("batch_SEQ", map.get("BATCH_SEQ"));
					}
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> listRecordList = checkList(dam.exeQuery(queryCondition));
					if (listRecordList.size() == 0 ) {  //沒資料，不列印報表
						return new ArrayList<String>();
					} 
					
					// 組合所有資料
					if("1".equals(prodType) && StringUtils.equals(tradeType, "3")){
						for (Map<String, Object> map2 : listRecordList) {
							HashMap<String, Object> addMap = new HashMap<String, Object>();
							if(StringUtils.isNotEmpty(getString(map2.get("PROD_ID1")))){
								addMap.put("PROD_ID", getString(map2.get("PROD_ID1")));
								addMap.put("PROD_NAME",  getString(map2.get("PROD_NAME1")));
								addMap.put("CUST_ID", getString(map2.get("CUST_ID")));
								totalList.add(addMap);
							}
							
							if(StringUtils.isNotEmpty(getString(map2.get("PROD_ID2")))){
								addMap = new HashMap<String, Object>();
								addMap.put("PROD_ID", getString(map2.get("PROD_ID2")));
								addMap.put("PROD_NAME",  getString(map2.get("PROD_NAME2")));
								addMap.put("CUST_ID", getString(map2.get("CUST_ID")));
								totalList.add(addMap);
							}
							
							if(StringUtils.isNotEmpty(getString(map2.get("PROD_ID3")))){
								addMap = new HashMap<String, Object>();
								addMap.put("PROD_ID", getString(map2.get("PROD_ID3")));
								addMap.put("PROD_NAME",  getString(map2.get("PROD_NAME3")));
								addMap.put("CUST_ID", getString(map2.get("CUST_ID")));
								totalList.add(addMap);
							}
							
						}
					}else{
						for (Map<String,Object> m : listRecordList) {
							Map<String, Object> addMap = new HashMap<String, Object>();
							addMap.put("PROD_ID", m.get("PROD_ID"));
							addMap.put("PROD_NAME", m.get("PROD_NAME"));
							addMap.put("CUST_ID", m.get("CUST_ID"));
							totalList.add(addMap);
						}
					}
				}
				
				//最多購物車只能六筆商品，隨後加上<以下空白>字樣，再補上空白(表格固定12格)。
				Map<String, Object> addBlank = new HashMap<String, Object>();
				addBlank.put("PROD_ID","<以下空白>");
				addBlank.put("PROD_NAME","<以下空白>");
				totalList.add(addBlank);
				
				for(int i=totalList.size();i<12;i++){
					addBlank = new HashMap<String, Object>();
					addBlank.put("PROD_ID","");
					addBlank.put("PROD_NAME","");
					addBlank.put("CUST_ID","");
					totalList.add(addBlank);
				}
				//
			} else if (inputVO.getCaseCode() == 2) {  //case2 適配 
				Map<String, Object> addMap = new HashMap<String, Object>();
				addMap.put("PROD_ID", inputVO.getPrdId());
				addMap.put("PROD_NAME", inputVO.getPrdName());
				addMap.put("CUST_ID", inputVO.getCustId());
				totalList.add(addMap);
				
				//適配只會有一筆，隨後加上<以下空白>字樣，再補上空白(表格固定12格)。
				Map<String, Object> addBlank = new HashMap<String, Object>();
				addBlank.put("PROD_ID","<以下空白>");
				addBlank.put("PROD_NAME","<以下空白>");
				totalList.add(addBlank);
				
				for(int i=totalList.size();i<12;i++){
					addBlank = new HashMap<String, Object>();
					addBlank.put("PROD_ID","");
					addBlank.put("PROD_NAME","");
					addBlank.put("CUST_ID","");
					totalList.add(addBlank);
				}
			}
			
			if (totalList.size() > 0) {
				data.addRecordList("Script Mult Data Set", totalList);
				
				String custID = "";
				if (inputVO.getCaseCode() == 1) {         //case1下單
					custID = (String) totalList.get(0).get("CUST_ID");
				} else if (inputVO.getCaseCode() == 2) {   //case2 適配 
					custID = inputVO.getCustId();
				}
				if (StringUtils.isNotBlank(custID)) {
					data.addParameter("CUST_ID", custID);
				}

				report = gen.generateReport(txnCode, reportID, data);
				url = report.getLocation();
				url_list.add(url);
//				notifyClientToDownloadFile(url, "SOT811.pdf");
//				notifyClientViewDoc(url, "pdf");
			}
			return url_list;

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}
}