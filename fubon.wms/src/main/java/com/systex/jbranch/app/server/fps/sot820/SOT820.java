package com.systex.jbranch.app.server.fps.sot820;

import com.systex.jbranch.app.server.fps.sot701.ContractVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp6a.NMVP6AOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp8a.NMVP8AOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp8a.NMVP8AOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.XmlInfo;

import java.math.BigDecimal;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * R1: 應附清單應檢附文件 / R2: 信託財產管理運用指示書
 **/
@Component("sot820")
@Scope("request")
public class SOT820 extends SotPdf {
    private final String TXN_CODE = "SOT820";    
    private DataAccessManager dam = null;

    @Override
    public List<String> printReport() throws Exception {
        PRDFitInputVO inputVO = getInputVO();
        List<String> urlList = new ArrayList<String>();
        dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
        
        //先取得所有批號
		String tableName = "";
		if (inputVO.getPrdType().equals("1") && (inputVO.getTradeType() == 1 || inputVO.getTradeType() == 5)) {
			//基金申購
			tableName = "TBSOT_NF_PURCHASE_D";
		} else if (inputVO.getPrdType().equals("1") && (inputVO.getTradeType() == 2)) {
			//基金贖回
			tableName = "TBSOT_NF_REDEEM_D";
		} else if (inputVO.getPrdType().equals("2")){
			//ETF申購贖回
			tableName = "TBSOT_ETF_TRADE_D";
		} else if (inputVO.getPrdType().equals("3")){
			//海外債申購贖回
			tableName = "TBSOT_BN_TRADE_D";
		} else if (inputVO.getPrdType().equals("5")){
			//SN申購贖回
			tableName = "TBSOT_SN_TRADE_D";
		}
        sql.append("select BATCH_SEQ,count(1) as BATCH_COUNT from " + tableName + " ");
  		sql.append(" where TRADE_SEQ = :trade_seq ");
  		
  		if (StringUtils.isNotEmpty(inputVO.getBatchSeq())) {
  			sql.append("and BATCH_SEQ = :batch_seq ");
  		}
  		sql.append("group by BATCH_SEQ order by BATCH_SEQ ");

  		queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
  		if (StringUtils.isNotEmpty(inputVO.getBatchSeq())) {
  			queryCondition.setObject("batch_seq", inputVO.getBatchSeq());
  		}
  		
  		queryCondition.setQueryString(sql.toString());
  		List<Map<String, Object>> list = dam.exeQuery(queryCondition);		
              
      	//列印表單
        if(CollectionUtils.isEmpty(inputVO.getMonTrustRptList())) {
        	//信託財產管理運用指示書
        	urlList = printR1(inputVO, list);
        } else {
        	//應附清單應檢附文件
        	urlList = printR2(inputVO, list);
        }
        
        return urlList;
    }
    
