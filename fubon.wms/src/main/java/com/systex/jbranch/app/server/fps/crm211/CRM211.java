package com.systex.jbranch.app.server.fps.crm211;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm210.CRM210;
import com.systex.jbranch.app.server.fps.crm210.CRM210OutputVO;
import com.systex.jbranch.app.server.fps.crm210.CRM210_ALLInputVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author walalala, Carely
 * @date 2016/11/17
 * @spec null
 */
@Component("crm211")
@Scope("request")
public class CRM211 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void getAOCode (Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM211OutputVO return_VO = new CRM211OutputVO();
		
		return_VO.setResultList(getAOCode("ORIGINAL"));
		
		this.sendRtnObject(return_VO);
	}
	
	public List<Map<String, Object>> getAOCode (String enterType) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("       INFO.AO_CODE, ");
		sql.append("       INFO.EMP_NAME, ");
		sql.append("       INFO.BRANCH_NBR, ");
		sql.append("       REPLACE(PAR.PARAM_NAME, 'CODE', '') AS CODE_TYPE_NAME, ");
		sql.append("       INFO.AO_CODE || '_' || INFO.EMP_NAME || '(' || REPLACE(PAR.PARAM_NAME, 'CODE', '') || ')' AS LABEL, ");
		sql.append("       INFO.AO_CODE AS DATA ");
		sql.append("FROM VWORG_EMP_INFO INFO ");
		sql.append("INNER JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'ORG.AOCODE_TYPE' AND PAR.PARAM_CODE = INFO.CODE_TYPE ");
		sql.append("WHERE INFO.AO_CODE IS NOT NULL ");
		sql.append("AND INFO.BRANCH_NBR IS NOT NULL ");
		
		if (!"ALL".equals(getCommonVariable(FubonSystemVariableConsts.LOGINROLE).toString()) && 
			((List<String>) getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST)).size() > 0
		) {  //判斷非總行人員再加條件
			sql.append("AND BRANCH_NBR IN (:brNbrList) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		switch (enterType) {
			case "Diamond Team":
				sql.append("AND INFO.EMP_ID = :empID ");
				queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				break;
			default:
				break;
		}
		
		sql.append("ORDER BY AO_CODE ");
		
		queryCondition.setQueryString(sql.toString());
		
		return dam.exeQuery(queryCondition);
	}
	
	public List<Map<String, Object>> getDiamondTeamAOCode () throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT AO_CODE, EMP_ID, EMP_NAME, BRANCH_NBR, CODE_TYPE, CODE_TYPE_NAME, TEAM_TYPE, LABEL, DATA ");
		sql.append("FROM ( ");
		sql.append("  SELECT INFO.AO_CODE, ");
		sql.append("         INFO.EMP_ID, ");
		sql.append("         INFO.EMP_NAME, ");
		sql.append("         INFO.BRANCH_NBR, ");
		sql.append("         INFO.CODE_TYPE, ");
		sql.append("         REPLACE(PAR.PARAM_NAME, 'CODE', '') AS CODE_TYPE_NAME, ");
		sql.append("         DT.TEAM_TYPE, ");
		sql.append("         INFO.AO_CODE || '_' || INFO.EMP_NAME || '(' || REPLACE(PAR.PARAM_NAME, 'CODE', '') || ')' AS LABEL, ");
		sql.append("         INFO.AO_CODE AS DATA ");
		sql.append("  FROM VWORG_EMP_INFO INFO ");
		sql.append("  LEFT JOIN TBORG_DIAMOND_TEAM DT ON DT.EMP_ID = INFO.EMP_ID AND DT.BRANCH_NBR = INFO.BRANCH_NBR ");
		sql.append("  INNER JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'ORG.AOCODE_TYPE' AND PAR.PARAM_CODE = INFO.CODE_TYPE ");
		sql.append("  WHERE INFO.AO_CODE IS NOT NULL ");
		sql.append("  AND INFO.BRANCH_NBR IS NOT NULL ");
		sql.append("  AND INFO.BRANCH_NBR IN (:brNbrList) ");
		sql.append("  AND EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM TEAM WHERE TEAM.TEAM_TYPE = DT.TEAM_TYPE AND TEAM.EMP_ID = :empID) ");
		sql.append("  UNION ");
		sql.append("  SELECT 'Diamond Team' AS AO_CODE, ");
		sql.append("         '' AS EMP_ID, ");
		sql.append("         '' AS EMP_NAME, ");
		sql.append("         '' AS BRANCH_NBR, ");
		sql.append("         '' AS CODE_TYPE, ");
		sql.append("         '' AS CODE_TYPE_NAME, ");
		sql.append("         '' AS TEAM_TYPE, ");
		sql.append("         'Diamond Team' AS LABEL, ");
		sql.append("         'Diamond Team' AS DATA ");
		sql.append("  FROM DUAL ");
		sql.append(") ");
		sql.append("ORDER BY DECODE(EMP_ID, :empID, 0, 99), EMP_ID, CODE_TYPE, AO_CODE ");
		
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		
		queryCondition.setQueryString(sql.toString());
		
		return dam.exeQuery(queryCondition);
	}
	
	// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管 change svn:log
	public void getAOCodeList(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM211OutputVO return_VO = new CRM211OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT COUNT(1) AS COUNTS ");
		sql.append("FROM TBORG_DIAMOND_TEAM ");
		sql.append("WHERE EMP_ID = :empID ");
		
		queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (((BigDecimal) list.get(0).get("COUNTS")).compareTo(BigDecimal.ZERO) == 1) {
			return_VO.setResultList(getDiamondTeamAOCode());
		} else {
			return_VO.setResultList(getAOCode("Diamond Team"));
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM211InputVO inputVO = (CRM211InputVO) body;
		
		CRM210_ALLInputVO inputVO_all = new CRM210_ALLInputVO();
		inputVO_all.setCrm211inputVO(inputVO);
		inputVO_all.setCrm210inputVO(inputVO);
		inputVO_all.setAvailRegionList(getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		inputVO_all.setAvailAreaList(getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		inputVO_all.setAvailBranchList(getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		inputVO_all.setLoginEmpID(ws.getUser().getUserID());
		inputVO_all.setLoginRole(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		String loginToken = "";
		//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
		if(getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN) != null){
			loginToken = getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN).toString();
		}
		
		Object loginAoCode = null;
		if(getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST) != null){
			loginAoCode = getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		}
		
		CRM210 crm210 = (CRM210) PlatformContext.getBean("crm210");
		CRM210OutputVO outputVO_crm210 = new CRM210OutputVO();
		outputVO_crm210 = crm210.inquire_common(inputVO_all, "CRM211", loginToken, loginAoCode);

		this.sendRtnObject(outputVO_crm210);
	}
	
	public void initQuery(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		CRM211OutputVO return_VO = new CRM211OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT PARAM_NAME ");
		sql.append("FROM TBSYSPARAMETER ");
		sql.append("WHERE PARAM_TYPE = 'CRM.INITIALIZE.TABLECHECK' ");
		sql.append("ORDER BY PARAM_ORDER ");
		
		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
			
		if(!StringUtils.equals((String) list.get(1).get("PARAM_NAME"), new SimpleDateFormat("yyyyMMdd").format(new Date()))){
			return_VO.setErrorMsg((String) list.get(0).get("PARAM_NAME"));
			return_VO.setShowMsg(true);
		} 
		
		sendRtnObject(return_VO);
	}
	
}