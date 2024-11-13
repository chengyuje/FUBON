package com.systex.jbranch.app.server.fps.crm131;

import java.io.FileOutputStream;
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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
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

/**
 * @author Stella
 * @date 2016/06/28
 * 
 */

@Component("crm131")
@Scope("request")
public class CRM131 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	
	public String VWCAM_SFA_LEADS_CURRENT (boolean codiEmpID, boolean codiBranchList, boolean codiPrivilegeID, String memLoginFlag) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT DISTINCT LEADS.EMP_ID, ");
		sb.append("         VDI.REGION_CENTER_ID, ");
		sb.append("         VDI.REGION_CENTER_NAME, ");
		sb.append("         VDI.BRANCH_AREA_ID, ");
		sb.append("         VDI.BRANCH_AREA_NAME, ");
		sb.append("         VDI.BRANCH_NBR, ");
		sb.append("         VDI.BRANCH_NAME, ");
		sb.append("         LEADS.CAMPAIGN_ID, ");
		sb.append("         LEADS.STEP_ID, ");
		sb.append("         LEADS.CUST_ID, ");
		sb.append("         MEM.EMP_NAME, ");
		sb.append("         AO.AO_CODE, ");
		sb.append("         ROLE.ROLE_ID, ");
		sb.append("         CASE WHEN TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE + 10) AND LEADS.LEAD_STATUS < '03' THEN 1 ELSE 0 END AS CONTACT_CUST, "); // -- 過期未超過十天 且 未結案 
		sb.append("         CASE WHEN TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE + 10) AND SUBSTR(LEADS.LEAD_STATUS, 0, 2) = '03' AND TRUNC(LEADS.LASTUPDATE) = TRUNC(SYSDATE) THEN 1 ELSE 0 END AS CONTACTED_CUST, "); // -- 今日執行結案 
		sb.append("         CASE WHEN LEADS.LEAD_STATUS < '03' AND TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE + 10) THEN 1 ELSE 0 END AS TOTAL_CONTACT_CUST, "); // -- 待聯繫名單 (未結案 且 過期未超過十天)
		sb.append("         CASE WHEN TRUNC(LEADS.END_DATE) <= TRUNC(SYSDATE) AND TRUNC(LEADS.END_DATE) > TRUNC(ADD_MONTHS(SYSDATE, -12)) AND LEADS.LEAD_STATUS < '03' THEN 1 ELSE 0 END AS NOT_CONTACT_CUST, "); // -- 未聯繫名單 (今日大於結束日 且 結束日在今日起往前算一年內 且 未結案) 
		sb.append("         CASE WHEN TRUNC(SYSDATE) >= TRUNC(LEADS.START_DATE) AND TRUNC(SYSDATE) <= TRUNC(LEADS.END_DATE) AND LEADS.LEAD_STATUS < '03' AND LEADS.LEAD_TYPE = '99' THEN 1 ELSE 0 END AS TOTAL_CONTROL_CUST ");
		sb.append("  FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("  INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEADS.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEADS.STEP_ID = CAMP.STEP_ID ");
		sb.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = LEADS.BRANCH_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON LEADS.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON AO.TYPE = '1' AND LEADS.EMP_ID = AO.EMP_ID ");
		sb.append("  LEFT JOIN (SELECT ROLE_ID, EMP_ID FROM TBORG_MEMBER_ROLE WHERE IS_PRIMARY_ROLE = 'Y') MROLE ON MROLE.EMP_ID = LEADS.EMP_ID ");
		sb.append("  LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y') ROLE ON ROLE.ROLE_ID = MROLE.ROLE_ID ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO VDI ON VDI.BRANCH_NBR = LEADS.BRANCH_ID ");
		sb.append("  LEFT JOIN TBSYSSECUROLPRIASS B ON B.ROLEID = ROLE.ROLE_ID ");

		sb.append("  WHERE NVL(LEADS.EMP_ID,' ') <> ' ' ");
		sb.append("  AND LEADS.LEAD_TYPE <> '04' ");
		sb.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		
		if (codiEmpID) 
			sb.append("  AND LEADS.EMP_ID = :empID ");
		
		if (codiBranchList) 
			sb.append("  AND VDI.BRANCH_NBR IN (:brList) ");
		
		if (codiPrivilegeID)
			sb.append("  AND B.PRIVILEGEID = :privilegeID ");
		
		if (StringUtils.indexOf(StringUtils.lowerCase(memLoginFlag), "uhrm") < 0) { //暫以Eli修改版本為主
				// 20200602 ADD BY OCEAN : 0000166: WMS-CR-20200226-01_高端業管功能改採兼任分行角色調整相關功能_登入底層+行銷模組
			sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID) ");
		} else {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID) ");
		}
		
		sb.append("  AND CAMP.REMOVE_FLAG = 'N' ");
		
		sb.append(") ");
		sb.append(", LEADS_CURRENT AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, AO_CODE, ROLE_ID, ");
		sb.append("         SUM(CONTACT_CUST + CONTACTED_CUST) AS CONTACT_CUST, ");
		sb.append("         SUM(CONTACTED_CUST) AS CONTACTED_CUST, ");
		sb.append("         SUM(TOTAL_CONTACT_CUST) AS TOTAL_CONTACT_CUST, ");
		sb.append("         SUM(NOT_CONTACT_CUST) AS NOT_CONTACT_CUST, ");
		sb.append("         SUM(TOTAL_CONTROL_CUST) AS TOTAL_CONTROL_CUST ");
		sb.append("  FROM BASE ");
		sb.append("  GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, AO_CODE, ROLE_ID ");
		sb.append(") ");
		
		return sb.toString();
	}

	public void ao_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
		Map<String, String> fchMap = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2); //FCH理專
		
		CRM131InputVO inputVO = (CRM131InputVO) body;
		CRM131OutputVO return_VO = new CRM131OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.EMP_ID, ");
		sql.append("       A.EMP_NAME, ");
		sql.append("       A.AO_CODE, ");
		sql.append("       A.ROLE_ID AS AO_JOB_RANK, ");
		sql.append("       A.BRA_NBR AS BRANCH_NBR, ");
		sql.append("       A.AREA_ID AS BRANCH_AREA_ID, ");
		sql.append("       A.CENTER_ID AS REGION_CENTER_ID, ");
		sql.append("       A.TYPE ");
		sql.append("FROM VWORG_AO_INFO A ");
		sql.append("WHERE A.AO_CODE IS NOT NULL ");
		sql.append("AND A.BRA_NBR IN (:brNbrList) ");
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND A.CENTER_ID = :centerID ");
			queryCondition.setObject("centerID", inputVO.getRegion_center_id());
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND A.AREA_ID = :areaID ");
			queryCondition.setObject("areaID", inputVO.getBranch_area_id());
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND A.BRA_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		}

		// 判斷如果是掛code
		if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			fchMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND A.AO_CODE IN (:ao_list) ");
			queryCondition.setObject("ao_list", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
		}

		sql.append(" ORDER BY A.AO_CODE ");

		queryCondition.setQueryString(sql.toString());

		return_VO.setAo_list(dam.exeQuery(queryCondition));

		sendRtnObject(return_VO);
	}

	public void login(Object body, IPrimitiveMap header) throws JBranchException {

		CRM131OutputVO return_VO = new CRM131OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		// 0-分行理專 、1-輔銷FA、2-輔銷IA、3-主管 、4-消金、5-UHRM
		sql.append("SELECT CASE WHEN PRIVILEGEID IN ('001', '002', '003', 'JRM') THEN 0 ");
		sql.append("            WHEN PRIVILEGEID IN ('014', '015') THEN 1 ");
		sql.append("            WHEN PRIVILEGEID IN ('023', '024') THEN 2 ");
		sql.append("            WHEN PRIVILEGEID IN ('006', '009', '010', '011', '012', '013') THEN 3 ");
		sql.append("            WHEN PRIVILEGEID IN ('004AO', '004', '005', '007', '008') THEN 4 ");
		sql.append("            WHEN PRIVILEGEID IN ('UHRM012', 'UHRM013') THEN 5 ");
		sql.append("       END AS COUNTS ");
		sql.append("FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE ROLEID = :roleID ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		return_VO.setPrivilege(dam.exeQuery(queryCondition));

		sendRtnObject(return_VO);
	}

	/** 聯繫名單-理專 、輔銷人員、消金PS **/
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);

		CRM131OutputVO return_VO = new CRM131OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT TOT_CONTROL_CUST, TOT_NEC_NOTIFY_CUST, TOT_MARKETING_CUST, ");
		sb.append(" TOT_LEAVE_INFO_CUST, TOT_REFER_INFO_CUST, TOT_OTHER_CUST, LEAVE_INFO_YN ");
		sb.append(" FROM TBCAM_LEADS_BY_EMP WHERE EMP_ID = :empID AND ROLE_ID = :roleID ");
		
