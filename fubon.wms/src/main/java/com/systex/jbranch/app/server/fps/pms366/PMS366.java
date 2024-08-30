package com.systex.jbranch.app.server.fps.pms366;

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

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms366")
@Scope("request")
public class PMS366 extends FubonWmsBizLogic {

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
				sb.append("       TO_CHAR(AUM.CREATETIME, 'YYYY/MM/DD') AS DATA_DATE").append(", ");
				sb.append("       CASE WHEN AUM.AO_CODE = 'MASS' THEN '000' ELSE AUM.AO_CODE END AS AO_CODE ").append(", ");
				sb.append("       AO.TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", ");
				sb.append("       AO.EMP_ID").append(", ");
				sb.append("       CASE WHEN AO.EMP_NAME IS NULL THEN '○○○' ELSE AO.EMP_NAME END ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME, " : ", ");
				sb.append("       AUM.DEP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEP_TYPE," : "").append(" ");
				
				break;
			case "ALL":
				sb.append("       'ALL_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       '全行 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM, "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS DATA_DATE" : "").append(", "); 
				sb.append("       'ZZZZZ' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZ' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", ");
				sb.append("       DEP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEP_TYPE": "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");

				break;
			case "REGION":
				sb.append("       REGION_CENTER_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       REGION_CENTER_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM, "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS DATA_DATE" : "").append(", "); 
				sb.append("       'ZZZZZREGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZREGION' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", "); 
				sb.append("       DEP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEP_TYPE": "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");

				break;
			case "AREA":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", ");
				sb.append("       BRANCH_AREA_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR " : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME " : "").append(", "); 
				sb.append("       YYYYMM, "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS DATA_DATE" : "").append(", "); 
				sb.append("       'ZZZZZAREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZAREA' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", "); 
				sb.append("       DEP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEP_TYPE": "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "BRANCH":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", ");
				sb.append("       BRANCH_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM, "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS DATA_DATE" : "").append(", "); 
				sb.append("       'ZZZZZBRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_CODE" : "").append(", "); 
				sb.append("       'ZZZZZBRANCH' ").append(StringUtils.equals("SELECT", sqlType) ? "AS AO_TYPE" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_NAME" : "").append(", "); 
				sb.append("       DEP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS DEP_TYPE": "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "EMP":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR").append(", ");
				sb.append("       BRANCH_NAME").append(", ");
				sb.append("       YYYYMM").append(", ");
				sb.append("       DATA_DATE").append(", ");
				sb.append("       AO_CODE").append(", ");
				sb.append("       AO_TYPE").append(", ");
				sb.append("       EMP_ID").append(", ");
				sb.append("       EMP_NAME").append(", ");
				sb.append("       DEP_TYPE").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
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
				sb.append("         YYYYMM, ");
				sb.append("         DECODE(DEP_TYPE, 'TW_SDEP', 1, 'TW_RDEP', 2, 'FO_SDEP', 3, 'FO_RDEP', 99) ");
				
				break;
		}
		
		return sb;
	}
	
	public StringBuffer genSQL (String genType, PMS366InputVO inputVO, boolean isFC, boolean isHeadMGR, String NOT_EXIST_BS, String NOT_EXIST_UHRM, String MEM_LOGIN_FLAG) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(returnAppend(genType, "SELECT"));
		
		switch (genType) {
			case "BASE":
			case "EMP":
				sb.append("       T_M AS T_M,  ");		// 本月
				sb.append("       L_M AS L_M,  ");		// 上月
				sb.append("       DIFF AS DIFF  ");		// 差異
				break;
			case "BRANCH":
			case "AREA":
			case "REGION":
			case "ALL":
				sb.append("       SUM(T_M)  AS T_M, ");
				sb.append("       SUM(L_M)  AS L_M, ");
				sb.append("       SUM(DIFF) AS DIFF ");
				break;
		}
		
		switch (genType) {
			case "BASE":
				sb.append("FROM TBPMS_CUST_DEP_AUM_AO AUM ");
				sb.append("INNER JOIN VWORG_DEFN_INFO VW ON VW.BRANCH_NBR = AUM.BRANCH_NBR ");
				sb.append("LEFT JOIN VWORG_AO_INFO AO ON AUM.AO_CODE = AO.AO_CODE ");
				sb.append("WHERE 1 = 1 ");
				
				break;
			case "EMP":
			case "BRANCH":
			case "AREA":
			case "REGION":
			case "ALL":
				sb.append("FROM BASE ");
				sb.append("WHERE 1 = 1 ");

				if (StringUtils.equals("Y", NOT_EXIST_BS)) {
					sb.append("AND NOT EXISTS ( ");
					sb.append("  SELECT 1 ");
					sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
					sb.append("  WHERE 1 = 1 ");
					sb.append("  AND LAST_DAY(TO_DATE(:YYYYMM || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
					sb.append("  AND IS_PRIMARY_ROLE = 'N' ");
					sb.append("  AND AO_JOB_RANK IS NOT NULL ");
					sb.append("  AND NOT EXISTS (SELECT 1 FROM TBPMS_EMPLOYEE_REC_N UCN_T WHERE UCN_T.DEPT_ID IN ('031', '1001') AND UCN.EMP_ID = UCN_T.EMP_ID) ");
					sb.append("  AND BASE.EMP_ID = UCN.EMP_ID ");
					sb.append(") ");
				}
				
				if (StringUtils.equals("Y", NOT_EXIST_UHRM)) {
					sb.append("AND NOT EXISTS ( ");
					sb.append("  SELECT 1 ");
					sb.append("  FROM TBPMS_EMPLOYEE_REC_N UCN ");
					sb.append("  WHERE 1 = 1 ");
					sb.append("  AND LAST_DAY(TO_DATE(:YYYYMM || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
					sb.append("  AND IS_PRIMARY_ROLE = 'N' ");
					sb.append("  AND AO_JOB_RANK IS NOT NULL ");
					sb.append("  AND EXISTS (SELECT 1 FROM TBPMS_EMPLOYEE_REC_N UCN_T WHERE UCN_T.DEPT_ID IN ('031', '1001') AND UCN.EMP_ID = UCN_T.EMP_ID) ");
					sb.append("  AND BASE.EMP_ID = UCN.EMP_ID ");
					sb.append(") ");
				}
				
				break;
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
		
		// ==主查詢條件==
		// 日期
		if (!StringUtils.isBlank(inputVO.getsCreDate()) && !inputVO.getsCreDate().toString().equals("0")) {
			sb.append("AND ").append(StringUtils.equals(genType, "BASE") ? "AUM." : "").append("YYYYMM = :YYYYMM ");
		}
		
		switch (genType) {
			case "EMP":
				if (isFC) {
					sb.append("AND EMP_ID = :empID ");
				} else if (StringUtils.isNotBlank(inputVO.getAo_code())) {
					sb.append("AND AO_CODE = :aoCode ");
				} 
			case "BRANCH":
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sb.append("AND BRANCH_NBR = :branch_nbr ");
				} else if (!isHeadMGR) {
					// 登入非總行人員強制加分行
					sb.append("AND BRANCH_NBR IN (:branch_nbr) ");
				}
			case "AREA":
				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
					sb.append("AND BRANCH_AREA_ID = :branch_area_id ");
				} else if (!isHeadMGR) {
					// 登入非總行人員強制加營運區
					sb.append("AND BRANCH_AREA_ID IN (:branch_area_id) ");
				}
			case "REGION":
				// 區域中心
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
					sb.append("AND REGION_CENTER_ID = :region_center_id ");
				} else if (!isHeadMGR) {
					// 登入非總行人員強制加區域中心
					sb.append("AND REGION_CENTER_ID IN (:region_center_id) ");
				}
		}
		
		if (!StringUtils.equals(genType, "BASE") && !StringUtils.equals(genType, "EMP") ) {
			sb.append("GROUP BY ");
			sb.append(returnAppend(genType, "GROUP") + " ");
		}
		
		return sb;
	}
	
	/** 主查詢 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS366InputVO inputVO = (PMS366InputVO) body;
		PMS366OutputVO outputVO = new PMS366OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		XmlInfo xmlInfo = new XmlInfo();
		boolean isHeadMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));

//		//=== 取得查詢資料可視範圍 START ===
//		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
//		PMS000InputVO pms000InputVO = new PMS000InputVO();
//		pms000InputVO.setReportDate(inputVO.getReportDate());
//		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
//		//=== 取得查詢資料可視範圍 END ===
		
		//=== TEAM SQL START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		//=== MAIN SQL START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM ( ").append(genSQL("BASE", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")).append("  ) "); 
		sb.append(") ");
		
		sb.append("SELECT * ");
		sb.append("FROM ( "); 
		
		// 人員
		sb.append(genSQL("EMP", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 分行合計
		sb.append(genSQL("BRANCH", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 營運區合計
		sb.append(genSQL("AREA", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 業務處合計
		sb.append(genSQL("REGION", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 

		sb.append("UNION "); 
		
		// 全行合行
		sb.append(genSQL("ALL", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 

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
		
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		} else if (!isHeadMGR) {
			//登入非總行人員強制加分行
			queryCondition.setObject("branch_nbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		// 營運區
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		} else if (!isHeadMGR) {
			//登入非總行人員強制加營運區
			queryCondition.setObject("branch_area_id", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
		
		// 區域中心
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		} else if (!isHeadMGR) {
			//登入非總行人員強制加區域中心
			queryCondition.setObject("region_center_id", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
		
		//=== MAIN SQL END ===
		
		queryCondition.setQueryString(sb.toString());
						
		List<Map<String, Object>> csvList = dam.exeQuery(queryCondition);

		outputVO.setResultList(csvList);
		outputVO.setTotalList(csvList);
		outputVO.setCsvList(csvList);
		
		this.sendRtnObject(outputVO);
	}

	/** 明細 **/
	public void getDTL(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS366InputVO inputVO = (PMS366InputVO) body;
		PMS366OutputVO outputVO = new PMS366OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// 增加排行榜
		sb.append("  SELECT CUST_ID, CUST_NAME, AUM * 1000000 AS AUM ");
		sb.append("  FROM ( ");
		sb.append("    SELECT BASE.CUST_ID, CM.CUST_NAME, BASE.AUM ");
		sb.append("    FROM ( ");
		sb.append("      SELECT YYYYMM, BRANCH_NBR, CUST_ID, ");
		sb.append("             CASE WHEN AO_CODE IS NULL THEN 'MASS' ELSE AO_CODE END AS AO_CODE, ");
		sb.append("             SUBSTR(AUM_TYPE, 0, INSTR(AUM_TYPE, '_', 1, 2) - 1) AS DEP_TYPE, ");
		sb.append("             SUBSTR(AUM_TYPE, INSTR(AUM_TYPE, '_', 1, 2) + 1, LENGTH(AUM_TYPE)) AS COL_TYPE, ");
		sb.append("             AUM ");
		sb.append("      FROM ( ");
		sb.append("        SELECT * ");
		sb.append("        FROM TBPMS_CUST_DEP_AUM_CUST ");
		sb.append("        WHERE (TW_SDEP_DIFF <> 0 OR TW_RDEP_DIFF <> 0 OR FO_SDEP_DIFF <> 0 OR FO_RDEP_DIFF <> 0 OR SUM_DEP_DIFF <> 0) ");
		sb.append("        AND YYYYMM = :YYYYMM ");
		sb.append("      ) ");
		sb.append("      UNPIVOT(AUM FOR AUM_TYPE IN(TW_SDEP_L_M, TW_SDEP_T_M, TW_SDEP_DIFF, TW_RDEP_L_M, TW_RDEP_T_M, TW_RDEP_DIFF, FO_SDEP_L_M, FO_SDEP_T_M, FO_SDEP_DIFF, FO_RDEP_L_M, FO_RDEP_T_M, FO_RDEP_DIFF, SUM_DEP_L_M, SUM_DEP_T_M, SUM_DEP_DIFF)) ");
		sb.append("    ) BASE ");
		sb.append("    LEFT JOIN TBCRM_CUST_MAST CM ON BASE.CUST_ID = CM.CUST_ID ");
		sb.append("    WHERE 1 = 1 ");
		sb.append("    AND BASE.COL_TYPE = 'DIFF' ");
		sb.append("    AND BASE.YYYYMM = :YYYYMM ");
		sb.append("    AND BASE.AO_CODE = :AO_CODE ");
		sb.append("    AND BASE.DEP_TYPE = :DEP_TYPE ");
		sb.append("    AND BASE.AUM <> 0 ");
		sb.append("    AND BASE.AUM > 0 ");
		sb.append("    ORDER BY AUM DESC ");
		sb.append("    FETCH FIRST 30 ROWS ONLY ");
		sb.append("  ) ");
		queryCondition.setObject("YYYYMM", inputVO.getYYYYMM());
		queryCondition.setObject("AO_CODE", inputVO.getAO_CODE());
		queryCondition.setObject("DEP_TYPE", inputVO.getDEP_TYPE());

		queryCondition.setQueryString(sb.toString());

		outputVO.setDtl_1_List(dam.exeQuery(queryCondition));
		
		// 減少排行榜
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("  SELECT CUST_ID, CUST_NAME, AUM * 1000000 AS AUM ");
		sb.append("  FROM ( ");
		sb.append("    SELECT BASE.CUST_ID, CM.CUST_NAME, BASE.AUM ");
		sb.append("    FROM ( ");
		sb.append("      SELECT YYYYMM, BRANCH_NBR, CUST_ID, ");
		sb.append("             CASE WHEN AO_CODE IS NULL THEN 'MASS' ELSE AO_CODE END AS AO_CODE, ");
		sb.append("             SUBSTR(AUM_TYPE, 0, INSTR(AUM_TYPE, '_', 1, 2) - 1) AS DEP_TYPE, ");
		sb.append("             SUBSTR(AUM_TYPE, INSTR(AUM_TYPE, '_', 1, 2) + 1, LENGTH(AUM_TYPE)) AS COL_TYPE, ");
		sb.append("             AUM ");
		sb.append("      FROM ( ");
		sb.append("        SELECT * ");
		sb.append("        FROM TBPMS_CUST_DEP_AUM_CUST ");
		sb.append("        WHERE (TW_SDEP_DIFF <> 0 OR TW_RDEP_DIFF <> 0 OR FO_SDEP_DIFF <> 0 OR FO_RDEP_DIFF <> 0 OR SUM_DEP_DIFF <> 0) ");
		sb.append("        AND YYYYMM = :YYYYMM ");
		sb.append("      ) ");
		sb.append("      UNPIVOT(AUM FOR AUM_TYPE IN(TW_SDEP_L_M, TW_SDEP_T_M, TW_SDEP_DIFF, TW_RDEP_L_M, TW_RDEP_T_M, TW_RDEP_DIFF, FO_SDEP_L_M, FO_SDEP_T_M, FO_SDEP_DIFF, FO_RDEP_L_M, FO_RDEP_T_M, FO_RDEP_DIFF, SUM_DEP_L_M, SUM_DEP_T_M, SUM_DEP_DIFF)) ");
		sb.append("    ) BASE ");
		sb.append("    LEFT JOIN TBCRM_CUST_MAST CM ON BASE.CUST_ID = CM.CUST_ID ");
		sb.append("    WHERE 1 = 1 ");
		sb.append("    AND BASE.COL_TYPE = 'DIFF' ");
		sb.append("    AND BASE.YYYYMM = :YYYYMM ");
		sb.append("    AND BASE.AO_CODE = :AO_CODE ");
		sb.append("    AND BASE.DEP_TYPE = :DEP_TYPE ");
		sb.append("    AND BASE.AUM <> 0 ");
		sb.append("    AND BASE.AUM < 0 ");
		sb.append("    ORDER BY AUM ASC ");
		sb.append("    FETCH FIRST 30 ROWS ONLY ");
		sb.append("  ) ");
		
		queryCondition.setObject("YYYYMM", inputVO.getYYYYMM());
		queryCondition.setObject("AO_CODE", inputVO.getAO_CODE());
		queryCondition.setObject("DEP_TYPE", inputVO.getDEP_TYPE());

		queryCondition.setQueryString(sb.toString());

		outputVO.setDtl_2_List(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public StringBuffer returnAppendByExport (String genType, String sqlType) {
		
		StringBuffer sb = new StringBuffer();
		
		switch (genType) {
			case "BASE":
				sb.append("       YYYYMM").append(", ");
				sb.append("       TO_CHAR(CREATETIME, 'YYYY/MM/DD') AS DATA_DATE").append(", ");
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR").append(", ");
				sb.append("       BRANCH_NAME").append(", ");
				sb.append("       EMP_TYPE").append(", ");
				
				break;
			case "ALL":
				sb.append("       'ALL_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       '全行 合計' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM, "); 
				sb.append("       DATA_DATE, "); 
				sb.append("       EMP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_TYPE": "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");

				break;
			case "REGION":
				sb.append("       REGION_CENTER_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_ID" : "").append(", ");
				sb.append("       REGION_CENTER_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS REGION_CENTER_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM, ");  
				sb.append("       DATA_DATE, "); 
				sb.append("       EMP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_TYPE": "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");

				break;
			case "AREA":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_ID" : "").append(", ");
				sb.append("       BRANCH_AREA_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_AREA_NAME" : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR " : "").append(", "); 
				sb.append("       NULL ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME " : "").append(", "); 
				sb.append("       YYYYMM, "); 
				sb.append("       DATA_DATE, "); 
				sb.append("       EMP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_TYPE": "").append(StringUtils.equals("SELECT", sqlType) ? ", " : " ");
				
				break;
			case "BRANCH":
				sb.append("       REGION_CENTER_ID").append(", ");
				sb.append("       REGION_CENTER_NAME").append(", ");
				sb.append("       BRANCH_AREA_ID").append(", ");
				sb.append("       BRANCH_AREA_NAME").append(", ");
				sb.append("       BRANCH_NBR || '_TOTAL' ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NBR" : "").append(", ");
				sb.append("       BRANCH_NAME ").append(StringUtils.equals("SELECT", sqlType) ? "AS BRANCH_NAME" : "").append(", "); 
				sb.append("       YYYYMM ").append(StringUtils.equals("SELECT", sqlType) ? "AS YYYYMM" : "").append(", "); 
				sb.append("       DATA_DATE ").append(StringUtils.equals("SELECT", sqlType) ? "AS DATA_DATE" : "").append(", "); 
				sb.append("       EMP_TYPE ").append(StringUtils.equals("SELECT", sqlType) ? "AS EMP_TYPE," : "").append(" "); 
				break;
			case "ORDER" :
				sb.append("ORDER BY EMP_TYPE, ");
				sb.append("         DECODE(REGION_CENTER_ID, '000', 999, 0), ");
				sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
				sb.append("         REGION_CENTER_NAME, ");
				sb.append("         DECODE(BRANCH_AREA_ID, NULL, 999, 0), ");
				sb.append("         BRANCH_AREA_NAME, ");
				sb.append("         DECODE(BRANCH_NBR, NULL, 999, 0), ");
				sb.append("         BRANCH_NAME, ");  
				sb.append("         YYYYMM ");
				
				break;
		}
		
		return sb;
	}
	
	public StringBuffer genSQLByExport (String genType, PMS366InputVO inputVO, boolean isFC, boolean isHeadMGR, String NOT_EXIST_BS, String NOT_EXIST_UHRM, String MEM_LOGIN_FLAG) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(returnAppendByExport(genType, "SELECT"));
		
		switch (genType) {
			case "BASE":
				sb.append("       TW_SDEP_L_M,  ");
				sb.append("       TW_SDEP_T_M,  ");
				sb.append("       TW_SDEP_DIFF, ");
				sb.append("       TW_RDEP_L_M,  ");
				sb.append("       TW_RDEP_T_M,  ");
				sb.append("       TW_RDEP_DIFF, ");
				sb.append("       FO_SDEP_L_M,  ");
				sb.append("       FO_SDEP_T_M,  ");
				sb.append("       FO_SDEP_DIFF, ");
				sb.append("       FO_RDEP_L_M,  ");
				sb.append("       FO_RDEP_T_M,  ");
				sb.append("       FO_RDEP_DIFF, ");
				sb.append("       SUM_DEP_L_M,  ");
				sb.append("       SUM_DEP_T_M,  ");
				sb.append("       SUM_DEP_DIFF  ");
				break;
			case "BRANCH":
			case "AREA":
			case "REGION":
			case "ALL":
				sb.append("       SUM(TW_SDEP_L_M)  AS TW_SDEP_L_M,  ");
				sb.append("       SUM(TW_SDEP_T_M)  AS TW_SDEP_T_M,  ");
				sb.append("       SUM(TW_SDEP_DIFF) AS TW_SDEP_DIFF, ");
				sb.append("       SUM(TW_RDEP_L_M)  AS TW_RDEP_L_M,  ");
				sb.append("       SUM(TW_RDEP_T_M)  AS TW_RDEP_T_M,  ");
				sb.append("       SUM(TW_RDEP_DIFF) AS TW_RDEP_DIFF, ");
				sb.append("       SUM(FO_SDEP_L_M)  AS FO_SDEP_L_M,  ");
				sb.append("       SUM(FO_SDEP_T_M)  AS FO_SDEP_T_M,  ");
				sb.append("       SUM(FO_SDEP_DIFF) AS FO_SDEP_DIFF, ");
				sb.append("       SUM(FO_RDEP_L_M)  AS FO_RDEP_L_M,  ");
				sb.append("       SUM(FO_RDEP_T_M)  AS FO_RDEP_T_M,  ");
				sb.append("       SUM(FO_RDEP_DIFF) AS FO_RDEP_DIFF, ");
				sb.append("       SUM(SUM_DEP_L_M)  AS SUM_DEP_L_M,  ");
				sb.append("       SUM(SUM_DEP_T_M)  AS SUM_DEP_T_M,  ");
				sb.append("       SUM(SUM_DEP_DIFF) AS SUM_DEP_DIFF  ");
				break;
		}
		
		switch (genType) {
			case "BASE":
				sb.append("FROM TBPMS_CUST_DEP_AUM_BRH ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND YYYYMM = :YYYYMM ");

				break;
			case "BRANCH":
			case "AREA":
			case "REGION":
			case "ALL":
				sb.append("FROM BASE ");
				sb.append("WHERE 1 = 1 ");

				if (StringUtils.lowerCase(MEM_LOGIN_FLAG).indexOf("uhrm") >= 0 &&
					!StringUtils.equals(MEM_LOGIN_FLAG, "uhrm")) {
					sb.append("AND EMP_TYPE = 'UHRM' ");
				} else {
				}

				if (StringUtils.equals("Y", NOT_EXIST_UHRM)) {
					sb.append("AND EMP_TYPE = 'BRH' ");
				}
				
				break;
		}
		
		// ==主查詢條件==
		// 日期
		if (!StringUtils.isBlank(inputVO.getsCreDate()) && !inputVO.getsCreDate().toString().equals("0")) {
			sb.append("AND YYYYMM = :YYYYMM ");
		}
		
		switch (genType) {
			case "BRANCH":
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sb.append("AND BRANCH_NBR = :branch_nbr ");
				} else if (!isHeadMGR) {
					// 登入非總行人員強制加分行
					if (StringUtils.lowerCase(MEM_LOGIN_FLAG).indexOf("uhrm") >= 0 &&
						!StringUtils.equals(MEM_LOGIN_FLAG, "uhrm")) {
					} else {
						sb.append("AND BRANCH_NBR IN (:branch_nbr) ");
					}
				}
				
			case "AREA":
				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
					sb.append("AND BRANCH_AREA_ID = :branch_area_id ");
				} else if (!isHeadMGR) {
					// 登入非總行人員強制加營運區
					if (StringUtils.lowerCase(MEM_LOGIN_FLAG).indexOf("uhrm") >= 0 &&
							!StringUtils.equals(MEM_LOGIN_FLAG, "uhrm")) {
						} else {
							sb.append("AND BRANCH_AREA_ID IN (:branch_area_id) ");
						}
				}
				
			case "REGION":
				// 區域中心
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
					sb.append("AND REGION_CENTER_ID = :region_center_id ");
				} else if (!isHeadMGR) {
					// 登入非總行人員強制加區域中心
					if (StringUtils.lowerCase(MEM_LOGIN_FLAG).indexOf("uhrm") >= 0 &&
						!StringUtils.equals(MEM_LOGIN_FLAG, "uhrm")) {
					} else {
						sb.append("AND REGION_CENTER_ID IN (:region_center_id) ");
					}
				}
		}
		
		if (!StringUtils.equals(genType, "BASE")) {
			sb.append("GROUP BY ");
			sb.append(returnAppendByExport(genType, "GROUP") + " ");
		}
		
		return sb;
	}

	/** 匯出 **/
	public void export(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		String fileName = "個金存款AUM異動日報_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + (String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		
		PMS366InputVO inputVO = (PMS366InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		XmlInfo xmlInfo = new XmlInfo();
		boolean isHeadMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		//=== 取得查詢資料可視範圍 START ===
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		//=== 取得查詢資料可視範圍 END ===
		
		//=== TEAM SQL START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		//=== MAIN SQL START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM ( ").append(genSQLByExport("BASE", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")).append("  ) "); 
		sb.append(") ");
		
		sb.append("SELECT * ");
		sb.append("FROM ( "); 
		
		// 分行合計
		sb.append(genSQLByExport("BRANCH", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 營運區合計
		sb.append(genSQLByExport("AREA", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 
		
		sb.append("UNION "); 
		
		// 業務處合計
		sb.append(genSQLByExport("REGION", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 

		sb.append("UNION "); 
		
		// 全行合行
		sb.append(genSQLByExport("ALL", inputVO, isFC, isHeadMGR, inputVO.getNOT_EXIST_BS(), inputVO.getNOT_EXIST_UHRM(), (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).toString().replaceAll("\\s+", " ")); 

		sb.append(") "); 
		
		sb.append(returnAppendByExport("ORDER", "").toString().replaceAll("\\s+", " "));
		
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
		
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		} else if (!isHeadMGR) {
			//登入非總行人員強制加分行
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				!StringUtils.equals((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), "uhrm")) {
			} else {
				queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
			}
		}
		
		// 營運區
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		} else if (!isHeadMGR) {
			//登入非總行人員強制加營運區
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				!StringUtils.equals((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), "uhrm")) {
			} else {
				queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
			}
		}
		
		// 區域中心
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		} else if (!isHeadMGR) {
			//登入非總行人員強制加區域中心
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				!StringUtils.equals((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG), "uhrm")) {
			} else {
				queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> csvList = dam.exeQuery(queryCondition);
		
		//=== MAIN SQL END ===
		
		List listCSV = new ArrayList();
		
		String[] csvHeader = {"資料年月", "資料更新日期", "業務處", "區別", "分行", "單位",
				  			  inputVO.getsCreDate() + "台活(百萬)", "上月底台活(百萬)", "差異數(百萬)",
				  			  inputVO.getsCreDate() + "台定(百萬)", "上月底台定(百萬)", "差異數(百萬)",
				  			  inputVO.getsCreDate() + "外活(百萬)", "上月底外活(百萬)", "差異數(百萬)",
				  			  inputVO.getsCreDate() + "外定(百萬)", "上月底外定(百萬)", "差異數(百萬)",
				  			  inputVO.getsCreDate() + "台外幣合計(百萬)", "上月底台外幣合計(百萬)", "差異數(百萬)"};

		String[] csvMain = {  "YYYYMM", "DATA_DATE", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "EMP_TYPE",
							  "TW_SDEP_T_M", "TW_SDEP_L_M", "TW_SDEP_DIFF",
							  "TW_RDEP_T_M", "TW_RDEP_L_M", "TW_RDEP_DIFF",
							  "FO_SDEP_T_M", "FO_SDEP_L_M", "FO_SDEP_DIFF",
							  "FO_RDEP_T_M", "FO_RDEP_L_M", "FO_RDEP_DIFF",
							  "SUM_DEP_T_M", "SUM_DEP_L_M", "SUM_DEP_DIFF"};
		
		for (Map<String, Object> map : csvList) {
			String[] records = new String[csvMain.length];

			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
					case "YYYYMM":
					case "DATA_DATE":
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
					case "EMP_TYPE":
						records[i] = (StringUtils.equals("BRH", checkIsNull(map, csvMain[i])) ? "分行" : "私銀");
						break;
					case "TW_SDEP_T_M": 
					case "TW_SDEP_L_M": 
					case "TW_SDEP_DIFF":
					case "TW_RDEP_T_M": 
					case "TW_RDEP_L_M": 
					case "TW_RDEP_DIFF":
					case "FO_SDEP_T_M": 
					case "FO_SDEP_L_M": 
					case "FO_SDEP_DIFF":
					case "FO_RDEP_T_M": 
					case "FO_RDEP_L_M":
					case "FO_RDEP_DIFF":
					case "SUM_DEP_T_M": 
					case "SUM_DEP_L_M": 
					case "SUM_DEP_DIFF":
//						BigDecimal defAum = new BigDecimal(checkIsNull(map, csvMain[i]));
//						records[i] = new DecimalFormat("#,##0").format();
						records[i] = checkIsNull(map, csvMain[i]);
						
						break;
					default:
						records[i] = checkIsNull(map, csvMain[i]);
						
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
