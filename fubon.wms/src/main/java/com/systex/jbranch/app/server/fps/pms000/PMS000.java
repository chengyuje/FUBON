package com.systex.jbranch.app.server.fps.pms000;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :共用版本<br>
 * Comments Name : PMS000.java<br>
 * Author : KevinHsu<br>
 * Date :2016年12月29日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2016年12月29日<br>
 */

@Component("pms000")
@Scope("prototype")
public class PMS000 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS000.class);

	/** ==年月=>取得可視範圍== by Frank **/
	public void getOrgM(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT V_REGION_CENTER_ID, V_REGION_CENTER_NAME,V_ORG_TYPE, ");
		sql.append("V_BRANCH_AREA_ID, V_BRANCH_AREA_NAME, ");
		sql.append("V_BRANCH_NBR, V_BRANCH_NAME, ");
		sql.append("V_AO_CODE, V_EMP_ID, V_EMP_NAME ");
		sql.append("FROM table( ");
		sql.append("FC_GET_VRR( ");
		sql.append(":purview_type, null, ");
		sql.append(":e_dt, :emp_id, ");
		sql.append("null, null, null, null) ");
		sql.append(") ");
		condition.setQueryString(sql.toString());
		condition.setObject("purview_type", "OTHER"); // 非業績報表

		if (inputVO.geteCreDate() != null) {
			condition.setObject("e_dt", inputVO.geteCreDate());
		} else
			condition.setObject("e_dt", stamp);

		condition.setObject("emp_id", loginID);

		outputVO.setOrgList(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}

	public void getAllOrg(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT REGION_CENTER_ID AS REGION_ID, REGION_CENTER_NAME AS REGION_NAME FROM vworg_defn_info ORDER BY REGION_CENTER_ID ");
		condition.setQueryString(sql.toString());
		outputVO.setRegionList(dam.exeQuery(condition));

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT DISTINCT BRANCH_AREA_ID AS AREA_ID, BRANCH_AREA_NAME AS AREA_NAME, REGION_CENTER_ID AS REGION_ID, REGION_CENTER_NAME AS REGION_NAME FROM vworg_defn_info ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID");
		condition.setQueryString(sql.toString());
		outputVO.setRegion_areaList(dam.exeQuery(condition));

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT DISTINCT BRANCH_AREA_ID AS AREA_ID, BRANCH_AREA_NAME AS AREA_NAME FROM vworg_defn_info ORDER BY BRANCH_AREA_ID ");
		condition.setQueryString(sql.toString());
		outputVO.setAreaList(dam.exeQuery(condition));

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT DISTINCT BRANCH_NBR, BRANCH_NAME, BRANCH_AREA_ID AS AREA_ID, BRANCH_AREA_NAME AS AREA_NAME FROM vworg_defn_info ORDER BY BRANCH_AREA_ID");
		condition.setQueryString(sql.toString());
		outputVO.setArea_branchList(dam.exeQuery(condition));

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT DISTINCT BRANCH_NBR, BRANCH_NAME FROM vworg_defn_info ORDER BY BRANCH_NBR");
		condition.setQueryString(sql.toString());
		outputVO.setBranchList(dam.exeQuery(condition));

		sendRtnObject(outputVO);
	}

	/** ==年月=>取得可視範圍== by Frank **/
	public void getOrgD(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		/* old version
		sql.append("SELECT * FROM table( ");
		sql.append("FN_getVISUAL_RANGE( ");
		sql.append(":purview_type, null, :e_dt, :emp_id, ");
		sql.append(":org_id, :v_ao_flag, :v_emp_id, NULL) ");
		sql.append(") ");
		*/
		//new version
		sql.append("SELECT V_REGION_CENTER_ID, V_REGION_CENTER_NAME,V_ORG_TYPE, ");
		sql.append("V_BRANCH_AREA_ID, V_BRANCH_AREA_NAME, ");
		sql.append("V_BRANCH_NBR, V_BRANCH_NAME, ");
		sql.append("V_AO_CODE, V_EMP_ID, V_EMP_NAME ");
		sql.append("FROM TABLE ( ");
		sql.append("FC_GET_VRR( ");
		sql.append(":purview_type, null, :e_dt, :emp_id, ");
		sql.append("null, null, null, null) ");
		sql.append(") ");

		condition.setQueryString(sql.toString());

		condition.setObject("purview_type", "OTHER"); //非業績報表

		if (StringUtils.isNotBlank(inputVO.getDataMonth())) {
			Date lastDate = getMonthLastDate(inputVO.getDataMonth());
			condition.setObject("e_dt", lastDate);
		} else
			condition.setObject("e_dt", stamp);

		condition.setObject("emp_id", loginID);
		outputVO.setOrgList(dam.exeQuery(condition));

		sendRtnObject(outputVO);
	}

	/** ==年月=>可視範圍== by KevinHsu 2017/01/26 **/
	public void getOrg(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String loginRole = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

//		// 20200602 ADD BY OCEAN : 0000166: WMS-CR-20200226-01_高端業管功能改採兼任分行角色調整相關功能_登入底層+行銷模組
//		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0 || 
//			StringUtils.equals("uhrm", StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)))) { 
			// 非任何uhrm相關人員 或 為031兼職FC

			String dbFunction = "";
			if (StringUtils.equals(inputVO.getEmpHistFlag(), "Y")) {
				dbFunction = "FN_GET_VRR_R2";
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				inputVO.setReportDate(sdf.format(cal.getTime())); //前端不上傳
			} else {
				dbFunction = "FN_GET_VRR_D";
			}

			/** 參數2=日期時間 參數3=登入者 參數4=PRID 參數5 TYPE **/
			/*
			 *TYPE='1' 業務處
			 TYPE='2' 營運區
			 TYPE='3' 分行 
			 TYPE='4' AO 
			 */

			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date report_date = null;
			if (StringUtils.length(inputVO.getReportDate()) == 6) {
				report_date = getMonthLastDate(inputVO.getReportDate());
			} else {
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
				report_date = sdf2.parse(inputVO.getReportDate());
			}
			String yyyy = (report_date.getYear() + 1900) + "";
			String mm = (report_date.getMonth() + 1) < 10 ? "0" + (report_date.getMonth() + 1) : (report_date.getMonth() + 1) + "";
			String dd = report_date.getDate() < 10 ? "0" + report_date.getDate() : report_date.getDate() + "";
			//String report_date_str = yyyy+"-"+mm+"-"+dd+"00:00:00";
			String report_date_str = yyyy + mm + dd;

			//20170713 君榮討論報表都不能查過去組織的資料, 只能看現行組織
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
			Date current = new Date();
			report_date_str = sdFormat.format(current);

			String aoFlag = inputVO.getAoFlag() == null ? "Y" : inputVO.getAoFlag();
			String psFlag = inputVO.getPsFlag() == null ? "N" : inputVO.getPsFlag();
			//業務處
			sql.append("select V_STRING1 AS REGION_ID,V_STRING2 AS RIGION_NAME from ");
			sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '1')) ");
			sql.append("ORDER BY V_STRING1");
			condition.setQueryString(sql.toString());
			condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
			condition.setObject("report_date", report_date_str);
			condition.setObject("emp_id", loginID);
			condition.setObject("roleID", loginRole);
			condition.setObject("aoFlag", aoFlag);
			condition.setObject("psFlag", psFlag);
			outputVO.setRegionList(dam.exeQueryWithoutSort(condition));

			//營運區
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select DISTINCT V_STRING3 AS AREA_ID,V_STRING4 AS AREA_NAME from ");
			sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '2')) ");
			sql.append("WHERE V_STRING3 IN (:areaList) ");
			sql.append("ORDER BY V_STRING3");
			condition.setQueryString(sql.toString());
			condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
			condition.setObject("report_date", report_date_str);
			condition.setObject("emp_id", loginID);
			condition.setObject("roleID", loginRole);
			condition.setObject("aoFlag", aoFlag);
			condition.setObject("psFlag", psFlag);
			condition.setObject("areaList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			outputVO.setAreaList(dam.exeQueryWithoutSort(condition));

			//業務處-營運區
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select V_STRING1 AS REGION_ID,V_STRING2 AS RIGION_NAME,");
			sql.append("V_STRING3 AS AREA_ID,V_STRING4 AS AREA_NAME from ");
			sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '2')) ");
			sql.append("WHERE V_STRING3 IN (:areaList) ");
			sql.append("ORDER BY V_STRING1,V_STRING3");
			condition.setQueryString(sql.toString());
			condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
			condition.setObject("report_date", report_date_str);
			condition.setObject("emp_id", loginID);
			condition.setObject("roleID", loginRole);
			condition.setObject("aoFlag", aoFlag);
			condition.setObject("psFlag", psFlag);
			condition.setObject("areaList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			outputVO.setRegion_areaList(dam.exeQueryWithoutSort(condition));

			//分行清單
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select DISTINCT V_STRING3 AS BRANCH_NBR,V_STRING4 AS BRANCH_NAME from ");
			sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '3')) ");
			sql.append("WHERE V_STRING3 IN (:branchList) ");
			sql.append("ORDER BY V_STRING3");
			condition.setQueryString(sql.toString());
			condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
			condition.setObject("report_date", report_date_str);
			condition.setObject("emp_id", loginID);
			condition.setObject("roleID", loginRole);
			condition.setObject("aoFlag", aoFlag);
			condition.setObject("psFlag", psFlag);
			condition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			outputVO.setBranchList(dam.exeQueryWithoutSort(condition));

			//營運區-分行
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select V_STRING1 AS AREA_ID,V_STRING2 AS AREA_NAME,");
			sql.append("V_STRING3 AS BRANCH_NBR,V_STRING4 AS BRANCH_NAME from ");
			sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '3')) ");
			sql.append("WHERE V_STRING3 IN (:branchList) ");
			sql.append("ORDER BY V_STRING1,V_STRING3");
			condition.setQueryString(sql.toString());
			condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
			condition.setObject("report_date", report_date_str);
			condition.setObject("emp_id", loginID);
			condition.setObject("roleID", loginRole);
			condition.setObject("aoFlag", aoFlag);
			condition.setObject("psFlag", psFlag);
			condition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			outputVO.setArea_branchList(dam.exeQueryWithoutSort(condition));

			//分行-AO_CODE 清單
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select V_STRING1 AS BRANCH_NBR,V_STRING2 AS BRANCH_NAME,");
			sql.append("V_STRING3 AS EMP_ID,V_STRING4 AS EMP_NAME,V_STRING5 AO_CODE from ");
			//2017/06/27 獎勵金需要 type==7做判斷 不等於 7 全部預設為4 267行註解
			//sql.append("table("+dbFunction+"(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '4')) ");
			//2017/06/27 獎勵金需要 type 7做判斷 269行新增
			sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag,'" + ((StringUtils.equals(inputVO.getPsFlagType(), "7")) ? "7" : "4") + "')) ");
			sql.append("WHERE V_STRING1 IN (:branchList) ");
			sql.append("ORDER BY V_STRING5");
			condition.setQueryString(sql.toString());
			condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
			condition.setObject("report_date", report_date_str);
			condition.setObject("emp_id", loginID);
			condition.setObject("roleID", loginRole);
			condition.setObject("aoFlag", aoFlag);
			condition.setObject("psFlag", psFlag);
			condition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			outputVO.setAoList(dam.exeQueryWithoutSort(condition));

			//分行-EMP人員清單
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select DISTINCT V_STRING1 AS BRANCH_NBR,V_STRING2 AS BRANCH_NAME,");
			sql.append("V_STRING3 AS EMP_ID,V_STRING4 AS EMP_NAME from ");
			//2017/06/27 獎勵金需要 type==7做判斷 不等於 7 全部預設為4 286行註解
			//sql.append("table("+dbFunction+"(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '4')) ");
			//2017/06/27 獎勵金需要 type 7做判斷 288行新增
			sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag,'" + ((StringUtils.equals(inputVO.getPsFlagType(), "7")) ? "7" : "4") + "')) ");
			sql.append("WHERE V_STRING1 IN (:branchList) ");
			sql.append("ORDER BY V_STRING3");
			condition.setQueryString(sql.toString());
			condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
			condition.setObject("report_date", report_date_str);
			condition.setObject("emp_id", loginID);
			condition.setObject("roleID", loginRole);
			condition.setObject("aoFlag", aoFlag);
			condition.setObject("psFlag", psFlag);
			condition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			outputVO.setEmpList(dam.exeQueryWithoutSort(condition));
