package com.systex.jbranch.app.server.fps.pms101;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * Description :金流名單
 * Author : 2016/07/14 KevinHsu
 * Editor : 2017/01/12 KevinHsu
 */
@Component("pms101")
@Scope("request")
public class PMS101 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS101.class);

	/**
	 * 未轉銷售計劃/已轉銷售計劃判斷
	 */
	public void queryDataY(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS101Object_OutputVO PMS101Object_OutputVO = new PMS101Object_OutputVO();

		dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ROWNUM,  ALLS.* ");

		Map<String, Object> listy = new HashMap<String, Object>();
		
		listy.put("listy", queryData_List(body, header, "Y"));
		
		PMS101Object_OutputVO.setAllList(listy);
		
		this.sendRtnObject(PMS101Object_OutputVO);
	}

	public void queryDataN(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS101Object_OutputVO PMS101Object_OutputVO = new PMS101Object_OutputVO();

		dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ROWNUM,  ALLS.* ");

		Map<String, Object> listy = new HashMap<String, Object>();
		
		listy.put("listn", queryData_List(body, header, "N"));
		
		PMS101Object_OutputVO.setAllList(listy);
		
		this.sendRtnObject(PMS101Object_OutputVO);
	}

	/**
	 * 主查詢
	 */
	public PMS101OutputVO queryData_List(Object body, IPrimitiveMap header, String Y_N) throws JBranchException, ParseException {

		PMS101InputVO inputVO = (PMS101InputVO) body;
		PMS101OutputVO outputVO = new PMS101OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		XmlInfo xmlInfo = new XmlInfo();
		boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isPSOP = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isOPMGR = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRM = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMBMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMBMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getsCreDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		// 筆數限制
		Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);

		try {
			//2017/05/27  金流主檔重新組裝
			sb.append("WITH TURNSELL AS ( ");
			sb.append("  SELECT MASTO.EXP_CF_TOTAL, ");
			sb.append("         MASTO.REC_CF_TOTAL, ");
			sb.append("         MASTO.REC_CF_TXN, ");
			sb.append("         MASTO.REC_CF_YTD_DEP, ");
			sb.append("         MASTO.CUST_ID, ");
			sb.append("         MASTO.CUST_NAME, ");
			sb.append("         MASTO.DATA_DATE, ");
			sb.append("         MASTO.REL_CODE, ");
			sb.append("         MASTO.REGION_CENTER_ID, ");
			sb.append("         REC.REGION_CENTER_NAME, ");
			sb.append("         MASTO.BRANCH_AREA_ID, ");
			sb.append("         REC.BRANCH_AREA_NAME, ");
			sb.append("         MASTO.BRANCH_NBR, ");
			sb.append("         REC.BRANCH_NAME, ");
			sb.append("         MASTO.AO_CODE, ");
			sb.append("         F.EMP_ID, ");
			sb.append("         F.DEPT_ID, ");
			sb.append("         0 AS REC_CF_CONT_FLAG, ");
			sb.append("         1 AS TSP, ");
			sb.append("         (NVL(MASTO.REC_CF_TOTAL, 0) - NVL(MASTO.REC_CF_TXN, 0)) AS REC_CF_BAL, ");
			sb.append("         CASE WHEN NVL(SP.EST_AMT, 0) > NVL(MASTO.REC_CF_TOTAL, 0) THEN NVL(MASTO.REC_CF_TOTAL, 0) ELSE NVL(SP.EST_AMT, 0) END AS REC_CF_PLAN, ");
			sb.append("         CASE WHEN NVL(SP.EST_AMT, 0) - NVL(MASTO.REC_CF_TOTAL, 0) > 0 THEN NVL(SP.EST_AMT, 0) - NVL(MASTO.REC_CF_TOTAL, 0) ELSE 0 END AS EXP_CF_PLAN, ");
			sb.append("         (NVL(MASTO.EXP_CF_TOTAL, 0) - (CASE WHEN NVL(SP.EST_AMT, 0) - NVL(MASTO.REC_CF_TOTAL, 0) > 0 THEN NVL(SP.EST_AMT, 0) - NVL(MASTO.REC_CF_TOTAL, 0) ELSE 0 END)) AS EXP_CF_BAL, ");
			sb.append("         CASE WHEN (NVL(MASTO.REC_CF_YTD_DEP, 0) - (NVL(MASTO.REC_CF_TOTAL, 0) - NVL(MASTO.REC_CF_TXN, 0))) < 0 THEN 'Y' ELSE 'N' END AS REC_CF_LOS_FLAG ");
			sb.append("  FROM ( ");
			sb.append("    SELECT *");
			sb.append("    FROM TBPMS_CUS_CF_MAST ");
			sb.append("    WHERE CUST_ID IN ( ");
			sb.append("      SELECT DISTINCT(CUST_ID) ");
			sb.append("      FROM TBPMS_SALES_PLANS ");
			sb.append("      WHERE SRC_TYPE = '1' ");
			sb.append("      AND PLAN_YEARMON BETWEEN :DATA_DATEE ");
			sb.append("      AND TO_CHAR(ADD_MONTHS(TO_DATE(:DATA_DATEE || 01, 'YYYYMMDD'), 50), 'YYYYMM')");
			sb.append("    ) ");
			sb.append("    AND SUBSTR(DATA_DATE, 0, 6) = :DATA_DATEE");
			sb.append("  ) MASTO　");
			sb.append("  LEFT JOIN (SELECT DISTINCT EMP_ID, AO_CODE, EMP_DEPT_ID AS DEPT_ID FROM VWORG_EMP_INFO) F ON F.AO_CODE = MASTO.AO_CODE "); 
			sb.append("  LEFT JOIN TBPMS_ORG_REC_N REC ON REC.BRANCH_AREA_ID = MASTO.BRANCH_AREA_ID AND REC.BRANCH_NBR = MASTO.BRANCH_NBR AND REC.REGION_CENTER_ID = MASTO.REGION_CENTER_ID AND TO_DATE(MASTO.DATA_DATE,'YYYYMMDD') BETWEEN REC.START_TIME AND REC.END_TIME  ");
			sb.append("  LEFT JOIN (");
			sb.append("    SELECT NVL(SUM(EST_AMT),0) AS EST_AMT, CUST_ID ");
			sb.append("    FROM TBPMS_SALES_PLANS ");
			sb.append("    WHERE SRC_TYPE = '1' ");
			sb.append("    AND PLAN_YEARMON BETWEEN :DATA_DATEE AND TO_CHAR(ADD_MONTHS(TO_DATE(:DATA_DATEE || 01,'YYYYMMDD'), 50), 'YYYYMM') ");
			sb.append("    GROUP BY CUST_ID ");
			sb.append("  ) SP ON SP.CUST_ID = MASTO.CUST_ID ");
			sb.append(") ");
			sb.append(", NOTURNSELL AS ( ");
			sb.append("  SELECT MASTO.EXP_CF_TOTAL, ");
			sb.append("         MASTO.REC_CF_TOTAL, ");
			sb.append("         MASTO.REC_CF_TXN, ");
			sb.append("         MASTO.REC_CF_CONT_FLAG, ");
			sb.append("         MASTO.REC_CF_YTD_DEP, ");
			sb.append("         MASTO.CUST_ID, ");
			sb.append("         MASTO.CUST_NAME, ");
			sb.append("         MASTO.DATA_DATE, ");
			sb.append("         MASTO.REL_CODE, ");
			sb.append("         MASTO.REGION_CENTER_ID, ");
			sb.append("         REC.REGION_CENTER_NAME, ");
			sb.append("         MASTO.BRANCH_AREA_ID, ");
			sb.append("         REC.BRANCH_AREA_NAME, ");
			sb.append("         MASTO.BRANCH_NBR, ");
			sb.append("         REC.BRANCH_NAME, ");
			sb.append("         MASTO.AO_CODE, ");
			sb.append("         F.EMP_ID, ");
			sb.append("         F.DEPT_ID, ");
			sb.append("         0 AS TSP, ");
			sb.append("         CASE WHEN (NVL(MASTO.REC_CF_YTD_DEP, 0) - (NVL(MASTO.REC_CF_TOTAL, 0) - NVL(MASTO.REC_CF_TXN, 0))) < 0 THEN 'Y' ELSE 'N' END AS REC_CF_LOS_FLAG ");
			sb.append("  FROM ( ");
			sb.append("    SELECT * ");
			sb.append("    FROM TBPMS_CUS_CF_MAST ");
			sb.append("    WHERE CUST_ID NOT IN ( ");
			sb.append("      SELECT DISTINCT(CUST_ID) ");
			sb.append("      FROM TBPMS_SALES_PLANS ");
			sb.append("      WHERE SRC_TYPE = '1' ");
			sb.append("      AND PLAN_YEARMON BETWEEN :DATA_DATEE AND TO_CHAR(ADD_MONTHS(TO_DATE(:DATA_DATEE || 01, 'YYYYMMDD'), 50), 'YYYYMM')");
			sb.append("    ) ");
			sb.append("    AND SUBSTR(DATA_DATE, 0, 6) = :DATA_DATEE");
			sb.append("  ) MASTO　");
			sb.append("  LEFT JOIN (SELECT DISTINCT EMP_ID, AO_CODE, EMP_DEPT_ID AS DEPT_ID FROM VWORG_EMP_INFO) F ON F.AO_CODE = MASTO.AO_CODE "); 
			sb.append("  LEFT JOIN TBPMS_ORG_REC_N REC ON REC.BRANCH_AREA_ID = MASTO.BRANCH_AREA_ID AND REC.BRANCH_NBR = MASTO.BRANCH_NBR AND REC.REGION_CENTER_ID = MASTO.REGION_CENTER_ID AND TO_DATE(MASTO.DATA_DATE, 'YYYYMMDD') BETWEEN REC.START_TIME AND REC.END_TIME ");
			sb.append(") ");

			//前端查詢標籤
			if (!StringUtils.isBlank(Y_N)) {
				switch (Y_N) {
					case "Y":
						sb.append("SELECT ROWNUM, ALLS.* ");
						sb.append("FROM ( ");
						sb.append("  SELECT * ");
						sb.append("  FROM TURNSELL T ");
						
						if (!StringUtils.isBlank(inputVO.getPROD_TYPE())) {
							sb.append("INNER JOIN ( ");
							sb.append("  SELECT PRD_TYPE, CUST_ID AS CUSTID, DATA_DATE AS DATA_D ");
							sb.append("  FROM TBPMS_CUS_CF_DTL ");
							sb.append("  GROUP BY PRD_TYPE, CUST_ID, DATA_DATE ");
							sb.append(") DTL ON T.CUST_ID = DTL.CUSTID AND SUBSTR(DTL.DATA_D, 0, 6) = :DATA_DATEE AND DTL.PRD_TYPE = :PRD_TYPE ");
							
							queryCondition.setObject("PRD_TYPE", inputVO.getPROD_TYPE().trim());
						}
						
						sb.append(" WHERE 1 = 1 ");
						
						break;
					case "N":
						sb.append("SELECT ROWNUM, ALLS.* ");
						sb.append("FROM ( ");
						sb.append("  SELECT * ");
						sb.append("  FROM NOTURNSELL T ");
						
						if (!StringUtils.isBlank(inputVO.getPROD_TYPE())) {
							sb.append("  INNER JOIN ( ");
							sb.append("    SELECT PRD_TYPE, CUST_ID AS CUSTID, DATA_DATE AS DATA_D ");
							sb.append("    FROM TBPMS_CUS_CF_DTL ");
							sb.append("    GROUP BY PRD_TYPE, CUST_ID, DATA_DATE");
							sb.append("  ) DTL ");
							sb.append(" ON T.CUST_ID = DTL.CUSTID AND SUBSTR(DTL.DATA_D, 0, 6) = :DATA_DATEE AND DTL.PRD_TYPE = :PRD_TYPE ");
							
							queryCondition.setObject("PRD_TYPE", inputVO.getPROD_TYPE().trim());
						}
						
						sb.append(" WHERE 1 = 1 ");
						
						break;
				}
			}

			switch (getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG).toString()) {
				case "UHRM":
					sb.append("  AND EMP_ID = :loginID ");
					
					queryCondition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
					break;
				case "uhrmMGR":
				case "uhrmBMMGR":
					sb.append("  AND ( ");
					sb.append("           T.EMP_ID IS NOT NULL ");
					sb.append("       AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE T.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
					sb.append("  ) ");
					
					queryCondition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
					queryCondition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
					
					break;
				default:
					//營運區
					if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
						sb.append("  AND T.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :branchArea) ");
						
						queryCondition.setObject("branchArea", inputVO.getBranch_area_id()); // 營運區
					} else {
						if (!isHANDMGR) {
							sb.append("  AND T.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID IN (:branch_area_id)) ");
							queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
						}
					}
					
					//分行
					if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
						sb.append("  AND T.BRANCH_NBR = :branch "); 
						queryCondition.setObject("branch", inputVO.getBranch_nbr()); 
					} else {
						if (!isHANDMGR) {
							sb.append("  AND T.BRANCH_NBR IN (:branch_nbr) ");
							queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
						}
					}
					
					break;
			}

			//員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sb.append("  AND T.AO_CODE = :AO_CODEE "); // 分行
				queryCondition.setObject("AO_CODEE", inputVO.getAo_code()); //員工
			} else {
				if (isFC || isPSOP || isUHRM) {
					sb.append("  AND T.AO_CODE IN (:ao_code) ");
					queryCondition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			
			//客戶ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				sb.append("  AND T.CUST_ID = :CUST_IDD "); // 客戶id
				queryCondition.setObject("CUST_IDD", inputVO.getCUST_ID().toUpperCase().trim());
			}

			//前端已入帳/未聯繫
			if (!StringUtils.isBlank(inputVO.getTYPE())) {
				switch (inputVO.getTYPE()) {
					case "Y":
						if (!StringUtils.isBlank(Y_N)) {
							if ("N".equals(Y_N)) {
								sb.append("  AND T.REC_CF_CONT_FLAG = 0 "); //已入帳未聯繫
							}
						}
						
						break;
					case "N":
						sb.append("  AND T.REC_CF_LOS_FLAG = 'Y' "); //已入帳且流失
	
						break;
				}
			}
			
			//日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				sb.append("  AND SUBSTR(T.DATA_DATE, 0, 6) = :DATA_DATEE ");
				queryCondition.setObject("DATA_DATEE", inputVO.getsCreDate());
			} else if (StringUtils.isBlank(inputVO.getFLAG())) {
				sb.append("  AND T.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBPMS_CUS_CF_MAST) ");
			}
			
			if (StringUtils.equals(inputVO.getFLAG(), "Y")) {
				sb.append("  AND SUBSTR(T.DATA_DATE, 0, 6) = TO_CHAR(TRUNC(SYSDATE, 'MONTH') - 1 , 'YYYYMM')");
			}

			sb.append(") ALLS ");
			sb.append("WHERE ROWNUM <= :QRY_MAX_LIMIT ");
			sb.append("ORDER BY ROWNUM ");
						
			queryCondition.setObject("QRY_MAX_LIMIT", qry_max_limit_xml.get("2000"));

			queryCondition.setQueryString(sb.toString());

			outputVO.setResultList(dam.exeQuery(queryCondition)); 

			return outputVO;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 明細查詢
	 */
	public void getLeadsList(Object body, IPrimitiveMap header) throws JBranchException {

		PMS101InputVO inputVO = (PMS101InputVO) body;
		PMS101OutputVO outputVO = new PMS101OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		try {
			//==查詢==
			sb.append("SELECT PRD_TYPE, PRD_NAME, CF_TYPE, EXP_AMT, ");
			sb.append("       TO_CHAR(EXP_DT, 'YYYY/MM/DD') AS EXP_DT ");
			sb.append("FROM TBPMS_CUS_CF_DTL ");
			sb.append("WHERE 1 = 1 ");
			
			if (!StringUtils.isBlank(inputVO.getCUST_ID()))
				sb.append(" AND CUST_ID = :CUST_IDDD ");
			
			if (!StringUtils.isBlank(inputVO.getDATA_DATE()))
				sb.append(" AND SUBSTR(DATA_DATE, 0, 6) = SUBSTR(:DATA_DATE, 0, 6) ");
			
			queryCondition.setObject("CUST_IDDD", inputVO.getCUST_ID());
			queryCondition.setObject("DATA_DATE", inputVO.getDATA_DATE());
			queryCondition.setQueryString(sb.toString());

			outputVO.setResultList(dam.exeQuery(queryCondition));
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
}