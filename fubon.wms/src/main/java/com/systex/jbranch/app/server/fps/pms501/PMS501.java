package com.systex.jbranch.app.server.fps.pms501;

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

@Component("pms501")
@Scope("request")
public class PMS501 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	LinkedHashMap<String, String> EST_PRD_RATE = new LinkedHashMap<String, String>();

	public PMS501 () {
		// 商品預估收益率
		EST_PRD_RATE.put("預計承作商品", "EST_PRD_NAME");	// 預計承作商品
		EST_PRD_RATE.put("預估收益率",  "EST_RETURN_RATE");	// 預估收益率
		
	}
	
	// 查詢
	public void query (Object body, IPrimitiveMap header) throws Exception {
		
		PMS501OutputVO outputVO = new PMS501OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT P.PARAM_CODE AS EST_PRD, ");
		sb.append("       P.PARAM_NAME AS EST_PRD_NAME, ");
		sb.append("       ERR.EST_RETURN_RATE * 100 AS EST_RETURN_RATE ");
		sb.append("FROM TBSYSPARAMETER P ");
		sb.append("LEFT JOIN TBPMS_EST_RETURN_RATE ERR ON ERR.EST_PRD = P.PARAM_CODE ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND P.PARAM_TYPE = 'PMS.SALE_PLAN_PTYPE' ");
		sb.append("ORDER BY P.PARAM_ORDER ");
				
		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}
	
	// 取得範例
	public void getExample(Object body, IPrimitiveMap header) throws JBranchException {
			
		CSVUtil csv = new CSVUtil();
		
		String fileName = "商品預估收益率.csv";
		String[] csvHeader = EST_PRD_RATE.keySet().toArray(new String[EST_PRD_RATE.keySet().size()]);
		String[] csvMain   = new String[] {"PARAM_NAME"};
		
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
		
		List<Object[]> csvData = new ArrayList<Object[]>();
		if (salePlanPtypeList.size() > 0) {
			for (Map<String, Object> map : salePlanPtypeList) {
				String[] records = new String[csvMain.length];
				for (int i = 0; i < csvMain.length; i++) {
					switch (csvMain[i]) {
						default:
							records[i] = checkIsNull(map, csvMain[i]);
							break;
					}
					
					csvData.add(records);
				}
			}
		}
		
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		
		sendRtnObject(null);
	}
	
	// 上傳
	public void updFile(Object body, IPrimitiveMap header) throws Exception {
	
		WorkStation ws = DataManager.getWorkStation(uuid);

		PMS501InputVO inputVO = (PMS501InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT PARAM_CODE ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND PARAM_TYPE = 'PMS.SALE_PLAN_PTYPE' ");
		sb.append("AND PARAM_NAME = :paramName ");
		sb.append("ORDER BY PARAM_ORDER ");
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			// 1. 整檔清空
			queryCondition.setQueryString("TRUNCATE TABLE TBPMS_EST_RETURN_RATE ");
			dam.exeUpdate(queryCondition);
			
			// 2. 讀檔
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, Object>> inputLst = new ArrayList<Map<String, Object>>();
			
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, Object> dataMap = new HashMap<String, Object>();

				for (int i = 0; i < data.length; i++) {
					dataMap.put(EST_PRD_RATE.get(head[i]), data[i]);
					
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString(sb.toString());
					
					switch (EST_PRD_RATE.get(head[i])) {
						case "EST_PRD_NAME":
							queryCondition.setObject("paramName", data[i]);
							
							List<Map<String, Object>> pType = dam.exeQuery(queryCondition);
							
							dataMap.put("EST_PRD", pType.size() > 0 ? pType.get(0).get("PARAM_CODE") : "");
							
							break;
					}
				}
				
				inputLst.add(dataMap);
			}
			
			// 3. 寫入
			for (Map<String, Object> dataMap: inputLst) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("INSERT INTO TBPMS_EST_RETURN_RATE ( ");
				sb.append("  EST_PRD, ");
				sb.append("  EST_RETURN_RATE, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  :EST_PRD, ");
				sb.append("  :EST_RETURN_RATE, ");
				sb.append("  :VERSION, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
				sb.append(") ");
				
				queryCondition.setObject("EST_PRD"             , dataMap.get("EST_PRD")        );
				queryCondition.setObject("EST_RETURN_RATE"     , dataMap.get("EST_RETURN_RATE"));
				queryCondition.setObject("VERSION"             , 0                             );
				queryCondition.setObject("CREATOR"             , ws.getUser().getUserID()      );
				queryCondition.setObject("MODIFIER"            , ws.getUser().getUserID()      );
				
				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);
			}
		}
		
		sendRtnObject(null);
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS501InputVO inputVO = (PMS501InputVO) body;
		
		List<Map<String, Object>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		String fileName = "商品預估收益率";
		String[] csvHeader = new String[] { "預計承作商品", "預估收益率" };
		String[] csvMain = new String[] { "EST_PRD_NAME", "EST_RETURN_RATE" };
		
		if (exportLst.size() > 0) {
			for (Map<String, Object> map : exportLst) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "EST_RETURN_RATE":			// 預估收益率
							records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0.00").format(map.get(csvMain[i])) + " %\"" : "=\"" + "\""; 
							break;
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
