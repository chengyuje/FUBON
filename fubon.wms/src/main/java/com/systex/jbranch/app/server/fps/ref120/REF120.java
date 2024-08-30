package com.systex.jbranch.app.server.fps.ref120;

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

import com.systex.jbranch.app.common.fps.table.TBCAM_LOAN_SALERECVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_LOAN_SALEREC_REVIEWVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/05/19
 * 
 */
@Component("ref120")
@Scope("request")
public class REF120 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfYYYYMMDD_ = new SimpleDateFormat("yyyy-MM-dd");

	public void query (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		REF120InputVO inputVO = (REF120InputVO) body;
		REF120OutputVO outputVO = new REF120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> bmmgrMap   = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> fcMap      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> psopMap    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> faiaMap    = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT PRIASS.PRIVILEGEID, ROL.JOB_TITLE_NAME, REPLACE(SYS_ROLE, '_ROLE', '') AS SYS_ROLE  ");
		sb.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sb.append("LEFT JOIN TBORG_ROLE ROL ON PRIASS.ROLEID = ROL.ROLE_ID AND ROL.JOB_TITLE_NAME IS NOT NULL ");
		sb.append("WHERE PRIASS.ROLEID = :roleID "); 
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List<Map<String, Object>> privilege = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
		sb.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sb.append("  UNION ");
		sb.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
		sb.append("  FROM VWORG_EMP_PLURALISM_INFO ");
		sb.append(") ");
		
		sb.append("SELECT SALEREC.SEQ, SALEREC.REF_CON_ID, SALEREC.TXN_DATE, ");
		sb.append("       INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME AS BRANCH_AREA, INFO.BRANCH_NBR, INFO.BRANCH_NAME AS BRANCH, ");
		sb.append("       SALEREC.SALES_PERSON, SALEREC.SALES_NAME, SALEREC.SALES_ROLE, ");
		sb.append("       SALEREC.CUST_ID, SALEREC.CUST_NAME, SALEREC.REF_PROD, ");
		sb.append("       INFO_U.REGION_CENTER_ID AS REGION_CENTER_ID_U, INFO_U.REGION_CENTER_NAME AS REGION_CENTER_NAME_U, INFO_U.BRANCH_AREA_ID AS BRANCH_AREA_ID_U, INFO_U.BRANCH_AREA_NAME AS BRANCH_AREA_U, INFO_U.BRANCH_NBR AS BRANCH_NBR_U, INFO_U.BRANCH_NAME AS BRANCH_U, ");
		sb.append("       SALEREC.USERID, SALEREC.USERNAME, SALEREC.USERROLE, SALEREC.CONT_RSLT, SALEREC.NON_GRANT_REASON, SALEREC.COMMENTS, ");
		if (bmmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行
			sb.append(" CASE WHEN SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM BASE WHERE BRANCH_NBR IN (:brIdList)) THEN 'Y' ELSE 'N' END AS USER_BOSS ");
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else {
			sb.append(" NULL AS USER_BOSS ");
		}
		
		sb.append("FROM TBCAM_LOAN_SALEREC SALEREC ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO INFO ON SALEREC.REF_ORG_ID = INFO.BRANCH_NBR ");
//		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = SALEREC.SALES_PERSON ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO_U ON INFO_U.EMP_ID = SALEREC.USERID ");
		sb.append("WHERE 1 = 1 ");
		
		//主管角色：主管角色，可查詢轄下分行及自己建立的資料。
		if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 總行
			sb.append("AND (INFO.REGION_CENTER_ID IN (:rcIdList) OR INFO.REGION_CENTER_ID IS NULL OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 區域中心
			sb.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM BASE WHERE REGION_CENTER_ID IN (:rcIdList)) ");
			sb.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM BASE WHERE REGION_CENTER_ID IN (:rcIdList)) ");
			sb.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (mbrmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運區
			sb.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM BASE WHERE BRANCH_AREA_ID IN (:opIdList)) ");
			sb.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM BASE WHERE BRANCH_AREA_ID IN (:opIdList)) ");
			sb.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (bmmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行
			sb.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM BASE WHERE BRANCH_NBR IN (:brIdList)) ");
			sb.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM BASE WHERE BRANCH_NBR IN (:brIdList)) ");
			sb.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
			
		} else { //非主管角色：非主管角色，只可查詢自己建立的資料。
			sb.append("AND (SALEREC.CREATOR = :loginID OR SALEREC.USERID = :loginID) ");
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		}

		if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
			sb.append("AND (INFO.REGION_CENTER_ID = :rc_id OR INFO_U.REGION_CENTER_ID = :rc_id) ");
			queryCondition.setObject("rc_id", inputVO.getRegionID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getBranchAreaID()) && !"null".equals(inputVO.getBranchAreaID())) {
			sb.append("AND (INFO.BRANCH_AREA_ID = :op_id OR INFO_U.BRANCH_AREA_ID = :op_id) ");
			queryCondition.setObject("op_id", inputVO.getBranchAreaID());
		} 
		
		if (StringUtils.isNotBlank(inputVO.getBranchID()) && Integer.valueOf(inputVO.getBranchID()) > 0) {
			sb.append("AND (INFO.BRANCH_NBR = :br_id OR INFO_U.BRANCH_NBR = :br_id) ");
			queryCondition.setObject("br_id", inputVO.getBranchID());
		} 
		
		if (StringUtils.isNotBlank(inputVO.getSeq())) { //案件編號
			sb.append("AND SALEREC.SEQ = :seq ");
			queryCondition.setObject("seq", inputVO.getSeq());
		}
		
		if (StringUtils.isNotBlank(inputVO.getSalesPerson())){ //轉介人員編
			sb.append("AND SALEREC.SALES_PERSON LIKE :salesPerson ");
			queryCondition.setObject("salesPerson", inputVO.getSalesPerson() + "%");
		}
			
		if (StringUtils.isNotBlank(inputVO.getSalesName())) { //轉介人姓名
			sb.append("AND SALEREC.SALES_NAME LIKE :salesName ");
			queryCondition.setObject("salesName", inputVO.getSalesName() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getSalesRole())) { //轉介人職務別
			sb.append("AND SALEREC.SALES_ROLE = :salesRole ");
			queryCondition.setObject("salesRole", inputVO.getSalesRole());
		}

		if (StringUtils.isNotBlank(inputVO.getUserID())) { //受轉介人員編
			sb.append("AND SALEREC.USERID LIKE :userID ");
			queryCondition.setObject("userID", inputVO.getUserID() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getUserName())) { //受轉介人姓名
			sb.append("AND SALEREC.USERNAME LIKE :userName ");
			queryCondition.setObject("userName", inputVO.getUserName() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getUserRole())) { //受轉介人職務別
			sb.append("AND SALEREC.USERROLE = :userRole ");
			queryCondition.setObject("userRole", inputVO.getUserRole());
		}
			
		if (null != inputVO.getsDate()) { //轉介日期-起
			sb.append("AND TO_CHAR(SALEREC.TXN_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}
		
		if (null != inputVO.geteDate()) { //轉介日期-迄
			sb.append("AND TO_CHAR(SALEREC.TXN_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustID())) { //客戶ID
			sb.append("AND SALEREC.CUST_ID LIKE :custID ");
			queryCondition.setObject("custID", inputVO.getCustID().toUpperCase() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustName())) { //客戶姓名
			sb.append("AND SALEREC.CUST_NAME LIKE :custName ");
			queryCondition.setObject("custName", inputVO.getCustName() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getRefProd())) { //轉介商品
			sb.append("AND SALEREC.REF_PROD = :refProd ");
			queryCondition.setObject("refProd", inputVO.getRefProd());
		}
		
		if (StringUtils.isNotBlank(inputVO.getRefInsContRslt())) { //投保商品案件進度查詢
			sb.append("AND SALEREC.CONT_RSLT = :refInsContRslt ");
			queryCondition.setObject("refInsContRslt", inputVO.getRefInsContRslt());
		}
		
		if (StringUtils.isNotBlank(inputVO.getRefLoanContRslt())) { //貸款類商品案件進度查詢
			sb.append("AND SALEREC.CONT_RSLT = :refLoanContRslt ");
			queryCondition.setObject("refLoanContRslt", inputVO.getRefLoanContRslt());
		}
		
		sb.append("ORDER BY DECODE(SALEREC.USERID, :loginID, 1, 99), TXN_DATE DESC ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition)); // data
		outputVO.setLoginSysRole((String) privilege.get(0).get("SYS_ROLE"));
		
		this.sendRtnObject(outputVO);
	}
	
	public void del (Object body, IPrimitiveMap header) throws JBranchException {
		
		REF120InputVO inputVO = (REF120InputVO) body;
		REF120OutputVO outputVO = new REF120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBCAM_LOAN_SALERECVO vo = new TBCAM_LOAN_SALERECVO();
		vo = (TBCAM_LOAN_SALERECVO) dam.findByPKey(TBCAM_LOAN_SALERECVO.TABLE_UID, inputVO.getSeq());
		dam.delete(vo);
		
		TBCAM_LOAN_SALEREC_REVIEWVO rvo = (TBCAM_LOAN_SALEREC_REVIEWVO) dam.findByPKey(TBCAM_LOAN_SALEREC_REVIEWVO.TABLE_UID, inputVO.getSeq());
		if(rvo != null)
			dam.delete(rvo);
		
		this.sendRtnObject(null);
	}
	
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		REF120InputVO inputVO = (REF120InputVO) body;
		REF120OutputVO outputVO = new REF120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> bmmgrMap   = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> fcMap      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> psopMap    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> faiaMap    = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT PRIASS.PRIVILEGEID, ROL.JOB_TITLE_NAME, REPLACE(SYS_ROLE, '_ROLE', '') AS SYS_ROLE  ");
		sb.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sb.append("LEFT JOIN TBORG_ROLE ROL ON PRIASS.ROLEID = ROL.ROLE_ID AND ROL.JOB_TITLE_NAME IS NOT NULL ");
		sb.append("WHERE PRIASS.ROLEID = :roleID "); 
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List<Map<String, Object>> privilege = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
		sb.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sb.append("  UNION ");
		sb.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
		sb.append("  FROM VWORG_EMP_PLURALISM_INFO ");
		sb.append(") ");
		
		sb.append("SELECT SALEREC.SEQ, SALEREC.REF_CON_ID, SALEREC.TXN_DATE, ");
		sb.append("       INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME AS BRANCH_AREA, INFO.BRANCH_NBR, INFO.BRANCH_NAME AS BRANCH, ");
		sb.append("       SALEREC.SALES_PERSON, SALEREC.SALES_NAME, SALEREC.SALES_ROLE, ");
		sb.append("       SALEREC.CUST_ID, SALEREC.CUST_NAME, SALEREC.REF_PROD, ");
		sb.append("       INFO_U.REGION_CENTER_ID AS REGION_CENTER_ID_U, INFO_U.REGION_CENTER_NAME AS REGION_CENTER_NAME_U, INFO_U.BRANCH_AREA_ID AS BRANCH_AREA_ID_U, INFO_U.BRANCH_AREA_NAME AS BRANCH_AREA_U, INFO_U.BRANCH_NBR AS BRANCH_NBR_U, INFO_U.BRANCH_NAME AS BRANCH_U, ");
		sb.append("       SALEREC.USERID, SALEREC.USERNAME, SALEREC.USERROLE, SALEREC.CONT_RSLT, SALEREC.NON_GRANT_REASON, SALEREC.COMMENTS, ");
		if (bmmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行
			sb.append(" CASE WHEN SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM BASE WHERE BRANCH_NBR IN (:brIdList)) THEN 'Y' ELSE 'N' END AS USER_BOSS ");
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else {
			sb.append(" NULL AS USER_BOSS ");
		}
		
		sb.append("FROM TBCAM_LOAN_SALEREC SALEREC ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO INFO ON SALEREC.REF_ORG_ID = INFO.BRANCH_NBR ");
//		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = SALEREC.SALES_PERSON ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO_U ON INFO_U.EMP_ID = SALEREC.USERID ");
		sb.append("WHERE 1 = 1 ");
		
		//主管角色：主管角色，可查詢轄下分行及自己建立的資料。
		if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 總行
			sb.append("AND (INFO.REGION_CENTER_ID IN (:rcIdList) OR INFO.REGION_CENTER_ID IS NULL OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 區域中心
			sb.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM BASE WHERE REGION_CENTER_ID IN (:rcIdList)) ");
			sb.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM BASE WHERE REGION_CENTER_ID IN (:rcIdList)) ");
			sb.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (mbrmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運區
			sb.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM BASE WHERE BRANCH_AREA_ID IN (:opIdList)) ");
			sb.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM BASE WHERE BRANCH_AREA_ID IN (:opIdList)) ");
			sb.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (bmmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行
			sb.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM BASE WHERE BRANCH_NBR IN (:brIdList)) ");
			sb.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM BASE WHERE BRANCH_NBR IN (:brIdList)) ");
			sb.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
			
		} else { //非主管角色：非主管角色，只可查詢自己建立的資料。
			sb.append("AND (SALEREC.CREATOR = :loginID OR SALEREC.USERID = :loginID) ");
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		}

		if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
			sb.append("AND (INFO.REGION_CENTER_ID = :rc_id OR INFO_U.REGION_CENTER_ID = :rc_id) ");
			queryCondition.setObject("rc_id", inputVO.getRegionID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getBranchAreaID()) && !"null".equals(inputVO.getBranchAreaID())) {
			sb.append("AND (INFO.BRANCH_AREA_ID = :op_id OR INFO_U.BRANCH_AREA_ID = :op_id) ");
			queryCondition.setObject("op_id", inputVO.getBranchAreaID());
		} 
		
		if (StringUtils.isNotBlank(inputVO.getBranchID()) && Integer.valueOf(inputVO.getBranchID()) > 0) {
			sb.append("AND (INFO.BRANCH_NBR = :br_id OR INFO_U.BRANCH_NBR = :br_id) ");
			queryCondition.setObject("br_id", inputVO.getBranchID());
		} 
		
		if (StringUtils.isNotBlank(inputVO.getSeq())) { //案件編號
			sb.append("AND SALEREC.SEQ = :seq ");
			queryCondition.setObject("seq", inputVO.getSeq());
		}
		
		if (StringUtils.isNotBlank(inputVO.getSalesPerson())){ //轉介人員編
			sb.append("AND SALEREC.SALES_PERSON LIKE :salesPerson ");
			queryCondition.setObject("salesPerson", inputVO.getSalesPerson() + "%");
		}
			
		if (StringUtils.isNotBlank(inputVO.getSalesName())) { //轉介人姓名
			sb.append("AND SALEREC.SALES_NAME LIKE :salesName ");
			queryCondition.setObject("salesName", inputVO.getSalesName() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getSalesRole())) { //轉介人職務別
			sb.append("AND SALEREC.SALES_ROLE = :salesRole ");
			queryCondition.setObject("salesRole", inputVO.getSalesRole());
		}

		if (StringUtils.isNotBlank(inputVO.getUserID())) { //受轉介人員編
			sb.append("AND SALEREC.USERID LIKE :userID ");
			queryCondition.setObject("userID", inputVO.getUserID() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getUserName())) { //受轉介人姓名
			sb.append("AND SALEREC.USERNAME LIKE :userName ");
			queryCondition.setObject("userName", inputVO.getUserName() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getUserRole())) { //受轉介人職務別
			sb.append("AND SALEREC.USERROLE = :userRole ");
			queryCondition.setObject("userRole", inputVO.getUserRole());
		}
			
		if (null != inputVO.getsDate()) { //轉介日期-起
			sb.append("AND TO_CHAR(SALEREC.TXN_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}
		
		if (null != inputVO.geteDate()) { //轉介日期-迄
			sb.append("AND TO_CHAR(SALEREC.TXN_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustID())) { //客戶ID
			sb.append("AND SALEREC.CUST_ID LIKE :custID ");
			queryCondition.setObject("custID", inputVO.getCustID().toUpperCase() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustName())) { //客戶姓名
			sb.append("AND SALEREC.CUST_NAME LIKE :custName ");
			queryCondition.setObject("custName", inputVO.getCustName() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getRefProd())) { //轉介商品
			sb.append("AND SALEREC.REF_PROD = :refProd ");
			queryCondition.setObject("refProd", inputVO.getRefProd());
		}
		
		if (StringUtils.isNotBlank(inputVO.getRefInsContRslt())) { //投保商品案件進度查詢
			sb.append("AND SALEREC.CONT_RSLT = :refInsContRslt ");
			queryCondition.setObject("refInsContRslt", inputVO.getRefInsContRslt());
		}
		
		if (StringUtils.isNotBlank(inputVO.getRefLoanContRslt())) { //貸款類商品案件進度查詢
			sb.append("AND SALEREC.CONT_RSLT = :refLoanContRslt ");
			queryCondition.setObject("refLoanContRslt", inputVO.getRefLoanContRslt());
		}
		
		sb.append("ORDER BY DECODE(SALEREC.USERID, :loginID, 1, 99), TXN_DATE DESC ");
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, String>> list = dam.exeQuery(queryCondition); // data
//		REF120InputVO inputVO = (REF120InputVO) body;
//		REF120OutputVO outputVO = new REF120OutputVO();
//		
//		List<Map<String, String>> list = inputVO.getEXPORT_LST();
		
		String fileName = "轉介資料_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		String[] headerLine = {"是否已處理", "案件編號", "日期", "業務處", "營運區", "分行", "轉介人員編", "轉介人姓名", "轉介人職務別", "客戶ID", "客戶姓名", "轉介商品", "受轉介人員編", "受轉介人姓名", "受轉介人職務別", "聯絡狀態", "洽談內容", "已核不撥原因"};
		String[] mainLine   = {"CONT_RSLT_FLAG", "SEQ", "TXN_DATE", "REGION_CENTER_NAME", "BRANCH_AREA", "BRANCH", "SALES_PERSON", "SALES_NAME", "SALES_ROLE", "CUST_ID", "CUST_NAME", "REF_PROD", "USERID", "USERNAME", "USERROLE", "CONT_RSLT", "COMMENTS", "NON_GRANT_REASON"};

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("轉介資料_" + sdfYYYYMMDD.format(new Date()));
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
		
		Map<String, String> salesRoleMap 	   = new XmlInfo().doGetVariable("CAM.REF_SALES_ROLE", FormatHelper.FORMAT_3);
		Map<String, String> userRoleMap		   = new XmlInfo().doGetVariable("CAM.REF_USER_ROLE", FormatHelper.FORMAT_3);
		Map<String, String> refProdMap 		   = new XmlInfo().doGetVariable("CAM.REF_PROD", FormatHelper.FORMAT_3);
		Map<String, String> refInsContRsltMap  = new XmlInfo().doGetVariable("CAM.REF_INS_CONT_RSLT", FormatHelper.FORMAT_3);
		Map<String, String> refLoanContRsltMap = new XmlInfo().doGetVariable("CAM.REF_LOAN_CONT_RSLT", FormatHelper.FORMAT_3);
		Map<String, String> nonGrantReasonMap  = new XmlInfo().doGetVariable("CAM.REF_NON_GRANT_REASON", FormatHelper.FORMAT_3);

		for (Map<String, String> map : list) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				if (StringUtils.equals("BRANCH", mainLine[j])) {
					cell.setCellValue(checkIsNull(map, "BRANCH_NBR") + "-" + checkIsNull(map, mainLine[j]));
				} else if (StringUtils.equals("CONT_RSLT_FLAG", mainLine[j])) {
					cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, "CONT_RSLT")) && !StringUtils.equals("A01", checkIsNull(map, "CONT_RSLT")) ? "✔" : "");
				} else if (StringUtils.equals("SEQ", mainLine[j])) {
					cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, "REF_CON_ID")) ? (checkIsNull(map, mainLine[j]) + "(舊)" + checkIsNull(map, "REF_CON_ID")) : checkIsNull(map, mainLine[j]));
				} else if (StringUtils.equals("SALES_ROLE", mainLine[j])) {
					cell.setCellValue(salesRoleMap.get(checkIsNull(map, mainLine[j])));
				} else if (StringUtils.equals("REF_PROD", mainLine[j])) {
					cell.setCellValue(refProdMap.get(checkIsNull(map, mainLine[j])));
				} else if (StringUtils.equals("USERROLE", mainLine[j])) {
					cell.setCellValue(userRoleMap.get(checkIsNull(map, mainLine[j])));
				} else if (StringUtils.equals("CONT_RSLT", mainLine[j])) {
					if (StringUtils.equals("5", checkIsNull(map, "REF_PROD"))) {
						cell.setCellValue(refInsContRsltMap.get(checkIsNull(map, mainLine[j])));
					} else {
						cell.setCellValue(refLoanContRsltMap.get(checkIsNull(map, mainLine[j])));
					}
				} else if (StringUtils.equals("NON_GRANT_REASON", mainLine[j])) {
					cell.setCellValue(nonGrantReasonMap.get(checkIsNull(map, mainLine[j])));
				} else {
					cell.setCellValue(checkIsNull(map, mainLine[j]));
				}
			}

			index++;
		}
		
		wb.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
		sendRtnObject(null);
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull (Map map, String key) {
		
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
}
