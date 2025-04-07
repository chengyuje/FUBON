package com.systex.jbranch.app.server.fps.ref110;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.systex.jbranch.app.server.fps.oth001.OTH001;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_LOAN_SALERECVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_LOAN_SALEREC_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_MD_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * REF110 新增轉介資料
 *
 * @author Ocean
 * @date 20160622
 * @spec
 */
@Component("ref110")
@Scope("request")
public class REF110 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(REF110.class);

	/*
	 * 取得受轉介人員資料
	 */
	public void queryUserProfile (Object body, IPrimitiveMap header) throws JBranchException {

		REF110InputVO inputVO = (REF110InputVO) body;
		REF110OutputVO outputVO = new REF110OutputVO();
		dam = this.getDataAccessManager();

		//判斷人員是否為理專
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT EMP_ID FROM TBORG_MEMBER_ROLE WHERE EMP_ID = :refEmpID AND ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID NOT IN ('002', '003')) ");
		queryCondition.setObject("refEmpID", inputVO.getRefEmpID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listNotFC = dam.exeQuery(queryCondition);

		//正式撈

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		String AO_str1 = "";
		String AO_str2 = "";

		if (listNotFC.size() > 0) {

		} else if ((StringUtils.isBlank(inputVO.getRefProd()) && StringUtils.isNotBlank(inputVO.getCustAoCode())) ||
				   (StringUtils.equals("5", inputVO.getRefProd()) && StringUtils.isNotBlank(inputVO.getCustAoCode()))) {
			AO_str1 = (", CASE WHEN AO.AO_CODE = :custAoCode THEN '1' ELSE '2' END SEQ ");
			AO_str2 = ("ORDER BY SEQ ");
			queryCondition.setObject("custAoCode", inputVO.getCustAoCode());
		}

		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM VWORG_DEPT_BR ");
		sb.append(") ");
		sb.append("SELECT M.EMP_ID, AO.AO_CODE, M.EMP_NAME, M.JOB_TITLE_NAME, MR.ROLE_ID, R.ROLE_NAME, PRIASS.PRIVILEGEID, ");
		sb.append("       RC.DEPT_ID AS REGION_CENTER_ID, RC.DEPT_NAME AS REGION_CENTER_NAME, OP.DEPT_ID AS BRANCH_AREA_ID, OP.DEPT_NAME AS BRANCH_AREA_NAME, BR.DEPT_ID AS BRANCH_NBR, BR.DEPT_NAME AS BRANCH_NAME ");
		sb.append(AO_str1);
		sb.append("FROM TBORG_MEMBER M ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON AO.EMP_ID = M.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON MR.EMP_ID = M.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("LEFT JOIN TBORG_ROLE R ON R.ROLE_ID = MR.ROLE_ID ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRIASS ON MR.ROLE_ID = PRIASS.ROLEID ");
		sb.append("LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '50') BR ON M.DEPT_ID = BR.DEPT_ID ");
		sb.append("LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '40') OP ON BR.PARENT_DEPT_ID = OP.DEPT_ID OR M.DEPT_ID = OP.DEPT_ID ");
		sb.append("LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '30') RC ON OP.PARENT_DEPT_ID = RC.DEPT_ID OR M.DEPT_ID = RC.DEPT_ID ");
		sb.append("WHERE PRIASS.PRIVILEGEID IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE LIKE 'CAM.REF_ROLE_%_DTL') ");
		sb.append("AND M.EMP_ID = :refEmpID ");
		sb.append(AO_str2);

		queryCondition.setObject("refEmpID", inputVO.getRefEmpID());

		logger.debug(sb.toString());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String[] roleArray = {"CAM.REF_ROLE_OPH_DTL", "CAM.REF_ROLE_FC_DTL", "CAM.REF_ROLE_PS_DTL", "", "", "CAM.REF_ROLE_PAO_DTL", "CAM.REF_ROLE_JRM_DTL", "CAM.REF_ROLE_NEWPS_DTL"};
		if (list.size() > 0) {
			//1:理專 / 2:消金PS / 3:新金PS / 4:商金RM / 0:作業主管(暫) / 5:個金AO / 6: JRM / 7: 新興PS
			StringBuffer roleSQL = new StringBuffer();
			roleSQL.append("SELECT PARAM_TYPE FROM TBSYSPARAMETER WHERE PARAM_TYPE = :paramType AND PARAM_CODE = :privilegeID ");
			Integer blur = 0;
			for (int i = 0; i < roleArray.length; i++) {
				if (StringUtils.isNotEmpty(roleArray[i])) {
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

					queryCondition.setQueryString(roleSQL.toString());
					queryCondition.setObject("paramType", roleArray[i]);
					queryCondition.setObject("privilegeID", list.get(0).get("PRIVILEGEID"));

					List<Map<String, Object>> listTemp = dam.exeQuery(queryCondition);
					if (listTemp.size() > 0)
						blur = i;
				}
			}

			outputVO.setRefEmpRoleName(String.valueOf(blur));
			outputVO.setRefEmpName((String) list.get(0).get("EMP_NAME"));
			outputVO.setRefEmpJobTitleName((String) list.get(0).get("JOB_TITLE_NAME"));
			outputVO.setRefEmpRegionID((String) list.get(0).get("REGION_CENTER_ID"));
			outputVO.setRefEmpBranchAreaID((String) list.get(0).get("BRANCH_AREA_ID"));
			outputVO.setRefEmpBranchID((String) list.get(0).get("BRANCH_NBR"));
			outputVO.setRefAoCode((String) list.get(0).get("AO_CODE"));
		} else {
			// 是否為UHRM
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT COUNT(1) AS COUNTS FROM VWORG_EMP_UHRM_INFO WHERE EMP_ID = :refEmpID ");

			queryCondition.setObject("refEmpID", inputVO.getRefEmpID());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> uhrmExists = dam.exeQuery(queryCondition);
			if (((BigDecimal) uhrmExists.get(0).get("COUNTS")).compareTo(BigDecimal.ZERO) == 1) {
				outputVO.setRefEmpRoleName("UHRM");
			} else {
				// 是否為銀證
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, BS_CODE AS AO_CODE, EMP_ID, EMP_NAME, JOB_TITLE_NAME ");
				sb.append("FROM VWORG_EMP_BS_INFO ");
				sb.append("WHERE EMP_ID = :refEmpID ");

				queryCondition.setObject("refEmpID", inputVO.getRefEmpID());
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> bsList = dam.exeQuery(queryCondition);
				if (bsList.size() > 0) {
					outputVO.setRefEmpRoleName(String.valueOf(1));
					outputVO.setRefEmpName((String) bsList.get(0).get("EMP_NAME"));
					outputVO.setRefEmpJobTitleName((String) bsList.get(0).get("JOB_TITLE_NAME"));
					outputVO.setRefEmpRegionID((String) bsList.get(0).get("REGION_CENTER_ID"));
					outputVO.setRefEmpBranchAreaID((String) bsList.get(0).get("BRANCH_AREA_ID"));
					outputVO.setRefEmpBranchID((String) bsList.get(0).get("BRANCH_NBR"));
					outputVO.setRefAoCode((String) bsList.get(0).get("AO_CODE"));
				}
			}
		}

		this.sendRtnObject(outputVO);
	}

	/*
	 * 取得轉介人資料
	 */
	public void querySaleProfile (Object body, IPrimitiveMap header) throws JBranchException {

		REF110InputVO inputVO = (REF110InputVO) body;
		REF110OutputVO outputVO = new REF110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("WITH RC_LIST AS ( ");
		sb.append("  SELECT P_M.PARAM_N, P_D.PARAM_CODE AS PRIVILEGEID, P_M.EMP_ROLE_CODE ");
		sb.append("  FROM ( ");
		sb.append("    SELECT PARAM_NAME AS PARAM_N, PARAM_CODE AS EMP_ROLE_CODE ");
		sb.append("    FROM TBSYSPARAMETER ");
		sb.append("    WHERE PARAM_TYPE = 'CAM.REF_ROLE_TYPE' ");
		sb.append("  ) P_M ");
		sb.append("  INNER JOIN TBSYSPARAMETER P_D ON P_M.PARAM_N = P_D.PARAM_TYPE ");
		sb.append(") ");
		sb.append("SELECT EMP_ID, CUST_ID, AO_CODE, EMP_NAME, JOB_TITLE_NAME, AO_JOB_RANK, ROLE_ID, ROLE_NAME, ");
		sb.append("       NVL((SELECT EMP_ROLE_CODE FROM RC_LIST RC WHERE RC.PRIVILEGEID = (SELECT PRI.PRIVILEGEID FROM TBSYSSECUROLPRIASS PRI WHERE PRI.ROLEID = INFO.ROLE_ID)), '7') AS ROLE_TYPE, ");
		sb.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME ");
		sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.EMP_ID = INFO.EMP_ID AND BS.ROLE_ID = 'BS02') ");
		sb.append("UNION ");
		sb.append("SELECT EMP_ID, CUST_ID, BS_CODE AS AO_CODE, EMP_NAME, JOB_TITLE_NAME, ROLE_NAME AS AO_JOB_RANK, ROLE_ID, ROLE_NAME, ");
		sb.append("       '2' AS ROLE_TYPE, ");
		sb.append("       REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME ");
		sb.append("FROM VWORG_EMP_BS_INFO INFO ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.EMP_ID = INFO.EMP_ID AND BS.ROLE_ID = 'BS02') ");
		sb.append("FETCH FIRST 1 ROWS ONLY ");

		queryCondition.setQueryString(sb.toString());

		queryCondition.setObject("empID", inputVO.getEmpID());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			//1:櫃員 / 2:理專 / 3:消金PS / 4:作業主管 / 5:個金主管 / 6:業務主管 / 7:其他 / 8:個金AO / 9:JRM / 10:新興PS
			outputVO.setEmpRoleName(list.get(0).get("ROLE_TYPE").toString());
			outputVO.setEmpName((String) list.get(0).get("EMP_NAME"));
			outputVO.setEmpJobTitleName((String) list.get(0).get("JOB_TITLE_NAME"));
			outputVO.setRegionID((String) list.get(0).get("REGION_CENTER_ID"));
			outputVO.setRegionName((String) list.get(0).get("REGION_CENTER_NAME"));
			outputVO.setBranchAreaID((String) list.get(0).get("BRANCH_AREA_ID"));
			outputVO.setBranchAreaName((String) list.get(0).get("BRANCH_AREA_NAME"));
			outputVO.setBranchID((String) list.get(0).get("BRANCH_NBR"));
			outputVO.setBranchName((String) list.get(0).get("BRANCH_NAME"));
		}

		this.sendRtnObject(outputVO);
	}

	/*
	 * 取得客戶資料
	 */
	public void queryCustProfile (Object body, IPrimitiveMap header) throws JBranchException {

		REF110InputVO inputVO = (REF110InputVO) body;
		REF110OutputVO outputVO = new REF110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CUST.CUST_ID, CUST.CUST_NAME, CUST.BRA_NBR, CUST.AO_CODE, MEM.EMP_ID, ");
		
		if (StringUtils.isNotEmpty(inputVO.getBranchID())) {
			sb.append("       (SELECT A.EMP_ID ");
			sb.append("        FROM ( ");
			sb.append("          SELECT * ");
			sb.append("          FROM TBCRM_WKPG_BY_EMPS_INFO ");
			sb.append("          WHERE PRIVILEGEID = 'JRM' ");
			sb.append("          AND BRANCH_NBR = :branchNbr ");
			sb.append("          ORDER BY CEIL(DBMS_RANDOM.VALUE * 1000) ");
			sb.append("        ) A ");
			sb.append("        WHERE ROWNUM = 1 ) AS JRM_EMP_ID, ");
			
			queryCondition.setObject("branchNbr", inputVO.getBranchID());
		} else {
			sb.append("       '' AS JRM_EMP_ID, ");
		}
		
		sb.append("       (SELECT CASE WHEN COUNT(1) > 0 THEN 'Y' ELSE 'N' END AS TEMP ");
		sb.append("        FROM TBCRM_CUST_AOCODE_CHGLOG ");
		sb.append("        WHERE ORG_AO_CODE IS NULL ");
		sb.append("        AND CUST_ID = CUST.CUST_ID ");
		sb.append("        AND TO_CHAR(LETGO_DATETIME, 'yyyyMMdd') = TO_CHAR(SYSDATE, 'yyyyMMdd')) AS YESTERDAY_NOAO, ");
		
		sb.append("       (SELECT INS_RECORD_YN FROM TBCRM_CUST_NOTE WHERE CUST_ID = CUST.CUST_ID) AS REC_YN, ");
		sb.append("       (SELECT CASE WHEN COUNT(1) > 0 THEN 'Y' ELSE 'N' END AS UHRM_YN FROM VWORG_EMP_UHRM_INFO WHERE UHRM_CODE = CUST.AO_CODE) AS UHRM_YN, ");
		
		sb.append("       CASE WHEN MEM.TYPE = '2' THEN 'Y' ELSE 'N' END AS VICE_CUST, ");		// 副CODE客戶
		sb.append("       CASE WHEN MEM.TYPE = '3' THEN 'Y' ELSE 'N' END AS MAINTAIN_CUST, ");	// 維護CODE客戶
		sb.append("       CASE WHEN CUST.AO_CODE IS NULL THEN 'Y' ELSE 'N' END AS MASS_CUST, ");// 空CODE客戶
		sb.append("       CASE WHEN PLIST_T.ORG_AO_TYPE NOT IN ('1', '2') AND MEM.TYPE IN ('1', '2') AND PLIST_T.TODAY_FLAG = 'Y' THEN 'Y' "); 	// 轉介客戶前次非主CODE/副CODE + 本次為主CODE客戶/副CODE客戶 + 當日登CODE = 可轉介
		sb.append("            WHEN MEM.TYPE IN ('1', '2') AND PLIST_T.TODAY_FLAG = 'N' AND (CASE WHEN AN.CUST_ID IS NULL THEN 'N' ELSE 'Y' END) = 'Y' THEN 'N'"); 	// 轉介客戶為主CODE客戶/副CODE客戶 + 非當日登CODE + 有投保實動 = 不可轉介
		sb.append("       ELSE 'Y' END AS AO_TYPE ");
		
		if (StringUtils.isNotBlank(inputVO.getRefProd())) {
			// 2017/11/27 test
			sb.append("       , ");
			sb.append("       (SELECT CASE WHEN COUNT(1) > 0 THEN 'Y' ELSE 'N' END AS TEMP ");
			sb.append("        FROM ( ");
			sb.append("          SELECT CUST_ID ");
			sb.append("          FROM TBCAM_LOAN_SALEREC ");
			sb.append("          WHERE CUST_ID = CUST.CUST_ID ");
			sb.append("          AND REF_PROD = :refProd ");
			sb.append("          AND TO_CHAR(SYSDATE, 'yyyyMMdd') BETWEEN TO_CHAR(TXN_DATE, 'yyyyMMdd') AND TO_CHAR(LAST_DAY(ADD_MONTHS(TXN_DATE, 3)), 'yyyyMMdd') ");
			sb.append("          UNION ");
			sb.append("          SELECT CUST_ID ");
			sb.append("          FROM TBCAM_LOAN_SALEREC_REVIEW ");
			sb.append("          WHERE CUST_ID = CUST.CUST_ID ");
			sb.append("          AND REF_PROD = :refProd ");
			sb.append("          AND STATUS != 'C' ");
			sb.append("          AND TO_CHAR(SYSDATE, 'yyyyMMdd') BETWEEN TO_CHAR(TXN_DATE, 'yyyyMMdd') AND TO_CHAR(LAST_DAY(ADD_MONTHS(TXN_DATE, 3)), 'yyyyMMdd') ");
			sb.append("        ) ");
			sb.append("       ) AS SALEREC_COUNTS ");
			
			queryCondition.setObject("refProd", inputVO.getRefProd());
		}

		sb.append("FROM TBCRM_CUST_MAST CUST ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE MEM ON CUST.AO_CODE = MEM.AO_CODE ");
		sb.append("LEFT JOIN TBPMS_CUST_ACTUAL AN ON AN.YYYYMM = (SELECT MAX(YYYYMM) FROM TBPMS_CUST_ACTUAL) AND AN.CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT CHG.CUST_ID, CHG.ORG_AO_CODE, AO.TYPE AS ORG_AO_TYPE, CHG.NEW_AO_CODE, TRUNC(CHG.LETGO_DATETIME) AS ACT_DATE, CASE WHEN TRUNC(CHG.LETGO_DATETIME) = TRUNC(SYSDATE) THEN 'Y' ELSE 'N' END AS TODAY_FLAG ");
		sb.append("  FROM TBCRM_CUST_AOCODE_CHGLOG CHG ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON CHG.ORG_AO_CODE = AO.AO_CODE ");
		sb.append("  WHERE (CHG.CUST_ID, TRUNC(CHG.LETGO_DATETIME)) IN ( ");
		sb.append("    SELECT CUST_ID, MAX(TRUNC(LETGO_DATETIME)) ");
		sb.append("    FROM TBCRM_CUST_AOCODE_CHGLOG ");
		sb.append("    GROUP BY CUST_ID ");
		sb.append("  ) ");
		sb.append(") PLIST_T ON CUST.CUST_ID = PLIST_T.CUST_ID AND CUST.AO_CODE = PLIST_T.NEW_AO_CODE ");
		
		sb.append("WHERE CUST.CUST_ID = :custID ");
		
		queryCondition.setObject("custID", inputVO.getCustID());

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			outputVO.setCustID((String) list.get(0).get("CUST_ID"));
			outputVO.setCustName((String) list.get(0).get("CUST_NAME"));
			outputVO.setCustBraNbr((String) list.get(0).get("BRA_NBR"));
			outputVO.setYesterdayNoAO((list.get(0).get("YESTERDAY_NOAO")).toString());
			outputVO.setRecYN((String) list.get(0).get("REC_YN"));
			
			if (StringUtils.isNotBlank(inputVO.getRefProd())) {
				outputVO.setSalerecCounts((list.get(0).get("SALEREC_COUNTS")).toString());
			}
			
			outputVO.setCustAoCode((String) list.get(0).get("AO_CODE"));
			outputVO.setRefEmpID((String) list.get(0).get("EMP_ID"));
			
			outputVO.setRefJRMEmpID((String) list.get(0).get("JRM_EMP_ID"));
			outputVO.setUhrmYN((list.get(0).get("UHRM_YN")).toString());
			
			outputVO.setCustAoType((list.get(0).get("AO_TYPE")).toString());
			outputVO.setMaintainCust((list.get(0).get("MAINTAIN_CUST")).toString());
			outputVO.setMassCust((list.get(0).get("MASS_CUST")).toString());
			outputVO.setViceCust((list.get(0).get("VICE_CUST")).toString());
		}
		
		this.sendRtnObject(outputVO);
	}

	/*
	 * 新增轉介
	 */
	public void addRef (Object body, IPrimitiveMap header) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);

		REF110InputVO inputVO = (REF110InputVO) body;
		REF110OutputVO outputVO = new REF110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT CUST_ID, MIN_OPN, MAX_OPN ");
		sb.append("FROM ( ");
		sb.append("  SELECT CUST_ID, MIN(OPN_DATE) AS MIN_OPN, MAX(OPN_DATE) AS MAX_OPN ");
		sb.append("  FROM TBPMS_F_WM_DEPOSIT_ACCT_M ");
		sb.append("  WHERE SNAP_YYYYMM = SUBSTR(TO_CHAR(ADD_MONTHS(SYSDATE, -1), 'yyyyMMdd'), 1, 6) ");
		sb.append("  AND PARTY_CLASS IN ('A','C') ");
		sb.append("  AND ACT_STS IN('0','1') ");
		sb.append("  GROUP BY CUST_ID ");
		sb.append(") ");
		sb.append("WHERE TRUNC(MIN_OPN) < TRUNC(:txnDate) ");
		sb.append("AND CUST_ID = :custID ");

		queryCondition.setObject("txnDate", new Timestamp(System.currentTimeMillis()));
		queryCondition.setObject("custID", inputVO.getCustID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> newCustList = dam.exeQuery(queryCondition);

		// 2017/11/14 change
		TBCAM_LOAN_SALEREC_REVIEWVO vo = new TBCAM_LOAN_SALEREC_REVIEWVO();
		vo.setSEQ(inputVO.getSeq());
		vo.setTERRITORY_ID(inputVO.getBranchAreaID()); //鍵機單位
		vo.setTXN_DATE(new Timestamp(System.currentTimeMillis()));
		vo.setREF_ORG_ID(inputVO.getBranchID());
		vo.setCUST_ID(inputVO.getCustID());
		vo.setCUST_NAME(inputVO.getCustName());
		vo.setSALES_PERSON(inputVO.getEmpID());
		vo.setSALES_NAME(inputVO.getEmpName());
		vo.setREF_PROD(inputVO.getRefProd());
		vo.setCASE_PROPERTY("1");
		vo.setSALES_ROLE(inputVO.getEmpRoleName());
		vo.setUSERID(inputVO.getRefEmpID());
		vo.setUSERNAME(inputVO.getRefEmpName());
		vo.setUSERROLE(inputVO.getRefEmpRoleName());
		vo.setNEW_CUST_FLAG(newCustList.size() > 0 ? "N" : "Y");
		vo.setSTATUS("W");
		dam.create(vo);
		//

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT M.EMP_ID, M.EMP_EMAIL_ADDRESS, PRI.PRIVILEGEID ");
		sb.append("FROM TBORG_MEMBER M ");
		sb.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
		sb.append("WHERE M.EMP_ID = :empID ");
		queryCondition.setObject("empID", inputVO.getRefEmpID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> empList = dam.exeQuery(queryCondition);
		if (empList.size() == 0)
			throw new APException("受轉介人沒有設定權限群組");

		//內部事件通知
		TBCRM_WKPG_MD_MASTVO msvo = new TBCRM_WKPG_MD_MASTVO();
		msvo.setSEQ(getSeqNum("TBCRM_WKPG_MD_MAST"));
		msvo.setPRIVILEGEID((String) empList.get(0).get("PRIVILEGEID"));
		msvo.setEMP_ID((String) empList.get(0).get("EMP_ID"));
		msvo.setROLE_LINK_YN("Y");
		msvo.setFRQ_TYPE("D");
		msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
		msvo.setDISPLAY_NO("104");
		msvo.setCLICK_YN("N");
		msvo.setCLICK_DATE(new Timestamp(System.currentTimeMillis()));
		msvo.setRPT_NAME("是否接受轉介案件-" + inputVO.getSeq());
		msvo.setRPT_PROG_URL("REF900");
		msvo.setPASS_PARAMS(inputVO.getSeq());
		msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));
		dam.create(msvo);


		// mail，平測時不需要 Mail SMTP
		if (!PlatformContext.getBean(OTH001.class).skipRefMail()) {
			String mail1 = ObjectUtils.toString(empList.get(0).get("EMP_EMAIL_ADDRESS"));
			if(StringUtils.isBlank(mail1))
				outputVO.setErrorMsg("受轉介人無E-mail");
			else if(isEmail(ObjectUtils.toString(mail1)) == false)
				outputVO.setErrorMsg("受轉介人Email格式錯誤");
			else {
				List<Map<String, String>> mailList = new ArrayList<Map<String,String>>();
				Map<String, String> mailMap = new HashMap<String, String>();
				mailMap.put(FubonSendJavaMail.MAIL, mail1);
				mailList.add(mailMap);
	
				QueryConditionIF salecon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				salecon.setQueryString("select BRANCH_NBR, BRANCH_NAME from VWORG_DEFN_INFO where BRANCH_NBR = :bra_nbr ");
				salecon.setObject("bra_nbr", inputVO.getBranchID());
				List<Map<String, String>> sale_data = dam.exeQuery(salecon);
				String BRANCH_NAME = sale_data.size() > 0 ? sale_data.get(0).get("BRANCH_NAME") : "";
	
				FubonSendJavaMail sendMail = new FubonSendJavaMail();
				FubonMail mail = new FubonMail();
				Map<String, Object> annexData = new HashMap<String, Object>();
				mail.setLstMailTo(mailList);
				//設定信件主旨
				mail.setSubject("個金分行業務管理系統轉介客戶通知");
				//設定信件內容
				String content = "<table border=\"1\" style=\"text-align:center;\">";
				content += "<tr><td>案件編號</td>";
				content += "<td>轉介日期</td>";
				content += "<td>轉介人分行</td>";
				content += "<td>轉介人員編</td>";
				content += "<td>轉介人姓名</td></tr>";
				content += "<tr><td>" + inputVO.getSeq() + "</td>";
				content += "<td>" + new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Timestamp(System.currentTimeMillis())) + "</td>";
				content += "<td>" + inputVO.getBranchID() + "-" + BRANCH_NAME + "</td>";
				content += "<td>" + inputVO.getEmpID() + "</td>";
				content += "<td>" + inputVO.getEmpName() + "</td></tr>";
				content += "<tr><td>客戶姓名</td>";
				content += "<td>轉介商品</td>";
				content += "<td>受轉介人員編</td>";
				content += "<td>受轉介人姓名</td>";
				content += "<td>回覆狀態</td></tr>";
				content += "<tr><td>" + inputVO.getCustName() + "</td>";
				XmlInfo xmlInfo = new XmlInfo();
				Map<String, String> ref_prod = xmlInfo.doGetVariable("CAM.REF_PROD", FormatHelper.FORMAT_3);
				Map<String, String> ref_status = xmlInfo.doGetVariable("CAM.REF_STATUS", FormatHelper.FORMAT_3);
				content += "<td>" + ref_prod.get(inputVO.getRefProd()) + "</td>";
				content += "<td>" + inputVO.getRefEmpID() + "</td>";
				content += "<td>" + inputVO.getRefEmpName() + "</td>";
				content += "<td>" + ref_status.get("W") + "</td></tr></table>";
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyy/MM/dd");
				Calendar calendar2 = Calendar.getInstance();
				calendar2.setTimeInMillis(getBusiDay().getTime());
				calendar2.add(Calendar.YEAR, -1911);
				content += "<br>您有一筆轉介案件，提醒您請至個金分行業務管理系統確認是否接受該筆轉介，若" + sdf2.format(calendar2.getTime()) + "前未進行回覆";
				content += "，視為同意接受該筆轉介。";
				mail.setContent(content);
//				sendMail.sendMail(mail,annexData);
			}
		}

		this.sendRtnObject(outputVO);
	}

	/*
	 * 信箱Email格式檢查
	 */
	public static boolean isEmail(String email) {
		Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find())
			return true;
		return false;
	}

	/*
	 * 取得工作日
	 */
	public Timestamp getBusiDay() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT PABTH_UTIL.FC_getBusiDay(TRUNC(sysdate), 'TWD', 5) AS TXN_DATE FROM DUAL");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return (Timestamp) list.get(0).get("TXN_DATE");
	}

	/*
	 * 取得轉介明細資料(UPDATE)
	 */
	public void getDtl (Object body, IPrimitiveMap header) throws JBranchException {

		REF110InputVO inputVO = (REF110InputVO) body;
		REF110OutputVO outputVO = new REF110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SALEREC.SEQ, SALEREC.TXN_DATE, ");
		sb.append("       SALEREC.SALES_PERSON, SALEREC.SALES_ROLE, ");
		sb.append("       SALEREC.CUST_ID, SALEREC.CUST_NAME, SALEREC.REF_PROD, ");
		sb.append("       SALEREC.USERID, SALEREC.USERNAME, SALEREC.USERROLE, ");
		sb.append("       SALEREC.REF_ORG_ID, SALEREC.CONT_RSLT, SALEREC.NON_GRANT_REASON, SALEREC.COMMENTS, CM.AO_CODE ");
		sb.append("FROM TBCAM_LOAN_SALEREC SALEREC ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CM ON CM.CUST_ID = SALEREC.CUST_ID ");
		sb.append("WHERE SEQ = :seq ");

		queryCondition.setQueryString(sb.toString());

		queryCondition.setObject("seq", inputVO.getSeq());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (list.size() > 0) {
			outputVO.setDataDate(new SimpleDateFormat("yyyy-MM-dd").format((Timestamp) list.get(0).get("TXN_DATE")));
			outputVO.setEmpID((String) list.get(0).get("SALES_PERSON"));
			outputVO.setCustID((String) list.get(0).get("CUST_ID"));
			outputVO.setCustName((String) list.get(0).get("CUST_NAME"));
			outputVO.setAoCode((String) list.get(0).get("AO_CODE"));
			outputVO.setRefProd((String) list.get(0).get("REF_PROD"));
			outputVO.setRefEmpID((String) list.get(0).get("USERID"));
			outputVO.setRefEmpName((String) list.get(0).get("USERNAME"));
			outputVO.setBranchID((String) list.get(0).get("REF_ORG_ID"));
			outputVO.setSalerecRefEmpRoleName((String) list.get(0).get("USERROLE"));
			outputVO.setContRslt((String) list.get(0).get("CONT_RSLT"));
			outputVO.setNonGrantReason((String) list.get(0).get("NON_GRANT_REASON"));
			outputVO.setComments((String) list.get(0).get("COMMENTS"));
		}

		this.sendRtnObject(outputVO);
	}

	/*
	 * 更新轉介
	 */
	public void updRef (Object body, IPrimitiveMap header) throws JBranchException {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);

		REF110InputVO inputVO = (REF110InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		TBCAM_LOAN_SALERECVO vo = new TBCAM_LOAN_SALERECVO();
		vo = (TBCAM_LOAN_SALERECVO) dam.findByPKey(TBCAM_LOAN_SALERECVO.TABLE_UID, inputVO.getSeq());

		if (!StringUtils.equals(vo.getUSERID(), inputVO.getRefEmpID())) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT M.EMP_ID, PRI.PRIVILEGEID ");
			sb.append("FROM TBORG_MEMBER M ");
			sb.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
			sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
			sb.append("WHERE M.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getRefEmpID());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> empList = dam.exeQuery(queryCondition);

			//內部事件通知
			if (empList.size() > 0) {
				TBCRM_WKPG_MD_MASTVO msvo = new TBCRM_WKPG_MD_MASTVO();
				msvo.setSEQ(getSeqNum("TBCRM_WKPG_MD_MAST"));
				msvo.setPRIVILEGEID((String) empList.get(0).get("PRIVILEGEID"));
				msvo.setEMP_ID((String) empList.get(0).get("EMP_ID"));
				msvo.setROLE_LINK_YN("N");
				msvo.setFRQ_TYPE("D");
				msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
				msvo.setDISPLAY_NO("103");
				msvo.setCLICK_YN("N");
				msvo.setRPT_NAME("待回報轉介案件-" + vo.getSEQ());
				msvo.setRPT_PROG_URL("REF120");
				msvo.setPASS_PARAMS(null);
				msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));

				dam.create(msvo);
			}
		}

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT CUST_ID, MIN_OPN, MAX_OPN ");
		sb.append("FROM ( ");
		sb.append("  SELECT CUST_ID, MIN(OPN_DATE) AS MIN_OPN, MAX(OPN_DATE) AS MAX_OPN ");
		sb.append("  FROM TBPMS_F_WM_DEPOSIT_ACCT_M ");
		sb.append("  WHERE SNAP_YYYYMM = SUBSTR(TO_CHAR(ADD_MONTHS(SYSDATE, -1), 'yyyyMMdd'), 1, 6) ");
		sb.append("  AND PARTY_CLASS IN ('A','C') ");
		sb.append("  AND ACT_STS IN('0','1') ");
		sb.append("  GROUP BY CUST_ID ");
		sb.append(") ");
		sb.append("WHERE TRUNC(MIN_OPN) < TRUNC(:txnDate) ");
		sb.append("AND CUST_ID = :custID ");

		queryCondition.setObject("txnDate", vo.getTXN_DATE());
		queryCondition.setObject("custID", inputVO.getCustID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> newCustList = dam.exeQuery(queryCondition);

		//轉介案件更新
		vo.setCUST_ID(inputVO.getCustID());
		vo.setCUST_NAME(inputVO.getCustName());
		vo.setUSERID(inputVO.getRefEmpID());
		vo.setUSERNAME(inputVO.getRefEmpName());
		vo.setUSERROLE(inputVO.getRefEmpRoleName());

		if (StringUtils.isNotBlank(inputVO.getContRslt())) {
			vo.setCONT_RSLT(inputVO.getContRslt());
		}

		if (StringUtils.isNotBlank(inputVO.getNonGrantReason())) {
			vo.setNON_GRANT_REASON(inputVO.getNonGrantReason());
		}

		if (StringUtils.isNotBlank(inputVO.getComments())) {
			vo.setCOMMENTS(inputVO.getComments());
		}

		vo.setREF_PROD(inputVO.getRefProd());
		vo.setNEW_CUST_FLAG(newCustList.size() > 0 ? "N" : "Y");

		dam.update(vo);

		this.sendRtnObject(null);
	}

	/*
	 * 初始頁面設定
	 */
	public void initial (Object body, IPrimitiveMap header) throws JBranchException {
		REF110OutputVO outputVO = new REF110OutputVO();
		dam = this.getDataAccessManager();

		// 序號命名規則：系統日字串(YYYYMMDD)+下一序號(00000~99999)
		// 2018/6/6 StringUtils.right(a, 5)
		outputVO.setSeq(new SimpleDateFormat("yyyyMMdd").format(new Date()) + StringUtils.leftPad(StringUtils.right(getSeqNum("TBCAM_LOAN_SALEREC_SEQ"), 5), 5, "0"));
		outputVO.setDataDate(new SimpleDateFormat("yyyy-MM-dd").format(new Timestamp(System.currentTimeMillis())));

		this.sendRtnObject(outputVO);
	}

	/*
	 * GET SEQNO
	 */
	private String getSeqNum(String TXN_ID) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch(TXN_ID) {
			case "TBCRM_WKPG_MD_MAST":
				sql.append("SELECT SQ_TBCRM_WKPG_MD_MAST.nextval AS SEQ FROM DUAL ");
				break;
			case "TBCAM_LOAN_SALEREC_SEQ":
				sql.append("SELECT SQ_TBCAM_LOAN_SALEREC_SEQ.nextval AS SEQ FROM DUAL ");
				break;
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

}
