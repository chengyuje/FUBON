package com.systex.jbranch.app.server.fps.crm8101;

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
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm8101")
@Scope("request")
public class CRM8101 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);

		CRM8101InputVO inputVO = (CRM8101InputVO) body;
		CRM8101OutputVO outputVO = new CRM8101OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
//		boolean isHeadMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));	// 總行
		boolean isArMGR   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));		// 業務處
		boolean isMbrMGR  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));	// 營運區
		boolean isFC      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));		// 理專
		boolean isFCH	  = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));		// 理專FCH
		boolean isPSOP    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));		// 作業人員
		boolean isFAIA    = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));		// 輔銷人員
		boolean isPAO     = xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));		// 作業人員

		// 取得客戶明細
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();

		sb.append("SELECT TO_CHAR((SELECT PABTH_UTIL.FC_getBusiDay(LAST_DAY(TO_DATE(SNAP_YYYYMM || '01', 'YYYYMMDD')), 'TWD', -1) FROM DUAL), 'YYYY/MM/DD') AS SNAP_YYYYMMDD, ");
		sb.append("       SNAP_YYYYMM AS SNAP_YYYYMM, ");
		sb.append("       M.CUST_ID, ");
		sb.append("       CM.CUST_NAME, ");
		sb.append("       M.CUST_KYC, ");
		sb.append("       TRIM(CASE WHEN M.CUST_KYC = 'C4' THEN '–' ELSE TO_CHAR(M.RISK_VALUE, '9.99') END) AS RISK_VALUE, ");
		sb.append("       M.REF_MK_VALUE, ");
		sb.append("       M.REF_ROI, ");
		sb.append("       PAR.PARAM_NAME AS REF_ROF_DEFAULT, ");
		sb.append("       CASE WHEN TO_NUMBER(M.REF_ROI) < TO_NUMBER(PAR.PARAM_NAME) THEN 'Y' ELSE 'N' END AS ERROR_ICON, ");
		sb.append("       M.PROD_RISK_P1_PR, ");
		sb.append("       M.PROD_RISK_P2_PR, ");
		sb.append("       M.PROD_RISK_P3_PR, ");
		sb.append("       M.PROD_RISK_P4_PR, ");
		sb.append("       M.RISK_SUM, ");
		sb.append("       M.VALIDATE_YN ");
		sb.append("FROM TBCRM_WMG_HAIA_M M ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CM ON M.CUST_ID = CM.CUST_ID ");
		sb.append("LEFT JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'CRM.WARNING_THRESHOLD' AND PAR.PARAM_CODE = M.CUST_KYC ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON CM.BRA_NBR = DEFN.BRANCH_NBR ");
		sb.append("WHERE M.SNAP_YYYYMM = ( ");
		sb.append("  SELECT MAX(SNAP_YYYYMM) ");
		sb.append("  FROM TBCRM_WMG_HAIA_M ");
		sb.append("  WHERE CUST_ID = :custID ");
		sb.append("  GROUP BY CUST_ID ");
		sb.append(") ");
		sb.append("AND M.CUST_ID = :custID ");
		
		// UHRM 主管類
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
			!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sb.append("AND EXISTS (SELECT 'X' FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.UHRM_CODE = CM.AO_CODE) ");
		} else if (StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sb.append("AND EXISTS (SELECT 'X' FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.UHRM_CODE = CM.AO_CODE AND UHRM.EMP_ID = :loginID) ");

			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else {
			// BS
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0 &&
				!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "bs")) {
				sb.append("AND EXISTS (SELECT 'X' FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = CM.AO_CODE) ");
			} else if (isArMGR || isMbrMGR || isFC || isPSOP || isFCH || isPAO) {
				sb.append("AND DEFN.BRANCH_NBR IN (:brNbrList) ");
				
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			} else if (isFAIA) {
				sb.append("AND EXISTS( ");
				sb.append("  SELECT 'X' ");
				sb.append("  FROM TBCRM_CUST_MAST A ");
				sb.append("  WHERE V.CUST_ID = A.CUST_ID ");
				sb.append("  AND EXISTS ( ");
				sb.append("    SELECT 'X' ");
				sb.append("    FROM VWORG_AO_INFO AI ");
				sb.append("    WHERE EXISTS (SELECT 'X' FROM TBORG_FAIA FAIA WHERE FAIA.EMP_ID = :loginID AND AI.BRA_NBR = FAIA.BRANCH_NBR) ");
				sb.append("    AND A.AO_CODE = AI.AO_CODE ");
				sb.append("  )");
				sb.append(")");
				
				queryCondition.setObject("loginID", ws.getUser().getUserID());
			}
		}
		
		queryCondition.setObject("custID", inputVO.getCustID());

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> custList = dam.exeQuery(queryCondition);

		outputVO.setCustList(dam.exeQuery(queryCondition));

		// 取得客戶產品幣別分佈
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();

		if (custList.size() > 0) {
			sb.append("SELECT PAR.PARAM_NAME AS CUR_TYPE_NAME, CUR.SNAP_YYYYMM, CUR.CUST_ID, CUR.CUR_TYPE, CUR.CUR_MK_VALUE, CUR.CUR_PR ");
			sb.append("FROM TBCRM_WMG_HAIA_CUR CUR ");
			sb.append("LEFT JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'FPS.CURRENCY' AND CUR.CUR_TYPE = PAR.PARAM_CODE ");
			sb.append("WHERE CUR.CUST_ID = :custID ");
			sb.append("AND CUR.SNAP_YYYYMM = :snapYYYYMM ");
			sb.append("ORDER BY CASE WHEN CUR.CUR_TYPE = 'TWD' THEN 999 ELSE TO_NUMBER(CUR.CUR_PR) END DESC ");

			queryCondition.setObject("custID", custList.get(0).get("CUST_ID"));
			queryCondition.setObject("snapYYYYMM", custList.get(0).get("SNAP_YYYYMM"));

			queryCondition.setQueryString(sb.toString());

			outputVO.setPrdCurrList(dam.exeQuery(queryCondition));
		}

		// 取得客戶產品分佈
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();

		if (custList.size() > 0) {
			sb.append("SELECT PAR.PARAM_NAME AS PROD_TYPE, PROD.PROD_MK_VALUE, TO_NUMBER(NVL(PROD.PROD_PR, 0)) AS PROD_PR ");
			sb.append("FROM TBSYSPARAMETER PAR ");
			sb.append("LEFT JOIN TBCRM_WMG_HAIA_PROD PROD ON PROD.PROD_TYPE = PAR.PARAM_NAME AND PROD.CUST_ID = :custID AND PROD.SNAP_YYYYMM = :snapYYYYMM ");
			sb.append("WHERE PAR.PARAM_TYPE = 'CRM.CRM8101_PRD' ");
			sb.append("ORDER BY TO_NUMBER(NVL(PROD.PROD_PR, 0)) DESC, PAR.PARAM_ORDER ");

			queryCondition.setObject("custID", custList.get(0).get("CUST_ID"));
			queryCondition.setObject("snapYYYYMM", custList.get(0).get("SNAP_YYYYMM"));

			queryCondition.setQueryString(sb.toString());

			outputVO.setPrdList(dam.exeQuery(queryCondition));
		}

		// 取得客戶損益
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();

		if (custList.size() > 0) {
			sb.append("SELECT C_PAR.PARAM_NAME AS CUR_TYPE_NAME, ");
			sb.append("       A.CUR_TYPE, ");
			sb.append("       A.PROD_TYPE, ");
			sb.append("       A.REF_MK_VALUE, ");
			sb.append("       A.REF_INVEST, ");
			sb.append("       A.REF_DIVIDEND, ");
			sb.append("       A.REF_INCOME, ");
			sb.append("       A.REF_ROI ");
			sb.append("FROM ( ");
			sb.append("  SELECT BASE.CUR_TYPE, ");
			sb.append("         NULL AS PROD_TYPE, ");
			sb.append("         SUM(INCOME.REF_MK_VALUE) AS REF_MK_VALUE, ");
			sb.append("         SUM(INCOME.REF_INVEST) AS REF_INVEST, ");
			sb.append("         SUM(INCOME.REF_DIVIDEND) AS REF_DIVIDEND, ");
			sb.append("         SUM(INCOME.REF_MK_VALUE) - SUM(INCOME.REF_INVEST) + SUM(INCOME.REF_DIVIDEND) AS REF_INCOME, ");
			sb.append("         ROUND((SUM(INCOME.REF_MK_VALUE) - SUM(INCOME.REF_INVEST) + SUM(INCOME.REF_DIVIDEND)) / SUM(INCOME.REF_INVEST) * 100, 2) AS REF_ROI ");
			sb.append("  FROM ( ");
			sb.append("    SELECT DISTINCT CUR_TYPE ");
			sb.append("    FROM TBCRM_WMG_HAIA_INCOME ");
			sb.append("    WHERE CUST_ID = :custID ");
			sb.append("    AND SNAP_YYYYMM = :snapYYYYMM ");
			sb.append("  ) BASE ");
			sb.append("  LEFT JOIN TBCRM_WMG_HAIA_INCOME INCOME ON BASE.CUR_TYPE = INCOME.CUR_TYPE AND INCOME.CUST_ID = :custID AND INCOME.SNAP_YYYYMM = :snapYYYYMM ");
			sb.append("  GROUP BY BASE.CUR_TYPE ");
			sb.append("  UNION ");
			sb.append("  SELECT BASE.CUR_TYPE, PAR.PROD_TYPE, ");
			sb.append("         TO_NUMBER(INCOME.REF_MK_VALUE) AS REF_MK_VALUE, ");
			sb.append("         TO_NUMBER(INCOME.REF_INVEST) AS REF_INVEST, ");
			sb.append("         TO_NUMBER(INCOME.REF_DIVIDEND) AS REF_DIVIDEND, ");
			sb.append("         TO_NUMBER(INCOME.REF_INCOME) AS REF_INCOME, ");
			sb.append("         TO_NUMBER(REF_ROI) AS REF_ROI ");
			sb.append("  FROM ( ");
			sb.append("    SELECT DISTINCT I.CUR_TYPE ");
			sb.append("    FROM TBCRM_WMG_HAIA_INCOME I ");
			sb.append("    WHERE I.CUST_ID = :custID ");
			sb.append("    AND I.SNAP_YYYYMM = :snapYYYYMM ");
			sb.append("  ) BASE ");
			sb.append("  LEFT JOIN ( ");
			sb.append("    SELECT PARAM_NAME AS PROD_TYPE, PARAM_ORDER AS PROD_ORDER ");
			sb.append("    FROM TBSYSPARAMETER ");
			sb.append("    WHERE PARAM_TYPE = 'CRM.CRM8101_PRD' ");
			sb.append("  ) PAR ON  1 = 1 ");
			sb.append("  LEFT JOIN TBCRM_WMG_HAIA_INCOME INCOME ON BASE.CUR_TYPE = INCOME.CUR_TYPE AND INCOME.PROD_TYPE = PAR.PROD_TYPE AND INCOME.CUST_ID = :custID AND INCOME.SNAP_YYYYMM = :snapYYYYMM ");
			sb.append(") A ");
			sb.append("LEFT JOIN TBSYSPARAMETER C_PAR ON C_PAR.PARAM_TYPE = 'FPS.CURRENCY' AND A.CUR_TYPE = C_PAR.PARAM_CODE ");
			sb.append("LEFT JOIN TBSYSPARAMETER P_PAR ON P_PAR.PARAM_TYPE = 'CRM.CRM8101_PRD' AND A.PROD_TYPE = P_PAR.PARAM_NAME ");
			sb.append("WHERE 1 = 1 ");
			sb.append("AND (REF_MK_VALUE IS NOT NULL OR REF_INVEST IS NOT NULL OR REF_DIVIDEND IS NOT NULL OR REF_INCOME IS NOT NULL OR REF_ROI IS NOT NULL) ");
			sb.append("ORDER BY CASE WHEN A.CUR_TYPE = 'TWD' THEN 0 ELSE C_PAR.PARAM_ORDER END, ");
			sb.append("         CASE WHEN A.REF_MK_VALUE IS NOT NULL THEN A.REF_MK_VALUE ELSE 0 END DESC, ");
			sb.append("         CASE WHEN A.PROD_TYPE IS NULL THEN 0 ELSE 1 END, CASE WHEN A.REF_MK_VALUE IS NOT NULL THEN P_PAR.PARAM_ORDER ELSE (P_PAR.PARAM_ORDER + 900) END ");

			queryCondition.setObject("custID", custList.get(0).get("CUST_ID"));
			queryCondition.setObject("snapYYYYMM", custList.get(0).get("SNAP_YYYYMM"));

			queryCondition.setQueryString(sb.toString());

			outputVO.setIncomeList(dam.exeQuery(queryCondition));
		}

		this.sendRtnObject(outputVO);
	}
}