package com.systex.jbranch.fubon.bth;

import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

@Component("btfpsperformance")
public class BTFPSPerformance extends BizLogic {

	public static void main(String[] args) {
//		String[] aa = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
//		String[] bb = (String[]) Arrays.copyOfRange(aa, 0, 5);
//		System.out.println(bb.toString());
	}
	
	/**
	 * 主要排程執行 Method
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public void excute() throws DAOException, JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		// 取得所有的 HEAD 資料
		List<Map<String, Object>> allHeadList = getAllHeadList(dam);
		
		for(Map<String, Object> headMap : allHeadList) {
			String custId = ObjectUtils.toString(headMap.get("CUST_ID"));
			String planId = ObjectUtils.toString(headMap.get("PLAN_ID"));
			Map<String, Object> calDataResourceMap = getCalDataResourceMap(dam, custId, planId);
			if(MapUtils.isNotEmpty(calDataResourceMap)) {
				BigDecimal marketValue = getMarketValue(dam, custId, planId);
				Map<String, BigDecimal> finalValueMap = doCalValue(dam, calDataResourceMap, custId, planId, marketValue);
				if(MapUtils.isNotEmpty(finalValueMap)) {
					doExcuteSQL(dam, finalValueMap, planId);
				}
			}
		}
	}
	
	/**
	 * 取得所有有規劃的資料
	 * @param dam
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getAllHeadList(DataAccessManager dam) throws DAOException, JBranchException {
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List<Map<String, Object>> allHeadList = new ArrayList<Map<String, Object>>();
	    qc.setQueryString(allHeadDataSQL());
	    allHeadList = dam.exeQuery(qc);
		return allHeadList;
	}
	
	/**
	 * 取得要計算的資料
	 * @param dam
	 * @param custId
	 * @param planId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, Object> getCalDataResourceMap(DataAccessManager dam, String custId, String planId) throws DAOException, JBranchException {
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		Map<String, Object> calDataResourceMap = new HashMap<String, Object>();
		qc.setObject("custId", custId);
		qc.setObject("planId", planId);
	    qc.setQueryString(oneDataSQL());
	    List<Map<String, Object>> tempList = dam.exeQuery(qc);
	    calDataResourceMap = CollectionUtils.isNotEmpty(tempList) ? tempList.get(0): null; 
		return calDataResourceMap;
	}
	
	/**
	 * 取得市值含息
	 * @param dam
	 * @param custId
	 * @param planId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private BigDecimal getMarketValue(DataAccessManager dam, String custId, String planId) throws DAOException, JBranchException {
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setObject("custId", custId);
		qc.setObject("planId", planId);
	    qc.setQueryString(marketValueSQL());
	    List<Map<String, Object>> tempList = dam.exeQuery(qc);
	    if(CollectionUtils.isNotEmpty(tempList) && MapUtils.isNotEmpty(tempList.get(0))) {
	    	return tempList.get(0).get("MARKET_VALUE") != null ? (BigDecimal) tempList.get(0).get("MARKET_VALUE") : BigDecimal.ZERO;
	    } else {
	    	return BigDecimal.ZERO;
	    }
	    
	}
	
	/**
	 * 計算 達成率, 報酬率, 應達目標
	 * @param dam
	 * @param calDataResourceMap
	 * @param custId
	 * @param planId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, BigDecimal> doCalValue(DataAccessManager dam, Map<String, Object> calDataResourceMap, String custId, String planId, BigDecimal marketValue) throws DAOException, JBranchException {
		Map<String, BigDecimal> finalValueMap = new HashMap<String, BigDecimal>();
		GenericMap resultGMap = new GenericMap(calDataResourceMap);
        
		finalValueMap.put("RETURN_RATE", FPSUtils.getInterestReturnRate(
				marketValue, resultGMap.getBigDecimal("INV_AMT_CURRENT"))
		); // 報酬率重算
        
        int targetYear = FPSUtils.getInvestmentYear(dam, resultGMap.getNotNullStr("PLAN_ID")); // 目標年期
        
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(resultGMap.getDate("CREATETIME"));
        targetDate.add(Calendar.YEAR, targetYear);

        if(targetDate.before(new Date())) {
        	return null;
        }
        
        // 未來剩餘月份
        int futureMonth = FPSUtils.getRemainingMonth(resultGMap.getDate("CREATETIME"), targetYear);
        
        // 已購入
        BigDecimal purchaseFutureValue = FPSUtils.getPurchaseFutureValue(dam, custId, FPSUtils.getCertificateIdList(dam, planId), futureMonth);
        
        // 未來投入金額 (未購入 + 已購入)
        BigDecimal futureQuota = FPSUtils.getFutureQuota(FPSUtils.getHistory(dam, planId, custId, "N"),futureMonth, purchaseFutureValue);

        finalValueMap.put("AMT_TARGET", FPSUtils.getAchievementTarget(resultGMap.getDate("CREATETIME"), 
        		resultGMap.getBigDecimal("INV_AMT_TARGET"), resultGMap.getBigDecimal("INV_AMT_CURRENT"), futureQuota, targetYear)
        ); // 應達目標重算
        
        finalValueMap.put("HIT_RATE", FPSUtils.getAchievementRate(
        		marketValue, new GenericMap(finalValueMap).getBigDecimal("AMT_TARGET"))
        ); // 達成率重算
        
        finalValueMap.put("MARKET_VALUE", marketValue); // 市值含息
        
		return finalValueMap;
	}

	/**
	 * 主要 updateSQL 執行
	 * @param dam
	 * @param finalValueMap
	 * @param planId
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private void doExcuteSQL(DataAccessManager dam, Map<String, BigDecimal> finalValueMap, String planId) throws DAOException, JBranchException {
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		for(Entry<String, BigDecimal> entry : finalValueMap.entrySet()) {
			qc.setObject(entry.getKey(), entry.getValue());
		}
		qc.setObject("planId", planId);
	    qc.setQueryString(updateSQL());
	    dam.exeUpdate(qc);
	}
	
	/**
	 * 所有規劃資料清單
	 * @return
	 */
	private String allHeadDataSQL() {
		StringBuffer allHeadDataSQL = new StringBuffer();
		allHeadDataSQL.append(" select PLAN_ID, CUST_ID from TBFPS_SPP_PRD_RETURN_HEAD ");
//		allHeadDataSQL.append(" where plan_id = 'SPP2018051600096' "); 測試用
		return allHeadDataSQL.toString();
	}
	
