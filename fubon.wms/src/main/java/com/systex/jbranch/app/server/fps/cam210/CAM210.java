package com.systex.jbranch.app.server.fps.cam210;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM998;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("cam210")
@Scope("request")
public class CAM210 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	// 2020-01-22 modify by ocean : #0000109 即期活動名單現況及已過期活動一覽頁面，活動名稱欄位輸入關係字後帶出相關名單，供使用者選擇後進行查詢，改善效能
	public void getCampaignName(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		CAM210InputVO inputVO = (CAM210InputVO) body;
		CAM210OutputVO outputVO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("  SELECT CAMPAIGN_NAME ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP ");
		
		sb.append("  WHERE 1 = 1 ");
		
		switch (inputVO.getTabSheet()) {
			case "3" :
				sb.append("  AND TRUNC(CAMP.START_DATE) <= TRUNC(SYSDATE) ");
				sb.append("  AND TO_CHAR(CAMP.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE - 10, 'yyyyMMdd') ");
				break;
			case "4" :
				sb.append("  AND TO_CHAR(CAMP.END_DATE, 'yyyyMMdd') < TO_CHAR(SYSDATE - 10, 'yyyyMMdd') ");
				break;
		}
		
		//#0002022: 請協助調整分行全部名單放大鏡搜尋條件
//		sb.append("  AND NOT EXISTS ( ");
//		sb.append("    SELECT 1 ");
//		sb.append("    FROM TBCAM_SFA_LEADS LEADS ");
//		sb.append("    WHERE LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID ");
//		sb.append("    AND LEADS.STEP_ID = CAMP.STEP_ID ");
//		sb.append("    AND NVL(LEAD_STATUS, 'X') = 'TR'");
//		sb.append("  ) ");

		sb.append("  AND EXISTS ( ");
		sb.append("      SELECT 1 ");
		sb.append("      FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("      WHERE LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID ");
		sb.append("      AND LEADS.STEP_ID = CAMP.STEP_ID ");
		
		if (!StringUtils.isBlank(inputVO.getLeadType())) {
			sb.append("      AND LEADS.LEAD_TYPE = :leadType ");
			queryCondition.setObject("leadType", inputVO.getLeadType());
		}
				
		if (StringUtils.isNotBlank(inputVO.getEmpId())) {
			sb.append("      AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getuEmpId())) {
			sb.append("      AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getuEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sb.append("      AND LEADS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sb.append("      AND LEADS.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		sb.append("  ) ");
		
		sb.append("  AND CAMP.REMOVE_FLAG = 'N'");
		
		if (inputVO.getCamp_sDate() != null) {
			sb.append("  AND CAMP.START_DATE >= TRUNC(:camp_sdate) ");
			queryCondition.setObject("camp_sdate", inputVO.getCamp_sDate());
		}	
		
		if (inputVO.getCamp_eDate() != null) {
			sb.append("  AND CAMP.START_DATE < TRUNC(:camp_edate) + 1 ");
			queryCondition.setObject("camp_edate", inputVO.getCamp_eDate());
		}
		
		if (inputVO.getCamp_esDate() != null) {
			sb.append("  AND CAMP.END_DATE >= TRUNC(:camp_ssdate) ");
			queryCondition.setObject("camp_ssdate", inputVO.getCamp_esDate());
		}	
		
		if (inputVO.getCamp_eeDate() != null) {
			sb.append("  AND CAMP.END_DATE < TRUNC(:camp_eedate) + 1 ");
			queryCondition.setObject("camp_eedate", inputVO.getCamp_eeDate());
		}

		if (!StringUtils.isBlank(inputVO.getStepId())) {
			sb.append("  AND CAMP.STEP_ID = :stepId ");
			queryCondition.setObject("stepId", inputVO.getStepId());	
		}
		
		sb.append("  GROUP BY CAMP.CAMPAIGN_NAME ");
		sb.append("  ORDER BY COUNT(CAMP.CAMPAIGN_ID) DESC ");
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setCnameList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	//轄下理專名單執行現狀
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO outputVO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
	
		sql.append("WITH ");
		sql.append("BASE AS ( ");
		sql.append("  SELECT DISTINCT LEADS.EMP_ID, ");
		sql.append("         VDI.REGION_CENTER_ID, ");
		sql.append("         VDI.REGION_CENTER_NAME, ");
		sql.append("         VDI.BRANCH_AREA_ID, ");
		sql.append("         VDI.BRANCH_AREA_NAME, ");
		sql.append("         VDI.BRANCH_NBR, ");
		sql.append("         VDI.BRANCH_NAME, ");
		sql.append("         LEADS.CAMPAIGN_ID, ");
		sql.append("         LEADS.STEP_ID, ");
		sql.append("         LEADS.CUST_ID, ");
		sql.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEADS.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sql.append("         AO.AO_CODE, ");
		sql.append("         ROLE.ROLE_ID, ");
		sql.append("         CASE WHEN TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE + 10) AND LEADS.LEAD_STATUS < '03' THEN 1 ELSE 0 END AS CONTACT_CUST, ");
		sql.append("         CASE WHEN TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE + 10) AND LEADS.LEAD_STATUS < '03' AND TRUNC(LEADS.LASTUPDATE) = TRUNC(SYSDATE) AND LENGTH(LEADS.MODIFIER) = 6 THEN 1 ELSE 0 END AS CONTACTED_CUST, ");
		sql.append("         CASE WHEN LEADS.LEAD_STATUS < '03' AND TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE + 10) THEN 1 ELSE 0 END AS TOTAL_CONTACT_CUST, ");
		sql.append("         CASE WHEN TRUNC(LEADS.END_DATE) <= TRUNC(SYSDATE) AND TRUNC(LEADS.END_DATE) > TRUNC(ADD_MONTHS(SYSDATE, -12)) AND LEADS.LEAD_STATUS < '03' THEN 1 ELSE 0 END AS NOT_CONTACT_CUST ");
		sql.append("  FROM TBCAM_SFA_LEADS LEADS ");
		sql.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEADS.BRANCH_ID ");
		sql.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM ON LEADS.EMP_ID = MEM.EMP_ID ");
		sql.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON AO.TYPE = '1' AND LEADS.EMP_ID = AO.EMP_ID ");
		sql.append("  LEFT JOIN (SELECT ROLE_ID, EMP_ID FROM TBORG_MEMBER_ROLE WHERE IS_PRIMARY_ROLE = 'Y') MROLE ON MROLE.EMP_ID = LEADS.EMP_ID ");
		sql.append("  LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y') ROLE ON ROLE.ROLE_ID = MROLE.ROLE_ID ");
		sql.append("  LEFT JOIN VWORG_DEFN_INFO VDI ON VDI.BRANCH_NBR = LEADS.BRANCH_ID ");
		sql.append("  WHERE NVL(LEADS.EMP_ID,' ') <> ' ' ");
		sql.append("  AND LEADS.LEAD_TYPE <> '04' ");
		sql.append("  AND LEADS.AO_CODE IS NOT NULL ");
		sql.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		sql.append("  AND TRUNC(SYSDATE) <= TRUNC (LEADS.END_DATE + 10) ");

		// 20190830 add by ocean : 0006667 刪除名單後,主管帶分派名單沒有移除(祐傑)
		sql.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			sql.append("  AND LEADS.AO_CODE <> LEADS.EMP_ID ");
			
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("  AND VDI.BRANCH_NBR = :branchID "); 
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("  AND ( ");
				sql.append("       VDI.BRANCH_AREA_ID = :branchAreaID ");
				sql.append("    OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append("  ) ");

				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("  AND VDI.REGION_CENTER_ID = :regionCenterID ");
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sql.append("  AND VDI.REGION_CENTER_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			}
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}

		if (StringUtils.isNotBlank(inputVO.getEmpId())) {
			sql.append("AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getuEmpId())) {
			sql.append("AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getuEmpId());
		}
		
		if (!StringUtils.isBlank(inputVO.getAoCode())) {
			sql.append("AND LEADS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sql.append("AND LEADS.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		
		// 名單分類
		if (StringUtils.isNotBlank(inputVO.getCampType())) {
			String campType = inputVO.getCampType();
			if ("OTHER".equals(campType)) {
				sql.append("AND CAMP.LEAD_TYPE NOT IN ( ");
				sql.append("SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE IN ('CAM.CAMP_TYPE_CONTROL', 'CAM.CAMP_TYPE_NEC_NOTIFY', 'CAM.CAMP_TYPE_MARKETING', 'CAM.CAMP_TYPE_LEAVE_INFO', 'CAM.CAMP_TYPE_REFER_INFO')) ");
				
			} else {
				sql.append("AND CAMP.LEAD_TYPE IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.CAMP_TYPE_" + campType + "') ");
			}
		}
		
		// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
		if (!StringUtils.isBlank(inputVO.getLeadType())) {
			sql.append("AND LEADS.LEAD_TYPE = :leadType ");
			queryCondition.setObject("leadType", inputVO.getLeadType());
		}
		
		sql.append(") ");
		
		sql.append("SELECT ");
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		} else {
		}
		
		sql.append("       EMP_ID, ");
		sql.append("       EMP_NAME, ");
		sql.append("       AO_CODE, ");
		sql.append("       ROLE_ID, ");
		sql.append("       SUM(CONTACT_CUST + CONTACTED_CUST) AS CONTACT_CUST, ");
		sql.append("       SUM(CONTACTED_CUST) AS CONTACTED_CUST, ");
		sql.append("       SUM(TOTAL_CONTACT_CUST) AS TOTAL_CONTACT_CUST, ");
		sql.append("       SUM(NOT_CONTACT_CUST) AS NOT_CONTACT_CUST ");
		sql.append("FROM BASE ");
		
		sql.append("GROUP BY ");
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			sql.append("         REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, AO_CODE, ROLE_ID ");
		} else {
			sql.append("         EMP_ID, EMP_NAME, AO_CODE, ROLE_ID ");
		}
		
		sql.append("HAVING SUM(CONTACT_CUST + CONTACTED_CUST) > 0 OR SUM(CONTACTED_CUST) > 0 OR SUM(TOTAL_CONTACT_CUST) > 0 OR SUM(NOT_CONTACT_CUST) > 0 ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			sql.append("ORDER BY ");
			sql.append("         REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ");
		} else {
		}
		
		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	//轄下非理專名單執行現狀
	public void queryData_2(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("WITH ");
		sql.append("BASE AS ( ");
		sql.append("  SELECT DISTINCT LEADS.EMP_ID, MEM.DEPT_ID AS EMP_DEPT, ");
		sql.append("         VDI.REGION_CENTER_ID, VDI.REGION_CENTER_NAME, VDI.BRANCH_AREA_ID, VDI.BRANCH_AREA_NAME, VDI.BRANCH_NBR, VDI.BRANCH_NAME, ");
		sql.append("         LEADS.CAMPAIGN_ID, LEADS.STEP_ID, LEADS.CUST_ID, MEM.EMP_NAME, AO.AO_CODE, ROLE.ROLE_ID, ");
		sql.append("         CASE WHEN TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE + 10) AND LEADS.LEAD_STATUS < '03' THEN 1 ELSE 0 END AS CONTACT_CUST, ");
		sql.append("         CASE WHEN SUBSTR(LEADS.LEAD_STATUS, 0, 2) = '03' AND TRUNC(LEADS.LASTUPDATE) = TRUNC(SYSDATE) AND LENGTH(LEADS.MODIFIER) = 6 THEN 1 ELSE 0 END AS CONTACTED_CUST, ");
		sql.append("         CASE WHEN LEADS.LEAD_STATUS < '03' AND TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE + 10) THEN 1 ELSE 0 END AS TOTAL_CONTACT_CUST, ");
		sql.append("         CASE WHEN TRUNC(LEADS.END_DATE) <= TRUNC(SYSDATE) AND TRUNC(LEADS.END_DATE) > TRUNC(ADD_MONTHS(SYSDATE, -12)) AND LEADS.LEAD_STATUS < '03' THEN 1 ELSE 0 END AS NOT_CONTACT_CUST, ");
		sql.append("         MEM.JOB_TITLE_NAME ");
		sql.append("  FROM TBCAM_SFA_LEADS LEADS ");
		sql.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEADS.BRANCH_ID ");
		sql.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM ON LEADS.EMP_ID = MEM.EMP_ID ");
		sql.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON AO.TYPE = '1' AND LEADS.EMP_ID = AO.EMP_ID ");
		sql.append("  LEFT JOIN (SELECT ROLE_ID, EMP_ID FROM TBORG_MEMBER_ROLE WHERE IS_PRIMARY_ROLE = 'Y') MROLE ON MROLE.EMP_ID = LEADS.EMP_ID ");
		sql.append("  LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y') ROLE ON ROLE.ROLE_ID = MROLE.ROLE_ID ");
		sql.append("  LEFT JOIN VWORG_DEFN_INFO VDI ON VDI.BRANCH_NBR = LEADS.BRANCH_ID ");
		sql.append("  WHERE NVL(LEADS.EMP_ID,' ') <> ' ' ");
		sql.append("  AND LEADS.LEAD_TYPE <> '04' ");
		sql.append("  AND LEADS.AO_CODE IS NOT NULL ");
		sql.append("  AND ROLE.ROLE_ID NOT IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('002', '003')) ");
		sql.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		
		// 20190830 add by ocean : 0006667 刪除名單後,主管帶分派名單沒有移除(祐傑)
		sql.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("  AND VDI.REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sql.append("  AND VDI.REGION_CENTER_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("  AND VDI.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼getOp
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else {
				sql.append("  AND VDI.BRANCH_AREA_ID IN (:branchAreaIDList) ");
				queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("  AND VDI.BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else {
				sql.append("  AND VDI.BRANCH_NBR IN (:branchIDList) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			} else {
				sql.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmpId())) {
			sql.append("AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getuEmpId())) {
			sql.append("AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getuEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND LEADS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sql.append("AND LEADS.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		
		// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
		if (!StringUtils.isBlank(inputVO.getLeadType())) {
			sql.append("AND LEADS.LEAD_TYPE = :leadType ");
			queryCondition.setObject("leadType", inputVO.getLeadType());
		}
		
		// 名單分類
		if (StringUtils.isNotBlank(inputVO.getCampType())) {
			String campType = inputVO.getCampType();
			if ("OTHER".equals(campType)) {
				sql.append("AND CAMP.LEAD_TYPE NOT IN ( ");
				sql.append("SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE IN ('CAM.CAMP_TYPE_CONTROL', 'CAM.CAMP_TYPE_NEC_NOTIFY', 'CAM.CAMP_TYPE_MARKETING', 'CAM.CAMP_TYPE_LEAVE_INFO', 'CAM.CAMP_TYPE_REFER_INFO')) ");
				
			} else {
				sql.append("AND CAMP.LEAD_TYPE IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.CAMP_TYPE_" + campType + "') ");
			}
		}
		
		sql.append(") ");
		
		sql.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, AO_CODE, ROLE_ID, EMP_DEPT, JOB_TITLE_NAME, ");
		sql.append("       CONTACT_CUST, CONTACTED_CUST, TOTAL_CONTACT_CUST, NOT_CONTACT_CUST ");
		sql.append("FROM ( ");
		sql.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, AO_CODE, ROLE_ID, EMP_DEPT, JOB_TITLE_NAME, ");
		sql.append("         SUM(CONTACT_CUST + CONTACTED_CUST) AS CONTACT_CUST, SUM(CONTACTED_CUST) AS CONTACTED_CUST, SUM(TOTAL_CONTACT_CUST) AS TOTAL_CONTACT_CUST, SUM(NOT_CONTACT_CUST) AS NOT_CONTACT_CUST ");
		sql.append("  FROM BASE ");
		sql.append("  GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, AO_CODE, ROLE_ID, EMP_DEPT, JOB_TITLE_NAME ");
		sql.append("  HAVING SUM(CONTACT_CUST + CONTACTED_CUST) > 0 OR SUM(CONTACTED_CUST) > 0 OR SUM(TOTAL_CONTACT_CUST) > 0 "); // OR SUM(NOT_CONTACT_CUST) > 0 
		sql.append(") ");
		sql.append("WHERE EMP_DEPT = BRANCH_NBR ");
		sql.append("OR (EMP_DEPT <> BRANCH_NBR AND CONTACT_CUST > 0) ");
		sql.append("ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ");
		queryCondition.setQueryString(sql.toString());	

		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
	
	//待聯繫客戶名單一覽
    public void queryData_3(Object body, IPrimitiveMap header) throws JBranchException {
    	
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
    	CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("WITH LEAD AS ( ");
		sql.append("  SELECT RC.DEPT_ID AS REGION_CENTER_ID, ");
		sql.append("         RC.DEPT_NAME AS REGION_CENTER_NAME, ");
		sql.append("         OP.DEPT_ID AS BRANCH_AREA_ID, ");
		sql.append("         OP.DEPT_NAME AS BRANCH_AREA_NAME, ");
		sql.append("         BR.DEPT_ID AS BRANCH_NBR, ");
		sql.append("         BR.DEPT_NAME AS BRANCH_NAME, ");
		sql.append("         LEADS.CUST_ID, ");
		sql.append("         CASE WHEN LEADS.AO_CODE = LEADS.EMP_ID THEN NULL ELSE LEADS.AO_CODE END AS AO_CODE, ");
		sql.append("         LEADS.EMP_ID, ");
		sql.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEADS.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sql.append("         MEM.JOB_TITLE_NAME, ");
		sql.append("         COUNT(1) AS COUNTS, ");
		sql.append("         MIN(LEADS.END_DATE) AS MIN_END_DATE ");
		sql.append("  FROM TBCAM_SFA_LEADS LEADS ");
		sql.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEADS.BRANCH_ID ");
		sql.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM ON LEADS.EMP_ID = MEM.EMP_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND LEADS.BRANCH_ID = BR.DEPT_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND BR.PARENT_DEPT_ID = OP.DEPT_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND OP.PARENT_DEPT_ID = RC.DEPT_ID ");
		sql.append("  WHERE LEADS.LEAD_STATUS < '03' ");
		sql.append("  AND TO_CHAR(LEADS.END_DATE+10, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		sql.append("  AND LEADS.AO_CODE IS NOT NULL ");
		
		// 20230914 add mark by ocean : #0001571:WMS-CR-20230727-01_為利名單有效利用及名單改派效率擬調整名單改派功能 (開放參考名單可以執行改派)
//		sql.append("  AND LEADS.LEAD_TYPE <> '04' ");
		
		// 20190830 add by ocean : 0006667 刪除名單後,主管帶分派名單沒有移除(祐傑)
		sql.append("  AND CAMP.REMOVE_FLAG = 'N' ");

		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			
		
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BR.DEPT_ID = :branchID "); 
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     OP.DEPT_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");

				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND RC.DEPT_ID = :regionCenterID "); 
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sql.append("AND RC.DEPT_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			} 
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
		if (!StringUtils.isBlank(inputVO.getLeadType())) {
			sql.append("AND LEADS.LEAD_TYPE = :leadType ");
			queryCondition.setObject("leadType", inputVO.getLeadType());
		}
		
		sql.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (StringUtils.isNotBlank(inputVO.getEmpId())) {
			sql.append("AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getuEmpId())) {
			sql.append("AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getuEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND LEADS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sql.append("AND LEADS.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		
		sql.append("  GROUP BY RC.DEPT_ID, ");
		sql.append("           RC.DEPT_NAME, ");
		sql.append("           OP.DEPT_ID, ");
		sql.append("           OP.DEPT_NAME, ");
		sql.append("           BR.DEPT_ID, ");
		sql.append("           BR.DEPT_NAME, ");
		sql.append("           LEADS.CUST_ID, ");
		sql.append("           LEADS.AO_CODE, ");
		sql.append("           LEADS.EMP_ID, ");
		sql.append("           MEM.EMP_NAME, ");
		sql.append("           MEM.JOB_TITLE_NAME ");
		sql.append("), CUST AS ( ");
		sql.append("  SELECT CM.CUST_ID, CM.CUST_NAME, CASE WHEN INFO.EMP_ID IS NULL THEN 'N' ELSE 'Y' END AS UC_FLAG ");
		sql.append("  FROM TBCRM_CUST_MAST CM ");
		sql.append("  LEFT JOIN VWORG_EMP_UHRM_INFO INFO ON CM.AO_CODE = INFO.UHRM_CODE ");
		sql.append("  WHERE CM.CUST_ID IN (SELECT CUST_ID FROM LEAD) ");
		sql.append(") ");
		    
		sql.append("SELECT CASE WHEN (CUST.UC_FLAG = 'Y' OR CUST.UC_FLAG IS NULL) AND LEAD.CUST_ID LIKE 'SFA%' THEN 'N' ELSE CUST.UC_FLAG END AS UC_FLAG, ");
		sql.append("       LEAD.REGION_CENTER_ID, ");
		sql.append("       LEAD.REGION_CENTER_NAME, ");
		sql.append("       LEAD.BRANCH_AREA_ID, ");
		sql.append("       LEAD.BRANCH_AREA_NAME, ");
		sql.append("       LEAD.BRANCH_NBR, ");
		sql.append("       LEAD.BRANCH_NAME, ");
		sql.append("       LEAD.EMP_ID, ");
		sql.append("       LEAD.EMP_NAME, ");
		sql.append("       LEAD.JOB_TITLE_NAME, ");
		sql.append("       LEAD.AO_CODE, ");
		sql.append("       LEAD.CUST_ID, ");
		sql.append("       CUST.CUST_NAME, ");
		sql.append("       LEAD.COUNTS, ");
		sql.append("       TO_CHAR(LEAD.MIN_END_DATE, 'yyyy-MM-dd') AS MIN_END_DATE, ");
		sql.append("       CASE WHEN TO_CHAR(LEAD.MIN_END_DATE, 'yyyyMMdd') <= TO_CHAR(SYSDATE, 'yyyyMMdd') THEN 'Y' ELSE 'N' END AS STATUS ");
		sql.append("FROM LEAD ");
		sql.append("LEFT JOIN CUST ON LEAD.CUST_ID = CUST.CUST_ID ");

		Map<String, String> max_qry_rows_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
		
		String max_qry_rows = "";
		for (Object key : max_qry_rows_xml.keySet()) {
			max_qry_rows = key.toString();
        }
		
		sql.append("AND ROWNUM <= :max_qry_rows ");
		queryCondition.setObject("max_qry_rows", max_qry_rows);
		
		sql.append("ORDER BY MIN_END_DATE ");

		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
   
    //即期活動執行現況
    public void queryData_4(Object body, IPrimitiveMap header) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
    	Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
    	
    	CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();	
		
		sql.append("WITH LEADS AS ( ");
		sql.append("  SELECT LEAD.LASTUPDATE, LEAD.LEAD_STATUS, LEAD.LEAD_TYPE, ");
		sql.append("         CAMP.CAMPAIGN_ID, CAMP.STEP_ID, CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, CAMP.END_DATE, LEAD.CUST_ID, LEAD.EMP_ID, MEM.EMP_NAME, MEM.JOB_TITLE_NAME, ");
		sql.append("         RC.DEPT_ID AS REGION_CENTER_ID, RC.DEPT_NAME AS REGION_CENTER_NAME, OP.DEPT_ID AS BRANCH_AREA_ID, OP.DEPT_NAME AS BRANCH_AREA_NAME, BR.DEPT_ID AS BRANCH_NBR, BR.DEPT_NAME AS BRANCH_NAME, ");
		sql.append("         1 AS LEAD_COUNTS, ");
		sql.append("         CASE WHEN LEAD.LEAD_STATUS >= '03' AND NVL(LEAD.AO_CODE, ' ') <> ' ' THEN 1 ELSE 0 END AS LEAD_CLOSE ");
		sql.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEAD ");
		sql.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEAD.BRANCH_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND LEAD.BRANCH_ID = BR.DEPT_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND BR.PARENT_DEPT_ID = OP.DEPT_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND OP.PARENT_DEPT_ID = RC.DEPT_ID ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM ON LEAD.EMP_ID = MEM.EMP_ID ");
		sql.append("  WHERE CAMP.CAMPAIGN_ID = LEAD.CAMPAIGN_ID AND CAMP.STEP_ID = LEAD.STEP_ID ");
		sql.append("  AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE-10, 'yyyyMMdd') ");
		sql.append("  AND LEAD.LEAD_STATUS <> 'TR' ");
//		sql.append("  AND NVL(LEAD.AO_CODE, ' ') <> ' ' "); // 20180907 MODIFY BY OCEAN 
		
		// 20190830 add by ocean : 0006667 刪除名單後,主管帶分派名單沒有移除(祐傑)
		sql.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			
		
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BR.DEPT_ID = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     OP.DEPT_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");

				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND RC.DEPT_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sql.append("AND RC.DEPT_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEAD.AO_CODE) ");
			}
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
				
		// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
		if (!StringUtils.isBlank(inputVO.getLeadType())) {
			sql.append("AND LEAD.LEAD_TYPE = :leadType ");
			queryCondition.setObject("leadType", inputVO.getLeadType());
		}
		
		sql.append("AND TRUNC(LEAD.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (StringUtils.isNotBlank(inputVO.getEmpId())) {
			sql.append("AND LEAD.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getuEmpId())) {
			sql.append("      AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getuEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND LEAD.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		if (!StringUtils.isBlank(inputVO.getCampaignName())) {
			if (headmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND CAMP.CAMPAIGN_NAME = :campaignName ");
				queryCondition.setObject("campaignName", inputVO.getCampaignName());
			} else {
				sql.append("AND CAMP.CAMPAIGN_NAME like :campaignName ");
				queryCondition.setObject("campaignName", "%" + inputVO.getCampaignName() + "%");
			}
		}
		
		if (!StringUtils.isBlank(inputVO.getStepId())) {
			sql.append("AND CAMP.STEP_ID = :stepId ");
			queryCondition.setObject("stepId", inputVO.getStepId());	
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sql.append("AND LEAD.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		
		if (inputVO.getCamp_sDate() != null) {
			sql.append("and CAMP.START_DATE >= TRUNC(:camp_sdate) ");
			queryCondition.setObject("camp_sdate", inputVO.getCamp_sDate());
		}	
		
		if (inputVO.getCamp_eDate() != null) {
			sql.append("and CAMP.START_DATE < TRUNC(:camp_edate)+1 ");
			queryCondition.setObject("camp_edate", inputVO.getCamp_eDate());
		}
		
		if (inputVO.getCamp_esDate() != null) {
			sql.append("and CAMP.END_DATE >= TRUNC(:camp_ssdate) ");
			queryCondition.setObject("camp_ssdate", inputVO.getCamp_esDate());
		}	
		
		if (inputVO.getCamp_eeDate() != null) {
			sql.append("and CAMP.END_DATE < TRUNC(:camp_eedate)+1 ");
			queryCondition.setObject("camp_eedate", inputVO.getCamp_eeDate());
		}
		
		sql.append("), COUNT_TEST AS ( ");
		sql.append("  SELECT L.CAMPAIGN_ID, L.STEP_ID, L.CAMPAIGN_NAME, L.CAMPAIGN_DESC, L.END_DATE, L.REGION_CENTER_ID, L.REGION_CENTER_NAME, L.BRANCH_AREA_ID, L.BRANCH_AREA_NAME, L.BRANCH_NBR, L.BRANCH_NAME, L.EMP_ID, L.EMP_NAME, L.JOB_TITLE_NAME, L.LEAD_TYPE, ");
		sql.append("         SUM(LEAD_COUNTS) AS LEAD_COUNTS, SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("  FROM LEADS L ");
		sql.append("  GROUP BY L.CAMPAIGN_ID, L.STEP_ID, L.CAMPAIGN_NAME, L.CAMPAIGN_DESC, L.END_DATE, L.REGION_CENTER_ID, L.REGION_CENTER_NAME, L.BRANCH_AREA_ID, L.BRANCH_AREA_NAME, L.BRANCH_NBR, L.BRANCH_NAME, L.EMP_ID, L.EMP_NAME, L.JOB_TITLE_NAME, L.LEAD_TYPE ");
		sql.append("  ORDER BY L.END_DATE DESC ");
		sql.append(") ");
		
		sql.append("SELECT CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, LEAD_TYPE, ");
		if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		} else if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		} else {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		}
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, ");
		sql.append("       SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("FROM  COUNT_TEST ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
			sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, REGION_CENTER_ID, REGION_CENTER_NAME, LEAD_TYPE ");
		} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
			sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRAND_AREA_NAME, LEAD_TYPE ");
		} else if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
			sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRAND_AREA_NAME, BRANCH_NBR, BRANCH_NAME, LEAD_TYPE ");
		} else {
			sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, REGION_CENTER_ID, REGION_CENTER_NAME, LEAD_TYPE ");
		}
		
		sql.append("ORDER BY ");
		
		if (!StringUtils.isBlank(inputVO.getCampaignName())) {
//			sql.append("DECODE(CAMPAIGN_NAME, :campaignName, 1, DECODE(STEP_ID, :stepId, 1, 99), 2, 99), ");
//			queryCondition.setObject("campaignName", inputVO.getCampaignName());	
//			queryCondition.setObject("stepId", inputVO.getStepId());
			sql.append("CAMPAIGN_ID, STEP_ID, ");
		}
		
		sql.append("END_DATE ");
		
		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
    
    //已過期活動一覽
    public void queryData_5(Object body, IPrimitiveMap header) throws JBranchException {	

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		
    	CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("WITH LEADS AS ( ");
		sql.append("  SELECT LEAD.LASTUPDATE, LEAD.LEAD_STATUS, LEAD.LEAD_TYPE, ");
		sql.append("         CAMP.CAMPAIGN_ID, CAMP.STEP_ID, CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, CAMP.END_DATE, LEAD.CUST_ID, LEAD.EMP_ID, MEM.EMP_NAME, MEM.JOB_TITLE_NAME, ");
		sql.append("         RC.DEPT_ID AS REGION_CENTER_ID, RC.DEPT_NAME AS REGION_CENTER_NAME, OP.DEPT_ID AS BRANCH_AREA_ID, OP.DEPT_NAME AS BRANCH_AREA_NAME, BR.DEPT_ID AS BRANCH_NBR, BR.DEPT_NAME AS BRANCH_NAME, ");
		sql.append("         1 AS LEAD_COUNTS, ");
		sql.append("         CASE WHEN LEAD.LEAD_STATUS >= '03' AND LEAD.AO_CODE IS NOT NULL THEN 1 ELSE 0 END AS LEAD_CLOSE ");
		sql.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEAD ");
		sql.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEAD.BRANCH_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND LEAD.BRANCH_ID = BR.DEPT_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND BR.PARENT_DEPT_ID = OP.DEPT_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND OP.PARENT_DEPT_ID = RC.DEPT_ID ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM ON LEAD.EMP_ID = MEM.EMP_ID ");
		sql.append("  WHERE CAMP.CAMPAIGN_ID = LEAD.CAMPAIGN_ID AND CAMP.STEP_ID = LEAD.STEP_ID ");
		sql.append("  AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') < TO_CHAR(SYSDATE-10, 'yyyyMMdd') ");
		sql.append("  AND LEAD.LEAD_STATUS <> 'TR' ");
		
		// 20190830 add by ocean : 0006667 刪除名單後,主管帶分派名單沒有移除(祐傑)
		sql.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {		
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BR.DEPT_ID = :branchID ");
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     OP.DEPT_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");
				
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND RC.DEPT_ID = :regionCenterID "); 
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sql.append("AND RC.DEPT_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
		
			// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEAD.AO_CODE) ");
			}
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
		if (!StringUtils.isBlank(inputVO.getLeadType())) {
			sql.append("AND LEAD.LEAD_TYPE = :leadType ");
			queryCondition.setObject("leadType", inputVO.getLeadType());
		}
				
		if (StringUtils.isNotBlank(inputVO.getEmpId())) {
			sql.append("AND LEAD.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getuEmpId())) {
			sql.append("      AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getuEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND LEAD.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		if (!StringUtils.isBlank(inputVO.getCampaignName())) {
			if (headmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND CAMP.CAMPAIGN_NAME = :campaignName ");
				queryCondition.setObject("campaignName", inputVO.getCampaignName());
			} else {
				sql.append("AND CAMP.CAMPAIGN_NAME like :campaignName ");
				queryCondition.setObject("campaignName", "%" + inputVO.getCampaignName() + "%");
			}
		}
		
		if (!StringUtils.isBlank(inputVO.getStepId())) {
			sql.append("AND CAMP.STEP_ID = :stepId ");
			queryCondition.setObject("stepId", inputVO.getStepId());	
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sql.append("AND LEAD.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		if (inputVO.getCamp_sDate() != null) {
			sql.append("and CAMP.START_DATE >= TRUNC(:camp_sdate) ");
			queryCondition.setObject("camp_sdate", inputVO.getCamp_sDate());
		}	
		if (inputVO.getCamp_eDate() != null) {
			sql.append("and CAMP.START_DATE < TRUNC(:camp_edate)+1 ");
			queryCondition.setObject("camp_edate", inputVO.getCamp_eDate());
		}
		if (inputVO.getCamp_esDate() != null) {
			sql.append("and CAMP.END_DATE >= TRUNC(:camp_ssdate) ");
			queryCondition.setObject("camp_ssdate", inputVO.getCamp_esDate());
		}	
		if (inputVO.getCamp_eeDate() != null) {
			sql.append("and CAMP.END_DATE < TRUNC(:camp_eedate)+1 ");
			queryCondition.setObject("camp_eedate", inputVO.getCamp_eeDate());
		}
		
		sql.append("), COUNT_TEST AS ( ");
		sql.append("  SELECT L.CAMPAIGN_ID, L.STEP_ID, L.CAMPAIGN_NAME, L.CAMPAIGN_DESC, L.END_DATE, L.REGION_CENTER_ID, L.REGION_CENTER_NAME, L.BRANCH_AREA_ID, L.BRANCH_AREA_NAME, L.BRANCH_NBR, L.BRANCH_NAME, L.EMP_ID, L.EMP_NAME, L.JOB_TITLE_NAME, L.LEAD_TYPE, ");
		sql.append("         SUM(LEAD_COUNTS) AS LEAD_COUNTS, SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("  FROM LEADS L ");
		sql.append("  GROUP BY L.CAMPAIGN_ID, L.STEP_ID, L.CAMPAIGN_NAME, L.CAMPAIGN_DESC, L.END_DATE, L.REGION_CENTER_ID, L.REGION_CENTER_NAME, L.BRANCH_AREA_ID, L.BRANCH_AREA_NAME, L.BRANCH_NBR, L.BRANCH_NAME, L.EMP_ID, L.EMP_NAME, L.JOB_TITLE_NAME, L.LEAD_TYPE ");
		sql.append(") ");
		
		sql.append("SELECT CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, LEAD_TYPE, ");
		if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		} else if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		} else {
			sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		}
		
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, ");
		sql.append("       SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("FROM  COUNT_TEST ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
			sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, REGION_CENTER_ID, REGION_CENTER_NAME, LEAD_TYPE ");
		} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
			sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRAND_AREA_NAME, LEAD_TYPE ");
		} else if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
			sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRAND_AREA_NAME, BRANCH_NBR, BRANCH_NAME, LEAD_TYPE ");
		} else {
			sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, END_DATE, CAMPAIGN_DESC, REGION_CENTER_ID, REGION_CENTER_NAME, LEAD_TYPE ");
		}
		
		sql.append("ORDER BY END_DATE ");
		
		queryCondition.setQueryString(sql.toString());		
		
		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
	
	//待聯繫客戶名單清單(名單分派)
    public void queryData_6(Object body, IPrimitiveMap header) throws JBranchException {
    	
    	XmlInfo xmlInfo = new XmlInfo();
    	Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
    	
    	CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO outputVO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DEFN.REGION_CENTER_ID, ");
		sb.append("       DEFN.REGION_CENTER_NAME, ");
		sb.append("       DEFN.BRANCH_AREA_ID, ");
		sb.append("       DEFN.BRANCH_AREA_NAME, ");
		sb.append("       DEFN.BRANCH_NBR, ");
		sb.append("       DEFN.BRANCH_NAME, ");
		sb.append("       LEADS.SFA_LEAD_ID, ");
		sb.append("       LEADS.CUST_ID, ");
		sb.append("       CM.CUST_NAME, ");
		sb.append("       LEADS.BRANCH_ID, ");
		sb.append("       CASE WHEN LEADS.AO_CODE = LEADS.EMP_ID THEN NULL ELSE LEADS.AO_CODE END AS AO_CODE, ");
		sb.append("       LEADS.EMP_ID, ");
		sb.append("       MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEADS.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sb.append("       MEM.JOB_TITLE_NAME, ");
		sb.append("       LEADS.CAMPAIGN_ID, ");
		sb.append("       LEADS.STEP_ID, ");
		sb.append("       LEADS.LEAD_NAME, ");
		sb.append("       TO_CHAR(LEADS.START_DATE, 'YYYY-MM-DD') AS START_DATE, ");
		sb.append("       TO_CHAR(LEADS.END_DATE, 'YYYY-MM-DD') AS END_DATE, ");
		sb.append("       LEADS.LEAD_TYPE ");
		sb.append("FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEADS.BRANCH_ID ");
		sb.append("INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CM ON LEADS.CUST_ID = CM.CUST_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON LEADS.EMP_ID = MEM.EMP_ID ");
		sb.append("WHERE LEADS.LEAD_STATUS < '03' ");
		sb.append("AND TO_CHAR(LEADS.END_DATE + 10, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		sb.append("AND LEADS.AO_CODE IS NOT NULL ");
		sb.append("AND CAMP.REMOVE_FLAG = 'N' ");
		sb.append("AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		
		// 查詢條件：業務處、營運區、分行
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {			
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sb.append("AND DEFN.BRANCH_NBR = :branchID ");
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else {
				if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
					sb.append("AND DEFN.REGION_CENTER_ID = :regionCenterID "); 
					queryCondition.setObject("regionCenterID", inputVO.getRegion());
				} else {
					sb.append("AND DEFN.REGION_CENTER_ID IN (:regionCenterIDList) ");
					queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
					sb.append("AND ( ");
					sb.append("     DEFN.BRANCH_AREA_ID = :branchAreaID ");
					sb.append("  OR MEM.DEPT_ID = :branchAreaID ");
					sb.append(")");
					queryCondition.setObject("branchAreaID", inputVO.getOp());
				} else {
					sb.append("AND DEFN.BRANCH_AREA_ID IN (:branchAreaIDList) ");
					queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
				
				sb.append("AND DEFN.BRANCH_NBR IN (:branchIDList) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			
			// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			} else {
				sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID) ");
			}
		} else {
			sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// 查詢條件：員工姓名
		if (StringUtils.isNotBlank(inputVO.getEmpId())) {
			sb.append("AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpId());
		}
		
		if (StringUtils.isNotBlank(inputVO.getuEmpId())) {
			sb.append("AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getuEmpId());
		}
		
		// 查詢條件：個金RM
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sb.append("AND LEADS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		// 查詢條件：客戶ID
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sb.append("AND LEADS.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		
		// 查詢條件：活動名稱
		if (!StringUtils.isBlank(inputVO.getCampaignName())) {
			if (headmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("AND CAMP.CAMPAIGN_NAME = :campaignName ");
				queryCondition.setObject("campaignName", inputVO.getCampaignName());
			} else {
				sb.append("AND CAMP.CAMPAIGN_NAME like :campaignName ");
				queryCondition.setObject("campaignName", "%" + inputVO.getCampaignName() + "%");
			}
		}
		
		// 查詢條件：活動起日-活動訖日
		if (inputVO.getCamp_sDate() != null) {
			sb.append("AND CAMP.START_DATE >= TRUNC(:camp_sdate) ");
			queryCondition.setObject("camp_sdate", inputVO.getCamp_sDate());
		}	
		
		if (inputVO.getCamp_eDate() != null) {
			sb.append("AND CAMP.START_DATE < TRUNC(:camp_edate) + 1 ");
			queryCondition.setObject("camp_edate", inputVO.getCamp_eDate());
		}
		
		if (inputVO.getCamp_esDate() != null) {
			sb.append("AND CAMP.END_DATE >= TRUNC(:camp_ssdate) ");
			queryCondition.setObject("camp_ssdate", inputVO.getCamp_esDate());
		}
		
		if (inputVO.getCamp_eeDate() != null) {
			sb.append("AND CAMP.END_DATE < TRUNC(:camp_eedate) + 1 ");
			queryCondition.setObject("camp_eedate", inputVO.getCamp_eeDate());
		}
		
		// 查詢限制筆數
		Map<String, String> max_qry_rows_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
		
		String max_qry_rows = "";
		for (Object key : max_qry_rows_xml.keySet()) {
			max_qry_rows = key.toString();
        }
		
		sb.append("AND ROWNUM <= :max_qry_rows ");
		queryCondition.setObject("max_qry_rows", max_qry_rows);

		// 排序
		sb.append("ORDER BY LEADS.END_DATE, ");
		sb.append("         DECODE(DEFN.REGION_CENTER_ID, '000', 999, 0), ");
		sb.append("         DECODE(REPLACE(REPLACE(REPLACE(DEFN.REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
		sb.append("         DEFN.REGION_CENTER_NAME, ");
		sb.append("         DECODE(DEFN.BRANCH_AREA_ID, NULL, 999, 0), ");
		sb.append("         DEFN.BRANCH_AREA_NAME, ");
		sb.append("         DECODE(DEFN.BRANCH_NBR, NULL, 999, 0), ");
		sb.append("         DEFN.BRANCH_NAME, ");
		sb.append("         LEADS.CUST_ID, LEADS.AO_CODE, LEADS.EMP_ID ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
    
    //理專下活動資訊
	public void queryAoData_1(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO outputVO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT  CAMPAIGN_ID, ");
		sql.append("        STEP_ID, ");
		sql.append("        CAMPAIGN_NAME, ");
		sql.append("        CAMPAIGN_DESC, ");
		sql.append("        START_DATE, ");
		sql.append("        TO_CHAR(END_DATE, 'yyyy-MM-dd') AS END_DATE, ");
		sql.append("        LEAD_TYPE, ");
		sql.append("        SUM (LEAD_TOT_CNT) AS TOTAL_COUNTS, ");
		sql.append("        SUM (LEAD_COLSE_CNT) AS CLOSE_COUNTS ");
		sql.append("FROM ( ");
		sql.append("  SELECT DISTINCT ");
		sql.append("         LEADS.CAMPAIGN_ID, ");
		sql.append("         LEADS.STEP_ID, ");
		sql.append("         CAMP.CAMPAIGN_NAME, ");
		sql.append("         CAMP.CAMPAIGN_DESC, ");
		sql.append("         CAMP.START_DATE, ");
		sql.append("         CAMP.END_DATE, ");
		sql.append("         CAMP.LEAD_TYPE, ");
		sql.append("         LEADS.CUST_ID, ");
		sql.append("         1 AS LEAD_TOT_CNT, ");
		sql.append("         CASE WHEN LEADS.LEAD_STATUS >= '03' THEN 1 ELSE 0 END AS LEAD_COLSE_CNT ");
		sql.append("    FROM TBCAM_SFA_LEADS LEADS ");
		sql.append("    LEFT JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		sql.append("    WHERE NVL (LEADS.EMP_ID, ' ') <> ' ' ");
		sql.append("    AND LEADS.AO_CODE IS NOT NULL ");
		sql.append("    AND LEADS.LEAD_TYPE <> '04' ");
		sql.append("    AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		sql.append("    AND TRUNC(SYSDATE) <= TRUNC (LEADS.END_DATE + 10) ");
		
		// 20190830 add by ocean : 0006667 刪除名單後,主管帶分派名單沒有移除(祐傑)
		sql.append("    AND CAMP.REMOVE_FLAG = 'N' ");
		
		// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {		
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			}

			if (!StringUtils.isBlank(inputVO.getEmpId())) {
				sql.append("    AND LEADS.BRANCH_ID = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranchID());
			}
		} 
		
		if (!StringUtils.isBlank(inputVO.getEmpId())) {
			sql.append("    AND LEADS.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpId());
		}
		
		sql.append(") BASE ");
		sql.append("GROUP BY  CAMPAIGN_ID, ");
		sql.append("          STEP_ID, ");
		sql.append("          CAMPAIGN_NAME, ");
		sql.append("          CAMPAIGN_DESC, ");
		sql.append("          START_DATE, ");
		sql.append("          END_DATE, ");
		sql.append("          LEAD_TYPE ");
		sql.append("ORDER BY END_DATE DESC ");
		
		queryCondition.setQueryString(sql.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	//改派/作廢-下拉式選單(取得名單/待辦工作)
	public void queryToDoListData(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		
		//改派名單
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT b.CAMPAIGN_NAME, a.SFA_LEAD_ID || ',' || a.CAMPAIGN_ID || ',' || a.STEP_ID || ',' || b.LEAD_TYPE || ',' ||TO_CHAR(a.START_DATE, 'yyyyMMdd')||','||TO_CHAR(a.END_DATE, 'yyyyMMdd') AS LIST_DATA  ");
		sql.append("FROM TBCAM_SFA_LEADS a, TBCAM_SFA_CAMPAIGN b ");
		sql.append("WHERE 1 = 1 ");
		
		sql.append("AND TRUNC(a.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (!StringUtils.isBlank(inputVO.getEmpId())) {
			sql.append("AND a.EMP_ID = :empId ");
			queryCondition.setObject("empId", inputVO.getEmpId());
		}
		if (!StringUtils.isBlank(inputVO.getCustId())) {
			sql.append("AND a.CUST_ID = :custId ");
			queryCondition.setObject("custId", inputVO.getCustId());
		}
		sql.append("AND a.LEAD_STATUS  < '03' ");
		sql.append("AND a.CAMPAIGN_ID = b.CAMPAIGN_ID ");
		sql.append("AND a.STEP_ID = b.STEP_ID ");
		sql.append("AND TO_CHAR(a.End_Date+10, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));
			
		//作廢名單
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT b.CAMPAIGN_NAME, a.SFA_LEAD_ID || ',' || a.CAMPAIGN_ID || ',' || a.STEP_ID || ',' || b.LEAD_TYPE || ',' || TO_CHAR(a.START_DATE, 'yyyyMMdd') || ',' || TO_CHAR(a.END_DATE, 'yyyyMMdd') AS LIST_DATA ");
		sql.append("FROM TBCAM_SFA_LEADS a, TBCAM_SFA_CAMPAIGN b ");
		sql.append("LEFT JOIN TBCAM_SFA_LEADS_IMP imp ON b.CAMPAIGN_ID = imp.CAMPAIGN_ID AND b.STEP_ID = imp.STEP_ID ");
		sql.append("WHERE 1 = 1 ");
		
		sql.append("AND TRUNC(a.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (!StringUtils.isBlank(inputVO.getEmpId())) {
			sql.append("AND a.EMP_ID = :empId ");
			queryCondition.setObject("empId", inputVO.getEmpId());
		}
		if (!StringUtils.isBlank(inputVO.getCustId())) {
			sql.append("AND a.CUST_ID = :custId ");
			queryCondition.setObject("custId", inputVO.getCustId());
		}
		sql.append("AND a.LEAD_STATUS < '03' ");
		sql.append("AND a.CAMPAIGN_ID = b.CAMPAIGN_ID ");
		sql.append("AND a.STEP_ID = b.STEP_ID ");
		sql.append("AND TO_CHAR(a.End_Date+10, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		sql.append("AND imp.CREATOR = :loginID ");
		
		queryCondition.setObject("loginID", ws.getUser().getUserID());
		
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList2(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(return_VO);
	}
	
	//改派/作廢-下拉式選單(改派人員)
	public void queryAoData_3(Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("WITH EMP_DTL AS ( ");

		switch (inputVO.getChannel()) {
			case "004AO":
				sql.append("  SELECT DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, MEM.EMP_ID, MEM.EMP_NAME, PAO.EMP_ID AS AO_CODE ");
				sql.append("  FROM TBORG_PAO PAO ");
				sql.append("  LEFT JOIN TBORG_MEMBER MEM ON PAO.EMP_ID = MEM.EMP_ID ");
				sql.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON PAO.BRANCH_NBR = DEFN.BRANCH_NBR ");
				sql.append("  WHERE 1 = 1 ");
				sql.append("  AND DEFN.BRANCH_NAME = :branchName ");
				
				break;
			default:
				sql.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, NVL(AO_CODE, EMP_ID) AS AO_CODE, ROLE_ID ");
				sql.append("  FROM VWORG_EMP_INFO INFO ");
				sql.append("  WHERE PRIVILEGEID = :channel ");
				sql.append("  AND BRANCH_NAME = :branchName ");
				
				// 20200602 ADD BY OCEAN : 0000166: WMS-CR-20200226-01_高端業管功能改採兼任分行角色調整相關功能_登入底層+行銷模組
				sql.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = INFO.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());

				break;
		}
		sql.append("), ");

		sql.append("EMP_LEADS AS ( ");
		sql.append("  SELECT AO_CODE, EMP_ID, COUNT(AO_CODE) AS COUNTS ");
		sql.append("  FROM TBCAM_SFA_LEADS ");
		sql.append("  WHERE LEAD_STATUS < '03' ");
		sql.append("  AND ((TO_CHAR(END_DATE + 10, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd'))) ");
		sql.append("  AND TRUNC(START_DATE) <= TRUNC(SYSDATE) ");
		sql.append("  GROUP BY AO_CODE, EMP_ID ");
		sql.append(") ");
		
		sql.append("SELECT NVL(ED.AO_CODE, ED.EMP_ID) || '-' || ED.EMP_NAME || '(' || NVL(EL.COUNTS, 0) || ')' AS AO_LABEL, NVL(ED.AO_CODE, ED.EMP_ID) || ',' || ED.EMP_ID || ',' || :channel AS AO_DATA ");
		sql.append("FROM EMP_DTL ED ");
		sql.append("LEFT JOIN EMP_LEADS EL ON ED.AO_CODE = EL.AO_CODE AND ED.EMP_ID = EL.EMP_ID ");

		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("branchName", inputVO.getBranch_Name());
		queryCondition.setObject("channel", inputVO.getChannel());
		
		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}
	
	//執行改派
	public void updLead(Object body, IPrimitiveMap header) throws Exception {
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM210InputVO inputVO = (CAM210InputVO) body;
		StringBuffer sb = new StringBuffer();
		
		String aoList = inputVO.getAoList();
		String custId = inputVO.getCustId();
		String branchNbr = inputVO.getBranchNbr();
		String toDoList = inputVO.getToDoList();
		
		/**toDoListData [0~5]
		 * 0:SFA_LEAD_ID,1:CAMPAIGN_ID,2:STEP_ID
		 * 3:LEAD_TYPE,4:START_DATE,5:END_DATE
		 */
		String [] toDoListData = toDoList.split(",");
		long StartDate = Long.parseLong(toDoListData[4]) ;
		long EndDate = Long.parseLong(toDoListData[5]) ;
		
		/**aoData [0~2]
		 * 0:AO_CODE,1:EMP_ID,2:CHANNEL
		 */
		String [] aoData = aoList.split(",");

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.CHANNEL_MAPPING' AND PARAM_CODE = :channel ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("channel", inputVO.getChannel());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("CUST_ID", custId);
		tempMap.put("AO_CODE", aoData[0]);
		tempMap.put("BRANCH_ID", branchNbr);
		tempMap.put("EMP_ID", aoData[1]);
		tempMap.put("SFA_LEAD_ID", toDoListData[0]);
		tempMap.put("LEAD_TYPE", toDoListData[3]);
		
		Map<String, Object> campMap = new HashMap<String, Object>();
		campMap.put("CAMPAIGN_ID", toDoListData[1]);
		campMap.put("STEP_ID", toDoListData[2]);
		campMap.put("LEAD_TYPE", toDoListData[3]);
		campMap.put("START_DATE",new Timestamp(StartDate));
		campMap.put("END_DATE", new Timestamp(EndDate));
		
		CAM998 cam998 = new CAM998();
		cam998.updLead(dam, ws.getUser().getUserID(), "20", toDoListData[1], (String) list.get(0).get("PARAM_NAME"), campMap, tempMap);
		
		this.sendRtnObject(null);
	}
	
	//執行一次性改派
	public void reSetLeads (Object body, IPrimitiveMap header) throws Exception {
		
		WorkStation ws = DataManager.getWorkStation(uuid);

		CAM210InputVO inputVO = (CAM210InputVO) body;
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// aoData [0~2] => 0:AO_CODE,1:EMP_ID,2:CHANNEL
		String [] aoData = inputVO.getAoList().split(",");
		
		// CHANNEL
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.CHANNEL_MAPPING' AND PARAM_CODE = :channel ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("channel", inputVO.getChannel());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		for (Map<String, Object> map : inputVO.getReSetLeList()) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("CUST_ID", map.get("CUST_ID"));
			tempMap.put("AO_CODE", aoData[0]);
			tempMap.put("BRANCH_ID", inputVO.getBranchNbr());
			tempMap.put("EMP_ID", aoData[1]);
			tempMap.put("SFA_LEAD_ID", map.get("SFA_LEAD_ID"));
			tempMap.put("LEAD_TYPE", map.get("LEAD_TYPE"));
						
			Map<String, Object> campMap = new HashMap<String, Object>();
			campMap.put("CAMPAIGN_ID", map.get("CAMPAIGN_ID"));
			campMap.put("STEP_ID", map.get("STEP_ID"));
			campMap.put("LEAD_TYPE", map.get("LEAD_TYPE"));
			campMap.put("START_DATE", Timestamp.valueOf(map.get("START_DATE") + " 00:00:00"));
			campMap.put("END_DATE", Timestamp.valueOf(map.get("END_DATE") + " 00:00:00"));
			
			CAM998 cam998 = new CAM998();
			cam998.updLead(dam, ws.getUser().getUserID(), "20", (String) map.get("CAMPAIGN_ID"), (String) list.get(0).get("PARAM_NAME"), campMap, tempMap);
		}
		
		this.sendRtnObject(null);
	}
		
	//執行作廢
	public void invalidLead(Object body, IPrimitiveMap header) throws Exception {
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		
		String reason = inputVO.getReason();
		String otherReason = inputVO.getOtherReason();
		String toDoList = inputVO.getToDoList();
		
		String [] toDoListData = toDoList.split(",");
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("SFA_LEAD_ID", toDoListData[0]);
		tempMap.put("REASON", ("其他".equals(reason) ? otherReason : reason));
		
		CAM998 cam998 = new CAM998();
		cam998.updLead(dam, ws.getUser().getUserID(), "10", null, null, null, tempMap);
		this.sendRtnObject(null);
	}
		
	public String vwcam_campaign_emp_stat_X4 (String leadType, String loginRole, String memLoginFlag, Map<String, String> headmgrMap) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH LEADS AS ( ");
		sb.append("  SELECT LEAD.LASTUPDATE, ");
		sb.append("         LEAD.LEAD_STATUS, ");
		sb.append("         CAMP.CAMPAIGN_ID, ");
		sb.append("         CAMP.STEP_ID, ");
		sb.append("         CAMP.CAMPAIGN_NAME, ");
		sb.append("         CAMP.CAMPAIGN_DESC, ");
		sb.append("         CAMP.START_DATE, ");
		sb.append("         CAMP.END_DATE, ");
		sb.append("         LEAD.CUST_ID, ");
		sb.append("         LEAD.EMP_ID, ");
		sb.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEAD.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sb.append("         MEM.JOB_TITLE_NAME, ");
		sb.append("         RC.DEPT_ID AS REGION_CENTER_ID, ");
		sb.append("         RC.DEPT_NAME AS REGION_CENTER_NAME, ");
		sb.append("         OP.DEPT_ID AS BRANCH_AREA_ID, ");
		sb.append("         OP.DEPT_NAME AS BRANCH_AREA_NAME, ");
		sb.append("         BR.DEPT_ID AS BRANCH_NBR, ");
		sb.append("         BR.DEPT_NAME AS BRANCH_NAME, ");
		sb.append("         1 AS LEAD_COUNTS, ");
		sb.append("         CASE WHEN NVL (LEAD.AO_CODE, ' ') = ' ' THEN 1 ELSE 0 END AS LEAD_WAIT_COUNTS, ");
		sb.append("         CASE WHEN NVL (LEAD.AO_CODE, ' ') <> ' ' AND LEAD.LEAD_STATUS >= '03' THEN 1 ELSE 0 END AS LEAD_CLOSE ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEAD ");
		sb.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEAD.BRANCH_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND LEAD.BRANCH_ID = BR.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND BR.PARENT_DEPT_ID = OP.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND OP.PARENT_DEPT_ID = RC.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON LEAD.EMP_ID = MEM.EMP_ID ");
		sb.append("  WHERE CAMP.CAMPAIGN_ID = LEAD.CAMPAIGN_ID ");
		sb.append("  AND CAMP.STEP_ID = LEAD.STEP_ID ");
		sb.append("  AND TO_CHAR (LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR (SYSDATE - 10, 'yyyyMMdd') ");
		sb.append("  AND LEAD.LEAD_STATUS <> 'TR' ");
		
		if ("04".equals(leadType))
			sb.append("  AND LEAD.LEAD_TYPE = '04' ");
		else
			sb.append("  AND LEAD.LEAD_TYPE <> '04' ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {		
			// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEAD.AO_CODE) ");
			} 
		} else {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
		}
	
		sb.append(") ");
		sb.append(", BASE AS ( ");
		sb.append("  SELECT L.CAMPAIGN_ID, ");
		sb.append("         L.STEP_ID, ");
		sb.append("         L.CAMPAIGN_NAME, ");
		sb.append("         L.CAMPAIGN_DESC, ");
		sb.append("         L.START_DATE, ");
		sb.append("         L.END_DATE, ");
		sb.append("         L.REGION_CENTER_ID, ");
		sb.append("         L.REGION_CENTER_NAME, ");
		sb.append("         L.BRANCH_AREA_ID, ");
		sb.append("         L.BRANCH_AREA_NAME, ");
		sb.append("         L.BRANCH_NBR, ");
		sb.append("         L.BRANCH_NAME, ");
		sb.append("         CASE WHEN SUM (LEAD_WAIT_COUNTS) > 0 THEN '-' ELSE L.EMP_ID END AS EMP_ID, ");
		sb.append("         CASE WHEN SUM (LEAD_WAIT_COUNTS) > 0 THEN '-' ELSE L.EMP_NAME END AS EMP_NAME, ");
		sb.append("         CASE WHEN SUM (LEAD_WAIT_COUNTS) > 0 THEN '主管待分派名單' ELSE L.JOB_TITLE_NAME END AS JOB_TITLE_NAME, ");
		sb.append("         SUM (LEAD_COUNTS) AS LEAD_COUNTS, ");
		sb.append("         SUM (LEAD_WAIT_COUNTS) AS LEAD_WAIT_COUNTS, ");
		sb.append("         SUM (LEAD_CLOSE) AS LEAD_CLOSE ");
		sb.append("  FROM LEADS L ");
		sb.append("  GROUP BY L.CAMPAIGN_ID, ");
		sb.append("           L.STEP_ID, ");
		sb.append("           L.CAMPAIGN_NAME, ");
		sb.append("           L.CAMPAIGN_DESC, ");
		sb.append("           L.START_DATE, ");
		sb.append("           L.END_DATE, ");
		sb.append("           L.REGION_CENTER_ID, ");
		sb.append("           L.REGION_CENTER_NAME, ");
		sb.append("           L.BRANCH_AREA_ID, ");
		sb.append("           L.BRANCH_AREA_NAME, ");
		sb.append("           L.BRANCH_NBR, ");
		sb.append("           L.BRANCH_NAME, ");
		sb.append("           L.EMP_ID, ");
		sb.append("           L.EMP_NAME, ");
		sb.append("           L.JOB_TITLE_NAME ");
		sb.append(") ");
		
		return sb.toString();	            
	}
	
	//即期活動執行現況By活動(營運區)
	public void sightActivities_opArea(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		XmlInfo xmlInfo = new XmlInfo();
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(vwcam_campaign_emp_stat_X4(inputVO.getLeadType(), (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2)));

		sql.append("SELECT CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, END_DATE, ");
		sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS EMP_ID, '' AS EMP_NAME, '' AS JOB_TITLE_NAME, ");
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, SUM(LEAD_WAIT_COUNTS) AS LEAD_WAIT_COUNTS, SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("FROM BASE ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND TRUNC(START_DATE) <= TRUNC(SYSDATE) ");
		sql.append("AND CAMPAIGN_ID = :campaignID ");
		sql.append("AND STEP_ID = :stepID ");
		
		queryCondition.setObject("campaignID", inputVO.getCampaignId());
		queryCondition.setObject("stepID", inputVO.getStepId());
		
		System.out.println("campaignID:"+ inputVO.getCampaignId());
		System.out.println("stepID:"+ inputVO.getStepId());
		System.out.println("campaignID:"+ inputVO.getOp());
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BRANCH_NBR = :branchID "); 
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     BASE.BRANCH_AREA_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = BASE.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");
				
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND REGION_CENTER_ID = :regionCenterID "); 
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} 
		} else {
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// 2017/9/1 ocean add ao_code
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, END_DATE, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME ");

		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
		
	//即期活動執行現況By活動(分行)
	public void sightActivities_branchList(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		XmlInfo xmlInfo = new XmlInfo();
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(vwcam_campaign_emp_stat_X4(inputVO.getLeadType(), (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2)));
		
		sql.append("SELECT CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, END_DATE, ");
		sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, JOB_TITLE_NAME, ");
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, SUM(LEAD_WAIT_COUNTS) AS LEAD_WAIT_COUNTS, SUM(LEAD_CLOSE) AS LEAD_CLOSE "); 
		sql.append("FROM BASE ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND TRUNC(START_DATE) <= TRUNC(SYSDATE) ");
		sql.append("AND CAMPAIGN_ID = :campaignID ");
		sql.append("AND STEP_ID = :stepID ");

		queryCondition.setObject("campaignID", inputVO.getCampaignId());
		queryCondition.setObject("stepID", inputVO.getStepId());
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     BASE.BRANCH_AREA_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = BASE.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");
				
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} 
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = BASE.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// 2017/9/1 ocean add ao_code
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, END_DATE, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, JOB_TITLE_NAME ");
		sql.append("ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID, JOB_TITLE_NAME ");
		
		queryCondition.setQueryString(sql.toString());
		
		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
		
	public String vwcam_camp_emp_cust_stat_X4(String leadType, String loginRole, String memLoginFlag, Map<String, String> headmgrMap) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH LEADS AS ( ");
		sb.append("  SELECT LEAD.LASTUPDATE, ");
		sb.append("         LEAD.LEAD_STATUS, ");
		sb.append("         RES.RESPONSE_NAME, ");
		sb.append("         CAMP.CAMPAIGN_ID, ");
		sb.append("         CAMP.STEP_ID, ");
		sb.append("         CAMP.CAMPAIGN_NAME, ");
		sb.append("         CAMP.CAMPAIGN_DESC, ");
		sb.append("         CAMP.START_DATE, ");
		sb.append("         CAMP.END_DATE, ");
		sb.append("         CAMP.LEAD_SOURCE_ID, ");
		sb.append("         LEAD.CUST_ID, ");
		sb.append("         CM.CUST_NAME, ");
		sb.append("         LEAD.AO_CODE, ");
		sb.append("         LEAD.EMP_ID, ");
		sb.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEAD.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sb.append("         MEM.JOB_TITLE_NAME, ");
		sb.append("         RC.DEPT_ID AS REGION_CENTER_ID, ");
		sb.append("         RC.DEPT_NAME AS REGION_CENTER_NAME, ");
		sb.append("         OP.DEPT_ID AS BRANCH_AREA_ID, ");
		sb.append("         OP.DEPT_NAME AS BRANCH_AREA_NAME, ");
		sb.append("         BR.DEPT_ID AS BRANCH_NBR, ");
		sb.append("         BR.DEPT_NAME AS BRANCH_NAME, ");
		sb.append("			CASE WHEN LEAD.LEAD_TYPE <> '04' THEN 1 ELSE 1 END AS LEAD_COUNTS, ");
		sb.append("			CASE WHEN LEAD.LEAD_TYPE <> '04' AND LEAD.LEAD_STATUS >= '03' THEN 1 ");
		sb.append("				 WHEN LEAD.LEAD_TYPE = '04' THEN 1 ");
		sb.append("         ELSE 0 END AS LEAD_CLOSE ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEAD ");
		sb.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEAD.BRANCH_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND LEAD.BRANCH_ID = BR.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND BR.PARENT_DEPT_ID = OP.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND OP.PARENT_DEPT_ID = RC.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON LEAD.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST CM ON LEAD.CUST_ID = CM.CUST_ID ");
		sb.append("  , TBCAM_SFA_CAMP_RESPONSE RES ");
		sb.append("  WHERE CAMP.CAMPAIGN_ID = LEAD.CAMPAIGN_ID ");
		sb.append("  AND CAMP.STEP_ID = LEAD.STEP_ID ");
		sb.append("  AND ( ");
		sb.append("    (RES.CAMPAIGN_ID = CAMP.CAMPAIGN_ID OR RES.CAMPAIGN_ID = CAMP.LEAD_RESPONSE_CODE) ");
		sb.append("    AND LEAD.LEAD_STATUS = RES.LEAD_STATUS ");
		sb.append("  ) ");
		sb.append("  AND TO_CHAR (LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR (SYSDATE - 10, 'yyyyMMdd') ");
		sb.append("  AND LEAD.LEAD_STATUS <> 'TR' ");
		sb.append("  AND LEAD.AO_CODE IS NOT NULL ");
		
		if ("04".equals(leadType))
			sb.append("  AND LEAD.LEAD_TYPE = '04' ");
		else
			sb.append("  AND LEAD.LEAD_TYPE <> '04' ");
		
		// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {		
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEAD.AO_CODE) ");
			}
		} else {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
		}
		
		sb.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		sb.append(") ");
		sb.append(", BASE AS ( ");
		sb.append("  SELECT L.CAMPAIGN_ID, ");
		sb.append("         L.STEP_ID, ");
		sb.append("         L.CAMPAIGN_NAME, ");
		sb.append("         L.CAMPAIGN_DESC, ");
		sb.append("         L.START_DATE, ");
		sb.append("         L.END_DATE, ");
		sb.append("         L.REGION_CENTER_ID, ");
		sb.append("         L.REGION_CENTER_NAME, ");
		sb.append("         L.BRANCH_AREA_ID, ");
		sb.append("         L.BRANCH_AREA_NAME, ");
		sb.append("         L.BRANCH_NBR, ");
		sb.append("         L.BRANCH_NAME, ");
		sb.append("         L.AO_CODE, ");
		sb.append("         L.EMP_ID, ");
		sb.append("         L.EMP_NAME, ");
		sb.append("         L.JOB_TITLE_NAME, ");
		sb.append("         L.CUST_ID, ");
		sb.append("         L.CUST_NAME, ");
		sb.append("         L.LEAD_STATUS, ");
		sb.append("         L.RESPONSE_NAME, ");
		sb.append("         SUM (LEAD_COUNTS) AS LEAD_COUNTS, ");
		sb.append("         SUM (LEAD_CLOSE) AS LEAD_CLOSE ");
		sb.append("  FROM LEADS L ");
		sb.append("  GROUP BY L.CAMPAIGN_ID, ");
		sb.append("          L.STEP_ID, ");
		sb.append("          L.CAMPAIGN_NAME, ");
		sb.append("          L.CAMPAIGN_DESC, ");
		sb.append("          L.START_DATE, ");
		sb.append("          L.END_DATE, ");
		sb.append("          L.REGION_CENTER_ID, ");
		sb.append("          L.REGION_CENTER_NAME, ");
		sb.append("          L.BRANCH_AREA_ID, ");
		sb.append("          L.BRANCH_AREA_NAME, ");
		sb.append("          L.BRANCH_NBR, ");
		sb.append("          L.BRANCH_NAME, ");
		sb.append("          L.AO_CODE, ");
		sb.append("          L.EMP_ID, ");
		sb.append("          L.EMP_NAME, ");
		sb.append("          L.JOB_TITLE_NAME, ");
		sb.append("          L.CUST_ID, ");
		sb.append("          L.CUST_NAME, ");
		sb.append("          L.LEAD_STATUS, ");
		sb.append("          L.RESPONSE_NAME ");
		sb.append(") ");
		
		return sb.toString();
	}
	
	//即期活動執行現況 By 理專/同仁
	public void sightActivities_aoList(Object body, IPrimitiveMap header) throws JBranchException, ParseException {		
	
		XmlInfo xmlInfo = new XmlInfo();
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(vwcam_camp_emp_cust_stat_X4(inputVO.getLeadType(), (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2)));

		sql.append("SELECT CAMPAIGN_ID, ");
		sql.append("       STEP_ID, ");
		sql.append("       CAMPAIGN_NAME, ");
		sql.append("       CAMPAIGN_DESC, ");
		sql.append("       END_DATE, ");
		sql.append("       REGION_CENTER_ID, ");
		sql.append("       REGION_CENTER_NAME, ");
		sql.append("       BRANCH_AREA_ID, ");
		sql.append("       BRANCH_AREA_NAME, ");
		sql.append("       BRANCH_NBR, ");
		sql.append("       BRANCH_NAME, ");
		sql.append("       EMP_ID, ");
		sql.append("       EMP_NAME, ");
		sql.append("       JOB_TITLE_NAME, ");
		sql.append("       CUST_ID, ");
		sql.append("       CUST_NAME, ");
		sql.append("       LEAD_STATUS, ");
		sql.append("       RESPONSE_NAME, ");
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, ");
		sql.append("       SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("FROM BASE ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND TRUNC(START_DATE) <= TRUNC(SYSDATE) ");
		sql.append("AND CAMPAIGN_ID = :campaignID ");
		sql.append("AND STEP_ID = :stepID ");
		sql.append("AND EMP_ID = :empID ");
		
		queryCondition.setObject("campaignID", inputVO.getCampaignId());
		queryCondition.setObject("stepID", inputVO.getStepId());
		queryCondition.setObject("empID", inputVO.getEmpId());
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     BASE.BRANCH_AREA_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = BASE.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");
				
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} 
		} else {
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// 2017/9/1 ocean add ao_code
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, ");
		sql.append("         CAMPAIGN_NAME, ");
		sql.append("         CAMPAIGN_DESC, ");
		sql.append("         END_DATE, ");
		sql.append("         REGION_CENTER_ID, ");
		sql.append("         REGION_CENTER_NAME, ");
		sql.append("         BRANCH_AREA_ID, ");
		sql.append("         BRANCH_AREA_NAME, ");
		sql.append("         BRANCH_NBR, ");
		sql.append("         BRANCH_NAME, ");
		sql.append("         EMP_ID, ");
		sql.append("         EMP_NAME, ");
		sql.append("         JOB_TITLE_NAME, ");
		sql.append("         CUST_ID, ");
		sql.append("         CUST_NAME, ");
		sql.append("         LEAD_STATUS, ");
		sql.append("         RESPONSE_NAME ");
		
		queryCondition.setQueryString(sql.toString());
		
		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}	
		
	public String vwcam_campaign_emp_stat_exp_X4(String leadType, String loginRole, String memLoginFlag, Map<String, String> headmgrMap) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH LEADS AS ( ");
		sb.append("  SELECT LEAD.LASTUPDATE, ");
		sb.append("         LEAD.LEAD_STATUS, ");
		sb.append("         CAMP.CAMPAIGN_ID, ");
		sb.append("         CAMP.STEP_ID, ");
		sb.append("         CAMP.CAMPAIGN_NAME, ");
		sb.append("         CAMP.CAMPAIGN_DESC, ");
		sb.append("         CAMP.END_DATE, ");
		sb.append("         LEAD.CUST_ID, ");
		sb.append("         LEAD.EMP_ID, ");
		sb.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEAD.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sb.append("         MEM.JOB_TITLE_NAME, ");
		sb.append("         RC.DEPT_ID AS REGION_CENTER_ID, ");
		sb.append("         RC.DEPT_NAME AS REGION_CENTER_NAME, ");
		sb.append("         OP.DEPT_ID AS BRANCH_AREA_ID, ");
		sb.append("         OP.DEPT_NAME AS BRANCH_AREA_NAME, ");
		sb.append("         BR.DEPT_ID AS BRANCH_NBR, ");
		sb.append("         BR.DEPT_NAME AS BRANCH_NAME, ");
		sb.append("         1 AS LEAD_COUNTS, ");
		sb.append("         CASE WHEN LEAD.LEAD_STATUS >= '03' THEN 1 ELSE 0 END AS LEAD_CLOSE, ");
		sb.append("         CASE WHEN NVL (LEAD.AO_CODE, ' ') = ' ' THEN 1 ELSE 0 END AS LEAD_WAIT_COUNTS ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEAD ");
		sb.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEAD.BRANCH_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND LEAD.BRANCH_ID = BR.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND BR.PARENT_DEPT_ID = OP.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND OP.PARENT_DEPT_ID = RC.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON LEAD.EMP_ID = MEM.EMP_ID ");
		sb.append("  WHERE CAMP.CAMPAIGN_ID = LEAD.CAMPAIGN_ID ");
		sb.append("  AND CAMP.STEP_ID = LEAD.STEP_ID ");
		sb.append("  AND TO_CHAR (LEAD.END_DATE, 'yyyyMMdd') < TO_CHAR (SYSDATE - 10, 'yyyyMMdd') ");
		sb.append("  AND LEAD.LEAD_STATUS <> 'TR' ");
		
		if ("04".equals(leadType))
			sb.append("  AND LEAD.LEAD_TYPE = '04' ");
		else
			sb.append("  AND LEAD.LEAD_TYPE <> '04' ");
		
		// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {		
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEAD.AO_CODE) ");
			} 
		} else {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
		}
		
		sb.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		sb.append(") ");
		sb.append(", BASE AS ( ");
		sb.append("  SELECT L.CAMPAIGN_ID, ");
		sb.append("         L.STEP_ID, ");
		sb.append("         L.CAMPAIGN_NAME, ");
		sb.append("         L.CAMPAIGN_DESC, ");
		sb.append("         L.END_DATE, ");
		sb.append("         L.REGION_CENTER_ID, ");
		sb.append("         L.REGION_CENTER_NAME, ");
		sb.append("         L.BRANCH_AREA_ID, ");
		sb.append("         L.BRANCH_AREA_NAME, ");
		sb.append("         L.BRANCH_NBR, ");
		sb.append("         L.BRANCH_NAME, ");
		sb.append("         CASE WHEN SUM (LEAD_WAIT_COUNTS) > 0 THEN '-' ELSE L.EMP_ID END AS EMP_ID, ");
		sb.append("         CASE WHEN SUM (LEAD_WAIT_COUNTS) > 0 THEN '-' ELSE L.EMP_NAME END AS EMP_NAME, ");
		sb.append("         CASE WHEN SUM (LEAD_WAIT_COUNTS) > 0 THEN '主管待分派名單' ELSE L.JOB_TITLE_NAME END AS JOB_TITLE_NAME, ");
		sb.append("         SUM(LEAD_WAIT_COUNTS) AS LEAD_WAIT_COUNTS, ");
		sb.append("         SUM (LEAD_COUNTS) AS LEAD_COUNTS, ");
		sb.append("         SUM (LEAD_CLOSE) AS LEAD_CLOSE ");
		sb.append("  FROM LEADS L ");
		sb.append("  GROUP BY L.CAMPAIGN_ID, ");
		sb.append("           L.STEP_ID, ");
		sb.append("           L.CAMPAIGN_NAME, ");
		sb.append("           L.CAMPAIGN_DESC, ");
		sb.append("           L.END_DATE, ");
		sb.append("           L.REGION_CENTER_ID, ");
		sb.append("           L.REGION_CENTER_NAME, ");
		sb.append("           L.BRANCH_AREA_ID, ");
		sb.append("           L.BRANCH_AREA_NAME, ");
		sb.append("           L.BRANCH_NBR, ");
		sb.append("           L.BRANCH_NAME, ");
		sb.append("           L.EMP_ID, ");
		sb.append("           L.EMP_NAME, ");
		sb.append("           L.JOB_TITLE_NAME ");
		sb.append(") ");
		
		return sb.toString();
	}
	
	//已過期活動執行現況By活動(營運區)
	public void expiredActivities_opArea(Object body, IPrimitiveMap header) throws JBranchException, ParseException {		
		
		XmlInfo xmlInfo = new XmlInfo();
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(vwcam_campaign_emp_stat_exp_X4(inputVO.getLeadType(), (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2)));

		sql.append("SELECT CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, END_DATE, ");
		sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS EMP_ID, '' AS EMP_NAME, '' AS JOB_TITLE_NAME, ");
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, SUM(LEAD_WAIT_COUNTS) AS LEAD_WAIT_COUNTS, SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("FROM BASE ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND CAMPAIGN_ID = :campaignID ");
		sql.append("AND STEP_ID = :stepID ");
		
		queryCondition.setObject("campaignID", inputVO.getCampaignId());
		queryCondition.setObject("stepID", inputVO.getStepId());
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BRANCH_NBR = :branchID "); 
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     BASE.BRANCH_AREA_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = BASE.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");
				
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND REGION_CENTER_ID = :regionCenterID "); 
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} 
		} else {
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// 2017/9/1 ocean add ao_code
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, END_DATE, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME ");
		sql.append("ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID, JOB_TITLE_NAME ");

		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
		
	//已過期活動執行現況By活動(分行)
	public void expiredActivities_branchList(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		XmlInfo xmlInfo = new XmlInfo();
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(vwcam_campaign_emp_stat_exp_X4(inputVO.getLeadType(), (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2)));

		sql.append("SELECT CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, END_DATE, ");
		sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, JOB_TITLE_NAME, ");
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, SUM(LEAD_WAIT_COUNTS) AS LEAD_WAIT_COUNTS, SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("FROM BASE ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND CAMPAIGN_ID = :campaignID ");
		sql.append("AND STEP_ID = :stepID ");

		queryCondition.setObject("campaignID", inputVO.getCampaignId());
		queryCondition.setObject("stepID", inputVO.getStepId());
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     BASE.BRANCH_AREA_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = BASE.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");
				
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} 
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = BASE.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// 2017/9/1 ocean add ao_code
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sql.append("GROUP BY CAMPAIGN_ID, STEP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, END_DATE, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, JOB_TITLE_NAME ");
		sql.append("ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID, JOB_TITLE_NAME ");

		queryCondition.setQueryString(sql.toString());
		
		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
	
	public String vwcam_camp_emp_cust_stat_exp_X4(String leadType, String loginRole, String memLoginFlag, Map<String, String> headmgrMap) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH LEADS AS ( ");
		sb.append("  SELECT LEAD.LASTUPDATE, ");
		sb.append("         LEAD.LEAD_STATUS, ");
		sb.append("         RES.RESPONSE_NAME, ");
		sb.append("         CAMP.CAMPAIGN_ID, ");
		sb.append("         CAMP.STEP_ID, ");
		sb.append("         CAMP.CAMPAIGN_NAME, ");
		sb.append("         CAMP.CAMPAIGN_DESC, ");
		sb.append("         CAMP.END_DATE, ");
		sb.append("         CAMP.LEAD_SOURCE_ID, ");
		sb.append("         LEAD.CUST_ID, ");
		sb.append("         CM.CUST_NAME, ");
		sb.append("         LEAD.AO_CODE, ");
		sb.append("         LEAD.EMP_ID, ");
		sb.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEAD.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sb.append("         MEM.JOB_TITLE_NAME, ");
		sb.append("         RC.DEPT_ID AS REGION_CENTER_ID, ");
		sb.append("         RC.DEPT_NAME AS REGION_CENTER_NAME, ");
		sb.append("         OP.DEPT_ID AS BRANCH_AREA_ID, ");
		sb.append("         OP.DEPT_NAME AS BRANCH_AREA_NAME, ");
		sb.append("         BR.DEPT_ID AS BRANCH_NBR, ");
		sb.append("         BR.DEPT_NAME AS BRANCH_NAME, ");
		sb.append("         1 AS LEAD_COUNTS, ");
		sb.append("         CASE WHEN LEAD.LEAD_STATUS >= '03' THEN 1 ELSE 0 END AS LEAD_CLOSE ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEAD ");
		sb.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEAD.BRANCH_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND LEAD.BRANCH_ID = BR.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND BR.PARENT_DEPT_ID = OP.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND OP.PARENT_DEPT_ID = RC.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON LEAD.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST CM ON LEAD.CUST_ID = CM.CUST_ID ");
		sb.append("  , TBCAM_SFA_CAMP_RESPONSE RES ");
		sb.append("  WHERE CAMP.CAMPAIGN_ID = LEAD.CAMPAIGN_ID ");
		sb.append("  AND CAMP.STEP_ID = LEAD.STEP_ID ");
		sb.append("  AND ( ");
		sb.append("    (RES.CAMPAIGN_ID = CAMP.CAMPAIGN_ID OR RES.CAMPAIGN_ID = CAMP.LEAD_RESPONSE_CODE) ");
		sb.append("    AND LEAD.LEAD_STATUS = RES.LEAD_STATUS ");
		sb.append("  ) ");
		sb.append("  AND TO_CHAR (LEAD.END_DATE, 'yyyyMMdd') < TO_CHAR (SYSDATE - 10, 'yyyyMMdd') ");
		sb.append("  AND LEAD.LEAD_STATUS <> 'TR' ");
		sb.append("  AND LEAD.AO_CODE IS NOT NULL ");
		
		if ("04".equals(leadType))
			sb.append("  AND LEAD.LEAD_TYPE = '04' ");
		else
			sb.append("  AND LEAD.LEAD_TYPE <> '04' ");
		
		// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {		
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEAD.AO_CODE) ");
			} 
		} else {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
		}
		
		sb.append(") ");
		sb.append(", BASE AS ( ");
		sb.append("  SELECT L.CAMPAIGN_ID, ");
		sb.append("         L.STEP_ID, ");
		sb.append("         L.CAMPAIGN_NAME, ");
		sb.append("         L.CAMPAIGN_DESC, ");
		sb.append("         L.END_DATE, ");
		sb.append("         L.REGION_CENTER_ID, ");
		sb.append("         L.REGION_CENTER_NAME, ");
		sb.append("         L.BRANCH_AREA_ID, ");
		sb.append("         L.BRANCH_AREA_NAME, ");
		sb.append("         L.BRANCH_NBR, ");
		sb.append("         L.BRANCH_NAME, ");
		sb.append("         L.AO_CODE, ");
		sb.append("         L.EMP_ID, ");
		sb.append("         L.EMP_NAME, ");
		sb.append("         L.JOB_TITLE_NAME, ");
		sb.append("         L.CUST_ID, ");
		sb.append("         L.CUST_NAME, ");
		sb.append("         L.LEAD_STATUS, ");
		sb.append("         L.RESPONSE_NAME, ");
		sb.append("         SUM (LEAD_COUNTS) AS LEAD_COUNTS, ");
		sb.append("         SUM (LEAD_CLOSE) AS LEAD_CLOSE ");
		sb.append("  FROM LEADS L ");
		sb.append("  GROUP BY L.CAMPAIGN_ID, ");
		sb.append("           L.STEP_ID, ");
		sb.append("           L.CAMPAIGN_NAME, ");
		sb.append("           L.CAMPAIGN_DESC, ");
		sb.append("           L.END_DATE, ");
		sb.append("           L.REGION_CENTER_ID, ");
		sb.append("           L.REGION_CENTER_NAME, ");
		sb.append("           L.BRANCH_AREA_ID, ");
		sb.append("           L.BRANCH_AREA_NAME, ");
		sb.append("           L.BRANCH_NBR, ");
		sb.append("           L.BRANCH_NAME, ");
		sb.append("           L.AO_CODE, ");
		sb.append("           L.EMP_ID, ");
		sb.append("           L.EMP_NAME, ");
		sb.append("           L.JOB_TITLE_NAME, ");
		sb.append("           L.CUST_ID, ");
		sb.append("           L.CUST_NAME, ");
		sb.append("           L.LEAD_STATUS, ");
		sb.append("           L.RESPONSE_NAME ");
		sb.append(") ");
											
		return sb.toString();
	}
	
	//已過期活動執行現況 By 理專/同仁
	public void expiredActivities_aoList(Object body, IPrimitiveMap header) throws JBranchException, ParseException {		
		
		XmlInfo xmlInfo = new XmlInfo();
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		CAM210OutputVO return_VO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(vwcam_camp_emp_cust_stat_exp_X4(inputVO.getLeadType(), (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2)));

		sql.append("SELECT CAMPAIGN_ID, ");
		sql.append("       STEP_ID, ");
		sql.append("       CAMPAIGN_NAME, ");
		sql.append("       CAMPAIGN_DESC, ");
		sql.append("       END_DATE, ");
		sql.append("       REGION_CENTER_ID, ");
		sql.append("       REGION_CENTER_NAME, ");
		sql.append("       BRANCH_AREA_ID, ");
		sql.append("       BRANCH_AREA_NAME, ");
		sql.append("       BRANCH_NBR, ");
		sql.append("       BRANCH_NAME, ");
		sql.append("       EMP_ID, ");
		sql.append("       EMP_NAME, ");
		sql.append("       JOB_TITLE_NAME, ");
		sql.append("       CUST_ID, ");
		sql.append("       CUST_NAME, ");
		sql.append("       LEAD_STATUS, ");
		sql.append("       RESPONSE_NAME, ");
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, SUM(LEAD_CLOSE) AS LEAD_CLOSE ");
		sql.append("FROM BASE ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND CAMPAIGN_ID = :campaignID ");
		sql.append("AND STEP_ID = :stepID ");
		sql.append("AND EMP_ID = :empID ");
		
		queryCondition.setObject("campaignID", inputVO.getCampaignId());
		queryCondition.setObject("stepID", inputVO.getStepId());
		queryCondition.setObject("empID", inputVO.getEmpId());
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("AND BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("AND ( ");
				sql.append("     BASE.BRANCH_AREA_ID = :branchAreaID ");
				sql.append("  OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = BASE.EMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
				sql.append(") ");
				
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} 
		} else {
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		// 2017/9/1 ocean add ao_code
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sql.append("AND EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sql.append("GROUP BY CAMPAIGN_ID, ");
		sql.append("         STEP_ID, ");
		sql.append("         CAMPAIGN_NAME, ");
		sql.append("         CAMPAIGN_DESC, ");
		sql.append("         END_DATE, ");
		sql.append("         REGION_CENTER_ID, ");
		sql.append("         REGION_CENTER_NAME, ");
		sql.append("         BRANCH_AREA_ID, ");
		sql.append("         BRANCH_AREA_NAME, ");
		sql.append("         BRANCH_NBR, ");
		sql.append("         BRANCH_NAME, ");
		sql.append("         EMP_ID, ");
		sql.append("         EMP_NAME, ");
		sql.append("         JOB_TITLE_NAME, ");
		sql.append("         CUST_ID, ");
		sql.append("         CUST_NAME, ");
		sql.append("         LEAD_STATUS, ");
		sql.append("         RESPONSE_NAME ");

		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
		
	//取得  信用卡(CARD)、信貸(CREDIT_LOAN)、房貸(HOME_LOAN)、保險(INSURANCE)、投資(INVEST)  檢核月數
	public Map<String, String> getMonths() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PARAM_CODE , PARAM_NAME_EDIT FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.PRD_TRACKING_PERIOD'");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> data = dam.exeQuery(queryCondition);
		
		Map<String, String> Months = new HashMap<String, String>();
		
		for(int i = 0; i < data.size(); i++) {
			Months.put((String) data.get(i).get("PARAM_CODE"), (String) data.get(i).get("PARAM_NAME_EDIT"));
		}
		return Months;
	}
		
	//匯出報表
	public void exportReport1 (Object body, IPrimitiveMap header) throws JBranchException, IOException {
		
		XmlInfo xmlInfo = new XmlInfo();
    	Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setFetchSize(3000);
		StringBuffer sql = new StringBuffer();
		
		sql.append("WITH LEADS AS ( ");
		sql.append("  SELECT LEAD.LASTUPDATE, ");
		sql.append("         LEAD.LEAD_STATUS, ");
		sql.append("         CAMP.CAMPAIGN_ID, ");
		sql.append("         CAMP.STEP_ID, ");
		sql.append("         CAMP.CAMPAIGN_NAME, ");
		sql.append("         CAMP.CAMPAIGN_DESC, ");
		sql.append("         CAMP.END_DATE, ");
		sql.append("         LEAD.CUST_ID, ");
		sql.append("         LEAD.EMP_ID, ");
		sql.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEAD.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sql.append("         MEM.JOB_TITLE_NAME, ");
		sql.append("         CASE WHEN LENGTH(LEAD.AO_CODE) = 3 THEN LEAD.AO_CODE ELSE NULL END AS AO_CODE, ");
		sql.append("         DEFN.REGION_CENTER_ID, ");
		sql.append("         DEFN.REGION_CENTER_NAME, ");
		sql.append("         DEFN.BRANCH_AREA_ID, ");
		sql.append("         DEFN.BRANCH_AREA_NAME, ");
		sql.append("         DEFN.BRANCH_NAME, ");
		sql.append("         DEFN.BRANCH_NBR, ");
		sql.append("         1 AS LEAD_COUNTS, ");
		sql.append("         CASE WHEN NVL(LEAD.AO_CODE, ' ') = ' ' THEN 1 ELSE 0 END AS LEAD_WAIT_COUNTS, ");
		sql.append("         CASE WHEN NVL(LEAD.AO_CODE, ' ') <> ' ' AND LEAD.LEAD_STATUS >= '03' THEN 1 ELSE 0 END AS LEAD_CLOSE, ");
		sql.append("         CASE WHEN LEAD.LEAD_STATUS < '03' THEN 1 ELSE 0 END AS LEAD_WAIT ");
		sql.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEAD ");
		sql.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON LEAD.BRANCH_ID = DEFN.BRANCH_NBR ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM ON LEAD.EMP_ID = MEM.EMP_ID ");
		sql.append("  WHERE CAMP.CAMPAIGN_ID = LEAD.CAMPAIGN_ID AND CAMP.STEP_ID = LEAD.STEP_ID ");
		sql.append("  AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE-10, 'yyyyMMdd') ");
		sql.append("  AND LEAD.LEAD_STATUS <> 'TR' ");
		sql.append("  AND TRUNC(LEAD.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			
			// 20240325 ADD BY OCEAN : 0001920: WMS-CR-20240129-01_行銷活動管理及高端名單統計數
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEAD.AO_CODE) ");
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
				sql.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID) ");
			} else {
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sql.append("  AND DEFN.REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sql.append("  AND (DEFN.REGION_CENTER_ID IN (:regionCenterIDList) OR DEFN.REGION_CENTER_ID IS NOT NULL) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sql.append("  AND DEFN.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼getOp
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else {
				sql.append("  AND (DEFN.BRANCH_AREA_ID IN (:branchAreaIDList) OR DEFN.BRANCH_AREA_ID IS NOT NULL) ");
				queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sql.append("  AND DEFN.BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else {
				sql.append("  AND (DEFN.BRANCH_NBR IN (:branchIDList) OR DEFN.BRANCH_NBR IS NOT NULL) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEAD.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		if (!StringUtils.isBlank(inputVO.getAoCode())) {
			sql.append("  AND LEAD.EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		if (!StringUtils.isBlank(inputVO.getCampaignName())) {
			sql.append("  AND CAMP.CAMPAIGN_NAME like :campaignName ");
			queryCondition.setObject("campaignName", "%" + inputVO.getCampaignName() + "%");
		}
		
		if (!StringUtils.isBlank(inputVO.getStepId())) {
			sql.append("  AND CAMP.STEP_ID = :stepId ");
			queryCondition.setObject("stepId", inputVO.getStepId());	
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sql.append("  AND LEAD.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		
		if (inputVO.getCamp_sDate() != null) {
			sql.append("  and CAMP.START_DATE >= TRUNC(:camp_sdate) ");
			queryCondition.setObject("camp_sdate", inputVO.getCamp_sDate());
		}	
		
		if (inputVO.getCamp_eDate() != null) {
			sql.append("  and CAMP.START_DATE < TRUNC(:camp_edate)+1 ");
			queryCondition.setObject("camp_edate", inputVO.getCamp_eDate());
		}
		
		if (inputVO.getCamp_esDate() != null) {
			sql.append("  and CAMP.END_DATE >= TRUNC(:camp_ssdate) ");
			queryCondition.setObject("camp_ssdate", inputVO.getCamp_esDate());
		}	
		
		if (inputVO.getCamp_eeDate() != null) {
			sql.append("  and CAMP.END_DATE < TRUNC(:camp_eedate)+1 ");
			queryCondition.setObject("camp_eedate", inputVO.getCamp_eeDate());
		}
		
		sql.append("), COUNT_TEST AS ( ");
		sql.append("  SELECT L.CAMPAIGN_ID, ");
		sql.append("         L.STEP_ID, ");
		sql.append("         L.CAMPAIGN_NAME, ");
		sql.append("         L.CAMPAIGN_DESC, ");
		sql.append("         L.END_DATE, ");
		sql.append("         L.REGION_CENTER_ID, ");
		sql.append("         L.REGION_CENTER_NAME, ");
		sql.append("         L.BRANCH_AREA_ID, ");
		sql.append("         L.BRANCH_AREA_NAME, ");
		sql.append("         L.BRANCH_NBR, ");
		sql.append("         L.BRANCH_NAME, ");
		sql.append("         L.EMP_ID, ");
		sql.append("         L.AO_CODE, ");
		sql.append("         L.EMP_NAME, ");
		sql.append("         L.JOB_TITLE_NAME, ");
		sql.append("         SUM(LEAD_COUNTS) AS LEAD_COUNTS, ");
		sql.append("         SUM(LEAD_WAIT_COUNTS) AS LEAD_WAIT_COUNTS, ");
		sql.append("         SUM(LEAD_CLOSE) AS LEAD_CLOSE, ");
		sql.append("         SUM(LEAD_WAIT) AS LEAD_WAIT ");
		sql.append("  FROM LEADS L ");
		sql.append("  GROUP BY L.CAMPAIGN_ID, L.STEP_ID, L.CAMPAIGN_NAME, L.CAMPAIGN_DESC, L.END_DATE, L.REGION_CENTER_ID, L.REGION_CENTER_NAME, L.BRANCH_AREA_ID, L.BRANCH_AREA_NAME, L.BRANCH_NBR, L.BRANCH_NAME, L.EMP_ID, L.AO_CODE, L.EMP_NAME, L.JOB_TITLE_NAME ");
		sql.append("  ORDER BY L.END_DATE DESC ");
		sql.append(") ");
		
		sql.append("SELECT L.CAMPAIGN_ID, ");
		sql.append("       L.STEP_ID, ");
		sql.append("       L.CAMPAIGN_NAME, ");
		sql.append("       TO_CHAR(L.END_DATE, 'yyyy-MM-dd') AS END_DATE, ");
		sql.append("       L.CAMPAIGN_DESC, ");
		sql.append("       L.REGION_CENTER_ID, ");
		sql.append("       L.REGION_CENTER_NAME, ");
		sql.append("       L.BRANCH_AREA_ID, ");
		sql.append("       L.BRANCH_AREA_NAME, ");
		sql.append("       L.BRANCH_NBR, ");
		sql.append("       L.BRANCH_NAME, ");
		sql.append("       R.ROLE_NAME, ");
		sql.append("       L.EMP_NAME, ");
		sql.append("       L.AO_CODE, ");
		sql.append("       L.EMP_ID, ");
		sql.append("       SUM(LEAD_COUNTS) AS LEAD_COUNTS, ");
		sql.append("       SUM(LEAD_WAIT_COUNTS) AS LEAD_WAIT_COUNTS, ");
		sql.append("       SUM(LEAD_CLOSE) AS LEAD_CLOSE, ");
		sql.append("       CASE WHEN SUM(LEAD_COUNTS) > 0 THEN TRUNC((SUM(LEAD_CLOSE) / SUM(LEAD_COUNTS)) * 100, 2) ELSE 0 END AS LEAD_RATE, ");
		sql.append("       SUM(LEAD_WAIT) AS LEAD_WAIT ");
		sql.append("FROM COUNT_TEST L ");
		sql.append("LEFT JOIN TBORG_MEMBER M ON L.EMP_ID = M.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sql.append("LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sql.append("WHERE 1 = 1 ");

		sql.append("GROUP BY L.CAMPAIGN_ID, L.STEP_ID, L.CAMPAIGN_NAME, TO_CHAR(L.END_DATE, 'yyyy-MM-dd'), L.CAMPAIGN_DESC, ");
		sql.append("         L.REGION_CENTER_ID, L.REGION_CENTER_NAME, L.BRANCH_AREA_ID, L.BRANCH_AREA_NAME, L.BRANCH_NBR, L.BRANCH_NAME, ");
		sql.append("         R.ROLE_NAME, L.EMP_NAME, L.AO_CODE, L.EMP_ID ");

		sql.append("ORDER BY ");
		
		if (!StringUtils.isBlank(inputVO.getCampaignName())) {
			sql.append("L.CAMPAIGN_ID, STEP_ID, ");
		}
		
		sql.append("END_DATE ");
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
		String fileName = "理專活動執行現況_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("理專活動執行現況_" + sdfYYYYMMDD.format(new Date()));
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
		
		String[] headerLine1 = {"業務處", "營運區", "分行", "分行代號", 
								"身分別", "姓名", "AO CODE", "員編", 
								"活動名稱", "簡要說明", "活動到期日", "應聯絡客戶數", "已聯絡客戶數", "聯繫完成率", "待聯繫客戶總數", "待分派客戶數"};
		String[] mainLine    = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "BRANCH_NBR", 
								"ROLE_NAME", "EMP_NAME", "AO_CODE", "EMP_ID", 
								"CAMPAIGN_NAME", "CAMPAIGN_DESC", "END_DATE", "LEAD_COUNTS", "LEAD_CLOSE", "LEAD_RATE", "LEAD_WAIT", "LEAD_WAIT_COUNTS"};
	
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

		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);

			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);
					if (mainLine[j].indexOf("_RATE") >= 0){
						cell.setCellValue(checkIsNull(map, mainLine[j]) + "%");
					} else if (mainLine[j].indexOf("ROLE_NAME") >= 0 && (new BigDecimal(checkIsNull(map, "LEAD_WAIT_COUNTS"))).compareTo(BigDecimal.ZERO) == 1){
						cell.setCellValue("主管待分派名單");
					} else {
						cell.setCellValue(checkIsNull(map, mainLine[j]));
					}
				}

				index++;
			}
		}
		
		workbook.write(new FileOutputStream(filePath));
		this.notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
	}
		
	public void exportReport2 (Object body, IPrimitiveMap header) throws JBranchException, IOException {
		
		XmlInfo xmlInfo = new XmlInfo();
    	Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
    	
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setFetchSize(3000);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT DISTINCT DEFN.REGION_CENTER_ID, ");
		sb.append("         DEFN.REGION_CENTER_NAME, ");
		sb.append("         DEFN.BRANCH_AREA_ID, ");
		sb.append("         DEFN.BRANCH_AREA_NAME, ");
		sb.append("         DEFN.BRANCH_NAME, ");
		sb.append("         DEFN.BRANCH_NBR, ");
		sb.append("         R.ROLE_NAME, ");
		sb.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEADS.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sb.append("         CASE WHEN LENGTH(LEADS.AO_CODE) > 3 THEN NULL ELSE LEADS.AO_CODE END AS AO_CODE, MEM.EMP_ID, ");
		sb.append("         CAMP.CAMPAIGN_NAME, ");
		sb.append("         CAMP.CAMPAIGN_DESC, ");
		sb.append("         CAMP.END_DATE, ");
		sb.append("         LEADS.CUST_ID, ");
		sb.append("         CM.CUST_NAME ");
		sb.append("  FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST CM ON LEADS.CUST_ID = CM.CUST_ID ");
		sb.append("  INNER JOIN TBORG_MEMBER MEM ON LEADS.EMP_ID = MEM.EMP_ID AND LEADS.AO_CODE IS NOT NULL ");
		sb.append("  LEFT JOIN TBORG_MEMBER_ROLE MR ON MR.EMP_ID = MEM.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("  LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON LEADS.BRANCH_ID = DEFN.BRANCH_NBR ");
		sb.append("  WHERE LEADS.LEAD_STATUS < '03' ");
		sb.append("  AND TO_CHAR(LEADS.END_DATE+10, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		sb.append("  AND LEADS.LEAD_TYPE <> '04' ");
		sb.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		sb.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
				sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID) ");
			} else {
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sb.append("  AND DEFN.REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sb.append("  AND (DEFN.REGION_CENTER_ID IN (:regionCenterIDList) OR DEFN.REGION_CENTER_ID IS NOT NULL) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sb.append("  AND DEFN.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼getOp
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else {
				sb.append("  AND (DEFN.BRANCH_AREA_ID IN (:branchAreaIDList) OR DEFN.BRANCH_AREA_ID IS NOT NULL) ");
				queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sb.append("  AND DEFN.BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else {
				sb.append("  AND (DEFN.BRANCH_NBR IN (:branchIDList) OR DEFN.BRANCH_NBR IS NOT NULL) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		} else {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		if (!StringUtils.isBlank(inputVO.getAoCode())) {
			sb.append("  AND LEADS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sb.append("  UNION ");
		
		sb.append("  SELECT DISTINCT DEFN.REGION_CENTER_ID, ");
		sb.append("         DEFN.REGION_CENTER_NAME, ");
		sb.append("         DEFN.BRANCH_AREA_ID, ");
		sb.append("         DEFN.BRANCH_AREA_NAME, ");
		sb.append("         DEFN.BRANCH_NAME, ");
		sb.append("         DEFN.BRANCH_NBR, ");
		sb.append("         NULL AS ROLE_NAME, ");
		sb.append("         NULL AS EMP_NAME, ");
		sb.append("         NULL AS AO_CODE, ");
		sb.append("         '主管待分派名單' AS EMP_ID, ");
		sb.append("         CAMP.CAMPAIGN_NAME, ");
		sb.append("         CAMP.CAMPAIGN_DESC, ");
		sb.append("         CAMP.END_DATE, ");
		sb.append("         LEADS.CUST_ID, ");
		sb.append("         CM.CUST_NAME ");
		sb.append("  FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST CM ON LEADS.CUST_ID = CM.CUST_ID ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON LEADS.BRANCH_ID = DEFN.BRANCH_NBR ");
		sb.append("  WHERE LEADS.LEAD_STATUS < '03' ");
		sb.append("  AND TO_CHAR(LEADS.END_DATE + 10, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		sb.append("  AND LEADS.LEAD_TYPE <> '04' ");
		sb.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		
		sb.append("  AND NVL(LEADS.EMP_ID, ' ') = ' ' ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
				sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID) ");
			} else {
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sb.append("  AND DEFN.REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sb.append("  AND (DEFN.REGION_CENTER_ID IN (:regionCenterIDList) OR DEFN.REGION_CENTER_ID IS NOT NULL) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sb.append("  AND DEFN.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼getOp
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else {
				sb.append("  AND (DEFN.BRANCH_AREA_ID IN (:branchAreaIDList) OR DEFN.BRANCH_AREA_ID IS NOT NULL) ");
				queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sb.append("  AND DEFN.BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else {
				sb.append("  AND (DEFN.BRANCH_NBR IN (:branchIDList) OR DEFN.BRANCH_NBR IS NOT NULL) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		} else {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		if (!StringUtils.isBlank(inputVO.getAoCode())) {
			sb.append("  AND LEADS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sb.append(") ");

		sb.append("SELECT REGION_CENTER_ID, ");
		sb.append("       REGION_CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, ");
		sb.append("       BRANCH_AREA_NAME, ");
		sb.append("       BRANCH_NAME, ");
		sb.append("       BRANCH_NBR, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       EMP_NAME, ");
		sb.append("       AO_CODE, ");
		sb.append("       EMP_ID, ");
		sb.append("       CAMPAIGN_NAME, ");
		sb.append("       CAMPAIGN_DESC, ");
		sb.append("       END_DATE, ");
		sb.append("       CUST_NAME, ");
		sb.append("       CUST_ID ");
		sb.append("FROM ( ");
		sb.append("  SELECT REGION_CENTER_ID, ");
		sb.append("         REGION_CENTER_NAME, ");
		sb.append("         BRANCH_AREA_ID, ");
		sb.append("         BRANCH_AREA_NAME, ");
		sb.append("         BRANCH_NAME, ");
		sb.append("         BRANCH_NBR, ");
		sb.append("         ROLE_NAME, ");
		sb.append("         EMP_NAME, ");
		sb.append("         AO_CODE, ");
		sb.append("         EMP_ID, ");
		sb.append("         CAMPAIGN_NAME, ");
		sb.append("         CAMPAIGN_DESC, ");
		sb.append("         TO_CHAR(END_DATE, 'yyyy-MM-dd') AS END_DATE, ");
		sb.append("         CUST_NAME, ");
		sb.append("         CUST_ID ");
		sb.append("  FROM BASE ");
		sb.append("  UNION ");
		sb.append("  SELECT NULL AS REGION_CENTER_ID, ");
		sb.append("         NULL AS REGION_CENTER_NAME, ");
		sb.append("         NULL AS BRANCH_AREA_ID, ");
		sb.append("         NULL AS BRANCH_AREA_NAME, ");
		sb.append("         NULL AS BRANCH_NAME, ");
		sb.append("         NULL AS BRANCH_NBR, ");
		sb.append("         NULL AS ROLE_NAME, ");
		sb.append("         NULL AS EMP_NAME, ");
		sb.append("         NULL AS AO_CODE, ");
		sb.append("         NULL AS EMP_ID, ");
		sb.append("         NULL AS CAMPAIGN_NAME, ");
		sb.append("         NULL AS CAMPAIGN_DESC, ");
		sb.append("         NULL AS END_DATE, ");
		sb.append("         '總計' AS CUST_NAME, ");
		sb.append("         COUNT(CUST_ID)||'' AS CUST_ID ");
		sb.append("  FROM BASE ");
		sb.append(") ");
		sb.append("WHERE 1 = 1 ");
		sb.append("ORDER BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NAME, BRANCH_NBR, DECODE(CUST_NAME, '總計', 999999999, 1) ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
		String fileName = "待聯繫客戶數_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("待聯繫客戶數_" + sdfYYYYMMDD.format(new Date()));
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
		
		String[] headerLine1 = {"業務處", "營運區", "分行", "分行代號", "身分別", "姓名", "AO CODE", "員編", "活動名稱", "簡要說明", "活動到期日", "客戶姓名", "客戶ID"};
		String[] mainLine    = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "BRANCH_NBR", 
								"ROLE_NAME", "EMP_NAME", "AO_CODE", "EMP_ID", 
								"CAMPAIGN_NAME", "CAMPAIGN_DESC", "END_DATE", "CUST_NAME", "CUST_ID"};
	
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

		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);

			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);
					if (mainLine[j].indexOf("_RATE") >= 0){
						cell.setCellValue(checkIsNull(map, mainLine[j]) + "%");
					} else {
						cell.setCellValue(checkIsNull(map, mainLine[j]));
					}
				}

				index++;
			}
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		this.notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
	}
		
	public void exportReport3 (Object body, IPrimitiveMap header) throws JBranchException, IOException {
		
		XmlInfo xmlInfo = new XmlInfo();
    	Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
    	
		CAM210InputVO inputVO = (CAM210InputVO) body ;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setFetchSize(3000);
		StringBuffer sb = new StringBuffer();

		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT DISTINCT DEFN.REGION_CENTER_ID, ");
		sb.append("         DEFN.REGION_CENTER_NAME, ");
		sb.append("         DEFN.BRANCH_AREA_ID, ");
		sb.append("         DEFN.BRANCH_AREA_NAME, ");
		sb.append("         DEFN.BRANCH_NAME, ");
		sb.append("         DEFN.BRANCH_NBR, ");
		sb.append("         R.ROLE_NAME, ");
		sb.append("         MEM.EMP_NAME || CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UI WHERE UI.EMP_ID = LEADS.EMP_ID) > 0 THEN '' ELSE '' END AS EMP_NAME, ");
		sb.append("         CASE WHEN LENGTH(LEADS.AO_CODE) > 3 THEN NULL ELSE LEADS.AO_CODE END AS AO_CODE, ");
		sb.append("         MEM.EMP_ID, ");
		sb.append("         CAMP.CAMPAIGN_NAME, ");
		sb.append("         CAMP.CAMPAIGN_DESC, ");
		sb.append("         CAMP.END_DATE, ");
		sb.append("         LEADS.CUST_ID, ");
		sb.append("         CM.CUST_NAME ");
		sb.append("  FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		sb.append("  INNER JOIN TBCRM_CUST_MAST CM ON LEADS.CUST_ID = CM.CUST_ID ");
		sb.append("  INNER JOIN TBORG_MEMBER MEM ON LEADS.EMP_ID = MEM.EMP_ID AND LEADS.AO_CODE IS NOT NULL ");
		sb.append("  LEFT JOIN TBORG_MEMBER_ROLE MR ON MR.EMP_ID = MEM.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("  LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON LEADS.BRANCH_ID = DEFN.BRANCH_NBR ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND TO_CHAR(LEADS.END_DATE, 'yyyyMMdd') <= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		sb.append("  AND TO_CHAR(LEADS.END_DATE, 'yyyyMMdd') > TO_CHAR(ADD_MONTHS(SYSDATE, -12), 'yyyyMMdd') ");
		sb.append("  AND LEADS.LEAD_STATUS < '03' ");
		sb.append("  AND LEADS.LEAD_TYPE <> '04' ");
		
		sb.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
			
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
				sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID) ");
			} else {
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			if (StringUtils.isNotBlank(inputVO.getRegion()) && !"null".equals(inputVO.getRegion())) {
				sb.append("  AND DEFN.REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion());
			} else {
				sb.append("  AND (DEFN.REGION_CENTER_ID IN (:regionCenterIDList) OR DEFN.REGION_CENTER_ID IS NOT NULL) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			if (StringUtils.isNotBlank(inputVO.getOp()) && !"null".equals(inputVO.getOp())) {
				sb.append("  AND DEFN.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼getOp
				queryCondition.setObject("branchAreaID", inputVO.getOp());
			} else {
				sb.append("  AND (DEFN.BRANCH_AREA_ID IN (:branchAreaIDList) OR DEFN.BRANCH_AREA_ID IS NOT NULL) ");
				queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch()) && Integer.valueOf(inputVO.getBranch()) > 0) {
				sb.append("  AND DEFN.BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch());
			} else {
				sb.append("  AND (DEFN.BRANCH_NBR IN (:branchIDList) OR DEFN.BRANCH_NBR IS NOT NULL) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			
			// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = LEADS.AO_CODE) ");
			}
		} else {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID AND UHRM.DEPT_ID = :uhrmOP) ");
			queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
		}
		
		if (!StringUtils.isBlank(inputVO.getAoCode())) {
			sb.append("  AND LEADS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		sb.append("  ) ");
		
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NAME, BRANCH_NBR, ROLE_NAME, EMP_NAME, AO_CODE, EMP_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, TO_CHAR(END_DATE, 'yyyy-MM-dd') AS END_DATE, CUST_NAME, CUST_ID ");
		sb.append("FROM BASE ");
		sb.append("UNION ");
		sb.append("SELECT NULL AS REGION_CENTER_ID, NULL AS REGION_CENTER_NAME, NULL AS BRANCH_AREA_ID, NULL AS BRANCH_AREA_NAME, NULL AS BRANCH_NAME, NULL AS BRANCH_NBR, NULL AS ROLE_NAME, NULL AS EMP_NAME, NULL AS AO_CODE, NULL AS EMP_ID, NULL AS CAMPAIGN_NAME, NULL AS CAMPAIGN_DESC, NULL AS END_DATE, '總計' AS CUST_NAME, COUNT(CUST_ID)||'' AS CUST_ID ");
		sb.append("FROM BASE ");
		
		sb.append("ORDER BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NAME, BRANCH_NBR ");

		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
		String fileName = "未結案已過期客戶數(近1年)_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("未結案已過期客戶數(近1年)_" + sdfYYYYMMDD.format(new Date()));
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
		
		String[] headerLine1 = {"業務處", "營運區", "分行", "分行代號", "身分別", "姓名", "AO CODE", "員編", "活動名稱", "簡要說明", "活動到期日", "客戶姓名", "客戶ID"};
		String[] mainLine    = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "BRANCH_NBR", 
								"ROLE_NAME", "EMP_NAME", "AO_CODE", "EMP_ID", 
								"CAMPAIGN_NAME", "CAMPAIGN_DESC", "END_DATE", "CUST_NAME", "CUST_ID"};
	
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

		Map<String, String> refProdMap = new XmlInfo().doGetVariable("CAM.REF_PROD", FormatHelper.FORMAT_3);

		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);

			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);
					if (mainLine[j].indexOf("_RATE") >= 0){
						cell.setCellValue(checkIsNull(map, mainLine[j]) + "%");
					} else {
						cell.setCellValue(checkIsNull(map, mainLine[j]));
					}
				}

				index++;
			}
		}
		
		workbook.write(new FileOutputStream(filePath));
		this.notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull (Map map, String key) {
		
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	public void getBranch(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();		
		CAM210InputVO inputVO = (CAM210InputVO) body;
		CAM210OutputVO outputVO = new CAM210OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DEFN.BRANCH_NBR, DEFN.BRANCH_NAME ");
		sb.append("FROM VWORG_DEFN_INFO DEFN ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND EXISTS (SELECT 1 FROM VWORG_DEFN_INFO T WHERE DEFN.BRANCH_AREA_ID = T.BRANCH_AREA_ID AND T.BRANCH_NAME = :branchName)  ");
		
		queryCondition.setObject("branchName", inputVO.getBranch_Name());

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setBranchList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
}