package com.systex.jbranch.app.server.fps.org110;

import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/*
 * modify by ocean 2016-11-08 add getReviewList()
 * modify by ocean 2017-02-02 組織連動
 */
@Component("org110")
@Scope("request")
public class ORG110 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	public void getOrgMemberLst(Object body, IPrimitiveMap header) throws Exception {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG110InputVO inputVO = (ORG110InputVO) body;
		ORG110OutputVO outputVO = new ORG110OutputVO();
		dam = this.getDataAccessManager();	
			
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
		StringBuffer sb = new StringBuffer();		
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID LIKE :roleID ");
		condition.setQueryString(sb.toString());
		condition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List<Map<String, Object>> temp = dam.exeQuery(condition);
		
		//可視範圍
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
		sb = new StringBuffer();	
		sb.append("SELECT DISTINCT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NULL) ");
		sb.append("AND PRIVILEGEID NOT IN ('000') ");
		sb.append("UNION ");
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN ('006', '009', '010', '011', '012') ");
		condition.setQueryString(sb.toString());
		List<Map<String, Object>> allSeePrivilegeIDList = dam.exeQuery(condition);
		List<String> aspl = new ArrayList<String>();
		for (Map<String, Object> map : allSeePrivilegeIDList) {
			aspl.add((String) map.get("PRIVILEGEID"));
		}

