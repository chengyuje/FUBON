package com.systex.jbranch.fubon.jlb;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("cam997")
@Scope("prototype")
public class CAM997 extends BizLogic {
	
	private Logger logger = LoggerFactory.getLogger(CAM997.class);
	
	//取得可分派總數
	public Map<String, Integer> getAssignDtl(DataAccessManager dam, List<Map<String, Object>> custList, String startDate, String endDate) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : custList) {
			sb = new StringBuffer();
			sb.append("INSERT INTO TBCAM_CUST_TEMP (CUST_ID) VALUES (:custID)");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("custID", (String) map.get("CUST_ID"));
			dam.exeUpdate(queryCondition);
		}
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT BASE.AO_CODE, (SUM(NVL(LIMITS, 0))-NVL((SELECT COUNT(DISTINCT LEADS.CUST_ID) AS LEADS_COUNTS ");
		sb.append("FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("WHERE LEADS.LEAD_STATUS IN ('01', '02', '03') ");
		sb.append("AND NVL(LEADS.LEAD_TYPE, '00') <> '04' ");
		sb.append("AND TO_CHAR(EXPECTED_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		sb.append("AND TO_CHAR(EXPECTED_DATE, 'yyyyMMdd') BETWEEN :startDate AND :endDate ");
		sb.append("AND LEADS.AO_CODE = BASE.AO_CODE ");
		sb.append("GROUP BY LEADS.AO_CODE), 0)) AS CAN_DISPATCH, ");
		sb.append("CUST.COUNT_AO AS CUSTS_DISPATCH ");
		sb.append("FROM ");
		sb.append("( SELECT AO_CODE, TBSYS.PARAM_NAME AS LIMITS ");
		sb.append("FROM (SELECT TO_CHAR(TO_DATE(:startDate, 'yyyyMMdd') + (level - 1), 'yyyyMMdd') AS WDATE ");
		sb.append("FROM DUAL ");
		sb.append("CONNECT BY TRUNC(TO_DATE(:startDate, 'yyyyMMdd')) + level - 1 <= TRUNC(TO_DATE(:endDate, 'yyyyMMdd')) ");
		sb.append("MINUS ");
		sb.append("SELECT TO_CHAR(HOL_DATE, 'yyyyMMdd') AS WDATE ");
		sb.append("FROM TBBTH_HOLIDAY ");
		sb.append(") WORK_DAYS, ");
		sb.append("VWORG_BRANCH_EMP_DETAIL_INFO EMP ");
		sb.append("LEFT JOIN TBSYSPARAMETER TBSYS ON TBSYS.PARAM_CODE = EMP.ROLE_NAME AND TBSYS.PARAM_TYPE = 'CAM.MAX_CONTACT' ");
		sb.append("WHERE AO_CODE IS NOT NULL ");
		sb.append("ORDER BY AO_CODE ");
		sb.append(") BASE ");
		// 需分派的客戶
		sb.append("LEFT JOIN ( SELECT CUST.AO_CODE, NVL(COUNT(CUST.AO_CODE), 0) AS COUNT_AO ");
		sb.append("FROM TBCRM_CUST_MAST CUST ");
		sb.append("WHERE EXISTS (SELECT TEMP.CUST_ID FROM TBCAM_CUST_TEMP TEMP WHERE CUST.CUST_ID = TEMP.CUST_ID)");
		sb.append("GROUP BY CUST.AO_CODE) CUST ON BASE.AO_CODE = CUST.AO_CODE ");
		sb.append("WHERE COUNT_AO <> 0 ");
		sb.append("GROUP BY BASE.AO_CODE, CUST.COUNT_AO ");

		queryCondition.setObject("startDate", StringUtils.isNotBlank(startDate) ? startDate : (String) custList.get(0).get("START_DATE"));
		queryCondition.setObject("endDate", StringUtils.isNotBlank(startDate) ? endDate : (String) custList.get(0).get("END_DATE"));

		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		BigDecimal successNum = new BigDecimal(0);
		BigDecimal failureNum = new BigDecimal(0);
		for (Map<String, Object> map : list) {
			BigDecimal canDispatch = (BigDecimal) map.get("CAN_DISPATCH");
			BigDecimal custDispatch = (BigDecimal) map.get("CUSTS_DISPATCH");
			
			if (canDispatch.compareTo(custDispatch) >= 0) {
				successNum = successNum.add(custDispatch);
			} else if (canDispatch.compareTo(custDispatch) < 0) {
//				failureNum = failureNum.add(custDispatch.subtract(canDispatch));
				successNum = successNum.add(custDispatch);
			}
		}
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("totalNum", custList.size());
		map.put("successNum", successNum.intValue());
		map.put("failureNum", (custList.size() - successNum.intValue()));
		
		sb.setLength(0);
		
		return map;
	}

	// 取得可分派日期列表
	public List<Map<String, Object>> getDateList(DataAccessManager dam, String custID, String aoCode, String startDate, String endDate, String upperLimit) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT TO_CHAR(SYSDATE + (level - 1), 'yyyyMMdd') AS WDATE ");
		sb.append("FROM DUAL ");
		sb.append("CONNECT BY TRUNC(SYSDATE) + level - 1 <= TRUNC(TO_DATE(:endDate, 'yyyyMMdd')) ");
		
		sb.append("MINUS ");
		
		sb.append("SELECT TO_CHAR(HOL_DATE, 'yyyyMMdd') AS WDATE ");
		sb.append("FROM TBBTH_HOLIDAY ");
		sb.append("WHERE TRUNC(HOL_DATE) >= TRUNC(SYSDATE) ");
		
		sb.append("MINUS "); //--差集 已分派額滿區間 
		
		sb.append("SELECT TO_CHAR(EXPECTED_DATE, 'yyyyMMdd') AS eDATE ");
		sb.append("FROM TBCAM_SFA_LEADS ");
		sb.append("WHERE LEAD_STATUS IN ('01', '02', '03') ");
		sb.append("AND NVL(LEAD_TYPE, '00') <> '04' ");
		sb.append("AND TO_CHAR(EXPECTED_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		sb.append("AND TO_CHAR(EXPECTED_DATE, 'yyyyMMdd') BETWEEN :startDate AND :endDate ");
		sb.append("AND AO_CODE = :aoCode ");
		sb.append("GROUP BY AO_CODE, TO_CHAR(EXPECTED_DATE, 'yyyyMMdd') ");
		sb.append("HAVING COUNT(DISTINCT CUST_ID) = :limit ");
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer agentSQL = new StringBuffer();
		agentSQL.append("SELECT TO_CHAR(START_DATE, 'yyyyMMdd') AS START_DATE, TO_CHAR(END_DATE, 'yyyyMMdd') AS END_DATE ");
		agentSQL.append("FROM TBORG_AGENT ");
		agentSQL.append("WHERE EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE ");
		agentSQL.append("WHERE AO_CODE = :aoCode) AND AGENT_STATUS IN ('U', 'S') ");
		agentSQL.append("AND TO_CHAR(END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		agentSQL.append("ORDER BY START_DATE ");
		queryCondition.setQueryString(agentSQL.toString());
		queryCondition.setObject("aoCode", aoCode);
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		logger.info("*****取得可分派日期列表*****");
		for (int i = 0; i < list.size(); i++) {
			sb.append("MINUS "); //--差集 請假區間 
			sb.append("SELECT TO_CHAR(TO_DATE(:agentStartDate").append(i).append(", 'yyyyMMdd') + (level - 1), 'yyyyMMdd') AS WDATE  ");
			sb.append("FROM DUAL ");
			sb.append("CONNECT BY TRUNC(TO_DATE(:agentStartDate").append(i).append(", 'yyyyMMdd')) + level - 1 <= TRUNC(TO_DATE(:agentEndDate").append(i).append(", 'yyyyMMdd')) ");
			queryCondition.setObject("agentStartDate" + i, (String) list.get(i).get("START_DATE"));
			queryCondition.setObject("agentEndDate" + i, (String) list.get(i).get("END_DATE"));
			logger.info("agentStartDate" + i + ": " + (String) list.get(i).get("START_DATE"));
			logger.info("agentEndDate" + i + ": " + (String) list.get(i).get("END_DATE"));
		}
		queryCondition.setQueryString(sb.toString());
		
		
		logger.info("aoCode: " + aoCode + "/ " + 
					"startDate: " + startDate + "/ " + 
					"endDate: " + endDate + "/ " + 
					"limit: " + upperLimit);
		logger.info("**********************");
		
		queryCondition.setObject("aoCode", aoCode);
		queryCondition.setObject("startDate", startDate);
		queryCondition.setObject("endDate", endDate);
		queryCondition.setObject("limit", upperLimit);
		
		sb.setLength(0);
		
		return dam.exeQuery(queryCondition);
	}
	
}
