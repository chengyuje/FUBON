package com.systex.jbranch.app.server.fps.org450;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org450")
@Scope("request")
public class ORG450 extends FubonWmsBizLogic {

	public DataAccessManager dam = null;

	SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	/**
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		ORG450InputVO inputVO = (ORG450InputVO) body;
		ORG450OutputVO outputVO = new ORG450OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// ===取得理專員額表
		StringBuffer sb = new StringBuffer();
		sb.append("WITH DTL AS ( ");
		sb.append("  SELECT CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, ROLE_NAME, ");
		sb.append("         SUM(SERVING_COUNT) AS SERVING_COUNT, SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("         NVL(MQUOTA.FC_COUNT, 0) AS FC_COUNT, ");
		sb.append("         (NVL(MQUOTA.FC_COUNT, 0) - NVL(SUM(BASE.SERVING_COUNT), 0)) AS SHORTFALL, ");
		sb.append("         NVL(ING_STATUS.WAIT_IN, 0) AS WAIT_IN, ");
		sb.append("         NVL(ING_STATUS.CHK_PRICE_ING, 0) AS CHK_PRICE_ING, ");
		sb.append("         NVL(ING_STATUS.CHK_WAIT_MEET, 0) AS CHK_WAIT_MEET, ");
		sb.append("         NVL(CHK_PIP_REPORT, 0) AS CHK_PIP_REPORT, PS_REMARK ");
		sb.append("  FROM ( ");
		sb.append("    SELECT CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, ROLEID, ROLE_NAME, ");
		sb.append("           NVL(NOW_STATUS.SERVING, 0) AS SERVING_COUNT, ");
		sb.append("           NVL(NOW_STATUS.LEAVING, 0) AS LEAVING_COUNT, ");
		sb.append("           (SELECT Q.PS_CNT_REMARK FROM TBORG_BRH_MBR_QUOTA Q WHERE Q.DEPT_ID = BRANCH_ID) AS PS_REMARK ");
		sb.append("    FROM ( ");
		sb.append("      SELECT REGION_CENTER_ID AS CENTER_ID, REGION_CENTER_NAME AS CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR AS BRANCH_ID, BRANCH_NAME, SYS_ROL.ROLEID, FUBON_ROL.ROLE_NAME ");
		sb.append("      FROM TBSYSSECUROLPRIASS SYS_ROL ");
		sb.append("      LEFT JOIN TBORG_ROLE FUBON_ROL ON SYS_ROL.ROLEID = FUBON_ROL.ROLE_ID ");
		sb.append("      LEFT JOIN VWORG_DEFN_INFO DEPT_DTL ON 1=1 ");
		sb.append("      WHERE PRIVILEGEID in  ('004') ");
		sb.append("      ORDER BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, ROLE_NAME ");
		sb.append("    ) BASE ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT DEPT_ID, ROLE_ID, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING ");
		sb.append("      FROM ( ");
		sb.append("        SELECT MEM.DEPT_ID, MEM.EMP_ID, MROLE.ROLE_ID, ");
		sb.append("               CASE WHEN (MEM.JOB_RESIGN_DATE IS NULL OR TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') > TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS SERVING, "); // --至系統日止仍在職(不含當日離職)
		sb.append("               CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS LEAVING "); // --至系統日止預計離職(含當日離職)
		sb.append("        FROM TBORG_MEMBER MEM, TBORG_MEMBER_ROLE MROLE ");
		sb.append("        WHERE MEM.DEPT_ID IS NOT NULL ");
		sb.append("        AND MEM.EMP_ID = MROLE.EMP_ID ");
		sb.append("        AND MROLE.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("        AND MROLE.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '004') ");
		sb.append("        AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("        AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("      ) ARRANGE ");
		sb.append("      GROUP BY DEPT_ID, ROLE_ID ");
		sb.append("    ) NOW_STATUS ON BASE.BRANCH_ID = NOW_STATUS.DEPT_ID AND BASE.ROLEID = NOW_STATUS.ROLE_ID ");
		sb.append("  ) BASE ");
		sb.append("    LEFT JOIN ( ");
		sb.append("    SELECT DEPT_ID, ");
		sb.append("           CASE WHEN REPLACE(ROLE_TYPE, '_CNT') = 'OP' THEN '作業人員' ");
		sb.append("                WHEN REPLACE(ROLE_TYPE, '_CNT') = 'OPH' THEN '作業主管' ");
		sb.append("                WHEN REPLACE(ROLE_TYPE, '_CNT') = 'AO' THEN '新興AO' ");
		sb.append("                WHEN REPLACE(ROLE_TYPE, '_CNT') = 'CAO' THEN '消金AO' ");
		sb.append("           ELSE REPLACE(ROLE_TYPE, '_CNT') END AS ROLE_TYPE, ");
		sb.append("           FC_COUNT ");
		sb.append("    FROM TBORG_BRH_MBR_QUOTA ");
		sb.append("    UNPIVOT (FC_COUNT FOR ROLE_TYPE IN (FC1_CNT, FC2_CNT, FC3_CNT, FC4_CNT, FC5_CNT, FCH_CNT, OP_CNT, OPH_CNT, PS_CNT, AO_CNT, CAO_CNT)) ");
		sb.append("  ) MQUOTA ON SUBSTR(BASE.ROLE_NAME, 3, 2) = MQUOTA.ROLE_TYPE AND MQUOTA.DEPT_ID = BASE.BRANCH_ID ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT BRANCH_NBR, AO_JOB_RANK, SUM(WAIT_IN) AS WAIT_IN, SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT ");
		sb.append("    FROM ( ");
		sb.append("      SELECT CUST_ID, BRANCH_NBR, AO_JOB_RANK, ");
		sb.append("             CASE WHEN STATUS = '07' THEN 1 ELSE 0 END AS WAIT_IN, ");
		sb.append("             CASE WHEN (STATUS = '07' AND TO_CHAR(BOOKED_ONBOARD_DATE, 'yyyyMM') = TO_CHAR(SYSDATE, 'yyyyMM') AND TO_CHAR(BOOKED_ONBOARD_DATE, 'yyyyMMdd') > TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS MON1_WAIT_IN, ");
		sb.append("             CASE WHEN (STATUS = '07' AND TO_CHAR(BOOKED_ONBOARD_DATE, 'yyyyMM') = TO_CHAR(ADD_MONTHS(SYSDATE, 1), 'yyyyMM')) THEN 1 ELSE 0 END AS MON2_WAIT_IN, ");
		sb.append("             CASE WHEN (STATUS = '07' AND TO_CHAR(BOOKED_ONBOARD_DATE, 'yyyyMM') = TO_CHAR(ADD_MONTHS(SYSDATE, 2), 'yyyyMM')) THEN 1 ELSE 0 END AS MON3_WAIT_IN, ");
		sb.append("             CASE WHEN STATUS IN ('04', '05', '06') THEN 1 ELSE 0 END AS CHK_PRICE_ING, ");
		sb.append("             CASE WHEN STATUS IN ('01', '02', '03', '11') THEN 1 ELSE 0 END AS CHK_WAIT_MEET, ");
		sb.append("             CASE WHEN STATUS IN ('01', '02', '03', '11') THEN 1 ELSE 0 END AS CHK_PIP_REPORT ");
		sb.append("      FROM TBORG_EMP_HIRE_INFO ");
		sb.append("      WHERE AO_JOB_RANK IN (SELECT ROLE_NAME FROM TBSYSSECUROLPRIASS PRIASS, TBORG_ROLE ORG_ROLE WHERE PRIASS.ROLEID = ORG_ROLE.ROLE_ID AND PRIVILEGEID in ('004')) ");
		sb.append("    ) HIRE_INFO ");
		sb.append("    GROUP BY BRANCH_NBR, AO_JOB_RANK ");
		sb.append("  ) ING_STATUS ON BASE.BRANCH_ID = ING_STATUS.BRANCH_NBR AND BASE.ROLE_NAME = ING_STATUS.AO_JOB_RANK ");
		sb.append("  GROUP BY BASE.CENTER_ID, BASE.CENTER_NAME, BASE.BRANCH_AREA_ID, BASE.BRANCH_AREA_NAME, BASE.BRANCH_ID, BASE.BRANCH_NAME, BASE.ROLE_NAME,BASE.PS_REMARK, ");
		sb.append("           MQUOTA.FC_COUNT, MQUOTA.FC_COUNT, ING_STATUS.WAIT_IN, ING_STATUS.CHK_PRICE_ING, ING_STATUS.CHK_WAIT_MEET, ING_STATUS.CHK_PIP_REPORT ");
		sb.append(") ");

		// 業務處合計
		sb.append("SELECT CENTER_ID, CENTER_NAME || ' 合計' AS CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_ID, '' AS BRANCH_NAME, ROLE_NAME, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, SUM(FC_COUNT) AS FC_COUNT, SUM(LEAVING_COUNT) AS LEAVING_COUNT, SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, '' AS PS_REMARK ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); // 區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}

		sb.append("GROUP BY CENTER_ID, CENTER_NAME || ' 合計', ROLE_NAME ");
		sb.append("UNION ");
		// 營運中心合計
		sb.append("SELECT CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計' AS BRANCH_AREA_NAME, '' AS BRANCH_ID, '' AS BRANCH_NAME, ROLE_NAME, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, SUM(FC_COUNT) AS FC_COUNT, SUM(LEAVING_COUNT) AS LEAVING_COUNT, SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, '' AS PS_REMARK ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); // 區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND DTL.BRANCH_AREA_ID = :branchAreaID "); // 營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sb.append("AND DTL.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}

		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計', ROLE_NAME ");
		sb.append("UNION ");
		// 分行小計 modify by ocean : 20181107 宥宜要求隱藏
		sb.append("SELECT CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, ROLE_NAME, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, SUM(FC_COUNT) AS FC_COUNT, SUM(LEAVING_COUNT) AS LEAVING_COUNT, SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, PS_REMARK ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); // 區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND DTL.BRANCH_AREA_ID = :branchAreaID "); // 營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sb.append("AND DTL.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND DTL.BRANCH_ID = :branchID "); // 分行代碼
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		} else {
			sb.append("AND DTL.BRANCH_ID IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, ROLE_NAME, PS_REMARK ");
		sb.append("UNION ");
		// 全行合計
		sb.append("SELECT '' as CENTER_ID, '全行 合計' AS CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_ID, '' AS BRANCH_NAME, ROLE_NAME, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, SUM(FC_COUNT) AS FC_COUNT, SUM(LEAVING_COUNT) AS LEAVING_COUNT, SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, '' AS PS_REMARK ");
		sb.append("FROM DTL GROUP BY '', '全行 合計', ROLE_NAME ");

		sb.append("ORDER BY CENTER_ID ASC, BRANCH_AREA_ID ASC, BRANCH_ID ASC, ROLE_NAME ASC ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

		ArrayList<String> centerTempList = new ArrayList<String>(); // 比對用
		for (Map<String, Object> centerMap : list) {
			String regionCenter = (String) centerMap.get("CENTER_NAME");
			if (centerTempList.indexOf(regionCenter) < 0) { // regionCenter
				centerTempList.add(regionCenter);

				Integer centerRowspan = 1;

				List<Map<String, Object>> branchAreaList = new ArrayList<Map<String, Object>>();
				ArrayList<String> branchAreaTempList = new ArrayList<String>(); // 比對用
				for (Map<String, Object> branchAreaMap : list) {
					String branchArea = (String) branchAreaMap.get("BRANCH_AREA_NAME");

					Integer branchAreaRowspan = 1;

					// ==== 營運區
					if (regionCenter.equals((String) branchAreaMap.get("CENTER_NAME"))) {
						if (branchAreaTempList.indexOf(branchArea) < 0) { // branchArea
							branchAreaTempList.add(branchArea);

							// ==== 分行
							List<Map<String, Object>> branchList = new ArrayList<Map<String, Object>>();
							ArrayList<String> branchTempList = new ArrayList<String>(); // 比對用
							ArrayList<String> centerCountTempList = new ArrayList<String>(); // 比對用
							for (Map<String, Object> branchMap : list) {
								String branch = (String) branchMap.get("BRANCH_NAME");
								BigDecimal servingSum = new BigDecimal(0);
								BigDecimal fcSum = new BigDecimal(0);
								BigDecimal shotfallSum = new BigDecimal(0);
								BigDecimal waitInSum = new BigDecimal(0);
								BigDecimal chkPriceIngSum = new BigDecimal(0);
								BigDecimal chkWaitMeetSum = new BigDecimal(0);
								if (branchArea != null && branchMap.get("BRANCH_AREA_NAME") != null) {
									if (branchArea.equals((String) branchMap.get("BRANCH_AREA_NAME"))) {
										if (branchTempList.indexOf(branch) < 0) { // branchArea
											branchTempList.add(branch);

											// ==== 詳細資訊
											List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
											ArrayList<String> roleTempList = new ArrayList<String>(); // 比對用
											for (Map<String, Object> roleMap : list) {
												String role = (String) roleMap.get("ROLE_NAME");

												if (branch != null && roleMap.get("BRANCH_NAME") != null) {
													if (branch.equals((String) roleMap.get("BRANCH_NAME"))) {
														if (roleTempList.indexOf(role) < 0) { // branchArea
															roleTempList.add(role);

															Map<String, Object> roleTempMap = new HashMap<String, Object>();
															roleTempMap.put("ROLE_NAME", role);
															roleTempMap.put("SERVING_COUNT", (BigDecimal) roleMap.get("SERVING_COUNT"));
															roleTempMap.put("FC_COUNT", (BigDecimal) roleMap.get("FC_COUNT"));
															roleTempMap.put("LEAVING_COUNT", (BigDecimal) roleMap.get("LEAVING_COUNT"));
															roleTempMap.put("SHORTFALL", (BigDecimal) roleMap.get("SHORTFALL"));
															roleTempMap.put("WAIT_IN", (BigDecimal) roleMap.get("WAIT_IN"));
															roleTempMap.put("CHK_PRICE_ING", (BigDecimal) roleMap.get("CHK_PRICE_ING"));
															roleTempMap.put("CHK_WAIT_MEET", (BigDecimal) roleMap.get("CHK_WAIT_MEET"));
															roleTempMap.put("CHK_PIP_REPORT", (BigDecimal) roleMap.get("CHK_PIP_REPORT"));
															roleTempMap.put("PS_REMARK", (String) roleMap.get("PS_REMARK"));

															roleList.add(roleTempMap);
															// Summary
															servingSum = servingSum.add((BigDecimal) roleMap.get("SERVING_COUNT"));
															fcSum = fcSum.add((BigDecimal) roleMap.get("FC_COUNT"));
															shotfallSum = shotfallSum.add((BigDecimal) roleMap.get("SHORTFALL"));
															waitInSum = waitInSum.add((BigDecimal) roleMap.get("WAIT_IN"));
															chkPriceIngSum = chkPriceIngSum.add((BigDecimal) roleMap.get("CHK_PRICE_ING"));
															chkWaitMeetSum = chkWaitMeetSum.add((BigDecimal) roleMap.get("CHK_WAIT_MEET"));
														}
													}
												} else if (branchArea.equals(roleMap.get("BRANCH_AREA_NAME"))) {
													Map<String, Object> roleTempMap = new HashMap<String, Object>();
													roleTempMap.put("ROLE_NAME", role);
													roleTempMap.put("SERVING_COUNT", (BigDecimal) roleMap.get("SERVING_COUNT"));
													roleTempMap.put("FC_COUNT", (BigDecimal) roleMap.get("FC_COUNT"));
													roleTempMap.put("LEAVING_COUNT", (BigDecimal) roleMap.get("LEAVING_COUNT"));
													roleTempMap.put("SHORTFALL", (BigDecimal) roleMap.get("SHORTFALL"));
													roleTempMap.put("WAIT_IN", (BigDecimal) roleMap.get("WAIT_IN"));
													roleTempMap.put("CHK_PRICE_ING", (BigDecimal) roleMap.get("CHK_PRICE_ING"));
													roleTempMap.put("CHK_WAIT_MEET", (BigDecimal) roleMap.get("CHK_WAIT_MEET"));
													roleTempMap.put("CHK_PIP_REPORT", (BigDecimal) roleMap.get("CHK_PIP_REPORT"));
													roleTempMap.put("PS_REMARK", (String) roleMap.get("PS_REMARK"));

													roleList.add(roleTempMap);
													// Summary
													// servingSum = servingSum.add((BigDecimal)
													// roleMap.get("SERVING_COUNT"));
													// fcSum = fcSum.add((BigDecimal) roleMap.get("FC_COUNT"));
													// shotfallSum = shotfallSum.add((BigDecimal)
													// roleMap.get("SHORTFALL"));
													// waitInSum = waitInSum.add((BigDecimal) roleMap.get("WAIT_IN"));
													// chkPriceIngSum = chkPriceIngSum.add((BigDecimal)
													// roleMap.get("CHK_PRICE_ING"));
													// chkWaitMeetSum = chkWaitMeetSum.add((BigDecimal)
													// roleMap.get("CHK_WAIT_MEET"));
												}
											}

											Map<String, Object> branchTempMap = new HashMap<String, Object>();
											branchTempMap.put("BRANCH_ID", (String) branchMap.get("BRANCH_ID"));
											branchTempMap.put("BRANCH_NAME", branch);
											branchTempMap.put("ROLE", roleList);
											if (branch != null) {
												centerRowspan = centerRowspan + roleList.size();
												branchAreaRowspan = branchAreaRowspan + roleList.size();
												branchTempMap.put("ROWSPAN", roleList.size());
											} else {
												centerRowspan = centerRowspan + roleList.size();
												branchAreaRowspan = branchAreaRowspan + roleList.size();
												branchTempMap.put("ROWSPAN", roleList.size());
											}
											// centerRowspan = centerRowspan + roleList.size();
											// branchAreaRowspan = branchAreaRowspan + roleList.size();
											// branchTempMap.put("ROWSPAN", roleList.size());
											
											// Summary
											// modify by ocean : 20181107 宥宜要求隱藏
//											if (branch != null) {
//												Map<String, Object> map = new HashMap<String, Object>();
//												map.put("ROLE_NAME", "小計");
//												map.put("SERVING_COUNT", servingSum);
//												map.put("FC_COUNT", fcSum);
//												map.put("SHORTFALL", shotfallSum);
//												map.put("WAIT_IN", waitInSum);
//												map.put("CHK_PRICE_ING", chkPriceIngSum);
//												map.put("CHK_WAIT_MEET", chkWaitMeetSum);
//												roleList.add(map);
//											}
											
											branchList.add(branchTempMap);
										}
									}
								} else if (regionCenter.equals(branchMap.get("CENTER_NAME"))) {
									String centerCount = (String) branchMap.get("CENTER_NAME");
									if (centerCountTempList.indexOf(centerCount) < 0) { // regionCenter

										centerCountTempList.add(centerCount);

										// ==== 詳細資訊
										List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
										ArrayList<String> roleTempList = new ArrayList<String>(); // 比對用
										for (Map<String, Object> roleMap : list) {
											String role = (String) roleMap.get("ROLE_NAME");

											if (regionCenter.equals(roleMap.get("CENTER_NAME"))) {
												if (roleTempList.indexOf(role) < 0) { // branchArea
													roleTempList.add(role);

													Map<String, Object> roleTempMap = new HashMap<String, Object>();
													roleTempMap.put("ROLE_NAME", role);
													roleTempMap.put("SERVING_COUNT", (BigDecimal) roleMap.get("SERVING_COUNT"));
													roleTempMap.put("FC_COUNT", (BigDecimal) roleMap.get("FC_COUNT"));
													roleTempMap.put("LEAVING_COUNT", (BigDecimal) roleMap.get("LEAVING_COUNT"));
													roleTempMap.put("SHORTFALL", (BigDecimal) roleMap.get("SHORTFALL"));
													roleTempMap.put("WAIT_IN", (BigDecimal) roleMap.get("WAIT_IN"));
													roleTempMap.put("CHK_PRICE_ING", (BigDecimal) roleMap.get("CHK_PRICE_ING"));
													roleTempMap.put("CHK_WAIT_MEET", (BigDecimal) roleMap.get("CHK_WAIT_MEET"));
													roleTempMap.put("CHK_PIP_REPORT", (BigDecimal) roleMap.get("CHK_PIP_REPORT"));
													roleTempMap.put("PS_REMARK", (BigDecimal) roleMap.get("PS_REMARK"));

													roleList.add(roleTempMap);
												}
											}
										}

										Map<String, Object> branchTempMap = new HashMap<String, Object>();
										branchTempMap.put("BRANCH_ID", (String) branchMap.get("BRANCH_ID"));
										branchTempMap.put("BRANCH_NAME", branch);
										branchTempMap.put("ROLE", roleList);
										if (branch != null) {
											centerRowspan = centerRowspan + roleList.size() + 1;
											branchAreaRowspan = branchAreaRowspan + roleList.size() + 1;
											branchTempMap.put("ROWSPAN", roleList.size() + 1);
										} else {
											centerRowspan = centerRowspan + roleList.size();
											branchAreaRowspan = branchAreaRowspan + roleList.size();
											branchTempMap.put("ROWSPAN", roleList.size());
										}

										// Summary
										// modify by ocean : 20181107 宥宜要求隱藏
										if (branch != null) {
											Map<String, Object> map = new HashMap<String, Object>();
											map.put("ROLE_NAME", "小計");
											map.put("SERVING_COUNT", servingSum);
											map.put("FC_COUNT", fcSum);
											map.put("SHORTFALL", shotfallSum);
											map.put("WAIT_IN", waitInSum);
											map.put("CHK_PRICE_ING", chkPriceIngSum);
											map.put("CHK_WAIT_MEET", chkWaitMeetSum);
											roleList.add(map);
										}
										
										branchList.add(branchTempMap);
									}
								}
							}

							Map<String, Object> branchAreaTempMap = new HashMap<String, Object>();
							branchAreaTempMap.put("BRANCH_AREA_NAME", branchArea);
							branchAreaTempMap.put("BRANCH", branchList);
							centerRowspan = centerRowspan + branchList.size();
							branchAreaRowspan = branchAreaRowspan + branchList.size();
							branchAreaTempMap.put("ROWSPAN", branchAreaRowspan);
							branchAreaTempMap.put("COLSPAN", (branchList.size() == 1 && branchList.get(0).get("BRANCH_NAME") == null ? 3 : 1));

							branchAreaList.add(branchAreaTempMap);
						}
					}
				}

				Map<String, Object> centerTempMap = new HashMap<String, Object>();
				centerTempMap.put("CENTER_NAME", regionCenter);
				centerTempMap.put("BRANCH_AREA", branchAreaList);
				centerRowspan = centerRowspan + branchAreaList.size();
				centerTempMap.put("ROWSPAN", centerRowspan);
				centerTempMap.put("COLSPAN", (branchAreaList.size() == 1 && branchAreaList.get(0).get("BRANCH_AREA_NAME") == null ? 4 : 1));

				returnList.add(centerTempMap);
			}
		}

		outputVO.setAoCntLst(returnList);
		outputVO.setReportLst(list);

		Calendar calender = Calendar.getInstance();
		outputVO.setYear(sdfYYYY.format(new Date()));
		outputVO.setToDay(sdfYYYYMMDD.format(new Date()));

		sendRtnObject(outputVO);
	}

	public void export(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		ORG450InputVO inputVO = (ORG450InputVO) body;
		ORG450OutputVO outputVO = new ORG450OutputVO();

		String[] headerLine1 = { "業務處", "營運區", "分行別", "分別名稱", "職務", inputVO.getYear() + "應有員額",
				inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額", "在途", "在途", "在途",
				"備註" };
		String[] headerLine2 = { "", "", "", "", "", "截至" + inputVO.getToDay() + "實際數", "員額", "預計離職人數", "缺額", "待報到總數",
				"核薪中", "待面試", "人員進用pipline report", "" };

		String[] mainLine = { "CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_ID", "BRANCH_NAME", "ROLE_NAME",
				"SERVING_COUNT", "FC_COUNT", "LEAVING_COUNT", "SHORTFALL", "WAIT_IN", "CHK_PRICE_ING", "CHK_WAIT_MEET",
				"CHK_PIP_REPORT", "PS_REMARK" };

		String fileName = "消金業務專員PS員額表_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("消金業務專員PS員額表_" + sdfYYYYMMDD.format(new Date()));
		sheet.setDefaultColumnWidth(20);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 水平置中
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); // 垂直置中

		Integer index = 0; // first row
		Integer startFlag = 0;
		Integer endFlag = 0;
		ArrayList<String> tempList = new ArrayList<String>(); // 比對用

		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine1.length; i++) {
			String headerLine = headerLine1[i];
			if (tempList.indexOf(headerLine) < 0) {
				tempList.add(headerLine);
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(headerLine1[i]);

				if (endFlag != 0) {
					sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow,
																							// firstColumn, endColumn
				}
				startFlag = i;
				endFlag = 0;
			} else {
				endFlag = i;
			}
		}
		if (endFlag != 0) { // 最後的CELL若需要合併儲存格，則在這裡做
			sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn,
																					// endColumn
		}

		index++; // next row
		row = sheet.createRow(index);
		for (int i = 0; i < headerLine2.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(headerLine2[i]);

			if ("".equals(headerLine2[i])) {
				sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
			}
		}

		index++;
		ArrayList<String> centerTempList = new ArrayList<String>(); // 比對用
		ArrayList<String> branchAreaTempList = new ArrayList<String>(); // 比對用
		ArrayList<String> branchTempList = new ArrayList<String>(); // 比對用
		Integer centerStartFlag = 0, branchAreaStartFlag = 0, branchStartFlag = 0;
		Integer centerEndFlag = 0, branchAreaEndFlag = 0, branchEndFlag = 0;

		Integer contectStartIndex = index;

		List<Map<String, String>> list = inputVO.getEXPORT_LST();
		// 20180919加入分行小計
//		 list = this.addSummary(list);

		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);

			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				String centerName = list.get(i).get("CENTER_NAME");
				String branchAreaName = list.get(i).get("BRANCH_AREA_NAME");
				String branchName = list.get(i).get("BRANCH_NAME");
				// 處
				if (StringUtils.isNotBlank(centerName) && j == 0 && centerTempList.indexOf(centerName) < 0) {
					if (centerName.indexOf("合計") < 0 )
						centerTempList.add(centerName);

					if (centerEndFlag != 0) {
						if (null != centerName && centerName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j)); // firstRow,endRow, firstColumn,endColumn
							sheet.addMergedRegion(new CellRangeAddress(i + 2, i + 2, j, j + 3)); // firstRow, endRow,firstColumn,endColumn
						}
					}

//LOCK					if (StringUtils.isBlank(centerName)) {
//LOCK						sheet.addMergedRegion(new CellRangeAddress(i + 2, i + 3, j, j + 3)); // firstRow, endRow,firstColumn,endColumn
//LOCK					}
					centerStartFlag = i;
					centerEndFlag = 0;
				} else if (j == 0 && null != list.get(i).get("CENTER_NAME")) {
					centerEndFlag = i;
				}
				
				// 區
				if (j == 1 && StringUtils.isNotBlank(branchAreaName) && branchAreaTempList.indexOf(branchAreaName) < 0) {
					branchAreaTempList.add(branchAreaName);

					if (branchAreaName.indexOf("合計") > 0) {
						sheet.addMergedRegion(new CellRangeAddress(i + 2, i + 2, j, j + 2)); // firstRow, endRow,firstColumn,endColumn

						if (branchAreaEndFlag != 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn,endColumn
						}
					}

					branchAreaStartFlag = i;
					branchAreaEndFlag = 0;
				} else if (j == 1 && StringUtils.isNotBlank(branchAreaName)) {
					branchAreaEndFlag = i;
				}
				
				// 分行
				if (j == 2 && branchTempList.indexOf(branchName) < 0) {
					branchTempList.add(branchName);

					if (branchEndFlag != 0) {
//						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j)); // firstRow,endRow, firstColumn, endColumn
//						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j + 1, j + 1)); // firstRow, endRow, firstColumn,endColumn
					}
					
					branchStartFlag = i;
					branchEndFlag = 0;
				} else if (j == 3 && null != list.get(i).get("BRANCH_NAME")) {
					branchEndFlag = i;
				}

				//依以上邏輯合併會忽略最後一筆分行資料，故加此段
				if (j == 2 && i == (list.size() - 3)) { //分行 最後一筆 9:倒數第9筆
					if (branchEndFlag != 0) {
//LOCK						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j)); // firstRow,endRow, firstColumn, endColumn
//LOCK						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j + 1, j + 1)); // firstRow, endRow, firstColumn,endColumn
					}
				} else if (j == 1 && i == (list.size() - 1)) {
					if (branchAreaEndFlag != 0) {
//LOCK						sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j + 3, j + 5)); // firstRow, endRow, firstColumn,endColumn
					}
				}

				//全行
				if (StringUtils.isNotBlank(centerName) && j == 0 && centerName.indexOf("全行") >= 0) {
					if (centerTempList.contains(centerName)) {
						contectStartIndex = i;
					} else {
						centerTempList.add(centerName);
					}
				}
				if (i == list.size() - 1 && j == mainLine.length-1) {//最後一筆資料結束後全行合計資料欄位合併
					sheet.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 0 , 3)); // firstRow,endRow, firstColumn, endColumn

//LOCK					sheet.addMergedRegion(new CellRangeAddress(contectStartIndex, contectStartIndex, 0 , 3)); // firstRow,endRow, firstColumn, endColumn
				}
				
				// modify by ocean : 20181107 宥宜要求隱藏
//				if (null != list.get(i).get(mainLine[0]) && "ROLE_NAME".equals(mainLine[j]) && null == list.get(i).get(mainLine[j])) {
//					cell.setCellValue("小計");
//				} else {
					cell.setCellValue(list.get(i).get(mainLine[j]));
//				}
			}
			index++;
		}

		workbook.write(new FileOutputStream(filePath));

		// download
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		outputVO.setReportLst(inputVO.getEXPORT_LST());

		sendRtnObject(outputVO);
	}

	private List<Map<String, String>> addSummary(List<Map<String, String>> list) {
		List result = new ArrayList();
		List branchTemp = new ArrayList();

		BigDecimal WAIT_IN = new BigDecimal(0);
		BigDecimal FC_COUNT = new BigDecimal(0);
		BigDecimal CHK_WAIT_MEET = new BigDecimal(0);
		BigDecimal LEAVING_COUNT = new BigDecimal(0);
		BigDecimal CHK_PRICE_ING = new BigDecimal(0);
		BigDecimal CHK_PIP_REPORT = new BigDecimal(0);
		BigDecimal SHORTFALL = new BigDecimal(0);
		BigDecimal SERVING_COUNT = new BigDecimal(0);
		for (int i=0	;	i<list.size()	;	i++) {
			Map	map	=	list.get(i);
			String CENTER_NAME = (String) map.get("CENTER_NAME");
			String BRANCH_NAME = (String) map.get("BRANCH_NAME");
			String BRANCH_AREA_NAME = (String) map.get("BRANCH_AREA_NAME");
			String BRANCH_ID = (String) map.get("BRANCH_ID");
			if (map.isEmpty())
				continue;

			result.add(map);
			if (!StringUtils.isEmpty(BRANCH_ID)) {//有分行id才累加
				Map nextMap	=	list.get(i+1);
				String nextBranchId = (String)nextMap.get("BRANCH_ID");
				if (BRANCH_ID.equals(nextBranchId))	{//與下一筆同分行，累加
					WAIT_IN = WAIT_IN.add(new BigDecimal((String) map.get("WAIT_IN")));
					FC_COUNT = FC_COUNT.add(new BigDecimal((String) map.get("FC_COUNT")));
					CHK_WAIT_MEET = CHK_WAIT_MEET.add(new BigDecimal((String) map.get("CHK_WAIT_MEET")));
					LEAVING_COUNT = LEAVING_COUNT.add(new BigDecimal((String) map.get("LEAVING_COUNT")));
					CHK_PRICE_ING = CHK_PRICE_ING.add(new BigDecimal((String) map.get("CHK_PRICE_ING")));
					CHK_PIP_REPORT = CHK_PIP_REPORT.add(new BigDecimal((String) map.get("CHK_PIP_REPORT")));
					SHORTFALL = SHORTFALL.add(new BigDecimal((String) map.get("SHORTFALL")));
					SERVING_COUNT = SERVING_COUNT.add(new BigDecimal((String) map.get("SERVING_COUNT")));
				} else	{//下一筆不同分行
					WAIT_IN = WAIT_IN.add(new BigDecimal((String) map.get("WAIT_IN")));
					FC_COUNT = FC_COUNT.add(new BigDecimal((String) map.get("FC_COUNT")));
					CHK_WAIT_MEET = CHK_WAIT_MEET.add(new BigDecimal((String) map.get("CHK_WAIT_MEET")));
					LEAVING_COUNT = LEAVING_COUNT.add(new BigDecimal((String) map.get("LEAVING_COUNT")));
					CHK_PRICE_ING = CHK_PRICE_ING.add(new BigDecimal((String) map.get("CHK_PRICE_ING")));
					CHK_PIP_REPORT = CHK_PIP_REPORT.add(new BigDecimal((String) map.get("CHK_PIP_REPORT")));
					SHORTFALL = SHORTFALL.add(new BigDecimal((String) map.get("SHORTFALL")));
					SERVING_COUNT = SERVING_COUNT.add(new BigDecimal((String) map.get("SERVING_COUNT")));
					
					Map mapToAdd = new HashMap();
					mapToAdd.put("CENTER_NAME", CENTER_NAME);
					mapToAdd.put("BRANCH_AREA_NAME", BRANCH_AREA_NAME);
					mapToAdd.put("BRANCH_NAME", BRANCH_NAME);
					mapToAdd.put("BRANCH_ID", BRANCH_ID);
					mapToAdd.put("ROLE_NAME", "小計");
					mapToAdd.put("WAIT_IN", WAIT_IN.toString());
					mapToAdd.put("FC_COUNT", FC_COUNT.toString());
					mapToAdd.put("CHK_WAIT_MEET", CHK_WAIT_MEET.toString());
					mapToAdd.put("LEAVING_COUNT", LEAVING_COUNT.toString());
					mapToAdd.put("CHK_PRICE_ING", CHK_PRICE_ING.toString());
					mapToAdd.put("CHK_PIP_REPORT", CHK_PIP_REPORT.toString());
					mapToAdd.put("SHORTFALL", SHORTFALL.toString());
					mapToAdd.put("SERVING_COUNT", SERVING_COUNT.toString());
					result.add(mapToAdd);
					WAIT_IN = new BigDecimal(0);
					FC_COUNT = new BigDecimal(0);
					CHK_WAIT_MEET = new BigDecimal(0);
					LEAVING_COUNT = new BigDecimal(0);
					CHK_PRICE_ING = new BigDecimal(0);
					CHK_PIP_REPORT = new BigDecimal(0);
					SHORTFALL = new BigDecimal(0);
					SERVING_COUNT = new BigDecimal(0);
					branchTemp.add(BRANCH_ID);
				}
			}
		}
		return result;
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank((String) map.get(key))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 取得temp資料夾絕對路徑
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String getTempPath() throws JBranchException {
		String serverPath = (String) getCommonVariable(SystemVariableConsts.SERVER_PATH);
		String seperator = System.getProperties().getProperty("file.separator");
		if (!serverPath.endsWith(seperator)) {
			serverPath += seperator;
		}
		return serverPath + "temp";
	}
}
