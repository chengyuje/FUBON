package com.systex.jbranch.app.server.fps.pms329;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
 * Author : 2016-05-24  Kevin
 * Editor : 2017-01-31 Kevin
 */

@Component("pms329")
@Scope("request")
public class PMS329 extends FubonWmsBizLogic {

	DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS329.class);

	public StringBuffer returnAppend (String genType, String sqlType, String funcPage) {
		
		StringBuffer sb = new StringBuffer();
		
		switch (genType) {
			case "BASE":
				sb.append("       VW.REGION_CENTER_ID").append(", ");
				sb.append("       VW.REGION_CENTER_NAME").append(", ");
				sb.append("       VW.BRANCH_AREA_ID").append(", ");
				sb.append("       VW.BRANCH_AREA_NAME").append(", ");
				sb.append("       AUM.BRANCH_NBR").append(", ");
				sb.append("       VW.BRANCH_NAME").append(", ");
				sb.append("       AUM.YEARMON").append(", ");
				
				if (StringUtils.equals(funcPage, "PMS329DT")) {
					sb.append("       AUM.TEAM_TYPE").append(", ");
				}
				
				sb.append("       EMP.DEPT_ID, ");
				sb.append("       AUM.AO_CODE").append(", ");
				sb.append("       SAO.TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", ");
				sb.append("       AO.EMP_ID").append(", ");
				sb.append("       CASE WHEN AO.EMP_NAME IS NULL THEN '○○○' ELSE AO.EMP_NAME END ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME, " : " ");
				
				break;
			case "ALL":
				sb.append("       'ALL_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       '全行 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       'ALL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       'ALL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS329DT")) {
					sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS TEAM_TYPE" : "").append(", "); 
				}
				
				sb.append("       'ZZZZZ' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZ' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZ' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "REGION":
				sb.append("       REGION_CENTER_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       REGION_CENTER_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       'REGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       'AREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS329DT")) {
					sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS TEAM_TYPE" : "").append(", "); 
				}
				
				sb.append("       'ZZZZZREGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZREGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZREGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "AREA":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", ");
				sb.append("       BRANCH_AREA_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR " : "").append(", "); 
				sb.append("       'AREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME " : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS329DT")) {
					sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS TEAM_TYPE" : "").append(", "); 
				}
				
				sb.append("       'ZZZZZAREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZAREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZAREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "BRANCH":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", ");
				sb.append("       BRANCH_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS329DT")) {
					sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS TEAM_TYPE" : "").append(", "); 
				}
				
				sb.append("       'ZZZZZBRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZBRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZBRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "EMP":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR").append(", ");
				sb.append("       BRANCH_NAME").append(", ");
				sb.append("       YEARMON").append(", ");
				
				if (StringUtils.equals(funcPage, "PMS329DT")) {
					sb.append("       TEAM_TYPE").append(", ");
				}
				
				sb.append("       DEPT_ID").append(", ");
				sb.append("       AO_CODE").append(", ");
				sb.append("       AO_TYPE").append(", ");
				sb.append("       EMP_ID").append(", ");
				sb.append("       EMP_NAME").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "TEAM":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR").append(", ");
				sb.append("       BRANCH_NAME").append(", ");
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS YEARMON" : "").append(", "); 
				
				if (StringUtils.equals(funcPage, "PMS329DT")) {
					sb.append("       TEAM_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "" : "").append(", ");
				}
				
				sb.append("       'ZZZZZTEAM' ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEPT_ID" : "").append(", "); 
				sb.append("       'ZZZZZTEAM' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZTEAM' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
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
				
				if (StringUtils.equals(funcPage, "PMS329DT")) {
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
	
	public StringBuffer genSQL (String genType, PMS329InputVO inputVO, boolean isFC, boolean isHeadMGR, String memLoginFlag, List<Map<String, Object>> teamDTL, String NOT_EXIST_UHRM) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(returnAppend(genType, "SELECT", inputVO.getFuncPage()));
		
		// 投資
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_BAL)    AS INV_BAL, ");		// 投資小計
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_FUND)   AS INV_FUND, ");		// 基金
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_ETF)    AS INV_ETF, ");		// ETF
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_STOCK)  AS INV_STOCK, ");		// 海外股票
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_SI)     AS INV_SI, ");		// SI
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_DCI)    AS INV_DCI, ");		// DCI
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_SN)     AS INV_SN, ");		// SN
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_RP)     AS INV_RP, ");		// RP
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_BOND)   AS INV_BOND, ");		// 海外債
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_GOLD)   AS INV_GOLD, ");		// 黃金存摺
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_STRST)  AS INV_STRST, ");		// 金錢信託
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_NAMI)   AS INV_NAMI, ");		// 奈米投
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INV_VBOND)  AS INV_VBOND, ");		// 金市海外債
		
		// 保險
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INS_BAL)   AS INS_BAL, ");		// 保險小計
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INS_IV)    AS INS_IV, ");		// 投資型
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INS_OT)    AS INS_OT, ");		// 躉繳
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INS_SY)    AS INS_SY, ");		// 短年期繳
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INS_LY)    AS INS_LY, ");		// 長年期繳
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("INS_NF)    AS INS_NF, ");		// 非富壽
		
		// 存款
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_BAL)    AS DEP_BAL, ");		// 存款小計
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_SAV)    AS DEP_SAV, ");		// 台幣活存(含台幣支存)
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_CD)     AS DEP_CD, ");		// 台幣定存
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_FSAV)   AS DEP_FSAV, ");		// 外幣活存
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_FCD)    AS DEP_FCD, ");		// 外幣定存
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_CSAV)   AS DEP_CSAV, ");		// 台幣支存
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("C_AVG_AUM)  AS C_AVG_AUM, ");	// C板塊
		
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_A_SAV)  AS DEP_A_SAV, ");	// 台幣活存-A板塊
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_A_CSAV) AS DEP_A_CSAV, ");	// 台幣支存-A板塊
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_A_CD)   AS DEP_A_CD, ");		// 台幣定存-A板塊
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_A_FSAV) AS DEP_A_FSAV, ");	// 外幣活存-A板塊
		sb.append("       SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("DEP_A_FCD)  AS DEP_A_FCD ");		// 外幣定存-A板塊
		
		switch (genType) {
			case "BASE":
				sb.append("FROM TBPMS_MONTHLY_AUM AUM ");
				sb.append("INNER JOIN VWORG_DEFN_INFO VW ON VW.BRANCH_NBR = AUM.BRANCH_NBR ");
				sb.append("LEFT JOIN VWORG_AO_INFO AO ON AUM.AO_CODE = AO.AO_CODE ");
				sb.append("LEFT JOIN TBORG_SALES_AOCODE SAO ON AUM.AO_CODE = SAO.AO_CODE ");
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
			sb.append("AND TO_DATE(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("YEARMON, 'YYYYMM') >= TO_DATE(:STARTTIME, 'YYYYMM') ");
		}
		
		if (!StringUtils.isBlank(inputVO.geteTimes()) && !inputVO.geteTimes().toString().equals("0")) {
			sb.append("AND TO_DATE(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("YEARMON, 'YYYYMM') <= TO_DATE(:ENDTIME, 'YYYYMM') ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
			sb.append("AND TO_DATE(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("YEARMON, 'YYYYMM') >= TO_DATE(:STARTTIME, 'YYYYMM') ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
			sb.append("AND TO_DATE(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("YEARMON, 'YYYYMM') <= TO_DATE(:ENDTIME, 'YYYYMM') ");
		}
		
		boolean teamLeaderFlag = false;
		if (teamDTL.size() > 0) {
			teamLeaderFlag = StringUtils.equals("Y", (String) teamDTL.get(0).get("LEADER_FLAG")) ? true : false;
		}
		
		switch (genType) {
			case "EMP":
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS329DT")) {
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
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS329DT")) {
					sb.append("AND TEAM_TYPE IS NOT NULL ");
					
					if (isFC && teamLeaderFlag) {
						sb.append("AND TEAM_TYPE = :teamType ");
					} else if (isFC || StringUtils.isNotBlank(inputVO.getEmp_id())) {
						sb.append("AND TEAM_TYPE = :teamType ");
					}
				}
			case "BRANCH":
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS329DT")) {
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
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS329DT")) {
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
						// 營運區
						if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
							sb.append("AND BRANCH_AREA_ID = :branch_area_id ");
						} else if (!isHeadMGR) {
							// 登入非總行人員強制加營運區
							sb.append("AND BRANCH_AREA_ID IN (:branch_area_id) ");
						}
						
						break;
				}
			case "REGION":
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS329DT")) {
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
				if (StringUtils.equals(inputVO.getFuncPage(), "PMS329DT")) {
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
		
		PMS329InputVO inputVO = (PMS329InputVO) body;
		PMS329OutputVO outputVO = new PMS329OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (StringUtils.isEmpty(inputVO.getNOT_EXIST_UHRM())) {
			inputVO.setNOT_EXIST_UHRM("N");
		}
		
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
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id()) && teamDTL.size() == 0) {
			throw new JBranchException("該人員無 Diamond Team 組別");
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
		if (StringUtils.equals(inputVO.getFuncPage(), "PMS329DT")) {
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
		// 日期
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
		
		// EMP
		if (StringUtils.equals(inputVO.getFuncPage(), "PMS329DT")) {
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

		PMS329OutputVO return_VO = (PMS329OutputVO) body;
		
		String fileName = "AUM報表" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + (String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		
		List<Map<String, Object>> list = return_VO.getList();
		List listCSV = new ArrayList();

		try {
			if (list.size() > 0) {
				String.format("%1$,09d", -3123);
				
				String[] csvHeader = {"資料統計年月", "業務處名稱", "營運區名稱", "分行ID", "分行名稱", "AO Code", "Code別", "理專員編", "理專姓名",
						  			  "存投保合計",
						  			  "投資-小計", "投資-基金", "投資-ETF", "投資-海外股票", "投資-SI", "投資-DCI", "投資-SN", "投資-RP", "投資-海外債", "投資-黃金存摺", "投資-金錢信託", "投資-奈米投", "投資-金市海外債",
						  			  "保險-小計", "保險-投資型", "保險-躉繳", "保險-短年期繳", "保險-長年期繳", "保險-非富壽",
						  			  "存款(A板塊+C板塊)-小計", "存款(A板塊+C板塊)-台幣活存", "存款(A板塊+C板塊)-台幣定存", "存款(A板塊+C板塊)-外幣活存", "存款(A板塊+C板塊)-外幣定存", 
						  			  "存款(A板塊)-台幣活存", "存款(A板塊)-台幣定存", "存款(A板塊)-外幣活存", "存款(A板塊)-外幣定存", 
						  			  "存款-C版塊"};
				
				String[] csvMain = {  "YEARMON", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "AO_CODE", "AO_TYPE", "EMP_ID", "EMP_NAME",
								      "SUM_BAL",
								      "INV_BAL", "INV_FUND", "INV_ETF", "INV_STOCK", "INV_SI", "INV_DCI", "INV_SN", "INV_RP", "INV_BOND", "INV_GOLD", "INV_STRST", "INV_NAMI", "INV_VBOND",
								      "INS_BAL", "INS_IV", "INS_OT", "INS_SY", "INS_LY", "INS_NF",
								      "DEP_BAL", "DEP_SAV", "DEP_CD", "DEP_FSAV", "DEP_FCD", 
									  "DEP_A_SAV", "DEP_A_CD", "DEP_A_FSAV", "DEP_A_FCD",
								      "C_AVG_AUM"};
				
				for (Map<String, Object> map : list) {
					if (!(checkIsNull(map, "AO_CODE").equals("ZZZZZREGION") || 
						  checkIsNull(map, "AO_CODE").equals("ZZZZZAREA") || 
						  checkIsNull(map, "AO_CODE").equals("ZZZZZBRANCH") ||
						  checkIsNull(map, "AO_CODE").equals("ZZZZZ") ||
						  checkIsNull(map, "AO_CODE").equals("ZZZZZTEAM"))) {
						
						String[] records = new String[csvMain.length];

						//加總存投保合計-by 20171107 willis 問題單:0003914
						Double d_sumBal = Double.parseDouble(map.get("INV_BAL").toString()) + Double.parseDouble(map.get("INS_BAL").toString()) + Double.parseDouble(map.get("DEP_BAL").toString());
						map.put("SUM_BAL", new BigDecimal(d_sumBal).setScale(0, BigDecimal.ROUND_HALF_UP)); // 四捨五入取整數

						for (int i = 0; i < csvHeader.length; i++) {
							switch (csvMain[i]) {
								case "YEARMON":
								case "REGION_CENTER_NAME":
								case "BRANCH_AREA_NAME":
								case "BRANCH_NBR":
								case "BRANCH_NAME":
								case "AO_CODE":
								case "EMP_ID":
								case "EMP_NAME":
									records[i] = "=\"" + (checkIsNull(map, csvMain[i])) + "\"";
									
									break;
								case "AO_TYPE":
									String codeType = checkIsNull(map, csvMain[i]);
									records[i] = "=\"" + (StringUtils.equals("1", codeType) ? "計績" : StringUtils.equals("2", codeType) ? "兼職" : StringUtils.equals("3", codeType) ? "維護" : "000") + "\"";
									
									break;
								case "DEP_A_SAV":
									BigDecimal depAsav = new BigDecimal((null != map.get(csvMain[i]) ? map.get(csvMain[i]) : "0").toString()).add(new BigDecimal((null != map.get("DEP_A_CSAV") ? map.get("DEP_A_CSAV") : "0").toString())); // 活存+支存
									if (depAsav.compareTo(BigDecimal.ZERO) == 0) {
										records[i] = "0";
									} else {
										records[i] = new DecimalFormat("#,##0").format(depAsav);
									}
									
									break;
								default:
									records[i] = currencyFormat(map, csvMain[i]);
									
									break;
							}
						}
						
						listCSV.add(records);
					}
				}
				
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();

				notifyClientToDownloadFile(url, fileName);

				this.sendRtnObject(null);
			} else {
				return_VO.setResultList(list);
				
				this.sendRtnObject(return_VO);
			}
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

	/** 處理貨幣格式 **/
	private String currencyFormat(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0");
			return df.format(map.get(key));
		} else
			return "0";
	}
}