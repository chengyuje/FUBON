package com.systex.jbranch.app.server.fps.crm123;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

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
 * MENU
 * 
 * @author Stella
 * @date 2016/08/17
 * @spec null
 */
	
@Component("crm123")
@Scope("request")
  public class CRM123 extends FubonWmsBizLogic{
		private  DataAccessManager dam = null;
		private  Logger logger = LoggerFactory.getLogger(CRM123.class);
		
		
		public void initial(Object body, IPrimitiveMap header) throws JBranchException {
			CRM123InputVO  inputVO = (CRM123InputVO) body;
			CRM123OutputVO return_VO = 	new CRM123OutputVO();
			dam = this.getDataAccessManager();
			
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			// get 前後和今月
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(inputVO.getDate());
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			calendar.add(Calendar.MONTH, -1);
			Date before = calendar.getTime();
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			calendar.add(Calendar.MONTH, 2);
			Date next = calendar.getTime();
			//======募集======
			sql.append("WITH INVEST AS ( ");
			sql.append("SELECT NUM, PTYPE, PRD_NAME, BGN_DATE_OF_INVEST, END_DATE_OF_INVEST ");
			sql.append("FROM ( ");
			//基金
			sql.append("SELECT '基金' as PTYPE, '1' as NUM, FUND_CNAME as PRD_NAME, IPO_SDATE as BGN_DATE_OF_INVEST, IPO_EDATE as END_DATE_OF_INVEST ");
			sql.append("FROM TBPRD_FUND M, TBPRD_FUNDINFO I ");
			sql.append("WHERE M.PRD_ID = I.PRD_ID ");
			sql.append("AND(TRUNC(I.IPO_SDATE) <= TRUNC(:end)  OR TRUNC(I.IPO_EDATE) >= TRUNC(:start))");
			sql.append("UNION ALL ");
			//SI
			sql.append("SELECT 'SI' AS PTYPE, '5' AS NUM, M.SI_CNAME AS PRD_NAME, I.INV_SDATE as BGN_DATE_OF_INVEST, I.INV_EDATE as END_DATE_OF_INVEST ");
			sql.append("FROM TBPRD_SI M, TBPRD_SIINFO I ");
			sql.append("WHERE M.PRD_ID = I.PRD_ID ");
			sql.append("AND(TRUNC(I.INV_SDATE) <= TRUNC(:end)  OR TRUNC(I.INV_EDATE) >= TRUNC(:start))");
			sql.append("UNION ALL ");
			//SN
			sql.append("SELECT 'SN' AS PTYPE, '6' AS NUM, M.SN_CNAME AS PRD_NAME, I.INV_SDATE as BGN_DATE_OF_INVEST, I.INV_EDATE as END_DATE_OF_INVEST ");
			sql.append("FROM TBPRD_SN M, TBPRD_SNINFO I ");
			sql.append("WHERE M.PRD_ID = I.PRD_ID ");
			sql.append("AND(TRUNC(I.INV_SDATE) <= TRUNC(:end)  OR TRUNC(I.INV_EDATE) >= TRUNC(:start))");
			sql.append("UNION ALL ");
			//ETF
			sql.append("SELECT 'ETF' AS PTYPE, '2' AS NUM, ETF_CNAME AS PRD_NAME, INV_SDATE, INV_EDATE ");
			sql.append("FROM TBPRD_ETF I WHERE 1 = 1 ");
			sql.append("AND(TRUNC(I.INV_SDATE) <= TRUNC(:end)  OR TRUNC(I.INV_EDATE) >= TRUNC(:start))");
			sql.append("UNION ALL ");
			//STOCK
			sql.append("SELECT 'STOCK' AS PTYPE, '3' AS NUM, STOCK_CNAME AS PRD_NAME, INV_SDATE, INV_EDATE ");
			sql.append("FROM TBPRD_STOCK I WHERE 1 = 1 ");
			sql.append("AND(TRUNC(I.INV_SDATE) <= TRUNC(:end)  OR TRUNC(I.INV_EDATE) >= TRUNC(:start))");
			sql.append("UNION ALL ");
			//海外債
			sql.append("SELECT 'BOND' AS PTYPE, '4' AS NUM, M.BOND_CNAME AS PRD_NAME, I.DATE_OF_FLOTATION, I.START_DATE_OF_BUYBACK ");
			sql.append("FROM TBPRD_BOND M, TBPRD_SNINFO I ");
			sql.append("WHERE M.PRD_ID = I.PRD_ID ");
			sql.append("AND(TRUNC(I.INV_SDATE) <= TRUNC(:end)  OR TRUNC(I.INV_EDATE) >= TRUNC(:start))");
			sql.append("UNION ALL ");
			//保險
			sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, INSPRD_NAME as PRD_NAME, I.IPO_SDATE as BGN_DATE_OF_INVEST, I.IPO_EDATE as END_DATE_OF_INVEST ");
			sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
			sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
			sql.append("AND(TRUNC(I.IPO_SDATE) <= TRUNC(:end)  OR TRUNC(I.IPO_EDATE) >= TRUNC(:start))");
			
			sql.append(") ORDER BY END_DATE_OF_INVEST, NUM, PRD_NAME ) ");
			
			sql.append("SELECT PTYPE, PRD_NAME, BGN_DATE_OF_INVEST, END_DATE_OF_INVEST FROM INVEST ");
			
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("start", before);
			queryCondition.setObject("end", next);
			List investList = dam.exeQuery(queryCondition);
			
			return_VO.setCountInvestList(investList);
			
			//======休市======
			sql = new StringBuffer();
			sql.append("WITH REST AS ( ");
			sql.append("SELECT NUM, PTYPE, MKT_NAME, DATE_OF_REST, PRD_NAME ");
			sql.append("FROM ( ");
			//SI
			sql.append("SELECT 'SI' AS PTYPE, '5' AS NUM, '' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, M.SI_CNAME AS PRD_NAME  ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_SI M ON M.PRD_ID = I.PRD_ID ");
			sql.append("WHERE I.PTYPE = 'SI' ");
			sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append("UNION ALL ");
			//SN
			sql.append("SELECT 'SN' AS PTYPE, '6' AS NUM, '' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, M.SN_CNAME AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I  ");
			sql.append("LEFT JOIN TBPRD_SN M ON M.PRD_ID = I.PRD_ID  ");
			sql.append("WHERE I.PTYPE = 'SN' ");
			sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append("UNION ALL ");
			//ETF
			sql.append("SELECT DISTINCT 'ETF' AS PTYPE, '2' AS NUM, I.STOCK_CODE AS MKT_NAME, I.REST_DAY as DATE_OF_REST, '' AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_ETF M ON M.STOCK_CODE = I.STOCK_CODE ");
			sql.append("WHERE I.PTYPE = 'ETF' ");
			sql.append("AND (TRUNC(I.REST_DAY) BETWEEN TRUNC(:start) AND TRUNC(:end)) ");
			sql.append("UNION ALL ");
			//STOCK
			sql.append("SELECT DISTINCT 'STOCK' AS PTYPE, '3' AS NUM, I.STOCK_CODE AS MKT_NAME, I.REST_DAY as DATE_OF_REST, '' AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_STOCK M ON M.STOCK_CODE = I.STOCK_CODE ");
			sql.append("WHERE I.PTYPE = 'STOCK' ");
			sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append("UNION ALL ");
			//海外債
			sql.append("SELECT 'BOND' AS PTYPE, '4' AS NUM, '' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, M.BOND_CNAME AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_BOND M ON M.PRD_ID = I.PRD_ID ");
			sql.append("WHERE I.PTYPE = 'BOND' ");
			sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append(") ORDER BY DATE_OF_REST, NUM, MKT_NAME ) ");
			sql.append("SELECT PTYPE, MKT_NAME, DATE_OF_REST, PRD_NAME FROM REST ");
			
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("start", before);
			queryCondition.setObject("end", next);
			List restList = dam.exeQuery(queryCondition);
			
			return_VO.setCountRestList(restList);
			
			//======配息====== 
			sql = new StringBuffer();
			sql.append("WITH DIVIDEND AS ( ");
			sql.append("SELECT NUM, PTYPE, PRD_NAME, DIV_RATE, DIV_DATE ");
			sql.append("FROM ( ");
			//SI
			sql.append("SELECT PRD_TYPE AS PTYPE, '5' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '1' ");
			sql.append("AND PRD_TYPE = 'SI' ");
			sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append("UNION ALL ");
			//SN
			sql.append("SELECT PRD_TYPE AS PTYPE, '6' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '1' ");
			sql.append("AND PRD_TYPE = 'SN' ");
			sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append("UNION ALL ");
			//海外債
			sql.append("SELECT PRD_TYPE AS PTYPE, '4' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '1' ");
			sql.append("AND PRD_TYPE = 'BOND' ");
			sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
//			sql.append("UNION ALL ");
//			//保險
//			sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, INSPRD_NAME as PRD_NAME, NULL as DIV_RATE ");
//			sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
//			sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
//			sql.append("AND ( TO_CHAR(I.SALE_SDATE,'YYYY/MM/DD') = TO_CHAR(:date,'YYYY/MM/DD') OR "); 
//			sql.append("TO_CHAR(I.SALE_EDATE,'YYYY/MM/DD') = TO_CHAR(:date,'YYYY/MM/DD') ) ");
			sql.append(") ORDER BY NUM, DIV_RATE, PRD_NAME ) ");
			
			sql.append("SELECT PTYPE, PRD_NAME, DIV_RATE, DIV_DATE FROM DIVIDEND ");
			
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("start", before);
			queryCondition.setObject("end", next);
			List dividendList = dam.exeQuery(queryCondition);
			
			return_VO.setCountDividendList(dividendList);
			
			//======到期======
			sql = new StringBuffer();
			sql.append("WITH EXPIRY AS ( ");
			sql.append("SELECT NUM, PTYPE, PRD_NAME, DATE_OF_MATURITY ");
			sql.append("FROM ( ");
			//SI-到期日
			sql.append("SELECT PRD_TYPE AS PTYPE, '5' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '2' ");
			sql.append("AND PRD_TYPE = 'SI' ");
			sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append("UNION ALL ");
			//SN-到期日
			sql.append("SELECT PRD_TYPE AS PTYPE, '6' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '2' ");
			sql.append("AND PRD_TYPE = 'SN' ");
			sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append("UNION ALL ");
			//BOND-到期日
			sql.append("SELECT PRD_TYPE AS PTYPE, '4' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '2' ");
			sql.append("AND PRD_TYPE = 'BOND' ");
			sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append("UNION ALL ");
			//保險
			sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, INSPRD_NAME as PRD_NAME, I.SALE_EDATE as DATE_OF_MATURITY ");
			sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
			sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
			sql.append("AND ( TRUNC(I.SALE_EDATE) BETWEEN TRUNC(:start) AND TRUNC(:end) ) ");
			sql.append(") ORDER BY NUM, DATE_OF_MATURITY, PRD_NAME ) ");
			
			sql.append("SELECT PTYPE, PRD_NAME, DATE_OF_MATURITY FROM EXPIRY ");
			
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("start", before);
			queryCondition.setObject("end", next);
			List expiryList = dam.exeQuery(queryCondition);
			
			return_VO.setCountExpiryList(expiryList);
			this.sendRtnObject(return_VO);
		}
		//募集
		public void showInvest(Object body, IPrimitiveMap header) throws JBranchException , ParseException{
			CRM123InputVO  inputVO = (CRM123InputVO) body;
			CRM123OutputVO return_VO = 	new CRM123OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("WITH INVEST AS ( ");
			sql.append("SELECT NUM, PTYPE, PRD_NAME, BGN_DATE_OF_INVEST, END_DATE_OF_INVEST ");
			sql.append("FROM ( ");
			//基金
			sql.append("SELECT '基金' as PTYPE, '1' as NUM, FUND_CNAME as PRD_NAME, IPO_SDATE as BGN_DATE_OF_INVEST, IPO_EDATE as END_DATE_OF_INVEST ");
			sql.append("FROM TBPRD_FUND M, TBPRD_FUNDINFO I ");
			sql.append("WHERE M.PRD_ID = I.PRD_ID ");
			sql.append("AND ( TRUNC(I.IPO_SDATE) <= TRUNC(:date) AND TRUNC(I.IPO_EDATE) >= TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//SI
			sql.append("SELECT 'SI' AS PTYPE, '5' AS NUM, M.SI_CNAME AS PRD_NAME, I.INV_SDATE as BGN_DATE_OF_INVEST, I.INV_EDATE as END_DATE_OF_INVEST ");
			sql.append("FROM TBPRD_SI M, TBPRD_SIINFO I ");
			sql.append("WHERE M.PRD_ID = I.PRD_ID ");
			sql.append("AND ( TRUNC(I.INV_SDATE) <= TRUNC(:date) AND TRUNC(I.INV_EDATE) >= TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//SN
			sql.append("SELECT 'SN' AS PTYPE, '6' AS NUM, M.SN_CNAME AS PRD_NAME, I.INV_SDATE as BGN_DATE_OF_INVEST, I.INV_EDATE as END_DATE_OF_INVEST ");
			sql.append("FROM TBPRD_SN M, TBPRD_SNINFO I ");
			sql.append("WHERE M.PRD_ID = I.PRD_ID ");
			sql.append("AND ( TRUNC(I.INV_SDATE) <= TRUNC(:date) AND TRUNC(I.INV_EDATE) >= TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//ETF
			sql.append("SELECT 'ETF' AS PTYPE, '2' AS NUM, ETF_CNAME AS PRD_NAME, INV_SDATE, INV_EDATE ");
			sql.append("FROM TBPRD_ETF I WHERE 1 = 1 ");
			sql.append("AND ( TRUNC(I.INV_SDATE) <= TRUNC(:date) AND TRUNC(I.INV_EDATE) >= TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//STOCK
			sql.append("SELECT 'STOCK' AS PTYPE, '3' AS NUM, STOCK_CNAME AS PRD_NAME, INV_SDATE, INV_EDATE ");
			sql.append("FROM TBPRD_STOCK I WHERE 1 = 1 ");
			sql.append("AND ( TRUNC(I.INV_SDATE) <= TRUNC(:date) AND TRUNC(I.INV_EDATE) >= TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//海外債
			sql.append("SELECT 'BOND' AS PTYPE, '4' AS NUM, M.BOND_CNAME AS PRD_NAME, I.DATE_OF_FLOTATION, I.START_DATE_OF_BUYBACK ");
			sql.append("FROM TBPRD_BOND M, TBPRD_SNINFO I ");
			sql.append("WHERE M.PRD_ID = I.PRD_ID ");
			sql.append("AND ( TRUNC(I.INV_SDATE) <= TRUNC(:date) AND TRUNC(I.INV_EDATE) >= TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//保險
			sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, INSPRD_NAME as PRD_NAME, I.IPO_SDATE as BGN_DATE_OF_INVEST, I.IPO_EDATE as END_DATE_OF_INVEST ");
			sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
			sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
			sql.append("AND ( TRUNC(I.IPO_SDATE) <= TRUNC(:date) AND TRUNC(I.IPO_EDATE) >= TRUNC(:date) ) ");
			sql.append(") ORDER BY END_DATE_OF_INVEST, NUM, PRD_NAME ) ");
			
			sql.append("SELECT PTYPE, PRD_NAME, BGN_DATE_OF_INVEST, END_DATE_OF_INVEST FROM INVEST ");
			
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("date", inputVO.getDate());
			
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			return_VO.setResultList(list);
			sendRtnObject(return_VO);
		}
		
		//休市
		public void showRest(Object body, IPrimitiveMap header) throws JBranchException {
			CRM123InputVO  inputVO = (CRM123InputVO) body;
			CRM123OutputVO return_VO = 	new CRM123OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("WITH REST AS ( ");
			sql.append("SELECT NUM, PTYPE, MKT_NAME, DATE_OF_REST, PRD_NAME ");
			sql.append("FROM ( ");
			//SI
			sql.append("SELECT 'SI' AS PTYPE, '5' AS NUM, '--' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, M.SI_CNAME AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_SI M ON M.PRD_ID = I.PRD_ID  ");
			sql.append("WHERE I.PTYPE = 'SI' ");
			sql.append("AND ( TRUNC(I.REST_DAY) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//SN
			sql.append("SELECT 'SN' AS PTYPE, '6' AS NUM, '--' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, M.SN_CNAME AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_SN M ON M.PRD_ID = I.PRD_ID  ");
			sql.append("WHERE I.PTYPE = 'SN' ");
			sql.append("AND ( TRUNC(I.REST_DAY) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//ETF
			sql.append("SELECT DISTINCT 'ETF' AS PTYPE, '2' AS NUM, I.STOCK_CODE AS MKT_NAME, I.REST_DAY as DATE_OF_REST, '--' AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_ETF M ON M.STOCK_CODE = I.STOCK_CODE ");
			sql.append("WHERE I.PTYPE = 'ETF' ");
			sql.append("AND ( TRUNC(I.REST_DAY) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//STOCK
			sql.append("SELECT DISTINCT 'STOCK' AS PTYPE, '3' AS NUM, I.STOCK_CODE AS MKT_NAME, I.REST_DAY as DATE_OF_REST, '--' AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_STOCK M ON M.STOCK_CODE = I.STOCK_CODE ");
			sql.append("WHERE I.PTYPE = 'STOCK' ");
			sql.append("AND ( TRUNC(I.REST_DAY) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//海外債
			sql.append("SELECT 'BOND' AS PTYPE, '4' AS NUM, '--' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, M.BOND_CNAME AS PRD_NAME ");
			sql.append("FROM TBPRD_REST_DAY I ");
			sql.append("LEFT JOIN TBPRD_BOND M ON M.PRD_ID = I.PRD_ID ");
			sql.append("WHERE I.PTYPE = 'BOND' ");
			sql.append("AND ( TRUNC(I.REST_DAY) = TRUNC(:date) ) ");
			sql.append(") ORDER BY DATE_OF_REST, NUM, MKT_NAME ) ");
			
			sql.append("SELECT PTYPE, MKT_NAME, DATE_OF_REST, PRD_NAME FROM REST ");
			
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("date", inputVO.getDate());
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			return_VO.setResultList(list);
			this.sendRtnObject(return_VO);
		}
		//配息
		public void showDividend(Object body, IPrimitiveMap header) throws JBranchException {
			CRM123InputVO  inputVO = (CRM123InputVO) body;
			CRM123OutputVO return_VO = 	new CRM123OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("WITH DIVIDEND AS ( ");
			sql.append("SELECT NUM, PTYPE, PRD_NAME, DIV_RATE, DIV_DATE ");
			sql.append("FROM ( ");
			//SI
			sql.append("SELECT PRD_TYPE AS PTYPE, '5' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE  1 = 1");
			sql.append("AND CAL_TYPE = '1' ");
			sql.append("AND PRD_TYPE = 'SI' ");
			sql.append("AND ( TRUNC(CUS_DATE) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//SN
			sql.append("SELECT PRD_TYPE AS PTYPE, '6' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE  1 = 1");
			sql.append("AND CAL_TYPE = '1' ");
			sql.append("AND PRD_TYPE = 'SN' ");
			sql.append("AND ( TRUNC(CUS_DATE) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//海外債
			sql.append("SELECT PRD_TYPE AS PTYPE, '4' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE  1 = 1");
			sql.append("AND CAL_TYPE = '1' ");
			sql.append("AND PRD_TYPE = 'BOND' ");
			sql.append("AND ( TRUNC(CUS_DATE) = TRUNC(:date) ) ");
//			sql.append("UNION ALL ");
//			//保險
//			sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, INSPRD_NAME as PRD_NAME, NULL as DIV_RATE ");
//			sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
//			sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
//			sql.append("AND ( TO_CHAR(I.SALE_SDATE,'YYYY/MM/DD') = TO_CHAR(:date,'YYYY/MM/DD') OR "); 
//			sql.append("TO_CHAR(I.SALE_EDATE,'YYYY/MM/DD') = TO_CHAR(:date,'YYYY/MM/DD') ) ");
			sql.append(") ORDER BY DIV_DATE, NUM, PRD_NAME ) ");
			
			sql.append("SELECT PTYPE, PRD_NAME, DIV_RATE, DIV_DATE FROM DIVIDEND ");
			
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("date", inputVO.getDate());
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			return_VO.setResultList(list);
			this.sendRtnObject(return_VO);
		}
		//到期
		public void showexpiry(Object body, IPrimitiveMap header) throws JBranchException {
			CRM123InputVO  inputVO = (CRM123InputVO) body;
			CRM123OutputVO return_VO = 	new CRM123OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("WITH EXPIRY AS ( ");
			sql.append("SELECT NUM, PTYPE, PRD_NAME, DATE_OF_MATURITY ");
			sql.append("FROM ( ");
			//SI-到期日
			sql.append("SELECT PRD_TYPE AS PTYPE, '5' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '2' ");
			sql.append("AND PRD_TYPE = 'SI' ");
			sql.append("AND ( TRUNC(CUS_DATE) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//SN-到期日
			sql.append("SELECT PRD_TYPE AS PTYPE, '6' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '2' ");
			sql.append("AND PRD_TYPE = 'SN' ");
			sql.append("AND ( TRUNC(CUS_DATE) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//BOND-到期日
			sql.append("SELECT PRD_TYPE AS PTYPE, '4' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
			sql.append("FROM TBPRD_CALENDAR ");
			sql.append("WHERE CAL_TYPE = '2' ");
			sql.append("AND PRD_TYPE = 'BOND' ");
			sql.append("AND ( TRUNC(CUS_DATE) = TRUNC(:date) ) ");
			sql.append("UNION ALL ");
			//保險
			sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, INSPRD_NAME as PRD_NAME, I.SALE_EDATE as DATE_OF_MATURITY ");
			sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
			sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
			sql.append("AND ( TRUNC(I.SALE_EDATE) = TRUNC(:date) ) ");
			sql.append(") ORDER BY DATE_OF_MATURITY, NUM, PRD_NAME ) ");
			
			sql.append("SELECT PTYPE, PRD_NAME, DATE_OF_MATURITY FROM EXPIRY ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("date", inputVO.getDate());
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			return_VO.setResultList(list);
			this.sendRtnObject(return_VO);
		}		
	}

