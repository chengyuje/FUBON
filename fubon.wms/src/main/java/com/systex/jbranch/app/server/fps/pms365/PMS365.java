package com.systex.jbranch.app.server.fps.pms365;

import java.math.BigDecimal;
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
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms365")
@Scope("request")
public class PMS365 extends FubonWmsBizLogic {

	DataAccessManager dam = null;

	public StringBuffer returnAppend (String genType, String sqlType) {
		
		StringBuffer sb = new StringBuffer();
		
		switch (genType) {
			case "BASE":
				sb.append("       VW.REGION_CENTER_ID").append(", ");
				sb.append("       VW.REGION_CENTER_NAME").append(", ");
				sb.append("       VW.BRANCH_AREA_ID").append(", ");
				sb.append("       VW.BRANCH_AREA_NAME").append(", ");
				sb.append("       AUM.BRANCH_NBR").append(", ");
				sb.append("       VW.BRANCH_NAME").append(", ");
				sb.append("       AUM.YYYYMM").append(", ");
				sb.append("       EMP.DEPT_ID, ");
				sb.append("       AUM.AO_CODE").append(", ");
				sb.append("       AO.TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", ");
				sb.append("       AO.EMP_ID").append(", ");
				sb.append("       CASE WHEN AO.EMP_NAME IS NULL THEN '○○○' ELSE AO.EMP_NAME END ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME, " : " ");
				
				break;
			case "ALL":
				sb.append("       'ALL_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       '全行 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM, "); 
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
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM, "); 
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
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM, "); 
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
				sb.append("       YYYYMM, "); 
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
				sb.append("       YYYYMM").append(", ");
				sb.append("       DEPT_ID").append(", ");
				sb.append("       AO_CODE").append(", ");
				sb.append("       AO_TYPE").append(", ");
				sb.append("       EMP_ID").append(", ");
				sb.append("       EMP_NAME").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
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
				sb.append("         DECODE(AO_CODE, '000', 997, 'ZZZZZTEAM', 998, 'ZZZZZBRANCH', 999, 0), ");    
				sb.append("         DECODE(AO_TYPE, NULL, 993, 'ZZZZZTEAM', 994, 'ZZZZZBRANCH', 995, 'ZZZZZAREA', 996, 'ZZZZZREGION', 997, 0), ");  
				sb.append("         EMP_ID DESC, ");    
				sb.append("         AO_TYPE, ");    
				sb.append("         YYYYMM ");
				
				break;
		}
		
		return sb;
	}
	
	public StringBuffer genSQL (String genType, PMS365InputVO inputVO, boolean isFC, boolean isHeadMGR, String NOT_EXIST_BS, String NOT_EXIST_UHRM, String MEM_LOGIN_FLAG) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(returnAppend(genType, "SELECT"));
		
		// 投資
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_SAV_L_M)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_SAV_L_M,  ");		// 存款AUM(萬)-本月
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_SAV_T_M)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_SAV_T_M,  ");		// 存款AUM(萬)-上月
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_SAV_DIFF)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_SAV_DIFF, ");		// 存款AUM(萬)-差異
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_INS_L_M)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_INS_L_M,  ");		// 投資AUM(萬)-本月
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_INS_T_M)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_INS_T_M,  ");		// 投資AUM(萬)-上月
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_INS_DIFF)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_INS_DIFF, ");		// 投資AUM(萬)-差異
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_INV_L_M)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_INV_L_M,  ");		// 保險AUM(萬)-本月
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_INV_T_M)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_INV_T_M,  ");		// 保險AUM(萬)-上月
		sb.append("       ").append(StringUtils.equals(genType, "BASE") ? "ROUND(" : "").append("SUM(").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("AUM_INV_DIFF)").append(StringUtils.equals(genType, "BASE") ? " / 10000, 4)" : "").append(" AS AUM_INV_DIFF  ");		// 保險AUM(萬)-差異
		
		switch (genType) {
			case "BASE":
				sb.append("FROM TBPMS_CUST_LOSS_AUM_AO AUM ");
				sb.append("INNER JOIN VWORG_DEFN_INFO VW ON VW.BRANCH_NBR = AUM.BRANCH_NBR ");
				sb.append("LEFT JOIN VWORG_AO_INFO AO ON AUM.AO_CODE = AO.AO_CODE ");
				sb.append("LEFT JOIN (SELECT DISTINCT EMP_ID, EMP_DEPT_ID AS DEPT_ID FROM VWORG_EMP_INFO) EMP ON AO.EMP_ID = EMP.EMP_ID ");
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

				switch (NOT_EXIST_UHRM) {
					case "Y":
						sb.append("AND NOT EXISTS ( ");
						sb.append("  SELECT 1 ");
						sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND LAST_DAY(TO_DATE(:YYYYMM || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
						sb.append("  AND UCN.DEPT_ID <> UCN.E_DEPT_ID "); // 私銀理專
						sb.append("  AND BASE.EMP_ID = UCN.EMP_ID ");
						sb.append(") ");
						break;
					case "U":
						sb.append("AND EXISTS ( ");
						sb.append("  SELECT 1 ");
						sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND LAST_DAY(TO_DATE(:YYYYMM || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
						sb.append("  AND UCN.DEPT_ID <> UCN.E_DEPT_ID "); // 私銀理專
						sb.append("  AND BASE.EMP_ID = UCN.EMP_ID ");
						sb.append(") ");
				}
				
				break;
		}
		
		// ==主查詢條件==
		// 日期
		if (!StringUtils.isBlank(inputVO.getsCreDate()) && !inputVO.getsCreDate().toString().equals("0")) {
			sb.append("AND ").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("YYYYMM = :YYYYMM ");
		}
		
		if (StringUtils.lowerCase(MEM_LOGIN_FLAG).indexOf("uhrm") < 0 || 
			StringUtils.lowerCase(MEM_LOGIN_FLAG).equals("uhrm")) {
			if (isHeadMGR) { // 總人行員 且 非 為031之兼任FC，僅可視轄下
			} else if (!isHeadMGR && !StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) { // 非總人行員 且 非 為031之兼任FC，僅可視轄下
				switch (genType) {
					case "BASE":
						sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE U.EMP_ID = AO.EMP_ID) ");
						break;
					case "EMP":
					case "TEAM":
					case "BRANCH":
					case "AREA":
					case "REGION":
					case "ALL":
						sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE U.EMP_ID = BASE.EMP_ID) ");
						break;
				}
			}
		} else if (StringUtils.lowerCase(MEM_LOGIN_FLAG).indexOf("uhrm") >= 0) {
			switch (genType) {
				case "BASE":
					sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE U.EMP_ID = AO.EMP_ID) ");
					break;
				case "EMP":
				case "TEAM":
				case "BRANCH":
				case "AREA":
				case "REGION":
				case "ALL":
					sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE U.EMP_ID = BASE.EMP_ID) ");
					break;
			}
		}
	
		switch (genType) {
			case "EMP":
				if (isFC) {
					sb.append("AND EMP_ID = :empID ");
				} else if (StringUtils.isNotBlank(inputVO.getAo_code())) {
					sb.append("AND AO_CODE = :aoCode ");
				} else if (StringUtils.equals(MEM_LOGIN_FLAG, "UHRM")) {
					sb.append("AND EMP_ID = :loginID ");
				}
			case "BRANCH":
				switch (MEM_LOGIN_FLAG) {
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
				switch (MEM_LOGIN_FLAG) {
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
				switch (MEM_LOGIN_FLAG) {
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
						if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
							sb.append("AND REGION_CENTER_ID = :region_center_id ");
						} else if (!isHeadMGR) {
							// 登入非總行人員強制加區域中心
							sb.append("AND REGION_CENTER_ID IN (:region_center_id) ");
						}
						
						break;
				}
				
		}
		sb.append("GROUP BY ");
		sb.append(returnAppend(genType, "GROUP") + " ");

		return sb;
	}
	
	/** 主查詢 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS365InputVO inputVO = (PMS365InputVO) body;
		PMS365OutputVO outputVO = new PMS365OutputVO();
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
		
		//=== TEAM SQL START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		//=== MAIN SQL START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM ( ").append(genSQL("BASE", inputVO, isFC, isHANDMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")).append("  ) "); 
		sb.append(") ");
		
		sb.append("SELECT * ");
		sb.append("FROM ( "); 
		
		// 人員
		sb.append(genSQL("EMP", inputVO, isFC, isHANDMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), memLoginFlag).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 分行合計
		sb.append(genSQL("BRANCH", inputVO, isFC, isHANDMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), memLoginFlag).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 營運區合計
		sb.append(genSQL("AREA", inputVO, isFC, isHANDMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), memLoginFlag).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 業務處合計
		sb.append(genSQL("REGION", inputVO, isFC, isHANDMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), memLoginFlag).toString().replaceAll("\\s+", " ")); 

		sb.append("UNION "); 
		
		// 全行合行
		sb.append(genSQL("ALL", inputVO, isFC, isHANDMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), memLoginFlag).toString().replaceAll("\\s+", " ")); 

		sb.append(") "); 
		sb.append(returnAppend("ORDER", "").toString().replaceAll("\\s+", " "));
		
		// ==主查詢條件==
		// 日期
		if (StringUtils.isNotBlank(inputVO.getsCreDate())) {
			queryCondition.setObject("YYYYMM", inputVO.getsCreDate());
		}
		
		// EMP 
		if (isFC) {
			queryCondition.setObject("empID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			queryCondition.setObject("aoCode", inputVO.getAo_code());
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
					//登入非總行人員強制加分行
					queryCondition.setObject("branch_nbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				
				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
					queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
				} else if (!isHANDMGR) {
					//登入非總行人員強制加營運區
					queryCondition.setObject("branch_area_id", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
				
				// 區域中心
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
					queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
				} else if (!isHANDMGR) {
					//登入非總行人員強制加區域中心
					queryCondition.setObject("region_center_id", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
				
				break;
		}
		
		//=== MAIN SQL END ===

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		outputVO.setResultList(list);
		outputVO.setTotalList(list);
		outputVO.setCsvList(list);
		
		this.sendRtnObject(outputVO);
	}

	/** 明細 **/
	public void getDTL(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		XmlInfo xmlInfo = new XmlInfo();
		
		PMS365InputVO inputVO = (PMS365InputVO) body;
		PMS365OutputVO outputVO = new PMS365OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT * ");
		sb.append("FROM ( ");
		switch (inputVO.getAumType()) {
			case "AO":
				sb.append("SELECT BASE.*, NVL(NOTE.CO_ACCT_YN, 'N') AS CUST_07 ");
				sb.append("FROM ( ");
				sb.append("  SELECT AO_CODE, CUST_ID, CUST_NAME, ");
				sb.append("         ROUND(AUM_SAV_T_M / 10000, 4)  AS AUM_SAV_T_M, ");
				sb.append("         ROUND(AUM_SAV_L_M / 10000, 4)  AS AUM_SAV_L_M, ");
				sb.append("         ROUND(AUM_SAV_DIFF / 10000, 4) AS AUM_SAV_DIFF, ");
				sb.append("         ROUND(AUM_INS_T_M / 10000, 4)  AS AUM_INS_T_M, ");
				sb.append("         ROUND(AUM_INS_L_M / 10000, 4)  AS AUM_INS_L_M, ");
				sb.append("         ROUND(AUM_INS_DIFF / 10000, 4) AS AUM_INS_DIFF, ");
				sb.append("         ROUND(AUM_INV_T_M / 10000, 4)  AS AUM_INV_T_M, ");
				sb.append("         ROUND(AUM_INV_L_M / 10000, 4)  AS AUM_INV_L_M, ");
				sb.append("         ROUND(AUM_INV_DIFF / 10000, 4) AS AUM_INV_DIFF ");
				sb.append("  FROM ( ");
				sb.append("    SELECT AO_CODE, CUST_ID, CUST_NAME, AUM_COLUMN || '_' || AUM_TYPE AS AUM_TYPE, AUM ");
				sb.append("    FROM ( ");
				sb.append("      SELECT AO_CODE, CUST_ID, CUST_NAME, AUM_T_M, AUM_L_M, AUM_DIFF, AUM_TYPE ");
				sb.append("      FROM TBPMS_CUST_LOSS_AUM_CUST ");
				sb.append("      WHERE 1 = 1 ");
				sb.append("      AND YYYYMM = :yyyymm ");
				sb.append("      AND BRANCH_NBR = :branchNbr ");
				sb.append("      AND AUM_DIFF <> 0 ");
				sb.append("      AND AO_CODE ").append(StringUtils.equals(inputVO.getAoCode(), "000") ? "IS NULL" : "= :aoCode").append(" ");
				sb.append("    ) ");
				sb.append("    UNPIVOT (AUM FOR AUM_COLUMN IN(AUM_T_M, AUM_L_M, AUM_DIFF)) ");
				sb.append("  ) ");
				sb.append("  PIVOT ( ");
				sb.append("    SUM(AUM) FOR AUM_TYPE IN ( ");
				sb.append("      'AUM_T_M_SAV'  AS AUM_SAV_T_M, ");
				sb.append("      'AUM_L_M_SAV'  AS AUM_SAV_L_M, ");
				sb.append("      'AUM_DIFF_SAV' AS AUM_SAV_DIFF, ");
				sb.append("      'AUM_T_M_INS'  AS AUM_INS_T_M, ");
				sb.append("      'AUM_L_M_INS'  AS AUM_INS_L_M, ");
				sb.append("      'AUM_DIFF_INS' AS AUM_INS_DIFF, ");
				sb.append("      'AUM_T_M_INV'  AS AUM_INV_T_M, ");
				sb.append("      'AUM_L_M_INV'  AS AUM_INV_L_M, ");
				sb.append("      'AUM_DIFF_INV' AS AUM_INV_DIFF ");
				sb.append("    ) ");
				sb.append("  ) ");
				sb.append(") BASE ");
				sb.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON NOTE.CUST_ID = BASE.CUST_ID ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND (ABS(AUM_SAV_DIFF) > 10 OR ABS(AUM_INS_DIFF) > 10 OR ABS(AUM_INV_DIFF) > 10) ");
				
				break;
			default :
				sb.append("SELECT BASE.*, NVL(NOTE.CO_ACCT_YN, 'N') AS CUST_07 ");
				sb.append("FROM ( ");
				sb.append("  SELECT AO_CODE, CUST_ID, CUST_NAME, ");
				sb.append("         ROUND(AUM_T_M / 10000, 4)  AS AUM_T_M, ");
				sb.append("         ROUND(AUM_L_M / 10000, 4)  AS AUM_L_M, ");
				sb.append("         ROUND(AUM_DIFF / 10000, 4) AS AUM_DIFF ");
				sb.append("  FROM ( ");
				sb.append("    SELECT AO_CODE, CUST_ID, CUST_NAME, AUM_COLUMN AS AUM_TYPE, AUM ");
				sb.append("    FROM ( ");
				sb.append("      SELECT AO_CODE, CUST_ID, CUST_NAME, AUM_T_M, AUM_L_M, AUM_DIFF, AUM_TYPE ");
				sb.append("      FROM TBPMS_CUST_LOSS_AUM_CUST ");
				sb.append("      WHERE 1 = 1 ");
				sb.append("      AND YYYYMM = :yyyymm ");
				sb.append("      AND BRANCH_NBR = :branchNbr ");
				sb.append("      AND AUM_DIFF <> 0 ");
				sb.append("      AND AO_CODE ").append(StringUtils.equals(inputVO.getAoCode(), "000") ? "IS NULL" : "= :aoCode").append(" ");
				sb.append("      AND AUM_TYPE = :aumType ");
				sb.append("    ) ");
				sb.append("    UNPIVOT (AUM FOR AUM_COLUMN IN(AUM_T_M, AUM_L_M, AUM_DIFF)) ");
				sb.append("  ) ");
				sb.append("  PIVOT ( ");
				sb.append("    SUM(AUM) FOR AUM_TYPE IN ( ");
				sb.append("      'AUM_T_M'  AS AUM_T_M, ");
				sb.append("      'AUM_L_M'  AS AUM_L_M, ");
				sb.append("      'AUM_DIFF' AS AUM_DIFF ");
				sb.append("    ) ");
				sb.append("  ) ");
				sb.append(") BASE ");
				sb.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON NOTE.CUST_ID = BASE.CUST_ID ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND ABS(AUM_DIFF) > 10 ");
				
				if (StringUtils.isNotEmpty(inputVO.getAumType())) {
					queryCondition.setObject("aumType", inputVO.getAumType());
				} 
				
				break;
		}

		sb.append(")  ");
		sb.append("WHERE 1 = 1 ");
		
		queryCondition.setObject("branchNbr", inputVO.getBranchNbr());
		queryCondition.setObject("yyyymm", inputVO.getYyyymm());
		
		if (!StringUtils.equals(inputVO.getAoCode(), "000")) {
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		} 
		
		switch (inputVO.getAumType()) {
			case "AO":
				sb.append("ORDER BY AUM_SAV_DIFF ASC, AUM_INS_DIFF ASC, AUM_INV_DIFF ASC,  CUST_ID ");
				break;
			default :
				sb.append("ORDER BY AUM_DIFF ASC, CUST_ID ");
				break;
		}
		
//		sb.append("FETCH FIRST 100 ROWS ONLY ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setDtlList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/** 匯出 **/
	public void export(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		PMS365InputVO inputVO = (PMS365InputVO) body;
		
		String fileName = "AUM報表_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + (String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		
		List<Map<String, Object>> list = inputVO.getExportList();
		List listCSV = new ArrayList();

		String[] csvHeader = {"資料年月", "業務處", "區別", "分行", "AO CODE",
				  			  "存款AUM(萬)-本月", "存款AUM(萬)-上月", "存款AUM(萬)-差異",
				  			  "投資AUM(萬)-本月", "投資AUM(萬)-上月", "投資AUM(萬)-差異",
				  			  "保險AUM(萬)-本月", "保險AUM(萬)-上月", "保險AUM(萬)-差異"};

		String[] csvMain = {  "YYYYMM", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "AO_CODE",
						      "AUM_SAV_T_M", "AUM_SAV_L_M", "AUM_SAV_DIFF", 
						      "AUM_INV_T_M", "AUM_INV_L_M", "AUM_INV_DIFF", 
						      "AUM_INS_T_M", "AUM_INS_L_M", "AUM_INS_DIFF"};
		
		for (Map<String, Object> map : list) {
			String[] records = new String[csvMain.length];

			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
					case "YYYYMM":
						records[i] = "=\"" + (checkIsNull(map, csvMain[i])) + "\"";
						break;
					case "REGION_CENTER_NAME":
					case "BRANCH_AREA_NAME":
					case "BRANCH_NAME":
						switch((checkIsNull(map, "AO_CODE"))) {
							case "ZZZZZBRANCH":
								records[i] = StringUtils.equals("BRANCH_NAME", csvMain[i]) ? ("=\"" + (checkIsNull(map, csvMain[i])) + " 合計\"") : (checkIsNull(map, csvMain[i]));
								break;
							case "ZZZZZAREA":
								records[i] = StringUtils.equals("BRANCH_AREA_NAME", csvMain[i]) ? ("=\"" + (checkIsNull(map, csvMain[i])) + " 合計\"") : (checkIsNull(map, csvMain[i]));
								break;
							case "ZZZZZREGION":
								records[i] = StringUtils.equals("REGION_CENTER_NAME", csvMain[i]) ? ("=\"" + (checkIsNull(map, csvMain[i])) + " 合計\"") : (checkIsNull(map, csvMain[i]));
								break;
							case "ZZZZZ":
								records[i] = StringUtils.equals("REGION_CENTER_NAME", csvMain[i]) ? ("=\"" + (checkIsNull(map, csvMain[i])) + "\"") : (checkIsNull(map, csvMain[i]));
								break;
							default:
								records[i] = "=\"" + (checkIsNull(map, csvMain[i])) + "\"";
								break;
						}
						
						break;
					case "AO_CODE":
						switch((checkIsNull(map, csvMain[i]))) {
							case "ZZZZZBRANCH":
							case "ZZZZZAREA":
							case "ZZZZZREGION":
							case "ZZZZZ":
								records[i] = "";
								break;
							default:
								String empTemp = (checkIsNull(map, csvMain[i]));
								switch ((checkIsNull(map, "AO_TYPE"))) {
									case "1":
										empTemp += "(計績)";
										break;
									case "2":
										empTemp += "(兼職)";
										break;
									case "3":
										empTemp += "(維護)";
										break;
								}
								empTemp += "-" + (checkIsNull(map, "EMP_NAME"));
								
								records[i] = empTemp;
								break;
						}
						
						break;
					case "AUM_SAV_T_M":
					case "AUM_SAV_L_M":
					case "AUM_SAV_DIFF":
					case "AUM_INS_T_M":
					case "AUM_INS_L_M":
					case "AUM_INS_DIFF":
					case "AUM_INV_T_M":
					case "AUM_INV_L_M":
					case "AUM_INV_DIFF":
						BigDecimal aum = new BigDecimal((null != map.get(csvMain[i]) ? map.get(csvMain[i]) : "0").toString()).add(new BigDecimal((null != map.get("DEP_A_CSAV") ? map.get("DEP_A_CSAV") : "0").toString())); // 活存+支存
						if (aum.compareTo(BigDecimal.ZERO) == 0) {
							records[i] = "0";
						} else {
							records[i] = new DecimalFormat("#,##0").format(aum);
						}
						
						break;
					default:
						records[i] = currencyFormat(map, csvMain[i]);
						
						break;
				}
			}
			
			listCSV.add(records);
		}
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);

		this.sendRtnObject(null);
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