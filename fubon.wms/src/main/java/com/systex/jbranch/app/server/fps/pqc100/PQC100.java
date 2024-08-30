package com.systex.jbranch.app.server.fps.pqc100;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import de.schlichtherle.io.FileInputStream;

@Component("pqc100")
@Scope("request")
public class PQC100 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();
	
	public PQC100() {
		headColumnMap.put("回報開始日(YYYYMMDD)", "START_DATE");
		headColumnMap.put("回報結束日(YYYYMMDD)", "END_DATE");
		headColumnMap.put("產品種類", "PRD_TYPE");
		headColumnMap.put("產品代號", "PRD_ID");
		headColumnMap.put("產品名稱", "PRD_NAME");
		headColumnMap.put("幣別", "CURRENCY");
	}
	
	public void getActivePrd(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC100InputVO inputVO = (PQC100InputVO) body;
		PQC100OutputVO outputVO = new PQC100OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT DISTINCT PRD_TYPE, PRD_ID AS DATA, PRD_ID || '-' || PRD_NAME AS LABEL ");
		sb.append("FROM TBPQC_QUOTA_PRD ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(inputVO.getPrdType())) {
			sb.append("AND PRD_TYPE = :prdType ");
			queryCondition.setObject("prdType", inputVO.getPrdType());
		}
		
		sb.append("AND TO_DATE(START_DATE, 'yyyyMMdd') <= TRUNC(SYSDATE) ");
		sb.append("AND TO_DATE(END_DATE, 'yyyyMMdd') >= TRUNC(SYSDATE) ");
	
		sb.append("ORDER BY PRD_TYPE, PRD_ID ");
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setActivePrdList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void getPrdList(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC100InputVO inputVO = (PQC100InputVO) body;
		PQC100OutputVO outputVO = new PQC100OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT TO_DATE(START_DATE, 'yyyyMMdd') AS START_DATE, ");
		sb.append("       TO_DATE(END_DATE, 'yyyyMMdd') AS END_DATE, ");
		sb.append("       START_DATE AS SEARCH_START_DATE, ");
		sb.append("       END_DATE AS SEARCH_END_DATE, ");
		sb.append("       PRD_TYPE, ");
		sb.append("       PRD_ID, ");
		sb.append("       PRD_NAME, ");
		sb.append("       CURRENCY ");
		sb.append("FROM TBPQC_QUOTA_PRD ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(inputVO.getPrdType())) {
			sb.append("AND PRD_TYPE = :prdType ");
			queryCondition.setObject("prdType", inputVO.getPrdType());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getPrdID())) {
			sb.append("AND PRD_ID = :prdID ");
			queryCondition.setObject("prdID", inputVO.getPrdID());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getReportType())) {
			switch (inputVO.getReportType()) {
				case "O":
					sb.append("AND TO_DATE(START_DATE, 'yyyyMMdd') <= TRUNC(SYSDATE) ");
					sb.append("AND TO_DATE(END_DATE, 'yyyyMMdd') >= TRUNC(SYSDATE) ");
					break;
				case "C":
					sb.append("AND TO_DATE(END_DATE, 'yyyyMMdd') < TRUNC(SYSDATE) ");
					break;
			}
		}
	
		queryCondition.setQueryString(sb.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		outputVO.setTotalList(dam.exeQuery(queryCondition));
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		
		this.sendRtnObject(outputVO);
	}

	public void getExample(Object body, IPrimitiveMap header) throws Exception {
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(headColumnMap.keySet().toArray(new String[headColumnMap.keySet().size()]));
		
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "商品額度控管上傳.csv");
		
		this.sendRtnObject(null);
	}
	
	public void updPrdList(Object body, IPrimitiveMap header) throws Exception {
		
		PQC100InputVO inputVO = (PQC100InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "big5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, String>> inputLst = new ArrayList<Map<String,String>>();
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, String> dataMap = new HashMap<String, String>();
				
				for (int i = 0; i < data.length; i++) {
					dataMap.put(headColumnMap.get(head[i]), data[i]); //data[i]
				}
				
				inputLst.add(dataMap);
			}
			
			StringBuffer sb = new StringBuffer();
						
			for (Map<String, String> dataMap: inputLst) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT START_DATE, END_DATE, PRD_TYPE, PRD_ID, PRD_NAME, CURRENCY ");
				sb.append("FROM TBPQC_QUOTA_PRD ");
				sb.append("WHERE START_DATE = :startDate ");
				sb.append("AND END_DATE = :endDate ");
				sb.append("AND PRD_TYPE = :prdType ");
				sb.append("AND PRD_ID = :prdID ");
				
				queryCondition.setObject("startDate", dataMap.get("START_DATE"));
				queryCondition.setObject("endDate", dataMap.get("END_DATE"));
				queryCondition.setObject("prdType", dataMap.get("PRD_TYPE"));
				queryCondition.setObject("prdID", dataMap.get("PRD_ID"));
				
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				if (list.size() > 0) {
					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

					sb.append("UPDATE TBPQC_QUOTA_PRD ");
					sb.append("SET PRD_NAME = :prdName, "); 
					sb.append("    CURRENCY = :currency, "); 
					sb.append("    VERSION = VERSION + 1, ");
					sb.append("    MODIFIER = :empID, ");
					sb.append("    LASTUPDATE = SYSDATE ");
					sb.append("WHERE START_DATE = :startDate ");
					sb.append("AND END_DATE = :endDate ");
					sb.append("AND PRD_TYPE = :prdType ");
					sb.append("AND PRD_ID = :prdID ");
										
					// PK
					queryCondition.setObject("startDate", dataMap.get("START_DATE"));
					queryCondition.setObject("endDate", dataMap.get("END_DATE"));
					queryCondition.setObject("prdType", dataMap.get("PRD_TYPE"));
					queryCondition.setObject("prdID", dataMap.get("PRD_ID"));
					
					// condition
					queryCondition.setObject("prdName", dataMap.get("PRD_NAME"));
					queryCondition.setObject("currency", dataMap.get("CURRENCY"));
					queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));

					queryCondition.setQueryString(sb.toString());
					
					dam.exeUpdate(queryCondition);
				} else {
					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

					sb.append("INSERT INTO TBPQC_QUOTA_PRD ( ");
					sb.append("  START_DATE, ");
					sb.append("  END_DATE, ");
					sb.append("  PRD_TYPE, ");
					sb.append("  PRD_ID, ");
					sb.append("  PRD_NAME, ");
					sb.append("  CURRENCY, ");
					sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
					sb.append(") ");
					sb.append("VALUES ( ");
					sb.append("  :startDate, ");
					sb.append("  :endDate, ");
					sb.append("  :prdType, ");
					sb.append("  :prdID, ");
					sb.append("  :prdName, ");
					sb.append("  :currency, ");
					sb.append("  0, sysdate, :empID, :empID, sysdate ");
					sb.append(") ");
					
					// PK
					queryCondition.setObject("startDate", dataMap.get("START_DATE"));
					queryCondition.setObject("endDate", dataMap.get("END_DATE"));
					queryCondition.setObject("prdType", dataMap.get("PRD_TYPE"));
					queryCondition.setObject("prdID", dataMap.get("PRD_ID"));
					
					// condition
					queryCondition.setObject("prdName", dataMap.get("PRD_NAME"));
					queryCondition.setObject("currency", dataMap.get("CURRENCY"));
					queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
					
					queryCondition.setQueryString(sb.toString());
					
					dam.exeUpdate(queryCondition);
				}
			}
		}
		
		this.sendRtnObject(null);
	}
	
	public void delPrdQuota(Object body, IPrimitiveMap header) throws Exception {
		
		PQC100InputVO inputVO = (PQC100InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("SELECT COUNT(*) AS COUNTS ");
		sb.append("FROM TBPQC_QUOTA_APPLY_M ");
		sb.append("WHERE APPLY_PRD_S_DATE = :startDate ");
		sb.append("AND APPLY_PRD_E_DATE = :endDate ");
		sb.append("AND APPLY_PRD_TYPE = :prdType ");
		sb.append("AND APPLY_PRD_ID = :prdID ");
		
		queryCondition.setObject("startDate", inputVO.getDelStartDate());
		queryCondition.setObject("endDate", inputVO.getDelEndDate());
		queryCondition.setObject("prdType", inputVO.getDelPrdType());
		queryCondition.setObject("prdID", inputVO.getDelPrdID());
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (((BigDecimal) list.get(0).get("COUNTS")).compareTo(BigDecimal.ZERO) == 1) {
			throw new JBranchException("該產品無法刪除，已有人員申請額度。");	
		} else {
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			sb.append("DELETE FROM TBPQC_QUOTA_PRD ");
			sb.append("WHERE START_DATE = :startDate ");
			sb.append("AND END_DATE = :endDate ");
			sb.append("AND PRD_TYPE = :prdType ");
			sb.append("AND PRD_ID = :prdID ");
			
			// PK
			queryCondition.setObject("startDate", inputVO.getDelStartDate());
			queryCondition.setObject("endDate", inputVO.getDelEndDate());
			queryCondition.setObject("prdType", inputVO.getDelPrdType());
			queryCondition.setObject("prdID", inputVO.getDelPrdID());
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
		
		this.sendRtnObject(null);
	}
	
}