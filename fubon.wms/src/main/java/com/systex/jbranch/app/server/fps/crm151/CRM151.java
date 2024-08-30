package com.systex.jbranch.app.server.fps.crm151;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm151.CRM151;
import com.systex.jbranch.app.server.fps.crm151.CRM151InputVO;
import com.systex.jbranch.app.server.fps.crm151.CRM151OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;



/**
 * MENU
 * 
 * @author Stella
 * @date 2016/09/23
 * @spec null
 */
@Component("crm151")
@Scope("request")
public class CRM151 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(CRM151.class);
	
	public void login(Object body, IPrimitiveMap header) throws JBranchException {
		CRM151InputVO inputVO = (CRM151InputVO) body;
		CRM151OutputVO return_VO = new CRM151OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 0-理專、1-主管 、2-輔銷 、3-消金PS、4-PM(未談)
		sql.append("SELECT CASE WHEN PRIVILEGEID IN ('002','003') THEN 0 ");
		sql.append("	   		WHEN PRIVILEGEID IN ('009','010','011','012','013') THEN 1 ");
		sql.append("			WHEN PRIVILEGEID IN ('014','015','023','024') THEN 2 ");
		sql.append("			WHEN PRIVILEGEID ='004' THEN 3 ");
		sql.append("		ELSE 4 END AS COUNTS ");
		sql.append("		, PRIVILEGEID ");
		sql.append("FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE ROLEID = :roleID ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		return_VO.setLogin(dam.exeQuery(queryCondition));
		sendRtnObject(return_VO);
	}
	
	//理專
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CRM151InputVO inputVO = (CRM151InputVO)body;

		switch (inputVO.getRole()) {
		// 0-理專、1-主管 、2-輔銷 、3-消金PS、4-PM(未談)
		case "0":
			A();
			break;
			
		case "1":
			B(inputVO);
			break;
			
		case "2":
			break;
			
		case "3":
			break;
			
		case "4":
			break;
		}
		
		return;
	}
	
	//理專
	private void A() throws JBranchException {
		CRM151OutputVO outputVO = new CRM151OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" WITH BASE AS ( ");
		sql.append("      SELECT DATA_DATE,AO_JOB_TITLE, SUM(MTD_SUM_FEE) AS ALL_MTD_JOB, SUM(YTD_SUM_FEE) AS ALL_YTD_JOB ");
		sql.append("  		FROM TBPMS_AO_DAY_PROFIT_MYTD ");
		sql.append("	  GROUP BY DATA_DATE,AO_JOB_TITLE ");
		sql.append(" ), ");
		sql.append(" GOAL AS ( ");
		sql.append("      SELECT EMP_ID,SUM(MTD_GOAL_FLAG) AS CNT ");
		sql.append("      FROM ( SELECT EMP_ID, CASE WHEN MTD_SUM_FEE >= MTD_SUM_GOAL THEN 0 ELSE 1 END AS MTD_GOAL_FLAG, ");
		sql.append("                    ROW_NUMBER() OVER(PARTITION BY emp_id, SUBSTR(DATA_DATE,0,6) ORDER BY DATA_DATE DESC ) ROWNUMBER ");
		sql.append(" 			 FROM TBPMS_AO_DAY_PROFIT_MYTD ");
		sql.append("  			 WHERE SUBSTR(DATA_DATE,0,6) <= TO_CHAR(SYSDATE,'YYYYMM') ");
		sql.append("  			   AND SUBSTR(DATA_DATE,0,6) > TO_CHAR(ADD_MONTHS(SYSDATE,-12),'YYYYMM') ) ");
		sql.append("	  WHERE ROWNUMBER = '1' ");
		sql.append("	  GROUP BY EMP_ID ");
		sql.append(" ) ");		
		sql.append(" SELECT M.DATA_DATE ");
		sql.append("   	   ,ROUND(ALL_MTD_JOB/CNT_MTD_ALL_BY_JOB,2) AS AVG_MTD , ROUND(ALL_YTD_JOB/CNT_YTD_ALL_BY_JOB,2) AS AVG_YTD ");
		sql.append("       ,MTD_SUM_FEE, MTD_SUM_GOAL, YTD_SUM_FEE, YTD_SUM_GOAL ");
		sql.append("       ,MTD_SUM_RATE * 100 AS MTD_SUM_RATE, MTD_SUM_RATE_MM * 100 AS MTD_SUM_RATE_MM, CASE WHEN MTD_SUM_RATE >= MTD_SUM_RATE_MM THEN 'Y' ELSE 'N' END AS MTD_GOAL_FLAG ");
		sql.append("       ,RANK_MTD_ALL_BY_JOB, CNT_MTD_ALL_BY_JOB ");
		sql.append("       ,YTD_SUM_RATE * 100 AS YTD_SUM_RATE, YTD_SUM_RATE_YY * 100 AS YTD_SUM_RATE_YY, CASE WHEN YTD_SUM_RATE >= YTD_SUM_RATE_YY THEN 'Y' ELSE 'N' END AS YTD_GOAL_FLAG ");
		sql.append("       ,RANK_YTD_ALL_BY_JOB, CNT_YTD_ALL_BY_JOB ");
		sql.append("	   ,(MTD_SUM_GOAL - MTD_SUM_FEE) AS STILL_NEED ");
		sql.append("  	   ,C.CNT");
		sql.append(" FROM TBPMS_AO_DAY_PROFIT_MYTD M ");
		sql.append(" LEFT JOIN BASE B ON M.DATA_DATE = B.DATA_DATE AND M.AO_JOB_TITLE = B.AO_JOB_TITLE ");
		sql.append(" LEFT JOIN GOAL C ON M.EMP_ID = C.EMP_ID ");
		sql.append(" WHERE M.DATA_DATE = ( select MAX(DATA_DATE) from TBPMS_AO_DAY_PROFIT_MYTD where SUBSTR(DATA_DATE,0,6) = TO_CHAR(SYSDATE,'YYYYMM')) ");
		sql.append("   AND M.EMP_ID = :emp_id ");
		
		condition.setObject("emp_id",ws.getUser().getUserID());
		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		resultList = dam.exeQuery(condition);
		outputVO.setResultList(resultList);
		
		this.sendRtnObject(outputVO);
	}
	//輔銷
	public void initialA(Object body, IPrimitiveMap header) throws JBranchException {
//		WorkStation ws = DataManager.getWorkStation(uuid);
//		CRM151OutputVO return_VO = new CRM151OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		
//		sql.append("SELECT MTD_A_FEE, MTD_T_FEE, MTD_S_RATE, MTD_A_RATE, UNDER_AO_CNT, OVER_AO_CNT, ");
//		sql.append("INV_MTD_A_FEE, INV_MTD_T_FEE, INV_MTD_S_RATE, INV_MTD_A_RATE,INV_UNDER_AO_CNT,INV_OVER_AO_CNT, PRJ_LIST, TAR_LIST, ACT_LIST  ");
//		sql.append("FROM VWPMS_WKPG_SALES_SPRT ");
//		sql.append("WHERE EMP_ID = :EMP ");
//		sql.append("AND DATA_DATE = TO_CHAR(SYSDATE,'YYYYMMDD') ");
//
//		queryCondition.setQueryString(sql.toString());
//		queryCondition.setObject("EMP", ws.getUser().getUserID());
//		return_VO.setResultList1(dam.exeQuery(queryCondition));
//		sendRtnObject(return_VO);
	}
	
	//主管
	public void B(CRM151InputVO body) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM151InputVO inputVO =  (CRM151InputVO) body;
		CRM151OutputVO return_VO = new CRM151OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		if(inputVO.getPriID().equals("013") || inputVO.getPriID().equals("012")){
			//012 -- 營運督導
			//013 -- 處長
			sql.append(" WITH ORIGINAL_VIEW AS ( ");
			sql.append("     SELECT BRSAL.REGION_CENTER_ID, BRSAL.REGION_CENTER_NAME, BRSAL.BRANCH_AREA_ID, BRSAL.BRANCH_AREA_NAME ");
			sql.append("          , BRSAL.MTD_ALL_FEE, BRSAL.MTD_ALL_FEE_GOAL, BRSAL.MTD_ALL_FEE_RATE, BRSAL.MTD_ALL_FEE_RATE_MM ");
			sql.append("          , BRSAL.YTD_ALL_FEE, BRSAL.YTD_ALL_FEE_GOAL, BRSAL.YTD_ALL_FEE_RATE, BRSAL.YTD_ALL_FEE_RATE_MM ");
			sql.append("     FROM TBPMS_BR_DAY_PROFIT_MYTD BRSAL ");
			sql.append("     WHERE BRSAL.DATA_DATE = ( select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MYTD where SUBSTR(DATA_DATE,0,6) = TO_CHAR(SYSDATE,'YYYYMM')) ");
			sql.append(" ), ");
			sql.append(" RATE AS ( ");
			sql.append("     SELECT REGION_CENTER_ID,BRANCH_AREA_ID, MTD_ALL_FEE_RATE_MM, YTD_ALL_FEE_RATE_MM ");
			sql.append("     FROM ORIGINAL_VIEW ");
			sql.append("     GROUP BY REGION_CENTER_ID,BRANCH_AREA_ID, MTD_ALL_FEE_RATE_MM, YTD_ALL_FEE_RATE_MM ");
			sql.append(" ), ");
			sql.append(" AREA AS ( ");
			sql.append("   SELECT OV.REGION_CENTER_ID, OV.REGION_CENTER_NAME, OV.BRANCH_AREA_ID, BRANCH_AREA_NAME ");
			sql.append("        , SUM(MTD_ALL_FEE) as MTD_ALL_FEE, SUM(MTD_ALL_FEE_GOAL) as MTD_ALL_FEE_GOAL ");
			sql.append("        , DECODE(SUM(MTD_ALL_FEE_GOAL),0,0,ROUND(SUM(MTD_ALL_FEE)*100/SUM(MTD_ALL_FEE_GOAL),3)) as MTD_ALL_FEE_RATE ");
			sql.append("        , R.MTD_ALL_FEE_RATE_MM ");
			sql.append("        , CASE WHEN SUM(MTD_ALL_FEE)*100/SUM(MTD_ALL_FEE_GOAL) > R.MTD_ALL_FEE_RATE_MM THEN 'Y' ");
			sql.append("          ELSE 'N' END AS MTD_ALL_FEE_FLAG ");
			sql.append("        , SUM(YTD_ALL_FEE) as YTD_ALL_FEE, SUM(YTD_ALL_FEE_GOAL) as YTD_ALL_FEE_GOAL ");
			sql.append("        , DECODE(SUM(YTD_ALL_FEE_GOAL),0,0,ROUND(SUM(YTD_ALL_FEE)*100/SUM(YTD_ALL_FEE_GOAL),3)) as YTD_ALL_FEE_RATE ");
			sql.append("        , R.YTD_ALL_FEE_RATE_MM ");
			sql.append("        , CASE WHEN SUM(YTD_ALL_FEE)*100/SUM(YTD_ALL_FEE_GOAL) > R.YTD_ALL_FEE_RATE_MM THEN 'Y' ");
			sql.append("          ELSE 'N' END AS YTD_ALL_FEE_FLAG ");
			sql.append("        , RANK() OVER(PARTITION BY ORG.DEPT_GROUP ORDER BY DECODE(SUM(MTD_ALL_FEE_GOAL),0,0,SUM(MTD_ALL_FEE)/SUM(MTD_ALL_FEE_GOAL)) DESC ) AS RANK_MTD_ALL_BY_CLS ");
			sql.append("        , RANK() OVER(PARTITION BY ORG.DEPT_GROUP ORDER BY DECODE(SUM(YTD_ALL_FEE_GOAL),0,0,SUM(YTD_ALL_FEE)/SUM(YTD_ALL_FEE_GOAL)) DESC ) AS RANK_YTD_ALL_BY_CLS ");
			sql.append("   FROM ORIGINAL_VIEW OV ");
			sql.append("   LEFT JOIN RATE R ON OV.BRANCH_AREA_ID = R.BRANCH_AREA_ID ");
	        sql.append("   LEFT JOIN TBORG_DEFN ORG ON OV.BRANCH_AREA_ID = ORG.DEPT_ID AND ORG.ORG_TYPE='40' and ORG.PARENT_DEPT_ID IN ('171','172','174') ");
			sql.append("   GROUP BY OV.REGION_CENTER_NAME,OV.BRANCH_AREA_NAME,OV.REGION_CENTER_ID,OV.BRANCH_AREA_ID,ORG.DEPT_GROUP,R.MTD_ALL_FEE_RATE_MM, R.YTD_ALL_FEE_RATE_MM ");
			sql.append(" ), ");
			sql.append(" REGION AS ( ");
			sql.append("   SELECT OV.REGION_CENTER_ID, OV.REGION_CENTER_NAME ");
			sql.append("        , SUM(MTD_ALL_FEE) as MTD_ALL_FEE, SUM(MTD_ALL_FEE_GOAL) as MTD_ALL_FEE_GOAL ");
			sql.append("        , DECODE(SUM(MTD_ALL_FEE_GOAL),0,0,ROUND(SUM(MTD_ALL_FEE)*100/SUM(MTD_ALL_FEE_GOAL),3)) as MTD_ALL_FEE_RATE ");
			sql.append("        , R.MTD_ALL_FEE_RATE_MM ");
			sql.append("        , CASE WHEN SUM(MTD_ALL_FEE)*100/SUM(MTD_ALL_FEE_GOAL) > R.MTD_ALL_FEE_RATE_MM THEN 'Y' ");
			sql.append("          ELSE 'N' END AS MTD_ALL_FEE_FLAG ");
			sql.append("        , SUM(YTD_ALL_FEE) as YTD_ALL_FEE, SUM(YTD_ALL_FEE_GOAL) as YTD_ALL_FEE_GOAL ");
			sql.append("        , DECODE(SUM(YTD_ALL_FEE_GOAL),0,0,ROUND(SUM(YTD_ALL_FEE)*100/SUM(YTD_ALL_FEE_GOAL),3)) as YTD_ALL_FEE_RATE ");
			sql.append("        , R.YTD_ALL_FEE_RATE_MM ");
			sql.append("        , CASE WHEN SUM(YTD_ALL_FEE)*100/SUM(YTD_ALL_FEE_GOAL) > R.YTD_ALL_FEE_RATE_MM THEN 'Y' ");
			sql.append("          ELSE 'N' END AS YTD_ALL_FEE_FLAG ");
			sql.append("        , RANK() OVER(ORDER BY DECODE(SUM(MTD_ALL_FEE_GOAL),0,0,SUM(MTD_ALL_FEE)/SUM(MTD_ALL_FEE_GOAL)) DESC ) AS RANK_MTD_ALL_BY_CLS ");
			sql.append("        , RANK() OVER(ORDER BY DECODE(SUM(YTD_ALL_FEE_GOAL),0,0,SUM(YTD_ALL_FEE)/SUM(YTD_ALL_FEE_GOAL)) DESC ) AS RANK_YTD_ALL_BY_CLS ");
			sql.append("   FROM ORIGINAL_VIEW OV ");
			sql.append("   LEFT JOIN (SELECT DISTINCT REGION_CENTER_ID, MTD_ALL_FEE_RATE_MM, YTD_ALL_FEE_RATE_MM ");
			sql.append("              FROM RATE ");
			sql.append("              GROUP BY REGION_CENTER_ID, MTD_ALL_FEE_RATE_MM, YTD_ALL_FEE_RATE_MM) R  ");
			sql.append("   ON OV.REGION_CENTER_ID = R.REGION_CENTER_ID ");
			sql.append("   GROUP BY OV.REGION_CENTER_NAME,OV.REGION_CENTER_ID, R.MTD_ALL_FEE_RATE_MM, R.YTD_ALL_FEE_RATE_MM ");
			sql.append(" ), ");
			sql.append(" TOTAL AS ( ");
			
			if(inputVO.getPriID().equals("012")){ 	
				//分組別(DEPT_GROUP)
				sql.append(" 	SELECT SUM(MTD_ALL_FEE) as SUM_MTD_ALL_FEE, SUM(YTD_ALL_FEE) as SUM_YTD_ALL_FEE, ORG.DEPT_GROUP, COUNT(DEPT_GROUP) AS TOTAL_CNT ");
				sql.append(" 	FROM AREA OV ");
				sql.append("	LEFT JOIN TBORG_DEFN ORG ON OV.BRANCH_AREA_ID = ORG.DEPT_ID AND ORG.ORG_TYPE='40' and ORG.PARENT_DEPT_ID IN ('171','172','174') ");
				sql.append("	GROUP BY ORG.DEPT_GROUP");
			}else{
				sql.append(" 		  SELECT SUM(MTD_ALL_FEE) as SUM_MTD_ALL_FEE, SUM(YTD_ALL_FEE) as SUM_YTD_ALL_FEE ");
				sql.append(" 		  FROM ORIGINAL_VIEW ");
			}
			
		    sql.append(") ");
		    
			if(inputVO.getPriID().equals("012")){
				sql.append(" SELECT A.*,T.TOTAL_CNT ");
				sql.append("      , ROUND(T.SUM_MTD_ALL_FEE/T.TOTAL_CNT,2) AS AVG_MTD_ALL_FEE ");
				sql.append("      , ROUND(T.SUM_YTD_ALL_FEE/T.TOTAL_CNT,2) AS AVG_YTD_ALL_FEE ");
				sql.append(" FROM AREA A ");
				sql.append(" LEFT JOIN TBORG_DEFN ORG ON A.BRANCH_AREA_ID = ORG.DEPT_ID AND ORG.ORG_TYPE='40' and ORG.PARENT_DEPT_ID IN ('171','172','174') ");
				sql.append(" LEFT JOIN TOTAL T ON T.DEPT_GROUP = ORG.DEPT_GROUP ");
				sql.append(" WHERE A.BRANCH_AREA_ID = (SELECT DEPT_ID FROM TBORG_MEMBER WHERE EMP_ID = :emp_id) ");
			}else{
				sql.append(" SELECT A.*,T.TOTAL_CNT ");
				sql.append("      , ROUND(TOTAL.SUM_MTD_ALL_FEE/T.TOTAL_CNT,2) AS AVG_MTD_ALL_FEE ");
				sql.append("      , ROUND(TOTAL.SUM_YTD_ALL_FEE/T.TOTAL_CNT,2) AS AVG_YTD_ALL_FEE ");
				sql.append(" FROM REGION A ");
				sql.append(" LEFT JOIN (SELECT MAX(RANK_MTD_ALL_BY_CLS) AS TOTAL_CNT FROM REGION) T ON 1=1 ");
				sql.append(" LEFT JOIN TOTAL ON 1=1 ");
				sql.append(" WHERE A.REGION_CENTER_ID = (SELECT DEPT_ID FROM TBORG_MEMBER WHERE EMP_ID = :emp_id) ");
			}
			
			queryCondition.setObject("emp_id", ws.getUser().getUserID());
			
		}else{
			//分行主管
			sql.append(" WITH TOTAL AS ( ");
			sql.append("     SELECT SUM(MTD_ALL_FEE) as SUM_MTD_ALL_FEE, SUM(YTD_ALL_FEE) as SUM_YTD_ALL_FEE ");
			sql.append("     FROM TBPMS_BR_DAY_PROFIT_MYTD  ");
			sql.append("     WHERE DATA_DATE = ( select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MYTD where SUBSTR(DATA_DATE,0,6) = TO_CHAR(SYSDATE,'YYYYMM')) ");
			sql.append(" ) ");
			sql.append(" SELECT RANK_MTD_ALL_BY_CLS, CNT_MTD_ALL_BY_CLS, RANK_YTD_ALL_BY_CLS, CNT_YTD_ALL_BY_CLS ");
			sql.append("      , MTD_ALL_FEE_RATE, MTD_ALL_FEE_RATE_MM, MTD_ALL_FEE, MTD_ALL_FEE_GOAL ");
			sql.append("      , CASE WHEN MTD_ALL_FEE_RATE > MTD_ALL_FEE_RATE_MM THEN 'Y' ELSE 'N' END AS MTD_ALL_FEE_FLAG ");
			sql.append("      , YTD_ALL_FEE_RATE, YTD_ALL_FEE_RATE_MM, YTD_ALL_FEE, YTD_ALL_FEE_GOAL ");
			sql.append("      , CASE WHEN YTD_ALL_FEE_RATE > YTD_ALL_FEE_RATE_MM THEN 'Y' ");
			sql.append("        ELSE 'N' END AS YTD_ALL_FEE_FLAG ");
			sql.append("      , ROUND(TOTAL.SUM_MTD_ALL_FEE/CNT_MTD_ALL_BY_ALL,2) AS AVG_MTD_ALL_FEE ");
			sql.append("      , ROUND(TOTAL.SUM_YTD_ALL_FEE/CNT_YTD_ALL_BY_ALL,2) AS AVG_YTD_ALL_FEE ");
			sql.append(" FROM TBPMS_BR_DAY_PROFIT_MYTD  ");
			sql.append(" LEFT JOIN TOTAL ON 1=1 ");
			sql.append(" WHERE DATA_DATE = ( select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MYTD where SUBSTR(DATA_DATE,0,6) = TO_CHAR(SYSDATE,'YYYYMM')) ");
			sql.append("   AND BRANCH_NBR = :branch ");
			
			queryCondition.setObject("branch", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
	
	public void initialC(Object body, IPrimitiveMap header) throws JBranchException {
		
//		WorkStation ws = DataManager.getWorkStation(uuid);
//		CRM151OutputVO return_VO = new CRM151OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		
//		sql.append("SELECT MTD_MRTG_RATE, MTD_TRUST_RATE, MTD_E_RATE,MTD_CREDIT_RATE, ");
//		sql.append("YTD_MRTG_RATE, YTD_TRUST_RATE, YTD_E_RATE,YTD_CREDIT_RATE, MTD_MRTG_RANK,MTD_TRUST_RANK, MTD_E_RANK,MTD_CREDIT_RANK,  ");
//		sql.append("YTD_MRTG_RANK, YTD_TRUST_RANK, YTD_E_RANK, YTD_CREDIT_RANK ");
//		sql.append("FROM VWPMS_WKPG_SALES_MNGR ");
//		sql.append("WHERE EMP_ID = :EMP ");
//		sql.append("AND DATA_DATE = TO_CHAR(SYSDATE,'YYYYMMDD') ");
//
//		queryCondition.setQueryString(sql.toString());
//		queryCondition.setObject("EMP", ws.getUser().getUserID());
//		return_VO.setResultList6(dam.exeQuery(queryCondition));
//		sendRtnObject(return_VO);
		
		
	}
	
	//近一年未達目標月份數( 主管/輔銷人員/輔銷科長)
	public void detail(Object body, IPrimitiveMap header) throws JBranchException {
//		WorkStation ws = DataManager.getWorkStation(uuid);
//		CRM151InputVO inputVO = (CRM151InputVO) body;
//		CRM151OutputVO return_VO = new CRM151OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		
//		sql.append("WITH BASE AS　( ");
//		//IF分行，轄下理專
//		if("1".equals(inputVO.getRole())){
//			sql.append("SELECT A.AO_CODE, A.EMP_ID, A.EMP_NAME ");
//			sql.append(" FROM VWORG_AO_INFO A ");
//			sql.append(" WHERE BRA_NBR IN ( SELECT E.DEPT_ID  ");
//			sql.append(" FROM TBORG_MEMBER E, ");
//			sql.append("TBORG_DEFN D, ");
//			sql.append(" TBORG_MEMBER_ROLE R ");
//			sql.append("  WHERE E.EMP_ID = R.EMP_ID ");
//			sql.append("  AND E.DEPT_ID = D.DEPT_ID ");
//			sql.append("  AND D.ORG_TYPE = '50'");
//			sql.append("  AND R.ROLE_ID IN (select roleid from TBSYSSECUROLPRIASS where privilegeid='011') ");
//			sql.append("  AND E.EMP_ID = :EMP) ");
//		}
//		//IF輔銷人員，轄下分行理專
//		if("2".equals(inputVO.getRole())){	
//		
//		sql.append("SELECT FAIA.EMP_ID, FAIA.BRANCH_NBR, INFO.* ");
//		sql.append("FROM TBORG_MEMBER_ROLE MEMR ");
//		sql.append("LEFT JOIN TBORG_FAIA FAIA ON MEMR.EMP_ID = FAIA.EMP_ID ");
//		sql.append("LEFT JOIN VWORG_AO_INFO INFO ON INFO.BRA_NBR = FAIA.BRANCH_NBR ");
//		sql.append("WHERE MEMR.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('014','015','023','024') ) ");
//		sql.append("AND MEMR.EMP_ID = :EMP ) ");
//		}
//		sql.append(" ) ");
//		
//		sql.append("SELECT A.REGION_CENTER_NAME, A.BRANCH_NAME, A.AO_CODE,A.EMP_NAME, ");
//		sql.append("B.ONE_A_FEE, B.ONE_T_FEE, C.TWO_A_FEE, C.TWO_T_FEE, ");
//		sql.append("D.THREE_A_FEE, D.THREE_T_FEE, E.FOUR_A_FEE, E.FOUR_T_FEE, ");
//		sql.append("F.FIVE_A_FEE, F.FIVE_T_FEE, G.SIX_A_FEE, G.SIX_T_FEE, ");
//		sql.append("H.SEVEN_A_FEE, H.SEVEN_T_FEE, I.EIGHT_A_FEE, I.EIGHT_T_FEE, ");
//		sql.append("J.NINE_A_FEE,J.NINE_T_FEE, K.TEN_A_FEE, K.TEN_T_FEE,  ");
//		sql.append("L.ELE_A_FEE, L.ELE_T_FEE, M.TWE_A_FEE, M.TWE_T_FEE, ");
//		sql.append("(SUM(B.ONE_A_FEE + C.TWO_A_FEE + D.THREE_A_FEE + E.FOUR_A_FEE + F.FIVE_A_FEE + G.SIX_A_FEE + H.SEVEN_A_FEE + I.EIGHT_A_FEE +  ");
//		sql.append("J.NINE_A_FEE + K.TEN_A_FEE + L.ELE_A_FEE + M.TWE_A_FEE)/12) AS AVG_FEE ");
//		sql.append("FROM  ");
//		sql.append("(SELECT REGION_CENTER_NAME, BRANCH_NAME, AO_CODE, EMP_ID, ");
//		sql.append("EMP_NAME ");
//		sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
//		sql.append("WHERE 1=1  ");
//		sql.append("AND EMP_ID IN (SELECT EMP_ID FROM BASE) )A, ");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS ONE_A_FEE, MTD_T_FEE AS ONE_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-11))),'YYYYMMDD'))B ON A.EMP_ID = B.EMP_ID ");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS TWO_A_FEE, MTD_T_FEE AS TWO_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-10))),'YYYYMMDD'))C ON A.EMP_ID = C.EMP_ID ");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS THREE_A_FEE, MTD_T_FEE AS THREE_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-9))),'YYYYMMDD'))D ON A.EMP_ID = D.EMP_ID ");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS FOUR_A_FEE, MTD_T_FEE AS FOUR_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE)AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-8))),'YYYYMMDD'))E ON A.EMP_ID = E.EMP_ID");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS FIVE_A_FEE, MTD_T_FEE AS FIVE_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-7))),'YYYYMMDD'))F ON A.EMP_ID = F.EMP_ID");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS SIX_A_FEE, MTD_T_FEE AS SIX_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-6))),'YYYYMMDD'))G ON A.EMP_ID = G.EMP_ID");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS SEVEN_A_FEE, MTD_T_FEE AS SEVEN_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-5))),'YYYYMMDD'))H ON A.EMP_ID = H.EMP_ID");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS EIGHT_A_FEE, MTD_T_FEE AS EIGHT_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-4))),'YYYYMMDD'))I ON A.EMP_ID = I.EMP_ID");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS NINE_A_FEE, MTD_T_FEE AS NINE_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-3))),'YYYYMMDD'))J ON A.EMP_ID = J.EMP_ID");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS TEN_A_FEE, MTD_T_FEE AS TEN_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-2))),'YYYYMMDD'))K ON A.EMP_ID = K.EMP_ID");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS ELE_A_FEE, MTD_T_FEE AS ELE_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1 AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-1))),'YYYYMMDD'))L ON A.EMP_ID = L.EMP_ID");
//		sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS TEW_A_FEE, MTD_T_FEE AS TWE_T_FEE FROM VWPMS_WKPG_SALES_AO ");
//		sql.append("WHERE 1=1AND EMP_ID IN (SELECT EMP_ID FROM BASE) AND DATA_DATE =TO_CHAR(SYSDATE),'YYYYMMDD'))M ON A.EMP_ID = M.EMP_ID");
//
//		queryCondition.setQueryString(sql.toString());
//		queryCondition.setObject("EMP", ws.getUser().getUserID());
//		return_VO.setResultList3(dam.exeQuery(queryCondition));
//		sendRtnObject(return_VO);
		
	}
	
	//近一年未達目標月份數明細(主管/輔銷人員/輔銷科長)
	public void detaila(Object body, IPrimitiveMap header) throws JBranchException {
//			WorkStation ws = DataManager.getWorkStation(uuid);
//			CRM151InputVO inputVO = (CRM151InputVO) body;
//			CRM151OutputVO return_VO = new CRM151OutputVO();
//			dam = this.getDataAccessManager();
//			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			StringBuffer sql = new StringBuffer();
//	
//			sql.append(" WITH BASE AS　( ");
//			sql.append("SELECT A.EMP_ID, A.ONE_A_FEE, A.ONE_T_FEE, C.TWO_A_FEE, C.TWO_T_FEE, D.THREE_A_FEE, D.THREE_T_FEE, E.FOUR_A_FEE, E.FOUR_T_FEE, ");
//			sql.append("F.FIVE_A_FEE, F.FIVE_T_FEE, G.SIX_A_FEE, G.SIX_T_FEE, H.SEVEN_A_FEE, H.SEVEN_T_FEE, I.EIGHT_A_FEE, I.EIGHT_T_FEE, ");
//			sql.append("J.NINE_A_FEE,J.NINE_T_FEE, K.TEN_A_FEE, K.TEN_T_FEE, L.ELE_A_FEE, L.ELE_T_FEE, M.TWE_A_FEE, M.TWE_T_FEE, ");
//			sql.append("(SUM(A.ONE_A_FEE+ C.TWO_A_FEE+ D.THREE_A_FEE+ E.FOUR_A_FEE+ F.FIVE_A_FEE+G.SIX_A_FEE+ H.SEVEN_A_FEE+ I.EIGHT_A_FEE+J.NINE_A_FEE+ K.TEN_A_FEE+L.ELE_A_FEE+ M.TWE_A_FEE)/12) AS AVG_FEE "); 
//			sql.append(" FROM ( ");
//			sql.append("SELECT EMP_ID, MTD_A_FEE AS ONE_A_FEE, MTD_T_FEE AS ONE_T_FEE  ");
//			sql.append("FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1' EMP_ID =:EMP AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-11))),'YYYYMMDD'))A  ");
//			sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS TWO_A_FEE, MTD_T_FEE AS TWO_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1 AND EMP_ID = :EMP AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-10))),'YYYYMMDD'))C ON A.EMP_ID = C.EMP_ID ");
//			sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS THREE_A_FEE, MTD_T_FEE AS THREE_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1 AND EMP_ID = :EMP AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-9))),'YYYYMMDD'))D ON A.EMP_ID = D.EMP_ID ");
//			sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS FOUR_A_FEE, MTD_T_FEE AS FOUR_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1 AND EMP_ID = :EMP AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-8))),'YYYYMMDD'))E ON A.EMP_ID = E.EMP_ID ");
//			sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS FIVE_A_FEE, MTD_T_FEE AS FIVE_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1 AND EMP_ID = :EMP AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-7))),'YYYYMMDD'))F ON A.EMP_ID = F.EMP_ID  ");
//			sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS SIX_A_FEE, MTD_T_FEE AS SIX_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1 AND EMP_ID = :EMP AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-6))),'YYYYMMDD'))G ON A.EMP_ID = G.EMP_ID ");
//			sql.append(" LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS SEVEN_A_FEE, MTD_T_FEE AS SEVEN_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append(" WHERE 1=1 	AND EMP_ID =:EMP AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-5))),'YYYYMMDD'))H ON A.EMP_ID = H.EMP_ID ");
//			sql.append("LEFT JOIN  (SELECT EMP_ID, MTD_A_FEE AS EIGHT_A_FEE, MTD_T_FEE AS EIGHT_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append(" WHERE 1=1 	AND EMP_ID = :EMP  AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-4))),'YYYYMMDD'))I ON A.EMP_ID = I.EMP_ID ");
//			sql.append(" LEFT JOIN 	(SELECT EMP_ID, MTD_A_FEE AS NINE_A_FEE, MTD_T_FEE AS NINE_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append(" WHERE 1=1 	AND EMP_ID = :EMP AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-3))),'YYYYMMDD'))J ON A.EMP_ID =J.EMP_ID  ");
//			sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS TEN_A_FEE, MTD_T_FEE AS TEN_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1 AND EMP_ID =:EMP 	AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-2))),'YYYYMMDD'))K ON A.EMP_ID = K.EMP_ID ");
//			sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS ELE_A_FEE, MTD_T_FEE AS ELE_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1 	AND EMP_ID = :EMP 	AND DATA_DATE =TO_CHAR(TRUNC(LAST_DAY(ADD_MONTHS (SYSDATE,-1))),'YYYYMMDD')L ON A.EMP_ID = L.EMP_ID ");
//			sql.append("LEFT JOIN (SELECT EMP_ID, MTD_A_FEE AS TEW_A_FEE, MTD_T_FEE AS TWE_T_FEE FROM VWPMS_WKPG_SALES_MNGR ");
//			sql.append("WHERE 1=1 AND EMP_ID = :EMP AND DATA_DATE =TO_CHAR(SYSDATE),'YYYYMMDD'))M ON A.EMP_ID = M.EMP_ID )) ");
//
//			sql.append(" SELECT INFO.REGION_CENTER_NAME, INFO.BRANCH_NAME, BASE.ONE_A_FEE, BASE.ONE_T_FEE, BASE.TWO_A_FEE, BASE.TWO_T_FEE,BASE.THREE_A_FEE, BASE.THREE_T_FEE, ");
//			sql.append(" BASE.FOUR_A_FEE, BASE.FOUR_T_FEE, BASE.FIVE_A_FEE, BASE.FIVE_T_FEE, BASE.SIX_A_FEE, BASE.SIX_T_FEE,BASE.SEVEN_A_FEE, BASE.SEVEN_T_FEE, BASE.EIGHT_A_FEE, ");
//			sql.append(" BASE.EIGHT_T_FEE,BASE.NINE_A_FEE, BASE.NINE_T_FEE, BASE.TEN_A_FEE, BASE.TEN_T_FEE, BASE.ELE_A_FEE, BASE.ELE_T_FEE, BASE.TWE_A_FEE, BASE.TWE_T_FEE,BASE.AVG_FEE ");
//			sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
//			sql.append("LEFT JOIN BASE BASE ON BASE.EMP_ID = BASE.EMP_ID ");
//			sql.append("WHERE 1=1 AND INFO. EMP_ID = :EMP ");
//
//			
//			queryCondition.setQueryString(sql.toString());
//			queryCondition.setObject("EMP", ws.getUser().getUserID());
//			return_VO.setResultList4(dam.exeQuery(queryCondition));
//			sendRtnObject(return_VO);
			
		}
			
	//近一年未達目標月份數明細(理專)
	public void detailb(Object body, IPrimitiveMap header) throws JBranchException {
		CRM151InputVO inputVO = (CRM151InputVO) body;
		CRM151OutputVO return_VO = new CRM151OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" WITH BASE AS ( ");
		sql.append("     SELECT RN.EMP_ID, RN.DATA_DATE, RN.MTD_SUM_FEE, RN.MTD_SUM_GOAL ");
		sql.append("     FROM (SELECT  EMP_ID, DATA_DATE, ");
		sql.append("                   MTD_SUM_FEE, MTD_SUM_GOAL, ");
		sql.append("                   ROW_NUMBER() OVER(PARTITION BY emp_id, SUBSTR(DATA_DATE,0,6) ORDER BY DATA_DATE DESC ) ROWNUMBER ");
		sql.append("           FROM TBPMS_AO_DAY_PROFIT_MYTD ");
		sql.append("           WHERE SUBSTR(DATA_DATE,0,6) <= TO_CHAR(SYSDATE,'YYYYMM') ");
		sql.append("             AND SUBSTR(DATA_DATE,0,6) > TO_CHAR(ADD_MONTHS(SYSDATE,-12),'YYYYMM') ");
		sql.append("     )RN ");
		sql.append("     WHERE ROWNUMBER = '1' ");
		sql.append(" ), ");
		sql.append(" BASE_A AS ( ");
		sql.append("     SELECT EMP_ID, SUM(MTD_SUM_FEE) AS SUM ");
		sql.append("     FROM BASE ");
		sql.append("     GROUP BY EMP_ID ");
		sql.append(" ) ");
		sql.append(" SELECT  M.EMP_NAME, M.DEPT_ID, D.BRANCH_AREA_NAME, D.BRANCH_NAME, B.EMP_ID, ");
		sql.append("  		 SUBSTR(B.DATA_DATE,0,6) AS DATA_DATE, B.MTD_SUM_FEE, B.MTD_SUM_GOAL,A.SUM/12 AS AVG ");
		sql.append(" FROM BASE B ");
		sql.append(" LEFT JOIN BASE_A A ON A.EMP_ID = B.EMP_ID ");
		sql.append(" LEFT JOIN TBORG_MEMBER M ON B.EMP_ID = M.EMP_ID ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO D ON M.DEPT_ID = D.BRANCH_NBR ");
		sql.append(" WHERE B.EMP_ID = :emp_id ");

//		queryCondition.setObject("emp_id", inputVO.getEmp_id());
		queryCondition.setObject("emp_id",ws.getUser().getUserID());
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
				
	}
	
}