		String privilegeID = null;
		if(temp.size() > 0){
			privilegeID = temp.get(0).get("PRIVILEGEID").toString();
			
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("WITH EMP_FINAL_BASE AS ( ");
			sql.append("  SELECT SEQNO, REVIEW_STATUS, ");
			sql.append("         CASE WHEN REVIEW_STATUS = 'W' THEN '待' || (SELECT LISTAGG(ROLE_NAME, '/') WITHIN GROUP (ORDER BY PRIVILEGEID) AS ROLE_NAME ");
			sql.append("                                                     FROM ( ");
			sql.append("                                                       SELECT DISTINCT R.ROLE_NAME, P.PRIVILEGEID ");
			sql.append("                                                       FROM TBORG_ROLE R ");
			sql.append("                                                       INNER JOIN ( ");
			sql.append("                                                         SELECT ROLEID, PRIVILEGEID ");
			sql.append("                                                         FROM TBSYSSECUROLPRIASS ");
			sql.append("                                                         WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'ORG110' AND FUNCTIONID = 'confirm') ");
			sql.append("                                                        ) P ON R.ROLE_ID = P.ROLEID ");
			sql.append("                                                       ORDER BY PRIVILEGEID ");
			sql.append("                                                    )) || '覆核' ");
			sql.append("         ELSE NULL END AS CONFIRM_USER, ");
			sql.append("         REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sql.append("         EMP_ID, EMP_NAME, JOB_TITLE_NAME, AO_CODE, CASE WHEN ROLE_TYPE = 'Y' THEN '【主要】' WHEN ROLE_TYPE = 'N' THEN '【兼職】' ELSE '' END AS TW_ROLE_TYPE, ROLE_TYPE, ");
			sql.append("         PHOTO_FLAG, PRIVILEGEID, ISUNAUTHED, UNAUTH_DATE, REAUTH_DATE, ");
			sql.append("         (SELECT COUNT(1) FROM TBORG_MEMBER_REVIEW MR WHERE MR.EMP_ID = BASE.EMP_ID) AS COUNTS ");
			sql.append("  FROM ( ");
			sql.append("    SELECT REVIEW.SEQNO, REVIEW.REVIEW_STATUS, INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, ");
			sql.append("           INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
			sql.append("           INFO.EMP_ID, INFO.EMP_NAME, INFO.JOB_TITLE_NAME, INFO.AO_CODE, ");
			sql.append("           (CASE WHEN REVIEW.EMP_PHOTO IS NOT NULL THEN 'Y' ELSE 'N' END) AS PHOTO_FLAG, ");
			sql.append("           IASS.PRIVILEGEID, UNAUTH.ISUNAUTHED, TO_CHAR(UNAUTH.UNAUTH_DATE, 'yyyy-MM-dd') UNAUTH_DATE,  ");
			sql.append("           TO_CHAR(UNAUTH.REAUTH_DATE, 'yyyy-MM-dd') REAUTH_DATE, ROLE_TYPE ");
			sql.append("    FROM VWORG_EMP_INFO INFO ");
			sql.append("    INNER JOIN TBORG_MEMBER_REVIEW REVIEW ON INFO.EMP_ID = REVIEW.EMP_ID ");
			sql.append("    LEFT JOIN TBSYSSECUROLPRIASS IASS ON INFO.ROLE_ID = IASS.ROLEID ");
			sql.append("    LEFT JOIN TBORG_MEMBER_UNAUTH UNAUTH ON INFO.EMP_ID = UNAUTH.EMP_ID AND ISUNAUTHED = 'Y' ");
			sql.append("    WHERE REVIEW.REVIEW_STATUS = 'W' ");
			sql.append("    AND REVIEW.EMP_NAME IS NOT NULL ");
			if (inputVO.getAO_CODE().trim().length() > 0) {
				sql.append("    AND INFO.EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
				condition.setObject("aoCode", inputVO.getAO_CODE().toUpperCase());
			}
			sql.append("    UNION ");
			sql.append("    SELECT NULL AS SEQNO, MEMBER.REVIEW_STATUS, INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, ");
			sql.append("           INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
			sql.append("           INFO.EMP_ID, INFO.EMP_NAME, INFO.JOB_TITLE_NAME, INFO.AO_CODE, ");
			sql.append("           (CASE WHEN MEMBER.EMP_PHOTO IS NOT NULL THEN 'Y' ELSE 'N' END) AS PHOTO_FLAG, ");
			sql.append("           IASS.PRIVILEGEID, UNAUTH.ISUNAUTHED, TO_CHAR(UNAUTH.UNAUTH_DATE, 'yyyy-MM-dd') UNAUTH_DATE, ");
			sql.append("           TO_CHAR(UNAUTH.REAUTH_DATE, 'yyyy-MM-dd') REAUTH_DATE, ROLE_TYPE ");
			sql.append("    FROM VWORG_EMP_INFO INFO ");
			sql.append("    LEFT JOIN TBORG_MEMBER MEMBER ON INFO.EMP_ID = MEMBER.EMP_ID ");
			sql.append("    LEFT JOIN TBSYSSECUROLPRIASS IASS ON INFO.ROLE_ID = IASS.ROLEID ");
			sql.append("    LEFT JOIN TBORG_MEMBER_UNAUTH UNAUTH ON INFO.EMP_ID = UNAUTH.EMP_ID AND ISUNAUTHED = 'Y' ");
			sql.append("    WHERE MEMBER.REVIEW_STATUS = 'Y' ");
			sql.append("    AND NOT EXISTS (SELECT T.EMP_ID FROM TBORG_MEMBER_REVIEW T WHERE T.REVIEW_STATUS = 'W' AND T.EMP_NAME IS NOT NULL AND T.EMP_ID = INFO.EMP_ID) ");
			if (inputVO.getAO_CODE().trim().length() > 0) {
				sql.append("    AND INFO.EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
				condition.setObject("aoCode", inputVO.getAO_CODE().toUpperCase());
			}

			sql.append("  ) BASE ");
			sql.append(") ");
			
			sql.append("SELECT *  ");
			sql.append("FROM EMP_FINAL_BASE EFB ");
			sql.append("WHERE 1 = 1 ");
			
			if (!uhrmmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) && !uhrmMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
					sql.append("AND EFB.REGION_CENTER_ID = :regionCenterID "); //區域代碼
					condition.setObject("regionCenterID", inputVO.getRegion_center_id());
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sql.append("AND (EFB.REGION_CENTER_ID IN (:regionCenterIDList) OR EFB.REGION_CENTER_ID IS NULL) ");
					condition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
					sql.append("AND EFB.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					condition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sql.append("AND (EFB.BRANCH_AREA_ID IN (:branchAreaIDList) OR EFB.BRANCH_AREA_ID IS NULL) ");
					condition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
					sql.append("AND EFB.BRANCH_NBR = :branchID "); //分行代碼
					condition.setObject("branchID", inputVO.getBranch_nbr());
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sql.append("AND (EFB.BRANCH_NBR IN (:branchIDList) OR EFB.BRANCH_NBR IS NULL) ");
					condition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
			}
			
			if (inputVO.getJOB_TITLE_NAME().trim().length() > 0) {
				sql.append(" AND EFB.JOB_TITLE_NAME = :jtId ");
				condition.setObject("jtId", inputVO.getJOB_TITLE_NAME());
			}
			
			if (inputVO.getEMP_ID().trim().length() > 0) {
				sql.append(" AND EFB.EMP_ID LIKE :empId ");
				condition.setObject("empId", "%" + inputVO.getEMP_ID() + "%");
			}
			
			if (inputVO.getEMP_NAME().trim().length() > 0) {
				sql.append(" AND EFB.EMP_NAME LIKE :empName ");
				condition.setObject("empName", "%" + inputVO.getEMP_NAME() + "%");
			}
			
			if (inputVO.getPHOTO_FLAG().trim().length() > 0) {
				if ("Y".equals(inputVO.getPHOTO_FLAG())) {
					sql.append(" AND EFB.PHOTO_FLAG LIKE 'Y' ");
				} else {
					sql.append(" AND EFB.PHOTO_FLAG LIKE 'N' ");
				}
			}
			
			if (aspl.indexOf(privilegeID) >= 0) { //分行個金主管(包括業務主管) + 總行人員 可查詢轄下分行所有同仁
				if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).equals("uhrm") ||
					uhrmMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					// UHRM 或 031兼FC5
					sql.append(" AND EFB.EMP_ID = :userID ");
					condition.setObject("userID", ws.getUser().getUserID());
				} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0) {
					sql.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO T WHERE T.EMP_ID = EFB.EMP_ID) ");
				}
			} else if (StringUtils.equals("006", privilegeID)) { //作業主管應可查詢轄下作業人員
				sql.append("AND (EFB.PRIVILEGEID IN ('005', '007', '008') OR EFB.EMP_ID = :userID) ");
				condition.setObject("userID", ws.getUser().getUserID());
			} else {
				sql.append(" AND EFB.EMP_ID LIKE :userID ");
				condition.setObject("userID", ws.getUser().getUserID());
			}

			sql.append(" ORDER BY EFB.REVIEW_STATUS, EFB.BRANCH_AREA_ID, EFB.BRANCH_NBR, EFB.EMP_ID ");

			condition.setQueryString(sql.toString());
			ResultIF orgMemberLst = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = orgMemberLst.getTotalPage(); // 分頁用
			int totalRecord_i = orgMemberLst.getTotalRecord(); // 分頁用
			outputVO.setOrgMemberLst(orgMemberLst);; // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			sendRtnObject(outputVO);
		}			
	}
	
