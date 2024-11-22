package com.systex.jbranch.app.server.fps.pms416u;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms416u")
@Scope("request")
public class PMS416U extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	// 目前未使用
	public void getEmpName(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS416UInputVO inputVO = (PMS416UInputVO) body;
		PMS416UOutputVO outputVO = new PMS416UOutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT EN.EMP_ID, EN.EMP_NAME, AR.AO_CODE, AR.TYPE ");
		sb.append("FROM TBPMS_EMPLOYEE_REC_N EN ");
		sb.append("LEFT JOIN TBPMS_SALES_AOCODE_REC AR ");
		sb.append("       ON EN.EMP_ID = AR.EMP_ID ");
		sb.append("      AND AR.START_TIME BETWEEN EN.START_TIME AND EN.END_TIME ");
		sb.append("      AND (AR.END_TIME BETWEEN EN.START_TIME AND EN.END_TIME OR AR.END_TIME > TRUNC(SYSDATE)) ");
		sb.append("WHERE EXISTS (SELECT ROLEID FROM TBSYSSECUROLPRIASS T WHERE T.ROLEID = EN.ROLE_ID AND T.PRIVILEGEID = 'UHRM002') ");
		sb.append("AND AR.TYPE IN ('5', '6') ");
		sb.append("AND :reportDate BETWEEN TO_CHAR(EN.START_TIME,'YYYYMM') AND TO_CHAR(EN.END_TIME,'YYYYMM') ");
		sb.append("ORDER BY EN.EMP_ID, AR.TYPE ");

		condition.setObject("reportDate", inputVO.getDataMon());
		condition.setQueryString(sb.toString());

		outputVO.setUHRMList(dam.exeQuery(condition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void getEmpNameByYYYYMMDD(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		initUUID();
		PMS416UInputVO inputVO = (PMS416UInputVO) body;
		PMS416UOutputVO outputVO = new PMS416UOutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT EN.E_DEPT_ID, EN.EMP_ID, EN.EMP_NAME || '(' || REPLACE(PAR.PARAM_NAME, 'CODE', '') || ')' AS EMP_NAME, AR.AO_CODE, AR.TYPE ");
		sb.append("FROM TBPMS_EMPLOYEE_REC_N EN ");
		sb.append("INNER JOIN TBPMS_SALES_AOCODE_REC AR ON EN.EMP_ID = AR.EMP_ID ");
		sb.append("       AND ( ");
		sb.append("         (AR.START_TIME BETWEEN EN.START_TIME AND EN.END_TIME AND (AR.END_TIME BETWEEN EN.START_TIME AND EN.END_TIME OR AR.END_TIME > TRUNC(SYSDATE))) ");
		sb.append("         OR ");
		sb.append("         (AR.START_TIME < EN.START_TIME AND (AR.END_TIME BETWEEN EN.START_TIME AND EN.END_TIME OR AR.END_TIME > TRUNC(SYSDATE))) ");
		sb.append("       ) ");
		sb.append("INNER JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'ORG.AOCODE_TYPE' AND PAR.PARAM_CODE = AR.TYPE ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON EN.ROLE_ID = PRI.ROLEID ");
		sb.append(" ");
		sb.append("WHERE PRI.PRIVILEGEID = 'UHRM002' ");
		sb.append("AND :reportDate BETWEEN TRUNC(EN.START_TIME, 'MM') AND EN.END_TIME ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).equals("uhrm")) {
			sb.append("AND EN.EMP_ID = :loginID ");
			condition.setObject("loginID", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getUhrmOP())) {
			sb.append("AND EN.E_DEPT_ID = :eDeptID ");
			condition.setObject("eDeptID", inputVO.getUhrmOP());
		}
		
		sb.append("ORDER BY EN.EMP_ID, AR.TYPE ");

		if (null != inputVO.getDataMon() && inputVO.getDataMon().length() > 6) {
			condition.setObject("reportDate", new SimpleDateFormat("yyyyMMdd").parse(new SimpleDateFormat("yyyyMM").format(new Date(Long.parseLong(inputVO.getDataMon()))) + "01"));
		} else if (null != inputVO.getDataMon() && inputVO.getDataMon().length() == 6) {
			condition.setObject("reportDate", new SimpleDateFormat("yyyyMMdd").parse(inputVO.getDataMon() + "01"));
		} else if (null != inputVO.getsCreDate()) {
			condition.setObject("reportDate", inputVO.getsCreDate());
		} else {
			condition.setObject("reportDate", new SimpleDateFormat("yyyyMMdd").parse(new SimpleDateFormat("yyyyMMdd").format(new Date())));
		} 

		condition.setQueryString(sb.toString());
		outputVO.setUHRMList(dam.exeQuery(condition));
		
		this.sendRtnObject(outputVO);
	}
	
	/*
	 * 用於以下程式：
	 *  SQM320 - 客戶服務定期查核
	 */
	public void getEmpNameByYMMD_NOCODE(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		initUUID();
		PMS416UInputVO inputVO = (PMS416UInputVO) body;
		PMS416UOutputVO outputVO = new PMS416UOutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT EN.EMP_ID, EN.EMP_NAME ");
		sb.append("FROM TBPMS_EMPLOYEE_REC_N EN ");
		sb.append("WHERE EN.DEPT_ID <> EN.E_DEPT_ID ");
		sb.append("AND :reportDate BETWEEN EN.START_TIME AND EN.END_TIME ");
		sb.append("AND EXISTS (SELECT 1 FROM TBORG_ROLE R WHERE R.IS_AO = 'Y' AND R.ROLE_ID = EN.ROLE_ID) ");

		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).equals("uhrm")) {
			sb.append("AND EN.EMP_ID = :loginID ");
			condition.setObject("loginID", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getUhrmOP())) {
			sb.append("AND EN.E_DEPT_ID = :eDeptID ");
			condition.setObject("eDeptID", inputVO.getUhrmOP());
		}
		
		sb.append("ORDER BY EN.EMP_ID ");
		
		if (null != inputVO.getDataMon() && inputVO.getDataMon().length() > 6) {
			condition.setObject("reportDate", new SimpleDateFormat("yyyyMMdd").parse(new SimpleDateFormat("yyyyMM").format(new Date(Long.parseLong(inputVO.getDataMon()))) + "01"));
		} else if (null != inputVO.getDataMon() && inputVO.getDataMon().length() == 6) {
			condition.setObject("reportDate", new SimpleDateFormat("yyyyMMdd").parse(inputVO.getDataMon() + "01"));
		} else if (null != inputVO.getsCreDate()) {
			condition.setObject("reportDate", inputVO.getsCreDate());
		} else {
			throw new JBranchException("請選擇資料統計日期");	
		}
				
		condition.setQueryString(sb.toString());

		outputVO.setUHRMList(dam.exeQuery(condition));
		
		this.sendRtnObject(outputVO);
	}

	// 目前未使用
	public void getEmpNameByYYYYMM(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		initUUID();
		PMS416UInputVO inputVO = (PMS416UInputVO) body;
		PMS416UOutputVO outputVO = new PMS416UOutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT EN.EMP_ID, EN.EMP_NAME || '(' || REPLACE(PAR.PARAM_NAME, 'CODE', '') || ')' AS EMP_NAME, AR.AO_CODE, AR.TYPE ");
		sb.append("FROM TBPMS_EMPLOYEE_REC_N EN ");
		sb.append("INNER JOIN TBPMS_SALES_AOCODE_REC AR ON EN.EMP_ID = AR.EMP_ID ");
		sb.append("       AND ( ");
		sb.append("         (AR.START_TIME BETWEEN EN.START_TIME AND EN.END_TIME AND (AR.END_TIME BETWEEN EN.START_TIME AND EN.END_TIME OR AR.END_TIME > TRUNC(SYSDATE))) ");
		sb.append("         OR ");
		sb.append("         (AR.START_TIME < EN.START_TIME AND (AR.END_TIME BETWEEN EN.START_TIME AND EN.END_TIME OR AR.END_TIME > TRUNC(SYSDATE))) ");
		sb.append("       ) ");
		sb.append("INNER JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'ORG.AOCODE_TYPE' AND PAR.PARAM_CODE = AR.TYPE ");
		sb.append("WHERE EXISTS ( ");
		sb.append("  SELECT 1 ");
		sb.append("  FROM TBPMS_EMPLOYEE_REC_N T ");
		sb.append("  WHERE T.DEPT_ID = '031' ");
		sb.append("  AND (:reportDate >= TO_CHAR(T.START_TIME, 'YYYYMM') AND :reportDate <= TO_CHAR(T.END_TIME, 'YYYYMM')) ");
		sb.append("  AND EN.EMP_ID = T.EMP_ID ");
		sb.append(") ");
		sb.append("AND (:reportDate >= TO_CHAR(EN.START_TIME, 'YYYYMM') AND :reportDate <= TO_CHAR(EN.END_TIME, 'YYYYMM')) ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).equals("uhrm")) {
			sb.append("AND EN.EMP_ID = :loginID ");
			condition.setObject("loginID", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
		}
		
		sb.append("ORDER BY EN.EMP_ID, AR.TYPE ");
		
		if (null != inputVO.getImportSDate()) {
			condition.setObject("reportDate", new SimpleDateFormat("yyyyMM").format(new Date(Long.parseLong(inputVO.getImportSDate()))));
		}
		
		condition.setQueryString(sb.toString());

		outputVO.setUHRMList(dam.exeQuery(condition));
		
		this.sendRtnObject(outputVO);
	}
}