//		}

		sendRtnObject(outputVO);
	}

	/** FC共用METHOD 2017/02/15 by KevinHsu **/
	public PMS000OutputVO getFC(PMS000InputVO body) throws JBranchException, ParseException {
		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ROLE_ID, ROLE_NAME ");
		sql.append("FROM TBSYSSECUROLPRIASS P ");
		sql.append("LEFT JOIN TBORG_ROLE R ON P.ROLEID = R.ROLE_ID ");
		sql.append("WHERE PRIVILEGEID = '002' ");
		sql.append("UNION ");
		sql.append("SELECT LISTAGG(ROLE_ID, ',') WITHIN GROUP (ORDER BY ROLE_ID) AS ROLE_ID, 'FCH' AS ROLE_NAME ");
		sql.append("FROM TBSYSSECUROLPRIASS P ");
		sql.append("LEFT JOIN TBORG_ROLE R ON P.ROLEID = R.ROLE_ID ");
		sql.append("WHERE PRIVILEGEID = '003' ");
		sql.append("ORDER BY ROLE_NAME ");
		condition.setQueryString(sql.toString());
		outputVO.setFcList(dam.exeQueryWithoutSort(condition));
		return outputVO;
	}

	/**
	 * @author Jacky Wu 給後端Java取資料加上可視範圍用
	 **/
	public PMS000OutputVO getOrg(PMS000InputVO body) throws JBranchException, ParseException {
		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		String loginID = (String) getCommonVariable(FubonSystemVariableConsts.LOGINID);
		String loginRole = (String) getCommonVariable(FubonSystemVariableConsts.LOGINROLE);
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		String dbFunction = "";
		if (StringUtils.equals(inputVO.getEmpHistFlag(), "Y")) {
			dbFunction = "FN_GET_VRR_R2";
		} else {
			dbFunction = "FN_GET_VRR_D";
		}

		/** 參數2=日期時間 參數3=登入者 參數4=PRID 參數5 TYPE **/
		/*
		 *TYPE='1' 業務處
		 TYPE='2' 營運區
		 TYPE='3' 分行 
		 TYPE='4' AO 
		 */

		Date report_date = null;
		if (StringUtils.length(inputVO.getReportDate()) == 6) {
			report_date = getMonthLastDate(inputVO.getReportDate());
		} else {
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
			report_date = sdf2.parse(inputVO.getReportDate());
		}
		String yyyy = (report_date.getYear() + 1900) + "";
		String mm = (report_date.getMonth() + 1) < 10 ? "0" + (report_date.getMonth() + 1) : (report_date.getMonth() + 1) + "";
		String dd = report_date.getDate() < 10 ? "0" + report_date.getDate() : report_date.getDate() + "";
		String report_date_str = yyyy + mm + dd;

		//20170713 君榮討論報表都不能查過去組織的資料, 只能看現行組織
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
		Date current = new Date();
		report_date_str = sdFormat.format(current);

		String aoFlag = inputVO.getAoFlag() == null ? "Y" : inputVO.getAoFlag();
		String psFlag = inputVO.getPsFlag() == null ? "N" : inputVO.getPsFlag();
		//業務處
		sql.append("select V_STRING1 AS REGION_ID,V_STRING2 AS RIGION_NAME from ");
		sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '1')) ");
		sql.append("ORDER BY V_STRING1");
		condition.setQueryString(sql.toString());
		condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
		condition.setObject("report_date", report_date_str);
		condition.setObject("emp_id", loginID);
		condition.setObject("roleID", loginRole);
		condition.setObject("aoFlag", aoFlag);
		condition.setObject("psFlag", psFlag);
		outputVO.setV_regionList(getList(dam.exeQueryWithoutSort(condition), "REGION_ID"));

		//營運區
		sql = new StringBuffer();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("select DISTINCT V_STRING3 AS AREA_ID,V_STRING4 AS AREA_NAME from ");
		sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '2')) ");
		sql.append("ORDER BY V_STRING3");
		condition.setQueryString(sql.toString());
		condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
		condition.setObject("report_date", report_date_str);
		condition.setObject("emp_id", loginID);
		condition.setObject("roleID", loginRole);
		condition.setObject("aoFlag", aoFlag);
		condition.setObject("psFlag", psFlag);
		outputVO.setV_areaList(getList(dam.exeQueryWithoutSort(condition), "AREA_ID"));

		//分行清單
		sql = new StringBuffer();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("select DISTINCT V_STRING3 AS BRANCH_NBR,V_STRING4 AS BRANCH_NAME from ");
		sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '3')) ");
		sql.append("ORDER BY V_STRING3");
		condition.setQueryString(sql.toString());
		condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
		condition.setObject("report_date", report_date_str);
		condition.setObject("emp_id", loginID);
		condition.setObject("roleID", loginRole);
		condition.setObject("aoFlag", aoFlag);
		condition.setObject("psFlag", psFlag);
		outputVO.setV_branchList(getList(dam.exeQueryWithoutSort(condition), "BRANCH_NBR"));

		//分行-人員清單
		sql = new StringBuffer();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("select V_STRING1 AS BRANCH_NBR,V_STRING2 AS BRANCH_NAME,");
		sql.append("V_STRING3 AS EMP_ID,V_STRING4 AS EMP_NAME,V_STRING5 AO_CODE from ");
		//2017/06/27 獎勵金需要 顧增加判斷 pstypeflag=='7' 
		//sql.append("table("+dbFunction+"(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag, '4')) ");
		//2017/06/27 獎勵金需要 type 7做判斷
		sql.append("table(" + dbFunction + "(:purview_type, to_date(:report_date,'YYYYMMDD'), :emp_id, :roleID, :aoFlag, :psFlag,'" + ((StringUtils.equals(inputVO.getPsFlagType(), "7")) ? "7" : "4") + "')) ");

		sql.append("ORDER BY V_STRING5,V_STRING4");
		condition.setQueryString(sql.toString());
		condition.setObject("purview_type", inputVO.getPreviewType() == null ? "" : inputVO.getPreviewType());
		condition.setObject("report_date", report_date_str);
		condition.setObject("emp_id", loginID);
		condition.setObject("roleID", loginRole);
		condition.setObject("aoFlag", aoFlag);
		condition.setObject("psFlag", psFlag);
		List<Map<String, Object>> list = dam.exeQueryWithoutSort(condition);
		outputVO.setV_aoList(getList(list, "AO_CODE"));
		outputVO.setV_empList(getList(list, "EMP_ID"));

		return outputVO;
	}

	/**
	 * ==可視範圍取月份最後一天==
	 * 
	 * @throws ParseException
	 **/
	public static Date getMonthLastDate(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		Date rptDate = df.parse(date);
		cal.setTime(rptDate);
		cal.set(cal.DATE, cal.getActualMaximum(cal.DATE));
		return cal.getTime();
	}

	private List<String> getList(List<Map<String, Object>> dataList, String datakey) {
		List<String> list = new ArrayList<String>();
		for (Map<String, Object> map : dataList) {
			list.add(map.get(datakey) == null ? "" : map.get(datakey).toString());
		}
		return list;
	}

	public void getLastYMlist(Object body, IPrimitiveMap header) {
		PMS000OutputVO outputVO = new PMS000OutputVO();
		outputVO.setYmList(this.getLastYMlist());
		sendRtnObject(outputVO);
	}

	public void getYMlist(Object body, IPrimitiveMap header) {
		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();
		outputVO.setYmList(this.getYMlist(inputVO.getMonth()));
		sendRtnObject(outputVO);
	}

	public static List<Map<String, Object>> getLastYMlist() {
		List<Map<String, Object>> ymList = new ArrayList<Map<String, Object>>();

		PMS000OutputVO outputVO = new PMS000OutputVO();

		Calendar cal = Calendar.getInstance();

		for (int i = 1; i <= 12; i++) {
			cal.setTime(new Date());
			// cal.add(Calendar.MONTH, -1*i);
			cal.add(Calendar.MONTH, -i); //修正此-i 取上個月以後的日期
			HashMap<String, Object> addMap = new HashMap<String, Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
			addMap.put("LABEL", sdf.format(cal.getTime()));
			addMap.put("DATA", sdf2.format(cal.getTime()));
			ymList.add(addMap);
		}
		return ymList;
	}

	// 取得系統日往前多少月之年月
	public static List<Map<String, Object>> getYMlist(int month) {
		List<Map<String, Object>> ymList = new ArrayList<Map<String, Object>>();

		PMS000OutputVO outputVO = new PMS000OutputVO();

		Calendar cal = Calendar.getInstance();

		for (int i = 0; i < month; i++) {
			Date lastMonth = new Date();
			lastMonth.setMonth(new Date().getMonth() - 1);
			cal.setTime(lastMonth);
			cal.add(Calendar.MONTH, -1 * i);
			HashMap<String, Object> addMap = new HashMap<String, Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
			addMap.put("LABEL", sdf.format(cal.getTime()));
			addMap.put("DATA", sdf2.format(cal.getTime()));
			ymList.add(addMap);
		}
		return ymList;
	}

	public void getSalesPlanYMlist(Object body, IPrimitiveMap header) {
		PMS000OutputVO outputVO = new PMS000OutputVO();
		outputVO.setYmList(this.getSalesPlanYMlist());

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		outputVO.setCurrentYM(sdf.format(cal.getTime()));

		sendRtnObject(outputVO);
	}

	public static List<Map<String, Object>> getSalesPlanYMlist() {
		List<Map<String, Object>> ymList = new ArrayList<Map<String, Object>>();

		Calendar cal = Calendar.getInstance();
		//#001958 : 應該從當月開始往舊的月份排
		//for(int i=6;i>=1;i--){
		// cal.setTime(new Date());
		// cal.add(Calendar.MONTH, 1*i);
		// HashMap<String, Object> addMap = new HashMap<String, Object>();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
		// SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
		// addMap.put("LABEL",sdf.format(cal.getTime()));
		// addMap.put("DATA",sdf2.format(cal.getTime()));
		// ymList.add(addMap);
		//}

		for (int i = -5; i < 12; i++) {
			cal.setTime(new Date());
			cal.add(Calendar.MONTH, -1 * i);
			HashMap<String, Object> addMap = new HashMap<String, Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
			addMap.put("LABEL", sdf.format(cal.getTime()));
			addMap.put("DATA", sdf2.format(cal.getTime()));
			ymList.add(addMap);
		}
		return ymList;
	}

	/**
	 * 客戶首頁業績達成狀況Java端程式
	 * 
	 * @param body
	 * @param header
	 */
	public void getRevAchieve(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS000OutputVO outputVO = new PMS000OutputVO();

		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2); //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //業務處主管
		Map<String, String> faiaMap = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2); //FAIA

		DataAccessManager dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		//判斷是否為消金人員
		if (StringUtils.equals(roleID, "A197")) {
			//MTD RANK_DATA =信貸.信用卡排名, RANK_DATA2 = 房貸排名, RANK_DATA3 = 信用卡排名

			sql = new StringBuffer();
			sql.append("WITH MAX_DATE(DATA_DATE) AS ");
			sql.append("(SELECT MAX(DATA_DATE) FROM TBPMS_CREDIT_PS_MTD), ");
			sql.append("MAX_DATE2(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_MRTG_PS_MTD), ");
			sql.append("MAX_DATE3(YEAR) AS (SELECT MAX(SUBSTRB(YEARMON,1,4)) FROM TBPMS_LOAN_BONUS_UPLOAD), ");
			//----------------------------------好運貸、信貸---------------------------------------
			sql.append("RANK_DATA(EMP_ID,CREDIT_LOAN_RANK,INS_LOAN_RANK,CT_MTD_RATE,E_MTD_RATE) AS ");
			sql.append("(SELECT EMP_ID, RANK() over (order by CT_MTD_RATE desc) as CREDIT_LOAN_RANK ");
			sql.append(", RANK() over (order by E_MTD_RATE desc) as INS_LOAN_RANK ");
			sql.append(",CT_MTD_RATE ,E_MTD_RATE ");
			sql.append("FROM TBPMS_CREDIT_PS_MTD DA ,MAX_DATE WHERE DA.DATA_DATE = MAX_DATE.DATA_DATE), ");
			//-------------------------------------房貸-----------------------------------------
			sql.append("RANK_DATA2(EMP_ID,HOUSE_LOAN_RANK,MRTG_MTD_RATE) AS ");
			sql.append("(SELECT EMP_ID, RANK() over (order by MRTG_MTD_RATE desc) as HOUSE_LOAN_RANK, ");
			sql.append("MRTG_MTD_RATE ");
			sql.append("FROM TBPMS_MRTG_PS_MTD DA,MAX_DATE2 WHERE DA.DATA_DATE = MAX_DATE2.DATA_DATE) ");
			//---------------------------------信用卡(無用)------------------------------------
			sql.append(",RANK_DATA3(EMP_ID,CARD_RANK) AS ");
			sql.append("(SELECT EMP_ID, RANK() over (order by SUM(CNT) desc) as CARD_RANK ");
			sql.append("FROM TBPMS_LOAN_BONUS_UPLOAD DA, MAX_DATE3 WHERE SUBSTRB(DA.YEARMON,1,4) = MAX_DATE3.YEAR GROUP BY EMP_ID) ");
			sql2 = new StringBuffer();
			sql2.append("SELECT ");
			sql2.append("RANK_DATA.EMP_ID, ");
			sql2.append("RANK_DATA.CREDIT_LOAN_RANK, ");
			sql2.append("RANK_DATA.CT_MTD_RATE, ");
			sql2.append("RANK_DATA.INS_LOAN_RANK, ");
			sql2.append("RANK_DATA.E_MTD_RATE, ");
			sql2.append("RANK_DATA2.HOUSE_LOAN_RANK, ");
			sql2.append("RANK_DATA2.MRTG_MTD_RATE, ");
			sql2.append("RANK_DATA3.CARD_RANK ");
			sql2.append("FROM RANK_DATA FULL OUTER JOIN RANK_DATA2 ON RANK_DATA.EMP_ID = RANK_DATA2.EMP_ID ");
			sql2.append("FULL OUTER JOIN RANK_DATA3 ON RANK_DATA.EMP_ID = RANK_DATA3.EMP_ID ");
			sql2.append("WHERE RANK_DATA.EMP_ID = :empID ");
			condition.setObject("empID", loginID);
			condition.setQueryString(sql.toString() + sql2.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);

			sql2 = new StringBuffer();
			sql2.append("SELECT COUNT(RANK_DATA.CREDIT_LOAN_RANK) AS CREDIT_LOAN_TOTAL ");
			sql2.append(",COUNT(RANK_DATA.INS_LOAN_RANK) AS INS_LOAN_TOTAL ");
			sql2.append(",COUNT(RANK_DATA2.HOUSE_LOAN_RANK) AS HOUSE_LOAN_TOTAL ");
			sql2.append(",COUNT(RANK_DATA3.CARD_RANK) AS CARD_TOTAL ");
			sql2.append("FROM RANK_DATA FULL OUTER JOIN RANK_DATA2 ON RANK_DATA.EMP_ID = RANK_DATA2.EMP_ID ");
			sql2.append("FULL OUTER JOIN RANK_DATA3 ON RANK_DATA.EMP_ID = RANK_DATA3.EMP_ID ");

			condition2.setQueryString(sql.toString() + sql2.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(condition2);

			List<Map<String, Object>> mTDList = new ArrayList<Map<String, Object>>();

			for (Map<String, Object> map : list) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				//房貸排名
				dataMap.put("TYPE1", "01");
				dataMap.put("RANK1", getString(map.get("HOUSE_LOAN_RANK")));
				dataMap.put("TOTAL1", getString(list2.get(0).get("HOUSE_LOAN_TOTAL")));
				dataMap.put("RATE1", getString(map.get("MRTG_MTD_RATE")));
				//信貸排名
				dataMap.put("TYPE2", "02");
				dataMap.put("RANK2", getString(map.get("CREDIT_LOAN_RANK")));
				dataMap.put("TOTAL2", getString(list2.get(0).get("CREDIT_LOAN_TOTAL")));
				dataMap.put("RATE2", getString(map.get("CT_MTD_RATE")));
				//好運貸排名
				dataMap.put("TYPE3", "03");
				dataMap.put("RANK3", getString(map.get("INS_LOAN_RANK")));
				dataMap.put("TOTAL3", getString(list2.get(0).get("INS_LOAN_TOTAL")));
				dataMap.put("RATE3", getString(map.get("E_MTD_RATE")));
				//信用卡排名
				dataMap.put("TYPE4", "04");
				dataMap.put("RANK4", getString(map.get("CARD_RANK")));
				dataMap.put("TOTAL4", getString(list2.get(0).get("CARD_TOTAL")));
				dataMap.put("RATE4", "");
				mTDList.add(dataMap);
			}

			//YTD RANK_DATA =信貸.信用卡排名, RANK_DATA2 = 房貸排名, RANK_DATA3 = 信用卡排名

			sql = new StringBuffer();
			sql.append("WITH MAX_DATE(DATA_DATE) AS ");
			sql.append("(SELECT MAX(DATA_DATE) FROM TBPMS_CREDIT_PS_YTD), ");
			sql.append("MAX_DATE2(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_MRTG_PS_YTD), ");
			sql.append("MAX_DATE3(YEAR) AS (SELECT MAX(SUBSTRB(YEARMON,1,4)) FROM TBPMS_LOAN_BONUS_UPLOAD), ");
			//----------------------------------好運貸、信貸---------------------------------------
			sql.append("RANK_DATA(EMP_ID,CREDIT_LOAN_RANK,INS_LOAN_RANK,CT_YTD_RATE,E_YTD_RATE) AS ");
			sql.append("(SELECT EMP_ID, RANK() over (order by CT_YTD_RATE desc) as CREDIT_LOAN_RANK ");
			sql.append(", RANK() over (order by E_YTD_RATE desc) as INS_LOAN_RANK ");
			sql.append(",CT_YTD_RATE ,E_YTD_RATE ");
			sql.append("FROM TBPMS_CREDIT_PS_YTD DA ,MAX_DATE WHERE DA.DATA_DATE = MAX_DATE.DATA_DATE), ");
			//-------------------------------------房貸-----------------------------------------
			sql.append("RANK_DATA2(EMP_ID,HOUSE_LOAN_RANK,MRTG_YTD_RATE) AS ");
			sql.append("(SELECT EMP_ID, RANK() over (order by MRTG_YTD_RATE desc) as HOUSE_LOAN_RANK, ");
			sql.append("MRTG_YTD_RATE ");
			sql.append("FROM TBPMS_MRTG_PS_YTD DA,MAX_DATE2 WHERE DA.DATA_DATE = MAX_DATE2.DATA_DATE) ");
			//---------------------------------信用卡(無用)------------------------------------
			sql.append(",RANK_DATA3(EMP_ID,CARD_RANK) AS ");
			sql.append("(SELECT EMP_ID, RANK() over (order by SUM(CNT) desc) as CARD_RANK ");
			sql.append("FROM TBPMS_LOAN_BONUS_UPLOAD DA, MAX_DATE3 WHERE SUBSTRB(DA.YEARMON,1,4) = MAX_DATE3.YEAR GROUP BY EMP_ID) ");
			sql2 = new StringBuffer();
			sql2.append("SELECT ");
			sql2.append("RANK_DATA.EMP_ID, ");
			sql2.append("RANK_DATA.CREDIT_LOAN_RANK, ");
			sql2.append("RANK_DATA.CT_YTD_RATE, ");
			sql2.append("RANK_DATA.INS_LOAN_RANK, ");
			sql2.append("RANK_DATA.E_YTD_RATE, ");
			sql2.append("RANK_DATA2.HOUSE_LOAN_RANK, ");
			sql2.append("RANK_DATA2.MRTG_YTD_RATE, ");
			sql2.append("RANK_DATA3.CARD_RANK ");
			sql2.append("FROM RANK_DATA FULL OUTER JOIN RANK_DATA2 ON RANK_DATA.EMP_ID = RANK_DATA2.EMP_ID ");
			sql2.append("FULL OUTER JOIN RANK_DATA3 ON RANK_DATA.EMP_ID = RANK_DATA3.EMP_ID ");
			sql2.append("WHERE RANK_DATA.EMP_ID = :empID ");
			condition.setObject("empID", loginID);
			condition.setQueryString(sql.toString() + sql2.toString());
			list = new ArrayList<Map<String, Object>>();
			list = dam.exeQuery(condition);

			//PS YTD排行總人數
			sql2 = new StringBuffer();
			sql2.append("SELECT COUNT(RANK_DATA.CREDIT_LOAN_RANK) AS CREDIT_LOAN_TOTAL ");
			sql2.append(",COUNT(RANK_DATA.INS_LOAN_RANK) AS INS_LOAN_TOTAL ");
			sql2.append(",COUNT(RANK_DATA2.HOUSE_LOAN_RANK) AS HOUSE_LOAN_TOTAL ");
			sql2.append(",COUNT(RANK_DATA3.CARD_RANK) AS CARD_TOTAL ");
			sql2.append("FROM RANK_DATA FULL OUTER JOIN RANK_DATA2 ON RANK_DATA.EMP_ID = RANK_DATA2.EMP_ID ");
			sql2.append("FULL OUTER JOIN RANK_DATA3 ON RANK_DATA.EMP_ID = RANK_DATA3.EMP_ID ");

			condition2.setQueryString(sql.toString() + sql2.toString());
			List<Map<String, Object>> list3 = dam.exeQuery(condition2);
			List<Map<String, Object>> yTDList = new ArrayList<Map<String, Object>>();
			//YTD
			for (Map<String, Object> map : list) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				//房貸排名
				dataMap.put("TYPE1", "01");
				dataMap.put("RANK1", getString(map.get("HOUSE_LOAN_RANK")));
				dataMap.put("TOTAL1", getString(list3.get(0).get("HOUSE_LOAN_TOTAL")));
				dataMap.put("RATE1", getString(map.get("MRTG_YTD_RATE")));
				//信貸排名
				dataMap.put("TYPE2", "02");
				dataMap.put("RANK2", getString(map.get("CREDIT_LOAN_RANK")));
				dataMap.put("TOTAL2", getString(list3.get(0).get("CREDIT_LOAN_TOTAL")));
				dataMap.put("RATE2", getString(map.get("CT_YTD_RATE")));
				//好運貸排名
				dataMap.put("TYPE3", "03");
				dataMap.put("RANK3", getString(map.get("INS_LOAN_RANK")));
				dataMap.put("TOTAL3", getString(list3.get(0).get("INS_LOAN_TOTAL")));
				dataMap.put("RATE3", getString(map.get("E_YTD_RATE")));
				//信用卡排名
				dataMap.put("TYPE4", "04");
				dataMap.put("RANK4", getString(map.get("CARD_RANK")));
				dataMap.put("TOTAL4", getString(list3.get(0).get("CARD_TOTAL")));
				dataMap.put("RATE4", "");
				yTDList.add(dataMap);
			}
			outputVO.setMTDList(mTDList);
			outputVO.setYTDList(yTDList);
		} else if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
			//理專排名
			//計算應達成率

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_AO_ACHD_RATE) ");
			sql.append("SELECT TOT_TAR_AMT_1,TOT_A_RATE_1,TOT_A_RATE_2 , ( CASE WHEN TOT_TAR_AMT_2 > 0 OR TOT_TAR_AMT_1 > 0 THEN ROUND(TOT_TAR_AMT_2*100/TOT_TAR_AMT_1,2) ELSE 0 END )AS TOTAL_ACH_RATE_MTD ");
			sql.append(",BK_RANK_LVL,DA.DATA_DATE,DA.JOB_TITLE_ID ");
			sql.append("FROM TBPMS_AO_ACHD_RATE DA INNER JOIN MAX_DATE ON DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql.append("LEFT JOIN TBPMS_AO_ACHD_RANK RK ON DA.DATA_DATE = RK.DATA_DATE AND DA.AO_CODE = RK.AO_CODE AND DA.EMP_ID = RK.EMP_ID AND RK.SUM_TYPE = 'M' ");
			sql.append("WHERE DA.EMP_ID = :empID AND DA.SUM_TYPE = 'M' ");
			condition.setObject("empID", loginID);
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			Map<String, Object> dataMap = new HashMap<String, Object>();

			String dataDate = "", jobTitle = "";
			if (!list.isEmpty()) {
				dataMap.put("MTD_RANK_LVL", getString(list.get(0).get("BK_RANK_LVL"))); //月目標排名
				dataMap.put("MTD_TAR_AMT", getString(list.get(0).get("TOT_TAR_AMT_1"))); //月目標
				//dataMap.put("MTD_TAR_AMT", getString(list.get(0).get("TOTAL_ACH_RATE_MTD"))); //MTD月目標
				dataMap.put("MTD_ACH_RATE", getString(list.get(0).get("TOTAL_ACH_RATE_MTD"))); //MTD已達成率
				dataMap.put("MTD_TAR_RATE", getString(list.get(0).get("TOT_A_RATE_2"))); //MTD應達成率
				dataDate = getString(list.get(0).get("DATA_DATE"));
				jobTitle = getString(list.get(0).get("JOB_TITLE_ID"));
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT * FROM TBPMS_AO_ACHD_RANK_BASE WHERE DATA_DATE = :dataDate and RANK_ORG_ID = 'BK' ");
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			if (!condition.getQueryString().isEmpty()) {
				list = dam.exeQuery(condition);
				if (!list.isEmpty()) {
					dataMap.put("CNT_LVL", getString(list.get(0).get(jobTitle))); //同職級人數
				}
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_AO_DAY_PROFIT_MTD) ");
			sql.append("SELECT SUM(FEE) AS MTD_FEE FROM TBPMS_AO_DAY_PROFIT_MTD DA , MAX_DATE WHERE DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql.append("AND DA.EMP_ID = :empID and ITEM = '92' ");
			condition.setQueryString(sql.toString());
			condition.setObject("empID", loginID);
			list = new ArrayList<Map<String, Object>>();
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("MTD_FEE", list.get(0).get("MTD_FEE")); //MTD月手收
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT * FROM ( ");
			sql.append("SELECT ");
			sql.append("A.EMP_ID ");
			sql.append(",A.TOT_TAR_AMT_1 ");
			sql.append(",A.TOT_TAR_AMT_2 ");
			//sql.append(",CASE WHEN A.TOT_TAR_AMT_2 > 0 THEN ROUND( F.FEE * 100 / A.TOT_TAR_AMT_2 , 2) ELSE 0 END AS TOTAL_RATE_YTD ");
			sql.append(",CASE WHEN A.TOT_TAR_AMT_1 > 0 THEN ROUND( A.TOT_TAR_AMT_2 *100 / A.TOT_TAR_AMT_1 ,2 ) ELSE 0 END AS TOTAL_RATE_YTD ");
			sql.append(",CASE WHEN A.TOT_TAR_AMT_1 > 0 THEN ROUND( F.FEE * 100 / A.TOT_TAR_AMT_1 , 2) ELSE 0 END AS TOT_A_RATE_1 ");
			sql.append(",RANK() OVER (PARTITION BY NULL ORDER BY CASE WHEN A.TOT_TAR_AMT_1 > 0 THEN NVL(ROUND( F.FEE * 100 / A.TOT_TAR_AMT_1 , 2),0) ELSE 0 END DESC) AS BK_RANK_LVL ");
			sql.append(",A.JOB_TITLE_ID ");
			sql.append("FROM TBPMS_AO_ACHD_RATE A ");
			sql.append("LEFT JOIN( ");
			sql.append("select EMP_ID , SUM(FEE) AS FEE from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'YYYY') and TO_DATE(:dataDate,'YYYYMMDD') ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("GROUP BY EMP_ID ");
			sql.append(") F ");
			sql.append("ON F.EMP_ID =A.EMP_ID ");
			sql.append("WHERE A.DATA_DATE=:dataDate AND A.SUM_TYPE='Y' AND A.JOB_TITLE_ID =:jobTitle ");
			sql.append(") WHERE EMP_ID =:empID");
			condition.setObject("dataDate", dataDate);
			condition.setObject("empID", loginID);
			condition.setObject("jobTitle", jobTitle);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("YTD_RANK_LVL", getString(list.get(0).get("BK_RANK_LVL"))); //年目標排名
				dataMap.put("YTD_TAR_AMT", getString(list.get(0).get("TOT_TAR_AMT_1"))); //年目標
				dataMap.put("YTD_TAR_RATE", getString(list.get(0).get("TOTAL_RATE_YTD"))); //應達成率
				dataMap.put("YTD_ACH_RATE", getString(list.get(0).get("TOT_A_RATE_1"))); //已達成率
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append(" SELECT ");
			sql.append("AVG(MTD.FEE) AS MTD_AVG_FEE, ");
			sql.append("AVG(YTD.FEE) AS YTD_AVG_FEE ");
			sql.append(" FROM TBPMS_AO_ACHD_RATE A ");
			sql.append(" LEFT JOIN( ");
			sql.append(" select EMP_ID , SUM(FEE) AS FEE from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append(" where ITEM IN('90','91','09') ");
			sql.append(" AND DATA_DATE IN ");
			sql.append(" (select MAX(DATA_DATE) from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append(" where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'MM') and TO_DATE(:dataDate,'YYYYMMDD') ");
			sql.append(" GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append(" GROUP BY EMP_ID ");
			sql.append(" ) MTD ");
			sql.append(" ON MTD.EMP_ID =A.EMP_ID ");
			sql.append("LEFT JOIN ( ");
			sql.append("select EMP_ID , SUM(FEE) AS FEE from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append(" where ITEM IN('90','91','09') ");
			sql.append(" AND DATA_DATE IN ");
			sql.append(" (select MAX(DATA_DATE) from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append(" where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'YYYY') and TO_DATE(:dataDate,'YYYYMMDD') ");
			sql.append(" GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append(" GROUP BY EMP_ID ");
			sql.append(") YTD ");
			sql.append(" ON YTD.EMP_ID =A.EMP_ID ");
			sql.append(" WHERE A.DATA_DATE=:dataDate AND A.JOB_TITLE_ID=:jobTitle ");
			condition.setObject("dataDate", dataDate);
			condition.setObject("jobTitle", jobTitle);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				for (Map<String, Object> map : list) {
					dataMap.put("MTD_FEE_AVG", getString(map.get("MTD_AVG_FEE"))); //MTD同職級平均手收
					dataMap.put("YTD_FEE_AVG", getString(map.get("YTD_AVG_FEE"))); //YTD同職級平均手收
				}
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT SUM(FEE) AS TD_FEE ");
			sql.append("FROM TBPMS_AO_IMM_PROFIT ");
			sql.append("WHERE TX_DATE = :dataDate AND EMP_ID = :empID ");
			condition.setObject("empID", loginID);
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			if (!condition.getQueryString().isEmpty()) {
				list = dam.exeQuery(condition);
				if (!list.isEmpty()) {
					dataMap.put("TD_FEE", getString(list.get(0).get("TD_FEE"))); //當日速報手收
				}
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select EMP_ID , SUM(FEE) AS YTD_FEE from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'YYYY') and TO_DATE(:dataDate,'YYYYMMDD') ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("AND EMP_ID=:empID GROUP BY EMP_ID ");
			condition.setObject("empID", loginID);
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("YTD_FEE", getString(list.get(0).get("YTD_FEE"))); //YTD月手收
			}

			//計算近一年未達goal月數
			int mon_not_ach = 0;

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("WITH AO_FEE(YEARMON,EMP_ID,FEE) AS ( ");
			sql.append("select SUBSTR(DATA_DATE ,1,6), EMP_ID , SUM(FEE) AS FEE from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_AO_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') >= TRUNC(ADD_MONTHS(SYSDATE,-12)) ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6),EMP_ID ");
			sql.append(") ");
			sql.append("SELECT NVL(TAR.DATA_YEARMON,AO_FEE.YEARMON) AS DATA_YEARMON ,TAR.BRANCH_NBR, TAR.BRANCH_AREA_ID, TAR.BRANCH_AREA_NAME, TAR.BRANCH_NAME,AO_CODE,EMP_NAME,NVL(TAR.EMP_ID,AO_FEE.EMP_ID) AS EMP_ID , NVL(TAR_AMOUNT,0) as GOAL,FEE FROM AO_FEE ");
			sql.append("LEFT JOIN TBPMS_AO_PRD_TAR_M TAR ");
			sql.append("ON TAR.DATA_YEARMON = AO_FEE.YEARMON AND TAR.EMP_ID = AO_FEE.EMP_ID ");
			sql.append("AND (DATA_YEARMON BETWEEN TO_CHAR(ADD_MONTHS(SYSDATE,-12),'YYYY') AND TO_CHAR(SYSDATE,'YYYYMM')) ");
			sql.append("WHERE AO_FEE.EMP_ID = :empID AND NVL(TAR_AMOUNT,0) > FEE ");
			condition.setObject("empID", loginID);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				mon_not_ach = list.size();
			}

			dataMap.put("MON_NOT_ACH", mon_not_ach); //近一年未達標月數
			result.add(dataMap);
			outputVO.setResultList(result);

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT IPO.PRD_ID,PRD.PNAME,SUM(BAL) AS IPO_AMT FROM TBPMS_IPO_RPT IPO INNER JOIN TBPMS_IPO_PARAM_MAST PARM ON IPO.PRJ_SEQ = PARM.PRJ_SEQ ");
			sql.append("LEFT JOIN VWPRD_MASTER PRD ON IPO.PRD_ID = PRD.PRD_ID ");
			sql.append("WHERE (SYSDATE BETWEEN START_DT AND END_DT) AND IPO.EMP_ID = :empID and DATA_DATE = :dataDate ");
			sql.append("GROUP BY IPO.PRD_ID,PRD.PNAME");
			condition.setObject("empID", loginID);
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				outputVO.setIPOList(list);
			}
		} else if (faiaMap.containsKey(roleID)) {

			/**
			 * Role供判斷輔銷人員、科長用 輔銷科長:檢視所有分行理專 輔銷人員:檢視轄下分行理專
			 */
			String Role = "";
			Role = ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINROLE));

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_AO_ACHD_RATE) ");
			sql.append(",AO_FEE(EMP_ID,INV_FEE,INS_FEE,TOT_FEE) AS (SELECT EMP_ID,SUM(COALESCE(INV,0)) AS INV,SUM(COALESCE(INS,0)) AS INS");
			sql.append(",SUM(COALESCE(TOT,0)) AS TOT FROM TBPMS_AO_DAY_PROFIT_MTD DA INNER JOIN MAX_DATE ON DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql.append("PIVOT( SUM(FEE) FOR ITEM IN ('90' AS INV,'91' AS INS,'92' AS TOT) ) ");
			sql.append("GROUP BY EMP_ID )");
			sql.append("SELECT SUM(TOT_TAR_AMT_2) AS TOT_TAR_AMT_2,SUM(INV_TAR_AMT_2) AS INV_TAR_AMT_2,SUM(INS_TAR_AMT_2) AS INS_TAR_AMT_2, ");
			sql.append("(CASE WHEN SUM(TOT_TAR_AMT_2) > 0 AND SUM(INV_FEE) > 0 THEN ROUND(SUM(TOT_TAR_AMT_2)*100/SUM(TOT_TAR_AMT_1),2) ELSE 0 END) AS MTD_TOT_RATE,");
			sql.append("(CASE WHEN SUM(INV_TAR_AMT_2) > 0 AND SUM(INV_FEE) > 0 THEN ROUND(SUM(INV_TAR_AMT_2)*100/SUM(INV_TAR_AMT_1),2) ELSE 0 END) AS MTD_INV_RATE,");
			sql.append("(CASE WHEN SUM(INS_TAR_AMT_2) > 0 AND SUM(INV_FEE) > 0 THEN ROUND(SUM(INS_TAR_AMT_2)*100/SUM(INS_TAR_AMT_1),2) ELSE 0 END) AS MTD_INS_RATE, ");
			sql.append("SUM(INV_FEE) AS INV_FEE,SUM(INS_FEE) AS INS_FEE,SUM(TOT_FEE) AS TOT_FEE, ");
			sql.append("(CASE WHEN SUM(INV_TAR_AMT_2) > 0 AND SUM(INV_FEE) > 0 THEN ROUND(SUM(INV_FEE)*100/SUM(INV_TAR_AMT_2),2) ELSE 0 END) AS INV_RATE, ");
			sql.append("(CASE WHEN SUM(INS_TAR_AMT_2) > 0 AND SUM(INV_FEE) > 0 THEN ROUND(SUM(INS_FEE)*100/SUM(INS_TAR_AMT_2),2) ELSE 0 END) AS INS_RATE, ");
			sql.append("(CASE WHEN SUM(TOT_TAR_AMT_2) > 0 AND SUM(INV_FEE) > 0 THEN ROUND(SUM(TOT_FEE)*100/SUM(TOT_TAR_AMT_2),2) ELSE 0 END) AS TOT_RATE, ");
			sql.append("SUM(CASE WHEN TOT_TAR_AMT_2 > 0 AND TOT_FEE*100/TOT_TAR_AMT_2 >= 100 THEN 1 ELSE 0 END) AS ACH_TOT100_CNT, ");
			sql.append("SUM(CASE WHEN TOT_TAR_AMT_2 > 0 AND TOT_FEE*100/TOT_TAR_AMT_2 < 100 THEN 1 ELSE 0 END) AS UN_ACH_TOT100_CNT,");
			sql.append("SUM(CASE WHEN INV_TAR_AMT_2 > 0 AND INV_FEE*100/INV_TAR_AMT_2 >= 100 THEN 1 ELSE 0 END) AS ACH_INV100_CNT, ");
			sql.append("SUM(CASE WHEN INV_TAR_AMT_2 > 0 AND INV_FEE*100/INV_TAR_AMT_2 < 100 THEN 1 ELSE 0 END) AS UN_ACH_INV100_CNT,");
			sql.append("SUM(CASE WHEN INS_TAR_AMT_2 > 0 AND INS_FEE*100/INS_TAR_AMT_2 >= 100 THEN 1 ELSE 0 END) AS ACH_INS100_CNT, ");
			sql.append("SUM(CASE WHEN INS_TAR_AMT_2 > 0 AND INS_FEE*100/INS_TAR_AMT_2 < 100 THEN 1 ELSE 0 END) AS UN_ACH_INS100_CNT ");
			sql.append("FROM TBPMS_AO_ACHD_RATE DA INNER JOIN MAX_DATE ON DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql.append("LEFT JOIN AO_FEE ON DA.EMP_ID = AO_FEE.EMP_ID ");
			sql.append("WHERE DA.SUM_TYPE = 'M' ");

			if (Role.equals("FA") || Role.equals("IA")) {
				sql.append("AND DA.BRANCH_NBR IN (:branch_nbr) ");
				condition.setObject("branch_nbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

			Map<String, Object> dataMap = new HashMap<String, Object>();

			if (!list.isEmpty()) {
				dataMap.put("TOT_TAR_AMT", getString(list.get(0).get("TOT_TAR_AMT_2"))); //月目標
				dataMap.put("INV_TAR_AMT", getString(list.get(0).get("INV_TAR_AMT_2"))); //投資月目標
				dataMap.put("INS_TAR_AMT", getString(list.get(0).get("INS_TAR_AMT_2"))); //保險月目標
				dataMap.put("MTD_TOT_RATE", getString(list.get(0).get("MTD_TOT_RATE"))); //MTD月目標應達成率
				dataMap.put("MTD_INV_RATE", getString(list.get(0).get("MTD_INV_RATE"))); //MTD投資月目標應達成率
				dataMap.put("MTD_INS_RATE", getString(list.get(0).get("MTD_INS_RATE"))); //MTD保險月目標應達成率
				dataMap.put("TOT_RATE", getString(list.get(0).get("TOT_RATE"))); //MTD月目標實際達成率
				dataMap.put("INV_RATE", getString(list.get(0).get("INV_RATE"))); //MTD投資月目標實際達成率
				dataMap.put("INS_RATE", getString(list.get(0).get("INS_RATE"))); //MTD保險月目標實際達成率
				dataMap.put("TOT_FEE", getString(list.get(0).get("TOT_FEE"))); //MTD實際手收
				dataMap.put("INV_FEE", getString(list.get(0).get("INV_FEE"))); //MTD投資實際手收
				dataMap.put("INS_FEE", getString(list.get(0).get("INS_FEE"))); //MTD保險實際手收
				dataMap.put("ACH_TOT100_CNT", getString(list.get(0).get("ACH_TOT100_CNT"))); //MTD月目標達成率>= 100人數
				dataMap.put("UN_ACH_TOT100_CNT", getString(list.get(0).get("UN_ACH_TOT100_CNT"))); //MTD月目標達成率< 100人數
				dataMap.put("ACH_INV100_CNT", getString(list.get(0).get("ACH_INV100_CNT"))); //MTD投資月目標達成率>= 100人數
				dataMap.put("UN_ACH_INV100_CNT", getString(list.get(0).get("UN_ACH_INV100_CNT"))); //MTD投資月目標達成率>= 100人數
				dataMap.put("ACH_INS100_CNT", getString(list.get(0).get("ACH_INV100_CNT"))); //MTD保險月目標達成率>= 100人數
				dataMap.put("UN_ACH_INS100_CNT", getString(list.get(0).get("UN_ACH_INV100_CNT"))); //MTD保險月目標達成率>= 100人數
			}
			result.add(dataMap);
			outputVO.setResultList(result);

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT IPO.PRD_ID,PRD.PNAME,SUM(BAL) AS IPO_AMT FROM TBPMS_IPO_RPT IPO INNER JOIN TBPMS_IPO_PARAM_MAST PARM ON IPO.PRJ_SEQ = PARM.PRJ_SEQ ");
			sql.append("LEFT JOIN VWPRD_MASTER PRD ON IPO.PRD_ID = PRD.PRD_ID ");
			sql.append("WHERE (SYSDATE BETWEEN START_DT AND END_DT) and SUBSTR(DATA_DATE,0,6) = TO_CHAR(SYSDATE,'YYYYMM') ");

			if (Role.equals("FA") || Role.equals("IA")) {
				sql.append("AND IPO.BRANCH_NBR IN (:branch_nbr)");
				condition.setObject("branch_nbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}

			sql.append("GROUP BY IPO.PRD_ID,PRD.PNAME");

			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				outputVO.setIPOList(list);
			}
		} else if (isPM(roleID)) { //判斷是否為商品PM
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_BR_DAY_PROFIT_MTD), ");
			sql.append("TAR_AMT(INV_TAR_AMT,INS_TAR_AMT) AS (SELECT SUM(INV_TAR_AMT_2),SUM(INS_TAR_AMT_2) ");
			sql.append("FROM TBPMS_BR_ACHD_RATE DA,MAX_DATE WHERE DA.DATA_DATE = MAX_DATE.DATA_DATE) ");
			sql.append("SELECT ITEM,SUM(COALESCE(BAL,0)) as BAL,SUM(COALESCE(FEE,0)) as FEE");
			sql.append(",SUM(INV_TAR_AMT)/COUNT(1) AS INV_TAR_AMT,SUM(INS_TAR_AMT)/COUNT(1) AS INS_TAR_AMT ");
			sql.append("FROM TBPMS_BR_DAY_PROFIT_MTD DA INNER JOIN MAX_DATE ON DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql.append("CROSS JOIN TAR_AMT ");
			sql.append("WHERE DA.ITEM IN ('01','02','03','04','05','06','07','08','13','17','91') ");
			sql.append("GROUP BY ITEM");

			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			BigDecimal mtd_fund_fee = new BigDecimal(0), mtd_bond_fee = new BigDecimal(0), mtd_etf_fee = new BigDecimal(0), mtd_stock_fee = new BigDecimal(0), mtd_si_fee = new BigDecimal(0), mtd_sn_fee = new BigDecimal(0), mtd_dci_fee = new BigDecimal(0), mtd_ins_fee = new BigDecimal(0);
			BigDecimal mtd_fund_amt = new BigDecimal(0), mtd_bond_amt = new BigDecimal(0), mtd_etf_amt = new BigDecimal(0), mtd_stock_amt = new BigDecimal(0), mtd_si_amt = new BigDecimal(0), mtd_sn_amt = new BigDecimal(0), mtd_dci_amt = new BigDecimal(0), mtd_ins_amt = new BigDecimal(0);
			BigDecimal mtd_inv_fee = new BigDecimal(0), mtd_inv_amt = new BigDecimal(0), mtd_inv_rate = new BigDecimal(0), mtd_ins_rate = new BigDecimal(0);

			if (!list.isEmpty()) {

				for (Map<String, Object> map : list) {
					if (StringUtils.equals("01", getString(map.get("ITEM")))) {
						mtd_fund_fee = mtd_fund_fee.add(new BigDecimal(getString(map.get("FEE"))));
						mtd_fund_amt = mtd_fund_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("05", getString(map.get("ITEM"))) || StringUtils.equals("13", getString(map.get("ITEM")))) {
						mtd_bond_fee = mtd_bond_fee.add(new BigDecimal(getString(map.get("FEE"))));
						mtd_bond_amt = mtd_bond_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("06", getString(map.get("ITEM")))) {
						mtd_etf_fee = mtd_etf_fee.add(new BigDecimal(getString(map.get("FEE"))));
						mtd_etf_amt = mtd_etf_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("02", getString(map.get("ITEM"))) || StringUtils.equals("07", getString(map.get("ITEM")))) {
						mtd_si_fee = mtd_si_fee.add(new BigDecimal(getString(map.get("FEE"))));
						mtd_si_amt = mtd_si_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("03", getString(map.get("ITEM"))) || StringUtils.equals("08", getString(map.get("ITEM")))) {
						mtd_sn_fee = mtd_sn_fee.add(new BigDecimal(getString(map.get("FEE"))));
						mtd_sn_amt = mtd_sn_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("04", getString(map.get("ITEM")))) {
						mtd_dci_fee = mtd_dci_fee.add(new BigDecimal(getString(map.get("FEE"))));
						mtd_dci_amt = mtd_dci_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("17", getString(map.get("ITEM")))) {
						mtd_stock_fee = mtd_stock_fee.add(new BigDecimal(getString(map.get("FEE"))));
						mtd_stock_amt = mtd_stock_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("91", getString(map.get("ITEM")))) {
						mtd_ins_fee = mtd_ins_fee.add(new BigDecimal(getString(map.get("FEE"))));
						mtd_ins_amt = mtd_ins_amt.add(new BigDecimal(getString(map.get("BAL"))));
						mtd_ins_rate = mtd_ins_fee.multiply(new BigDecimal(100)).divide(new BigDecimal(getString(map.get("INS_TAR_AMT"))), 2, BigDecimal.ROUND_HALF_UP);
					}
				}
				mtd_inv_fee = mtd_fund_fee.add(mtd_bond_fee).add(mtd_etf_fee).add(mtd_si_fee).add(mtd_sn_fee).add(mtd_dci_fee).add(mtd_stock_fee);
				mtd_inv_amt = mtd_fund_amt.add(mtd_bond_amt).add(mtd_etf_amt).add(mtd_si_amt).add(mtd_sn_amt).add(mtd_dci_amt).add(mtd_stock_amt);
				mtd_inv_rate = null == list.get(0).get("INV_TAR_AMT") ? BigDecimal.ZERO : mtd_inv_fee.multiply(new BigDecimal(100)).divide(new BigDecimal(getString(list.get(0).get("INV_TAR_AMT"))), 2, BigDecimal.ROUND_HALF_UP);
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_BR_DAY_PROFIT_MTD), ");
			sql.append("TAR_AMT(INV_TAR_AMT,INS_TAR_AMT) AS (SELECT SUM(INV_TAR_AMT_2),SUM(INS_TAR_AMT_2) ");
			sql.append("FROM TBPMS_BR_ACHD_RATE DA,MAX_DATE WHERE DA.DATA_DATE = MAX_DATE.DATA_DATE) ");
			sql.append("SELECT ITEM,SUM(COALESCE(BAL,0)) as BAL,SUM(COALESCE(FEE,0)) as FEE");
			sql.append(",SUM(INV_TAR_AMT)/COUNT(1) AS INV_TAR_AMT,SUM(INS_TAR_AMT)/COUNT(1) AS INS_TAR_AMT ");
			sql.append("FROM TBPMS_BR_DAY_PROFIT_MTD DA INNER JOIN MAX_DATE ON DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql.append("CROSS JOIN TAR_AMT ");
			sql.append("WHERE DA.ITEM IN ('01','02','03','04','05','06','07','08','13','17','91') ");
			sql.append("GROUP BY ITEM");

			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			BigDecimal ytd_fund_fee = new BigDecimal(0), ytd_bond_fee = new BigDecimal(0), ytd_etf_fee = new BigDecimal(0), ytd_stock_fee = new BigDecimal(0), ytd_si_fee = new BigDecimal(0), ytd_sn_fee = new BigDecimal(0), ytd_dci_fee = new BigDecimal(0), ytd_ins_fee = new BigDecimal(0);
			BigDecimal ytd_fund_amt = new BigDecimal(0), ytd_bond_amt = new BigDecimal(0), ytd_etf_amt = new BigDecimal(0), ytd_stock_amt = new BigDecimal(0), ytd_si_amt = new BigDecimal(0), ytd_sn_amt = new BigDecimal(0), ytd_dci_amt = new BigDecimal(0), ytd_ins_amt = new BigDecimal(0);
			BigDecimal ytd_inv_fee = new BigDecimal(0), ytd_inv_amt = new BigDecimal(0), ytd_inv_rate = new BigDecimal(0), ytd_ins_rate = new BigDecimal(0);

			if (!list.isEmpty()) {

				for (Map<String, Object> map : list) {
					if (StringUtils.equals("01", getString(map.get("ITEM")))) {
						ytd_fund_fee = ytd_fund_fee.add(new BigDecimal(getString(map.get("FEE"))));
						ytd_fund_amt = ytd_fund_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("05", getString(map.get("ITEM"))) || StringUtils.equals("13", getString(map.get("ITEM")))) {
						ytd_bond_fee = ytd_bond_fee.add(new BigDecimal(getString(map.get("FEE"))));
						ytd_bond_amt = ytd_bond_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("06", getString(map.get("ITEM")))) {
						ytd_etf_fee = ytd_etf_fee.add(new BigDecimal(getString(map.get("FEE"))));
						ytd_etf_amt = ytd_etf_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("02", getString(map.get("ITEM"))) || StringUtils.equals("07", getString(map.get("ITEM")))) {
						ytd_si_fee = ytd_si_fee.add(new BigDecimal(getString(map.get("FEE"))));
						ytd_si_amt = ytd_si_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("03", getString(map.get("ITEM"))) || StringUtils.equals("08", getString(map.get("ITEM")))) {
						ytd_sn_fee = ytd_sn_fee.add(new BigDecimal(getString(map.get("FEE"))));
						ytd_sn_amt = ytd_sn_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("04", getString(map.get("ITEM")))) {
						ytd_dci_fee = ytd_dci_fee.add(new BigDecimal(getString(map.get("FEE"))));
						ytd_dci_amt = ytd_dci_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("17", getString(map.get("ITEM")))) {
						ytd_stock_fee = ytd_stock_fee.add(new BigDecimal(getString(map.get("FEE"))));
						ytd_stock_amt = ytd_stock_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("91", getString(map.get("ITEM")))) {
						ytd_ins_fee = ytd_ins_fee.add(new BigDecimal(getString(map.get("FEE"))));
						ytd_ins_amt = ytd_ins_amt.add(new BigDecimal(getString(map.get("BAL"))));
						ytd_ins_rate = ytd_ins_fee.multiply(new BigDecimal(100)).divide(new BigDecimal(getString(map.get("INS_TAR_AMT"))), 2, BigDecimal.ROUND_HALF_UP);
					}
				}
				ytd_inv_fee = ytd_fund_fee.add(ytd_bond_fee).add(ytd_etf_fee).add(ytd_si_fee).add(ytd_sn_fee).add(ytd_dci_fee).add(ytd_stock_fee);
				ytd_inv_amt = ytd_fund_amt.add(ytd_bond_amt).add(ytd_etf_amt).add(ytd_si_amt).add(ytd_sn_amt).add(ytd_dci_amt).add(ytd_stock_amt);
				ytd_inv_rate = null == list.get(0).get("INV_TAR_AMT") ? BigDecimal.ZERO : ytd_inv_fee.multiply(new BigDecimal(100)).divide(new BigDecimal(getString(list.get(0).get("INV_TAR_AMT"))), 2, BigDecimal.ROUND_HALF_UP);
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT ITEM,SUM(COALESCE(BAL,0)) as BAL,SUM(COALESCE(FEE,0)) as FEE");
			sql.append("FROM TBPMS_BR_DAY_PROFIT DA ");
			sql.append("WHERE DA.DATA_DATE = TO_CHAR(SYSDATE-1,'YYYYMMDD') AND DA.ITEM IN ('01','02','03','04','05','06','07','08','13','17','91') ");
			sql.append("GROUP BY ITEM");

			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			BigDecimal day_fund_fee = new BigDecimal(0), day_bond_fee = new BigDecimal(0), day_etf_fee = new BigDecimal(0), day_stock_fee = new BigDecimal(0), day_si_fee = new BigDecimal(0), day_sn_fee = new BigDecimal(0), day_dci_fee = new BigDecimal(0), day_ins_fee = new BigDecimal(0);
			BigDecimal day_fund_amt = new BigDecimal(0), day_bond_amt = new BigDecimal(0), day_etf_amt = new BigDecimal(0), day_stock_amt = new BigDecimal(0), day_si_amt = new BigDecimal(0), day_sn_amt = new BigDecimal(0), day_dci_amt = new BigDecimal(0), day_ins_amt = new BigDecimal(0);
			BigDecimal day_inv_fee = new BigDecimal(0), day_inv_amt = new BigDecimal(0);

			if (!list.isEmpty()) {

				for (Map<String, Object> map : list) {
					if (StringUtils.equals("01", getString(map.get("ITEM")))) {
						day_fund_fee = day_fund_fee.add(new BigDecimal(getString(map.get("FEE"))));
						day_fund_amt = day_fund_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("05", getString(map.get("ITEM"))) || StringUtils.equals("13", getString(map.get("ITEM")))) {
						day_bond_fee = day_bond_fee.add(new BigDecimal(getString(map.get("FEE"))));
						day_bond_amt = day_bond_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("06", getString(map.get("ITEM")))) {
						day_etf_fee = day_etf_fee.add(new BigDecimal(getString(map.get("FEE"))));
						day_etf_amt = day_etf_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("02", getString(map.get("ITEM"))) || StringUtils.equals("07", getString(map.get("ITEM")))) {
						day_si_fee = day_si_fee.add(new BigDecimal(getString(map.get("FEE"))));
						day_si_amt = day_si_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("03", getString(map.get("ITEM"))) || StringUtils.equals("08", getString(map.get("ITEM")))) {
						day_sn_fee = day_sn_fee.add(new BigDecimal(getString(map.get("FEE"))));
						day_sn_amt = day_sn_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("04", getString(map.get("ITEM")))) {
						day_dci_fee = day_dci_fee.add(new BigDecimal(getString(map.get("FEE"))));
						day_dci_amt = day_dci_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("17", getString(map.get("ITEM")))) {
						day_stock_fee = day_stock_fee.add(new BigDecimal(getString(map.get("FEE"))));
						day_stock_amt = day_stock_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
					if (StringUtils.equals("91", getString(map.get("ITEM")))) {
						day_ins_fee = day_ins_fee.add(new BigDecimal(getString(map.get("FEE"))));
						day_ins_amt = day_ins_amt.add(new BigDecimal(getString(map.get("BAL"))));
					}
				}
				day_inv_fee = day_fund_fee.add(day_bond_fee).add(day_etf_fee).add(day_si_fee).add(day_sn_fee).add(day_dci_fee).add(day_stock_fee);
				day_inv_amt = day_fund_amt.add(day_bond_amt).add(day_etf_amt).add(day_si_amt).add(day_sn_amt).add(day_dci_amt).add(day_stock_amt);
			}

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "FUND");
			dataMap.put("DAY_FEE", day_fund_fee);
			dataMap.put("DAY_AMT", day_fund_amt);
			dataMap.put("MTD_FEE", mtd_fund_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_fund_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_fund_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_fund_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "BOND");
			dataMap.put("DAY_FEE", day_bond_fee);
			dataMap.put("DAY_AMT", day_bond_amt);
			dataMap.put("MTD_FEE", mtd_bond_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_bond_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_bond_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_bond_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "ETF");
			dataMap.put("DAY_FEE", day_etf_fee);
			dataMap.put("DAY_AMT", day_etf_amt);
			dataMap.put("MTD_FEE", mtd_etf_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_etf_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_etf_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_etf_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "SI");
			dataMap.put("DAY_FEE", day_si_fee);
			dataMap.put("DAY_AMT", day_si_amt);
			dataMap.put("MTD_FEE", mtd_si_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_si_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_si_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_si_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "SN");
			dataMap.put("DAY_FEE", day_sn_fee);
			dataMap.put("DAY_AMT", day_sn_amt);
			dataMap.put("MTD_FEE", mtd_sn_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_sn_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_sn_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_sn_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "DCI");
			dataMap.put("DAY_FEE", day_dci_fee);
			dataMap.put("DAY_AMT", day_dci_amt);
			dataMap.put("MTD_FEE", mtd_dci_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_dci_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_dci_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_dci_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "STOCK");
			dataMap.put("DAY_FEE", day_stock_fee);
			dataMap.put("DAY_AMT", day_stock_amt);
			dataMap.put("MTD_FEE", mtd_stock_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_stock_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_stock_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_stock_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "INV");
			dataMap.put("DAY_FEE", day_inv_fee);
			dataMap.put("DAY_AMT", day_inv_amt);
			dataMap.put("MTD_FEE", mtd_inv_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_inv_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_inv_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_inv_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			dataMap = new HashMap<String, Object>();
			dataMap.put("PRD_TYPE", "INS");
			dataMap.put("DAY_FEE", day_ins_fee);
			dataMap.put("DAY_AMT", day_ins_amt);
			dataMap.put("MTD_FEE", mtd_ins_fee);
			dataMap.put("MTD_FEE_RATE", "--");
			dataMap.put("MTD_AMT", mtd_ins_amt);
			dataMap.put("MTD_AMT_RATE", "--");
			dataMap.put("YTD_FEE", ytd_ins_fee);
			dataMap.put("YTD_FEE_RATE", "--");
			dataMap.put("YTD_AMT", ytd_ins_amt);
			dataMap.put("YTD_AMT_RATE", "--");
			result.add(dataMap);

			outputVO.setResultList(result);
		} else if (bmmgrMap.containsKey(roleID)) { //判斷為分行經理
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_BR_ACHD_RATE) ");
			sql.append("SELECT TOT_TAR_AMT_1,TOT_A_RATE_1, TOT_A_RATE_2, ( CASE WHEN TOT_TAR_AMT_2 > 0 OR TOT_TAR_AMT_1 > 0 THEN ROUND(TOT_TAR_AMT_2*100/TOT_TAR_AMT_1,2) ELSE 0 END )AS TOTAL_ACH_RATE_MTD ");
			sql.append(",BK_RANK_CLS,DA.DATA_DATE ");
			sql.append("FROM TBPMS_BR_ACHD_RATE DA INNER JOIN MAX_DATE ON DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql.append("LEFT JOIN TBPMS_BR_ACHD_RANK RK ON DA.DATA_DATE = RK.DATA_DATE AND DA.BRANCH_NBR = RK.BRANCH_NBR AND RK.SUM_TYPE = 'M' ");
			sql.append("WHERE DA.BRANCH_NBR = :branchNbr AND DA.SUM_TYPE = 'M' ");
			condition.setObject("branchNbr", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			Map<String, Object> dataMap = new HashMap<String, Object>();

			String dataDate = "";
			if (!list.isEmpty()) {
				dataMap.put("MTD_RANK_LVL", getString(list.get(0).get("BK_RANK_CLS"))); //月目標排名
				dataMap.put("MTD_TAR_AMT", getString(list.get(0).get("TOT_TAR_AMT_1"))); //月目標
				dataMap.put("MTD_ACH_RATE", getString(list.get(0).get("TOT_A_RATE_1"))); //MTD已達成率
				dataMap.put("MTD_TAR_RATE", getString(list.get(0).get("TOTAL_ACH_RATE_MTD"))); //MTD應達成率
				dataDate = getString(list.get(0).get("DATA_DATE"));
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			//計算分行家數
			sql.append("SELECT COUNT(1) AS GROUP_CNT FROM VWORG_DEFN_INFO WHERE BRANCH_AREA_ID IN (:areaList) ");
			condition.setQueryString(sql.toString());
			condition.setObject("areaList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("GROUP_CNT", getString(list.get(0).get("GROUP_CNT"))); //同分行等級家數
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select BRANCH_NBR , SUM(FEE) AS MTD_FEE from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'MM') and LAST_DAY(TO_DATE(:dataDate,'YYYYMMDD')) ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("AND BRANCH_NBR = :branchNbr ");
			sql.append("GROUP BY BRANCH_NBR ");
			condition.setObject("branchNbr", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			list = new ArrayList<Map<String, Object>>();
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("MTD_FEE", list.get(0).get("MTD_FEE")); //MTD月手收
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT ");
			sql.append("A.BRANCH_NBR ");
			sql.append(",A.TOT_TAR_AMT_1 ");
			sql.append(",A.TOT_TAR_AMT_2 ");
			sql.append(",CASE WHEN A.TOT_TAR_AMT_1 > 0 THEN ROUND( A.TOT_TAR_AMT_2 *100 / A.TOT_TAR_AMT_1 ,2 ) ELSE 0 END AS TOTAL_ACH_RATE_YTD ");
			sql.append(",CASE WHEN A.TOT_TAR_AMT_1 > 0 THEN ROUND( F.FEE * 100 / A.TOT_TAR_AMT_1 , 2) ELSE 0 END AS TOT_A_RATE_1 ");
			sql.append(",RANK() OVER (PARTITION BY O.BRANCH_CLS ORDER BY CASE WHEN A.TOT_TAR_AMT_1 > 0 THEN ROUND( F.FEE * 100 / A.TOT_TAR_AMT_1 , 2) ELSE 0 END DESC) AS BK_RANK_CLS ");
			sql.append("FROM TBPMS_BR_ACHD_RATE A ");
			sql.append("LEFT JOIN( ");
			sql.append("select BRANCH_NBR , SUM(FEE) AS FEE from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'YYYY') and TO_DATE(:dataDate,'YYYYMMDD') ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("GROUP BY BRANCH_NBR ");
			sql.append(") F ");
			sql.append("ON F.BRANCH_NBR =A.BRANCH_NBR ");
			sql.append("LEFT JOIN TBPMS_ORG_REC_N O ");
			sql.append("ON O.BRANCH_NBR=A.BRANCH_NBR ");
			sql.append("AND TO_DATE(:dataDate,'YYYYMMDD') BETWEEN O.START_TIME AND O.END_TIME ");
			sql.append("WHERE A.DATA_DATE=:dataDate ");
			sql.append("AND A.SUM_TYPE='Y' AND A.BRANCH_NBR = :branchNbr ");
			condition.setObject("dataDate", dataDate);
			condition.setObject("branchNbr", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("YTD_RANK_LVL", getString(list.get(0).get("BK_RANK_CLS"))); //年目標排名
				dataMap.put("YTD_TAR_AMT", getString(list.get(0).get("TOT_TAR_AMT_1"))); //年目標
				dataMap.put("YTD_TAR_RATE", getString(list.get(0).get("TOTAL_ACH_RATE_YTD"))); //YTD應達成率
				dataMap.put("YTD_ACH_RATE", getString(list.get(0).get("TOT_A_RATE_1"))); //YTD已達成率
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT SUM(FEE) AS TD_FEE ");
			sql.append("FROM TBPMS_BR_IMM_PROFIT ");
			sql.append("WHERE TX_DATE = :dataDate AND BRANCH_NBR = :branchNbr ");
			condition.setObject("branchNbr", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {

				dataMap.put("TD_FEE", getString(list.get(0).get("TD_FEE"))); //當日手收
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select BRANCH_NBR , SUM(FEE) AS YTD_FEE from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'YYYY') and TO_DATE(:dataDate,'YYYYMMDD') ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("AND BRANCH_NBR = :branchNbr ");
			sql.append("GROUP BY BRANCH_NBR ");
			condition.setObject("dataDate", dataDate);
			condition.setObject("branchNbr", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("YTD_FEE", getString(list.get(0).get("YTD_FEE"))); //YTD年手收
			}

			//計算近一年未達goal月數
			int mon_not_ach = 0;

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//未達標: GOAL > FEE
			sql.append("WITH BR_FEE(YEARMON,BRANCH_NBR,FEE) AS ( ");
			sql.append("select SUBSTR(DATA_DATE ,1,6), BRANCH_NBR , SUM(FEE) AS FEE from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') >= TRUNC(ADD_MONTHS(SYSDATE,-12)) ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("and BRANCH_NBR IN (:branchList) ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6),BRANCH_NBR ");
			sql.append(") ");
			sql.append("SELECT TAR.DATA_YEARMON, TAR.BRANCH_NBR, TAR.BRANCH_AREA_ID, TAR.BRANCH_AREA_NAME, TAR.BRANCH_NAME, TOT_TAR_AMOUNT as GOAL,FEE ");
			sql.append("FROM TBPMS_BR_PRD_TAR_M TAR LEFT JOIN BR_FEE ON TAR.DATA_YEARMON = BR_FEE.YEARMON AND TAR.BRANCH_NBR = BR_FEE.BRANCH_NBR ");
			sql.append("WHERE TAR.BRANCH_NBR IN (:branchList) AND (DATA_YEARMON BETWEEN TO_CHAR(ADD_MONTHS(SYSDATE,-12),'YYYY') AND TO_CHAR(SYSDATE,'YYYYMM')) ");
			sql.append("AND TOT_TAR_AMOUNT > FEE ");
			sql.append("ORDER BY BRANCH_AREA_ID, BRANCH_NBR, DATA_YEARMON ");
			condition.setQueryString(sql.toString());
			condition.setObject("branchList", (List<String>) getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				mon_not_ach = list.size();
			}

			dataMap.put("MON_NOT_ACH", mon_not_ach); //近一年未達標月數
			result.add(dataMap);
			outputVO.setResultList(result);

			//計算近一年未達goal專員數
			Map<String, Object> mon_not_ach_map = new HashMap<String, Object>();
			for (int i = 0; i < 12; i++) {
				sql = new StringBuffer();
				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_AO_ACHD_RATE WHERE SUBSTRB(DATA_DATE,1,6) = TO_CHAR(Add_months(SYSDATE,:mon),'YYYYMM')) ");
				sql.append("SELECT EMP_ID,CASE WHEN TOT_TAR_AMT_1 <= TOT_TAR_AMT_2 THEN 'Y' ELSE 'N' END as CHECK_TOT FROM TBPMS_AO_ACHD_RATE DA,MAX_DATE ");
				sql.append("WHERE DA.DATA_DATE = MAX_DATE.DATA_DATE AND DA.BRANCH_NBR = :branchNbr AND SUM_TYPE = 'M' ");
				condition.setObject("branchNbr", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
				condition.setObject("mon", i * -1);
				condition.setQueryString(sql.toString());
				list = dam.exeQuery(condition);

				if (!list.isEmpty()) {
					for (int j = 0; j < list.size(); j++) {
						String empID = getString(list.get(j).get("EMP_ID"));
						String checkTOT = getString(list.get(j).get("CHECK_TOT"));
						if (StringUtils.equals("N", getString(list.get(j).get("CHECK_TOT"))) && !mon_not_ach_map.containsKey(empID)) {
							mon_not_ach_map.put(empID, empID);
						}
					}
				}

			}
			dataMap.put("MON_NOT_ACH_AO", mon_not_ach_map.size()); //近一年未達標專員
			dataMap.put("MON_NOT_ACH_AO_D", mon_not_ach_map); //近一年未達標專員清單

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT IPO.PRD_ID,PRD.PNAME,SUM(BAL) AS IPO_AMT FROM TBPMS_IPO_RPT IPO INNER JOIN TBPMS_IPO_PARAM_MAST PARM ON IPO.PRJ_SEQ = PARM.PRJ_SEQ ");
			sql.append("LEFT JOIN VWPRD_MASTER PRD ON IPO.PRD_ID = PRD.PRD_ID ");
			sql.append("WHERE (SYSDATE BETWEEN START_DT AND END_DT) AND IPO.BRANCH_NBR = :branchNbr and DATA_DATE = :dataDate ");
			sql.append("GROUP BY IPO.PRD_ID,PRD.PNAME");
			condition.setObject("branchNbr", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				outputVO.setIPOList(list);
			}
		} else if (mbrmgrMap.containsKey(roleID) || armgrMap.containsKey(roleID)) { //其餘角色非總行. 營運區主管/業務處主管
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_BA_ACHD_RATE) ");
			sql.append("SELECT TOT_TAR_AMT_1,TOT_A_RATE_1, TOT_A_RATE_2, ( CASE WHEN TOT_TAR_AMT_2 > 0 OR TOT_TAR_AMT_1 > 0 THEN ROUND(TOT_TAR_AMT_2*100/TOT_TAR_AMT_1,2) ELSE 0 END )AS TOTAL_ACH_RATE_MTD ");
			sql.append(",BK_RANK_ALL,DA.DATA_DATE ");
			sql.append("FROM TBPMS_BA_ACHD_RATE DA INNER JOIN MAX_DATE ON DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql.append("LEFT JOIN TBPMS_BA_ACHD_RANK RK ON DA.DATA_DATE = RK.DATA_DATE AND DA.BRANCH_AREA_ID = RK.BRANCH_AREA_ID AND RK.SUM_TYPE = 'M' ");
			sql.append("WHERE DA.BRANCH_AREA_ID IN (:areaList) AND DA.SUM_TYPE = 'M' ");
			condition.setObject("areaList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

			//業務處主管
			sql2 = new StringBuffer();
			condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql2.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_BA_ACHD_RATE) ");
			sql2.append("SELECT SUM(TOT_TAR_AMT_1) AS NEW_TOT_TAR_AMT_1 ");
			sql2.append("FROM TBPMS_BA_ACHD_RATE DA INNER JOIN MAX_DATE ON DA.DATA_DATE = MAX_DATE.DATA_DATE ");
			sql2.append("LEFT JOIN TBPMS_BA_ACHD_RANK RK ON DA.DATA_DATE = RK.DATA_DATE AND DA.BRANCH_AREA_ID = RK.BRANCH_AREA_ID AND RK.SUM_TYPE = 'M' ");
			sql2.append("WHERE DA.BRANCH_AREA_ID IN (:areaList) AND DA.SUM_TYPE = 'M' ");
			condition2.setObject("areaList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			condition2.setQueryString(sql2.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(condition2);

			Map<String, Object> dataMap = new HashMap<String, Object>();
			String dataDate = "";
			if (!list.isEmpty()) {
				dataMap.put("MTD_RANK_LVL", getString(list.get(0).get("BK_RANK_ALL"))); //月目標排名
				dataMap.put("MTD_TAR_AMT", getString(list2.get(0).get("NEW_TOT_TAR_AMT_1"))); //月目標
				dataMap.put("MTD_TAR_RATE", getString(list.get(0).get("TOT_A_RATE_2"))); //MTD已達成率

				dataMap.put("MTD_ACH_RATE", getString(list.get(0).get("TOTAL_ACH_RATE_MTD"))); //MTD應達成率
				dataDate = getString(list.get(0).get("DATA_DATE"));
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//計算分行家數
			sql.append("SELECT COUNT(1) AS GROUP_CNT FROM VWORG_DEFN_INFO WHERE REGION_CENTER_ID IN (:reginList) ");
			condition.setQueryString(sql.toString());
			condition.setObject("reginList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("GROUP_CNT", getString(list.get(0).get("GROUP_CNT"))); //同分行等級家數
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT SUM(FEE) AS MTD_FEE FROM TBPMS_BR_DAY_PROFIT_MTD DA ");
			sql.append("WHERE DA.BRANCH_NBR IN (:branchList) and ITEM = '92' AND DATA_DATE = :dataDate ");
			condition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			list = new ArrayList<Map<String, Object>>();
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("MTD_FEE", list.get(0).get("MTD_FEE")); //MTD月手收
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT * FROM (");
			sql.append("SELECT ");
			sql.append(" DEPT_ID ");
			sql.append(" ,SUM(A.TOT_TAR_AMT_1) AS TOT_TAR_AMT_1 ");
			sql.append(" ,SUM(A.TOT_TAR_AMT_2) AS TOT_TAR_AMT_2 ");
			sql.append(" ,CASE WHEN SUM(A.TOT_TAR_AMT_1) > 0 THEN ROUND( SUM(A.TOT_TAR_AMT_2) *100 / SUM(A.TOT_TAR_AMT_1) ,2 ) ELSE 0 END AS TOTAL_ACH_RATE_YTD ");
			sql.append(" ,CASE WHEN SUM(A.TOT_TAR_AMT_1) > 0 THEN ROUND( SUM(F.FEE) * 100 / SUM(A.TOT_TAR_AMT_1) , 2) ELSE 0 END AS TOT_A_RATE_1 ");
			sql.append(" ,RANK() OVER (PARTITION BY NULL ORDER BY CASE WHEN SUM(A.TOT_TAR_AMT_1) > 0 THEN ROUND( SUM(F.FEE) * 100 / SUM(A.TOT_TAR_AMT_1) , 2) ELSE 0 END DESC) AS BK_RANK_CLS ");
			sql.append("FROM TBPMS_BR_ACHD_RATE A ");
			sql.append(" LEFT JOIN( ");
			sql.append(" select BRANCH_NBR , SUM(FEE) AS FEE from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append(" where ITEM IN('90','91','09') ");
			sql.append(" AND DATA_DATE IN ");
			sql.append(" (select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append(" where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'YYYY') and TO_DATE(:dataDate,'YYYYMMDD') ");
			sql.append(" GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append(" GROUP BY BRANCH_NBR ");
			sql.append(" ) F ");
			sql.append(" ON F.BRANCH_NBR =A.BRANCH_NBR ");
			sql.append(" LEFT JOIN VWORG_DEFN_BRH O ");
			sql.append(" ON O.BRANCH_NBR=A.BRANCH_NBR ");
			sql.append("WHERE A.DATA_DATE=:dataDate ");
			sql.append("AND A.SUM_TYPE='Y' ");
			sql.append("AND DEPT_ID IN( ");
			sql.append(" SELECT CASE WHEN R.PRIVILEGEID = '013' THEN REGION_CENTER_ID WHEN R.PRIVILEGEID = '012' THEN BRANCH_AREA_ID END FROM VWORG_DEFN_INFO ");
			sql.append(" INNER JOIN TBSYSSECUROLPRIASS R ");
			sql.append("ON R.ROLEID = :ROLEID ");
			sql.append(") ");
			sql.append("GROUP BY DEPT_ID ");
			sql.append(")WHERE DEPT_ID =:DEPT_ID ");

			condition.setObject("dataDate", dataDate);
			condition.setObject("ROLEID", roleID);
			if (mbrmgrMap.containsKey(roleID)) {
				condition.setObject("DEPT_ID", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			} else if (armgrMap.containsKey(roleID)) {
				condition.setObject("DEPT_ID", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);

			if (!list.isEmpty()) {
				dataMap.put("YTD_RANK_LVL", getString(list.get(0).get("BK_RANK_CLS"))); //年目標排名
				dataMap.put("YTD_TAR_AMT", getString(list.get(0).get("TOT_TAR_AMT_1"))); //年目標
				dataMap.put("YTD_TAR_RATE", getString(list.get(0).get("TOTAL_ACH_RATE_YTD"))); //YTD應達成率
				dataMap.put("YTD_ACH_RATE", getString(list.get(0).get("TOT_A_RATE_1"))); //YTD已達成率
			}
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT SUM(FEE) AS TD_FEE ");
			sql.append("FROM TBPMS_BR_IMM_PROFIT ");
			sql.append("WHERE TX_DATE = :dataDate AND BRANCH_NBR IN (:branchNbr) ");
			condition.setObject("branchNbr", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());

			if (!condition.getQueryString().isEmpty()) {
				list = dam.exeQuery(condition);
				if (!list.isEmpty()) {
					dataMap.put("TD_FEE", getString(list.get(0).get("TD_FEE"))); //當日手收
				}
			}

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("select SUM(FEE) AS YTD_FEE from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') between TRUNC(TO_DATE(:dataDate,'YYYYMMDD'),'YYYY') and TO_DATE(:dataDate,'YYYYMMDD') ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("AND BRANCH_NBR in (:branchNbr) ");
			condition.setObject("branchNbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				dataMap.put("YTD_FEE", getString(list.get(0).get("YTD_FEE"))); //YTD年手收
			}

			int mon_not_ach = 0;
			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//未達標: GOAL > FEE
			sql.append("WITH BR_FEE(YEARMON,BRANCH_NBR,FEE) AS ( ");
			sql.append("select SUBSTR(DATA_DATE ,1,6), BRANCH_NBR , SUM(FEE) AS FEE from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where ITEM IN('90','91','09') ");
			sql.append("AND DATA_DATE IN ");
			sql.append("(select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MTD ");
			sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') >= TRUNC(ADD_MONTHS(SYSDATE,-12)) ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
			sql.append("and BRANCH_NBR IN (:branchList) ");
			sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6),BRANCH_NBR ");
			sql.append(") ");
			sql.append("SELECT TAR.DATA_YEARMON, TAR.BRANCH_NBR, TAR.BRANCH_AREA_ID, TAR.BRANCH_AREA_NAME, TAR.BRANCH_NAME, TOT_TAR_AMOUNT as GOAL,FEE ");
			sql.append("FROM TBPMS_BR_PRD_TAR_M TAR LEFT JOIN BR_FEE ON TAR.DATA_YEARMON = BR_FEE.YEARMON AND TAR.BRANCH_NBR = BR_FEE.BRANCH_NBR ");
			sql.append("WHERE TAR.BRANCH_NBR IN (:branchList) AND (DATA_YEARMON BETWEEN TO_CHAR(ADD_MONTHS(SYSDATE,-12),'YYYY') AND TO_CHAR(SYSDATE,'YYYYMM')) ");
			sql.append("AND TOT_TAR_AMOUNT > FEE ");
			sql.append("ORDER BY BRANCH_AREA_ID, BRANCH_NBR, DATA_YEARMON ");
			condition.setQueryString(sql.toString());
			condition.setObject("branchList", (List<String>) getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				mon_not_ach = list.size();
			}

			dataMap.put("MON_NOT_ACH", mon_not_ach); //近一年未達標月數
			result.add(dataMap);
			outputVO.setResultList(result);

			//計算近一年未達goal專員數
			Map<String, Object> mon_not_ach_map = new HashMap<String, Object>();
			for (int i = 0; i < 12; i++) {
				sql = new StringBuffer();
				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql.append("WITH MAX_DATE(DATA_DATE) AS (SELECT MAX(DATA_DATE) FROM TBPMS_AO_ACHD_RATE WHERE SUBSTRB(DATA_DATE,1,6) = TO_CHAR(Add_months(SYSDATE,:mon),'YYYYMM')) ");
				sql.append("SELECT EMP_ID,CASE WHEN TOT_TAR_AMT_1 <= TOT_TAR_AMT_2 THEN 'Y' ELSE 'N' END as CHECK_TOT FROM TBPMS_AO_ACHD_RATE DA,MAX_DATE ");
				sql.append("WHERE DA.DATA_DATE = MAX_DATE.DATA_DATE AND DA.BRANCH_AREA_ID IN (:areaList) AND SUM_TYPE = 'M' ");
				condition.setObject("areaList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				condition.setObject("mon", i * -1);
				condition.setQueryString(sql.toString());
				list = dam.exeQuery(condition);

				if (!list.isEmpty()) {
					for (int j = 0; j < list.size(); j++) {
						String empID = getString(list.get(j).get("EMP_ID"));
						String checkTOT = getString(list.get(j).get("CHECK_TOT"));
						if (StringUtils.equals("N", getString(list.get(j).get("CHECK_TOT"))) && !mon_not_ach_map.containsKey(empID)) {
							mon_not_ach_map.put(empID, empID);
						}
					}
				}
			}
			dataMap.put("MON_NOT_ACH_AO", mon_not_ach_map.size()); //近一年未達標專員
			dataMap.put("MON_NOT_ACH_AO_D", mon_not_ach_map); //近一年未達標專員清單

			sql = new StringBuffer();
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT IPO.PRD_ID,PRD.PNAME,SUM(BAL) AS IPO_AMT FROM TBPMS_IPO_RPT IPO INNER JOIN TBPMS_IPO_PARAM_MAST PARM ON IPO.PRJ_SEQ = PARM.PRJ_SEQ ");
			sql.append("LEFT JOIN VWPRD_MASTER PRD ON IPO.PRD_ID = PRD.PRD_ID ");
			sql.append("WHERE (SYSDATE BETWEEN START_DT AND END_DT) AND IPO.BRANCH_AREA_ID IN (:areaList) and DATA_DATE = :dataDate ");
			sql.append("GROUP BY IPO.PRD_ID,PRD.PNAME");
			condition.setObject("areaList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			condition.setObject("dataDate", dataDate);
			condition.setQueryString(sql.toString());
			list = dam.exeQuery(condition);
			if (!list.isEmpty()) {
				outputVO.setIPOList(list);
			}
		}

		sendRtnObject(outputVO);
	}

	/**
	 * 取得近一年AO達成狀況
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getUnAchAOList(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> faiaMap = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2); //FAIA
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
		StringBuffer sql = new StringBuffer();
		DataAccessManager dam = this.getDataAccessManager();
		List<String> empList = new ArrayList<String>();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sql = new StringBuffer();
		sql.append("WITH AO_FEE(YEARMON,EMP_ID,FEE) AS ( ");
		sql.append("select SUBSTR(DATA_DATE ,1,6), EMP_ID , SUM(FEE) AS FEE from TBPMS_AO_DAY_PROFIT_MTD ");
		sql.append("where ITEM IN('90','91','09') ");
		sql.append("AND DATA_DATE IN ");
		sql.append("(select MAX(DATA_DATE) from TBPMS_AO_DAY_PROFIT_MTD ");
		sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') >= TRUNC(ADD_MONTHS(SYSDATE,-12)) ");
		sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
		sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6),EMP_ID ");
		sql.append(") ");
		sql.append("SELECT NVL(TAR.DATA_YEARMON,AO_FEE.YEARMON) AS DATA_YEARMON ,AO.BRA_NBR AS BRANCH_NBR, AO.AREA_ID AS BRANCH_AREA_ID, AO.AREA_NAME AS BRANCH_AREA_NAME, AO.BRANCH_NAME,AO.AO_CODE,AO.EMP_NAME,NVL(TAR.EMP_ID,AO_FEE.EMP_ID) AS EMP_ID , NVL(TAR_AMOUNT,0) as GOAL,FEE FROM AO_FEE ");
		sql.append("LEFT JOIN TBPMS_AO_PRD_TAR_M TAR ");
		sql.append("ON TAR.DATA_YEARMON = AO_FEE.YEARMON AND TAR.EMP_ID = AO_FEE.EMP_ID ");
		sql.append("AND (DATA_YEARMON BETWEEN TO_CHAR(ADD_MONTHS(SYSDATE,-12),'YYYY') AND TO_CHAR(SYSDATE,'YYYYMM')) ");
		sql.append("AND NVL(TAR_AMOUNT,0) > FEE ");
		sql.append("LEFT JOIN VWORG_AO_INFO AO ");
		sql.append("ON AO.EMP_ID = AO_FEE.EMP_ID AND AO.TYPE='1' ");
		sql.append("WHERE AO_FEE.EMP_ID IN (:empList) ");
		condition.setQueryString(sql.toString());
		//非理專
		if (!fcMap.containsKey(roleID)) {
			condition.setObject("empList", empList);
		} else {
			condition.setObject("empList", getUserVariable(FubonSystemVariableConsts.LOGINID));
		}

		List<Map<String, Object>> list = dam.exeQuery(condition);
		outputVO.setResultList(list);
		sendRtnObject(outputVO);
	}

	/**
	 * 取得近一年分行達成狀況
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getUnAchBranchList(Object body, IPrimitiveMap header) throws JBranchException {
		PMS000InputVO inputVO = (PMS000InputVO) body;
		PMS000OutputVO outputVO = new PMS000OutputVO();

		DataAccessManager dam = this.getDataAccessManager();

		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//未達標: GOAL > FEE
		sql.append("WITH BR_FEE(YEARMON,BRANCH_NBR,FEE) AS ( ");
		sql.append("select SUBSTR(DATA_DATE ,1,6), BRANCH_NBR , SUM(FEE) AS FEE from TBPMS_BR_DAY_PROFIT_MTD ");
		sql.append("where ITEM IN('90','91','09') ");
		sql.append("AND DATA_DATE IN ");
		sql.append("(select MAX(DATA_DATE) from TBPMS_BR_DAY_PROFIT_MTD ");
		sql.append("where TO_DATE(DATA_DATE,'YYYYMMDD') >= TRUNC(ADD_MONTHS(SYSDATE,-12)) ");
		sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6)) ");
		sql.append("and BRANCH_NBR IN (:branchList) ");
		sql.append("GROUP BY SUBSTR(DATA_DATE ,1,6),BRANCH_NBR ");
		sql.append(") ");
		sql.append("SELECT NVL(TAR.DATA_YEARMON,BR_FEE.YEARMON) AS DATA_YEARMON, NVL(TAR.BRANCH_NBR,BR_FEE.BRANCH_NBR) AS BRANCH_NBR, TAR.BRANCH_AREA_ID, TAR.BRANCH_AREA_NAME, TAR.BRANCH_NAME, NVL(TOT_TAR_AMOUNT,0) as GOAL,FEE FROM BR_FEE ");
		sql.append("LEFT JOIN TBPMS_BR_PRD_TAR_M TAR ");
		sql.append("ON TAR.DATA_YEARMON = BR_FEE.YEARMON AND TAR.BRANCH_NBR = BR_FEE.BRANCH_NBR ");
		sql.append("AND (DATA_YEARMON BETWEEN TO_CHAR(ADD_MONTHS(SYSDATE,-12),'YYYY') AND TO_CHAR(SYSDATE,'YYYYMM')) ");
		sql.append("AND BR_FEE.BRANCH_NBR IN (:branchList) ");
		sql.append("WHERE NVL(TOT_TAR_AMOUNT,0) > FEE ");
		condition.setQueryString(sql.toString());
		condition.setObject("branchList", (List<String>) getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		List<Map<String, Object>> list = dam.exeQuery(condition);

		//未達標LIST
		ArrayList<String> dataMap = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			dataMap.add(ObjectUtils.toString(list.get(i).get("BRANCH_NBR")));
		}

		outputVO.setResultList(list);
		sendRtnObject(outputVO);
	}

	private String getString(Object val) {
		if (val == null) {
			return "";
		} else {
			return val.toString();
		}

	}

	private boolean isPM(String roleID) throws JBranchException {
		StringBuffer sql = new StringBuffer();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sql.append("SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = :roleID");
		condition.setQueryString(sql.toString());
		condition.setObject("roleID", roleID);
		List<Map<String, Object>> list = dam.exeQuery(condition);

		if (!list.isEmpty()) {
			String privilegeID = getString(list.get(0).get("PRIVILEGEID"));
			if (StringUtils.equals("016", privilegeID) || StringUtils.equals("017", privilegeID) || StringUtils.equals("018", privilegeID) || StringUtils.equals("025", privilegeID) || StringUtils.equals("026", privilegeID) || StringUtils.equals("027", privilegeID)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}