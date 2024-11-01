package com.systex.jbranch.app.server.fps.org140;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 2016/07/05 add by Carley
 * 2017/04/27 modify by ocean - 由理專證照改為各角色證照
 * 2022/09/22 modify by ocean - WMS-CR-20220325-01_業管系統新增證券查詢資訊及報表
 * 2024/10/01 modify by ocean - WMS-CR-20231026-01_分行人員內外部證照比對差異報表
 */
@Component("org140")
@Scope("request")
public class ORG140 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void getRoleList (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG140InputVO inputVO = (ORG140InputVO) body;
		ORG140OutputVO outputVO = new ORG140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_CODE AS DATA, PARAM_NAME AS LABEL ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_TYPE IN ('ORG.CERT_MGR', 'ORG.CERT_FC', 'ORG.CERT_PS', 'ORG.CERT_OP_1', 'ORG.CERT_OP_2', 'ORG.CERT_NEWAO', 'ORG.CERT_PAO') ");
		sb.append("ORDER BY PARAM_TYPE, PARAM_ORDER, PARAM_CODE ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setRoleList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG140InputVO inputVO = (ORG140InputVO) body;
		ORG140OutputVO outputVO = new ORG140OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH CERT_GROUP_LIST AS ( ");
		sb.append("  SELECT PARAM_TYPE AS CERT_GROUP, PARAM_CODE AS PRIVILEGEID ");
		sb.append("  FROM TBSYSPARAMETER ");
		sb.append("  WHERE PARAM_TYPE IN ('ORG.CERT_MGR', 'ORG.CERT_FC', 'ORG.CERT_PS', 'ORG.CERT_OP_1', 'ORG.CERT_OP_2', 'ORG.CERT_NEWAO', 'ORG.CERT_PAO') ");
		sb.append(") ");
		sb.append(", CERT_MAIN_LIST AS ( ");
		sb.append("  SELECT REPLACE(PARAM_TYPE, '_LIST', '') AS CERT_GROUP, PARAM_CODE AS CERT_ID, PARAM_NAME AS CERT_NAME, PARAM_DESC AS CERT_GET_TYPE, PARAM_ORDER AS CERT_ORDER ");
		sb.append("  FROM TBSYSPARAMETER ");
		sb.append("  WHERE PARAM_TYPE IN (SELECT CERT_GROUP||'_LIST' FROM CERT_GROUP_LIST) ");
		sb.append(") ");
		sb.append(", MEM_CERT_LIST AS ( ");
		sb.append("  SELECT M.EMP_ID, ");
		sb.append("         MR.ROLE_ID, ");
		sb.append("         R.ROLE_NAME, ");
		sb.append("         MR.IS_PRIMARY_ROLE, ");
		sb.append("         CERT_LIST.PARAM_CODE AS CERTIFICATE_CODE, ");
		sb.append("         CERT_LIST.PARAM_NAME AS CERTIFICATE_NAME, ");
		sb.append("         MC.REG_TYPE, ");
		sb.append("         CASE WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NOT NULL AND (MC.UNREG_DATE IS NOT NULL AND TO_CHAR(MC.REG_DATE, 'yyyyMMdd') < TO_CHAR(MC.UNREG_DATE, 'yyyyMMdd')) THEN NULL "); // 若為 必要取得證照 且 人資證照檔登錄日期有值 且(註銷日有值 且 登錄日 < 註銷日) → 顯示空值
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NOT NULL THEN TO_CHAR(MC.REG_DATE, 'yyyy/MM/dd') "); // 若為 必要取得證照 且 人資證照檔登錄日期有值 → 顯示人資證照檔登錄日期
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL AND (MC_INS.UNREG_DATE IS NOT NULL AND TO_CHAR(MC_INS.REG_DATE, 'yyyyMMdd') < TO_CHAR(MC_INS.UNREG_DATE, 'yyyyMMdd')) THEN NULL "); // 若為 必要取得證照 且 人資證照檔登錄日期為空值 且 保險證照為01(Cert0011)/02(Cert0014)/03(Cert0051)/04(Cert0014) 且 人資證照檔登錄日期有值 且(註銷日有值 且 登錄日 < 註銷日) → 顯示空值
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL THEN TO_CHAR(MC_INS.REG_DATE, 'yyyy/MM/dd') "); // 若為 必要取得證照 且 人資證照檔登錄日期為空值 且 保險證照為01(Cert0011)/02(Cert0014)/03(Cert0051)/04(Cert0014)  → 顯示保險證照檔登錄日期
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NOT NULL THEN TO_CHAR(MC.REG_DATE, 'yyyy/MM/dd') "); 
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NOT NULL THEN TO_CHAR(MC.CERTIFICATE_GET_DATE, 'yyyy/MM/dd') "); // 若為 非必要取得證照 且 人資證照檔登錄日期為空值 且 人資證照檔取得日期有值 → 顯示人資證照檔取得日期
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NULL AND MC.APPLY_DATE IS NOT NULL THEN TO_CHAR(MC.APPLY_DATE, 'yyyy/MM/dd') "); // 若為 非必要取得證照 且 人資證照檔登錄日期為空值 且 人資證照檔取得日期為空值 且 人資證照檔申請日期有值 → 顯示人資證照檔申請日期
		sb.append("         ELSE NULL END AS REG_DATE, "); // 若以上條件皆不符合 → 顯示人資證照檔登錄日期
		sb.append("         MC.CERTIFICATE_GET_DATE, ");
		sb.append("         PRI.PRIVILEGEID, ");
		sb.append("         CERT_LIST.PARAM_ORDER AS CERT_ORDER, ");
		sb.append("         CERT_LIST.PARAM_DESC AS CERT_GET_TYPE ");
		sb.append("  FROM TBORG_MEMBER M ");
		sb.append("  LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID "); // AND MR.IS_PRIMARY_ROLE = 'Y'
		sb.append("  LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sb.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON R.ROLE_ID = PRI.ROLEID ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_TYPE ON CERT_TYPE.PARAM_TYPE LIKE 'ORG.CERT_%' AND PRI.PRIVILEGEID IN CERT_TYPE.PARAM_CODE ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_LIST ON CERT_TYPE.PARAM_TYPE||'_LIST' = CERT_LIST.PARAM_TYPE ");
		sb.append("  LEFT JOIN TBORG_MEMBER_CERT MC ON M.EMP_ID = MC.EMP_ID AND CERT_LIST.PARAM_CODE = MC.CERTIFICATE_CODE ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_MAPP ON CERT_MAPP.PARAM_TYPE = 'ORG.CERT_MAPPING' AND CERT_LIST.PARAM_CODE = CERT_MAPP.PARAM_CODE ");
		sb.append("  LEFT JOIN TBORG_MEMBER_CERT MC_INS ON M.EMP_ID = MC_INS.EMP_ID AND CERT_MAPP.PARAM_DESC = MC_INS.CERTIFICATE_CODE ");
		sb.append("  WHERE MR.ROLE_ID IS NOT NULL ");
		sb.append("  AND R.JOB_TITLE_NAME IS NOT NULL ");
		
		sb.append(") ");
		
		sb.append("SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
		sb.append("       BRANCH_NBR, BRANCH_NAME, ");
		sb.append("       EMP_ID, ");
		sb.append("       EMP_NAME, ");
		sb.append("       ROLE_ID, ");
		sb.append("       JOB_TITLE_NAME, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       IS_PRIMARY_ROLE, ");
		sb.append("       PRIVILEGEID, ");
		sb.append("       CERT_GROUP, ");
		sb.append("       CHOOSE_ONE_LIST, ");
		sb.append("       CHOOSE_ONE_GET_LIST, ");
		sb.append("       CHOOSE_ONE_NON_GET_LIST, ");
		sb.append("       ESSENTIAL_LIST, ");
		sb.append("       ESSENTIAL_GET_LIST, ");
		sb.append("       ESSENTIAL_NON_GET_LIST, ");
		sb.append("       EXPECTED_END_DATE, ");
		sb.append("       EXPECTED_END_FLAG, ");
		sb.append("       ONBOARD_DATE ");
		sb.append("FROM ( ");
		sb.append("  SELECT BASE.REGION_CENTER_ID, BASE.REGION_CENTER_NAME, ");
		sb.append("         BASE.BRANCH_AREA_ID, BASE.BRANCH_AREA_NAME, ");
		sb.append("         BASE.BRANCH_NBR, BASE.BRANCH_NAME, ");
		sb.append("         BASE.EMP_ID, ");
		sb.append("         BASE.EMP_NAME, ");
		sb.append("         BASE.ROLE_ID, ");
		sb.append("         BASE.ROLE_NAME, ");
		sb.append("         BASE.IS_PRIMARY_ROLE, ");
		sb.append("         CASE WHEN M.JOB_TITLE_NAME IS NOT NULL THEN M.JOB_TITLE_NAME WHEN MP.JOB_TITLE_NAME IS NOT NULL THEN MP.JOB_TITLE_NAME ELSE NULL END AS JOB_TITLE_NAME, PRI.PRIVILEGEID, CGL.CERT_GROUP, ");
		sb.append("         (SELECT LISTAGG(CERT_ID, ',') WITHIN GROUP (ORDER BY CERT_ORDER) FROM CERT_MAIN_LIST CML WHERE CML.CERT_GROUP = CGL.CERT_GROUP AND CML.CERT_GET_TYPE = 'CHOOSE_ONE') AS CHOOSE_ONE_LIST, ");
		sb.append("         (SELECT LISTAGG(CERTIFICATE_CODE, ',') WITHIN GROUP (ORDER BY CERT_ORDER) FROM MEM_CERT_LIST MCL WHERE MCL.EMP_ID = BASE.EMP_ID AND MCL.CERT_GET_TYPE = 'CHOOSE_ONE') AS CHOOSE_ONE_GET_LIST, ");
		sb.append("         (SELECT LISTAGG(CERT_ID, ',') WITHIN GROUP (ORDER BY CERT_ORDER) FROM CERT_MAIN_LIST CML WHERE CML.CERT_GROUP = CGL.CERT_GROUP AND CML.CERT_GET_TYPE = 'CHOOSE_ONE' AND CERT_ID NOT IN (SELECT CERTIFICATE_CODE FROM MEM_CERT_LIST MCL WHERE MCL.EMP_ID = BASE.EMP_ID AND MCL.CERT_GET_TYPE = 'CHOOSE_ONE' AND MCL.REG_DATE IS NOT NULL)) AS CHOOSE_ONE_NON_GET_LIST, ");
		sb.append("         (SELECT LISTAGG(CERT_ID, ',') WITHIN GROUP (ORDER BY CERT_ORDER) AS ROLE_ID FROM CERT_MAIN_LIST CML WHERE CML.CERT_GROUP = CGL.CERT_GROUP AND CML.CERT_GET_TYPE = 'ESSENTIAL') AS ESSENTIAL_LIST, ");
		sb.append("         (SELECT LISTAGG(CERTIFICATE_CODE, ',') WITHIN GROUP (ORDER BY CERT_ORDER) FROM MEM_CERT_LIST MCL WHERE MCL.EMP_ID = BASE.EMP_ID AND MCL.CERT_GET_TYPE = 'ESSENTIAL') AS ESSENTIAL_GET_LIST, ");
		sb.append("         (SELECT LISTAGG(CERT_ID, ',') WITHIN GROUP (ORDER BY CERT_ORDER) AS ROLE_ID FROM CERT_MAIN_LIST CML WHERE CML.CERT_GROUP = CGL.CERT_GROUP AND CML.CERT_GET_TYPE = 'ESSENTIAL' AND CERT_ID NOT IN (SELECT CERTIFICATE_CODE FROM MEM_CERT_LIST MCL WHERE MCL.EMP_ID = BASE.EMP_ID AND MCL.CERT_GET_TYPE = 'ESSENTIAL' AND MCL.REG_DATE IS NOT NULL)) AS ESSENTIAL_NON_GET_LIST, ");
		sb.append("         M.EXPECTED_END_DATE, CASE WHEN M.EXPECTED_END_DATE IS NULL THEN '✓' ELSE NULL END AS EXPECTED_END_FLAG, M.ONBOARD_DATE ");
		sb.append("  FROM ( ");
		sb.append("    SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, ROLE_ID, JOB_TITLE_NAME, ROLE_TYPE AS IS_PRIMARY_ROLE, ROLE_NAME ");
		sb.append("    FROM VWORG_EMP_INFO ");
		sb.append("  ) BASE ");
		sb.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON BASE.ROLE_ID = PRI.ROLEID ");
		sb.append("  LEFT JOIN CERT_GROUP_LIST CGL ON CGL.PRIVILEGEID = PRI.PRIVILEGEID ");
		sb.append("  LEFT JOIN TBORG_MEMBER M ON BASE.EMP_ID = M.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER_PLURALISM MP ON BASE.EMP_ID = MP.EMP_ID ");
		sb.append("  WHERE CGL.PRIVILEGEID IN (SELECT PRIVILEGEID FROM CERT_GROUP_LIST) ");
		sb.append(") A ");
		sb.append("WHERE 1 = 1 "); //((CHOOSE_ONE_LIST IS NOT NULL AND CHOOSE_ONE_GET_LIST IS NULL) OR (ESSENTIAL_LIST <> ESSENTIAL_GET_LIST)) ");

		// 為高端組織下，但非uhrm人員
		// 2023/08/02 ADD MARK BY OCEAN : WMS-CR-20230714-01_高端業務移轉人管科報表功能調整
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
			!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE A.EMP_ID = U.EMP_ID) ");
		} else {
			// 20210412 by 慧芝 : 證照大表乃個金總處客群業務處專屬之表單，請排除高端總處人員。 ==> modify by ocean
//			sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = A.EMP_ID) ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND REGION_CENTER_ID = :regID "); //區域代碼
			queryCondition.setObject("regID", inputVO.getRegion_center_id());
		} else {
			sb.append(" AND REGION_CENTER_ID IN (:rcIdList) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
		
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND BRANCH_AREA_ID = :branID "); //營運區代碼
			queryCondition.setObject("branID", inputVO.getBranch_area_id());
		} else {
			sb.append(" AND (BRANCH_AREA_ID IN (:opIdList) OR BRANCH_AREA_ID IS NULL) ");
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND BRANCH_NBR like :branNbr "); //分行代碼
			queryCondition.setObject("branNbr", inputVO.getBranch_nbr());
		} else {
			sb.append(" AND (BRANCH_NBR IN (:brNbrList) OR BRANCH_NBR IS NULL) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		if (!StringUtils.isBlank(inputVO.getEmpId())) {
			sb.append("AND EMP_ID like :empID "); //員工代碼
			queryCondition.setObject("empID", inputVO.getEmpId() + "%");
		}	
		
		if (!StringUtils.isBlank(inputVO.getEmpName())) {
			sb.append("AND EMP_NAME like :empName "); //員工姓名
			queryCondition.setObject("empName", inputVO.getEmpName() + "%");
		}
		
		if ("Y".equals(inputVO.getProbation())) {
			sb.append("AND EXPECTED_END_DATE IS NULL "); //過試用期 
		} else if ("N".equals(inputVO.getProbation())){
			sb.append("AND EXPECTED_END_DATE IS NOT NULL "); //未過試用期
		}
		
		if (null != inputVO.getOnboardDateStart()){   //入行日-起
			sb.append("AND TO_CHAR(ONBOARD_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getOnboardDateStart());			
		}
			
		if (null != inputVO.getOnboardDateEnd()){    //入行日-迄
			sb.append("AND TO_CHAR(ONBOARD_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", inputVO.getOnboardDateEnd());			
		}
		
		if (StringUtils.isNotBlank(inputVO.getCertID())){
			sb.append("AND ((NVL(LENGTH(CHOOSE_ONE_LIST), 0) > 0 AND NVL(LENGTH(CHOOSE_ONE_GET_LIST), 0) = 0 AND NVL(INSTR(CHOOSE_ONE_NON_GET_LIST, :certID, 1), 0) > 0) OR ");
			sb.append("     (INSTR(ESSENTIAL_NON_GET_LIST, :certID, 1) IS NOT NULL AND INSTR(ESSENTIAL_NON_GET_LIST, :certID, 1) > 0)) ");
			queryCondition.setObject("certID", inputVO.getCertID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getPrivilegeID())) {
			sb.append("AND PRIVILEGEID = :privilegeID ");
			queryCondition.setObject("privilegeID", inputVO.getPrivilegeID());
		}
		
		sb.append("ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, JOB_TITLE_NAME ");
				
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}

	/*  === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		ORG140InputVO inputVO = (ORG140InputVO) body;
		ORG140OutputVO outputVO = new ORG140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String fileName = "證照表_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		StringBuffer certTitleSB = new StringBuffer();
		certTitleSB.append("  SELECT PARAM_CODE AS CERT_ID, PARAM_NAME AS CERT_NAME, PARAM_DESC AS CERT_DESC ");
		certTitleSB.append("  FROM TBSYSPARAMETER ");
		certTitleSB.append("  WHERE PARAM_TYPE = 'ORG.CERT_ALL' ");
		certTitleSB.append("  ORDER BY PARAM_ORDER ");
		queryCondition.setQueryString(certTitleSB.toString());
		List<Map<String, Object>> certTitleList = dam.exeQuery(queryCondition);
		
		String certStr = "";
		String str = "";
		for (int i = 0; i < certTitleList.size(); i++) {
			str = "'" + certTitleList.get(i).get("CERT_ID") + "' AS " + certTitleList.get(i).get("CERT_ID").toString().toUpperCase();
			certStr = certStr + str;
			if ((i + 1) != certTitleList.size()) {
				certStr = certStr + ", ";
			}
		}
		
		ArrayList<String> headerLine = new ArrayList<String>();
		String[] headerLine1 = {"處別", "營運區", "分行代號", "分行別", "姓名", "員編", "職務", "是否為主要角色", "角色名稱"};
		String[] headerLine2 = {"過試用期", "入行日", "最後上班日"};
		
		for (int i = 0; i < headerLine1.length; i++) {
			headerLine.add(headerLine1[i]);
		}
		
		for (Map<String, Object> map : certTitleList) {
			headerLine.add((String) map.get("CERT_NAME"));
		}
		
		for (int i = 0; i < headerLine2.length; i++) {
			headerLine.add(headerLine2[i]);
		}
		
		ArrayList<String> mainLine = new ArrayList<String>();
		String[] mainLine1 = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "EMP_NAME", "EMP_ID", "JOB_TITLE_NAME", "IS_PRIMARY_ROLE", "ROLE_NAME"};
		String[] mainLine2 = {"EXPECTED_END_FLAG", "ONBOARD_DATE", "JOB_RESIGN_DATE"};
		
		for (int i = 0; i < mainLine1.length; i++) {
			mainLine.add(mainLine1[i]);
		}
		
		for (Map<String, Object> map : certTitleList) {
			mainLine.add((String) map.get("CERT_ID"));
		}
		
		for (int i = 0; i < mainLine2.length; i++) {
			mainLine.add(mainLine2[i]);
		}

		/* 據富邦 DBA 轉述 Oracle 技術人員說法，需在這裡加上 下列語法以避免錯誤。
		   技術人員指出這是 Oracle 的 Bug */
		String[] voidBugSqls = {"ALTER SESSION DISABLE PARALLEL DML", "ALTER SESSION DISABLE PARALLEL DDL", "ALTER SESSION DISABLE PARALLEL QUERY"};
		for (String sql: voidBugSqls)
			Manager.manage(dam).append(sql).update();

		StringBuffer sb = new StringBuffer();
		sb.append("WITH DEPT AS ( ");
		sb.append("  SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM VWORG_DEPT_BR ");
		sb.append(") ");
		sb.append(", CERT_ALL AS ( ");
		sb.append(certTitleSB.toString());
		sb.append(") ");

		sb.append("SELECT * ");
		sb.append("FROM ( ");
		sb.append("  SELECT MEM_LIST.PRIVILEGEID, ");
		sb.append("         MEM_LIST.REGION_CENTER_NAME, ");
		sb.append("         MEM_LIST.BRANCH_AREA_NAME, ");
		sb.append("         MEM_LIST.BRANCH_NBR, ");
		sb.append("         MEM_LIST.BRANCH_NAME, ");
		sb.append("         MEM_LIST.EMP_NAME, ");
		sb.append("         MEM_LIST.EMP_ID, ");
		sb.append("         MEM_LIST.JOB_TITLE_NAME, ");
		sb.append("         MEM_LIST.ROLE_NAME, ");
		sb.append("         CASE WHEN MEM_LIST.IS_PRIMARY_ROLE = 'Y' THEN '是' ELSE '否' END AS IS_PRIMARY_ROLE, ");
		sb.append("         CERT_ALL.CERT_ID, ");
		sb.append("         CASE WHEN CERT_ALL.CERT_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NOT NULL AND (MC.UNREG_DATE IS NOT NULL AND TO_CHAR(MC.REG_DATE, 'yyyyMMdd') < TO_CHAR(MC.UNREG_DATE, 'yyyyMMdd')) THEN NULL ");	// 若為 必要取得證照 且 人資證照檔登錄日期有值 且(註銷日有值 且 登錄日 < 註銷日) → 顯示空值
		sb.append("              WHEN CERT_ALL.CERT_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NOT NULL THEN TO_CHAR(MC.REG_DATE, 'yyyy/MM/dd') "); // 若為 必要取得證照 且 人資證照檔登錄日期有值 → 顯示人資證照檔登錄日期
		sb.append("              WHEN CERT_ALL.CERT_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL AND (MC_INS.UNREG_DATE IS NOT NULL AND TO_CHAR(MC_INS.REG_DATE, 'yyyyMMdd') < TO_CHAR(MC_INS.UNREG_DATE, 'yyyyMMdd')) THEN NULL "); // 若為 必要取得證照 且 人資證照檔登錄日期為空值 且 保險證照為01(Cert0011)/02(Cert0014)/03(Cert0051)/04(Cert0014) 且 人資證照檔登錄日期有值 且(註銷日有值 且 登錄日 < 註銷日) → 顯示空值
		sb.append("              WHEN CERT_ALL.CERT_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL THEN TO_CHAR(MC_INS.REG_DATE, 'yyyy/MM/dd') "); // 若為 必要取得證照 且 人資證照檔登錄日期為空值 且 保險證照為01(Cert0011)/02(Cert0014)/03(Cert0051)/04(Cert0014)  → 顯示保險證照檔登錄日期
		sb.append("              WHEN CERT_ALL.CERT_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NOT NULL THEN '已取得未登錄' "); // 若為 必要取得證照 且 人資證照檔登錄日期為空值 且 人資證照檔取得日期有值 → 顯示「已取得未登錄」
		sb.append("              WHEN CERT_ALL.CERT_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NOT NULL THEN TO_CHAR(MC.REG_DATE, 'yyyy/MM/dd') "); // 
		sb.append("              WHEN CERT_ALL.CERT_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NOT NULL THEN TO_CHAR(MC.CERTIFICATE_GET_DATE, 'yyyy/MM/dd') "); // 若為 非必要取得證照 且 人資證照檔登錄日期為空值 且 人資證照檔取得日期有值 → 顯示人資證照檔取得日期
		sb.append("              WHEN CERT_ALL.CERT_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NULL AND MC.APPLY_DATE IS NOT NULL THEN TO_CHAR(MC.APPLY_DATE, 'yyyy/MM/dd') "); // 若為 非必要取得證照 且 人資證照檔登錄日期為空值 且 人資證照檔取得日期為空值 且 人資證照檔申請日期有值 → 顯示人資證照檔申請日期
		sb.append("         ELSE NULL END AS REG_DATE, "); // 若以上條件皆不符合 → 顯示人資證照檔登錄日期
		sb.append("         EXPECTED_END_FLAG, TO_CHAR(ONBOARD_DATE, 'yyyy/MM/dd') AS ONBOARD_DATE, TO_CHAR(JOB_RESIGN_DATE, 'yyyy/MM/dd') AS JOB_RESIGN_DATE ");
		sb.append("  FROM CERT_ALL ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, ");
		sb.append("           INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, ");
		sb.append("           INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
		sb.append("           MBR.EMP_ID, ");
		sb.append("           MBR.EMP_NAME, ");
		sb.append("           MBR.JOB_TITLE_NAME, ");
		sb.append("           INFO.PRIVILEGEID, ");
		sb.append("           INFO.ROLE_ID, ");
		sb.append("           INFO.ROLE_NAME, ");
		sb.append("           INFO.ROLE_TYPE AS IS_PRIMARY_ROLE, ");
		sb.append("           MBR.EXPECTED_END_DATE, ");
		sb.append("           CASE WHEN MBR.EXPECTED_END_DATE IS NULL THEN '✓' ELSE NULL END AS EXPECTED_END_FLAG, ");
		sb.append("           MBR.ONBOARD_DATE, ");
		sb.append("           MBR.JOB_RESIGN_DATE ");
		sb.append("    FROM VWORG_EMP_INFO INFO ");
		sb.append("    LEFT JOIN TBORG_MEMBER MBR ON INFO.EMP_ID = MBR.EMP_ID ");
		sb.append("    WHERE INFO.PRIVILEGEID IN ( ");
		sb.append("      SELECT PARAM_CODE ");
		sb.append("      FROM TBSYSPARAMETER ");
		sb.append("      WHERE PARAM_TYPE IN ('ORG.CERT_MGR', 'ORG.CERT_FC', 'ORG.CERT_PS', 'ORG.CERT_OP_1', 'ORG.CERT_OP_2', 'ORG.CERT_NEWAO', 'ORG.CERT_PAO') ");
		sb.append("    ) ");
		sb.append("  ) MEM_LIST ON 1 = 1 ");
		sb.append("  LEFT JOIN TBORG_MEMBER_CERT MC ON MEM_LIST.EMP_ID = MC.EMP_ID AND CERT_ALL.CERT_ID = MC.CERTIFICATE_CODE ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_MAPP ON CERT_MAPP.PARAM_TYPE = 'ORG.CERT_MAPPING' AND CERT_ALL.CERT_ID = CERT_MAPP.PARAM_CODE ");
		sb.append("	 LEFT JOIN TBORG_MEMBER_CERT MC_INS ON MEM_LIST.EMP_ID = MC_INS.EMP_ID AND CERT_MAPP.PARAM_DESC = MC_INS.CERTIFICATE_CODE ");
		sb.append("  WHERE 1 = 1 ");
		
		// 為高端組織下，但非uhrm人員
//		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
//			!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
//			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = MEM_LIST.EMP_ID) ");
//		} else {
//			// 20210412 by 慧芝 : 證照大表乃個金總處客群業務處專屬之表單，請排除高端總處人員。 ==> modify by ocean
//			sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = MEM_LIST.EMP_ID) ");
//		}
		
		sb.append("  AND REGION_CENTER_NAME IS NOT NULL ");

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND MEM_LIST.REGION_CENTER_ID = :regID "); //區域代碼
			queryCondition.setObject("regID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND (MEM_LIST.REGION_CENTER_ID IN (:rcIdList) OR MEM_LIST.REGION_CENTER_ID IS NULL) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
		
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND MEM_LIST.BRANCH_AREA_ID = :branID "); //營運區代碼
			queryCondition.setObject("branID", inputVO.getBranch_area_id());
		} else {
			sb.append("AND (MEM_LIST.BRANCH_AREA_ID IN (:opIdList) OR MEM_LIST.BRANCH_AREA_ID IS NULL) ");
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND MEM_LIST.BRANCH_NBR like :branNbr "); //分行代碼
			queryCondition.setObject("branNbr", inputVO.getBranch_nbr());
		} else {
			sb.append("AND (MEM_LIST.BRANCH_NBR IN (:brNbrList) OR MEM_LIST.BRANCH_NBR IS NULL) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		if (!StringUtils.isBlank(inputVO.getEmpId())) {
			sb.append("AND MEM_LIST.EMP_ID like :empID "); //員工代碼
			queryCondition.setObject("empID", "%" + inputVO.getEmpId() + "%");
		}	
		
		if (!StringUtils.isBlank(inputVO.getEmpName())) {
			sb.append("AND MEM_LIST.EMP_NAME like :empName "); //員工姓名
			queryCondition.setObject("empName", "%" + inputVO.getEmpName() + "%");
		}
		
		if ("Y".equals(inputVO.getProbation())) {
			sb.append("AND (TO_CHAR(MEM_LIST.EXPECTED_END_DATE, 'YYYYMMDD') < TO_CHAR(SYSDATE, 'YYYYMMDD') OR MEM_LIST.EXPECTED_END_DATE IS NULL) "); //過試用期 
		} else if ("N".equals(inputVO.getProbation())){
			sb.append("AND (TO_CHAR(MEM_LIST.EXPECTED_END_DATE, 'YYYYMMDD') > TO_CHAR(SYSDATE, 'YYYYMMDD') AND MEM_LIST.EXPECTED_END_DATE IS NOT NULL) "); //未過試用期
		}
		
		if (null != inputVO.getOnboardDateStart()){   //入行日-起
			sb.append("AND TO_CHAR(MEM_LIST.ONBOARD_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getOnboardDateStart());			
		}
			
		if (null != inputVO.getOnboardDateEnd()){    //入行日-迄
			sb.append("AND TO_CHAR(MEM_LIST.ONBOARD_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", inputVO.getOnboardDateEnd());			
		}
		
		if (StringUtils.isNotBlank(inputVO.getPrivilegeID())) {
			sb.append("AND MEM_LIST.PRIVILEGEID = :privilegeID ");
			queryCondition.setObject("privilegeID", inputVO.getPrivilegeID());
		}
		
		sb.append(") PIVOT (MAX(REG_DATE) FOR CERT_ID IN (").append(certStr).append(")) ");

		if (StringUtils.isNotBlank(inputVO.getCertID())){
			sb.append("WHERE ").append(inputVO.getCertID().toUpperCase()).append(" IS NULL ");
		}
		
		sb.append("ORDER BY REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NBR, ROLE_NAME, EMP_ID ");
				
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("證照表_" + sdfYYYYMMDD.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
		// 表頭 CELL型式
		XSSFCellStyle headingStyle = wb.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);
		
		Integer index = 0; // first row
		
		// Heading
		XSSFRow row = sheet.createRow(index);
		
		row = sheet.createRow(index);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerLine.size(); i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine.get(i));
		}
		
		index++;
		
		// 資料 CELL型式
		XSSFCellStyle mainStyle = wb.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			for (int j = 0; j < mainLine.size(); j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				cell.setCellValue(checkIsNull(list.get(i), mainLine.get(j).toUpperCase()));
			}
			index++;
		}
		
		wb.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
		sendRtnObject(outputVO);
	}	
	
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}	
}
