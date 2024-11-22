package com.systex.jbranch.app.server.fps.pms364;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms364")
@Scope("request")
public class PMS364 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public StringBuffer returnAppend (String genType, String sqlType) {
		
		StringBuffer sb = new StringBuffer();
		
		switch (genType) {
			case "ALL":
				sb.append("       YYYYMM").append(", ");
				sb.append("       'ALL_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       '全行 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS CUST_AO" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				break;
			case "REGION":
				sb.append("       YYYYMM").append(", ");
				sb.append("       REGION_CENTER_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       REGION_CENTER_NAME || ' 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", ");
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS CUST_AO" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				break;
			case "AREA":
				sb.append("       YYYYMM").append(", ");
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", ");
				sb.append("       BRANCH_AREA_NAME || ' 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR " : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME " : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS CUST_AO" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				break;
			case "BRANCH":
				sb.append("       YYYYMM").append(", ");
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", ");
				sb.append("       BRANCH_NAME || ' 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS CUST_AO" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				break;
			case "EMP":
				sb.append("       YYYYMM").append(", ");
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR").append(", ");
				sb.append("       BRANCH_NAME").append(", ");
				sb.append("       EMP.DEPT_ID").append(", ");
				sb.append("       AO_CODE").append(", ");
				sb.append("       AO_CODE_TYPE").append(", ");
				sb.append("       AO_EMP_ID").append(", ");
				sb.append("       CUST_AO").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				break;
			case "ORDER" :
				sb.append("ORDER BY DECODE(REGION_CENTER_ID, '000', 999, 0), ");
				sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
				sb.append("         REGION_CENTER_NAME, ");
				sb.append("         DECODE(BRANCH_AREA_ID, '000', 999, 0), ");
				sb.append("         BRANCH_AREA_NAME, ");
				sb.append("         DECODE(BRANCH_NBR, '000', 999, 0), ");
				sb.append("         BRANCH_NAME, ");
				sb.append("         DECODE(AO_EMP_ID, '000', 999, 0), ");         
				sb.append("         AO_EMP_ID, ");    
				sb.append("         AO_CODE_TYPE ");
				break;
		}
		
		return sb;
	}
	
	public StringBuffer genSQL (String genType, PMS364InputVO inputVO, boolean isFC, boolean isHeadMGR, String memLoginFlag, String NOT_EXIST_BS, String NOT_EXIST_UHRM) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(returnAppend(genType, "SELECT"));
		sb.append("       SUM(SUM_PROF_INVESTOR_Y) AS SUM_PROF_INVESTOR_Y, ");
		sb.append("       SUM(SUM_PROF_INVESTOR_N) AS SUM_PROF_INVESTOR_N, ");
		sb.append("       SUM(SUM_PROF_INVESTOR_A_Y) AS SUM_PROF_INVESTOR_A_Y, ");
		sb.append("       SUM(SUM_PROF_INVESTOR_A_N) AS SUM_PROF_INVESTOR_A_N, ");
		sb.append("       SUM(SUM_PROF_INVESTOR_C_Y) AS SUM_PROF_INVESTOR_C_Y, ");
		sb.append("       SUM(SUM_PROF_INVESTOR_C_N) AS SUM_PROF_INVESTOR_C_N, ");
		sb.append("       SUM(SUM_TX_FLAG_Y) AS SUM_TX_FLAG_Y, ");
		sb.append("       SUM(SUM_IS_EFFETIVE_Y) AS SUM_IS_EFFETIVE_Y, ");
		sb.append("       SUM(SUM_COMPLAIN_Y) AS SUM_COMPLAIN_Y, ");
		sb.append("       SUM(SUM_COMM_RS_Y) AS SUM_COMM_RS_Y, ");
		sb.append("       SUM(SUM_COMM_NS_Y) AS SUM_COMM_NS_Y ");
		sb.append("FROM TBPMS_MONTHLY_CUST_NOTE MCN ");
		sb.append("LEFT JOIN (SELECT DISTINCT EMP_ID, EMP_DEPT_ID AS DEPT_ID FROM VWORG_EMP_INFO) EMP ON MCN.AO_EMP_ID = EMP.EMP_ID ");
		sb.append("WHERE 1 = 1 ");
		
		// ==主查詢條件==
		if (!StringUtils.isBlank(inputVO.getsCreDate())) {
			if (!inputVO.getsCreDate().toString().equals("0")) {
				sb.append("AND YYYYMM = :STARTTIME ");
			}
		}
		
		switch (genType) {
			case "EMP":
				// 人員
				if (StringUtils.isNotBlank(inputVO.getAo_code())) {
					sb.append("AND AO_CODE = :ao_code ");
				} else if (isFC) {
					sb.append("AND AO_EMP_ID = :empID ");
				} else if (StringUtils.equals(memLoginFlag, "UHRM")) {
					sb.append("AND AO_EMP_ID = :loginID ");
				}
				
			case "BRANCH":
				switch (memLoginFlag) {
					case "UHRM":
						break;
					case "uhrmMGR":
					case "uhrmBMMGR":
						sb.append("  AND ( ");
						sb.append("           EMP_ID IS NOT NULL ");
						sb.append("       AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE EMP.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
						sb.append("  ) ");
						
						break;
					default:
						if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
							sb.append("AND BRANCH_NBR = :branch_nbr ");
						} else if (!isHeadMGR) {
							// 登入非總行人員強制加分行
							sb.append("AND BRANCH_NBR IN (:branch_nbr) ");
						}
						
						break;
				}
			case "AREA":
				switch (memLoginFlag) {
					case "UHRM":
						break;
					case "uhrmMGR":
					case "uhrmBMMGR":
						sb.append("  AND ( ");
						sb.append("           EMP_ID IS NOT NULL ");
						sb.append("       AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE EMP.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
						sb.append("  ) ");
						
						break;
					default:
						if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
							sb.append("AND BRANCH_AREA_ID = :branch_area_id ");
						} else if (!isHeadMGR) {
							// 登入非總行人員強制加營運區
							sb.append("AND BRANCH_AREA_ID IN (:branch_area_id) ");
						}
						
						break;
				}
				
			case "REGION":
				switch (memLoginFlag) {
					case "UHRM":
						break;
					case "uhrmMGR":
					case "uhrmBMMGR":
						sb.append("  AND ( ");
						sb.append("           EMP_ID IS NOT NULL ");
						sb.append("       AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE EMP.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
						sb.append("  ) ");
						
						break;
					default:
						if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
							sb.append("AND REGION_CENTER_ID = :region_center_id ");
						} else if (!isHeadMGR) {
							// 登入非總行人員強制加區域中心
							sb.append("AND REGION_CENTER_ID IN (:region_center_id) ");
						}
						break;
				}
			case "ALL":
				if (StringUtils.equals("Y", NOT_EXIST_BS)) {
					sb.append("AND NOT EXISTS ( ");
					sb.append("  SELECT 1 ");
					sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
					sb.append("  WHERE 1 = 1 ");
					sb.append("  AND LAST_DAY(TO_DATE(:STARTTIME || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
					sb.append("  AND IS_PRIMARY_ROLE = 'N' ");
					sb.append("  AND AO_JOB_RANK IS NOT NULL ");
					sb.append("  AND NOT EXISTS (SELECT 1 FROM TBPMS_EMPLOYEE_REC_N UCN_T WHERE UCN_T.DEPT_ID IN ('031', '1001') AND UCN.EMP_ID = UCN_T.EMP_ID) ");
					sb.append("  AND MCN.AO_EMP_ID = UCN.EMP_ID ");
					sb.append(") ");
				}
				
				switch (NOT_EXIST_UHRM) {
					case "Y":
						sb.append("AND NOT EXISTS ( ");
						sb.append("  SELECT 1 ");
						sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND LAST_DAY(TO_DATE(:STARTTIME || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
						sb.append("  AND UCN.DEPT_ID <> UCN.E_DEPT_ID "); // 私銀理專
						sb.append("  AND MCN.AO_EMP_ID = UCN.EMP_ID ");
						sb.append(") ");
						break;
					case "U":
						sb.append("AND EXISTS ( ");
						sb.append("  SELECT 1 ");
						sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND LAST_DAY(TO_DATE(:STARTTIME || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
						sb.append("  AND UCN.DEPT_ID <> UCN.E_DEPT_ID "); // 私銀理專
						sb.append("  AND MCN.AO_EMP_ID = UCN.EMP_ID ");
						sb.append(") ");
				}
				break;
		}
		
		
		sb.append("GROUP BY ");
		sb.append(returnAppend(genType, "GROUP"));
		
		return sb;
	}

	// 取得統計
	public void queryData (Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS364InputVO inputVO = (PMS364InputVO) body;
		PMS364OutputVO outputVO = new PMS364OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		XmlInfo xmlInfo = new XmlInfo();
		boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isPSOP = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isOPMGR = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMBMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMBMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		String memLoginFlag = getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG).toString();
		
		//===取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();

		if (!StringUtils.isBlank(inputVO.getsCreDate())) {
			if (!inputVO.getsCreDate().toString().equals("0"))
				pms000InputVO.setReportDate(inputVO.getsCreDate());
		}

		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		//===
		
		sb.append("SELECT * ");
		sb.append("FROM ( "); 
		
		// 人員
		sb.append(genSQL("EMP", inputVO, isFC, isHANDMGR, memLoginFlag, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 分行合計
		sb.append(genSQL("BRANCH", inputVO, isFC, isHANDMGR, memLoginFlag, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 營運區合計
		sb.append(genSQL("AREA", inputVO, isFC, isHANDMGR, memLoginFlag, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 業務處合計
		sb.append(genSQL("REGION", inputVO, isFC, isHANDMGR, memLoginFlag, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 

		sb.append("UNION "); 
		
		// 全行合行
		sb.append(genSQL("ALL", inputVO, isFC, isHANDMGR, memLoginFlag, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 

		sb.append(") "); 
		sb.append(returnAppend("ORDER", "").toString().replaceAll("\\s+", " "));
		
		if (!StringUtils.isBlank(inputVO.getsCreDate())) {
			if (!inputVO.getsCreDate().toString().equals("0")) {
				queryCondition.setObject("STARTTIME", inputVO.getsCreDate());
			}
		}
		
		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			queryCondition.setObject("ao_code", inputVO.getAo_code());
		} else if (isFC) {
			queryCondition.setObject("empID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		}
		
		switch (memLoginFlag) {
			case "UHRM":
				queryCondition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
				
				break;
			case "uhrmMGR":
			case "uhrmBMMGR":
				queryCondition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
				queryCondition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
				
				break;
			default:
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
				} else if (!isHANDMGR) {
					// 登入非總行人員強制加分行
					queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
				
				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
					queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
				} else if (!isHANDMGR) {
					// 登入非總行人員強制加營運區
					queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}

				// 區域中心
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
					queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
				} else if (!isHANDMGR) {
					// 登入非總行人員強制加區域中心
					queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
				
				break;
		}
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setResultList(list);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		
		this.sendRtnObject(outputVO);
	}
	
	// 取得明細
	public void getDTL (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS364InputVO inputVO = (PMS364InputVO) body;
		PMS364OutputVO outputVO = new PMS364OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT BRANCH_NBR, "); 
		sb.append("       BRANCH_NAME, "); 
		sb.append("       CUST_AO, "); 
		sb.append("       CUST_ID, "); 
		sb.append("       CUST_NAME, "); 
		sb.append("       TO_CHAR(PROF_INVESTOR_DATE, 'yyyy-MM-dd') AS PROF_INVESTOR_DATE, "); 
		sb.append("       TX_FLAG, "); 
		sb.append("       IS_EFFETIVE, "); 
		sb.append("       COMPLAIN_YN, "); 
		sb.append("       COMM_RS_YN, "); 
		sb.append("       COMM_NS_YN "); 
		sb.append("FROM TBPMS_CUST_NOTE_REC_N "); 
		sb.append("WHERE 1 = 1 "); 
		sb.append("AND YYYYMM = :yyyymm "); 
		sb.append("AND BRANCH_NBR = :branchNBR "); 
		sb.append("AND CUST_AO = :custAO "); 
		
		switch (inputVO.getType()) {
			case "PROF_INVESTOR_Y":
				sb.append("AND PROF_INVESTOR_YN = 'Y' "); 
				break;
			case "PROF_INVESTOR_N":
				sb.append("AND PROF_INVESTOR_YN = 'N' "); 
				break;
			default:
				sb.append("AND ").append(inputVO.getType()).append(" = 'Y' "); 
				
				break;
		}
		
		queryCondition.setObject("yyyymm", inputVO.getYyyymm());
		queryCondition.setObject("branchNBR", inputVO.getBranchNbr());
		queryCondition.setObject("custAO", inputVO.getCustAO());

		queryCondition.setQueryString(sb.toString().toString().replaceAll("\\s+", " "));
		
		outputVO.setDtlList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	// 匯出明細
	public void exportDtl (Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {
		
		PMS364InputVO inputVO = (PMS364InputVO) body;
		
		List<Map<String, Object>> list = inputVO.getExportDtlList();
		
		String typeName = "";
		switch (inputVO.getType()) {
			case "PROF_INVESTOR_Y":
				typeName = "專業投資人(有效)";
				break;
			case "PROF_INVESTOR_N":
				typeName = "專業投資人(無效)";
				break;
			case "TX_FLAG":
				typeName = "信託同意";
				break;
			case "IS_EFFETIVE":
				typeName = "衍商同意";
				break;
			case "COMPLAIN_YN":
				typeName = "客訴戶";
				break;
			case "COMM_RS_YN":
				typeName = "拒銷戶";
				break;
			case "COMM_NS_YN":
				typeName = "禁銷戶";
				break;
		}
		
		String fileName = sdfYYYYMMDD.format(new Date()) + "_" + inputVO.getCustAO() + "_" + typeName + "_" + inputVO.getYyyymm() + "特定註記客戶明細表.xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(inputVO.getYyyymm());
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
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
		
		String[] headerLine = {"分行別", "理專", "客戶ID", "客戶姓名", "專業投資人效期", "信託同意", "衍商同意", "客訴戶", "拒銷戶", "禁銷戶"};
		String[] mainLine   = {"BRANCH_NBR", "CUST_AO", "CUST_ID", "CUST_NAME", "PROF_INVESTOR_DATE", "TX_FLAG", "IS_EFFETIVE", "COMPLAIN_YN", "COMM_RS_YN", "COMM_NS_YN"};

		Integer index = 0;
		
		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
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
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				switch (mainLine[j]) {
					case "BRANCH_NBR":
						cell.setCellValue(checkIsNull(map, mainLine[j]) + "-" + checkIsNull(map, "BRANCH_NAME"));
						break;
					case "CUST_ID":
						cell.setCellValue(DataFormat.getCustIdMaskForHighRisk(ObjectUtils.toString(checkIsNull(map, "CUST_ID"))));
						break;
					default:
						cell.setCellValue(checkIsNull(map, mainLine[j]));
						break;
				}
			}

			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
		sendRtnObject(null);
	}
	
	// 匯出統計
	// 20240711_#2072_俊達_特定註記客戶統計表調整  by Sam Tu
	public void export (Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {
		
		PMS364InputVO inputVO = (PMS364InputVO) body;
		
		List<Map<String, String>> list = inputVO.getExportList();
		
		String fileName = sdfYYYYMMDD.format(new Date()) + "_" + inputVO.getYyyymm() + "特定註記客戶明細表.xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(inputVO.getYyyymm());
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
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
		
		String[] headerLine = {"業務處", "區別", "分行", "AO Code", "專業投資人(有效)", "", "專業投資人(無效)", "", "信託同意", "衍商同意", "客訴戶", "拒銷戶", "禁銷戶"};
		String[] headerLine2 = {"", "", "", "", "A板塊", "C板塊", "A板塊", "C板塊", "", "", "", "", ""};
		String[] mainLine   = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "CUST_AO", "SUM_PROF_INVESTOR_A_Y", "SUM_PROF_INVESTOR_C_Y", "SUM_PROF_INVESTOR_A_N", "SUM_PROF_INVESTOR_C_N", "SUM_TX_FLAG_Y", "SUM_IS_EFFETIVE_Y", "SUM_COMPLAIN_Y", "SUM_COMM_RS_Y", "SUM_COMM_NS_Y"};

		Integer index = 0;
		
		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
		}
		index++;
		
		row = sheet.createRow(index);
		for (int i = 0; i < headerLine2.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine2[i]);
		}
		index++;
		
		//標頭合併欄位
		sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
		sheet.addMergedRegion(new CellRangeAddress(0,1,1,1));
		sheet.addMergedRegion(new CellRangeAddress(0,1,2,2));
		sheet.addMergedRegion(new CellRangeAddress(0,1,3,3));
		sheet.addMergedRegion(new CellRangeAddress(0,0,4,5));
		sheet.addMergedRegion(new CellRangeAddress(0,0,6,7));
		sheet.addMergedRegion(new CellRangeAddress(0,1,8,8));
		sheet.addMergedRegion(new CellRangeAddress(0,1,9,9));
		sheet.addMergedRegion(new CellRangeAddress(0,1,10,10));
		sheet.addMergedRegion(new CellRangeAddress(0,1,11,11));
		sheet.addMergedRegion(new CellRangeAddress(0,1,12,12));
		

		
		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		
		ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchTempList = new ArrayList<String>(); //比對用
		Integer centerStartFlag = 0, branchAreaStartFlag = 0, branchStartFlag = 0;
		Integer centerEndFlag = 0, branchAreaEndFlag = 0, branchEndFlag = 0;
		
		Integer contectStartIndex = index;
		
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				
				String regionCenterName = list.get(i).get("REGION_CENTER_NAME");
				String branchAreaName = list.get(i).get("BRANCH_AREA_NAME");
				String branchName = list.get(i).get("BRANCH_NAME");
				
				//REGION_CENTER
				if (j == 0 && centerTempList.indexOf(regionCenterName) < 0) {
					centerTempList.add(regionCenterName);
					
					if (centerEndFlag != 0) {
						if (null != regionCenterName && regionCenterName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(centerEndFlag + contectStartIndex + 1, centerEndFlag + contectStartIndex + 1, j, j + 3)); // firstRow, endRow, firstColumn, endColumn
						} 
					}
					
					centerStartFlag = i;
					centerEndFlag = 0;
				} else if (j == 0 && null != list.get(i).get("REGION_CENTER_NAME")) {
					centerEndFlag = i;
				}
				
				// BRANCH_AREA
				if (j == 1 && branchAreaTempList.indexOf(branchAreaName) < 0) {
					branchAreaTempList.add(branchAreaName);
					
					if (branchAreaEndFlag != 0) {
						if (null != branchAreaName && branchAreaName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(branchAreaEndFlag + contectStartIndex + 1, branchAreaEndFlag + contectStartIndex + 1, j, j + 2)); // firstRow, endRow, firstColumn, endColumn
						}
					}
					
					branchAreaStartFlag = i;
					branchAreaEndFlag = 0;
				} else if (j == 1 && null != list.get(i).get("BRANCH_AREA_NAME")) {
					branchAreaEndFlag = i;
				}
				
				// BRANCH
				if (j == 2 && branchTempList.indexOf(branchName) < 0) {
					branchTempList.add(branchName);
					
					if (branchEndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
						sheet.addMergedRegion(new CellRangeAddress(branchEndFlag + contectStartIndex + 1, branchEndFlag + contectStartIndex + 1, j, j + 1)); // firstRow, endRow, firstColumn, endColumn
					}
					branchStartFlag = i;
					branchEndFlag = 0;
				} else if (j == 2 && null != list.get(i).get("BRANCH_NAME")) {
					branchEndFlag = i; 
				}
				
				// 全行合計
				if (j == 0 && i == (list.size() - 1)) {
					sheet.addMergedRegion(new CellRangeAddress(i + 2, i + 2, j, j + 3)); // firstRow, endRow, firstColumn, endColumn
				}
				
				cell.setCellValue(list.get(i).get(mainLine[j]));
			}

			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
		sendRtnObject(null);
	}
	
	// 空值檢查
	private String checkIsNull (Map map, String key) {
		
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}