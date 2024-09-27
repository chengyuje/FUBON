package com.systex.jbranch.app.server.fps.crm662;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_PRVVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_RELPRV_LOGVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_RELVO;
import com.systex.jbranch.app.server.fps.crm661.CRM661;
import com.systex.jbranch.app.server.fps.crm661.CRM661InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/08/18
 * 
 */
@Component("crm662")
@Scope("request")
public class CRM662 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;

	/** 初始查詢 **/
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662InputVO inputVO = (CRM662InputVO) body;
		CRM662OutputVO outputVO = new CRM662OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.CUST_ID, A.CUST_NAME, A.VIP_DEGREE, A.AO_CODE, ");
		sql.append("       B.EMP_ID, B.EMP_NAME, B.BRANCH_NAME ");
		sql.append("FROM TBCRM_CUST_MAST A ");
		sql.append("LEFT JOIN VWORG_AO_INFO B ON A.AO_CODE = B.AO_CODE ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND A.CUST_ID = :cust_id ");
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}

	/** 家庭戶查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662InputVO inputVO = (CRM662InputVO) body;
		CRM662OutputVO outputVO = new CRM662OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//VAtype = 2 : 代表是主戶
		if (StringUtils.equals(inputVO.getVA_type(), "2")) {
			sb = new StringBuffer();
			
			sb.append("SELECT P.SEQ, ");
			sb.append("       P.PRV_MBR_YN, ");
			sb.append("       P.PRV_MBR_NO, ");
			sb.append("       (CASE WHEN P.PRV_MBR_NO_NEW = 0 THEN NULL ELSE P.PRV_MBR_NO_NEW END) AS PRV_MBR_NO_NEW, ");
			sb.append("       (C2.CUST_NAME || case NVL(P.PRV_MBR_MAST_YN, 'N') when 'Y' then '(主戶)' else '' end) as CUST_NAME, ");
			sb.append("       C2.CUST_ID, ");
			sb.append("       C2.AO_CODE, ");
			//			sql2.append("C2.BIRTH_DATE, C2.AUM_AMT,");

			// add by ocean 6705
			sb.append("       UI.EMP_ID AS UEMP_ID, ");
			sb.append("       UI.EMP_NAME || CASE WHEN UI.CODE_TYPE = '1' THEN '(計績)' WHEN UI.CODE_TYPE = '3' THEN '(維護)' ELSE '' END AS UEMP_NAME, ");

			sb.append("       C2.BIRTH_DATE, ");
			sb.append("       NVL(C2AUM.AVG_AUM_AMT,0) as AUM_AMT, ");
			sb.append("       NVL(C2.VIP_DEGREE,'M') AS VIP_DEGREE, ");
			sb.append("       P.REL_TYPE, ");
			sb.append("       P.REL_TYPE_OTH, ");
			sb.append("       P.CUST_ID_M, ");
			sb.append("       P.APL_DATE, ");
			sb.append("       P.ACT_DATE, ");
			sb.append("       P.PRV_STATUS, ");
			sb.append("       P.CREATOR, ");
			sb.append("       P.BRA_MGR_EMP_ID, ");
			sb.append("       P.BRA_MGR_RPL_DATE, ");
			sb.append("       P.OP_MGR_EMP_ID, ");
			sb.append("       P.OP_MGR_RPL_DATE, ");
			sb.append("       E.EMP_ID, ");
			sb.append("       E.EMP_NAME, ");
			sb.append("       PARAM.PARAM_NAME AS REL_TYPE_DESC, ");
			sb.append("       P.PRV_MBR_MAST_YN ");
			sb.append("FROM TBCRM_CUST_PRV P ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST C1 ON P.CUST_ID_M = C1.CUST_ID ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST C2 ON P.CUST_ID_S = C2.CUST_ID ");

			// add by ocean 6705
			sb.append("LEFT JOIN VWORG_EMP_UHRM_INFO UI ON C2.AO_CODE = UI.UHRM_CODE ");

			sb.append("LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_CODE = P.REL_TYPE AND PARAM.PARAM_TYPE = 'CRM.REL_TYPE' ");
			
			//近6周最低AUM
			sb.append("LEFT JOIN ( ");
			sb.append("  SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT ");
			sb.append("  FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
			sb.append("  WHERE CUST_ID IN (");
			sb.append("    SELECT CUST_ID_S ");
			sb.append("    FROM TBCRM_CUST_PRV ");
			sb.append("    WHERE CUST_ID_M = :cust_id");
			sb.append("  ) ");
			sb.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (12*7) ");
//			sb.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (6*7) ");
			sb.append("  GROUP BY CUST_ID ");
			sb.append(") C2AUM ON C2.CUST_ID = C2AUM.CUST_ID ");

			sb.append("LEFT JOIN VWORG_AO_INFO E ON C2.AO_CODE = E.AO_CODE ");
			sb.append("WHERE 1 = 1 ");
			sb.append("AND C1.CUST_ID = :cust_id ");

			sb.append("ORDER BY PRV_MBR_NO ASC ");
			
			queryCondition.setObject("cust_id", inputVO.getCust_id());

			queryCondition.setQueryString(sb.toString().replaceAll("\\s+", " "));

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			//2017-11-15 如果一開始沒有主戶資料則帶回主戶近六週最低AUM
			if (list.isEmpty()) {
				//近6周最低AUM	
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("SELECT (C.CUST_NAME || '(主戶)') as CUST_NAME, ");
				sb.append("       C.CUST_ID, ");
				sb.append("       C.AO_CODE, ");
				sb.append("       C.BIRTH_DATE, ");
				sb.append("       NVL(B.AVG_AUM_AMT,0) as AUM_AMT, ");
				sb.append("       E.EMP_ID, ");
				sb.append("       E.EMP_NAME, ");
				sb.append("       NVL(C.VIP_DEGREE,'M') AS VIP_DEGREE, ");
				sb.append("       '00' AS REL_TYPE ");
				sb.append("FROM TBCRM_CUST_MAST C ");
				sb.append("LEFT JOIN ( ");
				sb.append("  SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT ");
				sb.append("  FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
				sb.append("  WHERE CUST_ID = :cust_id ");
				sb.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (12*7) ");
//				sb.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (6*7) ");
				sb.append("  GROUP BY CUST_ID ");
				sb.append(") B ON C.CUST_ID = B.CUST_ID ");
				sb.append("LEFT JOIN VWORG_AO_INFO E ON trim(C.AO_CODE) = trim(E.AO_CODE) ");
				sb.append("WHERE C.CUST_ID = :cust_id ");
				
				queryCondition.setObject("cust_id", inputVO.getCust_id());

				queryCondition.setQueryString(sb.toString().replaceAll("\\s+", " "));
								
				outputVO.setResultList_main(dam.exeQuery(queryCondition));
			}

			outputVO.setResultList_prv(list);
			
			sendRtnObject(outputVO);
		}
		//VAtype != 2 : 代表是從戶或是非私人銀行，第一次進入不用自建一筆，先查詢自己的主戶，再用主戶ID查詢明細(僅顯示自己和主戶)		
		else {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			// 取得關係戶
			sb.append("SELECT CUST_ID_M FROM TBCRM_CUST_PRV WHERE CUST_ID_S = :cust_id ");
			
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			if (list.size() > 0) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT P.SEQ, ");
				sb.append("       P.PRV_MBR_YN, ");
				sb.append("       P.PRV_MBR_NO, ");
				sb.append("       (CASE WHEN P.PRV_MBR_NO_NEW = 0 THEN NULL ELSE P.PRV_MBR_NO_NEW END) AS PRV_MBR_NO_NEW, ");
				sb.append("       (C2.CUST_NAME || case P.PRV_MBR_MAST_YN when 'Y' then '(主戶)' else '' end) as CUST_NAME, ");
				sb.append("       C2.CUST_ID, ");
				sb.append("       C2.AO_CODE, ");

				// add by ocean 6705
				sb.append("       UI.EMP_ID AS UEMP_ID, UI.EMP_NAME || CASE WHEN UI.CODE_TYPE = '1' THEN '(計績)' WHEN UI.CODE_TYPE = '3' THEN '(維護)' ELSE '' END AS UEMP_NAME, ");

				sb.append("       C2.BIRTH_DATE, ");
				sb.append("       NVL(C2AUM.AVG_AUM_AMT,0) as AUM_AMT, ");
				sb.append("       NVL(C2.VIP_DEGREE,'M') AS VIP_DEGREE, ");
				sb.append("       '00' AS REL_TYPE, ");
				sb.append("       P.REL_TYPE_OTH, ");
				sb.append("       P.CUST_ID_M, ");
				sb.append("       P.APL_DATE, ");
				sb.append("       P.ACT_DATE, ");
				sb.append("       P.PRV_STATUS, ");
				sb.append("       P.CREATOR, ");
				sb.append("       P.BRA_MGR_EMP_ID, ");
				sb.append("       P.BRA_MGR_RPL_DATE, ");
				sb.append("       P.OP_MGR_EMP_ID, ");
				sb.append("       P.OP_MGR_RPL_DATE, ");
				sb.append("       E.EMP_ID, ");
				sb.append("       E.EMP_NAME, ");
				sb.append("       PARAM.PARAM_NAME AS REL_TYPE_DESC, ");
				sb.append("       P.PRV_MBR_MAST_YN ");
				sb.append("FROM TBCRM_CUST_PRV P ");
				sb.append("LEFT JOIN TBCRM_CUST_MAST C1 ON P.CUST_ID_M = C1.CUST_ID ");
				sb.append("LEFT JOIN TBCRM_CUST_MAST C2 ON P.CUST_ID_S = C2.CUST_ID ");

				// add by ocean 6705
				sb.append("LEFT JOIN VWORG_EMP_UHRM_INFO UI ON C2.AO_CODE = UI.UHRM_CODE ");

				sb.append("LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_CODE = '00' AND PARAM.PARAM_TYPE = 'CRM.REL_TYPE' ");

				//近6周最低AUM
				sb.append("LEFT JOIN ( ");
				sb.append("  SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT ");
				sb.append("  FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
				sb.append("  WHERE CUST_ID IN (SELECT CUST_ID_S FROM TBCRM_CUST_PRV WHERE CUST_ID_M = :cust_id_m) ");
				sb.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (12*7) ");
//				sb.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (6*7) ");
				sb.append("  GROUP BY CUST_ID ");
				sb.append(") C2AUM ON C2.CUST_ID = C2AUM.CUST_ID ");

				sb.append("LEFT JOIN VWORG_AO_INFO E ON C2.AO_CODE = E.AO_CODE ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND C2.CUST_ID = :cust_id_s ");
				sb.append("AND NOT (P.CUST_ID_M = P.CUST_ID_S AND C1.CUST_ID = :cust_id_s) ");

				sb.append("UNION ");
				
				sb.append("SELECT P.SEQ, ");
				sb.append("       P.PRV_MBR_YN, ");
				sb.append("       P.PRV_MBR_NO, ");
				sb.append("       (CASE WHEN P.PRV_MBR_NO_NEW = 0 THEN NULL ELSE P.PRV_MBR_NO_NEW END) AS PRV_MBR_NO_NEW, ");
				sb.append("       (C2.CUST_NAME || case P.PRV_MBR_MAST_YN when 'Y' then '(主戶)' else '' end) as CUST_NAME, ");
				sb.append("       C2.CUST_ID, ");
				sb.append("       C2.AO_CODE, ");

				// add by ocean 6705
				sb.append("       UI.EMP_ID AS UEMP_ID, UI.EMP_NAME || CASE WHEN UI.CODE_TYPE = '1' THEN '(計績)' WHEN UI.CODE_TYPE = '3' THEN '(維護)' ELSE '' END AS UEMP_NAME, ");

				sb.append("       C2.BIRTH_DATE, ");
				sb.append("       NVL(C2AUM.AVG_AUM_AMT,0) as AUM_AMT, ");
				sb.append("       NVL(C2.VIP_DEGREE,'M') AS VIP_DEGREE, ");
				sb.append("       REL.REL_TYPE, ");
				sb.append("       P.REL_TYPE_OTH, ");
				sb.append("       P.CUST_ID_M, ");
				sb.append("       P.APL_DATE, ");
				sb.append("       P.ACT_DATE, ");
				sb.append("       P.PRV_STATUS, ");
				sb.append("       P.CREATOR, ");
				sb.append("       P.BRA_MGR_EMP_ID, ");
				sb.append("       P.BRA_MGR_RPL_DATE, ");
				sb.append("       P.OP_MGR_EMP_ID, ");
				sb.append("       P.OP_MGR_RPL_DATE, ");
				sb.append("       E.EMP_ID, ");
				sb.append("       E.EMP_NAME, ");
				sb.append("       PARAM.PARAM_NAME AS REL_TYPE_DESC, ");
				sb.append("       P.PRV_MBR_MAST_YN ");
				sb.append("FROM TBCRM_CUST_PRV P ");
				sb.append("LEFT JOIN TBCRM_CUST_MAST C1 ON P.CUST_ID_M = C1.CUST_ID ");
				sb.append("LEFT JOIN TBCRM_CUST_MAST C2 ON P.CUST_ID_S = C2.CUST_ID ");
				
				// add by ocean 6705
				sb.append("LEFT JOIN VWORG_EMP_UHRM_INFO UI ON C2.AO_CODE = UI.UHRM_CODE ");
				
				sb.append("LEFT JOIN (SELECT CUST_ID_S, REL_TYPE FROM TBCRM_CUST_PRV WHERE CUST_ID_M != CUST_ID_S) REL ON REL.CUST_ID_S = :cust_id_s ");
				sb.append("LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_CODE = REL.REL_TYPE AND PARAM.PARAM_TYPE = 'CRM.REL_TYPE' ");

				//近6周最低AUM
				sb.append("LEFT JOIN ( ");
				sb.append("  SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT ");
				sb.append("  FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
				sb.append("  WHERE CUST_ID IN (SELECT CUST_ID_S FROM TBCRM_CUST_PRV WHERE CUST_ID_M = :cust_id_m) ");
				sb.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (6*7) ");
				sb.append("  GROUP BY CUST_ID ");
				sb.append(") C2AUM ON C2.CUST_ID = C2AUM.CUST_ID ");

				sb.append("LEFT JOIN VWORG_AO_INFO E ON C2.AO_CODE = E.AO_CODE ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND C2.CUST_ID = :cust_id_m ");
				sb.append("AND NOT(P.CUST_ID_M = P.CUST_ID_S AND C1.CUST_ID = :cust_id_s) ");
				sb.append("ORDER BY PRV_MBR_NO ASC ");

				queryCondition.setObject("cust_id_m", list.get(0).get("CUST_ID_M"));
				queryCondition.setObject("cust_id_s", inputVO.getCust_id());
				
				queryCondition.setQueryString(sb.toString().replaceAll("\\s+", " "));
				
				outputVO.setResultList_prv(dam.exeQuery(queryCondition));
				
				sendRtnObject(outputVO);
			}
		}
	}

	/** 家庭戶新增查詢-轄下客戶 SQL **/
	public StringBuffer getCustomerSQL (StringBuffer sql, CRM662InputVO inputVO) {
		
		sql.append("WITH BASE AS ( ");
		sql.append("  SELECT distinct ");
		sql.append("         A.CUST_NAME, ");
		sql.append("         A.CUST_ID, ");
		sql.append("         A.AO_CODE, ");
		sql.append("         NVL(A.VIP_DEGREE,'M') AS VIP_DEGREE, ");
		sql.append("         C.EMP_NAME, ");
		sql.append("         C.EMP_ID, ");
		sql.append("         C.BRANCH_NAME, ");
		sql.append("         CASE WHEN NVL(P.CUST_ID_M,'X') <> 'X' THEN 'N' ELSE 'Y' END M_CHECK, ");
		sql.append("         (CASE WHEN A.CUST_ID IN (SELECT CUST_ID_S AS CUST_ID FROM TBCRM_CUST_PRV) THEN 'Y' ELSE 'N' END) AS EXIST_FAM ");
		sql.append("  FROM TBCRM_CUST_MAST A ");
		sql.append("  LEFT JOIN VWORG_AO_INFO C ON A.AO_CODE = C.AO_CODE ");
		sql.append("  LEFT JOIN TBCRM_CUST_PRV P ON P.CUST_ID_M = A.CUST_ID AND P.CUST_ID_S <> P.CUST_ID_M ");
		sql.append("  WHERE 1 = 1  ");
		sql.append("  AND A.CUST_ID NOT IN (SELECT distinct CUST_ID_S FROM TBCRM_CUST_PRV WHERE CUST_ID_M <> CUST_ID_S) ");
		sql.append("  AND A.CUST_ID NOT IN (SELECT distinct CUST_ID_M FROM TBCRM_CUST_PRV WHERE CUST_ID_M <> CUST_ID_S) ");

		if (StringUtils.isNotBlank(inputVO.getCust_id()) || StringUtils.isNotBlank(inputVO.getCust_id_m_dc())) {
			sql.append("  AND A.CUST_ID = :cust_id ");
		}
		
//		if (StringUtils.isNotBlank(inputVO.getCust_name())) {
//			sql.append("  AND A.CUST_NAME LIKE :cust_name ");
//		}

		sql.append(") ");
		
		sql.append("SELECT BASE.*, NVL(C2AUM.AVG_AUM_AMT,0) as AUM_AMT ");
		sql.append("FROM BASE ");
		
		//近6周最低AUM ==> 改成12週
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT ");
		sql.append("  FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
		sql.append("  WHERE CUST_ID = (SELECT CUST_ID FROM BASE) ");
		sql.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (12*7) ");
//		sql.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (6*7) ");
		sql.append("  GROUP BY CUST_ID ");
		sql.append(") C2AUM ON C2AUM.CUST_ID = BASE.CUST_ID ");
		
		return sql;
	}
	
	/** 家庭戶新增查詢-轄下客戶 **/
	/** 家庭戶新增查詢-轄下客戶 **/
	public void queryCustomer(Object body, IPrimitiveMap header) throws JBranchException {

		CRM662InputVO inputVO = (CRM662InputVO) body;
		CRM662OutputVO outputVO = new CRM662OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql = getCustomerSQL(sql, inputVO);
		
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
//		if (StringUtils.isNotBlank(inputVO.getCust_name())) {
//			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
//		}
		
		queryCondition.setQueryString(sql.toString().replaceAll("\\s+", " "));

		queryCondition.setMaxResults((Integer) SysInfo.getInfoValue(FubonSystemVariableConsts.QRY_MAX_RESULTS));
		
		outputVO.setResultList_prv_add(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}

	/** 家庭戶新增查詢-現有關係戶 **/
	/** 家庭戶新增查詢-現有關係戶 **/
	public void queryRelCustomer(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662InputVO inputVO = (CRM662InputVO) body;
		CRM662OutputVO outputVO = new CRM662OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//從戶資料
		sql.append("SELECT ");
		sql.append("       C2.CUST_NAME, ");
		sql.append("       C2.CUST_ID, ");
		sql.append("       C2.AO_CODE, ");
		sql.append("       R.REL_TYPE, ");
		sql.append("       R.REL_STATUS, ");
		sql.append("       R.REL_TYPE_OTH, ");
		sql.append("       E.BRANCH_NAME, ");
		sql.append("       E.EMP_NAME, ");
		sql.append("       E.EMP_ID, ");
		sql.append("       NVL(C2.VIP_DEGREE,'M') AS VIP_DEGREE, ");
		sql.append("       NVL(C2AUM.AVG_AUM_AMT,0) as AUM_AMT, ");
		
		//給前端判斷是否為其他主戶或清單內的客戶
		sql.append("       (CASE WHEN C2.CUST_ID IN (SELECT CUST_ID_S AS CUST_ID FROM TBCRM_CUST_PRV WHERE CUST_ID_M = :cust_id) THEN 'N' ELSE 'Y' END) AS M_CHECK, ");
		sql.append("       PARAM.PARAM_NAME AS REL_TYPE_DESC, ");
		
		//是否為主戶或其他客戶的從戶
		sql.append("       (CASE WHEN C2.CUST_ID IN (SELECT CUST_ID_S AS CUST_ID FROM TBCRM_CUST_PRV) THEN 'Y' ELSE 'N' END) AS EXIST_FAM ");

		sql.append("FROM TBCRM_CUST_REL R ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C1 ON R.CUST_ID_M = C1.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C2 ON R.CUST_ID_S = C2.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO E ON C2.AO_CODE = E.AO_CODE ");
		sql.append("LEFT JOIN VWCRM_REL_TXFEE_DSCNT_RATE D ON C2.CUST_ID = D.CUST_ID ");
		sql.append("LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_CODE = R.REL_TYPE AND PARAM.PARAM_TYPE = 'CRM.REL_TYPE' ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT ");
		sql.append("  FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
		sql.append("  WHERE CUST_ID IN (SELECT CUST_ID_S FROM TBCRM_CUST_REL WHERE CUST_ID_M = :cust_id) ");
		sql.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (12*7) ");
//		sql.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (6*7) ");
		sql.append("  GROUP BY CUST_ID ");
		sql.append(") C2AUM ON C2.CUST_ID = C2AUM.CUST_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND R.REL_MBR_YN = 'Y' ");
		sql.append("AND C1.CUST_ID = :cust_id ");
		sql.append("AND C1.CUST_ID <> C2.CUST_ID ");

		sql.append("UNION ");

		//主戶資料
		sql.append("SELECT ");
		sql.append("       C1.CUST_NAME, ");
		sql.append("       C1.CUST_ID, ");
		sql.append("       C1.AO_CODE, ");
		sql.append("       R.REL_TYPE, ");
		sql.append("       R.REL_STATUS, ");
		sql.append("       R.REL_TYPE_OTH, ");
		sql.append("       E.BRANCH_NAME, ");
		sql.append("       E.EMP_NAME, ");
		sql.append("       E.EMP_ID, ");
		sql.append("       NVL(C1.VIP_DEGREE,'M') AS VIP_DEGREE, ");
		sql.append("       NVL(C2AUM.AVG_AUM_AMT,0) as AUM_AMT, ");
		
		//給前端判斷是否為其他主戶或清單內的客戶
		sql.append("       (CASE WHEN C1.CUST_ID IN (SELECT CUST_ID_S AS CUST_ID FROM TBCRM_CUST_PRV WHERE CUST_ID_M = :cust_id) THEN 'N' ELSE 'Y' END) AS M_CHECK, ");
		sql.append("       PARAM.PARAM_NAME AS REL_TYPE_DESC, ");
		
		//是否為主戶或其他客戶的從戶
		sql.append("       (CASE WHEN C1.CUST_ID IN (SELECT CUST_ID_S AS CUST_ID FROM TBCRM_CUST_PRV) THEN 'Y' ELSE 'N' END) AS EXIST_FAM ");

		sql.append("FROM TBCRM_CUST_REL R ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C1 ON R.CUST_ID_M = C1.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C2 ON R.CUST_ID_S = C2.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO E ON C1.AO_CODE = E.AO_CODE ");
		sql.append("LEFT JOIN VWCRM_REL_TXFEE_DSCNT_RATE D ON C1.CUST_ID = D.CUST_ID ");
		sql.append("LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_CODE = R.REL_TYPE AND PARAM.PARAM_TYPE = 'CRM.REL_TYPE' ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT ");
		sql.append("  FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
		sql.append("  WHERE CUST_ID IN (SELECT CUST_ID_M FROM TBCRM_CUST_REL WHERE CUST_ID_S = :cust_id) ");
		sql.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (12*7) ");
//		sql.append("  and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (6*7) ");
		sql.append("  GROUP BY CUST_ID ");
		sql.append(") C2AUM ON C1.CUST_ID = C2AUM.CUST_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND R.REL_MBR_YN = 'Y' ");
		sql.append("AND C2.CUST_ID = :cust_id ");
		sql.append("AND C1.CUST_ID <> C2.CUST_ID ");
		
		queryCondition.setObject("cust_id", inputVO.getCust_id_m());

		queryCondition.setQueryString(sql.toString().replaceAll("\\s+", " "));

		outputVO.setResultList_rel_add(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}

	/** 新增家庭戶 **/
	public void prv_add(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662InputVO inputVO = (CRM662InputVO) body;
		CRM662OutputVO outputVO = new CRM662OutputVO();
		dam = this.getDataAccessManager();

		// 不要畫面一進入就加一筆主戶. 請改到prv_add method. 有申請時再去判斷有無家庭戶資料, 若無資料則建立一筆主戶
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQ FROM TBCRM_CUST_PRV WHERE CUST_ID_M = :cust_id AND CUST_ID_S = CUST_ID_M");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		List list = dam.exeQuery(queryCondition);

		if (list.size() == 0) {
			String seq = getSN("CRM662");
			TBCRM_CUST_PRVVO vo = new TBCRM_CUST_PRVVO();

			vo.setSEQ(seq);
			vo.setCUST_ID_M(inputVO.getCust_id());
			vo.setCUST_ID_S(inputVO.getCust_id());
			vo.setREL_TYPE("00");
			vo.setPRV_MBR_MAST_YN("Y");
			vo.setPRV_MBR_YN("N");
			vo.setPRV_APL_TYPE("3");
			vo.setPRV_STATUS("PSN");
			vo.setPRV_MBR_NO(BigDecimal.valueOf(0));
			vo.setAPL_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
			dam.create(vo);

			//=====================================加入FAMILY_DEGREE欄位======================================//
			dam = this.getDataAccessManager();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();

			sql.append("SELECT * FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());

			List<Map<String, Object>> listCustMast = dam.exeQuery(queryCondition);
			if (CollectionUtils.isNotEmpty(listCustMast) && listCustMast.size() > 0) {
				dam = this.getDataAccessManager();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sqlInsert = new StringBuffer();

				String family_degree = StringUtils.isNotEmpty(listCustMast.get(0).get("VIP_DEGREE").toString()) ? listCustMast.get(0).get("VIP_DEGREE").toString() : "";

				sqlInsert.append("UPDATE TBCRM_CUST_PRV SET FAMILY_DEGREE = :family_degree WHERE SEQ = :seq");
				queryCondition.setObject("family_degree", family_degree);
				queryCondition.setObject("seq", seq);
				queryCondition.setQueryString(sqlInsert.toString());
				dam.exeUpdate(queryCondition);
			}
			//===========================================================================================//
		}

		dam = this.getDataAccessManager();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		//算現有家庭成員數目，之後與新加成員，總數>=8 需要雙主管覆核
		sql.append("SELECT count(1) as CNT FROM TBCRM_CUST_PRV WHERE CUST_ID_M = :cust_id AND CUST_ID_S != CUST_ID_M");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		List<Map<String, Object>> memList = dam.exeQuery(queryCondition);
		int memCount = (CollectionUtils.isEmpty(memList) ? 0 : ((BigDecimal) memList.get(0).get("CNT")).intValue());

		List<Map<String, Object>> addList = inputVO.getAdd_list_prv();
		for (int i = 0; i < addList.size(); i++) {
			if (addList.get(i).get("add_success").toString().equals("Y")) {
				/** 是否有舊資料 or 該客戶是他人家庭戶會員(CUST_ID_S = 申請會員 CUST_ID) **/
				QueryConditionIF queryCondition_C = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql_C = new StringBuffer();
				sql_C.append(" SELECT COUNT(SEQ) AS APL_PPL ");
				sql_C.append(" FROM TBCRM_CUST_PRV  ");
				sql_C.append(" WHERE CUST_ID_S = :cust_id ");
				sql_C.append(" AND PRV_STATUS <> 'PAD' ");

				queryCondition_C.setQueryString(sql_C.toString());
				queryCondition_C.setObject("cust_id", addList.get(i).get("prv_cust_id"));
				List<Map<String, Object>> list_C = dam.exeQuery(queryCondition_C);

				//錯誤類型C:申請人只能歸屬一位主戶成為家庭會員
				if ((((BigDecimal) list_C.get(0).get("APL_PPL")).intValue()) > 0) {
					outputVO.setPrv_add_err_type("C");
					sendRtnObject(outputVO);
					return;
				}
				/** 檢核通過:開始新增 **/
				String seq = getSN("CRM662");
				TBCRM_CUST_PRVVO vo = new TBCRM_CUST_PRVVO();
				vo.setSEQ(seq);
				vo.setCUST_ID_M(inputVO.getCust_id());
				vo.setCUST_ID_S(addList.get(i).get("prv_cust_id").toString());
				vo.setREL_TYPE(addList.get(i).get("rel_type") == null || addList.get(i).get("rel_type").toString().equals("") ? "99" : addList.get(i).get("rel_type").toString());
				vo.setREL_TYPE_OTH(inputVO.getRel_type_oth());
				vo.setPRV_MBR_YN("N");
				vo.setPRV_MBR_NO(new BigDecimal(inputVO.getPrv_mbr_no()));
				vo.setPRV_STATUS("PAN"); //家庭戶申請中PAN
				vo.setAPL_DATE(new Timestamp(new Date().getTime()));

				//判斷目前會員人數是否超過8人，是則需走督導覆核
				if (addList.size() + memCount < 8) {
					vo.setPRV_APL_TYPE("3");
				} else {
					vo.setPRV_APL_TYPE("6");
				}

				dam.create(vo);

				//=====================================加入FAMILY_DEGREE欄位======================================//
				dam = this.getDataAccessManager();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();

				sql.append("SELECT * FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
				queryCondition.setObject("cust_id", inputVO.getCust_id());
				queryCondition.setQueryString(sql.toString());

				List<Map<String, Object>> listCustMast = dam.exeQuery(queryCondition);
				if (CollectionUtils.isNotEmpty(listCustMast) && listCustMast.size() > 0) {
					dam = this.getDataAccessManager();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();

					String family_degree = StringUtils.isNotEmpty(listCustMast.get(0).get("VIP_DEGREE").toString()) ? listCustMast.get(0).get("VIP_DEGREE").toString() : "";
					sql.append("UPDATE TBCRM_CUST_PRV SET FAMILY_DEGREE = (:family_degree) WHERE SEQ = :seq");
					queryCondition.setObject("family_degree", family_degree);
					queryCondition.setObject("seq", seq);
					queryCondition.setQueryString(sql.toString());
					dam.exeUpdate(queryCondition);
				}
				//===========================================================================================//
			}
		}
		outputVO.setPrv_add_err_type("N");
		this.sendRtnObject(outputVO);
	}

	/** 刪除家庭戶 **/
	/** 刪除家庭戶 **/
	public void prv_delete(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662InputVO inputVO = (CRM662InputVO) body;
		dam = this.getDataAccessManager();
		CRM662OutputVO outputVO = new CRM662OutputVO();

		TBCRM_CUST_PRVVO vo = new TBCRM_CUST_PRVVO();
		vo = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, inputVO.getSeq());
		//申請待覆核
		if (inputVO.getPrv_status().equals("PAN") || inputVO.getPrv_status().equals("PAN2")) {
			dam.delete(vo);
			outputVO.setPrv_delete("D");
		} else {
			vo.setPRV_MBR_NO_NEW(new BigDecimal(0));
			vo.setPRV_STATUS("PAD");
			vo.setPRV_APL_TYPE("4");
			dam.update(vo);
			outputVO.setPrv_delete("U");
		}
		
		sendRtnObject(outputVO);
	}

	/** 排序服務執行 **/
	public void prv_sort(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662InputVO inputVO = (CRM662InputVO) body;
		dam = this.getDataAccessManager();

		//主戶狀態改為排序待覆核
		TBCRM_CUST_PRVVO vom = new TBCRM_CUST_PRVVO();
		vom = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, inputVO.getSeq());
		vom.setPRV_APL_TYPE("5");
		vom.setPRV_STATUS("PAO"); //排序申請中
		dam.update(vom);

		//從戶資料迴圈更改排序
		for (Map<String, Object> list : inputVO.getPrv_sort_list()) {
			TBCRM_CUST_PRVVO vos = new TBCRM_CUST_PRVVO();
			vos = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, list.get("SEQ").toString());
			//MBR_NO 原排序	MBR_NO_NEW 新排序
			vos.setPRV_MBR_NO_NEW(getBigDecimal(list.get("PRV_MBR_NO_NEW")));
			dam.update(vos);
		}

		sendRtnObject(null);
	}

	/** 家庭戶覆核查詢 **/
	/** 家庭戶覆核查詢 **/
	public void prv_rpy_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662OutputVO outputVO = new CRM662OutputVO();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//		sql.append("SELECT ");
		//		sql.append("P.SEQ, C1.CUST_NAME AS CUST_NAME_M, C1.CUST_ID AS CUST_ID_M, ");
		//		sql.append("C2.CUST_NAME AS CUST_NAME_S, C2.CUST_ID AS CUST_ID_S, ");
		//		sql.append("P.REL_TYPE, NVL(C2AUM.AVG_AUM_AMT,0) as AUM_AMT_S, ");
		//		sql.append("E2.BRANCH_NAME AS BRANCH_NAME_S, ");
		//		sql.append("E2.EMP_NAME AS EMP_NAME_S, E1.EMP_NAME AS EMP_NAME_M,  ");
		//		sql.append("P.PRV_APL_TYPE, P.PRV_MBR_NO, P.PRV_MBR_NO_NEW, P.PRV_STATUS ");
		//		
		//		sql.append("FROM TBCRM_CUST_PRV P ");
		//		sql.append("LEFT JOIN TBCRM_CUST_MAST C1 ON P.CUST_ID_M = C1.CUST_ID ");
		//		sql.append("LEFT JOIN TBCRM_CUST_MAST C2 ON P.CUST_ID_S = C2.CUST_ID ");
		//		sql.append("LEFT JOIN VWORG_AO_INFO E1 ON C1.AO_CODE = E1.AO_CODE ");
		//		sql.append("LEFT JOIN VWORG_AO_INFO E2 ON C2.AO_CODE = E2.AO_CODE ");
		//		
		//		//近6周最低AUM
		//		sql.append("LEFT JOIN ");
		//		sql.append(" (SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
		//		sql.append(" 	WHERE CUST_ID IN (SELECT P.CUST_ID_S FROM TBCRM_CUST_PRV P LEFT JOIN TBCRM_CUST_MAST C ON C.CUST_ID = P.CUST_ID_M WHERE C.BRA_NBR IN (:brNbrList)) ");
		//		sql.append(" 	and to_date(DATA_DAY, 'yyyyMMdd') > sysdate - (6*7) GROUP BY CUST_ID ");
		//		sql.append(")C2AUM ON C2.CUST_ID = C2AUM.CUST_ID ");

		sql.append("SELECT P.SEQ, C1.CUST_NAME AS CUST_NAME_M, C1.CUST_ID AS CUST_ID_M, ");
		sql.append("       C2.CUST_NAME AS CUST_NAME_S, C2.CUST_ID AS CUST_ID_S, ");
		sql.append("       P.REL_TYPE, NVL(C2AUM.AVG_AUM_AMT, 0) AS AUM_AMT_S, ");
		sql.append("       E2.BRANCH_NAME AS BRANCH_NAME_S, ");
		sql.append("       E2.EMP_NAME AS EMP_NAME_S, E1.EMP_NAME AS EMP_NAME_M, ");
		sql.append("       P.PRV_APL_TYPE, P.PRV_MBR_NO, P.PRV_MBR_NO_NEW, P.PRV_STATUS ");
		sql.append("FROM TBCRM_CUST_PRV P ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C1 ON P.CUST_ID_M = C1.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C2 ON P.CUST_ID_S = C2.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO E1 ON C1.AO_CODE = E1.AO_CODE ");
		sql.append("LEFT JOIN VWORG_AO_INFO E2 ON C2.AO_CODE = E2.AO_CODE ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT CUST_ID, MIN(AVG_AUM_AMT) as AVG_AUM_AMT ");
		sql.append("  FROM TBCRM_CUST_AUM_WK_CAL_DEGREE ");
		sql.append("  WHERE CUST_ID IN (SELECT P.CUST_ID_S FROM TBCRM_CUST_PRV P LEFT JOIN TBCRM_CUST_MAST C ON C.CUST_ID = P.CUST_ID_M WHERE C.BRA_NBR IN (:brNbrList)) ");
		sql.append("  AND TO_DATE(DATA_DAY, 'yyyyMMdd') > SYSDATE - (12 * 7) ");
//		sql.append("  AND TO_DATE(DATA_DAY, 'yyyyMMdd') > SYSDATE - (6 * 7) ");
		sql.append("  GROUP BY CUST_ID ");
		sql.append(") C2AUM ON C2.CUST_ID = C2AUM.CUST_ID ");

		sql.append("WHERE 1 = 1 ");
		sql.append("AND C1.BRA_NBR IN (:brNbrList) ");

		List<Map<String, Object>> privilegeId = getPrivilegeId();
		
		if (!privilegeId.isEmpty() || privilegeId != null) {
			String pid = (String) privilegeId.get(0).get("PRIVILEGEID");
			
			if (pid.equals("009") || pid.equals("010") || pid.equals("011")) {
				//分行主管覆核清單
				sql.append("AND P.PRV_STATUS IN ('PAN', 'PAD', 'PAO') ");
				sql.append("AND (CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO PL WHERE P.CREATOR = PL.EMP_ID) >= 1 THEN 'Y' ELSE 'N' END) = 'N' ");
			} else if (pid.equals("012") || pid.equals("013")) {
				//區域督導覆核清單
				sql.append("AND P.PRV_STATUS IN ('PAN2', 'PAD', 'PAO') ");
				sql.append("AND (CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO PL WHERE P.CREATOR = PL.EMP_ID) >= 1 THEN 'Y' ELSE 'N' END) = 'N' ");
			} else if (pid.equals("UHRM012")) {
				//UHRM科主管
				sql.append("AND P.PRV_STATUS IN ('PAN', 'PAD', 'PAO') ");
				sql.append("AND (CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO PL WHERE P.CREATOR = PL.EMP_ID) >= 1 THEN 'Y' ELSE 'N' END) = 'Y' ");
			} else if (pid.equals("UHRM013")) {
				//UHRM處主管
				sql.append("AND P.PRV_STATUS IN ('PAN2', 'PAD', 'PAO') ");
				sql.append("AND (CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO PL WHERE P.CREATOR = PL.EMP_ID) >= 1 THEN 'Y' ELSE 'N' END) = 'Y' ");
			}
		}

		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList_prv_rpy(dam.exeQuery(queryCondition));
		sendRtnObject(outputVO);
	}

	/** 家庭戶覆核執行 **/
	/** 家庭戶覆核執行 **/
	public void prv_rpy(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662InputVO inputVO = (CRM662InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		TBCRM_CUST_PRVVO vo = new TBCRM_CUST_PRVVO();
		vo = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, inputVO.getSeq());
		
		TBCRM_CUST_PRVVO vom = new TBCRM_CUST_PRVVO();
		// =====================================同意==============================
		if (inputVO.getPrv_rpy_type().equals("Y")) {
			//取得主戶資料 
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			
			sql.append("select SEQ from TBCRM_CUST_PRV where CUST_ID_M = :cust_id_m AND CUST_ID_M = CUST_ID_S ");
			queryCondition.setObject("cust_id_m", inputVO.getCust_id_m());
			queryCondition.setQueryString(sql.toString());
			
			List<Map<String, Object>> listm = dam.exeQuery(queryCondition);
			if (CollectionUtils.isNotEmpty(listm))
				vom = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, listm.get(0).get("SEQ").toString());

			/** PAN家庭戶申請-主管同意 **/
			if (inputVO.getPrv_status().equals("PAN")) {
				//超過8人:type=6	STATUS改為PAN2(主管同意，走到營運督導簽核)
				if (inputVO.getPrv_apl_type().equals("6")) {
					vo.setPRV_STATUS("PAN2");
					vo.setPRV_MBR_NO_NEW(new BigDecimal(0));
					vo.setBRA_MGR_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setBRA_MGR_RPL_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);

					//初次覆核，將覆核資料寫入主戶資料中
					if (vom != null && !StringUtils.equals(vom.getPRV_MBR_YN(), "Y")) {
						vom.setBRA_MGR_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						vom.setBRA_MGR_RPL_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
						dam.update(vom);
					}
				}
				//未滿8人:type=3	STATUS改為PSN(覆核通過，自動生效關係戶)
				else if (inputVO.getPrv_apl_type().equals("3")) {
					vo.setPRV_MBR_YN("Y"); //TBCRM_CUST_PRV的關係戶判斷欄位生效
					vo.setPRV_STATUS("PSN");
					vo.setPRV_MBR_NO_NEW(new BigDecimal(0));
					vo.setBRA_MGR_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setBRA_MGR_RPL_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

					//獲取下個月的第一天
					Calendar cal_1 = Calendar.getInstance();//或許當前日期 
					cal_1.add(Calendar.MONTH, +1);
					cal_1.set(Calendar.DAY_OF_MONTH, 1);//設置為1號,當前日期既為本月第一天 
					vo.setACT_DATE(new Timestamp(cal_1.getTime().getTime()));

					dam.update(vo);

					//初次覆核，將覆核資料寫入主戶資料中
					if (vom != null && !StringUtils.equals(vom.getPRV_MBR_YN(), "Y")) {
						vom.setPRV_MBR_YN("Y");
						vom.setBRA_MGR_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						vom.setBRA_MGR_RPL_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
						vom.setACT_DATE(new Timestamp(cal_1.getTime().getTime()));
						dam.update(vom);
					}
				}
			}
			/** PAN2家庭戶申請-區督導同意 **/
			else if (inputVO.getPrv_status().equals("PAN2")) {
				vo.setPRV_MBR_YN("Y");
				vo.setPRV_STATUS("PSN");
				vo.setPRV_MBR_NO_NEW(new BigDecimal(0));
				vo.setOP_MGR_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				vo.setOP_MGR_RPL_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

				//獲取下個月的第一天
				Calendar cal_1 = Calendar.getInstance();//或許當前日期 
				cal_1.add(Calendar.MONTH, +1);
				cal_1.set(Calendar.DAY_OF_MONTH, 1);//設置為1號,當前日期既為本月第一天 
				vo.setACT_DATE(new Timestamp(cal_1.getTime().getTime()));

				dam.update(vo);

				//初次覆核，將覆核資料寫入主戶資料中
				if (vom != null && !StringUtils.equals(vom.getPRV_MBR_YN(), "Y")) {
					vom.setPRV_MBR_YN("Y");
					vom.setBRA_MGR_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					vom.setBRA_MGR_RPL_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vom.setACT_DATE(new Timestamp(cal_1.getTime().getTime()));
					dam.update(vom);
				}
			}
			/** 刪除覆核-同意 **/
			else if (inputVO.getPrv_status().equals("PAD")) {
				TBCRM_CUST_MASTVO custvo = new TBCRM_CUST_MASTVO();
				custvo = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCust_id_s());
				
				if (custvo != null && StringUtils.isNotBlank(custvo.getFAMILY_DEGREE())) {
					//客戶主檔有FAMILY_DEGREE才需要將移除後資料拋送主機並更新主檔FAMILY_DEGREE資料
					//寫入TBCRM_CUST_VIP_DEGREE_CHGLOG，當晚拋送390/400主機
					//CHG_TYPE = '3' 家庭戶資料
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					
					sql.append("Insert into TBCRM_CUST_VIP_DEGREE_CHGLOG (SEQ, CUST_ID, CHG_TYPE, CHG_DATE, ORG_DEGREE, NEW_DEGREE, NEW_NOTE, APPL_EMP_ID, APPL_DATE, REVIEW_DATE, RETURN_400_YN, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
					sql.append("values (TBCRM_CUST_VIP_DEGREE_SEQ.nextval, :cust_id, '3', sysdate, :family_degree, null, '臨櫃刪除家庭戶成員資格，主管覆核完成', 'CRM662', sysdate, sysdate, 'U', 0, 'CRM662', sysdate, 'CRM662', sysdate) ");

					queryCondition.setObject("cust_id", inputVO.getCust_id_s());
					queryCondition.setObject("family_degree", custvo.getFAMILY_DEGREE());
					queryCondition.setQueryString(sql.toString());
					dam.exeUpdate(queryCondition);

					//CHG_TYPE = '1' 個人VIP_DEGREE資料
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					
					sql.append("Insert into TBCRM_CUST_VIP_DEGREE_CHGLOG (SEQ, CUST_ID, CHG_TYPE, CHG_DATE, ORG_DEGREE, NEW_DEGREE, NEW_NOTE, APPL_EMP_ID, APPL_DATE, REVIEW_DATE, RETURN_400_YN, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
					sql.append("values (TBCRM_CUST_VIP_DEGREE_SEQ.nextval, :cust_id, '1', sysdate, :vip_degree, :vip_degree, '臨櫃刪除家庭戶成員資格，主管覆核完成。個人等級變更須放入原個人等級資料', 'CRM662', sysdate, sysdate, 'U', 0, 'CRM662', sysdate, 'CRM662', sysdate) ");

					queryCondition.setObject("cust_id", inputVO.getCust_id_s());
					queryCondition.setObject("vip_degree", custvo.getVIP_DEGREE());
					queryCondition.setQueryString(sql.toString());
					dam.exeUpdate(queryCondition);

					//更新主檔FAMILY_DEGREE資料
					custvo.setFAMILY_DEGREE(null);
					dam.update(custvo);
				}

				//儲存覆核退回結果到紀錄檔，並刪除申請紀錄
				TBCRM_CUST_RELPRV_LOGVO vos = new TBCRM_CUST_RELPRV_LOGVO();
				vos.setSEQ(getSN("CRM662_LOG"));
				vos.setCHG_SRC_TYPE("P");
				vos.setCUST_ID_M(vo.getCUST_ID_M());
				vos.setCUST_ID_S(vo.getCUST_ID_S());
				vos.setREL_TYPE(vo.getREL_TYPE());
				vos.setREL_TYPE_OTH(vo.getREL_TYPE_OTH());
				vos.setSTATUS("PSD");
				vos.setAPL_DATE(vo.getAPL_DATE());
				vos.setACT_DATE(vo.getACT_DATE());
				vos.setBRA_MGR_EMP_ID(vo.getBRA_MGR_EMP_ID());
				vos.setBRA_MGR_RPL_DATE(vo.getBRA_MGR_RPL_DATE());
				vos.setOP_MGR_EMP_ID(vo.getOP_MGR_EMP_ID());
				vos.setOP_MGR_RPL_DATE(vo.getOP_MGR_RPL_DATE());
				dam.create(vos);

				//刪除申請紀錄TBCRM_CUST_PRV
				dam.delete(vo);

				/** 刪除只剩主戶一人 **/
				Boolean prv_count = getPrvFamilyCount(inputVO.getCust_id_m());
				if (prv_count) {
					//取得主戶資料
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					
					sql.append("select SEQ from TBCRM_CUST_PRV where CUST_ID_M = :cust_id_m AND REL_TYPE = '00'");
					queryCondition.setObject("cust_id_m", inputVO.getCust_id_m());
					queryCondition.setQueryString(sql.toString());
					
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);

					if (CollectionUtils.isNotEmpty(list)) {
						vom = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, listm.get(0).get("SEQ").toString());

						//儲存覆核退回結果到紀錄檔，並刪除申請紀錄
						TBCRM_CUST_RELPRV_LOGVO vos2 = new TBCRM_CUST_RELPRV_LOGVO();
						vos2.setSEQ(getSN("CRM662_LOG"));
						vos2.setCHG_SRC_TYPE("P");
						vos2.setCUST_ID_M(vom.getCUST_ID_M());
						vos2.setCUST_ID_S(vom.getCUST_ID_M());
						vos2.setREL_TYPE(vom.getREL_TYPE());
						vos2.setREL_TYPE_OTH(vom.getREL_TYPE_OTH());
						vos2.setSTATUS("PSD");
						vos2.setAPL_DATE(vom.getAPL_DATE());
						vos2.setACT_DATE(vom.getACT_DATE());
						vos2.setBRA_MGR_EMP_ID(vom.getBRA_MGR_EMP_ID());
						vos2.setBRA_MGR_RPL_DATE(vom.getBRA_MGR_RPL_DATE());
						vos2.setOP_MGR_EMP_ID(vom.getOP_MGR_EMP_ID());
						vos2.setOP_MGR_RPL_DATE(vom.getOP_MGR_RPL_DATE());
						dam.create(vos2);

						//刪除申請紀錄TBCRM_CUST_PRV
						dam.delete(vom);
					}
					
					TBCRM_CUST_MASTVO custvo_m = new TBCRM_CUST_MASTVO();
					custvo_m = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCust_id_m());
					if (custvo_m != null && StringUtils.isNotBlank(custvo_m.getFAMILY_DEGREE())) {
						//客戶主檔有FAMILY_DEGREE才需要將移除後資料拋送主機並更新主檔FAMILY_DEGREE資料
						//寫入TBCRM_CUST_VIP_DEGREE_CHGLOG，當晚拋送390/400主機
						//CHG_TYPE = '3' 家庭戶資料
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sql = new StringBuffer();
						
						sql.append("Insert into TBCRM_CUST_VIP_DEGREE_CHGLOG (SEQ, CUST_ID, CHG_TYPE, CHG_DATE, ORG_DEGREE, NEW_DEGREE, NEW_NOTE, APPL_EMP_ID, APPL_DATE, REVIEW_DATE, RETURN_400_YN, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
						sql.append("values (TBCRM_CUST_VIP_DEGREE_SEQ.nextval, :cust_id, '3', sysdate, :family_degree, null, '臨櫃刪除家庭戶成員資格，主管覆核完成(家庭戶只剩主戶一戶，刪除主戶)', 'CRM662', sysdate, sysdate, 'U', 0, 'CRM662', sysdate, 'CRM662', sysdate) ");

						queryCondition.setObject("cust_id", inputVO.getCust_id_m());
						queryCondition.setObject("family_degree", custvo_m.getFAMILY_DEGREE());
						queryCondition.setQueryString(sql.toString());
						dam.exeUpdate(queryCondition);

						//CHG_TYPE = '1' 個人VIP_DEGREE資料
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sql = new StringBuffer();
						sql.append("Insert into TBCRM_CUST_VIP_DEGREE_CHGLOG (SEQ, CUST_ID, CHG_TYPE, CHG_DATE, ORG_DEGREE, NEW_DEGREE, NEW_NOTE, APPL_EMP_ID, APPL_DATE, REVIEW_DATE, RETURN_400_YN, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
						sql.append("values (TBCRM_CUST_VIP_DEGREE_SEQ.nextval, :cust_id, '1', sysdate, :vip_degree, :vip_degree, '臨櫃刪除家庭戶成員資格，主管覆核完成。個人等級變更須放入原個人等級資料(家庭戶只剩主戶一戶，刪除主戶)', 'CRM662', sysdate, sysdate, 'U', 0, 'CRM662', sysdate, 'CRM662', sysdate) ");

						queryCondition.setObject("cust_id", inputVO.getCust_id_m());
						queryCondition.setObject("vip_degree", custvo_m.getVIP_DEGREE());
						queryCondition.setQueryString(sql.toString());
						dam.exeUpdate(queryCondition);

						//更新主檔FAMILY_DEGREE資料
						custvo_m.setFAMILY_DEGREE(null);
						dam.update(custvo_m);
					}
				}

			}
			/** 排序申請-同意 **/
			else if (inputVO.getPrv_status().equals("PAO")) {
				//主戶上待覆核狀態解除
				vo.setPRV_STATUS("PSO");
				dam.update(vo);

				//搜尋從戶SEQ
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				
				sql.append("SELECT SEQ, PRV_MBR_NO, PRV_MBR_NO_NEW ");
				sql.append("FROM TBCRM_CUST_PRV ");
				sql.append("WHERE CUST_ID_M = :cust_id_m AND CUST_ID_M <> CUST_ID_S ");
				
				queryCondition.setObject("cust_id_m", inputVO.getCust_id_m());
				queryCondition.setQueryString(sql.toString());
				
				List<Map<String, Object>> list_S = dam.exeQuery(queryCondition);

				//從戶新排序蓋掉舊排序
				for (Map<String, Object> list : list_S) {
					TBCRM_CUST_PRVVO vos = new TBCRM_CUST_PRVVO();
					vos = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, list.get("SEQ").toString());
					
					//MBR_NO 原排序	MBR_NO_NEW 新排序
					vos.setPRV_MBR_NO(getBigDecimal(list.get("PRV_MBR_NO_NEW")));
					vos.setPRV_MBR_NO_NEW(getBigDecimal(0));
					
					dam.update(vos);
				}
			}
		}
		// =====================================退回==============================
		else {
			/** PAN家庭戶申請-主管退回 **/
			if (inputVO.getPrv_status().equals("PAN")) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				
				sql.append("SELECT SEQ, CUST_ID_M, CUST_ID_S, REL_TYPE, REL_TYPE_OTH, APL_DATE ");
				sql.append("FROM TBCRM_CUST_PRV ");
				sql.append("WHERE SEQ = :seq ");

				queryCondition.setObject("seq", inputVO.getSeq());
				queryCondition.setQueryString(sql.toString());
				
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);

				//儲存覆核退回結果到紀錄檔，並刪除申請紀錄
				TBCRM_CUST_RELPRV_LOGVO vos = new TBCRM_CUST_RELPRV_LOGVO();
				vos.setSEQ(getSN("CRM662_LOG"));
				vos.setCHG_SRC_TYPE("P");
				vos.setCUST_ID_M(list2.get(0).get("CUST_ID_M").toString());
				vos.setCUST_ID_S(list2.get(0).get("CUST_ID_S").toString());
				vos.setREL_TYPE(list2.get(0).get("REL_TYPE") == null || list2.get(0).get("REL_TYPE").equals("") ? "99" : list2.get(0).get("REL_TYPE").toString());
				
				if (list2.get(0).get("REL_TYPE_OTH") != null)
					vos.setREL_TYPE_OTH(list2.get(0).get("REL_TYPE_OTH").toString());
				
				vos.setSTATUS("PRN");
				vos.setAPL_DATE((Timestamp) list2.get(0).get("APL_DATE"));

				//獲取下個月的第一天
				Calendar cal_1 = Calendar.getInstance();//或許當前日期 
				cal_1.add(Calendar.MONTH, +1);
				cal_1.set(Calendar.DAY_OF_MONTH, 1);//設置為1號,當前日期既為本月第一天 
				vos.setACT_DATE(new Timestamp(cal_1.getTime().getTime()));

				dam.create(vos);

				dam.delete(vo);
			}
			/** PAN2家庭戶申請-區督導退回 **/
			else if (inputVO.getPrv_status().equals("PAN2")) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				
				sql.append("SELECT SEQ, CUST_ID_M, CUST_ID_S, REL_TYPE, REL_TYPE_OTH, APL_DATE ");
				sql.append("FROM TBCRM_CUST_PRV ");
				sql.append("WHERE SEQ = :seq ");

				queryCondition.setObject("seq", inputVO.getSeq());
				queryCondition.setQueryString(sql.toString());
				
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);

				//儲存覆核退回結果到紀錄檔，並刪除申請紀錄
				TBCRM_CUST_RELPRV_LOGVO vos = new TBCRM_CUST_RELPRV_LOGVO();
				vos.setSEQ(getSN("CRM662_LOG"));
				vos.setCHG_SRC_TYPE("P");
				vos.setCUST_ID_M(list2.get(0).get("CUST_ID_M").toString());
				vos.setCUST_ID_S(list2.get(0).get("CUST_ID_S").toString());
				vos.setREL_TYPE(list2.get(0).get("REL_TYPE") == null || list2.get(0).get("REL_TYPE").equals("") ? "99" : list2.get(0).get("REL_TYPE").toString());
			
				if (list2.get(0).get("REL_TYPE_OTH") != null)
					vos.setREL_TYPE_OTH(list2.get(0).get("REL_TYPE_OTH").toString());
				
				vos.setSTATUS("PRN2");
				vos.setAPL_DATE((Timestamp) list2.get(0).get("APL_DATE"));

				//獲取下個月的第一天
				Calendar cal_1 = Calendar.getInstance();//或許當前日期 
				cal_1.add(Calendar.MONTH, +1);
				cal_1.set(Calendar.DAY_OF_MONTH, 1);//設置為1號,當前日期既為本月第一天 
				vos.setACT_DATE(new Timestamp(cal_1.getTime().getTime()));

				dam.create(vos);

				dam.delete(vo);
			}
			/** 刪除覆核-退回 **/
			else if (inputVO.getPrv_status().equals("PAD")) {
				vo.setPRV_STATUS("PSN");
				vo.setPRV_MBR_NO_NEW(new BigDecimal(0));
				dam.update(vo);
			}
			/** 排序申請-退回 **/
			else if (inputVO.getPrv_status().equals("PAO")) {
				//主戶上待覆核狀態解除
				vo.setPRV_STATUS("PRO");
				dam.update(vo);

				//搜尋從戶SEQ
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				
				sql.append("SELECT SEQ, PRV_MBR_NO, PRV_MBR_NO_NEW ");
				sql.append("FROM TBCRM_CUST_PRV ");
				sql.append("WHERE CUST_ID_M = :cust_id_m AND CUST_ID_M <> CUST_ID_S ");
				
				queryCondition.setObject("cust_id_m", inputVO.getCust_id_m());
				queryCondition.setQueryString(sql.toString());
				
				List<Map<String, Object>> list_S = dam.exeQuery(queryCondition);

				//從戶 0蓋掉新排序
				for (Map<String, Object> list : list_S) {
					TBCRM_CUST_PRVVO vos = new TBCRM_CUST_PRVVO();
					vos = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, list.get("SEQ").toString());
					//MBR_NO 原排序	MBR_NO_NEW 新排序
					vos.setPRV_MBR_NO_NEW(getBigDecimal(0));
					dam.update(vos);
				}
			}
		}

		if (vo.getPRV_STATUS().equals("PSN")) {
			CRM661InputVO inputVO_crm661_add = new CRM661InputVO();
			CRM661InputVO inputVO_crm661_rpy = new CRM661InputVO();
			CRM661 crm661 = (CRM661) PlatformContext.getBean("crm661");

			TBCRM_CUST_RELVO rel = new TBCRM_CUST_RELVO();

			int count = getRelFamilyCount(inputVO.getCust_id_m(), inputVO.getCust_id_s());
			if (count > 0) {

			} else {
				inputVO_crm661_add.setCust_id(vo.getCUST_ID_M());
				inputVO_crm661_add.setRel_cust_id(vo.getCUST_ID_S());
				inputVO_crm661_add.setRel_type(vo.getREL_TYPE());
				if (vo.getREL_TYPE_OTH() != null) {
					inputVO_crm661_add.setRel_type_oth(vo.getREL_TYPE_OTH());
				}

				inputVO_crm661_rpy = crm661.rel_add(inputVO_crm661_add);

				inputVO_crm661_rpy.setRel_rpy_type("Y");
				inputVO_crm661_rpy.setRel_status("RAN");
				crm661.rel_rpy(inputVO_crm661_rpy);
			}
		}
		
		this.sendRtnObject(null);
	}

	/** 重覆確認是否僅存主戶一人，若是，執行刪除 **/
	public void doubleChk(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM662InputVO inputVO = (CRM662InputVO) body;
		CRM662OutputVO outputVO = new CRM662OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		TBCRM_CUST_PRVVO vom = new TBCRM_CUST_PRVVO();

		/** 刪除只剩主戶一人 **/
		Boolean prv_count = getPrvFamilyCount(inputVO.getCust_id_m_dc());
		if (prv_count) {
			//取得主戶資料
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			
			sql.append("select SEQ from TBCRM_CUST_PRV where CUST_ID_M = :cust_id_m AND REL_TYPE = '00' ");
			queryCondition.setObject("cust_id_m", inputVO.getCust_id_m_dc());
			queryCondition.setQueryString(sql.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (CollectionUtils.isNotEmpty(list)) {
				vom = (TBCRM_CUST_PRVVO) dam.findByPKey(TBCRM_CUST_PRVVO.TABLE_UID, list.get(0).get("SEQ").toString());

				//刪除申請紀錄TBCRM_CUST_PRV
				dam.delete(vom);
				
				System.out.println("DELETE");
			}
		}
		
		System.out.println( getPrvFamilyCount(inputVO.getCust_id_m_dc()));
		
		// 重新取得客戶資料
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		
		sql = getCustomerSQL(sql, inputVO);
		
		if (StringUtils.isNotBlank(inputVO.getCust_id_m_dc())) {
			queryCondition.setObject("cust_id", inputVO.getCust_id_m_dc());
		}
		
		queryCondition.setQueryString(sql.toString().replaceAll("\\s+", " "));
		
		outputVO.setResultList_prv_rpy_dc(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	/** 取序號**/
	private String getSN(String CRM) throws JBranchException {
		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		
		try {
			seqNum = sn.getNextSerialNumber(CRM);
		} catch (Exception e) {
			sn.createNewSerial(CRM, "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber(CRM);
		}
		
		return seqNum;
	}

	/** 轉Decimal **/
	public BigDecimal getBigDecimal(Object value) {
		
		BigDecimal ret = null;
		
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
			}
		}
		
		return ret;
	}

	/** 取得權限群組代碼 **/
	public List<Map<String, Object>> getPrivilegeId() throws DAOException, JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT PRIVILEGEID, ROLEID ");
		sql.append(" FROM TBSYSSECUROLPRIASS ");
		sql.append(" WHERE ROLEID = :role_id ");

		queryCondition.setObject("role_id", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> result = dam.exeQuery(queryCondition);

		return result;
	}

	/** 取得關係戶戶數 **/
	public int getRelFamilyCount(String cust_id_m, String cust_id_s) throws DAOException, JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT count(*) as count ");
		sql.append("FROM TBCRM_CUST_REL ");
		sql.append("WHERE CUST_ID_M = :cust_id_m ");
		sql.append("and CUST_ID_S = :cust_id_s ");
		//sql.append("    and REL_STATUS <> 'RSN' ");

		queryCondition.setObject("cust_id_m", cust_id_m);
		queryCondition.setObject("cust_id_s", cust_id_s);
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> result = dam.exeQuery(queryCondition);

		int count = 0;
		if (CollectionUtils.isNotEmpty(result)) {
			BigDecimal count_change = new BigDecimal(result.get(0).get("COUNT").toString());

			count = count_change.intValue();
		}

		return count;
	}

	/** 取得家庭戶- 關係類型 **/
	public boolean getPrvFamilyCount(String cust_id_m) throws DAOException, JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT REL_TYPE FROM TBCRM_CUST_PRV WHERE CUST_ID_M = :CUST_ID_M ");

		queryCondition.setObject("CUST_ID_M", cust_id_m);
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> result = dam.exeQuery(queryCondition);

		if (CollectionUtils.isNotEmpty(result) && result.size() == 1 && StringUtils.equals("00", result.get(0).get("REL_TYPE").toString())) {
			return true;
		}
		
		return false;
	}
}