//		sb.append("SELECT EMP.CONTACT_CUST, ");
//		sb.append("       EMP.CONTACTED_CUST, ");
//		sb.append("       EMP.CMPLT_R, ");
//		sb.append("       EMP.TOT_CONTROL_CUST AS TOTAL_CONTROL_CUST, ");
//		
//		sb.append("       (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END AS LEAVE_INFO_YN ");
//		sb.append("        FROM TBCAM_SFA_LEADS LE ");
//		sb.append("        INNER JOIN TBCAM_SFA_CAMPAIGN CA ON LE.CAMPAIGN_ID = CA.CAMPAIGN_ID AND LE.STEP_ID = CA.STEP_ID ");
//		sb.append("        WHERE 1 = 1 ");
//		sb.append("        AND CA.LEAD_TYPE IN ('05', '06', '07', '08', 'H1', 'H2', 'F1', 'F2', 'I1', 'I2', '09', 'UX') ");
//		sb.append("        AND TRUNC(SYSDATE) BETWEEN LE.START_DATE AND LE.END_DATE ");
//		sb.append("        AND LE.LEAD_STATUS < '03' ");
//		sb.append("        AND LE.EMP_ID = EMP.EMP_ID ");
//		sb.append("        GROUP BY LE.EMP_ID) AS LEAVE_INFO_YN, ");
//		
//		sb.append("       EMP.BELLOW_UNDERSERV_CNT, ");
//		sb.append("       EMP.NEAR_UNDERSERV_CNT ");
//		sb.append("FROM TBCAM_LEADS_BY_EMP EMP ");
//		sb.append("WHERE EMP.EMP_ID = :empID ");
		
		queryCondition.setObject("empID", ws.getUser().getUserID());
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sb.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));

		sendRtnObject(return_VO);
	}

	/** 聯繫名單-主管轄下理專今日名單應聯繫、已聯繫、完成率、主管應聯絡數 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM131InputVO inputVO = (CRM131InputVO) body;
		CRM131OutputVO return_VO2 = new CRM131OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

//		sb.append("SELECT A.RM_CONTACT_CUST, ");
//		sb.append("       A.RM_TOT_CONTROL_CUST, ");	
//		sb.append("       A.RM_CONTACTED_CUST, ");
//		sb.append("       A.RM_CMPLT_R,  ");
//		sb.append("       A.CONTACT_CUST AS M_CONTACT_CUST, ");
//
//		sb.append("       (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END AS LEAVE_INFO_YN ");
//		sb.append("        FROM TBCAM_SFA_LEADS LE ");
//		sb.append("        INNER JOIN TBCAM_SFA_CAMPAIGN CA ON LE.CAMPAIGN_ID = CA.CAMPAIGN_ID AND LE.STEP_ID = CA.STEP_ID ");
//		sb.append("        WHERE 1 = 1 ");
//		sb.append("        AND CA.LEAD_TYPE IN ('05', '06', '07', '08', 'H1', 'H2', 'F1', 'F2', 'I1', 'I2', '09', 'UX') ");
//		sb.append("        AND LE.AO_CODE IS NULL ");
//		sb.append("        AND TRUNC(SYSDATE) BETWEEN LE.START_DATE AND LE.END_DATE ");
//		sb.append("        AND LE.BRANCH_ID = A.BRANCH_NBR ");
//		sb.append("        GROUP BY LE.BRANCH_ID) AS LEAVE_INFO_YN, ");
//		
//		sb.append("       A.NEAR_UNDERSERV_CNT, ");
//		sb.append("       A.BELLOW_UNDERSERV_CNT, ");
//		sb.append("       A.WAIT_DISPATCH_LEADS, ");
//		sb.append("       A.NRM_CONTACT_CUST, ");
//		sb.append("       A.NRM_TOT_CONTROL_CUST, ");	
//		sb.append("       A.NRM_CONTACTED_CUST, ");
//		sb.append("       A.NRM_CMPLT_R  ");
//		sb.append("FROM TBCAM_LEADS_BY_BOSS A ");
//		sb.append("WHERE 1 = 1 ");
//		
//		switch (inputVO.getPri_id()) {
//			case "UHRM012":
//			case "UHRM013":
//				sb.append("AND BRANCH_NBR = '031' ");
//				
//				break;
//			default:
//				sb.append("AND BRANCH_NBR IN (:brList) ");
//				queryCondition.setObject("brList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
//				
//				break;
//		}
//		sb.append("AND EMP_ID = :empID ");
		
		sb.append("SELECT RM_TOT_CONTROL_CUST, ");
		sb.append("		  RM_TOT_NEC_NOTIFY_CUST, ");
		sb.append("		  RM_TOT_MARKETING_CUST, ");
		sb.append("	      RM_TOT_LEAVE_INFO_CUST, ");
		sb.append("	      RM_TOT_REFER_INFO_CUST, ");
		sb.append("	      RM_TOT_OTHER_CUST, ");
		sb.append("	      NRM_TOT_CONTROL_CUST, ");
		sb.append("	      NRM_TOT_NEC_NOTIFY_CUST, ");
		sb.append("	      NRM_TOT_MARKETING_CUST, ");
		sb.append("	      NRM_TOT_LEAVE_INFO_CUST, ");
		sb.append("	      NRM_TOT_REFER_INFO_CUST, ");
		sb.append("	      NRM_TOT_OTHER_CUST, ");
		sb.append("	      CONTACT_CUST AS M_CONTACT_CUST, ");
		sb.append("	      WAIT_DISPATCH_LEADS ");
		sb.append("FROM TBCAM_LEADS_BY_BOSS ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("AND ROLE_ID = :loginRole ");
		sb.append("AND BRANCH_AREA_ID = :loginArea ");
		
		switch (inputVO.getPri_id()) {
			case "UHRM012":
			case "UHRM013":
				break;
			default:
				sb.append("AND BRANCH_NBR IN (:brList) ");
				queryCondition.setObject("brList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				break;
		}
		
		queryCondition.setObject("empID", ws.getUser().getUserID());
		queryCondition.setObject("loginRole", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setObject("loginArea", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
		
		queryCondition.setQueryString(sb.toString());
		return_VO2.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(return_VO2);
	}

	/** 未分派 ADD MARK : #0001929_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_聯繫名單統計 **/
	public void inquire1(Object body, IPrimitiveMap header) throws JBranchException {

		// ADD MARK BY OCEAN : #0001929_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_聯繫名單統計
//		CRM131OutputVO return_VO2 = new CRM131OutputVO();
//		dam = this.getDataAccessManager();
//
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//
//		sql.append("SELECT COUNT(1) AS UNCONTACT_CUST ");
//		sql.append("FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEADS ");
//		sql.append("WHERE CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID ");
//		sql.append("AND CAMP.STEP_ID = LEADS.STEP_ID ");
//		sql.append("AND LEADS.BRANCH_ID IN (:brNbrList) ");
//		sql.append("AND LEADS.AO_CODE IS NULL ");
//		sql.append("AND LEADS.LEAD_STATUS < '03'");
//		sql.append("AND TRUNC(LEADS.END_DATE) + 10 >= TRUNC(SYSDATE) ");
//		sql.append("AND LEADS.LEAD_STATUS <> 'TR' ");
//		sql.append("AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
//		
//		queryCondition.setQueryString(sql.toString());
//		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
//
//		return_VO2.setResultList3(dam.exeQuery(queryCondition));

		sendRtnObject(null);
	}

	/** 非理專人員 ADD MARK : #0001929_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_聯繫名單統計 **/
	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {

		// ADD MARK BY OCEAN : #0001929_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_聯繫名單統計
//		CRM131InputVO inputVO = (CRM131InputVO) body;
//		CRM131OutputVO return_VO2 = new CRM131OutputVO();
//		dam = this.getDataAccessManager();
//
//		// 依系統角色決定下拉選單可視範圍
//		XmlInfo xmlInfo = new XmlInfo();
//		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
//		Map<String, String> faiaMap = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2);
//
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append(VWCAM_SFA_LEADS_CURRENT(false, false, false, inputVO.getMemLoginFlag()));
//		sql.append("SELECT NVL(CONTACT_CUST,0) AS CONTACT_CUST, ");
//		sql.append("       NVL(CONTACTED_CUST,0) AS CONTACTED_CUST, ");
//		sql.append("       CASE WHEN CONTACT_CUST !=0 THEN (ROUND(CONTACTED_CUST / CONTACT_CUST,3))*100 ||'%' ELSE '0' END AS CMPLT_R, ");
//		sql.append("       NVL(TOTAL_CONTROL_CUST, 0) AS TOTAL_CONTROL_CUST ");
//		sql.append("FROM ( ");
//		sql.append("  SELECT SUM (A.CONTACT_CUST) AS CONTACT_CUST, ");
//		sql.append("         SUM (A.CONTACTED_CUST) AS CONTACTED_CUST, ");
//		sql.append("         SUM(A.TOTAL_CONTROL_CUST) AS TOTAL_CONTROL_CUST ");
//		sql.append("  FROM LEADS_CURRENT A ");
//		sql.append("  LEFT JOIN TBSYSSECUROLPRIASS B ON B.ROLEID = A.ROLE_ID ");
//		sql.append("  WHERE 1 = 1 ");
//		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
//
//		} else if (faiaMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
//			String privilegeID = getPrivilegeID();
//			if (StringUtils.equals("015", privilegeID) || StringUtils.equals("024", privilegeID)) {
//
//			} else {
//				sql.append("  AND A.REGION_CENTER_ID IN (:rcIdList) ");
//				sql.append("  AND A.BRANCH_AREA_ID IN (:opIdList) ");
//				sql.append("  AND A.BRANCH_NBR IN (:brNbrList) ");
//				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
//				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
//				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
//				sql.append("  AND B.PRIVILEGEID NOT IN ('002', '003') AND NVL(A.EMP_ID, ' ') <> ' ' ");
//
//			}
//		} else {
//			sql.append("  AND A.REGION_CENTER_ID IN (:rcIdList) ");
//			sql.append("  AND A.BRANCH_AREA_ID IN (:opIdList) ");
//			sql.append("  AND A.BRANCH_NBR IN (:brNbrList) ");
//			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
//			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
//			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
//			sql.append("  AND B.PRIVILEGEID NOT IN ('002', '003') AND NVL(A.EMP_ID, ' ') <> ' ' ");
//
//		}
//
//		sql.append(") ");
//
//		queryCondition.setQueryString(sql.toString());
//
//		return_VO2.setResultList4(dam.exeQuery(queryCondition));

		sendRtnObject(null);
	}

	// === 查詢登入者的PRIVILEGEID
	private String getPrivilegeID() throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :loginRoleID ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("loginRoleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		List<Map<String, Object>> tempList = dam.exeQueryWithoutSort(queryCondition);
		if (tempList.size() > 0) {
			return (String) tempList.get(0).get("PRIVILEGEID");
		}

		return null;
	}

	//WMS-CR-20181025-02_新增客戶聯繫頻率管理報表
	public void inquire3(Object body, IPrimitiveMap header) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
		Map<String, String> fchMap = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2); //FCH理專
		
		CRM131InputVO inputVO = (CRM131InputVO) body;
		CRM131OutputVO return_VO2 = new CRM131OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT UR.DATA_DATE, ");
		sql.append("       SUM(CASE WHEN (UR.TAKE_CARE_MATCH_YN = 'N' OR UR.TAKE_CARE_MATCH_YN IS NULL) THEN 1 ELSE 0 END) AS BELLOW_UNDERSERV_CNT, ");
		sql.append("       SUM(CASE WHEN UR.TAKE_CARE_DUE_YN = 'Y' THEN 1 ELSE 0 END) AS NEAR_UNDERSERV_CNT ");
		sql.append("FROM TBCRM_CUST_MAST CM ");
		sql.append("INNER JOIN TBCRM_UNDERSERV_RPT UR ON CM.CUST_ID = UR.CUST_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND (UR.AO_INTERACT_LASTUPDATE is NULL or UR.AO_INTERACT_LASTUPDATE < SYSDATE -1) ");
		

		switch (inputVO.getPri_id()) {
			case "UHRM012":
			case "UHRM013":
				sql.append("AND CM.BRA_NBR = '031' ");
				
				break;
			default:
				sql.append("AND CM.BRA_NBR IN (:brNbrList) ");
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				
				break;
		}

		// 判斷如果是掛code
		if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			fchMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND CM.AO_CODE IN (:ao_list) ");
			queryCondition.setObject("ao_list", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
		}

		sql.append(" GROUP BY DATA_DATE ");

		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> result = dam.exeQuery(queryCondition);
		if (result.isEmpty()) {
			result = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			map.put("DATA_DATE", dateFormat.format(new Date()));
			map.put("BELLOW_UNDERSERV_CNT", 0);
			map.put("NEAR_UNDERSERV_CNT", 0);
			result.add(map);
		}

		return_VO2.setResultList5(result);

		sendRtnObject(return_VO2);
	}

	public void getUnderservRpt(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
		Map<String, String> fchMap = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2); //FCH理專
		
		CRM131InputVO inputVO = (CRM131InputVO) body;
		CRM131OutputVO return_VO2 = new CRM131OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT CM.CUST_ID, ");
		sql.append("       CM.BRA_NBR, ");
		sql.append("       BR.BRANCH_NAME, ");
		sql.append("       CM.AO_CODE, ");
		sql.append("       NVL(AO.EMP_ID,' ') as EMP_ID, ");
		sql.append("       NVL(AO.EMP_NAME,' ') as EMP_NAME, ");
		sql.append("       CM.CUST_NAME, ");
		sql.append("       M.FRQ_DAY, ");
		sql.append("       UR.AO_INTERACT_LASTUPDATE, ");
		sql.append("       CM.AUM_AMT, ");
		sql.append("       CM.CON_DEGREE, ");
		sql.append("       CM.VIP_DEGREE, ");
		sql.append("       UR.AO_INTERACT_LASTUPDATE + M.FRQ_DAY AS LAST_CONTACT_DATE ");
		sql.append("FROM TBCRM_CUST_MAST CM INNER JOIN TBCRM_UNDERSERV_RPT UR ON CM.CUST_ID = UR.CUST_ID ");
		sql.append("LEFT JOIN VWCRM_CUST_MGMT_FRQ_MAP M ON NVL(CM.VIP_DEGREE, 'M') = M.VIP_DEGREE AND NVL(CM.CON_DEGREE, 'S') = M.CON_DEGREE ");
		sql.append("LEFT JOIN VWORG_AO_INFO AO ON CM.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO BR ON CM.BRA_NBR = BR.BRANCH_NBR ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND (UR.AO_INTERACT_LASTUPDATE is NULL or UR.AO_INTERACT_LASTUPDATE < SYSDATE -1) ");

		if (StringUtils.isNotEmpty(inputVO.getBranch_nbr())) {
			sql.append("AND CM.BRA_NBR = :brNbrList ");
			queryCondition.setObject("brNbrList", inputVO.getBranch_nbr());
		} else {
			sql.append("AND CM.BRA_NBR IN (:brNbrList) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		if (StringUtils.isNotEmpty(inputVO.getAo_code())) {
			sql.append("AND CM.AO_CODE = :ao_list ");
			queryCondition.setObject("ao_list", inputVO.getAo_code());
		} else {
			// 判斷如果是掛code
			if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || fchMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND CM.AO_CODE IN (:ao_list) ");
				queryCondition.setObject("ao_list", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			}
		}

		//查詢7天後即將落入不符經營頻次
		if (StringUtils.isNotBlank(inputVO.getTAKE_CARE_DUE_YN())) {
			sql.append(" AND UR.TAKE_CARE_DUE_YN = 'Y' ");
		}
		
		//查詢已落入不符經營頻次
		if (StringUtils.isNotBlank(inputVO.getTAKE_CARE_MATCH_YN())) {
			sql.append(" AND (UR.TAKE_CARE_MATCH_YN = 'N' OR UR.TAKE_CARE_MATCH_YN IS NULL) ");
		}

		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> result = dam.exeQuery(queryCondition);
		
		if (result.isEmpty()) {
			result = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			map.put("DATA_DATE", dateFormat.format(new Date()));
			map.put("BELLOW_UNDERSERV_CNT", 0);
			map.put("NEAR_UNDERSERV_CNT", 0);
			result.add(map);
		}
		
		return_VO2.setResultList5(result);

		sendRtnObject(return_VO2);
	}

	//2018-12-14 by Jacky WMS-CR-20181025-02_新增客戶聯繫頻率管理報表
	//排除當日連繫客戶
	public void updateUnderservCust(String custID) throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("UPDATE TBCRM_UNDERSERV_RPT ");
		sb.append("SET AO_INTERACT_LASTUPDATE = SYSDATE ");
		sb.append("WHERE CUST_ID = :custID");
		
		queryCondition.setObject("custID", custID);
		queryCondition.setQueryString(sb.toString());

		dam.exeUpdate(queryCondition);
	}

	//2018-12-14 by Jacky WMS-CR-20181025-02_新增客戶聯繫頻率管理報表
	//下載客戶清單
	public void export(Object body, IPrimitiveMap header) throws Exception {

		CRM131InputVO inputVO = (CRM131InputVO) body;
		dam = this.getDataAccessManager();

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		// copy cam210 excel
		String fileName = "客戶聯繫頻率管理報表";

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

		String[] headerLine1 = { "分行", "理專", "AO_CODE", "客戶ID", "客戶姓名", "應聯繫頻率(天)", "最新訪談日期", "歸戶總資產", "貢獻度", "理財等級", "最後應聯繫日期" };
		String[] mainLine = { "BRANCH_NAME", "EMP_NAME", "AO_CODE", "CUST_ID", "CUST_NAME", "FRQ_DAY", "AO_INTERACT_LASTUPDATE", "AUM_AMT", "CON_DEGREE", "VIP_DEGREE", "LAST_CONTACT_DATE" };

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
		Map<String, String> vipMap = new XmlInfo().doGetVariable("CRM.VIP_DEGREE", FormatHelper.FORMAT_3);

		for (Map<String, Object> map : inputVO.getResultList()) {
			row = sheet.createRow(index);

			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);

					if (StringUtils.equals("CON_DEGREE", mainLine[j])) {
						cell.setCellValue(conMap.get(checkIsNull(map, mainLine[j])));
					} else if (StringUtils.equals("VIP_DEGREE", mainLine[j])) {
						cell.setCellValue(vipMap.get(checkIsNull(map, mainLine[j])));
					} else if (StringUtils.equals("AO_INTERACT_LASTUPDATE", mainLine[j]) || StringUtils.equals("LAST_CONTACT_DATE", mainLine[j])) {
						cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, mainLine[j])) ? sdf2.format(sdf2.parse(checkIsNull(map, mainLine[j]))) : null);
					}
					/** 資料隱蔽欄位 **/
					else if (StringUtils.equals("EMP_NAME", mainLine[j])) {
						cell.setCellValue(DataFormat.getNameForHighRisk(checkIsNull(map, mainLine[j])));
					} else if (StringUtils.equals("CUST_ID", mainLine[j])) {
						cell.setCellValue(DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, mainLine[j])));
					} else if (StringUtils.equals("CUST_NAME", mainLine[j])) {
						cell.setCellValue(DataFormat.getNameForHighRisk(checkIsNull(map, mainLine[j])));
					}
					/****************/
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

	private String checkIsNull(Map map, String key) {
		if (null != map && null != map.get(key))
			return String.valueOf(map.get(key));
		else
			return "";
	}
}