	/**
	 * 單一一個人的規劃資料
	 * @return
	 */
	private String oneDataSQL() {
		StringBuffer oneDataSQL = new StringBuffer();
		oneDataSQL.append(" WITH BASE AS ( "); 
		oneDataSQL.append(" SELECT DE.SPP_ACHIVE_RATE_1, DE.SPP_ACHIVE_RATE_2 FROM TBFPS_OTHER_PARA_HEAD HE "); 
		oneDataSQL.append(" INNER JOIN TBFPS_OTHER_PARA DE ON HE.PARAM_NO = DE.PARAM_NO WHERE HE.STATUS ='A' "); 
		oneDataSQL.append(" FETCH FIRST 1 ROWS ONLY ), "); 
		oneDataSQL.append(" TPPS AS ( ");
		oneDataSQL.append(" SELECT PLAN_ID, (POLICY_NO ||'-'|| TRIM(TO_CHAR(POLICY_SEQ  ,'00')) || CASE WHEN ID_DUP <> ' ' THEN '-' || ID_DUP END ) AS CERT_NBR "); 
		oneDataSQL.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID IN (SELECT PLAN_ID FROM TBFPS_SPP_PRD_RETURN_HEAD WHERE CUST_ID = :custId) AND POLICY_NO IS NOT NULL "); 
		oneDataSQL.append(" UNION "); 
		oneDataSQL.append(" SELECT PLAN_ID, CERTIFICATE_ID AS CERT_NBR FROM TBFPS_PLANID_MAPPING WHERE PLAN_ID IN (SELECT PLAN_ID FROM TBFPS_SPP_PRD_RETURN_HEAD WHERE CUST_ID = :custId)), "); 
		oneDataSQL.append(" VAAD AS ( "); 
		oneDataSQL.append(" SELECT TPPS.PLAN_ID AS PLAN_ID, VAAD.INV_AMT_TWD FROM MVFPS_AST_ALLPRD_DETAIL VAAD "); 
		oneDataSQL.append(" LEFT JOIN TPPS ON VAAD.CERT_NBR = TPPS.CERT_NBR WHERE CUST_ID = :custId AND AST_TYPE IN ('07', '08', '09', '14')) "); 
		oneDataSQL.append(" SELECT "); 
		oneDataSQL.append(" RS.TRACE_V_FLAG AS TRACE_V_FLAG, RS.REVIEW_V_FLAG AS REVIEW_V_FLAG, "); 
		oneDataSQL.append(" RS.TRACE_P_FLAG AS TRACE_P_FLAG, RS.REVIEW_P_FLAG AS REVIEW_P_FLAG, "); 
		oneDataSQL.append(" CASE WHEN RS.HIT_RATE < (SELECT SPP_ACHIVE_RATE_1 FROM BASE) THEN '落後' WHEN RS.HIT_RATE >= (SELECT SPP_ACHIVE_RATE_2 FROM BASE) THEN '符合進度' ELSE '微幅落後' END AS HIT_RATE_DESC, "); 
		oneDataSQL.append(" CASE WHEN RS.HIT_RATE < (SELECT SPP_ACHIVE_RATE_1 FROM BASE) THEN '-1' WHEN RS.HIT_RATE >= (SELECT SPP_ACHIVE_RATE_2 FROM BASE) THEN '1' ELSE '0' END AS HIT_RATE_FLAG, "); 
		oneDataSQL.append(" PS.INV_PLAN_NAME, PS.INV_AMT_TARGET, (SELECT SUM(NVL(INV_AMT_TWD, 0)) FROM VAAD WHERE PLAN_ID = RS.PLAN_ID) AS INV_AMT_CURRENT, "); 
		oneDataSQL.append(" RS.MARKET_VALUE, RS.RETURN_RATE, RS.AMT_TARGET, RS.HIT_RATE, RS.PLAN_ID, PS.RISK_ATTR, PS.SPP_TYPE, RS.CREATETIME "); 
		oneDataSQL.append(" from TBFPS_SPP_PRD_RETURN_HEAD RS "); 
		oneDataSQL.append(" left join TBFPS_PORTFOLIO_PLAN_SPP_HEAD PS ON RS.PLAN_ID = PS.PLAN_ID "); 
		oneDataSQL.append(" where RS.CUST_ID = :custId AND RS.PLAN_ID = :planId ");
		return oneDataSQL.toString();
	}
	
	/**
	 * 取得市值(含息)
	 * @return
	 */
	private String marketValueSQL() {
		StringBuffer marketValueSQL = new StringBuffer();
		marketValueSQL.append(" select sum(NOW_AMT_TWD + T_DIV_TWD) market_value from MVFPS_AST_ALLPRD_DETAIL ");
		marketValueSQL.append(" where cust_id = :custId and CERT_NBR in (select CERTIFICATE_ID from TBFPS_PLANID_MAPPING where PLAN_ID = :planId) ");
		return marketValueSQL.toString();
	}
	
	
	
	/**
	 * 進行 update 的 SQL
	 * @return
	 */
	private String updateSQL() {
		StringBuffer updateSQL = new StringBuffer();
		updateSQL.append(" update TBFPS_SPP_PRD_RETURN_HEAD "); 
		updateSQL.append(" set RETURN_RATE = :RETURN_RATE, AMT_TARGET = :AMT_TARGET, HIT_RATE = :HIT_RATE, MARKET_VALUE = :MARKET_VALUE ");
		updateSQL.append(" where PLAN_ID = :planId ");
		return updateSQL.toString();
	}
}
