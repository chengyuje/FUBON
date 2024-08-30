package com.systex.jbranch.app.server.fps.pms422;

import static com.systex.jbranch.fubon.jlb.DataFormat.getCustIdMaskForHighRisk;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.defaultString;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * 分行人員與客戶資金往來異常報表
 */
@Component("pms422")
@Scope("prototype")
public class PMS422 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS422OutputVO outputVO = new PMS422OutputVO();
	    outputVO = this.queryData(body);

	    sendRtnObject(outputVO);
	}

	public PMS422OutputVO queryData(Object body) throws JBranchException {

		initUUID();

		PMS422InputVO inputVO = (PMS422InputVO) body;
		PMS422OutputVO outputVO = new PMS422OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員

		sb.append("SELECT CASE WHEN RPT.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("       SNAP_DATE, ");
		sb.append("       TO_CHAR(CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("       CASE WHEN TRUNC(CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("       BRANCH_NBR, ");
		sb.append("       BRANCH_NAME, ");
		sb.append("       EMP_ID, ");
		sb.append("       AO_CODE, ");
		sb.append("       ID, ");
		sb.append("       NAME, ");
		sb.append("       AMT_SUM, ");
		sb.append("       NOTE_TYPE, ");
		sb.append("       NOTE, ");
		sb.append("       NOTE2, ");
		sb.append("       NOTE3, ");
		sb.append("       RECORD_SEQ, ");
		sb.append("       EX_FLAG, ");
		sb.append("       FIRSTUPDATE, ");
		sb.append("       LASTUPDATE, ");
		sb.append("       MODIFIER, ");
		sb.append("       ACCT_OUT_OWNER, ");
		sb.append("       TXN_DATE, ");
		sb.append("       IN_CUST_ID, ");
		sb.append("       IN_CUST_NAME, ");
		sb.append("       IN_ACCT_NUMBER, ");
		sb.append("       OUT_CUST_ID, ");
		sb.append("       OUT_CUST_NAME, ");
		sb.append("       OUT_ACCT_NUMBER, ");
		sb.append("       TXN_AMT, ");
		sb.append("       DATA_TYPE, ");
		sb.append("       SEQNO, ");
		sb.append("       SOURCE_OF_DEMAND_N, ");
		sb.append("       ALLOW_FLAG ");
		sb.append("FROM ( ");

		// 舊資料
		sb.append("  SELECT MEM.E_DEPT_ID, ");
		sb.append("         SUBSTR(RPT.SEQNO, 0, 3) AS DATA_TYPE, ");
		sb.append("         TO_CHAR(RPT.SNAP_DATE, 'YYYYMMDD') AS SNAP_DATE, ");
		sb.append("         RPT.CREATETIME, ");
		sb.append("         ORG.REGION_CENTER_ID, ");
		sb.append("         ORG.BRANCH_AREA_ID, ");
		sb.append("         RPT.BRANCH_NBR, ");
		sb.append("         ORG.BRANCH_NAME, ");
		sb.append("         MEM.EMP_ID, ");
		sb.append("         RPT.AO_CODE, ");
		sb.append("         RPT.ID, ");
		sb.append("         RPT.NAME, ");
		sb.append("         RPT.AMT_SUM, ");
		sb.append("         RPT.NOTE_TYPE, ");
		sb.append("         RPT.NOTE, ");
		sb.append("         RPT.NOTE2, ");
		sb.append("         RPT.NOTE3, ");
		sb.append("         RPT.RECORD_SEQ, ");
		sb.append("         RPT.EX_FLAG, ");
		sb.append("         RPT.FIRSTUPDATE, ");
		sb.append("         RPT.LASTUPDATE, ");
		sb.append("         RPT.MODIFIER, ");
		sb.append("         RPT.RM_FLAG, ");
		
		sb.append("         NULL AS ACCT_OUT_OWNER, ");
		sb.append("         NULL AS TXN_DATE, ");
		sb.append("         NULL AS IN_CUST_ID, ");
		sb.append("         NULL AS IN_CUST_NAME, ");
		sb.append("         NULL AS IN_ACCT_NUMBER, ");
		sb.append("         NULL AS OUT_CUST_ID, ");
		sb.append("         NULL AS OUT_CUST_NAME, ");
		sb.append("         NULL AS OUT_ACCT_NUMBER, ");
		sb.append("         NULL AS TXN_AMT, ");
		sb.append("         NULL AS SEQNO, ");
		sb.append("         'MONITOR' AS SOURCE_OF_DEMAND, ");
		sb.append("         PAR.PARAM_NAME AS SOURCE_OF_DEMAND_N, ");
		sb.append("         'N' AS ALLOW_FLAG ");
		sb.append("  FROM TBPMS_SELF_ACCT_MONITOR_RPT RPT ");
		sb.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON RPT.BRANCH_NBR = ORG.DEPT_ID AND RPT.SNAP_DATE BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'PMS.PMS422_SOURCE_OF_DEMAND' AND 'MONITOR' = PAR.PARAM_CODE ");
		// MODIFY BY OCEAN : 改成用歷史檔案串交易當下的員工資料，後來想到inner join可能離職之後就都查不到了
//		sb.append("  INNER JOIN TBORG_MEMBER MEM ON MEM.CUST_ID = RPT.ID AND MEM.SERVICE_FLAG = 'A' AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON RPT.ID = MEM.CUST_ID AND RPT.BRANCH_NBR = MEM.DEPT_ID AND RPT.SNAP_DATE BETWEEN MEM.START_TIME AND MEM.END_TIME ");
		sb.append("  WHERE SUBSTR(RPT.SEQNO, 0, 3) = 'OLD' ");

		if (StringUtils.isNotEmpty(inputVO.getCustID())) {
			sb.append("  AND RPT.ID = :custID ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("AND RPT.EX_FLAG IS NOT NULL ");
					break;
				case "02":
					sb.append("AND RPT.EX_FLAG IS NULL ");
					break;
			}
		}

		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sb.append("AND RPT.EX_FLAG IS NULL ");
		}
		
		sb.append("  UNION ");

		// 新資料
		sb.append("  SELECT MEM.E_DEPT_ID, ");
		sb.append("         SUBSTR(RPT.SEQNO, 0, 3) AS DATA_TYPE, ");
		sb.append("         TO_CHAR(RPT.SNAP_DATE, 'YYYYMMDD') AS SNAP_DATE, ");
		sb.append("         RPT.CREATETIME, ");
		sb.append("         ORG.REGION_CENTER_ID, ");
		sb.append("         ORG.BRANCH_AREA_ID, ");
		sb.append("         RPT.BRANCH_NBR, ");
		sb.append("         ORG.BRANCH_NAME, ");
		sb.append("         MEM.EMP_ID, ");
		sb.append("         RPT.AO_CODE, ");
		sb.append("         RPT.ID, ");
		sb.append("         RPT.NAME, ");
		sb.append("         RPT.AMT_SUM, ");
		sb.append("       	RPTD.NOTE_TYPE, ");
		sb.append("         RPTD.NOTE, ");
		sb.append("         RPTD.NOTE2, ");
		sb.append("         RPTD.NOTE3, ");
		sb.append("         RPTD.RECORD_SEQ, ");
		sb.append("         RPTD.EX_FLAG, ");
		sb.append("         RPTD.FIRSTUPDATE, ");
		sb.append("         RPTD.LASTUPDATE, ");
		sb.append("         RPTD.MODIFIER, ");
		sb.append("         RPT.RM_FLAG, ");

		sb.append("         RPTD.ACCT_OUT_OWNER, ");
		sb.append("         TO_CHAR(RPTD.TXN_DATE, 'YYYY/MM/DD') AS TXN_DATE, ");
		sb.append("         RPTD.IN_CUST_ID, ");
		sb.append("         RPTD.IN_CUST_NAME, ");
		sb.append("         RPTD.IN_ACCT_NUMBER, ");
		sb.append("         RPTD.OUT_CUST_ID, ");
		sb.append("         RPTD.OUT_CUST_NAME, ");
		sb.append("         RPTD.OUT_ACCT_NUMBER, ");
		sb.append("         RPTD.AMT_NTD AS TXN_AMT, ");
		sb.append("         RPTD.SEQNO, ");
		sb.append("         RPTD.SOURCE_OF_DEMAND, ");
		sb.append("         PAR.PARAM_NAME AS SOURCE_OF_DEMAND_N, ");
		sb.append("         RPTD.ALLOW_FLAG ");
		sb.append("  FROM ( ");
		sb.append("    SELECT * ");
		sb.append("    FROM TBPMS_SELF_ACCT_MONITOR_RPT ");
		sb.append("    WHERE (TO_CHAR(SNAP_DATE, 'YYYYMM'), BRANCH_NBR, AO_CODE, ID, NAME, AMT_SUM) IN ( ");
		sb.append("      SELECT TO_CHAR(SNAP_DATE, 'YYYYMM') AS YYYYMM, BRANCH_NBR, AO_CODE, ID, NAME, MAX(AMT_SUM) AS AMT_SUM ");
		sb.append("      FROM TBPMS_SELF_ACCT_MONITOR_RPT ");
		sb.append("      GROUP BY TO_CHAR(SNAP_DATE, 'YYYYMM'), BRANCH_NBR, AO_CODE, ID, NAME ");
		sb.append("    ) ");
		sb.append("  ) RPT ");
		sb.append("  LEFT JOIN TBPMS_SELF_ACCT_MONITOR_RPT_D RPTD ON TO_CHAR(RPTD.TXN_DATE, 'YYYYMM') = TO_CHAR(RPT.SNAP_DATE, 'YYYYMM') AND RPTD.ID = RPT.ID ");
		sb.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON RPT.BRANCH_NBR = ORG.DEPT_ID AND RPT.SNAP_DATE BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'PMS.PMS422_SOURCE_OF_DEMAND' AND RPTD.SOURCE_OF_DEMAND = PAR.PARAM_CODE ");
		// MODIFY BY OCEAN : 改成用歷史檔案串交易當下的員工資料，後來想到inner join可能離職之後就都查不到了
//		sb.append("  INNER JOIN TBORG_MEMBER MEM ON MEM.CUST_ID = RPT.ID AND MEM.SERVICE_FLAG = 'A' AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON RPT.ID = MEM.CUST_ID AND RPT.BRANCH_NBR = MEM.DEPT_ID AND RPT.SNAP_DATE BETWEEN MEM.START_TIME AND MEM.END_TIME ");
		sb.append("  WHERE SUBSTR(RPT.SEQNO, 0, 3) <> 'OLD' ");

		if (StringUtils.isNotEmpty(inputVO.getCustID())) {
			sb.append("  AND RPT.ID = :custID ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("  AND RPTD.FIRSTUPDATE IS NOT NULL ");
					break;
				case "02":
					sb.append("  AND RPTD.FIRSTUPDATE IS NULL ");
					break;
			}
		}
		
		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
//			sb.append("AND RPT.EX_FLAG IS NULL ");
			// modify by ocean : 20240524 偲偲信件
			sb.append("  AND RPTD.FIRSTUPDATE IS NULL ");
		}
		
		sb.append(") RPT ");

		sb.append("WHERE 1 = 1 ");

		sb.append("AND ( ");
		sb.append("    (RPT.ALLOW_FLAG = 'Y' AND AMT_SUM >= 90000) ");  // 為Y(白名單)且累積金額達90000時
		sb.append(" OR (RPT.ALLOW_FLAG = 'N' AND AMT_SUM >= 30000)  "); // 為N(非白名單)且累積金額達30000時
		sb.append(") ");
		
		if (null != inputVO.getStart()) {
			sb.append("AND TRUNC(RPT.CREATETIME) >= TRUNC(:sDate)  ");
			queryCondition.setObject("sDate", inputVO.getStart());
		}

		if (null != inputVO.getEnd()) {
			sb.append("AND TRUNC(RPT.CREATETIME) <= TRUNC(:eDate)  ");
			queryCondition.setObject("eDate", inputVO.getEnd());
		}

		if (null != inputVO.getSourceOfDemand()) {
			sb.append("AND SOURCE_OF_DEMAND = :sourceOfDemand  ");
			queryCondition.setObject("sourceOfDemand", inputVO.getSourceOfDemand());
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0 ||
			StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {

			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("AND RPT.BRANCH_NBR = :branch ");
				queryCondition.setObject("branch", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("AND RPT.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :area) ");
				queryCondition.setObject("area", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("AND RPT.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :region) ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("AND (");
				sb.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE MT.EMP_ID = RPT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("  OR RPT.BRANCH_AREA_ID = :uhrmOP ");
				sb.append(") ");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("AND RPT.RM_FLAG = 'U' ");
		}
		
		sb.append("ORDER BY RPT.CREATETIME DESC, RPT.SNAP_DATE DESC, RPT.BRANCH_NBR, RPT.ID, TXN_DATE ");

		queryCondition.setQueryString(sb.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		outputVO.setReportList(dam.exeQuery(queryCondition));

		return outputVO;
	}

	public void queryDataV2(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS422OutputVO outputVO = new PMS422OutputVO();
	    outputVO = this.queryDataV2(body);

	    sendRtnObject(outputVO);
	}

	public PMS422OutputVO queryDataV2(Object body) throws JBranchException {

		initUUID();

		PMS422InputVO inputVO = (PMS422InputVO) body;
		PMS422OutputVO outputVO = new PMS422OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員

		sb.append("SELECT * ");
		sb.append("FROM ( ");
		sb.append("  SELECT CASE WHEN RPT.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("         RPT.YYYYMM, ");
		sb.append("         CASE WHEN TRUNC(RPT.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("         TO_CHAR(RPT.CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("         NVL(ORG.REGION_CENTER_ID, ' ') AS REGION_CENTER_ID, ");
		sb.append("         NVL(ORG.BRANCH_AREA_ID, ' ') AS BRANCH_AREA_ID, ");
		sb.append("         NVL(RPT.BRANCH_NBR, ' ') AS BRANCH_NBR, ");
		sb.append("         NVL(ORG.BRANCH_NAME, ' ') AS BRANCH_NAME, ");
		sb.append("         RPT.DATA_DATE, ");
		sb.append("         TO_CHAR(RPT.ACT_START_DATE, 'YYYY/MM/DD') AS ACT_START_DATE, ");
		sb.append("         TO_CHAR(RPT.ACT_END_DATE, 'YYYY/MM/DD') AS ACT_END_DATE, ");
		sb.append("         RPT.EMP_ID, ");
		sb.append("         RPT.AO_CODE, ");
		sb.append("         RPT.OUT_CUST_ID1, ");
		sb.append("         RPT.OUT_CUST_NAME1, ");
		sb.append("         RPT.OUT_TYPE1, ");
		sb.append("         RPT.IN_CUST_ID1, ");
		sb.append("         RPT.IN_CUST_NAME1, ");
		sb.append("         RPT.IN_TYPE1, ");
		sb.append("         RPT.A_TO_B_ALLOW_FLAG, ");
		sb.append("         RPT.SUM_TX_AMT1, ");
		sb.append("         RPT.IN_CUST_ID2, ");
		sb.append("         RPT.IN_CUST_NAME2, ");
		sb.append("         RPT.B_TO_C_ALLOW_FLAG, ");
		sb.append("         RPT.SUM_TX_AMT2, ");
		sb.append("         NVL((SELECT COUNT(*) AS COUNTS ");
		sb.append("              FROM TBPMS_SELF_ACCT_FLOW_RPT_D D ");
		sb.append("              WHERE D.FIRSTUPDATE IS NOT NULL ");
		sb.append("              AND RPT.OUT_CUST_ID1 = D.OUT_CUST_ID ");
		sb.append("              AND RPT.IN_CUST_ID1 = D.IN_CUST_ID ");
		sb.append("              AND TRUNC(D.TXN_DATE) BETWEEN TRUNC(RPT.ACT_START_DATE) AND TRUNC(RPT.ACT_END_DATE) ");
		sb.append("              GROUP BY D.OUT_CUST_ID, D.IN_CUST_ID), 0) ");
		sb.append("         + ");
		sb.append("         NVL((SELECT NVL(COUNT(1), 0) ");
		sb.append("              FROM TBPMS_SELF_ACCT_FLOW_RPT_D D ");
		sb.append("              WHERE D.FIRSTUPDATE IS NOT NULL ");
		sb.append("              AND RPT.IN_CUST_ID1 = D.OUT_CUST_ID ");
		sb.append("              AND RPT.IN_CUST_ID2 = D.IN_CUST_ID ");
		sb.append("              AND TRUNC(D.TXN_DATE) BETWEEN TRUNC(RPT.ACT_START_DATE) AND TRUNC(RPT.ACT_END_DATE) ");
		sb.append("              GROUP BY D.OUT_CUST_ID, D.IN_CUST_ID), 0) AS RESPONDING ");
		sb.append("         , ");
		sb.append("         NVL((SELECT NVL(COUNT(1), 0) ");
		sb.append("              FROM TBPMS_SELF_ACCT_FLOW_RPT_D D ");
		sb.append("              WHERE D.FIRSTUPDATE IS NULL ");
		sb.append("              AND RPT.OUT_CUST_ID1 = D.OUT_CUST_ID ");
		sb.append("              AND RPT.IN_CUST_ID1 = D.IN_CUST_ID ");
		sb.append("              AND TRUNC(D.TXN_DATE) BETWEEN TRUNC(RPT.ACT_START_DATE) AND TRUNC(RPT.ACT_END_DATE) ");
		sb.append("              GROUP BY D.OUT_CUST_ID, D.IN_CUST_ID), 0) ");
		sb.append("         + ");
		sb.append("         NVL((SELECT NVL(COUNT(1), 0) ");
		sb.append("              FROM TBPMS_SELF_ACCT_FLOW_RPT_D D ");
		sb.append("              WHERE D.FIRSTUPDATE IS NULL ");
		sb.append("              AND RPT.IN_CUST_ID1 = D.OUT_CUST_ID ");
		sb.append("              AND RPT.IN_CUST_ID2 = D.IN_CUST_ID ");
		sb.append("              AND TRUNC(D.TXN_DATE) BETWEEN TRUNC(RPT.ACT_START_DATE) AND TRUNC(RPT.ACT_END_DATE) ");
	    sb.append("              GROUP BY D.OUT_CUST_ID, D.IN_CUST_ID), 0) AS NOT_RESPONDING ");
		sb.append("  FROM TBPMS_SELF_ACCT_FLOW_RPT RPT ");
		sb.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON RPT.BRANCH_NBR = ORG.DEPT_ID AND RPT.DATA_DATE BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON RPT.EMP_ID = MEM.EMP_ID AND RPT.BRANCH_NBR = MEM.DEPT_ID AND RPT.DATA_DATE BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND ( ");
		sb.append("    (RPT.A_TO_B_ALLOW_FLAG = 'N' OR RPT.B_TO_C_ALLOW_FLAG = 'N') "); // 其中一欄=N(不符合白名單範圍內)顯示該筆
		sb.append("    OR (RPT.A_TO_B_ALLOW_FLAG = 'Y' AND RPT.B_TO_C_ALLOW_FLAG = 'Y' AND (RPT.SUM_TX_AMT1 + RPT.SUM_TX_AMT2) >= 90000) "); // 皆為Y(白名單)且累積金額達90000時
		sb.append("  ) ");
		
		if (StringUtils.isNotEmpty(inputVO.getOutCustID())) {
			sb.append("  AND RPT.OUT_CUST_ID1 LIKE :outCustID ");
			queryCondition.setObject("outCustID", inputVO.getOutCustID().toUpperCase() + "%");
		}

		if (null != inputVO.getStart()) {
			sb.append("  AND TRUNC(RPT.CREATETIME) >= TRUNC(:sDate) ");
			queryCondition.setObject("sDate", inputVO.getStart());
		}

		if (null != inputVO.getEnd()) {
			sb.append("  AND TRUNC(RPT.CREATETIME) <= TRUNC(:eDate) ");
			queryCondition.setObject("eDate", inputVO.getEnd());
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("  AND RPT.BRANCH_NBR = :branch ");
				queryCondition.setObject("branch", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("  AND RPT.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :area) ");
				queryCondition.setObject("area", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("  AND RPT.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :region) ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("  AND (");
				sb.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE MEM.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("    OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append("  )");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("  AND RPT.RM_FLAG = 'U' ");
		}
	
		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sb.append("  AND RPT.NOT_RESPONDING > 0 ");
		}

		sb.append(") BASE ");

		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("AND BASE.RESPONDING > 0 ");
					break;
				case "02":
					sb.append("AND BASE.NOT_RESPONDING > 0 ");
					break;
			}
		}


		sb.append("ORDER BY BASE.CREATETIME DESC, BASE.DATA_DATE DESC, BASE.BRANCH_NBR, BASE.OUT_CUST_ID1 ");

		queryCondition.setQueryString(sb.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		outputVO.setReportList(dam.exeQuery(queryCondition));

		return outputVO;
	}

	// query detail action
	public void queryDetail(Object body, IPrimitiveMap header) throws JBranchException {

		PMS422InputVO inputVO = (PMS422InputVO) body;
		PMS422OutputVO outputVO = new PMS422OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT TXN_DATE, ");
		sb.append("       CASE WHEN TRUNC(CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("       ID, ");
		sb.append("       EMP_NAME, ");
		sb.append("       EMP_ACCT, ");
		sb.append("       CUST_NAME, ");
		sb.append("       CUST_ACCT, ");
		sb.append("       AMT_ORGD, ");
		sb.append("       AMT_NTD ");
		sb.append("FROM TBPMS_SELF_ACCT_MONITOR_RPT_D ");
		sb.append("WHERE TXN_DATE >= trunc(to_date(:end, 'YYYYMMDD'), 'MM') ");
		sb.append("AND TXN_DATE < trunc(to_date(:end, 'YYYYMMDD')) + 1 ");
		sb.append("AND ID = :id ");
		sb.append("ORDER BY TXN_DATE DESC ");

		queryCondition.setObject("end", inputVO.getSnapDate());
		queryCondition.setObject("id", inputVO.getEmp_id());

		queryCondition.setQueryString(sb.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		sendRtnObject(outputVO);
	}

	public void queryDetailV2(Object body, IPrimitiveMap header) throws JBranchException {

		PMS422InputVO inputVO = (PMS422InputVO) body;
		PMS422OutputVO outputVO = new PMS422OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT TO_CHAR(DATA_DATE, 'YYYY/MM/DD') AS DATA_DATE, ");
		sb.append("       CASE WHEN TRUNC(CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("       TO_CHAR(TXN_DATE, 'YYYY/MM/DD') AS TXN_DATE, ");
		sb.append("       EMP_ID, ");
		sb.append("       OUT_TYPE, ");
		sb.append("       OUT_CUST_NAME, ");
		sb.append("       OUT_CUST_ID, ");
		sb.append("       OUT_ACCT, ");
		sb.append("       IN_TYPE, ");
		sb.append("       IN_CUST_NAME, ");
		sb.append("       IN_CUST_ID, ");
		sb.append("       IN_ACCT, ");
		sb.append("       TX_AMT, ");
		sb.append("       JRNL_NO, ");
		sb.append("       NOTE_TYPE, ");
		sb.append("       NOTE, ");
		sb.append("       NOTE2, ");
		sb.append("       NOTE3, ");
		sb.append("       RECORD_SEQ, ");
		sb.append("       HR_ATTR, ");
		sb.append("       FIRSTUPDATE, ");
		sb.append("       VERSION, ");
		sb.append("       TO_CHAR(CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("       CREATOR, ");
		sb.append("       MODIFIER, ");
		sb.append("       LASTUPDATE ");
		sb.append("FROM TBPMS_SELF_ACCT_FLOW_RPT_D ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND OUT_CUST_ID = :outCustID1 ");
		sb.append("AND IN_CUST_ID = :inCustID1 ");
		sb.append("AND TRUNC(TXN_DATE) BETWEEN TO_DATE(:actStartDate, 'YYYY/MM/DD') AND TO_DATE(:actEndDate, 'YYYY/MM/DD') ");

		sb.append("UNION ");

		sb.append("SELECT TO_CHAR(DATA_DATE, 'YYYY/MM/DD') AS DATA_DATE, ");
		sb.append("       CASE WHEN TRUNC(CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("       TO_CHAR(TXN_DATE, 'YYYY/MM/DD') AS TXN_DATE, ");
		sb.append("       EMP_ID, ");
		sb.append("       OUT_TYPE, ");
		sb.append("       OUT_CUST_NAME, ");
		sb.append("       OUT_CUST_ID, ");
		sb.append("       OUT_ACCT, ");
		sb.append("       IN_TYPE, ");
		sb.append("       IN_CUST_NAME, ");
		sb.append("       IN_CUST_ID, ");
		sb.append("       IN_ACCT, ");
		sb.append("       TX_AMT, ");
		sb.append("       JRNL_NO, ");
		sb.append("       NOTE_TYPE, ");
		sb.append("       NOTE, ");
		sb.append("       NOTE2, ");
		sb.append("       NOTE3, ");
		sb.append("       RECORD_SEQ, ");
		sb.append("       HR_ATTR, ");
		sb.append("       FIRSTUPDATE, ");
		sb.append("       VERSION, ");
		sb.append("       TO_CHAR(CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("       CREATOR, ");
		sb.append("       MODIFIER, ");
		sb.append("       LASTUPDATE ");
		sb.append("FROM TBPMS_SELF_ACCT_FLOW_RPT_D ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND OUT_CUST_ID = :inCustID1 ");
		sb.append("AND IN_CUST_ID = :inCustID2 ");
		sb.append("AND TRUNC(TXN_DATE) BETWEEN TO_DATE(:actStartDate, 'YYYY/MM/DD') AND TO_DATE(:actEndDate, 'YYYY/MM/DD') ");

		sb.append("ORDER BY TXN_DATE ");

		queryCondition.setObject("outCustID1", inputVO.getOutCustID1());
		queryCondition.setObject("inCustID1", inputVO.getInCustID1());
		queryCondition.setObject("inCustID2", inputVO.getInCustID2());
		queryCondition.setObject("actStartDate", inputVO.getActStartDate());
		queryCondition.setObject("actEndDate", inputVO.getActEndDate());

		queryCondition.setQueryString(sb.toString());

		outputVO.setDtlList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}

	// save action 由前端傳回篩選過的使用者編輯資料並更新
	public void save(Object body, IPrimitiveMap header) throws JBranchException {

		PMS422InputVO inputVO = (PMS422InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		for (Map<String, Object> map : inputVO.getEditedList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();

			switch((String) map.get("DATA_TYPE")) {
				// 舊資料
				case "OLD":
					sb.append("UPDATE TBPMS_SELF_ACCT_MONITOR_RPT ");
					sb.append("SET EX_FLAG = :exFlag, ");
					sb.append("    NOTE_TYPE = :noteType, ");
					sb.append("    NOTE = :note, ");
					sb.append("    NOTE2 = :note2, ");
					sb.append("    NOTE3 = :note3, ");
					sb.append("    RECORD_SEQ = :recordSEQ, ");
					sb.append("    MODIFIER = :modifier, ");
					sb.append("    LASTUPDATE = sysdate ");

					if (null == map.get("FIRSTUPDATE")) {
						sb.append("    , FIRSTUPDATE = sysdate ");
					}

					sb.append("WHERE to_char(SNAP_DATE, 'yyyymmdd') = :snapDate ");
					sb.append("AND BRANCH_NBR = :branchNbr ");
					sb.append("AND ID = :id ");

					// KEY
					queryCondition.setObject("snapDate", map.get("SNAP_DATE"));
					queryCondition.setObject("branchNbr", map.get("BRANCH_NBR"));
					queryCondition.setObject("id", map.get("ID"));

					// CONTENT
					queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
					queryCondition.setObject("note", map.get("NOTE"));
					queryCondition.setObject("note2", map.get("NOTE2"));
					queryCondition.setObject("note3", map.get("NOTE3"));
					queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
					queryCondition.setObject("exFlag", map.get("EX_FLAG"));
					queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());

					queryCondition.setQueryString(sb.toString());

					dam.exeUpdate(queryCondition);

					break;
				// 新資料
				default :
					sb.append("UPDATE TBPMS_SELF_ACCT_MONITOR_RPT_D ");
					sb.append("SET EX_FLAG = :exFlag, ");
					sb.append("    NOTE_TYPE = :noteType, ");
					sb.append("    NOTE = :note, ");
					sb.append("    NOTE2 = :note2, ");
					sb.append("    NOTE3 = :note3, ");
					sb.append("    RECORD_SEQ = :recordSEQ, ");
					sb.append("    MODIFIER = :modifier, ");
					sb.append("    LASTUPDATE = sysdate ");

					if (null == map.get("FIRSTUPDATE")) {
						sb.append("    , FIRSTUPDATE = sysdate ");
					}

					sb.append("WHERE SEQNO = :seqNO ");

					// KEY
					queryCondition.setObject("seqNO", map.get("SEQNO"));

					// CONTENT
					queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
					queryCondition.setObject("note", map.get("NOTE"));
					queryCondition.setObject("note2", map.get("NOTE2"));
					queryCondition.setObject("note3", map.get("NOTE3"));
					queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
					queryCondition.setObject("exFlag", map.get("EX_FLAG"));
					queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());

					queryCondition.setQueryString(sb.toString());

					dam.exeUpdate(queryCondition);

					break;
			}
		}

		sendRtnObject(null);
	}

	public void saveR(Object body, IPrimitiveMap header) throws JBranchException {

		PMS422InputVO inputVO = (PMS422InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		for (Map<String, Object> map : inputVO.getEditedList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();

			sb.append("UPDATE TBPMS_SELF_ACCT_FLOW_RPT_D ");
			sb.append("SET HR_ATTR = :hrAttr, ");
			sb.append("    NOTE_TYPE = :noteType, ");
			sb.append("    NOTE = :note, ");
			sb.append("    NOTE2 = :note2, ");
			sb.append("    NOTE3 = :note3, ");
			sb.append("    RECORD_SEQ = :recordSEQ, ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = sysdate ");

			if (null == map.get("FIRSTUPDATE")) {
				sb.append("    ,  ");
				sb.append("    FIRSTUPDATE = sysdate ");
			}

			sb.append("WHERE JRNL_NO = :jrnlNO ");

			// KEY
			queryCondition.setObject("jrnlNO", map.get("JRNL_NO"));

			// CONTENT
			queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
			queryCondition.setObject("note", map.get("NOTE"));
			queryCondition.setObject("note2", map.get("NOTE2"));
			queryCondition.setObject("note3", map.get("NOTE3"));
			queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());

			queryCondition.setQueryString(sb.toString());

			dam.exeUpdate(queryCondition);
		}

		sendRtnObject(null);
	}

	// export action 匯出 Csv
	public void export(Object body, IPrimitiveMap header) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();

		PMS422OutputVO outputVO = this.queryData(body);

		String[] csvHeader = { "私銀註記", "最新統計日期(註:當有新增交易時同步更新統計日期)", "資料來源", "理專歸屬行", "理專姓名",
							   "交易日期", "轉出姓名", "轉出帳號", "轉入姓名", "轉入帳號", "交易金額",
							   "累計金額(註:當有新增交易時同步更新累積金額)", 
							   "查證方式", "電訪錄音編號", "資金來源/帳戶關係", "具體原因/用途", "初判異常轉法遵部調查",
							   "首次建立時間", "最新異動人員", "最新異動日期" };
		String[] csvMain   = { "RM_FLAG", "CREATETIME", "SOURCE_OF_DEMAND_N", "BRANCH_NBR", "NAME",
							   "TXN_DATE", "OUT_CUST_NAME", "OUT_ACCT_NUMBER", "IN_CUST_NAME", "IN_ACCT_NUMBER", "TXN_AMT",
							   "AMT_SUM", 
							   "NOTE", "RECORD_SEQ", "NOTE2", "NOTE3", "EX_FLAG",
							   "FIRSTUPDATE", "MODIFIER", "LASTUPDATE"};


		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		List<Map<String, Object>> list = outputVO.getReportList();
		List listCSV = new ArrayList();
		
		if (isEmpty(list))
			listCSV.add(new String[] { "查無資料" });
		else {
			SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
			for (Map<String, Object> map : list) {
				String[] records = new String[csvHeader.length];

				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "FIRSTUPDATE":	// 首次建立時間
						case "LASTUPDATE":	// 最新異動日期
							records[i] = formatDate(map.get(csvMain[i]), timeSdf);
							break;
						case "BRANCH_NBR":	// 理專歸屬行
							records[i] = format("%s-%s", defaultString((String) map.get(csvMain[i])), defaultString((String) map.get("BRANCH_NAME")));
							break;
						case "ID":			// 行員身分證號（高風險隱蔽資料）
							records[i] = getCustIdMaskForHighRisk(defaultString((String) map.get(csvMain[i])));
							break;
						case "TXN_AMT":		// 交易金額
						case "AMT_SUM":		// 累計金額
							if (null != map.get(csvMain[i])) {
								records[i] = new DecimalFormat("#,##0.00").format(map.get(csvMain[i]));
							} else {
								records[i] = "0.00";
							}

							break;
						case "EX_FLAG":		// 初判異常轉法遵部調查 Y: 是、N: 否
							records[i] = formatFlag(defaultString((String) map.get(csvMain[i]))); // 初判異常轉法遵部調查 Y: 是、N: 否
							break;
						case "NOTE":
							String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");
							
							if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
								note = note + "：" + StringUtils.defaultString((String) map.get(csvMain[i]));
							}
							
							records[i] = (StringUtils.isNotEmpty(note) ? note : "");
							break;
						case "RECORD_SEQ":
							records[i] = checkIsNull(map, csvMain[i]) + "";
							break;
						default:
							if (null != map.get(csvMain[i])) {
								records[i] = defaultString(map.get(csvMain[i]).toString());
							} else {
								records[i] = "";
							}
							
							break;
					}
				}

				for (int index = 0; index < records.length; index++)
					records[index] = format("=\"%s\"", records[index]);

				listCSV.add(records);
			}
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);

		notifyClientToDownloadFile(csv.generateCSV(), format("分行人員與客戶資金往來異常報表(RM與客戶往來)_%s.csv", dateSdf.format(new Date())));
	}

	// 理專十誡2.O 匯出CSV
	public void exportV2(Object body, IPrimitiveMap header) throws JBranchException {

		PMS422InputVO inputVO = (PMS422InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員

		boolean mainFlag = StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0;
		boolean uhrmFlag = StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm");
		boolean headFlag = headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT CASE WHEN RPT.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("         RPT.YYYYMM, ");
		sb.append("         RPT.CREATETIME, ");
		sb.append("         NVL(RPT.BRANCH_NBR, ' ') AS BRANCH_NBR, ");
		sb.append("         NVL(ORG.BRANCH_NAME, ' ') AS BRANCH_NAME, ");
		sb.append("         RPT.DATA_DATE, ");
		sb.append("         RPT.ACT_START_DATE, ");
		sb.append("         RPT.ACT_END_DATE, ");
		sb.append("         RPT.EMP_ID, ");
		sb.append("         RPT.AO_CODE, ");
		sb.append("         RPT.OUT_CUST_ID1, ");
		sb.append("         RPT.OUT_CUST_NAME1, ");
		sb.append("         RPT.OUT_TYPE1, ");
		sb.append("         RPT.IN_CUST_ID1, ");
		sb.append("         RPT.IN_CUST_NAME1, ");
		sb.append("         RPT.IN_TYPE1, ");
		sb.append("         RPT.SUM_TX_AMT1, ");
		sb.append("         RPT.IN_CUST_ID2, ");
		sb.append("         RPT.IN_CUST_NAME2, ");
		sb.append("         RPT.SUM_TX_AMT2, ");
		sb.append("         RPT.RESPONDING, ");
		sb.append("         RPT.NOT_RESPONDING ");
		sb.append("  FROM TBPMS_SELF_ACCT_FLOW_RPT RPT ");
		sb.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON RPT.BRANCH_NBR = ORG.DEPT_ID AND RPT.DATA_DATE BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON RPT.EMP_ID = MEM.EMP_ID AND RPT.BRANCH_NBR = MEM.DEPT_ID AND RPT.DATA_DATE BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sb.append("  WHERE 1 = 1 ");
		if (null != inputVO.getStart()) {
			sb.append("  AND TRUNC(RPT.CREATETIME) >= TRUNC(:sDate) ");
			queryCondition.setObject("sDate", inputVO.getStart());
		}

		if (null != inputVO.getEnd()) {
			sb.append("  AND TRUNC(RPT.CREATETIME) <= TRUNC(:eDate) ");
			queryCondition.setObject("eDate", inputVO.getEnd());
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("  AND RPT.BRANCH_NBR = :branch ");
				queryCondition.setObject("branch", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && (mainFlag || uhrmFlag)) {
				sb.append("  AND RPT.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :area) ");
				queryCondition.setObject("area", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && (mainFlag || uhrmFlag)) {
				sb.append("  AND RPT.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :region) ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("  AND (");
				sb.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE RPT.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("    OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append("  )");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("  AND RPT.RM_FLAG = 'U' ");
		}

		sb.append(") ");
		sb.append(", A_TO_B AS ( ");
		sb.append("  SELECT  BASE.RM_FLAG, ");
		sb.append("          TO_CHAR(BASE.CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("          TO_CHAR(BASE.ACT_START_DATE, 'YYYY/MM/DD') AS ACT_START_DATE, ");
		sb.append("          TO_CHAR(BASE.ACT_END_DATE, 'YYYY/MM/DD') AS ACT_END_DATE, ");
		sb.append("          BASE.BRANCH_NBR, ");
		sb.append("          BASE.BRANCH_NAME, ");
		sb.append("          BASE.AO_CODE, ");
		sb.append("          BASE.OUT_CUST_ID1, ");
		sb.append("          BASE.OUT_CUST_NAME1, ");
		sb.append("          BASE.IN_CUST_ID1, ");
		sb.append("          BASE.IN_CUST_NAME1, ");
		sb.append("          BASE.SUM_TX_AMT1, ");
		sb.append("          BASE.IN_CUST_ID2, ");
		sb.append("          BASE.IN_CUST_NAME2, ");
		sb.append("          BASE.SUM_TX_AMT2, ");
		sb.append("          BASE.RESPONDING, ");
		sb.append("          BASE.NOT_RESPONDING, ");
		sb.append("          TO_CHAR(D.CREATETIME, 'YYYY/MM/DD') AS DATA_DATE, ");
		sb.append("          TO_CHAR(D.TXN_DATE, 'YYYY/MM/DD') AS TXN_DATE, ");
		sb.append("          D.EMP_ID, ");
		sb.append("          D.OUT_TYPE, ");
		sb.append("          D.OUT_CUST_NAME, ");
		sb.append("          D.OUT_CUST_ID, ");
		sb.append("          D.OUT_ACCT, ");
		sb.append("          D.IN_TYPE, ");
		sb.append("          D.IN_CUST_NAME, ");
		sb.append("          D.IN_CUST_ID, ");
		sb.append("          D.IN_ACCT, ");
		sb.append("          D.TX_AMT, ");
		sb.append("          D.JRNL_NO, ");
		sb.append("          D.NOTE_TYPE, ");
		sb.append("          D.NOTE, ");
		sb.append("          D.NOTE2, ");
		sb.append("          D.NOTE3, ");
		sb.append("          D.RECORD_SEQ, ");
		sb.append("          D.HR_ATTR, ");
		sb.append("          D.FIRSTUPDATE, ");
		sb.append("          D.MODIFIER, ");
		sb.append("          D.LASTUPDATE ");
		sb.append("  FROM BASE ");
		sb.append("  LEFT JOIN TBPMS_SELF_ACCT_FLOW_RPT_D D ON D.OUT_CUST_ID = BASE.OUT_CUST_ID1 AND D.IN_CUST_ID = BASE.IN_CUST_ID1 AND TRUNC(D.TXN_DATE) BETWEEN BASE.ACT_START_DATE AND BASE.ACT_END_DATE ");
		sb.append(") ");
		sb.append(", B_TO_C AS ( ");
		sb.append("  SELECT  BASE.RM_FLAG, ");
		sb.append("          TO_CHAR(BASE.CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("          TO_CHAR(BASE.ACT_START_DATE, 'YYYY/MM/DD') AS ACT_START_DATE, ");
		sb.append("          TO_CHAR(BASE.ACT_END_DATE, 'YYYY/MM/DD') AS ACT_END_DATE, ");
		sb.append("          BASE.BRANCH_NBR, ");
		sb.append("          BASE.BRANCH_NAME, ");
		sb.append("          BASE.AO_CODE, ");
		sb.append("          BASE.OUT_CUST_ID1, ");
		sb.append("          BASE.OUT_CUST_NAME1, ");
		sb.append("          BASE.IN_CUST_ID1, ");
		sb.append("          BASE.IN_CUST_NAME1, ");
		sb.append("          BASE.SUM_TX_AMT1, ");
		sb.append("          BASE.IN_CUST_ID2, ");
		sb.append("          BASE.IN_CUST_NAME2, ");
		sb.append("          BASE.SUM_TX_AMT2, ");
		sb.append("          BASE.RESPONDING, ");
		sb.append("          BASE.NOT_RESPONDING, ");
		sb.append("          TO_CHAR(D.DATA_DATE, 'YYYY/MM/DD') AS DATA_DATE, ");
		sb.append("          TO_CHAR(D.TXN_DATE, 'YYYY/MM/DD') AS TXN_DATE, ");
		sb.append("          D.EMP_ID, ");
		sb.append("          D.OUT_TYPE, ");
		sb.append("          D.OUT_CUST_NAME, ");
		sb.append("          D.OUT_CUST_ID, ");
		sb.append("          D.OUT_ACCT, ");
		sb.append("          D.IN_TYPE, ");
		sb.append("          D.IN_CUST_NAME, ");
		sb.append("          D.IN_CUST_ID, ");
		sb.append("          D.IN_ACCT, ");
		sb.append("          D.TX_AMT, ");
		sb.append("          D.JRNL_NO, ");
		sb.append("          D.NOTE_TYPE, ");
		sb.append("          D.NOTE, ");
		sb.append("          D.NOTE2, ");
		sb.append("          D.NOTE3, ");
		sb.append("          D.RECORD_SEQ, ");
		sb.append("          D.HR_ATTR, ");
		sb.append("          D.FIRSTUPDATE, ");
		sb.append("          D.MODIFIER, ");
		sb.append("          D.LASTUPDATE ");
		sb.append("  FROM BASE ");
		sb.append("  LEFT JOIN TBPMS_SELF_ACCT_FLOW_RPT_D D ON D.OUT_CUST_ID = BASE.IN_CUST_ID1 AND D.IN_CUST_ID = BASE.IN_CUST_ID2 AND TRUNC(D.TXN_DATE) BETWEEN BASE.ACT_START_DATE AND BASE.ACT_END_DATE ");
		sb.append(") ");
		sb.append(" ");
		sb.append("SELECT * ");
		sb.append("FROM ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM A_TO_B ");
		sb.append("  UNION ");
		sb.append("  SELECT * ");
		sb.append("  FROM B_TO_C ");
		sb.append(") ");
		sb.append("WHERE 1 = 1 ");

		sb.append("ORDER BY BRANCH_NBR, OUT_CUST_ID1, IN_CUST_ID1, IN_CUST_ID2, TXN_DATE ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		String[] csvHeader = { "私銀註記", "資料統計日期", "帳務起日", "帳務迄日", "理專歸屬行",
							   "轉出ID", "轉出姓名", "轉入ID", "轉入姓名", "累積金額",
							   "又再轉入ID", "又再轉入姓名", "累積金額",
							   "已回覆筆數", "未回覆筆數",
							   "資料日期", "交易日期", "理專員編", "轉出姓名", "轉出帳號", "轉入姓名", "轉入帳號", "交易金額",
							   "查證方式", "電訪錄音編號", "資金來源/帳戶關係", "具體原因/用途", "初判異常轉法遵部調查", "首次建立時間",
							   "最新異動人員", "最新異動日期"
							   };
		String[] csvMain   = { "RM_FLAG", "CREATETIME", "ACT_START_DATE", "ACT_END_DATE", "BRANCH_NBR",
							   "OUT_CUST_ID1", "OUT_CUST_NAME1", "IN_CUST_ID1", "IN_CUST_NAME1", "SUM_TX_AMT1",
							   "IN_CUST_ID2", "IN_CUST_NAME2", "SUM_TX_AMT2",
							   "RESPONDING", "NOT_RESPONDING",
							   "DATA_DATE", "TXN_DATE",
							   "EMP_ID", "OUT_CUST_NAME", "OUT_ACCT", "IN_CUST_NAME", "IN_ACCT", "TX_AMT",
							   "NOTE", "RECORD_SEQ", "NOTE2", "NOTE3", "HR_ATTR", "FIRSTUPDATE",
							   "MODIFIER", "LASTUPDATE"
							   };

		List listCSV = new ArrayList();
		if (isEmpty(list))
			listCSV.add(new String[] { "查無資料" });
		else {
			SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
			for (Map<String, Object> map : list) {
				String[] records = new String[csvHeader.length];

				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "FIRSTUPDATE":		// 首次建立時間
						case "LASTUPDATE":		// 最新異動日期
							records[i] = formatDate(map.get(csvMain[i]), timeSdf);
							break;
						case "BRANCH_NBR":		// 理專歸屬行
							records[i] = format("%s-%s", defaultString((String) map.get(csvMain[i])), defaultString((String) map.get("BRANCH_NAME")));
							break;
						case "OUT_CUST_ID1":	// 身分證號（高風險隱蔽資料）
						case "IN_CUST_ID1":
						case "IN_CUST_ID2":
							records[i] = getCustIdMaskForHighRisk(defaultString((String) map.get(csvMain[i])));
							break;
						case "SUM_TX_AMT1":		// 累計金額
						case "SUM_TX_AMT2":		// 累計金額
						case "TX_AMT":
							if (null != map.get(csvMain[i])) {
								records[i] = new DecimalFormat("#,##0.00").format(map.get(csvMain[i]));
							} else {
								records[i] = "0.00";
							}

							break;
						case "RESPONDING":
						case "NOT_RESPONDING":
							if (null != map.get(csvMain[i])) {
								records[i] = new DecimalFormat("#,##0").format(map.get(csvMain[i]));
							} else {
								records[i] = "0";
							}

							break;
						case "EX_FLAG":		// 初判異常轉法遵部調查 Y: 是、N: 否
							records[i] = formatFlag(defaultString((String) map.get(csvMain[i]))); // 初判異常轉法遵部調查 Y: 是、N: 否
							break;
						case "NOTE":
							String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");
							
							if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
								note = note + "：" + StringUtils.defaultString((String) map.get(csvMain[i]));
							}
							
							records[i] = (StringUtils.isNotEmpty(note) ? note : "");
							break;
						case "RECORD_SEQ":
							records[i] = checkIsNull(map, csvMain[i]) + "";
							break;
						default:
							if (null != map.get(csvMain[i])) {
								records[i] = defaultString(map.get(csvMain[i]).toString());
							} else {
								records[i] = "";
							}
							
							break;
					}
				}

				for (int index = 0; index < records.length; index++)
					records[index] = format("=\"%s\"", records[index]);

				listCSV.add(records);
			}
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);

		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		notifyClientToDownloadFile(csv.generateCSV(), format("分行人員與客戶資金往來異常報表(RM與客戶往來又轉入關聯戶)_%s.csv", dateSdf.format(new Date())));
	}

	// 格式時間
	private String formatDate(Object date, SimpleDateFormat sdf) {

		if (date != null)
			return sdf.format(date);
		else
			return "";
	}

	// Flag Y="是"，N="否"
	private String formatFlag(String flag) {

		return "Y".equals(flag) ? "是" : "N".equals(flag) ? "否" : "";
	}
	
	private String checkIsNull (Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
