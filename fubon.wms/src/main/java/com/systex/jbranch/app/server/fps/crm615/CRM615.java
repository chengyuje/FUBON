package com.systex.jbranch.app.server.fps.crm615;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm615
 * 
 * @author moron
 * @date 2016/06/06
 * @spec null
 * 
 * Mark:2017/01/18 Modify by Stella
 */
@Component("crm615")
@Scope("request")
public class CRM615 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM615.class);
	
	public void getDate(Object body, IPrimitiveMap header) throws JBranchException {
		CRM615InputVO inputVO = (CRM615InputVO) body;
		CRM615OutputVO return_VO = new CRM615OutputVO();
		List<Map<String, Object>> list;
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		return_VO.setOneYAgoFee(getYearFee(inputVO.getCust_id(), 0, "OneY"));	//過去一年貢獻度 :months = -12  改成--> 今年至今 :months =  0
		return_VO.setTwoYAgoFee(getYearFee(inputVO.getCust_id(), -24, "TwoY")); //過去兩年貢獻度

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT PRFT_LAST_YEAR, PRFT_NEWEST_YEAR, PRCH_AMT_LAST_YEAR,DATA_PERIOD_LAST_YEAR, DATA_PERIOD_NEWEST_YEAR, ");
		sql.append(" PRFT_LAST_YEAR_NOTE, PRFT_NEWEST_YEAR_NOTE ");
		sql.append(" FROM TBCRM_CUST_CON_NOTE ");
		sql.append(" WHERE CUST_ID = :id ");
//		sql.append("and concat(DATA_YEAR,DATA_MONTH) = (SELECT MAX(concat(DATA_YEAR,DATA_MONTH)) FROM TBCRM_CUST_CON_NOTE where CUST_ID = :id) ");
		sql.append("ORDER BY DATA_YEAR DESC, DATA_MONTH DESC FETCH FIRST 1 ROWS ONLY ");	// Jemmy 2017-07-26, turning performance
		queryCondition.setObject("id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		list = dam.exeQuery(queryCondition);
		if(list.isEmpty()){
			return_VO.setLastYearFee(new java.math.BigDecimal("0"));
			return_VO.setThisYearFee(new java.math.BigDecimal("0"));
			return_VO.setThisYInsure(new java.math.BigDecimal("0"));
			return_VO.setPrftLastYear(new java.math.BigDecimal("0"));
			return_VO.setPrftNewestYear(new java.math.BigDecimal("0"));
			return_VO.setDATA_PERIOD_LAST_YEAR("0");
			return_VO.setDATA_PERIOD_NEWEST_YEAR("0");
			return_VO.setPRFT_LAST_YEAR_NOTE("0");
			return_VO.setPRFT_NEWEST_YEAR_NOTE("0");
		}else{
			return_VO.setLastYearFee((java.math.BigDecimal)list.get(0).get("PRFT_LAST_YEAR"));
			return_VO.setThisYearFee((java.math.BigDecimal)list.get(0).get("PRFT_NEWEST_YEAR"));
			return_VO.setThisYInsure((java.math.BigDecimal)list.get(0).get("PRCH_AMT_LAST_YEAR"));
			return_VO.setPrftLastYear((java.math.BigDecimal)list.get(0).get("PRFT_LAST_YEAR"));
			return_VO.setPrftNewestYear((java.math.BigDecimal)list.get(0).get("PRFT_NEWEST_YEAR"));
			return_VO.setDATA_PERIOD_LAST_YEAR(list.get(0).get("DATA_PERIOD_LAST_YEAR").toString());
			return_VO.setDATA_PERIOD_NEWEST_YEAR(list.get(0).get("DATA_PERIOD_NEWEST_YEAR").toString());
			return_VO.setPRFT_LAST_YEAR_NOTE(list.get(0).get("PRFT_LAST_YEAR_NOTE").toString() + "級客戶");
			return_VO.setPRFT_NEWEST_YEAR_NOTE(list.get(0).get("PRFT_NEWEST_YEAR_NOTE").toString() + "級客戶");
		}
		this.sendRtnObject(return_VO);
	}
	
	private BigDecimal getYearFee(String custId, int months, String period) throws JBranchException {
		BigDecimal fee = BigDecimal.ZERO;
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append(" with YEAR_FEE(CUST_ID, FEE) as ");
		sql.append(" (select CUST_ID, NVL(ACT_PRFT, 0) ");
		sql.append("     from TBCRM_CUST_PROFEE ");
		
		if(period.equals("OneY")){
			sql.append("	 where CUST_ID = :id and DATA_YEAR >= to_char(add_months(sysdate, :months),'yyyy') ");
		}else if (period.equals("TwoY")){
			sql.append("	 where CUST_ID = :id and DATA_YEAR = to_char(add_months(sysdate, :months),'yyyy') ");
		}
		sql.append("  union all ");
		sql.append("  select CUST_ID, (NVL(INTEST, 0) + NVL(INT_FEE, 0)) ");
		sql.append("     from TBCRM_CUST_PROFEE_SAV ");
		
		if(period.equals("OneY")){
			sql.append("	 where CUST_ID = :id and DATA_YEAR >= to_char(add_months(sysdate, :months),'yyyy') ");
		}else if (period.equals("TwoY")){
			sql.append("	 where CUST_ID = :id and DATA_YEAR = to_char(add_months(sysdate, :months),'yyyy') ");
		}
		  sql.append("     and BIG_BLOCK = 'A') ");
		sql.append("select sum(FEE) as FEE from YEAR_FEE group by CUST_ID ");
		queryCondition.setObject("id", custId);
		queryCondition.setObject("months", months);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(!CollectionUtils.isEmpty(list)) {
			fee = (BigDecimal)list.get(0).get("FEE");
		}
		
		return fee;
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM615InputVO inputVO = (CRM615InputVO) body;
		CRM615OutputVO return_VO = new CRM615OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DATE_TIME, SUM(INVAPRU) AS INVAPRU, SUM(DEPAPRU) AS DEPAPRU FROM( ");
		sql.append("SELECT (PROFEE.DATA_YEAR||PROFEE.DATA_MONTH) AS DATE_TIME, sum(PROFEE.ACT_PRFT)AS INVAPRU, 0 as DEPAPRU ");
		sql.append("FROM TBCRM_CUST_PROFEE PROFEE WHERE PROFEE.CUST_ID = :cust_id ");
		sql.append("AND PROFEE.DATA_YEAR||PROFEE.DATA_MONTH >= TO_CHAR(ADD_MONTHS(SYSDATE,-24),'YYYYMM') ");	//取過去24個月(2年)資料
		sql.append("GROUP BY PROFEE.DATA_YEAR, PROFEE.DATA_MONTH ");
		sql.append("UNION ");
		sql.append("SELECT (SAV.DATA_YEAR||SAV.DATA_MONTH) AS DATE_TIME, 0 AS INVAPRU, sum(SAV.INTEST + SAV.INT_FEE) AS DEPAPRU ");
		sql.append("FROM TBCRM_CUST_PROFEE_SAV SAV  WHERE SAV.CUST_ID = :cust_id ");
		sql.append("AND SAV.DATA_YEAR||SAV.DATA_MONTH >= TO_CHAR(ADD_MONTHS(SYSDATE,-24),'YYYYMM') ");
		sql.append("AND SAV.BIG_BLOCK = 'A' GROUP BY SAV.DATA_YEAR, SAV.DATA_MONTH ");
		sql.append(") GROUP BY DATE_TIME ORDER BY DATE_TIME DESC ");
		
		// where 
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
		
	}
}