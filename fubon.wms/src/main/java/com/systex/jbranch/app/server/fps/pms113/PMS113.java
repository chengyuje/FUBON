package com.systex.jbranch.app.server.fps.pms113;

import java.io.FileOutputStream;
import java.math.BigDecimal;
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

import com.systex.jbranch.app.server.fps.pms111.PMS111;
import com.systex.jbranch.app.server.fps.pms111.PMS111InputVO;
import com.systex.jbranch.app.server.fps.pms111.PMS111OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms113")
@Scope("request")
public class PMS113 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	// 已聯繫狀態表 - 查詢
	public void getList (Object body, IPrimitiveMap header) throws Exception {
		
		PMS113InputVO inputVO = (PMS113InputVO) body;
		PMS113OutputVO outputVO = new PMS113OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 防呆
		if (StringUtils.isBlank(inputVO.geteDate())) {
			// 若前端沒有選擇起日，預設為系統日(年月)
			if (StringUtils.isBlank(inputVO.getsDate())) {
				inputVO.setsDate(new SimpleDateFormat("yyyyMM").format(new Date()));
			}
			
			// 以起日查LIST取得第一筆(最大值) - 若無迄日=當月(1)
			PMS111InputVO inputVO_111 = new PMS111InputVO();
			inputVO_111.setsDate(inputVO.getsDate());
			inputVO_111.setMonInterval(BigDecimal.ONE);
			PMS111 pms111 = (PMS111) PlatformContext.getBean("pms111");
			PMS111OutputVO outputVO_temp = pms111.getEDateList(inputVO_111);
			if (outputVO_temp.geteDateList().size() > 0) 
				inputVO.seteDate((String) outputVO_temp.geteDateList().get(0).get("DATA"));
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, DEFN.BRANCH_AREA_GROUP, DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, DEFN.BRANCH_GROUP, ");
		sb.append("         EP.EST_PRD_CODE, PS.PIPELINE_STATUS, NVL(PIP.TOT_PIPELINE_STATUS, 0) AS TOT_PIPELINE_STATUS, NVL(PIP.TOT_APPROPRIATION_AMT, 0) AS TOT_APPROPRIATION_AMT ");
		sb.append("  FROM VWORG_DEFN_INFO DEFN ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT PARAM_CODE AS PIPELINE_STATUS, PARAM_NAME AS PIPELINE_S_NAME ");
		sb.append("    FROM TBSYSPARAMETER ");
		sb.append("    WHERE PARAM_TYPE = 'PMS.PIPELINE_STATUS' ");
		sb.append("  ) PS ON 1 = 1 ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT PARAM_CODE AS EST_PRD_CODE, PARAM_NAME AS EST_PRD_NAME ");
		sb.append("    FROM TBSYSPARAMETER ");
		sb.append("    WHERE PARAM_TYPE = 'PMS.PIPELINE_PRD' ");
		sb.append("  ) EP ON 1 = 1 ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT LEADS.PLAN_CENTER_ID, ");
		sb.append("           LEADS.PLAN_AREA_ID, ");
		sb.append("           PL.BRA_NBR, ");
		sb.append("           PL.EST_PRD, ");
		sb.append("           PIPELINE_STATUS, ");
		sb.append("           COUNT(PIPELINE_STATUS) AS TOT_PIPELINE_STATUS, ");
		sb.append("           SUM(APPROPRIATION_AMT) AS TOT_APPROPRIATION_AMT ");
		sb.append("    FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("    INNER JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("    LEFT JOIN TBPMS_PIPELINE PL ON LEADS.PLAN_SEQ = PL.PLAN_SEQ AND LEADS.PLAN_YEARMON = PL.PLAN_YEARMON AND LEADS.PLAN_CENTER_ID = PL.CENTER_ID AND LEADS.PLAN_AREA_ID = PL.AREA_ID ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT CASE_NO, CASE_TYPE, PIPELINE_STATUS, MAX(APPROPRIATION_DATE) AS APPROPRIATION_DATE, SUM(APPROPRIATION_AMT) AS APPROPRIATION_AMT ");
		sb.append("      FROM TBPMS_PIPELINE_DTL ");
		sb.append("      GROUP BY CASE_NO, CASE_TYPE, PIPELINE_STATUS ");
		sb.append("    ) DL ON PL.CASE_NUM = DL.CASE_NO AND PL.EST_PRD = DL.CASE_TYPE ");
		sb.append("    WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') ");
		sb.append("    AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("    AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("    AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");
		sb.append("    GROUP BY LEADS.PLAN_CENTER_ID, LEADS.PLAN_AREA_ID, PL.BRA_NBR, PL.EST_PRD, DL.PIPELINE_STATUS ");
		sb.append("  ) PIP ON DEFN.REGION_CENTER_ID = PIP.PLAN_CENTER_ID AND DEFN.BRANCH_AREA_ID = PIP.PLAN_AREA_ID AND DEFN.BRANCH_NBR = PIP.BRA_NBR AND EP.EST_PRD_CODE = PIP.EST_PRD AND PS.PIPELINE_STATUS = PIP.PIPELINE_STATUS ");
		sb.append(") ");
		sb.append(", AMT_PIVOT AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_AREA_GROUP, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP, ");
		sb.append("         EST_PRD_CODE, APP_AMT_01, APP_AMT_02, APP_AMT_03, APP_AMT_04, APP_AMT_05, APP_AMT_06 ");
		sb.append("  FROM BASE ");
		sb.append("  PIVOT (SUM(TOT_APPROPRIATION_AMT) FOR PIPELINE_STATUS IN ('01' AS APP_AMT_01, '02' AS APP_AMT_02, '03' AS APP_AMT_03, '04' AS APP_AMT_04, '05' AS APP_AMT_05, '06' AS APP_AMT_06)) ");
		sb.append(") ");
		sb.append(", STS_PIVOT AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_AREA_GROUP, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP, ");
		sb.append("         EST_PRD_CODE, PIP_STS_01, PIP_STS_02, PIP_STS_03, PIP_STS_04, PIP_STS_05, PIP_STS_06 ");
		sb.append("  FROM BASE ");
		sb.append("  PIVOT (SUM(TOT_PIPELINE_STATUS) FOR PIPELINE_STATUS IN ('01' AS PIP_STS_01, '02' AS PIP_STS_02, '03' AS PIP_STS_03, '04' AS PIP_STS_04, '05' AS PIP_STS_05, '06' AS PIP_STS_06)) ");
		sb.append(") ");
		sb.append(", TOT_COLUMN AS ( ");
		sb.append("  SELECT A.REGION_CENTER_ID, A.REGION_CENTER_NAME, A.BRANCH_AREA_ID, A.BRANCH_AREA_NAME, A.BRANCH_AREA_GROUP, A.BRANCH_NBR, A.BRANCH_NAME, A.BRANCH_GROUP, ");
		sb.append("         A.EST_PRD_CODE, ");
		sb.append("         B.PIP_STS_01, A.APP_AMT_01, ");
		sb.append("         B.PIP_STS_02, A.APP_AMT_02, ");
		sb.append("         B.PIP_STS_03, A.APP_AMT_03, ");
		sb.append("         B.PIP_STS_04, A.APP_AMT_04, ");
		sb.append("         B.PIP_STS_05, A.APP_AMT_05, ");
		sb.append("         B.PIP_STS_06, A.APP_AMT_06 ");
		sb.append("  FROM AMT_PIVOT A ");
		sb.append("  LEFT JOIN STS_PIVOT B ON A.REGION_CENTER_ID = B.REGION_CENTER_ID AND A.BRANCH_AREA_ID = B.BRANCH_AREA_ID AND A.BRANCH_NBR = B.BRANCH_NBR ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND A.EST_PRD_CODE = :estPrdCode ");
		sb.append(") ");

		// 處 合計
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME || ' 合計' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS AREA_GROUP, ");
		sb.append("       EST_PRD_CODE, ");
		sb.append("       SUM(PIP_STS_01) AS PIP_STS_01, SUM(APP_AMT_01) AS APP_AMT_01, ");
		sb.append("       SUM(PIP_STS_02) AS PIP_STS_02, SUM(APP_AMT_02) AS APP_AMT_02, ");
		sb.append("       SUM(PIP_STS_03) AS PIP_STS_03, SUM(APP_AMT_03) AS APP_AMT_03, ");
		sb.append("       SUM(PIP_STS_04) AS PIP_STS_04, SUM(APP_AMT_04) AS APP_AMT_04, ");
		sb.append("       SUM(PIP_STS_05) AS PIP_STS_05, SUM(APP_AMT_05) AS APP_AMT_05, ");
		sb.append("       SUM(PIP_STS_06) AS PIP_STS_06, SUM(APP_AMT_06) AS APP_AMT_06 ");
		sb.append("FROM TOT_COLUMN ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME || ' 合計', EST_PRD_CODE ");
				
		sb.append("UNION ");
				
		// 區 合計
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, BRANCH_AREA_GROUP AS AREA_GROUP, ");
		sb.append("       EST_PRD_CODE, ");
		sb.append("       SUM(PIP_STS_01) AS PIP_STS_01, SUM(APP_AMT_01) AS APP_AMT_01, ");
		sb.append("       SUM(PIP_STS_02) AS PIP_STS_02, SUM(APP_AMT_02) AS APP_AMT_02, ");
		sb.append("       SUM(PIP_STS_03) AS PIP_STS_03, SUM(APP_AMT_03) AS APP_AMT_03, ");
		sb.append("       SUM(PIP_STS_04) AS PIP_STS_04, SUM(APP_AMT_04) AS APP_AMT_04, ");
		sb.append("       SUM(PIP_STS_05) AS PIP_STS_05, SUM(APP_AMT_05) AS APP_AMT_05, ");
		sb.append("       SUM(PIP_STS_06) AS PIP_STS_06, SUM(APP_AMT_06) AS APP_AMT_06 ");
		sb.append("FROM TOT_COLUMN ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計', BRANCH_AREA_GROUP, EST_PRD_CODE ");
				
		sb.append("UNION ");
				
		// 分行
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP AS AREA_GROUP, ");
		sb.append("       EST_PRD_CODE, ");
		sb.append("       SUM(PIP_STS_01) AS PIP_STS_01, SUM(APP_AMT_01) AS APP_AMT_01, ");
		sb.append("       SUM(PIP_STS_02) AS PIP_STS_02, SUM(APP_AMT_02) AS APP_AMT_02, ");
		sb.append("       SUM(PIP_STS_03) AS PIP_STS_03, SUM(APP_AMT_03) AS APP_AMT_03, ");
		sb.append("       SUM(PIP_STS_04) AS PIP_STS_04, SUM(APP_AMT_04) AS APP_AMT_04, ");
		sb.append("       SUM(PIP_STS_05) AS PIP_STS_05, SUM(APP_AMT_05) AS APP_AMT_05, ");
		sb.append("       SUM(PIP_STS_06) AS PIP_STS_06, SUM(APP_AMT_06) AS APP_AMT_06 ");
		sb.append("FROM TOT_COLUMN ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY  REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP, EST_PRD_CODE ");
				
		sb.append("UNION ");
				
		// 全行 合計
		sb.append("SELECT '' AS REGION_CENTER_ID, '全行 合計' AS REGION_CENTER_NAME, '' AS  BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS AREA_GROUP, "); 
		sb.append("       EST_PRD_CODE, ");
		sb.append("       SUM(PIP_STS_01) AS PIP_STS_01, SUM(APP_AMT_01) AS APP_AMT_01, ");
		sb.append("       SUM(PIP_STS_02) AS PIP_STS_02, SUM(APP_AMT_02) AS APP_AMT_02, ");
		sb.append("       SUM(PIP_STS_03) AS PIP_STS_03, SUM(APP_AMT_03) AS APP_AMT_03, ");
		sb.append("       SUM(PIP_STS_04) AS PIP_STS_04, SUM(APP_AMT_04) AS APP_AMT_04, ");
		sb.append("       SUM(PIP_STS_05) AS PIP_STS_05, SUM(APP_AMT_05) AS APP_AMT_05, ");
		sb.append("       SUM(PIP_STS_06) AS PIP_STS_06, SUM(APP_AMT_06) AS APP_AMT_06 ");
		sb.append("FROM TOT_COLUMN ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY '', '全行 合計', EST_PRD_CODE ");

		sb.append("ORDER BY REGION_CENTER_ID ASC, BRANCH_AREA_ID ASC, BRANCH_NBR ASC ");
		
		queryCondition.setObject("sDATE", inputVO.getsDate());
		queryCondition.setObject("eDATE", inputVO.geteDate());
		queryCondition.setObject("estPrdCode", inputVO.getEstPrd());
		
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);		
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
		for (Map<String, Object> centerMap : list) {
			String regionCenter = (String) centerMap.get("REGION_CENTER_NAME");
			if (centerTempList.indexOf(regionCenter) < 0) { //regionCenter
				centerTempList.add(regionCenter);
				
				Integer centerRowspan = 1;
				
				List<Map<String, Object>> branchAreaList = new ArrayList<Map<String,Object>>();
				ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
				for (Map<String, Object> branchAreaMap : list) {
					String branchArea = (String) branchAreaMap.get("BRANCH_AREA_NAME");
					
					Integer branchAreaRowspan = 1;
					
					//==== 營運區
					if (regionCenter.equals((String) branchAreaMap.get("REGION_CENTER_NAME"))) { 
						if (branchAreaTempList.indexOf(branchArea) < 0) { //branchArea
							branchAreaTempList.add(branchArea);
							
							//==== 分行
							List<Map<String, Object>> branchList = new ArrayList<Map<String,Object>>();
							ArrayList<String> branchTempList = new ArrayList<String>(); //比對用
							ArrayList<String> centerCountTempList = new ArrayList<String>(); //比對用
							for (Map<String, Object> branchMap : list) {
								String branch = (String) branchMap.get("BRANCH_NAME");
								
								if (branchArea != null && branchMap.get("BRANCH_AREA_NAME") != null) {
									if (branchArea.equals((String) branchMap.get("BRANCH_AREA_NAME"))) {
										if (branchTempList.indexOf(branch) < 0) { //branchArea
											branchTempList.add(branch);
											
											//==== 詳細資訊
											List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
											for (Map<String, Object> roleMap : list) {
												
												if (branch != null && roleMap.get("BRANCH_NAME") != null) {
													if (branch.equals((String) roleMap.get("BRANCH_NAME"))) {
														Map<String, Object> roleTempMap = new HashMap<String, Object>();
														
														roleTempMap.put("PIP_STS_01", (BigDecimal) branchMap.get("PIP_STS_01"));
														roleTempMap.put("APP_AMT_01", (BigDecimal) branchMap.get("APP_AMT_01"));
														
														roleTempMap.put("PIP_STS_02", (BigDecimal) branchMap.get("PIP_STS_02"));
														roleTempMap.put("APP_AMT_02", (BigDecimal) branchMap.get("APP_AMT_02"));
														
														roleTempMap.put("PIP_STS_03", (BigDecimal) branchMap.get("PIP_STS_03"));
														roleTempMap.put("APP_AMT_03", (BigDecimal) branchMap.get("APP_AMT_03"));
														
														roleTempMap.put("PIP_STS_04", (BigDecimal) branchMap.get("PIP_STS_04"));
														roleTempMap.put("APP_AMT_04", (BigDecimal) branchMap.get("APP_AMT_04"));
														
														roleTempMap.put("PIP_STS_05", (BigDecimal) branchMap.get("PIP_STS_05"));
														roleTempMap.put("APP_AMT_05", (BigDecimal) branchMap.get("APP_AMT_05"));
														
														roleTempMap.put("PIP_STS_06", (BigDecimal) branchMap.get("PIP_STS_06"));
														roleTempMap.put("APP_AMT_06", (BigDecimal) branchMap.get("APP_AMT_06"));
														
														roleList.add(roleTempMap);
													}
												} else if (branchArea.equals(roleMap.get("BRANCH_AREA_NAME"))) {
													Map<String, Object> roleTempMap = new HashMap<String, Object>();
													
													roleTempMap.put("PIP_STS_01", (BigDecimal) branchMap.get("PIP_STS_01"));
													roleTempMap.put("APP_AMT_01", (BigDecimal) branchMap.get("APP_AMT_01"));
													
													roleTempMap.put("PIP_STS_02", (BigDecimal) branchMap.get("PIP_STS_02"));
													roleTempMap.put("APP_AMT_02", (BigDecimal) branchMap.get("APP_AMT_02"));
													
													roleTempMap.put("PIP_STS_03", (BigDecimal) branchMap.get("PIP_STS_03"));
													roleTempMap.put("APP_AMT_03", (BigDecimal) branchMap.get("APP_AMT_03"));
													
													roleTempMap.put("PIP_STS_04", (BigDecimal) branchMap.get("PIP_STS_04"));
													roleTempMap.put("APP_AMT_04", (BigDecimal) branchMap.get("APP_AMT_04"));
													
													roleTempMap.put("PIP_STS_05", (BigDecimal) branchMap.get("PIP_STS_05"));
													roleTempMap.put("APP_AMT_05", (BigDecimal) branchMap.get("APP_AMT_05"));
													
													roleTempMap.put("PIP_STS_06", (BigDecimal) branchMap.get("PIP_STS_06"));
													roleTempMap.put("APP_AMT_06", (BigDecimal) branchMap.get("APP_AMT_06"));
													
													roleList.add(roleTempMap);
												}
											}
											
											Map<String, Object> branchTempMap = new HashMap<String, Object>();
											branchTempMap.put("AREA_GROUP", (String) branchMap.get("AREA_GROUP"));
											branchTempMap.put("BRANCH_NBR", (String) branchMap.get("BRANCH_NBR"));
											branchTempMap.put("BRANCH_NAME", branch);
											branchTempMap.put("ROLE", roleList);
											centerRowspan = centerRowspan + roleList.size();
											branchAreaRowspan = branchAreaRowspan + roleList.size();
											branchTempMap.put("ROWSPAN", roleList.size());
											
											branchList.add(branchTempMap);
										}
									}
								} else if (regionCenter.equals(branchMap.get("REGION_CENTER_NAME"))) {
									String centerCount = (String) branchMap.get("REGION_CENTER_NAME");
									if (centerCountTempList.indexOf(centerCount) < 0) { //regionCenter
										
										centerCountTempList.add(centerCount);
										
										//==== 詳細資訊
										List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
										for (Map<String, Object> roleMap : list) {
											
											if (regionCenter.equals(roleMap.get("REGION_CENTER_NAME"))) {
												Map<String, Object> roleTempMap = new HashMap<String, Object>();
											
												roleTempMap.put("PIP_STS_01", (BigDecimal) branchMap.get("PIP_STS_01"));
												roleTempMap.put("APP_AMT_01", (BigDecimal) branchMap.get("APP_AMT_01"));
												
												roleTempMap.put("PIP_STS_02", (BigDecimal) branchMap.get("PIP_STS_02"));
												roleTempMap.put("APP_AMT_02", (BigDecimal) branchMap.get("APP_AMT_02"));
												
												roleTempMap.put("PIP_STS_03", (BigDecimal) branchMap.get("PIP_STS_03"));
												roleTempMap.put("APP_AMT_03", (BigDecimal) branchMap.get("APP_AMT_03"));
												
												roleTempMap.put("PIP_STS_04", (BigDecimal) branchMap.get("PIP_STS_04"));
												roleTempMap.put("APP_AMT_04", (BigDecimal) branchMap.get("APP_AMT_04"));
												
												roleTempMap.put("PIP_STS_05", (BigDecimal) branchMap.get("PIP_STS_05"));
												roleTempMap.put("APP_AMT_05", (BigDecimal) branchMap.get("APP_AMT_05"));
												
												roleTempMap.put("PIP_STS_06", (BigDecimal) branchMap.get("PIP_STS_06"));
												roleTempMap.put("APP_AMT_06", (BigDecimal) branchMap.get("APP_AMT_06"));
												
												roleList.add(roleTempMap);
												
											}
										}
										
										Map<String, Object> branchTempMap = new HashMap<String, Object>();
										branchTempMap.put("AREA_GROUP", (String) branchMap.get("AREA_GROUP"));
										branchTempMap.put("BRANCH_NBR", (String) branchMap.get("BRANCH_NBR"));
										branchTempMap.put("BRANCH_NAME", branch);
										branchTempMap.put("ROLE", roleList);
										centerRowspan = centerRowspan + roleList.size();
										branchAreaRowspan = branchAreaRowspan + roleList.size();
										branchTempMap.put("ROWSPAN", roleList.size());
										
										branchList.add(branchTempMap);
									}
								}
							}
							
							Map<String, Object> branchAreaTempMap = new HashMap<String, Object>();
							branchAreaTempMap.put("BRANCH_AREA_GROUP", (String) branchAreaMap.get("AREA_GROUP"));
							branchAreaTempMap.put("BRANCH_AREA_NAME", branchArea);
							branchAreaTempMap.put("BRANCH", branchList);
							centerRowspan = centerRowspan + branchList.size();
							branchAreaRowspan = branchAreaRowspan + branchList.size();
							branchAreaTempMap.put("ROWSPAN", branchAreaRowspan);
							branchAreaTempMap.put("COLSPAN", (branchList.size() == 1 && branchList.get(0).get("BRANCH_NAME") == null ? 3: 1));

							branchAreaList.add(branchAreaTempMap);
						}
					}
				}

				Map<String, Object> centerTempMap = new HashMap<String, Object>();
				centerTempMap.put("REGION_CENTER_NAME", regionCenter);
				centerTempMap.put("BRANCH_AREA", branchAreaList);
				centerRowspan = centerRowspan + branchAreaList.size();
				centerTempMap.put("ROWSPAN", centerRowspan);
				centerTempMap.put("COLSPAN", (branchAreaList.size() == 1 && branchAreaList.get(0).get("BRANCH_AREA_NAME") == null ? 4: 1));

				returnList.add(centerTempMap);
			}
		}
		
		outputVO.setQryList(returnList);
		outputVO.setRep_qryList(list);
		
		sendRtnObject(outputVO);
	}
	
	// 匯出
	public void export (Object body, IPrimitiveMap header) throws Exception {
		
		PMS113InputVO inputVO = (PMS113InputVO) body;
		PMS113OutputVO outputVO = new PMS113OutputVO();
		
		String[] headerLine1 = {"業務處", "營運區", "分行別", "分別名稱", "組別", 
							    "進件", "進件", 
							    "核准", "核准",
//							    "已核准撤件", "已核准撤件",
							    "撥款", "撥款", 
//							    "婉拒", "婉拒", 
//							    "自行撤件", "自行撤件"
							    };
		
		String[] headerLine2 = {"", "", "", "", "", 
								"件數", "金額",
								"件數", "金額",
//								"件數", "金額",
								"件數", "金額",
//								"件數", "金額",
//								"件數", "金額"
								};
		
		String[] mainLine    = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "AREA_GROUP",
								"PIP_STS_01", "APP_AMT_01",
								"PIP_STS_02", "APP_AMT_02",
//								"PIP_STS_03", "APP_AMT_03",
								"PIP_STS_04", "APP_AMT_04",
//								"PIP_STS_05", "APP_AMT_05",
//								"PIP_STS_06", "APP_AMT_06"
								}; 
				
		StringBuffer fileName = new StringBuffer();
		fileName.append("留資名單進件狀態統計表_").append(inputVO.geteDate()).append(".xlsx");

		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("留資名單進件狀態統計表");
		sheet.setDefaultColumnWidth(20);
		
		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);
		
		XSSFCellStyle qryHeadStyle = workbook.createCellStyle();
		qryHeadStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		qryHeadStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		qryHeadStyle.setFillForegroundColor(HSSFColor.YELLOW.index);// 填滿顏色
		qryHeadStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		qryHeadStyle.setBorderBottom((short) 1);
		qryHeadStyle.setBorderTop((short) 1);
		qryHeadStyle.setBorderLeft((short) 1);
		qryHeadStyle.setBorderRight((short) 1);
		
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
				cell.setCellStyle(qryHeadStyle);
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
		
		index++; //next row
		
		row = sheet.createRow(index);
		for (int i = 0; i < headerLine2.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(qryHeadStyle);
			cell.setCellValue(headerLine2[i]);
			
			if ("".equals(headerLine2[i])) {
				sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
			}
		}
		
		index++;
		
		ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchTempList = new ArrayList<String>(); //比對用
		Integer centerStartFlag = 0, branchAreaStartFlag = 0, branchStartFlag = 0;
		Integer centerEndFlag = 0, branchAreaEndFlag = 0, branchEndFlag = 0;
		
		Integer contectStartIndex = index;
		
		List<Map<String, String>> list = inputVO.getRep_qryList();
		
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				
				String centerName = list.get(i).get("REGION_CENTER_NAME");
				String branchAreaName = list.get(i).get("BRANCH_AREA_NAME");
				String branchName = list.get(i).get("BRANCH_NAME");
				
				if (j == 0 && centerTempList.indexOf(centerName) < 0) {
					centerTempList.add(centerName);
					if (centerEndFlag != 0) {
						if (null != centerName && centerName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(i + contectStartIndex, i + contectStartIndex, j, j + 4)); // firstRow, endRow, firstColumn, endColumn
						}
					} else {
						if (StringUtils.equals("全行 合計", centerName)) {
							sheet.addMergedRegion(new CellRangeAddress(i + contectStartIndex, i + contectStartIndex, j, j + 4)); // firstRow, endRow, firstColumn, endColumn
						}
					}
					
					centerStartFlag = i;
					centerEndFlag = 0;
				} else if (j == 0 && null != list.get(i).get("REGION_CENTER_NAME")) {
					centerEndFlag = i;
				}
				
				if (j == 1 && branchAreaTempList.indexOf(branchAreaName) < 0) {
					branchAreaTempList.add(branchAreaName);
					
					if (branchAreaEndFlag != 0) {
						if (null != branchAreaName && branchAreaName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(i + contectStartIndex, i + contectStartIndex, j, j + 2)); // firstRow, endRow, firstColumn, endColumn
						} 
					} 
					
					branchAreaStartFlag = i;
					branchAreaEndFlag = 0;
				} else if (j == 1 && null != list.get(i).get("BRANCH_AREA_NAME")) {
					branchAreaEndFlag = i;
				}
				
				if (j == 2 && branchTempList.indexOf(branchName) < 0) {
					branchTempList.add(branchName);
					
					if (branchEndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j + 1, j + 1)); // firstRow, endRow, firstColumn, endColumn
					}
					
					branchStartFlag = i;
					branchEndFlag = 0;
				} else if (j == 2 && null != list.get(i).get("BRANCH_NAME")) {
					branchEndFlag = i; 
				}
				
				if (null != list.get(i).get(mainLine[0]) && null == list.get(i).get(mainLine[j])) {
				} else {
					cell.setCellValue(list.get(i).get(mainLine[j]));
				}
			}
			
			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		// download
 		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName.toString());
		
 		sendRtnObject(outputVO);
	}
}
