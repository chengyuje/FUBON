package com.systex.jbranch.app.server.fps.prd290;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd290
 * 
 * @author moron
 * @date 2016/10/03
 * @spec null
 */
@Component("prd290")
@Scope("request")
public class PRD290 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD290.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD290InputVO inputVO = (PRD290InputVO) body;
		PRD290OutputVO return_VO = new PRD290OutputVO();
		dam = this.getDataAccessManager();
		
		// 預設帶出前一個工作天還是主推基金的基金績效
		// select PABTH_UTIL.FC_getBusiDay(sysdate, 'TWD', -1) from dual; 
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select info.PRD_ID,fund.FUND_CNAME_A from TBPRD_FUNDINFO info ");
		sql.append("left join TBPRD_FUND fund on info.PRD_ID = fund.PRD_ID ");
		sql.append("where info.MAIN_PRD= 'Y' and (TRUNC(PABTH_UTIL.FC_getBusiDay(sysdate, 'TWD', -1)) BETWEEN TRUNC(info.MAIN_PRD_SDATE) and TRUNC(info.MAIN_PRD_EDATE)) ");
		if (StringUtils.isNotBlank(inputVO.getPrd_id())) {
			sql.append("and info.PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			// get other
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("select min(C_RETURN) as MIN from VWPRD_MAIN_FUND_PERFORMANCE where prd_id = :id ");
			queryCondition.setObject("id", map.get("PRD_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> ch1 = dam.exeQuery(queryCondition);
			map.put("C_RETURN_MIN", ch1.get(0).get("MIN"));
			//
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("select max(C_RETURN) as MAX from VWPRD_MAIN_FUND_PERFORMANCE where prd_id = :id ");
			queryCondition.setObject("id", map.get("PRD_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> ch2 = dam.exeQuery(queryCondition);
			map.put("C_RETURN_MAX", ch2.get(0).get("MAX"));
			//
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("select sum(C_RETURN) / count(*) as AVG from VWPRD_MAIN_FUND_PERFORMANCE where prd_id = :id ");
			queryCondition.setObject("id", map.get("PRD_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> ch3 = dam.exeQuery(queryCondition);
			map.put("C_RETURN_AVG", ch3.get(0).get("AVG"));
			//
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select PRICE_DATE, C_RETURN from VWPRD_MAIN_FUND_PERFORMANCE where prd_id = :id and C_RETURN >= :cret ");
			queryCondition.setObject("id", map.get("PRD_ID"));
			queryCondition.setObject("cret", inputVO.getTarget() / 100);
			List<Map<String, Object>> ch4 = dam.exeQuery(queryCondition);
			if(ch4.size() > 0)
				map.put("PRICE_DATE_SUM", ch4);
		}
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getSetting(Object body, IPrimitiveMap header) throws JBranchException {
		PRD290OutputVO return_VO = new PRD290OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PARAM_CODE,PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'PRD.PACKAGE_SETTING' ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void updateSetting(Object body, IPrimitiveMap header) throws JBranchException {
		PRD290InputVO inputVO = (PRD290InputVO) body;
		dam = this.getDataAccessManager();
		
		// fund
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("update TBSYSPARAMETER set PARAM_NAME = :name where PARAM_TYPE = 'PRD.PACKAGE_SETTING' and PARAM_CODE = 'FUND'");
		condition.setObject("name", inputVO.getFund());
		dam.exeUpdate(condition);
		// fund_month
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("update TBSYSPARAMETER set PARAM_NAME = :name where PARAM_TYPE = 'PRD.PACKAGE_SETTING' and PARAM_CODE = 'FUND_MONTH'");
		condition.setObject("name", inputVO.getFund_month());
		dam.exeUpdate(condition);
		
		this.sendRtnObject(null);
	}
	
	public void getReturn(Object body, IPrimitiveMap header) throws JBranchException {
		PRD290InputVO inputVO = (PRD290InputVO) body;
		PRD290OutputVO return_VO = new PRD290OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from VWPRD_MAIN_FUND_PERFORMANCE where PRD_ID = :id and (TRUNC(PRICE_DATE) BETWEEN TRUNC(sysdate - 7) and TRUNC(sysdate)) order by PRICE_DATE ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select * from VWPRD_MAIN_FUND_PERFORMANCE where PRD_ID = :id and (TRUNC(PRICE_DATE) BETWEEN TRUNC(sysdate - 7) and TRUNC(sysdate)) order by PRICE_DATE ");
		queryCondition.setObject("id", inputVO.getPrd_id2());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		return_VO.setResultList2(list2);
		
		this.sendRtnObject(return_VO);
	}
	
	public void getLinear(Object body, IPrimitiveMap header) throws JBranchException {
		PRD290InputVO inputVO = (PRD290InputVO) body;
		PRD290OutputVO return_VO = new PRD290OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 淨值
		if(StringUtils.equals("1", inputVO.getType())) {
			sql.append("select PRICE_DATE as SDATE,PRICE as SDATA from TBPRD_FUNDPRICE where PRD_ID = :id and (TRUNC(PRICE_DATE) BETWEEN TRUNC(:start) and TRUNC(:end)) order by PRICE_DATE ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			if(inputVO.getsDate() != null) {
				queryCondition.setObject("start", inputVO.getsDate());
				if(inputVO.geteDate() != null)
					queryCondition.setObject("end", inputVO.geteDate());
				else {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getsDate());
					calendar.add(Calendar.DATE, 14);
					queryCondition.setObject("end", calendar.getTime());
				}
			}
			if(inputVO.geteDate() != null) {
				queryCondition.setObject("end", inputVO.geteDate());
				if(inputVO.getsDate() != null)
					queryCondition.setObject("start", inputVO.getsDate());
				else {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.geteDate());
					calendar.add(Calendar.DATE, -14);
					queryCondition.setObject("start", calendar.getTime());
				}
			}
			if(inputVO.getsDate() == null && inputVO.geteDate() == null) {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(new Date());
				queryCondition.setObject("end", calendar.getTime());
				calendar.add(Calendar.DATE, -14);
				queryCondition.setObject("start", calendar.getTime());
			}
		}
		// 單日報酬
		else if(StringUtils.equals("2", inputVO.getType())) {
			sql.append("select RETURN_DATE as SDATE, RETURN as SDATA from TBPRD_FUNDRETURN where PRD_ID = :id and (TRUNC(RETURN_DATE) BETWEEN TRUNC(:start) and TRUNC(:end)) order by RETURN_DATE ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			if(inputVO.getsDate() != null) {
				queryCondition.setObject("start", inputVO.getsDate());
				if(inputVO.geteDate() != null)
					queryCondition.setObject("end", inputVO.geteDate());
				else {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getsDate());
					calendar.add(Calendar.DATE, 14);
					queryCondition.setObject("end", calendar.getTime());
				}
			}
			if(inputVO.geteDate() != null) {
				queryCondition.setObject("end", inputVO.geteDate());
				if(inputVO.getsDate() != null)
					queryCondition.setObject("start", inputVO.getsDate());
				else {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.geteDate());
					calendar.add(Calendar.DATE, -14);
					queryCondition.setObject("start", calendar.getTime());
				}
			}
			if(inputVO.getsDate() == null && inputVO.geteDate() == null) {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(new Date());
				queryCondition.setObject("end", calendar.getTime());
				calendar.add(Calendar.DATE, -14);
				queryCondition.setObject("start", calendar.getTime());
			}
		}
		// 每日進場累計報酬
		else {
			sql.append("select PRICE_DATE as SDATE,C_RETURN as SDATA from VWPRD_MAIN_FUND_PERFORMANCE where PRD_ID = :id and (TRUNC(PRICE_DATE) BETWEEN TRUNC(:start) and TRUNC(:end)) order by PRICE_DATE ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			if(inputVO.getsDate() != null) {
				queryCondition.setObject("start", inputVO.getsDate());
				if(inputVO.geteDate() != null)
					queryCondition.setObject("end", inputVO.geteDate());
				else {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getsDate());
					calendar.add(Calendar.DATE, 14);
					queryCondition.setObject("end", calendar.getTime());
				}
			}
			if(inputVO.geteDate() != null) {
				queryCondition.setObject("end", inputVO.geteDate());
				if(inputVO.getsDate() != null)
					queryCondition.setObject("start", inputVO.getsDate());
				else {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.geteDate());
					calendar.add(Calendar.DATE, -14);
					queryCondition.setObject("start", calendar.getTime());
				}
			}
			if(inputVO.getsDate() == null && inputVO.geteDate() == null) {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(new Date());
				queryCondition.setObject("end", calendar.getTime());
				calendar.add(Calendar.DATE, -14);
				queryCondition.setObject("start", calendar.getTime());
			}
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	
	
	
}