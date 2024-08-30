package com.systex.jbranch.fubon.bth.mplus;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.mplus.MPlusEmpolyeeUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Component("mplusWebServiceDao")
@Scope("prototype")
public class MplusWebServiceDao extends BizLogic { 
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired@Qualifier("mPlusEmpolyeeUtil")
	private MPlusEmpolyeeUtil mPlusEmpolyeeUtil;
	
	@SuppressWarnings("unchecked")
	public List<Map<String , Object>> queryInstalList() throws Exception {
		//新進人員+今日職務異動後可覆核人員清單
		StringBuffer queryStr = new StringBuffer();
//		queryStr.append(" select * from tborg_member where emp_id in ( ");
//		queryStr.append(" 	select emp_id from tborg_member ");
//		queryStr.append(" 	where job_title_name in ( ");
//		queryStr.append(" 		select job_title_name from tborg_role where role_id in ( ");
//		queryStr.append(" 			select roleid from tbsyssecurolpriass where privilegeid in ( ");
//		queryStr.append(" 				select param_code from tbsysparameter where param_type like 'CRM.BRG_ROLEID%' ");
//		queryStr.append(" 			) ");
//		queryStr.append(" 		) ");
//		queryStr.append(" 	) ");
//		queryStr.append(" 	minus  ");
//		queryStr.append(" 	select emp_number from tbapi_mplus ");
//		queryStr.append(" ) ");
//		queryStr.append(" and change_flag in ('A','M','P') ");
//		queryStr.append(" and service_flag='A' ");
//		queryStr.append(" and DEPT_ID IN ( ");
//		queryStr.append(" 	SELECT DEPT_ID FROM TBORG_DEFN ");
//		queryStr.append(" 	WHERE ORG_TYPE = '50' ");
//		queryStr.append(" 	AND DEPT_ID > '199' ");
//		queryStr.append(" 	AND DEPT_ID < '801' ");
//		queryStr.append(" 	AND LENGTH(DEPT_ID) = 3 ");
//		queryStr.append(" ) ");
//		queryStr.append(" order by job_title_name ");
		
		// add by ocean 6592:WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5	
		queryStr.append("SELECT * ");
		queryStr.append("FROM TBORG_MEMBER MEM ");
		queryStr.append("WHERE EMP_ID IN ( ");
		queryStr.append("  SELECT EMP_ID ");
		queryStr.append("  FROM VWORG_EMP_INFO EMP ");
		queryStr.append("  WHERE EXISTS (SELECT P.PARAM_CODE FROM TBSYSPARAMETER P WHERE P.PARAM_TYPE LIKE 'CRM.BRG_ROLEID%' AND EMP.PRIVILEGEID = P.PARAM_CODE) ");
		queryStr.append("	MINUS ");
		queryStr.append("	SELECT MPLUS.EMP_NUMBER ");
		queryStr.append("  FROM TBAPI_MPLUS MPLUS ");
		queryStr.append(") ");
		queryStr.append("AND CHANGE_FLAG IN ('A', 'M', 'P') ");
		queryStr.append("AND SERVICE_FLAG = 'A' ");
		queryStr.append("ORDER BY JOB_TITLE_NAME ");

		return query(queryStr.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String , Object>> queryDelGroupList() throws Exception {
		//新進人員+今日職務異動後可覆核人員清單
		StringBuffer queryStr = new StringBuffer();
//		queryStr.append(" select * from tborg_member where emp_id in ( ");
//		queryStr.append(" 	select emp_number from tbapi_mplus ");
//		queryStr.append(" 	minus ");
//		queryStr.append(" 	select emp_id from tborg_member ");
//		queryStr.append(" 	where job_title_name in ( ");
//		queryStr.append(" 		select job_title_name from tborg_role ");
//		queryStr.append(" 		where role_id in ( ");
//		queryStr.append(" 			select roleid from tbsyssecurolpriass ");
//		queryStr.append(" 			where privilegeid in ( ");
//		queryStr.append(" 				select param_code from tbsysparameter ");
//		queryStr.append(" 				where param_type like 'CRM.BRG_ROLEID%' ");
//		queryStr.append(" 			) ");
//		queryStr.append(" 		) ");
//		queryStr.append(" 	) ");
//		queryStr.append(" ) ");
//		queryStr.append(" and change_flag in ('A','M','D') ");
//		queryStr.append(" and service_flag='A' ");
//		queryStr.append(" and DEPT_ID IN ( ");
//		queryStr.append(" 	SELECT DEPT_ID FROM TBORG_DEFN WHERE ORG_TYPE = '50' ");
//		queryStr.append(" 	AND DEPT_ID > '199' ");
//		queryStr.append(" 	AND DEPT_ID < '801' ");
//		queryStr.append(" 	AND LENGTH(DEPT_ID) = 3 ");
//		queryStr.append(" ) ");
//		queryStr.append(" order by job_title_name ");
		
		// add by ocean 6592:WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5	
		queryStr.append("SELECT * ");
		queryStr.append("FROM TBORG_MEMBER ");
		queryStr.append("WHERE EMP_ID IN ( ");
		queryStr.append("	SELECT EMP_NUMBER FROM TBAPI_MPLUS ");
		queryStr.append("	MINUS ");
		queryStr.append("	SELECT EMP_ID ");
		queryStr.append("	FROM VWORG_EMP_INFO EMP ");
		queryStr.append("	WHERE EXISTS (SELECT P.PARAM_CODE FROM TBSYSPARAMETER P WHERE P.PARAM_TYPE LIKE 'CRM.BRG_ROLEID%' AND EMP.PRIVILEGEID = P.PARAM_CODE) ");
		queryStr.append(") ");
		queryStr.append("AND CHANGE_FLAG IN ('A', 'M', 'D') ");
		queryStr.append("AND SERVICE_FLAG = 'A' ");
		queryStr.append("ORDER BY JOB_TITLE_NAME ");

		return query(queryStr.toString());
	}
	
	@SuppressWarnings("rawtypes")
	private List query(String queryStr){
		try{
			return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(queryStr.toString()));
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return null;
	}
}
