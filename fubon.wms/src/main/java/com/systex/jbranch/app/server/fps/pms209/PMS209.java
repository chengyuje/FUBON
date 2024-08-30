package com.systex.jbranch.app.server.fps.pms209;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.org420.ORG420InputVO;
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

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :競爭力趨勢 <br>
 * Comments Name : pms209.java<br>
 * Author :Kevin<br>
 * Date :2016年06月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2016年02月01日<br>
 */

@Component("pms209")
@Scope("request")
public class PMS209 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS209.class);

	/*
	 * 查詢（主表）
	 * 
	 * 2017-02-23 MDOIFY BY OCEAN
	 * 
	 */
	public void queryData (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS209InputVO inputVO = (PMS209InputVO) body;
		PMS209OutputVO outputVO = new PMS209OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer yyyymmSb = new StringBuffer();
		yyyymmSb.append("SELECT YYYYMM ");
		yyyymmSb.append("FROM ( ");
		yyyymmSb.append("  SELECT TO_CHAR(TO_DATE(:startDate, 'yyyyMM') + (level - 1), 'yyyyMM') AS YYYYMM ");
		yyyymmSb.append("  FROM DUAL ");
		yyyymmSb.append("  CONNECT BY TRUNC(TO_DATE(:startDate, 'yyyyMM')) + level - 1 <= TRUNC(TO_DATE(:endDate, 'yyyyMM')) ");
		yyyymmSb.append(") ");
		yyyymmSb.append("GROUP BY YYYYMM ");
		yyyymmSb.append("ORDER BY YYYYMM ");
		queryCondition.setObject("startDate", inputVO.getsTime());
		queryCondition.setObject("endDate", inputVO.geteTime());
		queryCondition.setQueryString(yyyymmSb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String yyyyMMStr = "";
		for (int i = 0; i < list.size(); i++) {
			yyyyMMStr = yyyyMMStr + list.get(i).get("YYYYMM");
			if ((i + 1) != list.size()) {
				yyyyMMStr = yyyyMMStr + ",";
			}
		}
		
		outputVO.setTITLE(list);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("WITH ");
		sb.append("REGION AS ( ");
		sb.append("  SELECT AR.REGION_CENTER_ID, AR.REGION_CENTER_NAME, BR.BRANCH_AREA_ID, BR.BRANCH_AREA_NAME, BR.BRANCH_NBR, BR.BRANCH_NAME ");
		sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", EMP.EMP_ID, EMP.EMP_NAME, EMP.AO_JOB_RANK, AO.AO_CODE " : ""));
		sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", PROD.PTYPE, PROD.PRD_ID, PROD.PNAME " : ""));
		sb.append("  FROM ( ");
		sb.append("    SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_NBR, BRANCH_CLS ");
		sb.append("    FROM TBPMS_ORG_REC_N ");
		sb.append("    WHERE LAST_DAY(TO_DATE(:endDate, 'YYYYMM')) BETWEEN START_TIME AND END_TIME ");
		sb.append("    AND ORG_TYPE = '50' ");
		sb.append("    AND DEPT_ID >= '200' ");
		sb.append("    AND DEPT_ID <= '900' ");
		sb.append("    AND LENGTH(DEPT_ID) = 3 ");
		sb.append("  ) AR ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT V_STRING1 AS BRANCH_AREA_ID, V_STRING2 AS BRANCH_AREA_NAME, V_STRING3 AS BRANCH_NBR, V_STRING4 AS BRANCH_NAME ");
		sb.append("    FROM TABLE(FN_GET_VRR_D('', LAST_DAY(TO_DATE(:endDate, 'YYYYMM')), :empid, :loginRole, '', '', '3')) ");
		sb.append("  ) BR ON AR.BRANCH_NBR = BR.BRANCH_NBR ");

		if (StringUtils.isBlank(inputVO.getAojob()) && StringUtils.isBlank(inputVO.getAo_code()) && StringUtils.isBlank(inputVO.getProdType()) && StringUtils.isBlank(inputVO.getProdID())) {

		} else {
			if ((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q())) {
				sb.append("  LEFT JOIN ( ");
				sb.append("    SELECT DISTINCT EMP_ID, EMP_NAME, DEPT_ID AS BRANCH_NBR, AO_JOB_RANK ");
				sb.append("    FROM TBPMS_EMPLOYEE_REC_N ");
				sb.append("    WHERE LAST_DAY(TO_DATE(:endDate, 'YYYYMM')) BETWEEN START_TIME AND END_TIME ");
				sb.append("  ) EMP ON EMP.BRANCH_NBR = BR.BRANCH_NBR AND EMP.AO_JOB_RANK IS NOT NULL ");
				sb.append("  LEFT JOIN ( ");
				sb.append("    SELECT DISTINCT EMP_ID, AO_CODE ");
				sb.append("    FROM TBPMS_SALES_AOCODE_REC ");
				sb.append("    WHERE LAST_DAY(TO_DATE(:endDate, 'YYYYMM')) BETWEEN START_TIME AND END_TIME ");
				sb.append("  ) AO ON AO.EMP_ID = EMP.EMP_ID ");
			}
			
			
			if ((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q())) {
				sb.append("  LEFT JOIN ( ");
				sb.append("    SELECT DISTINCT PNAME, PRD_ID, PTYPE ");
				sb.append("    FROM VWPRD_MASTER ");
				sb.append("    WHERE PNAME IS NOT NULL ");
				sb.append("  ) PROD ON 1 = 1 ");
			}
		}
		
		sb.append("  WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append("AND BR.BRANCH_NBR = :branch_nbr ");
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sb.append("AND BR.BRANCH_AREA_ID = :branch_area_id ");
			queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		}

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sb.append("AND AR.REGION_CENTER_ID = :region_center_id ");
			queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		}
		
		sb.append("), ");
		sb.append("YM_LIST AS ( ");
		sb.append(yyyymmSb);
		sb.append("), ");
				
		switch (Integer.valueOf(inputVO.getType_Q())) {
			case 1 : //收益
			case 2 : //銷量
				sb.append(Integer.valueOf(inputVO.getType_Q()) == 1 ? "FEE_LIST AS ( " : "BAL_LIST AS ( ");
				sb.append("  SELECT SUBSTR(DATA_DATE, 1, 6) AS YYYYMM, BRANCH_NBR");
				sb.append(Integer.valueOf(inputVO.getType_Q()) == 1 ? ", NVL(SUM(FEE), 0) AS FEE " : ", NVL(SUM(BAL), 0) AS BAL ");
				sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", EMP_ID, AO_CODE, AO_JOB_RANK " : ""));
				sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", PRD_ID " : ""));

				if (StringUtils.isBlank(inputVO.getAojob()) && StringUtils.isBlank(inputVO.getAo_code()) && StringUtils.isBlank(inputVO.getProdType()) && StringUtils.isBlank(inputVO.getProdID())) {
					sb.append("  FROM TBPMS_BR_DAY_PROFIT PRO ");
					sb.append("  WHERE PRO.ITEM = '92' ");
				} else {
					if (((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && StringUtils.isBlank(inputVO.getProdType())) && StringUtils.isBlank(inputVO.getProdID()) && !StringUtils.equals("3", inputVO.getType_Q())){
						sb.append("  FROM TBPMS_AO_DAY_PROFIT PRO ");
						sb.append("  WHERE PRO.ITEM = '92' ");
					} else if ((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q())){
						sb.append("  FROM TBPMS_AO_PRD_PROFIT PRO ");
						sb.append("  WHERE 1 = 1 ");
					}
				}
				
				sb.append("  GROUP BY SUBSTR(DATA_DATE, 1, 6), BRANCH_NBR ");
				sb.append((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code()) ? ", EMP_ID, AO_CODE, AO_JOB_RANK " : ""));
				sb.append((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID()) ? ", PRD_ID " : ""));
				sb.append(") ");

				//查詢
				sb.append("SELECT * ");
				sb.append("FROM ( ");
				sb.append("  SELECT R.REGION_CENTER_ID, R.REGION_CENTER_NAME, R.BRANCH_AREA_ID, R.BRANCH_AREA_NAME, R.BRANCH_NBR, R.BRANCH_NAME, Y.YYYYMM ");
				sb.append(Integer.valueOf(inputVO.getType_Q()) == 1 ? ", NVL(F.FEE, 0) AS FEE " : ", NVL(F.BAL, 0) AS BAL ");
				sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", R.EMP_ID, R.EMP_NAME, R.AO_JOB_RANK, R.AO_CODE " : ""));
				sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", R.PTYPE, R.PRD_ID, R.PNAME " : ""));
				sb.append("  FROM REGION R ");
				sb.append("  LEFT JOIN YM_LIST Y ON 1 = 1 ");
				sb.append("  LEFT JOIN ").append(Integer.valueOf(inputVO.getType_Q()) == 1 ? "FEE_LIST" : "BAL_LIST").append(" F ON Y.YYYYMM = F.YYYYMM AND R.BRANCH_NBR = F.BRANCH_NBR ");
				sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? "AND R.EMP_ID = F.EMP_ID " : ""));
				sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? "AND R.PRD_ID = F.PRD_ID " : ""));
				sb.append("WHERE 1 = 1 ");
				
				
				if (StringUtils.isNotBlank(inputVO.getAojob())) {
					sb.append("AND R.AO_JOB_RANK = :aoJob ");
					queryCondition.setObject("aoJob", inputVO.getAojob());
				}

				if (StringUtils.isNotBlank(inputVO.getAo_code())) {
					sb.append("AND R.AO_CODE = :aoCode ");
					queryCondition.setObject("aoCode", inputVO.getAo_code());
				}
				
				if (StringUtils.isNotBlank(inputVO.getProdType())) {
					sb.append("AND R.PTYPE = :pType ");
					queryCondition.setObject("pType", inputVO.getProdType());
				}

				if (StringUtils.isNotBlank(inputVO.getProdID())) {
					sb.append("AND R.PRD_ID = :prodID ");
					queryCondition.setObject("prodID", inputVO.getProdID());
				}
				sb.append("ORDER BY R.REGION_CENTER_ID, R.REGION_CENTER_NAME, R.BRANCH_AREA_ID, R.BRANCH_AREA_NAME, R.BRANCH_NBR, R.BRANCH_NAME, Y.YYYYMM ");
				sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", R.EMP_ID, R.EMP_NAME, R.AO_JOB_RANK, R.AO_CODE " : ""));
				sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", R.PTYPE, R.PRD_ID, R.PNAME " : ""));

				sb.append(") PIVOT (SUM(").append(Integer.valueOf(inputVO.getType_Q()) == 1 ? "FEE" : "BAL").append(") FOR (YYYYMM) IN (").append(yyyyMMStr).append(")) "); 

				break;
			case 3: //達成率
				sb.append("RATE_LIST AS ( ");
				sb.append("  SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, BRANCH_NBR, TOT_A_RATE_1 ");
				sb.append("  FROM TBPMS_BR_ACHD_RATE ");
				sb.append("  WHERE DATA_DATE IN (SELECT MAX(DATA_DATE) AS MONTH_MAX_DAY ");
				sb.append("                      FROM TBPMS_BR_ACHD_RATE ");
				sb.append("                      GROUP BY SUBSTR(DATA_DATE, 0 ,6)) ");
				sb.append("  AND SUM_TYPE = 'M' ");
				sb.append(") ");
				
				//查詢
				sb.append("SELECT * ");
				sb.append("FROM ( ");
				sb.append("  SELECT R.REGION_CENTER_ID, R.REGION_CENTER_NAME, R.BRANCH_AREA_ID, R.BRANCH_AREA_NAME, R.BRANCH_NBR, R.BRANCH_NAME, Y.YYYYMM, NVL(F.TOT_A_RATE_1, 0) AS TOT_A_RATE_1 ");
				sb.append("  FROM REGION R ");
				sb.append("  LEFT JOIN RATE_LIST Y ON 1 = 1 ");
				sb.append("  LEFT JOIN RATE_LIST F ON Y.YYYYMM = F.YYYYMM AND R.BRANCH_NBR = F.BRANCH_NBR ");
				sb.append("  WHERE 1 = 1 ");

				sb.append(") PIVOT (SUM(TOT_A_RATE_1) FOR (YYYYMM) IN (").append(yyyyMMStr).append(")) "); 

				break;
		}
		
		queryCondition.setObject("empid", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("loginRole", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setObject("startDate", inputVO.getsTime());
		queryCondition.setObject("endDate", inputVO.geteTime());
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> finalList = dam.exeQuery(queryCondition);
		
		outputVO.setDATA(finalList);
		
		this.sendRtnObject(outputVO);
	}
	
//	private void setCondition(StringBuffer view,QueryConditionIF condition,PMS209InputVO inputVO) throws JBranchException, ParseException {
//		
////		String roleType = "";
//		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
//		XmlInfo xmlInfo = new XmlInfo();
//		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
//		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); //OP
//		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);//個金主管
//		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運督導
//		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //區域中心主管
//		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
//		
//		//取得查詢資料可視範圍
//		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
//		PMS000InputVO pms000InputVO = new PMS000InputVO();
//		pms000InputVO.setReportDate(inputVO.getsTime());
//		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
//		
//		// AO_COCE
//		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
//			view.append("and AO_CODE = :ao_code ");
//			condition.setObject("ao_code", inputVO.getAo_code());
//		}else{
//			//登入為銷售人員強制加AO_CODE
//			if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
//				view.append("and AO_CODE IN (:ao_code) ");
//				condition.setObject("ao_code", pms000outputVO.getV_aoList());
//			}
//		}
//		// 分行
//		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
//			view.append("and V.BRANCH_NBR = :branch_nbr ");
//			condition.setObject("branch_nbr", inputVO.getBranch_nbr());
//		}else{
//			//登入非總行人員強制加分行
//			if(!headmgrMap.containsKey(roleID)) {
//				view.append("and V.BRANCH_NBR IN (:branch_nbr) ");
//				condition.setObject("branch_nbr", pms000outputVO.getBranchList());
//			}
//		}
//		// 營運區
//		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
//			view.append("and V.BRANCH_AREA_ID = :branch_area_id ");
//			condition.setObject("branch_area_id", inputVO.getBranch_area_id());
//		}else{
//			//登入非總行人員強制加營運區
//			if(!headmgrMap.containsKey(roleID)) {
//				view.append("and V.BRANCH_AREA_ID IN (:branch_area_id) ");
//				condition.setObject("branch_area_id", pms000outputVO.getArea_branchList());
//			}
//		}
//		// 區域中心
//		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
//			view.append("and V.REGION_CENTER_ID = :region_center_id ");
//			condition.setObject("region_center_id", inputVO.getRegion_center_id());
//		}else{
//			//登入非總行人員強制加區域中心
//			if(!headmgrMap.containsKey(roleID)) {
//				view.append("and V.REGION_CENTER_ID IN (:region_center_id) ");
//				condition.setObject("region_center_id", pms000outputVO.getRegionList());
//			}
//		}	
//	}
	
	/*
	 * 查詢商品主檔及年月下拉選單
	 * 
	 */
	public void initLoad(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS209InputVO inputVO = (PMS209InputVO) body;
		PMS209OutputVO outputVO = new PMS209OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF conditionview = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer view = new StringBuffer();
		view.append("SELECT DISTINCT PNAME, PRD_ID, PTYPE ");
		view.append("FROM VWPRD_MASTER ");
		view.append("WHERE PNAME IS NOT NULL ");
		
		conditionview.setQueryString(view.toString());
		List<Map<String, Object>> prodList = dam.exeQuery(conditionview);
		
		outputVO.setProdList(prodList);
		outputVO.setYmList(PMS000.getLastYMlist());
		
		this.sendRtnObject(outputVO);
	}

	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
		PMS209InputVO inputVO = (PMS209InputVO) body;

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> prodType = xmlInfo.doGetVariable("FPS.PROD_TYPE", FormatHelper.FORMAT_3);

		List<String> csvHeader = new ArrayList<String>();
		List<String> csvHeaderKey = new ArrayList<String>();
		csvHeader.add("區域中心");
		csvHeader.add("營運區");
		csvHeader.add("分行別");
		csvHeaderKey.add("REGION_CENTER_NAME");
		csvHeaderKey.add("BRANCH_AREA_NAME");
		csvHeaderKey.add("BRANCH_NAME");
		if (StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code()) && !StringUtils.equals("3", inputVO.getType_Q())) {
			csvHeader.add("職級");
			csvHeader.add("理專");
			csvHeaderKey.add("AO_JOB_RANK");
			csvHeaderKey.add("EMP_NAME");
		}
		if (StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID()) && !StringUtils.equals("3", inputVO.getType_Q())) {
			csvHeader.add("商品類型");
			csvHeader.add("產品名稱");
			csvHeaderKey.add("PTYPE");
			csvHeaderKey.add("PNAME");
		}
		List<Map<String, Object>> titleList = inputVO.getTitleList();
		for (Map<String, Object> map : titleList) {
			csvHeader.add((String) map.get("YYYYMM") + (StringUtils.equals("3", inputVO.getType_Q()) ? " (%)" : ""));
			csvHeaderKey.add((String) map.get("YYYYMM"));
		}
		
		List<Map<String, Object>> csvMain = inputVO.getParamList();
		if (csvMain.size() > 0) {
			String fileName = "競爭力趨勢(" + (StringUtils.equals("1", inputVO.getType_Q()) ? "收益" : StringUtils.equals("2", inputVO.getType_Q()) ? "銷量" : "達成率") + ")_" + sdfYYYYMMDD.format(new Date()) + ".csv";

			List contectCSV = new ArrayList();
			for (Map<String, Object> map: csvMain) {
				String[] records = new String[map.size()];
				
				for (int i = 0; i < csvHeaderKey.size(); i++) {
					if (map.get(csvHeaderKey.get(i)) instanceof String) {
						if (StringUtils.equals("BRANCH_NAME", csvHeaderKey.get(i))) {
							records[i] = map.get("BRANCH_NBR") + "-" + checkIsNull(map, csvHeaderKey.get(i));
						} else if (StringUtils.equals("EMP_NAME", csvHeaderKey.get(i))) {
							records[i] = map.get("AO_CODE") + "-" + checkIsNull(map, csvHeaderKey.get(i));
						} else if (StringUtils.equals("PNAME", csvHeaderKey.get(i))) {
							records[i] = map.get("PRD_ID") + "-" + checkIsNull(map, csvHeaderKey.get(i));
						} else if (StringUtils.equals("PTYPE", csvHeaderKey.get(i))){
							records[i] = prodType.get(map.get("PTYPE"));
						} else {
							records[i] = checkIsNull(map, csvHeaderKey.get(i));
						}
					} else {
						records[i] = null != map.get(csvHeaderKey.get(i)) ? String.valueOf(map.get(csvHeaderKey.get(i))) : "";
					}
				}
				
				contectCSV.add(records);
			}
			
			CSVUtil csv = new CSVUtil();			
			csv.setHeader((String[]) csvHeader.toArray(new String[0]));  
			csv.addRecordList(contectCSV);
			String url = csv.generateCSV();
			
			// download
			notifyClientToDownloadFile(url, fileName);
		}
		
		sendRtnObject(null);
	}
	
	/**
	 * 匯出判斷NULL
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * =趨勢圖=
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryImage(Object body, IPrimitiveMap header)
			throws JBranchException {
		
		PMS209InputVO inputVO = (PMS209InputVO) body;
		PMS209OutputVO outputVO = new PMS209OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer yyyymmSb = new StringBuffer();
		yyyymmSb.append("SELECT YYYYMM ");
		yyyymmSb.append("FROM ( ");
		yyyymmSb.append("  SELECT TO_CHAR(TO_DATE(:startDate, 'yyyyMM') + (level - 1), 'yyyyMM') AS YYYYMM ");
		yyyymmSb.append("  FROM DUAL ");
		yyyymmSb.append("  CONNECT BY TRUNC(TO_DATE(:startDate, 'yyyyMM')) + level - 1 <= TRUNC(TO_DATE(:endDate, 'yyyyMM')) ");
		yyyymmSb.append(") ");
		yyyymmSb.append("GROUP BY YYYYMM ");
		yyyymmSb.append("ORDER BY YYYYMM ");
		queryCondition.setObject("startDate", inputVO.getsTime());
		queryCondition.setObject("endDate", inputVO.geteTime());
		queryCondition.setQueryString(yyyymmSb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String yyyyMMStr = "";
		for (int i = 0; i < list.size(); i++) {
			yyyyMMStr = yyyyMMStr + list.get(i).get("YYYYMM");
			if ((i + 1) != list.size()) {
				yyyyMMStr = yyyyMMStr + ",";
			}
		}
		
		outputVO.setTITLE(list);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("WITH ");
		sb.append("REGION AS ( ");
		sb.append("  SELECT AR.REGION_CENTER_ID, AR.REGION_CENTER_NAME, BR.BRANCH_AREA_ID, BR.BRANCH_AREA_NAME, BR.BRANCH_NBR, BR.BRANCH_NAME ");
		sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", EMP.EMP_ID, EMP.EMP_NAME, EMP.AO_JOB_RANK, AO.AO_CODE " : ""));
		sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", PROD.PTYPE, PROD.PRD_ID, PROD.PNAME " : ""));
		sb.append("  FROM ( ");
		sb.append("    SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_NBR, BRANCH_CLS ");
		sb.append("    FROM TBPMS_ORG_REC_N ");
		sb.append("    WHERE LAST_DAY(TO_DATE(:endDate, 'YYYYMM')) BETWEEN START_TIME AND END_TIME ");
		sb.append("    AND ORG_TYPE = '50' ");
		sb.append("    AND DEPT_ID >= '200' ");
		sb.append("    AND DEPT_ID <= '900' ");
		sb.append("    AND LENGTH(DEPT_ID) = 3 ");
		sb.append("  ) AR ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT V_STRING1 AS BRANCH_AREA_ID, V_STRING2 AS BRANCH_AREA_NAME, V_STRING3 AS BRANCH_NBR, V_STRING4 AS BRANCH_NAME ");
		sb.append("    FROM TABLE(FN_GET_VRR_D('', LAST_DAY(TO_DATE(:endDate, 'YYYYMM')), :empid, :loginRole, '', '', '3')) ");
		sb.append("  ) BR ON AR.BRANCH_NBR = BR.BRANCH_NBR ");

		if (StringUtils.isBlank(inputVO.getAojob()) && StringUtils.isBlank(inputVO.getAo_code()) && StringUtils.isBlank(inputVO.getProdType()) && StringUtils.isBlank(inputVO.getProdID())) {

		} else {
			if ((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q())) {
				sb.append("  LEFT JOIN ( ");
				sb.append("    SELECT DISTINCT EMP_ID, EMP_NAME, DEPT_ID AS BRANCH_NBR, AO_JOB_RANK ");
				sb.append("    FROM TBPMS_EMPLOYEE_REC_N ");
				sb.append("    WHERE LAST_DAY(TO_DATE(:endDate, 'YYYYMM')) BETWEEN START_TIME AND END_TIME ");
				sb.append("  ) EMP ON EMP.BRANCH_NBR = BR.BRANCH_NBR AND EMP.AO_JOB_RANK IS NOT NULL ");
				sb.append("  LEFT JOIN ( ");
				sb.append("    SELECT DISTINCT EMP_ID, AO_CODE ");
				sb.append("    FROM TBPMS_SALES_AOCODE_REC ");
				sb.append("    WHERE LAST_DAY(TO_DATE(:endDate, 'YYYYMM')) BETWEEN START_TIME AND END_TIME ");
				sb.append("  ) AO ON AO.EMP_ID = EMP.EMP_ID ");
			}
			
			
			if ((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q())) {
				sb.append("  LEFT JOIN ( ");
				sb.append("    SELECT DISTINCT PNAME, PRD_ID, PTYPE ");
				sb.append("    FROM VWPRD_MASTER ");
				sb.append("    WHERE PNAME IS NOT NULL ");
				sb.append("  ) PROD ON 1 = 1 ");
			}
		}
		
		sb.append("  WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append("AND BR.BRANCH_NBR = :branch_nbr ");
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sb.append("AND BR.BRANCH_AREA_ID = :branch_area_id ");
			queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		}

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sb.append("AND AR.REGION_CENTER_ID = :region_center_id ");
			queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		}
		
		sb.append("), ");
		sb.append("YM_LIST AS ( ");
		sb.append(yyyymmSb);
		sb.append("), ");
				
		switch (Integer.valueOf(inputVO.getType_Q())) {
			case 1 : //收益
			case 2 : //銷量
				sb.append(Integer.valueOf(inputVO.getType_Q()) == 1 ? "FEE_LIST AS ( " : "BAL_LIST AS ( ");
				sb.append("  SELECT SUBSTR(DATA_DATE, 1, 6) AS YYYYMM, BRANCH_NBR");
				sb.append(Integer.valueOf(inputVO.getType_Q()) == 1 ? ", NVL(SUM(FEE), 0) AS FEE " : ", NVL(SUM(BAL), 0) AS BAL ");
				sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", EMP_ID, AO_CODE, AO_JOB_RANK " : ""));
				sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", PRD_ID " : ""));

				if (StringUtils.isBlank(inputVO.getAojob()) && StringUtils.isBlank(inputVO.getAo_code()) && StringUtils.isBlank(inputVO.getProdType()) && StringUtils.isBlank(inputVO.getProdID())) {
					sb.append("  FROM TBPMS_BR_DAY_PROFIT PRO ");
					sb.append("  WHERE PRO.ITEM = '92' ");
				} else {
					if (((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && StringUtils.isBlank(inputVO.getProdType())) && StringUtils.isBlank(inputVO.getProdID()) && !StringUtils.equals("3", inputVO.getType_Q())){
						sb.append("  FROM TBPMS_AO_DAY_PROFIT PRO ");
						sb.append("  WHERE PRO.ITEM = '92' ");
					} else if ((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q())){
						sb.append("  FROM TBPMS_AO_PRD_PROFIT PRO ");
						sb.append("  WHERE 1 = 1 ");
					}
				}
				
				sb.append("  GROUP BY SUBSTR(DATA_DATE, 1, 6), BRANCH_NBR ");
				sb.append((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code()) ? ", EMP_ID, AO_CODE, AO_JOB_RANK " : ""));
				sb.append((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID()) ? ", PRD_ID " : ""));
				sb.append(") ");

				//查詢
				sb.append("SELECT * ");
				sb.append("FROM ( ");
				sb.append("  SELECT R.REGION_CENTER_ID, R.REGION_CENTER_NAME, R.BRANCH_AREA_ID, R.BRANCH_AREA_NAME, R.BRANCH_NBR, R.BRANCH_NAME, Y.YYYYMM ");
				sb.append(Integer.valueOf(inputVO.getType_Q()) == 1 ? ", NVL(F.FEE, 0) AS FEE " : ", NVL(F.BAL, 0) AS BAL ");
				sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", R.EMP_ID, R.EMP_NAME, R.AO_JOB_RANK, R.AO_CODE " : ""));
				sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", R.PTYPE, R.PRD_ID, R.PNAME " : ""));
				sb.append("  FROM REGION R ");
				sb.append("  LEFT JOIN YM_LIST Y ON 1 = 1 ");
				sb.append("  LEFT JOIN ").append(Integer.valueOf(inputVO.getType_Q()) == 1 ? "FEE_LIST" : "BAL_LIST").append(" F ON Y.YYYYMM = F.YYYYMM AND R.BRANCH_NBR = F.BRANCH_NBR ");
				sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? "AND R.EMP_ID = F.EMP_ID " : ""));
				sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? "AND R.PRD_ID = F.PRD_ID " : ""));
				sb.append("WHERE 1 = 1 ");
				
				
				if (StringUtils.isNotBlank(inputVO.getAojob())) {
					sb.append("AND R.AO_JOB_RANK = :aoJob ");
					queryCondition.setObject("aoJob", inputVO.getAojob());
				}

				if (StringUtils.isNotBlank(inputVO.getAo_code())) {
					sb.append("AND R.AO_CODE = :aoCode ");
					queryCondition.setObject("aoCode", inputVO.getAo_code());
				}
				
				if (StringUtils.isNotBlank(inputVO.getProdType())) {
					sb.append("AND R.PTYPE = :pType ");
					queryCondition.setObject("pType", inputVO.getProdType());
				}

				if (StringUtils.isNotBlank(inputVO.getProdID())) {
					sb.append("AND R.PRD_ID = :prodID ");
					queryCondition.setObject("prodID", inputVO.getProdID());
				}
				sb.append("ORDER BY R.REGION_CENTER_ID, R.REGION_CENTER_NAME, R.BRANCH_AREA_ID, R.BRANCH_AREA_NAME, R.BRANCH_NBR, R.BRANCH_NAME, Y.YYYYMM ");
				sb.append(((StringUtils.isNotBlank(inputVO.getAojob()) || StringUtils.isNotBlank(inputVO.getAo_code())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", R.EMP_ID, R.EMP_NAME, R.AO_JOB_RANK, R.AO_CODE " : ""));
				sb.append(((StringUtils.isNotBlank(inputVO.getProdType()) || StringUtils.isNotBlank(inputVO.getProdID())) && !StringUtils.equals("3", inputVO.getType_Q()) ? ", R.PTYPE, R.PRD_ID, R.PNAME " : ""));

				sb.append(") PIVOT (SUM(").append(Integer.valueOf(inputVO.getType_Q()) == 1 ? "FEE" : "BAL").append(") FOR (YYYYMM) IN (").append(yyyyMMStr).append(")) "); 

				break;
			case 3: //達成率
				sb.append("RATE_LIST AS ( ");
				sb.append("  SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, BRANCH_NBR, TOT_A_RATE_1 ");
				sb.append("  FROM TBPMS_BR_ACHD_RATE ");
				sb.append("  WHERE DATA_DATE IN (SELECT MAX(DATA_DATE) AS MONTH_MAX_DAY ");
				sb.append("                      FROM TBPMS_BR_ACHD_RATE ");
				sb.append("                      GROUP BY SUBSTR(DATA_DATE, 0 ,6)) ");
				sb.append("  AND SUM_TYPE = 'M' ");
				sb.append(") ");
				
				//查詢
				sb.append("SELECT * ");
				sb.append("FROM ( ");
				sb.append("  SELECT R.REGION_CENTER_ID, R.REGION_CENTER_NAME, R.BRANCH_AREA_ID, R.BRANCH_AREA_NAME, R.BRANCH_NBR, R.BRANCH_NAME, Y.YYYYMM, NVL(F.TOT_A_RATE_1, 0) AS TOT_A_RATE_1 ");
				sb.append("  FROM REGION R ");
				sb.append("  LEFT JOIN RATE_LIST Y ON 1 = 1 ");
				sb.append("  LEFT JOIN RATE_LIST F ON Y.YYYYMM = F.YYYYMM AND R.BRANCH_NBR = F.BRANCH_NBR ");
				sb.append("  WHERE 1 = 1 ");

				sb.append(") PIVOT (SUM(TOT_A_RATE_1) FOR (YYYYMM) IN (").append(yyyyMMStr).append(")) "); 

				break;
		}
		
		queryCondition.setObject("empid", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("loginRole", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setObject("startDate", inputVO.getsTime());
		queryCondition.setObject("endDate", inputVO.geteTime());
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> finalList = dam.exeQuery(queryCondition);
		
		outputVO.setResultList(finalList);
		
		this.sendRtnObject(outputVO);
	}

}
