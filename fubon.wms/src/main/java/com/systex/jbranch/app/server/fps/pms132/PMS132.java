package com.systex.jbranch.app.server.fps.pms132;

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

@Component("pms132")
@Scope("request")
public class PMS132 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	LinkedHashMap<String, String> GROUP_DETAIL = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> BONUS_RATE = new LinkedHashMap<String, String>();

	public PMS132 () {
		// 業務處回覆組別明細檔
		GROUP_DETAIL.put("DT查詢碼", "DTCCC");
		GROUP_DETAIL.put("編號", "NO");
		GROUP_DETAIL.put("業務處回覆組別", "GRN");
		GROUP_DETAIL.put("業務處", "REGION_CENTER_NAME");
		GROUP_DETAIL.put("營運區", "BRANCH_AREA_NAME");
		GROUP_DETAIL.put("分行名稱", "BRANCH_NAME");
		GROUP_DETAIL.put("分行代碼", "BRANCH_NBR");
		GROUP_DETAIL.put("職級", "JOB_TITLE_NAME");
		GROUP_DETAIL.put("員編", "EMP_ID");
		GROUP_DETAIL.put("姓名", "EMP_NAME");
		GROUP_DETAIL.put("成本1", "WANBP");
		GROUP_DETAIL.put("倍數", "MUL");
		GROUP_DETAIL.put("財務指標得分", "FS");
		GROUP_DETAIL.put("行為指標扣減金額", "BP");
		GROUP_DETAIL.put("DT獎金率", "GCPOR_DT");
		GROUP_DETAIL.put("DT獎金率加碼", "GCPORUP1_DT");
		GROUP_DETAIL.put("無違規/客訴加碼", "GCPORUP2_DT");
		GROUP_DETAIL.put("去年收益", "LAST_Y_REBP");
		GROUP_DETAIL.put("去年成本", "LAST_Y_TWPCBP");
		GROUP_DETAIL.put("去年掛goal月數", "LAST_Y_GM");
		GROUP_DETAIL.put("是否Leader", "LEADER_YN");
		
		BONUS_RATE.put("收益絕對數(最小值)", "INCOME_MIN");
		BONUS_RATE.put("收益絕對數(最大值)", "INCOME_MAX");
		BONUS_RATE.put("職稱", "JOB_TITLE_NAME");
		BONUS_RATE.put("獎金率", "BONUS_RATE");
	}
	
	// 查詢
	public void queryData (Object body, IPrimitiveMap header) throws Exception {
		
		PMS132InputVO inputVO = (PMS132InputVO) body;
		PMS132OutputVO outputVO = new PMS132OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		switch (inputVO.getSelectType()) {
			case "G":
				sb.append("SELECT A.DTCCC, ");
				sb.append("       A.NO, ");
				sb.append("       A.GRN, ");
				sb.append("       A.REGION_CENTER_NAME, ");
				sb.append("       A.BRANCH_AREA_NAME, ");
				sb.append("       A.BRANCH_NAME, ");
				sb.append("       A.BRANCH_NBR, ");
				sb.append("       A.JOB_TITLE_NAME, ");
				sb.append("       A.EMP_ID, ");
				sb.append("       M.EMP_NAME, ");
				sb.append("       A.WANBP, ");
				sb.append("       A.MUL, ");
				sb.append("       A.FS, ");
				sb.append("       A.BP, ");
				sb.append("       A.GCPOR_DT * 100 AS GCPOR_DT, ");
				sb.append("       A.GCPORUP1_DT * 100 AS GCPORUP1_DT, ");
				sb.append("       A.GCPORUP2_DT * 100 AS GCPORUP2_DT, ");
				sb.append("       A.LAST_Y_REBP, ");
				sb.append("       A.LAST_Y_TWPCBP, ");
				sb.append("       A.LAST_Y_GM, ");
				sb.append("       A.DT_CODE, ");
				sb.append("       A.QUAL, ");
				sb.append("       A.PDBBP, ");
				sb.append("       A.PDBB_DT, ");
				sb.append("       A.LAST_Y_AVRP, ");
				sb.append("       A.LAST_Y_REBDT_DT, ");
				sb.append("       A.LAST_Y_TWPCBP_DT, ");
				sb.append("       A.LEADER_YN ");
//				sb.append("	      DT.LEADER_FLAG ");
				sb.append("FROM TBPMS_GROUP_BONUS_CAL A ");
				sb.append("LEFT JOIN TBORG_MEMBER M ON A.EMP_ID = M.EMP_ID ");
//				sb.append("LEFT JOIN TBORG_DIAMOND_TEAM DT ON A.EMP_ID = DT.EMP_ID ");
				
				sb.append("WHERE 1 = 1 ");
	
				if (StringUtils.isNotEmpty(inputVO.getDtccc())) {
					sb.append("AND A.DTCCC = :dtccc ");
					queryCondition.setObject("dtccc", inputVO.getDtccc());
				}
				
				break;
			case "P":
				sb.append("SELECT INCOME_MIN, ");
				sb.append("       INCOME_MAX, ");
				sb.append("       JOB_TITLE_NAME, ");
				sb.append("       BONUS_RATE * 100 AS BONUS_RATE, ");
				sb.append("       VERSION, ");
				sb.append("       CREATETIME, ");
				sb.append("       CREATOR, ");
				sb.append("       MODIFIER, ");
				sb.append("       LASTUPDATE ");
				sb.append("FROM TBPMS_G_PERSONAL_BONUS_CAL ");
				
				sb.append("WHERE 1 = 1 ");
				
				sb.append("ORDER BY JOB_TITLE_NAME, INCOME_MIN ");
				
				break;
		}
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}
	
	// 取得範例
	public void getExample(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS132InputVO inputVO = (PMS132InputVO) body;
		
		CSVUtil csv = new CSVUtil();

		String fileName = "";
		switch (inputVO.getSelectType()) {
			case "G":
				csv.setHeader(GROUP_DETAIL.keySet().toArray(new String[GROUP_DETAIL.keySet().size()]));
				fileName = "業務處回覆組別明細檔.csv";
				break;
			case "P":
				fileName = "個人獎金率.csv";
				csv.setHeader(BONUS_RATE.keySet().toArray(new String[BONUS_RATE.keySet().size()]));
				break;
		}
		
		// 設定表頭
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		
		sendRtnObject(null);
	}
	
	// 上傳-業務處回覆組別明細檔
	public void updGroupBonusCal(Object body, IPrimitiveMap header) throws Exception {
	
		WorkStation ws = DataManager.getWorkStation(uuid);

		PMS132InputVO inputVO = (PMS132InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		DecimalFormat df0 = new DecimalFormat("###");
		DecimalFormat df2 = new DecimalFormat("###.##");
		
		String[] calculateList = {"DT_CODE", "QUAL", "PDBBP", "LAST_Y_AVRP"};
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			// 1. 整檔清空
			queryCondition.setQueryString("TRUNCATE TABLE TBPMS_GROUP_BONUS_CAL ");
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
					dataMap.put(GROUP_DETAIL.get(head[i]), data[i]);
				}

				for (int i = 0; i < calculateList.length; i++) {
					switch (calculateList[i]) {
						case "DT_CODE": // DT CODE：分行代碼 +業務處回覆組別
							dataMap.put(calculateList[i], dataMap.get("BRANCH_NBR") + "" + dataMap.get("GRN"));
							break;
						case "QUAL": // 門檻：成本1 *倍數
							dataMap.put(calculateList[i], df0.format((new BigDecimal((String) dataMap.get("WANBP"))).multiply((new BigDecimal((String) dataMap.get("MUL"))))));
							break;
						case "PDBBP": // 生產力基期：若去年收益 > 0 則寫入去年收益 ÷ 去年成本 否則寫入0
							dataMap.put(calculateList[i], (new BigDecimal((String) dataMap.get("LAST_Y_REBP"))).compareTo(BigDecimal.ZERO) > 0 ? df2.format((new BigDecimal((String) dataMap.get("LAST_Y_REBP"))).divide((new BigDecimal((String) dataMap.get("LAST_Y_TWPCBP"))))) : 0);
							break;
						case "LAST_Y_AVRP": // 去年收益/平均每月：若去年收益 > 0 且 去年掛GOAL月數 > 0 則寫入去年收益 ÷去年掛GOAL月數 否則寫入0
							dataMap.put(calculateList[i], (new BigDecimal((String) dataMap.get("LAST_Y_REBP"))).compareTo(BigDecimal.ZERO) > 0 && (new BigDecimal((String) dataMap.get("LAST_Y_GM"))).compareTo(BigDecimal.ZERO) > 0 ? df0.format((new BigDecimal((String) dataMap.get("LAST_Y_REBP"))).divide((new BigDecimal((String) dataMap.get("LAST_Y_GM"))))) : 0);
							break;
					}
				}
				
				inputLst.add(dataMap);
			}
			
			// 3. 寫入
			for (Map<String, Object> dataMap: inputLst) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("INSERT INTO TBPMS_GROUP_BONUS_CAL ( ");
				sb.append("  DTCCC, ");
				sb.append("  NO, ");
				sb.append("  GRN, ");
				sb.append("  REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NAME, BRANCH_NBR, ");
				sb.append("  JOB_TITLE_NAME, EMP_ID, EMP_NAME, ");
				sb.append("  WANBP, ");
				sb.append("  MUL, ");
				sb.append("  FS, ");
				sb.append("  BP, ");
				sb.append("  GCPOR_DT, GCPORUP1_DT, GCPORUP2_DT, ");
				sb.append("  LAST_Y_REBP, LAST_Y_TWPCBP, LAST_Y_GM, ");
				sb.append("  DT_CODE, ");
				sb.append("  QUAL, ");
				sb.append("  PDBBP, ");
				sb.append("  LAST_Y_AVRP, ");
				sb.append("  LEADER_YN, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");

				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  :DTCCC, ");
				sb.append("  :NO, ");
				sb.append("  :GRN, ");
				sb.append("  :REGION_CENTER_NAME, :BRANCH_AREA_NAME, :BRANCH_NAME, :BRANCH_NBR, ");
				sb.append("  :JOB_TITLE_NAME, :EMP_ID, :EMP_NAME, ");
				sb.append("  :WANBP, ");
				sb.append("  :MUL, ");
				sb.append("  :FS, ");
				sb.append("  :BP, ");
				sb.append("  :GCPOR_DT, :GCPORUP1_DT, :GCPORUP2_DT, ");
				sb.append("  :LAST_Y_REBP, :LAST_Y_TWPCBP, :LAST_Y_GM, ");
				sb.append("  :DT_CODE, ");
				sb.append("  :QUAL, ");
				sb.append("  :PDBBP, ");
				sb.append("  :LAST_Y_AVRP, ");
				sb.append("  :LEADER_YN, ");
				sb.append("  :VERSION, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
				sb.append(") ");
				
				queryCondition.setObject("DTCCC"               , dataMap.get("DTCCC")                            );
				queryCondition.setObject("NO"                  , dataMap.get("NO")                               );
				queryCondition.setObject("GRN"                 , dataMap.get("GRN")                              );
				queryCondition.setObject("REGION_CENTER_NAME"  , dataMap.get("REGION_CENTER_NAME")               );
				queryCondition.setObject("BRANCH_AREA_NAME"    , dataMap.get("BRANCH_AREA_NAME")                 );
				queryCondition.setObject("BRANCH_NAME"         , dataMap.get("BRANCH_NAME")                      );
				queryCondition.setObject("BRANCH_NBR"          , dataMap.get("BRANCH_NBR")                       );
				queryCondition.setObject("JOB_TITLE_NAME"      , dataMap.get("JOB_TITLE_NAME")                   );
				queryCondition.setObject("EMP_ID"              , addZeroForNum((String)dataMap.get("EMP_ID"), 6) );
				queryCondition.setObject("EMP_NAME"            , dataMap.get("EMP_NAME")                         );
				queryCondition.setObject("WANBP"               , dataMap.get("WANBP")                            );
				queryCondition.setObject("MUL"                 , dataMap.get("MUL")                              );
				queryCondition.setObject("FS"                  , dataMap.get("FS")                               );
				queryCondition.setObject("BP"                  , dataMap.get("BP")                               );
				queryCondition.setObject("GCPOR_DT"            , dataMap.get("GCPOR_DT")                         );
				queryCondition.setObject("GCPORUP1_DT"         , dataMap.get("GCPORUP1_DT")                      );
				queryCondition.setObject("GCPORUP2_DT"         , dataMap.get("GCPORUP2_DT")                      );
				queryCondition.setObject("LAST_Y_REBP"         , dataMap.get("LAST_Y_REBP")                      );
				queryCondition.setObject("LAST_Y_TWPCBP"       , dataMap.get("LAST_Y_TWPCBP")                    );
				queryCondition.setObject("LAST_Y_GM"           , dataMap.get("LAST_Y_GM")                        );
				queryCondition.setObject("DT_CODE"             , dataMap.get("DT_CODE")                          );
				queryCondition.setObject("QUAL"                , dataMap.get("QUAL")                             );
				queryCondition.setObject("PDBBP"               , dataMap.get("PDBBP")                            );
				queryCondition.setObject("LAST_Y_AVRP"         , dataMap.get("LAST_Y_AVRP")                      );
				queryCondition.setObject("LEADER_YN"           , dataMap.get("LEADER_YN")                        );
				queryCondition.setObject("VERSION"             , 0                                               );
				queryCondition.setObject("CREATOR"             , ws.getUser().getUserID()                        );
				queryCondition.setObject("MODIFIER"            , ws.getUser().getUserID()                        );
				
				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);
			}
			
			
			// 4. 計算生產力基期_DT、去年收益_DT、去年成本_DT
			//    生產力基期_DT：若去年收益_DT > 0 則寫入去年收益_DT ÷ 去年成本_DT 否則寫入0
			//    去年收益_DT：上傳資料寫入後，將同組人員去年收益加總並寫入。
			//    去年成本_DT：上傳資料寫入後，將同組人員去年成本加總並寫入。
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			sb.append("MERGE INTO TBPMS_GROUP_BONUS_CAL A ");
			sb.append("USING ( ");
			sb.append("  SELECT DT_CODE, SUM(LAST_Y_REBP) AS LAST_Y_REBDT_DT, SUM(LAST_Y_TWPCBP) AS LAST_Y_TWPCBP_DT, ");
			sb.append("         CASE WHEN SUM(LAST_Y_REBP) > 0 THEN ROUND(SUM(LAST_Y_REBP) / SUM(LAST_Y_TWPCBP), 2) ELSE 0 END AS PDBB_DT ");
			sb.append("  FROM TBPMS_GROUP_BONUS_CAL ");
			sb.append("  GROUP BY DT_CODE ");
			sb.append(") B ON ( ");
			sb.append("  A.DT_CODE = B.DT_CODE ");
			sb.append(") ");
			sb.append("WHEN MATCHED THEN ");
			sb.append("  UPDATE SET A.LAST_Y_REBDT_DT = B.LAST_Y_REBDT_DT, A.LAST_Y_TWPCBP_DT = B.LAST_Y_TWPCBP_DT, A.PDBB_DT = B.PDBB_DT ");
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
		
		sendRtnObject(null);
	}
	
	// 匯出-業務處回覆組別明細檔
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS132InputVO inputVO = (PMS132InputVO) body;
		
		List<Map<String, Object>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		String fileName = "";
		String[] csvHeader = new String[0];
		String[] csvMain = new String[0];
		
		switch (inputVO.getSelectType()) {
			case "G":
				fileName = "業務處回覆組別明細檔";
				csvHeader = new String[] { "DT查詢碼", "編號", "業務處回覆組別", "DT CODE",
										   "業務處", "營運區", "分行代碼", "分行名稱",
										   "職級", "員編", "姓名",
										   "成本", "倍數", "財務指標得分", "行為指標扣減金額",
										   "門檻", "生產力基期", "DT生產力基期",
										   "DT獎金率", "DT獎金率加碼", "無違規/客訴加碼",
										   "去年收益", "去年成本", "去年掛GOAL月數", "去年收益/平均每月",
										   "DT去年收益", "DT去年成本", "是否Leader"};
				csvMain   = new String[] { "DTCCC", "NO", "GRN", "DT_CODE",
								   		   "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", 
								   		   "JOB_TITLE_NAME", "EMP_ID", "EMP_NAME", 				
								   		   "WANBP", "MUL", "FS", "BP",		
								   		   "QUAL", "PDBBP", "PDBB_DT",			
								   		   "GCPOR_DT", "GCPORUP1_DT", "GCPORUP2_DT",
								   		   "LAST_Y_REBP", "LAST_Y_TWPCBP", "LAST_Y_GM", "LAST_Y_AVRP",
								   		   "LAST_Y_REBDT_DT", "LAST_Y_TWPCBP_DT", "LEADER_YN"};
				break;
			case "P":
				fileName = "個人獎金率";
				csvHeader = BONUS_RATE.keySet().toArray(new String[BONUS_RATE.keySet().size()]);
				csvMain = BONUS_RATE.values().toArray(new String[BONUS_RATE.keySet().size()]);
				
				break;
		}
		
		if (exportLst.size() > 0) {
			for (Map<String, Object> map : exportLst) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
					case "WANBP": 				// 成本
					case "FS":					// 財務指標得分
					case "BP":					// 行為指標扣減金額
					case "QUAL":				// 門檻
					case "LAST_Y_TWPCBP":		// 去年成本
					case "LAST_Y_GM":			// 去年掛GOAL月數
					case "LAST_Y_REBDT_DT":		// DT去年收益
					case "LAST_Y_TWPCBP_DT":	// DT去年成本
					case "LAST_Y_AVRP":			// 去年收益/平均每月
						records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0").format(map.get(csvMain[i])) + "\"" : "=\""  + "\""; 
						break;
					case "MUL":					// 倍數
						records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0.0").format(map.get(csvMain[i])) + "\"" : "=\"" + "\"";  
						break;
					case "PDBBP":				// 生產力基期
					case "PDBB_DT":				// DT生產力基期
						records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0.00").format(map.get(csvMain[i])) + "\"" : "=\"" + "\""; 
						break;
					case "GCPOR_DT":			// DT獎金率
					case "GCPORUP1_DT":			// DT獎金率加碼
					case "GCPORUP2_DT":			// 無違規/客訴加碼
					case "BONUS_RATE":			// 獎金率
						records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0.00").format(map.get(csvMain[i])) + " %\"" : "=\"" + "\""; 
						break;
					case "LAST_Y_REBP":			// 去年收益
						records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0.0000000000").format(map.get(csvMain[i])) + "\"" : "=\"" + "\""; 
						break;
					case "INCOME_MIN": 			// 收益絕對數(最小值)
					case "INCOME_MAX":			// 收益絕對數(最大值)
						records[i] = (null != map.get(csvMain[i])) ? "=\"" + new DecimalFormat("#,##0").format(map.get(csvMain[i])) + "\"" : "=\""  + "\""; 
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
	
	// 上傳-個人獎金率
	public void updPersonalBonusCal(Object body, IPrimitiveMap header) throws Exception {
	
		WorkStation ws = DataManager.getWorkStation(uuid);

		PMS132InputVO inputVO = (PMS132InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			// 1. 整檔清空
			queryCondition.setQueryString("TRUNCATE TABLE TBPMS_G_PERSONAL_BONUS_CAL ");
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
					dataMap.put(BONUS_RATE.get(head[i]), data[i]);
				}
				
				inputLst.add(dataMap);
			}
			
			// 3. 寫入
			for (Map<String, Object> dataMap: inputLst) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("INSERT INTO TBPMS_G_PERSONAL_BONUS_CAL ( ");
				sb.append("  INCOME_MIN, ");
				sb.append("  INCOME_MAX, ");
				sb.append("  JOB_TITLE_NAME, ");
				sb.append("  BONUS_RATE, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");

				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  :INCOME_MIN, ");
				sb.append("  :INCOME_MAX, ");
				sb.append("  :JOB_TITLE_NAME, ");
				sb.append("  :BONUS_RATE, ");
				sb.append("  :VERSION, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
				sb.append(") ");
				
				queryCondition.setObject("INCOME_MIN"    , dataMap.get("INCOME_MIN"));
				queryCondition.setObject("INCOME_MAX"    , dataMap.get("INCOME_MAX"));
				queryCondition.setObject("JOB_TITLE_NAME", dataMap.get("JOB_TITLE_NAME"));
				queryCondition.setObject("BONUS_RATE"    , dataMap.get("BONUS_RATE"));
				queryCondition.setObject("VERSION"       , 0);
				queryCondition.setObject("CREATOR"       , ws.getUser().getUserID());
				queryCondition.setObject("MODIFIER"      , ws.getUser().getUserID());
				
				queryCondition.setQueryString(sb.toString());
				dam.exeUpdate(queryCondition);
			}
		}
		
		sendRtnObject(null);
	}
	
	// 左補0
	private String addZeroForNum(String str, int strLength) {
		
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }

	    return str;
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
