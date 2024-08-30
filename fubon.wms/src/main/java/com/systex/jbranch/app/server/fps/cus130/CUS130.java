package com.systex.jbranch.app.server.fps.cus130;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_FIELDPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_FIELDVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_MAIN_MEMOVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_MAIN_ORGVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * cus130
 *
 * @author moron
 * @date 2016/06/29
 * @spec null
 */
@Component("cus130")
@Scope("request")
public class CUS130 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CUS130.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運區

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * ");
		sql.append("from ( ");
		sql.append("  SELECT a.IVG_PLAN_NAME, a.IVG_PLAN_DESC, a.IVG_TYPE, a.IVG_PLAN_TYPE, a.IVG_START_DATE, a.IVG_END_DATE, a.MODIFIER, a.IVG_PLAN_SEQ, ");
		// TOTAL_COUNT
		sql.append("         ( ");
		sql.append("          SELECT COUNT (con1.IVG_PLAN_SEQ) ");
		sql.append("          FROM TBCAM_IVG_PLAN_CONTENT con1 ");
		sql.append("          WHERE con1.IVG_PLAN_SEQ = a.IVG_PLAN_SEQ ");
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("         AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("         AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		} else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("         AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		} else {
			sql.append("         AND (con1.BRANCH_NBR IN (:brNbrList)) ");
		}
		sql.append("         ) AS TOTAL_COUNT, ");
		// NO_RETURN
		sql.append("         ( ");
		sql.append("          SELECT COUNT (con2.IVG_PLAN_SEQ) ");
		sql.append("          FROM TBCAM_IVG_PLAN_CONTENT con2 ");
		sql.append("          WHERE con2.IVG_PLAN_SEQ = a.IVG_PLAN_SEQ AND RES_FLAG = 'N' ");
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("          AND (con2.BRANCH_NBR IN (:brNbrList) OR con2.BRANCH_NBR IS NULL) ");
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("          AND (con2.BRANCH_NBR IN (:brNbrList) OR con2.BRANCH_NBR IS NULL) ");
		} else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("          AND (con2.BRANCH_NBR IN (:brNbrList) OR con2.BRANCH_NBR IS NULL) ");
		} else {
			sql.append("          AND (con2.BRANCH_NBR IN (:brNbrList)) ");
		}
		sql.append("        ) AS NO_RETURN, ");
		
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		
		//
		sql.append("        a.LASTUPDATE, b.DOC_ID, b.DOC_NAME, c.EMP_NAME AS MEMP_NAME ");
		sql.append("  FROM TBCAM_IVG_PLAN_MAIN a ");
		sql.append("  LEFT JOIN TBSYS_FILE_MAIN b ON a.DOC_ID = b.DOC_ID ");
		sql.append("  LEFT JOIN VWORG_EMP_INFO d ON a.CREATOR = d.EMP_ID ");
		sql.append("  LEFT JOIN TBORG_MEMBER c ON a.MODIFIER = c.EMP_ID ");
		sql.append("  WHERE 1 = 1 ");
		sql.append("  and d.ROLE_TYPE = 'Y' ");

		// WHERE
		// 登入者身份!=總行
		// #0006732:新增回報計劃功能查詢設控 BY 君榮  === MODIFY BY OCEAN
		// #0000836:新增可檢視全行回報計劃角色(AU-董事會稽核室場外監控組"角色可於新增回報計劃功能中檢視全行回報計劃內容)BY 君榮  === MODIFY BY OCEAN
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			if (StringUtils.equals("B026", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
				StringUtils.equals("B027", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
				StringUtils.equals("AU", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {

			} else {
				// 使用者需看到所在「處」所有人的回報資料
				sql.append("  AND EXISTS ( ");
				sql.append("    SELECT EN.EMP_ID, EN.START_TIME, EN.END_TIME, EN.DEPT_ID, EN.ROLE_ID ");
				sql.append("    FROM TBPMS_EMPLOYEE_REC_N EN ");
				sql.append("    LEFT JOIN TBPMS_ORG_REC_N ORGN ON EN.DEPT_ID = ORGN.DEPT_ID");
				sql.append("    WHERE TRIM(EN.DEPT_ID) IS NOT NULL ");
				sql.append("    AND A.CREATOR = EN.EMP_ID ");
				sql.append("    AND TRUNC(A.CREATETIME) BETWEEN EN.START_TIME AND EN.END_TIME ");
				sql.append("    AND ( EN.START_TIME BETWEEN ORGN.START_TIME AND ORGN.END_TIME ");
				sql.append("    or EN.END_TIME BETWEEN ORGN.START_TIME AND ORGN.END_TIME ) ");
				sql.append("    AND D.DEPT_ID_20 = ORGN.HEAD_OFFICE_ID ");
				sql.append("    AND EXISTS (SELECT 1 FROM TBORG_MEMBER T ");
				sql.append("                LEFT JOIN VWORG_EMP_INFO S ON T.EMP_ID = S.EMP_ID ");
				sql.append("                WHERE T.EMP_ID = :loginID ");
				sql.append("                AND S.DEPT_ID_20 = ORGN.HEAD_OFFICE_ID) ");
				sql.append("  ) ");
				queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
			}

		}

		if (!headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("  AND a.CREATOR = :creator ");
			queryCondition.setObject("creator", getUserVariable(FubonSystemVariableConsts.LOGINID));
		}

		if (!StringUtils.isBlank(inputVO.getCreatorID())) {
			sql.append("  AND a.CREATOR = :creator2 ");
			queryCondition.setObject("creator2", inputVO.getCreatorID());
		}

		if (!StringUtils.isBlank(inputVO.getCreatorName())) {
			sql.append("  AND d.EMP_NAME like :creator_name ");
			queryCondition.setObject("creator_name", "%" + inputVO.getCreatorName() + "%");
		}

		if (!StringUtils.isBlank(inputVO.getIvgPlanName())) {
			sql.append("  AND a.IVG_PLAN_NAME like :plan_name ");
			queryCondition.setObject("plan_name", "%" + inputVO.getIvgPlanName() + "%");
		}

		if (!StringUtils.isBlank(inputVO.getIvgType())) {
			sql.append("  AND a.IVG_TYPE = :type ");
			queryCondition.setObject("type", inputVO.getIvgType());
		}

		if (!StringUtils.isBlank(inputVO.getIvgPlanType())) {
			sql.append("  AND a.IVG_PLAN_TYPE = :plan_type ");
			queryCondition.setObject("plan_type", inputVO.getIvgPlanType());
		}

		if (inputVO.getIvgStartDate() != null) {
			sql.append("  AND a.IVG_START_DATE >= :start ");
			queryCondition.setObject("start", inputVO.getIvgStartDate());
		}

		if (inputVO.getIvgEndDate() != null) {
			sql.append("AND a.IVG_END_DATE <= :end ");
			queryCondition.setObject("end", inputVO.getIvgEndDate());
		}
		//

		sql.append(") temp ");
		sql.append("where temp.TOTAL_COUNT <> 0 ");
		sql.append("order by IVG_END_DATE desc");

		queryCondition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(return_VO);
	}

	public void getRole(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		List<Map<String, Object>> list = this.getRole_Sql(null);

		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}

	private List<Map<String, Object>> getRole_Sql(String[] roleName) throws Exception {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("With Temp As ( ");
		sql.append("  select PARAM_CODE, PARAM_NAME ");
		sql.append("  from TBSYSPARAMETER ");
		sql.append("  where PARAM_TYPE = 'FUBONSYS.HEADMGR_ROLE'");
		sql.append(")");
		sql.append(", Temp2 As ( ");
		sql.append("  SELECT ROLE_NAME, listagg(ROLE_ID,',') WITHIN GROUP (ORDER BY ROLE_ID) as ROLE_ID ");
		sql.append("  FROM TBORG_ROLE ");
		sql.append("  WHERE REVIEW_STATUS = 'Y' ");
		sql.append("  GROUP BY ROLE_NAME");
		sql.append("), Temp3 As ( ");
		sql.append("  select PARAM_CODE, PARAM_NAME ");
		sql.append("  from TBSYSPARAMETER ");
		sql.append("  where PARAM_TYPE = 'FUBONSYS.FAIA_ROLE'");
		sql.append(") ");
		sql.append("SELECT ROLE_NAME, ROLE_ID ");
		sql.append("FROM Temp2 ");
		sql.append("where 1 = 1 ");
		
		// 2018/2/22 add
		if (roleName != null) {
			sql.append("and ROLE_NAME in :role_list ");
			queryCondition.setObject("role_list", roleName);
		}
		
		// 登入者身份=總行
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {

		} else {
			sql.append("and substr(ROLE_ID,0,4) not in (select PARAM_CODE from Temp) ");
			sql.append("and substr(ROLE_ID,0,4) not in (select PARAM_CODE from Temp3) ");
		}
		
		sql.append("ORDER BY CASE WHEN ROLE_ID in ('ALL') THEN 9 ");
		sql.append("              WHEN ROLE_ID in ('I001', 'I999', 'B015', 'B034', 'B036') THEN 8 ");
		sql.append("              WHEN substr(ROLE_ID, 0, 4) in (select PARAM_CODE from Temp) Then 7 ELSE 0 END, ");
		sql.append("         ROLE_NAME ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return list;
	}

	public void insert(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		// TBCAM_IVG_PLAN_MAIN
		String id = getSN("CUS130_MAIN");
		TBCAM_IVG_PLAN_MAINVO vo = new TBCAM_IVG_PLAN_MAINVO();
		vo.setIVG_PLAN_SEQ(new BigDecimal(id));
		vo.setIVG_PLAN_NAME(inputVO.getIvgPlanName());
		vo.setIVG_PLAN_TYPE(inputVO.getIvgPlanType());
		vo.setIVG_TYPE(inputVO.getIvgType());
		vo.setIVG_START_DATE(new Timestamp(inputVO.getIvgStartDate().getTime()));
		vo.setIVG_END_DATE(new Timestamp(inputVO.getIvgEndDate().getTime()));
		vo.setIVG_PLAN_DESC(inputVO.getIvgPlanDesc());
		dam.create(vo);
		
		// file
		if (!StringUtils.isBlank(inputVO.getFileName())) {
			String SN = getSN("File");
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());

			vo.setDOC_ID(SN);

			TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
			fvo.setDOC_ID(SN);
			fvo.setDOC_NAME(inputVO.getFileRealName());
			fvo.setSUBSYSTEM_TYPE("CAM");
			fvo.setDOC_TYPE("01");
			dam.create(fvo);

			TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
			dvo.setDOC_ID(SN);
			dvo.setDOC_VERSION_STATUS("2");
			dvo.setDOC_FILE_TYPE("D");
			dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
			dam.create(dvo);
		}
		
		dam.update(vo);

		// TBCAM_IVG_PLAN_CONTENT
		// 設定回報對象
		Integer oldSize = -1;
		Integer newSize = 0;
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();

		// JDBC START
		GenFileTools gft = new GenFileTools();
		Connection conn = gft.getConnection();
		conn.setAutoCommit(false);

		StringBuffer contentSql = new StringBuffer();
		contentSql.append("INSERT INTO TBCAM_IVG_PLAN_CONTENT (IVG_RESULT_SEQ, IVG_PLAN_SEQ, EMP_ID, BRANCH_NBR, RES_FLAG, ALERT_FLAG, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, REGION_CENTER_ID, BRANCH_AREA_ID, ROLE_ID) ");
		contentSql.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

		PreparedStatement pstmtC = conn.prepareStatement(contentSql.toString());

		StringBuffer fileSql = new StringBuffer();
		fileSql.append("INSERT INTO TBCAM_IVG_PLAN_FIELD (IVG_FIELD_SEQ, IVG_RESULT_SEQ, IVG_PLAN_SEQ, FIELD_LABEL, FIELD_TYPE, FIELD_VALUE, DROPDOWN_CONTENT, FIELD_RANK, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, DISPLAYONLY) ");
		fileSql.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

		PreparedStatement pstmtF = conn.prepareStatement(fileSql.toString());
		//===

		try {
			switch (inputVO.getSetType()) {
				case "1":
	
					Boolean ALL_BRANCH = false;
					Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
					Set<String> roleList = new HashSet<String>();
					// 2017/7/17 add branch name temp
					for (Map<String, Object> map : inputVO.getTotalList()) {
						TBCAM_IVG_PLAN_MAIN_ORGVO main_vo = new TBCAM_IVG_PLAN_MAIN_ORGVO();
						main_vo.setIVG_ORG_SEQ(new BigDecimal(getSN("CUS130_MAIN_ORG")));
						main_vo.setIVG_PLAN_SEQ(new BigDecimal(id));
						main_vo.setALL_BRANCH(ObjectUtils.toString(map.get("ALL_BRANCH")));
						main_vo.setREGION_CENTER_ID(ObjectUtils.toString(map.get("REGION")));
						main_vo.setBRANCH_AREA_ID(ObjectUtils.toString(map.get("AREA")));
						main_vo.setBRANCH_NBR(ObjectUtils.toString(map.get("BRANCH")));
						dam.create(main_vo);
						// 選總行只有一個 ALL_BRANCH
						if (map.get("ALL_BRANCH") != null) {
							ALL_BRANCH = true;
							break;
						}
					}
	
					// 2017/7/28 emp_id temp
					for (Map<String, Object> fmap : inputVO.getSelectList()) {
						TBCAM_IVG_PLAN_MAIN_MEMOVO memovo = new TBCAM_IVG_PLAN_MAIN_MEMOVO();
						memovo.setIVG_MEMO_SEQ(new BigDecimal(getSN("CUS130_MAIN_MEMO")));
						memovo.setIVG_PLAN_SEQ(new BigDecimal(id));
						memovo.setEMP_ID(ObjectUtils.toString(fmap.get("EMP_ID")));
						memovo.setEMP_NAME(ObjectUtils.toString(fmap.get("EMP_NAME")));
						memovo.setMEMO_DESC(ObjectUtils.toString(fmap.get("DESC")));
						dam.create(memovo);
					}
	
					// 2017/7/17 add role name temp
					List<String> roleNameTemp = new ArrayList<String>();
					for (Map<String, Object> map2 : inputVO.getMemberList()) {
						String[] roleArr = StringUtils.split(ObjectUtils.toString(map2.get("ROLE_ID")), ",");
						roleList.addAll(Arrays.asList(roleArr));
						roleNameTemp.add(ObjectUtils.toString(map2.get("ROLE_NAME")));
					}
					vo.setROLE_ORG(TextUtils.join(",", roleNameTemp));
					dam.update(vo);
	
					// 總行人員
					if (roleList.contains("ALL")) {
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FUBONSYS.HEADMGR_ROLE'");
						List<Map<String, Object>> list = dam.exeQuery(queryCondition);
						for (Map<String, Object> param : list) {
							roleList.add(param.get("PARAM_CODE").toString());
						}
					}
	
					// 2017/7/31 ocean 找轄上主管 so for
					if (inputVO.getSelectList().size() > 0) {
						for (Map<String, Object> selmap : inputVO.getSelectList()) {
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							// 2019/07/03 MODIFY BY OCEAN
							sql.append("WITH BRA_ROLE_LIST AS ( ");
							sql.append("  SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, PARAM_CODE AS ROLE_ID ");
							sql.append("  FROM VWORG_DEFN_INFO DEFN ");
							sql.append("  LEFT JOIN TBSYSPARAMETER PAM ON PARAM_TYPE IN ('FUBONSYS.FCH_ROLE', 'FUBONSYS.FC_ROLE', 'FUBONSYS.PSOP_ROLE', 'FUBONSYS.BMMGR_ROLE', 'FUBONSYS.FAIA_ROLE') ");
							sql.append("  UNION ");
							sql.append("  SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, NULL AS BRANCH_NBR, NULL AS BRANCH_NAME, PARAM_CODE AS ROLE_ID ");
							sql.append("  FROM VWORG_DEFN_INFO DEFN ");
							sql.append("  LEFT JOIN TBSYSPARAMETER PAM ON PARAM_TYPE IN ('FUBONSYS.MBRMGR_ROLE') ");
							sql.append("  UNION ");
							sql.append("  SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME, NULL AS BRANCH_AREA_ID, NULL AS BRANCH_AREA_NAME, NULL AS BRANCH_NBR, NULL AS BRANCH_NAME, PARAM_CODE AS ROLE_ID ");
							sql.append("  FROM VWORG_DEFN_INFO DEFN ");
							sql.append("  LEFT JOIN TBSYSPARAMETER PAM ON PARAM_TYPE IN ('FUBONSYS.ARMGR_ROLE') ");
							sql.append(") ");
							sql.append(", Y_ROLE_EMP_LIST AS ( ");
							sql.append("  SELECT MR.CREATETIME AS CREATETIME, M.EMP_ID, M.CUST_ID, M.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, MR.ROLE_ID, MR.IS_PRIMARY_ROLE AS ROLE_TYPE, SYSDATE AS EFFDT ");
							sql.append("  FROM TBORG_MEMBER M ");
							sql.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
							sql.append("  LEFT JOIN ( ");
							sql.append("      SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
							sql.append("      FROM TBORG_DEFN ");
							sql.append("      START WITH DEPT_ID IS NOT NULL ");
							sql.append("      CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
							sql.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
							sql.append("  WHERE M.SERVICE_FLAG = 'A' ");
							sql.append("  AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
							sql.append(") ");
							sql.append(", N_ROLE_EMP_LIST AS ( ");
							sql.append("  SELECT MR.CREATETIME, M.EMP_ID, MEM.CUST_ID, MEM.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, MR.ROLE_ID, MR.IS_PRIMARY_ROLE AS ROLE_TYPE, M.EFFDT ");
							sql.append("  FROM TBORG_MEMBER_PLURALISM M ");
							sql.append("  LEFT JOIN TBORG_MEMBER MEM ON M.EMP_ID = MEM.EMP_ID ");
							sql.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'N' ");
							sql.append("  LEFT JOIN ( ");
							sql.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
							sql.append("    FROM TBORG_DEFN ");
							sql.append("    START WITH DEPT_ID IS NOT NULL ");
							sql.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
							sql.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
							sql.append("  WHERE (TRUNC(M.TERDTE) >= TRUNC(SYSDATE) OR M.TERDTE IS NULL) ");
							sql.append("  AND M.ACTION <> 'D' ");
							sql.append("  UNION ");
							sql.append("    SELECT MR.CREATETIME AS CREATETIME, M.EMP_ID, M.CUST_ID, M.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, MR.ROLE_ID, MR.IS_PRIMARY_ROLE AS ROLE_TYPE, SYSDATE AS EFFDT ");
							sql.append("  FROM TBORG_MEMBER M ");
							sql.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'N' ");
							sql.append("  LEFT JOIN ( ");
							sql.append("      SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
							sql.append("      FROM TBORG_DEFN ");
							sql.append("      START WITH DEPT_ID IS NOT NULL ");
							sql.append("      CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
							sql.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
							sql.append("  WHERE M.SERVICE_FLAG = 'A' ");
							sql.append("  AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
							sql.append("  AND NOT EXISTS (SELECT R.ROLE_ID FROM TBORG_ROLE R WHERE R.JOB_TITLE_NAME IS NOT NULL AND MR.ROLE_ID = R.ROLE_ID) ");
							sql.append(") ");
							sql.append(", X_ROLE_EMP_LIST AS ( ");
							sql.append(" SELECT NULL AS CREATETIME, M.EMP_ID, M.CUST_ID, M.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, NULL, 'X' AS ROLE_TYPE, SYSDATE AS EFFDT ");
							sql.append(" FROM TBORG_MEMBER M ");
							sql.append(" LEFT JOIN ( ");
							sql.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
							sql.append("    FROM TBORG_DEFN ");
							sql.append("    START WITH DEPT_ID IS NOT NULL ");
							sql.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
							sql.append(" ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
							sql.append(" WHERE M.SERVICE_FLAG = 'A' ");
							sql.append(" AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
							sql.append(" AND NOT EXISTS (SELECT Y.EMP_ID FROM Y_ROLE_EMP_LIST Y WHERE M.EMP_ID = Y.EMP_ID) ");
							sql.append(" AND NOT EXISTS (SELECT N.EMP_ID FROM N_ROLE_EMP_LIST N WHERE M.EMP_ID = N.EMP_ID) ");
							sql.append(") ");
							sql.append(", TREE_BY_EMP AS ( ");
							sql.append("  SELECT * FROM Y_ROLE_EMP_LIST WHERE EMP_ID = :keyEmpID ");
							sql.append("  UNION ");
							sql.append("  SELECT * FROM N_ROLE_EMP_LIST WHERE EMP_ID = :keyEmpID ");
							sql.append("  UNION ");
							sql.append("  SELECT * FROM X_ROLE_EMP_LIST WHERE EMP_ID = :keyEmpID ");
							sql.append(") ");
	
							sql.append("SELECT DISTINCT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, DEPT_ID, EMP_ID, ROLE_ID ");
							sql.append("FROM ( ");
							sql.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_DEPT_ID AS DEPT_ID, EMP_ID, ROLE_ID ");
							sql.append("  FROM VWORG_EMP_INFO ");
							sql.append("  WHERE 1 = 1 ");
							sql.append("  AND (ROLE_ID, EMP_DEPT_ID) IN ( ");
							sql.append("    SELECT ROLE_ID, ");
							sql.append("           CASE WHEN BRANCH_NBR IS NOT NULL THEN BRANCH_NBR ");
							sql.append("                WHEN BRANCH_AREA_ID IS NOT NULL THEN BRANCH_AREA_ID ");
							sql.append("                WHEN REGION_CENTER_ID IS NOT NULL THEN REGION_CENTER_ID ");
							sql.append("           ELSE NULL END AS DEPT_ID ");
							sql.append("    FROM BRA_ROLE_LIST ");
							sql.append("    WHERE ROLE_ID IN (:role_id) ");
							sql.append("    AND CASE WHEN BRANCH_NBR IS NOT NULL THEN BRANCH_NBR ");
							sql.append("             WHEN BRANCH_AREA_ID IS NOT NULL THEN BRANCH_AREA_ID ");
							sql.append("             WHEN REGION_CENTER_ID IS NOT NULL THEN REGION_CENTER_ID ");
							sql.append("        ELSE NULL END IN (SELECT DEPT_ID FROM TREE_BY_EMP) ");
							
							if (!ALL_BRANCH) {
								sql.append("AND ( ");
								for (int i = 0; i < inputVO.getTotalList().size(); i++) {
									if (i == 0) {
										sql.append("( ");
										if (StringUtils.isNotBlank(ObjectUtils.toString(inputVO.getTotalList().get(i).get("REGION"))))
											sql.append("REGION_CENTER_ID = :region" + i + " ");
										if (StringUtils.isNotBlank(ObjectUtils.toString(inputVO.getTotalList().get(i).get("AREA"))))
											sql.append("AND BRANCH_AREA_ID = :branch_area" + i + " ");
										if (StringUtils.isNotBlank(ObjectUtils.toString(inputVO.getTotalList().get(i).get("BRANCH"))))
											sql.append("AND BRANCH_NBR = :branch" + i + " ");
										sql.append(") ");
									}
									// add or
									else {
										sql.append("or ( ");
										if (StringUtils.isNotBlank(ObjectUtils.toString(inputVO.getTotalList().get(i).get("REGION"))))
											sql.append("REGION_CENTER_ID = :region" + i + " ");
										if (StringUtils.isNotBlank(ObjectUtils.toString(inputVO.getTotalList().get(i).get("AREA"))))
											sql.append("AND BRANCH_AREA_ID = :branch_area" + i + " ");
										if (StringUtils.isNotBlank(ObjectUtils.toString(inputVO.getTotalList().get(i).get("BRANCH"))))
											sql.append("AND BRANCH_NBR = :branch" + i + " ");
										sql.append(") ");
									}
								}
								sql.append(") ");
							}
							
							sql.append("  ) ");
	
							sql.append("  UNION ");
	
							sql.append("  SELECT NULL AS REGION_CENTER_ID, NULL AS REGION_CENTER_NAME, NULL AS BRANCH_AREA_ID, NULL AS BRANCH_AREA_NAME, NULL AS BRANCH_NBR, NULL AS BRANCH_NAME, NULL AS DEPT_ID, MR.EMP_ID, MR.ROLE_ID  ");
							sql.append("  FROM TBORG_MEMBER_ROLE MR ");
							sql.append("  INNER JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = MR.EMP_ID AND MEM.SERVICE_FLAG = 'A' AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
							sql.append("  WHERE MR.ROLE_ID IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE in ('FUBONSYS.HEADMGR_ROLE', 'FUBONSYS.FAIA_ROLE')) ");
							sql.append("  AND MR.ROLE_ID IN (:role_id)");
							sql.append(") M ");
	
							sql.append("WHERE INSTR((SELECT LISTAGG(DEFN2.DEPT_ID, ',') WITHIN GROUP (ORDER BY 1) AS DEPT_LIST ");
							sql.append("FROM TBORG_DEFN DEFN ");
							sql.append("LEFT JOIN TBORG_DEFN DEFN2 ON DEFN.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
							sql.append("START WITH DEFN2.DEPT_ID = M.DEPT_ID ");
							sql.append("CONNECT BY PRIOR DEFN2.DEPT_ID  = DEFN2.PARENT_DEPT_ID), (SELECT DEPT_ID FROM TBORG_MEMBER WHERE EMP_ID = :keyEmpID), 1) > 0 ");
	
							queryCondition.setObject("keyEmpID", StringUtils.leftPad(ObjectUtils.toString(selmap.get("EMP_ID")), 6, "0")); // 2017/7/20 補六碼0
							queryCondition.setObject("role_id", roleList);
							queryCondition.setQueryString(sql.toString());
	
							List<Map<String, Object>> list = dam.exeQuery(queryCondition);
							set.addAll(new HashSet<Map<String, Object>>(list));
	
							for (Map<String, Object> map : list) {
								String rid = getSN("CUS130_CONTENT");
	
								// TBCAM_IVG_PLAN_CONTENT
								insertCONTENT(pstmtC, map, id, rid);
								pstmtC.addBatch();
	
								// TBCAM_IVG_PLAN_FIELD
								for (Map<String, Object> fmap : inputVO.getListBase()) {
									Map<String, Object> fieldValue = new HashMap<String, Object>();
	
									if (StringUtils.equals(ObjectUtils.toString(fmap.get("FIELD_LABEL")), "員工編號"))
										fieldValue.put("EMP_ID", ObjectUtils.toString(selmap.get("EMP_ID")));
	
									if (StringUtils.equals(ObjectUtils.toString(fmap.get("FIELD_LABEL")), "員工姓名"))
										fieldValue.put("EMP_NAME", ObjectUtils.toString(selmap.get("EMP_NAME")));
	
									if (StringUtils.equals(ObjectUtils.toString(fmap.get("FIELD_LABEL")), "備註內容"))
										fieldValue.put("DESC", ObjectUtils.toString(selmap.get("DESC")));
	
									insertFIELD(pstmtF, fmap, fieldValue, "byUpload", id, rid);
									pstmtF.addBatch();
								}
							}
						}
	
						pstmtC.executeBatch();
						pstmtF.executeBatch();
						conn.commit();
	
						if (set.size() == 0)
							throw new APException("ehl_01_cus130_007");
					} else {
						List<Map<String, Object>> list = this.getEmp_id_old_sql(null, ALL_BRANCH, inputVO.getTotalList(), roleList);
						set.addAll(new HashSet<Map<String, Object>>(list));
	
						if (set.size() == 0)
							throw new APException("ehl_01_cus130_007");
	
						for (Map<String, Object> map : set) {
							String rid = getSN("CUS130_CONTENT");
	
							// TBCAM_IVG_PLAN_CONTENT
							insertCONTENT(pstmtC, map, id, rid);
							pstmtC.addBatch();
	
							// TBCAM_IVG_PLAN_FIELD
							for (Map<String, Object> fmap : inputVO.getListBase()) {
								insertFIELD(pstmtF, fmap, null, "byChooseRole", id, rid);
								pstmtF.addBatch();
							}
						}
	
						pstmtC.executeBatch();
						pstmtF.executeBatch();
						conn.commit();
					}
					
					break;
				case "2": // 上傳指定回報員編
					Set<String> dup = new HashSet<String>(inputVO.getEmpList());
					oldSize = dup.size();
					for (String str : dup) {
						if (StringUtils.isNotBlank(str)) {
							// 2017/7/20 補六碼0
							str = StringUtils.leftPad(str, 6, "0");
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append("select EMP_ID, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, ROLE_ID from VWORG_BRANCH_EMP_DETAIL_INFO where EMP_ID = :id");
							queryCondition.setObject("id", str);
							queryCondition.setQueryString(sql.toString());
							List<Map<String, Object>> list = dam.exeQuery(queryCondition);
							if (list.size() == 0) {
								error.add(str);
								newSize += 1;
								continue;
							}

							String rid = getSN("CUS130_CONTENT");

							// TBCAM_IVG_PLAN_CONTENT
							insertCONTENT(pstmtC, list.get(0), id, rid);
							pstmtC.addBatch();

							// TBCAM_IVG_PLAN_FIELD
							for (Map<String, Object> fmap : inputVO.getListBase()) {
								insertFIELD(pstmtF, fmap, null, "byChooseEmp", id, rid);
								pstmtF.addBatch();
							}
						}
					}

					pstmtC.executeBatch();
					pstmtF.executeBatch();
					conn.commit();
					
					break;
				case "3": // 上傳客戶名單
					List<Map<String, Object>> idlist = new ArrayList<Map<String, Object>>();
					Set<Map<String, Object>> dup2 = new HashSet<Map<String, Object>>(inputVO.getCustList());
					oldSize = dup2.size();
					for (Map<String, Object> cust : dup2) {
						if (null != cust.get("CUST_ID")) {
							// 2017/7/20 通通大寫
							String CUST_BIG = ObjectUtils.toString(cust.get("CUST_ID")).toUpperCase();
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sql = new StringBuffer();
							sql.append("WITH CUST AS ( ");
							sql.append("  SELECT INFO.EMP_ID, INFO.REGION_CENTER_ID, INFO.BRANCH_AREA_ID, INFO.BRANCH_NBR, INFO.ROLE_ID, ");
							sql.append("         A.CUST_ID, A.CUST_NAME, INFO.AO_CODE, A.BRA_NBR, A.OPEN_BRA_NBR ");
							sql.append("  FROM TBCRM_CUST_MAST A ");
							sql.append("  LEFT JOIN VWORG_EMP_INFO INFO ON A.AO_CODE = INFO.AO_CODE ");
							sql.append("  WHERE A.CUST_ID = :id ");
							sql.append("), ");
							sql.append("BRA_EMP AS ( ");
							sql.append("  SELECT EMP_ID, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, ROLE_ID ");
							sql.append("  FROM VWORG_EMP_INFO ");
							sql.append("  WHERE PRIVILEGEID IN ('011') ");
							sql.append(") ");

							sql.append("SELECT ");
							sql.append("       CASE WHEN CUST.AO_CODE IS NOT NULL AND CUST.EMP_ID IS NOT NULL THEN CUST.EMP_ID ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NOT NULL THEN BRA_EMP.EMP_ID ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NULL AND OPEN_BRA_EMP.EMP_ID IS NOT NULL THEN OPEN_BRA_EMP.EMP_ID ");
							sql.append("       ELSE NULL END AS EMP_ID, ");
							sql.append("       CASE WHEN CUST.AO_CODE IS NOT NULL AND CUST.EMP_ID IS NOT NULL THEN CUST.REGION_CENTER_ID ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NOT NULL THEN BRA_EMP.REGION_CENTER_ID ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NULL AND OPEN_BRA_EMP.EMP_ID IS NOT NULL THEN OPEN_BRA_EMP.REGION_CENTER_ID ");
							sql.append("       ELSE NULL END AS REGION_CENTER_ID, ");
							sql.append("       CASE WHEN CUST.AO_CODE IS NOT NULL AND CUST.EMP_ID IS NOT NULL THEN CUST.BRANCH_AREA_ID ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NOT NULL THEN BRA_EMP.BRANCH_AREA_ID ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NULL AND OPEN_BRA_EMP.EMP_ID IS NOT NULL THEN OPEN_BRA_EMP.BRANCH_AREA_ID ");
							sql.append("       ELSE NULL END AS BRANCH_AREA_ID, ");
							sql.append("       CASE WHEN CUST.AO_CODE IS NOT NULL AND CUST.EMP_ID IS NOT NULL THEN CUST.BRANCH_NBR ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NOT NULL THEN BRA_EMP.BRANCH_NBR ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NULL AND OPEN_BRA_EMP.EMP_ID IS NOT NULL THEN OPEN_BRA_EMP.BRANCH_NBR ");
							sql.append("       ELSE NULL END AS BRANCH_NBR, ");
							sql.append("       CASE WHEN CUST.AO_CODE IS NOT NULL AND CUST.EMP_ID IS NOT NULL THEN CUST.ROLE_ID ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NOT NULL THEN BRA_EMP.ROLE_ID ");
							sql.append("            WHEN CUST.EMP_ID IS NULL AND BRA_EMP.EMP_ID IS NULL AND OPEN_BRA_EMP.EMP_ID IS NOT NULL THEN OPEN_BRA_EMP.ROLE_ID ");
							sql.append("       ELSE NULL END AS ROLE_ID, ");
							sql.append("       CUST.CUST_ID, CUST.CUST_NAME, CUST.AO_CODE, CUST.BRA_NBR, CUST.OPEN_BRA_NBR ");
							sql.append("FROM CUST ");
							sql.append("LEFT JOIN BRA_EMP ON CUST.BRA_NBR = BRA_EMP.BRANCH_NBR ");
							sql.append("LEFT JOIN BRA_EMP OPEN_BRA_EMP ON CUST.OPEN_BRA_NBR = OPEN_BRA_EMP.BRANCH_NBR ");

							queryCondition.setObject("id", CUST_BIG);
							queryCondition.setQueryString(sql.toString());
							List<Map<String, Object>> list = dam.exeQuery(queryCondition);

							if (list.size() == 0) {
								error2.add(ObjectUtils.toString(CUST_BIG));
								newSize += 1;
								continue;
							} else {
								if (StringUtils.isNotBlank((String) list.get(0).get("EMP_ID"))) {
									idlist = list;
								} else {
									error2.add(CUST_BIG);
									newSize += 1;
									continue;
								}
							}

							if (idlist.size() == 0) {
								error2.add(CUST_BIG);
								newSize += 1;
								continue;
							}

							String rid = getSN("CUS130_CONTENT");

							// TBCAM_IVG_PLAN_CONTENT
							insertCONTENT(pstmtC, idlist.get(0), id, rid);
							pstmtC.addBatch();

							// TBCAM_IVG_PLAN_FIELD
							for (Map<String, Object> fmap : inputVO.getListBase()) {
								Map<String, Object> fieldValue = new HashMap<String, Object>();
								if (StringUtils.equals(ObjectUtils.toString(fmap.get("FIELD_LABEL")), "客戶ID"))
									fieldValue.put("CUST_ID", CUST_BIG);

								if (StringUtils.equals(ObjectUtils.toString(fmap.get("FIELD_LABEL")), "客戶姓名"))
									fieldValue.put("CUST_NAME", ObjectUtils.toString(list.get(0).get("CUST_NAME")));

								if (StringUtils.equals(ObjectUtils.toString(fmap.get("FIELD_LABEL")), "備註內容"))
									fieldValue.put("DESC", ObjectUtils.toString(cust.get("DESC")));

								insertFIELD(pstmtF, fmap, fieldValue, "byUploadCust", id, rid);
								pstmtF.addBatch();
							}
						}
					}

					pstmtC.executeBatch();
					pstmtF.executeBatch();
					conn.commit();
					
					break;
				case "4": // 選擇轄下理專/PS
					for (Map<String, Object> map : inputVO.getType4_empList()) {
						String rid = getSN("CUS130_CONTENT");

						// TBCAM_IVG_PLAN_CONTENT
						insertCONTENT(pstmtC, map, id, rid);
						pstmtC.addBatch();

						// TBCAM_IVG_PLAN_FIELD
						for (Map<String, Object> fmap : inputVO.getListBase()) {
							insertFIELD(pstmtF, fmap, null, "byChooseSales", id, rid);
							pstmtF.addBatch();
						}
					}

					pstmtC.executeBatch();
					pstmtF.executeBatch();
					conn.commit();
					
					break;
			}
		} catch(Exception e) {
			if (conn != null) try { conn.rollback(); } catch (Exception e2) {}
			e.printStackTrace();
			return_VO.setErrorMsg(e.getMessage());
		} finally {
			if (pstmtC != null) try { pstmtC.close(); } catch (Exception e) {}
			if (pstmtF != null) try { pstmtF.close(); } catch (Exception e) {}
	        if (conn != null) try { conn.close(); } catch (Exception e) {}
		}

		// 上傳都廢
		if (oldSize == newSize)
			throw new APException("ehl_01_cus130_003");
		
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		
		this.sendRtnObject(return_VO);
	}

	private void insertCONTENT (PreparedStatement pstmtC, Map<String, Object> map, String id, String rid) throws Exception {

		pstmtC.setBigDecimal(1, new BigDecimal(rid));
		pstmtC.setBigDecimal(2, new BigDecimal(id));
		pstmtC.setString(3, ObjectUtils.toString(map.get("EMP_ID")));
		pstmtC.setString(4, ObjectUtils.toString(map.get("BRANCH_NBR")));
		pstmtC.setString(5, "N");
		pstmtC.setNull(6, Types.VARCHAR);
		pstmtC.setBigDecimal(7, BigDecimal.ZERO);
		pstmtC.setTimestamp(8, new Timestamp(Calendar.getInstance().getTime().getTime()));
		pstmtC.setString(9, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
		pstmtC.setString(10, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
		pstmtC.setTimestamp(11, new Timestamp(Calendar.getInstance().getTime().getTime()));
		pstmtC.setString(12, ObjectUtils.toString(map.get("REGION_CENTER_ID")));
		pstmtC.setString(13, ObjectUtils.toString(map.get("BRANCH_AREA_ID")));
		pstmtC.setString(14, ObjectUtils.toString(map.get("ROLE_ID")));
	}

	private void insertFIELD (PreparedStatement pstmtF, Map<String, Object> fmap, Map<String, Object> selmap, String type, String id, String rid) throws Exception{

		pstmtF.setBigDecimal(1, new BigDecimal(getSN("CUS130_FIELD")));
		pstmtF.setBigDecimal(2, new BigDecimal(rid));
		pstmtF.setBigDecimal(3, new BigDecimal(id));
		pstmtF.setString(4, ObjectUtils.toString(fmap.get("FIELD_LABEL")));
		pstmtF.setString(5, ObjectUtils.toString(fmap.get("FIELD_TYPE")));

		switch (type) {
			case "byUpload":
				switch (ObjectUtils.toString(fmap.get("FIELD_LABEL"))) {
					case "員工編號":
						pstmtF.setString(6, ObjectUtils.toString(selmap.get("EMP_ID")));
						pstmtF.setString(14, "Y");
						break;
					case "員工姓名":
						pstmtF.setString(6, ObjectUtils.toString(selmap.get("EMP_NAME")));
						pstmtF.setString(14, "Y");
						break;
					case "備註內容":
						pstmtF.setString(6, ObjectUtils.toString(selmap.get("DESC")));
						pstmtF.setString(14, "Y");
						break;
					default:
						pstmtF.setNull(6, Types.VARCHAR);
						pstmtF.setNull(14, Types.VARCHAR);
						break;
				}
				break;
			case "byUploadCust":
				switch (ObjectUtils.toString(fmap.get("FIELD_LABEL"))) {
					case "客戶ID":
						pstmtF.setString(6, ObjectUtils.toString(selmap.get("CUST_ID")));
						pstmtF.setString(14, "Y");
						break;
					case "客戶姓名":
						pstmtF.setString(6, ObjectUtils.toString(selmap.get("CUST_NAME")));
						pstmtF.setString(14, "Y");
						break;
					case "備註內容":
						pstmtF.setString(6, ObjectUtils.toString(selmap.get("DESC")));
						pstmtF.setString(14, "Y");
						break;
					default:
						pstmtF.setNull(6, Types.VARCHAR);
						pstmtF.setNull(14, Types.VARCHAR);
						break;
				}
				break;
			default:
				pstmtF.setNull(6, Types.VARCHAR);
				pstmtF.setNull(14, Types.VARCHAR);
				break;
			
		}

		pstmtF.setString(7, ObjectUtils.toString(fmap.get("DROPDOWN_CONTENT")));
		pstmtF.setBigDecimal(8, new BigDecimal(ObjectUtils.toString(fmap.get("ORDER_NO"))));
		pstmtF.setBigDecimal(9, BigDecimal.ZERO);
		pstmtF.setTimestamp(10, new Timestamp(Calendar.getInstance().getTime().getTime()));
		pstmtF.setString(11, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
		pstmtF.setString(12, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
		pstmtF.setTimestamp(13, new Timestamp(Calendar.getInstance().getTime().getTime()));
	}
	

	// old code sql
	private List<Map<String, Object>> getEmp_id_old_sql(BigDecimal IVG_PLAN_SEQ, Boolean ALL_BRANCH, List<Map<String, Object>> totalList, Set<String> roleList) throws JBranchException {

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_DEPT_ID, EMP_ID, ROLE_ID ");
		sql.append("FROM VWORG_EMP_INFO ");
		sql.append("WHERE ROLE_ID IN (:role_id) ");

		if (!ALL_BRANCH) {
			sql.append("AND ( ");
			for (int i = 0; i < totalList.size(); i++) {
				if (i == 0) {
					sql.append("( ");
					if (StringUtils.isNotBlank(ObjectUtils.toString(totalList.get(i).get("REGION")))) {
						sql.append("REGION_CENTER_ID = :region" + i + " ");
						queryCondition.setObject("region" + i, totalList.get(i).get("REGION"));
					}

					if (StringUtils.isNotBlank(ObjectUtils.toString(totalList.get(i).get("AREA")))) {
						sql.append("AND BRANCH_AREA_ID = :branch_area" + i + " ");
						queryCondition.setObject("branch_area" + i, totalList.get(i).get("AREA"));
					}

					if (StringUtils.isNotBlank(ObjectUtils.toString(totalList.get(i).get("BRANCH")))) {
						sql.append("AND BRANCH_NBR = :branch" + i + " ");
						queryCondition.setObject("branch" + i, totalList.get(i).get("BRANCH"));
					}

					sql.append(") ");
				}
				// add or
				else {
					sql.append("or ( ");
					if (StringUtils.isNotBlank(ObjectUtils.toString(totalList.get(i).get("REGION")))) {
						sql.append("REGION_CENTER_ID = :region" + i + " ");
						queryCondition.setObject("region" + i, totalList.get(i).get("REGION"));
					}

					if (StringUtils.isNotBlank(ObjectUtils.toString(totalList.get(i).get("AREA")))) {
						sql.append("AND BRANCH_AREA_ID = :branch_area" + i + " ");
						queryCondition.setObject("branch_area" + i, totalList.get(i).get("AREA"));
					}

					if (StringUtils.isNotBlank(ObjectUtils.toString(totalList.get(i).get("BRANCH")))) {
						sql.append("AND BRANCH_NBR = :branch" + i + " ");
						queryCondition.setObject("branch" + i, totalList.get(i).get("BRANCH"));
					}

					sql.append(") ");
				}
			}
			sql.append(") ");
		}

		// 2018/2/22 add for update
		if (IVG_PLAN_SEQ != null) {
			sql.append("AND (EMP_ID, ROLE_ID) NOT IN ( ");
			sql.append("  SELECT EMP_ID, ROLE_ID ");
			sql.append("  FROM TBCAM_IVG_PLAN_CONTENT old ");
			sql.append("  WHERE old.IVG_PLAN_SEQ = :ivg_plan_seq ");
			sql.append(") ");

			logger.info("SQLException(ORA-00936: missing expression):ivg_plan_seq=" + IVG_PLAN_SEQ);
			queryCondition.setObject("ivg_plan_seq", IVG_PLAN_SEQ);
		}
		queryCondition.setObject("role_id", roleList);
		queryCondition.setQueryString(sql.toString());

		logger.info("SQLException(ORA-00936: missing expression):role_id=" + roleList);
		logger.info("SQLException(ORA-00936: missing expression):" + sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return list;
	}
	

	// 流水號
	private String getSN(String name) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch (name) {
			case "File":
				sql.append("SELECT SQ_TBSYS_FILE_MAIN.nextval AS SEQ FROM DUAL ");
				break;
			case "CUS130_MAIN":
				sql.append("SELECT SQ_CUS130_MAIN.nextval AS SEQ FROM DUAL ");
				break;
			case "CUS130_MAIN_ORG":
				sql.append("SELECT SQ_CUS130_MAIN_ORG.nextval AS SEQ FROM DUAL ");
				break;
			case "CUS130_MAIN_MEMO":
				sql.append("SELECT SQ_CUS130_MAIN_MEMO.nextval AS SEQ FROM DUAL ");
				break;
			case "CUS130_CONTENT":
				sql.append("SELECT SQ_CUS130_CONTENT.nextval AS SEQ FROM DUAL ");
				break;
			case "CUS130_FIELD":
				sql.append("SELECT SQ_CUS130_FIELD.nextval AS SEQ FROM DUAL ");
				break;
		}
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	public void update(Object body, IPrimitiveMap header) throws Exception {

		CUS130InputVO inputVO = (CUS130InputVO) body;
		dam = this.getDataAccessManager();


		// TODO:JDBC START
		GenFileTools gft = new GenFileTools();
		Connection conn = gft.getConnection();
		conn.setAutoCommit(false);

		StringBuffer contentSql = new StringBuffer();
		contentSql.append("INSERT INTO TBCAM_IVG_PLAN_CONTENT (IVG_RESULT_SEQ, IVG_PLAN_SEQ, EMP_ID, BRANCH_NBR, RES_FLAG, ALERT_FLAG, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, REGION_CENTER_ID, BRANCH_AREA_ID, ROLE_ID) ");
		contentSql.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

		PreparedStatement pstmtC = conn.prepareStatement(contentSql.toString());

		StringBuffer fileSql = new StringBuffer();
		fileSql.append("INSERT INTO TBCAM_IVG_PLAN_FIELD (IVG_FIELD_SEQ, IVG_RESULT_SEQ, IVG_PLAN_SEQ, FIELD_LABEL, FIELD_TYPE, FIELD_VALUE, DROPDOWN_CONTENT, FIELD_RANK, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, DISPLAYONLY) ");
		fileSql.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

		PreparedStatement pstmtF = conn.prepareStatement(fileSql.toString());
		//===

		// TBCAM_IVG_PLAN_MAIN
		TBCAM_IVG_PLAN_MAINVO vo = new TBCAM_IVG_PLAN_MAINVO();
		vo = (TBCAM_IVG_PLAN_MAINVO) dam.findByPKey(TBCAM_IVG_PLAN_MAINVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
			vo.setIVG_PLAN_NAME(inputVO.getIvgPlanName());
			vo.setIVG_PLAN_TYPE(inputVO.getIvgPlanType());
			vo.setIVG_TYPE(inputVO.getIvgType());
			vo.setIVG_START_DATE(new Timestamp(inputVO.getIvgStartDate().getTime()));
			vo.setIVG_END_DATE(new Timestamp(inputVO.getIvgEndDate().getTime()));
			vo.setIVG_PLAN_DESC(inputVO.getIvgPlanDesc());
			dam.update(vo);
			// file
			if (!StringUtils.isBlank(inputVO.getFileName())) {
				// remove garbage
				if (!StringUtils.isBlank(vo.getDOC_ID())) {
					TBSYS_FILE_MAINVO fgvo = new TBSYS_FILE_MAINVO();
					fgvo = (TBSYS_FILE_MAINVO) dam.findByPKey(TBSYS_FILE_MAINVO.TABLE_UID, vo.getDOC_ID());
					if (fgvo != null) {
						dam.delete(fgvo);
					}
					TBSYS_FILE_DETAILVO dgvo = new TBSYS_FILE_DETAILVO();
					dgvo = (TBSYS_FILE_DETAILVO) dam.findByPKey(TBSYS_FILE_DETAILVO.TABLE_UID, vo.getDOC_ID());
					if (dgvo != null) {
						dam.delete(dgvo);
					}
				}
				//
				String SN = getSN("File");
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());

				vo.setDOC_ID(SN);

				TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
				fvo.setDOC_ID(SN);
				fvo.setDOC_NAME(inputVO.getFileRealName());
				fvo.setSUBSYSTEM_TYPE("CAM");
				fvo.setDOC_TYPE("01");
				dam.create(fvo);

				TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
				dvo.setDOC_ID(SN);
				dvo.setDOC_VERSION_STATUS("2");
				dvo.setDOC_FILE_TYPE("D");
				dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
				dam.create(dvo);
				dam.update(vo);
			}

			// 2018/2/22 mantis 4246 edit can add new role
			if (inputVO.getTotalList().size() > 0) {
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				condition.setQueryString("select ALL_BRANCH, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR from TBCAM_IVG_PLAN_MAIN_ORG where IVG_PLAN_SEQ = :seq ");
				condition.setObject("seq", inputVO.getSeq());
				List<Map<String, Object>> old_list = dam.exeQuery(condition);
				//
				Boolean ALL_BRANCH = old_list.get(0).get("ALL_BRANCH") != null ? true : false;
				// TBCAM_IVG_PLAN_MAIN_ORG
				if (!ALL_BRANCH) {
					for (Map<String, Object> map : inputVO.getTotalList()) {
						// 選總行只有一個 ALL_BRANCH
						if (map.get("ALL_BRANCH") != null) {
							ALL_BRANCH = true;
							// DEL 先前只留一個ALL_BRANCH
							QueryConditionIF del_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							del_con.setQueryString("delete TBCAM_IVG_PLAN_MAIN_ORG where IVG_PLAN_SEQ = :seq");
							del_con.setObject("seq", inputVO.getSeq());
							dam.exeUpdate(del_con);
							// add
							TBCAM_IVG_PLAN_MAIN_ORGVO main_vo = new TBCAM_IVG_PLAN_MAIN_ORGVO();
							main_vo.setIVG_ORG_SEQ(new BigDecimal(getSN("CUS130_MAIN_ORG")));
							main_vo.setIVG_PLAN_SEQ(inputVO.getSeq());
							main_vo.setALL_BRANCH(ObjectUtils.toString(map.get("ALL_BRANCH")));
							main_vo.setREGION_CENTER_ID(ObjectUtils.toString(map.get("REGION")));
							main_vo.setBRANCH_AREA_ID(ObjectUtils.toString(map.get("AREA")));
							main_vo.setBRANCH_NBR(ObjectUtils.toString(map.get("BRANCH")));
							dam.create(main_vo);
							break;
						} else {
							// check exist
							Boolean check = true;
							for (Map<String, Object> old_map : old_list) {
								if (StringUtils.equals(ObjectUtils.toString(old_map.get("REGION_CENTER_ID")), ObjectUtils.toString(map.get("REGION"))) && StringUtils.equals(ObjectUtils.toString(old_map.get("BRANCH_AREA_ID")), ObjectUtils.toString(map.get("AREA"))) && StringUtils.equals(ObjectUtils.toString(old_map.get("BRANCH_NBR")), ObjectUtils.toString(map.get("BRANCH")))) {
									check = false;
									break;
								}
							}
							if (check) {
								TBCAM_IVG_PLAN_MAIN_ORGVO main_vo = new TBCAM_IVG_PLAN_MAIN_ORGVO();
								main_vo.setIVG_ORG_SEQ(new BigDecimal(getSN("CUS130_MAIN_ORG")));
								main_vo.setIVG_PLAN_SEQ(inputVO.getSeq());
								main_vo.setALL_BRANCH(ObjectUtils.toString(map.get("ALL_BRANCH")));
								main_vo.setREGION_CENTER_ID(ObjectUtils.toString(map.get("REGION")));
								main_vo.setBRANCH_AREA_ID(ObjectUtils.toString(map.get("AREA")));
								main_vo.setBRANCH_NBR(ObjectUtils.toString(map.get("BRANCH")));
								dam.create(main_vo);
							}
						}
					}
				}
				// 合併ROLE_ORG, 不過roleList還是目前的
				Set<String> roleList = new HashSet<String>();
				Set<String> roleNameTemp = new HashSet<String>();
				roleNameTemp.addAll(Arrays.asList(StringUtils.split(vo.getROLE_ORG(), ",")));
				for (Map<String, Object> map2 : inputVO.getMemberList()) {
					String[] roleArr = StringUtils.split(ObjectUtils.toString(map2.get("ROLE_ID")), ",");
					roleList.addAll(Arrays.asList(roleArr));
					roleNameTemp.add(ObjectUtils.toString(map2.get("ROLE_NAME")));
				}
				vo.setROLE_ORG(TextUtils.join(",", roleNameTemp));
				dam.update(vo);
				// 總行人員
				if (roleList.contains("ALL")) {
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FUBONSYS.HEADMGR_ROLE'");
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					for (Map<String, Object> param : list) {
						roleList.add(param.get("PARAM_CODE").toString());
					}
				}
				// old code
				Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
				List<Map<String, Object>> list = this.getEmp_id_old_sql(inputVO.getSeq(), ALL_BRANCH, inputVO.getTotalList(), roleList);
				set.addAll(new HashSet<Map<String, Object>>(list));
				// not exist add
				for (Map<String, Object> map : set) {
					String rid = getSN("CUS130_CONTENT");

					// TBCAM_IVG_PLAN_CONTENT
					insertCONTENT(pstmtC, map, inputVO.getSeq().toString(), rid);
					pstmtC.addBatch();

					// TBCAM_IVG_PLAN_FIELD
					for (Map<String, Object> fmap : inputVO.getListBase()) {
						insertFIELD(pstmtF, fmap, null, "byOther", inputVO.getSeq().toString(), rid);
						pstmtF.addBatch();
					}
				}

				pstmtC.executeBatch();
				pstmtF.executeBatch();
				conn.commit();
			}
			// 2018/2/22 mantis 4246 change edit run again
			// 這個會合併上面的分行
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("select * from TBCAM_IVG_PLAN_MAIN_ORG where IVG_PLAN_SEQ = :seq ");
			condition.setObject("seq", inputVO.getSeq());
			List<Map<String, Object>> org_list = dam.exeQuery(condition);
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("select * from TBCAM_IVG_PLAN_MAIN_MEMO where IVG_PLAN_SEQ = :seq ");
			condition.setObject("seq", inputVO.getSeq());
			List<Map<String, Object>> memo_list = dam.exeQuery(condition);
			// 設定回報對象才會有且檢視員工這功能不支援4246
			if (org_list.size() > 0 && memo_list.size() == 0) {
				Boolean ALL_BRANCH = false;
				// 選總行只有一個 ALL_BRANCH
				if (org_list.get(0).get("ALL_BRANCH") != null)
					ALL_BRANCH = true;
				else {
					// 改個名字而已
					for (Map<String, Object> map : org_list) {
						map.put("REGION", map.get("REGION_CENTER_ID"));
						map.put("AREA", map.get("BRANCH_AREA_ID"));
						map.put("BRANCH", map.get("BRANCH_NBR"));
					}
				}
				// old role
				String[] roleName = StringUtils.split(vo.getROLE_ORG(), ",");
				List<Map<String, Object>> role_list = this.getRole_Sql(roleName);
				Set<String> roleList = new HashSet<String>();
				for (Map<String, Object> map : role_list) {
					String[] roleArr = StringUtils.split(ObjectUtils.toString(map.get("ROLE_ID")), ",");
					roleList.addAll(Arrays.asList(roleArr));
				}
				// 總行人員
				if (roleList.contains("ALL")) {
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FUBONSYS.HEADMGR_ROLE'");
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					for (Map<String, Object> param : list) {
						roleList.add(param.get("PARAM_CODE").toString());
					}
				}
				// old code
				Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
				List<Map<String, Object>> list = this.getEmp_id_old_sql(inputVO.getSeq(), ALL_BRANCH, org_list, roleList);
				set.addAll(new HashSet<Map<String, Object>>(list));
				// not exist add
				for (Map<String, Object> map : set) {
					String rid = getSN("CUS130_CONTENT");

					// TBCAM_IVG_PLAN_CONTENT
					insertCONTENT(pstmtC, map, inputVO.getSeq().toString(), rid);
					pstmtC.addBatch();

					// TBCAM_IVG_PLAN_FIELD
					for (Map<String, Object> fmap : inputVO.getListBase()) {
						insertFIELD(pstmtF, fmap, null, "byOther", inputVO.getSeq().toString(), rid);
						pstmtF.addBatch();
					}
				}

				pstmtC.executeBatch();
				pstmtF.executeBatch();
				conn.commit();
			}

			if (!inputVO.getStartDisable()) {
				// TBCAM_IVG_PLAN_FIELD
				// del first
				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				condition.setQueryString("delete TBCAM_IVG_PLAN_FIELD where IVG_PLAN_SEQ = :seq and (DISPLAYONLY is null or DISPLAYONLY != 'Y')");
				condition.setObject("seq", inputVO.getSeq());
				dam.exeUpdate(condition);
				// update RES_FLAG
				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				condition.setQueryString("UPDATE TBCAM_IVG_PLAN_CONTENT SET RES_FLAG = 'N' where IVG_PLAN_SEQ = :seq");
				condition.setObject("seq", inputVO.getSeq());
				dam.exeUpdate(condition);
				// then add
				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				condition.setQueryString("select IVG_RESULT_SEQ from TBCAM_IVG_PLAN_CONTENT where IVG_PLAN_SEQ = :seq");
				condition.setObject("seq", inputVO.getSeq());
				List<Map<String, Object>> list = dam.exeQuery(condition);
				for (Map<String, Object> map : list) {
					// TBCAM_IVG_PLAN_FIELD
					for (Map<String, Object> fmap : inputVO.getListBase()) {
						// not need Y
						if (!"Y".equals(ObjectUtils.toString(fmap.get("DISPLAYONLY")))) {
							TBCAM_IVG_PLAN_FIELDVO pvo = new TBCAM_IVG_PLAN_FIELDVO();
							TBCAM_IVG_PLAN_FIELDPK ppk = new TBCAM_IVG_PLAN_FIELDPK();
							ppk.setIVG_FIELD_SEQ(new BigDecimal(getSN("CUS130_FIELD")));
							ppk.setIVG_PLAN_SEQ(inputVO.getSeq());
							ppk.setIVG_RESULT_SEQ(new BigDecimal(ObjectUtils.toString(map.get("IVG_RESULT_SEQ"))));
							pvo.setcomp_id(ppk);
							pvo.setFIELD_RANK(new BigDecimal(ObjectUtils.toString(fmap.get("ORDER_NO"))));
							pvo.setFIELD_LABEL(ObjectUtils.toString(fmap.get("FIELD_LABEL")));
							pvo.setFIELD_TYPE(ObjectUtils.toString(fmap.get("FIELD_TYPE")));
							pvo.setDROPDOWN_CONTENT(ObjectUtils.toString(fmap.get("DROPDOWN_CONTENT")));
							dam.create(pvo);
						}
					}
				}
			}
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}

	public void copy(Object body, IPrimitiveMap header) throws Exception {
		
		this.insert(body, header);
	}

	public void delete(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		dam = this.getDataAccessManager();

		// TBCAM_IVG_PLAN_MAIN
		TBCAM_IVG_PLAN_MAINVO vo = new TBCAM_IVG_PLAN_MAINVO();
		vo = (TBCAM_IVG_PLAN_MAINVO) dam.findByPKey(TBCAM_IVG_PLAN_MAINVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
			// 不可刪除已回報之計畫
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT (IVG_PLAN_SEQ) AS COUNT FROM TBCAM_IVG_PLAN_CONTENT ");
			sql.append("WHERE IVG_PLAN_SEQ = :seq AND RES_FLAG = 'Y' ");
			condition.setObject("seq", inputVO.getSeq());
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			BigDecimal resultCnt = (BigDecimal) list.get(0).get("COUNT");
			if (resultCnt.intValue() > 0)
				throw new APException("ehl_01_cus130_004");

			// remove garbage
			if (!StringUtils.isBlank(vo.getDOC_ID())) {
				TBSYS_FILE_MAINVO fgvo = new TBSYS_FILE_MAINVO();
				fgvo = (TBSYS_FILE_MAINVO) dam.findByPKey(TBSYS_FILE_MAINVO.TABLE_UID, vo.getDOC_ID());
				if (fgvo != null) {
					dam.delete(fgvo);
				}
				TBSYS_FILE_DETAILVO dgvo = new TBSYS_FILE_DETAILVO();
				dgvo = (TBSYS_FILE_DETAILVO) dam.findByPKey(TBSYS_FILE_DETAILVO.TABLE_UID, vo.getDOC_ID());
				if (dgvo != null) {
					dam.delete(dgvo);
				}
			}
			//
			dam.delete(vo);

			// TBCAM_IVG_PLAN_MAIN_ORG
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("delete TBCAM_IVG_PLAN_MAIN_ORG where IVG_PLAN_SEQ = :seq");
			condition.setObject("seq", inputVO.getSeq());
			dam.exeUpdate(condition);

			// TBCAM_IVG_PLAN_MAIN_MEMO
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("delete TBCAM_IVG_PLAN_MAIN_MEMO where IVG_PLAN_SEQ = :seq");
			condition.setObject("seq", inputVO.getSeq());
			dam.exeUpdate(condition);

			// TBCAM_IVG_PLAN_CONTENT
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("delete TBCAM_IVG_PLAN_CONTENT where IVG_PLAN_SEQ = :seq");
			condition.setObject("seq", inputVO.getSeq());
			dam.exeUpdate(condition);
			// 2016/12/08 TBCRM_WKPG_MD_MAST
			//			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//			condition.setQueryString("delete TBCRM_WKPG_MD_MAST where PASS_PARAMS = :params ");
			//			condition.setObject("params", "TBCAM_IVG_PLAN_MAIN:"+inputVO.getSeq());
			//			dam.exeUpdate(condition);

			// TBCAM_IVG_PLAN_FIELD
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("delete TBCAM_IVG_PLAN_FIELD where IVG_PLAN_SEQ = :seq");
			condition.setObject("seq", inputVO.getSeq());
			dam.exeUpdate(condition);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}

	public void getCONTENT(Object body, IPrimitiveMap header) throws JBranchException {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		// 點”檢視”後所查看的畫面,請移除紅框會顯示所有人員的畫面
		//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//		StringBuffer sql = new StringBuffer();
		//		sql.append("select a.BRANCH_ID,a.EMP_ID,b.EMP_NAME from TBCAM_IVG_PLAN_CONTENT a ");
		//		sql.append("left join TBORG_MEMBER b on a.EMP_ID = b.EMP_ID where IVG_PLAN_SEQ = :seq order by a.BRANCH_ID ");
		//		queryCondition.setObject("seq", inputVO.getSeq());
		//		queryCondition.setQueryString(sql.toString());
		//		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		//		return_VO.setResultList(list);

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select EMP_ID, MEMO_DESC from TBCAM_IVG_PLAN_MAIN_MEMO where IVG_PLAN_SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		return_VO.setResultList(dam.exeQuery(queryCondition));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select ROLE_ORG from TBCAM_IVG_PLAN_MAIN where IVG_PLAN_SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		return_VO.setResultList2(dam.exeQuery(queryCondition));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select * from TBCAM_IVG_PLAN_MAIN_ORG where IVG_PLAN_SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		return_VO.setResultList3(dam.exeQuery(queryCondition));

		this.sendRtnObject(return_VO);
	}

	public void getFIELD(Object body, IPrimitiveMap header) throws JBranchException {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select DISTINCT FIELD_LABEL,FIELD_TYPE,DROPDOWN_CONTENT,FIELD_RANK,DISPLAYONLY from TBCAM_IVG_PLAN_FIELD where IVG_PLAN_SEQ = :seq order by FIELD_RANK ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);

		// 2018/2/22
		Boolean canAddRoleFlag = false;
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select * from TBCAM_IVG_PLAN_MAIN_ORG where IVG_PLAN_SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> org_list = dam.exeQuery(queryCondition);
		if (org_list.size() > 0)
			canAddRoleFlag = true;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select * from TBCAM_IVG_PLAN_MAIN_MEMO where IVG_PLAN_SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> org_list2 = dam.exeQuery(queryCondition);
		if (org_list2.size() > 0)
			canAddRoleFlag = false;

		return_VO.setCanAddRoleFlag(canAddRoleFlag);
		
		this.sendRtnObject(return_VO);
	}

	public void readEMP(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		Set<String> res = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getCustFileName());
		if (!dataCsv.isEmpty()) {
			for (int i = 0; i < dataCsv.size(); i++) {
				if (i == 0) {
					continue;
				}
				String[] str = dataCsv.get(i);
				res.add(StringUtils.trim(str[0]));
			}
		}
		
		return_VO.setResultList(new ArrayList(res));
		
		this.sendRtnObject(return_VO);
	}

	public void readCUST(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		Set<Map<String, Object>> res = new HashSet<Map<String, Object>>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getCustFileName());
		if (!dataCsv.isEmpty()) {
			for (int i = 0; i < dataCsv.size(); i++) {
				if (i == 0) {
					continue;
				}
				String[] str = dataCsv.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("CUST_ID", StringUtils.trim(str[0])); // 客戶ID
				map.put("DESC", StringUtils.trim(str[1])); // 備註內容
				res.add(map);
			}
		}
		
		return_VO.setResultList(new ArrayList(res));
		
		this.sendRtnObject(return_VO);
	}
	

	// 不想共用一個
	public void readTYPE1CUST(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		Set<Map<String, Object>> res = new HashSet<Map<String, Object>>();
		Set<String> error = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getCustFileName());
		if (!dataCsv.isEmpty()) {
			for (int i = 0; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				if (i == 0) {
					try {
						if (!"員工編號".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if (!"備註內容（限 200 字）".equals(str[1].trim()))
							throw new Exception(str[1]);
					} catch (Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				Map<String, Object> map = new HashMap<String, Object>();

				String emp_id = StringUtils.leftPad(StringUtils.trim(str[0]), 6, "0");
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("select M.EMP_ID, M.EMP_NAME, D.DEPT_ID AS BRANCH_NBR from TBORG_MEMBER M, TBORG_DEFN D where M.DEPT_ID = D.DEPT_ID AND D.ORG_TYPE = 50 AND EMP_ID = :emp_id AND M.SERVICE_FLAG = 'A' AND M.CHANGE_FLAG IN ('A', 'M', 'P')");
				queryCondition.setObject("emp_id", emp_id);
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0)
					error.add(emp_id);
				else {
					map.put("EMP_ID", emp_id);
					map.put("DESC", StringUtils.trim(str[1]));
					map.put("EMP_NAME", list.get(0).get("EMP_NAME"));
					map.put("BRANCH_NBR", list.get(0).get("BRANCH_NBR"));
					res.add(map);
				}
			}
		}
		
		return_VO.setResultList(new ArrayList(res));
		return_VO.setResultList2(new ArrayList(error));
		
		this.sendRtnObject(return_VO);
	}

	public void download(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = inputVO.getFileName();
		String uuid = UUID.randomUUID().toString();
		
		sql.append("SELECT DOC_FILE FROM TBSYS_FILE_DETAIL where DOC_ID = :id ");
		queryCondition.setObject("id", inputVO.getFileID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		Blob blob = (Blob) list.get(0).get("DOC_FILE");
		int blobLength = (int) blob.length();
		byte[] blobAsBytes = blob.getBytes(1, blobLength);

		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
		fos.write(blobAsBytes);
		fos.close();
		notifyClientToDownloadFile("temp//" + uuid, fileName);
	}

	public void showPDF(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DOC_FILE FROM TBSYS_FILE_DETAIL where DOC_ID = :id ");
		queryCondition.setObject("id", inputVO.getFileID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		Blob blob = (Blob) list.get(0).get("DOC_FILE");
		int blobLength = (int) blob.length();
		byte[] blobAsBytes = blob.getBytes(1, blobLength);

		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
		fos.write(blobAsBytes);
		fos.close();

		return_VO.setFileUrl("temp//" + uuid);
		
		this.sendRtnObject(return_VO);
	}
	

	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		
		switch (inputVO.getSetType()) {
			case "1":
				notifyClientToDownloadFile("doc//CUS//CUS_130_EXAMPLE1.csv", "上傳指定回報員編範例.csv");
				break;
			case "2":
				notifyClientToDownloadFile("doc//CUS//CUS_130_EXAMPLE2.csv", "上傳客戶名單範例.csv");
				break;
			default:
				notifyClientToDownloadFile("doc//CUS//CUS_130_EXAMPLE3.csv", "上傳轄下員編範例.csv");
				break;
		}
		
		this.sendRtnObject(null);
	}
	

	public void getDetail(Object body, IPrimitiveMap header) throws JBranchException {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運區

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("WITH TEMP AS (select * from vworg_dept_br) ");
		sql.append("select DISTINCT a.REGION_CENTER_ID, ");
		sql.append("       (select DEPT_NAME from TEMP where DEPT_ID = a.REGION_CENTER_ID) as REGION_CENTER_NAME, ");
		sql.append("       a.BRANCH_AREA_ID, (select DEPT_NAME from TEMP where DEPT_ID = a.BRANCH_AREA_ID) as BRANCH_AREA_NAME, ");
		sql.append("       a.BRANCH_NBR, ");
		sql.append("       (select DEPT_NAME from TEMP where DEPT_ID = a.BRANCH_NBR) as BRANCH_NAME, ");
		sql.append("       a.EMP_ID, ");
		sql.append("       b.EMP_NAME, ");
		sql.append("       r.ROLE_NAME, ");
		sql.append("       a.RES_FLAG, ");
		sql.append("       a.LASTUPDATE, ");
		sql.append("       a.CREATETIME, ");
		sql.append("       a.IVG_RESULT_SEQ, ");
		sql.append("       c.FIELD_TYPE, ");
		sql.append("       c.FIELD_RANK, ");
		sql.append("       c.FIELD_LABEL, ");
		sql.append("       c.FIELD_VALUE ");
		sql.append("from ( ");
		sql.append("  select con1.* ");
		sql.append("  from TBCAM_IVG_PLAN_CONTENT con1 ");
		sql.append("  where con1.IVG_PLAN_SEQ = :seq ");
		
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("  AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("  AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		} else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("  AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		} else {
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList)) ");
		}
		
		sql.append(") a ");
		sql.append("left join TBORG_MEMBER b on a.EMP_ID = b.EMP_ID ");
		sql.append("left join TBORG_ROLE r on a.ROLE_ID = r.ROLE_ID ");
		sql.append("left join TBCAM_IVG_PLAN_FIELD c on a.IVG_RESULT_SEQ = c.IVG_RESULT_SEQ ");
		sql.append("order by a.IVG_RESULT_SEQ,c.FIELD_RANK ");
		
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		// resize
		List<Map<String, Object>> ans = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			Map<String, Object> obj = new HashMap<String, Object>();
			boolean exist = false;
			for (int k = 0; k < ans.size(); k++) {
				if (ObjectUtils.toString(ans.get(k).get("IVG_RESULT_SEQ")).equals(ObjectUtils.toString(map.get("IVG_RESULT_SEQ")))) {
					exist = true;
					obj = ans.get(k);
					break;
				}
			}
			
			if (!exist) {
				List<Map<String, Object>> subItem = new ArrayList<Map<String, Object>>();
				Map<String, Object> obj2 = new HashMap<String, Object>();
				obj2.put("FIELD_TYPE", map.get("FIELD_TYPE"));
				obj2.put("FIELD_LABEL", map.get("FIELD_LABEL"));
				obj2.put("FIELD_VALUE", map.get("FIELD_VALUE"));
				subItem.add(obj2);
				
				obj.put("REGION_CENTER_ID", map.get("REGION_CENTER_ID"));
				obj.put("REGION_CENTER_NAME", map.get("REGION_CENTER_NAME"));
				obj.put("BRANCH_AREA_ID", map.get("BRANCH_AREA_ID"));
				obj.put("BRANCH_AREA_NAME", map.get("BRANCH_AREA_NAME"));
				obj.put("BRANCH_NBR", map.get("BRANCH_NBR"));
				obj.put("BRANCH_NAME", map.get("BRANCH_NAME"));
				obj.put("EMP_ID", map.get("EMP_ID"));
				obj.put("EMP_NAME", map.get("EMP_NAME"));
				obj.put("ROLE_NAME", map.get("ROLE_NAME"));
				obj.put("RES_FLAG", map.get("RES_FLAG"));
				obj.put("CREATETIME", map.get("CREATETIME"));
				obj.put("LASTUPDATE", map.get("LASTUPDATE"));
				obj.put("IVG_RESULT_SEQ", map.get("IVG_RESULT_SEQ"));
				obj.put("SUBITEM", subItem);
				
				ans.add(obj);
			} else {
				List<Map<String, Object>> subItem = (List<Map<String, Object>>) obj.get("SUBITEM");
				Map<String, Object> obj2 = new HashMap<String, Object>();
				obj2.put("FIELD_TYPE", map.get("FIELD_TYPE"));
				obj2.put("FIELD_LABEL", map.get("FIELD_LABEL"));
				obj2.put("FIELD_VALUE", map.get("FIELD_VALUE"));
				subItem.add(obj2);
				
				obj.put("SUBITEM", subItem);
			}
		}
		
		return_VO.setResultList(ans);
		
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select DISTINCT b.FIELD_LABEL,b.FIELD_RANK ");
		sql.append("from ( ");
		sql.append("  select * ");
		sql.append("  from TBCAM_IVG_PLAN_CONTENT ");
		sql.append("  where IVG_PLAN_SEQ = :seq ");
		sql.append(") a ");
		sql.append("left join TBCAM_IVG_PLAN_FIELD b on a.IVG_RESULT_SEQ = b.IVG_RESULT_SEQ ");
		sql.append("order by b.FIELD_RANK ");
		
		queryCondition.setObject("seq", inputVO.getSeq());
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		
		return_VO.setResultList2(list2);

		this.sendRtnObject(return_VO);
	}
	

	public void getEmpList(Object body, IPrimitiveMap header) throws JBranchException {

		CUS130InputVO inputVO = (CUS130InputVO) body;
		CUS130OutputVO return_VO = new CUS130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT EMP_ID, EMP_NAME, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, BRANCH_NAME, ROLE_ID, ROLE_NAME, AO_CODE ");
		sql.append("FROM ( ");
		sql.append("  SELECT INFO.EMP_ID, INFO.EMP_NAME, INFO.REGION_CENTER_ID, INFO.BRANCH_AREA_ID, INFO.BRANCH_NBR, INFO.BRANCH_NAME, INFO.ROLE_ID, INFO.ROLE_NAME, INFO.CODE_TYPE, INFO.AO_CODE ");
		sql.append("  FROM VWORG_EMP_INFO INFO ");
		sql.append("  WHERE INFO.BRANCH_NBR = :bra_nbr ");
		sql.append("  AND INFO.PRIVILEGEID IN ('001', '002', '003', '004') ");
		sql.append("  AND NVL(INFO.CODE_TYPE, '1') = '1' ");
		sql.append("  UNION ");
		sql.append("  SELECT INFO.EMP_ID, MEM.EMP_NAME, DEFN.REGION_CENTER_ID, DEFN.BRANCH_AREA_ID, DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, MR.ROLE_ID, MR.ROLE_NAME, NULL AS CODE_TYPE, NULL AS AO_CODE ");
		sql.append("  FROM TBORG_PAO INFO ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
		sql.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON INFO.BRANCH_NBR = DEFN.BRANCH_NBR ");
		sql.append("  INNER JOIN ( ");
		sql.append("    SELECT MR.EMP_ID, MR.ROLE_ID, R.ROLE_NAME ");
		sql.append("    FROM TBORG_MEMBER_ROLE MR ");
		sql.append("    INNER JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID AND R.JOB_TITLE_NAME IS NOT NULL ");
		sql.append("    INNER JOIN TBSYSSECUROLPRIASS PRI ON R.ROLE_ID = PRI.ROLEID AND PRI.PRIVILEGEID = '004AO' ");
		sql.append("  ) MR ON INFO.EMP_ID = MR.EMP_ID ");
		sql.append("  WHERE DEFN.BRANCH_NBR = :bra_nbr ");
		sql.append(") ");
		sql.append("ORDER BY ROLE_NAME, EMP_ID ");

		queryCondition.setObject("bra_nbr", inputVO.getBranch());
		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(return_VO);
	}
	

	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		CUS130InputVO inputVO = (CUS130InputVO) body;
		XmlInfo xmlInfo = new XmlInfo();

		// gen csv
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		String fileName = "回報結果_" + sdf.format(new Date()) + ".csv";
		
		List listCSV = new ArrayList();
		for (Map<String, Object> map : inputVO.getListBase()) {
			// MAX 30
			String[] records = new String[30];
			int i = 0;
			records[i] = checkIsNull(map, "ROW_NO"); // 序號
			records[++i] = xmlInfo.getVariable("COMMON.YES_NO", checkIsNull(map, "RES_FLAG"), FormatHelper.FORMAT_3); // 是否已回報
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區
			records[++i] = StringUtils.isNotBlank(ObjectUtils.toString(map.get("BRANCH_NBR"))) ? map.get("BRANCH_NBR") + "-" + map.get("BRANCH_NAME") : ""; // 分行
			records[++i] = "=\"" + checkIsNull(map, "EMP_ID") + "-" + checkIsNull(map, "EMP_NAME") + "\""; // 回報人員
			records[++i] = checkIsNull(map, "ROLE_NAME"); // 角色
			records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\""; // 最後回報日期
			records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\""; // 最後修改日期
			
			// 動態
			List<Map<String, Object>> subItem = (List<Map<String, Object>>) map.get("SUBITEM");
			for (Map<String, Object> map2 : subItem) {
				if ("4".equals(map2.get("FIELD_TYPE")) && map2.get("FIELD_VALUE") != null)
					records[++i] = "=\"" + map2.get("FIELD_VALUE").toString() + "\"";
				else
					records[++i] = checkIsNull(map2, "FIELD_VALUE");
			}
			
			listCSV.add(records);
		}
		
		// header
		String[] csvHeader = new String[30];
		int j = 0;
		csvHeader[j] = "序號";
		csvHeader[++j] = "是否已回報";
		csvHeader[++j] = "區域中心";
		csvHeader[++j] = "營運區";
		csvHeader[++j] = "分行";
		csvHeader[++j] = "回報人員";
		csvHeader[++j] = "角色";
		csvHeader[++j] = "最後回報日期";
		csvHeader[++j] = "最後修改日期";
		
		// 動態
		for (Map<String, Object> map : inputVO.getCustList()) {
			csvHeader[++j] = ObjectUtils.toString(map.get("FIELD_LABEL"));
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		
		notifyClientToDownloadFile(url, fileName);
		
		this.sendRtnObject(null);
	}
	

	private String checkIsNull(Map map, String key) {
		
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

}
