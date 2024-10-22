package com.systex.jbranch.app.server.fps.cam180;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("cam180")
@Scope("request")
public class CAM180 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataAccessManager dam = null;

	// 讀取理專資料
	public void queryAoData(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM180InputVO inputVO = (CAM180InputVO) body;
		CAM180OutputVO return_VO = new CAM180OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRIASS.PRIVILEGEID, PRIASS.ROLEID, CASE WHEN PRIASS.PRIVILEGEID IN ('011', '012', '013', '006', '009') THEN 'BOSS' WHEN ROL.JOB_TITLE_NAME IS NULL THEN 'ALL' ELSE 'MEMBER' END AS MEM_TYPE ");
		sql.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sql.append("LEFT JOIN TBORG_ROLE ROL ON PRIASS.ROLEID = ROL.ROLE_ID AND ROL.JOB_TITLE_NAME IS NOT NULL ");
		sql.append("WHERE PRIASS.ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT INFO.EMP_NAME AS LABEL, INFO.EMP_ID AS DATA ");
		sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getbCode()) && Integer.valueOf(inputVO.getbCode()) > 0) {
			sql.append(" AND INFO.BRANCH_NBR = :brNbr ");
			queryCondition.setObject("brNbr", inputVO.getbCode());
		} else if (list.size() > 0 && StringUtils.equals("ALL", (String) list.get(0).get("MEM_TYPE"))) {
			sql.append(" AND (INFO.BRANCH_NBR IN (:brNbrList) OR INFO.BRANCH_NBR IS NULL) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		if (list.size() > 0 && StringUtils.equals("MEMBER", (String) list.get(0).get("MEM_TYPE"))) {
			sql.append("AND INFO.EMP_ID = :empID ");
			queryCondition.setObject("empID", ws.getUser().getUserID());
		}
		sql.append("ORDER BY INFO.EMP_ID ");

		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CAM180InputVO inputVO = (CAM180InputVO) body;
		CAM180OutputVO return_VO = new CAM180OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		queryCondition.setFetchSize(3000); //沒有用，不會真的限制3000筆資料
		queryCondition.setMaxResults((Integer) SysInfo.getInfoValue(FubonSystemVariableConsts.QRY_MAX_RESULTS));
		queryCondition.setQueryString(genSql(queryCondition, inputVO).toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}

	//inquire查詢SQL
	private StringBuffer genSql(QueryConditionIF queryCondition, CAM180InputVO inputVO) throws JBranchException {
		StringBuffer sql = new StringBuffer();
		
		if ("CUS_RECORD".equals(inputVO.getFrom())) {
			// TBCRM_CUST_VISIT_RECORD is A
			//來源名單：客戶所有訪談記錄
			if (StringUtils.isNotBlank(inputVO.getbCode()) &&
					StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
				//查詢條件有分行代碼，行銷名單以及客戶主檔的分行條件不以OR做，用UNION ALL
				inputVO.setbCodeType("1");
				sql.append(genSql_CUS_RECORD(queryCondition, inputVO));
				sql.append(" UNION ");
				inputVO.setbCodeType("2");
				sql.append(genSql_CUS_RECORD(queryCondition, inputVO));
			} else {
				//沒有輸入分行代碼或是私銀人員，不需要UNION SQL
				sql.append(genSql_CUS_RECORD(queryCondition, inputVO));
			}
		} else {
			// TBCRM_CUST_VISIT_RECORD is F
			//來源名單：非客戶所有訪談記錄
			if (StringUtils.isNotBlank(inputVO.getbCode()) &&
					StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) {
				//查詢條件有分行代碼，行銷名單以及客戶主檔的分行條件不以OR做，用UNION ALL
				inputVO.setbCodeType("1");
				sql.append(genSql_NOT_ALL(queryCondition, inputVO));
				sql.append(" UNION ");
				inputVO.setbCodeType("2");
				sql.append(genSql_NOT_ALL(queryCondition, inputVO));
			} else {
				//沒有輸入分行代碼或是私銀人員，不需要UNION SQL
				sql.append(genSql_NOT_ALL(queryCondition, inputVO));
			}
		}
		sql.append("order by LEAD_NAME,DEPT_NAME,EMP_ID,CUST_ID ");
		
		logger.info("CAM180 inquire genSQL loginID: " + (String) getCommonVariable(FubonSystemVariableConsts.LOGINID));
		logger.info("CAM180 inquire genSQL loginRole: " + (String) getCommonVariable(FubonSystemVariableConsts.LOGINROLE));
		logger.info("CAM180 inquire genSQL loginFlag: " + (String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG));
		logger.info("CAM180 inquire genSQL SQL: " + sql.toString());
		
		return sql;
	}

	//客戶所有訪談記錄
	private StringBuffer genSql_CUS_RECORD(QueryConditionIF queryCondition, CAM180InputVO inputVO) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員

		StringBuffer sql = new StringBuffer();
		// TBCRM_CUST_VISIT_RECORD is A
		sql.append("select r.RESPONSE_NAME, ");
		sql.append("       b.LEAD_NAME, ");
		sql.append("       c.CAMPAIGN_ID, ");
		sql.append("       c.CAMPAIGN_DESC, ");
		sql.append("       h.DEPT_NAME, ");
		sql.append("       b.EMP_ID, ");
		sql.append("       b.AO_CODE, ");
		sql.append("       CASE WHEN cu.EMP_ID IS NOT NULL THEN NULL ELSE d.AO_CODE END AS CAO_CODE, ");
		sql.append("       CASE WHEN cu.EMP_ID IS NOT NULL THEN cu.EMP_ID ELSE NULL END AS CU_EMP_ID, ");
		sql.append("       d.CUST_ID, ");
		sql.append("       d.CUST_NAME, ");
		sql.append("       f.BASE_FLAG, ");
		sql.append("       e.COMM_RS_YN, ");
		sql.append("       e.COMM_NS_YN, ");
		sql.append("       e.PROF_INVESTOR_YN, ");
		sql.append("       e.SP_CUST_YN, ");
		sql.append("       e.REC_YN, ");
		sql.append("       e.SIGN_AGMT_YN, ");
		sql.append("       c.LEAD_SOURCE_ID, ");
		sql.append("       c.LEAD_TYPE, ");
		sql.append("       b.LEAD_STATUS, ");
		sql.append("       b.START_DATE, b.END_DATE, a.VISIT_MEMO, a.CREATETIME, a.LASTUPDATE, ");
		sql.append("       a.VISIT_DT, a.VISIT_CREPLY, "); // #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單");
		sql.append("       a.CMU_TYPE, ");
		sql.append("       CASE WHEN a.CREATOR <> 'SCHEDULER' THEN DECODE(a.CREATOR, NULL, NULL, g2.EMP_NAME || '-' || a.CREATOR) ELSE a.CREATOR END as CREATOR, ");
		sql.append("       CASE WHEN a.MODIFIER <> 'SCHEDULER' THEN DECODE(a.MODIFIER, NULL, NULL, g3.EMP_NAME || '-' || a.MODIFIER) ELSE a.MODIFIER END as MODIFIER ");
		sql.append("from ( ");
		switch (inputVO.getTabType()) {
			case "0":
				sql.append("  SELECT * FROM TBCRM_CUST_VISIT_RECORD_NEW ");
				sql.append(") a ");
				break;
			case "1":
				sql.append("  SELECT * FROM TBCRM_CUST_VISIT_RECORD WHERE TRUNC(LASTUPDATE) < TRUNC(ADD_MONTHS(SYSDATE, -60)) ");
				sql.append(") a ");
				break;
		}
		sql.append("left join TBCAM_SFA_LEADS b on a.VISIT_SEQ = b.CUST_MEMO_SEQ ");
		sql.append("left join TBCAM_SFA_CAMPAIGN c on (c.CAMPAIGN_ID = b.CAMPAIGN_ID and c.STEP_ID = b.STEP_ID) ");
		sql.append("left join TBCAM_SFA_CAMP_RESPONSE r ON (c.LEAD_RESPONSE_CODE = r.CAMPAIGN_ID OR c.CAMPAIGN_ID = r.CAMPAIGN_ID) AND b.LEAD_STATUS = r.LEAD_STATUS ");
		sql.append("left join TBCRM_CUST_MAST d on a.CUST_ID = d.CUST_ID ");
		sql.append("left join VWORG_EMP_UHRM_INFO cu on cu.UHRM_CODE = d.AO_CODE ");
		sql.append("left join TBCRM_CUST_NOTE e on a.CUST_ID = e.CUST_ID ");
		sql.append("left join TBCRM_CUST_AGR_MKT_NOTE f on a.CUST_ID = f.CUST_ID ");
		sql.append("left join TBORG_MEMBER g on b.EMP_ID = g.EMP_ID ");
		sql.append("left join TBORG_MEMBER g2 on a.CREATOR = g2.EMP_ID ");
		sql.append("left join TBORG_MEMBER g3 on a.MODIFIER = g3.EMP_ID ");
		sql.append("left join TBORG_DEFN h on d.BRA_NBR = h.DEPT_ID ");
		sql.append("where 1 = 1 ");

		// 20200602 ADD BY OCEAN : 0000166: WMS-CR-20200226-01_高端業管功能改採兼任分行角色調整相關功能_登入底層+行銷模組
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) { // 非任何uhrm相關人員
			// 1.	以分行人員登入時，應僅含登入者建立的客戶訪談記錄。
			// 2.	以分行主管登入時，應僅含分行人員建立(排除高端人員)的客戶訪談記錄。
			// 3.	以總行人員登入時，應為全行客戶訪談記錄。
			if (!headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.UHRM_CODE = d.AO_CODE) ");
			}
			//非總行一定要選分行，不用IN
			//總行不需要限制分行
			if (StringUtils.isNotBlank(inputVO.getbCode()) && StringUtils.equals("1", inputVO.getbCodeType())) {
				sql.append("and b.BRANCH_ID = :bcode ");
				queryCondition.setObject("bcode", inputVO.getbCode());
			} else if (StringUtils.isNotBlank(inputVO.getbCode()) && StringUtils.equals("2", inputVO.getbCodeType())) {
				sql.append("and d.BRA_NBR = :bcode ");
				queryCondition.setObject("bcode", inputVO.getbCode());
			}
			
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = d.AO_CODE) ");
			}
		} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).equals("uhrm")) {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.UHRM_CODE = d.AO_CODE AND UHRM.EMP_ID = :loginID) ");
			queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.UHRM_CODE = d.AO_CODE AND UHRM.DEPT_ID = :loginArea) ");
			queryCondition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
		}

		if (StringUtils.isNotBlank(inputVO.getpCode())) {
			// modify by ocean 20170905：下載時，若有指定理專，則找訪談記錄中建立人為該理專的訪談記錄
			sql.append("and a.CREATOR = :pcode ");
			queryCondition.setObject("pcode", inputVO.getpCode());
		}

		if (StringUtils.isNotBlank(inputVO.getId())) {
			sql.append("and a.CUST_ID = :id ");
			queryCondition.setObject("id", inputVO.getId());
		}

		if (StringUtils.isNotBlank(inputVO.getName())) {
			sql.append("and b.LEAD_NAME = :name ");
			queryCondition.setObject("name", inputVO.getName());
		}

		if (StringUtils.isNotBlank(inputVO.getModifier())) {
			sql.append("and g.EMP_NAME = :mod ");
			queryCondition.setObject("mod", inputVO.getModifier());
		}

		if (StringUtils.isNotBlank(inputVO.getForce())) {
			sql.append("and a.VISITOR_ROLE IN (select roleid from tbsyssecurolpriass where privilegeid in (select param_code from TBSYSPARAMETER where PARAM_TYPE = 'CAM.CHANNEL_MAPPING' and param_name = :chan)) ");
			queryCondition.setObject("chan", inputVO.getForce());
		}

		if (inputVO.getsDate() != null) {
			sql.append("and a.CREATETIME >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}

		if (inputVO.geteDate() != null) {
			sql.append("and a.CREATETIME < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.geteDate());
		}

		if (inputVO.getsEDate() != null) {
			sql.append("and a.LASTUPDATE >= TRUNC(:estart) ");
			queryCondition.setObject("estart", inputVO.getsEDate());
		}

		if (inputVO.geteEDate() != null) {
			sql.append("and a.LASTUPDATE < TRUNC(:eend)+1 ");
			queryCondition.setObject("eend", inputVO.geteEDate());
		}

//		sql.append("order by LEAD_NAME,DEPT_NAME,EMP_ID,CUST_ID ");

		return sql;
	}
	
	//非客戶所有訪談記錄
	private StringBuffer genSql_NOT_ALL(QueryConditionIF queryCondition, CAM180InputVO inputVO) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員

		StringBuffer sql = new StringBuffer();
		// TBCRM_CUST_VISIT_RECORD is F
		sql.append("select r.RESPONSE_NAME, ");
		sql.append("       b.LEAD_NAME, ");
		sql.append("       a.CAMPAIGN_ID, ");
		sql.append("       a.CAMPAIGN_DESC, ");
		sql.append("       case when c.BRA_NBR is not null then h.DEPT_ID || '-' || h.DEPT_NAME else h2.DEPT_ID || '-' || h2.DEPT_NAME end as DEPT_NAME, ");
		sql.append("       b.EMP_ID, ");
		sql.append("       b.AO_CODE, ");
		sql.append("       CASE WHEN cu.EMP_ID IS NOT NULL THEN NULL ELSE c.AO_CODE END AS CAO_CODE, ");
		sql.append("       CASE WHEN cu.EMP_ID IS NOT NULL THEN cu.EMP_ID ELSE NULL END AS CU_EMP_ID, ");
		sql.append("       case when c.CUST_ID is not null then c.CUST_ID else b.CUST_ID end as CUST_ID, ");
		sql.append("       c.CUST_NAME, ");
		sql.append("       e.BASE_FLAG, ");
		sql.append("       d.COMM_RS_YN, ");
		sql.append("       d.COMM_NS_YN, ");
		sql.append("       d.PROF_INVESTOR_YN, ");
		sql.append("       d.SP_CUST_YN, ");
		sql.append("       d.REC_YN, ");
		sql.append("       d.SIGN_AGMT_YN, ");
		sql.append("       a.LEAD_SOURCE_ID, ");
		sql.append("       a.LEAD_TYPE, ");
		sql.append("       b.LEAD_STATUS, ");
		sql.append("       b.START_DATE, b.END_DATE, f.VISIT_MEMO, b.CREATETIME, b.LASTUPDATE, ");
		sql.append("       f.VISIT_DT, f.VISIT_CREPLY, "); // #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
		sql.append("       f.CMU_TYPE, ");
		sql.append("       CASE WHEN b.CREATOR <> 'SCHEDULER' THEN DECODE(b.CREATOR, NULL, NULL, g2.EMP_NAME || '-' || b.CREATOR) ELSE b.CREATOR END as CREATOR, ");
		sql.append("       CASE WHEN b.MODIFIER <> 'SCHEDULER' THEN DECODE(b.MODIFIER, NULL, NULL, g3.EMP_NAME || '-' || b.MODIFIER) ELSE b.MODIFIER END as MODIFIER ");
		sql.append("from TBCAM_SFA_CAMPAIGN a ");
		sql.append("inner join TBCAM_SFA_LEADS b on (a.CAMPAIGN_ID = b.CAMPAIGN_ID and a.STEP_ID = b.STEP_ID) ");
		sql.append("left join TBCAM_SFA_CAMP_RESPONSE r ON (a.LEAD_RESPONSE_CODE = r.CAMPAIGN_ID OR a.CAMPAIGN_ID = r.CAMPAIGN_ID) AND b.LEAD_STATUS = r.LEAD_STATUS ");
		sql.append("left join TBCRM_CUST_MAST c on b.CUST_ID = c.CUST_ID ");
		sql.append("left join VWORG_EMP_UHRM_INFO cu on c.AO_CODE = cu.UHRM_CODE ");
		sql.append("left join TBCRM_CUST_NOTE d on b.CUST_ID = d.CUST_ID ");
		sql.append("left join TBCRM_CUST_AGR_MKT_NOTE e on b.CUST_ID = e.CUST_ID ");
		sql.append("left join TBCRM_CUST_VISIT_RECORD f on b.CUST_MEMO_SEQ = f.VISIT_SEQ ");
		sql.append("left join TBORG_MEMBER g on b.EMP_ID = g.EMP_ID ");
		sql.append("left join TBORG_MEMBER g2 on b.CREATOR = g2.EMP_ID ");
		sql.append("left join TBORG_MEMBER g3 on b.MODIFIER = g3.EMP_ID ");
		sql.append("left join TBORG_DEFN h on c.BRA_NBR = h.DEPT_ID ");
		sql.append("left join TBORG_DEFN h2 on b.branch_ID = h2.DEPT_ID ");
		sql.append("where 1=1 ");

		// 20200602 ADD BY OCEAN : 0000166: WMS-CR-20200226-01_高端業管功能改採兼任分行角色調整相關功能_登入底層+行銷模組
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0) { // 非任何uhrm相關人員
			// 1.	以分行人員登入時，應僅含登入者建立的客戶訪談記錄。
			// 2.	以分行主管登入時，應僅含分行人員建立(排除高端人員)的客戶訪談記錄。
			// 3.	以總行人員登入時，應為全行客戶訪談記錄。
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {

			} else {
				sql.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = b.EMP_ID) ");
			}
			
			// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = b.AO_CODE) ");
			}
			//非總行一定要選分行，不用IN
			//總行不需要限制分行
			if (StringUtils.isNotBlank(inputVO.getbCode()) && StringUtils.equals("1", inputVO.getbCodeType())) {
				sql.append(" and c.BRA_NBR = :bcode ");
				queryCondition.setObject("bcode", inputVO.getbCode());
			} else if (StringUtils.isNotBlank(inputVO.getbCode()) && StringUtils.equals("2", inputVO.getbCodeType())) {
				sql.append(" and b.BRANCH_ID = :bcode ");
				queryCondition.setObject("bcode", inputVO.getbCode());
			}
		} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).equals("uhrm")) {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = b.EMP_ID AND UHRM.EMP_ID = :loginID) ");
			queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else {
			sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = b.EMP_ID) ");
		}

		if (StringUtils.isNotBlank(inputVO.getpCode())) {
			sql.append("and b.EMP_ID = :pcode ");
			queryCondition.setObject("pcode", inputVO.getpCode());
		}

		if (StringUtils.isNotBlank(inputVO.getId())) {
			sql.append("and b.CUST_ID = :id ");
			queryCondition.setObject("id", inputVO.getId());
		}

		if (StringUtils.isNotBlank(inputVO.getName())) {
			sql.append("and b.LEAD_NAME = :name ");
			queryCondition.setObject("name", inputVO.getName());
		}

		if (StringUtils.isNotBlank(inputVO.getFrom())) {
			switch (inputVO.getFrom()) {
				case "UNICA_MKT":
					sql.append("and a.LEAD_SOURCE_ID = '01' and a.LEAD_TYPE in ('01', '02') ");
					break;
				case "UNICA_INFO":
					sql.append("and a.LEAD_SOURCE_ID = '01' and a.LEAD_TYPE in ('05', '06', '07', '08', '09', 'H1', 'H2', 'F1', 'F2', 'I1', 'I2') ");
					break;
				case "USR":
					sql.append("and a.LEAD_SOURCE_ID = '05' ");
					break;
				case "OTHER_LEADS":
					sql.append("and SUBSTR(a.CAMPAIGN_ID, 9, 4) = 'TODO' ");
					break;
				default:
					sql.append("and a.CAMPAIGN_ID = :campaign_id ");
					queryCondition.setObject("campaign_id", inputVO.getFrom());
					break;
			}
		}

		if (StringUtils.isNotBlank(inputVO.getModifier())) {
			sql.append("and g.EMP_NAME = :mod ");
			queryCondition.setObject("mod", inputVO.getModifier());
		}

		if (StringUtils.isNotBlank(inputVO.getForce())) {
			sql.append("and f.VISITOR_ROLE IN (select roleid from tbsyssecurolpriass where privilegeid in (select param_code from TBSYSPARAMETER where PARAM_TYPE = 'CAM.CHANNEL_MAPPING' and param_name = :chan)) ");
			queryCondition.setObject("chan", inputVO.getForce());
		}

		if (inputVO.getsDate() != null) {
			sql.append("and b.CREATETIME >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}

		if (inputVO.geteDate() != null) {
			sql.append("and b.CREATETIME < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.geteDate());
		}

		if (inputVO.getsEDate() != null) {
			sql.append("and b.LASTUPDATE >= TRUNC(:estart) ");
			queryCondition.setObject("estart", inputVO.getsEDate());
		}

		if (inputVO.geteEDate() != null) {
			sql.append("and b.LASTUPDATE < TRUNC(:eend)+1 ");
			queryCondition.setObject("eend", inputVO.geteEDate());
		}

//		sql.append("order by LEAD_NAME,DEPT_NAME,EMP_ID,CUST_ID ");

		return sql;
	}
	
	public void download(Object body, IPrimitiveMap header) throws Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM180InputVO inputVO = (CAM180InputVO) body;
		CAM180OutputVO return_VO = new CAM180OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		//header
		String[] csvHeader = new String[] { "名單名稱", "名單內容/簡要說明", "歸屬行", "指定員編", "名單AOCode", "客戶AOCode", "目前UHRM", "客戶統一編號", "客戶姓名", "可否推介", "共銷註記", "投資註記", "名單來源", "名單狀態", "結案原因", "名單起日", "名單迄日", "聯繫方式", "通知客戶內容", "客戶回應內容", "聯繫日期", "名單維護者", "名單維護時間", "名單建立者", "名單建立時間" };

		String[] csvRecord = new String[] { "LEAD_NAME", "CAMPAIGN_DESC", "DEPT_NAME", "EMP_ID", "AO_CODE", "CAO_CODE", "CU_EMP_ID", "CUST_ID", "CUST_NAME", "REC_YN", "BASE_FLAG", "TRADE_FLAG", "FROM_FLAG", "RESPONSE_NAME", "COLSE_REASON", "START_DATE", "END_DATE", "CMU_TYPE", "VISIT_MEMO", "VISIT_CREPLY", "VISIT_DT", "MODIFIER", "LASTUPDATE", "CREATOR", "CREATETIME" };
		// TRADE_FLAG 含 COMM_RS_YN、COMM_NS_YN、PROF_INVESTOR_YN、SP_CUST_YN、SIGN_AGMT_YN … ETC
		// FROM_FLAG 含CUS_RECORD、UNICA_MKT、UNICA_INFO、USR、CAMPAIGN_ID … ETC
		// COLSE_REASON 為空值
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		queryCondition.setFetchSize(3000); //沒有用，不會真的限制3000筆資料
		String downloadCount = (String) xmlInfo.getVariable("CAM.180_MAX_DOWNLOAD_COUNT", "1", "F3");
		queryCondition.setMaxResults(Integer.parseInt(downloadCount)); //10000筆
		queryCondition.setQueryString(genSql(queryCondition, inputVO).toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			Map<String, String> visitCmuType = xmlInfo.doGetVariable("CAM.VST_REC_CMU_TYPE", FormatHelper.FORMAT_3);
			Map<String, String> source_id = xmlInfo.doGetVariable("CAM.CAM_CATEGORY", FormatHelper.FORMAT_3);
			Map<String, String> comm_yn = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			String fileName = "名單訪談紀錄下載清單_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";

			List<Object[]> csvData = new ArrayList<Object[]>();
			for (Map<String, Object> map : list) {
				String[] records = new String[csvHeader.length];

				// 2017/5/9 投資註記
				String tradeFlag = "";
				if ("Y".equals(ObjectUtils.toString(map.get("COMM_RS_YN"))))
					tradeFlag = "RS戶";
				else if ("Y".equals(ObjectUtils.toString(map.get("COMM_NS_YN"))))
					tradeFlag = "NS戶";
				else if ("Y".equals(ObjectUtils.toString(map.get("PROF_INVESTOR_YN"))))
					tradeFlag = "專業投資人";
				else if ("Y".equals(ObjectUtils.toString(map.get("SP_CUST_YN"))))
					tradeFlag = "特定客戶";
				else if ("Y".equals(ObjectUtils.toString(map.get("SIGN_AGMT_YN"))))
					tradeFlag = "有簽推介同意書";
				else
					tradeFlag = "未簽推介同意書";

				// 名單來源
				String leadSourse = "";
				if ("CUS_RECORD".equals(inputVO.getFrom())) {
					leadSourse = source_id.get(inputVO.getFrom());
				} else {
					switch (inputVO.getFrom()) {
						case "UNICA_MKT":
							leadSourse = source_id.get(inputVO.getFrom());
							break;
						case "UNICA_INFO":
							leadSourse = source_id.get(inputVO.getFrom());
							break;
						case "USR":
							leadSourse = source_id.get(inputVO.getFrom());
							break;
						default:
							if (StringUtils.isNotEmpty(inputVO.getFrom())) {
								leadSourse = source_id.get(inputVO.getFrom());
							}
							break;
					}
				}

				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvRecord[i]) {
					case "EMP_ID":
					case "AO_CODE":
					case "CAO_CODE":
					case "CUST_ID":
					case "MODIFIER":
					case "CREATOR":
					case "CU_EMP_ID":
						records[i] = "=\"" + checkIsNull(map, csvRecord[i]) + "\"";
						break;
					case "REC_YN":
					case "BASE_FLAG":
						records[i] = comm_yn.get(checkIsNull(map, csvRecord[i]));
						break;
					case "TRADE_FLAG":
						records[i] = tradeFlag;
						break;
					case "FROM_FLAG":
						records[i] = leadSourse;
						break;
					case "COLSE_REASON":
						records[i] = "";
						break;
					case "START_DATE":
					case "END_DATE":
					case "LASTUPDATE":
					case "CREATETIME":
						try {
							records[i] = "=\"" + new SimpleDateFormat("yyyy/MM/dd").format(map.get(csvRecord[i])) + "\"";
						} catch (Exception e) {
							records[i] = "";
						}
						break;
					case "VISIT_DT":
						try {
							if(map.get(csvRecord[i]) != null) {
								records[i] = "=\"" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(map.get(csvRecord[i])) + "\"";
							} else {
								records[i] = "";
							}
						} catch (Exception e) {
							records[i] = "";
							e.printStackTrace();
						}
						break;
					case "CMU_TYPE":
						records[i] = visitCmuType.get(checkIsNull(map, csvRecord[i]));
						break;
					default:
						records[i] = checkIsNull(map, csvRecord[i]);
					}
				}

				csvData.add(records);
			}

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(csvData);
			String url = csv.generateCSV();

			// download
			this.notifyClientToDownloadFile(url, fileName);
		} else
			return_VO.setResultList(list);

		this.sendRtnObject(return_VO);
	}

	private String checkIsNull(Map map, String key) {
		if (map.get(key) == null)
			return "";
		else if (StringUtils.isNotBlank(String.valueOf(map.get(key))))
			return String.valueOf(map.get(key));
		else
			return "";
	}

}