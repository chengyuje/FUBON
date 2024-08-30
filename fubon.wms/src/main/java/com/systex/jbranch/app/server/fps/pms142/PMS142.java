package com.systex.jbranch.app.server.fps.pms142;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

@Component("pms142")
@Scope("request")
public class PMS142 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	LinkedHashMap<String, String> PROD_YIELD = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> BASIC_THRESHHOLD = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> NUM_NEW_APPOINTMENTS = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> BONUS_RATE = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> FINANCIAL_INDICATOR = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> BEHAVIORAL_INDICATOR = new LinkedHashMap<String, String>();

	public PMS142 () {
		
		// 產品收益率
		PROD_YIELD.put("產品", "PROD");
		PROD_YIELD.put("收益率", "YIELD");

		// 基本門檻
		BASIC_THRESHHOLD.put("職稱", "JOB_TITLE_NAME");
		BASIC_THRESHHOLD.put("本薪倍數", "SALARY_MULTIPLE");
		
		// 新任成數
		NUM_NEW_APPOINTMENTS.put("職稱", "JOB_TITLE_NAME");
		NUM_NEW_APPOINTMENTS.put("月數", "MONTHS");
		NUM_NEW_APPOINTMENTS.put("新任成數", "NUM_APPONTMENTS");
		
		// 獎金率
		BONUS_RATE.put("收益絕對數(最小值)", "INCOME_MIN");
		BONUS_RATE.put("收益絕對數(最大值)", "INCOME_MAX");
		BONUS_RATE.put("職稱", "JOB_TITLE_NAME");
		BONUS_RATE.put("獎金率", "BONUS_RATE");
		
		// 財務指標
		FINANCIAL_INDICATOR.put("財務指標", "INDICATOR_TYPE");
		FINANCIAL_INDICATOR.put("達成率上限", "TARGET_MAX");
		FINANCIAL_INDICATOR.put("權重", "WEIGHTS");
		
		// 行為指標
		BEHAVIORAL_INDICATOR.put("行為指標", "INDICATOR_TYPE");
		BEHAVIORAL_INDICATOR.put("扣減比例", "DEDUCTION_RATIO");
	}
	
	// 查詢
	public void queryData (Object body, IPrimitiveMap header) throws Exception {
		
		PMS142InputVO inputVO = (PMS142InputVO) body;
		PMS142OutputVO outputVO = new PMS142OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
				
		switch (inputVO.getSelectType()) {
			// 產品收益率
			case "PROD_YIELD": 
				sb.append("SELECT PROD, ");
				sb.append("       YIELD * 100 AS YIELD, ");
				sb.append("       PROD AS LABEL, ");	// 用於PMS141
				sb.append("       PROD AS DATA ");		// 用於PMS141
				sb.append("FROM TBPMS_PROD_YIELD ");
				
				break;
				
			// 基本門檻
			case "BASIC_THRESHHOLD": 
				sb.append("SELECT JOB_TITLE_NAME, ");
				sb.append("       SALARY_MULTIPLE ");
				sb.append("FROM TBPMS_BASIC_THRESHHOLD ");
				
				break;
			
			// 新任成數
			case "NUM_NEW_APPOINTMENTS": 
				sb.append("SELECT JOB_TITLE_NAME, ");
				sb.append("       MONTHS, ");
				sb.append("       NUM_APPONTMENTS * 100 AS NUM_APPONTMENTS ");
				sb.append("FROM TBPMS_NUM_NEW_APPOINTMENTS ");
				
				break;
				
			// 獎金率
			case "BONUS_RATE": 
				sb.append("SELECT INCOME_MIN, ");
				sb.append("       INCOME_MAX, ");
				sb.append("       JOB_TITLE_NAME, ");
				sb.append("       BONUS_RATE * 100 AS BONUS_RATE ");
				sb.append("FROM TBPMS_P_PERSONAL_BONUS_CAL ");
				
				break;
			
			// 財務指標
			case "FINANCIAL_INDICATOR": 
				sb.append("SELECT INDICATOR_TYPE, ");
				sb.append("       TARGET_MAX * 100 AS TARGET_MAX, ");
				sb.append("       WEIGHTS * 100 AS WEIGHTS ");
				sb.append("FROM TBPMS_FINANCIAL_INDICATOR ");
				
				break;
				
			// 行為指標
			case "BEHAVIORAL_INDICATOR": 
				sb.append("SELECT INDICATOR_TYPE, ");
				sb.append("       DEDUCTION_RATIO * 100 AS DEDUCTION_RATIO ");
				sb.append("FROM TBPMS_BEHAVIORAL_INDICATOR ");
				
				break;
			
			// 各月份工作天數
			case "BUSI_DAYS":
				sb.append("SELECT TO_CHAR(T, 'YYYYMM') AS YYYYMM, ");
				sb.append("       TO_CHAR(T, 'DD') - (SELECT COUNT(1) FROM TBBTH_HOLIDAY WHERE TO_CHAR(HOL_DATE, 'YYYYMM') = TO_CHAR(T, 'YYYYMM')) AS BUSI_DAYS ");
				sb.append("FROM ( ");
				sb.append("  SELECT DECODE(LEVEL, ");
				sb.append("                1, ");
				sb.append("                ADD_MONTHS(TRUNC(SYSDATE, 'MM'), LEVEL) - 1, ");
				sb.append("                ADD_MONTHS(TRUNC(SYSDATE, 'MM'), LEVEL) - 1) T ");
				sb.append("    FROM DUAL ");
				sb.append("  CONNECT BY LEVEL < 5 ");
				sb.append(") ");

				break;
		}
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}
	
	// 取得範例
	public void getExample (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS142InputVO inputVO = (PMS142InputVO) body;

		CSVUtil csv = new CSVUtil();

		String fileName = "";
		
		switch (inputVO.getSelectType()) {
			case "PROD_YIELD": 				// 產品收益率
				fileName = "產品收益率.csv";
				csv.setHeader(PROD_YIELD.keySet().toArray(new String[PROD_YIELD.keySet().size()]));
				break;
			case "BASIC_THRESHHOLD": 		// 基本門檻
				fileName = "基本門檻.csv";
				csv.setHeader(BASIC_THRESHHOLD.keySet().toArray(new String[BASIC_THRESHHOLD.keySet().size()]));
				break;
			case "NUM_NEW_APPOINTMENTS": 	// 新任成數
				fileName = "新任成數.csv";
				csv.setHeader(NUM_NEW_APPOINTMENTS.keySet().toArray(new String[NUM_NEW_APPOINTMENTS.keySet().size()]));
				break;
			case "BONUS_RATE": 				// 獎金率
				fileName = "獎金率.csv";
				csv.setHeader(BONUS_RATE.keySet().toArray(new String[BONUS_RATE.keySet().size()]));
				break;
			case "FINANCIAL_INDICATOR": 	// 財務指標
				fileName = "財務指標.csv";
				csv.setHeader(FINANCIAL_INDICATOR.keySet().toArray(new String[FINANCIAL_INDICATOR.keySet().size()]));
				break;
			case "BEHAVIORAL_INDICATOR": 	// 行為指標
				fileName = "行為指標.csv";
				csv.setHeader(BEHAVIORAL_INDICATOR.keySet().toArray(new String[BEHAVIORAL_INDICATOR.keySet().size()]));
				break;
		}
			
		// 設定表頭
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		
		sendRtnObject(null);
	}
	
	// 上傳
	public void updFile (Object body, IPrimitiveMap header) throws Exception {
	
		WorkStation ws = DataManager.getWorkStation(uuid);

		PMS142InputVO inputVO = (PMS142InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			// 1. 整檔清空 => 2. 讀檔 
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, Object>> inputLst = new ArrayList<Map<String, Object>>();
			LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();
			
			switch (inputVO.getSelectType()) {
				case "PROD_YIELD":
					queryCondition.setQueryString("TRUNCATE TABLE TBPMS_PROD_YIELD ");					
					headColumnMap = PROD_YIELD;
					break;
				case "BASIC_THRESHHOLD":
					queryCondition.setQueryString("TRUNCATE TABLE TBPMS_BASIC_THRESHHOLD ");
					headColumnMap = BASIC_THRESHHOLD;
					break;
				case "NUM_NEW_APPOINTMENTS":
					queryCondition.setQueryString("TRUNCATE TABLE TBPMS_NUM_NEW_APPOINTMENTS ");
					headColumnMap = NUM_NEW_APPOINTMENTS;
					break;
				case "BONUS_RATE":
					queryCondition.setQueryString("TRUNCATE TABLE TBPMS_P_PERSONAL_BONUS_CAL ");
					headColumnMap = BONUS_RATE;
					break;
				case "FINANCIAL_INDICATOR":
					queryCondition.setQueryString("TRUNCATE TABLE TBPMS_FINANCIAL_INDICATOR ");
					headColumnMap = FINANCIAL_INDICATOR;
					break;
				case "BEHAVIORAL_INDICATOR":
					queryCondition.setQueryString("TRUNCATE TABLE TBPMS_BEHAVIORAL_INDICATOR ");
					headColumnMap = BEHAVIORAL_INDICATOR;
					break;
			}
			
			dam.exeUpdate(queryCondition);
			
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				
				for (int i = 0; i < data.length; i++) {
					dataMap.put(headColumnMap.get(head[i]), data[i]);
				}
		
				inputLst.add(dataMap);
			}

			// 3. 寫入
			for (Map<String, Object> dataMap: inputLst) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				switch (inputVO.getSelectType()) {
					case "PROD_YIELD":
						sb.append("INSERT INTO TBPMS_PROD_YIELD ( ");
						sb.append("  PROD, YIELD, ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES ( ");
						sb.append("  :PROD, :YIELD, ");
						sb.append("  0, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
						sb.append(") ");
						
						queryCondition.setObject("PROD"    , dataMap.get("PROD")     );
						queryCondition.setObject("YIELD"   , dataMap.get("YIELD")    );
						queryCondition.setObject("CREATOR" , ws.getUser().getUserID());
						queryCondition.setObject("MODIFIER", ws.getUser().getUserID());
						
						break;
					case "BASIC_THRESHHOLD":
						sb.append("INSERT INTO TBPMS_BASIC_THRESHHOLD ( ");
						sb.append("  JOB_TITLE_NAME, SALARY_MULTIPLE, ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES ( ");
						sb.append("  :JOB_TITLE_NAME, :SALARY_MULTIPLE, ");
						sb.append("  0, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
						sb.append(") ");
						
						queryCondition.setObject("JOB_TITLE_NAME" , dataMap.get("JOB_TITLE_NAME"));
						queryCondition.setObject("SALARY_MULTIPLE", dataMap.get("SALARY_MULTIPLE"));
						queryCondition.setObject("CREATOR"        , ws.getUser().getUserID()      );
						queryCondition.setObject("MODIFIER"       , ws.getUser().getUserID()      );
						
						break;
					case "NUM_NEW_APPOINTMENTS":
						sb.append("INSERT INTO TBPMS_NUM_NEW_APPOINTMENTS ( ");
						sb.append("  JOB_TITLE_NAME, MONTHS, NUM_APPONTMENTS, ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES ( ");
						sb.append("  :JOB_TITLE_NAME, :MONTHS, :NUM_APPONTMENTS, ");
						sb.append("  0, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
						sb.append(") ");
						
						queryCondition.setObject("JOB_TITLE_NAME" , dataMap.get("JOB_TITLE_NAME") );
						queryCondition.setObject("MONTHS"         , dataMap.get("MONTHS")         );
						queryCondition.setObject("NUM_APPONTMENTS", dataMap.get("NUM_APPONTMENTS"));
						queryCondition.setObject("CREATOR"        , ws.getUser().getUserID()      );
						queryCondition.setObject("MODIFIER"       , ws.getUser().getUserID()      );
						
						break;
					case "BONUS_RATE":
						sb.append("INSERT INTO TBPMS_P_PERSONAL_BONUS_CAL ( ");
						sb.append("  INCOME_MIN, INCOME_MAX, JOB_TITLE_NAME, BONUS_RATE, ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES ( ");
						sb.append("  :INCOME_MIN, :INCOME_MAX, :JOB_TITLE_NAME, :BONUS_RATE, ");
						sb.append("  0, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
						sb.append(") ");
						
						queryCondition.setObject("INCOME_MIN"    , dataMap.get("INCOME_MIN")    );
						queryCondition.setObject("INCOME_MAX"    , dataMap.get("INCOME_MAX")    );
						queryCondition.setObject("JOB_TITLE_NAME", dataMap.get("JOB_TITLE_NAME"));
						queryCondition.setObject("BONUS_RATE"    , dataMap.get("BONUS_RATE")    );
						queryCondition.setObject("CREATOR"       , ws.getUser().getUserID()     );
						queryCondition.setObject("MODIFIER"      , ws.getUser().getUserID()     );
						
						break;
					case "FINANCIAL_INDICATOR":
						sb.append("INSERT INTO TBPMS_FINANCIAL_INDICATOR ( ");
						sb.append("  INDICATOR_TYPE, TARGET_MAX, WEIGHTS, ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES ( ");
						sb.append("  :INDICATOR_TYPE, :TARGET_MAX, :WEIGHTS, ");
						sb.append("  0, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
						sb.append(") ");
						
						queryCondition.setObject("INDICATOR_TYPE", dataMap.get("INDICATOR_TYPE"));
						queryCondition.setObject("TARGET_MAX"    , dataMap.get("TARGET_MAX")    );
						queryCondition.setObject("WEIGHTS"       , dataMap.get("WEIGHTS")       );
						queryCondition.setObject("CREATOR"       , ws.getUser().getUserID()     );
						queryCondition.setObject("MODIFIER"      , ws.getUser().getUserID()     );
						
						break;
					case "BEHAVIORAL_INDICATOR":
						sb.append("INSERT INTO TBPMS_BEHAVIORAL_INDICATOR ( ");
						sb.append("  INDICATOR_TYPE, DEDUCTION_RATIO, ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES ( ");
						sb.append("  :INDICATOR_TYPE, :DEDUCTION_RATIO, ");
						sb.append("  0, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
						sb.append(") ");
						
						queryCondition.setObject("INDICATOR_TYPE" , dataMap.get("INDICATOR_TYPE") );
						queryCondition.setObject("DEDUCTION_RATIO", dataMap.get("DEDUCTION_RATIO"));
						queryCondition.setObject("CREATOR"        , ws.getUser().getUserID()      );
						queryCondition.setObject("MODIFIER"       , ws.getUser().getUserID()      );
						
						break;
				}
				
				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);
			}
		}
		
		sendRtnObject(null);
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS142InputVO inputVO = (PMS142InputVO) body;
		
		List<Map<String, Object>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		CSVUtil csv = new CSVUtil();
		
		String fileName = "";
		String[] csvHeader = new String[0];
		String[] csvMain = new String[0];
		
		switch (inputVO.getSelectType()) {
			case "PROD_YIELD": 				// 產品收益率
				fileName = "產品收益率";
				csvHeader = PROD_YIELD.keySet().toArray(new String[PROD_YIELD.keySet().size()]);
				csvMain = PROD_YIELD.values().toArray(new String[PROD_YIELD.keySet().size()]);
				break;
			case "BASIC_THRESHHOLD": 		// 基本門檻
				fileName = "基本門檻";
				csvHeader = BASIC_THRESHHOLD.keySet().toArray(new String[BASIC_THRESHHOLD.keySet().size()]);
				csvMain = BASIC_THRESHHOLD.values().toArray(new String[BASIC_THRESHHOLD.keySet().size()]);
				break;
			case "NUM_NEW_APPOINTMENTS": 	// 新任成數
				fileName = "新任成數";
				csvHeader = NUM_NEW_APPOINTMENTS.keySet().toArray(new String[NUM_NEW_APPOINTMENTS.keySet().size()]);
				csvMain = NUM_NEW_APPOINTMENTS.values().toArray(new String[NUM_NEW_APPOINTMENTS.keySet().size()]);
				break;
			case "BONUS_RATE": 				// 獎金率
				fileName = "獎金率";
				csvHeader = BONUS_RATE.keySet().toArray(new String[BONUS_RATE.keySet().size()]);
				csvMain = BONUS_RATE.values().toArray(new String[BONUS_RATE.keySet().size()]);
				break;
			case "FINANCIAL_INDICATOR": 	// 財務指標
				fileName = "財務指標";
				csvHeader = FINANCIAL_INDICATOR.keySet().toArray(new String[FINANCIAL_INDICATOR.keySet().size()]);
				csvMain = FINANCIAL_INDICATOR.values().toArray(new String[FINANCIAL_INDICATOR.keySet().size()]);
				break;
			case "BEHAVIORAL_INDICATOR": 	// 行為指標
				fileName = "行為指標";
				csvHeader = BEHAVIORAL_INDICATOR.keySet().toArray(new String[BEHAVIORAL_INDICATOR.keySet().size()]);
				csvMain = BEHAVIORAL_INDICATOR.values().toArray(new String[BEHAVIORAL_INDICATOR.keySet().size()]);
				break;
		}
		
		if (exportLst.size() > 0) {
			for (Map<String, Object> map : exportLst) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "YIELD":
						case "NUM_APPONTMENTS":
						case "BONUS_RATE":
						case "TARGET_MAX":
						case "WEIGHTS":
						case "DEDUCTION_RATIO":
							records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0.00").format(map.get(csvMain[i])) + " %\"" : "=\"" + "\""; 
							break;
						case "SALARY_MULTIPLE":
							records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0.00").format(map.get(csvMain[i])) + "\"" : "=\"" + "\""; 
							break;
						case "INCOME_MIN": 
						case "INCOME_MAX":
							records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0").format(map.get(csvMain[i])) + "\"" : "=\""  + "\""; 
							break;
						default:
							records[i] = checkIsNull(map, csvMain[i]);
							break;
					}
				}
				
				csvData.add(records);
			}
			
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
