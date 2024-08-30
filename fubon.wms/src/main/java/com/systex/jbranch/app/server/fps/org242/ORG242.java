package com.systex.jbranch.app.server.fps.org242;

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

import com.systex.jbranch.app.server.fps.org260.ORG260InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org242")
@Scope("request")
public class ORG242 extends FubonWmsBizLogic {

	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();

	public ORG242() {
		headColumnMap.put("員工編號", "EMP_ID");
		headColumnMap.put("駐點分行代號", "BRANCH_NBR");
		headColumnMap.put("管轄分行代號", "SERVICE_NBR");
	}

	// 查詢
	public void query(Object body, IPrimitiveMap header) throws Exception {

		ORG242InputVO inputVO = (ORG242InputVO) body;
		ORG242OutputVO outputVO = new ORG242OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT PAO.EMP_ID, PAO.BRANCH_NBR, PAO.SERVICE_NBR, PAO.ISONTHEJOB, MEM.EMP_NAME, DEFN.BRANCH_NAME, DEFN2.BRANCH_NAME as SERVICE_NAME, PAO.MODIFIER || '-' || MOD_MEM.EMP_NAME AS MODIFIER, PAO.LASTUPDATE ");
		sb.append("FROM TBORG_PSAO PAO ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON PAO.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON PAO.BRANCH_NBR = DEFN.BRANCH_NBR ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN2 ON PAO.SERVICE_NBR = DEFN2.BRANCH_NBR ");
		sb.append("LEFT JOIN TBORG_MEMBER MOD_MEM ON PAO.MODIFIER = MOD_MEM.EMP_ID ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DEFN.REGION_CENTER_ID = :regionCenterID "); // 區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DEFN.REGION_CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND DEFN.BRANCH_AREA_ID = :branchAreaID "); // 營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sb.append("AND DEFN.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND DEFN.BRANCH_NBR = :branchID "); // 分行代碼
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		} else {
			sb.append("AND DEFN.BRANCH_NBR IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition)); // data

		sendRtnObject(outputVO);
	}

	// 取得範例
	public void getExample(Object body, IPrimitiveMap header) throws JBranchException {

		CSVUtil csv = new CSVUtil();

		// 設定表頭
		csv.setHeader(headColumnMap.keySet().toArray(new String[headColumnMap.keySet().size()]));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "消金貸後維護專員上傳資料檔.csv");

		sendRtnObject(null);
	}