  //應附清單應檢附文件
    private List<String> printR2(PRDFitInputVO inputVO, List<Map<String, Object>> batchList) throws Exception {
    	dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		List<String> url_list = new ArrayList<String>();
		String REPORT_ID = "R1";	//應附清單應檢附文件
				
		//依批號列印
		for(Map<String, Object> map :batchList) {
			ReportGeneratorIF generator = new ReportFactory().getGenerator();
	        ReportDataIF data = new ReportData();
	        
	        sql = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//
	        if(StringUtils.equals("1", inputVO.getPrdType())) {	//基金
	        	if(inputVO.getTradeType() == 1 || inputVO.getTradeType() == 5) {	//1:單筆申購//5:定期(不)定額申購
	        		sql.append("select M.CUST_ID, M.CUST_NAME, D.CONTRACT_ID ");
	        		sql.append(" from TBSOT_TRADE_MAIN M ");
	    			sql.append(" LEFT JOIN TBSOT_NF_PURCHASE_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
	    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        	} else if(inputVO.getTradeType() == 2) {	//贖回
	        		sql.append("select M.CUST_ID, M.CUST_NAME, D.CONTRACT_ID ");
	    			sql.append(" from TBSOT_TRADE_MAIN M ");
	    			sql.append(" LEFT JOIN TBSOT_NF_REDEEM_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
	    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        	}				
	        } else if(StringUtils.equals("2", inputVO.getPrdType())) {	//ETF
	        	sql.append("select M.CUST_ID, M.CUST_NAME, D.CONTRACT_ID ");
    			sql.append(" from TBSOT_TRADE_MAIN M ");
    			sql.append(" LEFT JOIN TBSOT_ETF_TRADE_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        } else if(StringUtils.equals("3", inputVO.getPrdType())) {	//海外債
	        	sql.append("select M.CUST_ID, M.CUST_NAME, D.CONTRACT_ID ");
    			sql.append(" from TBSOT_TRADE_MAIN M ");
    			sql.append(" LEFT JOIN TBSOT_BN_TRADE_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        } else if(StringUtils.equals("5", inputVO.getPrdType())) {	//SN
	        	sql.append("select M.CUST_ID, M.CUST_NAME, D.CONTRACT_ID ");
    			sql.append(" from TBSOT_TRADE_MAIN M ");
    			sql.append(" LEFT JOIN TBSOT_SN_TRADE_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        }
	        
	        queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
			queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> data_list1 = dam.exeQuery(queryCondition);
			
			//申購交易，取得信託財產管理運用有權指示人，有權人年齡超過65歲(含)需增加「高齡客戶資訊觀察表」
			if((StringUtils.equals("1", inputVO.getPrdType()) && (inputVO.getTradeType() == 1 || inputVO.getTradeType() == 5)) || 
					(inputVO.getPrdType().matches("2|3|5") && inputVO.getTradeSubType() == 1 )) {
				Map<String, Object> relMap = getRelContact(data_list1.get(0).get("CONTRACT_ID").toString());
				if(StringUtils.isNotBlank(relMap.get("AGEOVER65_RMK").toString())) {
					inputVO.getMonTrustRptList().add("G"); //高齡客戶資訊觀察表
				}
			}
			
			sql = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);	
			//由ReportID取得報表名稱
			sql.append("SELECT PARAM_CODE, PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.REPORT_LIST' ORDER BY PARAM_ORDER ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> paramList = dam.exeQuery(queryCondition);
			
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	        for(Map<String, Object> param : paramList) {
	        	if(inputVO.getMonTrustRptList().contains(param.get("PARAM_CODE").toString())) {
	        		//取得有列印的報表名稱
	        		dataList.add(param);
	        	}
	        }
				 
	        data.addParameter("P1_CONTRACT_ID", data_list1.get(0).get("CONTRACT_ID").toString());
	        data.addParameter("P1_CUST_ID", data_list1.get(0).get("CUST_ID").toString());
			data.addParameter("P1_CUST_NAME", data_list1.get(0).get("CUST_NAME").toString());
			data.addParameter("P1_BATCH_NO", (String)map.get("BATCH_SEQ"));
			data.addParameter("TOTAL_REC", Integer.toString(dataList.size()));
			
			data.addRecordList("DATALIST", dataList);
			
	        url_list.add(generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation());
		}
        
        return url_list;
    }
    
    //信託財產管理運用指示書
    @SuppressWarnings("unchecked")
	private List<String> printR1(PRDFitInputVO inputVO, List<Map<String, Object>> batchList) throws Exception {
    	dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		List<String> url_list = new ArrayList<String>();
		String REPORT_ID = "R2";	//信託財產管理運用指示書
		
		//依批號列印
		for(Map<String, Object> map :batchList) {
			ReportGeneratorIF generator = new ReportFactory().getGenerator();
	        ReportDataIF data = new ReportData();
			sql = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//
	        if(StringUtils.equals("1", inputVO.getPrdType())) {	//基金
	        	data.addParameter("PROD_TYPE", "■ 基金    □ 海外債    □ SN    □ ETF/海外股票");
	        	
	        	if(inputVO.getTradeType() == 1 || inputVO.getTradeType() == 5) {	//1:單筆申購//5:定期(不)定額申購
	        		REPORT_ID = "R21";	//申購
	        		data.addParameter("AMT_LABEL", "金額");
	        		
	        		if(inputVO.getTradeType() == 1) {	//1:單筆申購
	        			data.addParameter("TRADE_TYPE", "■ 申購(單筆)    □ 贖回");
	        		} else if(inputVO.getTradeType() == 5) {	//5:定期(不)定額申購
	        			data.addParameter("TRADE_TYPE", "■ 申購(定期定額)    □ 贖回");
	        		}
	        		
	        		sql.append("select M.CUST_ID, M.CUST_NAME, ");
	        		sql.append(" D.PROD_ID, D.PROD_NAME, D.TRUST_CURR AS PROD_CURR, D.PURCHASE_AMT, D.PURCHASE_AMT_L, D.PURCHASE_AMT_M, D.PURCHASE_AMT_H, D.CONTRACT_ID, D.BATCH_SEQ, ");
	        		sql.append(" '' AS CERTIFICATE_ID, '' AS UNIT_NUM, '' AS REDEEM_TYPE, (CASE WHEN NVL(D.TRUST_PEOP_NUM, 'N') = 'N' THEN ' ' ELSE '* ' END) AS TRUST_PEOP_NUM "); 
	        		sql.append(" from TBSOT_TRADE_MAIN M ");
	    			sql.append(" LEFT JOIN TBSOT_NF_PURCHASE_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
	    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        	} else if(inputVO.getTradeType() == 2) {	//贖回
	        		REPORT_ID = "R22";	//贖回
	        		data.addParameter("AMT_LABEL", "贖回單位數");
	        		data.addParameter("TRADE_TYPE", "□ 申購    ■ 贖回");
	        		//
	        		sql.append("select M.CUST_ID, M.CUST_NAME, ");
	        		sql.append(" D.RDM_PROD_ID AS PROD_ID, D.RDM_PROD_NAME AS PROD_NAME, D.RDM_PROD_CURR AS PROD_CURR, D.RDM_UNIT AS PURCHASE_AMT, D.CONTRACT_ID, D.BATCH_SEQ, ");
	        		sql.append(" D.RDM_CERTIFICATE_ID AS CERTIFICATE_ID, to_char(D.UNIT_NUM,'FM9999999999999990.0000') as UNIT_NUM, (CASE WHEN D.REDEEM_TYPE = '1' THEN '全部' ELSE '部分' END) AS REDEEM_TYPE, ");
	        		sql.append(" (CASE WHEN NVL(D.TRUST_PEOP_NUM, 'N') = 'N' THEN ' ' ELSE '* ' END) AS TRUST_PEOP_NUM ");
	    			sql.append(" from TBSOT_TRADE_MAIN M ");
	    			sql.append(" LEFT JOIN TBSOT_NF_REDEEM_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
	    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        	}				
	        } else if(StringUtils.equals("2", inputVO.getPrdType())) {	//ETF/海外股票
	        	if(inputVO.getTradeSubType() == 1) {
	        		REPORT_ID = "R21";	//申購
	        		data.addParameter("TRADE_TYPE", "■ 申購    □ 贖回");
	        	} else {
	        		REPORT_ID = "R22";	//贖回
	        		data.addParameter("TRADE_TYPE", "□ 申購    ■ 贖回");
	        	}
	        	data.addParameter("PROD_TYPE", "□ 基金    □ 海外債    □ SN    ■ ETF/海外股票");
	        	data.addParameter("AMT_LABEL", "股數/手");
	        	
	        	sql.append("select M.CUST_ID, M.CUST_NAME, ");
        		sql.append(" D.PROD_ID, D.PROD_NAME, D.PROD_CURR, D.UNIT_NUM AS PURCHASE_AMT, D.CONTRACT_ID, ");
        		sql.append(" D.BATCH_SEQ, D.CERTIFICATE_ID, D.UNIT_NUM, ");
        		sql.append(" '' AS REDEEM_TYPE, (CASE WHEN NVL(D.TRUST_PEOP_NUM, 'N') = 'N' THEN ' ' ELSE '* ' END) AS TRUST_PEOP_NUM, ");
        		sql.append(" (CASE WHEN E.PRD_ID IS NULL THEN NVL(F.PI_BUY, 'N') ELSE NVL(E.PI_BUY, 'N') END) AS PI_BUY ");
    			sql.append(" from TBSOT_TRADE_MAIN M ");    			
    			sql.append(" LEFT JOIN TBSOT_ETF_TRADE_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
    			sql.append(" LEFT JOIN TBPRD_ETF E ON E.PRD_ID = D.PROD_ID ");
    			sql.append(" LEFT JOIN TBPRD_STOCK F ON F.PRD_ID = D.PROD_ID ");
    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        } else if(StringUtils.equals("3", inputVO.getPrdType())) {	//海外債
	        	if(inputVO.getTradeSubType() == 1) {
	        		REPORT_ID = "R21";	//申購
	        		data.addParameter("TRADE_TYPE", "■ 申購    □ 贖回");
	        	} else {
	        		REPORT_ID = "R22";	//贖回
	        		data.addParameter("TRADE_TYPE", "□ 申購    ■ 贖回");
	        	}
	        	data.addParameter("PROD_TYPE", "□ 基金    ■ 海外債    □ SN    □ ETF/海外股票");
	        	data.addParameter("AMT_LABEL", "面額");
	        	
	        	sql.append("select M.CUST_ID, M.CUST_NAME, ");
        		sql.append(" D.PROD_ID, D.PROD_NAME, D.PROD_CURR, D.PURCHASE_AMT, D.CONTRACT_ID, D.BATCH_SEQ, D.CERTIFICATE_ID, D.PURCHASE_AMT AS UNIT_NUM, ");
        		sql.append(" '全部' AS REDEEM_TYPE, (CASE WHEN NVL(D.TRUST_PEOP_NUM, 'N') = 'N' THEN ' ' ELSE '* ' END) AS TRUST_PEOP_NUM, ");
        		sql.append(" P.PI_BUY ");
    			sql.append(" from TBSOT_TRADE_MAIN M ");
    			sql.append(" LEFT JOIN TBSOT_BN_TRADE_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
    			sql.append(" LEFT JOIN TBPRD_BOND P ON P.PRD_ID = D.PROD_ID ");
    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");
	        } else if(StringUtils.equals("5", inputVO.getPrdType())) {	//SN
	        	if(inputVO.getTradeSubType() == 1) {
	        		REPORT_ID = "R21";	//申購
	        		data.addParameter("TRADE_TYPE", "■ 申購    □ 贖回");
	        	} else {
	        		REPORT_ID = "R22";	//贖回
	        		data.addParameter("TRADE_TYPE", "□ 申購    ■ 贖回");
	        	}
	        	data.addParameter("PROD_TYPE", "□ 基金    □ 海外債    ■ SN    □ ETF/海外股票");
	        	data.addParameter("AMT_LABEL", "面額");
	        	
	        	sql.append("select M.CUST_ID, M.CUST_NAME, ");
        		sql.append(" D.PROD_ID, D.PROD_NAME, D.PROD_CURR, D.PURCHASE_AMT, D.CONTRACT_ID, D.BATCH_SEQ, D.CERTIFICATE_ID, D.REDEEM_AMT AS UNIT_NUM, ");
        		sql.append(" (CASE WHEN D.REDEEM_TYPE = '1' THEN '全部' ELSE '部分' END) AS REDEEM_TYPE , (CASE WHEN NVL(D.TRUST_PEOP_NUM, 'N') = 'N' THEN ' ' ELSE '* ' END) AS TRUST_PEOP_NUM, ");
        		sql.append(" P.PI_BUY ");
        		sql.append(" from TBSOT_TRADE_MAIN M ");
    			sql.append(" LEFT JOIN TBSOT_SN_TRADE_D D ON M.TRADE_SEQ = D.TRADE_SEQ ");
    			sql.append(" LEFT JOIN TBPRD_SN P ON P.PRD_ID = D.PROD_ID ");
    			sql.append(" where M.TRADE_SEQ = :trade_seq AND D.BATCH_SEQ = :batch_seq ");     	
	        }
	        
	        queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
			queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> data_list = dam.exeQuery(queryCondition);
			
			data.addParameter("P1_BATCH_NO", data_list.get(0).get("BATCH_SEQ").toString());
			data.addParameter("P1_CUST_ID", data_list.get(0).get("CUST_ID").toString());
			data.addParameter("P1_CUST_NAME", data_list.get(0).get("CUST_NAME").toString() + data_list.get(0).get("TRUST_PEOP_NUM").toString());
			data.addParameter("P1_CONTRACT_ID", data_list.get(0).get("CONTRACT_ID").toString());
			
			//限專投申購商品
			data.addParameter("PI_BUY", "");
			if(StringUtils.equals("1", inputVO.getPrdType()) && (inputVO.getTradeType() == 1 || inputVO.getTradeType() == 5)) {	//基金申購
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT COUNT(1) AS PI_BUY_CNT FROM TBPRD_FUND F WHERE F.OBU_PRO = '1' ");
				sql.append(" AND F.PRD_ID IN (SELECT PROD_ID FROM TBSOT_NF_PURCHASE_D WHERE TRADE_SEQ = :trade_seq AND BATCH_SEQ = :batch_seq) ");
				queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
				queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> pList = dam.exeQuery(queryCondition);
				
				if(CollectionUtils.isNotEmpty(pList) && ((BigDecimal)pList.get(0).get("PI_BUY_CNT")).compareTo(BigDecimal.ZERO) > 0) {
					data.addParameter("PI_BUY", "(本次申購含專投商品)");
				}
			} else if(inputVO.getPrdType().matches("2|3|5") && inputVO.getTradeSubType() == 1 &&		//ETF/海外債/SN申購
					StringUtils.equals("Y", ObjectUtils.toString(data_list.get(0).get("PI_BUY")))) {	//限專投申購商品
				data.addParameter("PI_BUY", "(本次申購含專投商品)");
			}
			
			data.addParameter("PROD_TYPE_CODE", inputVO.getPrdType());
			data.addParameter("TRADE_TYPE_CODE", inputVO.getTradeType());
						
			data.addRecordList("DATALIST", data_list);
			data.addParameter("PageFoot1", "");
			
			//取得信託財產管理運用有權指示人
			Map<String, Object> relMap = getRelContact(data_list.get(0).get("CONTRACT_ID").toString());
			data.addParameter("P1_CON_TYPE", (String) relMap.get("TYPE")); //約定方式
			data.addRecordList("P1_REL_LIST", (List<Map<String, String>>) relMap.get("DATA")); //信託財產管理運用關係人
			data.addParameter("AGEOVER65_RMK", (String) relMap.get("AGEOVER65_RMK")); //關係人超過65歲註記
			
	        url_list.add(generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation());
		}
        
        return url_list;
    }
    
    /**
     * 依契約編號取得信託財產管理運用有權指示人
     * @param contractNo
     * @return：Map<key, value>
     * 			key="TYPE":約定方式	key="DATA":有權指示人List
     * @throws Exception
     */
  	private Map<String, Object> getRelContact(String contractNo) throws Exception {
  		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		NMVP8AOutputVO nmvp8DataVO = sot701.getNMVP8AData(contractNo);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		List<Map<String, String>> relList = new ArrayList<Map<String, String>>();
		
		String ageOver65Rmk = "";	
		//指示類型為"5.信託財產管理運用"
		if (null != nmvp8DataVO && CollectionUtils.isNotEmpty(nmvp8DataVO.getDetails()) && StringUtils.equals("5", nmvp8DataVO.getINS_TYPE())) {
			XmlInfo xmlInfo = new XmlInfo();
			rtnMap.put("TYPE", ObjectUtils.toString(xmlInfo.getVariable("CRM.MON_REL_INS_TYPE_5", nmvp8DataVO.getCON_TYPE(), "F3"))); //約定方式
			
			if(nmvp8DataVO.getDetails().size() == 1 && 
					(StringUtils.isBlank(nmvp8DataVO.getDetails().get(0).getREL_NAME()) || StringUtils.isBlank(nmvp8DataVO.getDetails().get(0).getREL_TYPE()))) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("REL_TYPE", "");	//信託關係
				map.put("REL_ID", "");		//關係人ID
				map.put("REL_NAME", "");	//關係人姓名
				map.put("INS_TYPE2", "");	//指示方式代碼
				map.put("INS_TYPE2_NAME", "");	//指示方式中文
				map.put("AGEOVER65", "");	//關係人是否超過65歲
				relList.add(map);
			} else {
				Collections.sort(nmvp8DataVO.getDetails()); //先按照指示方式排序，相同指示方式只需顯示第一個，之後的不用顯示
				String relType = "";
				String insType2 = "";				
				for (NMVP8AOutputDetailsVO detailVO : nmvp8DataVO.getDetails()) {
					Map<String, String> map = new HashMap<String, String>();
					
					map.put("REL_TYPE", ObjectUtils.toString(xmlInfo.getVariable("CRM.MON_REL_TYPE", detailVO.getREL_TYPE(), "F3")));	//信託關係中文
					map.put("REL_ID", detailVO.getREL_ID());		//關係人ID
					
					//#2269
					String relName = checkHotName(detailVO.getREL_ID()); 
					if (StringUtils.isNotBlank(relName)) {
						map.put("REL_NAME", relName);
					} else {
						map.put("REL_NAME", detailVO.getREL_NAME());
					}
					
					String ageover65 = "";
					if(nmvp8DataVO.getCON_TYPE().matches("1|2") && StringUtils.equals("1", detailVO.getREL_TYPE()) && isAgeOver65(detailVO.getREL_ID())) {
						ageover65 = "●"; 	//委託人單獨 或 委託人+全體信託監察人 => 判讀委託人年齡是否超過65歲
						ageOver65Rmk = "● 高齡客戶";
					} else if(StringUtils.equals("3", nmvp8DataVO.getCON_TYPE()) && StringUtils.equals("3", detailVO.getREL_TYPE()) && isAgeOver65(detailVO.getREL_ID())) {
						ageover65 = "●";	//特定信託監察人 => 判讀監察人年齡是否超過65歲
						ageOver65Rmk = "● 高齡客戶";
					} else if(StringUtils.equals("5", nmvp8DataVO.getCON_TYPE()) && StringUtils.equals("2", detailVO.getREL_TYPE()) && isAgeOver65(detailVO.getREL_ID())) {
						ageover65 = "●";	//受益人+全體信託監察人 => 判讀受益人年齡是否超過65歲
						ageOver65Rmk = "● 高齡客戶";
					} else if(StringUtils.equals("6", nmvp8DataVO.getCON_TYPE()) && StringUtils.equals("3", detailVO.getREL_TYPE()) && isAgeOver65(detailVO.getREL_ID())) {
						ageover65 = "●";	//全體信託監察人 => 判讀監察人年齡是否超過65歲
						ageOver65Rmk = "● 高齡客戶";
					}
					map.put("AGEOVER65", ageover65);	//關係人是否超過65歲
					
					
					if(!StringUtils.equals(relType, detailVO.getREL_TYPE())) {
						map.put("INS_TYPE2", detailVO.getINS_TYPE2());	//指示方式代碼
						map.put("INS_TYPE2_NAME", ObjectUtils.toString(xmlInfo.getVariable("CRM.MON_REL_INS_WAY", detailVO.getINS_TYPE2(), "F3")));	//指示方式中文
						
						relType = detailVO.getREL_TYPE();
						insType2 = detailVO.getINS_TYPE2();
					} else {
						if(!StringUtils.equals(insType2, detailVO.getINS_TYPE2())) {	//指示方式不同才顯示
							map.put("INS_TYPE2", detailVO.getINS_TYPE2());	//指示方式代碼
							map.put("INS_TYPE2_NAME", ObjectUtils.toString(xmlInfo.getVariable("CRM.MON_REL_INS_WAY", detailVO.getINS_TYPE2(), "F3")));	//指示方式中文
							
							insType2 = detailVO.getINS_TYPE2();
						} else {
							map.put("INS_TYPE2", "");	//指示方式代碼
							map.put("INS_TYPE2_NAME", "");	//指示方式中文
						}
					}
					
					relList.add(map);
				}
			}
		}
		
		rtnMap.put("DATA", relList);
		rtnMap.put("AGEOVER65_RMK", ageOver65Rmk);
		
		return rtnMap;		
  	}
  	
  	/*** 
  	 * 檢查關係人年是否超過65歲(含)
  	 * @param custId
  	 * @return
  	 * @throws Exception
  	 */
  	private Boolean isAgeOver65(String custId) throws Exception {
    	dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		queryCondition.setObject("custId", custId);
		queryCondition.setQueryString("SELECT CASE WHEN AGE >= 65 THEN 'Y' ELSE 'N' END AS OVER65_YN FROM TBCRM_CUST_MAST WHERE CUST_ID = :custId ");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return (CollectionUtils.isNotEmpty(list) && StringUtils.equals("Y", list.get(0).get("OVER65_YN").toString())) ? true : false;
  	}
  	
  	/*
  	 * #2269
  	 * AS400回傳的編碼是ANSI，回傳到報表難字會顯示不出來
  	 * REL_ID有出現在TBCRM_HOTNAME_FLAG裡面，
  	 * 取 => TBCRM_HOTNAME.NAME
  	 * 其餘使用400回傳
  	 */
  	private String checkHotName(String relID) throws Exception {
  		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT A.NAME FROM ");
		sb.append("TBCRM_HOTNAME A JOIN TBCRM_HOTNAME_FLAG B ");
		sb.append("ON A.ID = B.ID ");
		sb.append("WHERE A.ID = :relID");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("relID", relID);
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return list.size() > 0 ? list.get(0).get("NAME").toString() : "";
  	}
  	
}
