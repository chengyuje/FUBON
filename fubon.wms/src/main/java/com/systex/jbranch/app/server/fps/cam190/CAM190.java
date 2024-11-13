package com.systex.jbranch.app.server.fps.cam190;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM999;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/06/07
 * 
 */
@Component("cam190")
@Scope("request")
public class CAM190 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM999.class);
	
	// By 客戶_共用SQL
	private StringBuffer getByCustListSQL (String type) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DEFN.BRANCH_NAME, ");
		sb.append("       LEADS.AO_CODE, ");
		sb.append("       INFO.EMP_NAME, ");
		sb.append("       CASE WHEN CUST.CUST_ID IS NULL THEN LEADS.CUST_ID ELSE CUST.CUST_ID END AS CUST_ID, ");
		sb.append("       CUST.CUST_NAME, ");
		sb.append("       LEADS.COUNTS, ");
		sb.append("       LEADS.MAX_END_DATE, ");
		sb.append("       CASE WHEN TO_CHAR(LEADS.MAX_END_DATE, 'yyyyMMdd') <= TO_CHAR(SYSDATE, 'yyyyMMdd') THEN 'Y' ELSE 'N' END AS STATUS ");
		sb.append("FROM (");
		sb.append("  SELECT AO_CODE, ");
		sb.append("         CUST_ID, ");
		sb.append("         EMP_ID, ");
		sb.append("         BRANCH_ID, ");
		sb.append("         COUNT(CUST_ID) AS COUNTS, ");
		sb.append("         MAX(TEMP.END_DATE) AS MAX_END_DATE ");
		sb.append("  FROM TBCAM_SFA_LEADS TEMP ");
		sb.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON TEMP.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND TEMP.STEP_ID = CAMP.STEP_ID ");
		sb.append("  WHERE AO_CODE IS NOT NULL ");
		sb.append("  AND TEMP.LEAD_STATUS < '03' ");
		sb.append("  AND TEMP.LEAD_STATUS <> 'TR' ");
		sb.append("  AND TEMP.LEAD_TYPE <> '04' ");
		
		if ("getCustExpiredList".equals(type)) { 
			sb.append("  AND (TO_CHAR(TEMP.END_DATE, 'yyyyMMdd') < TO_CHAR(SYSDATE-10, 'yyyyMMdd')) ");
		} else {
			sb.append("  AND (TO_CHAR(TEMP.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE-10, 'yyyyMMdd')) ");
		}
		
		sb.append("  AND TRUNC(TEMP.START_DATE) <= TRUNC(SYSDATE) ");

		sb.append("  GROUP BY AO_CODE, CUST_ID, EMP_ID, BRANCH_ID ");
		sb.append(") LEADS ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = LEADS.CUST_ID ");
		sb.append("INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEADS.BRANCH_ID ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = LEADS.EMP_ID ");
		sb.append("WHERE LEADS.EMP_ID = :empID ");
		sb.append("AND LEADS.COUNTS > 0 ");
		
		sb.append("ORDER BY LEADS.MAX_END_DATE DESC, ");
		sb.append("         DEFN.BRANCH_NBR, ");
		sb.append("         INFO.AO_CODE, ");
		sb.append("         CASE WHEN CUST.CUST_ID IS NULL THEN LEADS.CUST_ID ELSE CUST.CUST_ID END ");
		
		return sb;
	}
	
	// By 客戶(已過期)_前後端入口
	public void getCustExpiredList (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM190OutputVO outputVO = new CAM190OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(getByCustListSQL("getCustExpiredList").toString());
		queryCondition.setObject("empID", ws.getUser().getUserID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setCustExpiredList(list);
		
		this.sendRtnObject(outputVO);
	}

	// By 客戶(即期)_前後端入口
	public void getCustNExpiredList (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM190OutputVO outputVO = new CAM190OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(getByCustListSQL("getCustNExpiredList").toString());
		queryCondition.setObject("empID", ws.getUser().getUserID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setCustNExpiredList(list);
		this.sendRtnObject(outputVO);
	}
	
	// By 活動_共用SQL
	public StringBuffer getByCampListSQL (String type, String TxnCode) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		
		if ("getCampExpiredList".equals(type)) {
			sb.append("SELECT DISTINCT ");
			sb.append("       vw_stat.CAMPAIGN_ID, ");
			sb.append("       vw_stat.STEP_ID, ");
			sb.append("       vw_stat.CAMPAIGN_NAME, ");
			sb.append("       vw_stat.CAMPAIGN_DESC, ");
			sb.append("       camp.START_DATE, ");
			sb.append("       vw_stat.END_DATE, ");
			
			switch (TxnCode) {
				case "CAM190":
					sb.append("       vw_stat.EMP_ID, ");
					break;
				case "CAM191":
					break;
				default:
					sb.append("       vw_stat.EMP_ID, ");
					break;
			}
			
			sb.append("       camp.LEAD_TYPE, ");
			sb.append("       SUM(vw_stat.LEAD_COUNTS) AS LEAD_COUNTS, ");
			sb.append("       SUM(vw_stat.LEAD_CLOSE) AS LEAD_CLOSE ");
			sb.append("FROM VWCAM_CAMPAIGN_EMP_STAT_EXP vw_stat ");
			sb.append("INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = vw_stat.BRANCH_NBR ");
			sb.append("INNER JOIN TBCAM_SFA_CAMPAIGN camp on vw_stat.CAMPAIGN_ID = camp.CAMPAIGN_ID and vw_stat.STEP_ID = camp.STEP_ID ");
			sb.append("WHERE 1 = 1 ");
			
			switch (TxnCode) {
				case "CAM190":
					sb.append("AND vw_stat.EMP_ID = :empID ");
					break;
				case "CAM191":
					sb.append("AND EXISTS ( ");
					sb.append("  SELECT AO.AO_CODE, DT_A.EMP_ID, DT_A.BRANCH_NBR ");
					sb.append("  FROM TBORG_DIAMOND_TEAM DT_A ");
					sb.append("  INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
					sb.append("  WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :empID) ");
					sb.append("  AND vw_stat.EMP_ID = DT_A.EMP_ID ");
					sb.append(") ");
					break;
				default:
					sb.append("AND vw_stat.EMP_ID = :empID ");
					break;
			}

			sb.append("AND vw_stat.LEAD_COUNTS <> 0 ");
			sb.append("AND TRUNC(camp.START_DATE) <= TRUNC(SYSDATE) ");
			sb.append("GROUP BY vw_stat.CAMPAIGN_ID, ");
			sb.append("         vw_stat.STEP_ID, ");
			sb.append("         vw_stat.CAMPAIGN_NAME, ");
			sb.append("         vw_stat.CAMPAIGN_DESC, ");
			sb.append("         camp.START_DATE, ");
			sb.append("         vw_stat.END_DATE, ");
			
			switch (TxnCode) {
				case "CAM190":
					sb.append("         vw_stat.EMP_ID, ");
					break;
				case "CAM191":
					break;
				default:
					sb.append("         vw_stat.EMP_ID, ");
					break;
			}

			sb.append("         camp.LEAD_TYPE ");
			sb.append("ORDER BY vw_stat.END_DATE DESC ");
		} else {
			sb.append("SELECT vw_stat.CAMPAIGN_ID, ");
			sb.append("       vw_stat.STEP_ID, ");
			sb.append("       vw_stat.CAMPAIGN_NAME, ");
			sb.append("       vw_stat.CAMPAIGN_DESC, ");
			sb.append("       camp.START_DATE, ");
			sb.append("       vw_stat.END_DATE, ");
			
			switch (TxnCode) {
				case "CAM190":
					sb.append("       vw_stat.EMP_ID, ");
					break;
				case "CAM191":
					break;
				default:
					sb.append("       vw_stat.EMP_ID, ");
					break;
			}
			
			sb.append("       camp.LEAD_TYPE, ");
			sb.append("       SUM(vw_stat.LEAD_COUNTS) AS LEAD_COUNTS, ");
			sb.append("       SUM(vw_stat.LEAD_CLOSE) AS LEAD_CLOSE ");
			sb.append("FROM VWCAM_CAMPAIGN_EMP_STAT vw_stat ");
			sb.append("INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = vw_stat.BRANCH_NBR ");
			sb.append("INNER JOIN TBCAM_SFA_CAMPAIGN camp on vw_stat.CAMPAIGN_ID = camp.CAMPAIGN_ID and vw_stat.STEP_ID = camp.STEP_ID ");
			sb.append("WHERE 1 = 1 ");
			
			switch (TxnCode) {
				case "CAM190":
					sb.append("AND vw_stat.EMP_ID = :empID ");
					break;
				case "CAM191":
					sb.append("AND EXISTS ( ");
					sb.append("  SELECT AO.AO_CODE, DT_A.EMP_ID, DT_A.BRANCH_NBR ");
					sb.append("  FROM TBORG_DIAMOND_TEAM DT_A ");
					sb.append("  INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
					sb.append("  WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :empID) ");
					sb.append("  AND vw_stat.EMP_ID = DT_A.EMP_ID ");
					sb.append(") ");
					break;
				default:
					sb.append("AND vw_stat.EMP_ID = :empID ");
					break;
			}
			
			sb.append("AND vw_stat.LEAD_COUNTS <> 0 ");
			sb.append("AND TRUNC(camp.START_DATE) <= TRUNC(SYSDATE) ");
			sb.append("GROUP BY vw_stat.CAMPAIGN_ID, ");
			sb.append("         vw_stat.STEP_ID, ");
			sb.append("         vw_stat.CAMPAIGN_NAME, ");
			sb.append("         vw_stat.CAMPAIGN_DESC, ");
			sb.append("         camp.START_DATE, ");
			sb.append("         vw_stat.END_DATE, ");
			
			switch (TxnCode) {
				case "CAM190":
					sb.append("         vw_stat.EMP_ID, ");
					break;
				case "CAM191":
					break;
				default:
					sb.append("         vw_stat.EMP_ID, ");
					break;
			}
			
			sb.append("         camp.LEAD_TYPE ");
			sb.append("ORDER BY vw_stat.END_DATE ");
		}

		return sb;
	}
	
	// By 活動(已過期)_前後端入口
	public void getCampExpiredList (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM190OutputVO outputVO = new CAM190OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(getByCampListSQL("getCampExpiredList", "CAM190").toString());
		queryCondition.setObject("empID", ws.getUser().getUserID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setCampExpiredList(list);
		
		this.sendRtnObject(outputVO);
	}
	
	// By 活動(即期)_前後端入口
	public void getCampNExpiredList (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM190OutputVO outputVO = new CAM190OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(getByCampListSQL("getCampNExpiredList", "CAM190").toString());
		queryCondition.setObject("empID", ws.getUser().getUserID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setCampNExpiredList(list);
		
		this.sendRtnObject(outputVO);
	}
	
	// 自訂查詢_共用SQL
	public StringBuffer customSQL (String type, CAM190InputVO inputVO, String TxnCode) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT AREA.BRANCH_NAME, ");
		sb.append("       LEAD.AO_CODE, ");
		sb.append("       INFO.EMP_NAME, ");//--LEAD.SFA_LEAD_ID, 
		sb.append("       LEAD.CUST_ID, ");
		sb.append("       CUST.CUST_NAME, ");
		sb.append("       CAMP.CAMPAIGN_NAME, ");
		sb.append("       LEAD.END_DATE, ");
		sb.append("       CUST.CON_DEGREE, ");
		sb.append("       CAMP.LEAD_TYPE, ");
		sb.append("		  LEAD.SFA_LEAD_ID ");
		sb.append("FROM TBCAM_SFA_LEADS LEAD ");
		sb.append("INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEAD.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEAD.STEP_ID = CAMP.STEP_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = LEAD.CUST_ID ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = LEAD.EMP_ID ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO AREA ON AREA.BRANCH_NBR = LEAD.BRANCH_ID ");
		sb.append("WHERE 1=1 ");
		
		sb.append("AND TRUNC(CAMP.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (StringUtils.isNotBlank(inputVO.getRegionID()))
			sb.append("AND AREA.REGION_CENTER_ID = :regionID ");
		
		if (StringUtils.isNotBlank(inputVO.getOpID()))
			sb.append("AND AREA.BRANCH_AREA_ID = :opID ");
		
		if (StringUtils.isNotBlank(inputVO.getBranchID()))
			sb.append("AND AREA.BRANCH_NBR = :branchID ");
		
		if (StringUtils.isNotBlank(inputVO.getCustID()))
			sb.append("AND LEAD.CUST_ID LIKE :custID ");
		
		if (StringUtils.isNotBlank(inputVO.getCustName()))
			sb.append("AND CUST.CUST_NAME LIKE :custName ");
		
		// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
		switch (TxnCode) {
			case "CAM190":
				if (StringUtils.isNotBlank(inputVO.getAoCode())) {
					sb.append("AND LEAD.AO_CODE = :aoCode ");
				} else {
					sb.append("AND LEAD.EMP_ID = :empID ");
				}
				break;
			case "CAM191":
				if (StringUtils.isNotBlank(inputVO.getAoCode())) {
					switch (inputVO.getAoCode()) {
						case "Diamond Team":
							sb.append("AND EXISTS ( ");
							sb.append("  SELECT AO.AO_CODE, DT_A.EMP_ID, DT_A.BRANCH_NBR ");
							sb.append("  FROM TBORG_DIAMOND_TEAM DT_A ");
							sb.append("  INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
							sb.append("  WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :empID) ");
							sb.append("  AND LEAD.EMP_ID = DT_A.EMP_ID ");
							sb.append(") ");
							
							break;
						default:
							sb.append("AND LEAD.AO_CODE = :aoCode ");
							break;
					}
				} else {
					sb.append("AND EXISTS ( ");
					sb.append("  SELECT AO.AO_CODE, DT_A.EMP_ID, DT_A.BRANCH_NBR ");
					sb.append("  FROM TBORG_DIAMOND_TEAM DT_A ");
					sb.append("  INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
					sb.append("  WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :empID) ");
					sb.append("  AND LEAD.EMP_ID = DT_A.EMP_ID ");
					sb.append(") ");
				}
				break;
			default:
				sb.append("AND LEAD.EMP_ID = :empID ");
				break;
		}
			
		if (StringUtils.isNotBlank(inputVO.getCampName()))
			sb.append("AND CAMP.CAMPAIGN_NAME LIKE :campName ");
		
		if (StringUtils.isNotBlank(inputVO.getLeadStatus()))
			sb.append("AND LEAD.LEAD_STATUS LIKE :leadStatus ");
		
		if (StringUtils.isNotBlank(inputVO.getVipDegree()))
			sb.append("AND CUST.VIP_DEGREE = :vipDegree ");
		
		if (StringUtils.isNotBlank(inputVO.getLeadDateRange())) {
			Integer leadDateRange = Integer.valueOf(inputVO.getLeadDateRange());
			String startCompare = ("getBeContactList".equals(type) ? ">=" : "<");
			String endCompare = ("getBeContactList".equals(type) ? "<=" : ">");
			String negative = ("getBeContactList".equals(type) ? "+" : "-");
			// EXAMPLE:近十日內
			// 待聯繫: 系統日>到期日(代表未過期) AND 到期日 <= 系統日+10天
			// 未結案已過期/已結案: 系統日<到期日(代表已過期) AND 到期日 >= 系統日-10天
			switch (leadDateRange) {
				case 1:
					sb.append("AND (TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(startCompare).append(" TO_CHAR(SYSDATE, 'yyyyMMdd') AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(endCompare).append(" TO_CHAR(SYSDATE").append(negative).append("10, 'yyyyMMdd')) ");
					break;
				case 2:
					sb.append("AND (TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(startCompare).append(" TO_CHAR(SYSDATE, 'yyyyMMdd') AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(endCompare).append(" TO_CHAR(ADD_MONTHS(SYSDATE,").append(negative).append("1), 'yyyyMMdd')) ");
					break;
				case 3:
					sb.append("AND (TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(startCompare).append(" TO_CHAR(SYSDATE, 'yyyyMMdd') AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(endCompare).append(" TO_CHAR(ADD_MONTHS(SYSDATE,").append(negative).append("3), 'yyyyMMdd')) ");
					break;
				case 4:
					sb.append("AND (TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(startCompare).append(" TO_CHAR(SYSDATE, 'yyyyMMdd') AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(endCompare).append(" TO_CHAR(ADD_MONTHS(SYSDATE,").append(negative).append("6), 'yyyyMMdd')) ");
					break;
				case 5:
					sb.append("AND (TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(startCompare).append(" TO_CHAR(SYSDATE, 'yyyyMMdd') AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') ").append(endCompare).append(" TO_CHAR(ADD_MONTHS(SYSDATE,").append(negative).append("12), 'yyyyMMdd')) ");
					break;
			}
		}
		
		// 名單類型
		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			sb.append("AND CAMP.LEAD_TYPE = :leadType ");
		
		// 名單目的
		if (StringUtils.isNotBlank(inputVO.getCampPurpose()))
			sb.append("AND CAMP.CAMP_PURPOSE = :campPurpose ");
		
		// 名單分類
		if (StringUtils.isNotBlank(inputVO.getCampType())) {
			String campType = inputVO.getCampType();
			if ("OTHER".equals(campType)) {
				sb.append("AND CAMP.LEAD_TYPE NOT IN ( ");
				sb.append("SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE IN ('CAM.CAMP_TYPE_CONTROL', 'CAM.CAMP_TYPE_NEC_NOTIFY', 'CAM.CAMP_TYPE_MARKETING', 'CAM.CAMP_TYPE_LEAVE_INFO', 'CAM.CAMP_TYPE_REFER_INFO')) ");
				
			} else {
				sb.append("AND CAMP.LEAD_TYPE IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.CAMP_TYPE_" + campType + "') ");
			}
		}
		
		if (StringUtils.isNotBlank(inputVO.getConDegree()))
			sb.append("AND CUST.CON_DEGREE = :conDegree ");
		
		if ("getBeContactList".equals(type)) {
			sb.append("AND ( ");
			sb.append("  (LEAD.LEAD_STATUS < '03' ");
			sb.append("   AND (TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') OR TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE-10, 'yyyyMMdd'))"); 
			sb.append("  ) "); 
			sb.append(") ");
		} else if ("getExpiredList".equals(type)) {
			sb.append("AND ( ");
			sb.append("  (LEAD.LEAD_STATUS < '03' AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') < TO_CHAR(SYSDATE-10, 'yyyyMMdd')) ");
			sb.append(") ");
		} else if ("getClosedList".equals(type)) {
			sb.append("AND LEAD.LEAD_STATUS >= '03' ");
		}
		
		sb.append("ORDER BY LEAD.END_DATE DESC ");
		
		return sb;
	}
	
	// 自訂查詢_前後端入口
	public void getList (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM190InputVO inputVO = (CAM190InputVO) body;
		CAM190OutputVO outputVO = new CAM190OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String type = "";
		if ("tab97".equals(inputVO.getCustomTabType())) {
			type = "getBeContactList";
		} else if ("tab98".equals(inputVO.getCustomTabType())) {
			type = "getExpiredList";
		} else if ("tab99".equals(inputVO.getCustomTabType())) {
			type = "getClosedList";
		}
		
		queryCondition.setQueryString(customSQL(type, inputVO, "CAM190").toString());
		
		logger.info("=====CAM190 debug start=====");
		logger.info(customSQL(type, inputVO, "CAM190").toString());
		logger.info("regionID:" + inputVO.getRegionID());
		logger.info("opID:" + inputVO.getOpID());
		logger.info("branchID:" + inputVO.getBranchID());
		logger.info("custID:" + "%" + inputVO.getCustID() + "%");
		logger.info("custName:" + "%" + inputVO.getCustName() + "%");
		logger.info("aoCode:" + inputVO.getAoCode());
		logger.info("empID:" + ws.getUser().getUserID());
		logger.info("campName:" + "%" + inputVO.getCampName() + "%");
		logger.info("leadStatus:" + inputVO.getLeadStatus() + "%");
		logger.info("vipDegree:" + inputVO.getVipDegree());
		logger.info("leadType:" + inputVO.getLeadType());
		logger.info("campPurpose:" + inputVO.getCampPurpose());
		logger.info("conDegree:" + inputVO.getConDegree());
		logger.info("=====CAM190 debug end=====");
		
		if (StringUtils.isNotBlank(inputVO.getRegionID()))
			queryCondition.setObject("regionID", inputVO.getRegionID());
		if (StringUtils.isNotBlank(inputVO.getOpID()))
			queryCondition.setObject("opID", inputVO.getOpID());
		if (StringUtils.isNotBlank(inputVO.getBranchID()))
			queryCondition.setObject("branchID", inputVO.getBranchID());
		if (StringUtils.isNotBlank(inputVO.getCustID()))
			queryCondition.setObject("custID", "%" + inputVO.getCustID() + "%");
		if (StringUtils.isNotBlank(inputVO.getCustName()))
			queryCondition.setObject("custName", "%" + inputVO.getCustName() + "%");
		
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		} else {
			queryCondition.setObject("empID", ws.getUser().getUserID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCampName()))
			queryCondition.setObject("campName", "%" + inputVO.getCampName() + "%");
		if (StringUtils.isNotBlank(inputVO.getLeadStatus()))
			queryCondition.setObject("leadStatus", inputVO.getLeadStatus() + "%");
		if (StringUtils.isNotBlank(inputVO.getVipDegree()))
			queryCondition.setObject("vipDegree", inputVO.getVipDegree());
		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			queryCondition.setObject("leadType", inputVO.getLeadType());
		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			queryCondition.setObject("leadType", inputVO.getLeadType());
		if (StringUtils.isNotBlank(inputVO.getCampPurpose()))
			queryCondition.setObject("campPurpose", inputVO.getCampPurpose());
		if (StringUtils.isNotBlank(inputVO.getConDegree()))
			queryCondition.setObject("conDegree", inputVO.getConDegree());
		
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setCustomList(list); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		
		this.sendRtnObject(outputVO);
	}
	
	// 匯出
	public void exportCust(Object body, IPrimitiveMap header) throws Exception {
		
		CAM190InputVO inputVO = (CAM190InputVO) body;
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		// copy cam210 excel
		String fileName = "";
		if("tab97".equals(inputVO.getCustomTabType()))
			fileName += "待聯繫名單_" + sdf.format(new Date());
		else if("tab98".equals(inputVO.getCustomTabType()))
			fileName += "未結案已過期名單_" + sdf.format(new Date());
		else if("tab99".equals(inputVO.getCustomTabType()))
			fileName += "已結案名單_" + sdf.format(new Date());
		
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
		
		String[] headerLine1 = {"分行", "AO CODE", "理專姓名", "客戶身份證字號", "客戶姓名", "名單/待辦工作名稱", "名單到期日", "貢獻度等級"};
		String[] mainLine    = {"BRANCH_NAME", "AO_CODE", "EMP_NAME", "CUST_ID", 
								"CUST_NAME", "CAMPAIGN_NAME", "END_DATE", "CON_DEGREE"};
		
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
		
		Map<String, String> conMap = new XmlInfo().doGetVariable("CRM.CON_DEGREE", FormatHelper.FORMAT_3);
		
		for (Map<String, Object> map : inputVO.getCustom_list()) {
			row = sheet.createRow(index);
			
			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);
					
					if(StringUtils.equals("CON_DEGREE", mainLine[j])) {
						cell.setCellValue(conMap.get(checkIsNull(map, mainLine[j])));
					}
					else if(StringUtils.equals("END_DATE", mainLine[j])) {
						cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, mainLine[j])) ? sdf2.format(sdf2.parse(checkIsNull(map, mainLine[j]))) : null);
					}
					else {
						cell.setCellValue(checkIsNull(map, mainLine[j]));
					}
				}
				index++;
			}
		}
		
		workbook.write(new FileOutputStream(filePath));
		this.notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName + ".xlsx");
	}
	
	//更新名單聯絡紀錄
	public void updateContactInfo (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM190InputVO inputVO = (CAM190InputVO)body;
		CAM190OutputVO outputVO = new CAM190OutputVO();
		String sfaLeadID = inputVO.getSfaLeadID();
		String custID = inputVO.getCustID();
		
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE TBCAM_SFA_LEADS ");
		sb.append("SET CONTACT = 'Y', CONTACT_DATE = SYSDATE, LASTUPDATE = SYSDATE ");
		sb.append("WHERE SFA_LEAD_ID IN :sfaLeadID AND CUST_ID = :custID");
		
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("sfaLeadID", sfaLeadID);
		queryCondition.setObject("custID", custID);
		
		int row = dam.exeUpdate(queryCondition);
		
		outputVO.setRowUpdate(row);
		
		this.sendRtnObject(outputVO);
	}
	
	private String checkIsNull (Map map, String key) {
		if (null != map && null != map.get(key))
			return String.valueOf(map.get(key));
		else
			return "";
	}
	
}