package com.systex.jbranch.app.server.fps.org270;

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

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/*
 * #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
 */
@Component("org270")
@Scope("request")
public class ORG270 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();
	
	public ORG270 () {
		headColumnMap.put("分行代碼", "BRANCH_NBR");
		headColumnMap.put("組別", "TEAM_TYPE");
		headColumnMap.put("員工編號", "EMP_ID");
		headColumnMap.put("是否為Team Leader", "LEADER_FLAG");
	}
	
	// 查詢
	public void query (Object body, IPrimitiveMap header) throws Exception {
		
		ORG270InputVO inputVO = (ORG270InputVO) body;
		ORG270OutputVO outputVO = new ORG270OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, ");
		sb.append("       DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, ");
		sb.append("       DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, ");
		sb.append("       DT.TEAM_TYPE, DT.LEADER_FLAG, ");
		sb.append("       DT.EMP_ID, MEM.EMP_NAME, ");
		sb.append("       DT.LASTUPDATE, DT.MODIFIER ");
		sb.append("FROM TBORG_DIAMOND_TEAM DT ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON DT.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON DT.BRANCH_NBR = DEFN.BRANCH_NBR ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DEFN.REGION_CENTER_ID = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DEFN.REGION_CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND DEFN.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sb.append("AND DEFN.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND DEFN.BRANCH_NBR = :branchID "); //分行代碼
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		} else {
			sb.append("AND DEFN.BRANCH_NBR IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		sb.append("ORDER BY DEFN.REGION_CENTER_ID, DEFN.BRANCH_AREA_ID, DEFN.BRANCH_NBR, DT.TEAM_TYPE ");

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition)); 

		sendRtnObject(outputVO);
	}
	
	// 取得範例
	public void getExample(Object body, IPrimitiveMap header) throws JBranchException {
		
		CSVUtil csv = new CSVUtil();

		// 設定表頭
		csv.setHeader(headColumnMap.keySet().toArray(new String[headColumnMap.keySet().size()]));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "分行DiamondTeam人員上傳資料檔.csv");
		
		sendRtnObject(null);
	}
	
	public void add(Object body, IPrimitiveMap header) throws Exception {
		
		ORG270InputVO inputVO = (ORG270InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		// 整檔清空
		queryCondition.setQueryString("TRUNCATE TABLE TBORG_DIAMOND_TEAM ");
		dam.exeUpdate(queryCondition);
		
		int lineNum = 0;
		for (Map<String, Object> dataMap : inputVO.getTEAM_LST()) {
			lineNum++;
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("INSERT INTO TBORG_DIAMOND_TEAM (BRANCH_NBR, TEAM_TYPE, EMP_ID, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, LEADER_FLAG) ");
			sb.append("VALUES (:branchNBR, :teamType, :empID,  0, SYSDATE, :creator, :modifier, SYSDATE, :leaderFlag) ");
			queryCondition.setObject("branchNBR", dataMap.get("BRANCH_NBR"));
			queryCondition.setObject("teamType", dataMap.get("TEAM_TYPE"));
			queryCondition.setObject("empID", addZeroForNum((String) dataMap.get("EMP_ID"), 6));
			queryCondition.setObject("creator", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("modifier", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("leaderFlag", dataMap.get("LEADER_FLAG"));
			queryCondition.setQueryString(sb.toString());
			
			try {
				dam.exeUpdate(queryCondition);
			} catch (DAOException de) {
				throw new APException("上傳DiamondTeam人員資料第" + (lineNum) + "筆有誤(人員重覆上傳)");
			}
		}
		
		sendRtnObject(null);
	}
	
	public void export (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG270InputVO inputVO = (ORG270InputVO) body;
		
		List<Map<String, Object>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		String[] csvHeader = new String[] { "業務處", "營運區", "分行代碼", "分行名稱", "組別", "員工編號", "員工姓名", "是否為Team Leader"};
		String[] csvMain = new String[] { "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "TEAM_TYPE", "EMP_ID", "EMP_NAME", "LEADER_FLAG"};

		if (exportLst.size() > 0) {
			for (Map<String, Object> map : exportLst) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					records[i] = checkIsNull(map, csvMain[i]);
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
			notifyClientToDownloadFile(url, "分行DiamondTeam人員資料檔_" + sdfYYYYMMDD.format(new Date()) + ".csv");
		}
		
		sendRtnObject(null);
	}
	
	// 檢查關卡
	public void checkPoint(Object body, IPrimitiveMap header) throws Exception {
	
		ORG270InputVO inputVO = (ORG270InputVO) body;
		dam = this.getDataAccessManager();
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, Object>> inputLst = new ArrayList<Map<String, Object>>();
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				Map<String, Object> dataMap = new HashMap<String, Object>();
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				
				for (int i = 0; i < data.length; i++) {
					// EMP_ID 向左補0至6碼
					dataMap.put(headColumnMap.get(head[i].trim()), ("EMP_ID".equals(headColumnMap.get(head[i].trim())) ? addZeroForNum(data[i], 6) : data[i]));
					
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					List<Map<String, Object>> rl = new ArrayList<Map<String,Object>>();

					if (StringUtils.isNotEmpty(data[i])) {
						switch (headColumnMap.get(head[i].trim())) {
							case "EMP_ID":
								sql.append("SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = :empId ");
								queryCondition.setQueryString(sql.toString());
								queryCondition.setObject("empId", addZeroForNum(data[i], 6));
								rl = dam.exeQuery(queryCondition);
								
								if (rl.size() > 0) {
									dataMap.put("EMP_NAME", (String) rl.get(0).get("EMP_NAME"));
								} else {
									dataMap.put("EMP_NAME", "");
								}
								
								break;
							case "BRANCH_NBR":
								sql.append("SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = :deptId ");
								queryCondition.setQueryString(sql.toString());
								queryCondition.setObject("deptId", data[i]);
								rl = dam.exeQuery(queryCondition);
								
								if (rl.size() > 0) {
									dataMap.put("DEPT_NAME", (String) rl.get(0).get("DEPT_NAME"));
								} else {
									dataMap.put("DEPT_NAME", "");
								}
								
								break;
							default :
								dataMap.put(headColumnMap.get(head[i].trim()), data[i]);
								
								break;
						}
					} else {
						dataMap = new HashMap<String, Object>();
						break;
					}
				}
				
				if (dataMap.size() > 0 && StringUtils.isNotEmpty(checkIsNull(dataMap, "EMP_NAME"))) {
					inputLst.add(dataMap);
				}
			}
			
			ORG270OutputVO outputVO = new ORG270OutputVO();
			outputVO.setResultList(inputLst);
			
			sendRtnObject(outputVO);
		} else {
			sendRtnObject(null);
		}
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
