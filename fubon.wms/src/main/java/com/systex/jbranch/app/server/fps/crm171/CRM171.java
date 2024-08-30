package com.systex.jbranch.app.server.fps.crm171;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron
 * @date 2016/06/06
 * @spec null
 */
@Component("crm171")
@Scope("request")
public class CRM171 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM171.class);

	public void getCFData(Object body, IPrimitiveMap header) throws JBranchException {
		//		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM171InputVO inputVO = (CRM171InputVO) body;
		CRM171OutputVO return_VO = new CRM171OutputVO();
		dam = this.getDataAccessManager();

		XmlInfo xmlInfo = new XmlInfo();

		Map<String, String> fchMap = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		// by willis 20171019 增加FCH等級理專
		fcMap.putAll(fchMap);
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT COUNT(C.CUST_ID) AS CUST_CNT_UNPLAN, SUM(C.REC_CF_TOTAL) as REC_CF_TOTAL ");
		sql.append("FROM TBPMS_CUS_CF_MAST C WHERE 1 = 1 ");
		sql.append("AND C.DATA_DATE = (SELECT MAX(DATA_DATE)FROM TBPMS_CUS_CF_MAST) ");
		//IF 登入者身份為分行主管or 業務主管
		if (bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND C.BRANCH_NBR = :BRANCH_NBR ");
			queryCondition.setObject("BRANCH_NBR", inputVO.getCUST_NBR());
			//ELSE IF 登入者身份為區督導
		} else if (mbrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND C.BRANCH_AREA_ID = :BRANCH_AREA_ID  ");
			queryCondition.setObject("BRANCH_AREA_ID", inputVO.getCUST_BRANCH());
			//ELSE IF 登入者身份為業務處主管	
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND C.REGION_CENTER_ID = :REGION_CENTER_ID  ");
			queryCondition.setObject("REGION_CENTER_ID", inputVO.getCUST_REGION());
		} else if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			switch ((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) {
			case "JRM":
				sql.append("AND C.BRANCH_NBR = :BRANCH_NBR ");
				queryCondition.setObject("BRANCH_NBR", inputVO.getCUST_NBR());
				break;
			default:
				sql.append("AND C.AO_CODE IN ( :AO_CODE )");
				queryCondition.setObject("AO_CODE", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
				break;
			}
		}

		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(return_VO);
	}

	public void getCustCFData(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM171InputVO inputVO = (CRM171InputVO) body;
		CRM171OutputVO return_VO = new CRM171OutputVO();
		dam = this.getDataAccessManager();
		//		inputVO.setCUST_ID("A102136095");

		//		XmlInfo xmlInfo = new XmlInfo();
		//		
		//		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		//		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		//		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//		sql.append(" WITH CFDATA as ( ");
		//		sql.append(" SELECT CUST_ID, SUM(EST_AMT) AS EST_AMT FROM TBPMS_SALES_PLAN WHERE SRC_TYPE='1' AND CLOSE_DATE >= TRUNC(sysdate+1) ");  
		//		sql.append("	GROUP BY CUST_ID  ");
		//		sql.append(" ), MAX_CFDATE as ( ");
		//		sql.append("  SELECT MAX(DATA_DATE) FROM TBPMS_CUS_CF_MAST ");
		//		sql.append(" ) ");

		//		//0-理專，1-業務主管，2-主管，3-輔銷 ，4-總行
		/*
		sql.append("SELECT SUM(C.REC_CF_TOTAL) as REC_CF_TOTAL, ");
		sql.append("SUM(CASE WHEN S.EST_AMT<=C.REC_CF_TOTAL THEN S.EST_AMT ELSE C.REC_CF_TOTAL  END) AS REC_CF_PLAN ");
		sql.append("FROM TBPMS_CUS_CF_MAST C ");
		
		sql.append("LEFT JOIN ( SELECT CUST_ID, SUM(EST_AMT) AS EST_AMT FROM TBPMS_SALES_PLAN WHERE SRC_TYPE='1' AND CLOSE_DATE>=TRUNC(sysdate+1)  ");
		sql.append("GROUP BY CUST_ID ) S ON C.CUST_ID=S.CUST_ID ");
		//sql.append("   LEFT JOIN (SELECT * FROM CFDATA) S ON C.CUST_ID = S.CUST_ID   ");
		sql.append("WHERE C.DATA_DATE=(SELECT MAX(DATA_DATE)FROM TBPMS_CUS_CF_MAST) ");
		//sql.append(" AND C.DATA_DATE = (SELECT DATA_DATE FROM MAX_CFDATE)  ");
		sql.append(" AND C.CUST_ID = :CUST_ID ");	
		queryCondition.setQueryString(sql.toString());
		*/
		sql.append("SELECT REC_CF_TOTAL, REC_CF_PLAN FROM MVCRM_AST_AMT WHERE CUST_ID = :CUST_ID  ");
		queryCondition.setQueryString(sql.toString());

		queryCondition.setObject("CUST_ID", inputVO.getCUST_ID());

		//IF 登入者身份為分行主管or 業務主管
		//		if (bmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
		//			sql.append("AND C.BRANCH_NBR = :BRANCH_NBR in ('01', '0A1') ");
		//			queryCondition.setObject("BRANCH_NBR", inputVO);
		//		//ELSE IF 登入者身份為區督導
		//		} else if (mbrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
		//			sql.append("AND C.BRANCH_AREA_ID = :BRANCH_AREA_ID in ('02', '0A2') ");
		//			queryCondition.setObject("BRANCH_AREA_ID", inputVO);
		//		//ELSE IF 登入者身份為業務處主管	
		//		} else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
		//			sql.append("AND C.REGION_CENTER_ID = :REGION_CENTER_ID in ('03', '0A3') ");
		//			queryCondition.setObject("REGION_CENTER_ID", inputVO);
		//		} else {
		//			sql.append("AND C.AO_CODE = :AO_CODE ");
		//			queryCondition.setObject("AO_CODE", inputVO);
		//		}

		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		sendRtnObject(return_VO);

	}

	public void cashflowArea(Object body, IPrimitiveMap header) throws JBranchException {
		//		WorkStation ws = DataManager.getWorkStation(uuid);
		//		CRM171InputVO inputVO = (CRM171InputVO) body;
		//		CRM171OutputVO return_VO = new CRM171OutputVO();
		//		dam = this.getDataAccessManager();
		//		
		//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//		StringBuffer sql = new StringBuffer();
		//		
		//		//0-理專，1-業務主管，2-主管，3-輔銷 ，4-總行
		//		sql.append("SELECT CASE WHEN PRIVILEGEID IN ('002','003') THEN 0 ");
		//		sql.append("WHEN PRIVILEGEID = '009' THEN 1 ");
		//		sql.append("WHEN PRIVILEGEID IN ('010','011','012','013') THEN 2 ");
		//		sql.append("WHEN PRIVILEGEID IN ('014','015','023','024') THEN 3 ELSE 4 END AS COUNTS ");
		//		sql.append("FROM TBSYSSECUROLPRIASS ");
		//		sql.append("WHERE 1 = 1 ");  
		//		sql.append("AND ROLEID = :roleID ");
		//		
		//		queryCondition.setQueryString(sql.toString());
		//		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		//		//===
		//		
		//		List<Map<String, Object>> privilege = dam.exeQuery(queryCondition);
		//		
		//		sql = new StringBuffer();	
		//		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//		if(!"0".equals(privilege.get(0).get("COUNTS").toString())){
		//		sql.append("WITH BASE AS　( ");
		//		//if業務主管
		//		if("1".equals(privilege.get(0).get("COUNTS").toString())){
		//			sql.append(" SELECT MEM.EMP_ID, MEM.EMP_NAME, AO.AO_CODE ");
		//			sql.append(" FROM TBORG_MEMBER MEM, TBORG_SALES_AOCODE AO ");
		//			sql.append(" WHERE MEM.EMP_ID = AO.EMP_ID ");
		//			sql.append(" AND (DEPT_ID, GROUP_TYPE) IN (SELECT DEPT_ID, GROUP_TYPE FROM TBORG_MEMBER WHERE EMP_ID = :emp_id ) ");
		//			sql.append(" AND AO.TYPE = '1' "); 
		//			queryCondition.setObject("emp_id", ws.getUser().getUserID());
		//		}
		//	
		//		//if主管，轄下理專
		//		if("2".equals(privilege.get(0).get("COUNTS").toString())||"4".equals(privilege.get(0).get("COUNTS").toString())){
		//			sql.append("SELECT A.AO_CODE, A.EMP_ID, A.EMP_NAME ");
		//			sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO A WHERE 1=1 AND A.AO_CODE <> A.EMP_ID ");
		//			if((getUserVariable(FubonSystemVariableConsts.LOGIN_REGION))!=null){
		//				sql.append("  AND A.REGION_CENTER_ID = :rc_id ");
		//				queryCondition.setObject("rc_id", getUserVariable(FubonSystemVariableConsts.LOGIN_REGION));
		//			}else{
		//				sql.append(" AND A.REGION_CENTER_ID IN (:rc_List ) ");
		//				queryCondition.setObject("rc_List", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		//			}
		//			if((getUserVariable(FubonSystemVariableConsts.LOGIN_AREA))!=null){
		//				sql.append(" AND A.BRANCH_AREA_ID = :op_id ");
		//				queryCondition.setObject("op_id", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
		//			}else{
		//				sql.append(" AND A.BRANCH_AREA_ID IN (:op_List ) ");
		//				queryCondition.setObject("op_List", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		//			}
		//			if(!"0000".equals(getUserVariable(FubonSystemVariableConsts.LOGINBRH))){
		//				sql.append(" AND A.BRANCH_NBR = :br_id ");
		//				queryCondition.setObject("br_id", getUserVariable(FubonSystemVariableConsts.LOGINBRH));
		//			}else{
		//				sql.append(" AND A.BRANCH_NBR IN (:br_List ) ");
		//				queryCondition.setObject("br_List", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		//			}
		//			
		//		}
		//		//if輔銷人員，轄下分行理專
		//		if("3".equals(privilege.get(0).get("COUNTS").toString())){	
		//		
		//			sql.append("SELECT FAIA.EMP_ID, FAIA.BRANCH_NBR, INFO.* ");
		//			sql.append("FROM TBORG_MEMBER_ROLE MEMR ");
		//			sql.append("LEFT JOIN TBORG_FAIA FAIA ON MEMR.EMP_ID = FAIA.EMP_ID ");
		//			sql.append("LEFT JOIN VWORG_AO_INFO INFO ON INFO.BRA_NBR = FAIA.BRANCH_NBR ");
		//			sql.append("WHERE MEMR.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('014','015','023','024') ) ");
		//			sql.append("AND MEMR.EMP_ID = :emp_id ) ");
		//			queryCondition.setObject("emp_id", ws.getUser().getUserID());
		//		}
		//		
		//		sql.append(" ) ");
		//		
		//		}
		//		sql.append("SELECT NVL((SUM(A.DEPOSIT_AMT) + SUM(A.BOND_AMT) + SUM(A.NEW_INS_CF) + SUM(A.INTEREST_INS_CF)),0) AS CASHFLOW_A , TO_CHAR(SYSDATE,'YYYY/MM') AS YEARMON, NVL(B.CUST_CNT_UNPLAN,0)AS CUST_CNT_UNPLAN  ");
		//		sql.append("FROM TBPMS_UNPLAN_RPT_DTL A, TBPMS_UNPLAN_RPT_MAST B WHERE 1=1 ");
		//		if("0".equals(privilege.get(0).get("COUNTS").toString())){
		//			sql.append("AND B.EMP_ID = :emp_id ");
		//			queryCondition.setObject("emp_id", ws.getUser().getUserID());
		//		}else{
		//			sql.append("AND A.AO_CODE IN (SELECT AO_CODE FROM BASE ) ");
		//		}
		//		sql.append("AND A.AO_CODE = B.AO_CODE AND A.YEARMON = B.YEARMON " );
		//	    sql.append("AND A.YEARMON = TO_CHAR(SYSDATE,'YYYYMM') ");
		//		sql.append("GROUP BY A.YEARMON, B.CUST_CNT_UNPLAN ");
		//		
		//		queryCondition.setQueryString(sql.toString());
		//		
		//		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		//		return_VO.setResultList(list);
		//		this.sendRtnObject(return_VO);
	}

}