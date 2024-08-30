package com.systex.jbranch.app.server.fps.cam220;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADSVO;
import com.systex.jbranch.app.server.fps.crm131.CRM131;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM998;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/05/19
 * 
 */
@Component("cam220")
@Scope("request")
public class CAM220 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM220.class);

	public void getAssBranchList(Object body, IPrimitiveMap header) throws JBranchException { 

		CAM220InputVO inputVO = (CAM220InputVO) body;
		CAM220OutputVO outputVO = new CAM220OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT LEADS.BRANCH_ID, LEADS.CUST_ID, LEADS.LEAD_TYPE, CAMP.CAMPAIGN_ID, CAMP.STEP_ID, CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, CAMP.FIRST_CHANNEL, CAMP.START_DATE, CAMP.END_DATE ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEADS ");
		sb.append("  WHERE CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID ");
		sb.append("  AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("  AND LEADS.BRANCH_ID = :branchID ");
		sb.append("  AND LEADS.AO_CODE IS NULL ");
		sb.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		sb.append("  AND LEADS.LEAD_STATUS < '03' ");
		sb.append("  AND TRUNC(LEADS.END_DATE) + 10 >= TRUNC(SYSDATE) ");
		sb.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		sb.append("  AND LEADS.LEAD_STATUS <> 'TR' ");
		sb.append(") ");

		sb.append("SELECT BRANCH_ID, BRANCH_NAME, CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, LEAD_TYPE, FIRST_CHANNEL, START_DATE, END_DATE, COUNTS ");
		sb.append("FROM ( ");
		sb.append("  SELECT BASE.BRANCH_ID, DEFN1.BRANCH_NAME, ");
		sb.append("         BASE.CAMPAIGN_ID, BASE.STEP_ID, BASE.CAMPAIGN_NAME, BASE.CAMPAIGN_DESC, BASE.LEAD_TYPE, BASE.FIRST_CHANNEL, BASE.START_DATE, BASE.END_DATE, ");
		sb.append("         (SELECT COUNT(1) ");
		sb.append("          FROM TBCAM_SFA_LEADS TEMP ");
		sb.append("          WHERE TEMP.AO_CODE IS NULL ");
		sb.append("          AND TEMP.CAMPAIGN_ID = BASE.CAMPAIGN_ID ");
		sb.append("          AND TEMP.STEP_ID = BASE.STEP_ID ");
		sb.append("          AND TEMP.BRANCH_ID = BASE.BRANCH_ID ");
		sb.append("          AND TEMP.LEAD_STATUS < '03' ");
		sb.append("          AND TRUNC(TEMP.END_DATE) + 10 >= TRUNC(SYSDATE) ");
		sb.append("          AND TEMP.LEAD_STATUS <> 'TR' ");
		sb.append("          AND TRUNC(TEMP.START_DATE) <= TRUNC(SYSDATE) ");
		sb.append("          ) AS COUNTS ");
		sb.append("  FROM BASE ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = BASE.BRANCH_ID ");
		sb.append("  WHERE TRUNC(BASE.END_DATE) + 10 >= TRUNC(SYSDATE) ");
		sb.append("  AND TRUNC(BASE.START_DATE) <= TRUNC(SYSDATE) ");
		sb.append("  GROUP BY BASE.BRANCH_ID, DEFN1.BRANCH_NAME, BASE.CAMPAIGN_ID, BASE.STEP_ID, BASE.CAMPAIGN_NAME, BASE.CAMPAIGN_DESC, BASE.LEAD_TYPE, BASE.FIRST_CHANNEL, BASE.START_DATE, BASE.END_DATE ");
		sb.append(") ");
		sb.append("WHERE COUNTS > 0 ");
		sb.append("ORDER BY END_DATE ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("branchID", inputVO.getBranchID());

		outputVO.setAssBranchList(dam.exeQuery(queryCondition));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT COUNT(1) AS UNCONTACT_CUST ");
		sb.append("FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEADS ");
		sb.append("WHERE CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID ");
		sb.append("AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("AND LEADS.BRANCH_ID = :branchID ");
		sb.append("AND LEADS.AO_CODE IS NULL ");
		sb.append("AND LEADS.LEAD_STATUS < '03'");
		sb.append("AND TRUNC(LEADS.END_DATE) + 10 >= TRUNC(SYSDATE) ");
		sb.append("AND LEADS.LEAD_STATUS <> 'TR' ");
		sb.append("AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("branchID", inputVO.getBranchID());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		outputVO.setLeadsCounts((BigDecimal) list.get(0).get("UNCONTACT_CUST"));

		this.sendRtnObject(outputVO);
	}

	public void getAssBranchListByCust(Object body, IPrimitiveMap header) throws JBranchException {

		CAM220InputVO inputVO = (CAM220InputVO) body;
		CAM220OutputVO outputVO = new CAM220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer withSB = new StringBuffer();
		withSB.append("WITH BASE AS ( ");
		withSB.append("  SELECT CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, CAMP.FIRST_CHANNEL, CAMP.LEAD_TYPE, LEADS.SFA_LEAD_ID, LEADS.AO_CODE, CUST.AO_CODE AS CUST_AO_CODE, CUST.EMP_NAME AS CUST_AO_NAME, LEADS.CUST_ID AS L_CUST_ID, CUST.CUST_NAME, CUST.CUST_ID, LEADS.BRANCH_ID, LEADS.CAMPAIGN_ID, LEADS.STEP_ID, LEADS.START_DATE, LEADS.END_DATE, CUST.VIP_DEGREE, CUST.CON_DEGREE, CUST.AUM_AMT  ");
		withSB.append("  FROM TBCAM_SFA_LEADS LEADS ");
		withSB.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		withSB.append("  LEFT JOIN (SELECT MAST.CUST_ID, MAST.CUST_NAME, MAST.AO_CODE, MAST.VIP_DEGREE, MAST.CON_DEGREE, MAST.AUM_AMT, INFO.EMP_NAME FROM TBCRM_CUST_MAST MAST LEFT JOIN VWORG_AO_INFO INFO ON INFO.AO_CODE = MAST.AO_CODE) CUST ON LEADS.CUST_ID = CUST.CUST_ID ");
		withSB.append("  WHERE LEADS.AO_CODE IS NULL ");
		withSB.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		//withSB.append("  AND TRUNC(SYSDATE) <= (SELECT PABTH_UTIL.FC_getBusiDay(LEADS.END_DATE, 'TWD', 3) AS BUSI_DAY FROM DUAL) ");
		withSB.append("  AND TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE) + 10 ");
		withSB.append("  AND LEADS.BRANCH_ID = :branchID ");

		// 20190830 add by ocean : 0006667 刪除名單後,主管帶分派名單沒有移除(祐傑)
		withSB.append("  AND CAMP.REMOVE_FLAG = 'N' ");

		withSB.append("  AND NOT EXISTS (SELECT 1 ");
		withSB.append("                  FROM TBCRM_CUST_MAST UC, VWORG_EMP_UHRM_INFO UI ");
		withSB.append("                  WHERE UC.AO_CODE = UI.UHRM_CODE ");
		withSB.append("                  AND LEADS.CUST_ID = UC.CUST_ID) ");

		if (StringUtils.isNotBlank(inputVO.getAssCampaignName())) {
			withSB.append("  AND CAMP.CAMPAIGN_NAME LIKE :campName ");
			queryCondition.setObject("campName", "%" + inputVO.getAssCampaignName() + "%");
		}
		//2017/10/17
		if (StringUtils.isNotBlank(inputVO.getHaveAo_code())) {
			if ("Y".equals(inputVO.getHaveAo_code()))
				withSB.append("AND CUST.AO_CODE is null ");
			else
				withSB.append("AND CUST.AO_CODE is not null ");
		}

		withSB.append(") ");
		withSB.append(", INFO AS ( ");
		withSB.append("  SELECT EMP_ID, EMP_NAME, BRANCH_NBR, AO_CODE, ROLE_NAME ");
		withSB.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		withSB.append("  UNION ");
		withSB.append("  SELECT EMP_ID, EMP_NAME, BRANCH_NBR, AO_CODE, ROLE_NAME ");
		withSB.append("  FROM VWORG_EMP_PLURALISM_INFO ");
		withSB.append(") ");

		StringBuffer sb = new StringBuffer();
		sb.append(withSB.toString());
		sb.append("SELECT BASE.LEAD_TYPE, BASE.SFA_LEAD_ID, BASE.CUST_NAME, BASE.CUST_AO_CODE, BASE.CUST_AO_NAME, ");
		sb.append("       CASE WHEN BASE.CUST_ID IS NULL THEN BASE.L_CUST_ID ELSE BASE.CUST_ID END AS CUST_ID, BASE.BRANCH_ID, DEFN1.BRANCH_NAME, ");
		sb.append("       BASE.CAMPAIGN_NAME, BASE.CAMPAIGN_DESC, BASE.FIRST_CHANNEL, BASE.END_DATE, ");
		sb.append("       (SELECT AO_TEMP.AO_CODE || '-' || INFO.EMP_NAME ");
		sb.append("        FROM (SELECT CUST_ID, AO_CODE, END_DATE FROM TBCAM_SFA_LEADS LEADS_TEMP WHERE LEADS_TEMP.CUST_ID = BASE.CUST_ID AND AO_CODE IS NOT NULL ORDER BY END_DATE DESC) AO_TEMP ");
		sb.append("        LEFT JOIN INFO ON AO_TEMP.AO_CODE = INFO.AO_CODE ");
		sb.append("        WHERE INFO.EMP_NAME IS NOT NULL AND ROWNUM = 1) AS HIS_AO_CODE, ");
		sb.append("       (SELECT INFO.EMP_ID || '-' || INFO.EMP_NAME ");
		sb.append("        FROM (SELECT CUST_ID, AO_CODE, END_DATE, LEADS_TEMP.EMP_ID FROM TBCAM_SFA_LEADS LEADS_TEMP WHERE LEADS_TEMP.CUST_ID = BASE.CUST_ID AND AO_CODE IS NOT NULL ORDER BY END_DATE DESC) AO_TEMP ");
		sb.append("        LEFT JOIN INFO ON AO_TEMP.EMP_ID = INFO.EMP_ID ");
		sb.append("        WHERE INFO.ROLE_NAME = 'PS' AND ROWNUM = 1) AS HIS_PS, ");
		sb.append("       BASE.VIP_DEGREE, BASE.CON_DEGREE, BASE.AUM_AMT ");
		sb.append("FROM BASE ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = BASE.BRANCH_ID ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND TRUNC(BASE.START_DATE) <= TRUNC(SYSDATE) ");
		sb.append("AND BASE.L_CUST_ID IN (SELECT DISTINCT LEADS.L_CUST_ID AS CUST_ID ");
		sb.append("                       FROM BASE LEADS ");
		sb.append("                       LEFT JOIN TBCRM_CUST_MAST CM ON LEADS.CUST_ID = CM.CUST_ID ");
		sb.append("                       WHERE LEADS.BRANCH_ID = :branchID ");
		sb.append("                       AND LEADS.AO_CODE IS NULL ");

		if (StringUtils.isNotBlank(inputVO.getAssCustID())) {
			sb.append("AND LEADS.CUST_ID LIKE :custID ");
			queryCondition.setObject("custID", inputVO.getAssCustID() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getAssCustName())) {
			sb.append("AND CM.CUST_NAME LIKE :custName ");
			queryCondition.setObject("custName", inputVO.getAssCustName() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getVipDegree())) {
			sb.append("AND CM.VIP_DEGREE = :vipDegree ");
			queryCondition.setObject("vipDegree", inputVO.getVipDegree());
		}

		if (StringUtils.isNotBlank(inputVO.getConDegree())) {
			sb.append("AND CM.CON_DEGREE = :conDegree ");
			queryCondition.setObject("conDegree", inputVO.getConDegree());
		}

		sb.append("                       ORDER BY LEADS.L_CUST_ID  ");
		sb.append("                       FETCH FIRST 3000 ROWS ONLY) ");

		queryCondition.setObject("branchID", inputVO.getBranchID());

		if (StringUtils.isNotBlank(inputVO.getAssCustID())) {
			sb.append("AND BASE.CUST_ID LIKE :custID ");
			queryCondition.setObject("custID", inputVO.getAssCustID() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getAssCustName())) {
			sb.append("AND BASE.CUST_NAME LIKE :custName ");
			queryCondition.setObject("custName", inputVO.getAssCustName() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getVipDegree())) {
			sb.append("AND BASE.VIP_DEGREE = :vipDegree ");
			queryCondition.setObject("vipDegree", inputVO.getVipDegree());
		}

		if (StringUtils.isNotBlank(inputVO.getConDegree())) {
			sb.append("AND BASE.CON_DEGREE = :conDegree ");
			queryCondition.setObject("conDegree", inputVO.getConDegree());
		}
		sb.append("ORDER BY BASE.CUST_ID ");

		queryCondition.setQueryString(sb.toString());
		outputVO.setAssBranchListByCust(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	// === 輪流、指定 共用
	public void getEmpList(Object body, IPrimitiveMap header) throws JBranchException {

		CAM220InputVO inputVO = (CAM220InputVO) body;
		CAM220OutputVO outputVO = new CAM220OutputVO();
		dam = this.getDataAccessManager();

		// 2017/8/3 russle want less sql
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT EMP_ID, ROLE_ID, ROLE_NAME, EMP_NAME, AO_CODE, EMP_NAME AS LABEL, EMP_ID AS DATA ");
		sb.append("FROM ( ");

		if (StringUtils.equals(inputVO.getChannel(), "004AO")) {
			sb.append("  SELECT PAO.EMP_ID, MR.ROLE_ID, R.ROLE_NAME, MEM.EMP_NAME, NULL AS AO_CODE ");
			sb.append("  FROM TBORG_PAO PAO ");
			sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON PAO.BRANCH_NBR = DEFN.BRANCH_NBR ");
			sb.append("  LEFT JOIN TBORG_MEMBER MEM ON PAO.EMP_ID = MEM.EMP_ID ");
			sb.append("  LEFT JOIN TBORG_MEMBER_ROLE MR ON PAO.EMP_ID = MR.EMP_ID ");
			sb.append("  INNER JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID AND R.JOB_TITLE_NAME IS NOT NULL ");
			sb.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON PRI.ROLEID = MR.ROLE_ID ");
			sb.append("  WHERE 1 = 1 ");

			if (StringUtils.isNotBlank(inputVO.getChannel())) {
				sb.append("  AND PRI.PRIVILEGEID = :channel ");
				queryCondition.setObject("channel", inputVO.getChannel());
			}

			sb.append("  AND PAO.BRANCH_NBR = :branchID ");
		} else {
			sb.append("  SELECT INFO.EMP_ID, INFO.ROLE_ID, INFO.ROLE_NAME, INFO.EMP_NAME, INFO.AO_CODE ");
			sb.append("  FROM VWORG_EMP_INFO INFO ");
			sb.append("  WHERE INFO.BRANCH_NBR = :branchID ");
			
			if (StringUtils.isNotBlank(inputVO.getChannel())) {
				if (StringUtils.equals(inputVO.getChannel(), "002") || StringUtils.equals(inputVO.getChannel(), "003")) {
					sb.append("  AND CODE_TYPE = '1' ");
				}

				sb.append("  AND INFO.PRIVILEGEID = :channel ");

				queryCondition.setObject("channel", inputVO.getChannel());
			}
			sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE INFO.EMP_ID = UHRM.EMP_ID) ");
		}

		sb.append(") ");
		sb.append("ORDER BY ROLE_NAME, EMP_ID ");

		queryCondition.setObject("branchID", inputVO.getBranchID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQueryWithoutSort(queryCondition);
		if (list.size() > 0) {
			List<String> empList = new ArrayList<String>();
			for (Map<String, Object> map : list) {
				empList.add(ObjectUtils.toString(map.get("EMP_ID")));
			}
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();

			CRM131 crm131 = (CRM131) PlatformContext.getBean("crm131");
			sb.append(crm131.VWCAM_SFA_LEADS_CURRENT(false, false, false, (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)));
			sb.append("SELECT INFO.EMP_ID, ");
			sb.append("       SUM(NVL(CURR.TOTAL_CONTACT_CUST, '0')) AS TOTAL_CONTACT_CUST ");
			sb.append("FROM VWORG_EMP_INFO INFO ");
			sb.append("LEFT JOIN LEADS_CURRENT CURR ON INFO.EMP_ID = CURR.EMP_ID "); // AND INFO.AO_CODE = CURR.AO_CODE
			sb.append("WHERE CURR.BRANCH_NBR = :branchID ");
			sb.append("AND INFO.EMP_ID IN (:emp_list) ");
			sb.append("GROUP BY INFO.EMP_ID ");

			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("branchID", inputVO.getBranchID());
			queryCondition.setObject("emp_list", empList);
			List<Map<String, Object>> list2 = dam.exeQueryWithoutSort(queryCondition);
			for (Map<String, Object> map : list) {
				boolean flag = false;
				for (Map<String, Object> map2 : list2) {
					if (StringUtils.equals(ObjectUtils.toString(map.get("EMP_ID")), ObjectUtils.toString(map2.get("EMP_ID")))) {
						flag = true;
						map.put("TOTAL_CONTACT_CUST", map2.get("TOTAL_CONTACT_CUST"));
					} 
				}
				
				if (!flag) {
					map.put("TOTAL_CONTACT_CUST", 0);
				}
			}
		}
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}

	public void getLogList(Object body, IPrimitiveMap header) throws JBranchException {

		CAM220InputVO inputVO = (CAM220InputVO) body;
		CAM220OutputVO outputVO = new CAM220OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT MBR.EMP_ID, MBR.CUST_ID, AO.AO_CODE, MBR.EMP_NAME, MBR.JOB_TITLE_NAME, AOR.ROLE_NAME AS AO_JOB_RANK, ROLE.ROLE_ID AS ROLE_ID, ROLE.ROLE_NAME AS ROLE_NAME ");
		sb.append("  FROM TBORG_MEMBER MBR ");
		sb.append("  LEFT JOIN (SELECT ROLE_ID, EMP_ID FROM TBORG_MEMBER_ROLE WHERE IS_PRIMARY_ROLE = 'Y') MROLE ON MROLE.EMP_ID = MBR.EMP_ID ");
		sb.append("  LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y' AND IS_AO = 'Y') AOR ON AOR.ROLE_ID = MROLE.ROLE_ID ");
		sb.append("  LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y') ROLE ON ROLE.ROLE_ID = MROLE.ROLE_ID ");
		sb.append("  LEFT JOIN (SELECT EMP_ID, AO_CODE FROM TBORG_SALES_AOCODE WHERE TYPE = '1') AO ON MBR.EMP_ID = AO.EMP_ID ");
		sb.append("  WHERE SERVICE_FLAG = 'A' AND CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append(") ");
		sb.append("SELECT RLOG.LE_REA_DTTM, CUST.CUST_NAME, LEAD.CUST_ID, ");
		sb.append("       CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, LEAD.END_DATE, ");
		sb.append("       CUST.VIP_DEGREE, CUST.CON_DEGREE, CUST.AUM_AMT, ");
		sb.append("       LEAD.LEAD_STATUS, CASE WHEN LEAD.LEAD_STATUS = 'TR' THEN '作廢' ELSE RES.RESPONSE_NAME END AS LEAD_STATUS_NAME, EMP_LOG.EMP_ID, "); // LEAD.EMP_ID -> EMP_LOG.EMP_ID
		sb.append("       EMP_LOG.ROLE_NAME AS LOG_JOB_TYPE, EMP_LOG.EMP_NAME AS LOG_EMP_NAME, EMP_LOG.AO_CODE AS LOG_AO_CODE, ");
		sb.append("       EMP_BOSS.EMP_NAME AS BOSS, LEAD.LASTUPDATE, ");
		sb.append("       CASE WHEN RLOG.LE_REA_TYPE = '20' THEN 'Y' ELSE 'N' END AS LE_REA_STATUS, ");
		sb.append("       EMP_NOW.EMP_NAME AS NOW_EMP_NAME, EMP_NOW.AO_CODE AS NOW_AO_CODE ");
		sb.append("FROM TBCAM_SFA_LE_REA_LOG RLOG ");
		sb.append("LEFT JOIN TBCAM_SFA_LEADS LEAD ON RLOG.SFA_LEAD_ID = LEAD.SFA_LEAD_ID ");
		sb.append("INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON CAMP.CAMPAIGN_ID = LEAD.CAMPAIGN_ID AND CAMP.STEP_ID = LEAD.STEP_ID ");
		sb.append("LEFT JOIN TBCAM_SFA_CAMP_RESPONSE RES ON RES.CAMPAIGN_ID IN (CAMP.LEAD_RESPONSE_CODE, CAMP.CAMPAIGN_ID) AND LEAD.LEAD_STATUS = RES.LEAD_STATUS ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = LEAD.CUST_ID ");
		sb.append("LEFT JOIN BASE EMP_BOSS ON RLOG.REV_LOGIN_ID = EMP_BOSS.EMP_ID ");
		sb.append("LEFT JOIN BASE EMP_LOG ON RLOG.EMP_ID = EMP_LOG.EMP_ID ");
		sb.append("LEFT JOIN BASE EMP_NOW ON LEAD.EMP_ID = EMP_NOW.EMP_ID ");
		sb.append("WHERE 1 = 1 ");

		sb.append("AND NOT EXISTS (SELECT 1 ");
		sb.append("                FROM TBCRM_CUST_MAST UC, VWORG_EMP_UHRM_INFO UI ");
		sb.append("                WHERE UC.AO_CODE = UI.UHRM_CODE ");
		sb.append("                AND LEAD.CUST_ID = UC.CUST_ID) ");

		if (StringUtils.isNotBlank(inputVO.getBranchID())) {
			sb.append("AND LEAD.BRANCH_ID = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranchID());
		}

		if (StringUtils.isNotBlank(inputVO.getEmpID())) {
			sb.append("AND EMP_LOG.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpID());
		}

		if (StringUtils.isNotBlank(inputVO.getCustID())) {
			sb.append("AND CUST.CUST_ID LIKE :custID ");
			queryCondition.setObject("custID", "%" + inputVO.getCustID() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getCustName())) {
			sb.append("AND CUST.CUST_NAME LIKE :custName ");
			queryCondition.setObject("custName", "%" + inputVO.getCustName() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getCampName())) {
			sb.append("AND CAMP.CAMPAIGN_NAME LIKE :campName ");
			queryCondition.setObject("campName", "%" + inputVO.getCampName() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getLeadStatus())) {
			sb.append("AND LEAD.LEAD_STATUS = :leadStatus ");
			queryCondition.setObject("leadStatus", inputVO.getLeadStatus());
		}

		if (null != inputVO.getsDate()) {
			sb.append("AND TO_CHAR(RLOG.LE_REA_DTTM, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}

		if (null != inputVO.geteDate()) {
			sb.append("AND TO_CHAR(RLOG.LE_REA_DTTM, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}

		if (StringUtils.isNotBlank(inputVO.getYesORno())) {
			sb.append("AND RLOG.LE_REA_TYPE = :LeReaType ");
		}
		sb.append("ORDER BY RLOG.LE_REA_DTTM DESC");

		queryCondition.setQueryString(sb.toString());

		if (StringUtils.isNotBlank(inputVO.getYesORno()) && "Y".equals(inputVO.getYesORno())) {
			queryCondition.setObject("LeReaType", "20");
		} else if (StringUtils.isNotBlank(inputVO.getYesORno()) && "N".equals(inputVO.getYesORno())) {
			queryCondition.setObject("LeReaType", "30");
		}

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setLogList(list);
		this.sendRtnObject(outputVO);
	}

	//2017/10/17
	public void exportHistory(Object body, IPrimitiveMap header) throws Exception {

		CAM220InputVO inputVO = (CAM220InputVO) body;
		dam = this.getDataAccessManager();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fileName = "已分派歷史紀錄_" + sdf.format(new Date());

		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(fileName);
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		// 表頭 CELL型式
		XSSFCellStyle headingStyle = workbook.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		String[] headerLine1 = { "客戶姓名", "身分證字號", "活動名稱", "簡要說明", "名單到期日", "理財會員等級", "貢獻度等級", "資產總餘額", "處理狀態", "分派人員員編", "分派人員類別", "分派人員姓名", "分派人員AO CODE", "分派主管姓名", "分派日期", "改派名單", "目前理專姓名", "目前理專AO OCDE" };
		String[] mainLine = { "CUST_NAME", "CUST_ID", "CAMPAIGN_NAME", "CAMPAIGN_DESC", "END_DATE", "VIP_DEGREE", "CON_DEGREE", "AUM_AMT", "LEAD_STATUS_NAME", "EMP_ID", "LOG_JOB_TYPE", "LOG_EMP_NAME", "LOG_AO_CODE", "BOSS", "LE_REA_DTTM", "LE_REA_STATUS", "NOW_EMP_NAME", "NOW_AO_CODE" };

		Integer index = 0; // first row
		Integer startFlag = 0;
		Integer endFlag = 0;
		ArrayList<String> tempList = new ArrayList<String>(); //比對用

		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine1.length; i++) {
			String headerLine = headerLine1[i];
			if (tempList.indexOf(headerLine) < 0) {
				tempList.add(headerLine);
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLine1[i]);

				if (endFlag != 0) {
					sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
				}
				startFlag = i;
				endFlag = 0;
			} else {
				endFlag = i;
			}
		}
		if (endFlag != 0) { //最後的CELL若需要合併儲存格，則在這裡做
			sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
		}
		index++;

		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);

		Map<String, String> vipMap = new XmlInfo().doGetVariable("CRM.VIP_DEGREE", FormatHelper.FORMAT_3);
		Map<String, String> conMap = new XmlInfo().doGetVariable("CRM.CON_DEGREE", FormatHelper.FORMAT_3);

		for (Map<String, Object> map : inputVO.getHistory_list()) {
			row = sheet.createRow(index);

			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);

					if (StringUtils.equals("VIP_DEGREE", mainLine[j])) {
						cell.setCellValue(vipMap.get(checkIsNull(map, mainLine[j])));
					}
					if (StringUtils.equals("CON_DEGREE", mainLine[j])) {
						cell.setCellValue(conMap.get(checkIsNull(map, mainLine[j])));
					} else if (StringUtils.equals("END_DATE", mainLine[j])) {
						cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, mainLine[j])) ? sdf2.format(sdf2.parse(checkIsNull(map, mainLine[j]))) : null);
					} else if (StringUtils.equals("LE_REA_DTTM", mainLine[j])) {
						cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, mainLine[j])) ? sdf3.format(sdf3.parse(checkIsNull(map, mainLine[j]))) : null);
					} else {
						cell.setCellValue(checkIsNull(map, mainLine[j]));
					}
				}
				index++;
			}
		}

		workbook.write(new FileOutputStream(filePath));
		this.notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName + ".xlsx");
	}

	private String checkIsNull(Map map, String key) {

		if (null != map && null != map.get(key))
			return String.valueOf(map.get(key));
		else
			return "";
	}

	// 輪流分派
	public void turnDispatchLead(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM220InputVO inputVO = (CAM220InputVO) body;
		CAM220OutputVO outputVO = new CAM220OutputVO();
		dam = this.getDataAccessManager();

		Integer flag = 0;
		// 2017/8/3 russle want less sql
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		if (StringUtils.isNotBlank(inputVO.getChannel()) && StringUtils.equals("004AO", inputVO.getChannel())) {
			sql.append("SELECT EMP.EMP_ID, EMP.ROLE_ID, EMP.ROLE_NAME, EMP.EMP_NAME, EMP.AO_CODE ");
			sql.append("FROM VWORG_EMP_INFO EMP ");
			sql.append("INNER JOIN TBORG_PAO PAO ON EMP.EMP_ID = PAO.EMP_ID ");
			sql.append("WHERE EMP.PRIVILEGEID = :channel ");
			sql.append("AND PAO.BRANCH_NBR = :branchID ");

			queryCondition.setObject("channel", inputVO.getChannel());
		} else {
			sql.append("SELECT INFO.EMP_ID, INFO.ROLE_ID, INFO.ROLE_NAME, INFO.EMP_NAME, INFO.AO_CODE ");
			sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
			sql.append("WHERE INFO.BRANCH_NBR = :branchID ");

			if (StringUtils.isNotBlank(inputVO.getChannel()))
				sql.append("AND INFO.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = :channel) ");

			sql.append("UNION ALL ");
			sql.append("SELECT PT_INFO.EMP_ID, PT_INFO.ROLE_ID, PT_INFO.ROLE_NAME, PT_INFO.EMP_NAME, PT_INFO.AO_CODE ");
			sql.append("FROM VWORG_EMP_PLURALISM_INFO PT_INFO ");
			sql.append("WHERE PT_INFO.BRANCH_NBR = :branchID ");
			if (StringUtils.isNotBlank(inputVO.getChannel())) {
				sql.append("AND PT_INFO.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = :channel) ");
				queryCondition.setObject("channel", inputVO.getChannel());
			}
		}

		queryCondition.setObject("branchID", inputVO.getBranchID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> channelEmpList = dam.exeQuery(queryCondition);
		// === END

		// === 取得需分派的SFA_LEAD_ID(需排序)
		StringBuffer campIDTempSQL = new StringBuffer();
		campIDTempSQL.append("SELECT DISTINCT SFA_LEAD_ID ");
		campIDTempSQL.append("FROM TBCAM_SFA_LEADS ");
		campIDTempSQL.append("WHERE 1=1 ");
		campIDTempSQL.append("AND CAMPAIGN_ID = :campID ");
		campIDTempSQL.append("AND STEP_ID = :stepID ");
		campIDTempSQL.append("AND BRANCH_ID = :branchID ");
		campIDTempSQL.append("AND AO_CODE IS NULL ");
		campIDTempSQL.append("ORDER BY SFA_LEAD_ID ");

		List<String> finalList = new ArrayList<String>();

		for (int i = 0; i < inputVO.getCampIDList().length; i++) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString(campIDTempSQL.toString());
			queryCondition.setObject("campID", inputVO.getCampIDList()[i]);
			queryCondition.setObject("stepID", inputVO.getStepIDList()[i]);
			queryCondition.setObject("branchID", inputVO.getBranchID());

			List<Map<String, Object>> sfaLeadIDList = dam.exeQuery(queryCondition);
			for (Map<String, Object> temp : sfaLeadIDList) {
				if (!StringUtils.isBlank((String) temp.get("SFA_LEAD_ID")))
					finalList.add((String) temp.get("SFA_LEAD_ID"));
			}
		}
		Collections.sort(finalList);
		// === END

		for (int i = 0; i < finalList.size(); i++) {
			if (flag >= channelEmpList.size()) {
				flag = 0;
			}

			if (channelEmpList.size() > 0) {
				TBCAM_SFA_LEADSVO leadVO = new TBCAM_SFA_LEADSVO();
				leadVO = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, (String) finalList.get(i));

				// 活動
				TBCAM_SFA_CAMPAIGNVO campVO = new TBCAM_SFA_CAMPAIGNVO();
				TBCAM_SFA_CAMPAIGNPK campPK = new TBCAM_SFA_CAMPAIGNPK();
				campPK.setCAMPAIGN_ID(leadVO.getCAMPAIGN_ID());
				campPK.setSTEP_ID(leadVO.getSTEP_ID());
				campVO.setcomp_id(campPK);
				campVO = (TBCAM_SFA_CAMPAIGNVO) dam.findByPKey(TBCAM_SFA_CAMPAIGNVO.TABLE_UID, campVO.getcomp_id());
				Map<String, Object> campMap = new HashMap<String, Object>();
				campMap.put("CAMPAIGN_ID", leadVO.getCAMPAIGN_ID());
				campMap.put("STEP_ID", leadVO.getSTEP_ID());
				campMap.put("LEAD_TYPE", campVO.getLEAD_TYPE());
				campMap.put("START_DATE", leadVO.getSTART_DATE());
				campMap.put("END_DATE", leadVO.getEND_DATE());

				String campID = leadVO.getCAMPAIGN_ID();
				String custID = leadVO.getCUST_ID();
				leadVO = null;

				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.CHANNEL_MAPPING' AND PARAM_CODE = :paramCode ");
				queryCondition.setQueryString(sb.toString());
				queryCondition.setObject("paramCode", inputVO.getChannel());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				CAM998 cam998 = (CAM998) PlatformContext.getBean("cam998");

				Map<String, Object> returnVO = new HashMap<String, Object>();
				do {
					logger.info("===============執行輪流分派初始LOG(START)===============");
					logger.info("do while Condition(into): flag→ " + flag + " / channelEmpList.size()→ " + channelEmpList.size() + " / returnVO.get(\"EMP_ID\")→ " + returnVO.get("EMP_ID"));
					logger.info("do while Condition(into): Condition1→ " + (flag < channelEmpList.size()) + " / Condition2→ " + (null == returnVO.get("EMP_ID")));
					logger.info("returnVO:" + returnVO);

					if (null != returnVO.get("DISPATCH") && null == returnVO.get("EMP_ID")) { //該理專無法分派，應分派給下一位
						returnVO.put("CONTINUANCT", "Y");
						logger.info("名單:" + finalList.get(i) + "→ 該理專(" + channelEmpList.get(flag - 1).get("EMP_ID") + ")無法分派，應分派給下一位");
					} else if (null != returnVO.get("DISPATCH") && null != returnVO.get("EMP_ID")) {
						returnVO.put("CONTINUANCT", "N");
						logger.info("名單:" + finalList.get(i) + "→ 分派成功");
					} else {
						returnVO.put("CONTINUANCT", "Y");
						logger.info("名單:" + finalList.get(i) + "→ 初始進入");
					}

					logger.info("returnVO-CONTINUANCT:" + returnVO);
					logger.info("===============執行輪流分派初始LOG(END)===============");

					if (StringUtils.equals("Y", (String) returnVO.get("CONTINUANCT"))) {
						//名單
						Map<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("CUST_ID", custID);
						tempMap.put("AO_CODE", channelEmpList.get(flag).get("AO_CODE"));
						tempMap.put("BRANCH_ID", inputVO.getBranchID());
						tempMap.put("EMP_ID", channelEmpList.get(flag).get("EMP_ID"));
						tempMap.put("SFA_LEAD_ID", (String) finalList.get(i));
						tempMap.put("LEAD_ID", (String) finalList.get(i));
						tempMap.put("LEAD_TYPE", (String) campMap.get("LEAD_TYPE"));

						returnVO = cam998.updLead(dam, ws.getUser().getUserID(), "30", campID, (String) list.get(0).get("PARAM_NAME"), campMap, tempMap);

						flag++;
					}

					logger.info("===============執行輪流分派結束LOG(START)===============");
					logger.info("do while Condition(final): flag→ " + flag + " / channelEmpList.size()→ " + channelEmpList.size() + " / returnVO.get(\"EMP_ID\")→ " + returnVO.get("EMP_ID"));
					logger.info("do while Condition(final): Condition1→ " + (flag < channelEmpList.size()) + " / Condition2→ " + (null == returnVO.get("EMP_ID")));
					logger.info("returnVO:" + returnVO);
					logger.info("===============執行輪流分派結束LOG(END)===============");
				} while (flag < channelEmpList.size() && null == returnVO.get("EMP_ID"));
			} else {
				throw new APException("分派失敗");
			}
		}

		this.sendRtnObject(null);
	}

	// 指定分派
	public void designDispatchLead(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM220InputVO inputVO = (CAM220InputVO) body;
		CAM220OutputVO outputVO = new CAM220OutputVO();
		dam = this.getDataAccessManager();

		Integer flag = 0;
		inputVO.setAction("DESIGN");
		// 2017/8/3 russle want less sql
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		if (StringUtils.equals("004AO", inputVO.getChannel())) {
			sql.append("  SELECT PAO.EMP_ID, MR.ROLE_ID, R.ROLE_NAME, MEM.EMP_NAME, NULL AS AO_CODE ");
			sql.append("  FROM TBORG_PAO PAO ");
			sql.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON PAO.BRANCH_NBR = DEFN.BRANCH_NBR ");
			sql.append("  LEFT JOIN TBORG_MEMBER MEM ON PAO.EMP_ID = MEM.EMP_ID ");
			sql.append("  LEFT JOIN TBORG_MEMBER_ROLE MR ON PAO.EMP_ID = MR.EMP_ID ");
			sql.append("  INNER JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID AND R.JOB_TITLE_NAME IS NOT NULL ");
			sql.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON PRI.ROLEID = MR.ROLE_ID ");
			sql.append("  WHERE 1 = 1 ");

			if (StringUtils.isNotBlank(inputVO.getChannel())) {
				sql.append("  AND PRI.PRIVILEGEID = :channel ");
				queryCondition.setObject("channel", inputVO.getChannel());
			}

			sql.append("  AND PAO.BRANCH_NBR = :branchID ");
			sql.append("  AND PAO.EMP_ID = :empID ");
		} else {
			sql.append("SELECT INFO.EMP_ID, INFO.ROLE_ID, INFO.ROLE_NAME, INFO.EMP_NAME, INFO.AO_CODE ");
			sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
			sql.append("WHERE INFO.BRANCH_NBR = :branchID ");
			
			if (StringUtils.isNotBlank(inputVO.getChannel())) {
				sql.append("AND INFO.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = :channel) ");
				queryCondition.setObject("channel", inputVO.getChannel());
			}
			
			sql.append("AND INFO.EMP_ID = :empID ");
			sql.append("UNION ALL ");
			sql.append("SELECT PT_INFO.EMP_ID, PT_INFO.ROLE_ID, PT_INFO.ROLE_NAME, PT_INFO.EMP_NAME, PT_INFO.AO_CODE ");
			sql.append("FROM VWORG_EMP_PLURALISM_INFO PT_INFO ");
			sql.append("WHERE PT_INFO.BRANCH_NBR = :branchID ");
			
			if (StringUtils.isNotBlank(inputVO.getChannel())) {
				sql.append("AND PT_INFO.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = :channel) ");
				queryCondition.setObject("channel", inputVO.getChannel());
			}
			
			sql.append("AND PT_INFO.EMP_ID = :empID ");
		}
		
		queryCondition.setObject("branchID", inputVO.getBranchID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, String>> finalEmpList = new ArrayList<Map<String, String>>();
		
		for (int i = 0; i < inputVO.getEmpIDList().length; i++) {
			queryCondition.setObject("empID", inputVO.getEmpIDList()[i]);
			List<Map<String, Object>> empDtl = dam.exeQueryWithoutSort(queryCondition);
			if (empDtl.size() > 0) {
				Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put("EMP_ID", (String) empDtl.get(0).get("EMP_ID"));
				if (StringUtils.isBlank(ObjectUtils.toString(empDtl.get(0).get("AO_CODE"))))
					tempMap.put("AO_CODE", (String) empDtl.get(0).get("EMP_ID"));
				else
					tempMap.put("AO_CODE", (String) empDtl.get(0).get("AO_CODE"));
				finalEmpList.add(tempMap);
			}
		}
		//=== END

		// === 取得需分派的SFA_LEAD_ID(需排序)
		StringBuffer campIDTempSQL = new StringBuffer();
		campIDTempSQL.append("SELECT SFA_LEAD_ID ");
		campIDTempSQL.append("FROM TBCAM_SFA_LEADS ");
		campIDTempSQL.append("WHERE BRANCH_ID = :brancdID ");
		campIDTempSQL.append("AND CUST_ID = :custID ");
		campIDTempSQL.append("AND AO_CODE IS NULL ");
		campIDTempSQL.append("AND TRUNC(SYSDATE) >= TRUNC(START_DATE) AND TRUNC(SYSDATE) <= TRUNC(END_DATE + 10) ");
		campIDTempSQL.append("ORDER BY SFA_LEAD_ID ");
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(campIDTempSQL.toString());
		List<String> finalList = new ArrayList<String>();
		for (int i = 0; i < inputVO.getCustIDList().length; i++) {
			queryCondition.setObject("brancdID", inputVO.getBranchID());
			queryCondition.setObject("custID", inputVO.getCustIDList()[i]);

			List<Map<String, Object>> sfaLeadIDList = dam.exeQueryWithoutSort(queryCondition);
			for (Map<String, Object> temp : sfaLeadIDList) {
				if (!StringUtils.isBlank((String) temp.get("SFA_LEAD_ID")))
					finalList.add((String) temp.get("SFA_LEAD_ID"));
			}
		}
		Collections.sort(finalList);
		// === END

		for (int i = 0; i < finalList.size(); i++) {
			if (flag >= finalEmpList.size()) {
				flag = 0;
			}

			if (finalEmpList.size() > 0) {
				TBCAM_SFA_LEADSVO leadVO = new TBCAM_SFA_LEADSVO();
				leadVO = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, (String) finalList.get(i));
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("CUST_ID", leadVO.getCUST_ID());
				tempMap.put("AO_CODE", finalEmpList.get(flag).get("AO_CODE"));
				tempMap.put("BRANCH_ID", inputVO.getBranchID());
				tempMap.put("EMP_ID", finalEmpList.get(flag).get("EMP_ID"));
				tempMap.put("SFA_LEAD_ID", (String) finalList.get(i));

				TBCAM_SFA_CAMPAIGNVO campVO = new TBCAM_SFA_CAMPAIGNVO();
				TBCAM_SFA_CAMPAIGNPK campPK = new TBCAM_SFA_CAMPAIGNPK();
				campPK.setCAMPAIGN_ID(leadVO.getCAMPAIGN_ID());
				campPK.setSTEP_ID(leadVO.getSTEP_ID());
				campVO.setcomp_id(campPK);
				campVO = (TBCAM_SFA_CAMPAIGNVO) dam.findByPKey(TBCAM_SFA_CAMPAIGNVO.TABLE_UID, campVO.getcomp_id());
				Map<String, Object> campMap = new HashMap<String, Object>();
				campMap.put("CAMPAIGN_ID", leadVO.getCAMPAIGN_ID());
				campMap.put("STEP_ID", leadVO.getSTEP_ID());
				campMap.put("LEAD_TYPE", campVO.getLEAD_TYPE());
				campMap.put("START_DATE", leadVO.getSTART_DATE());
				campMap.put("END_DATE", leadVO.getEND_DATE());

				// 20170918 ADD BY OCEAN : 指定分派不走任何分派邏輯，直接分派
				TBCAM_SFA_LEADSVO vo = new TBCAM_SFA_LEADSVO();
				vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, (String) tempMap.get("SFA_LEAD_ID"));
				vo.setAO_CODE((String) tempMap.get("AO_CODE"));
				vo.setEMP_ID((String) tempMap.get("EMP_ID"));
				vo.setDISP_FLAG("B"); //B：分行主管分派
				vo.setDISP_DATE(new Timestamp(System.currentTimeMillis()));
				vo.setModifier(ws.getUser().getUserID());
				vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
				
				dam.update(vo);

				CAM998 cam998 = new CAM998();
				cam998.insReaLog(dam, ws.getUser().getUserID(), new Timestamp(System.currentTimeMillis()), (String) tempMap.get("SFA_LEAD_ID"), "30", (String) tempMap.get("REASON"));

				flag++;
			} else {
				throw new APException("分派失敗");
			}
		}

		this.sendRtnObject(null);
	}

}
