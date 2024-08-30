package com.systex.jbranch.app.server.fps.crm381;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/08/31
 * 
 */
@Component("crm381")
@Scope("request")
public class CRM381 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;

	public void ao_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM381InputVO inputVO = (CRM381InputVO) body;
		CRM381OutputVO return_VO = new CRM381OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.EMP_ID, ");
		sql.append("       A.EMP_NAME || '(' || CASE WHEN A.TYPE = '1' THEN '主' WHEN A.TYPE = '2' THEN '副' WHEN A.TYPE = '3' THEN '維護' END || ')' AS EMP_NAME, ");
		sql.append("       A.AO_CODE, ");
		sql.append("       A.ROLE_ID AS AO_JOB_RANK, ");
		sql.append("       A.BRA_NBR AS BRANCH_NBR, ");
		sql.append("       A.AREA_ID AS BRANCH_AREA_ID, ");
		sql.append("       A.CENTER_ID AS REGION_CENTER_ID, ");
		sql.append("       A.TYPE, ");
		sql.append("       A.EMP_NAME AS EMP_NAME_ONLY ");
		sql.append("FROM VWORG_AO_INFO A ");
		//		sql.append("LEFT JOIN TBORG_SALES_AOCODE B on A.AO_CODE = B.AO_CODE and A.EMP_ID = B.EMP_ID ");
		sql.append("WHERE A.AO_CODE IS NOT NULL ");

		// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
			sql.append("AND EXISTS (SELECT 'X' FROM VWORG_EMP_BS_INFO BS WHERE A.AO_CODE = BS.BS_CODE) ");
		}

		sql.append("AND A.BRA_NBR IN (:brNbrList) ");
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND A.CENTER_ID = :centerID ");
			queryCondition.setObject("centerID", inputVO.getRegion_center_id());
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND A.AREA_ID = :areaID ");
			queryCondition.setObject("areaID", inputVO.getBranch_area_id());
		}

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND A.BRA_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		}

		sql.append(" ORDER BY A.AO_CODE ");

		queryCondition.setQueryString(sql.toString());

		return_VO.setAo_list(dam.exeQuery(queryCondition));
		sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM381InputVO inputVO = (CRM381InputVO) body;
		CRM381OutputVO return_VO = new CRM381OutputVO();
		dam = this.getDataAccessManager();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //區域中心主管
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運督導
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2); //個金主管

		QueryConditionIF queryCondition1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql1 = new StringBuffer();
		sql1.append("	SELECT  ");
		//客戶數
		sql1.append("		D.AO_CODE, D.TYPE AS AO_CODE_TYPE, D.EMP_NAME, D.ROLE_NAME, ");
		sql1.append("		NVL(D.BF_CUST_NO,0) AS BF_CUST_NO, NVL(H.IN_CUST_NO,0) AS IN_CUST_NO, NVL(L.OUT_CUST_NO,0) AS OUT_CUST_NO, ");
		sql1.append("       (NVL(D.BF_CUST_NO,0)+NVL(H.IN_CUST_NO,0)-NVL(L.OUT_CUST_NO,0)) AS AF_CUST_NO, ");
		//調整前AUM
		//2018-12-11 by Jacky 增加調整前A版塊AUM、調整前C版塊AUM、調整後A版塊AUM、調整後C版塊AUM
		//加上AO_CODE TYPE 顯示主code, 副code , 維護Code
		sql1.append("		NVL(D.BF_AUM,0)AS BF_AUM , NVL(D.BF_A_AUM,0) AS BF_A_AUM, NVL(D.BF_C_AUM,0) AS BF_C_AUM, ");
		sql1.append("		NVL(D.BF_INVS_AUM,0) AS BF_INVS_AUM, NVL(D.BF_INV_AUM,0)AS BF_INV_AUM ,NVL(D.BF_INS_AUM,0)AS BF_INS_AUM, ");
		//平均AUM
		//sql1.append("		NVL(D.BF_INVS_AUM/NULLIF((D.BF_INVS_AUM+D.BF_DEP_AUM),0),0)AS AVG_AUM, ");
		sql1.append("       B.IN_INVS_AUM/B.AUM_TOTAL AS AVG_AUM, ");
		//移入移出AUM
		sql1.append("		NVL(H.IN_AUM,0)AS IN_AUM , NVL(L.OUT_AUM,0)AS OUT_AUM , ");
		//調整後AUM
		sql1.append("		(NVL(D.BF_AUM,0)+NVL(H.IN_AUM,0)-NVL(L.OUT_AUM,0)) AS AF_AUM, ");
		sql1.append("		(NVL(D.BF_A_AUM,0)+NVL(H.IN_A_AUM,0)-NVL(L.OUT_A_AUM,0)) AS AF_A_AUM, ");
		sql1.append("		(NVL(D.BF_C_AUM,0)+NVL(H.IN_C_AUM,0)-NVL(L.OUT_C_AUM,0)) AS AF_C_AUM, ");

		sql1.append("		(NVL(D.BF_INVS_AUM,0) + NVL(H.IN_INVS_AUM,0) - NVL(L.OUT_INVS_AUM,0)) AS AF_INVS_AUM, ");
		sql1.append("		(NVL(D.BF_INV_AUM,0) + NVL(H.IN_INV_AUM,0) - NVL(L.OUT_INV_AUM,0)) AS AF_INV_AUM, ");
		sql1.append("		(NVL(D.BF_INS_AUM,0) + NVL(H.IN_INS_AUM,0) - NVL(L.OUT_INS_AUM,0)) AS AF_INS_AUM  ");
		sql1.append("FROM  ");
		//		sql1.append("		(SELECT A.BRA_NBR, A.AO_CODE, A.EMP_NAME, A.ROLE_NAME, SUM(CASE WHEN A.AVG_AUM_AMT > 0 THEN 1 ELSE 0 END) AS BF_CUST_NO, ");
		sql1.append("		(SELECT A.BRA_NBR, A.AO_CODE, A.TYPE, A.EMP_NAME, A.ROLE_NAME, SUM(CASE WHEN A.CUST_FLAG = 'Y' THEN 1 ELSE 0 END) AS BF_CUST_NO, ");
		sql1.append("               SUM(A.AVG_AUM_AMT) AS BF_AUM, ");
		sql1.append("               SUM(A.A_AVG_AUM) AS BF_A_AUM, ");
		sql1.append("               SUM(A.C_AVG_AUM) AS BF_C_AUM, ");
		sql1.append("				SUM( (A.BFUND_AUM+A.DOM_FUND_AUM+A.OVS_FUND_AUM)+ ");
		sql1.append("                    (A.OVS_STK_AUM+A.OVS_ETF_AUM)+ ");
		sql1.append("                    (A.SN_AUM+A.SI_AUM+A.FBOND_AUM)+ ");
		sql1.append("                    (A.FDCD_AUM+A.MNY_TRST_AUM+A.GOLD_BOOK_AUM)+ ");
		sql1.append("                     A.INS_AUM )AS BF_INVS_AUM, ");
		sql1.append("				SUM((A.BFUND_AUM+A.DOM_FUND_AUM+A.OVS_FUND_AUM)+ ");
		sql1.append("				(A.OVS_STK_AUM+A.OVS_ETF_AUM)+ ");
		sql1.append("				(A.SN_AUM+A.SI_AUM+A.FBOND_AUM)+ ");
		sql1.append("				(A.FDCD_AUM+A.MNY_TRST_AUM+A.GOLD_BOOK_AUM)) AS BF_INV_AUM, ");
		sql1.append("               SUM(A.INS_AUM) AS BF_INS_AUM, ");
		sql1.append("				SUM(A.SAV_AUM+A.CD_AUM+A.FSAV_AUM+A.FCD_AUM) AS BF_DEP_AUM ");
		sql1.append("FROM ( ");
		// 2017/11/13 jacky
		sql1.append("      SELECT B.CUST_ID,B.BRA_NBR,B.AO_CODE,A.TYPE,A.EMP_NAME,R.ROLE_NAME,C.AVG_AUM_AMT, ");
		sql1.append("      CASE WHEN CN.CO_ACCT_YN = 'Y' THEN 0 ELSE C.AVG_AUM_AMT END AS A_AVG_AUM,");
		sql1.append("      CASE WHEN CN.CO_ACCT_YN = 'Y' THEN C.AVG_AUM_AMT ELSE 0 END AS C_AVG_AUM,");
		sql1.append("      C.BFUND_AUM, C.DOM_FUND_AUM, C.OVS_FUND_AUM, C.OVS_STK_AUM, C.OVS_ETF_AUM, ");
		sql1.append("      C.SN_AUM, C.SI_AUM, C.FBOND_AUM, C.FDCD_AUM, C.MNY_TRST_AUM, C.GOLD_BOOK_AUM, C.INS_AUM, ");
		sql1.append("      C.SAV_AUM, C.CD_AUM, C.FSAV_AUM, C.FCD_AUM, 'Y' CUST_FLAG ");
		sql1.append("      FROM TBCRM_CUST_MAST B ");
		sql1.append("      LEFT JOIN TBCRM_CUST_NOTE CN ON B.CUST_ID = CN.CUST_ID ");
		sql1.append("      LEFT JOIN VWORG_AO_INFO A ON B.AO_CODE = A.AO_CODE ");
		sql1.append("      LEFT JOIN TBORG_MEMBER_ROLE MR ON A.EMP_ID = MR.EMP_ID ");
		sql1.append("      LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sql1.append("      LEFT JOIN TBCRM_CUST_LDAY_AUM_MONTHLY C ON B.CUST_ID = C.CUST_ID ");
		sql1.append("      WHERE 1=1 AND B.AO_CODE IS NOT NULL ");
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql1.append(" AND B.BRA_NBR = :branch_nbr ");
			queryCondition1.setObject("branch_nbr", inputVO.getBranch_nbr());
		}
		if (!StringUtils.isBlank(inputVO.getAo_job_rank())) {
			sql1.append(" AND A.ROLE_NAME = :ao_job_rank ");
			queryCondition1.setObject("ao_job_rank", inputVO.getAo_job_rank());
		}
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sql1.append(" AND B.AO_CODE = :ao_code ");
			queryCondition1.setObject("ao_code", inputVO.getAo_code());
		}
		sql1.append("UNION ");
		sql1.append("SELECT '' AS CUST_ID,A.BRA_NBR,A.AO_CODE,A.TYPE,A.EMP_NAME,R.ROLE_NAME, ");
		sql1.append("      0 AS AVG_AUM_AMT,0 AS A_AVG_AUM, 0 AS C_AVG_AUM, 0 AS BFUND_AUM, 0 AS DOM_FUND_AUM, 0 AS OVS_FUND_AUM, 0 AS OVS_STK_AUM, 0 AS OVS_ETF_AUM, ");
		sql1.append("      0 AS SN_AUM, 0 AS SI_AUM, 0 AS FBOND_AUM, 0 AS FDCD_AUM, 0 AS MNY_TRST_AUM, 0 AS GOLD_BOOK_AUM, 0 AS INS_AUM, ");
		sql1.append("      0 AS SAV_AUM, 0 AS CD_AUM, 0 AS FSAV_AUM, 0 AS FCD_AUM, 'N' CUST_FLAG ");
		sql1.append("      FROM VWORG_AO_INFO A ");
		//		sql1.append("      LEFT JOIN TBCRM_CUST_MAST B ON A.AO_CODE = B.AO_CODE ");
		sql1.append("      LEFT JOIN TBORG_MEMBER_ROLE MR ON A.EMP_ID = MR.EMP_ID ");
		sql1.append("      LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		//		sql1.append("      LEFT JOIN TBCRM_CUST_LDAY_AUM_MONTHLY C ON B.CUST_ID = C.CUST_ID ");
		sql1.append("      WHERE 1=1 AND A.AO_CODE IS NOT NULL ");
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql1.append(" AND A.BRA_NBR = :branch_nbr ");
			queryCondition1.setObject("branch_nbr", inputVO.getBranch_nbr());
		}
		if (!StringUtils.isBlank(inputVO.getAo_job_rank())) {
			sql1.append(" AND A.ROLE_NAME = :ao_job_rank ");
			queryCondition1.setObject("ao_job_rank", inputVO.getAo_job_rank());
		}
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sql1.append(" AND A.AO_CODE = :ao_code ");
			queryCondition1.setObject("ao_code", inputVO.getAo_code());
		}
		sql1.append(" ) A ");
		//
		sql1.append(" GROUP BY A.BRA_NBR, A.AO_CODE, A.TYPE, A.EMP_NAME, A.ROLE_NAME");
		sql1.append(" ) D ");
		//移入:客戶數、AUM	
		sql1.append("	LEFT JOIN ");
		sql1.append("		(SELECT A.AO_CODE, COUNT(B.NEW_AO_CODE) AS IN_CUST_NO,  ");
		sql1.append("               SUM(C.AVG_AUM_AMT) AS IN_AUM, ");
		sql1.append("               SUM(CASE WHEN CN.CO_ACCT_YN = 'Y' THEN 0 ELSE C.AVG_AUM_AMT END) AS IN_A_AUM, ");
		sql1.append("               SUM(CASE WHEN CN.CO_ACCT_YN = 'Y' THEN C.AVG_AUM_AMT ELSE 0 END) AS IN_C_AUM, ");
		sql1.append("				SUM( (C.BFUND_AUM+C.DOM_FUND_AUM+C.OVS_FUND_AUM)+ ");
		sql1.append("                    (C.OVS_STK_AUM+C.OVS_ETF_AUM)+ ");
		sql1.append("                    (C.SN_AUM+C.SI_AUM+C.FBOND_AUM)+ ");
		sql1.append("                    (C.FDCD_AUM+C.MNY_TRST_AUM+C.GOLD_BOOK_AUM)+ ");
		sql1.append("                     C.INS_AUM ) AS IN_INVS_AUM, ");
		sql1.append("				SUM((C.BFUND_AUM+C.DOM_FUND_AUM+C.OVS_FUND_AUM)+ ");
		sql1.append("				(C.OVS_STK_AUM+C.OVS_ETF_AUM)+ ");
		sql1.append("				(C.SN_AUM+C.SI_AUM+C.FBOND_AUM)+ ");
		sql1.append("				(C.FDCD_AUM+C.MNY_TRST_AUM+C.GOLD_BOOK_AUM)) AS IN_INV_AUM, ");
		sql1.append("               SUM(C.INS_AUM) AS IN_INS_AUM, ");
		sql1.append("				SUM(C.SAV_AUM+C.CD_AUM+C.FSAV_AUM+C.FCD_AUM) AS IN_DEP_AUM ");
		sql1.append("		FROM VWORG_AO_INFO A ");
		sql1.append("		LEFT JOIN TBCRM_TRS_AOCHG_PLIST B ON A.AO_CODE = B.NEW_AO_CODE ");
		sql1.append("		LEFT JOIN TBCRM_CUST_NOTE CN ON B.CUST_ID = CN.CUST_ID ");
		sql1.append("		LEFT JOIN TBCRM_CUST_LDAY_AUM_MONTHLY C ON B.CUST_ID = C.CUST_ID ");
		sql1.append("		WHERE 1=1 ");

		/* 縮小子QUERY查詢條件 */
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql1.append("			AND A.BRA_NBR = :branch_nbr ");
			queryCondition1.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		// 2017/6/1
		//登入者身份=總行
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql1.append(" AND (( B.TEMP_CAL_YN ='Y' AND B.NEW_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L5') ) ");
			queryCondition1.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		//登入者身份=處主管
		else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql1.append(" AND (( B.TEMP_CAL_YN ='Y' AND B.NEW_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L3') OR ");
			sql1.append(" ( B.NEW_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L5')) ");
			queryCondition1.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		//登入者身份=區域督導
		else if (mbrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql1.append(" AND (( B.TEMP_CAL_YN ='Y' AND B.NEW_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L2') OR ");
			sql1.append(" ( B.NEW_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L3') OR ");
			sql1.append(" ( B.NEW_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L5')) ");
			queryCondition1.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		//登入者身份=主管
		else if (bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql1.append(" AND (( B.TEMP_CAL_YN ='Y' AND B.NEW_AO_BRH = :brNbrList AND B.PROCESS_STATUS = 'L1') OR ");
			sql1.append(" ( B.NEW_AO_BRH = :brNbrList AND B.PROCESS_STATUS = 'L2') OR ");
			sql1.append(" ( B.NEW_AO_BRH = :brNbrList AND B.PROCESS_STATUS = 'L3') OR ");
			sql1.append(" ( B.NEW_AO_BRH = :brNbrList AND B.PROCESS_STATUS = 'L5')) ");
			queryCondition1.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		sql1.append("    GROUP BY A.AO_CODE		 ");
		sql1.append("	) H ON H.AO_CODE = D.AO_CODE ");
		//移出:客戶數、AUM	
		sql1.append("	LEFT JOIN ");
		sql1.append("			(SELECT B.ORG_AO_CODE AS AO_CODE, COUNT(B.ORG_AO_CODE) AS OUT_CUST_NO,  ");
		sql1.append("               SUM(C.AVG_AUM_AMT) AS OUT_AUM, ");
		sql1.append("               SUM(CASE WHEN CN.CO_ACCT_YN = 'Y' THEN 0 ELSE C.AVG_AUM_AMT END) AS OUT_A_AUM, ");
		sql1.append("               SUM(CASE WHEN CN.CO_ACCT_YN = 'Y' THEN C.AVG_AUM_AMT ELSE 0 END) AS OUT_C_AUM, ");
		sql1.append("				SUM( (C.BFUND_AUM+C.DOM_FUND_AUM+C.OVS_FUND_AUM)+ ");
		sql1.append("                    (C.OVS_STK_AUM+C.OVS_ETF_AUM)+ ");
		sql1.append("                    (C.SN_AUM+C.SI_AUM+C.FBOND_AUM)+ ");
		sql1.append("                    (C.FDCD_AUM+C.MNY_TRST_AUM+C.GOLD_BOOK_AUM)+ ");
		sql1.append("                     C.INS_AUM )AS OUT_INVS_AUM, ");
		sql1.append("				SUM((C.BFUND_AUM+C.DOM_FUND_AUM+C.OVS_FUND_AUM)+ ");
		sql1.append("				(C.OVS_STK_AUM+C.OVS_ETF_AUM)+ ");
		sql1.append("				(C.SN_AUM+C.SI_AUM+C.FBOND_AUM)+ ");
		sql1.append("				(C.FDCD_AUM+C.MNY_TRST_AUM+C.GOLD_BOOK_AUM)) AS OUT_INV_AUM, ");
		sql1.append("               SUM(C.INS_AUM) AS OUT_INS_AUM, ");
		sql1.append("				SUM(C.SAV_AUM+C.CD_AUM+C.FSAV_AUM+C.FCD_AUM) AS OUT_DEP_AUM ");
		//2017-12-4 by　Jacky MANTIS單號:0003950 移出AOCODE可能已不在原分行
		sql1.append("		FROM TBCRM_TRS_AOCHG_PLIST B ");
		//		sql1.append("		LEFT JOIN TBCRM_TRS_AOCHG_PLIST B ON A.AO_CODE = B.ORG_AO_CODE ");
		sql1.append("		LEFT JOIN TBCRM_CUST_NOTE CN ON B.CUST_ID = CN.CUST_ID ");
		sql1.append("		LEFT JOIN TBCRM_CUST_LDAY_AUM_MONTHLY C ON B.CUST_ID = C.CUST_ID  ");
		sql1.append("		WHERE 1=1  ");
		//		sql1.append("		AND B.ORG_AO_CODE = A.AO_CODE ");

		/* 縮小子QUERY查詢條件 */
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql1.append("			AND B.ORG_AO_BRH = :branch_nbr ");
			queryCondition1.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		// 2017/6/1
		//登入者身份=總行
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql1.append(" AND (( B.TEMP_CAL_YN ='Y' AND B.ORG_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L5')) ");
			queryCondition1.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		//登入者身份=處主管
		else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql1.append(" AND (( B.TEMP_CAL_YN ='Y' AND B.ORG_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L3') OR ");
			sql1.append(" ( B.ORG_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L5')) ");
			queryCondition1.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		//登入者身份=區域督導
		else if (mbrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql1.append(" AND (( B.TEMP_CAL_YN ='Y' AND B.ORG_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L2') OR ");
			sql1.append(" ( B.ORG_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L3') OR ");
			sql1.append(" ( B.ORG_AO_BRH in (:brNbrList) AND B.PROCESS_STATUS = 'L5')) ");
			queryCondition1.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		//登入者身份=主管
		else if (bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql1.append(" AND (( B.TEMP_CAL_YN ='Y' AND B.ORG_AO_BRH = :brNbrList AND B.PROCESS_STATUS = 'L1') OR ");
			sql1.append(" ( B.ORG_AO_BRH = :brNbrList AND B.PROCESS_STATUS = 'L2') OR ");
			sql1.append(" ( B.ORG_AO_BRH = :brNbrList AND B.PROCESS_STATUS = 'L3') OR ");
			sql1.append(" ( B.ORG_AO_BRH = :brNbrList AND B.PROCESS_STATUS = 'L5')) ");
			queryCondition1.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		sql1.append("    GROUP BY B.ORG_AO_CODE		 ");
		sql1.append("	) L ON L.AO_CODE = D.AO_CODE ");

		// 2017/6/13  分行平均投保AUM
		sql1.append("	LEFT JOIN ");
		sql1.append("        (SELECT M.BRA_NBR, ");
		sql1.append("                SUM(C.AVG_AUM_AMT) AS AUM_TOTAL, ");
		sql1.append("                SUM( (C.BFUND_AUM+C.DOM_FUND_AUM+C.OVS_FUND_AUM)+ ");
		sql1.append("                     (C.OVS_STK_AUM+C.OVS_ETF_AUM)+ ");
		sql1.append("                     (C.SN_AUM+C.SI_AUM+C.FBOND_AUM)+ ");
		sql1.append("                     (C.FDCD_AUM+C.MNY_TRST_AUM+C.GOLD_BOOK_AUM)+ ");
		sql1.append("                      C.INS_AUM ) AS IN_INVS_AUM, ");
		sql1.append("                SUM( (C.BFUND_AUM+C.DOM_FUND_AUM+C.OVS_FUND_AUM)+ ");
		sql1.append("                     (C.OVS_STK_AUM+C.OVS_ETF_AUM)+ ");
		sql1.append("                     (C.SN_AUM+C.SI_AUM+C.FBOND_AUM)+ ");
		sql1.append("                     (C.FDCD_AUM+C.MNY_TRST_AUM+C.GOLD_BOOK_AUM)+ ");
		sql1.append("                      C.INS_AUM )/SUM(C.AVG_AUM_AMT) as AVG_INVS_AUM ");
		sql1.append("         FROM  TBCRM_CUST_LDAY_AUM_MONTHLY C, TBCRM_CUST_MAST M ");
		sql1.append("         WHERE 1=1 AND C.CUST_ID = M.CUST_ID AND M.AO_CODE IS NOT NULL ");

		/* 縮小子QUERY查詢條件 */
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql1.append("			AND M.BRA_NBR = :branch_nbr ");
			queryCondition1.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		sql1.append("         GROUP BY M.BRA_NBR ");
		sql1.append("  ) B ON B.BRA_NBR = D.BRA_NBR ");

		sql1.append("ORDER BY D.TYPE, D.ROLE_NAME, D.AO_CODE ");
		queryCondition1.setQueryString(sql1.toString());
		List list1 = dam.exeQuery(queryCondition1);
		return_VO.setResultList1(list1);
		this.sendRtnObject(return_VO);
	}

	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		CRM381InputVO inputVO = (CRM381InputVO) body;
		CRM381OutputVO return_VO = new CRM381OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();
		//2018-12-11 by Jacky 加上AO_CODE TYPE 顯示主code, 副code , 維護Code
		sql2.append("SELECT A.ROLE_NAME, A.TYPE AS AO_CODE_TYPE, A.AO_CODE, A.EMP_ID, A.EMP_NAME, A.LIMIT_BY_AUM_YN, A.TTL_CUST_NO_LIMIT_UP, A.AUM_LIMIT_UP, ");
		sql2.append("A.CUST_NO_LIMIT_UP_H, A.CUST_NO_LIMIT_UP_T, A.CUST_NO_LIMIT_UP_K, A.CUST_NO_LIMIT_UP_C, A.CUST_NO_LIMIT_UP_M, ");
		sql2.append("NVL(B.CNT_CUST_H,0) as CNT_CUST_H, NVL(B.CNT_CUST_T,0) as CNT_CUST_T, NVL(B.CNT_CUST_K,0) as CNT_CUST_K, NVL(B.CNT_CUST_C,0) as CNT_CUST_C, NVL(B.CNT_CUST_M,0) as CNT_CUST_M, ");
		sql2.append("NVL(C.AUM_CUST_H,0) as AUM_CUST_H, NVL(C.AUM_CUST_T,0) as AUM_CUST_T, NVL(C.AUM_CUST_K,0) as AUM_CUST_K, NVL(C.AUM_CUST_C,0) as AUM_CUST_C, NVL(C.AUM_CUST_M,0) as AUM_CUST_M, ");
		sql2.append("NVL(D.CNT_NC_CUST_H,0) as CNT_NC_CUST_H, NVL(D.CNT_NC_CUST_T,0) as CNT_NC_CUST_T, NVL(D.CNT_NC_CUST_K,0) as CNT_NC_CUST_K, NVL(D.CNT_NC_CUST_C,0) as CNT_NC_CUST_C, NVL(D.CNT_NC_CUST_M,0) as CNT_NC_CUST_M ");
//		sql2.append("A.CUST_NO_LIMIT_UP_V, A.CUST_NO_LIMIT_UP_A, A.CUST_NO_LIMIT_UP_B, A.CUST_NO_LIMIT_UP_M, ");
//		sql2.append("NVL(B.CNT_CUST_V,0) as CNT_CUST_V, NVL(B.CNT_CUST_A,0) as CNT_CUST_A, NVL(B.CNT_CUST_B,0) as CNT_CUST_B, NVL(B.CNT_CUST_M,0) as CNT_CUST_M, ");
//		sql2.append("NVL(C.AUM_CUST_V,0) as AUM_CUST_V, NVL(C.AUM_CUST_A,0) as AUM_CUST_A, NVL(C.AUM_CUST_B,0) as AUM_CUST_B, NVL(C.AUM_CUST_M,0) as AUM_CUST_M, ");
//		sql2.append("NVL(D.CNT_NC_CUST_V,0) as CNT_NC_CUST_V, NVL(D.CNT_NC_CUST_A,0) as CNT_NC_CUST_A, NVL(D.CNT_NC_CUST_B,0) as CNT_NC_CUST_B, NVL(D.CNT_NC_CUST_M,0) as CNT_NC_CUST_M ");
		sql2.append("FROM ( ");
		sql2.append("SELECT R.ROLE_NAME, E.TYPE, E.AO_CODE, E.EMP_ID, E.EMP_NAME, ");
		sql2.append("NVL(SD.CUST_NO_LIMIT_UP_H,0) as CUST_NO_LIMIT_UP_H, ");
		sql2.append("NVL(SD.CUST_NO_LIMIT_UP_T,0) as CUST_NO_LIMIT_UP_T, NVL(SD.CUST_NO_LIMIT_UP_K,0) as CUST_NO_LIMIT_UP_K, ");
		sql2.append("NVL(SD.CUST_NO_LIMIT_UP_C,0) as CUST_NO_LIMIT_UP_C, NVL(SD.CUST_NO_LIMIT_UP_M,0) as CUST_NO_LIMIT_UP_M, ");
		sql2.append("S.LIMIT_BY_AUM_YN, S.TTL_CUST_NO_LIMIT_UP, S.AUM_LIMIT_UP ");
		sql2.append("FROM VWORG_AO_INFO E, TBCRM_TRS_CUST_MGMT_SET S, TBORG_MEMBER_ROLE MR, TBORG_ROLE R, VWCRM_AO_CUST_NO_LIMIT SD ");
		sql2.append("WHERE 1=1 ");
		sql2.append("AND S.AO_JOB_RANK = R.ROLE_ID ");
		sql2.append("AND S.AO_JOB_RANK = SD.AO_JOB_RANK ");
		sql2.append("AND E.EMP_ID = MR.EMP_ID ");
		sql2.append("AND MR.ROLE_ID = R.ROLE_ID ");
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql2.append("AND E.CENTER_ID = :region_center_id ");
			queryCondition2.setObject("region_center_id", inputVO.getRegion_center_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql2.append("AND E.AREA_ID = :branch_area_id ");
			queryCondition2.setObject("branch_area_id", inputVO.getBranch_area_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql2.append("AND E.BRA_NBR = :branch_nbr ");
			queryCondition2.setObject("branch_nbr", inputVO.getBranch_nbr());
		}
		if (!StringUtils.isBlank(inputVO.getAo_job_rank())) {
			//sql2.append("AND S.AO_JOB_RANK = :ao_job_rank ");
			sql2.append("AND R.ROLE_NAME = :ao_job_rank ");
			queryCondition2.setObject("ao_job_rank", inputVO.getAo_job_rank());
		}
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sql2.append("AND E.AO_CODE = :ao_code ");
			queryCondition2.setObject("ao_code", inputVO.getAo_code());
		}

		sql2.append("ORDER BY R.ROLE_NAME, E.AO_CODE ");
		sql2.append(") A,  ");
		sql2.append("VWCRM_AO_CUST_NO_BY_DEGREE B, ");
		sql2.append("VWCRM_AO_CUST_AUM_BY_DEGREE C , ");
		sql2.append("VWCRM_AO_CUST_NC_NO_BY_DEGREE D ");
		sql2.append("WHERE 1=1  ");
		sql2.append("AND A.AO_CODE = B.AO_CODE(+) ");
		sql2.append("AND A.AO_CODE = C.AO_CODE(+) ");
		sql2.append("AND A.AO_CODE = D.AO_CODE(+) ");
		sql2.append("ORDER BY A.TYPE, A.ROLE_NAME, A.EMP_ID ");

		queryCondition2.setQueryString(sql2.toString());
		List list2 = dam.exeQuery(queryCondition2);
		return_VO.setResultList2(list2);
		this.sendRtnObject(return_VO);
	}

	public void inquire3(Object body, IPrimitiveMap header) throws JBranchException {
		CRM381InputVO inputVO = (CRM381InputVO) body;
		CRM381OutputVO return_VO = new CRM381OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql3 = new StringBuffer();
		sql3.append("SELECT * ");
		sql3.append("FROM   ( ");
		//經營客戶數上限
		sql3.append("        SELECT ao_job_rank, TO_CHAR(ttl_cust_no_limit_up,'FM999,999,999,999,999') sum_val ");
		sql3.append("        FROM   TBCRM_TRS_CUST_MGMT_SET ");
		sql3.append("        ) ");
		sql3.append("PIVOT  ( ");
		sql3.append("     MAX(sum_val) AS CUST_LIMIT FOR (ao_job_rank) IN ('A8A7' AS FC5, 'ABRE' AS FC4, 'A148' AS FC3, 'ABRH' AS FC2, 'A145' AS FC1) ");
		sql3.append(") ");
		sql3.append("UNION ALL ");
		sql3.append("SELECT * ");
		sql3.append("FROM   ( ");
		//AUM上限
		sql3.append("        SELECT ao_job_rank, TO_CHAR(aum_limit_up,'FM999,999,999,999,999') sum_val ");
		sql3.append("        FROM   TBCRM_TRS_CUST_MGMT_SET ");
		sql3.append(") ");
		sql3.append("PIVOT  ( ");
		sql3.append("     MAX(sum_val) AS CUST_LIMIT FOR (ao_job_rank) IN ('A8A7' AS FC5, 'ABRE' AS FC4, 'A148' AS FC3, 'ABRH' AS FC2, 'A145' AS FC1) ");
		sql3.append(") ");
		sql3.append("UNION ALL  ");
		sql3.append("SELECT * ");
		sql3.append("FROM  ( ");
		//恆富理財會員(3,000萬以上)
		sql3.append("        SELECT A.AO_JOB_RANK,A.CUST_NO_LIMIT_UP||'('||ROUND((A.CUST_NO_LIMIT_UP/B.ttl_cust_no_limit_up),3)*100||'%)' sum_val ");
		sql3.append("        FROM TBCRM_TRS_CUST_MGMT_SET_DTL A ");
		sql3.append("        LEFT JOIN TBCRM_TRS_CUST_MGMT_SET B ON A.AO_JOB_RANK = B.AO_JOB_RANK ");
		sql3.append("        WHERE A.VIP_DEGREE = 'H' ");
		sql3.append("        ) ");
		sql3.append("PIVOT  ( ");
		sql3.append("     MAX(sum_val) AS CUST_LIMIT FOR (ao_job_rank) IN ('A8A7' AS FC5, 'ABRE' AS FC4, 'A148' AS FC3, 'ABRH' AS FC2, 'A145' AS FC1) ");
		sql3.append(") ");
		sql3.append("UNION ALL ");
		sql3.append("SELECT * ");
		sql3.append("FROM  ( ");
		//智富理財會員(1,000 ~ 3,000萬)
		sql3.append("        SELECT A.AO_JOB_RANK,A.CUST_NO_LIMIT_UP||'('||ROUND((A.CUST_NO_LIMIT_UP/B.ttl_cust_no_limit_up),3)*100||'%)' sum_val ");
		sql3.append("        FROM TBCRM_TRS_CUST_MGMT_SET_DTL A ");
		sql3.append("        LEFT JOIN TBCRM_TRS_CUST_MGMT_SET B ON A.AO_JOB_RANK = B.AO_JOB_RANK ");
		sql3.append("        WHERE A.VIP_DEGREE = 'T' ");
		sql3.append("        ) ");
		sql3.append("PIVOT  ( ");
		sql3.append("     MAX(sum_val) AS CUST_LIMIT FOR (ao_job_rank) IN ('A8A7' AS FC5, 'ABRE' AS FC4, 'A148' AS FC3, 'ABRH' AS FC2, 'A145' AS FC1) ");
		sql3.append(") ");
		sql3.append("UNION ALL ");
		sql3.append("SELECT * ");
		sql3.append("FROM  ( ");
		//穩富理財會員(300 ~ 1,000萬)
		sql3.append("        SELECT C.AO_JOB_RANK,C.CUST_NO_LIMIT_UP ||'('||ROUND((C.CUST_NO_LIMIT_UP/D.ttl_cust_no_limit_up),3)*100||'%)' SUM_VAL ");
		sql3.append("        FROM TBCRM_TRS_CUST_MGMT_SET_DTL C ");
		sql3.append("        LEFT JOIN TBCRM_TRS_CUST_MGMT_SET D ON C.AO_JOB_RANK = D.AO_JOB_RANK ");
		sql3.append("        WHERE C.VIP_DEGREE = 'K' ");
		sql3.append("        ) ");
		sql3.append("PIVOT  ( ");
		sql3.append("     MAX(sum_val) AS CUST_LIMIT FOR (ao_job_rank) IN ('A8A7' AS FC5, 'ABRE' AS FC4, 'A148' AS FC3, 'ABRH' AS FC2, 'A145' AS FC1) ");
		sql3.append(") ");
		sql3.append("UNION ALL ");
		sql3.append("SELECT * ");
		sql3.append("FROM  ( ");
		//一般存戶-跨優(100 ~ 300萬)
		sql3.append("        SELECT E.AO_JOB_RANK,E.CUST_NO_LIMIT_UP||'('||ROUND((E.CUST_NO_LIMIT_UP/F.ttl_cust_no_limit_up),3)*100||'%)' SUM_VAL ");
		sql3.append("        FROM TBCRM_TRS_CUST_MGMT_SET_DTL E ");
		sql3.append("        LEFT JOIN TBCRM_TRS_CUST_MGMT_SET F ON E.AO_JOB_RANK = F.AO_JOB_RANK ");
		sql3.append("        WHERE E.VIP_DEGREE = 'C' ");
		sql3.append("        ) ");
		sql3.append("PIVOT  ( ");
		sql3.append("     MAX(sum_val) AS CUST_LIMIT FOR (ao_job_rank) IN ('A8A7' AS FC5, 'ABRE' AS FC4, 'A148' AS FC3, 'ABRH' AS FC2, 'A145' AS FC1) ");
		sql3.append(") ");
		sql3.append("UNION ALL ");
		sql3.append("SELECT * ");
		sql3.append("FROM  ( ");
		//一般 (100萬以下掛Code)
		sql3.append("        SELECT G.AO_JOB_RANK,G.CUST_NO_LIMIT_UP||'('||ROUND((G.CUST_NO_LIMIT_UP/H.ttl_cust_no_limit_up),3)*100||'%)' SUM_VAL ");
		sql3.append("       FROM TBCRM_TRS_CUST_MGMT_SET_DTL G ");
		sql3.append("        LEFT JOIN TBCRM_TRS_CUST_MGMT_SET H ON G.AO_JOB_RANK = H.AO_JOB_RANK ");
		sql3.append("        WHERE G.VIP_DEGREE = 'M' ");
		sql3.append(") ");
		sql3.append("PIVOT  ( ");
		sql3.append("     MAX(sum_val) AS CUST_LIMIT FOR (ao_job_rank) IN ('A8A7' AS FC5, 'ABRE' AS FC4, 'A148' AS FC3, 'ABRH' AS FC2, 'A145' AS FC1) ");
		sql3.append(") ");

		queryCondition3.setQueryString(sql3.toString());
		List list3 = dam.exeQuery(queryCondition3);
		return_VO.setResultList3(list3);
		this.sendRtnObject(return_VO);
	}

	/*  === 產出Excel === */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		CRM381OutputVO outputVO = (CRM381OutputVO) body;

		List<Map<String, Object>> list = outputVO.getResultList1();
		String fileName = "客戶調整前後統計表.csv";
		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			String[] records = new String[21];
			int i = 0;
			if ("1".equals(ObjectUtils.toString(map.get("AO_CODE_TYPE")))) {
				records[i] = "主Code";
			} else if ("2".equals(ObjectUtils.toString(map.get("AO_CODE_TYPE")))) {
				records[i] = "副Code";
			} else if ("3".equals(ObjectUtils.toString(map.get("AO_CODE_TYPE")))) {
				records[i] = "維護Code";
			} else {
				records[i] = "";
			}
			records[++i] = checkIsNull(map, "AO_CODE") + "-" + checkIsNull(map, "EMP_NAME"); //理專
			records[++i] = checkIsNull(map, "BF_CUST_NO"); //調整前客戶數
			records[++i] = checkIsNull(map, "IN_CUST_NO"); //+客戶移入數
			records[++i] = checkIsNull(map, "OUT_CUST_NO"); //-客戶移出數
			records[++i] = checkIsNull(map, "AF_CUST_NO"); //=調整後客戶數
			records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_AUM").toString())) + ""; //調整前AUM
			records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_A_AUM").toString())) + ""; //調整前A版塊AUM
			records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_C_AUM").toString())) + ""; //調整前C版塊AUM
			Double bfAum = (Double) Double.parseDouble(checkIsNull(map, "BF_AUM"));
			if (bfAum == 0) {
				records[++i] = "";
				records[++i] = "";
				records[++i] = "";
			} else {
				records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_INVS_AUM")) / bfAum * Double.parseDouble("100")) + "%";
				records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_INV_AUM")) / bfAum * Double.parseDouble("100")) + "%";
				records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_INS_AUM")) / bfAum * Double.parseDouble("100")) + "%";
			}
			records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "AVG_AUM")) * (int) Double.parseDouble("100")) + "";//平均投保AUM%
			records[++i] = checkIsNull(map, "IN_AUM"); //+AUM移入數
			records[++i] = checkIsNull(map, "OUT_AUM"); //-AUM移出數
			records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "AF_AUM").toString())) + ""; //=調整後AUM
			records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "AF_A_AUM").toString())) + ""; //=調整後A版塊AUM
			records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "AF_C_AUM").toString())) + ""; //=調整後C版塊AUM
			Double afAum = (Double) Double.parseDouble(checkIsNull(map, "AF_AUM"));
			if (afAum == 0) {
				records[++i] = "";
				records[++i] = "";
				records[++i] = "";
			} else {
				records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_INVS_AUM")) / afAum * Double.parseDouble("100")) + "%";
				records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_INV_AUM")) / afAum * Double.parseDouble("100")) + "%";
				records[++i] = ((Double) Double.parseDouble(checkIsNull(map, "BF_INS_AUM")) / afAum * Double.parseDouble("100")) + "%";
			}

			listCSV.add(records);
		}
		//header
		String[] csvHeader = new String[21];
		int j = 0;
		csvHeader[j] = "Code別";
		csvHeader[++j] = "理專";
		csvHeader[++j] = "調整前客戶數";
		csvHeader[++j] = "客戶移入數";
		csvHeader[++j] = "客戶移出數";
		csvHeader[++j] = "調整後客戶數";
		csvHeader[++j] = "調整前AUM";
		csvHeader[++j] = "調整前A版塊AUM";
		csvHeader[++j] = "調整前C版塊AUM";
		csvHeader[++j] = "調整前投保AUM%";
		csvHeader[++j] = "調整前投資AUM%";
		csvHeader[++j] = "調整前保險AUM%";
		csvHeader[++j] = "平均投保AUM%";
		csvHeader[++j] = "AUM移入數";
		csvHeader[++j] = "AUM移出數";
		csvHeader[++j] = "調整後AUM";
		csvHeader[++j] = "調整後A版塊AUM";
		csvHeader[++j] = "調整後C版塊AUM";
		csvHeader[++j] = "調整後投保AUM%";
		csvHeader[++j] = "調整後投資AUM%";
		csvHeader[++j] = "調整後保險AUM%";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private String addUp(Map map, String key1, String key2) {

		BigDecimal a = new BigDecimal(ObjectUtils.toString(map.get(key1)));
		BigDecimal b = new BigDecimal(ObjectUtils.toString(map.get(key2)));

		System.out.println("---------a-------" + a);
		System.out.println("---------b-------" + b);

		if (a.compareTo(new BigDecimal(0)) > 0 && b.compareTo(new BigDecimal(0)) > 0) {
			return a.divide(b).multiply(new BigDecimal(100)).toString();
		} else {
			return "";
		}
	}

}