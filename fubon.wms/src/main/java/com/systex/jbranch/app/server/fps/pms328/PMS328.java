package com.systex.jbranch.app.server.fps.pms328;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Author : 2016-05-31 Kevin
 * Editor : 2017-01-31 Kevin
 */

@Component("pms328")
@Scope("request")
public class PMS328 extends FubonWmsBizLogic {

	DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS328.class);

	public StringBuffer returnAppend (String genType, String sqlType, String funcPage) {
		
		StringBuffer sb = new StringBuffer();
		
		switch (genType) {
			case "BASE":
				sb.append("       VW.REGION_CENTER_ID").append(", ");
				sb.append("       VW.REGION_CENTER_NAME").append(", ");
				sb.append("       VW.BRANCH_AREA_ID").append(", ");
				sb.append("       VW.BRANCH_AREA_NAME").append(", ");
				sb.append("       TBPMS.BRANCH_NBR").append(", ");
				sb.append("       VW.BRANCH_NAME").append(", ");
				
				sb.append("       TBPMS.YEARMON").append(", ");

				if (StringUtils.equals(funcPage, "PMS328DT")) {
					sb.append("       TBPMS.TEAM_TYPE").append(", ");
				}
				
				sb.append("       EMP.DEPT_ID, ");
				sb.append("       TBPMS.AO_CODE, ");
				sb.append("       SAO.TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", ");
				sb.append("       TBPMS.EMP_ID, ");
				sb.append("       TBPMS.EMP_NAME, ");

				sb.append("       SUBSTR(TBPMS.YEARMON, 0, 4) || '/' || SUBSTR(TBPMS.YEARMON, 5, 6) ").append(StringUtils.equals("SELECT", sqlType) ? "AS YM " : " ").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "ALL":
				sb.append("       'ALL_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       '全行 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       'ALL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       'ALL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS328DT")) {
					sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS TEAM_TYPE" : "").append(", "); 
				}
				
				sb.append("       'ZZZZZ' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZ' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZ' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", "); 
				
				sb.append("       'ALL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS YM " : " ").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "REGION":
				sb.append("       REGION_CENTER_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       REGION_CENTER_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       'REGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       'AREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS328DT")) {
					sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS TEAM_TYPE" : "").append(", "); 
				}
				
				sb.append("       'ZZZZZREGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZREGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZREGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", "); 
				
				sb.append("       'REGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS YM " : " ").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				
				break;
			case "AREA":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", ");
				sb.append("       BRANCH_AREA_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR " : "").append(", "); 
				sb.append("       'AREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME " : "").append(", "); 
				
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS328DT")) {
					sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS TEAM_TYPE" : "").append(", "); 
				}
				
				sb.append("       'ZZZZZAREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZAREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZAREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", "); 
				
				sb.append("       'AREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS YM " : " ").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "BRANCH":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", ");
				sb.append("       BRANCH_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS328DT")) {
					sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS TEAM_TYPE" : "").append(", "); 
				}
				
				sb.append("       'ZZZZZBRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZBRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZBRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", "); 
				
				sb.append("       'BRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS YM " : " ").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "EMP":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR").append(", ");
				sb.append("       BRANCH_NAME").append(", ");
				
				sb.append("       YEARMON").append(", ");
				
				if (StringUtils.equals(funcPage, "PMS328DT")) {
					sb.append("       TEAM_TYPE").append(", ");
				}
				
				sb.append("       DEPT_ID").append(", ");
				sb.append("       AO_CODE").append(", ");
				sb.append("       AO_TYPE").append(", ");
				sb.append("       EMP_ID").append(", ");
				sb.append("       EMP_NAME").append(", ");
				
				sb.append("       YM").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "TEAM":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR").append(", ");
				sb.append("       BRANCH_NAME").append(", ");
				
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS328DT")) {
					sb.append("       TEAM_TYPE").append(", ");
				}
				
				sb.append("       'ZZZZZTEAM' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZTEAM' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZTEAM' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", ");
				
				sb.append("       'TEAM' ").append(StringUtils.equals("SELECT", sqlType) ? "AS YM " : " ").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "ORDER" :
				sb.append("ORDER BY ");
				sb.append("         DECODE(REGION_CENTER_ID, '000', 999, 0), ");
				sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
				sb.append("         REGION_CENTER_NAME, ");
				sb.append("         DECODE(BRANCH_AREA_ID, NULL, 999, 0), ");
				sb.append("         BRANCH_AREA_NAME, ");
				sb.append("         DECODE(BRANCH_NBR, NULL, 999, 0), ");
				sb.append("         BRANCH_NAME, ");

				if (StringUtils.equals(funcPage, "PMS328DT")) {
					sb.append("         DECODE(TEAM_TYPE, '000_TOTAL', 999, 0), ");    
					sb.append("         TEAM_TYPE, ");  
				}
				
				sb.append("         DECODE(AO_CODE, '000', 997, 'ZZZZZTEAM', 998, 'ZZZZZBRANCH', 999, 0), ");    
				sb.append("         DECODE(AO_TYPE, NULL, 993, 'ZZZZZTEAM', 994, 'ZZZZZBRANCH', 995, 'ZZZZZAREA', 996, 'ZZZZZREGION', 997, 0), ");  
				sb.append("         EMP_ID DESC, ");    
				sb.append("         AO_TYPE, ");    
				sb.append("         YEARMON ");
				
				break;
		}
		
		return sb;
	}
	
	public StringBuffer genSQL (String genType, PMS328InputVO inputVO, boolean isFC, boolean isHeadMGR, String memLoginFlag, List<Map<String, Object>> teamDTL, String NOT_EXIST_UHRM) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(returnAppend(genType, "SELECT", inputVO.getFuncPage()));
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("TOTAL) AS TOTAL, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("CGPR_PRIVATE) AS CGPR_PRIVATE, ");		// 客戶群權益等級-私人		/ 新等級H
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("CGPR_PLATINUM) AS CGPR_PLATINUM, ");	// 客戶群權益等級-白金		/ 新等級T
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("CGPR_PERSON) AS CGPR_PERSON, ");		// 客戶群權益等級-個人		/ 新等級K
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("CGPR_MASS) AS CGPR_MASS, ");			// 客戶群權益等級-MASS戶	/ 新等級C
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("CGPR_SUM) AS CGPR_SUM, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("EIP_E) AS EIP_E, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("EIP_I) AS EIP_I, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("EIP_P) AS EIP_P, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("EIP_O) AS EIP_O, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("EIP_S) AS EIP_S, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("EIP_Z) AS EIP_Z, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("EIP_SUM) AS EIP_SUM, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("II_INV_INS) AS II_INV_INS, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("II_NON) AS II_NON, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("II_INV) AS II_INV, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("II_INS) AS II_INS, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("II_SUM) AS II_SUM, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("CUST_TYPE_PERSON) AS CUST_TYPE_PERSON, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("CUST_TYPE_CORPOR) AS CUST_TYPE_CORPOR, ");
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("CUST_TYPE_SUM) AS CUST_TYPE_SUM ");
		
		switch (genType) {
			case "BASE":
				sb.append("FROM TBPMS_MONTHLY_CUST_NUMBER TBPMS ");
				sb.append("INNER JOIN VWORG_DEFN_INFO VW ON VW.BRANCH_NBR = TBPMS.BRANCH_NBR ");
				sb.append("LEFT JOIN VWORG_AO_INFO AO ON TBPMS.AO_CODE = AO.AO_CODE ");
				sb.append("LEFT JOIN TBORG_SALES_AOCODE SAO ON TBPMS.AO_CODE = SAO.AO_CODE ");
				sb.append("LEFT JOIN (SELECT DISTINCT EMP_ID, EMP_DEPT_ID AS DEPT_ID FROM VWORG_EMP_INFO) EMP ON SAO.EMP_ID = EMP.EMP_ID ");
				sb.append("WHERE 1 = 1 ");
				
				break;
			case "EMP":
			case "TEAM":
			case "BRANCH":
			case "AREA":
			case "REGION":
			case "ALL":
				sb.append("FROM BASE ");
				sb.append("WHERE 1 = 1 ");
				
				break;
		}
		
		// ==主查詢條件==
		// 日期
		if (!StringUtils.isBlank(inputVO.getsTimes()) && !inputVO.getsTimes().toString().equals("0")) {
			sb.append("AND TO_DATE(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("YEARMON, 'YYYYMM') >= TO_DATE(:STARTTIME, 'YYYYMM') ");
		}
		
		if (!StringUtils.isBlank(inputVO.geteTimes()) && !inputVO.geteTimes().toString().equals("0")) {
			sb.append("AND TO_DATE(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("YEARMON, 'YYYYMM') <= TO_DATE(:ENDTIME, 'YYYYMM') ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
			sb.append("AND TO_DATE(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("YEARMON, 'YYYYMM') >= TO_DATE(:STARTTIME, 'YYYYMM') ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
			sb.append("AND TO_DATE(").append(StringUtils.equals(genType, "BASE") ? "TBPMS." : "").append("YEARMON, 'YYYYMM') <= TO_DATE(:ENDTIME, 'YYYYMM') ");
		}
		
		boolean teamLeaderFlag = false;
		if (teamDTL.size() > 0) {
			teamLeaderFlag = StringUtils.equals("Y", (String) teamDTL.get(0).get("LEADER_FLAG")) ? true : false;
		}
		
		switch (genType) {
			case "EMP":
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS328DT")) {
					if (isFC && teamLeaderFlag) {
						sb.append("AND TEAM_TYPE = :teamType ");
					} else if (isFC || StringUtils.isNotBlank(inputVO.getEmp_id())) {
						sb.append("AND EMP_ID = :empID ");
					}
				} else {
					if (isFC || StringUtils.isNotBlank(inputVO.getEmp_id())) {
						sb.append("AND EMP_ID = :empID ");
					} 
					
					if (StringUtils.equals(memLoginFlag, "UHRM")) {
						sb.append("AND EMP_ID = :loginID ");
					}
				}
			case "TEAM":
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS328DT")) {
					sb.append("AND TEAM_TYPE IS NOT NULL ");
					
					if (isFC && teamLeaderFlag) {
						sb.append("AND TEAM_TYPE = :teamType ");
					} else if (isFC || StringUtils.isNotBlank(inputVO.getEmp_id())) {
						sb.append("AND TEAM_TYPE = :teamType ");
					}
				}
			case "BRANCH":
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS328DT")) {
					sb.append("AND TEAM_TYPE IS NOT NULL ");
				}
				
				switch (memLoginFlag) {
					case "UHRM":
						break;
					case "uhrmMGR":
					case "uhrmBMMGR":
						sb.append("  AND ( ");
						sb.append("           EMP_ID IS NOT NULL ");
						sb.append("       AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE BASE.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
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
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS328DT")) {
					sb.append("AND TEAM_TYPE IS NOT NULL ");
				}
				
				switch (memLoginFlag) {
					case "UHRM":
						break;
					case "uhrmMGR":
					case "uhrmBMMGR":
						sb.append("  AND ( ");
						sb.append("           EMP_ID IS NOT NULL ");
						sb.append("       AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE BASE.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
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
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS328DT")) {
					sb.append("AND TEAM_TYPE IS NOT NULL ");
				}
				
				switch (memLoginFlag) {
					case "UHRM":
						break;
					case "uhrmMGR":
					case "uhrmBMMGR":
						sb.append("  AND ( ");
						sb.append("           EMP_ID IS NOT NULL ");
						sb.append("       AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE BASE.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
						sb.append("  ) ");
						
						break;
					default:
						// 區域中心
						if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
							sb.append("AND REGION_CENTER_ID = :region_center_id ");
						} else if (!isHeadMGR) {
							// 登入非總行人員強制加區域中心
							sb.append("AND REGION_CENTER_ID IN (:region_center_id) ");
						}
						
						break;
				}
			case "ALL":
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS328DT")) {
					sb.append("AND TEAM_TYPE IS NOT NULL ");
				}
				
				switch (NOT_EXIST_UHRM) {
					case "Y":
						sb.append("AND NOT EXISTS ( ");
						sb.append("  SELECT 1 ");
						sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND LAST_DAY(TO_DATE(:STARTTIME || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
						sb.append("  AND UCN.DEPT_ID <> UCN.E_DEPT_ID "); // 私銀理專
						sb.append("  AND BASE.EMP_ID = UCN.EMP_ID ");
						sb.append(") ");
						break;
					case "U":
						sb.append("AND EXISTS ( ");
						sb.append("  SELECT 1 ");
						sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND LAST_DAY(TO_DATE(:STARTTIME || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
						sb.append("  AND UCN.DEPT_ID <> UCN.E_DEPT_ID "); // 私銀理專
						sb.append("  AND BASE.EMP_ID = UCN.EMP_ID ");
						sb.append(") ");
				}
				
				break;
		}
		
		sb.append("GROUP BY ");
		sb.append(returnAppend(genType, "GROUP", inputVO.getFuncPage()));
		
		return sb;
	}
	
	/** 主查詢 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		initUUID();
		SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");
		
		PMS328InputVO inputVO = (PMS328InputVO) body;
		PMS328OutputVO outputVO = new PMS328OutputVO();
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
		
		//=== 取得查詢資料可視範圍 START ===
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();

		if (!StringUtils.isBlank(inputVO.getsTimes()) && !inputVO.getsTimes().toString().equals("0")) {
			pms000InputVO.setReportDate(inputVO.getsTimes());
		}

		if (!StringUtils.isBlank(inputVO.geteTimes()) && !inputVO.geteTimes().toString().equals("0")) {
			pms000InputVO.setReportDate(inputVO.geteTimes());
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
			pms000InputVO.setReportDate(sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportSDate()))));
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
			pms000InputVO.setReportDate(sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportEDate()))));
		}

		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		//=== 取得查詢資料可視範圍 END ===
		
		//=== TEAM SQL START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		List<Map<String, Object>> teamDTL = new ArrayList<Map<String,Object>>();
		
		sb.append("SELECT TEAM_TYPE, LEADER_FLAG ");
		sb.append("FROM VWORG_AO_INFO ");
		sb.append("WHERE EMP_ID = :empID ");
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			queryCondition.setObject("empID", inputVO.getEmp_id());
			
			queryCondition.setQueryString(sb.toString());
			
			teamDTL = dam.exeQuery(queryCondition);
		} else if (isFC) {
			queryCondition.setObject("empID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition.setQueryString(sb.toString());
			
			teamDTL = dam.exeQuery(queryCondition);
		}
		
		boolean teamLeaderFlag = false;
		if (teamDTL.size() > 0) {
			teamLeaderFlag = StringUtils.equals("Y", (String) teamDTL.get(0).get("LEADER_FLAG")) ? true : false;
		}
		//=== TEAM SQL END ===
		
		//=== MAIN SQL START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM ( ").append(genSQL("BASE", inputVO, isFC, isHANDMGR, memLoginFlag, teamDTL, inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")).append("  ) "); 
		sb.append(") ");
		
		sb.append("SELECT * ");
		sb.append("FROM ( "); 
		
		// 人員
		sb.append(genSQL("EMP", inputVO, isFC, isHANDMGR, memLoginFlag, teamDTL, inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// TEAM合計
		if (StringUtils.equals(inputVO.getFuncPage(), "PMS328DT")) {
			sb.append(genSQL("TEAM", inputVO, isFC, isHANDMGR, memLoginFlag, teamDTL, inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 
			
			// 組別
			if (isFC && teamLeaderFlag) { // teamLeaderFlag
				queryCondition.setObject("teamType", (String) teamDTL.get(0).get("TEAM_TYPE"));
			} else if (isFC) {
				queryCondition.setObject("teamType", (String) teamDTL.get(0).get("TEAM_TYPE"));
			} else if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				queryCondition.setObject("teamType", (String) teamDTL.get(0).get("TEAM_TYPE"));
			} 
			
			sb.append("UNION ");
		}
		
		// 分行合計
		sb.append(genSQL("BRANCH", inputVO, isFC, isHANDMGR, memLoginFlag, teamDTL, inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 營運區合計
		sb.append(genSQL("AREA", inputVO, isFC, isHANDMGR, memLoginFlag, teamDTL, inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 業務處合計
		sb.append(genSQL("REGION", inputVO, isFC, isHANDMGR, memLoginFlag, teamDTL, inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 

		sb.append("UNION "); 
		
		// 全行合行
		sb.append(genSQL("ALL", inputVO, isFC, isHANDMGR, memLoginFlag, teamDTL, inputVO.getNOT_EXIST_UHRM()).toString().replaceAll("\\s+", " ")); 

		sb.append(") "); 
		sb.append(returnAppend("ORDER", "", inputVO.getFuncPage()).toString().replaceAll("\\s+", " "));
		
		// ==主查詢條件==
		// ===== 日期
		if (!StringUtils.isBlank(inputVO.getsTimes()) && !inputVO.getsTimes().toString().equals("0")) {
			queryCondition.setObject("STARTTIME", inputVO.getsTimes());
		}
		
		if (!StringUtils.isBlank(inputVO.geteTimes()) && !inputVO.geteTimes().toString().equals("0")) {
			queryCondition.setObject("ENDTIME", inputVO.geteTimes());
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
			queryCondition.setObject("STARTTIME", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportSDate()))));
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
			queryCondition.setObject("ENDTIME", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportEDate()))));
		}
		// =====
		
		// EMP
		if (StringUtils.equals(inputVO.getFuncPage(), "PMS328DT")) {
			if (isFC && teamLeaderFlag) {
				queryCondition.setObject("teamType", (String) teamDTL.get(0).get("TEAM_TYPE"));
			} else if (isFC) {
				queryCondition.setObject("empID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			} else if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				queryCondition.setObject("empID", inputVO.getEmp_id());
			} 
		} else {
			if (isFC) {
				queryCondition.setObject("empID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			} else if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				queryCondition.setObject("empID", inputVO.getEmp_id());
			} 
			
			if (StringUtils.equals(memLoginFlag, "UHRM")) {
				queryCondition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
			}
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
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
				} else if (!isHANDMGR) {
					queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
				
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
					queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
				} else if (!isHANDMGR) {
					queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
				
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
					queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
				} else if (!isHANDMGR) {
					queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
				
				break;
		}
		
		//=== MAIN SQL END ===
		
		queryCondition.setQueryString(sb.toString());
		
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List<Map<String, Object>> csvList = dam.exeQuery(queryCondition);

		outputVO.setResultList(list); // data
		outputVO.setTotalList(csvList);
		outputVO.setCsvList(csvList); // csv list
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(list.getTotalPage());// 總頁次
		outputVO.setTotalRecord(list.getTotalRecord());// 總筆數
		
		this.sendRtnObject(outputVO);
	}

	/** 匯出 **/
	public void export(Object body, IPrimitiveMap header) throws JBranchException {

		PMS328OutputVO return_VO = (PMS328OutputVO) body;

		String fileName = "客戶數報表" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + (String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		
		List<Map<String, Object>> list = return_VO.getList();
		List listCSV = new ArrayList();

		try {
			if (list.size() > 0) {
				String[] csvHeader = {"資料統計年月", "業務處名稱", "營運區名稱", "分行ID", "分行名稱", "AO Code", "Code別", "理專員編", "理專姓名",
									  "總客戶數",
								   	  "客戶群權益等級-私人", "客戶群權益等級-白金", "客戶群權益等級-個人", "客戶群權益等級-MASS", "客戶群權益等級-合計",
									  "EIP客戶-E", "EIP客戶-I", "EIP客戶-P", "EIP客戶-O", "EIP客戶-S", "EIP客戶-MASS", "EIP客戶-合計",
									  "投資和保險", "無投資和無保險", "投保客戶-投資", "投保客戶-保險", "投保客戶-合計",
									  "客戶類型-自然人", "客戶類型-法人", "客戶類型-合計"};
				
				String[] csvHeaderNew = {"資料統計年月", "業務處名稱", "營運區名稱", "分行ID", "分行名稱", "AO Code", "Code別", "理專員編", "理專姓名",
										 "總客戶數",
										 "客戶群權益等級-恆富", "客戶群權益等級-智富", "客戶群權益等級-穩富", "客戶群權益等級-一般存戶-跨優", "客戶群權益等級-MASS", "客戶群權益等級-合計",
										 "EIP客戶-E", "EIP客戶-I", "EIP客戶-P", "EIP客戶-O", "EIP客戶-S", "EIP客戶-MASS", "EIP客戶-合計",
										 "投資和保險", "無投資和無保險", "投保客戶-投資", "投保客戶-保險", "投保客戶-合計",
										 "客戶類型-自然人", "客戶類型-法人", "客戶類型-合計"};
				
				String[] csvMain = {"YEARMON", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "AO_CODE", "AO_TYPE", "EMP_ID", "EMP_NAME",
									"TOTAL",
									"CGPR_PRIVATE", "CGPR_PLATINUM", "CGPR_PERSON", "CGPR_MASS", "CGPR_SUM",
									"EIP_E", "EIP_I", "EIP_P", "EIP_O", "EIP_S", "EIP_Z", "EIP_SUM",
									"II_INV_INS", "II_NON", "II_INV", "II_INS", "II_SUM",
									"CUST_TYPE_PERSON", "CUST_TYPE_CORPOR", "CUST_TYPE_SUM"};
				
				String[] csvMainNew = {"YEARMON", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "AO_CODE", "AO_TYPE", "EMP_ID", "EMP_NAME",
									   "TOTAL",
									   "CGPR_PRIVATE", "CGPR_PLATINUM", "CGPR_PERSON", "CGPR_MASS", "CGPR_REAL_MASS", "CGPR_SUM",
									   "EIP_E", "EIP_I", "EIP_P", "EIP_O", "EIP_S", "EIP_Z", "EIP_SUM",
									   "II_INV_INS", "II_NON", "II_INV", "II_INS", "II_SUM",
									   "CUST_TYPE_PERSON", "CUST_TYPE_CORPOR", "CUST_TYPE_SUM"};
				
				
				
				
				for (Map<String, Object> map : list) {
					if (!(checkIsNull(map, "AO_CODE").equals("ZZZZZREGION") || checkIsNull(map, "AO_CODE").equals("ZZZZZAREA") || checkIsNull(map, "AO_CODE").equals("ZZZZZBRANCH"))) {
						String[] records = new String[csvMain.length];
						
						if (return_VO.getS_time().compareTo("2024/08") >= 0) {
							// #1973 WMS-CR-20240411-01_新制財管會員分級權益專案
							records = new String[csvMainNew.length];
							
							for (int i = 0; i < csvHeaderNew.length; i++) {
								switch (csvMainNew[i]) {
									case "AO_CODE":
									case "EMP_ID":
										records[i] = "=\"" + (checkIsNull(map, csvMainNew[i])) + "\"";
										
										break;
									case "AO_TYPE":
										String codeType = checkIsNull(map, csvMainNew[i]);
										records[i] = StringUtils.equals("1", codeType) ? "計績" : StringUtils.equals("2", codeType) ? "兼職" : StringUtils.equals("3", codeType) ? "維護" : "000";
										
										break;
									case "CGPR_REAL_MASS":
										int cgpr_sum 	  = Double.valueOf(map.get("CGPR_SUM").toString()).intValue();
										int cgpr_private  = Double.valueOf(map.get("CGPR_PRIVATE").toString()).intValue();
										int cgpr_platinum = Double.valueOf(map.get("CGPR_PLATINUM").toString()).intValue();
										int cgpr_person   = Double.valueOf(map.get("CGPR_PERSON").toString()).intValue();
										int cgpr_mass	  = Double.valueOf(map.get("CGPR_MASS").toString()).intValue();
//										int cgpr_sum 	  = Integer.parseInt(map.get("CGPR_SUM").toString());		// TOTAL
//										int cgpr_private  = Integer.parseInt(map.get("CGPR_PRIVATE").toString());	// 新等級H (恆富)
//										int cgpr_platinum = Integer.parseInt(map.get("CGPR_PLATINUM").toString());	// 新等級T (智富)
//										int cgpr_person   = Integer.parseInt(map.get("CGPR_PERSON").toString());	// 新等級K (穩富)
//										int cgpr_mass	  = Integer.parseInt(map.get("CGPR_MASS").toString());		// 新等級C (一般存戶-跨優)
										int cgpr_real_mass = cgpr_sum - cgpr_private - cgpr_platinum - cgpr_person - cgpr_mass; 
										
										records[i] = "=\"" + cgpr_real_mass + "\"";
										
										break;
									default:
										records[i] = checkIsNull(map, csvMainNew[i]);
										
										break;
								}
							}
						} else {
							for (int i = 0; i < csvHeader.length; i++) {
								switch (csvMain[i]) {
									case "AO_CODE":
									case "EMP_ID":
										records[i] = "=\"" + (checkIsNull(map, csvMain[i])) + "\"";
										
										break;
									case "AO_TYPE":
										String codeType = checkIsNull(map, csvMain[i]);
										records[i] = StringUtils.equals("1", codeType) ? "計績" : StringUtils.equals("2", codeType) ? "兼職" : StringUtils.equals("3", codeType) ? "維護" : "000";
										
										break;
									default:
										records[i] = checkIsNull(map, csvMain[i]);
										
										break;
								}
							}
						}
						
						listCSV.add(records);
					}
				}

				CSVUtil csv = new CSVUtil();
				
				if (return_VO.getS_time().compareTo("2024/08") >= 0) {
					csv.setHeader(csvHeaderNew); // 設定標頭
				} else {
					csv.setHeader(csvHeader); // 設定標頭
				}
				
				csv.addRecordList(listCSV); // 設定內容
				
				String url = csv.generateCSV();

				notifyClientToDownloadFile(url, fileName);
			} else
				this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/** 匯出確認NULL **/
	private String checkIsNull(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * =趨勢圖=
	 */
	public void queryImage(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS328IInputVO inputVO = (PMS328IInputVO) body;
		PMS328OutputVO outputVO = new PMS328OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> list = inputVO.getList();

		List<Map<String, Object>> list_AOCODE = new ArrayList<Map<String, Object>>();

		try {
			String type_rob = "REGION_CENTER_NAME";
			String type = "TOTAL";
			
			switch (inputVO.getType()) {
				case "(客戶權益)":
					type = "CGPR_SUM";
					break;
				case "(EIP)":
					type = "EIP_SUM";
					break;
				case "(投保)":
					type = "II_SUM";
					break;
				case "(TOTAL)":
					type = "TOTAL";
					break;
			}
			
			switch (inputVO.getROB()) {
				case "1":
					type_rob = "REGION_CENTER_ID";
					break;
				case "2":
					type_rob = "BRANCH_AREA_ID";
					break;
				case "3":
					type_rob = "BRANCH_NBR";
					break;
				case "4":
					type_rob = "AO_CODE";
					break;
			}

			if (!StringUtils.isBlank(inputVO.getAocode())) {
				type_rob = "AO_CODE";
				inputVO.setROB("4");
				
				if (list != null) {
					for (Map<String, Object> map : list) {
						if (map.get("DATA").equals(inputVO.getAocode().toString()))
							list_AOCODE.add(map);
					}
				}
				
				list = list_AOCODE;
			}
			
			//==**趨勢圖開始**==
			sql.append("select YEARMON as YM, LAST_DAY(TO_DATE(YEARMON || '01', 'YYYYMMDD')) as YMD, ");
			sql.append("  ( ");
			sql.append("    LAST_DAY(TO_DATE(YEARMON || '01', 'YYYYMMDD')) - ");
			sql.append("    TO_date('19700101','YYYYMMDD')  ");
			sql.append("  ) * 86400000 as YEARMON, ");
			sql.append("  NVL(AVG_ALL,0) as AVG  ");
			int c = 0; //串聯list陣列長度年月欄位 avg0,avg1,avg2
			int i = 0; //串 in 條件一
			int j = 0; //串 in 條件二
			//串聯list陣列長度年月欄位 avg0,avg1,avg2

			for (Map<String, Object> map : list) {
				sql.append(" ,NVL(AVG" + c + "," + c + ") as AVG" + c);
				c++;
			}
			sql.append("    from   (");
			//年月, 區域中心|營運區|分行|AO, 分析類別
			sql.append(" select  ");
			//年月
			sql.append("  YEARMON,");
			//區域中心|營運區|分行|AO --Java 動態組
			sql.append(" '全行平均' as  " + type_rob + ",");
			//分析類別 --Java 動態組
			sql.append("  AVG(NVL(" + type + ",0)) as " + type + "  ");
			//*****表格*******
			sql.append(" from TBPMS_MONTHLY_CUST_NUMBER ");
			sql.append("   where YEARMON between TO_CHAR(add_months(last_day(add_months((SYSDATE -1),-1)),-13), 'YYYYMM')");
			//--上月底日期 往前13個月
			sql.append("   and TO_CHAR(last_day(add_months((SYSDATE -1),-1)), 'YYYYMM')");
			sql.append("    group by YEARMON  ");
			sql.append("   		  UNION    ");
			//--年月
			sql.append("   select YEARMON ,  ");
			//--區域中心|營運區|分行|AO --Java 動態組
			sql.append(" " + type_rob + ", ");
			//--分析類別 --Java 動態組
			sql.append(" " + type + "  ");
			sql.append("   from TBPMS_MONTHLY_CUST_NUMBER  ");
			sql.append("   where YEARMON between TO_CHAR(add_months(last_day(add_months((SYSDATE -1),-1)),-13), 'YYYYMM') ");
			//--上月底日期 往前13個月
			sql.append("   and TO_CHAR(last_day(add_months((SYSDATE -1),-1)), 'YYYYMM')  ");
			//--Java 動態組 Begin
			//--and REGION_CENTER_ID in ('171A', '', '') --區域中心
			//--and BRANCH_AREA_ID in ('171EF','171GF','171CC') --營運區
			//--and BRANCH_NBR in ('171') --分行
			//--and AO_CODE in ('71O') --理專
			//--Java 動態組 End
			if (!StringUtils.isBlank(inputVO.getROB()) && list.size() >= 1) {
				sql.append("  and " + type_rob + "  in ");
			}
			if (list.size() >= 1) {
				sql.append(" ( ");
			}
			//串接in 內容
			for (Map<String, Object> map : list) {
				//				if(!map.get("DATA").equals("") ){

				sql.append(":PARAMTER" + i);
				condition.setObject("PARAMTER" + i, checkIsNull(map, "DATA"));
				if (list.size() - 1 != i)
					sql.append(",");
				i++;
				//				}
			}
			if (list.size() >= 1) {
				sql.append(") ");
			}
			sql.append(") ");

			sql.append(" PIVOT ( AVG(NVL(" + type + ",0))");
			i = 0; //串接in 歸0
			sql.append(" FOR ( " + type_rob + ") in ('全行平均' as AVG_ALL ");

			if (list.size() >= 1) {
				sql.append(",");
			}
			//串接in 內容2  for 裡面
			for (Map<String, Object> map : list) {
				//				if(!checkIsNull(map,"DATA").equals("") ){
				sql.append("'" + checkIsNull(map, "DATA") + "' as AVG" + i);
				if (list.size() - 1 != i)
					sql.append(",");
				i++;
				//				}
			}
			sql.append(" ) )");

			//			sql.append(" WHERE 1=1 and '201605'<YEARMON AND '201612'>YEARMON  ");

			sql.append(" WHERE 1=1 and :sDate<=YEARMON AND :eDate+1>YEARMON  ");
			condition.setObject("sDate", inputVO.getsTimes());
			condition.setObject("eDate", inputVO.geteTimes());

			sql.append("  order by  YEARMON");

			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			outputVO.setResultList(list1);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
		}
	}
}