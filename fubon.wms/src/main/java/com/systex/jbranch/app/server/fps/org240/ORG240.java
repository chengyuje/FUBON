package com.systex.jbranch.app.server.fps.org240;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_FAIAPK;
import com.systex.jbranch.app.common.fps.table.TBORG_FAIAVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import de.schlichtherle.io.FileInputStream;

@Component("org240")
@Scope("request")
public class ORG240 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();
	
	public ORG240 () {
		headColumnMap.put("輔銷團隊(IA/FA)", "SUPT_SALES_TEAM_ID");
		headColumnMap.put("員工編號", "EMP_ID");
		headColumnMap.put("員工姓名", "EMP_NAME");
		headColumnMap.put("分行代碼", "BRANCH_NBR");
		headColumnMap.put("分行名稱", "DEPT_NAME");
		headColumnMap.put("最後修改人", "MODIFIER");
		headColumnMap.put("最後修改時間", "LASTUPDATE");
	}
	
	public void querySuptSalesTeamLst(Object body, IPrimitiveMap header) throws Exception {

		ORG240InputVO inputVO = (ORG240InputVO) body;
		ORG240OutputVO outputVO = new ORG240OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SUPT.SUPT_SALES_TEAM_ID, SUPT.EMP_ID, SUPT.BRANCH_NBR, MEM.EMP_NAME, DEFN.DEPT_NAME, SUPT.MODIFIER, SUPT.LASTUPDATE ");
		sql.append("FROM TBORG_FAIA SUPT ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM ON SUPT.EMP_ID = MEM.EMP_ID ");
		sql.append("LEFT JOIN TBORG_DEFN DEFN ON SUPT.BRANCH_NBR = DEFN.DEPT_ID ");
		sql.append("WHERE 1=1 ");
		sql.append("AND MEM.SERVICE_FLAG = 'A' ");
		sql.append("AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		
		if (inputVO.getSUPT_SALES_TEAM_ID().trim().length() > 0) {
			sql.append(" AND SUPT.SUPT_SALES_TEAM_ID = :sId ");
			condition.setObject("sId", inputVO.getSUPT_SALES_TEAM_ID());
		}
		
		condition.setQueryString(sql.toString());
		ResultIF suptSalesTeamLst = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = suptSalesTeamLst.getTotalPage(); // 分頁用
		int totalRecord_i = suptSalesTeamLst.getTotalRecord(); // 分頁用
		outputVO.setSuptSalesTeamLst(suptSalesTeamLst); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		sendRtnObject(outputVO);
	}
	
	public void getSuptSalesTeamLst(Object body, IPrimitiveMap header) throws Exception {
		
		ORG240InputVO inputVO = (ORG240InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SUPT.SUPT_SALES_TEAM_ID, SUPT.EMP_ID, SUPT.BRANCH_NBR, MEM.EMP_NAME, DEFN.DEPT_NAME, SUPT.MODIFIER, SUPT.LASTUPDATE ");
		sql.append("FROM TBORG_FAIA SUPT ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM ON SUPT.EMP_ID = MEM.EMP_ID ");
		sql.append("LEFT JOIN TBORG_DEFN DEFN ON SUPT.BRANCH_NBR = DEFN.DEPT_ID ");
		sql.append("WHERE 1=1 ");
		sql.append("AND MEM.SERVICE_FLAG = 'A' ");
		sql.append("AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		
		if (inputVO.getSUPT_SALES_TEAM_ID().trim().length() > 0) {
			sql.append(" AND SUPT.SUPT_SALES_TEAM_ID = :sId ");
			condition.setObject("sId", inputVO.getSUPT_SALES_TEAM_ID());
		}
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> rl = dam.exeQuery(condition);
		List<Object[]> csv_list = new ArrayList<Object[]>();

		if (rl.size() > 0) {

			for (Map<String, Object> rm : rl) {
				ArrayList<String> data = new ArrayList<String>();
				for(String columnName : headColumnMap.values()) {
					if (rm.get(columnName) instanceof Timestamp) {
						Timestamp lastupdate = (Timestamp) rm.get(columnName);
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						data.add(sf.format(lastupdate));
					} else {
						data.add((String) (rm.get(columnName) == null ? "" : rm.get(columnName)));
					}
				}
				csv_list.add(data.toArray());
			}

			CSVUtil csv = new CSVUtil();

			// 設定表頭
			csv.setHeader(headColumnMap.keySet().toArray(new String[headColumnMap.keySet().size()]));
			// 添加明細的List
			csv.addRecordList(csv_list);

			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();

			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
			String fileGenTime = sf.format(System.currentTimeMillis());
			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, "輔銷團隊人員設定_" + fileGenTime + ".csv");
		}
		
		sendRtnObject(null);
	}
	
	public void uploadSuptSalesTeamLst(Object body, IPrimitiveMap header) throws Exception {
		
		ORG240InputVO inputVO = (ORG240InputVO) body;
		dam = this.getDataAccessManager();
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, String>> inputLst = new ArrayList<Map<String,String>>();
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, String> dataMap = new HashMap<String, String>();
				for (int i = 0; i < data.length; i++) {
					if ("EMP_NAME".equals(headColumnMap.get(head[i].trim())) || "DEPT_NAME".equals(headColumnMap.get(head[i].trim()))) {
					} else {
						dataMap.put(headColumnMap.get(head[i].trim()), ("EMP_ID".equals(headColumnMap.get(head[i].trim())) ? addZeroForNum(data[i], 6) : data[i]));
						
						if ("EMP_ID".equals(headColumnMap.get(head[i].trim()))) {
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append(" SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = :empId ");
							queryCondition.setQueryString(sql.toString());
							queryCondition.setObject("empId", addZeroForNum(data[i], 6));
							List<Map<String, Object>> rl = dam.exeQuery(queryCondition);
							
							if (rl.size() > 0) {
								Map<String, Object> rm = rl.get(0);
								dataMap.put("EMP_NAME", (String) rl.get(0).get("EMP_NAME"));
							} else {
								dataMap = new HashMap<String, String>();
								break;
							}
						}
						
						if ("BRANCH_NBR".equals(headColumnMap.get(head[i].trim()))) {
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append(" SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = :deptId ");
							queryCondition.setQueryString(sql.toString());
							queryCondition.setObject("deptId", data[i]);
							List<Map<String, Object>> rl = dam.exeQuery(queryCondition);
							
							if (rl.size() > 0) {
								dataMap.put("DEPT_NAME", (String) rl.get(0).get("DEPT_NAME"));
							} else {
								dataMap = new HashMap<String, String>();
								break;
							}
						}
					}
				}
				
				if (dataMap.size() > 0) {
					inputLst.add(dataMap);
				}
			}
			
			ORG240OutputVO outputVO = new ORG240OutputVO();
			outputVO.setSuptSalesTeamLst(inputLst);
			
			sendRtnObject(outputVO);
		} else {
			sendRtnObject(null);
		}
	}
	
	public void addSuptSalesTeamLst(Object body, IPrimitiveMap header) throws Exception {
		ORG240InputVO inputVO = (ORG240InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// delete old records
		HashSet<String> teamSet = new HashSet<String>(); 
		for (Map<String, String> dataMap : inputVO.getSUPT_SALES_TEAM_LST()) {
			teamSet.add(dataMap.get("SUPT_SALES_TEAM_ID"));
		}
		
		for (String teamId : teamSet) {
			StringBuffer sql = new StringBuffer();
			sql.append(" DELETE FROM TBORG_FAIA WHERE SUPT_SALES_TEAM_ID = :teamId ");
			condition.setQueryString(sql.toString());
			condition.setObject("teamId", teamId);
			dam.exeUpdate(condition);
		}
		
		int lineNum = 0;
		for (Map<String, String> dataMap : inputVO.getSUPT_SALES_TEAM_LST()) {
			lineNum++;
			TBORG_FAIAPK pk = new TBORG_FAIAPK();
			pk.setBRANCH_NBR(dataMap.get("BRANCH_NBR"));
			pk.setEMP_ID(addZeroForNum(dataMap.get("EMP_ID"), 6));
			pk.setSUPT_SALES_TEAM_ID(dataMap.get("SUPT_SALES_TEAM_ID"));
			TBORG_FAIAVO txnVO = new TBORG_FAIAVO(pk);
			try {
				dam.create(txnVO);
			} catch (DAOException de) {
				throw new APException("上傳輔銷人員資料第" + (lineNum) + "筆有誤");
			}
		}
		
		sendRtnObject(null);
	}
	
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
}
