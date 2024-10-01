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
import org.apache.commons.lang.ObjectUtils;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;

/**
 * MENU
 * 動態鎖利自主聲明書
 */
@Component("sot811dyna")
@Scope("request")
public class SOT811DYNA extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT811DYNA.class);

	@Override
	public List<String> printReport() throws Exception {
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
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();	
		String tableName = "";
		
		if (inputVO.getCaseCode() == 1) { //case1下單
			if (inputVO.getTradeType() == 1) {
				tableName = "TBSOT_NF_PURCHASE_DYNA";
			} else if (inputVO.getTradeType() == 3) {
				tableName = "TBSOT_NF_TRANSFER_DYNA";
			} else if (inputVO.getTradeType() == 4) {
				tableName = "TBSOT_NF_CHANGE_DYNA";
			} else if (inputVO.getTradeType() == 5) {
				tableName = "TBSOT_NF_RAISE_AMT_DYNA";
			}
			
			sql.append("select M.CUST_ID, D.* ");
			sql.append("from " + tableName + " D ");
			sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
			sql.append("where D.TRADE_SEQ = :tradeSeq ");
			queryCondition.setObject("tradeSeq",inputVO.getTradeSeq());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> rList = checkList(dam.exeQuery(queryCondition));
			if (rList.size() == 0 ) {  //沒資料，不列印報表
				return new ArrayList<String>();
			} 
			
			int totalProdLine = 0;
			Map<String, Object> rMap = rList.get(0);
			if(inputVO.getTradeType() == 1) { 
				//單筆申購，母子基金都要列出
				for(int rIdx = 0; rIdx <= 3; rIdx++) {
					String rType = (rIdx == 0 ? "" : "_C" + Integer.toString(rIdx));
					if(StringUtils.isNotBlank(ObjectUtils.toString(rMap.get("PROD_ID" + rType)))) {
						HashMap<String, Object> addMap = new HashMap<String, Object>();
						addMap.put("PROD_ID", ObjectUtils.toString(rMap.get("PROD_ID" + rType)));
						addMap.put("PROD_NAME",  ObjectUtils.toString(rMap.get("PROD_NAME" + rType)));
						addMap.put("CUST_ID", ObjectUtils.toString(rMap.get("CUST_ID")));
						totalList.add(addMap);
						totalProdLine++;
					}
				}
			} else if(inputVO.getTradeType() == 3) { 
				//轉換 
				//轉換方式 1：母基金轉換 ==> 列出母基金
				//轉換方式2：子基金轉回母基金 ==> 列出子基金
				if(StringUtils.equals("1", ObjectUtils.toString(rMap.get("TRANSFER_TYPE")))) { //母基金轉換 
					HashMap<String, Object> addMap = new HashMap<String, Object>();
					addMap.put("PROD_ID", ObjectUtils.toString(rMap.get("IN_PROD_ID")));
					addMap.put("PROD_NAME",  ObjectUtils.toString(rMap.get("IN_PROD_NAME")));
					addMap.put("CUST_ID", ObjectUtils.toString(rMap.get("CUST_ID")));
					totalList.add(addMap);
					totalProdLine++;
				} else if(StringUtils.equals("2", ObjectUtils.toString(rMap.get("TRANSFER_TYPE")))) { //子基金轉回母基金
					for(int rIdx = 1; rIdx <= 5; rIdx++) {
						String rType = "_C" + Integer.toString(rIdx);
						if(StringUtils.equals("Y", ObjectUtils.toString(rMap.get("IN_PROD" + rType + "_YN")))) { //有轉入子基金
							HashMap<String, Object> addMap = new HashMap<String, Object>();
							addMap.put("PROD_ID", ObjectUtils.toString(rMap.get("PROD_ID" + rType)));
							addMap.put("PROD_NAME",  ObjectUtils.toString(rMap.get("PROD_NAME" + rType)));
							addMap.put("CUST_ID", ObjectUtils.toString(rMap.get("CUST_ID")));
							totalList.add(addMap);
							totalProdLine++;
						}
					}
				}
			} else if(inputVO.getTradeType() == 4) { 
				//事件變更，沒有轉換金額也沒有增加子基金，不用印自主聲明書
				if(StringUtils.isBlank(ObjectUtils.toString(rMap.get("BATCH_SEQ_AMOUNT"))) && StringUtils.isBlank(ObjectUtils.toString(rMap.get("BATCH_SEQ_ADDPROD")))) {
					return new ArrayList<String>();
				}
				//事件變更，有轉換金額或增加子基金，則列出這些子基金
				if(StringUtils.isNotBlank(ObjectUtils.toString(rMap.get("BATCH_SEQ_AMOUNT")))){
					//有子基金轉換金額
					for(int rIdx = 1; rIdx <= 5; rIdx++) {
						String rType = "_C" + Integer.toString(rIdx);
						if(rMap.get("F_PURCHASE_AMT" + rType) != null) {
							HashMap<String, Object> addMap = new HashMap<String, Object>();
							addMap.put("PROD_ID", ObjectUtils.toString(rMap.get("PROD_ID" + rType)));
							addMap.put("PROD_NAME",  ObjectUtils.toString(rMap.get("PROD_NAME" + rType)));
							addMap.put("CUST_ID", ObjectUtils.toString(rMap.get("CUST_ID")));
							totalList.add(addMap);
							totalProdLine++;
						}
					}
				} else if(StringUtils.isNotBlank(ObjectUtils.toString(rMap.get("BATCH_SEQ_ADDPROD")))){
					//有新增子基金
					for(int rIdx = 1; rIdx <= 5; rIdx++) {
						String rType = "_C" + Integer.toString(rIdx);
						if(StringUtils.isNotBlank(ObjectUtils.toString(rMap.get("F_PROD_ID" + rType)))) {
							HashMap<String, Object> addMap = new HashMap<String, Object>();
							addMap.put("PROD_ID", ObjectUtils.toString(rMap.get("F_PROD_ID" + rType)));
							addMap.put("PROD_NAME",  ObjectUtils.toString(rMap.get("F_PROD_ID" + rType)));
							addMap.put("CUST_ID", ObjectUtils.toString(rMap.get("CUST_ID")));
							totalList.add(addMap);
							totalProdLine++;
						}
					}
				}
			} else if(inputVO.getTradeType() == 5) { 
				//母基金加碼，母子基金都要列出
				for(int rIdx = 0; rIdx <= 5; rIdx++) {
					String rType = (rIdx == 0 ? "" : "_C" + Integer.toString(rIdx));
					if(StringUtils.isNotBlank(ObjectUtils.toString(rMap.get("PROD_ID" + rType)))) {
						HashMap<String, Object> addMap = new HashMap<String, Object>();
						addMap.put("PROD_ID", ObjectUtils.toString(rMap.get("PROD_ID" + rType)));
						addMap.put("PROD_NAME",  ObjectUtils.toString(rMap.get("PROD_NAME" + rType)));
						addMap.put("CUST_ID", ObjectUtils.toString(rMap.get("CUST_ID")));
						totalList.add(addMap);
						totalProdLine++;
					}
				}
			}
			
			//最多購物車只能六筆商品，隨後加上<以下空白>字樣，再補上空白(表格固定12格)。
			Map<String, Object> addBlank = new HashMap<String, Object>();
			addBlank.put("PROD_ID","<以下空白>");
			addBlank.put("PROD_NAME","<以下空白>");
			totalList.add(addBlank);
			totalProdLine++;
			
			for(int i=totalList.size();i<12;i++){
				addBlank = new HashMap<String, Object>();
				addBlank.put("PROD_ID","");
				addBlank.put("PROD_NAME","");
				addBlank.put("CUST_ID","");
				totalList.add(addBlank);
			}
		} else if (inputVO.getCaseCode() == 2) {  //case2 適配 
			Map<String, Object> addMap = new HashMap<String, Object>();
			//母基金
			addMap.put("PROD_ID", inputVO.getPrdId());
			addMap.put("PROD_NAME", inputVO.getPrdName());
			addMap.put("CUST_ID", inputVO.getCustId());
			totalList.add(addMap);
			//子基金1
			addMap = new HashMap<String, Object>();
			addMap.put("PROD_ID", inputVO.getPrdIdC1());
			addMap.put("PROD_NAME", inputVO.getPrdNameC1());
			addMap.put("CUST_ID", inputVO.getCustId());
			totalList.add(addMap);
			//子基金2
			if(StringUtils.isNotBlank(inputVO.getPrdIdC2())) {
				addMap = new HashMap<String, Object>();
				addMap.put("PROD_ID", inputVO.getPrdIdC2());
				addMap.put("PROD_NAME", inputVO.getPrdNameC2());
				addMap.put("CUST_ID", inputVO.getCustId());
				totalList.add(addMap);
			}
			//子基金3
			if(StringUtils.isNotBlank(inputVO.getPrdIdC3())) {
				addMap = new HashMap<String, Object>();
				addMap.put("PROD_ID", inputVO.getPrdIdC3());
				addMap.put("PROD_NAME", inputVO.getPrdNameC3());
				addMap.put("CUST_ID", inputVO.getCustId());
				totalList.add(addMap);
			}
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
			data.addParameter("CUST_ID", ObjectUtils.toString(totalList.get(0).get("CUST_ID")));

			report = gen.generateReport(txnCode, reportID, data);
			url = report.getLocation();
			url_list.add(url);
		}
		return url_list;

	}
	
}