package com.systex.jbranch.app.server.fps.org250;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_AGENT_CONSTRAINVO;
import com.systex.jbranch.app.common.fps.table.TBORG_AGENT_CONSTRAIN_REVIEWVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import de.schlichtherle.io.FileInputStream;

@Component("org250")
@Scope("request")
public class ORG250 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();
	
	public void export (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG250InputVO inputVO = (ORG250InputVO) body;
		
		List<Map<String, String>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		String[] csvHeader = new String[] { "單位", "營運區", "分行", "被代理人員編", "代理人1員編", "代理人2員編", "代理人3員編"};
		String[] csvMain = new String[] { "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "EMP_ID", "AGENT_ID_1", "AGENT_ID_2", "AGENT_ID_3"};

		if (exportLst.size() > 0) {
			for (Map<String, String> map : exportLst) {
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
			notifyClientToDownloadFile(url, "代理人名單.csv");
		}
		
		sendRtnObject(null);
	}

	public ORG250() {
		headColumnMap.put("被代理人員編", "EMP_ID");
		headColumnMap.put("代理人1員編", "AGENT_ID_1");
		headColumnMap.put("代理人2員編", "AGENT_ID_2");
		headColumnMap.put("代理人3員編", "AGENT_ID_3");
	}
	
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG250InputVO inputVO = (ORG250InputVO) body;
		ORG250OutputVO outputVO = new ORG250OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// (顯示需覆核) 0-OP主管  1-覆核主管
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRIASS.PRIVILEGEID, ROL.JOB_TITLE_NAME ");
		sql.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sql.append("LEFT JOIN TBORG_ROLE ROL ON PRIASS.ROLEID = ROL.ROLE_ID AND ROL.JOB_TITLE_NAME IS NOT NULL ");
		sql.append("WHERE PRIASS.ROLEID = :roleID ");
		sql.append("AND PRIASS.PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'ORG250' AND FUNCTIONID = 'confirm') ");
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List<Map<String, Object>> privilege = dam.exeQuery(queryCondition);
		
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sql = new StringBuffer();
		sql.append("WITH BASE AS ( ");
		sql.append("SELECT ");
		if (privilege.size() > 0) { //登入者為覆核主管
			sql.append("AGE.SEQNO, ");
		} else {
			sql.append("0 AS SEQNO, ");
		}
		sql.append("  AGE.REVIEW_STATUS, ");
		sql.append("  INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
		sql.append("  AGE.EMP_ID, INFO.EMP_NAME, ");
		sql.append("  AGE.AGENT_ID_1, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_1) AS AGENT_NAME_1, ");
		sql.append("  AGE.AGENT_ID_2, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_2) AS AGENT_NAME_2, ");
		sql.append("  AGE.AGENT_ID_3, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_3) AS AGENT_NAME_3, ");
		sql.append("  AGE.CREATOR, AGE.CREATETIME ");
		sql.append("FROM TBORG_AGENT_CONSTRAIN_REVIEW AGE ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = AGE.EMP_ID ");
		sql.append("WHERE AGE.REVIEW_STATUS = 'W' ");
		sql.append("AND INFO.EMP_ID IS NOT NULL ");
		
		
		
		if (privilege.size() > 0) { //登入者為覆核主管
			if (new BigDecimal((String) privilege.get(0).get("PRIVILEGEID")).compareTo(new BigDecimal("13")) <= 0) { // 區域中心以下
				sql.append("AND INFO.REGION_CENTER_ID IS NOT NULL");
			}
		}
		
		if (StringUtils.isNotBlank(inputVO.getEMP_ID())) {
			sql.append(" AND INFO.EMP_ID LIKE :empId ");
			queryCondition.setObject("empId", inputVO.getEMP_ID());
		}
	
		if (StringUtils.isNotBlank(inputVO.getEMP_NAME())) {
			sql.append(" AND INFO.EMP_NAME LIKE :empName ");
			queryCondition.setObject("empName", inputVO.getEMP_NAME());
		}
	
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sql.append(" AND INFO.REGION_CENTER_ID = :rcId ");
			queryCondition.setObject("rcId", inputVO.getRegion_center_id());
		} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
			sql.append(" AND (INFO.REGION_CENTER_ID IN (:rcIdList) OR INFO.REGION_CENTER_ID IS NULL) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sql.append(" AND INFO.BRANCH_AREA_ID = :opId ");
			queryCondition.setObject("opId", inputVO.getBranch_area_id());
		} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
			sql.append(" AND (INFO.BRANCH_AREA_ID IN (:opIdList) OR INFO.BRANCH_AREA_ID IS NULL) ");
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sql.append(" AND INFO.BRANCH_NBR = :brNbr ");
			queryCondition.setObject("brNbr", inputVO.getBranch_nbr());
		} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
			sql.append(" AND (INFO.BRANCH_NBR IN (:brNbrList) OR INFO.BRANCH_NBR IS NULL) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		sql.append(") ");

		sql.append("SELECT SEQNO, REVIEW_STATUS, ORDER_NUM, ");
		sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sql.append("       EMP_ID, EMP_NAME, ");
		sql.append("       AGENT_ID_1, AGENT_NAME_1, ");
		sql.append("       AGENT_ID_2, AGENT_NAME_2, ");
		sql.append("       AGENT_ID_3, AGENT_NAME_3, ");
		sql.append("       CREATOR, CREATETIME, ");
		sql.append("       (SELECT COUNT(1) FROM TBORG_AGENT_CONSTRAIN_REVIEW ACR WHERE ACR.EMP_ID = B.EMP_ID) AS COUNTS ");
		sql.append("FROM ( ");
		sql.append("  SELECT SEQNO, REVIEW_STATUS, 0 AS ORDER_NUM, ");
		sql.append("         REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sql.append("         EMP_ID, EMP_NAME, ");
		sql.append("         AGENT_ID_1, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = BASE.AGENT_ID_1) AS AGENT_NAME_1, ");
		sql.append("         AGENT_ID_2, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = BASE.AGENT_ID_2) AS AGENT_NAME_2, ");
		sql.append("         AGENT_ID_3, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = BASE.AGENT_ID_3) AS AGENT_NAME_3, ");
		sql.append("         CREATOR, CREATETIME ");
		sql.append("  FROM BASE ");
		sql.append("  UNION ");
		sql.append("  SELECT null AS SEQNO, REVIEW_STATUS, 1 AS ORDER_NUM, ");
		sql.append("         INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
		sql.append("         AGE.EMP_ID, INFO.EMP_NAME, ");
		sql.append("         AGE.AGENT_ID_1, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_1) AS AGENT_NAME_1, ");
		sql.append("         AGE.AGENT_ID_2, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_2) AS AGENT_NAME_2, ");
		sql.append("         AGE.AGENT_ID_3, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_3) AS AGENT_NAME_3, ");
		// mantis 4549
		sql.append("         AGE.MODIFIER AS CREATOR, AGE.LASTUPDATE AS CREATETIME ");
		sql.append("  FROM TBORG_AGENT_CONSTRAIN AGE ");
		sql.append("  LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = AGE.EMP_ID ");
		sql.append("  WHERE 1 = 1 ");
		sql.append("  AND AGE.REVIEW_STATUS = 'Y' ");
		sql.append("  AND AGE.EMP_ID NOT IN (SELECT EMP_ID FROM BASE) ");
		sql.append("  AND INFO.EMP_ID IS NOT NULL ");
		
		if (StringUtils.isNotBlank(inputVO.getEMP_ID())) {
			sql.append(" AND INFO.EMP_ID LIKE :empId ");
			queryCondition.setObject("empId", inputVO.getEMP_ID());
		}
	
		if (StringUtils.isNotBlank(inputVO.getEMP_NAME())) {
			sql.append(" AND INFO.EMP_NAME LIKE :empName ");
			queryCondition.setObject("empName", inputVO.getEMP_NAME());
		}
		
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sql.append(" AND INFO.REGION_CENTER_ID = :rcId ");
			queryCondition.setObject("rcId", inputVO.getRegion_center_id());
		} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
			sql.append(" AND (INFO.REGION_CENTER_ID IN (:rcIdList) OR INFO.REGION_CENTER_ID IS NULL) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sql.append(" AND INFO.BRANCH_AREA_ID = :opId ");
			queryCondition.setObject("opId", inputVO.getBranch_area_id());
		} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
			sql.append(" AND (INFO.BRANCH_AREA_ID IN (:opIdList) OR INFO.BRANCH_AREA_ID IS NULL) ");
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sql.append(" AND INFO.BRANCH_NBR = :brNbr ");
			queryCondition.setObject("brNbr", inputVO.getBranch_nbr());
		} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
			sql.append(" AND (INFO.BRANCH_NBR IN (:brNbrList) OR INFO.BRANCH_NBR IS NULL) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		sql.append(") B ");
//		sql.append("SELECT SEQNO, REVIEW_STATUS, 0 AS ORDER_NUM, ");
//		sql.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
//		sql.append("       EMP_ID, EMP_NAME, ");
//		sql.append("       AGENT_ID_1, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = BASE.AGENT_ID_1) AS AGENT_NAME_1, ");
//		sql.append("       AGENT_ID_2, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = BASE.AGENT_ID_2) AS AGENT_NAME_2, ");
//		sql.append("       AGENT_ID_3, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = BASE.AGENT_ID_3) AS AGENT_NAME_3, ");
//		sql.append("       CREATOR, CREATETIME ");
//		sql.append("FROM BASE ");
//		sql.append("UNION ");
//		sql.append("SELECT null AS SEQNO, REVIEW_STATUS, 1 AS ORDER_NUM, ");
//		sql.append("       INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
//		sql.append("       AGE.EMP_ID, INFO.EMP_NAME, ");
//		sql.append("       AGE.AGENT_ID_1, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_1) AS AGENT_NAME_1, ");
//		sql.append("       AGE.AGENT_ID_2, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_2) AS AGENT_NAME_2, ");
//		sql.append("       AGE.AGENT_ID_3, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = AGE.AGENT_ID_3) AS AGENT_NAME_3, ");
//		sql.append("       AGE.CREATOR, AGE.CREATETIME ");
//		sql.append("FROM TBORG_AGENT_CONSTRAIN AGE ");
//		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = AGE.EMP_ID ");
//		sql.append("WHERE 1 = 1 ");
//		sql.append("AND AGE.REVIEW_STATUS = 'Y' ");
//		sql.append("AND AGE.EMP_ID NOT IN (SELECT EMP_ID FROM BASE) ");
//		sql.append("AND INFO.EMP_ID IS NOT NULL ");
		


		sql.append("ORDER BY ORDER_NUM, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ");
		
		queryCondition.setQueryString(sql.toString());
	
		outputVO.setAgentLst(dam.exeQuery(queryCondition)); // data
		
		sendRtnObject(outputVO);
	}
	
	public void getExample(Object body, IPrimitiveMap header) throws Exception {
		CSVUtil csv = new CSVUtil();

		// 設定表頭
		csv.setHeader(headColumnMap.keySet().toArray(new String[headColumnMap.keySet().size()]));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "代理人名單.csv");
		sendRtnObject(null);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		
		ORG250InputVO inputVO = (ORG250InputVO) body;
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> bmmgrMap   = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> fcMap      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> psopMap    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> faiaMap    = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2);
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));

			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, String>> inputLst = new ArrayList<Map<String,String>>();
			dam = this.getDataAccessManager();
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, String> dataMap = new HashMap<String, String>();
				for (int i = 0; i < data.length; i++) {
					if (("EMP_ID".equals(headColumnMap.get(head[i]))) || (headColumnMap.get(head[i]).startsWith("AGENT_ID_"))) {
						dataMap.put(headColumnMap.get(head[i]), addZeroForNum(data[i], 6));
					} else {
						dataMap.put(headColumnMap.get(head[i]), data[i]);
					}
					
					if (StringUtils.isNotBlank(data[i])) {
						if ("EMP_ID".equals(headColumnMap.get(head[i]))) {
							QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append(" SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :empId ");
							condition.setQueryString(sql.toString());
							condition.setObject("empId", addZeroForNum(data[i], 6));

							List<Map<String, Object>> rl = dam.exeQuery(condition);
							if (rl.size() > 0) {
								Map<String, Object> rm = rl.get(0);
								dataMap.put("REGION_CENTER_ID", (String) rm.get("REGION_CENTER_ID"));
								dataMap.put("REGION_CENTER_NAME", (String) rm.get("REGION_CENTER_NAME"));
								dataMap.put("BRANCH_AREA_ID", (String) rm.get("BRANCH_AREA_ID"));
								dataMap.put("BRANCH_AREA_NAME", (String) rm.get("BRANCH_AREA_NAME"));
								dataMap.put("BRANCH_NBR", (String) rm.get("BRANCH_NBR"));
								dataMap.put("BRANCH_NAME", (String) rm.get("BRANCH_NAME"));
								dataMap.put("EMP_NAME", (String) rm.get("EMP_NAME"));
							} else {
								throw new APException(addZeroForNum(data[i], 6) + "-員工代號有誤！");
							}
						}
						
						if (headColumnMap.get(head[i]).startsWith("AGENT_ID_")) {
							QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append(" SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = :empId ");
							condition.setQueryString(sql.toString());
							condition.setObject("empId", addZeroForNum(data[i], 6));
							List<Map<String, Object>> rl = dam.exeQuery(condition);
							if (rl.size() > 0) {
								Map<String, Object> rm = rl.get(0);
								dataMap.put("AGENT_NAME_" + headColumnMap.get(head[i]).replace("AGENT_ID_", ""), (String) rm.get("EMP_NAME"));
							} else {
								throw new APException(addZeroForNum(data[i], 6) + "-員工代號有誤！");
							}
							
						}
					}
				}
				
				if (bmmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
					fcMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
					psopMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
					faiaMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行

					if (dataMap.size() > 0 && 
						StringUtils.isNotBlank(dataMap.get("BRANCH_NBR")) && 
						(((String)((List) getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST)).get(0)).indexOf(dataMap.get("BRANCH_NBR")) > -1)) {
						inputLst.add(dataMap);
					}
					
				} else if (mbrmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運區
					if (dataMap.size() > 0 && 
						StringUtils.isNotBlank(dataMap.get("BRANCH_AREA_ID")) && 
						(((String)((List) getUserVariable(FubonSystemVariableConsts.AVAILAREALIST)).get(0)).indexOf(dataMap.get("BRANCH_AREA_ID")) > -1)) {
						inputLst.add(dataMap);
					}
				} else if (armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 業務處
					if (dataMap.size() > 0 && 
						StringUtils.isNotBlank(dataMap.get("REGION_CENTER_ID")) && 
						(((String)((List) getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST)).get(0)).indexOf(dataMap.get("REGION_CENTER_ID")) > -1)) {
						inputLst.add(dataMap);
					}
				} else if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 總行
					inputLst.add(dataMap);
				}
			}
			ORG250OutputVO outputVO = new ORG250OutputVO();
			outputVO.setAgentLst(inputLst);
			sendRtnObject(outputVO);
		} else {
			sendRtnObject(null);
		}
	}
	
	//上傳新增
	public void confirm(Object body, IPrimitiveMap header) throws Exception {
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG250InputVO inputVO = (ORG250InputVO) body;
		dam = this.getDataAccessManager();
		
		for (Map<String, String> dataMap : inputVO.getAGENT_LST()) {
			StringBuffer sql = new StringBuffer();
			QueryConditionIF queryCondition = null;
			List<Map<String, Object>> reviewCount = null;
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			sql.append("SELECT A.COUNTS , B.AL_COUNTS ");
			sql.append("FROM ");
			sql.append("(SELECT COUNT(EMP_ID) AS COUNTS FROM TBORG_AGENT_CONSTRAIN_REVIEW WHERE 1=1 AND REVIEW_STATUS = 'W' AND EMP_ID =:emp_id ) A , ");
			sql.append("(SELECT COUNT(EMP_ID) AS AL_COUNTS FROM TBORG_AGENT_CONSTRAIN_REVIEW WHERE 1=1 AND ACT_TYPE IN ('A','D') AND REVIEW_STATUS = 'Y' AND EMP_ID =:emp_id) B ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("emp_id", dataMap.get("EMP_ID").toString());
			
			reviewCount = dam.exeQuery(queryCondition);
			
			TBORG_AGENT_CONSTRAINVO vo = new TBORG_AGENT_CONSTRAINVO();
			vo =  (TBORG_AGENT_CONSTRAINVO) dam.findByPKey(TBORG_AGENT_CONSTRAINVO.TABLE_UID, dataMap.get("EMP_ID").toString());
			//新增
			if(null == vo || (((BigDecimal) reviewCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) == 0 && ((BigDecimal) reviewCount.get(0).get("AL_COUNTS")).compareTo(new BigDecimal(0)) == 0)){
				BigDecimal seqno = new BigDecimal(getSEQ()); //產生序號
				Timestamp currentTM = new Timestamp(System.currentTimeMillis());
				TBORG_AGENT_CONSTRAIN_REVIEWVO rev_vo = (TBORG_AGENT_CONSTRAIN_REVIEWVO) dam.findByPKey(TBORG_AGENT_CONSTRAIN_REVIEWVO.TABLE_UID,seqno);
				TBORG_AGENT_CONSTRAIN_REVIEWVO txnVO = (rev_vo == null ? new TBORG_AGENT_CONSTRAIN_REVIEWVO() : rev_vo); 
				txnVO.setSEQNO(seqno);
				txnVO.setEMP_ID(dataMap.get("EMP_ID"));
				txnVO.setAGENT_ID_1(dataMap.get("AGENT_ID_1"));
				txnVO.setAGENT_ID_2(dataMap.get("AGENT_ID_2"));
				txnVO.setAGENT_ID_3(dataMap.get("AGENT_ID_3"));
				txnVO.setACT_TYPE("A");
				txnVO.setREVIEW_STATUS("W");
				txnVO.setCreatetime(currentTM);
				txnVO.setCreator(ws.getUser().getUserID());
				txnVO.setModifier(ws.getUser().getUserID());
				txnVO.setLastupdate(currentTM);
				dam.create(txnVO);
			//修改
			} else if(((BigDecimal) reviewCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) == 0 && ((BigDecimal) reviewCount.get(0).get("AL_COUNTS")).compareTo(new BigDecimal(0)) > 0){
				BigDecimal seqno = new BigDecimal(getSEQ()); //產生序號
				Timestamp currentTM = new Timestamp(System.currentTimeMillis());
				TBORG_AGENT_CONSTRAIN_REVIEWVO rev_vo = (TBORG_AGENT_CONSTRAIN_REVIEWVO) dam.findByPKey(TBORG_AGENT_CONSTRAIN_REVIEWVO.TABLE_UID,seqno);
				TBORG_AGENT_CONSTRAIN_REVIEWVO txnVO = (rev_vo == null ? new TBORG_AGENT_CONSTRAIN_REVIEWVO() : rev_vo); 
				txnVO.setSEQNO(seqno);
				txnVO.setEMP_ID(dataMap.get("EMP_ID"));
				txnVO.setAGENT_ID_1(dataMap.get("AGENT_ID_1"));
				txnVO.setAGENT_ID_2(dataMap.get("AGENT_ID_2"));
				txnVO.setAGENT_ID_3(dataMap.get("AGENT_ID_3"));
				txnVO.setACT_TYPE("M");
				txnVO.setREVIEW_STATUS("W");
				txnVO.setCreatetime(currentTM);
				txnVO.setCreator(ws.getUser().getUserID());
				txnVO.setModifier(ws.getUser().getUserID());
				txnVO.setLastupdate(currentTM);
				dam.create(txnVO);
			}
			
			sendRtnObject(null);
		}
	}
	
	//刪除
	public void delete(Object body, IPrimitiveMap header) throws Exception {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG250InputVO inputVO = (ORG250InputVO) body;
		dam = this.getDataAccessManager();
		
		BigDecimal seqno = new BigDecimal(getSEQ()); //產生序號
		
		TBORG_AGENT_CONSTRAIN_REVIEWVO agentVO = (TBORG_AGENT_CONSTRAIN_REVIEWVO) dam.findByPKey(TBORG_AGENT_CONSTRAIN_REVIEWVO.TABLE_UID, seqno);
		if(agentVO == null){
			TBORG_AGENT_CONSTRAIN_REVIEWVO VO = new TBORG_AGENT_CONSTRAIN_REVIEWVO();
			VO.setSEQNO(seqno);
			VO.setEMP_ID(inputVO.getEMP_ID().trim());
			VO.setAGENT_ID_1(inputVO.getAGENT_ID_1());
			VO.setAGENT_ID_2(inputVO.getAGENT_ID_2());
			VO.setAGENT_ID_3(inputVO.getAGENT_ID_3());
			VO.setACT_TYPE("D");
			VO.setREVIEW_STATUS("W");
			
			dam.create(VO);
			
			sendRtnObject(null);
		}
	}
	
	public void review (Object body, IPrimitiveMap header) throws Exception {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG250InputVO inputVO = (ORG250InputVO) body;
		dam = this.getDataAccessManager();
		
		BigDecimal seqno = new BigDecimal(inputVO.getSEQNO()); //產生序號
		
		TBORG_AGENT_CONSTRAIN_REVIEWVO rev_vo = new TBORG_AGENT_CONSTRAIN_REVIEWVO();
		rev_vo = (TBORG_AGENT_CONSTRAIN_REVIEWVO) dam.findByPKey(TBORG_AGENT_CONSTRAIN_REVIEWVO.TABLE_UID, seqno);
		
		if ("A".equals(rev_vo.getACT_TYPE())) {
			TBORG_AGENT_CONSTRAINVO vo = new TBORG_AGENT_CONSTRAINVO();
			vo =  (TBORG_AGENT_CONSTRAINVO) dam.findByPKey(TBORG_AGENT_CONSTRAINVO.TABLE_UID, rev_vo.getEMP_ID());
			if(vo == null){
				vo = new TBORG_AGENT_CONSTRAINVO();
				vo.setEMP_ID(inputVO.getEMP_ID());
				vo.setAGENT_ID_1((StringUtils.isNotBlank(rev_vo.getAGENT_ID_1()) ? rev_vo.getAGENT_ID_1() : null));
				vo.setAGENT_ID_2((StringUtils.isNotBlank(rev_vo.getAGENT_ID_2()) ? rev_vo.getAGENT_ID_2() : null));
				vo.setAGENT_ID_3((StringUtils.isNotBlank(rev_vo.getAGENT_ID_3()) ? rev_vo.getAGENT_ID_3() : null));
				vo.setACT_TYPE(rev_vo.getACT_TYPE().trim());
				vo.setREVIEW_STATUS("Y");
				dam.create(vo);
			}
		} else if ("M".equals(rev_vo.getACT_TYPE())) {
			TBORG_AGENT_CONSTRAINVO vo = new TBORG_AGENT_CONSTRAINVO();
			vo = (TBORG_AGENT_CONSTRAINVO) dam.findByPKey(TBORG_AGENT_CONSTRAINVO.TABLE_UID, rev_vo.getEMP_ID());
			vo.setAGENT_ID_1((StringUtils.isNotBlank(rev_vo.getAGENT_ID_1()) ? rev_vo.getAGENT_ID_1() : null));
			vo.setAGENT_ID_2((StringUtils.isNotBlank(rev_vo.getAGENT_ID_2()) ? rev_vo.getAGENT_ID_2() : null));
			vo.setAGENT_ID_3((StringUtils.isNotBlank(rev_vo.getAGENT_ID_3()) ? rev_vo.getAGENT_ID_3() : null));
			vo.setREVIEW_STATUS("Y");
			vo.setACT_TYPE(rev_vo.getACT_TYPE());
			vo.setModifier(ws.getUser().getUserID());
			vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
			dam.update(vo);
		} else if ("D".equals(rev_vo.getACT_TYPE())) {
			TBORG_AGENT_CONSTRAINVO vo = new TBORG_AGENT_CONSTRAINVO();
			vo = (TBORG_AGENT_CONSTRAINVO) dam.findByPKey(TBORG_AGENT_CONSTRAINVO.TABLE_UID, rev_vo.getEMP_ID());
			
			if(vo != null){
				dam.delete(vo);
			}
		}
		
		if(rev_vo != null){
			rev_vo.setREVIEW_STATUS("Y");
		}
		
		dam.update(rev_vo);
		
		sendRtnObject(null);
	}
	
	public void reback(Object body, IPrimitiveMap header) throws Exception {
		
		ORG250InputVO inputVO = (ORG250InputVO) body;
		dam = this.getDataAccessManager();
		
		BigDecimal seqno = new BigDecimal(inputVO.getSEQNO().trim()); //產生序號
		TBORG_AGENT_CONSTRAIN_REVIEWVO rev_vo = (TBORG_AGENT_CONSTRAIN_REVIEWVO) dam.findByPKey(TBORG_AGENT_CONSTRAIN_REVIEWVO.TABLE_UID,seqno);
		if(rev_vo != null){
			rev_vo.setREVIEW_STATUS("N");
			dam.update(rev_vo);
		}
		
		sendRtnObject(null);
	}
	
	public void alert(Object body, IPrimitiveMap header) throws Exception {
		
		ORG250InputVO inputVO = (ORG250InputVO) body;
		ORG250OutputVO outputVO = new ORG250OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT AGENT.SEQNO, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NAME, INFO.EMP_NAME, AGENT.EMP_ID, AGENT.AGENT_ID_1, ");
		sql.append("AGENT.AGENT_ID_2, AGENT.AGENT_ID_3, AGENT.ACT_TYPE, AGENT.REVIEW_STATUS, AGENT.CREATETIME, AGENT.CREATOR, AGENT.MODIFIER, ");
		sql.append("AGENT.LASTUPDATE, A.EMP_NAME AS AGENT_NAME_1, B.EMP_NAME AS AGENT_NAME_2, C.EMP_NAME  AS AGENT_NAME_3 ");	
		sql.append("FROM TBORG_AGENT_CONSTRAIN_REVIEW AGENT ");
		sql.append("LEFT JOIN TBORG_MEMBER A ON AGENT.AGENT_ID_1 = A.EMP_ID AND A.SERVICE_FLAG = 'A' AND A.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sql.append("LEFT JOIN TBORG_MEMBER B ON AGENT.AGENT_ID_2 = B.EMP_ID AND B.SERVICE_FLAG = 'A' AND B.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sql.append("LEFT JOIN TBORG_MEMBER C ON AGENT.AGENT_ID_3 = C.EMP_ID AND C.SERVICE_FLAG = 'A' AND C.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON AGENT.EMP_ID = INFO.EMP_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND AGENT.EMP_ID = :emp_id ");
		sql.append("AND INFO.EMP_ID IS NOT NULL ");
		sql.append("ORDER BY AGENT.LASTUPDATE DESC ");
		
		queryCondition.setObject("emp_id", inputVO.getEMP_ID().trim());
		
		queryCondition.setQueryString(sql.toString());

		outputVO.setAlertLst(dam.exeQuery(queryCondition)); // data
		
		sendRtnObject(outputVO);
	}
	
	/**產生seq No */
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBORG_AGENT_CONSTRAIN_RW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull(Map<String, String> map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
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
