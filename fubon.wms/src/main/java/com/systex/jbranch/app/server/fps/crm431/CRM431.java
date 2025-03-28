package com.systex.jbranch.app.server.fps.crm431;

import com.ibm.icu.util.Calendar;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPLY_PERIODVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPLY_SINGLEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPROVAL_HISTORYVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421;
import com.systex.jbranch.app.server.fps.crm421.CRM421InputVO;
import com.systex.jbranch.app.server.fps.crm451.CRM451;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709;
import com.systex.jbranch.app.server.fps.sot709.SOT709InputVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709OutputVO;
import com.systex.jbranch.app.server.fps.sot710.SOT710;
import com.systex.jbranch.app.server.fps.sot710.SOT710InputVO;
import com.systex.jbranch.app.server.fps.sot710.SOT710OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MENU
 * 
 * @author Stella
 * @date 2016/10/03
 * @spec null
 */
@Component("crm431")
@Scope("request")
public class CRM431 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM431.class);
	private static final String APPLY_STATUS_REVIEW = "1"; //待覆核
	private static final String APPLY_STATUS_AUTH = "2"; //已授權
	private static final String APPLY_STATUS_TERMINATE = "3"; //已終止
	private static final String APPLY_STATUS_REJECT = "9"; //已退回

	/*
	 * 查詢
	 * 
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM431InputVO inputVO = (CRM431InputVO) body;
		CRM431OutputVO return_VO = new CRM431OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		//		sql.append("WITH BASE AS ( ");
		//		sql.append("  SELECT A.AO_CODE, A.EMP_ID, A.EMP_NAME ");
		//		sql.append("  FROM VWORG_AO_INFO A ");
		//		sql.append("  WHERE A.BRA_NBR IN ( :brList ) ");
		//		sql.append(") ");
		//		queryCondition.setObject("brList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));

		//Data1: 理規系統內的期間議價資料 (基金)
		sql.append("SELECT A.CREATOR, A.APPLY_SEQ, A.CUST_ID, C.CUST_NAME, '1' AS APPLY_CAT, A.APPLY_TYPE, ");
		sql.append("       (CASE A.APPLY_TYPE WHEN '1' THEN '基金' WHEN '2' THEN '海外ETF/股票' END ) AS PROD_CAT, '' AS PROD_ID, ");
		sql.append("       (CASE WHEN A.DMT_STOCK IS NOT NULL THEN '國內股票型折數'|| ");
		sql.append("       (CASE WHEN A.DMT_STOCK < 1 THEN TO_CHAR(A.DMT_STOCK,'0.99') ELSE TO_CHAR(A.DMT_STOCK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.DMT_BOND IS NOT NULL THEN '國內債券型折數'|| ");
		sql.append("       (CASE WHEN A.DMT_BOND < 1 THEN TO_CHAR(A.DMT_BOND,'0.99') ELSE TO_CHAR(A.DMT_BOND) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.DMT_BALANCED IS NOT NULL THEN '國內平衡型折數'|| ");
		sql.append("       (CASE WHEN A.DMT_BALANCED < 1 THEN TO_CHAR(A.DMT_BALANCED,'0.99') ELSE TO_CHAR(A.DMT_BALANCED) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.FRN_STOCK IS NOT NULL THEN '國外股票型折數'|| ");
		sql.append("       (CASE WHEN A.FRN_STOCK < 1 THEN TO_CHAR(A.FRN_STOCK,'0.99') ELSE TO_CHAR(A.FRN_STOCK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.FRN_BOND IS NOT NULL THEN '國外債券型折數'|| ");
		sql.append("       (CASE WHEN A.FRN_BOND < 1 THEN TO_CHAR(A.FRN_BOND,'0.99') ELSE TO_CHAR(A.FRN_BOND) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.FRN_BALANCED IS NOT NULL THEN '國外平衡型折數'|| ");
		sql.append("       (CASE WHEN A.FRN_BALANCED < 1 THEN TO_CHAR(A.FRN_BALANCED,'0.99') ELSE TO_CHAR(A.FRN_BALANCED) END) || '折\n' END ");
		sql.append("       ) AS PROD_NAME, ");
		sql.append("       0 AS DISCOUNT, A.AUTH_STATUS, A.APPLY_STATUS, A.MGR_EMP_ID_1, A.MGR_EMP_ID_2, A.MGR_EMP_ID_3, A.MGR_EMP_ID_4, A.APPLY_DATE, ");
		sql.append("       A.APPLE_SETUP_TYPE, A.C_BRANCH_NBR ");
		sql.append("FROM TBCRM_BRG_APPLY_PERIOD A, VWCRM_CUST_INFO C ");
		sql.append("WHERE A.CUST_ID = C.CUST_ID ");
		sql.append("AND A.APPLY_TYPE = '1' ");
		//		sql.append("AND A.CREATOR IN (SELECT EMP_ID FROM BASE) ");
		//		sql.append("AND A.APPLY_STATUS NOT IN ('V', '0', '2', '3', '5') ");

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("AND A.CUST_ID = :cust ");
			queryCondition.setObject("cust", inputVO.getCust_id());
		}
		//		if(inputVO.getStatusList().size()>0){
		//			sql.append("AND A.APPLY_STATUS IN ( :statusLst ) ");
		//			queryCondition.setObject("statusLst", inputVO.getStatusList());
		//		}
		if (StringUtils.isNotBlank(inputVO.getApply_status())) {
			if (StringUtils.equals(APPLY_STATUS_REVIEW, inputVO.getApply_status())) {
				sql.append("AND A.APPLY_STATUS IN ('1', '4') ");
				sql.append("AND ( ");
				sql.append("(INSTR(A.MGR_EMP_ID_1, :emp_id ) > 0 AND AUTH_STATUS = '0') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_2, :emp_id ) > 0 AND AUTH_STATUS = '1') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_3, :emp_id ) > 0 AND AUTH_STATUS = '2') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_4, :emp_id ) > 0 AND AUTH_STATUS = '3') ");
				sql.append(") ");
			} else {
				if (StringUtils.equals(APPLY_STATUS_AUTH, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '2' ");
				} else if (StringUtils.equals(APPLY_STATUS_TERMINATE, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '3' ");
				} else if (StringUtils.equals(APPLY_STATUS_REJECT, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '9' ");
				}
				sql.append("AND ( ");
				sql.append("(INSTR(A.MGR_EMP_ID_1, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_2, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_3, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_4, :emp_id ) > 0) ");
				sql.append(") ");
			}
		}
		if (inputVO.getAuth_date_bgn() != null) {
			sql.append("AND TRUNC (A.LASTUPDATE) >= TRUNC ( :bgn ) ");
			queryCondition.setObject("bgn", inputVO.getAuth_date_bgn());
		}
		if (inputVO.getAuth_date_end() != null) {
			sql.append("AND TRUNC (A.LASTUPDATE) <= TRUNC ( :end ) ");
			queryCondition.setObject("end", inputVO.getAuth_date_end());
		}
		sql.append("UNION ");

		//Data1: 理規系統內的期間議價資料 (ETF/股票)
		sql.append("SELECT A.CREATOR, A.APPLY_SEQ, A.CUST_ID, C.CUST_NAME, '1' AS APPLY_CAT, A.APPLY_TYPE, ");
		sql.append("       (CASE A.APPLY_TYPE WHEN '1' THEN '基金' WHEN '2' THEN '海外ETF/股票' END ) AS PROD_CAT, '' AS PROD_ID, ");
		sql.append("       (CASE WHEN A.BUY_HK_MRK IS NOT NULL THEN '買入香港市場折數' || ");
		sql.append("       (CASE WHEN A.BUY_HK_MRK < 1 THEN TO_CHAR(A.BUY_HK_MRK,'0.99') ELSE TO_CHAR(A.BUY_HK_MRK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.BUY_US_MRK IS NOT NULL THEN '買入美國市場折數'|| ");
		sql.append("       (CASE WHEN A.BUY_US_MRK < 1 THEN TO_CHAR(A.BUY_US_MRK,'0.99') ELSE TO_CHAR(A.BUY_US_MRK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.BUY_UK_MRK IS NOT NULL THEN '買入英國市場折數'|| ");
		sql.append("       (CASE WHEN A.BUY_UK_MRK < 1 THEN TO_CHAR(A.BUY_UK_MRK,'0.99') ELSE TO_CHAR(A.BUY_UK_MRK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.BUY_JP_MRK IS NOT NULL THEN '買入日本市場折數'|| ");
		sql.append("       (CASE WHEN A.BUY_JP_MRK < 1 THEN TO_CHAR(A.BUY_JP_MRK,'0.99') ELSE TO_CHAR(A.BUY_JP_MRK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.SELL_HK_MRK IS NOT NULL THEN '賣出香港市場折數'|| ");
		sql.append("       (CASE WHEN A.SELL_HK_MRK < 1 THEN TO_CHAR(A.SELL_HK_MRK,'0.99') ELSE TO_CHAR(A.SELL_HK_MRK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.SELL_US_MRK IS NOT NULL THEN '賣出美國市場折數'|| ");
		sql.append("       (CASE WHEN A.SELL_US_MRK < 1 THEN TO_CHAR(A.SELL_US_MRK,'0.99') ELSE TO_CHAR(A.SELL_US_MRK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.SELL_UK_MRK IS NOT NULL THEN '賣出英國市場折數'|| ");
		sql.append("       (CASE WHEN A.SELL_UK_MRK < 1 THEN TO_CHAR(A.SELL_UK_MRK,'0.99') ELSE TO_CHAR(A.SELL_UK_MRK) END) || '折\n' END) || ");
		sql.append("       (CASE WHEN A.SELL_JP_MRK IS NOT NULL THEN '賣出日本市場折數'|| ");
		sql.append("       (CASE WHEN A.SELL_JP_MRK < 1 THEN TO_CHAR(A.SELL_JP_MRK,'0.99') ELSE TO_CHAR(A.SELL_JP_MRK) END) || '折\n' END ");
		sql.append("       ) AS PROD_NAME, ");
		sql.append("       0 AS DISCOUNT, A.AUTH_STATUS, A.APPLY_STATUS, A.MGR_EMP_ID_1, A.MGR_EMP_ID_2, A.MGR_EMP_ID_3, A.MGR_EMP_ID_4, A.APPLY_DATE, ");
		sql.append("       A.APPLE_SETUP_TYPE, A.C_BRANCH_NBR ");
		sql.append("FROM TBCRM_BRG_APPLY_PERIOD A, VWCRM_CUST_INFO C ");
		sql.append("WHERE A.CUST_ID = C.CUST_ID ");
		sql.append("AND A.APPLY_TYPE = '2' ");
		//		sql.append("AND A.CREATOR IN (SELECT EMP_ID FROM BASE) ");
		//		sql.append("AND A.APPLY_STATUS NOT IN ('V', '0', '2', '3', '5') ");
		//		sql.append("AND ( ");
		//		sql.append("(INSTR(A.MGR_EMP_ID_1, :emp_id ) > 0) OR ");
		//		sql.append("(INSTR(A.MGR_EMP_ID_2, :emp_id ) > 0) OR ");
		//		sql.append("(INSTR(A.MGR_EMP_ID_3, :emp_id ) > 0) OR ");
		//		sql.append("(INSTR(A.MGR_EMP_ID_4, :emp_id ) > 0) ");
		//		sql.append(") ");

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("AND A.CUST_ID = :cust ");
			queryCondition.setObject("cust", inputVO.getCust_id());
		}
		//		if(inputVO.getStatusList().size()>0){
		//			sql.append("AND A.APPLY_STATUS IN ( :statusLst ) ");
		//			queryCondition.setObject("statusLst", inputVO.getStatusList());
		//		}
		if (StringUtils.isNotBlank(inputVO.getApply_status())) {
			if (StringUtils.equals(APPLY_STATUS_REVIEW, inputVO.getApply_status())) {
				sql.append("AND A.APPLY_STATUS IN ('1', '4') ");
				sql.append("AND ( ");
				sql.append("(INSTR(A.MGR_EMP_ID_1, :emp_id ) > 0 AND AUTH_STATUS = '0') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_2, :emp_id ) > 0 AND AUTH_STATUS = '1') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_3, :emp_id ) > 0 AND AUTH_STATUS = '2') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_4, :emp_id ) > 0 AND AUTH_STATUS = '3') ");
				sql.append(") ");
			} else {
				if (StringUtils.equals(APPLY_STATUS_AUTH, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '2' ");
				} else if (StringUtils.equals(APPLY_STATUS_TERMINATE, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '3' ");
				} else if (StringUtils.equals(APPLY_STATUS_REJECT, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '9' ");
				}
				sql.append("AND ( ");
				sql.append("(INSTR(A.MGR_EMP_ID_1, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_2, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_3, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_4, :emp_id ) > 0) ");
				sql.append(") ");
			}
		}
		if (inputVO.getAuth_date_bgn() != null) {
			sql.append("AND TRUNC (A.LASTUPDATE) >= TRUNC ( :bgn ) ");
			queryCondition.setObject("bgn", inputVO.getAuth_date_bgn());
		}
		if (inputVO.getAuth_date_end() != null) {
			sql.append("AND TRUNC (A.LASTUPDATE) <= TRUNC ( :end ) ");
			queryCondition.setObject("end", inputVO.getAuth_date_end());
		}

		sql.append("UNION ");
		//Data2: 理規系統內的單次議價資料
		sql.append("SELECT A.CREATOR, A.APPLY_SEQ, A.CUST_ID, C.CUST_NAME, '2' AS APPLY_CAT, A.APPLY_TYPE, A.APPLY_TYPE AS PROD_CAT, A.PROD_ID, A.PROD_NAME, ");
		sql.append("	   (CASE DISCOUNT_TYPE WHEN '1' THEN A.FEE_RATE WHEN '2' THEN A.FEE_DISCOUNT END ) AS DISCOUNT, ");
		sql.append("	   A.AUTH_STATUS, A.APPLY_STATUS, A.MGR_EMP_ID_1, A.MGR_EMP_ID_2, A.MGR_EMP_ID_3, A.MGR_EMP_ID_4, A.APPLY_DATE, ");
		sql.append("       A.APPLE_SETUP_TYPE, A.C_BRANCH_NBR ");
		sql.append("FROM TBCRM_BRG_APPLY_SINGLE A, VWCRM_CUST_INFO C ");
		sql.append("WHERE A.CUST_ID = C.CUST_ID ");
		//		sql.append("AND A.CREATOR IN (SELECT EMP_ID FROM BASE) ");
		//		sql.append("AND A.APPLY_STATUS NOT IN ('V', '0', '2', '3', '5') ");
		//		sql.append("AND ( ");
		//		sql.append("(INSTR(A.MGR_EMP_ID_1, :emp_id ) > 0) OR ");
		//		sql.append("(INSTR(A.MGR_EMP_ID_2, :emp_id ) > 0) OR ");
		//		sql.append("(INSTR(A.MGR_EMP_ID_3, :emp_id ) > 0) OR ");
		//		sql.append("(INSTR(A.MGR_EMP_ID_4, :emp_id ) > 0) ");
		//		sql.append(") ");

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("AND A.CUST_ID = :cust ");
			queryCondition.setObject("cust", inputVO.getCust_id());
		}

		//		if(inputVO.getStatusList().size()>0){
		//			sql.append("AND A.APPLY_STATUS IN ( :statusLst ) ");
		//			queryCondition.setObject("statusLst", inputVO.getStatusList());
		//		}
		if (StringUtils.isNotBlank(inputVO.getApply_status())) {
			if (StringUtils.equals(APPLY_STATUS_REVIEW, inputVO.getApply_status())) {
				sql.append("AND A.APPLY_STATUS IN ('1', '4') ");
				sql.append("AND ( ");
				sql.append("(INSTR(A.MGR_EMP_ID_1, :emp_id ) > 0 AND AUTH_STATUS = '0') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_2, :emp_id ) > 0 AND AUTH_STATUS = '1') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_3, :emp_id ) > 0 AND AUTH_STATUS = '2') OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_4, :emp_id ) > 0 AND AUTH_STATUS = '3') ");
				sql.append(") ");
			} else {
				if (StringUtils.equals(APPLY_STATUS_AUTH, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '2' ");
				} else if (StringUtils.equals(APPLY_STATUS_TERMINATE, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '3' ");
				} else if (StringUtils.equals(APPLY_STATUS_REJECT, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS = '9' ");
				}
				sql.append("AND ( ");
				sql.append("(INSTR(A.MGR_EMP_ID_1, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_2, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_3, :emp_id ) > 0) OR ");
				sql.append("(INSTR(A.MGR_EMP_ID_4, :emp_id ) > 0) ");
				sql.append(") ");
			}
		}
		if (inputVO.getAuth_date_bgn() != null) {
			sql.append("AND TRUNC (A.LASTUPDATE) >= TRUNC ( :bgn ) ");
			queryCondition.setObject("bgn", inputVO.getAuth_date_bgn());
		}

		if (inputVO.getAuth_date_end() != null) {
			sql.append("AND TRUNC (A.LASTUPDATE) <= TRUNC ( :end ) ");
			queryCondition.setObject("end", inputVO.getAuth_date_end());
		}

		StringBuffer sqlMain = new StringBuffer();
		sqlMain.append("SELECT CASE WHEN DEFN.DEPT_ID IS NULL THEN INFO.BRANCH_NBR ELSE DEFN.DEPT_ID END AS BRANCH_NBR, ");
		sqlMain.append("       CASE WHEN DEFN.DEPT_NAME IS NULL THEN INFO.BRANCH_NAME ELSE DEFN.DEPT_NAME END AS BRANCH_NAME, ");
		sqlMain.append("       CASE WHEN A.APPLE_SETUP_TYPE = '0' THEN INFO.AO_CODE ELSE INFO.EMP_ID END AS AO_CODE, INFO.EMP_NAME, ");
		sqlMain.append("       A.* ");
		sqlMain.append("FROM ( ");
		sqlMain.append(sql);
		sqlMain.append(") A ");
		sqlMain.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON A.CREATOR = INFO.EMP_ID ");
		sqlMain.append("LEFT JOIN TBORG_DEFN DEFN ON A.C_BRANCH_NBR = DEFN.DEPT_ID ");
		
		queryCondition.setQueryString(sqlMain.toString());
		queryCondition.setObject("emp_id", ws.getUser().getUserID());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		sendRtnObject(return_VO);
	}

	/*
	 * 申請內容
	 * 
	 */
	public void queryDTL(Object body, IPrimitiveMap header) throws JBranchException {

		CRM421 crm421 = (CRM421) PlatformContext.getBean("crm421");
		
		CRM431InputVO inputVO = (CRM431InputVO) body;
		CRM431OutputVO return_VO = new CRM431OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//1-期間議價、2-單次議價
		sql.append("WITH BASE AS ( ");
		sql.append("  SELECT CUST_ID, NVL(SUM(ACT_PRFT), 0) AS Y_PROFEE ");
		sql.append("  FROM TBCRM_CUST_PROFEE ");
		sql.append("  WHERE DATA_YEAR||DATA_MONTH BETWEEN TO_CHAR(CURRENT_DATE-365, 'YYYYMM') ");
		sql.append("  AND TO_CHAR(CURRENT_DATE, 'YYYYMM') ");
		sql.append("  GROUP BY CUST_ID ");
		sql.append(") ");
		if ("1".equals(inputVO.getApply_cat())) {
			sql.append("SELECT H.COMMENTS,H.TERMINATE_REASON, H.LASTUPDATE, A.APPLY_SEQ, A.TERMINATE_SEQ, A.APPLY_DATE, A.APPLY_TYPE, ");
			sql.append("       A.DMT_STOCK, A.DMT_BOND, A.DMT_BALANCED, A.FRN_STOCK, A.FRN_BOND, A.FRN_BALANCED, A.BUY_HK_MRK, A.BUY_US_MRK, A.BUY_UK_MRK, A.BUY_JP_MRK, A.SELL_HK_MRK, A.SELL_US_MRK, A.SELL_UK_MRK, A.SELL_JP_MRK, ");
			sql.append("       A.BRG_BEGIN_DATE, A.BRG_END_DATE, A.BRG_REASON, A.MGR_EMP_ID_1, A.MGR_EMP_ID_2, A.MGR_EMP_ID_3, A.MGR_EMP_ID_4, ");
			sql.append("       (SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) FROM TBSYSPARAMETER WHERE PARAM_TYPE = '").append(crm421.chkIsUHRM() ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV").append("'||A.HIGHEST_AUTH_LV) AS HIGHEST_AUTH_LV_NAME, A.HIGHEST_AUTH_LV, A.AUTH_STATUS, A.APPLY_STATUS, ");
			sql.append("       CUST.CUST_ID, CUST.CUST_NAME, CUST.CON_DEGREE, CUST.VIP_DEGREE, CUST.AUM_AMT, CUST.BRA_NBR, CUST.Y_PROFEE ");
			sql.append("FROM TBCRM_BRG_APPLY_PERIOD A ");
		} else {
			sql.append("SELECT H.COMMENTS,H.TERMINATE_REASON, H.LASTUPDATE, A.APPLY_SEQ, A.APPLY_TYPE, A.PROD_ID, A.PROD_NAME, A.TRUST_CURR_TYPE, A.TRUST_CURR, ");
			sql.append("       A.PURCHASE_AMT, A.ENTRUST_UNIT, A.ENTRUST_AMT, A.DEFAULT_FEE_RATE, A.FEE_RATE, A.FEE, A.FEE_DISCOUNT, ");
			sql.append("       A.MGR_EMP_ID_1, A.MGR_EMP_ID_2, A.MGR_EMP_ID_3, A.MGR_EMP_ID_4, A.BRG_REASON, ");
			sql.append("       (SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) FROM TBSYSPARAMETER WHERE PARAM_TYPE = '").append(crm421.chkIsUHRM() ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV").append("'||A.HIGHEST_AUTH_LV) AS HIGHEST_AUTH_LV_NAME, A.HIGHEST_AUTH_LV, A.AUTH_STATUS, A.APPLY_STATUS, A.APPLY_DATE, A.DISCOUNT_TYPE, ");
			sql.append("       CUST.CUST_ID, CUST.CUST_NAME, CUST.CON_DEGREE, CUST.VIP_DEGREE, CUST.AUM_AMT, CUST.BRA_NBR, CUST.Y_PROFEE ");
			sql.append("FROM TBCRM_BRG_APPLY_SINGLE A ");
		}
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT CM.CUST_ID, CM.CUST_NAME, CM.CON_DEGREE, CM.VIP_DEGREE, NVL(CM.AUM_AMT, 0) AS AUM_AMT, CM.BRA_NBR, NVL(CP.Y_PROFEE, 0) AS Y_PROFEE ");
		sql.append("  FROM TBCRM_CUST_MAST CM ");
		sql.append("  LEFT JOIN BASE CP ON CM.CUST_ID = CP.CUST_ID ");
		sql.append(") CUST ON CUST.CUST_ID = A.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_BRG_APPROVAL_HISTORY H ");
		sql.append("ON A.APPLY_SEQ = H.APPLY_SEQ ");
		sql.append("WHERE A.APPLY_SEQ = :applySEQ ");
		sql.append("ORDER BY H.LASTUPDATE DESC ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("applySEQ", inputVO.getApply_seq());

		return_VO.setDetailList(dam.exeQuery(queryCondition));

		sendRtnObject(return_VO);
	}

	/** 產生seq No */
	private String getSN() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("CRM431");
		} catch (Exception e) {
			sn.createNewSerial("CRM431", "0000000000", null, null, null, 6, new Long("99999999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("CRM431");
		}
		return seqNum;
	}

	/*
	 * 覆核
	 * 
	 * 2017-01-16 add by ocean
	 * 
	 */
	public void review(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		//		if(new Date() == null){
		//			System.out.println("=========系統日期為null============");
		//		}else{
		//			System.out.println("=========系統日期為============" + new Date());
		//		}

		Date now = getZeroTimeDate(new Date()); //系統日期去時分秒

		WorkStation ws = DataManager.getWorkStation(uuid);

		CRM431InputVO inputVO = (CRM431InputVO) body;
		dam = this.getDataAccessManager();
		String empID = "";

		//		System.out.println("=====================inputVO.getEmpID()======================");
		//		System.out.println(inputVO.getEmpID());

		if (StringUtils.isNotBlank(inputVO.getEmpID())) { //由M+進行覆核			
			empID = inputVO.getEmpID();
			DataManager.getUser(uuid).setUserID(inputVO.getEmpID()); //需要setUserID ==> 更新vo時才會顯示CREATOR及MODIFIER

		} else {
			if (StringUtils.isNotBlank(ws.getUser().getCurrentUserId())) { //由桌機進行覆核
				empID = ws.getUser().getCurrentUserId();
			} else {
				throw new APException("無帶入覆核人員ID，請重新操作。");
			}
		}

		if (null != inputVO.getReviewList() && inputVO.getReviewList().size() > 0 && (StringUtils.equals("allAccept", inputVO.getActionType()) || StringUtils.equals("allReject", inputVO.getActionType()))) { // 全部同意/退回

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			for (Map<String, Object> tempMap : inputVO.getReviewList()) {
				if ((StringUtils.equals("1", inputVO.getApply_cat()) && StringUtils.equals("1", (String) tempMap.get("APPLY_TYPE"))) || (StringUtils.equals("2", inputVO.getApply_cat()) && (StringUtils.equals("1", (String) tempMap.get("APPLY_TYPE")) || StringUtils.equals("2", (String) tempMap.get("APPLY_TYPE"))))) { // 單筆 & 基金 or 期間 & 基金議價
					tempMap.put("prod_type", "1");
				} else {
					tempMap.put("prod_type", "2");
				}

				if (StringUtils.equals("1", inputVO.getApply_cat()) && StringUtils.equals("allAccept", inputVO.getActionType())) { //期間議價 && 議價全部同意 才需要判斷是否已超過期間議價迄日
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date brg_end_date = sdf.parse(tempMap.get("BRG_END_DATE").toString());

					if (!brg_end_date.before(now)) {
						list.add(tempMap);
					}
				} else { //單次議價 or 議價全部退回
					list = inputVO.getReviewList();
				}
			}
			String seqNum = getSN();
			Set<String> set = new HashSet<String>();

			if (list.size() > 0) {

				String custID = list.get(0).get("CUST_ID").toString();

				for (Map<String, Object> map : list) {
					List tempData = new ArrayList<>();
					tempData.add((String) map.get("APPLY_SEQ"));
					tempData.add(inputVO.getApply_cat());
					tempData.add((StringUtils.equals("allAccept", inputVO.getActionType()) ? "accept" : "reject"));
					tempData.add(map.get("comments") == null ? "" : (String) map.get("comments"));
					tempData.add(empID);
					tempData.add((String) map.get("CUST_ID"));
					tempData.add((String) map.get("prod_type"));
					tempData.add(inputVO.getFromMPlus());
					tempData.add(seqNum + map.get("CREATOR").toString());

					dam.newTransactionExeMethod(this, "reviewAct", tempData);

					//					reviewAct(dam, 
					//							(String) map.get("APPLY_SEQ"), 
					//							inputVO.getApply_cat(), 
					//							(StringUtils.equals("allAccept", inputVO.getActionType()) ? "accept" : "reject"), 
					//							(String) map.get("comments"), 
					//							empID, 
					//							(String) map.get("CUST_ID"), 
					//							(String) map.get("prod_type"),
					//							inputVO.getFromMPlus(),
					//							seqNum + map.get("CREATOR").toString());

					set.add(map.get("CREATOR").toString());

				}

				if (set.size() > 0) {
					for (String creator : set) {

						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						StringBuffer sql = new StringBuffer();

						sql.append("SELECT DECODE(AUTH_STATUS, '0', MGR_EMP_ID_1, '1', MGR_EMP_ID_2, '2', MGR_EMP_ID_3, '3', MGR_EMP_ID_4) AS EMPIDLIST, APPLE_SETUP_TYPE, C_BRANCH_NBR ");

						if ("1".equals(inputVO.getApply_cat())) { //期間議價
							sql.append("FROM TBCRM_BRG_APPLY_PERIOD ");

						} else { //單次議價
							sql.append("FROM TBCRM_BRG_APPLY_SINGLE ");
						}

						sql.append("WHERE MPLUS_BATCH = :seqNum ");
						sql.append("AND APPLY_STATUS = '1' ");

						queryCondition.setQueryString(sql.toString());
						queryCondition.setObject("seqNum", seqNum + creator);

						List<Map<String, Object>> EMPIDLIST = dam.exeQuery(queryCondition);
						Set<String> empSet = new HashSet<String>();

						if (EMPIDLIST.size() > 0) {
							String empIDList = "";
							for (Map<String, Object> map : EMPIDLIST) {
								empSet.add(map.get("EMPIDLIST").toString());
							}
							if (empSet.size() > 0) {
								for (String emp : empSet) {
									empIDList += (emp + ",");
								}
							}

							if (empIDList.length() > 0) {
								empIDList = empIDList.substring(0, empIDList.length() - 1);
							}

							//要發M+的主管ID
							if (StringUtils.isNotBlank(empIDList) && StringUtils.isNotBlank(custID)) {
								CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");
								crm451.pushAuthMessage(empIDList, ("1".equals(inputVO.getApply_cat()) ? "period" : "single"), custID, creator, seqNum + creator, (String) EMPIDLIST.get(0).get("APPLE_SETUP_TYPE"), (String) EMPIDLIST.get(0).get("C_BRANCH_NBR"));
							}
						}
					}
				}

			} else {
				if (StringUtils.equals("1", inputVO.getApply_cat()) && StringUtils.equals("allAccept", inputVO.getActionType())) { //期間議價 && 議價全部同意 才需要判斷是否已超過期間議價迄日
					throw new APException("ehl_02_CRM431_001"); //已超過期間議價迄日，無法進行覆核。					
				}
			}

		} else { // 單筆同意/退回
			if (StringUtils.equals("1", inputVO.getApply_cat()) && StringUtils.equals("accept", inputVO.getActionType())) { //期間議價 && 同意議價 才需要判斷是否已超過期間議價迄日

				//				System.out.println("=============期間議價迄日============" + inputVO.getAuth_date_end());
				if (inputVO.getAuth_date_end() != null) {
					Date brg_end_date = getZeroTimeDate(inputVO.getAuth_date_end());
					if (brg_end_date.before(now)) {
						throw new APException("ehl_02_CRM431_001"); //已超過期間議價迄日，無法進行覆核。	
					}
				} else { //M+傳到後端會收不到inputVO.getAuth_date_end()
					String apply_seq = inputVO.getApply_seq();
					TBCRM_BRG_APPLY_PERIODVO pvo = new TBCRM_BRG_APPLY_PERIODVO();
					pvo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, apply_seq);
					Date brg_end_date = pvo.getBRG_END_DATE();

					//					System.out.println("=============期間議價迄日============" + brg_end_date);

					if (brg_end_date.before(now)) {
						throw new APException("ehl_02_CRM431_001"); //已超過期間議價迄日，無法進行覆核。	
					}
				}

			}
			String seqNum = getSN();
			reviewAct(inputVO.getApply_seq(), inputVO.getApply_cat(), inputVO.getActionType(), inputVO.getComments(), empID, inputVO.getCust_id(), inputVO.getProd_type(), inputVO.getFromMPlus(), seqNum);

			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT DECODE(AUTH_STATUS, '0', MGR_EMP_ID_1, '1', MGR_EMP_ID_2, '2', MGR_EMP_ID_3, '3', MGR_EMP_ID_4) AS EMPIDLIST, CREATOR, APPLE_SETUP_TYPE, C_BRANCH_NBR ");

			if ("1".equals(inputVO.getApply_cat())) { //期間議價
				sql.append("FROM TBCRM_BRG_APPLY_PERIOD ");

			} else { //單次議價
				sql.append("FROM TBCRM_BRG_APPLY_SINGLE ");
			}

			sql.append("WHERE MPLUS_BATCH = :seqNum ");
			sql.append("AND APPLY_STATUS = '1' ");

			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("seqNum", seqNum);

			List<Map<String, Object>> EMPIDLIST = dam.exeQuery(queryCondition);
			if (EMPIDLIST.size() > 0) {
				String empIDList = EMPIDLIST.get(0).get("EMPIDLIST").toString();
				String creator = EMPIDLIST.get(0).get("CREATOR").toString();

				//要發M+的主管ID
				if (StringUtils.isNotBlank(empIDList) && StringUtils.isNotBlank(inputVO.getCust_id())) {
					CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");
					crm451.pushAuthMessage(empIDList, ("1".equals(inputVO.getApply_cat()) ? "period" : "single"), inputVO.getCust_id(), creator, seqNum, (String) EMPIDLIST.get(0).get("APPLE_SETUP_TYPE"), (String) EMPIDLIST.get(0).get("C_BRANCH_NBR"));
				}
			}
		}

		sendRtnObject(null);
	}

	//日期去時分秒
	private static Date getZeroTimeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		return date;
	}

	public void getTerminateReason(Object body, IPrimitiveMap header) throws JBranchException {
		CRM431InputVO inputVO = (CRM431InputVO) body;
		CRM431OutputVO return_VO = new CRM431OutputVO();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT TERMINATE_REASON FROM TBCRM_BRG_APPROVAL_HISTORY ");
		sql.append("WHERE APPLY_SEQ = :apply_seq ORDER BY CREATETIME DESC ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("apply_seq", inputVO.getApply_seq());

		return_VO.setResultList(dam.exeQuery(queryCondition));

		sendRtnObject(return_VO);

	}

	public void reviewAct(String applySEQ, String applyCat, String actionType, String comments, String empID, String custID, String prodType, Boolean fromMPlus, String seqNum) throws JBranchException, Exception {

		TBCRM_BRG_APPLY_PERIODVO pvo = new TBCRM_BRG_APPLY_PERIODVO();
		TBCRM_BRG_APPLY_SINGLEVO svo = new TBCRM_BRG_APPLY_SINGLEVO();

//		logger.info("Step1. " + empID + " review " + custID + (StringUtils.equals("1", applyCat) ? " PERIOD" : " SINGLE"));

		Boolean actFlag = true; //判斷是否該繼續執行
		dam = this.getDataAccessManager();
		if (StringUtils.equals("1", applyCat)) { //期間
			pvo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, applySEQ);
		} else if (StringUtils.equals("2", applyCat)) { //單筆
			svo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, applySEQ);
		}

		CRM421 crm421 = (CRM421) PlatformContext.getBean("crm421");
		CRM421InputVO inputVO_421 = new CRM421InputVO();
		if (StringUtils.equals("1", applyCat) && null != pvo) { //期間議價 且 有資料 
		//			if (null == pvo.getAUTH_STATUS()) 
		//				throw new APException("此議價已由其他主管退回"); 

			if (null != pvo.getAUTH_STATUS()) {
				//根據覆核層級，確認前端傳入的員編與相對應的層級相同。若不同直接拋錯
				if ((pvo.getAUTH_STATUS().equals("0") && StringUtils.indexOf(pvo.getMGR_EMP_ID_1(), empID) == -1) || (pvo.getAUTH_STATUS().equals("1") && StringUtils.indexOf(pvo.getMGR_EMP_ID_2(), empID) == -1) || (pvo.getAUTH_STATUS().equals("2") && StringUtils.indexOf(pvo.getMGR_EMP_ID_3(), empID) == -1) || (pvo.getAUTH_STATUS().equals("3") && StringUtils.indexOf(pvo.getMGR_EMP_ID_4(), empID) == -1)) {

					//考慮代理人
					dam = this.getDataAccessManager();
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sb = new StringBuffer();

					sb.append("select EMP_ID from TBORG_AGENT ");
					sb.append("where AGENT_ID = :empID and AGENT_STATUS = 'S' ");

					queryCondition.setObject("empID", empID); //若為代理人登入：empID會抓代理人員編
					queryCondition.setQueryString(sb.toString());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);

					if (list.size() > 0) {
						for (Map<String, Object> map : list) { //可能代理多人
							String agentEmpId = map.get("EMP_ID").toString();

							if ((pvo.getAUTH_STATUS().equals("0") && StringUtils.indexOf(pvo.getMGR_EMP_ID_1(), agentEmpId) == -1) || (pvo.getAUTH_STATUS().equals("1") && StringUtils.indexOf(pvo.getMGR_EMP_ID_2(), agentEmpId) == -1) || (pvo.getAUTH_STATUS().equals("2") && StringUtils.indexOf(pvo.getMGR_EMP_ID_3(), agentEmpId) == -1) || (pvo.getAUTH_STATUS().equals("3") && StringUtils.indexOf(pvo.getMGR_EMP_ID_4(), agentEmpId) == -1)) {
								//								throw new APException("已經覆核完畢，請勿重複覆核!"); 
								actFlag = false; //停止執行該筆
							} else {
								actFlag = true;
								break; //有其中一個被代理人符合即跳出迴圈。
							}
						}
					} else {
						//						throw new APException("已經覆核完畢，請勿重複覆核!");
						actFlag = false; //停止執行該筆
					}
				}

				if (actFlag) {
					if (StringUtils.equals("accept", actionType)) { //同意申請		
//						logger.info("Step2. " + empID + " accept " + custID + (StringUtils.equals("1", applyCat) ? " PERIOD" : " SINGLE"));

						Integer authStatus = Integer.parseInt(pvo.getAUTH_STATUS()) + 1;

						if (authStatus > Integer.parseInt(pvo.getHIGHEST_AUTH_LV())) { //當同層級有多個主管可以覆核，透過M+可能導致同時間一起覆核					
						//							throw new APException("ehl_02_CRM431_002"); 				//此議價已由其他主管覆核
						} else if (authStatus == Integer.parseInt(pvo.getHIGHEST_AUTH_LV())) {
							if (StringUtils.equals("4", pvo.getAPPLY_STATUS())) { //終止待覆核
								saveHistory(dam, applyCat, pvo.getAPPLY_SEQ(), "1", String.valueOf(authStatus), comments, empID, fromMPlus);
								setPeriodFinal(dam, crm421, inputVO_421, comments, empID, pvo, String.valueOf(authStatus), "6", fromMPlus); //1檢核2鍵機 3覆核 4刪除 5修改 6終止

								//只有終止再申請才需要更新資料
								if (StringUtils.isNotBlank(pvo.getTERMINATE_SEQ())) {
									TBCRM_BRG_APPLY_PERIODVO newPvo = new TBCRM_BRG_APPLY_PERIODVO();
									if (pvo.getTERMINATE_SEQ() != null) {
										newPvo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, pvo.getTERMINATE_SEQ());
										newPvo.setAPPLY_STATUS("1");
										dam.update(newPvo);

										//==== for CBS 模擬日測試用========
										setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", pvo.getTERMINATE_SEQ());
										//================================================================
									}
								}
							} else { //新申請
								saveHistory(dam, applyCat, pvo.getAPPLY_SEQ(), "1", String.valueOf(authStatus), comments, empID, fromMPlus);
								setPeriodFinal(dam, crm421, inputVO_421, comments, empID, pvo, String.valueOf(authStatus), "3", fromMPlus);
							}
						} else {
							setMgrEmp(dam, crm421, custID, comments, empID, prodType, pvo, null, String.valueOf(authStatus), fromMPlus, seqNum);
						}
					} else if (StringUtils.equals("reject", actionType)) { //退回申請
						Integer authStatus = Integer.parseInt(pvo.getAUTH_STATUS()) + 1;

						if (authStatus > Integer.parseInt(pvo.getHIGHEST_AUTH_LV())) { //當同層級有多個主管可以覆核，透過M+可能導致同時間一起覆核					
						//							throw new APException("ehl_02_CRM431_002"); //此議價已由其他主管覆核
						} else {
							saveHistory(dam, applyCat, pvo.getAPPLY_SEQ(), "2", String.valueOf(authStatus), comments, empID, fromMPlus);

							if (StringUtils.equals("4", pvo.getAPPLY_STATUS())) { //終止再申請待覆核
								//只有終止再申請才需要更新資料
								if (StringUtils.isNotBlank(pvo.getTERMINATE_SEQ())) {
									//刪除新申請那筆, 還原既有那筆
									TBCRM_BRG_APPLY_PERIODVO newPvo = new TBCRM_BRG_APPLY_PERIODVO();
									newPvo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, pvo.getTERMINATE_SEQ());
									dam.delete(newPvo);
								}

								pvo.setTERMINATE_SEQ(null);
								pvo.setAUTH_STATUS(pvo.getHIGHEST_AUTH_LV());
								pvo.setAPPLY_STATUS(APPLY_STATUS_AUTH);
								dam.update(pvo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", pvo.getAPPLY_SEQ());
								//================================================================
							} else { //新申請
								setPeriodFinal(dam, crm421, inputVO_421, comments, empID, pvo, null, "4", fromMPlus); //單次&期間(待覆核) 僅可刪除: 1檢核2鍵機 3覆核 4刪除 5修改 6終止
							}
						}

					}
				}
			}
		} else if (StringUtils.equals("2", applyCat) && null != svo) { //單筆議價 且 有資料
		//			if (null == svo.getAUTH_STATUS()) 
		//				throw new APException("此議價已由其他主管退回"); 

			if (null != svo.getAUTH_STATUS()) {
				if ((svo.getAUTH_STATUS().equals("0") && StringUtils.indexOf(svo.getMGR_EMP_ID_1(), empID) == -1) || (svo.getAUTH_STATUS().equals("1") && StringUtils.indexOf(svo.getMGR_EMP_ID_2(), empID) == -1) || (svo.getAUTH_STATUS().equals("2") && StringUtils.indexOf(svo.getMGR_EMP_ID_3(), empID) == -1) || (svo.getAUTH_STATUS().equals("3") && StringUtils.indexOf(svo.getMGR_EMP_ID_4(), empID) == -1)) {

					//考慮代理人
					dam = this.getDataAccessManager();
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sb = new StringBuffer();

					sb.append("select EMP_ID from TBORG_AGENT ");
					sb.append("where AGENT_ID = :empID and AGENT_STATUS = 'S' ");

					queryCondition.setObject("empID", empID);
					queryCondition.setQueryString(sb.toString());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);

					if (list.size() > 0) {
						logger.info(empID + " AGENTS" + list.size() + " PERSON(S).");
						for (Map<String, Object> map : list) { //可能代理多人
							String agentEmpId = map.get("EMP_ID").toString();
							logger.info(empID + " AGENTS" + agentEmpId);

							if ((svo.getAUTH_STATUS().equals("0") && StringUtils.indexOf(svo.getMGR_EMP_ID_1(), agentEmpId) == -1) || (svo.getAUTH_STATUS().equals("1") && StringUtils.indexOf(svo.getMGR_EMP_ID_2(), agentEmpId) == -1) || (svo.getAUTH_STATUS().equals("2") && StringUtils.indexOf(svo.getMGR_EMP_ID_3(), agentEmpId) == -1) || (svo.getAUTH_STATUS().equals("3") && StringUtils.indexOf(svo.getMGR_EMP_ID_4(), agentEmpId) == -1)) {
								//								throw new APException("已經覆核完畢，請勿重複覆核!"); 
								actFlag = false; //停止執行該筆
								logger.info(agentEmpId + " IS NOT NGR_EMP_ID");
							} else {
								actFlag = true;
								logger.info(agentEmpId + " IS NGR_EMP_ID");
								break; //有其中一個被代理人符合即跳出迴圈。
							}
						}
					} else {
						//						throw new APException("已經覆核完畢，請勿重複覆核!"); 
						logger.info(empID + " AGENTS NOBODY !!");
						actFlag = false; //停止執行該筆
					}
				}
				if (actFlag) {
					if (StringUtils.equals("accept", actionType)) { //同意申請		
//						logger.info("Step2. " + empID + " accept " + custID + (StringUtils.equals("1", applyCat) ? " PERIOD" : " SINGLE"));
						Integer authStatus = Integer.parseInt(svo.getAUTH_STATUS()) + 1;
						if (authStatus > Integer.parseInt(svo.getHIGHEST_AUTH_LV())) { //當同層級有多個主管可以覆核，透過M+可能導致同時間一起覆核
						//							throw new APException("ehl_02_CRM431_002"); //此議價已由其他主管覆核
						} else if (authStatus == Integer.parseInt(svo.getHIGHEST_AUTH_LV())) {
							saveHistory(dam, applyCat, svo.getAPPLY_SEQ(), "1", String.valueOf(authStatus), comments, empID, fromMPlus);
							setSingleFinal(dam, crm421, inputVO_421, comments, empID, svo, String.valueOf(authStatus), "3", fromMPlus);
						} else {
							setMgrEmp(dam, crm421, custID, comments, empID, prodType, null, svo, String.valueOf(authStatus), fromMPlus, seqNum);
						}
					} else if (StringUtils.equals("reject", actionType)) { //退回申請
//						logger.info("Step2. " + empID + " reject " + custID + (StringUtils.equals("1", applyCat) ? " PERIOD" : " SINGLE"));
						Integer authStatus = Integer.parseInt(svo.getAUTH_STATUS()) + 1;
						if (authStatus > Integer.parseInt(svo.getHIGHEST_AUTH_LV())) { //當同層級有多個主管可以覆核，透過M+可能導致同時間一起覆核
						//							throw new APException("ehl_02_CRM431_002"); //此議價已由其他主管覆核
						} else {
							saveHistory(dam, applyCat, svo.getAPPLY_SEQ(), "2", String.valueOf(authStatus), comments, empID, fromMPlus);
							setSingleFinal(dam, crm421, inputVO_421, comments, empID, svo, null, "4", fromMPlus); //單次&期間(待覆核) 僅可刪除: 1檢核2鍵機 3覆核 4刪除 5修改 6終止 9:退回
						}
					}
				}
			}
		}
	}

	/*
	 * 期間：最後一關時，需發覆核電文
	 * 
	 * 2017-01-16 add by ocean
	 * 
	 */
	public void setPeriodFinal(DataAccessManager dam, CRM421 crm421, CRM421InputVO inputVO_421, String comments, String empID, TBCRM_BRG_APPLY_PERIODVO pvo, String authStatus, String checkCode, Boolean fromMPlus) throws JBranchException, Exception {

		pvo.setAUTH_STATUS(authStatus);

		inputVO_421.setCust_id(pvo.getCUST_ID());
		FP032675DataVO outputVO_FP032675 = crm421.getFC032675Data(inputVO_421);

		SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
		SOT709InputVO inputVO_709 = new SOT709InputVO();
		SOT709OutputVO outputVO_709 = new SOT709OutputVO();

		SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
		SOT710InputVO inputVO_710 = new SOT710InputVO();
		SOT710OutputVO outputVO_710 = new SOT710OutputVO();

		Thread.sleep(500); //避免申請會有10毫秒內重送400電文2次，增加Sleep 500毫秒(#4414)
		logger.info("Step4. insert info into 400");
		if (StringUtils.equals("1", pvo.getAPPLY_TYPE())) { //基金
			inputVO_709.setCheckCode(getCONFIRM_CODE(checkCode, pvo.getAPPLY_TYPE()));
			inputVO_709.setApplySeq(pvo.getAPPLY_SEQ());

			if (StringUtil.isEqual(outputVO_FP032675.getObuFlag(), "Y")) {
				outputVO_709 = sot709.periodBargainApplyOBU(inputVO_709);
			} else {
				outputVO_709 = sot709.periodBargainApplyDBU(inputVO_709);
			}
		} else if (StringUtils.equals("2", pvo.getAPPLY_TYPE())) { //ETF
			inputVO_710.setCheckCode(getCONFIRM_CODE(checkCode, pvo.getAPPLY_TYPE()));
			inputVO_710.setApplySEQ(pvo.getAPPLY_SEQ());

			outputVO_710 = sot710.periodBargainApply(inputVO_710);
		}

		if ((StringUtils.equals("1", pvo.getAPPLY_TYPE()) && StringUtils.isBlank(outputVO_709.getErrorMsg())) || (StringUtils.equals("2", pvo.getAPPLY_TYPE()) && StringUtils.isBlank(outputVO_710.getErrorTxt()))) {
			//checkCode 1檢核2鍵機 3覆核 4刪除 5基金的修改及終止 6ETF終止
			//APPLY_SATUS V：送檢核中 0：暫存 1：待覆核 2：已授權 3：已終止 4：終止待覆核 
			if (StringUtils.equals("3", checkCode)) { //發覆核電文成功
				pvo.setAPPLY_STATUS(APPLY_STATUS_AUTH); //已授權
				dam.update(pvo);
				//==== for CBS 模擬日測試用========
				setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", pvo.getAPPLY_SEQ());
				//================================================================
			} else if (StringUtils.equals("5", getCONFIRM_CODE(checkCode, pvo.getAPPLY_TYPE()))) { //基金發終止電文成功
				pvo.setAPPLY_STATUS(APPLY_STATUS_TERMINATE); //已終止
				dam.update(pvo);
				//==== for CBS 模擬日測試用========
				setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", pvo.getAPPLY_SEQ());
				//================================================================
			} else if (StringUtils.equals("6", getCONFIRM_CODE(checkCode, pvo.getAPPLY_TYPE()))) { //ETF發終止電文成功
				pvo.setAPPLY_STATUS(APPLY_STATUS_TERMINATE); //已終止
				dam.update(pvo);
				//==== for CBS 模擬日測試用========
				setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", pvo.getAPPLY_SEQ());
				//================================================================
			} else if (StringUtils.equals("4", checkCode)) { //發刪除電文成功
				//2017-9-3 主管退回不刪除 update成已退回
				pvo.setAPPLY_STATUS(APPLY_STATUS_REJECT); //已終止
				dam.update(pvo);
				logger.info("Step5. insert info into TBCRM_BRG_APPLY_PERIOD");
				//==== for CBS 模擬日測試用========
				setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", pvo.getAPPLY_SEQ());
				//================================================================
			}
		} else {
			if (StringUtils.equals("1", pvo.getAPPLY_TYPE()) && StringUtils.isNotBlank(outputVO_709.getErrorMsg())) {
				throw new APException(outputVO_709.getErrorMsg());
			} else if (StringUtils.equals("2", pvo.getAPPLY_TYPE()) && StringUtils.isNotBlank(outputVO_710.getErrorTxt())) {
				throw new APException(outputVO_710.getErrorTxt());
			}
		}
	}

	/**
	 *
	 * @param checkCode 基金 CONIRM CODE 1檢核 2鍵機 3覆核4刪除 5終止/修改 ETF CONIRM CODE 1檢核
	 *            2鍵機 3覆核4刪除 5修改 6終止
	 * @param prodCate //1 基金 2 ETF
	 * @throws JBranchException
	 * @throws Exception
	 */
	private String getCONFIRM_CODE(String checkCode, String prodCate) {

		//基金 CONIRM CODE 1檢核 2鍵機 3覆核4刪除 5終止/修改
		//ETF CONIRM CODE 1檢核 2鍵機 3覆核4刪除 5修改 6終止
		if (StringUtils.equals(prodCate, "1")) {
			if (StringUtils.equals(checkCode, "5") || StringUtils.equals(checkCode, "6")) {
				return "5";
			} else {
				return checkCode;
			}
		} else {
			return checkCode;
		}

	}

	/*
	 * 單筆：最後一關時，需發覆核電文
	 * 
	 * 2017-01-16 add by ocean
	 * 
	 */
	public void setSingleFinal(DataAccessManager dam, CRM421 crm421, CRM421InputVO inputVO_421, String comments, String empID, TBCRM_BRG_APPLY_SINGLEVO svo, String authStatus, String checkCode, Boolean fromMPlus) throws JBranchException, Exception {

		svo.setAUTH_STATUS(authStatus);

		inputVO_421.setCust_id(svo.getCUST_ID());

		//2020-4-17 by Jacky 沒有用到,多打的電文
//		FC032675DataVO outputVO_FC032675 = crm421.getFC032675Data(inputVO_421);

		SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
		SOT709InputVO inputVO_709 = new SOT709InputVO();
		SOT709OutputVO outputVO_709 = new SOT709OutputVO();

		SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
		SOT710InputVO inputVO_710 = new SOT710InputVO();
		SOT710OutputVO outputVO_710 = new SOT710OutputVO();

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO inputVO_712 = new SOT712InputVO();

		if (StringUtils.equals("1", svo.getAPPLY_TYPE()) || StringUtils.equals("2", svo.getAPPLY_TYPE()) || StringUtils.equals("6", svo.getAPPLY_TYPE())) { //基金
			inputVO_709.setCheckCode(checkCode);
			inputVO_709.setApplySeq(svo.getAPPLY_SEQ());

			if (StringUtil.isEqual(svo.getAPPLY_TYPE(), "1") || StringUtil.isEqual(svo.getAPPLY_TYPE(), "6")) { //1:基金單筆申購 6:基金動態鎖利
				outputVO_709 = sot709.singleBargainApply(inputVO_709);
			} else if (StringUtil.isEqual(svo.getAPPLY_TYPE(), "2")) { //基金定期(不)定額申購
				outputVO_709 = sot709.singleRegBargainApply(inputVO_709);
			}
		} else if (StringUtils.equals("4", svo.getAPPLY_TYPE())) { //ETF
			inputVO_710.setCheckCode(checkCode);
			inputVO_710.setApplySEQ(svo.getAPPLY_SEQ());

			outputVO_710 = sot710.singleBargainApply(inputVO_710);
		}

		if (((StringUtils.equals("1", svo.getAPPLY_TYPE()) || StringUtils.equals("2", svo.getAPPLY_TYPE()) || StringUtils.equals("6", svo.getAPPLY_TYPE())) 
				&& StringUtils.isBlank(outputVO_709.getErrorMsg())) || (StringUtils.equals("4", svo.getAPPLY_TYPE()) && StringUtils.isBlank(outputVO_710.getErrorTxt()))) {
			if (StringUtils.equals("3", checkCode)) { //發覆核電文成功
				svo.setAPPLY_STATUS(APPLY_STATUS_AUTH); //已授權
				dam.update(svo);

				//==== for CBS 模擬日測試用========
				setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", svo.getAPPLY_SEQ());
				//================================================================

				//議價覆核完成後，更新明細檔該筆議價狀態為已完成；若明細檔中所有申請議價交易的議價覆核皆完成，則更新主檔議價狀態為已完成 add by jimmy 2017/03/14
				inputVO_712.setBargainApplySeq(svo.getAPPLY_SEQ());
				inputVO_712.setBargainStatus("2");
				sot712.updateBargainStatus(inputVO_712, null);

			} else if (StringUtils.equals("5", checkCode)) { //發終止電文成功
				svo.setAPPLY_STATUS(APPLY_STATUS_TERMINATE); //已終止
				dam.update(svo);
				//==== for CBS 模擬日測試用========
				setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", svo.getAPPLY_SEQ());
				//================================================================
			} else if (StringUtils.equals("4", checkCode)) { //發刪除電文成功
				inputVO_712.setBargainApplySeq(svo.getAPPLY_SEQ());
				inputVO_712.setBargainStatus("3");
				sot712.updateBargainStatus(inputVO_712, null);
				//				dam.delete(svo);
				//2017-9-3 主管退回不刪除 update成已退回
				svo.setAPPLY_STATUS(APPLY_STATUS_REJECT); //已終止
				dam.update(svo);
				//==== for CBS 模擬日測試用========
				setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", svo.getAPPLY_SEQ());
				//================================================================

				//				saveHistory(dam, "2", svo.getAPPLY_SEQ(), "1", authStatus, comments, empID, fromMPlus);

			}
		} else {
			if ((StringUtils.equals("1", svo.getAPPLY_TYPE()) || StringUtils.equals("2", svo.getAPPLY_TYPE()) || StringUtils.equals("6", svo.getAPPLY_TYPE())) 
					&& StringUtils.isNotBlank(outputVO_709.getErrorMsg())) {
				throw new APException(outputVO_709.getErrorMsg());
			} else if (StringUtils.equals("4", svo.getAPPLY_TYPE()) && StringUtils.isNotBlank(outputVO_710.getErrorTxt())) {
				throw new APException(outputVO_710.getErrorTxt());
			}
		}
	}

	/*
	 * 儲存下一層覆核人員
	 * 
	 * 2016-01-17 add by ocean
	 * 
	 */
	private void setMgrEmp(DataAccessManager dam, CRM421 crm421, String custID, String comments, String empID, String prodType, TBCRM_BRG_APPLY_PERIODVO pvo, TBCRM_BRG_APPLY_SINGLEVO svo, String authStatus, Boolean fromMPlus, String seqNum) throws JBranchException, Exception {

		//		String empIDList = crm421.getReviewLevelEmpList(custID, String.valueOf((Integer.valueOf(authStatus) + 1)), prodType);

		if (null != pvo) { //期間議價

			pvo.setMPLUS_BATCH(seqNum);
			pvo.setAUTH_STATUS(authStatus);
			//			String creator = pvo.getCreator();
			String empIDList = crm421.getReviewLevelEmpList(custID, String.valueOf((Integer.valueOf(authStatus) + 1)), prodType, pvo.getCreator(), pvo.getC_BRANCH_NBR(), pvo.getAPPLE_SETUP_TYPE());

			switch ((Integer.valueOf(authStatus) + 1)) {
			case 1:
				pvo.setMGR_EMP_ID_1(empIDList);
				break;
			case 2:
				pvo.setMGR_EMP_ID_2(empIDList);
				break;
			case 3:
				pvo.setMGR_EMP_ID_3(empIDList);
				break;
			case 4:
				pvo.setMGR_EMP_ID_4(empIDList);
				break;
			}

			dam.update(pvo);
			//==== for CBS 模擬日測試用========
			setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", pvo.getAPPLY_SEQ());
			//================================================================


			saveHistory(dam, "1", pvo.getAPPLY_SEQ(), "1", authStatus, comments, empID, fromMPlus);

			//			//要發M+的主管ID
			//			if (StringUtils.isNotBlank(empIDList) && StringUtils.isNotBlank(custID)) {
			//				CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");
			//				crm451.pushAuthMessage(empIDList, (null != pvo ? "period" : "single"), custID, creator, seqNum);
			//			}

		} else if (null != svo) { //單次議價

			svo.setMPLUS_BATCH(seqNum);
			svo.setAUTH_STATUS(authStatus);
			//			String creator = svo.getCreator();
			String empIDList = crm421.getReviewLevelEmpList(custID, String.valueOf((Integer.valueOf(authStatus) + 1)), prodType, svo.getCreator(), svo.getC_BRANCH_NBR(), svo.getAPPLE_SETUP_TYPE());

			switch ((Integer.valueOf(authStatus) + 1)) {
			case 1:
				svo.setMGR_EMP_ID_1(empIDList);
				break;
			case 2:
				svo.setMGR_EMP_ID_2(empIDList);
				break;
			case 3:
				svo.setMGR_EMP_ID_3(empIDList);
				break;
			case 4:
				svo.setMGR_EMP_ID_4(empIDList);
				break;
			}

			dam.update(svo);
			//==== for CBS 模擬日測試用========
			setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", svo.getAPPLY_SEQ());
			//================================================================

			saveHistory(dam, "2", svo.getAPPLY_SEQ(), "1", authStatus, comments, empID, fromMPlus);

			//			//要發M+的主管ID
			//			if (StringUtils.isNotBlank(empIDList) && StringUtils.isNotBlank(custID)) {
			//				CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");
			//				crm451.pushAuthMessage(empIDList, (null != pvo ? "period" : "single"), custID, creator, seqNum);
			//			}
		}
	}

	/*
	 * 歷史記錄
	 * 
	 * 2017-01-17 add by ocean
	 * 
	 */
	private void saveHistory(DataAccessManager dam, String applyCat, String applySEQ, String authType, String authLV, String comments, String empID, Boolean fromMPlus) throws JBranchException, Exception {

		TBCRM_BRG_APPROVAL_HISTORYVO hvo = new TBCRM_BRG_APPROVAL_HISTORYVO();
		hvo.setAPPROVAL_SEQ(new BigDecimal(new SimpleDateFormat("yyyyMMdd").format(new Date()) + getSeqNum(new SerialNumberUtil(), "SQ_TBCRM_BRG_APPROVAL_HISTORY", "00000000000", new java.sql.Timestamp(System.currentTimeMillis()), 1, new Long("99999999999"), "y", new Long("0")))); //簽核歷程編號
		hvo.setAPPLY_CAT(applyCat); //類別 1:期間、2:單次
		hvo.setAPPLY_SEQ(applySEQ); //議價編號
		hvo.setAUTH_TYPE(authType); //記錄型態 1：同意、2：退回、3：修改
		hvo.setAUTH_LV(authLV);
		hvo.setAUTH_EMP_ID(empID);
		hvo.setAUTH_DATE(new Timestamp(new Date().getTime()));
		if (fromMPlus != null && fromMPlus == true) {
			if (StringUtils.isNotBlank(comments)) {
				hvo.setCOMMENTS(comments + " by M+");
			} else {
				hvo.setCOMMENTS("by M+");
			}
		} else {
			hvo.setCOMMENTS(comments);
		}
		logger.info("Step3. insert info into TBCRM_BRG_APPROVAL_HISTORY");
		dam.create(hvo);
	}

	/*
	 * 取得平台序號
	 * 
	 * 2017-01-16 add by ocean
	 * 
	 */
	private String getSeqNum(SerialNumberUtil sn, String TXN_ID, String format, Timestamp timeStamp, Integer minNum, Long maxNum, String status, Long nowNum) throws JBranchException {

		String seqNum = "";

		try {
			seqNum = sn.getNextSerialNumber(TXN_ID);
		} catch (Exception e) {
			sn.createNewSerial(TXN_ID, format, 1, "d", timeStamp, minNum, maxNum, status, nowNum, null);
			seqNum = sn.getNextSerialNumber(TXN_ID);
		}

		return seqNum;
	}

	/** 議價 For CBS 環境先用模擬日代替 **/
	private void setfakeSysdate(String table, String seq) throws JBranchException {
		Manager.manage(getDataAccessManager())
				.append("update " + table + " ")
				.append("set LASTUPDATE = to_date(:fake, 'yyyymmddhh24miss') ")
				.append("where APPLY_SEQ = :seq ")
				.put("fake", cbsservice.getCBSTestDate())
				.put("seq", seq)
				.update();
	}
}
