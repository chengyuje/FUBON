package com.systex.jbranch.app.server.fps.pqc101;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pqc101")
@Scope("request")
public class PQC101 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	
	public void queryByType(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC101InputVO inputVO = (PQC101InputVO) body;
		PQC101OutputVO outputVO = new PQC101OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		switch (inputVO.getQueryType()) {
			case "Basic":
				sb.append("SELECT BASE.PRIVILEGEID AS PRIVILEGE_ID, PRI.NAME AS PRIVILEGE_NAME, BASE.MIN_QUOTA, BASE.MAX_QUOTA ");
				sb.append("FROM TBPQC_QUOTA_CONTROL_BASIC BASE ");
				sb.append("LEFT JOIN TBSYSSECUPRI PRI ON BASE.PRIVILEGEID = PRI.PRIVILEGEID ");
				
				queryCondition.setQueryString(sb.toString());

				outputVO.setBasicList(dam.exeQuery(queryCondition));

				break;
			case "Org":
				sb.append("SELECT ROWNUM, BASE.* ");
				sb.append("FROM ( ");
				sb.append("  SELECT PRD.START_DATE, PRD.END_DATE, PRD.PRD_TYPE, PRD.PRD_ID, PRD.PRD_NAME, PRD.CURRENCY, ");
				sb.append("         TO_DATE(PRD.START_DATE, 'yyyyMMdd') AS SHOW_SDT, ");
				sb.append("         TO_DATE(PRD.END_DATE, 'yyyyMMdd') AS SHOW_EDT, ");
				
				sb.append("         CASE WHEN TO_DATE(PRD.END_DATE, 'yyyyMMdd') < TRUNC(SYSDATE) THEN 'Y' ELSE 'N' END AS DISABLED_BASE, ");
				
				sb.append("         NVL(ORG.PRD_QUOTA, 0) AS PRD_QUOTA, ");
				sb.append("         APP_M_PRD.APPLY_QUOTA AS PRD_APPLY_TEMP, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 THEN 0 ELSE APP_M_PRD.APPLY_QUOTA END AS PRD_APPLY, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 THEN 0 ELSE NVL(ORG.PRD_QUOTA, 0) - APP_M_PRD.APPLY_QUOTA END AS PRD_LAVE, ");
				sb.append("         CASE WHEN TO_DATE(PRD.END_DATE, 'yyyyMMdd') < TRUNC(SYSDATE) THEN 'Y' ELSE 'N' END AS DISABLED_PQ, ");
				 
				sb.append("         NVL(ORG.ALL_QUOTA, 0) AS ALL_QUOTA, ");
				sb.append("         APP_M_PRD.APPLY_QUOTA AS ALL_APPLY_TEMP, ");
				sb.append("         CASE WHEN NVL(ORG.ALL_QUOTA, 0) = 0 THEN 0 ELSE APP_M_PRD.APPLY_QUOTA END AS ALL_APPLY, ");
				sb.append("         CASE WHEN NVL(ORG.ALL_QUOTA, 0) = 0 THEN 0 ELSE NVL(ORG.ALL_QUOTA, 0) - APP_M_PRD.APPLY_QUOTA END AS ALL_LAVE, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 OR (NVL(ORG.RC_QUOTA_1, 0) + NVL(ORG.RC_QUOTA_2, 0) + NVL(ORG.RC_QUOTA_3, 0) + NVL(ORG.RC_QUOTA_4, 0) + NVL(ORG.RC_QUOTA_5, 0) + NVL(ORG.RC_QUOTA_6, 0) + NVL(ORG.RC_QUOTA_UHRM, 0)) > 0 THEN 'Y' ELSE 'N' END AS DISABLED_AQ, ");
				
				sb.append("         NVL(ORG.RC_QUOTA_1, 0) AS RC_QUOTA_1, ");
				sb.append("         APP_M_RC.RC_APPLY_1 AS RC_APPLY_TEMP_1, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_1, 0) = 0 THEN 0 ELSE APP_M_RC.RC_APPLY_1 END AS RC_APPLY_1, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_1, 0) = 0 THEN 0 ELSE NVL(ORG.RC_QUOTA_1, 0) - APP_M_RC.RC_APPLY_1 END AS RC_LAVE_1, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 OR NVL(ORG.ALL_QUOTA, 0) > 0 THEN 'Y' ELSE 'N' END AS DISABLED_RCQ1, ");
				
				sb.append("         NVL(ORG.RC_QUOTA_2, 0) AS RC_QUOTA_2, ");
				sb.append("         APP_M_RC.RC_APPLY_2 AS RC_APPLY_TEMP_2, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_2, 0) = 0 THEN 0 ELSE APP_M_RC.RC_APPLY_2 END AS RC_APPLY_2, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_2, 0) = 0 THEN 0 ELSE NVL(ORG.RC_QUOTA_2, 0) - APP_M_RC.RC_APPLY_2 END AS RC_LAVE_2, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 OR NVL(ORG.ALL_QUOTA, 0) > 0 THEN 'Y' ELSE 'N' END AS DISABLED_RCQ2, ");
				
				sb.append("         NVL(ORG.RC_QUOTA_3, 0) AS RC_QUOTA_3, ");
				sb.append("         APP_M_RC.RC_APPLY_3 AS RC_APPLY_TEMP_3, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_3, 0) = 0 THEN 0 ELSE APP_M_RC.RC_APPLY_3 END AS RC_APPLY_3, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_3, 0) = 0 THEN 0 ELSE NVL(ORG.RC_QUOTA_3, 0) - APP_M_RC.RC_APPLY_3 END AS RC_LAVE_3, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 OR NVL(ORG.ALL_QUOTA, 0) > 0 THEN 'Y' ELSE 'N' END AS DISABLED_RCQ3, ");
				
				sb.append("         NVL(ORG.RC_QUOTA_4, 0) AS RC_QUOTA_4, ");
				sb.append("         APP_M_RC.RC_APPLY_4 AS RC_APPLY_TEMP_4, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_4, 0) = 0 THEN 0 ELSE APP_M_RC.RC_APPLY_4 END AS RC_APPLY_4, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_4, 0) = 0 THEN 0 ELSE NVL(ORG.RC_QUOTA_4, 0) - APP_M_RC.RC_APPLY_4 END AS RC_LAVE_4, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 OR NVL(ORG.ALL_QUOTA, 0) > 0 THEN 'Y' ELSE 'N' END AS DISABLED_RCQ4, ");
				
				sb.append("         NVL(ORG.RC_QUOTA_5, 0) AS RC_QUOTA_5, ");
				sb.append("         APP_M_RC.RC_APPLY_5 AS RC_APPLY_TEMP_5, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_5, 0) = 0 THEN 0 ELSE APP_M_RC.RC_APPLY_5 END AS RC_APPLY_5, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_5, 0) = 0 THEN 0 ELSE NVL(ORG.RC_QUOTA_5, 0) - APP_M_RC.RC_APPLY_5 END AS RC_LAVE_5, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 OR NVL(ORG.ALL_QUOTA, 0) > 0 THEN 'Y' ELSE 'N' END AS DISABLED_RCQ5, ");
				
				sb.append("         NVL(ORG.RC_QUOTA_6, 0) AS RC_QUOTA_6, ");
				sb.append("         APP_M_RC.RC_APPLY_6 AS RC_APPLY_TEMP_6, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_6, 0) = 0 THEN 0 ELSE APP_M_RC.RC_APPLY_6 END AS RC_APPLY_6, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_6, 0) = 0 THEN 0 ELSE NVL(ORG.RC_QUOTA_6, 0) - APP_M_RC.RC_APPLY_6 END AS RC_LAVE_6, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 OR NVL(ORG.ALL_QUOTA, 0) > 0 THEN 'Y' ELSE 'N' END AS DISABLED_RCQ6, ");
				
				sb.append("         NVL(ORG.RC_QUOTA_UHRM, 0) AS RC_QUOTA_UHRM, ");
				sb.append("         APP_M_RC.RC_APPLY_UHRM AS RC_APPLY_TEMP_UHRM, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_UHRM, 0) = 0 THEN 0 ELSE APP_M_RC.RC_APPLY_UHRM END AS RC_APPLY_UHRM, ");
				sb.append("         CASE WHEN NVL(ORG.RC_QUOTA_UHRM, 0) = 0 THEN 0 ELSE NVL(ORG.RC_QUOTA_UHRM, 0) - APP_M_RC.RC_APPLY_UHRM END AS RC_LAVE_UHRM, ");
				sb.append("         CASE WHEN NVL(ORG.PRD_QUOTA, 0) = 0 OR NVL(ORG.ALL_QUOTA, 0) > 0 THEN 'Y' ELSE 'N' END AS DISABLED_RCQUHRM ");
				
				sb.append("  FROM TBPQC_QUOTA_PRD PRD ");
				sb.append("  LEFT JOIN TBPQC_QUOTA_CONTROL_ORG ORG ON ORG.START_DATE = PRD.START_DATE AND ORG.END_DATE = PRD.END_DATE AND ORG.PRD_TYPE = PRD.PRD_TYPE AND ORG.PRD_ID = PRD.PRD_ID ");
				
				// 全行/處 已申請總額度
				sb.append("  LEFT JOIN ( ");
				sb.append("    SELECT START_DATE, END_DATE, PRD_TYPE, PRD_ID, RC_APPLY_1, RC_APPLY_2, RC_APPLY_3, RC_APPLY_4, RC_APPLY_5, RC_APPLY_6, RC_APPLY_UHRM ");
				sb.append("    FROM ( ");
				sb.append("      SELECT BASE.START_DATE, BASE.END_DATE, BASE.PRD_TYPE, BASE.PRD_ID, ");
				sb.append("             CASE WHEN BASE.REGION_CENTER_ID = '031' THEN 'RC_APPLY_UHRM' ");
				sb.append("                  WHEN BASE.REGION_CENTER_ID = '171' THEN 'RC_APPLY_1' ");
				sb.append("                  WHEN BASE.REGION_CENTER_ID = '172' THEN 'RC_APPLY_2' ");
				sb.append("                  WHEN BASE.REGION_CENTER_ID = '174' THEN 'RC_APPLY_3' ");
				sb.append("                  WHEN BASE.REGION_CENTER_ID = '142' THEN 'RC_APPLY_4' ");
				sb.append("                  WHEN BASE.REGION_CENTER_ID = '145' THEN 'RC_APPLY_5' ");
				sb.append("                  WHEN BASE.REGION_CENTER_ID = '146' THEN 'RC_APPLY_6' ");
				sb.append("              ELSE NULL END AS ORG_TYPE, ");
				sb.append("              NVL(APPLY_Q.SUM_APPLY_QUOTA, 0) AS SUM_APPLY_QUOTA ");
				sb.append("      FROM ( ");
				sb.append("        SELECT DEFN_BASE.REGION_CENTER_ID, PRD.START_DATE, PRD.END_DATE, PRD.PRD_TYPE, PRD.PRD_ID ");
				sb.append("        FROM TBPQC_QUOTA_PRD PRD ");
				sb.append("        LEFT JOIN ( ");
				sb.append("          SELECT DISTINCT REGION_CENTER_ID FROM VWORG_DEFN_INFO ");
				sb.append("          UNION ");
				sb.append("          SELECT '031' AS REGION_CENTER_ID FROM DUAL ");
				sb.append("        ) DEFN_BASE ON 1 = 1 ");
				sb.append("      ) BASE ");
				sb.append("      LEFT JOIN ( ");
				sb.append("        SELECT CASE WHEN MEM.DEPT_ID = '031' THEN '031' ELSE DEFN_APPLY.REGION_CENTER_ID END AS REGION_CENTER_ID, ");
				sb.append("               A.APPLY_PRD_S_DATE, A.APPLY_PRD_E_DATE, A.APPLY_PRD_TYPE, A.APPLY_PRD_ID, SUM(A.APPLY_QUOTA) AS SUM_APPLY_QUOTA ");
				sb.append("        FROM TBPQC_QUOTA_APPLY_M A ");
				sb.append("        LEFT JOIN TBORG_MEMBER MEM ON A.APPLY_EMP_ID = MEM.EMP_ID ");
				sb.append("        LEFT JOIN VWORG_DEFN_INFO DEFN_APPLY ON MEM.DEPT_ID = DEFN_APPLY.BRANCH_NBR ");
				sb.append("        GROUP BY CASE WHEN MEM.DEPT_ID = '031' THEN '031' ELSE DEFN_APPLY.REGION_CENTER_ID END, A.APPLY_PRD_S_DATE, A.APPLY_PRD_E_DATE, A.APPLY_PRD_TYPE, A.APPLY_PRD_ID ");
				sb.append("      ) APPLY_Q ON APPLY_Q.APPLY_PRD_S_DATE = BASE.START_DATE AND APPLY_Q.APPLY_PRD_E_DATE = BASE.END_DATE AND APPLY_Q.APPLY_PRD_TYPE = BASE.PRD_TYPE AND APPLY_Q.APPLY_PRD_ID = BASE.PRD_ID AND APPLY_Q.REGION_CENTER_ID = BASE.REGION_CENTER_ID ");
				sb.append("    ) ");
				sb.append("    PIVOT (SUM(SUM_APPLY_QUOTA) FOR ORG_TYPE IN ('RC_APPLY_1' AS RC_APPLY_1, 'RC_APPLY_2' AS RC_APPLY_2, 'RC_APPLY_3' AS RC_APPLY_3, 'RC_APPLY_4' AS RC_APPLY_4, 'RC_APPLY_5' AS RC_APPLY_5, 'RC_APPLY_6' AS RC_APPLY_6, 'RC_APPLY_UHRM' AS RC_APPLY_UHRM )) ");
				sb.append("  ) APP_M_RC ON APP_M_RC.START_DATE = PRD.START_DATE AND APP_M_RC.END_DATE = PRD.END_DATE AND APP_M_RC.PRD_TYPE = PRD.PRD_TYPE AND APP_M_RC.PRD_ID = PRD.PRD_ID ");
				
				// 已申請總額度
				sb.append("  LEFT JOIN ( ");
				sb.append("    SELECT PRD.START_DATE, PRD.END_DATE, PRD.PRD_TYPE, PRD.PRD_ID, NVL(SUM(A.APPLY_QUOTA), 0) AS APPLY_QUOTA ");
				sb.append("    FROM TBPQC_QUOTA_PRD PRD ");
				sb.append("    LEFT JOIN TBPQC_QUOTA_APPLY_M A ON A.APPLY_PRD_S_DATE = PRD.START_DATE AND A.APPLY_PRD_E_DATE = PRD.END_DATE AND A.APPLY_PRD_TYPE = PRD.PRD_TYPE AND A.APPLY_PRD_ID = PRD.PRD_ID ");
				sb.append("    GROUP BY PRD.START_DATE, PRD.END_DATE, PRD.PRD_TYPE, PRD.PRD_ID ");
				sb.append("  ) APP_M_PRD ON APP_M_PRD.START_DATE = PRD.START_DATE AND APP_M_PRD.END_DATE = PRD.END_DATE AND APP_M_PRD.PRD_TYPE = PRD.PRD_TYPE AND APP_M_PRD.PRD_ID = PRD.PRD_ID ");
				
				sb.append("  WHERE 1 = 1 ");
				
				if (StringUtils.isNotEmpty(inputVO.getPrdType())) {
					sb.append("  AND PRD.PRD_TYPE = :prdType ");
					queryCondition.setObject("prdType", inputVO.getPrdType());
				}
				
				if (StringUtils.isNotEmpty(inputVO.getPrdID())) {
					sb.append("  AND PRD.PRD_ID = :prdID ");
					queryCondition.setObject("prdID", inputVO.getPrdID());
				}
				
				if (StringUtils.isNotEmpty(inputVO.getReportType())) {
					switch (inputVO.getReportType()) {
						case "O":
							sb.append("  AND TO_DATE(PRD.START_DATE, 'yyyyMMdd') <= TRUNC(SYSDATE) ");
							sb.append("  AND TO_DATE(PRD.END_DATE, 'yyyyMMdd') >= TRUNC(SYSDATE) ");
							break;
						case "C":
							sb.append("  AND TO_DATE(PRD.END_DATE, 'yyyyMMdd') < TRUNC(SYSDATE) ");
							break;
					}
				}
				
				sb.append("  ORDER BY PRD.END_DATE DESC, PRD.PRD_ID ");
				sb.append(") BASE ");
				
				queryCondition.setQueryString(sb.toString());

				outputVO.setOrgList(dam.exeQuery(queryCondition));

				break;
		}
		
		this.sendRtnObject(outputVO);
	}
	
	public void updByType(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC101InputVO inputVO = (PQC101InputVO) body;
		PQC101OutputVO outputVO = new PQC101OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		switch (inputVO.getUpdType()) {
			case "Basic":
				List<Map<String, Object>> basicUpdList = inputVO.getBasicUpdList();
				for (Map<String, Object> map : basicUpdList) {
					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					
					sb.append("UPDATE TBPQC_QUOTA_CONTROL_BASIC ");
					sb.append("SET MIN_QUOTA = :minQuota, ");
					sb.append("    MAX_QUOTA = :maxQuota, ");
					sb.append("    VERSION = VERSION + 1, ");
					sb.append("    MODIFIER = :empID, ");
					sb.append("    LASTUPDATE = SYSDATE ");
					sb.append("WHERE PRIVILEGEID = :privilegeid ");
					
					// PK
					queryCondition.setObject("privilegeid", map.get("PRIVILEGE_ID"));
					
					// condition
					queryCondition.setObject("minQuota", map.get("MIN_QUOTA"));
					queryCondition.setObject("maxQuota", map.get("MAX_QUOTA"));
					queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));

					queryCondition.setQueryString(sb.toString());
					
					dam.exeUpdate(queryCondition);
				}
				
				break;
			case "Org":
				List<Map<String, Object>> orgUpdList = inputVO.getOrgUpdList();
				System.out.println("orgUpdList.size():"+orgUpdList.size());
				for (Map<String, Object> map : orgUpdList) {
					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					
					sb.append("SELECT START_DATE, END_DATE, PRD_TYPE, PRD_ID ");
					sb.append("FROM TBPQC_QUOTA_CONTROL_ORG ");
					sb.append("WHERE START_DATE = :startDate ");
					sb.append("AND END_DATE = :endDate ");
					sb.append("AND PRD_TYPE = :prdType ");
					sb.append("AND PRD_ID = :prdID ");
					
					queryCondition.setObject("startDate", map.get("START_DATE"));
					queryCondition.setObject("endDate", map.get("END_DATE"));
					queryCondition.setObject("prdType", map.get("PRD_TYPE"));
					queryCondition.setObject("prdID", map.get("PRD_ID"));
					
					queryCondition.setQueryString(sb.toString());
					
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					
					if (list.size() > 0) {
						sb = new StringBuffer();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

						sb.append("UPDATE TBPQC_QUOTA_CONTROL_ORG ");
						sb.append("SET PRD_QUOTA = :prdQuota, ");
						sb.append("    ALL_QUOTA = :allQuota, ");
						sb.append("    RC_QUOTA_1 = :rcQuota1, ");
						sb.append("    RC_QUOTA_2 = :rcQuota2, ");
						sb.append("    RC_QUOTA_3 = :rcQuota3, ");
						sb.append("    RC_QUOTA_4 = :rcQuota4, ");
						sb.append("    RC_QUOTA_5 = :rcQuota5, ");
						sb.append("    RC_QUOTA_6 = :rcQuota6, ");
						sb.append("    RC_QUOTA_UHRM = :rcQuotaUHRM, ");
						sb.append("    VERSION = VERSION + 1, ");
						sb.append("    MODIFIER = :empID, ");
						sb.append("    LASTUPDATE = SYSDATE ");
						sb.append("WHERE START_DATE = :startDate ");
						sb.append("AND END_DATE = :endDate ");
						sb.append("AND PRD_TYPE = :prdType ");
						sb.append("AND PRD_ID = :prdID ");
						
						// PK
						queryCondition.setObject("startDate", map.get("START_DATE"));
						queryCondition.setObject("endDate", map.get("END_DATE"));
						queryCondition.setObject("prdType", map.get("PRD_TYPE"));
						queryCondition.setObject("prdID", map.get("PRD_ID"));
						
						// condition
						queryCondition.setObject("prdQuota", map.get("PRD_QUOTA"));
						queryCondition.setObject("allQuota", map.get("ALL_QUOTA"));
						queryCondition.setObject("rcQuota1", map.get("RC_QUOTA_1"));
						queryCondition.setObject("rcQuota2", map.get("RC_QUOTA_2"));
						queryCondition.setObject("rcQuota3", map.get("RC_QUOTA_3"));
						queryCondition.setObject("rcQuota4", map.get("RC_QUOTA_4"));
						queryCondition.setObject("rcQuota5", map.get("RC_QUOTA_5"));
						queryCondition.setObject("rcQuota6", map.get("RC_QUOTA_6"));
						queryCondition.setObject("rcQuotaUHRM", map.get("RC_QUOTA_UHRM"));
						queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));

						queryCondition.setQueryString(sb.toString());
						
						dam.exeUpdate(queryCondition);
					} else {
						sb = new StringBuffer();
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

						sb.append("INSERT INTO TBPQC_QUOTA_CONTROL_ORG ( ");
						sb.append("  START_DATE, END_DATE, PRD_TYPE, PRD_ID,  ");
						sb.append("  PRD_QUOTA, ALL_QUOTA, RC_QUOTA_1, RC_QUOTA_2, RC_QUOTA_3, RC_QUOTA_4, RC_QUOTA_5, RC_QUOTA_6, RC_QUOTA_UHRM,  ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES ( ");
						sb.append("  :startDate,  :endDate, :prdType, :prdID, ");
						sb.append("  :prdQuota, :allQuota, :rcQuota1, :rcQuota2, :rcQuota3, :rcQuota4, :rcQuota5, :rcQuota6, :rcQuotaUHRM, ");
						sb.append("  0, SYSDATE, :empID, :empID, SYSDATE ");
						sb.append(") ");
						
						// PK
						queryCondition.setObject("startDate", map.get("START_DATE"));
						queryCondition.setObject("endDate", map.get("END_DATE"));
						queryCondition.setObject("prdType", map.get("PRD_TYPE"));
						queryCondition.setObject("prdID", map.get("PRD_ID"));
						
						// condition
						queryCondition.setObject("prdQuota", map.get("PRD_QUOTA"));
						queryCondition.setObject("allQuota", map.get("ALL_QUOTA"));
						queryCondition.setObject("rcQuota1", map.get("RC_QUOTA_1"));
						queryCondition.setObject("rcQuota2", map.get("RC_QUOTA_2"));
						queryCondition.setObject("rcQuota3", map.get("RC_QUOTA_3"));
						queryCondition.setObject("rcQuota4", map.get("RC_QUOTA_4"));
						queryCondition.setObject("rcQuota5", map.get("RC_QUOTA_5"));
						queryCondition.setObject("rcQuota6", map.get("RC_QUOTA_6"));
						queryCondition.setObject("rcQuotaUHRM", map.get("RC_QUOTA_UHRM"));
						queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));

						queryCondition.setQueryString(sb.toString());
						
						dam.exeUpdate(queryCondition);
					}
				}
				
				break;
		}
		
		this.sendRtnObject(outputVO);
	}
	
}