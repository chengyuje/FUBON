package com.systex.jbranch.app.server.fps.sot807;

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
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
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
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

/**
 * 下單專區交易
 * 
 * @author Jacky Wu
 * @date 2017/1/5
 * @spec 海外股票ETF申購_贖回申請書
 */
@Component("sot807")
@Scope("request")
public class SOT807 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT807.class);
	
	@Override
	public List<String> printReport() throws Exception {
		String url = null;
		String txnCode = "SOT807";
		String reportID = "R1";
		ReportIF report = null;
		int i =0; //取batchSeq
		
		PRDFitInputVO inputVO = getInputVO();
		
//		try{
			XmlInfo xmlinfo = new XmlInfo();
			
			Map<String, String> trustTypeMap = xmlinfo.doGetVariable("SOT.ASSET_TRADE_SUB_TYPE", FormatHelper.FORMAT_3);
			Map<String, String> transferTypeMap = xmlinfo.doGetVariable("SOT.TRANSFER_TYPE", FormatHelper.FORMAT_3);
			
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sql = new StringBuffer();
			sql.append("select BATCH_SEQ,count(1) as BATCH_COUNT from TBSOT_ETF_TRADE_D ");
			sql.append("where TRADE_SEQ = :trade_seq ");
			
			if (StringUtils.isNotEmpty(inputVO.getBatchSeq())) {
				sql.append("and BATCH_SEQ = :batch_seq ");
			}
			sql.append("group by BATCH_SEQ order by BATCH_SEQ");

			queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
			if (StringUtils.isNotEmpty(inputVO.getBatchSeq())) {
				queryCondition.setObject("batch_seq", inputVO.getBatchSeq());
			}
			
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			List<String> url_list = new ArrayList<String>();
			

			if(list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");

			List<Map<String, Object>> data_list = null;
			ReportFactory factory = new ReportFactory();
			ReportDataIF data = new ReportData();
			ReportGeneratorIF gen = factory.getGenerator();  //產出pdf
			
			for(Map<String, Object> map :list){
				List<Map<String, Object>> buyList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> sellList = new ArrayList<Map<String, Object>>();
				sql = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql.append("select M.CUST_ID,M.CUST_NAME,M.BRANCH_NBR,D.*,M.LASTUPDATE as MLASTUPDATE,M.REC_SEQ,TO_CHAR(D.DUE_DATE, 'yyyyMMdd') as DUE_DATE_NEW, ");
				sql.append("(SELECT COUNTRY_CODE FROM TBPRD_STOCK_NREXGFL WHERE STOCK_CODE = NVL(F.STOCK_CODE, S.STOCK_CODE)) COUNTRY_CODE, M.GUARDIANSHIP_FLAG, ");
				sql.append("T.PARAM_NAME ENTRUST_TYPE_DESC, M.TRUST_TRADE_TYPE, (CASE WHEN F.PRD_ID IS NULL THEN S.PI_BUY ELSE F.PI_BUY END) AS PI_BUY ");
				sql.append("from TBSOT_ETF_TRADE_D D ");
				sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
				sql.append("left join TBPRD_ETF F on F.PRD_ID = D.PROD_ID ");
				sql.append("left join TBPRD_STOCK S on S.PRD_ID = D.PROD_ID ");
				sql.append("left join TBSYSPARAMETER T on T.PARAM_CODE = decode(D.ENTRUST_TYPE, '4', '3', D.ENTRUST_TYPE) ");
				sql.append("where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq ");
				sql.append("and T.PARAM_TYPE = 'SOT.ENTRUST_TYPE' ");
				queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
				queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
				queryCondition.setQueryString(sql.toString());
							
				data_list = checkList(dam.exeQuery(queryCondition));				

				if(data_list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");
				
				data.addParameter("BATCH_SEQ",getString(list.get(i).get("BATCH_SEQ")));
				data.addParameter("CUST_ID",getString(data_list.get(0).get("CUST_ID")));
				data.addParameter("CUST_NAME",getString(data_list.get(0).get("CUST_NAME")) + (StringUtils.equals("Y", getString(data_list.get(0).get("TRUST_PEOP_NUM"))) ? "*" : ""));
				data.addParameter("TRUST_ACCT", getString(data_list.get(0).get("TRUST_ACCT")));
				data.addParameter("TRADE_SUB_TYPE", getString(data_list.get(0).get("TRADE_SUB_TYPE")));
				
				//金錢信託
				data.addParameter("GUARDIANSHIP_FLAG", "");
				if(StringUtils.equals("M", getString(data_list.get(0).get("TRUST_TRADE_TYPE")))) {
					reportID = "R2";
					data.addParameter("CONTRACT_ID", getString(data_list.get(0).get("CONTRACT_ID")));
					data.addParameter("BRANCH_NBR",getString(data_list.get(0).get("BRANCH_NBR")));
					
					if(StringUtils.equals("B", getString(data_list.get(0).get("TRADE_SUB_TYPE"))) && StringUtils.equals("Y", getString(data_list.get(0).get("PI_BUY")))) { 
						//申購時，金錢信託顯示專投商品註記
						data.addParameter("PI_BUY", "(本次申購含專投商品)");
					} else {
						data.addParameter("PI_BUY", "");
					}
					
					data.addParameter("GUARDIANSHIP_FLAG", data_list.get(0).get("GUARDIANSHIP_FLAG").toString().equals("1") ? "*受監護宣告*" : data_list.get(0).get("GUARDIANSHIP_FLAG").toString().equals("2") ? "*受輔助宣告*" : " " );
				}
				
				i++;
				for(Map<String, Object> data_map : data_list){
						Map<String, Object> rptMap = new HashMap<String, Object>();
						if(StringUtils.equals(getString(data_map.get("TRADE_SUB_TYPE")),"B")){
							rptMap.put("CREDIT_ACCT", getString(data_map.get("CREDIT_ACCT")));
							rptMap.put("DEBIT_ACCT", getString(data_map.get("DEBIT_ACCT")));
							rptMap.put("DUE_DATE", getString(data_map.get("DUE_DATE_NEW")));
							rptMap.put("ENTRUST_AMT", getString(data_map.get("ENTRUST_TYPE_DESC"))+getString(data_map.get("ENTRUST_AMT")));
							rptMap.put("FEE_RATE", getString(data_map.get("FEE_RATE"))+" %");
							rptMap.put("PROD_CURR", getString(data_map.get("PROD_CURR")));
							rptMap.put("PROD_ID", getString(data_map.get("PROD_ID")));
							rptMap.put("PROD_NAME", getString(data_map.get("PROD_NAME")));
							rptMap.put("PROD_RISK_LV", getString(data_map.get("PROD_RISK_LV")));
							rptMap.put("STOCK_CODE", getString(data_map.get("COUNTRY_CODE")));
							rptMap.put("TOT_AMT", getString(data_map.get("TOT_AMT")));
							rptMap.put("UNIT_NUM", getString(data_map.get("UNIT_NUM")));
							buyList.add(rptMap);
						}else if(StringUtils.equals(data_map.get("TRADE_SUB_TYPE").toString(),"S")){
							rptMap.put("CREDIT_ACCT", getString(data_map.get("CREDIT_ACCT")));
							rptMap.put("DUE_DATE", getString(data_map.get("DUE_DATE_NEW")));
							if ("市價".equals(getString(data_map.get("ENTRUST_TYPE_DESC")).trim())) {
								rptMap.put("ENTRUST_AMT", getString(data_map.get("ENTRUST_TYPE_DESC")));
							} else {
								rptMap.put("ENTRUST_AMT", getString(data_map.get("ENTRUST_TYPE_DESC"))+getString(data_map.get("ENTRUST_AMT")));
							}
							
							rptMap.put("FEE_RATE", getString(data_map.get("FEE_RATE"))+" %");
							rptMap.put("PROD_CURR", getString(data_map.get("PROD_CURR")));
							rptMap.put("PROD_ID", getString(data_map.get("PROD_ID")));
							rptMap.put("PROD_NAME", getString(data_map.get("PROD_NAME")));
							rptMap.put("STOCK_CODE", getString(data_map.get("COUNTRY_CODE")));
							rptMap.put("UNIT_NUM", getString(data_map.get("UNIT_NUM")));
							sellList.add(rptMap);
						}
						
				}

			for(int j = buyList.size(); j < 2 ; j++){
				HashMap<String, Object> addMap = new HashMap<String, Object>();
				addMap.put("CREDIT_ACCT", "");
				addMap.put("DEBIT_ACCT", "");
				addMap.put("DUE_DATE", "");
				addMap.put("ENTRUST_AMT", "");
				addMap.put("FEE_RATE", "");
				addMap.put("PROD_CURR", "");
				addMap.put("PROD_ID", "");
				addMap.put("PROD_NAME", "");
				addMap.put("PROD_RISK_LV", "");
				addMap.put("STOCK_CODE", "");
				addMap.put("TOT_AMT", "");
				addMap.put("UNIT_NUM", "");
				buyList.add(addMap);
				
			}
			for(int j = sellList.size(); j < 2 ; j++){
				HashMap<String, Object> addMap = new HashMap<String, Object>();
				addMap.put("CREDIT_ACCT", "");
				addMap.put("DEBIT_ACCT", "");
				addMap.put("DUE_DATE", "");
				addMap.put("ENTRUST_AMT", "");
				addMap.put("FEE_RATE", "");
				addMap.put("PROD_CURR", "");
				addMap.put("PROD_ID", "");
				addMap.put("PROD_NAME", "");
				addMap.put("PROD_RISK_LV", "");
				addMap.put("STOCK_CODE", "");
				addMap.put("TOT_AMT", "");
				addMap.put("UNIT_NUM", "");
				sellList.add(addMap);
				
			}
			
		
		
				data.addRecordList("BUY", buyList);
				data.addRecordList("SELL", sellList);
				
	
				String custID = data_list.get(0).get("CUST_ID").toString();
				SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		        SOT712InputVO  sot712InputVO = new SOT712InputVO();
		        sot712InputVO.setCustID(custID);
				if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
					data.addParameter("ADULT", "N");
				}
			
				String getMonth = "";
				String getDate = "";
				java.sql.Timestamp ts = (java.sql.Timestamp) data_list.get(0).get("MLASTUPDATE");
				getMonth = (ts.getMonth()+1)+"";
				getDate = ts.getDate()+"";
				if (getMonth.length() < 2){
					getMonth = "0"+getMonth;
				}
				if (getDate.length() < 2){
					getDate = "0"+getDate;
				}
				data.addParameter("NARRATOR_ID",data_list.get(0).get("NARRATOR_ID").toString());
				data.addParameter("NARRATOR_NAME",data_list.get(0).get("NARRATOR_NAME").toString());
				if (StringUtils.isNotBlank(getString(data_list.get(0).get("REC_SEQ")))) {
					data.addParameter("REC_SEQ","  錄音序號："+data_list.get(0).get("REC_SEQ").toString());
				}
				data.addParameter("REPORT_DATE", "中華民國  "+ (ts.getYear()-11) + "  年  " + getMonth + "  月  " + getDate +"  日"); 
				data.addParameter("REPORT_TIME", "時間： "+ts.getHours()+"  時  "+ts.getMinutes()+"  分  ");
				data.addParameter("PageFoot","第一聯:受理單位留存聯");
				report = gen.generateReport(txnCode, reportID, data);
				url = report.getLocation();
				url_list.add(url);
				data.addParameter("PageFoot","第二聯:客戶收執聯");
				report = gen.generateReport(txnCode, reportID, data);
				url = report.getLocation();
				url_list.add(url);
			}

			return url_list;
//		}catch(Exception e){
//			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
	}
	
	private String getString(Object val){
		if(val != null){
			return val.toString();
		}else{
			return "";
		}
	}
}