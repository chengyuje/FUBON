package com.systex.jbranch.app.server.fps.crm121;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Stella
 * @date 2016/09/12
 * 
 * Mark: 20170106 Stella 1.編輯add提醒處理狀態
 */
@Component("crm121")
@Scope("request")
public class CRM121 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	// 2018/4/17 財神爺
	public void fortuna(Object body, IPrimitiveMap header) throws Exception {
		
		CRM121OutputVO return_VO = new CRM121OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();

		// 待規劃客戶數
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ma.CUST_ID, ");
		sql.append("       ma.CUST_NAME, ");
		sql.append("       NVL(li.AUM, 0) as AUM, ");
		sql.append("       NVL(li.DEPOSIT_AMT_NO_CK, 0) as DEPOSIT_AMT_NO_CK, ");
		sql.append("       NVL(li.INS_YEAR_AMT_1, 0) + NVL(li.INS_YEAR_AMT_2, 0) as INS_YEAR_AMT, ");
		sql.append("       NVL(li.LN_YEAR_AMT_1, 0) + NVL(li.LN_YEAR_AMT_2, 0) + NVL(li.LN_YEAR_AMT_3, 0) as LN_YEAR_AMT ");
		sql.append("FROM tbcrm_cust_mast ma ");
		sql.append("inner join tbfps_cust_list li on li.cust_id = ma.cust_id ");
		sql.append("where ma.ao_code in (select ao_code from vworg_ao_info where emp_id = :emp_id) ");
		sql.append("and li.FPS_YN = 'Y' ");
		
		queryCondition.setObject("emp_id", ws.getUser().getUserID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);

		// 待執行交易客戶
		List<Map<String, Object>> list2 = pendingTran(ws.getUser().getUserID());
		List<Map<String, Object>> resultList2 = new ArrayList<Map<String, Object>>();

		Set set = new HashSet();
		for (Map<String, Object> map : list2) {
			if (map.get("CUST_ID") != null) {
				set.add(map.get("CUST_ID").toString());
			}
		}

		Iterator<Object> it = set.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("CUST_ID", it.next());
			resultList2.add(map);
		}
		
		return_VO.setResultList2(resultList2);
		
		this.sendRtnObject(return_VO);
	}

	public void fortunaGetfunc(Object body, IPrimitiveMap header) throws Exception {
		
		CRM121OutputVO return_VO = new CRM121OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		return_VO.setResultList(pendingTran(ws.getUser().getUserID()));
		
		this.sendRtnObject(return_VO);
	}

	//查詢(全資產/目標理財)待執行交易
	private List<Map<String, Object>> pendingTran(String emp_id) throws Exception {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("WITH TXN AS (");
		sql.append("  select * from tbsysparameter where param_type = 'FPS.TXN_TYPE'");
		sql.append("), ");
		sql.append("INV_BASE AS ( ");
		sql.append("  SELECT HE.PLAN_ID, HE.CREATOR, HE.CUST_ID, MA.CUST_NAME, '全資產規劃' AS TITLE, DE.PRD_ID, DE.PRD_NAME, TXN.PARAM_NAME AS TXN_TYPE, DE.TRUST_CURR, DE.PURCHASE_ORG_AMT, TRUNC(DE.LASTUPDATE)  + 14 - TRUNC(SYSDATE) AS DAYS ");
		sql.append("  FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD HE ");
		sql.append("  LEFT JOIN TBFPS_PORTFOLIO_PLAN_INV DE ON HE.PLAN_ID = DE.PLAN_ID ");
		sql.append("  LEFT JOIN TBCRM_CUST_MAST MA ON MA.CUST_ID = HE.CUST_ID ");
		sql.append("  LEFT JOIN TXN ON DE.TXN_TYPE = TXN.PARAM_CODE ");
		sql.append("  INNER JOIN (");
		sql.append("    SELECT COUNT(*) COUNTINV, PLAN_ID ");
		sql.append("    FROM TBFPS_PORTFOLIO_PLAN_INV ");
		sql.append("    WHERE NVL(PURCHASE_TWD_AMT_ORDER,0) > 0 ");
		sql.append("    AND (NVL(PURCHASE_TWD_AMT,0) - NVL(PURCHASE_TWD_AMT_ORDER,0) = 0) ");
		sql.append("    GROUP BY PLAN_ID");
		sql.append("  ) TMP ON TMP.PLAN_ID = HE.PLAN_ID ");
		sql.append("  WHERE HE.CREATOR = :emp_id ");
		sql.append("  AND SUBSTR(NVL(HE.VALID_FLAG, 'Y'), 0, 1) <> 'N' ");
		sql.append("  AND (NVL(PURCHASE_TWD_AMT,0) - NVL(PURCHASE_TWD_AMT_ORDER,0) > 0) ");
		sql.append("  AND NOT(DE.INV_PRD_TYPE = '1' AND DE.INV_PRD_TYPE_2 in ('1' ,'2')) ");
		sql.append("  AND TRUNC(DE.LASTUPDATE)  + 14 - TRUNC(SYSDATE) > 0");
		sql.append("), ");
		sql.append("SPP_BASE AS ( ");
		sql.append("  SELECT HE.PLAN_ID, HE.CREATOR, HE.CUST_ID, MA.CUST_NAME, INV_PLAN_NAME AS TITLE, DE.PRD_ID, VW.PNAME, TXN.PARAM_NAME AS TXN_TYPE, DE.TRUST_CURR, DE.PURCHASE_ORG_AMT, TRUNC(DE.LASTUPDATE)  + 14 - TRUNC(SYSDATE) AS DAYS ");
		sql.append("  FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE ");
		sql.append("  LEFT JOIN TBFPS_PORTFOLIO_PLAN_SPP DE ON HE.PLAN_ID = DE.PLAN_ID ");
		sql.append("  LEFT JOIN TBCRM_CUST_MAST MA ON MA.CUST_ID = HE.CUST_ID ");
		sql.append("  LEFT JOIN VWPRD_MASTER VW ON VW.PRD_ID = DE.PRD_ID AND VW.PTYPE = DE.PTYPE ");
		sql.append("  LEFT JOIN TXN ON DE.TXN_TYPE = TXN.PARAM_CODE ");
		sql.append("  INNER JOIN ( ");
		sql.append("    SELECT COUNT(*) COUNTINV, PLAN_ID ");
		sql.append("    FROM TBFPS_PORTFOLIO_PLAN_SPP ");
		sql.append("    WHERE NVL(PURCHASE_TWD_AMT_ORDER,0) > 0 ");
		sql.append("    AND (NVL(PURCHASE_TWD_AMT,0) - NVL(PURCHASE_TWD_AMT_ORDER,0) = 0) ");
		sql.append("    GROUP BY PLAN_ID");
		sql.append("  ) TMP ON TMP.PLAN_ID = HE.PLAN_ID ");
		sql.append("  WHERE HE.CREATOR = :emp_id ");
		sql.append("  AND SUBSTR(NVL(HE.VALID_FLAG, 'Y'), 0, 1) <> 'N' ");
		sql.append("  AND (NVL(PURCHASE_TWD_AMT,0) - NVL(PURCHASE_TWD_AMT_ORDER,0) > 0) ");
		sql.append("  AND TRUNC(DE.LASTUPDATE)  + 14 - TRUNC(SYSDATE) > 0");
		sql.append(") ");
		sql.append("SELECT * ");
		sql.append("FROM ( ");
		sql.append("  SELECT * FROM INV_BASE ");
		sql.append("  UNION ");
		sql.append("  SELECT * FROM SPP_BASE ");
		sql.append(") ORDER BY CUST_ID, CASE WHEN TITLE = '目標理財規劃' THEN 0 ELSE 1 END, PLAN_ID, DAYS ");
		
		queryCondition.setObject("emp_id", ws.getUser().getUserID());
		
		queryCondition.setQueryString(sql.toString());
		
		return dam.exeQuery(queryCondition);
	}

	private StringBuffer login_sql(QueryConditionIF queryCondition) throws Exception {
		
		// 1-理專 0-輔消 、2分行以上、3-總行
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT CASE WHEN PRIVILEGEID IN ('001', '002', '003') THEN 1 ");
		sql.append("            WHEN PRIVILEGEID IN ('014', '015', '023', '024') THEN 0 ");
		sql.append("            WHEN PRIVILEGEID IN ('009', '010', '011', '012', '013') THEN 2 ");
		sql.append("       ELSE 3 END AS COUNTS ");
		sql.append("FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE ROLEID = :roleID ");
		queryCondition.setObject("roleID", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE));

		return sql;
	}

	public void login(Object body, IPrimitiveMap header) throws Exception {
		
		CRM121OutputVO return_VO = new CRM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = login_sql(queryCondition);
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> PriID = dam.exeQuery(queryCondition);
		
		return_VO.setPrivilege(PriID.get(0).get("COUNTS").toString());

		this.sendRtnObject(return_VO);
	}

	// 2018/5/24 test
	public String api_login() throws Exception {
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		queryCondition.setQueryString(login_sql(queryCondition).toString());
		
		List<Map<String, Object>> PriID = dam.exeQuery(queryCondition);

		return PriID.get(0).get("COUNTS").toString();
	}

	//行事曆
	public void getTodo(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM121InputVO inputVO = (CRM121InputVO) body;
		CRM121OutputVO return_vo = new CRM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		/**
		 * 業務排程 適用人員 : 分行以上
		 */
		if ("2".equals(inputVO.getPrivilege())) {
			sql.append("WITH AO_List AS ( ");
			sql.append(" SELECT AO_CODE, EMP_ID, EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE BRANCH_NBR IN (:br_id ) AND AO_CODE IS NOT NULL ");
			sql.append(") ");

			sql.append("SELECT null AS CUST_ID , null AS CUST_NAME , '待主管評估'AS TITLE, FIN_TYPE AS STATUS, TRUNC(SCH.COACH_DATE) AS DATETIME,  '0800' AS STIME, '0900'AS ETIME, SCH.EMP_ID, INFO.EMP_NAME  ");
			sql.append("FROM TBPMS_MNGR_EVAL_SCHEDULE SCH ");
			sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON SCH.EMP_ID = INFO.EMP_ID ");
			sql.append("WHERE 1 = 1  ");
			sql.append("AND SCH.REGION_CENTER_ID IN (:rcIdList ) ");
			sql.append("AND SCH.BRANCH_AREA_ID IN (:op_id ) ");
			sql.append("AND SCH.BRANCH_ID IN (:br_id ) ");
			sql.append("AND SCH.EMP_ID IN (SELECT EMP_ID FROM AO_List) ");

			sql.append("UNION ALL ");

			sql.append("SELECT TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_TITLE AS TITLE, 'I' AS STATUS, TRUNC(TASK.TASK_DATE) AS DATETIME, TASK.TASK_STIME AS STIME, TASK.TASK_ETIME AS ETIME, TASK.EMP_ID, D.EMP_NAME ");
			sql.append("FROM TBCAM_CAL_SALES_TASK TASK ");
			sql.append("LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID ");
			sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D ON D.EMP_ID = TASK.EMP_ID ");
			sql.append("WHERE TASK.EMP_ID = :EMP ");

			sql.append("UNION ALL ");

			//			// 呼叫⟪WMS-TDS-PMS103_檢視銷售計劃明細.docx ⟫.query()
			sql.append("SELECT CUST_ID, CUST_NAME, TITLE, STATUS, DATETIME, STIME, ETIME, EMP_ID, EMP_NAME ");
			sql.append("FROM ( ");
			sql.append("  SELECT CUST_ID,  CUST_NAME, TITLE, STATUS, DATETIME, STIME, ETIME, OS_FLAG, PFD_FLAG, BE_FLAG, EMP_ID, EMP_NAME ");
			sql.append("  FROM ( ");

			// --M日期
			sql.append("    SELECT TPS.CUST_ID, ");
			sql.append("           TPS.CUST_NAME, ");
			sql.append("           TPS.EST_PRD AS TITLE, ");
			sql.append("           'M' AS STATUS, ");
			sql.append("           TRUNC(TPS.MEETING_DATE) AS DATETIME, ");
			sql.append("           '0800' AS STIME, ");
			sql.append("           '1800' AS ETIME, ");
			sql.append("           'N' AS OS_FLAG, ");
			sql.append("           'N' AS PFD_FLAG, ");
			sql.append("           'N' AS BE_FLAG, ");
			sql.append("           TPS.EMP_ID, ");
			sql.append("           INFO.EMP_NAME ");
			sql.append("    FROM TBPMS_SALES_PLANS TPS ");
			sql.append("    LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON TPS.EMP_ID = INFO.EMP_ID ");
			sql.append("    WHERE TPS.EMP_ID IN ( SELECT EMP_ID FROM AO_List ) ");

			//			if (ARMGR.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			//				sql.append("    AND TPS.PFD_FLAG = 'Y' ");
			//			} else if (MBRMGR.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			//				sql.append("    AND TPS.OS_FLAG = 'Y' ");
			//			} else if (BMMGR.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			//				sql.append("    AND TPS.BE_FLAG = 'Y' ");
			//			}

			sql.append("  ) ");
			sql.append(") ");

			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("EMP", ws.getUser().getUserID());
			queryCondition.setObject("op_id", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("br_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));

			/**
			 * 待辦行事曆 適用人員: 理專 、輔消 、總行 僅可看到自己的待辦行事曆
			 */
		} else {
			// A/M/C
			sql.append("WITH BASE AS ( ");
			sql.append("  SELECT CUST_ID, CUST_NAME, TITLE, STATUS, DATETIME, STIME, ETIME ");
			sql.append("  FROM ( ");

			// --A日期
			sql.append("   SELECT CUST_ID, ");
			sql.append("          CUST_NAME, ");
			sql.append("          EST_PRD AS TITLE, ");
			sql.append("          (CASE WHEN TRUNC(ACTION_DATE) = TRUNC(ACTION_DATE) THEN 'A' END) AS STATUS, ");
			sql.append("          TRUNC(ACTION_DATE) AS DATETIME, ");
			sql.append("          '0800' AS STIME, ");
			sql.append("          '0900' AS ETIME ");
			sql.append("   FROM TBPMS_SALES_PLANS ");
			sql.append("   WHERE EMP_ID = :EMP ");

			sql.append("   UNION ALL ");

			// --M日期
			sql.append("   SELECT CUST_ID, ");
			sql.append("          CUST_NAME, ");
			sql.append("          EST_PRD AS TITLE, ");
			sql.append("          'M' AS STATUS, ");
			sql.append("          TRUNC(MEETING_DATE) AS DATETIME, ");
			sql.append("          '0800' AS STIME, ");
			sql.append("          '1800' AS ETIME ");
			sql.append("   FROM TBPMS_SALES_PLANS  ");
			sql.append("   WHERE EMP_ID = :EMP ");

			sql.append("   UNION ALL  ");

			// --C日期
			sql.append("   SELECT CUST_ID, ");
			sql.append("          CUST_NAME, ");
			sql.append("          EST_PRD AS TITLE, ");
			sql.append("          'C' AS STATUS, ");
			sql.append("          TRUNC(CLOSE_DATE) AS DATETIME, ");
			sql.append("          '0800' AS STIME, ");
			sql.append("          '1800' AS ETIME ");
			sql.append("   FROM TBPMS_SALES_PLANS ");
			sql.append("   WHERE  EMP_ID = :EMP ) ");
			sql.append(")");

			/** 待辦事項已處理狀態C與銷售計劃C衝突，行事曆日期上的點不需要特別區分待辦事項是否處理，因此全部以I作為待辦事項 **/
			sql.append("SELECT TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_TITLE AS TITLE, 'I' AS STATUS, TRUNC(TASK.TASK_DATE) AS DATETIME, TASK.TASK_STIME AS STIME, TASK.TASK_ETIME AS ETIME ");
			sql.append("FROM TBCAM_CAL_SALES_TASK TASK ");
			sql.append("LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID  ");
			sql.append("WHERE TASK.EMP_ID = :EMP ");
			/**
			 * ===========================2017/02/20===========================*
			 * * 需求 : 輔銷駐點不在待辦事項出現，但陪訪放行必須在待辦事項 問題 :
			 * 輔銷駐點跟陪訪放行都是自建4，無法用TASK_SOURCE做判斷 方法 :
			 * 輔銷駐點變更覆核的TITLE是由程式生成而非使用者自定義，因此直接判斷TITLE
			 * **/
			sql.append(" AND TASK.TASK_TITLE != '輔銷人員變更駐點行' ");
			/** ================================================================ **/
			//					
			sql.append(" UNION ALL ");
			sql.append(" SELECT  CUST_ID, CUST_NAME, TITLE, STATUS, DATETIME, STIME, ETIME");
			sql.append(" FROM BASE ");

			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("EMP", ws.getUser().getUserID());

		}
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return_vo.setResultList(list);
		
		this.sendRtnObject(return_vo);

	}

	// 點選日期帶出當日待辦事項&AMC
	public void getTodoDtl(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		
		CRM121InputVO inputVO = (CRM121InputVO) body;
		CRM121OutputVO return_vo = new CRM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		/**
		 * 業務排程 欄位TYPE: manager主管評估行事曆、null 檢視銷售計劃明細 STATUS :FIN_TYPE :1-完成、
		 * 0-未完成
		 * */
		if ("2".equals(inputVO.getPrivilege())) {
			sql.append(" WITH AO_List AS ( ");
			sql.append(" SELECT AO_CODE, EMP_ID, EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE BRANCH_NBR IN (:br_id )  ) ");

			// 主管評估行事曆

			sql.append(" SELECT NULL AS SEQ_NO, NULL AS CUST_ID, NULL AS CUST_NAME, '待主管評估'AS TITLE, FIN_TYPE AS STATUS, ");
			sql.append(" TRUNC(SCH.COACH_DATE) AS DATETIME,  '0800' AS STIME, '0900'AS ETIME, SCH.EMP_ID, INFO.EMP_NAME, INFO.AO_CODE, NULL AS TASK_MEMO, NULL AS TASK_SOURCE, 'manager' AS TYPE  ");
			sql.append(" FROM TBPMS_MNGR_EVAL_SCHEDULE SCH ");
			sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON SCH.EMP_ID = INFO.EMP_ID ");
			sql.append(" WHERE 1 = 1  ");
			sql.append(" AND SCH.REGION_CENTER_ID IN (:rcIdList ) ");
			sql.append(" AND SCH.BRANCH_AREA_ID IN (:op_id ) ");
			sql.append(" AND SCH.BRANCH_ID IN (:br_id ) ");
			sql.append(" AND SCH.EMP_ID IN (SELECT EMP_ID FROM AO_List) ");
			sql.append(" AND TO_CHAR(SCH.COACH_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD')");

			sql.append(" UNION ALL ");

			sql.append(" SELECT TASK.SEQ_NO, TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_TITLE AS TITLE, TASK_STATUS AS STATUS, ");
			sql.append(" TRUNC(TASK.TASK_DATE) AS DATETIME, TASK.TASK_STIME AS STIME, TASK.TASK_ETIME AS ETIME, TASK.EMP_ID, D.EMP_NAME, '' AS AO_CODE, TASK.TASK_MEMO, TASK.TASK_SOURCE, 'TASK' AS TYPE ");
			sql.append(" FROM TBCAM_CAL_SALES_TASK TASK ");
			sql.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID  ");
			sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D ON D.EMP_ID = TASK.EMP_ID  ");
			sql.append(" WHERE TASK.EMP_ID = :EMP ");
			sql.append(" AND TO_CHAR(TASK.TASK_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD')");

			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("EMP", ws.getUser().getUserID());
			queryCondition.setObject("op_id", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("br_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("date_o", new Timestamp(inputVO.getDate().getTime()));
			/** 待辦事項 */
		} else {
			sql.append(" SELECT TASK.SEQ_NO, TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_TITLE AS TITLE, TASK.TASK_STATUS AS STATUS, TRUNC(TASK.TASK_DATE) AS DATETIME, TASK.TASK_STIME AS STIME, TASK.TASK_ETIME AS ETIME, ");
			sql.append(" TASK.TASK_MEMO, TASK.TASK_SOURCE, TASK.TASK_TYPE ");
			sql.append(" FROM TBCAM_CAL_SALES_TASK TASK ");
			sql.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID ");
			sql.append(" WHERE TASK.EMP_ID = :EMP AND TO_CHAR(TASK.TASK_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD') ");

			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("EMP", ws.getUser().getUserID());
			queryCondition.setObject("date_o", new Timestamp(inputVO.getDate().getTime()));
		}
		

		return_vo.setMyTodoLst(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(return_vo);
	}

	// amc
	public void getAMC(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		
		CRM121InputVO inputVO = (CRM121InputVO) body;
		CRM121OutputVO return_vo = new CRM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		if ("2".equals(inputVO.getPrivilege())) {
			// 呼叫⟪WMS-TDS-PMS103_檢視銷售計劃明細.docx ⟫.query()
			sql.append("WITH AO_List AS ( ");
			sql.append("  SELECT AO_CODE, EMP_ID, EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE BRANCH_NBR IN (:br_id ) ");
			sql.append(") ");

			sql.append("SELECT SEQ, CUST_ID, CUST_NAME, TITLE, STATUS, DATETIME, STIME, ETIME, EMP_ID, EMP_NAME, AO_CODE, TYPE ");
			sql.append("FROM ( ");

			// --M日期
			sql.append("  SELECT TPS.SEQ, ");
			sql.append("         TPS.CUST_ID, ");
			sql.append("         TPS.CUST_NAME, ");
			sql.append("         TPS.EST_PRD AS TITLE, ");
			sql.append("         'M' AS STATUS, ");
			sql.append("         TRUNC(TPS.MEETING_DATE_S) AS DATETIME, ");
			sql.append("         '0800' AS STIME, ");
			sql.append("         '1800' AS ETIME, ");
			sql.append("         TPS.OS_FLAG, ");
			sql.append("         TPS.PFD_FLAG, ");
			sql.append("         TPS.BE_FLAG, ");
			sql.append("         TPS.EMP_ID, ");
			sql.append("         INFO.EMP_NAME, ");
			sql.append("         INFO.AO_CODE, ");
			sql.append("         null as TYPE ");
			sql.append("  FROM TBPMS_SALES_PLAN TPS ");
			sql.append("  LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON TPS.EMP_ID = INFO.EMP_ID ");
			sql.append("  WHERE TPS.EMP_ID IN ( SELECT EMP_ID FROM AO_List ) AND TRUNC(TPS.MEETING_DATE_S, 'DD') = TRUNC(:date_o, 'DD') ");

			//			if (ARMGR.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			//				sql.append("  AND TPS.PFD_FLAG = 'Y' ) ");
			//			}
			//			if (MBRMGR.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			//				sql.append("  AND TPS.OS_FLAG = 'Y'  ) ");
			//			}
			//			if (BMMGR.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			//				sql.append("  AND TPS.BE_FLAG = 'Y'  )  ");
			//			}
			
			sql.append(")");
			
			queryCondition.setObject("br_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else {
			sql.append("SELECT B.SEQ, B.CUST_ID, B.CUST_NAME, B.STATUS, B.EST_PRD, B.EST_AMT, B.EST_EARNINGS, B.MEETING_DATE_S, B.MEETING_DATE_E, B.ACTION_DATE, B.CLOSE_DATE, B.FAIA_STATUS ");
			sql.append("FROM ( ");

			//A
			sql.append("  SELECT SEQ, ");
			sql.append("         CUST_ID, ");
			sql.append("         CUST_NAME, ");
			sql.append("         '0800' AS MEETING_DATE_S, ");
			sql.append("         '1800' AS MEETING_DATE_E,");
			sql.append("         (CASE WHEN TRUNC(ACTION_DATE) = TRUNC(:date_o) THEN 'A' END) AS STATUS, ");
			sql.append("         EST_PRD, ");
			sql.append("         EST_AMT, ");
			sql.append("         EST_EARNINGS, ");
			sql.append("         ACTION_DATE, ");
			sql.append("         CLOSE_DATE, ");
			sql.append("         NULL AS FAIA_STATUS ");
			sql.append("  FROM TBPMS_SALES_PLANS ");
			sql.append("  WHERE EMP_ID = :EMP ");
			sql.append("  AND TO_CHAR(ACTION_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD') ");

			sql.append("  UNION ALL  ");

			//M
			sql.append("  SELECT P.SEQ, ");
			sql.append("         P.CUST_ID, ");
			sql.append("         P.CUST_NAME, ");
			sql.append("         '0800' AS MEETING_DATE_S, ");
			sql.append("         '1800' AS MEETING_DATE_E, ");
			sql.append("         'M' AS STATUS, ");
			sql.append("         P.EST_PRD, ");
			sql.append("         EST_AMT, ");
			sql.append("         P.EST_EARNINGS, ");
			sql.append("         P.ACTION_DATE, ");
			sql.append("         P.CLOSE_DATE, ");
			sql.append("         M.STATUS AS FAIA_STATUS ");
			sql.append("  FROM TBPMS_SALES_PLANS P ");
			sql.append("  LEFT JOIN TBCRM_WKPG_AS_MAST M ON  P.SEQ = M.SALES_PLAN_SEQ AND P.CUST_ID = M.CUST_ID ");
			sql.append("  WHERE P.EMP_ID = :EMP ");
			sql.append("  AND TO_CHAR(:date_o ,'YYYY/MM/DD') = TO_CHAR(P.MEETING_DATE, 'YYYY/MM/DD') ");

			sql.append("  UNION ALL  ");

			//C
			sql.append("  SELECT SEQ, ");
			sql.append("         CUST_ID, ");
			sql.append("         CUST_NAME, ");
			sql.append("         '0800' AS MEETING_DATE_S, ");
			sql.append("         '1800' AS MEETING_DATE_E, ");
			sql.append("         'C' AS STATUS, ");
			sql.append("         EST_PRD, ");
			sql.append("         EST_AMT, ");
			sql.append("         EST_EARNINGS, ");
			sql.append("         ACTION_DATE, ");
			sql.append("         CLOSE_DATE, ");
			sql.append("         null AS FAIA_STATUS ");
			sql.append("  FROM TBPMS_SALES_PLANS ");
			sql.append("  WHERE EMP_ID = :EMP ");
			sql.append("  AND TO_CHAR(CLOSE_DATE, 'YYYY/MM/DD') = TO_CHAR(:date_o, 'YYYY/MM/DD') ");
			sql.append(") B ");
			sql.append("ORDER BY CASE WHEN B.STATUS = 'A' THEN 0 WHEN B.STATUS = 'M' THEN 1 WHEN B.STATUS = 'C' THEN 2 END ASC  ");

			queryCondition.setObject("EMP", ws.getUser().getUserID());
		}

		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("date_o", new Timestamp(inputVO.getDate().getTime()));

		return_vo.setMyAUMLst(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(return_vo);
	}

	// 刪除

	public void delTodo(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM121InputVO inputVO = (CRM121InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		if (!StringUtils.isEmpty(inputVO.getSeq())) {
			BigDecimal seqNo = new BigDecimal(inputVO.getSeq().toString());
			
			TBCAM_CAL_SALES_TASKVO vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, seqNo);
			
			if (vo != null) {
				dam.delete(vo);
			}
		} else {
			/**
			 * chkCodedata : 待辦事項 chkCode_1data: AMC chkCode_2data: 提醒
			 * **/
			int C1 = 0, C2 = 0, C3 = 0;

			if (inputVO.getChkCodedata() != null)
				C1 = inputVO.getChkCodedata().size();

			if (inputVO.getChkCode_1data() != null)
				C2 = inputVO.getChkCode_1data().size();

			if (inputVO.getChkCode_2data() != null)
				C3 = inputVO.getChkCode_2data().size();

			if (C1 > 0) {
				for (int i = 0; i < inputVO.getChkCodedata().size(); i++) {
					BigDecimal seqNo = new BigDecimal(inputVO.getChkCodedata().get(i).get("SEQ_NO").toString());

					TBCAM_CAL_SALES_TASKVO vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, seqNo);
					
					if (vo != null) {
						dam.delete(vo);
					}
				}
			}
			
			if (C2 > 0) {
				for (int j = 0; j < inputVO.getChkCode_1data().size(); j++) {
					for (int k = 0; k < inputVO.getChkCode_1data().size(); k++) {
						BigDecimal seqNo = new BigDecimal(inputVO.getChkCode_1data().get(j).get("SEQ").toString());
						
						StringBuffer sb = new StringBuffer();
						sb.append("DELETE FROM TBPMS_SALES_PLANS WHERE SEQ = :SEQ ");
						
						queryCondition.setObject("SEQ", seqNo);
						
						queryCondition.setQueryString(sb.toString());
						
						dam.exeUpdate(queryCondition);
					}
				}
			}
			
			if (C3 > 0) {
				for (int i = 0; i < inputVO.getChkCode_2data().size(); i++) {
					BigDecimal seqNo = new BigDecimal(inputVO.getChkCode_2data().get(i).get("SEQ_NO").toString());

					TBCAM_CAL_SALES_TASKVO vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, seqNo);
					
					if (vo != null) {
						dam.delete(vo);
					}
				}
			}
		}
		
		sendRtnObject(null);
	}

	// 編輯 (待辦事項)
	public void update(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		TBCAM_CAL_SALES_TASKVO vo1 = new TBCAM_CAL_SALES_TASKVO();

		CRM121InputVO inputVO = (CRM121InputVO) body;
		dam = this.getDataAccessManager();
		
		if ("B".equals(inputVO.getUpdateType()) || "R2".equals(inputVO.getUpdateType())) {
			BigDecimal seqNo = new BigDecimal(inputVO.getSeq());
			vo1 = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, seqNo);
		} else {
			BigDecimal seqNo = new BigDecimal(inputVO.getChkCodedata().get(0).get("SEQ_NO").toString());
			vo1 = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, seqNo);
		}

		if (vo1 != null) {
			if ("B".equals(inputVO.getUpdateType()) || "R2".equals(inputVO.getUpdateType())) {
				if (inputVO.getTASK_DATE() != null) {
					vo1.setTASK_DATE(new Timestamp(inputVO.getTASK_DATE().getTime()));
				}

				if (inputVO.getTASK_SOURCE() != null) {
					vo1.setTASK_SOURCE(inputVO.getTASK_SOURCE());
				}

				if (StringUtils.isNotBlank(inputVO.getsHour()) && StringUtils.isNotBlank(inputVO.getsMinute())) {
					vo1.setTASK_STIME(inputVO.getsHour() + inputVO.getsMinute());
				}

				if (StringUtils.isNotBlank(inputVO.geteHour()) && StringUtils.isNotBlank(inputVO.geteMinute())) {
					vo1.setTASK_ETIME(inputVO.geteHour() + inputVO.geteMinute());
				}

				if (inputVO.getTASK_STATUS() != null) {
					vo1.setTASK_STATUS(inputVO.getTASK_STATUS());
				}

				if (StringUtils.isNotBlank(inputVO.getTASK_TITLE())) {
					vo1.setTASK_TITLE(inputVO.getTASK_TITLE().trim());
				} else {
					vo1.setTASK_TITLE(null);
				}

				if (StringUtils.isNotBlank(inputVO.getTASK_MEMO())) {
					vo1.setTASK_MEMO(inputVO.getTASK_MEMO());
				} else {
					vo1.setTASK_MEMO(null);
				}
			} else {
				if (inputVO.getTASK_DATE() != null) {
					vo1.setTASK_DATE(new Timestamp(inputVO.getTASK_DATE().getTime()));
				}
			}
			
			vo1.setModifier(ws.getUser().getUserID());
			vo1.setLastupdate(currentTM);

			dam.update(vo1);
		}

		sendRtnObject(null);
	}

	// 輔銷新增待辦事項
	public void getAddTodo(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");

		CRM121InputVO inputVO = (CRM121InputVO) body;
		dam = this.getDataAccessManager();

		// seq
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCAM_CAL_SALES_TASK.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");

		TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
		Timestamp TASK_DATE = new Timestamp(inputVO.getTASK_DATE().getTime());
		vo.setSEQ_NO(seqNo);
		vo.setEMP_ID(ws.getUser().getUserID());
		vo.setCUST_ID(inputVO.getCUST_ID());
		vo.setTASK_DATE(TASK_DATE);
		vo.setTASK_STIME(sdf.format(inputVO.getTASK_STIME()));
		vo.setTASK_ETIME(sdf.format(inputVO.getTASK_ETIME()));
		vo.setTASK_TITLE(inputVO.getTASK_TITLE());
		vo.setTASK_MEMO(inputVO.getTASK_MEMO());
		vo.setTASK_SOURCE("2");
		vo.setTASK_STATUS("I");
		dam.create(vo);

		this.sendRtnObject(null);
	}

}