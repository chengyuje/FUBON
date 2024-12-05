package com.systex.jbranch.app.server.fps.crm361;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.internal.LinkedTreeMap;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMPVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LE_IMP_TEMPVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AOCODE_CHGLOGVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_AOCHG_PLISTVO;
import com.systex.jbranch.app.server.fps.crm341.CRM341;
import com.systex.jbranch.app.server.fps.crm8501.CRM8501;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/08/31
 * 
 */
@Component("crm361")
@Scope("request")
public class CRM361 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM361.class);

	public void prj_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM361OutputVO return_VO = new CRM361OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_ID, PRJ_NAME, DESC_01, DESC_02, DESC_03, DESC_04, DESC_05, DESC_06, DESC_07, ");
		sql.append("DESC_08, DESC_09, DESC_10, DESC_11, DESC_12, DESC_13, DESC_14, DESC_15 FROM TBCRM_TRS_PRJ_MAST ");
		sql.append("WHERE TRUNC(current_date) BETWEEN PRJ_DATE_BGN AND PRJ_DATE_END ");
		sql.append(" AND PRJ_TYPE IS NULL ");  //非輪調換手名單
		sql.append("ORDER BY PRJ_DATE_BGN DESC ");
		queryCondition.setQueryString(sql.toString());

		List list = dam.exeQuery(queryCondition);
		return_VO.setPrj_list(list);
		this.sendRtnObject(return_VO);
	}

	public void inquire_common(Object body, IPrimitiveMap header) throws JBranchException {
		CRM361InputVO inputVO = (CRM361InputVO) body;
		CRM361OutputVO return_VO = new CRM361OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT CASE WHEN LENGTH(P.CREATOR) = 6 AND P.CREATOR <> 'SYSTEM' THEN P.CREATOR ELSE NULL END AS CREATOR, "); // MODIFY BY OCEAN 0006655 : WMS-CR-20190812-01_調整理專非歸屬行空Code客戶申請移入維護Code流程	
		sql.append("       P.CUST_ID, N.COMPLAIN_YN, N.COMM_NS_YN, C.CUST_NAME, P.TRS_TYPE, P.TEMP_CAL_YN, C.CON_DEGREE, P.SEQ AS TRS_SEQ, ");
		sql.append("       C.VIP_DEGREE, C.AUM_AMT, P.ORG_AO_CODE, P.NEW_AO_CODE, P.APL_REASON, P.APL_OTH_REASON, P.ORG_AO_BRH, ");
		sql.append("       P.OVER_CUST_NO_LIMIT_UP_YN, P.NEW_AO_BRH, P.AGMT_SEQ, P.AGMT_FILE, P.TRS_FLOW_TYPE, P.PROCESS_STATUS, P.TRS_TXN_SOURCE, P.APL_EMP_ID, P.APL_DATETIME, P.APL_BRH_MGR_RPL_DATETIME, ORG_BRH_MGR_RPL_DATETIME, OP_MGR_RPL_DATETIME, ");
		sql.append("       DC_MGR_RPL_DATETIME, EMP.EMP_NAME AS ORG_AO_NAME, DEFN1.BRANCH_NAME AS ORG_BRANCH_NAME, EMP.EMP_ID AS ORG_AO_EMP_ID, EMP2.EMP_NAME AS NEW_AO_NAME, DEFN2.BRANCH_NAME AS NEW_BRANCH_NAME, EMP2.EMP_ID AS NEW_AO_EMP_ID, RO.ROLE_ID as OLD_ROLE_ID ");
		sql.append("FROM TBCRM_CUST_MAST C ");
		sql.append(" INNER JOIN TBCRM_TRS_AOCHG_PLIST P ON C.CUST_ID = P.CUST_ID ");
		sql.append(" LEFT JOIN TBCRM_CUST_NOTE N ON C.CUST_ID = N.CUST_ID ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = P.ORG_AO_BRH ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO DEFN2 ON DEFN2.BRANCH_NBR = P.NEW_AO_BRH ");
		sql.append(" LEFT JOIN VWORG_AO_INFO EMP ON P.ORG_AO_CODE = EMP.AO_CODE ");
		sql.append(" LEFT JOIN VWORG_AO_INFO EMP2 ON P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append(" LEFT JOIN (SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO WHERE ");
		sql.append(" 			MR.ROLE_ID = RO.ROLE_ID AND MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y') RO ON EMP.EMP_ID = RO.EMP_ID ");
		sql.append(" WHERE P.PROCESS_STATUS <> 'F' ");
		//移轉類別增加判斷例行、專案篩選條件
		sql.append(" AND P.TRS_TYPE in (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.QUERY_TRS_TYPE_1') ");

		if (!StringUtils.isBlank(inputVO.getTrs_type())) {
			sql.append(" AND P.TRS_TYPE = :trs_type ");
			queryCondition.setObject("trs_type", inputVO.getTrs_type());
		}

		// 舊理專篩選條件改為抓新理專, 沒改變數名
		if (StringUtils.isNotBlank(inputVO.getOrg_branch_nbr())) {
			sql.append(" AND P.NEW_AO_BRH = :new_ao_brh ");
			queryCondition.setObject("new_ao_brh", inputVO.getOrg_branch_nbr());
		} else {
			sql.append(" AND P.NEW_AO_BRH in (:branchlist) ");
			queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		if (StringUtils.isNotBlank(inputVO.getOrg_ao_code())) {
			sql.append(" AND P.NEW_AO_CODE = :new_ao_code ");
			queryCondition.setObject("new_ao_code", inputVO.getOrg_ao_code());
		}

		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append(" AND P.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}

		if (!StringUtils.isBlank(inputVO.getCon_degree())) {
			sql.append(" AND C.CON_DEGREE = :con_degree ");
			queryCondition.setObject("con_degree", inputVO.getCon_degree());
		}

		if (!StringUtils.isBlank(inputVO.getChg_frq())) {
			sql.append(" AND P.CUST_ID IN (SELECT CUST_ID FROM TBCRM_CUST_AOCODE_CHGLOG ");
			sql.append(" WHERE TRUNC(LETGO_DATETIME) >= TRUNC(SYSDATE - :chg_frq )) ");
			queryCondition.setObject("chg_frq", inputVO.getChg_frq());
		}

		if (!StringUtils.isBlank(inputVO.getCust_name())) {
			sql.append(" AND C.CUST_NAME like :cust_name ");
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
		}

		if (!StringUtils.isBlank(inputVO.getVip_degree())) {
			sql.append(" AND C.VIP_DEGREE = :vip_degree ");
			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
		}

		if (!StringUtils.isBlank(inputVO.getMatch_yn())) {
			sql.append(" AND N.TAKE_CARE_MATCH_YN = :match_yn ");
			queryCondition.setObject("match_yn", inputVO.getMatch_yn());
		}

		if (!StringUtils.isBlank(inputVO.getTemp_yn())) {
			sql.append(" AND P.TEMP_CAL_YN = :temp_yn ");
			queryCondition.setObject("temp_yn", inputVO.getTemp_yn());
		}

		//加入覆核流程&權限判斷

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);

		//總行
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND P.PROCESS_STATUS in ('L1','L2','L3','L4') ");
		}
		//登入者身份=主管
		else if (bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (((  ");
			sql.append(" (P.TRS_FLOW_TYPE = '1' AND P.PROCESS_STATUS = 'L1') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '2' AND P.PROCESS_STATUS = 'L1') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '3' AND P.PROCESS_STATUS = 'L1') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '4' AND P.PROCESS_STATUS = 'L1'))  ");
			sql.append(" AND P.NEW_AO_BRH = :new_branch_nbr ) OR ");
			sql.append(" ((P.TRS_FLOW_TYPE = '3' AND P.PROCESS_STATUS = 'L4')  ");
			//org_branch_nbr 這應該是抓取登入者資訊 是否為 該覆核資料的原分行主管? 
			sql.append(" AND P.ORG_AO_BRH = :org_branch_nbr )) ");

			queryCondition.setObject("new_branch_nbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			queryCondition.setObject("org_branch_nbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH));

			//登入者身份=營運區督導
		} else if (mbrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (  ");
			sql.append(" (P.TRS_FLOW_TYPE = '1' AND P.PROCESS_STATUS = 'L2') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '2' AND P.PROCESS_STATUS = 'L2')) ");

			if (!StringUtils.isBlank(inputVO.getNew_branch_nbr())) {
				sql.append(" AND P.NEW_AO_BRH = :new_branch_nbr ");
				queryCondition.setObject("new_branch_nbr", inputVO.getNew_branch_nbr());
			} else {
				sql.append(" AND P.NEW_AO_BRH IN (:brNbrList) ");
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}

			//登入者身份=區域中心主管
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (  ");
			sql.append(" (P.TRS_FLOW_TYPE = '1' AND P.PROCESS_STATUS = 'L3') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '2' AND P.PROCESS_STATUS = 'L3')) ");

			if (!StringUtils.isBlank(inputVO.getNew_branch_nbr())) {
				sql.append(" AND P.NEW_AO_BRH = :new_branch_nbr ");
				queryCondition.setObject("new_branch_nbr", inputVO.getNew_branch_nbr());
			} else {
				sql.append(" AND P.NEW_AO_BRH IN (:brNbrList) ");
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		} else {
			throw new APException("使用者登入身分不適用");
		}

		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void inquire_project(Object body, IPrimitiveMap header) throws JBranchException {
		CRM361InputVO inputVO = (CRM361InputVO) body;
		CRM361OutputVO return_VO = new CRM361OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// modify by ocean 6001:WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P2
		sql.append("SELECT CASE WHEN LENGTH(P.CREATOR) = 6 AND P.CREATOR <> 'SYSTEM' THEN P.CREATOR ELSE NULL END AS CREATOR, "); // MODIFY BY OCEAN 0006655 : WMS-CR-20190812-01_調整理專非歸屬行空Code客戶申請移入維護Code流程	
		sql.append("       P.CUST_ID, N.COMPLAIN_YN, C.CUST_NAME, P.TRS_TYPE, P.TEMP_CAL_YN, C.CON_DEGREE, C.UEMP_ID, P.SEQ AS TRS_SEQ, ");
		sql.append("       C.VIP_DEGREE, C.AUM_AMT, P.ORG_AO_CODE, P.NEW_AO_CODE, P.APL_REASON, P.APL_OTH_REASON, P.ORG_AO_BRH, ");
		sql.append("       P.OVER_CUST_NO_LIMIT_UP_YN, P.NEW_AO_BRH, P.AGMT_SEQ, P.AGMT_FILE, P.TRS_FLOW_TYPE, P.PROCESS_STATUS, P.TRS_TXN_SOURCE, P.APL_EMP_ID, P.APL_DATETIME, P.APL_BRH_MGR_RPL_DATETIME, ORG_BRH_MGR_RPL_DATETIME, OP_MGR_RPL_DATETIME, ");
		sql.append("       DC_MGR_RPL_DATETIME, EMP.EMP_NAME AS ORG_AO_NAME, DEFN1.BRANCH_NAME AS ORG_BRANCH_NAME, EMP.EMP_ID AS ORG_AO_EMP_ID, EMP2.EMP_NAME AS NEW_AO_NAME, DEFN2.BRANCH_NAME AS NEW_BRANCH_NAME, EMP2.EMP_ID AS NEW_AO_EMP_ID, RO.ROLE_ID as OLD_ROLE_ID, ");
		sql.append("       PDTL.DATA_01, PDTL.DATA_02, PDTL.DATA_03, PDTL.DATA_04, PDTL.DATA_05, PDTL.DATA_06, PDTL.DATA_07, ");
		sql.append("       PDTL.DATA_08, PDTL.DATA_09, PDTL.DATA_10, PDTL.DATA_11, PDTL.DATA_12, PDTL.DATA_13, PDTL.DATA_14, PDTL.DATA_15 ");
		sql.append(" FROM TBCRM_CUST_MAST C ");
		sql.append(" INNER JOIN TBCRM_TRS_AOCHG_PLIST P ON C.CUST_ID = P.CUST_ID ");
		sql.append(" LEFT JOIN TBCRM_CUST_NOTE N ON N.CUST_ID = C.CUST_ID ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = P.ORG_AO_BRH ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO DEFN2 ON DEFN2.BRANCH_NBR = P.NEW_AO_BRH ");
		sql.append(" LEFT JOIN TBCRM_TRS_PRJ_MAST PRJ ON P.PRJ_ID = PRJ.PRJ_ID  ");
		sql.append(" LEFT JOIN TBCRM_TRS_PRJ_DTL PDTL ON PRJ.PRJ_ID = PDTL.PRJ_ID AND P.PRJ_ID = PDTL.PRJ_ID AND P.CUST_ID = PDTL.CUST_ID ");
		sql.append(" LEFT JOIN VWORG_AO_INFO EMP ON  P.ORG_AO_CODE = EMP.AO_CODE ");
		sql.append(" LEFT JOIN VWORG_AO_INFO EMP2 ON P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append(" LEFT JOIN ( ");
		sql.append("  SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME ");
		sql.append("  FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO ");
		sql.append("  WHERE MR.ROLE_ID = RO.ROLE_ID ");
		sql.append("  AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sql.append("  AND RO.REVIEW_STATUS = 'Y' ");
		sql.append("  AND RO.IS_AO = 'Y' ");
		sql.append(") RO ON EMP.EMP_ID = RO.EMP_ID ");
		sql.append(" WHERE P.PROCESS_STATUS <> 'F' ");
		//移轉類別增加判斷例行、專案篩選條件
		sql.append(" AND P.TRS_TYPE in (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.QUERY_TRS_TYPE_2') ");
		sql.append(" AND PRJ.PRJ_TYPE IS NULL ");  //非輪調換手名單
		sql.append(" AND (P.TRS_TYPE <> 'G' "); //非調離職理專轄下客戶指派名單
		//或調離職理專轄下客戶指派名單且尚未處理(客戶主檔AOCODE仍是調離職理專)且沒有在帶移轉清單中
		sql.append("	 OR (P.TRS_TYPE = 'G' AND P.ORG_AO_CODE = C.AO_CODE ");
		sql.append(" 			AND P.CUST_ID NOT IN (SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE TRS_TYPE <> 'G' AND PROCESS_STATUS in ('L0','L1', 'L2', 'L3', 'L4', 'L5', 'BS')))) ");

		if (!StringUtils.isBlank(inputVO.getTrs_type())) {
			sql.append(" AND P.TRS_TYPE = :trs_type ");
			queryCondition.setObject("trs_type", inputVO.getTrs_type());
			if (inputVO.getTrs_type().matches("7|9")) {
				sql.append(" AND LENGTH(P.CUST_ID) >= 10 ");
			}
			
			// 認養空CODE只要認養HTK的 by 俊達 2024.07.29
			if (inputVO.getTrs_type().matches("8")) {
				sql.append(" AND C.VIP_DEGREE IN ('H', 'T', 'K') ");
			}
		}

		if (!StringUtils.isBlank(inputVO.getPrj_id())) {
			sql.append(" AND P.PRJ_ID = :prj_id ");
			queryCondition.setObject("prj_id", inputVO.getPrj_id());
		}

		// 舊理專篩選條件改為抓新理專, 沒改變數名
		if (StringUtils.isNotBlank(inputVO.getOrg_branch_nbr())) {
			sql.append(" AND P.NEW_AO_BRH = :new_ao_brh ");
			queryCondition.setObject("new_ao_brh", inputVO.getOrg_branch_nbr());
		} else {
			sql.append(" AND P.NEW_AO_BRH in (:branchlist) ");
			queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		if (StringUtils.isNotBlank(inputVO.getOrg_ao_code())) {
			sql.append(" AND P.NEW_AO_CODE = :new_ao_code ");
			queryCondition.setObject("new_ao_code", inputVO.getOrg_ao_code());
		}

		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append(" AND P.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}

		if (!StringUtils.isBlank(inputVO.getCon_degree())) {
			sql.append(" AND C.CON_DEGREE = :con_degree ");
			queryCondition.setObject("con_degree", inputVO.getCon_degree());
		}

		if (!StringUtils.isBlank(inputVO.getChg_frq())) {
			sql.append(" AND P.CUST_ID IN (SELECT CUST_ID FROM TBCRM_CUST_AOCODE_CHGLOG ");
			sql.append(" WHERE TRUNC(LETGO_DATETIME) >= TRUNC(SYSDATE - :chg_frq )) ");
			queryCondition.setObject("chg_frq", inputVO.getChg_frq());
		}

		if (!StringUtils.isBlank(inputVO.getCust_name())) {
			sql.append(" AND C.CUST_NAME like :cust_name ");
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
		}

		if (!StringUtils.isBlank(inputVO.getVip_degree())) {
			sql.append(" AND C.VIP_DEGREE = :vip_degree ");
			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
		}

		if (!StringUtils.isBlank(inputVO.getMatch_yn())) {
			sql.append(" AND N.TAKE_CARE_MATCH_YN = :match_yn ");
			queryCondition.setObject("match_yn", inputVO.getMatch_yn());
		}

		if (!StringUtils.isBlank(inputVO.getTemp_yn())) {
			sql.append(" AND P.TEMP_CAL_YN = :temp_yn ");
			queryCondition.setObject("temp_yn", inputVO.getTemp_yn());
		}

		//加入覆核流程&權限判斷

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);

		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND P.PROCESS_STATUS in ('L1','L2','L3','L4') ");
		}
		//登入者身份=主管
		else if (bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (((  ");
			sql.append(" (P.TRS_FLOW_TYPE = '1' AND P.PROCESS_STATUS = 'L1') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '2' AND P.PROCESS_STATUS = 'L1') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '3' AND P.PROCESS_STATUS = 'L1') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '4' AND P.PROCESS_STATUS = 'L1')) ");
			sql.append(" AND P.NEW_AO_BRH = :new_branch_nbr ) OR ");
			sql.append(" ((P.TRS_FLOW_TYPE = '3' AND P.PROCESS_STATUS = 'L4')  ");
			sql.append(" AND P.ORG_AO_BRH = :org_branch_nbr )) ");

			queryCondition.setObject("new_branch_nbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			queryCondition.setObject("org_branch_nbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH));

			//登入者身份=營運區督導
		} else if (mbrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (  ");
			sql.append(" (P.TRS_FLOW_TYPE = '1' AND P.PROCESS_STATUS = 'L2') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '2' AND P.PROCESS_STATUS = 'L2')) ");

			if (!StringUtils.isBlank(inputVO.getNew_branch_nbr())) {
				sql.append(" AND P.NEW_AO_BRH = :new_branch_nbr ");
				queryCondition.setObject("new_branch_nbr", inputVO.getNew_branch_nbr());
			} else {
				sql.append(" AND P.NEW_AO_BRH IN (:brNbrList) ");
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}

			//登入者身份=區域中心主管
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (  ");
			sql.append(" (P.TRS_FLOW_TYPE = '1' AND P.PROCESS_STATUS = 'L3') OR ");
			sql.append(" (P.TRS_FLOW_TYPE = '2' AND P.PROCESS_STATUS = 'L3')) ");

			if (!StringUtils.isBlank(inputVO.getNew_branch_nbr())) {
				sql.append(" AND P.NEW_AO_BRH = :new_branch_nbr ");
				queryCondition.setObject("new_branch_nbr", inputVO.getNew_branch_nbr());
			} else {
				sql.append(" AND P.NEW_AO_BRH IN (:brNbrList) ");
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		} else {
			throw new APException("使用者登入身分不適用");
		}

		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void checkMax(Object body, IPrimitiveMap header) throws Exception {
		CRM361InputVO inputVO = (CRM361InputVO) body;
		CRM361OutputVO return_VO = new CRM361OutputVO();
		dam = this.getDataAccessManager();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> fcMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2)); //理專
		Map<String, String> fchMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2)); //FCH理專

		// 檢核是否超過客戶數上限
		// 2017/10/2 新流程 營運督導直接生效 不檢查
		Boolean Check1 = false;
		List<String> res1 = new ArrayList<String>();
		if (!mbrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			for (String str : inputVO.getAo_object().keySet()) {
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT AO_CODE FROM VWCRM_AO_CONTROL WHERE AO_CODE = :ao_code");
				queryCondition.setObject("ao_code", str);
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() > 0) {
					Check1 = true;
					res1.add(str);
				}
			}
		}

		// 2017/6/27
		Boolean Check2 = false;
		List<String> res2 = new ArrayList<String>();
		Boolean Check3 = false;
		List<String> res3 = new ArrayList<String>();
		Boolean Check4 = false;
		List<String> res4 = new ArrayList<String>();
		Boolean Check5 = false;
		List<String> res5 = new ArrayList<String>();
		Boolean Check6 = false;//客戶在ON CODE排除名單中，不可做移轉
		List<String> res6 = new ArrayList<String>();
		Boolean Check7 = false;
		List<String> res7 = new ArrayList<String>();
		Boolean Check8 = false;
		List<String> res8 = new ArrayList<String>();
		Boolean Check9 = false;
		List<String> res9 = new ArrayList<String>();
		Boolean Check10 = false;
		List<String> res10 = new ArrayList<String>();
		
		// follow crm331
		for (Map<String, Object> map : inputVO.getApply_list()) {
			// get new role by user select
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT MR.ROLE_ID ");
			sql.append("FROM VWORG_AO_INFO EMP ");
			sql.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON EMP.EMP_ID = MR.EMP_ID ");
			sql.append("LEFT JOIN TBORG_ROLE RO ON MR.ROLE_ID = RO.ROLE_ID ");
			sql.append("WHERE 1 = 1 "); // 20210205 modify by ocean : mark by 祐傑，銀證(兼FC)申請移轉時，主管無法覆核 => MR.IS_PRIMARY_ROLE = 'Y'
			sql.append("AND RO.REVIEW_STATUS = 'Y' ");
			sql.append("AND RO.IS_AO = 'Y' ");
			sql.append("AND EMP.AO_CODE = :ao_code ");
			
			queryCondition.setObject("ao_code", map.get("NEW_AO_CODE"));
			queryCondition.setQueryString(sql.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			if (list.size() == 0)
				throw new APException(map.get("NEW_AO_CODE") + ":該理專無角色，請先設定角色");
			
			String NEW_ROLE_ID = ObjectUtils.toString(list.get(0).get("ROLE_ID"));
			
			// 檢核禁移條件
			// FC移到FCH
			if (fcMap.containsKey(map.get("OLD_ROLE_ID")) && fchMap.containsKey(NEW_ROLE_ID)) {
				Check2 = true;
				res2.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			// 計績FCH移到維護FCH
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT A1.TYPE as OLD_TYPE, A2.TYPE as NEW_TYPE FROM TBORG_SALES_AOCODE A1, TBORG_SALES_AOCODE A2 ");
			sql.append("WHERE A1.AO_CODE = :ao_code ");
			sql.append("AND A2.AO_CODE = :ao_code2 ");
			queryCondition.setObject("ao_code", map.get("ORG_AO_CODE"));
			queryCondition.setObject("ao_code2", map.get("NEW_AO_CODE"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				String OLD_TYPE = ObjectUtils.toString(list2.get(0).get("OLD_TYPE"));
				String NEW_TYPE = ObjectUtils.toString(list2.get(0).get("NEW_TYPE"));
				if ("1".equals(OLD_TYPE) && fchMap.containsKey(map.get("OLD_ROLE_ID")) && "3".equals(NEW_TYPE) && fchMap.containsKey(NEW_ROLE_ID)) {
					Check3 = true;
					res3.add(ObjectUtils.toString(map.get("CUST_ID")));
				}
				//#2225: 主CODE移轉規則調整，主CODE客戶不可移轉至維護CODE
				if ("1".equals(OLD_TYPE) && "3".equals(NEW_TYPE)) {
					Check10 = true;
					res10.add(ObjectUtils.toString(map.get("CUST_ID")));
				}
			}
			
			//加強管控未回函重新分派檢核
			//重新分派的新理專不可與名單匯入時原理專相同
			if(StringUtils.equals("C", inputVO.getTrs_type())) { //加強管控未回函重新分派名單 (CRM363用)
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT B.EMP_ID ");
				sql.append(" FROM TBPMS_ROTATION_MAIN A ");
				sql.append(" LEFT JOIN TBPMS_ROTATION_5YCUST B ON B.PRJ_ID = A.PRJ_ID AND B.CUST_ID = A.CUST_ID ");
				sql.append(" WHERE A.CUST_ID = :cust_id AND A.PRJ_ID = :prj_id ");
				queryCondition.setObject("cust_id", map.get("CUST_ID"));
				queryCondition.setObject("prj_id", inputVO.getPrj_id());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append(" SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :newAoCode ");
				queryCondition.setObject("newAoCode", ObjectUtils.toString(map.get("NEW_AO_CODE")));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
				
				if (CollectionUtils.isNotEmpty(list3) && CollectionUtils.isNotEmpty(list4)) {
					String oldEmpId = ObjectUtils.toString(list3.get(0).get("EMP_ID"));
					String newEmpId = ObjectUtils.toString(list4.get(0).get("EMP_ID"));
					if (StringUtils.equals(oldEmpId, newEmpId)) {
						Check5 = true;
						res5.add(ObjectUtils.toString(map.get("CUST_ID")));
					}
				}
			} else {
				//加強管控名單：未執行帳務確認客戶於專案執行日系統拔CODE後，主管不得於”例行”以及”專案”移轉覆核時分派新理專
				//加強管控未回函重新分派名單尚未完成覆核，主管不得於”例行”以及”專案”移轉覆核時分派新理專
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT 1 FROM TBCRM_TRS_AOCHG_PLIST ");
				sql.append(" WHERE CUST_ID = :cust_id AND TRS_TYPE = 'C' AND PROCESS_STATUS IN ('L0','L1', 'L2', 'L3', 'L4', 'L5', 'BS') "); //尚未完成移轉
				queryCondition.setObject("cust_id", ObjectUtils.toString(map.get("CUST_ID")));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list6 = dam.exeQuery(queryCondition);
				if(CollectionUtils.isNotEmpty(list6)) {
					Check9 = true;
					res9.add(ObjectUtils.toString(map.get("CUST_ID")));
				}
			}
			
			//必輪調未帶走拔CODE後，區域分行不可再移回原RM主code/副code/維護code (CRM363用)
			if(StringUtils.equals("D", inputVO.getTrs_type())) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT A.EMP_ID ");
				sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
				sql.append(" INNER JOIN TBCRM_TRS_PRJ_ROTATION_M B ON B.PRJ_ID = A.PRJ_ID AND B.BRANCH_NBR = A.BRANCH_NBR AND B.EMP_ID = A.EMP_ID ");
				sql.append(" WHERE A.CUST_ID = :cust_id AND A.PRJ_ID = :prj_id AND B.REGION_BRANCH_YN = 'Y' ");
				queryCondition.setObject("cust_id", map.get("CUST_ID"));
				queryCondition.setObject("prj_id", inputVO.getPrj_id());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append(" SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :newAoCode ");
				queryCondition.setObject("newAoCode", ObjectUtils.toString(map.get("NEW_AO_CODE")));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
				
				if (CollectionUtils.isNotEmpty(list3) && CollectionUtils.isNotEmpty(list4)) {
					String oldEmpId = ObjectUtils.toString(list3.get(0).get("EMP_ID"));
					String newEmpId = ObjectUtils.toString(list4.get(0).get("EMP_ID"));
					if (StringUtils.equals(oldEmpId, newEmpId)) {
						Check7 = true;
						res7.add(ObjectUtils.toString(map.get("CUST_ID")));
					}
				}
			}
			
			//必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
			CRM341 crm341 = (CRM341) PlatformContext.getBean("crm341");
			String check2023CMDTCust3 = crm341.check2023CMDTCust3(ObjectUtils.toString(map.get("CUST_ID")), ObjectUtils.toString(map.get("NEW_AO_CODE")));
			if(StringUtils.equals("Y", check2023CMDTCust3)) {
				Check8 = true;
				res8.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//是否在ONCODE客戶排除列表中
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT 1 FROM TBCRM_TRS_CUST_EXCLUDE WHERE CUST_ID = :cust_id AND NVL(DEL_YN, 'N') = 'N' ");
			queryCondition.setObject("cust_id", map.get("CUST_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
			if(CollectionUtils.isNotEmpty(list4)) {
				Check6 = true;
				res6.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			// 公司戶與公司負責人Code不同
			//			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//			sql = new StringBuffer();
			//			sql.append("SELECT M1.CUST_ID, TRIM(M1.RPRS_ID) as RPRS_ID, M2.AO_CODE FROM TBCRM_CUST_MAST M1 ");
			//			sql.append("LEFT JOIN TBCRM_CUST_MAST M2 ON TRIM(M1.RPRS_ID) = M2.CUST_ID WHERE 1=1 ");
			//			sql.append("AND LENGTH(M1.CUST_ID) < 10 AND M1.CUST_ID = :cust_id ");
			//			queryCondition.setObject("cust_id", map.get("CUST_ID"));
			//			queryCondition.setQueryString(sql.toString());
			//			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			//			if(list3.size() != 0) {
			//				// 有公司
			//				if(list3.get(0).get("RPRS_ID") != null && list3.get(0).get("AO_CODE") != null) {
			//					if(!StringUtils.equals(map.get("NEW_AO_CODE").toString(), ObjectUtils.toString(list3.get(0).get("AO_CODE")))) {
			//						Check4 = true;
			//						res4.add(ObjectUtils.toString(map.get("CUST_ID")));
			//					}
			//				}
			//			}
		}

		if (Check2) {
			return_VO.setResultList2("ERR2");
			return_VO.setResultList(res2);
		} else if (Check3) {
			return_VO.setResultList2("ERR3");
			return_VO.setResultList(res3);
		} else if (Check4) {
			return_VO.setResultList2("ERR4");
			return_VO.setResultList(res4);
		} else if (Check1) {
			return_VO.setResultList2("ERR1");
			return_VO.setResultList(res1);
		} else if (Check5) {
			return_VO.setResultList2("ERR5");
			return_VO.setResultList(res5);
		} else if (Check6) {
			return_VO.setResultList2("ERR6"); //在ONCODE客戶排除列表中
			return_VO.setResultList(res6);
		} else if (Check7) {
			return_VO.setResultList2("ERR7");
			return_VO.setResultList(res7);
		} else if (Check8) { //必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
			return_VO.setResultList2("ERR8");
			return_VO.setResultList(res8);
		} else if (Check9) { //加強管控未回函重新分派名單，尚未完成覆核，主管不得於”例行”以及”專案”移轉覆核時分派新理專
			return_VO.setResultList2("ERR9");
			return_VO.setResultList(res9);
		} else if(Check10) { //#2225: 主CODE移轉規則調整，主CODE客戶不可移轉至維護CODE
			return_VO.setResultList2("ERR10");
			return_VO.setResultList(res10);
		} else
			return_VO.setResultList2("GOOD");
		this.sendRtnObject(return_VO);
	}

	public void save(Object body, IPrimitiveMap header) throws Exception {
		CRM361InputVO inputVO = (CRM361InputVO) body;
		CRM361OutputVO return_VO = new CRM361OutputVO();
		dam = this.getDataAccessManager();

		String CURRUSER = DataManager.getWorkStation(uuid).getUser().getCurrentUserId();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		//總行 不給放
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			throw new APException("使用者登入身分不可操作");
		}
		//禁銷戶
		if (inputVO.getApply_list().get(0).get("COMM_NS_YN") != null) {
			String text1 = "";
			if (StringUtils.equals(inputVO.getApply_list().get(0).get("COMM_NS_YN").toString(), "Y")) {
				for (Map<String, Object> data : inputVO.getApply_list()) {
					text1 += data.get("CUST_NAME").toString() + "(" + data.get("CUST_ID").toString() + ")" + "、";
				}
				throw new APException(text1.substring(0, text1.length() - 1) + "為NS戶，無法申請客戶移入，請洽分行內控品管科。");
			}
		}
		// 暫存試算
		if ("1".equals(inputVO.getProcess_type())) {
			for (Map<String, Object> data : inputVO.getApply_list()) {
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
				vo.setTEMP_CAL_YN("Y");
				vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
				dam.update(vo);
			}
		}
		// 取消暫存試算
		else if ("2".equals(inputVO.getProcess_type())) {
			for (Map<String, Object> data : inputVO.getApply_list()) {
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
				vo.setTEMP_CAL_YN("N");
				dam.update(vo);
			}
		}
		// 進行分派
		else if ("3".equals(inputVO.getProcess_type())) {
			// jacky count_by_ao_code_change
			Set<String> ao_List = new HashSet<String>();

			for (Map<String, Object> data : inputVO.getApply_list()) {
				//20200325 by Jacky 增加理專十保檢查
				//2022換手名單：已換手經營客戶未滿6個月移轉回原個金RM，需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」
				CRM341 crm341 = (CRM341) PlatformContext.getBean("crm341");
				if(crm341.check10CMDTCust((String)data.get("CUST_ID"), (String)data.get("NEW_AO_CODE"))){
					if(!crm341.check10CMDTCustAgree(new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))))){
						throw new APException((String)data.get("CUST_ID")+"-" + (String)data.get("CUST_NAME")+":該客戶屬十保監控客戶未滿六個月需客戶同意書才可移回原理專");
					}	
				}
				//2022換手名單：已換手經營客戶未滿6個月移轉回原個金RM，需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」
				if(StringUtils.equals("Y", crm341.check2022CMDTCust3((String)data.get("CUST_ID"), (String)data.get("NEW_AO_CODE")))) {
					if(!crm341.check10CMDTCustAgree(new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))))){
						throw new APException((String)data.get("CUST_ID")+"-"+(String)data.get("CUST_NAME")+":該客戶換手經營未滿六個月需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」才可移回原理專");
					}	
				}
				
				//2023必輪調：區域分行非核心客戶一年內移回原理專(需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」)
				if(StringUtils.equals("Y", crm341.check2023CMDTCust4((String)data.get("CUST_ID"), (String)data.get("NEW_AO_CODE")))){
					if(!crm341.check10CMDTCustAgree(new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))))){
						throw new APException((String)data.get("CUST_ID")+"-"+(String)data.get("CUST_NAME")+":輪調專案客戶移回原RM需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」才可移回原理專");
					}
				}
				
				// get new role by user select
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRI.PRIVILEGEID ");
				sql.append("FROM VWORG_AO_INFO EMP ");
				sql.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON EMP.EMP_ID = MR.EMP_ID ");
				sql.append("LEFT JOIN TBORG_ROLE RO ON MR.ROLE_ID = RO.ROLE_ID ");
				sql.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON RO.ROLE_ID = PRI.ROLEID ");
				sql.append("WHERE 1 = 1 "); // 20210205 modify by ocean : mark by 祐傑，銀證(兼FC)申請移轉時，主管無法覆核 => MR.IS_PRIMARY_ROLE = 'Y'
				sql.append("AND RO.REVIEW_STATUS = 'Y' ");
				sql.append("AND RO.IS_AO = 'Y' ");
				sql.append("AND EMP.AO_CODE = :ao_code ");
				
				queryCondition.setObject("ao_code", data.get("NEW_AO_CODE"));
				queryCondition.setQueryString(sql.toString());
				
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
				if (list.size() == 0)
					throw new APException(data.get("NEW_AO_CODE") + ":該理專無角色，請先設定角色");
				
				String PRIVILEGEID = ObjectUtils.toString(list.get(0).get("PRIVILEGEID"));
				
				//分行主管
				if ("L1".equals(data.get("PROCESS_STATUS"))) {
					if ("1".equals(data.get("TRS_FLOW_TYPE")) || "2".equals(data.get("TRS_FLOW_TYPE"))) {
						// 2017/9/27 CR
						TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
						vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
						vo.setAPL_BRH_MGR(CURRUSER);
						vo.setAPL_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
						vo.setAPL_BRH_MGR_RPL_STATUS("Y");
						vo.setTEMP_CAL_YN("N");
						vo.setTEMP_CAL_EMP_ID(null);
						vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
						// 超過最適戶
						//「調離職理專轄下客戶指派」移轉名單(TRS_TYPE="G")不須檢核最適客戶數，一階主管覆核即結案
						if (!StringUtils.equals("G", ObjectUtils.toString(data.get("TRS_TYPE"))) 
								&& StringUtils.isNotBlank(inputVO.getFlag()) && inputVO.getFlaggedList().contains(ObjectUtils.toString(data.get("NEW_AO_CODE")))) {
							vo.setPROCESS_STATUS("L2");
							vo.setOVER_CUST_NO_LIMIT_UP_YN("Y");

							// mantis 0004267: 主管覆核客戶移轉 先埋log
							logger.info("CRM361NEW_AO-超過最適戶 L2:" + ObjectUtils.toString(data.get("NEW_AO_CODE")));
							logger.info("CRM361處理流程-超過最適戶 L2:" + vo.getPROCESS_STATUS());
						} else {
							if(StringUtils.equals("E", vo.getTRS_TYPE())) {
								//2023必輪調區域分行未帶走拔CODE後移回原理專，經由一階主管同意，再經由處主管同意後生效，客戶即可轉回原RM
								vo.setPROCESS_STATUS("L5");
							} else {
								vo.setPROCESS_STATUS("S");
								vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
							}
							// jacky
							ao_List.add(ObjectUtils.toString(data.get("ORG_AO_CODE")));
							ao_List.add(ObjectUtils.toString(data.get("NEW_AO_CODE")));
						}
						dam.update(vo);

						//移轉生效，寫入客戶主檔以及移轉紀錄
						if(StringUtils.equals("S", vo.getPROCESS_STATUS())) {
							this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
						}
					} else if ("3".equals(data.get("TRS_FLOW_TYPE"))) {
						TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
						vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
						vo.setAPL_BRH_MGR(CURRUSER);
						vo.setAPL_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
						vo.setAPL_BRH_MGR_RPL_STATUS("Y");
						vo.setPROCESS_STATUS("L5");
						vo.setTEMP_CAL_YN("N");
						vo.setTEMP_CAL_EMP_ID(null);
						dam.update(vo);

						// 2017/5/26 add // email from old code
						if (data.get("ORG_AO_BRH") != null) {
							String con_degree = (String) data.get("CON_DEGREE");

							if ("E".equals(con_degree) || "I".equals(con_degree)) {
								// 分行主管 E-mail
								QueryConditionIF queryCondition_mail = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								StringBuffer sql_mail = new StringBuffer();
								sql_mail.append("SELECT E.EMP_EMAIL_ADDRESS ");
								sql_mail.append("FROM ( ");
								sql_mail.append("    SELECT M.EMP_ID, M.DEPT_ID, M.EMP_EMAIL_ADDRESS, '主職' AS MEM_TYPE ");
								sql_mail.append("    FROM TBORG_MEMBER M ");
								sql_mail.append("    WHERE M.SERVICE_FLAG = 'A' AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
								sql_mail.append("    UNION ALL ");
								sql_mail.append("    SELECT P.EMP_ID, P.DEPT_ID, M.EMP_EMAIL_ADDRESS, '兼職' AS MEM_TYPE ");
								sql_mail.append("    FROM TBORG_MEMBER_PLURALISM P ");
								sql_mail.append("    LEFT JOIN TBORG_MEMBER M ");
								sql_mail.append("      ON P.EMP_ID = M.EMP_ID ");
								sql_mail.append("    WHERE (TRUNC(P.TERDTE) >= TRUNC(SYSDATE) OR P.TERDTE IS NULL) AND P.ACTION <> 'D' ) E ");
								sql_mail.append("INNER JOIN TBORG_MEMBER_ROLE R  ON E.EMP_ID  = R.EMP_ID ");
								sql_mail.append("INNER JOIN TBORG_DEFN D         ON E.DEPT_ID = D.DEPT_ID ");
								sql_mail.append("WHERE D.ORG_TYPE = '50' ");
								sql_mail.append("  AND R.ROLE_ID IN (SELECT ROLEID from TBSYSSECUROLPRIASS where PRIVILEGEID = '011') ");
								sql_mail.append("  AND E.DEPT_ID = :list_branch ");
								queryCondition_mail.setObject("list_branch", data.get("ORG_AO_BRH"));
								queryCondition_mail.setQueryString(sql_mail.toString());
								List<Map<String, String>> mail_data = dam.exeQuery(queryCondition_mail);

								// 原理專 E-mail
								QueryConditionIF querymail2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								querymail2.setQueryString("select EMP_EMAIL_ADDRESS from TBORG_MEMBER where EMP_ID = :emp_id ");
								querymail2.setObject("emp_id", data.get("ORG_AO_EMP_ID"));
								List<Map<String, String>> mail_data2 = dam.exeQuery(querymail2);

								// UHRM E-mail
								QueryConditionIF querymail3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								querymail3.setQueryString("select EMP_EMAIL_ADDRESS from TBORG_MEMBER where EMP_ID = :emp_id ");
								querymail3.setObject("emp_id", data.get("UEMP_ID"));
								List<Map<String, String>> mail_data3 = dam.exeQuery(querymail3);

								// 2017/6/6 follow old code
								List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
								String mail1 = mail_data.size() == 0 ? "" : mail_data.get(0).get("EMP_EMAIL_ADDRESS");
								String mail2 = mail_data2.size() == 0 ? "" : mail_data2.get(0).get("EMP_EMAIL_ADDRESS");
								String mail3 = mail_data3.size() == 0 ? "" : mail_data3.get(0).get("EMP_EMAIL_ADDRESS");

								if (StringUtils.isBlank(mail1) && StringUtils.isBlank(mail2))
									return_VO.setErrorMsg("原分行主管及原理專皆無E-mail");
								else if (StringUtils.isBlank(mail1)) {
									return_VO.setErrorMsg("原分行主管無E-mail");
									if (isEmail(ObjectUtils.toString(mail2)) == false)
										return_VO.setErrorMsg("原分行主管無E-mail, " + mail2 + "原理專Email格式錯誤");
									else {
										Map<String, String> mailMap = new HashMap<String, String>();
										mailMap.put(FubonSendJavaMail.MAIL, mail2);
										mailList.add(mailMap);
									}
								} else if (StringUtils.isBlank(mail2)) {
									return_VO.setErrorMsg("原理專無E-mail");
									if (isEmail(ObjectUtils.toString(mail1)) == false)
										return_VO.setErrorMsg("原理專無E-mail, " + mail1 + "原分行主管Email格式錯誤");
									else {
										Map<String, String> mailMap = new HashMap<String, String>();
										mailMap.put(FubonSendJavaMail.MAIL, mail1);
										mailList.add(mailMap);
									}
								} else {
									if (isEmail(ObjectUtils.toString(mail1)) == false && isEmail(ObjectUtils.toString(mail2)) == false)
										return_VO.setErrorMsg(mail1 + "原分行主管Email格式錯誤, " + mail2 + "原理專Email格式錯誤");
									else if (isEmail(ObjectUtils.toString(mail1)) == false)
										return_VO.setErrorMsg(mail1 + "原分行主管Email格式錯誤");
									else if (isEmail(ObjectUtils.toString(mail2)) == false)
										return_VO.setErrorMsg(mail2 + "原理專Email格式錯誤");
									else {
										Map<String, String> mailMap = new HashMap<String, String>();
										mailMap.put(FubonSendJavaMail.MAIL, mail1);
										mailList.add(mailMap);
										mailMap = new HashMap<String, String>();
										mailMap.put(FubonSendJavaMail.MAIL, mail2);
										mailList.add(mailMap);
									}
								}

								// 加入UHRM
								if (StringUtils.isNotBlank(mail3) && isEmail(ObjectUtils.toString(mail3))) {
									Map<String, String> mailMap = new HashMap<String, String>();
									mailMap.put(FubonSendJavaMail.MAIL, mail3);
									mailList.add(mailMap);
								}

								// send
								if (mailList.size() > 0) {
									FubonSendJavaMail sendMail = new FubonSendJavaMail();
									FubonMail mail = new FubonMail();
									Map<String, Object> annexData = new HashMap<String, Object>();
									mail.setLstMailTo(mailList);
									//設定信件主旨
									mail.setSubject("系統通知！轄下分行客戶申請移轉至他行" + ObjectUtils.toString(data.get("NEW_AO_BRH")));
									//設定信件內容
									mail.setContent("轄下分行客戶" + data.get("CUST_ID") + "-" + data.get("CUST_NAME") + "申請移轉至他行" + data.get("NEW_AO_BRH") + "-" + data.get("NEW_BRANCH_NAME"));
									//附件
									QueryConditionIF queryCondition_annexData = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
									queryCondition_annexData.setQueryString("select AGMT_FILE FROM TBCRM_TRS_AOCHG_PLIST WHERE SEQ = :seq");
									queryCondition_annexData.setObject("seq", data.get("TRS_SEQ"));
									List<Map<String, Object>> annexData_data = dam.exeQuery(queryCondition_annexData);
									if (annexData_data.size() > 0 && annexData_data.get(0).get("AGMT_FILE") != null) {
										Blob file = (Blob) annexData_data.get(0).get("AGMT_FILE");
										int fileLength = (int) file.length();
										byte[] onversion = file.getBytes(1, fileLength);
										file.free();
										annexData.put("異動申請書.pdf", onversion);
									}
									//寄出信件
									sendMail.sendMail(mail, annexData);
								}
							}
						} else {
							return_VO.setErrorMsg("該客戶無原分行代碼");
						}
					} else if ("4".equals(data.get("TRS_FLOW_TYPE"))) {
						TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
						vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
						vo.setAPL_BRH_MGR(CURRUSER);
						vo.setAPL_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
						vo.setAPL_BRH_MGR_RPL_STATUS("Y");
						vo.setPROCESS_STATUS("L5");
						vo.setTEMP_CAL_YN("N");
						vo.setTEMP_CAL_EMP_ID(null);
						dam.update(vo);
						// email from old code
						if (data.get("ORG_AO_BRH") != null) {
							List<Map<String, String>> mail_data = new ArrayList<Map<String, String>>();
							if (null == data.get("ORG_AO_CODE") && 
								!StringUtils.equals((String) data.get("ORG_AO_BRH"), (String) data.get("NEW_AO_BRH"))) {
								// 空code客戶 + 跨分行移轉 add by ocean/ CR:20190812085945-0001
							} else {
								// 分行主管 E-mail
								QueryConditionIF queryCondition_mail = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								StringBuffer sql_mail = new StringBuffer();
								sql_mail.append("SELECT E.EMP_EMAIL_ADDRESS ");
								sql_mail.append("FROM TBORG_MEMBER E, ");
								sql_mail.append("TBORG_DEFN D, ");
								sql_mail.append("TBORG_MEMBER_ROLE R ");
								sql_mail.append("WHERE E.EMP_ID = R.EMP_ID ");
								sql_mail.append("AND E.DEPT_ID = D.DEPT_ID ");
								sql_mail.append("AND D.ORG_TYPE = '50' ");
								sql_mail.append("AND R.ROLE_ID IN  (select roleid from TBSYSSECUROLPRIASS where privilegeid = '011') ");
								sql_mail.append("AND E.DEPT_ID = :list_branch ");
								queryCondition_mail.setObject("list_branch", data.get("ORG_AO_BRH"));
								queryCondition_mail.setQueryString(sql_mail.toString());
								mail_data = dam.exeQuery(queryCondition_mail);
							
								// 原理專 E-mail
								QueryConditionIF querymail2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								querymail2.setQueryString("select EMP_EMAIL_ADDRESS from TBORG_MEMBER where EMP_ID = :emp_id ");
								querymail2.setObject("emp_id", data.get("ORG_AO_EMP_ID"));
								List<Map<String, String>> mail_data2 = dam.exeQuery(querymail2);
	
								// UHRM E-mail
								QueryConditionIF querymail3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								querymail3.setQueryString("select EMP_EMAIL_ADDRESS from TBORG_MEMBER where EMP_ID = :emp_id ");
								querymail3.setObject("emp_id", data.get("UEMP_ID"));
								List<Map<String, String>> mail_data3 = dam.exeQuery(querymail3);
	
								// 2017/6/6 follow old code
								List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
								String mail1 = mail_data.size() == 0 ? "" : mail_data.get(0).get("EMP_EMAIL_ADDRESS");
								String mail2 = mail_data2.size() == 0 ? "" : mail_data2.get(0).get("EMP_EMAIL_ADDRESS");
								String mail3 = mail_data3.size() == 0 ? "" : mail_data3.get(0).get("EMP_EMAIL_ADDRESS");
	
								if (StringUtils.isBlank(mail1) && StringUtils.isBlank(mail2))
									return_VO.setErrorMsg("原分行主管及原理專皆無E-mail");
								else if (StringUtils.isBlank(mail1)) {
									return_VO.setErrorMsg("原分行主管無E-mail");
									if (isEmail(ObjectUtils.toString(mail2)) == false)
										return_VO.setErrorMsg("原分行主管無E-mail, " + mail2 + "原理專Email格式錯誤");
									else {
										Map<String, String> mailMap = new HashMap<String, String>();
										mailMap.put(FubonSendJavaMail.MAIL, mail2);
										mailList.add(mailMap);
									}
								} else if (StringUtils.isBlank(mail2)) {
									return_VO.setErrorMsg("原理專無E-mail");
									if (isEmail(ObjectUtils.toString(mail1)) == false)
										return_VO.setErrorMsg("原理專無E-mail, " + mail1 + "原分行主管Email格式錯誤");
									else {
										Map<String, String> mailMap = new HashMap<String, String>();
										mailMap.put(FubonSendJavaMail.MAIL, mail1);
										mailList.add(mailMap);
									}
								} else {
									if (isEmail(ObjectUtils.toString(mail1)) == false && isEmail(ObjectUtils.toString(mail2)) == false)
										return_VO.setErrorMsg(mail1 + "原分行主管Email格式錯誤, " + mail2 + "原理專Email格式錯誤");
									else if (isEmail(ObjectUtils.toString(mail1)) == false)
										return_VO.setErrorMsg(mail1 + "原分行主管Email格式錯誤");
									else if (isEmail(ObjectUtils.toString(mail2)) == false)
										return_VO.setErrorMsg(mail2 + "原理專Email格式錯誤");
									else {
										Map<String, String> mailMap = new HashMap<String, String>();
										mailMap.put(FubonSendJavaMail.MAIL, mail1);
										mailList.add(mailMap);
										mailMap = new HashMap<String, String>();
										mailMap.put(FubonSendJavaMail.MAIL, mail2);
										mailList.add(mailMap);
									}
								}
	
								// 加入UHRM
								if (StringUtils.isNotBlank(mail3) && isEmail(ObjectUtils.toString(mail3))) {
									Map<String, String> mailMap = new HashMap<String, String>();
									mailMap.put(FubonSendJavaMail.MAIL, mail3);
									mailList.add(mailMap);
								}
	
								// send
								if (mailList.size() > 0) {
									FubonSendJavaMail sendMail = new FubonSendJavaMail();
									FubonMail mail = new FubonMail();
									Map<String, Object> annexData = new HashMap<String, Object>();
									mail.setLstMailTo(mailList);
									//設定信件主旨
									mail.setSubject("系統通知！轄下分行客戶申請移轉至他行" + ObjectUtils.toString(data.get("NEW_AO_BRH")));
									//設定信件內容
									mail.setContent("轄下分行客戶" + data.get("CUST_ID") + "-" + data.get("CUST_NAME") + "申請移轉至他行" + data.get("NEW_AO_BRH") + "-" + data.get("NEW_BRANCH_NAME"));
									//附件
									QueryConditionIF queryCondition_annexData = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
									queryCondition_annexData.setQueryString("select AGMT_FILE FROM TBCRM_TRS_AOCHG_PLIST WHERE SEQ = :seq");
									queryCondition_annexData.setObject("seq", data.get("TRS_SEQ"));
									List<Map<String, Object>> annexData_data = dam.exeQuery(queryCondition_annexData);
									if (annexData_data.size() > 0 && annexData_data.get(0).get("AGMT_FILE") != null) {
										Blob file = (Blob) annexData_data.get(0).get("AGMT_FILE");
										int fileLength = (int) file.length();
										byte[] onversion = file.getBytes(1, fileLength);
										file.free();
										annexData.put("異動申請書.pdf", onversion);
									}
									//寄出信件
									sendMail.sendMail(mail, annexData);
								}
							}
						} else {
							return_VO.setErrorMsg("該客戶無原分行代碼");
						}
					}
				}
				// 原分行主管
				else if ("L4".equals(data.get("PROCESS_STATUS"))) {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setORG_BRH_MGR(CURRUSER);
					vo.setORG_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setORG_BRH_MGR_RPL_STATUS("Y");
					vo.setPROCESS_STATUS("L5");
					vo.setTEMP_CAL_YN("N");
					vo.setTEMP_CAL_EMP_ID(null);
					dam.update(vo);
				}
				// 營運區督導
				else if ("L2".equals(data.get("PROCESS_STATUS"))) {
					if (("1".equals(data.get("TRS_FLOW_TYPE")) || "2".equals(data.get("TRS_FLOW_TYPE")))) {
						TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
						vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
						vo.setOP_MGR(CURRUSER);
						vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
						vo.setOP_MGR_RPL_STATUS("Y");
						if(StringUtils.equals("E", vo.getTRS_TYPE())) {
							//2023必輪調區域分行未帶走拔CODE後移回原理專，經由一階主管同意，再經由處主管同意後生效，客戶即可轉回原RM
							vo.setPROCESS_STATUS("L5");
						} else {
							vo.setPROCESS_STATUS("S");
							vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
						}
						vo.setTEMP_CAL_YN("N");
						vo.setTEMP_CAL_EMP_ID(null);
						vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
						dam.update(vo);
						
						if(!StringUtils.equals("E", vo.getTRS_TYPE())) {
							this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
						}
					}
					// old code
					else {
						TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
						vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
						vo.setOP_MGR(CURRUSER);
						vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
						vo.setOP_MGR_RPL_STATUS("Y");
						vo.setPROCESS_STATUS("L3");
						vo.setTEMP_CAL_YN("N");
						vo.setTEMP_CAL_EMP_ID(null);
						vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
						dam.update(vo);
					}
					//如果不小心有料到業務處長的. 也可以覆核成功
				} else if ("L3".equals(data.get("PROCESS_STATUS"))) {
					if (("1".equals(data.get("TRS_FLOW_TYPE")) || "2".equals(data.get("TRS_FLOW_TYPE")))) {
						TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
						vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
						vo.setOP_MGR(CURRUSER);
						vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
						vo.setOP_MGR_RPL_STATUS("Y");
						vo.setPROCESS_STATUS("S");
						vo.setTEMP_CAL_YN("N");
						vo.setTEMP_CAL_EMP_ID(null);
						vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
						vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
						dam.update(vo);
						this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
					}
				}
			}

			// jacky
			//			CRM331 crm331 = (CRM331) PlatformContext.getBean("crm331");
			//			crm331.count_by_ao_code_change(ao_List);
		}
		// 退件
		else {
			for (Map<String, Object> data : inputVO.getApply_list()) {
				//分行主管
				if ("L1".equals(data.get("PROCESS_STATUS"))) {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setAPL_BRH_MGR(CURRUSER);
					vo.setAPL_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setAPL_BRH_MGR_RPL_STATUS("N");
					vo.setPROCESS_STATUS("F");
					dam.update(vo);
				}
				//原分行主管	
				else if ("L4".equals(data.get("PROCESS_STATUS"))) {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setORG_BRH_MGR(CURRUSER);
					vo.setORG_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setORG_BRH_MGR_RPL_STATUS("N");
					vo.setPROCESS_STATUS("F");
					dam.update(vo);
				}
				//營運區督導	
				else if ("L2".equals(data.get("PROCESS_STATUS"))) {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setOP_MGR(CURRUSER);
					vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setOP_MGR_RPL_STATUS("N");
					vo.setPROCESS_STATUS("F");
					dam.update(vo);
				}
				//區域中心主管
				else if ("L3".equals(data.get("PROCESS_STATUS"))) {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setDC_MGR(CURRUSER);
					vo.setDC_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setDC_MGR_RPL_STATUS("N");
					vo.setPROCESS_STATUS("F");
					dam.update(vo);
				}
				
				// 20190828:跨分行申請退件，無論如何都要寄信(君榮說的) modify by ocean
				if (StringUtils.equals("4", (String) data.get("TRS_FLOW_TYPE")) && StringUtils.equals("3", (String) data.get("TRS_TYPE"))) {
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT EMP_EMAIL_ADDRESS FROM TBORG_MEMBER WHERE EMP_ID = :emp_id ");
					queryCondition.setObject("emp_id", data.get("CREATOR"));
					List<Map<String, String>> mailToCreator = dam.exeQuery(queryCondition);

					List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
					String creatorMail = mailToCreator.size() == 0 ? "" : mailToCreator.get(0).get("EMP_EMAIL_ADDRESS");

					if (StringUtils.isBlank(creatorMail)) {
						return_VO.setErrorMsg("申請人無E-mail");
					} else {
						if (isEmail(ObjectUtils.toString(creatorMail)) == false)
							return_VO.setErrorMsg(creatorMail + "申請人Email格式錯誤");
						else {
							Map<String, String> mailMap = new HashMap<String, String>();
							mailMap.put(FubonSendJavaMail.MAIL, creatorMail);
							mailList.add(mailMap);
						}
					}
					
					// send
					if (mailList.size() > 0) {
						FubonSendJavaMail sendMail = new FubonSendJavaMail();
						FubonMail mail = new FubonMail();
						Map<String, Object> annexData = new HashMap<String, Object>();
						mail.setLstMailTo(mailList);
						//設定信件主旨
						mail.setSubject("跨分行移轉覆核退件通知");
						//設定信件內容
						mail.setContent("客戶" + data.get("CUST_ID") + "-" + data.get("CUST_NAME") + "申請移轉至他行" + data.get("NEW_AO_BRH") + "-" + data.get("NEW_BRANCH_NAME"));

						//寄出信件
						sendMail.sendMail(mail, annexData);
					}
				}
			}
		}

		this.sendRtnObject(return_VO);
	}

	private void ReCustAO(DataAccessManager dam, Map<String, Object> data, String PRIVILEGEID, String CURRUSER) throws Exception {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// Step1.更新客戶主檔，並回傳名單檔案更新主機資料
		TBCRM_CUST_MASTVO vo_mast = new TBCRM_CUST_MASTVO();
		vo_mast = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, ObjectUtils.toString(data.get("CUST_ID")));
		vo_mast.setAO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
		vo_mast.setBRA_NBR(ObjectUtils.toString(data.get("NEW_AO_BRH")));
		vo_mast.setAO_LASTUPDATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
		dam.update(vo_mast);

		// Step2.新增一筆到移轉紀錄TBCRM_CUST_AOCHG_LOG
		TBCRM_CUST_AOCODE_CHGLOGVO vo_log = new TBCRM_CUST_AOCODE_CHGLOGVO();
		vo_log.setSEQ(getSEQ());
		vo_log.setCUST_ID(ObjectUtils.toString(data.get("CUST_ID")));
		vo_log.setORG_AO_CODE(ObjectUtils.toString(data.get("ORG_AO_CODE")));
		vo_log.setORG_AO_BRH(ObjectUtils.toString(data.get("ORG_AO_BRH")));
		vo_log.setORG_AO_NAME(ObjectUtils.toString(data.get("ORG_AO_NAME")));
		vo_log.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
		vo_log.setNEW_AO_BRH(ObjectUtils.toString(data.get("NEW_AO_BRH")));
		vo_log.setNEW_AO_NAME(ObjectUtils.toString(data.get("NEW_AO_NAME")));
		vo_log.setREG_AOCODE_EMP_ID(ObjectUtils.toString(data.get("APL_EMP_ID")));
		vo_log.setREG_AOCODE_SUB_DATETIME(new Timestamp(sdf1.parse(ObjectUtils.toString(data.get("APL_DATETIME"))).getTime()));
		vo_log.setLETGO_EMP_ID(CURRUSER);
		vo_log.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
		vo_log.setAPL_REASON(ObjectUtils.toString(data.get("APL_REASON")));
		if (data.get("APL_OTH_REASON") != null) {
			vo_log.setAPL_OTH_REASON(ObjectUtils.toString(data.get("APL_OTH_REASON")));
		} else if(StringUtils.equals("G", ObjectUtils.toString(data.get("TRS_TYPE")))) {
			vo_log.setAPL_OTH_REASON("調離職理專轄下客戶指派名單調整");
		}
		vo_log.setTRS_TXN_SOURCE(ObjectUtils.toString(data.get("TRS_TXN_SOURCE")));
		vo_log.setRETURN_400_YN("N");
		dam.create(vo_log);

		// 2017/9/4 test
		//Step3-1.寫資料到名單匯入主檔TBCAM_SFA_LEADS_IMP
		List tempData = new ArrayList<>();
		tempData.add(data);
		tempData.add(PRIVILEGEID);
		dam.newTransactionExeMethod(this, "GO_LEADS_IMP", tempData);
	}

	public void GO_LEADS_IMP(LinkedTreeMap<String, Object> data, String PRIVILEGEID) throws JBranchException {
		SimpleDateFormat HHMMSS = new SimpleDateFormat("HHmmss");
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT EMP_ID, EMP_NAME FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code");
		queryCondition.setObject("ao_code", data.get("NEW_AO_CODE"));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, String>> query = dam.exeQuery(queryCondition);

		//Step3-1.寫資料到名單匯入主檔TBCAM_SFA_LEADS_IMP
		QueryConditionIF queryCondition_imp = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql_imp = new StringBuffer();
		sql_imp.append("SELECT SEQNO FROM TBCAM_SFA_LEADS_IMP WHERE CAMPAIGN_ID = ");
		sql_imp.append("'TRS'||TO_CHAR(current_date, 'YYYYMMDD') || 'FLW' || :trs_flow_type ");
		queryCondition_imp.setObject("trs_flow_type", data.get("TRS_FLOW_TYPE"));
		queryCondition_imp.setQueryString(sql_imp.toString());
		List<Map<String, Object>> imp_list = dam.exeQuery(queryCondition_imp);
		if (imp_list.size() > 0) {
			//To Step3-2
			//Step3-2.寫資料到名單匯入暫存檔(客戶明細) 
			QueryConditionIF queryCondition_Imptemp_SEQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql_ImptempSEQ = new StringBuffer();
			sql_ImptempSEQ.append("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO FROM DUAL");
			queryCondition_Imptemp_SEQ.setQueryString(sql_ImptempSEQ.toString());
			List<Map<String, Object>> leadsImptempSEQ = dam.exeQuery(queryCondition_Imptemp_SEQ);
			BigDecimal seqNo_imptemp = (BigDecimal) leadsImptempSEQ.get(0).get("SEQNO");

			TBCAM_SFA_LE_IMP_TEMPVO vo_imp_temp = new TBCAM_SFA_LE_IMP_TEMPVO();
			vo_imp_temp.setSEQNO(seqNo_imptemp);
			vo_imp_temp.setIMP_SEQNO((BigDecimal) imp_list.get(0).get("SEQNO"));
			vo_imp_temp.setLEAD_ID("SYS" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + addZeroForNum(seqNo_imptemp.toString(), 8));
			vo_imp_temp.setCUST_ID(ObjectUtils.toString(data.get("CUST_ID")));
			vo_imp_temp.setCUST_NAME(ObjectUtils.toString(data.get("CUST_NAME")));
			vo_imp_temp.setBRANCH_ID(ObjectUtils.toString(data.get("NEW_AO_BRH")));
			vo_imp_temp.setEMP_ID(query.get(0).get("EMP_ID"));
			vo_imp_temp.setAO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
			vo_imp_temp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

			QueryConditionIF queryCondition_FRQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql_frq = new StringBuffer();
			sql_frq.append("SELECT FRQ_DAY FROM VWCRM_CUST_REVIEWDATE_MAP WHERE CON_DEGREE = ");
			sql_frq.append("(SELECT NVL(CON_DEGREE, 'S') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
			sql_frq.append("AND VIP_DEGREE = (SELECT NVL(TRIM(VIP_DEGREE), 'M') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
			queryCondition_FRQ.setObject("cust_id", data.get("CUST_ID"));
			queryCondition_FRQ.setQueryString(sql_frq.toString());
			List<Map<String, Object>> list_srq = dam.exeQuery(queryCondition_FRQ);
			BigDecimal seq_srq = new BigDecimal(ObjectUtils.toString(list_srq.get(0).get("FRQ_DAY")));

			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_WEEK, seq_srq.intValue());
			vo_imp_temp.setEND_DATE(new Timestamp(cal.getTime().getTime()));
			vo_imp_temp.setLEAD_TYPE("03");
			dam.create(vo_imp_temp);
		} else {
			QueryConditionIF queryCondition_ImpSEQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql_ImpSEQ = new StringBuffer();
			sql_ImpSEQ.append("SELECT SQ_TBCAM_SFA_LEADS_IMP.nextval AS SEQNO FROM DUAL");
			queryCondition_ImpSEQ.setQueryString(sql_ImpSEQ.toString());
			List<Map<String, Object>> leadsImpSEQ = dam.exeQuery(queryCondition_ImpSEQ);
			BigDecimal seqNo_imp = (BigDecimal) leadsImpSEQ.get(0).get("SEQNO");

			TBCAM_SFA_LEADS_IMPVO vo_imp = new TBCAM_SFA_LEADS_IMPVO();
			vo_imp.setSEQNO(seqNo_imp);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			vo_imp.setCAMPAIGN_ID("TRS" + sdf.format(new Date()) + "FLW" + data.get("TRS_FLOW_TYPE").toString());

			if ("1".equals(data.get("TRS_FLOW_TYPE"))) {
				vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 空Code");
			} else if ("2".equals(data.get("TRS_FLOW_TYPE"))) {
				vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code 同分行");
			} else if ("3".equals(data.get("TRS_FLOW_TYPE"))) {
				vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code FCH跨分行");
			} else if ("4".equals(data.get("TRS_FLOW_TYPE"))) {
				vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code跨分行");
			}

			vo_imp.setCAMPAIGN_DESC("提醒理專聯繫並告知客戶已更換新理專");
			vo_imp.setSTEP_ID(HHMMSS.format(new Date()));
			vo_imp.setLEAD_SOURCE_ID("04");
			vo_imp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

			Date dt = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(dt);
			c.add(Calendar.DAY_OF_WEEK, 35);
			vo_imp.setEND_DATE(new Timestamp(c.getTime().getTime()));

			vo_imp.setLEAD_TYPE("03");
			vo_imp.setLEAD_PARA1("N");
			vo_imp.setLEAD_PARA2("N");
			vo_imp.setIMP_STATUS("IN");
			if ("002".equals(PRIVILEGEID))
				vo_imp.setFIRST_CHANNEL("FCALL");
			else if ("003".equals(PRIVILEGEID))
				vo_imp.setFIRST_CHANNEL("FCH");
			dam.create(vo_imp);

			//Step3-2.寫資料到名單匯入暫存檔(客戶明細) 

			QueryConditionIF queryCondition_Imptemp_SEQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql_ImptempSEQ = new StringBuffer();
			sql_ImptempSEQ.append("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO FROM DUAL");
			queryCondition_Imptemp_SEQ.setQueryString(sql_ImptempSEQ.toString());
			List<Map<String, Object>> leadsImptempSEQ = dam.exeQuery(queryCondition_Imptemp_SEQ);
			BigDecimal seqNo_imptemp = (BigDecimal) leadsImptempSEQ.get(0).get("SEQNO");

			TBCAM_SFA_LE_IMP_TEMPVO vo_imp_temp = new TBCAM_SFA_LE_IMP_TEMPVO();

			vo_imp_temp.setSEQNO(seqNo_imptemp);
			vo_imp_temp.setIMP_SEQNO(seqNo_imp);

			vo_imp_temp.setLEAD_ID("SYS" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + addZeroForNum(seqNo_imptemp.toString(), 8));

			vo_imp_temp.setCUST_ID(ObjectUtils.toString(data.get("CUST_ID")));
			vo_imp_temp.setCUST_NAME(ObjectUtils.toString(data.get("CUST_NAME")));
			vo_imp_temp.setBRANCH_ID(ObjectUtils.toString(data.get("NEW_AO_BRH")));
			vo_imp_temp.setEMP_ID(query.get(0).get("EMP_ID"));
			vo_imp_temp.setAO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
			vo_imp_temp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

			QueryConditionIF queryCondition_FRQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql_frq = new StringBuffer();
			sql_frq.append("SELECT FRQ_DAY FROM VWCRM_CUST_REVIEWDATE_MAP WHERE CON_DEGREE = ");
			sql_frq.append("(SELECT NVL(CON_DEGREE, 'S') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
			sql_frq.append("AND VIP_DEGREE = (SELECT NVL(TRIM(VIP_DEGREE), 'M') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
			queryCondition_FRQ.setObject("cust_id", data.get("CUST_ID"));
			queryCondition_FRQ.setQueryString(sql_frq.toString());
			List<Map<String, Object>> list_srq = dam.exeQuery(queryCondition_FRQ);
			BigDecimal seq_srq = new BigDecimal(ObjectUtils.toString(list_srq.get(0).get("FRQ_DAY")));

			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_WEEK, seq_srq.intValue());

			vo_imp_temp.setEND_DATE(new Timestamp(cal.getTime().getTime()));
			vo_imp_temp.setLEAD_TYPE("03");

			dam.create(vo_imp_temp);
		}

		//Step3-3當客戶改AO Code或歸屬行時，若理專沒執行該名單，則要將所有名單換AO Code
		QueryConditionIF queryCondition_update = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql_update = new StringBuffer();
		sql_update.append("UPDATE TBCAM_SFA_LEADS SET AO_CODE = :new_ao_code, ");
		sql_update.append("EMP_ID = :new_ao_emp_id, ");
		sql_update.append("BRANCH_ID = :new_ao_brh ");
		sql_update.append("WHERE CUST_ID = :cust_id ");
		sql_update.append("AND LEAD_STATUS < '03' ");
		sql_update.append("AND fn_is_fc(EMP_ID) = 1 "); //#1357
		queryCondition_update.setQueryString(sql_update.toString());
		queryCondition_update.setObject("new_ao_code", data.get("NEW_AO_CODE"));
		queryCondition_update.setObject("new_ao_emp_id", query.get(0).get("EMP_ID"));
		queryCondition_update.setObject("new_ao_brh", data.get("NEW_AO_BRH"));
		queryCondition_update.setObject("cust_id", data.get("CUST_ID"));
		dam.exeUpdate(queryCondition_update);
	}

	//信箱Email格式檢查
	public static boolean isEmail(String email) {
		Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	/** 產生seq No */
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_CUST_AOCODE_CHGLOG_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	public String addZeroForNum(String str, int strLength) {

		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左補0
				// sb.append(str).append("0");//右補0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	//是否仍有調離職理專轄下客戶未指派
	public void chkInvalidRMPlist(Object body, IPrimitiveMap header) throws JBranchException {
		CRM361InputVO inputVO = (CRM361InputVO) body;
		CRM361OutputVO outputVO = new CRM361OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT 1 FROM TBCRM_TRS_AOCHG_PLIST A ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST B ON B.CUST_ID = A.CUST_ID ");
		sql.append(" WHERE A.TRS_TYPE = 'G' "); //調離職理專轄下客戶指派名單
		sql.append(" AND A.PROCESS_STATUS in ('L0','L1', 'L2', 'L3', 'L4', 'L5', 'BS') "); //尚未完成移轉
		sql.append(" AND A.ORG_AO_BRH = :loginBrh "); //登入分行
		sql.append(" AND A.ORG_AO_CODE = B.AO_CODE "); //客戶仍掛在已調離職理專身上
		//沒有其他帶移轉清單
		sql.append(" AND A.CUST_ID NOT IN (SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE TRS_TYPE <> 'G' AND PROCESS_STATUS in ('L0','L1', 'L2', 'L3', 'L4', 'L5', 'BS')) ");
		
		queryCondition.setObject("loginBrh", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		
		String msg = CollectionUtils.isEmpty(list) ? "" : "請先完成「調離職理專轄下客戶指派」專案名單";
		outputVO.setErrorMsg(msg);
		this.sendRtnObject(outputVO);
	}
	
}