	public void cancelUnauth(Object body, IPrimitiveMap header) throws Exception {
		
		ORG110InputVO inputVO = (ORG110InputVO) body;
		ORG110OutputVO outputVO = new ORG110OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append("UPDATE TBORG_MEMBER_UNAUTH	");
		sb.append("SET ISUNAUTHED = 'N', CANCEL = 'Y', MODIFIER = :modifier, LASTUPDATE = SYSDATE ");
		sb.append("WHERE EMP_ID = :empId ");
		sb.append("AND TO_CHAR(UNAUTH_DATE, 'yyyyMMdd') = :unauthDate ");
		sb.append("AND TO_CHAR(REAUTH_DATE, 'yyyyMMdd') = :reauthDate ");
		queryCondition.setObject("empId", inputVO.getEMP_ID());
		queryCondition.setObject("unauthDate", new SimpleDateFormat("yyyyMMdd").format(inputVO.getUNAUTH_DATE()));
		queryCondition.setObject("reauthDate", new SimpleDateFormat("yyyyMMdd").format(inputVO.getREAUTH_DATE()));
		queryCondition.setObject("modifier", (String) getCommonVariable(SystemVariableConsts.LOGINID));
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		sendRtnObject(outputVO);
	}
	
	public void getProfilePicture(Object body, IPrimitiveMap header) throws Exception {
		
		ORG110InputVO inputVO = (ORG110InputVO) body;
		ORG110OutputVO outputVO = new ORG110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT MEM.EMP_ID, CASE WHEN MEM.EMP_PHOTO IS NOT NULL THEN MEM.EMP_PHOTO ELSE MEMR.EMP_PHOTO END AS EMP_PHOTO ");
		sb.append("FROM TBORG_MEMBER MEM ");
		sb.append("LEFT JOIN (SELECT EMP_ID, EMP_PHOTO FROM TBORG_MEMBER_REVIEW WHERE REVIEW_STATUS = 'W') MEMR ON MEMR.EMP_ID = MEM.EMP_ID ");
		sb.append("WHERE MEM.EMP_ID = :empId ");
		condition.setQueryString(sb.toString());
		condition.setObject("empId", inputVO.getEMP_ID());
		
		List<Map<String, Object>> orgMemberLst = dam.exeQuery(condition);
		List<Map<String, Object>> outputLst = new ArrayList<Map<String,Object>>();
		
		for(Map<String, Object> memberPhoto : orgMemberLst) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put("EMP_ID", memberPhoto.get("EMP_ID"));
			outputMap.put("EMP_PHOTO", ((Blob) memberPhoto.get("EMP_PHOTO")).getBytes(1, (int) ((Blob) memberPhoto.get("EMP_PHOTO")).length()));
			outputLst.add(outputMap);
		}
		
