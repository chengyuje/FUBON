package com.systex.jbranch.app.server.fps.crm3121;

import java.sql.Timestamp;
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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/08/29
 * 
 */
@Component("crm3121")
@Scope("request")
public class CRM3121 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM3121.class);
	
	public void ao_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3121InputVO inputVO = (CRM3121InputVO) body;
		CRM3121OutputVO return_VO = new CRM3121OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT AO_CODE, BRANCH_NBR, EMP_NAME, EMP_ID, AO_JOB_RANK FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sql.append("WHERE AO_CODE IS NOT NULL ");
//		sql.append("AND BRANCH_NBR IN (:brNbrList)  ");
//		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
			sql.append(" AND REGION_CENTER_ID = :centerID ");
			queryCondition.setObject("centerID", inputVO.getRegion_center_id());
		}
		if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
			sql.append(" AND BRANCH_AREA_ID = :areaID ");
			queryCondition.setObject("areaID", inputVO.getBranch_area_id());
		}
		if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
			sql.append(" AND BRANCH_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		}
		sql.append(" ORDER BY AO_CODE ");
		queryCondition.setQueryString(sql.toString());

		List list = dam.exeQuery(queryCondition);
		return_VO.setAo_list(list);
		sendRtnObject(return_VO);
	}
	
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3121InputVO inputVO = (CRM3121InputVO) body;
		CRM3121OutputVO return_VO = new CRM3121OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT  ");
		sql.append("E.REGION_CENTER_NAME,  ");    	/*業務處*/
		sql.append("E.BRANCH_AREA_NAME,  ");		/*營運區*/
		sql.append("E.BRANCH_NBR,    ");            /*分行代碼*/
		sql.append("E.BRANCH_NAME,   ");            /*分行名稱*/
		sql.append("E.AO_CODE,   ");                /*理專AOCode*/
		sql.append("E.EMP_NAME,  ");                /*理專名稱*/
		sql.append("E.AO_CODE||'-'||E.EMP_NAME AS NEW_AO_NAME,");/*AOCode-理專名稱*/
		if (inputVO.getLimit_s_date() != null)
			sql.append(":start AS START_DATE, ");
		if (inputVO.getLimit_e_date() != null)
			sql.append(":end AS END_DATE, ");
		// E級客戶完成數
		sql.append("SUM(CASE WHEN C.CON_DEGREE = 'E' AND ( ");
		sql.append("L.CHG_DATE IS NULL AND  ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) ");
		sql.append(") OR ");
		sql.append("(L.CHG_DATE IS NOT NULL AND ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) ");
		sql.append(") THEN 1 ELSE 0 END) AS E_CMP_CNT, ");
		// E級客戶總數
		sql.append("SUM(CASE WHEN C.CON_DEGREE = 'E' THEN 1 ELSE 0 END) AS E_TTL_CNT, ");
		// I級客戶完成數
		sql.append("SUM(CASE WHEN C.CON_DEGREE = 'I' AND ( ");
		sql.append("L.CHG_DATE IS NULL AND  ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) ");
		sql.append(") OR ");
		sql.append("(L.CHG_DATE IS NOT NULL AND ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) ");
		sql.append(") THEN 1 ELSE 0 END) AS I_CMP_CNT, ");
		// I級客戶總數
		sql.append("SUM(CASE WHEN C.CON_DEGREE = 'I' THEN 1 ELSE 0 END) AS I_TTL_CNT, ");
		// P級客戶完成數
		sql.append("SUM(CASE WHEN C.CON_DEGREE = 'P' AND ( ");
		sql.append("L.CHG_DATE IS NULL AND  ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) ");
		sql.append(") OR ");
		sql.append("(L.CHG_DATE IS NOT NULL AND ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) ");
		sql.append(") THEN 1 ELSE 0 END) AS P_CMP_CNT, ");
		// P級客戶總數
		sql.append("SUM(CASE WHEN C.CON_DEGREE = 'P' THEN 1 ELSE 0 END) AS P_TTL_CNT, ");
		// O級客戶完成數
		sql.append("SUM(CASE WHEN C.CON_DEGREE = 'O' AND ( ");
		sql.append("L.CHG_DATE IS NULL AND  ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) ");
		sql.append(") OR ");
		sql.append("(L.CHG_DATE IS NOT NULL AND ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) ");
		sql.append(") THEN 1 ELSE 0 END) AS O_CMP_CNT, ");
		// O級客戶總數
		sql.append("SUM(CASE WHEN C.CON_DEGREE = 'O' THEN 1 ELSE 0 END) AS O_TTL_CNT ");

		sql.append("FROM  ");
		sql.append("TBCRM_CUST_AOCODE_CHGLOG L  ");
		sql.append("LEFT JOIN TBCRM_CUST_VISIT_RECORD R1 ON  ");
		sql.append("L.CUST_ID = R1.CUST_ID AND R1.CMU_TYPE = 'B'  ");
		sql.append("AND TRUNC(R1.LASTUPDATE) >= TRUNC(L.LETGO_DATETIME)  ");
		sql.append("LEFT JOIN TBCRM_CUST_VISIT_RECORD R2 ON  ");
		sql.append("L.CUST_ID = R2.CUST_ID AND R2.CMU_TYPE = 'P' ");
		sql.append("AND TRUNC(R2.LASTUPDATE) >= TRUNC(L.LETGO_DATETIME), ");
		sql.append("TBCRM_CUST_MAST C, ");
		sql.append("VWORG_BRANCH_EMP_DETAIL_INFO E, ");
		sql.append("VWCRM_CUST_REVIEWDATE_MAP M ");
		sql.append("WHERE  L.CUST_ID = C.CUST_ID ");
		sql.append("AND  L.NEW_AO_CODE = E.AO_CODE ");
		sql.append("AND  C.CON_DEGREE = M.CON_DEGREE ");
		sql.append("AND  C.VIP_DEGREE = M.VIP_DEGREE ");
		sql.append("AND  C.CON_DEGREE IN ('E','I','P','O') ");
		
		if(!StringUtils.isBlank(inputVO.getNew_ao_code())){
			sql.append("AND E.AO_CODE = :new_ao_code ");
			queryCondition.setObject("new_ao_code", inputVO.getNew_ao_code());
		}else{
			if(!StringUtils.isBlank(inputVO.getBranch_nbr())){
				sql.append("AND E.BRANCH_NBR = :branch_nbr ");
				queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				if(!StringUtils.isBlank(inputVO.getBranch_area_id())){
					sql.append("AND E.BRANCH_AREA_ID = :branch_area_id ");
					queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
				}else{
					if(!StringUtils.isBlank(inputVO.getRegion_center_id())){
						sql.append("AND E.REGION_CENTER_ID = :region_center_id ");
						queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
					}
				}
			}
		}
		
		//如果有選擇起迄時間
		if (inputVO.getLimit_s_date() != null) {
			sql.append("AND TRUNC(L.LETGO_DATETIME) + M.FRQ_DAY >= :start ");
			queryCondition.setObject("start", new Timestamp(inputVO.getLimit_s_date().getTime()));
		}
		if (inputVO.getLimit_e_date() != null) {
			sql.append("AND TRUNC(L.LETGO_DATETIME) + M.FRQ_DAY <= :end ");
			queryCondition.setObject("end", new Timestamp(inputVO.getLimit_e_date().getTime()));
		}
		sql.append("GROUP BY ");
		sql.append("E.REGION_CENTER_NAME, E.BRANCH_AREA_NAME, E.BRANCH_NBR, E.BRANCH_NAME, E.AO_CODE, E.EMP_NAME ");
		sql.append("ORDER BY ");
		sql.append("E.REGION_CENTER_NAME, E.BRANCH_AREA_NAME, E.BRANCH_NBR ");
		
		// 2017/10/31 esorter會錯 加這個
		sql.append("ORDER BY NULL");
		
		queryCondition.setQueryString(sql.toString());
				
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());   //分頁用executePaging，一次10筆
		int totalPage_i = list.getTotalPage();                        	  // 分頁用
		int totalRecord_i = list.getTotalRecord();                    	  // 分頁用
		return_VO.setResultList(list);                           		  // 查詢結果
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());     // 當前頁次
		return_VO.setTotalPage(totalPage_i);                              // 總頁次
		return_VO.setTotalRecord(totalRecord_i);                          // 總筆數
		
		this.sendRtnObject(return_VO);
		
		
	}
	
	public void detail(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3121InputVO inputVO = (CRM3121InputVO) body;
		CRM3121OutputVO return_VO = new CRM3121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT C.CUST_NAME, C.CUST_ID, E.AO_CODE, E.EMP_NAME, ");
		sql.append("CASE WHEN L.CHG_DATE IS NULL THEN TO_CHAR(L.LETGO_DATETIME,'YYYY/MM/DD') ELSE TO_CHAR(L.CHG_DATE,'YYYY/MM/DD') END AS LETGO_DATETIME, ");
		sql.append("TO_CHAR(TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0),'YYYY/MM/DD')AS LAST_REVIEW_DATE, C.CON_DEGREE ");
		sql.append("FROM TBCRM_CUST_AOCODE_CHGLOG L ");
		sql.append("LEFT JOIN TBCRM_CUST_VISIT_RECORD R1 ON L.CUST_ID = R1.CUST_ID AND R1.CMU_TYPE = 'B' AND TRUNC(R1.CREATETIME) >= TRUNC(L.LETGO_DATETIME) ");
		sql.append("LEFT JOIN TBCRM_CUST_VISIT_RECORD R2 ON L.CUST_ID = R2.CUST_ID AND R2.CMU_TYPE = 'P' AND TRUNC(R2.CREATETIME) >= TRUNC(L.LETGO_DATETIME), ");
		sql.append("TBCRM_CUST_MAST C, VWORG_BRANCH_EMP_DETAIL_INFO E, VWCRM_CUST_REVIEWDATE_MAP M ");
		sql.append("WHERE L.CUST_ID = C.CUST_ID ");
		sql.append("AND L.NEW_AO_CODE = E.AO_CODE ");
		sql.append("AND C.CON_DEGREE = M.CON_DEGREE AND C.VIP_DEGREE = M.VIP_DEGREE ");
		sql.append("AND C.CON_DEGREE IN ('E','I','P','O') ");
		sql.append("AND NOT ( ");
		sql.append("(L.CHG_DATE IS NULL AND ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.LETGO_DATETIME)+NVL(M.FRQ_DAY,0) ");
		sql.append(") OR ");
		sql.append("(L.CHG_DATE IS NOT NULL AND ");
		sql.append("TRUNC(NVL(R1.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) AND ");
		sql.append("TRUNC(NVL(R2.CREATETIME,TO_DATE('99991231','YYYYMMDD'))) <= TRUNC(L.CHG_DATE)+NVL(M.FRQ_DAY,0) ");
		sql.append(")) ");
		sql.append("AND E.REGION_CENTER_NAME = :region_center_id ");
		sql.append("AND E.BRANCH_AREA_NAME = :branch_area_id ");
		sql.append("AND E.BRANCH_NBR = :branch_nbr ");
		sql.append("AND E.AO_CODE = :new_ao_code ");
		
		if (inputVO.getLimit_s_date() != null) {
			sql.append("AND TRUNC(L.LETGO_DATETIME) + M.FRQ_DAY >= :start ");
			queryCondition.setObject("start", new Timestamp(inputVO.getLimit_s_date().getTime()));
		}
		if (inputVO.getLimit_e_date() != null) {
			sql.append("AND TRUNC(L.LETGO_DATETIME) + M.FRQ_DAY <= :end ");
			queryCondition.setObject("end", new Timestamp(inputVO.getLimit_e_date().getTime()));
		}
		sql.append("ORDER BY DECODE(C.CON_DEGREE, 'E', 1, 'I', 2, 'P', 3, 'O', 4, 5) ");
		
		queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		queryCondition.setObject("new_ao_code", inputVO.getNew_ao_code());
//		queryCondition.setObject("start", new Timestamp(inputVO.getLimit_s_date().getTime()));
//		queryCondition.setObject("end", new Timestamp(inputVO.getLimit_e_date().getTime()));
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList2(list);
		sendRtnObject(return_VO);
	}
	
	public void exportfile(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3121InputVO inputVO = (CRM3121InputVO) body;
		
		// gen csv
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "總行理專管理追蹤報表_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		
		for (Map<String, Object> map : inputVO.getList()) {
				// 7 column
			String[] records = new String[7];
			int i = 0;
			System.out.println("LETGO_DATETIME == " + checkIsNull(map, "LAST_REVIEW_DATE"));
			records[i] = String.valueOf(i+1);
			records[++i] = checkIsNull(map, "CUST_NAME");
			records[++i] = checkIsNull(map, "CUST_ID");
			records[++i] = checkIsNull(map, "AO_CODE")+ "-" +checkIsNull(map, "EMP_NAME");
			records[++i] = checkIsNull(map, "LETGO_DATETIME");
			records[++i] = checkIsNull(map, "LAST_REVIEW_DATE");
			records[++i] = checkIsNull(map, "CON_DEGREE");
			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[7];
		int j = 0;
		csvHeader[j] = "項次";
		csvHeader[++j] = "客戶姓名";
		csvHeader[++j] = "客戶ID";
		csvHeader[++j] = "AO_CODE";
		csvHeader[++j] = "異動日期";
		csvHeader[++j] = "限辦日期";
		csvHeader[++j] = "等級";
			
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);  
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		// download
		notifyClientToDownloadFile(url, fileName);
		
		this.sendRtnObject(null);
	}
			
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key)))){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}

	
}