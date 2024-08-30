package com.systex.jbranch.app.server.fps.pms503;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms503")
@Scope("request")
public class PMS503 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	LinkedHashMap<String, String> TARGET = new LinkedHashMap<String, String>();

	public PMS503 () {
		// 分行銷量目標
		TARGET.put("年月", "YYYYMM");	
		TARGET.put("分行代碼", "BRANCH_NBR");
	}
	
	// 查詢
	public void query (Object body, IPrimitiveMap header) throws Exception {
		
		PMS503InputVO inputVO = (PMS503InputVO) body;
		PMS503OutputVO outputVO = new PMS503OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT * ");
		sb.append("FROM ( ");
				
		// 分行
		sb.append("  SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("         TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
		sb.append("         FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
		sb.append("         TO_NUMBER(FUND_S_S) + TO_NUMBER(FUND_BA_S) + TO_NUMBER(FUND_BN_S) + TO_NUMBER(FUND_CR_S) AS FUND_S, ");
		sb.append("         TO_NUMBER(FUND_S_RQ) + TO_NUMBER(FUND_BA_RQ) + TO_NUMBER(FUND_BN_RQ) + TO_NUMBER(FUND_CR_RQ) AS FUND_RQ, ");
		sb.append("         TO_NUMBER(STOCK) + TO_NUMBER(ETF) AS STOCK_ETF, ");
		sb.append("         BOND, ");
		sb.append("         TO_NUMBER(SI) + TO_NUMBER(SN) + TO_NUMBER(DCI) AS SI_SN_DCI, ");
		sb.append("         TRUST, ");
		sb.append("         OTHER, ");
		sb.append("         INS_INV, INS_SP, INS_STAG ");
		sb.append("  FROM TBPMS_EST_PRD_SALES_TARGET ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND BRANCH_NBR IS NOT NULL ");
		
		if (StringUtils.isNotEmpty(inputVO.getsCreDate())) {
			sb.append("  AND YYYYMM = :YYYYMM ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getRegion_center_id())) {
			sb.append("  AND REGION_CENTER_ID = :REGION_CENTER_ID ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranch_area_id())) {
			sb.append("  AND BRANCH_AREA_ID = :BRANCH_AREA_ID ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranch_nbr())) {
			sb.append("  AND BRANCH_NBR = :BRANCH_NBR ");
		}
		
		sb.append("  UNION ");
		
		// 營運區
		sb.append("  SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("         TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
		sb.append("         FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
		sb.append("         TO_NUMBER(FUND_S_S) + TO_NUMBER(FUND_BA_S) + TO_NUMBER(FUND_BN_S) + TO_NUMBER(FUND_CR_S) AS FUND_S, ");
		sb.append("         TO_NUMBER(FUND_S_RQ) + TO_NUMBER(FUND_BA_RQ) + TO_NUMBER(FUND_BN_RQ) + TO_NUMBER(FUND_CR_RQ) AS FUND_RQ, ");
		sb.append("         TO_NUMBER(STOCK) + TO_NUMBER(ETF) AS STOCK_ETF, ");
		sb.append("         BOND, ");
		sb.append("         TO_NUMBER(SI) + TO_NUMBER(SN) + TO_NUMBER(DCI) AS SI_SN_DCI, ");
		sb.append("         TRUST, ");
		sb.append("         OTHER, ");
		sb.append("         INS_INV, INS_SP, INS_STAG ");
		sb.append("  FROM TBPMS_EST_PRD_SALES_TARGET ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND BRANCH_AREA_ID IS NOT NULL ");
		
		if (StringUtils.isNotEmpty(inputVO.getsCreDate())) {
			sb.append("  AND YYYYMM = :YYYYMM ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getRegion_center_id())) {
			sb.append("  AND REGION_CENTER_ID = :REGION_CENTER_ID ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranch_area_id())) {
			sb.append("  AND BRANCH_AREA_ID = :BRANCH_AREA_ID ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranch_nbr())) {
			sb.append("  AND BRANCH_NBR IS NULL ");
		}
		
		sb.append("  UNION ");
		
		// 業務處
		sb.append("  SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("         TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
		sb.append("         FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
		sb.append("         TO_NUMBER(FUND_S_S) + TO_NUMBER(FUND_BA_S) + TO_NUMBER(FUND_BN_S) + TO_NUMBER(FUND_CR_S) AS FUND_S, ");
		sb.append("         TO_NUMBER(FUND_S_RQ) + TO_NUMBER(FUND_BA_RQ) + TO_NUMBER(FUND_BN_RQ) + TO_NUMBER(FUND_CR_RQ) AS FUND_RQ, ");
		sb.append("         TO_NUMBER(STOCK) + TO_NUMBER(ETF) AS STOCK_ETF, ");
		sb.append("         BOND, ");
		sb.append("         TO_NUMBER(SI) + TO_NUMBER(SN) + TO_NUMBER(DCI) AS SI_SN_DCI, ");
		sb.append("         TRUST, ");
		sb.append("         OTHER, ");
		sb.append("         INS_INV, INS_SP, INS_STAG ");
		sb.append("  FROM TBPMS_EST_PRD_SALES_TARGET ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND REGION_CENTER_ID IS NOT NULL ");
		sb.append("  AND REGION_CENTER_ID <> '000' ");
		
		if (StringUtils.isNotEmpty(inputVO.getsCreDate())) {
			sb.append("  AND YYYYMM = :YYYYMM ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getRegion_center_id())) {
			sb.append("  AND REGION_CENTER_ID = :REGION_CENTER_ID ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranch_area_id())) {
			sb.append("  AND BRANCH_AREA_ID IS NULL ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranch_nbr())) {
			sb.append("  AND BRANCH_NBR IS NULL ");
		}
		
		sb.append("  UNION ");
		
		// 全行
		sb.append("  SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("         TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
		sb.append("         FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
		sb.append("         TO_NUMBER(FUND_S_S) + TO_NUMBER(FUND_BA_S) + TO_NUMBER(FUND_BN_S) + TO_NUMBER(FUND_CR_S) AS FUND_S, ");
		sb.append("         TO_NUMBER(FUND_S_RQ) + TO_NUMBER(FUND_BA_RQ) + TO_NUMBER(FUND_BN_RQ) + TO_NUMBER(FUND_CR_RQ) AS FUND_RQ, ");
		sb.append("         TO_NUMBER(STOCK) + TO_NUMBER(ETF) AS STOCK_ETF, ");
		sb.append("         BOND, ");
		sb.append("         TO_NUMBER(SI) + TO_NUMBER(SN) + TO_NUMBER(DCI) AS SI_SN_DCI, ");
		sb.append("         TRUST, ");
		sb.append("         OTHER, ");
		sb.append("         INS_INV, INS_SP, INS_STAG ");
		sb.append("  FROM TBPMS_EST_PRD_SALES_TARGET ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND REGION_CENTER_ID IS NOT NULL ");
		sb.append("  AND REGION_CENTER_ID = '000' ");
		
		if (StringUtils.isNotEmpty(inputVO.getsCreDate())) {
			sb.append("  AND YYYYMM = :YYYYMM ");
		}
		
		sb.append(") ");
		
		sb.append("ORDER BY DECODE(REGION_CENTER_ID, '000', 999, 0), ");
		sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
		sb.append("         REGION_CENTER_NAME, ");
		sb.append("         DECODE(BRANCH_AREA_ID, NULL, 999, 0), ");
		sb.append("         BRANCH_AREA_NAME, ");
		sb.append("         DECODE(BRANCH_NBR, NULL, 999, 0), ");
		sb.append("         BRANCH_NAME ");
				
		if (StringUtils.isNotEmpty(inputVO.getsCreDate())) {
			queryCondition.setObject("YYYYMM", inputVO.getsCreDate());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getRegion_center_id())) {
			queryCondition.setObject("REGION_CENTER_ID", inputVO.getRegion_center_id());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranch_area_id())) {
			queryCondition.setObject("BRANCH_AREA_ID", inputVO.getBranch_area_id());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranch_nbr())) {
			queryCondition.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
		}
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}
	
	// 取得範例
	public void getExample(Object body, IPrimitiveMap header) throws JBranchException {
			
		CSVUtil csv = new CSVUtil();
		
		String fileName = "分行銷量目標.csv";
		
		ArrayList<String> csvHeader = new ArrayList<String>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT PARAM_CODE, PARAM_NAME ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND PARAM_TYPE = 'PMS.SALE_PLAN_PTYPE' ");
		sb.append("ORDER BY PARAM_ORDER ");
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> salePlanPtypeList = dam.exeQuery(queryCondition);
		
		if (salePlanPtypeList.size() > 0) {
			for (Entry<String, String> entry : TARGET.entrySet()) {
				csvHeader.add(entry.getKey());
			}
			
			for (Map<String, Object> map : salePlanPtypeList) {
				csvHeader.add(checkIsNull(map, "PARAM_NAME"));
			}
		}
		
		csv.setHeader(csvHeader.toArray(new String[0]));

		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		
		sendRtnObject(null);
	}
	
	// 上傳
	public void updFile(Object body, IPrimitiveMap header) throws Exception {
	
		WorkStation ws = DataManager.getWorkStation(uuid);

		PMS503InputVO inputVO = (PMS503InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// === GET PMS.SALE_PLAN_PTYPE START === //
		sb.append("SELECT PARAM_CODE, PARAM_NAME ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND PARAM_TYPE = 'PMS.SALE_PLAN_PTYPE' ");
		sb.append("ORDER BY PARAM_ORDER ");
		
		ArrayList<String> csvHeader = new ArrayList<String>();
		ArrayList<String> csvHeaderKey = new ArrayList<String>();
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> salePlanPtypeList = dam.exeQuery(queryCondition);
		
		if (salePlanPtypeList.size() > 0) {
			for (Entry<String, String> entry : TARGET.entrySet()) {
				csvHeader.add(entry.getKey());
				csvHeaderKey.add(entry.getValue());
			}
			
			for (Map<String, Object> map : salePlanPtypeList) {
				csvHeader.add(checkIsNull(map, "PARAM_NAME"));
				csvHeaderKey.add(checkIsNull(map, "PARAM_CODE"));
			}
		}
		// === GET PMS.SALE_PLAN_PTYPE END === //
		
		String[] otherArray = {"TOTAL_INV_INS", "TOTAL_INV", "TOTAL_INS", "BRANCH_NBR"};

		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			// 1. 讀檔
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, Object>> inputLst = new ArrayList<Map<String, Object>>();
			
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, Object> dataMap = new HashMap<String, Object>();

				for (int i = 0; i < data.length; i++) {
					dataMap.put(csvHeaderKey.get(i), data[i]);
				}
				
				for (int i = 0; i < otherArray.length; i++) {
					switch (otherArray[i]) {
						case "BRANCH_NBR":
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuffer();
							
							sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NAME ");
							sb.append("FROM VWORG_DEFN_INFO ");
							sb.append("WHERE 1 = 1 ");
							sb.append("AND BRANCH_NBR = :branchNBR ");
							
							queryCondition.setObject("branchNBR", dataMap.get(otherArray[i]));
							
							queryCondition.setQueryString(sb.toString());
							
							List<Map<String, Object>> defnList = dam.exeQuery(queryCondition);
							
							if (defnList.size() > 0) {
								dataMap.put("REGION_CENTER_ID", defnList.get(0).get("REGION_CENTER_ID"));
								dataMap.put("REGION_CENTER_NAME", defnList.get(0).get("REGION_CENTER_NAME"));
								dataMap.put("BRANCH_AREA_ID", defnList.get(0).get("BRANCH_AREA_ID"));
								dataMap.put("BRANCH_AREA_NAME", defnList.get(0).get("BRANCH_AREA_NAME"));
								dataMap.put("BRANCH_NAME", defnList.get(0).get("BRANCH_NAME"));
							}
							

							break;
						case "TOTAL_INV_INS":
							dataMap.put(otherArray[i], (new BigDecimal((String) dataMap.get("FUND_S_S"))
												   .add(new BigDecimal((String) dataMap.get("FUND_S_RQ")))
												   .add(new BigDecimal((String) dataMap.get("FUND_BA_S")))
												   .add(new BigDecimal((String) dataMap.get("FUND_BA_RQ")))
												   .add(new BigDecimal((String) dataMap.get("FUND_BN_S")))
												   .add(new BigDecimal((String) dataMap.get("FUND_BN_RQ")))
												   .add(new BigDecimal((String) dataMap.get("FUND_CR_S")))
												   .add(new BigDecimal((String) dataMap.get("FUND_CR_RQ")))
												   .add(new BigDecimal((String) dataMap.get("STOCK")))
												   .add(new BigDecimal((String) dataMap.get("ETF")))
												   .add(new BigDecimal((String) dataMap.get("BOND")))
												   .add(new BigDecimal((String) dataMap.get("SI")))
												   .add(new BigDecimal((String) dataMap.get("SN")))
												   .add(new BigDecimal((String) dataMap.get("DCI")))
												   .add(new BigDecimal((String) dataMap.get("TRUST")))
												   .add(new BigDecimal((String) dataMap.get("OTHER")))
												   .add(new BigDecimal((String) dataMap.get("INS_INV")))
												   .add(new BigDecimal((String) dataMap.get("INS_SP")))
												   .add(new BigDecimal((String) dataMap.get("INS_STAG")))));
							break;
						case "TOTAL_INV":
							dataMap.put(otherArray[i], (new BigDecimal((String) dataMap.get("FUND_S_S"))
												   .add(new BigDecimal((String) dataMap.get("FUND_S_RQ")))
												   .add(new BigDecimal((String) dataMap.get("FUND_BA_S")))
												   .add(new BigDecimal((String) dataMap.get("FUND_BA_RQ")))
												   .add(new BigDecimal((String) dataMap.get("FUND_BN_S")))
												   .add(new BigDecimal((String) dataMap.get("FUND_BN_RQ")))
												   .add(new BigDecimal((String) dataMap.get("FUND_CR_S")))
												   .add(new BigDecimal((String) dataMap.get("FUND_CR_RQ")))
												   .add(new BigDecimal((String) dataMap.get("STOCK")))
												   .add(new BigDecimal((String) dataMap.get("ETF")))
												   .add(new BigDecimal((String) dataMap.get("BOND")))
												   .add(new BigDecimal((String) dataMap.get("SI")))
												   .add(new BigDecimal((String) dataMap.get("SN")))
												   .add(new BigDecimal((String) dataMap.get("DCI")))
												   .add(new BigDecimal((String) dataMap.get("TRUST")))
												   .add(new BigDecimal((String) dataMap.get("OTHER")))));
							break;
						case "TOTAL_INS":
							dataMap.put(otherArray[i], (new BigDecimal((String) dataMap.get("INS_INV"))
												   .add(new BigDecimal((String) dataMap.get("INS_SP")))
												   .add(new BigDecimal((String) dataMap.get("INS_STAG")))));
							break;
					}
				}

				inputLst.add(dataMap);
			}
			
			// 2. 月檔清空
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("DELETE FROM TBPMS_EST_PRD_SALES_TARGET WHERE YYYYMM = :yyyymm ");
			
			queryCondition.setObject("yyyymm", (String) inputLst.get(0).get("YYYYMM"));
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
			
			// 3. 寫入
			for (Map<String, Object> dataMap : inputLst) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("INSERT INTO TBPMS_EST_PRD_SALES_TARGET ( ");
				sb.append("  YYYYMM, ");
				sb.append("  REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
				sb.append("  TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
				sb.append("  FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
				sb.append("  STOCK, ETF, BOND, SI, SN, DCI, TRUST, OTHER, ");
				sb.append("  INS_INV, INS_SP, INS_STAG, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  :YYYYMM, ");
				sb.append("  :REGION_CENTER_ID, :REGION_CENTER_NAME, :BRANCH_AREA_ID, :BRANCH_AREA_NAME, :BRANCH_NBR, :BRANCH_NAME, ");
				sb.append("  :TOTAL_INV_INS, :TOTAL_INV, :TOTAL_INS, ");
				sb.append("  :FUND_S_S, :FUND_S_RQ, :FUND_BA_S, :FUND_BA_RQ, :FUND_BN_S, :FUND_BN_RQ, :FUND_CR_S, :FUND_CR_RQ, ");
				sb.append("  :STOCK, :ETF, :BOND, :SI, :SN, :DCI, :TRUST, :OTHER, ");
				sb.append("  :INS_INV, :INS_SP, :INS_STAG, ");
				sb.append("  :VERSION, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
				sb.append(") ");
				
				queryCondition.setObject("YYYYMM"              , dataMap.get("YYYYMM"));
				queryCondition.setObject("REGION_CENTER_ID"    , dataMap.get("REGION_CENTER_ID"));
				queryCondition.setObject("REGION_CENTER_NAME"  , dataMap.get("REGION_CENTER_NAME"));
				queryCondition.setObject("BRANCH_AREA_ID"      , dataMap.get("BRANCH_AREA_ID"));
				queryCondition.setObject("BRANCH_AREA_NAME"    , dataMap.get("BRANCH_AREA_NAME"));
				queryCondition.setObject("BRANCH_NBR"          , dataMap.get("BRANCH_NBR"));
				queryCondition.setObject("BRANCH_NAME"         , dataMap.get("BRANCH_NAME"));
				queryCondition.setObject("TOTAL_INV_INS"       , String.valueOf(dataMap.get("TOTAL_INV_INS")));
				queryCondition.setObject("TOTAL_INV"           , String.valueOf(dataMap.get("TOTAL_INV")));
				queryCondition.setObject("TOTAL_INS"           , String.valueOf(dataMap.get("TOTAL_INS")));
				queryCondition.setObject("FUND_S_S"            , dataMap.get("FUND_S_S"));
				queryCondition.setObject("FUND_S_RQ"           , dataMap.get("FUND_S_RQ"));
				queryCondition.setObject("FUND_BA_S"           , dataMap.get("FUND_BA_S"));
				queryCondition.setObject("FUND_BA_RQ"          , dataMap.get("FUND_BA_RQ"));
				queryCondition.setObject("FUND_BN_S"           , dataMap.get("FUND_BN_S"));
				queryCondition.setObject("FUND_BN_RQ"          , dataMap.get("FUND_BN_RQ"));
				queryCondition.setObject("FUND_CR_S"           , dataMap.get("FUND_CR_S"));
				queryCondition.setObject("FUND_CR_RQ"          , dataMap.get("FUND_CR_RQ"));
				queryCondition.setObject("STOCK"               , dataMap.get("STOCK"));
				queryCondition.setObject("ETF"                 , dataMap.get("ETF"));
				queryCondition.setObject("BOND"                , dataMap.get("BOND"));
				queryCondition.setObject("SI"                  , dataMap.get("SI"));
				queryCondition.setObject("SN"                  , dataMap.get("SN"));
				queryCondition.setObject("DCI"                 , dataMap.get("DCI"));
				queryCondition.setObject("TRUST"               , dataMap.get("TRUST"));
				queryCondition.setObject("OTHER"               , dataMap.get("OTHER"));
				queryCondition.setObject("INS_INV"             , dataMap.get("INS_INV"));
				queryCondition.setObject("INS_SP"              , dataMap.get("INS_SP"));
				queryCondition.setObject("INS_STAG"            , dataMap.get("INS_STAG"));
				queryCondition.setObject("VERSION"             , 0);
				queryCondition.setObject("CREATOR"             , ws.getUser().getUserID());
				queryCondition.setObject("MODIFIER"            , ws.getUser().getUserID());
				
				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);
			}
			
			// 4. 寫入合計
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("INSERT INTO TBPMS_EST_PRD_SALES_TARGET ( ");
			sb.append("  YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sb.append("  TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
			sb.append("  FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
			sb.append("  STOCK, ETF, BOND, SI, SN, DCI, TRUST, OTHER, INS_INV, INS_SP, INS_STAG, ");
			sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
			sb.append(") ");
			sb.append("SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sb.append("       TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
			sb.append("       FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
			sb.append("       STOCK, ETF, BOND, SI, SN, DCI, TRUST, OTHER, INS_INV, INS_SP, INS_STAG, ");
			sb.append("       0 AS VERSION, SYSDATE AS CREATETIME, :CREATOR, :MODIFIER, SYSDATE AS LASTUPDATE ");
			sb.append("FROM ( ");
			sb.append("  SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
			sb.append("         SUM(TOTAL_INV_INS) AS TOTAL_INV_INS, SUM(TOTAL_INV) AS TOTAL_INV, SUM(TOTAL_INS) AS TOTAL_INS, ");
			sb.append("         SUM(FUND_S_S) AS FUND_S_S, SUM(FUND_S_RQ) AS FUND_S_RQ, SUM(FUND_BA_S) AS FUND_BA_S, SUM(FUND_BA_RQ) AS FUND_BA_RQ, SUM(FUND_BN_S) AS FUND_BN_S, SUM(FUND_BN_RQ) AS FUND_BN_RQ, SUM(FUND_CR_S) AS FUND_CR_S, SUM(FUND_CR_RQ) AS FUND_CR_RQ, ");
			sb.append("         SUM(STOCK) AS STOCK, SUM(ETF) AS ETF, SUM(BOND) AS BOND, SUM(SI) AS SI, SUM(SN) AS SN, SUM(DCI) AS DCI, SUM(TRUST) AS TRUST, SUM(OTHER) AS OTHER, SUM(INS_INV) AS INS_INV, SUM(INS_SP) AS INS_SP, SUM(INS_STAG) AS INS_STAG ");
			sb.append("  FROM ( ");
			sb.append("    SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sb.append("           TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
			sb.append("           FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
			sb.append("           STOCK, ETF, BOND, SI, SN, DCI, TRUST, OTHER, INS_INV, INS_SP, INS_STAG ");
			sb.append("    FROM TBPMS_EST_PRD_SALES_TARGET ");
			sb.append("    WHERE YYYYMM = :YYYYMM ");
			sb.append("  ) ");
			sb.append("  GROUP BY YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME ");
			sb.append("  UNION ");
			sb.append("  SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
			sb.append("         SUM(TOTAL_INV_INS) AS TOTAL_INV_INS, SUM(TOTAL_INV) AS TOTAL_INV, SUM(TOTAL_INS) AS TOTAL_INS, ");
			sb.append("         SUM(FUND_S_S) AS FUND_S_S, SUM(FUND_S_RQ) AS FUND_S_RQ, SUM(FUND_BA_S) AS FUND_BA_S, SUM(FUND_BA_RQ) AS FUND_BA_RQ, SUM(FUND_BN_S) AS FUND_BN_S, SUM(FUND_BN_RQ) AS FUND_BN_RQ, SUM(FUND_CR_S) AS FUND_CR_S, SUM(FUND_CR_RQ) AS FUND_CR_RQ, ");
			sb.append("         SUM(STOCK) AS STOCK, SUM(ETF) AS ETF, SUM(BOND) AS BOND, SUM(SI) AS SI, SUM(SN) AS SN, SUM(DCI) AS DCI, SUM(TRUST) AS TRUST, SUM(OTHER) AS OTHER, SUM(INS_INV) AS INS_INV, SUM(INS_SP) AS INS_SP, SUM(INS_STAG) AS INS_STAG ");
			sb.append("  FROM ( ");
			sb.append("    SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sb.append("           TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
			sb.append("           FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
			sb.append("           STOCK, ETF, BOND, SI, SN, DCI, TRUST, OTHER, INS_INV, INS_SP, INS_STAG ");
			sb.append("    FROM TBPMS_EST_PRD_SALES_TARGET ");
			sb.append("    WHERE YYYYMM = :YYYYMM ");
			sb.append("  ) ");
			sb.append("  GROUP BY YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME ");
			sb.append("  UNION ");
			sb.append("  SELECT YYYYMM, '000' AS REGION_CENTER_ID, '全行' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
			sb.append("         SUM(TOTAL_INV_INS) AS TOTAL_INV_INS, SUM(TOTAL_INV) AS TOTAL_INV, SUM(TOTAL_INS) AS TOTAL_INS, ");
			sb.append("         SUM(FUND_S_S) AS FUND_S_S, SUM(FUND_S_RQ) AS FUND_S_RQ, SUM(FUND_BA_S) AS FUND_BA_S, SUM(FUND_BA_RQ) AS FUND_BA_RQ, SUM(FUND_BN_S) AS FUND_BN_S, SUM(FUND_BN_RQ) AS FUND_BN_RQ, SUM(FUND_CR_S) AS FUND_CR_S, SUM(FUND_CR_RQ) AS FUND_CR_RQ, ");
			sb.append("         SUM(STOCK) AS STOCK, SUM(ETF) AS ETF, SUM(BOND) AS BOND, SUM(SI) AS SI, SUM(SN) AS SN, SUM(DCI) AS DCI, SUM(TRUST) AS TRUST, SUM(OTHER) AS OTHER, SUM(INS_INV) AS INS_INV, SUM(INS_SP) AS INS_SP, SUM(INS_STAG) AS INS_STAG ");
			sb.append("  FROM ( ");
			sb.append("    SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sb.append("           TOTAL_INV_INS, TOTAL_INV, TOTAL_INS, ");
			sb.append("           FUND_S_S, FUND_S_RQ, FUND_BA_S, FUND_BA_RQ, FUND_BN_S, FUND_BN_RQ, FUND_CR_S, FUND_CR_RQ, ");
			sb.append("           STOCK, ETF, BOND, SI, SN, DCI, TRUST, OTHER, INS_INV, INS_SP, INS_STAG ");
			sb.append("    FROM TBPMS_EST_PRD_SALES_TARGET ");
			sb.append("    WHERE YYYYMM = :YYYYMM ");
			sb.append("  ) ");
			sb.append("  GROUP BY YYYYMM ");
			sb.append(") ");
			
			queryCondition.setObject("YYYYMM"  , (String) inputLst.get(0).get("YYYYMM"));
			queryCondition.setObject("CREATOR" , ws.getUser().getUserID());
			queryCondition.setObject("MODIFIER", ws.getUser().getUserID());
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);

		}
		
		sendRtnObject(null);
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS503InputVO inputVO = (PMS503InputVO) body;
		
		List<Map<String, Object>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		String fileName = "分行銷量目標";
		String[] csvHeader = new String[] { "年月", "業務處", "營運區", "分行", "投保銷量", 
											"投資銷量",
											"基金-股票型-單筆", "基金-股票型-定期定額",
											"基金-平衡型-單筆", "基金-平衡型-定期定額",
											"基金-債券型-單筆", "基金-債券型-定期定額",
											"基金-貨幣型-單筆", "基金-貨幣型-定期定額",
											"基金-單筆", "基金-定期定額",
											"海外股票/ETF", "海外債(含自營)", "SI/SN/DCI", "信託", "其他(奈米投/黃金存摺)",
											"投險銷量", "保險-投資型", "保險-躉繳", "保險-分期型"};
		
		String[] csvMain   = new String[] { "YYYYMM", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "TOTAL_INV_INS", 
										  	"TOTAL_INV", 
											"FUND_S_S", "FUND_S_RQ", 
											"FUND_BA_S", "FUND_BA_RQ", 
											"FUND_BN_S", "FUND_BN_RQ", 
											"FUND_CR_S", "FUND_CR_RQ",
											"FUND_S", "FUND_RQ", 
											"STOCK_ETF", "BOND", "SI_SN_DCI", "TRUST", "OTHER", 
											"TOTAL_INS", "INS_INV", "INS_SP", "INS_STAG" };
		
		if (exportLst.size() > 0) {
			for (Map<String, Object> map : exportLst) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						default:
							records[i] = checkIsNull(map, csvMain[i]);
							break;
					}
				}
		
				csvData.add(records);
			}
		
			CSVUtil csv = new CSVUtil();
		
			// 設定表頭
			csv.setHeader(csvHeader);
			// 添加明細的List
			csv.addRecordList(csvData);
			
			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();
		
			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, fileName + "_" + sdfYYYYMMDD.format(new Date()) + ".csv");
		}
		
		sendRtnObject(null);
	}
	
	// 檢查Map取出欄位是否為Null
	private String checkIsNull(Map<String, Object> map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
