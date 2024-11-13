package com.systex.jbranch.app.server.fps.pms421;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_10CMDT_EBILL_COMFIRM_NYPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_10CMDT_EBILL_COMFIRM_NYVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms421")
@Scope("prototype")
public class PMS421 extends FubonWmsBizLogic {

	DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS421OutputVO outputVO = new PMS421OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}

	public PMS421OutputVO queryData(Object body) throws JBranchException, ParseException {

		initUUID();

		PMS421InputVO inputVO = (PMS421InputVO) body;
		PMS421OutputVO outputVO = new PMS421OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		String roleID = inputVO.getLoginRole();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");

		PMS000OutputVO pms000outputVO = new PMS000OutputVO();
		
		// 非由工作首頁 CRM181 過來
		if (!StringUtils.equals("Y", inputVO.getFrom181())) {
			if (inputVO.getPerson_role() == null || !inputVO.getPerson_role().equals("UHRM")) {
				// 取得查詢資料可視範圍
				PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
				PMS000InputVO pms000InputVO = new PMS000InputVO();
				pms000InputVO.setReportDate(sdf1.format(inputVO.getsCreDate()));
				pms000outputVO = pms000.getOrg(pms000InputVO);
			}
		}
		
		sql.append("SELECT CASE WHEN B.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sql.append("       B.REGION_CENTER_NAME, ");
		sql.append("	   B.BRANCH_AREA_NAME, ");
		sql.append("	   B.BRANCH_NAME, ");
		sql.append("	   B.BRANCH_NBR, ");
		sql.append("       B.CUST_NAME, ");
		sql.append("	   B.CUST_ID, ");
		sql.append("	   B.EMP_NAME, ");
		sql.append("	   B.AO_CODE, ");
		sql.append("       CASE WHEN TRUNC(B.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sql.append("	   CASE WHEN B.DATA_SOURCE = 'E' THEN '電子函證' ELSE '簡訊' END AS DATA_SOURCE, ");
		sql.append("	   B.DATA_DATE, ");
		sql.append("	   SUBSTR(B.SEND_DATE, 0, 10) AS SEND_DATE, ");
		sql.append("	   SUBSTR(B.RECV_DATE, 0, 10) AS RECV_DATE, ");
		sql.append("	   B.UPDATE_REMARK, ");
		sql.append("       B.RECORD_SEQ, ");
		sql.append("	   B.FIRSTUPDATE_TIME, ");
		sql.append("	   B.FIRSTUPDATE_EMPID, ");
		sql.append("	   B.LASTUPDATE, ");
		sql.append("	   B.MODIFIER, ");
		sql.append(" 	   M.EMP_NAME AS FIRSTUPDATE_EMPNAME, ");
		sql.append("	   B.EBILL_CONTENT_FLAG, ");
		sql.append("	   CASE B.EBILL_COMFIRM_SOU WHEN '1' THEN '每季' WHEN '2' THEN '輪調加強' ELSE '其它' END AS EBILL_COMFIRM_SOU ");
		sql.append("FROM TBPMS_10CMDT_EBILL_COMFIRM B ");
		sql.append("LEFT JOIN TBORG_MEMBER M ON B.FIRSTUPDATE_EMPID = M.EMP_ID ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON B.FIRSTUPDATE_EMPID = MEM.EMP_ID AND B.BRANCH_NBR = MEM.DEPT_ID AND B.DATA_DATE BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sql.append("WHERE 1 = 1 ");
		
		sql.append("AND B.CONFIRM_FLAG = 'N' ");
		sql.append("AND SUBSTR(B.RECV_DATE, 0, 10) BETWEEN :sDATE AND :eDATE ");

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			// 登入為銷售人員強制加AO_CODE
			if (fcMap.containsKey(loginRoleID) || psopMap.containsKey(loginRoleID)) {
				sql.append("AND B.AO_CODE IN :aoCodeList ");
				condition.setObject("aoCodeList", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			}
			
			if (StringUtils.isNumeric(inputVO.getBranch_nbr()) && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {				
				sql.append("AND B.BRANCH_NBR = :BRNCH_NBRR ");
				condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sql.append("AND ( ");
				sql.append("  (B.RM_FLAG = 'B' AND B.BRANCH_AREA_ID = :BRANCH_AREA_IDD) ");
				
				if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
					armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sql.append("  OR (B.RM_FLAG = 'U' AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE MEM.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :BRANCH_AREA_IDD )) ");
				}
			
				sql.append(") ");
				condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("AND B.REGION_CENTER_ID = :REGION_CENTER_IDD ");
				condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}

			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) &&
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND B.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("AND (");
				sql.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE B.FIRSTUPDATE_EMPID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("  OR MEM.E_DEPT_ID = :uhrmOP ");
				sql.append(")");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("AND B.RM_FLAG = 'U' ");
		}

		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			sql.append("AND B.AO_CODE = :AO_CODE ");
			condition.setObject("AO_CODE", inputVO.getAo_code());
		}

		if (StringUtils.isNotBlank(inputVO.getEbill_Comfirm_Sou())) {
			sql.append("AND B.EBILL_COMFIRM_SOU = :EBILL_COMFIRM_SOU ");
			condition.setObject("EBILL_COMFIRM_SOU", inputVO.getEbill_Comfirm_Sou());
		}

		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getFrom181())) {
			sql.append(" and B.UPDATE_REMARK is null ");
		}

		condition.setObject("sDATE", sdfYYYYMMDD.format(inputVO.getsCreDate()));
		condition.setObject("eDATE", sdfYYYYMMDD.format(inputVO.getEndDate()));

		sql.append("ORDER BY B.REGION_CENTER_ID, B.BRANCH_AREA_ID, B.BRANCH_NBR ");

		condition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(condition));

		return outputVO;
	}
	
	// #1832_工作首頁
	public PMS421OutputVO queryData2(Object body) throws JBranchException, ParseException {
		
		return null;

//		initUUID();
//
//		PMS421InputVO inputVO = (PMS421InputVO) body;
//		PMS421OutputVO outputVO = new PMS421OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		
//		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
//
//		String roleID = inputVO.getLoginRole();
//
//		XmlInfo xmlInfo = new XmlInfo();
//		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
//		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
//		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
//
//		PMS000OutputVO pms000outputVO = new PMS000OutputVO();
//		
//		// 非由工作首頁 CRM181 過來
//		if (!StringUtils.equals("Y", inputVO.getFrom181())) {
//			if (inputVO.getPerson_role() == null || !inputVO.getPerson_role().equals("UHRM")) {
//				// 取得查詢資料可視範圍
//				PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
//				PMS000InputVO pms000InputVO = new PMS000InputVO();
//				pms000InputVO.setReportDate(sdfYYYYMM.format(inputVO.getsCreDate()));
//				pms000outputVO = pms000.getOrg(pms000InputVO);
//			}
//		}
//		
//		sql.append("SELECT B.REGION_CENTER_NAME, ");
//		sql.append("	   B.BRANCH_AREA_NAME, ");
//		sql.append("	   B.BRANCH_NAME, ");
//		sql.append("	   B.BRANCH_NBR, ");
//		sql.append("       B.CUST_NAME, ");
//		sql.append("	   B.CUST_ID, ");
//		sql.append("	   B.EMP_NAME, ");
//		sql.append("	   B.AO_CODE, ");
//		sql.append("       CASE WHEN TRUNC(B.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
//		sql.append("	   CASE WHEN B.DATA_SOURCE = 'E' THEN '電子函證' ELSE '簡訊' END AS DATA_SOURCE, ");
//		sql.append("	   B.DATA_DATE, ");
//		sql.append("	   SUBSTR(B.SEND_DATE, 0, 10) AS SEND_DATE, ");
//		sql.append("	   SUBSTR(B.RECV_DATE,0,10) AS RECV_DATE, ");
//		sql.append("	   B.UPDATE_REMARK, ");
//		sql.append("       B.RECORD_SEQ, ");
//		sql.append("	   B.FIRSTUPDATE_TIME, ");
//		sql.append("	   B.FIRSTUPDATE_EMPID, ");
//		sql.append("	   B.LASTUPDATE, ");
//		sql.append("	   B.MODIFIER, ");
//		sql.append(" 	   M.EMP_NAME AS FIRSTUPDATE_EMPNAME, ");
//		sql.append("	   B.EBILL_CONTENT_FLAG, ");
//		sql.append("	   CASE B.EBILL_COMFIRM_SOU WHEN '1' THEN '每季' WHEN '2' THEN '輪調加強' ELSE '其它' END AS EBILL_COMFIRM_SOU ");
//		sql.append("FROM TBPMS_10CMDT_EBILL_COMFIRM_TD B ");
//		sql.append("LEFT JOIN TBORG_MEMBER M ON B.FIRSTUPDATE_EMPID = M.EMP_ID ");
//		sql.append("WHERE 1 = 1 ");
//		sql.append("AND B.CONFIRM_FLAG = 'N' ");
//		sql.append("AND SUBSTR(B.RECV_DATE, 0, 10) BETWEEN :sDATE AND :eDATE ");
//
//		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0 || StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
//
//			if (!headmgrMap.containsKey(loginRoleID) && !StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
//				// 非總人行員 且 非 為031之兼任FC，僅可視轄下
//				sql.append("AND B.RM_FLAG = 'B' ");
//
//				// 登入為銷售人員強制加AO_CODE
//				if (fcMap.containsKey(loginRoleID) || psopMap.containsKey(loginRoleID)) {
//					sql.append("AND B.AO_CODE IN :aoCodeList ");
//					condition.setObject("aoCodeList", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
//				}
//			} else if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
//				sql.append("AND B.AO_CODE IN :aoCodeList ");
//				condition.setObject("aoCodeList", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
//			}
//
//			// 區域中心
//			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
//				sql.append("AND B.BRANCH_NBR IN ( ");
//				sql.append("  SELECT BRANCH_NBR ");
//				sql.append("  FROM VWORG_DEFN_BRH ");
//				sql.append("  WHERE DEPT_ID = :region_id ");
//				sql.append(") ");
//				condition.setObject("region_id", inputVO.getRegion_center_id());
//			} else {
//				// 登入非總行人員強制加區域中心
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append("AND B.BRANCH_NBR IN ( ");
//					sql.append("  SELECT BRANCH_NBR ");
//					sql.append("  FROM VWORG_DEFN_BRH ");
//					sql.append("  WHERE DEPT_ID IN (:REGION_CENTER_IDD) ");
//					sql.append(") ");
//					condition.setObject("REGION_CENTER_IDD", pms000outputVO.getV_regionList());
//				}
//			}
//
//			// 營運區	
//			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
//				sql.append("AND B.BRANCH_NBR IN ( ");
//				sql.append("  SELECT BRANCH_NBR ");
//				sql.append("  FROM VWORG_DEFN_BRH ");
//				sql.append("  WHERE DEPT_ID = :area_id ");
//				sql.append(") ");
//				condition.setObject("area_id", inputVO.getBranch_area_id());
//			} else {
//				// 登入非總行人員強制加營運區
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append("AND B.BRANCH_NBR IN ( ");
//					sql.append("  SELECT BRANCH_NBR ");
//					sql.append("  FROM VWORG_DEFN_BRH ");
//					sql.append("  WHERE DEPT_ID IN (:BRANCH_AREA_IDD) ");
//					sql.append(") ");
//					condition.setObject("BRANCH_AREA_IDD", pms000outputVO.getV_areaList());
//				}
//			}
//
//			// 分行
//			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
//				sql.append("AND B.BRANCH_NBR = :branch_nbr ");
//				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
//			} else if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
//				// 登入非總行人員強制加分行
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append("AND B.BRANCH_NBR IN (:BRNCH_NBRR) ");
//					condition.setObject("BRNCH_NBRR", pms000outputVO.getV_branchList());
//				}
//			}
//		} else {
//			sql.append("AND B.RM_FLAG = 'U' ");
//		}
//
//		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
//			sql.append("AND B.AO_CODE = :AO_CODE ");
//			condition.setObject("AO_CODE", inputVO.getAo_code());
//		}
//
//		if (StringUtils.isNotBlank(inputVO.getEbill_Comfirm_Sou())) {
//			sql.append("AND B.EBILL_COMFIRM_SOU = :EBILL_COMFIRM_SOU ");
//			condition.setObject("EBILL_COMFIRM_SOU", inputVO.getEbill_Comfirm_Sou());
//		}
//
//		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
//		if (StringUtils.equals("Y", inputVO.getFrom181())) {
//			sql.append(" and B.UPDATE_REMARK is null ");
//		}
//
//		condition.setObject("sDATE", sdfYYYYMMDD.format(inputVO.getsCreDate()));
//		condition.setObject("eDATE", sdfYYYYMMDD.format(inputVO.getEndDate()));
//
//		sql.append("ORDER BY B.REGION_CENTER_ID, B.BRANCH_AREA_ID, B.BRANCH_NBR ");
//
//		condition.setQueryString(sql.toString());
//
//		outputVO.setResultList(dam.exeQuery(condition));
//
//		return outputVO;
	}

	// 【儲存】更新資料，在前端篩選編輯過的資料。
	public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS421InputVO inputVO = (PMS421InputVO) body;
		dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getList()) {
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE TBPMS_10CMDT_EBILL_COMFIRM ");
			sql.append("SET FIRSTUPDATE_EMPID = (CASE WHEN FIRSTUPDATE_EMPID IS NULL THEN :modifier ELSE FIRSTUPDATE_EMPID END), ");
			sql.append("	FIRSTUPDATE_TIME = (CASE WHEN FIRSTUPDATE_TIME IS NULL THEN sysdate ELSE FIRSTUPDATE_TIME END), ");
			sql.append(" 	UPDATE_REMARK = :remark, ");
			sql.append(" 	RECORD_SEQ = :recordSEQ,  ");
			sql.append("    LASTUPDATE = sysdate, ");
			sql.append("    MODIFIER = :modifier, ");
			sql.append("    EBILL_CONTENT_FLAG = :ebill_content_flag ");
			sql.append("WHERE DATA_DATE = :data_date ");
			sql.append("AND CUST_ID = :cust_id ");

			// KEY
			condition.setObject("data_date", new SimpleDateFormat("yyyy-MM-dd").parse(map.get("DATA_DATE").toString()));
			condition.setObject("cust_id", map.get("CUST_ID"));

			// CONTENT
			condition.setObject("remark", map.get("UPDATE_REMARK"));
			condition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			condition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
			condition.setObject("ebill_content_flag", map.get("EBILL_CONTENT_FLAG"));

			condition.setQueryString(sql.toString());
			
			dam.exeUpdate(condition);
		}
		
		sendRtnObject(null);
	}

	// 產生CSV範例檔
	public void getExample(Object body, IPrimitiveMap header) throws Exception {

		CSVUtil csv = new CSVUtil();
		String[] headColumnArray;
		String csv_name;

		headColumnArray = new String[] { "資料年月(YYYYMM)", "理專員編", "服務滿N年" };
		csv_name = "服務滿7年理專.csv";

		// 設定表頭
		csv.setHeader(headColumnArray);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, csv_name);
		sendRtnObject(null);
	}

	public void queryServiceDate(Object body, IPrimitiveMap header) throws Exception {

		PMS421OutputVO outputVO = new PMS421OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT YYYYMM FROM TBPMS_10CMDT_EBILL_COMFIRM_NY order by YYYYMM desc");
		condition.setQueryString(sql.toString());
		outputVO.setDATE_LIST(dam.exeQuery(condition));

		sql = new StringBuffer();
		sql.append("select CRONEXPRESSION from TBSYSSCHD where scheduleid='BTPMS421_AO7Y'");
		condition.setQueryString(sql.toString());
		outputVO.setEXECUTE_TIME(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	public void queryServiceData(Object body, IPrimitiveMap header) throws Exception {
		
		PMS421InputVO inputVO = (PMS421InputVO) body;
		PMS421OutputVO outputVO = new PMS421OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT * FROM TBPMS_10CMDT_EBILL_COMFIRM_NY WHERE YYYYMM = :yyyymm ");

		condition.setObject("yyyymm", inputVO.getDATA_DATE());
		condition.setQueryString(sql.toString());
		outputVO.setSERVICE_LIST(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	public void deleteServiceData(Object body, IPrimitiveMap header) throws Exception {
		
		PMS421InputVO inputVO = (PMS421InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM TBPMS_10CMDT_EBILL_COMFIRM_NY WHERE YYYYMM = :yyyymm ");

		queryCondition.setObject("yyyymm", inputVO.getDATA_DATE());
		queryCondition.setQueryString(sql.toString());
		dam.exeUpdate(queryCondition);

		this.sendRtnObject(null);
	}

	//上傳CSV檔案
	public void uploadFile(Object body, IPrimitiveMap header) throws Exception {

		PMS421InputVO inputVO = (PMS421InputVO) body;
		PMS421OutputVO outputVO = new PMS421OutputVO();
		dam = this.getDataAccessManager();

		//顯示上傳成功的訊息
		String successMsg;
		String[] columns = new String[3];
		columns[0] = "YYYYMM";
		columns[1] = "EMP_ID";
		columns[2] = "SERVICE_YEARS";
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());

		if (!dataCsv.isEmpty()) {
			int j = 0;

			GenFileTools gft = new GenFileTools();
			Connection conn = gft.getConnection();
			conn.setAutoCommit(false);

			StringBuffer insSQL = new StringBuffer();
			insSQL.append("INSERT INTO TBPMS_10CMDT_EBILL_COMFIRM_NY (YYYYMM, EMP_ID, EMP_NAME, SERVICE_YEARS, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
			insSQL.append("VALUES (?, ?, (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = ?), ?, ?, ?, SYSDATE, ?, SYSDATE) ");
			PreparedStatement pstmtINS = conn.prepareStatement(insSQL.toString());

			StringBuffer updSQL = new StringBuffer();
			updSQL.append("UPDATE TBPMS_10CMDT_EBILL_COMFIRM_NY BASE ");
			updSQL.append("SET SERVICE_YEARS = ?, EMP_NAME = (SELECT EMP.EMP_NAME FROM TBORG_MEMBER EMP WHERE EMP.EMP_ID = BASE.EMP_ID), VERSION = ?, MODIFIER = ?, LASTUPDATE = SYSDATE ");
			updSQL.append("WHERE YYYYMM = ? ");
			updSQL.append("AND EMP_ID = ? ");
			PreparedStatement pstmtUPD = conn.prepareStatement(updSQL.toString());

			int columnRow = 0;
			try {
				for (int i = 1; i < dataCsv.size(); i++) { //i=1 (不讀標頭)
					columnRow = i;
					TBPMS_10CMDT_EBILL_COMFIRM_NYPK com_pk = new TBPMS_10CMDT_EBILL_COMFIRM_NYPK();
					TBPMS_10CMDT_EBILL_COMFIRM_NYVO com_vo = new TBPMS_10CMDT_EBILL_COMFIRM_NYVO();

					String[] str = dataCsv.get(i);
					com_pk.setYYYYMM(str[0]);
					// 上傳的員編不足6碼時,前面自動補0
					String emp_id = str[1].trim();
					if (emp_id.length() < 6) {
						int len = emp_id.length();
						for (int l = len; l < 6; l++) {
							emp_id = "0" + emp_id;
						}
					}
					com_pk.setEMP_ID(emp_id);

					com_vo = (TBPMS_10CMDT_EBILL_COMFIRM_NYVO) getDataAccessManager().findByPKey(TBPMS_10CMDT_EBILL_COMFIRM_NYVO.TABLE_UID, com_pk);
					if (null == com_vo) {
						pstmtINS.setString(1, str[0]);
						pstmtINS.setString(2, emp_id);
						pstmtINS.setString(3, emp_id);
						pstmtINS.setBigDecimal(4, new BigDecimal(str[2]));
						pstmtINS.setBigDecimal(5, BigDecimal.ZERO);
						pstmtINS.setString(6, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
						pstmtINS.setString(7, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());

						pstmtINS.addBatch();
					} else {
						pstmtUPD.setBigDecimal(1, new BigDecimal(str[2]));
						pstmtUPD.setBigDecimal(2, new BigDecimal(com_vo.getVersion()).add(BigDecimal.ONE));
						pstmtUPD.setString(3, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
						pstmtUPD.setString(4, str[0]);
						pstmtUPD.setString(5, emp_id);

						pstmtUPD.addBatch();
					}

					j++;

				}

				pstmtINS.executeBatch();
				pstmtUPD.executeBatch();

				conn.commit();

				successMsg = "資料匯入成功，筆數" + j;
				outputVO.setSuccessMsg(successMsg);
			} catch (Exception e) {
				if (conn != null)
					try {
						conn.rollback();
					} catch (Exception e2) {
					}
				e.printStackTrace();
				throw new APException("案件編號" + columnRow + "有問題，上傳失敗！");
			} finally {
				if (pstmtINS != null)
					try {
						pstmtINS.close();
					} catch (Exception e) {
					}
				if (pstmtUPD != null)
					try {
						pstmtUPD.close();
					} catch (Exception e) {
					}
				if (conn != null)
					try {
						conn.close();
					} catch (Exception e) {
					}
			}
		}

		sendRtnObject(outputVO);
	}

	public void setting(Object body, IPrimitiveMap header) throws Exception {
		
		SimpleDateFormat hh = new SimpleDateFormat("HH");
		SimpleDateFormat mm = new SimpleDateFormat("mm");
		
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);

		PMS421InputVO inputVO = (PMS421InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE TBSYSSCHD SET CRONEXPRESSION = :CRONEXPRESSION, ISUSE='Y', ISSCHEDULED='N', MODIFIER = :MODIFIER , LASTUPDATE = sysdate WHERE scheduleid='BTPMS421_AO7Y' ");

		condition.setObject("CRONEXPRESSION", "0 " + Integer.parseInt(mm.format(inputVO.getEXE_TIME())) + " " + Integer.parseInt(hh.format(inputVO.getEXE_TIME())) + " " + inputVO.getEXE_DATE() + " " + inputVO.getMONTH() + " ?");
		condition.setObject("MODIFIER", loginID);

		condition.setQueryString(sql.toString());
		
		dam.exeUpdate(condition);
		
		this.sendRtnObject(null);
	}

	//匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {

		PMS421InputVO inputVO = (PMS421InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "對帳單函證回函(電子/簡訊)回覆異常報表_" + sdf.format(new Date()) + ".csv";

		String[] csvHeader = { 	"私銀註記", "業務處", "區別", "分行名稱", "分行代碼", 
								"客戶姓名", "身分證號", "服務理專", "AO CODE", "名單來源", "對帳單基準日", "函證/簡訊寄送日期", "客戶回覆日期", 
								"聯繫內容", "電訪錄音編號", "異常通報", "名單來源", 
								"首次建立時間", "訪查人", "最後異動時間", "最後異動人員" };

		String[] csvMain   = { 	"RM_FLAG", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "BRANCH_NBR", 
								"CUST_NAME", "CUST_ID", "EMP_NAME", "AO_CODE", "DATA_SOURCE", "DATA_DATE", "SEND_DATE", "RECV_DATE", 
								"UPDATE_REMARK", "RECORD_SEQ", "EBILL_CONTENT_FLAG", "EBILL_COMFIRM_SOU", 
								"FIRSTUPDATE_TIME", "FIRSTUPDATE_EMPID", "LASTUPDATE", "MODIFIER", };

		List<Object[]> csvData = new ArrayList<Object[]>();

		for (Map<String, Object> map : inputVO.getList()) {

			String[] records = new String[csvHeader.length];

			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
				case "CUST_ID":
					records[i] = DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, csvMain[i]));
					break;
				case "AO_CODE":
					records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
					break;
				case "RECORD_SEQ":
					records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
					break;
				default:
					records[i] = checkIsNull(map, csvMain[i]);
					break;
				}
			}

			csvData.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);
	}

	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}