	// 檢查關卡
	public void checkPoint(Object body, IPrimitiveMap header) throws Exception {

		ORG242InputVO inputVO = (ORG242InputVO) body;
		dam = this.getDataAccessManager();

		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());

			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));

			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, Object>> inputLst = new ArrayList<Map<String, Object>>();
			char[] isOnTheJob = { 'A', 'M', 'P' };
			StringBuffer str = new StringBuffer();
			int countRow = 0;
			boolean isEmpty = false;
			while ((line = br.readLine()) != null) {
				countRow++;
				String[] data = line.split(",");
				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				for (int i = 0; i < data.length; i++) {
					if ("EMP_NAME".equals(headColumnMap.get(head[i].trim())) || "DEPT_NAME".equals(headColumnMap.get(head[i].trim()))) {
					} else {
						// EMP_ID 向左補0至6碼
						dataMap.put(headColumnMap.get(head[i].trim()), ("EMP_ID".equals(headColumnMap.get(head[i].trim())) ? addZeroForNum(data[i], 6) : data[i]));

						if ("EMP_ID".equals(headColumnMap.get(head[i].trim()))) {
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append("SELECT EMP_NAME, CHANGE_FLAG FROM TBORG_MEMBER WHERE EMP_ID = :empId ");
							queryCondition.setQueryString(sql.toString());
							queryCondition.setObject("empId", addZeroForNum(data[i], 6));
							List<Map<String, Object>> rl = dam.exeQuery(queryCondition);

							if (rl.size() > 0) {
								// Map<String, Object> rm = rl.get(0);
								dataMap.put("EMP_NAME", (String) rl.get(0).get("EMP_NAME"));
								if (StringUtils.containsAny(rl.get(0).get("CHANGE_FLAG").toString(), isOnTheJob)) {
									dataMap.put("ISONTHEJOB", "Y");
								} else {
									dataMap.put("ISONTHEJOB", "N");
								}

							} else {
								dataMap.put("EMP_NAME", "");
								dataMap.put("ISONTHEJOB", "N");
								isEmpty = true;
							}
						}

						if ("BRANCH_NBR".equals(headColumnMap.get(head[i].trim()))) {
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append("SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = :deptId ");
							queryCondition.setQueryString(sql.toString());
							queryCondition.setObject("deptId", data[i]);
							List<Map<String, Object>> rl = dam.exeQuery(queryCondition);

							if (rl.size() > 0) {
								dataMap.put("BRANCH_NAME", (String) rl.get(0).get("DEPT_NAME"));
							} else {
								dataMap.put("BRANCH_NAME", "");
								isEmpty = true;
							}
						}

						if ("SERVICE_NBR".equals(headColumnMap.get(head[i].trim()))) {
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append("SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = :deptId ");
							queryCondition.setQueryString(sql.toString());
							queryCondition.setObject("deptId", data[i]);
							List<Map<String, Object>> rl = dam.exeQuery(queryCondition);

							if (rl.size() > 0) {
								dataMap.put("SERVICE_NAME", (String) rl.get(0).get("DEPT_NAME"));
							} else {
								dataMap.put("SERVICE_NAME", "");
								isEmpty = true;
							}
						}
					}
					if (isEmpty) {
						str.append(countRow + ", ");
						isEmpty = false;
					}
				}

				if (dataMap.size() > 0) {
					inputLst.add(dataMap);
				}
			}

			ORG242OutputVO outputVO = new ORG242OutputVO();
			outputVO.setResultList(inputLst);
			if(str.toString().length() > 0){
				outputVO.setEmptyColumnMessage("第" + str.toString() + "筆有空白，請檢查後重新上傳。");
			}
			sendRtnObject(outputVO);
		} else {
			sendRtnObject(null);
		}
	}

	public void add(Object body, IPrimitiveMap header) throws Exception {

		ORG242InputVO inputVO = (ORG242InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		// 整檔清空
		queryCondition.setQueryString("TRUNCATE TABLE TBORG_PSAO ");
		dam.exeUpdate(queryCondition);

		int lineNum = 0;
		for (Map<String, Object> dataMap : inputVO.getTEAM_LST()) {
			lineNum++;
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("INSERT INTO TBORG_PSAO (EMP_ID, BRANCH_NBR, SERVICE_NBR, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, ISONTHEJOB) ");
			sb.append("VALUES (:empID, :branchNBR, :serviceNBR, 0, SYSDATE, :creator, :modifier, SYSDATE, :isOnTheJob) ");
			queryCondition.setObject("empID", dataMap.get("EMP_ID"));
			queryCondition.setObject("branchNBR", dataMap.get("BRANCH_NBR"));
			queryCondition.setObject("serviceNBR", dataMap.get("SERVICE_NBR"));
			queryCondition.setObject("isOnTheJob", dataMap.get("ISONTHEJOB"));
			queryCondition.setObject("creator", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("modifier", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setQueryString(sb.toString());

			try {
				dam.exeUpdate(queryCondition);
			} catch (DAOException de) {
				throw new APException("上傳輔銷人員資料第" + (lineNum) + "筆有誤(人員之管轄行重覆上傳)");
			}
		}

		sendRtnObject(null);
	}

	public void export(Object body, IPrimitiveMap header) throws JBranchException {

		ORG242InputVO inputVO = (ORG242InputVO) body;

		List<Map<String, Object>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();

		String[] csvHeader = new String[] { "員工編號", "員工姓名", "駐點分行代碼", "駐點分行名稱", "管轄分行代碼", "管轄分行名稱", "員工是否在職", "最後修改人", "最後修改時間" };
		String[] csvMain = new String[] { "EMP_ID", "EMP_NAME", "BRANCH_NBR", "BRANCH_NAME", "SERVICE_NBR", "SERVICE_NAME", "ISONTHEJOB", "MODIFIER", "LASTUPDATE" };

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
			notifyClientToDownloadFile(url, "消金貸後維護專員資料檔_" + sdfYYYYMMDD.format(new Date()) + ".csv");
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
