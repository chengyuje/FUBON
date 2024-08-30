package com.systex.jbranch.app.server.fps.crm3111;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.org130.ORG130InputVO;
import com.systex.jbranch.app.server.fps.org130.ORG130OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Stella
 * @date 2016/08/29
 * @spec null
 */
@Component("crm3111")
@Scope("request")
public class CRM3111 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void login(Object body, IPrimitiveMap header) throws JBranchException {

		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//		sql.append("SELECT COUNT(ROLEID) AS COUNTS 	FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID ='013' AND ROLEID = :roleID ");
		sql.append("SELECT CASE WHEN PRIVILEGEID IN ( '002','003,'004') THEN 1 ");
		sql.append("ELSE 2 END AS COUNTS ");
		sql.append("FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE ROLEID = :roleID ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		return_VO.setRoleID(dam.exeQuery(queryCondition));
		sendRtnObject(return_VO);
	}

	public void getNewAocode(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM3111InputVO inputVO = (CRM3111InputVO) body;
		CRM3111OutputVO return_VO = new CRM3111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT AO_CODE, (AO_CODE||'-'||EMP_NAME)AS AO_NAME  FROM VWORG_BRANCH_EMP_DETAIL_INFO " + "  WHERE 1=1 AND AO_CODE IS NOT NULL");

		if (inputVO.getNew_ao_brh() != null && StringUtils.isNotBlank(inputVO.getNew_ao_brh())) {
			sql.append(" AND BRANCH_NBR = :NEW_AO_BRH ");
			queryCondition.setObject("NEW_AO_BRH", inputVO.getNew_ao_brh());
		} else {
			sql.append(" AND BRANCH_NBR IN (:NEW_AO_BRH) ");
			queryCondition.setObject("NEW_AO_BRH", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		queryCondition.setQueryString(sql.toString());

		return_VO.setNewaoCode(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM3111InputVO inputVO = (CRM3111InputVO) body;
		CRM3111OutputVO return_VO = new CRM3111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		// 查詢類型：1-訪談內容、2-AUM
		if ("1".equals(inputVO.getAct_type())) {
			sql.append("SELECT TCAC.CUST_ID, ");
			sql.append("       TCM.CUST_NAME, ");
			sql.append("       NVL(TCAC.CHG_DATE,TCAC.LETGO_DATETIME) LETGO_DATETIME, ");
			sql.append("       TCM.CON_DEGREE, ");
			sql.append("       TCVRD.M_LASTUPDATE, ");
			sql.append("       TCVRD.AO_LASTUPDATE, ");
			sql.append("       TCVRD.VISIT_MEMO, ");
			sql.append("       TCVRD.VISIT_CREPLY, ");
			sql.append("       NVL(TCAC.CHG_DATE, ");
			sql.append("       TCAC.LETGO_DATETIME) + NVL(WCRMM.FRQ_DAY, 0) LIMIT_DATETIME, ");
			sql.append("       TCAC.NEW_AO_CODE, ");
			sql.append("       TCAC.NEW_AO_NAME ");
			sql.append("FROM TBCRM_CUST_AOCODE_CHGLOG TCAC ");
			sql.append("JOIN TBCRM_CUST_MAST TCM ON TCAC.CUST_ID = TCM.CUST_ID ");
			sql.append("LEFT JOIN VWCRM_CUST_REVIEWDATE_MAP WCRMM ON NVL(TCM.CON_DEGREE,'OTH') = WCRMM.CON_DEGREE AND NVL(TCM.VIP_DEGREE,'M') = WCRMM.VIP_DEGREE ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT CUST_ID, ");
			sql.append("         MAX(BTIME) M_LASTUPDATE, ");
			sql.append("         MAX(PTIME) AO_LASTUPDATE, ");
			sql.append("         MAX(VMEMO) VISIT_MEMO, ");
			sql.append("         MAX(VREPLY) VISIT_CREPLY ");
			sql.append("  FROM (");
			sql.append("    SELECT CUST_ID, ");
			sql.append("           CASE WHEN CMU_TYPE = 'B' THEN TO_DATE(TO_CHAR(LASTUPDATE, 'YYYY/MM/DD HH24:MI:SS'),'YYYY/MM/DD HH24:MI:SS') ELSE NULL END BTIME, ");
			sql.append("           CASE WHEN CMU_TYPE = 'P' THEN TO_DATE(TO_CHAR(LASTUPDATE, 'YYYY/MM/DD HH24:MI:SS'),'YYYY/MM/DD HH24:MI:SS') ELSE NULL END PTIME, ");
			sql.append("           CASE WHEN VRANK = 1 THEN VISIT_MEMO ELSE NULL END VMEMO, ");
			sql.append("           CASE WHEN VRANK = 1 THEN VISIT_CREPLY ELSE NULL END VREPLY ");
			sql.append("    FROM ( ");
			sql.append("      SELECT TCVR.*, ");
			sql.append("             RANK() OVER( PARTITION BY TCVR.CUST_ID, TCVR.CMU_TYPE ORDER BY TCVR.LASTUPDATE DESC ) AS RANK, ");
			sql.append("             RANK() OVER( PARTITION BY TCVR.CUST_ID ORDER BY TCVR.LASTUPDATE DESC ) AS VRANK ");
			sql.append("      FROM TBCRM_CUST_VISIT_RECORD TCVR");
			sql.append("    )");
			sql.append("  ) ");
			sql.append("  GROUP BY CUST_ID ");
			sql.append(") TCVRD ON TCAC.CUST_ID = TCVRD.CUST_ID ");
			sql.append("WHERE 1 = 1 ");

			// #0001852_WMS-CR-20231222-01_新增保管箱資訊及分行追蹤管理報表調整
			if (StringUtils.isNotBlank(inputVO.getVisitRecordType())) {
				switch (inputVO.getVisitRecordType()) {
					case "Y":
						sql.append("AND TCVRD.VISIT_MEMO IS NULL ");
						break;
					case "N":
						sql.append("AND TCVRD.VISIT_MEMO IS NOT NULL ");
						break;
				}
			}
			
			// 分行
			if (StringUtils.isNotBlank(inputVO.getNew_ao_brh())) {
				sql.append("AND TCAC.NEW_AO_BRH = :new_ao_brh ");
				queryCondition.setObject("new_ao_brh", inputVO.getNew_ao_brh());
			} else {
				sql.append("AND TCAC.NEW_AO_BRH IN (:new_ao_brh) ");
				queryCondition.setObject("new_ao_brh", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}

			//新理專
			if (StringUtils.isNotBlank(inputVO.getNew_ao_code())) {
				sql.append("AND TCAC.NEW_AO_CODE = :new_ao_code ");
				queryCondition.setObject("new_ao_code", inputVO.getNew_ao_code());
			}

			//客戶ID
			if (StringUtils.isNotBlank(inputVO.getCust_id())) {
				sql.append("AND TCAC.CUST_ID = :cust_id ");
				queryCondition.setObject("cust_id", inputVO.getCust_id().trim());
			}

			//客戶姓名
			if (StringUtils.isNotBlank(inputVO.getCust_name())) {
				sql.append("AND TCM.CUST_NAME like :cust_name ");
				queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
			}

			//貢獻度等級
			if (StringUtils.isNotBlank(inputVO.getCon_degree())) {
				sql.append("AND TCM.CON_DEGREE = :con_degree ");
				queryCondition.setObject("con_degree", inputVO.getCon_degree());
			}

			//理財會員等級
			if (StringUtils.isNotBlank(inputVO.getVip_degree())) {
				sql.append("AND TCM.VIP_DEGREE = :vip_degree ");
				queryCondition.setObject("vip_degree", inputVO.getVip_degree());
			}

			//客戶異動日期
			if (inputVO.getsCreDate() != null) {
				sql.append("AND TRUNC(LETGO_DATETIME) >= :start ");
				queryCondition.setObject("start", inputVO.getsCreDate());
			}
			if (inputVO.geteCreDate() != null) {
				sql.append("AND TRUNC(LETGO_DATETIME) <= :end ");
				queryCondition.setObject("end", inputVO.geteCreDate());
			}

			// 2017/10/31 esorter會錯 加這個
			sql.append("ORDER BY NULL");
		} else if ("2".equals(inputVO.getAct_type())) {
			sql.append("SELECT ROWNUM, ");
			sql.append("       ANDC.CUST_ID, ");
			sql.append("       C.CUST_NAME, ");
			sql.append("       C.MAST_AO_CODE, ");
			sql.append("       AO.EMP_NAME, ");
			sql.append("       C.LETGO_DATETIME, ");
			sql.append("       C.CON_DEGREE, ");
			sql.append("       C.VIP_DEGREE, ");
			sql.append("       (D.AUM)AS BF_AUM, ");
			sql.append("       (ROUND((D.INV_AUM + D.INS_AUM) / (D.DEP_AUM + D.INV_AUM + D.INS_AUM), 2) * 100 || '%') AS BF_AUM_RATE, ");
			sql.append("       M.PRFT_NEWEST_YEAR, ");
			sql.append("       (D.AUM - E.BF_ONE_AUM) AS ONE_AUM,  ");
			sql.append("       (ROUND((E.INV_AUM + E.INS_AUM) / (E.DEP_AUM + E.INV_AUM + E.INS_AUM), 2) * 100 || '%') AS ONE_AUM_RATE, ");
			sql.append("       (D.AUM - F.BF_TWO_AUM) AS TWO_AUM, ");
			sql.append("       (ROUND((F.INV_AUM + F.INS_AUM) / (F.DEP_AUM + F.INV_AUM + F.INS_AUM), 2) * 100 || '%') AS TWO_AUM_RATE, ");
			sql.append("       (D.AUM - G.BF_THREE_AUM) AS THREE_AUM, ");
			sql.append("       (ROUND((G.INV_AUM + G.INS_AUM) / (G.DEP_AUM + G.INV_AUM+G.INS_AUM), 2) * 100 || '%') AS THREE_AUM_RATE, ");
			sql.append("       (D.AUM - H.BF_FOUR_AUM)AS FOUR_AUM, ");
			sql.append("       (ROUND((H.INV_AUM + H.INS_AUM) / (H.DEP_AUM + H.INV_AUM + H.INS_AUM), 2) * 100 || '%') AS FOUR_AUM_RATE, ");
			sql.append("       (D.AUM - I.BF_FIVE_AUM)AS FIVE_AUM, ");
			sql.append("       (ROUND((I.INV_AUM + I.INS_AUM) / (I.DEP_AUM + I.INV_AUM + I.INS_AUM), 2) * 100 || '%') AS FIVE_AUM_RATE, ");
			sql.append("       (D.AUM - J.BF_SIX_AUM)AS SIX_AUM, ");
			sql.append("       (ROUND((J.INV_AUM + J.INS_AUM) / (J.DEP_AUM + J.INV_AUM + J.INS_AUM), 2) * 100 || '%') AS SIX_AUM_RATE, ");
			sql.append("       2 AS ACT_TYPE ");
			sql.append("FROM ( ");
			sql.append("  SELECT A.CUST_ID, ");
			sql.append("         B.CUST_NAME, ");
			sql.append("         B.AO_CODE AS MAST_AO_CODE, ");
			sql.append("         A.LETGO_DATETIME, ");
			sql.append("         TRUNC(A.LETGO_DATETIME) AS LETGO_DATE, ");
			sql.append("         B.CON_DEGREE, ");
			sql.append("         B.VIP_DEGREE, ");
			sql.append("         A. NEW_AO_BRH, ");
			sql.append("         A.NEW_AO_CODE ");
			sql.append("  FROM (SELECT ROW_NUMBER() OVER(PARTITION BY TCAC.CUST_ID ORDER BY TCAC.LETGO_DATETIME DESC) AS RANK, TCAC.* FROM TBCRM_CUST_AOCODE_CHGLOG TCAC) A ");
			sql.append("  LEFT JOIN TBCRM_CUST_MAST B ON A.CUST_ID = B.CUST_ID AND A.RANK = 1 ");
			sql.append("  WHERE A.CUST_ID = B.CUST_ID ");
			sql.append(") C ");
			sql.append("LEFT JOIN (");
			sql.append("  SELECT CUST_ID,PRFT_NEWEST_YEAR, ROW_NUMBER() OVER(PARTITION BY CUST_ID ORDER BY DATA_YEAR|| DATA_MONTH DESC) AS RANK ");
			sql.append("  FROM TBCRM_CUST_CON_NOTE ");
			sql.append("  ORDER BY (DATA_YEAR|| DATA_MONTH) DESC");
			sql.append(") M ON C.CUST_ID = M.CUST_ID AND M.RANK = 1 ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT CUST_ID, ");
			sql.append("         AVG_AUM_AMT AS AUM, ");
			sql.append("         (SAV_AUM + CD_AUM + CHK_AUM + FSAV_AUM) AS DEP_AUM , ");
			sql.append("         (RP_AUM + BFOND_AUM + DOM_FUND_AUM + OVS_COMM_AUM + OVS_STK_AUM + OVS_FUND_AUM) AS INV_AUM,");
			sql.append("         INS_AUM  ");
			sql.append("  FROM TBCRM_CUST_AUM_MONTHLY_HIST  ");
			sql.append("  WHERE DATA_YEAR || DATA_MONTH = TO_CHAR (SYSDATE, 'YYYYMM') ");
			sql.append(") D ON D.CUST_ID = C.CUST_ID ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT CUST_ID, ");
			sql.append("         DATA_YEAR, ");
			sql.append("         DATA_MONTH,  ");
			sql.append("         AVG_AUM_AMT AS BF_ONE_AUM, ");
			sql.append("         (SAV_AUM + CD_AUM + CHK_AUM + FSAV_AUM) AS DEP_AUM, ");
			sql.append("         (RP_AUM + BFOND_AUM + DOM_FUND_AUM + OVS_COMM_AUM + OVS_STK_AUM + OVS_FUND_AUM) AS INV_AUM, ");
			sql.append("         INS_AUM  ");
			sql.append("  FROM TBCRM_CUST_AUM_MONTHLY_HIST  ");
			sql.append(") E ON E.CUST_ID = C.CUST_ID AND E.DATA_YEAR || E.DATA_MONTH = TO_CHAR (ADD_MONTHS(C.LETGO_DATE, +1), 'YYYYMM') ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT CUST_ID,");
			sql.append("         DATA_YEAR, ");
			sql.append("         DATA_MONTH, ");
			sql.append("         AVG_AUM_AMT AS BF_TWO_AUM, ");
			sql.append("         (SAV_AUM + CD_AUM + CHK_AUM + FSAV_AUM) AS DEP_AUM, ");
			sql.append("         (RP_AUM + BFOND_AUM + DOM_FUND_AUM + OVS_COMM_AUM + OVS_STK_AUM + OVS_FUND_AUM) AS INV_AUM, ");
			sql.append("         INS_AUM ");
			sql.append("  FROM TBCRM_CUST_AUM_MONTHLY_HIST ");
			sql.append(") F ON F.CUST_ID = C.CUST_ID and F.DATA_YEAR || F.DATA_MONTH = TO_CHAR (ADD_MONTHS(C.LETGO_DATE, +2), 'YYYYMM') ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT CUST_ID, ");
			sql.append("         DATA_YEAR, ");
			sql.append("         DATA_MONTH, ");
			sql.append("         AVG_AUM_AMT AS BF_THREE_AUM, ");
			sql.append("         (SAV_AUM + CD_AUM + CHK_AUM + FSAV_AUM) AS DEP_AUM, ");
			sql.append("         (RP_AUM + BFOND_AUM + DOM_FUND_AUM + OVS_COMM_AUM + OVS_STK_AUM + OVS_FUND_AUM) AS INV_AUM, ");
			sql.append("         INS_AUM  ");
			sql.append("  FROM TBCRM_CUST_AUM_MONTHLY_HIST  ");
			sql.append(") G ON G.CUST_ID = C.CUST_ID and G.DATA_YEAR || G.DATA_MONTH = TO_CHAR (ADD_MONTHS(C.LETGO_DATE, +3), 'YYYYMM') ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT CUST_ID, ");
			sql.append("         DATA_YEAR, ");
			sql.append("         DATA_MONTH, ");
			sql.append("         AVG_AUM_AMT AS BF_FOUR_AUM, ");
			sql.append("         (SAV_AUM + CD_AUM + CHK_AUM + FSAV_AUM) AS DEP_AUM, ");
			sql.append("         (RP_AUM + BFOND_AUM + DOM_FUND_AUM + OVS_COMM_AUM + OVS_STK_AUM + OVS_FUND_AUM) AS INV_AUM, ");
			sql.append("         INS_AUM  ");
			sql.append("  FROM TBCRM_CUST_AUM_MONTHLY_HIST ");
			sql.append(") H ON H.CUST_ID = C.CUST_ID AND  H.DATA_YEAR || H.DATA_MONTH = TO_CHAR (ADD_MONTHS(C.LETGO_DATE, +4), 'YYYYMM') ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT CUST_ID, ");
			sql.append("         DATA_YEAR, ");
			sql.append("         DATA_MONTH, ");
			sql.append("         AVG_AUM_AMT AS BF_FIVE_AUM, ");
			sql.append("         (SAV_AUM + CD_AUM + CHK_AUM + FSAV_AUM) AS DEP_AUM, ");
			sql.append("         (RP_AUM + BFOND_AUM + DOM_FUND_AUM + OVS_COMM_AUM + OVS_STK_AUM + OVS_FUND_AUM) AS INV_AUM, ");
			sql.append("         INS_AUM  ");
			sql.append("  FROM TBCRM_CUST_AUM_MONTHLY_HIST ");
			sql.append(") I ON I.CUST_ID = C.CUST_ID AND  I.DATA_YEAR || I.DATA_MONTH = TO_CHAR (ADD_MONTHS(C.LETGO_DATE, +5), 'YYYYMM')");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT CUST_ID, ");
			sql.append("         DATA_YEAR, ");
			sql.append("         DATA_MONTH, ");
			sql.append("         AVG_AUM_AMT AS BF_SIX_AUM, ");
			sql.append("         (SAV_AUM + CD_AUM + CHK_AUM + FSAV_AUM) AS DEP_AUM, ");
			sql.append("         (RP_AUM + BFOND_AUM + DOM_FUND_AUM + OVS_COMM_AUM + OVS_STK_AUM + OVS_FUND_AUM) AS INV_AUM, ");
			sql.append("         INS_AUM ");
			sql.append("  FROM TBCRM_CUST_AUM_MONTHLY_HIST ");
			sql.append(") J ON J.CUST_ID = C.CUST_ID AND J.DATA_YEAR || J.DATA_MONTH = TO_CHAR(ADD_MONTHS(C.LETGO_DATE, +6), 'YYYYMM') ");
			
			// 2017/6/8
			sql.append("LEFT JOIN VWORG_AO_INFO AO ON C.MAST_AO_CODE = AO.AO_CODE ");
			
			sql.append("WHERE 1=1 ");
			if (StringUtils.isNotBlank(inputVO.getNew_ao_brh())) {
				sql.append("AND C.NEW_AO_BRH = :NEW_AO_BRH ");
				queryCondition.setObject("NEW_AO_BRH", inputVO.getNew_ao_brh());
			} else {
				sql.append("AND C.NEW_AO_BRH IN (:NEW_AO_BRH) ");
				queryCondition.setObject("NEW_AO_BRH", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			
			if (StringUtils.isNotBlank(inputVO.getNew_ao_code())) {
				sql.append("AND C.NEW_AO_CODE = :NEW_AO_CODE ");
				queryCondition.setObject("NEW_AO_CODE", inputVO.getNew_ao_code());
			}
			
			if (inputVO.getsCreDate() != null) {
				sql.append("and TRUNC(C.LETGO_DATETIME) >= :start ");
				queryCondition.setObject("start", inputVO.getsCreDate());
			}
			
			if (inputVO.geteCreDate() != null) {
				sql.append("and TRUNC(C.LETGO_DATETIME) <= :end ");
				queryCondition.setObject("end", inputVO.geteCreDate());
			}
			
			if (StringUtils.isNotBlank(inputVO.getCust_id())) {
				sql.append("AND C.CUST_ID = :CUST_ID ");
				queryCondition.setObject("CUST_ID", inputVO.getCust_id().trim());
			}
			
			if (StringUtils.isNotBlank(inputVO.getCust_name())) {
				sql.append("AND C.CUST_NAME like :CUST_NAME ");
				queryCondition.setObject("CUST_NAME", "%" + inputVO.getCust_name() + "%");
			}
			
			// 2017/6/8
			if (StringUtils.isNotBlank(inputVO.getCon_degree())) {
				sql.append("AND C.CON_DEGREE = :con_degree ");
				queryCondition.setObject("con_degree", inputVO.getCon_degree());
			}
			
			if (StringUtils.isNotBlank(inputVO.getVip_degree())) {
				sql.append("AND C.VIP_DEGREE = :vip_degree ");
				queryCondition.setObject("vip_degree", inputVO.getVip_degree());
			}
			//
			sql.append("ORDER BY ROWNUM ASC, LETGO_DATETIME ASC ");
		}

		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}

}