		outputVO.setOrgMemberLst(outputLst);
		sendRtnObject(outputVO);
	}
	
	public void getReviewList (Object body, IPrimitiveMap header) throws Exception {
		
		ORG110InputVO inputVO = (ORG110InputVO) body;
		ORG110OutputVO outputVO = new ORG110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer(); //replace(AGENT_ROLE, ',', ' \n') AS 
		sb.append("SELECT SEQNO, EMP_ID, CUST_ID, EMP_NAME, DEPT_ID, EMP_CELL_NUM, EMP_DEPT_EXT, EMP_FAX, JOB_TITLE_NAME, JOB_RANK, EXPECTED_END_DATE, GROUP_TYPE, ONBOARD_DATE, JOB_ONBOARD_DATE, JOB_RESIGN_DATE, SALES_SUP_EMP_ID, EMP_MAIL_ADDRESS, EMP_EMAIL_ADDRESS, EMP_PHONE_NUM, PERF_EFF_DATE, HANDOVER_FLAG, HANDOVER_DATE, REMARK, EMP_PHOTO, SERVICE_FLAG, CHANGE_FLAG, REVIEW_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBORG_MEMBER_REVIEW ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("AND SERVICE_FLAG = 'A' ");
		sb.append("AND CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("ORDER BY CREATETIME DESC");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("empID", inputVO.getEMP_ID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		for(Map<String, Object> memberPhoto : list) {
			if (null != memberPhoto.get("EMP_PHOTO")) {
				memberPhoto.put("EMP_PHOTO", ((Blob) memberPhoto.get("EMP_PHOTO")).getBytes(1, (int) ((Blob) memberPhoto.get("EMP_PHOTO")).length()));
			}
		}
		
		outputVO.setReviewList(list);
	
		sendRtnObject(outputVO);
	}
}
