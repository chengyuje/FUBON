package com.systex.jbranch.app.server.fps.org210;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_DEFNVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org210")
@Scope("request")
public class ORG210 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void export (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		ORG210InputVO inputVO = (ORG210InputVO) body;
		ORG210OutputVO outputVO = new ORG210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM VWORG_DEPT_BR ");
		sb.append(") ");
		sb.append("SELECT RC.DEPT_ID AS REGION_CENTER_ID, RC.DEPT_NAME AS REGION_CENTER_NAME, ");
		sb.append("       OP.DEPT_ID AS BRANCH_AREA_ID, OP.DEPT_NAME AS BRANCH_AREA_NAME, ");
		sb.append("       BR.DEPT_ID AS BRANCH_NBR, BR.DEPT_NAME AS BRANCH_NAME, ");
		sb.append("       EMP_ID, EMP_NAME, EMP_EMAIL_ADDRESS, GROUP_TYPE, AO_CODE, JOB_TITLE_NAME, PRIVILEGEID ");
		sb.append("FROM ( ");
		sb.append("  SELECT MEM.DEPT_ID, MEM.EMP_ID, MEM.EMP_NAME, MEM.EMP_EMAIL_ADDRESS, MEM.GROUP_TYPE, AO.AO_CODE, MEM.JOB_TITLE_NAME, PRI.PRIVILEGEID ");
		sb.append("  FROM TBORG_MEMBER MEM ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON MEM.EMP_ID = AO.EMP_ID AND AO.TYPE = '1' ");
		sb.append("  LEFT JOIN (SELECT P.PRIVILEGEID, R.ROLE_ID, R.ROLE_NAME, R.JOB_TITLE_NAME FROM TBSYSSECUROLPRIASS P LEFT JOIN TBORG_ROLE R ON P.ROLEID = R.ROLE_ID) PRI ON MEM.JOB_TITLE_NAME = PRI.JOB_TITLE_NAME ");
		sb.append("  WHERE MEM.DEPT_ID IN ( ");
		sb.append("    SELECT DISTINCT DEFN2.DEPT_ID ");
		sb.append("    FROM TBORG_DEFN DEFN ");
		sb.append("    LEFT JOIN TBORG_DEFN DEFN2 ON DEFN.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
		sb.append("    START WITH DEFN2.DEPT_ID = :deptId ");
		sb.append("    CONNECT BY PRIOR DEFN2.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
		sb.append("  ) ");
		sb.append("  AND SERVICE_FLAG = 'A' ");
		sb.append("  AND CHANGE_FLAG IN ('A', 'M', 'P') ");
				  
		sb.append("  UNION ");
				  
		sb.append("  SELECT PT.DEPT_ID, PT.EMP_ID, MEM.EMP_NAME, MEM.EMP_EMAIL_ADDRESS, MEM.GROUP_TYPE, AO.AO_CODE, PT.JOB_TITLE_NAME, PRI.PRIVILEGEID ");
		sb.append("  FROM TBORG_MEMBER_PLURALISM PT ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = PT.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON PT.EMP_ID = AO.EMP_ID AND AO.TYPE = '1' ");
		sb.append("  LEFT JOIN (SELECT P.PRIVILEGEID, R.ROLE_ID, R.ROLE_NAME, R.JOB_TITLE_NAME FROM TBSYSSECUROLPRIASS P LEFT JOIN TBORG_ROLE R ON P.ROLEID = R.ROLE_ID) PRI ON PT.JOB_TITLE_NAME = PRI.JOB_TITLE_NAME ");
		sb.append("  WHERE (TRUNC(PT.TERDTE) >= TRUNC(SYSDATE) OR PT.TERDTE IS NULL) ");
		sb.append("  AND PT.ACTION <> 'D' ");
		sb.append("  AND PT.DEPT_ID IN ( ");
		sb.append("    SELECT DISTINCT DEFN2.DEPT_ID ");
		sb.append("    FROM TBORG_DEFN DEFN ");
		sb.append("    LEFT JOIN TBORG_DEFN DEFN2 ON DEFN.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
		sb.append("    START WITH DEFN2.DEPT_ID = :deptId ");
		sb.append("    CONNECT BY PRIOR DEFN2.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
		sb.append("  ) ");
		sb.append(") MBR ");
		sb.append("LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '50') BR ON MBR.DEPT_ID = BR.DEPT_ID ");
		sb.append("LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '40') OP ON BR.PARENT_DEPT_ID = OP.DEPT_ID OR MBR.DEPT_ID = OP.DEPT_ID ");
		sb.append("LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '30') RC ON OP.PARENT_DEPT_ID = RC.DEPT_ID OR MBR.DEPT_ID = RC.DEPT_ID ");

		sb.append("ORDER BY DECODE (PRIVILEGEID, '012', 0, '011', 1, '010', 2, '009', 3, '002', 4, '003', 5, '001', 6, 99), PRIVILEGEID, JOB_TITLE_NAME DESC ");
		
		condition.setQueryString(sb.toString());
		condition.setObject("deptId", inputVO.getDEPT_ID());
		
		List<Map<String, String>> list = dam.exeQuery(condition);
		for (Map<String, String> map : list) {
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT DISTINCT ROLE_NAME ");
			sb.append("FROM TBORG_MEMBER_ROLE MR ");
			sb.append("LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
			sb.append("WHERE EMP_ID = :empID ");
			
			condition.setQueryString(sb.toString());
			condition.setObject("empID", map.get("EMP_ID"));
			List<Map<String, Object>> roleList = dam.exeQuery(condition);
			StringBuffer sb2  = new StringBuffer();
			for(Map<String,Object> map2: roleList){
				sb2.append((String)map2.get("ROLE_NAME")).append("\n");
			}
			String roleName_str = sb2.toString();
			map.put("ROLE_LIST", roleName_str);
		}
		

		String fileName = "人員職務&角色明細_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		String[] headerLine = {"單位", "營運區", "分行代號", "營業單位", "職務", "業務組別", "員工編號", "AO CODE", "員工姓名", "角色清單"};
		String[] mainLine   = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "JOB_TITLE_NAME", "GROUP_TYPE", "EMP_ID", "AO_CODE", "EMP_NAME", "ROLE_LIST"};

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("人員職務&角色明細_" + sdfYYYYMMDD.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
		// 表頭 CELL型式
		XSSFCellStyle headingStyle = wb.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);
		
		Integer index = 0; // first row
		
		// Heading
		XSSFRow row = sheet.createRow(index);
		
		row = sheet.createRow(index);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
		}
		
		index++;
		
		// 資料 CELL型式
		XSSFCellStyle mainStyle = wb.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		
		for (Map<String, String> map : list) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				if (StringUtils.equals("ROLE_LIST", mainLine[j])) {
					String roleList = checkIsNull(map, mainLine[j]).replaceAll("\n|\r", "/");
					cell.setCellValue(roleList.length() > 0 ? roleList.substring(0, checkIsNull(map, mainLine[j]).length() - 1) : "");
				} else {
					cell.setCellValue(checkIsNull(map, mainLine[j]));
				}
			}
			
			index++;
		}
		
		wb.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
		
//		String[] csvHeader = new String[] {"職務", "業務組別", "員工編號", "AO CODE", "員工姓名", "角色清單"};
//		String[] csvMain = new String[] { "JOB_TITLE_NAME", "GROUP_TYPE", "EMP_ID", "AO_CODE", "EMP_NAME", "ROLE_LIST"};
//		List<Object[]> csvData = new ArrayList<Object[]>();
//
//		if (list.size() > 0) {
//			for (Map<String, String> map : list) {
//				String[] records = new String[csvHeader.length];
//				for (int i = 0; i < csvHeader.length; i++) {
//					if (StringUtils.equals("ROLE_LIST", csvMain[i]))	{
//						String roleList = checkIsNull(map, csvMain[i]).replaceAll("\n|\r", "/");
//						records[i] = roleList.length() > 0 ? roleList.substring(0, checkIsNull(map, csvMain[i]).length() - 1) : "";
//					} else {
//						records[i] = checkIsNull(map, csvMain[i]);
//					}
//				}
//				
//				csvData.add(records);
//			}
//			
//			CSVUtil csv = new CSVUtil();
//			
//			// 設定表頭
//			csv.setHeader(csvHeader);
//			// 添加明細的List
//			csv.addRecordList(csvData);
//
//			// 執行產生csv并收到該檔的url
//			String url = csv.generateCSV();
//
//			// 將url送回FlexClient存檔
//			notifyClientToDownloadFile(url, "人員角色明細檔_" + inputVO.getDEPT_NAME() + ".csv");
//		}
		
		sendRtnObject(null);
	}
	
	public void getTopMostDept(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210OutputVO outputVO = new ORG210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
		sb.append("FROM TBORG_DEFN ");
		sb.append("WHERE ORG_TYPE = '00' ");
		sb.append("ORDER BY DEPT_ID, DEPT_NAME ");
		condition.setQueryString(sb.toString());

		outputVO.setParentLst(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
	
	
	public void getDeptDetail(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210InputVO inputVO = (ORG210InputVO) body;
		ORG210OutputVO outputVO = new ORG210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
		sb.append("FROM TBORG_DEFN ");
		sb.append("WHERE DEPT_ID = :deptId ");
		sb.append("ORDER BY DEPT_ID, DEPT_NAME ");
		condition.setQueryString(sb.toString());
		condition.setObject("deptId", inputVO.getDEPT_ID());
		outputVO.setDeptDetail(dam.exeQuery(condition));
		
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT EMP_ID, EMP_NAME, EMP_EMAIL_ADDRESS, GROUP_TYPE, AO_CODE, JOB_TITLE_NAME, PRIVILEGEID ");
		sb.append("FROM ( ");
		sb.append("  SELECT MEM.EMP_ID, MEM.EMP_NAME, MEM.EMP_EMAIL_ADDRESS, MEM.GROUP_TYPE, AO.AO_CODE, MEM.JOB_TITLE_NAME, PRI.PRIVILEGEID ");
		sb.append("  FROM TBORG_MEMBER MEM ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON MEM.EMP_ID = AO.EMP_ID AND AO.TYPE = '1' ");
		sb.append("  LEFT JOIN (SELECT P.PRIVILEGEID, R.ROLE_ID, R.ROLE_NAME, R.JOB_TITLE_NAME FROM TBSYSSECUROLPRIASS P LEFT JOIN TBORG_ROLE R ON P.ROLEID = R.ROLE_ID) PRI ON MEM.JOB_TITLE_NAME = PRI.JOB_TITLE_NAME ");
		sb.append("  WHERE MEM.DEPT_ID = :deptId ");
		sb.append("  AND SERVICE_FLAG = 'A' ");
		sb.append("  AND CHANGE_FLAG IN ('A', 'M', 'P') ");
		  
		sb.append("  UNION ");
		  
		sb.append("  SELECT PT.EMP_ID, MEM.EMP_NAME, MEM.EMP_EMAIL_ADDRESS, MEM.GROUP_TYPE, AO.AO_CODE, PT.JOB_TITLE_NAME, PRI.PRIVILEGEID ");
		sb.append("  FROM TBORG_MEMBER_PLURALISM PT ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = PT.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON PT.EMP_ID = AO.EMP_ID AND AO.TYPE = '1' ");
		sb.append("  LEFT JOIN (SELECT P.PRIVILEGEID, R.ROLE_ID, R.ROLE_NAME, R.JOB_TITLE_NAME FROM TBSYSSECUROLPRIASS P LEFT JOIN TBORG_ROLE R ON P.ROLEID = R.ROLE_ID) PRI ON PT.JOB_TITLE_NAME = PRI.JOB_TITLE_NAME ");
		sb.append("  WHERE (TRUNC(PT.TERDTE) >= TRUNC(SYSDATE) OR PT.TERDTE IS NULL) ");
		sb.append("  AND PT.ACTION <> 'D' ");
		sb.append("  AND PT.DEPT_ID = :deptId ");
		sb.append(") ");

		sb.append("ORDER BY DECODE (PRIVILEGEID, '012', 0, '011', 1, '010', 2, '009', 3, '002', 4, '003', 5, '001', 6, 99), PRIVILEGEID, JOB_TITLE_NAME DESC ");
		
		condition.setQueryString(sb.toString());
		condition.setObject("deptId", inputVO.getDEPT_ID());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		for (Map<String, Object> map : list) {
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT DISTINCT ROLE_NAME ");
			sb.append("FROM TBORG_MEMBER_ROLE MR ");
			sb.append("LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
			sb.append("WHERE EMP_ID = :empID ");
			
			condition.setQueryString(sb.toString());
			condition.setObject("empID", map.get("EMP_ID"));
			List<Map<String, Object>> roleList = dam.exeQuery(condition);
			StringBuffer sb2  = new StringBuffer();
			for(Map<String,Object> map2: roleList){
				sb2.append((String)map2.get("ROLE_NAME")).append("\n");
			}
			String roleName_str = sb2.toString();
			map.put("ROLE_LIST", roleName_str);
		}
		
		outputVO.setDeptEmpLst(list);
		
		sendRtnObject(outputVO);
	}
	
	public void getSubDeptLst(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210InputVO inputVO = (ORG210InputVO) body;
		ORG210OutputVO outputVO = new ORG210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
		sb.append("FROM TBORG_DEFN ");
		sb.append("WHERE PARENT_DEPT_ID = :deptId ");
		sb.append("ORDER BY DEPT_ID, DEPT_NAME ");
		condition.setQueryString(sb.toString());
		condition.setObject("deptId", inputVO.getDEPT_ID());
		
		outputVO.setSubDeptLst(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}

	public void getParentLst(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210InputVO inputVO = (ORG210InputVO) body;
		ORG210OutputVO outputVO = new ORG210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		if ("".equals(inputVO.getORG_TYPE().trim())) {
			sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
			sb.append("FROM TBORG_DEFN ");
			sb.append("WHERE ORG_TYPE < (SELECT ORG_TYPE FROM TBORG_DEFN WHERE DEPT_ID = :deptId) ");
			sb.append("ORDER BY DEPT_ID, DEPT_NAME ");;
			condition.setQueryString(sb.toString());
			condition.setObject("deptId", inputVO.getDEPT_ID());
		} else {
			sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
			sb.append("FROM TBORG_DEFN ");
			sb.append("WHERE ORG_TYPE < :orgType ");
			sb.append("ORDER BY DEPT_ID, DEPT_NAME ");
			condition.setQueryString(sb.toString());
			condition.setObject("orgType", inputVO.getORG_TYPE());
		}
		
		outputVO.setParentLst(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
	
	public void modDept(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210InputVO inputVO = (ORG210InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_DEFNVO defnVO = (TBORG_DEFNVO) dam.findByPKey(TBORG_DEFNVO.TABLE_UID, inputVO.getDEPT_ID());
		if (!inputVO.getDEPT_DEGREE().equals(defnVO.getDEPT_DEGREE()) || !inputVO.getDEPT_GROUP().equals(defnVO.getDEPT_GROUP())) {
			defnVO.setDEPT_DEGREE(inputVO.getDEPT_DEGREE());
			defnVO.setDEPT_GROUP(inputVO.getDEPT_GROUP());
			dam.update(defnVO);	
		}
		
		dam = this.getDataAccessManager();
		for(Map<String, String> empGroup: inputVO.getEMP_GROUP_LST()) {
			TBORG_MEMBERVO memVO = (TBORG_MEMBERVO) dam.findByPKey(TBORG_MEMBERVO.TABLE_UID, empGroup.get("EMP_ID"));
			memVO.setGROUP_TYPE(empGroup.get("GROUP_TYPE"));
			dam.update(memVO);
		}
		
		sendRtnObject(null);
	}
	
	public void delDeptMember(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210InputVO inputVO = (ORG210InputVO) body;
		dam = this.getDataAccessManager();
		
		for(Map<String, String> delMem: inputVO.getDEL_MEMBER_LST()) {
			if (Boolean.valueOf(delMem.get("DEL"))) {
				TBORG_MEMBERVO memVO = (TBORG_MEMBERVO) dam.findByPKey(TBORG_MEMBERVO.TABLE_UID, delMem.get("EMP_ID"));
				memVO.setDEPT_ID("");
				dam.update(memVO);
			}
		}
		
		sendRtnObject(null);
	}
	
	public void addEmp(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210InputVO inputVO = (ORG210InputVO) body;
		dam = this.getDataAccessManager();
		
		for (Map<String, Object> emp : inputVO.getADD_MEMBER_LST()) {
			if ((Boolean)emp.get("SEL")) {
				TBORG_MEMBERVO mem = (TBORG_MEMBERVO) dam.findByPKey(TBORG_MEMBERVO.TABLE_UID, (String) emp.get("EMP_ID"));
				mem.setDEPT_ID(inputVO.getDEPT_ID());
				dam.update(mem);
			}
		}
		
		sendRtnObject(null);
	}
	
	public void getOrgType(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210InputVO inputVO = (ORG210InputVO) body;
		ORG210OutputVO outputVO = new ORG210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PARAM_CODE AS ORG_TYPE ");
		sql.append("FROM TBSYSPARAMETER ");
		sql.append("WHERE PARAM_TYPE = 'ORG.TYPE' ");
		sql.append("AND PARAM_CODE > (SELECT ORG_TYPE FROM TBORG_DEFN WHERE DEPT_ID = :parentDeptId) ");
		sql.append("AND ROWNUM < 2 ");
		sql.append("ORDER BY PARAM_CODE ASC");
		condition.setQueryString(sql.toString());
		condition.setObject("parentDeptId", inputVO.getPARENT_DEPT_ID());

		outputVO.setDeptDetail(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
	
	public void getEmpWODept(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG210InputVO inputVO = (ORG210InputVO) body;
		ORG210OutputVO outputVO = new ORG210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MEM.*, AO.AO_CODE, ROLE.ROLE_ID ");
		sql.append("FROM TBORG_MEMBER MEM ");
		sql.append("LEFT JOIN TBORG_SALES_AOCODE AO ON MEM.EMP_ID = AO.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER_ROLE ROLE ON MEM.EMP_ID = ROLE.EMP_ID AND ROLE.IS_PRIMARY_ROLE = 'Y' ");
		sql.append("WHERE (MEM.DEPT_ID IS NULL OR MEM.DEPT_ID NOT IN (SELECT DISTINCT(DEPT_ID) AS DEPT_ID FROM TBORG_DEFN)) ");
		sql.append("AND MEM.SERVICE_FLAG = 'A' ");
		sql.append("AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		
		if (!"".equals(inputVO.getEMP_ID().trim())){
			sql.append(" AND MEM.EMP_ID LIKE :empId ");
			condition.setObject("empId", "%" + inputVO.getEMP_ID() + "%");
		}
		if (!"".equals(inputVO.getEMP_NAME().trim())){
			sql.append(" AND MEM.EMP_NAME LIKE :empName ");
			condition.setObject("empName", "%" + inputVO.getEMP_NAME() + "%");
		}
		if (!"".equals(inputVO.getAO_CODE().trim())){
			sql.append(" AND AO.AO_CODE LIKE :aoCode ");
			condition.setObject("aoCode", "%" + inputVO.getAO_CODE() + "%");
		}
		if (!"".equals(inputVO.getROLE_ID().trim())){
			sql.append(" AND MEM.JOB_TITLE_NAME = :roleId ");
			condition.setObject("roleId", inputVO.getROLE_ID());
		}
		condition.setQueryString(sql.toString());
		
		outputVO.setEmpWODeptLst(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
	
	public void getRoleLst(Object body, IPrimitiveMap header) throws JBranchException {

		ORG210OutputVO outputVO = new ORG210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT(M.JOB_TITLE_NAME) AS JOB_TITLE_NAME, R.JOB_TITLE_NAME AS MJTN ");
		sb.append("FROM TBORG_MEMBER M ");
		sb.append("LEFT JOIN TBORG_ROLE R ON M.JOB_TITLE_NAME = R.JOB_TITLE_NAME ");
		sb.append("WHERE M.JOB_TITLE_NAME IS NOT NULL ");
		sb.append("ORDER BY R.JOB_TITLE_NAME, M.JOB_TITLE_NAME ");
		
		condition.setQueryString(sb.toString());

		outputVO.setRoleLst(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	
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
}
