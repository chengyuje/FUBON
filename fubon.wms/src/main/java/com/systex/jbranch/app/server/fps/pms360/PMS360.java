package com.systex.jbranch.app.server.fps.pms360;

import java.text.NumberFormat;
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
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :專員生產力每日戰報<br>
 * Comments Name : PMS360java<br>
 * Author : Frank<br>
 * Date :2016/11/10 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms360")
@Scope("request")
public class PMS360 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS360InputVO inputVO = (PMS360InputVO) body;
		PMS360OutputVO outputVO = new PMS360OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		StringBuffer sql = new StringBuffer();
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		try{			
			sql.append(" WITH RPT_DATE AS ( ");
			sql.append("   SELECT MAX(DATA_DATE) AS DATA_DATE FROM TBPMS_AO_DAY_PROFIT_MYTD WHERE SUBSTR(DATA_DATE,1,6) = SUBSTR(:sDate,1,6) ) , ");
			sql.append(" RTO AS ( ");
			sql.append("   SELECT TO_NUMBER(INV_RATE) AS p_INV_RATE ");
			sql.append("        , TO_NUMBER(INS_RATE) AS p_INS_RATE ");
			sql.append("        , TO_NUMBER(EXG_RATE) AS p_EXG_RATE ");
			sql.append("   FROM ( ");
			sql.append("     SELECT PARAM_CODE, PARAM_DESC ");
			sql.append("     FROM TBSYSPARAMETER ");
			sql.append("     WHERE PARAM_TYPE='PMS.TARGET_RATE' ) ");
			sql.append("   PIVOT (SUM(PARAM_DESC) FOR PARAM_CODE IN ( ");
			sql.append("     'INV' AS INV_RATE, ");
			sql.append("     'INS' AS INS_RATE, ");
			sql.append("     'EXG' AS EXG_RATE ) ) ) , ");
			sql.append(" ALL_YEAR_DTL AS ( ");
			sql.append("   SELECT AO.DATA_DATE ");
			sql.append("        , AO.REGION_CENTER_ID , AO.REGION_CENTER_NAME, AO.BRANCH_AREA_ID, AO.BRANCH_AREA_NAME ");
			sql.append("        , AO.BRANCH_NBR, AO.BRANCH_NAME, AO.GROUP_TYPE ");
			sql.append("        , AO.EMP_ID, AO.EMP_NAME, AO.AO_CODE, AO.AO_JOB_TITLE ");
			sql.append("        , AO.MTD_INV_FEE, NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INV_RATE*AO.MTD_INV_RATE_MM  AS MTD_INV_GOAL ");
			sql.append("        , DECODE(NVL(AOTAR.TAR_AMOUNT,0),0,0,(AO.MTD_INV_FEE/(NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INV_RATE*AO.MTD_INV_RATE_MM)*100)) AS MTD_INV_RATE ");
			sql.append("        , AO.MTD_INS_FEE, NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INS_RATE*AO.MTD_INS_RATE_MM  AS MTD_INS_GOAL ");
			sql.append("        , DECODE(NVL(AOTAR.TAR_AMOUNT,0),0,0,(AO.MTD_INS_FEE/(NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INS_RATE*AO.MTD_INS_RATE_MM)*100)) AS MTD_INS_RATE ");
			sql.append("        , AO.MTD_EXG_FEE, NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_EXG_RATE*AO.MTD_INV_RATE_MM  AS MTD_EXG_GOAL ");
			sql.append("        , DECODE(NVL(AOTAR.TAR_AMOUNT,0),0,0,(AO.MTD_EXG_FEE/(NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_EXG_RATE*AO.MTD_INV_RATE_MM)*100)) AS MTD_EXG_RATE ");
			sql.append("        , AO.MTD_SUM_FEE ");
			sql.append("        , ( NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INV_RATE*AO.MTD_INV_RATE_MM + ");
			sql.append("            NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INS_RATE*AO.MTD_INS_RATE_MM + ");
			sql.append("            NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_EXG_RATE*AO.MTD_INV_RATE_MM ) AS MTD_SUM_GOAL ");
			sql.append("        , DECODE(NVL(AOTAR.TAR_AMOUNT,0),0,0,(AO.MTD_SUM_FEE / ");
			sql.append("          ( NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INV_RATE*AO.MTD_INV_RATE_MM + ");
			sql.append("            NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INS_RATE*AO.MTD_INS_RATE_MM + ");
			sql.append("            NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_EXG_RATE*AO.MTD_INV_RATE_MM )*100)) AS MTD_SUM_RATE ");
			sql.append("        , AO.MTD_SUM_RATE_MM, AO.YTD_SUM_FEE, AO.YTD_SUM_RATE_YY ");
			sql.append("    FROM TBPMS_AO_DAY_PROFIT_MYTD AO ");
			sql.append("    LEFT JOIN RTO ON 1 = 1 ");
			sql.append("    LEFT JOIN TBPMS_EMP_PRD_TAR_M AOTAR ");
			sql.append("      ON AO.EMP_ID = AOTAR.EMP_ID ");
			sql.append("     AND AOTAR.DATA_YEARMON = SUBSTR(AO.DATA_DATE, 1, 6) ");
			sql.append("    WHERE AO.DATA_DATE <= (SELECT DATA_DATE FROM RPT_DATE) ");
			sql.append("      AND AO.DATA_DATE IN ( ");
			sql.append("         SELECT MAX(DATA_DATE) ");
			sql.append("         FROM TBPMS_AO_DAY_PROFIT_MYTD ");
			sql.append("         WHERE SUBSTR(DATA_DATE,1,4) = SUBSTR(:sDate, 1, 4) ");
			sql.append("         GROUP BY SUBSTR(DATA_DATE,1,6) ) ) ");
			sql.append(" SELECT RTN.DATA_DATE ");
			sql.append("      , RTN.REGION_CENTER_ID, RTN.REGION_CENTER_NAME, RTN.BRANCH_AREA_ID, RTN.BRANCH_AREA_NAME ");
			sql.append("      , RTN.BRANCH_NBR, RTN.BRANCH_NAME, RTN.GROUP_TYPE  ");
			sql.append("      , RTN.EMP_ID, RTN.EMP_NAME, RTN.AO_CODE, RTN.AO_JOB_TITLE ");
			sql.append("      , RTN.MTD_INV_FEE, RTN.MTD_INV_GOAL, ROUND(RTN.MTD_INV_RATE,2) AS MTD_INV_RATE ");
			sql.append("      , RTN.MTD_INS_FEE, RTN.MTD_INS_GOAL, ROUND(RTN.MTD_INS_RATE,2) AS MTD_INS_RATE ");
			sql.append("      , RTN.MTD_EXG_FEE, RTN.MTD_EXG_GOAL, ROUND(RTN.MTD_EXG_RATE,2) AS MTD_EXG_RATE ");
			sql.append("      , RTN.MTD_SUM_FEE, RTN.MTD_SUM_GOAL, ROUND(RTN.MTD_SUM_RATE,2) AS MTD_SUM_RATE ");
			sql.append("      , CASE WHEN RTN.MTD_SUM_RATE >= RTN.MTD_SUM_RATE_MM*100 THEN 'Y' ELSE 'N' END AS MTD_GOAL_FLAG ");
			sql.append("      , RTN.YTD_SUM_FEE, YRS.YTD_SUM_GOAL, DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)*100) AS YTD_SUM_RATE ");
			sql.append("      , CASE WHEN DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) >= RTN.YTD_SUM_RATE_YY THEN 'Y' ELSE 'N' END AS YTD_GOAL_FLAG ");
			sql.append("      , RANK() OVER(PARTITION BY RTN.AO_JOB_TITLE ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_ALL_BY_JOB ");
			sql.append("      , RANK() OVER(PARTITION BY RTN.AO_JOB_TITLE ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_ALL_BY_JOB ");
			sql.append(" FROM ALL_YEAR_DTL RTN ");
			sql.append(" LEFT JOIN ( ");
			sql.append("     SELECT EMP_ID, SUM(MTD_SUM_GOAL) AS YTD_SUM_GOAL ");
			sql.append("     FROM ALL_YEAR_DTL RTN ");
			sql.append("     GROUP BY EMP_ID ) YRS ");
			sql.append("   ON RTN.EMP_ID = YRS.EMP_ID ");
			sql.append(" WHERE RTN.DATA_DATE = (SELECT DATA_DATE FROM RPT_DATE) ");
			
			//如果員編有值，查詢條件只取員編；如果員編空白，分行有值，查詢條件只取分行
			//員編
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" and RTN.EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}else{
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and RTN.BRANCH_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}else{
				//登入非總行人員強制加分行
					if(!headmgrMap.containsKey(roleID)) {
						sql.append(" and RTN.BRANCH_NBR IN (:branch_nbr) ");
						condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
					}
					// 區域中心
					if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
						sql.append(" and RTN.REGION_CENTER_ID = :region_center_id ");
						condition.setObject("region_center_id", inputVO.getRegion_center_id());
					}else{
					//登入非總行人員強制加區域中心
						if(!headmgrMap.containsKey(roleID)) {
							sql.append(" and RTN.REGION_CENTER_ID IN (:region_center_id) ");
							condition.setObject("region_center_id", pms000outputVO.getV_regionList());
						}
					}					
					// 營運區
					if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
						sql.append(" and RTN.BRANCH_AREA_ID = :branch_area_id ");
						condition.setObject("branch_area_id", inputVO.getBranch_area_id());
					}else{
					//登入非總行人員強制加營運區
						if(!headmgrMap.containsKey(roleID)) {
							sql.append(" and RTN.BRANCH_AREA_ID IN (:branch_area_id) ");
							condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
						}
					}
					
				}
			}
							
			if (StringUtils.isNotBlank(inputVO.getReportDate())) {
				condition.setObject("sDate", inputVO.getReportDate());
			}
			
			sql.append(" order by RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR, RTN.EMP_ID ");
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			outputVO.setTotalList(dam.exeQuery(condition));
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
	
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* === 產出EXCEL==== */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PMS360OutputVO outputVO = (PMS360OutputVO) body;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "專員生產力每日戰報" + sdf.format(new Date())
				+ "-"+getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
		List<Map<String, Object>> list = outputVO.getTotalList();
		if (list.size() > 0) {
			String STATUSMTD = "";
			String STATUSYTD = "";
			for (Map<String, Object> data : list) {
				if ("Y".equals(data.get("MTD_GOAL_FLAG"))) {
					STATUSMTD = "已達成";
				} else {
					STATUSMTD = "未達成";
				}
				
				if ("Y".equals(data.get("YTD_GOAL_FLAG"))) {
					STATUSYTD = "已達成";
				} else {
					STATUSYTD = "未達成";
				}
				
				data.put("STATUSMTD", STATUSMTD);
				data.put("STATUSYTD", STATUSYTD);
				
			}

			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				String[] records = new String[28];
				int i = 0;
				records[i] = checkIsNull(map, "REGION_CENTER_NAME");
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
				records[++i] = checkIsNull(map, "BRANCH_NBR");
				records[++i] = checkIsNull(map, "BRANCH_NAME");
				records[++i] = checkIsNull(map, "EMP_NAME");
				records[++i] = checkIsNull(map, "EMP_ID");
				records[++i] = checkIsNull(map, "AO_JOB_TITLE");
				records[++i] = checkIsNull(map, "TOT_TAR_AMT_1");
				records[++i] = checkIsNull(map, "GROUP_TYPE");
				records[++i] = checkIsNull(map, "MTD_INV_FEE");
				records[++i] = checkIsNull(map, "MTD_INV_GOAL");
				records[++i] = checkIsNullRate(map, "MTD_INV_RATE");
				records[++i] = checkIsNull(map, "MTD_INS_FEE");
				records[++i] = checkIsNull(map, "MTD_INS_GOAL");
				records[++i] = checkIsNullRate(map, "MTD_INS_RATE");
				records[++i] = checkIsNull(map, "MTD_EXG_FEE");
				records[++i] = checkIsNull(map, "MTD_EXG_GOAL");
				records[++i] = checkIsNullRate(map, "MTD_EXG_RATE");
				records[++i] = checkIsNull(map, "MTD_SUM_FEE");
				records[++i] = String.valueOf(Float.parseFloat(checkIsNull(map, "MTD_INV_GOAL")) + Float.parseFloat(checkIsNull(map, "MTD_INS_GOAL")) + Float.parseFloat(checkIsNull(map, "MTD_EXG_GOAL")));
				records[++i] = checkIsNullRate(map, "MTD_SUM_RATE");
				records[++i] = checkIsNull(map, "STATUSMTD");
				records[++i] = checkIsNull2(map, "RANK_MTD_ALL_BY_JOB");
				records[++i] = checkIsNull(map, "YTD_SUM_FEE");
				records[++i] = checkIsNull(map, "YTD_SUM_GOAL");
				records[++i] = checkIsNullRate(map, "YTD_SUM_RATE");
				records[++i] = checkIsNull(map, "STATUSYTD");
				records[++i] = checkIsNull2(map, "RANK_YTD_ALL_BY_JOB");

				listCSV.add(records);
			}

			// header
			String[] csvHeader = new String[28];
			int j = 0;
			csvHeader[j] = "業務處";
			csvHeader[++j] = "營運區";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行名稱";
			csvHeader[++j] = "專員姓名";
			csvHeader[++j] = "員編";
			csvHeader[++j] = "目前職務別";
			csvHeader[++j] = "月目標";
			csvHeader[++j] = "SH_分組";
			csvHeader[++j] = "MTD投資_手收";
			csvHeader[++j] = "MTD投資_目標";
			csvHeader[++j] = "MTD投資_達成率";
			csvHeader[++j] = "MTD保險_手收";
			csvHeader[++j] = "MTD保險_目標";
			csvHeader[++j] = "MTD保險_達成率";
			csvHeader[++j] = "MTD匯兌_手收";
			csvHeader[++j] = "MTD匯兌_目標";
			csvHeader[++j] = "MTD匯兌_達成率";
			csvHeader[++j] = "MTD合計_手收";
			csvHeader[++j] = "MTD合計_目標";
			csvHeader[++j] = "MTD合計_達成率";
			csvHeader[++j] = "MTD合計_進度達成情形";
			csvHeader[++j] = "MTD合計_依專員別排名";
			csvHeader[++j] = "YTD合計_手收";
			csvHeader[++j] = "YTD合計_目標";
			csvHeader[++j] = "YTD合計_達成率";
			csvHeader[++j] = "YTD合計_進度達成情形";
			csvHeader[++j] = "YTD合計_依專員別排名";

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader); // 設定標頭
			csv.addRecordList(listCSV); // 設定內容
			String url = csv.generateCSV();
			// download csv
			notifyClientToDownloadFile(url, fileName);
		} else {
			this.sendRtnObject(null);
		}
	}
	
	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	private String checkIsNull2(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "1";
		}
	}


	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));
		} else
			return "0.00";
	}

	// 達成率格式
	private String checkIsNullRate(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))&& map.get(key) != null) {
			return map.get(key) + "%";
		} else
			return "0%";
	}
}