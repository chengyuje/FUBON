package com.systex.jbranch.app.server.fps.crm371;

import java.io.File;
import java.io.FileOutputStream;
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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
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
import com.systex.jbranch.app.server.fps.crm372.CRM372;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/09/05
 * 
 */
@Component("crm371")
@Scope("request")
public class CRM371 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM371.class);

	//	public void ao_inquire(Object body, IPrimitiveMap header) throws JBranchException {
	//		CRM371OutputVO return_VO = new CRM371OutputVO();
	//		dam = this.getDataAccessManager();
	//		
	//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	//		StringBuffer sql = new StringBuffer();
	//		sql.append("SELECT AO_CODE, BRANCH_NBR, EMP_NAME, EMP_ID, AO_JOB_RANK FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
	//		sql.append("WHERE AO_CODE IS NOT NULL AND BRANCH_NBR IN (:brNbrList)  ");
	//		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
	//		queryCondition.setQueryString(sql.toString());
	//
	//		List list = dam.exeQuery(queryCondition);
	//		return_VO.setAo_list(list);
	//		sendRtnObject(return_VO);
	//	}

	public void prj_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM371InputVO inputVO = (CRM371InputVO) body;
		CRM371OutputVO return_VO = new CRM371OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_ID, PRJ_NAME, DESC_01, DESC_02, DESC_03, DESC_04, DESC_05, DESC_06, DESC_07, ");
		sql.append("DESC_08, DESC_09, DESC_10, DESC_11, DESC_12, DESC_13, DESC_14, DESC_15 FROM TBCRM_TRS_PRJ_MAST ");
		sql.append("WHERE TRUNC(current_date) BETWEEN PRJ_DATE_BGN AND PRJ_DATE_END ");
		sql.append("AND PRJ_TYPE IS NULL ");  //非輪調換手名單
		sql.append("ORDER BY PRJ_DATE_BGN DESC ");
		queryCondition.setQueryString(sql.toString());

		List list = dam.exeQuery(queryCondition);
		return_VO.setPrj_list(list);
		
		//十保專區
		if(StringUtils.isNotBlank(inputVO.getPrj_status())){
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> headmgrMap = xmlInfo.doGetVariable(
					"FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			boolean headFlag = headmgrMap.containsKey(roleID);
			
			String[] prjStatus = inputVO.getPrj_status().split(",");
			List prjStatusList = new ArrayList();
			for(int i=0;i<prjStatus.length;i++){
				prjStatusList.add(prjStatus[i]);
			}
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRJ_CODE, PRJ_NAME FROM TBCRM_10CMDT_PLAN_MAIN M ");
			sql.append("WHERE PRJ_STATUS IN (:prjStatus) ");
			sql.append("AND EXISTS (SELECT 'X' FROM TBCRM_TRS_AOCHG_PLIST P WHERE M.PRJ_CODE = P.PRJ_ID  ");
			//不是總行人員增加可視範圍
			if(!headFlag){
				sql.append(" AND P.ORG_AO_BRH IN (:LoginBrch) ");
				queryCondition.setObject("LoginBrch",  getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
						
			sql.append(") ORDER BY START_DATE DESC ");
			queryCondition.setObject("prjStatus", prjStatusList);
			queryCondition.setQueryString(sql.toString());

			List list2 = dam.exeQuery(queryCondition);
			return_VO.setPrj_list2(list2);
		}		
		
		this.sendRtnObject(return_VO);
	}

	public void inquire_common(Object body, IPrimitiveMap header) throws JBranchException {
		CRM371InputVO inputVO = (CRM371InputVO) body;
		CRM371OutputVO return_VO = new CRM371OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT P.CUST_ID, N.COMPLAIN_YN, C.CUST_NAME, P.TRS_TYPE, P.TEMP_CAL_YN, NVL(C.CON_DEGREE,'OTH') as CON_DEGREE, P.SEQ AS TRS_SEQ, ");
		sql.append("       NVL(C.VIP_DEGREE,'M') as VIP_DEGREE, C.AUM_AMT, P.ORG_AO_CODE, P.NEW_AO_CODE, P.APL_REASON, P.APL_OTH_REASON, P.ORG_AO_BRH, ");
		sql.append("       P.NEW_AO_BRH, P.AGMT_SEQ, P.AGMT_FILE, P.CALL_REVIEW_STATUS, P.CALL_REVIEW_NOTE, P.TRS_FLOW_TYPE, P.PROCESS_STATUS, P.TRS_TXN_SOURCE, P.APL_EMP_ID, P.APL_DATETIME, P.APL_BRH_MGR_RPL_DATETIME, ORG_BRH_MGR_RPL_DATETIME, OP_MGR_RPL_DATETIME, ");
		sql.append("       DC_MGR_RPL_DATETIME, EMP.EMP_NAME AS ORG_AO_NAME, DEFN1.BRANCH_NAME AS ORG_BRANCH_NAME, EMP2.EMP_NAME AS NEW_AO_NAME, DEFN2.BRANCH_NAME AS NEW_BRANCH_NAME, EMP2.EMP_ID AS NEW_AO_EMP_ID, RO.ROLE_ID, PRI.PRIVILEGEID, ");
		sql.append("       CASE WHEN LENGTH(P.CREATOR) = 6 AND P.CREATOR <> 'SYSTEM' THEN P.CREATOR ELSE NULL END AS CREATOR "); // 20190828:跨分行申請退件，無論如何都要寄信(君榮說的) modify by ocean
		sql.append("FROM TBCRM_CUST_MAST C, ");
		sql.append("TBCRM_CUST_NOTE N, ");
		sql.append("TBCRM_TRS_AOCHG_PLIST P ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = P.ORG_AO_BRH ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN2 ON DEFN2.BRANCH_NBR = P.NEW_AO_BRH ");
		//sql.append("VWORG_AO_INFO EMP, VWORG_AO_INFO EMP2 ");
		sql.append("LEFT JOIN VWORG_AO_INFO EMP ON P.ORG_AO_CODE = EMP.AO_CODE ");
		sql.append("LEFT JOIN VWORG_AO_INFO EMP2 ON P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append("LEFT JOIN (");
		sql.append("  SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME ");
		sql.append("  FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO ");
		sql.append("  WHERE MR.ROLE_ID = RO.ROLE_ID AND MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y'");
		sql.append(") RO ON EMP2.EMP_ID = RO.EMP_ID ");
		sql.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON RO.ROLE_ID = PRI.ROLEID ");
		sql.append("WHERE C.CUST_ID = P.CUST_ID ");
		sql.append("AND C.CUST_ID = N.CUST_ID ");
		//sql.append("AND P.ORG_AO_CODE = EMP.AO_CODE ");
		//sql.append("AND P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append("AND P.PROCESS_STATUS <> 'F' ");
		//0506: 理專輪調十戒客戶移轉總行覆核不可於一般總行覆核出現
		sql.append("AND P.TRS_TYPE NOT IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.QUERY_TRS_TYPE_3') ");

		//例行與最適客戶需分開
		if (!StringUtils.isBlank(inputVO.getControl_yn())) {
			if ("Y".equals(inputVO.getControl_yn())) { //最適客戶
				//移轉類別在最適客戶不控
				sql.append("AND ");
			} else if ("N".equals(inputVO.getControl_yn())) { //例行
				//移轉類別增加判斷例行、專案篩選條件
				sql.append("AND P.TRS_TYPE in (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.QUERY_TRS_TYPE_1') ");
				sql.append("AND NOT ");
			}
			sql.append("EXISTS (SELECT 1 FROM VWCRM_AO_CONTROL VAC WHERE P.NEW_AO_CODE = VAC.AO_CODE) ");
		}

		if (!StringUtils.isBlank(inputVO.getTrs_type())) {
			sql.append("AND P.TRS_TYPE = :trs_type ");
			queryCondition.setObject("trs_type", inputVO.getTrs_type());
		}

		// 舊理專篩選條件改為抓新理專, 沒改變數名
		if (StringUtils.isNotBlank(inputVO.getOrg_branch_nbr())) {
			sql.append("AND P.NEW_AO_BRH = :new_ao_brh ");
			queryCondition.setObject("new_ao_brh", inputVO.getOrg_branch_nbr());
		} else {
			sql.append("AND P.NEW_AO_BRH in (:branchlist) ");
			queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		if (StringUtils.isNotBlank(inputVO.getOrg_ao_code())) {
			sql.append("AND P.NEW_AO_CODE = :new_ao_code ");
			queryCondition.setObject("new_ao_code", inputVO.getOrg_ao_code());
		}

		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append("AND P.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}

		if (!StringUtils.isBlank(inputVO.getCon_degree())) {
			sql.append("AND C.CON_DEGREE = :con_degree ");
			queryCondition.setObject("con_degree", inputVO.getCon_degree());
		}

		if (!StringUtils.isBlank(inputVO.getChg_frq())) {
			sql.append("AND P.CUST_ID IN (SELECT CUST_ID FROM TBCRM_CUST_AOCODE_CHGLOG ");
			sql.append("WHERE TRUNC(LETGO_DATETIME) >= TRUNC(SYSDATE - :chg_frq )) ");
			queryCondition.setObject("chg_frq", inputVO.getChg_frq());
		}

		if (!StringUtils.isBlank(inputVO.getCust_name())) {
			sql.append("AND C.CUST_NAME like :cust_name ");
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
		}

		if (!StringUtils.isBlank(inputVO.getVip_degree())) {
			sql.append("AND C.VIP_DEGREE = :vip_degree ");
			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
		}

		if (!StringUtils.isBlank(inputVO.getMatch_yn())) {
			sql.append("AND N.TAKE_CARE_MATCH_YN = :match_yn ");
			queryCondition.setObject("match_yn", inputVO.getMatch_yn());
		}

		if (!StringUtils.isBlank(inputVO.getCall_review_type())) {
			sql.append("AND P.CALL_REVIEW_STATUS = :call_review_type ");
			queryCondition.setObject("call_review_type", inputVO.getCall_review_type());
		}

		//加入覆核流程&權限判斷

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);//總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //區域中心主管

		//#0002402 : 登入者身份=總行 OR 區域中心主管
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND (P.PROCESS_STATUS = 'L5') ");
		} else {
			throw new APException("使用者登入身分不適用");
		}

		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void inquire_project(Object body, IPrimitiveMap header) throws JBranchException {
		CRM371InputVO inputVO = (CRM371InputVO) body;
		CRM371OutputVO return_VO = new CRM371OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT P.CUST_ID, N.COMPLAIN_YN, C.CUST_NAME, P.TRS_TYPE, P.TEMP_CAL_YN, NVL(C.CON_DEGREE,'OTH') as CON_DEGREE, P.SEQ AS TRS_SEQ, ");
		sql.append("       NVL(C.VIP_DEGREE,'M') as VIP_DEGREE, C.AUM_AMT, P.ORG_AO_CODE, P.NEW_AO_CODE, P.APL_REASON, P.APL_OTH_REASON, P.ORG_AO_BRH, ");
		sql.append("       P.NEW_AO_BRH, P.AGMT_SEQ, P.AGMT_FILE, P.CALL_REVIEW_STATUS, P.CALL_REVIEW_NOTE, P.TRS_FLOW_TYPE, P.PROCESS_STATUS, P.TRS_TXN_SOURCE, P.APL_EMP_ID, P.APL_DATETIME, P.APL_BRH_MGR_RPL_DATETIME, ORG_BRH_MGR_RPL_DATETIME, OP_MGR_RPL_DATETIME, ");
		sql.append("       DC_MGR_RPL_DATETIME, EMP.EMP_NAME AS ORG_AO_NAME, DEFN1.BRANCH_NAME AS ORG_BRANCH_NAME, EMP2.EMP_NAME AS NEW_AO_NAME, DEFN2.BRANCH_NAME AS NEW_BRANCH_NAME, EMP2.EMP_ID AS NEW_AO_EMP_ID, RO.ROLE_ID, PRI.PRIVILEGEID, ");
		sql.append("       PDTL.DATA_01, PDTL.DATA_02, PDTL.DATA_03, PDTL.DATA_04, PDTL.DATA_05, PDTL.DATA_06, PDTL.DATA_07, ");
		sql.append("       PDTL.DATA_08, PDTL.DATA_09, PDTL.DATA_10, PDTL.DATA_11, PDTL.DATA_12, PDTL.DATA_13, PDTL.DATA_14, PDTL.DATA_15, ");
		sql.append("       CASE WHEN LENGTH(P.CREATOR) = 6 AND P.CREATOR <> 'SYSTEM' THEN P.CREATOR ELSE NULL END AS CREATOR "); // 20190828:跨分行申請退件，無論如何都要寄信(君榮說的) modify by ocean
		sql.append("FROM TBCRM_CUST_MAST C, ");
		sql.append("TBCRM_CUST_NOTE N, ");
		sql.append("TBCRM_TRS_AOCHG_PLIST P ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = P.ORG_AO_BRH ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN2 ON DEFN2.BRANCH_NBR = P.NEW_AO_BRH ");
		sql.append("LEFT JOIN TBCRM_TRS_PRJ_MAST PRJ ON P.PRJ_ID = PRJ.PRJ_ID ");
		//sql.append("LEFT JOIN TBCRM_TRS_PRJ_DTL PDTL ON PRJ.PRJ_ID = PDTL.PRJ_ID, ");
		sql.append("LEFT JOIN TBCRM_TRS_PRJ_DTL PDTL ON PRJ.PRJ_ID = PDTL.PRJ_ID AND P.PRJ_ID = PDTL.PRJ_ID AND P.CUST_ID = PDTL.CUST_ID ");
		//sql.append("VWORG_AO_INFO EMP, VWORG_AO_INFO EMP2 ");
		sql.append("LEFT JOIN VWORG_AO_INFO EMP ON P.ORG_AO_CODE = EMP.AO_CODE ");
		sql.append("LEFT JOIN VWORG_AO_INFO EMP2 ON P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append("LEFT JOIN (");
		sql.append("  SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME ");
		sql.append("  FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO ");
		sql.append("  WHERE MR.ROLE_ID = RO.ROLE_ID AND MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y'");
		sql.append(") RO ON EMP2.EMP_ID = RO.EMP_ID ");
		sql.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON RO.ROLE_ID = PRI.ROLEID ");
		sql.append("WHERE C.CUST_ID = P.CUST_ID ");
		sql.append("AND C.CUST_ID = N.CUST_ID ");
		//sql.append("AND P.ORG_AO_CODE = EMP.AO_CODE ");
		//sql.append("AND P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append("AND P.PROCESS_STATUS <> 'F' ");
		sql.append("AND PRJ.PRJ_TYPE IS NULL ");  //非輪調換手名單
		//移轉類別增加判斷例行、專案篩選條件
		if(StringUtils.equals(inputVO.getTrs_type(), "A")){ //十保專區
			sql.append("AND P.TRS_TYPE in (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.QUERY_TRS_TYPE_3') ");
		}else{
			sql.append("AND P.TRS_TYPE in (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.QUERY_TRS_TYPE_2') ");
		}
		

		// 2021/01/21 0500: 十誡總行覆核功能不檢核最適客戶數
		//sql.append("AND NOT EXISTS (SELECT 1 FROM VWCRM_AO_CONTROL VAC WHERE P.NEW_AO_CODE = VAC.AO_CODE) ");

		if (!StringUtils.isBlank(inputVO.getTrs_type())) {
			sql.append("AND P.TRS_TYPE = :trs_type ");
			queryCondition.setObject("trs_type", inputVO.getTrs_type());
		}

		if (!StringUtils.isBlank(inputVO.getPrj_id())) {
			sql.append("AND P.PRJ_ID = :prj_id ");
			queryCondition.setObject("prj_id", inputVO.getPrj_id());
		}

		// 舊理專篩選條件改為抓新理專, 沒改變數名
		if (StringUtils.isNotBlank(inputVO.getOrg_branch_nbr())) {
			sql.append("AND P.NEW_AO_BRH = :new_ao_brh ");
			queryCondition.setObject("new_ao_brh", inputVO.getOrg_branch_nbr());
		} else {
			sql.append("AND P.NEW_AO_BRH in (:branchlist) ");
			queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		if (StringUtils.isNotBlank(inputVO.getOrg_ao_code())) {
			sql.append("AND P.NEW_AO_CODE = :new_ao_code ");
			queryCondition.setObject("new_ao_code", inputVO.getOrg_ao_code());
		}

		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append("AND P.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}

		if (!StringUtils.isBlank(inputVO.getCon_degree())) {
			sql.append("AND C.CON_DEGREE = :con_degree ");
			queryCondition.setObject("con_degree", inputVO.getCon_degree());
		}

		if (!StringUtils.isBlank(inputVO.getChg_frq())) {
			sql.append("AND P.CUST_ID IN (SELECT CUST_ID FROM TBCRM_CUST_AOCODE_CHGLOG ");
			sql.append("WHERE TRUNC(LETGO_DATETIME) >= TRUNC(SYSDATE - :chg_frq )) ");
			queryCondition.setObject("chg_frq", inputVO.getChg_frq());
		}

		if (!StringUtils.isBlank(inputVO.getCust_name())) {
			sql.append("AND C.CUST_NAME like :cust_name ");
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
		}

		if (!StringUtils.isBlank(inputVO.getVip_degree())) {
			sql.append("AND C.VIP_DEGREE = :vip_degree ");
			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
		}

		if (!StringUtils.isBlank(inputVO.getMatch_yn())) {
			sql.append("AND N.TAKE_CARE_MATCH_YN = :match_yn ");
			queryCondition.setObject("match_yn", inputVO.getMatch_yn());
		}

		if (!StringUtils.isBlank(inputVO.getTemp_yn())) {
			sql.append("AND P.TEMP_CAL_YN = :temp_yn ");
			queryCondition.setObject("temp_yn", inputVO.getTemp_yn());
		}

		//加入覆核流程&權限判斷

		XmlInfo xmlInfo = new XmlInfo();

		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);//總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //區域中心主管

		//#0002402 : 登入者身份=總行 OR 區域中心主管
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			if(StringUtils.equals(inputVO.getTrs_type(), "A")){//十保專區覆核
				sql.append("AND P.PROCESS_STATUS = 'L5'  ");
			}else{
				sql.append("AND ((P.TRS_FLOW_TYPE = '3' AND P.PROCESS_STATUS = 'L5') OR ");
				sql.append("(P.TRS_FLOW_TYPE = '4' AND P.PROCESS_STATUS = 'L5'))  ");
			}
			

		} else {
			throw new APException("使用者登入身分不適用");
		}

		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);

		sendRtnObject(return_VO);
	}

	public void save_cust(Object body, IPrimitiveMap header) throws JBranchException {
		CRM371InputVO inputVO = (CRM371InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
		vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, inputVO.getSeq());
		vo.setCALL_REVIEW_NOTE(inputVO.getTemp_yn());
		vo.setCALL_REVIEW_STATUS(inputVO.getCall_review_type());
		dam.update(vo);

		this.sendRtnObject(null);
	}

	public void checkOther(Object body, IPrimitiveMap header) throws JBranchException {
		CRM371InputVO inputVO = (CRM371InputVO) body;
		CRM371OutputVO return_VO = new CRM371OutputVO();
		dam = this.getDataAccessManager();

		Boolean Check1 = false;
		List<String> res1 = new ArrayList<String>();
		Boolean Check2 = false;
		List<String> res2 = new ArrayList<String>();
		for (Map<String, Object> map : inputVO.getApply_list()) {
			// 該客戶有2筆以上正在進行跨分行移轉，不得進行放行
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE CUST_ID = :cust_id AND PROCESS_STATUS in ( 'L1', 'L2', 'L3', 'L4', 'L5') AND TRS_TYPE = '3'");
			queryCondition.setObject("cust_id", map.get("CUST_ID"));
			List list = dam.exeQuery(queryCondition);
			if (list.size() > 1) {
				Check1 = true;
				res1.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			// 公司戶與公司負責人Code不同
			//			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//			StringBuffer sql = new StringBuffer();
			//			sql.append("SELECT M1.CUST_ID, TRIM(M1.RPRS_ID) as RPRS_ID, M2.AO_CODE FROM TBCRM_CUST_MAST M1 ");
			//			sql.append("LEFT JOIN TBCRM_CUST_MAST M2 ON TRIM(M1.RPRS_ID) = M2.CUST_ID WHERE 1=1 ");
			//			sql.append("AND LENGTH(M1.CUST_ID) < 10 AND M1.CUST_ID = :cust_id ");
			//			queryCondition.setObject("cust_id", map.get("CUST_ID"));
			//			queryCondition.setQueryString(sql.toString());
			//			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			//			if(list2.size() != 0) {
			//				// 有公司
			//				if(list2.get(0).get("RPRS_ID") != null && list2.get(0).get("AO_CODE") != null) {
			//					if(!StringUtils.equals(map.get("NEW_AO_CODE").toString(), ObjectUtils.toString(list2.get(0).get("AO_CODE")))) {
			//						Check2 = true;
			//						res2.add(ObjectUtils.toString(map.get("CUST_ID")));
			//					}
			//				}
			//			}
		}

		if (Check1) {
			return_VO.setResultList2("ERR1");
			return_VO.setResultList(res1);
		} else if (Check2) {
			return_VO.setResultList2("ERR2");
			return_VO.setResultList(res2);
		} else
			return_VO.setResultList2("GOOD");
		this.sendRtnObject(return_VO);
	}

	public void save(Object body, IPrimitiveMap header) throws Exception {

		CRM371InputVO inputVO = (CRM371InputVO) body;
		dam = this.getDataAccessManager();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
				//總行
				if ("L5".equals(data.get("PROCESS_STATUS"))) {
					/* TRS_FLOW_TYPE = 3 or 4, 不判斷，進來的途徑太多，有可能有同分行移轉 */
					//if ("3".equals(data.get("TRS_FLOW_TYPE")) || "4".equals(data.get("TRS_FLOW_TYPE"))) {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setHQ_MGR((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setHQ_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setHQ_MGR_RPL_STATUS("Y");
					if ("S".equals(inputVO.getAgree_type())) {
						vo.setPROCESS_STATUS("S");
						vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					} else if ("BS".equals(inputVO.getAgree_type())) {
						vo.setPROCESS_STATUS("BS");
						vo.setACT_DATE(new Timestamp(inputVO.getAct_date().getTime()));
					}
					dam.update(vo);
					//}
					// 立即生效
					//======執行生效後動作======
					if ("S".equals(inputVO.getAgree_type())) {
						// jacky
						ao_List.add(ObjectUtils.toString(data.get("ORG_AO_CODE")));
						ao_List.add(ObjectUtils.toString(data.get("NEW_AO_CODE")));
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
						vo_log.setLETGO_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						if ("S".equals(inputVO.getAgree_type())) {
							vo_log.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
						} else if ("BS".equals(inputVO.getAgree_type())) {
							vo_log.setLETGO_DATETIME(new Timestamp(inputVO.getAct_date().getTime()));
						}
						vo_log.setAPL_REASON(ObjectUtils.toString(data.get("APL_REASON")));
						if (data.get("APL_OTH_REASON") != null)
							vo_log.setAPL_OTH_REASON(ObjectUtils.toString(data.get("APL_OTH_REASON")));
						vo_log.setTRS_TXN_SOURCE(ObjectUtils.toString(data.get("TRS_TXN_SOURCE")));
						vo_log.setRETURN_400_YN("N");
						dam.create(vo_log);

						// 2017/9/4 test
						//Step3-1.寫資料到名單匯入主檔TBCAM_SFA_LEADS_IMP
						List tempData = new ArrayList<>();
						tempData.add(data);
						dam.newTransactionExeMethod(this, "GO_LEADS_IMP", tempData);
					}
					// 2020-8-17 增加預約生效應作廢流程中案件
					// 2017/7/3 結束同一客戶在移轉流程的移轉單
					// 2020-8-17 結束同一客戶在移轉流程中的移轉單(for十保名單) 增加一參數CRM.TRS_TYPE_PRIORITY = 'A',PARAM_ORDER=5, PROCESS_STATUS='BS'
					if("S".equals(inputVO.getAgree_type()) || "BS".equals(inputVO.getAgree_type())) {
						QueryConditionIF queryCondition_close = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						StringBuffer sql_close = new StringBuffer();
						sql_close.append("UPDATE TBCRM_TRS_AOCHG_PLIST ");
						sql_close.append("SET HQ_MGR = :emp_id, HQ_MGR_RPL_DATETIME = sysdate, HQ_MGR_RPL_STATUS = 'N', PROCESS_STATUS = 'F' ");
						sql_close.append("WHERE 1=1 AND CUST_ID = :cust_id ");
						sql_close.append("AND SEQ <> :seq ");
						sql_close.append("AND PROCESS_STATUS in ('L1', 'L2', 'L3', 'L4', 'L5', 'BS') ");
						sql_close.append("AND TRS_TYPE in ( SELECT PARAM_CODE FROM TBSYSPARAMETER ");
						sql_close.append("WHERE PARAM_TYPE = 'CRM.TRS_TYPE_PRIORITY' ");
						sql_close.append("AND PARAM_ORDER >= (SELECT PARAM_ORDER FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.TRS_TYPE_PRIORITY' AND PARAM_CODE = :param_code)) ");
						queryCondition_close.setQueryString(sql_close.toString());
						queryCondition_close.setObject("seq", data.get("TRS_SEQ"));
						queryCondition_close.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID).toString());
						queryCondition_close.setObject("cust_id", data.get("CUST_ID"));
						queryCondition_close.setObject("param_code", data.get("TRS_TYPE"));
						dam.exeUpdate(queryCondition_close);
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
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
				// 總行
				if (data.get("PROCESS_STATUS").equals("L5")) {
					vo.setHQ_MGR((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setHQ_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setHQ_MGR_RPL_STATUS("N");
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
						Log.info("申請人無E-mail");
					} else {
						if (isEmail(ObjectUtils.toString(creatorMail)) == false)
							Log.info(creatorMail + "申請人Email格式錯誤");
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
		
		//十保需求檢查是否均已完成生效日設定 S7：已確認生效日 --總行都已將客戶押上生效日
		//還有其他放行功能-如只有十保專案才做以下檢查
		if(StringUtils.isNotBlank(inputVO.getPrj_id())){
			CRM372 crm372 = (CRM372) PlatformContext.getBean("crm372");
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE PRJ_ID = :PRJ_ID AND PROCESS_STATUS IN ('L1', 'L2', 'L3', 'L4', 'L5') ");
			queryCondition.setObject("PRJ_ID", inputVO.getPrj_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if(list.isEmpty()){
				crm372.updatePlanStatus("S7", inputVO.getPrj_id());
			}
		}
		this.sendRtnObject(null);
	}

	public void GO_LEADS_IMP(LinkedTreeMap<String, Object> data) throws JBranchException {
		SimpleDateFormat HHMMSS = new SimpleDateFormat("HHmmss");
		dam = this.getDataAccessManager();

		// Step3-1.寫資料到名單匯入主檔TBCAM_SFA_LEADS_IMP
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
			vo_imp_temp.setEMP_ID(ObjectUtils.toString(data.get("NEW_AO_EMP_ID")));
			vo_imp_temp.setAO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
			vo_imp_temp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
			QueryConditionIF queryCondition_FRQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition_FRQ.setQueryString("SELECT FRQ_DAY FROM VWCRM_CUST_REVIEWDATE_MAP WHERE CON_DEGREE = :con_degree AND VIP_DEGREE = :vip_degree ");
			queryCondition_FRQ.setObject("con_degree", data.get("CON_DEGREE"));
			queryCondition_FRQ.setObject("vip_degree", data.get("VIP_DEGREE"));
			List<Map<String, Object>> list_srq = dam.exeQuery(queryCondition_FRQ);
			BigDecimal seq_srq = new BigDecimal(ObjectUtils.toString(list_srq.get(0).get("FRQ_DAY")));

			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_WEEK, seq_srq.intValue());
			vo_imp_temp.setEND_DATE(new Timestamp(cal.getTime().getTime()));
			if("3".equals(data.get("TRS_FLOW_TYPE"))) {
				vo_imp_temp.setLEAD_TYPE("03");
			} else {
				vo_imp_temp.setLEAD_TYPE("04");
			}
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

			if("3".equals(data.get("TRS_FLOW_TYPE"))) {
				vo_imp.setLEAD_TYPE("03");
			} else {
				vo_imp.setLEAD_TYPE("04");
			}
			vo_imp.setLEAD_PARA1("N");
			vo_imp.setLEAD_PARA2("N");
			vo_imp.setIMP_STATUS("IN");
			if ("002".equals(data.get("PRIVILEGEID")))
				vo_imp.setFIRST_CHANNEL("FCALL");
			else if ("003".equals(data.get("PRIVILEGEID")))
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
			vo_imp_temp.setEMP_ID(ObjectUtils.toString(data.get("NEW_AO_EMP_ID")));
			vo_imp_temp.setAO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
			vo_imp_temp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

			QueryConditionIF queryCondition_FRQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition_FRQ.setQueryString("SELECT FRQ_DAY FROM VWCRM_CUST_REVIEWDATE_MAP WHERE CON_DEGREE = :con_degree AND VIP_DEGREE = :vip_degree ");
			queryCondition_FRQ.setObject("con_degree", data.get("CON_DEGREE"));
			queryCondition_FRQ.setObject("vip_degree", data.get("VIP_DEGREE"));
			List<Map<String, Object>> list_srq = dam.exeQuery(queryCondition_FRQ);
			BigDecimal seq_srq = new BigDecimal(ObjectUtils.toString(list_srq.get(0).get("FRQ_DAY")));

			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_WEEK, seq_srq.intValue());

			vo_imp_temp.setEND_DATE(new Timestamp(cal.getTime().getTime()));
			if("3".equals(data.get("TRS_FLOW_TYPE"))) {
				vo_imp_temp.setLEAD_TYPE("03");
			} else {
				vo_imp_temp.setLEAD_TYPE("04");
			}
			dam.create(vo_imp_temp);
		}

		// Step3-3當客戶改AO Code或歸屬行時，若理專沒執行該名單，則要將所有名單換AO Code
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
		queryCondition_update.setObject("new_ao_emp_id", data.get("NEW_AO_EMP_ID"));
		queryCondition_update.setObject("new_ao_brh", data.get("NEW_AO_BRH"));
		queryCondition_update.setObject("cust_id", data.get("CUST_ID"));
		dam.exeUpdate(queryCondition_update);
	}

	//資況表客戶同意書
	public void download(Object body, IPrimitiveMap header) throws Exception {
		CRM371InputVO inputVO = (CRM371InputVO) body;
		dam = this.getDataAccessManager();
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT AGMT_SEQ, AGMT_FILE, AGMT_FILE_TYPE FROM TBCRM_TRS_AOCHG_PLIST where SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(list)) {
			Blob blob = (Blob) list.get(0).get("AGMT_FILE");
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			String fileName = "客戶同意書_" + ObjectUtils.toString(list.get(0).get("AGMT_SEQ")) + "." + ObjectUtils.toString(list.get(0).get("AGMT_FILE_TYPE"));
	
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.write(blobAsBytes);
			fos.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName);
		}
		
		this.sendRtnObject(null);
	}
	
	//十保客戶指定聲明書
	public void download2(Object body, IPrimitiveMap header) throws Exception {
		CRM371InputVO inputVO = (CRM371InputVO) body;
		dam = this.getDataAccessManager();
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//檢查是否有十保客戶指定聲明書
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT SEQ, CMDT_FILE, CMDT_FILE_TYPE FROM TBCRM_10CMDT_CHGAO_FILE where PLIST_SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(list)) {
			Blob blob2 = (Blob) list.get(0).get("CMDT_FILE");
			int blobLength2 = (int) blob2.length();
			byte[] blobAsBytes2 = blob2.getBytes(1, blobLength2);
			String fileName2 = "客戶指定理專聲明書_" + ObjectUtils.toString(list.get(0).get("SEQ")) + "." + ObjectUtils.toString(list.get(0).get("CMDT_FILE_TYPE"));
			
			uuid = UUID.randomUUID().toString();
			File targetFile2 = new File(filePath, uuid);
			FileOutputStream fos2 = new FileOutputStream(targetFile2);
			fos2.write(blobAsBytes2);
			fos2.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName2);
		}
		this.sendRtnObject(null);
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

	//信箱Email格式檢查
	public static boolean isEmail(String email) {
		Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

}