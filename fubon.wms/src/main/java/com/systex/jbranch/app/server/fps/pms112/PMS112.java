package com.systex.jbranch.app.server.fps.pms112;

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

@Component("pms112")
@Scope("request")
public class PMS112 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	// 查詢
	public void getList (Object body, IPrimitiveMap header) throws Exception {
		
		PMS112InputVO inputVO = (PMS112InputVO) body;
		PMS112OutputVO outputVO = new PMS112OutputVO();
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
		sb.append("WITH DTL AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_AREA_GROUP, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP, ");
		sb.append("         NVL(STATUS_ALL, 0) AS STATUS_ALL, ");
		sb.append("         NVL(STATUS_01, 0) AS STATUS_01, ");
		sb.append("         NVL(STATUS_99, 0) AS STATUS_99, ");
		sb.append("         NVL(STATUS_03B, 0) AS STATUS_03B, ");
		sb.append("         NVL(STATUS_02A, 0) AS STATUS_02A, ");
		sb.append("         NVL(STATUS_02B, 0) AS STATUS_02B, ");
		sb.append("         NVL(STATUS_02, 0) AS STATUS_02, ");
		sb.append("         NVL(STATUS_02ALL, 0) AS STATUS_02ALL, ");
		sb.append("         NVL(STATUS_03A, 0) AS STATUS_03A, ");
		sb.append("         NVL(STATUS_03D, 0) AS STATUS_03D, ");
		sb.append("         NVL(STATUS_03E, 0) AS STATUS_03E, ");
		sb.append("         NVL(STATUS_03F, 0) AS STATUS_03F, ");
		sb.append("         NVL(STATUS_03G, 0) AS STATUS_03G, ");
		sb.append("         NVL(STATUS_03H, 0) AS STATUS_03H, ");
		sb.append("         NVL(STATUS_03I, 0) AS STATUS_03I, ");
		sb.append("         NVL(STATUS_03J, 0) AS STATUS_03J, ");
		sb.append("         NVL(STATUS_03K, 0) AS STATUS_03K, ");
		sb.append("         NVL(STATUS_03ALL, 0) AS STATUS_03ALL ");
		sb.append("  FROM ( ");
		sb.append("    SELECT DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, DEFN.BRANCH_AREA_GROUP, DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, DEFN.BRANCH_GROUP, ");
		sb.append("           RES.RESPONSE_CODE, SC.COUNTS ");
		sb.append("    FROM VWORG_DEFN_INFO DEFN ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT PARAM_CODE AS RESPONSE_CODE, PARAM_NAME AS RESPONSE_NAME ");
		sb.append("      FROM TBSYSPARAMETER ");
		sb.append("      WHERE PARAM_TYPE = 'PMS.PIPE_CONTENT_STAT' ");
		sb.append("    ) RES ON 1 = 1 ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT LEADS.BRANCH_ID, LEADS.LEAD_STATUS, COUNT(1) AS COUNTS ");
		sb.append("      FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("      LEFT JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("      WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') ");
		sb.append("      AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");
		
		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			sb.append("      AND CAMP.LEAD_TYPE = :leadType ");
		
		sb.append("      GROUP BY LEADS.BRANCH_ID, LEAD_STATUS ");
		sb.append("      UNION ");
		sb.append("      SELECT LEADS.BRANCH_ID, '02ALL' AS LEAD_STATUS, COUNT(1) AS COUNTS ");
		sb.append("      FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("      LEFT JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("      WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') ");
		sb.append("      AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");

		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			sb.append("      AND CAMP.LEAD_TYPE = :leadType ");
		
		sb.append("      AND EXISTS (SELECT 1 FROM TBSYSPARAMETER P WHERE P.PARAM_TYPE = 'PMS.PIPE_CONTENT_STAT_02ALL' AND LEADS.LEAD_STATUS = P.PARAM_CODE) ");
		sb.append("      GROUP BY LEADS.BRANCH_ID, 'PIPE_CONTENT_STAT_02ALL' ");
		sb.append("      UNION ");
		sb.append("      SELECT LEADS.BRANCH_ID, '03ALL' AS LEAD_STATUS, COUNT(1) AS COUNTS ");
		sb.append("      FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("      LEFT JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("      WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') ");
		sb.append("      AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");

		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			sb.append("      AND CAMP.LEAD_TYPE = :leadType ");
		
		sb.append("      AND EXISTS (SELECT 1 FROM TBSYSPARAMETER P WHERE P.PARAM_TYPE = 'PMS.PIPE_CONTENT_STAT_03ALL' AND LEADS.LEAD_STATUS = P.PARAM_CODE) ");
		sb.append("      GROUP BY LEADS.BRANCH_ID, 'PMS.PIPE_CONTENT_STAT_03ALL' ");
		sb.append("      UNION ");
		sb.append("      SELECT LEADS.BRANCH_ID, 'ALL' AS LEAD_STATUS, COUNT(1) AS COUNTS ");
		sb.append("      FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("      LEFT JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("      WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') ");
		sb.append("      AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");

		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			sb.append("      AND CAMP.LEAD_TYPE = :leadType ");
		
		sb.append("      GROUP BY LEADS.BRANCH_ID, 'ALL' ");
		sb.append("    ) SC ON DEFN.BRANCH_NBR = SC.BRANCH_ID AND RES.RESPONSE_CODE = SC.LEAD_STATUS ");
		sb.append("  ) ");
		sb.append("  PIVOT (SUM(COUNTS) FOR RESPONSE_CODE IN ('ALL' AS STATUS_ALL, ");
		sb.append("                                           '01' AS STATUS_01, ");
		sb.append("                                           '99' AS STATUS_99, ");
		sb.append("                                           '03B' AS STATUS_03B, ");
		sb.append("                                           '02A' AS STATUS_02A, ");
		sb.append("                                           '02B' AS STATUS_02B, ");
		sb.append("                                           '02' AS STATUS_02, ");
		sb.append("                                           '02ALL' AS STATUS_02ALL, ");
		sb.append("                                           '03A' AS STATUS_03A, ");
		sb.append("                                           '03D' AS STATUS_03D, ");
		sb.append("                                           '03E' AS STATUS_03E, ");
		sb.append("                                           '03F' AS STATUS_03F, ");
		sb.append("                                           '03G' AS STATUS_03G, ");
		sb.append("                                           '03H' AS STATUS_03H, ");
		sb.append("                                           '03I' AS STATUS_03I, ");
		sb.append("                                           '03J' AS STATUS_03J, ");
		sb.append("                                           '03K' AS STATUS_03K, ");
		sb.append("                                           '03ALL' AS STATUS_03ALL)) ");
		sb.append(") ");

		// 處 合計
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME || ' 合計' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS AREA_GROUP, ");
		sb.append("       SUM(STATUS_ALL) AS STATUS_ALL, ");
		sb.append("       SUM(STATUS_01) AS STATUS_01, ");
		sb.append("       SUM(STATUS_99) AS STATUS_99, ");
		sb.append("       SUM(STATUS_03B) AS STATUS_03B, ");
		sb.append("       SUM(STATUS_02A) AS STATUS_02A, SUM(STATUS_02B) AS STATUS_02B, SUM(STATUS_02) AS STATUS_02, SUM(STATUS_02ALL) AS STATUS_02ALL, ");
		sb.append("       SUM(STATUS_03A) AS STATUS_03A, SUM(STATUS_03D) AS STATUS_03D, SUM(STATUS_03E) AS STATUS_03E, SUM(STATUS_03F) AS STATUS_03F, SUM(STATUS_03G) AS STATUS_03G, SUM(STATUS_03H) AS STATUS_03H, SUM(STATUS_03I) AS STATUS_03I, SUM(STATUS_03J) AS STATUS_03J, SUM(STATUS_03K) AS STATUS_03K, SUM(STATUS_03ALL) AS STATUS_03ALL ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME || ' 合計' ");
		
		sb.append("UNION ");
		
		// 區 合計
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, BRANCH_AREA_GROUP AS AREA_GROUP, ");
		sb.append("       SUM(STATUS_ALL) AS STATUS_ALL, ");
		sb.append("       SUM(STATUS_01) AS STATUS_01, ");
		sb.append("       SUM(STATUS_99) AS STATUS_99, ");
		sb.append("       SUM(STATUS_03B) AS STATUS_03B, ");
		sb.append("       SUM(STATUS_02A) AS STATUS_02A, SUM(STATUS_02B) AS STATUS_02B, SUM(STATUS_02) AS STATUS_02, SUM(STATUS_02ALL) AS STATUS_02ALL, ");
		sb.append("       SUM(STATUS_03A) AS STATUS_03A, SUM(STATUS_03D) AS STATUS_03D, SUM(STATUS_03E) AS STATUS_03E, SUM(STATUS_03F) AS STATUS_03F, SUM(STATUS_03G) AS STATUS_03G, SUM(STATUS_03H) AS STATUS_03H, SUM(STATUS_03I) AS STATUS_03I, SUM(STATUS_03J) AS STATUS_03J, SUM(STATUS_03K) AS STATUS_03K, SUM(STATUS_03ALL) AS STATUS_03ALL ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計', BRANCH_AREA_GROUP ");
		
		sb.append("UNION ");
		
		// 分行
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP AS AREA_GROUP, ");
		sb.append("       SUM(STATUS_ALL) AS STATUS_ALL, ");
		sb.append("       SUM(STATUS_01) AS STATUS_01, ");
		sb.append("       SUM(STATUS_99) AS STATUS_99, ");
		sb.append("       SUM(STATUS_03B) AS STATUS_03B, ");
		sb.append("       SUM(STATUS_02A) AS STATUS_02A, SUM(STATUS_02B) AS STATUS_02B, SUM(STATUS_02) AS STATUS_02, SUM(STATUS_02ALL) AS STATUS_02ALL, ");
		sb.append("       SUM(STATUS_03A) AS STATUS_03A, SUM(STATUS_03D) AS STATUS_03D, SUM(STATUS_03E) AS STATUS_03E, SUM(STATUS_03F) AS STATUS_03F, SUM(STATUS_03G) AS STATUS_03G, SUM(STATUS_03H) AS STATUS_03H, SUM(STATUS_03I) AS STATUS_03I, SUM(STATUS_03J) AS STATUS_03J, SUM(STATUS_03K) AS STATUS_03K, SUM(STATUS_03ALL) AS STATUS_03ALL ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY  REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP ");
		
		sb.append("UNION ");
		
		// 全行 合計
		sb.append("SELECT '' AS REGION_CENTER_ID, '全行 合計' AS REGION_CENTER_NAME, '' AS  BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS AREA_GROUP, ");
		sb.append("       SUM(STATUS_ALL) AS STATUS_ALL, ");
		sb.append("       SUM(STATUS_01) AS STATUS_01, ");
		sb.append("       SUM(STATUS_99) AS STATUS_99, ");
		sb.append("       SUM(STATUS_03B) AS STATUS_03B, ");
		sb.append("       SUM(STATUS_02A) AS STATUS_02A, SUM(STATUS_02B) AS STATUS_02B, SUM(STATUS_02) AS STATUS_02, SUM(STATUS_02ALL) AS STATUS_02ALL, ");
		sb.append("       SUM(STATUS_03A) AS STATUS_03A, SUM(STATUS_03D) AS STATUS_03D, SUM(STATUS_03E) AS STATUS_03E, SUM(STATUS_03F) AS STATUS_03F, SUM(STATUS_03G) AS STATUS_03G, SUM(STATUS_03H) AS STATUS_03H, SUM(STATUS_03I) AS STATUS_03I, SUM(STATUS_03J) AS STATUS_03J, SUM(STATUS_03K) AS STATUS_03K, SUM(STATUS_03ALL) AS STATUS_03ALL ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY '', '全行 合計' ");
		
		// 排序條件
		sb.append("ORDER BY REGION_CENTER_ID ASC, BRANCH_AREA_ID ASC, BRANCH_NBR ASC ");
		   
		queryCondition.setObject("sDATE", inputVO.getsDate());
		queryCondition.setObject("eDATE", inputVO.geteDate());
		
		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			queryCondition.setObject("leadType", inputVO.getLeadType());
		
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
														
														roleTempMap.put("STATUS_ALL", (BigDecimal) branchMap.get("STATUS_ALL"));
														roleTempMap.put("STATUS_01", (BigDecimal) branchMap.get("STATUS_01"));
														roleTempMap.put("STATUS_99", (BigDecimal) branchMap.get("STATUS_99"));
														roleTempMap.put("STATUS_03B", (BigDecimal) branchMap.get("STATUS_03B"));
														
														roleTempMap.put("STATUS_02A", (BigDecimal) branchMap.get("STATUS_02A"));
														roleTempMap.put("STATUS_02B", (BigDecimal) branchMap.get("STATUS_02B"));
														roleTempMap.put("STATUS_02", (BigDecimal) branchMap.get("STATUS_02"));
														roleTempMap.put("STATUS_02ALL", (BigDecimal) branchMap.get("STATUS_02ALL"));
														
														roleTempMap.put("STATUS_03A", (BigDecimal) branchMap.get("STATUS_03A"));
														roleTempMap.put("STATUS_03D", (BigDecimal) branchMap.get("STATUS_03D"));
														roleTempMap.put("STATUS_03E", (BigDecimal) branchMap.get("STATUS_03E"));
														roleTempMap.put("STATUS_03F", (BigDecimal) branchMap.get("STATUS_03F"));
														roleTempMap.put("STATUS_03G", (BigDecimal) branchMap.get("STATUS_03G"));
														roleTempMap.put("STATUS_03H", (BigDecimal) branchMap.get("STATUS_03H"));
														roleTempMap.put("STATUS_03I", (BigDecimal) branchMap.get("STATUS_03I"));
														roleTempMap.put("STATUS_03J", (BigDecimal) branchMap.get("STATUS_03J"));
														roleTempMap.put("STATUS_03K", (BigDecimal) branchMap.get("STATUS_03K"));
														roleTempMap.put("STATUS_03ALL", (BigDecimal) branchMap.get("STATUS_03ALL"));
														
														roleList.add(roleTempMap);
													}
												} else if (branchArea.equals(roleMap.get("BRANCH_AREA_NAME"))) {
													Map<String, Object> roleTempMap = new HashMap<String, Object>();
													
													roleTempMap.put("STATUS_ALL", (BigDecimal) branchMap.get("STATUS_ALL"));
													roleTempMap.put("STATUS_01", (BigDecimal) branchMap.get("STATUS_01"));
													roleTempMap.put("STATUS_99", (BigDecimal) branchMap.get("STATUS_99"));
													roleTempMap.put("STATUS_03B", (BigDecimal) branchMap.get("STATUS_03B"));
													
													roleTempMap.put("STATUS_02A", (BigDecimal) branchMap.get("STATUS_02A"));
													roleTempMap.put("STATUS_02B", (BigDecimal) branchMap.get("STATUS_02B"));
													roleTempMap.put("STATUS_02", (BigDecimal) branchMap.get("STATUS_02"));
													roleTempMap.put("STATUS_02ALL", (BigDecimal) branchMap.get("STATUS_02ALL"));
													
													roleTempMap.put("STATUS_03A", (BigDecimal) branchMap.get("STATUS_03A"));
													roleTempMap.put("STATUS_03D", (BigDecimal) branchMap.get("STATUS_03D"));
													roleTempMap.put("STATUS_03E", (BigDecimal) branchMap.get("STATUS_03E"));
													roleTempMap.put("STATUS_03F", (BigDecimal) branchMap.get("STATUS_03F"));
													roleTempMap.put("STATUS_03G", (BigDecimal) branchMap.get("STATUS_03G"));
													roleTempMap.put("STATUS_03H", (BigDecimal) branchMap.get("STATUS_03H"));
													roleTempMap.put("STATUS_03I", (BigDecimal) branchMap.get("STATUS_03I"));
													roleTempMap.put("STATUS_03J", (BigDecimal) branchMap.get("STATUS_03J"));
													roleTempMap.put("STATUS_03K", (BigDecimal) branchMap.get("STATUS_03K"));
													roleTempMap.put("STATUS_03ALL", (BigDecimal) branchMap.get("STATUS_03ALL"));
													
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
											
												roleTempMap.put("STATUS_ALL", (BigDecimal) branchMap.get("STATUS_ALL"));
												roleTempMap.put("STATUS_01", (BigDecimal) branchMap.get("STATUS_01"));
												roleTempMap.put("STATUS_99", (BigDecimal) branchMap.get("STATUS_99"));
												roleTempMap.put("STATUS_03B", (BigDecimal) branchMap.get("STATUS_03B"));
												
												roleTempMap.put("STATUS_02A", (BigDecimal) branchMap.get("STATUS_02A"));
												roleTempMap.put("STATUS_02B", (BigDecimal) branchMap.get("STATUS_02B"));
												roleTempMap.put("STATUS_02", (BigDecimal) branchMap.get("STATUS_02"));
												roleTempMap.put("STATUS_02ALL", (BigDecimal) branchMap.get("STATUS_02ALL"));
												
												roleTempMap.put("STATUS_03A", (BigDecimal) branchMap.get("STATUS_03A"));
												roleTempMap.put("STATUS_03D", (BigDecimal) branchMap.get("STATUS_03D"));
												roleTempMap.put("STATUS_03E", (BigDecimal) branchMap.get("STATUS_03E"));
												roleTempMap.put("STATUS_03F", (BigDecimal) branchMap.get("STATUS_03F"));
												roleTempMap.put("STATUS_03G", (BigDecimal) branchMap.get("STATUS_03G"));
												roleTempMap.put("STATUS_03H", (BigDecimal) branchMap.get("STATUS_03H"));
												roleTempMap.put("STATUS_03I", (BigDecimal) branchMap.get("STATUS_03I"));
												roleTempMap.put("STATUS_03J", (BigDecimal) branchMap.get("STATUS_03J"));
												roleTempMap.put("STATUS_03K", (BigDecimal) branchMap.get("STATUS_03K"));
												roleTempMap.put("STATUS_03ALL", (BigDecimal) branchMap.get("STATUS_03ALL"));
												
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
		
		PMS112InputVO inputVO = (PMS112InputVO) body;
		PMS112OutputVO outputVO = new PMS112OutputVO();
		
		String[] headerLine1 = {"業務處", "營運區", "分行別", "分別名稱", "組別", 
							    "名單數", "未處理", "成功進件", "約訪收件", 
							    "追蹤聯絡中", "追蹤聯絡中", "追蹤聯絡中", "追蹤聯絡中", 
							    "未成功名單", "未成功名單", "未成功名單", "未成功名單", "未成功名單", "未成功名單", "未成功名單", "未成功名單", "未成功名單", "未成功名單"};
		
		String[] headerLine2 = {"", "", "", "", "", 
								"", "", "", "",
								"第一次聯絡不上，再聯絡", "第二次聯絡不上，再聯絡", "處理中", "合計",  
								"連續三次聯絡不上", "聯絡電話不完整/錯誤", "非本行目標/可承作客群", "利費率不符需求", "貸款金額/擔保品初估額度不符需求", "其他授信條件不符需求", "擔保品屬本行不承作類型/區域", "已有其他業務人員聯繫/名單重複", "無資金需求", "合計"};
		
		String[] mainLine    = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "AREA_GROUP",
								"STATUS_ALL", "STATUS_01", "STATUS_99", "STATUS_03B", 
								"STATUS_02A", "STATUS_02B", "STATUS_02", "STATUS_02ALL", 
								"STATUS_03A", "STATUS_03D", "STATUS_03E", "STATUS_03F", "STATUS_03G", "STATUS_03H", "STATUS_03I", "STATUS_03J", "STATUS_03K", "STATUS_03ALL"}; 
				
		StringBuffer fileName = new StringBuffer();
		fileName.append("已聯繫狀態表_");
		
		if (StringUtils.isNotBlank(inputVO.getLeadType())) {
			XmlInfo xmlInfo = new XmlInfo();
			String leadTypeName = (String) xmlInfo.getVariable("PMS.PIPE_CONTENT_LEAD_TYPE", inputVO.getLeadType(), "F3");
			
			fileName.append(leadTypeName).append("_");
		} else {
			fileName.append("留資房信貸總名單_");
		}
		
		fileName.append(inputVO.getsDate());
		if (StringUtils.isNotBlank(inputVO.geteDate()) && !StringUtils.equals(inputVO.getsDate(), inputVO.geteDate())) {
			fileName.append("-" + inputVO.geteDate());
		}
		
		fileName.append(".xlsx");

		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("留資名單執行統計表");
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
