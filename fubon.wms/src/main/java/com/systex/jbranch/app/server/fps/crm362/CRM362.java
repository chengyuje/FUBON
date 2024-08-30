package com.systex.jbranch.app.server.fps.crm362;

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
import com.systex.jbranch.app.server.fps.crm372.CRM372;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
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
 * @author Levi
 * @date 2020/03/18
 * 
 */
@Component("crm362")
@Scope("request")
public class CRM362 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM362.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM362InputVO inputVO = (CRM362InputVO) body;
		CRM362OutputVO outputVO = new CRM362OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT P.PRJ_ID, CASE WHEN LENGTH(P.CREATOR) = 6 AND P.CREATOR <> 'SYSTEM' THEN P.CREATOR ELSE NULL END AS CREATOR, "); 
		sql.append("       P.CUST_ID, N.COMPLAIN_YN, C.CUST_NAME, P.TRS_TYPE, P.TEMP_CAL_YN, C.CON_DEGREE, C.UEMP_ID, P.SEQ AS TRS_SEQ, ");
		sql.append("       C.VIP_DEGREE, C.AUM_AMT, P.ORG_AO_CODE, P.NEW_AO_CODE, P.APL_REASON, P.APL_OTH_REASON as IS_7YEARS, P.ORG_AO_BRH, ");
		sql.append("       P.OVER_CUST_NO_LIMIT_UP_YN, P.NEW_AO_BRH, P.AGMT_SEQ, P.AGMT_FILE, P.TRS_FLOW_TYPE, P.PROCESS_STATUS, P.TRS_TXN_SOURCE as CHG_TYPE, P.APL_EMP_ID, P.APL_DATETIME, P.APL_BRH_MGR_RPL_DATETIME, ORG_BRH_MGR_RPL_DATETIME, OP_MGR_RPL_DATETIME, ");
		sql.append("       DC_MGR_RPL_DATETIME, EMP.EMP_NAME AS ORG_AO_NAME, DEFN1.BRANCH_NAME AS ORG_BRANCH_NAME, EMP.EMP_ID AS ORG_AO_EMP_ID, EMP2.EMP_NAME AS NEW_AO_NAME, DEFN2.BRANCH_NAME AS NEW_BRANCH_NAME, EMP2.EMP_ID AS NEW_AO_EMP_ID, RO.ROLE_ID as OLD_ROLE_ID ");
		sql.append("FROM TBCRM_CUST_MAST C, TBCRM_CUST_NOTE N, TBCRM_TRS_AOCHG_PLIST P ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = P.ORG_AO_BRH ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN2 ON DEFN2.BRANCH_NBR = P.NEW_AO_BRH ");
		sql.append("LEFT JOIN VWORG_AO_INFO EMP ON  P.ORG_AO_CODE = EMP.AO_CODE ");
		sql.append("LEFT JOIN VWORG_AO_INFO EMP2 ON P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append("LEFT JOIN (SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO ");
		sql.append("WHERE MR.ROLE_ID = RO.ROLE_ID AND MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y') RO ON EMP.EMP_ID = RO.EMP_ID ");
		sql.append("WHERE C.CUST_ID = P.CUST_ID AND C.CUST_ID = N.CUST_ID AND P.PROCESS_STATUS <> 'F' ");
		sql.append("AND P.TRS_TYPE in (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.QUERY_TRS_TYPE_3') ");
		
		//客戶ID
		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append("and P.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
		//客戶姓名
		if (!StringUtils.isBlank(inputVO.getCust_name())) {
			sql.append("AND C.CUST_NAME like :cust_name ");
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
		}
		
		//等級
		if (!StringUtils.isBlank(inputVO.getCon_degree())) {
			sql.append("and C.CON_DEGREE = :con_degree ");
			queryCondition.setObject("con_degree", inputVO.getCon_degree());
		}
		
		//客群身份
		if (!StringUtils.isBlank(inputVO.getVip_degree())) {
			sql.append("and C.VIP_DEGREE = :vip_degree ");
			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
		}
		
		//專案代碼
		if (!StringUtils.isBlank(inputVO.getPrj_code())) {
			sql.append("and P.PRJ_ID = :PRJ_CODE ");
			queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
		}
		
//		// 舊理專篩選條件改為抓新理專, 沒改變數名
		if (StringUtils.isNotBlank(inputVO.getOrg_branch_nbr())) {
			sql.append("AND P.NEW_AO_BRH = :new_ao_brh ");
			queryCondition.setObject("new_ao_brh", inputVO.getOrg_branch_nbr());
		} else {
			sql.append("AND P.NEW_AO_BRH in (:branchlist) ");
			queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		
		//加入覆核流程&權限判斷
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);

		//總行
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND P.PROCESS_STATUS in ('L1','L2','L3','L4') ");
		}
		//登入者身份=主管
		else if (bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND P.PROCESS_STATUS = 'L1' ");

			//登入者身份=營運區督導
		} else if (mbrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND  P.PROCESS_STATUS = 'L2' "); 

			//登入者身份=區域中心主管
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND  P.PROCESS_STATUS = 'L3' "); 
			
		} else {
			throw new APException("使用者登入身分不適用");
		}
				
				
//		sql.append("ORDER BY PRJ_DATE_BGN DESC ");
		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(outputVO);
	}

	public void save(Object body, IPrimitiveMap header) throws Exception {
		CRM362InputVO inputVO = (CRM362InputVO) body;
		CRM362OutputVO return_VO = new CRM362OutputVO();
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
		//進行分派
		// jacky count_by_ao_code_change
		// S5:個金主管進行名單指派中(定義：任一分行未完成指派)
		CRM372 crm372 = (CRM372) PlatformContext.getBean("crm372");
		crm372.updatePlanStatus("S5", inputVO.getPrj_code());
				
		Set<String> ao_List = new HashSet<String>();

		for (Map<String, Object> data : inputVO.getApply_list()) {
			// get new role by user select
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRI.PRIVILEGEID FROM VWORG_AO_INFO EMP ");
			sql.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON EMP.EMP_ID = MR.EMP_ID ");
			sql.append("LEFT JOIN TBORG_ROLE RO ON MR.ROLE_ID = RO.ROLE_ID ");
			sql.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON RO.ROLE_ID = PRI.ROLEID ");
			sql.append("WHERE MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y' ");
			sql.append("AND EMP.AO_CODE = :ao_code ");
			queryCondition.setObject("ao_code", data.get("NEW_AO_CODE"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() == 0)
				throw new APException(data.get("NEW_AO_CODE") + ":該理專無角色，請先設定角色");
			String PRIVILEGEID = ObjectUtils.toString(list.get(0).get("PRIVILEGEID"));
			//分行主管
			if ("L1".equals(data.get("PROCESS_STATUS"))) {
				//十保不判斷TRS_FLOW_TYPE
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
				vo.setAPL_BRH_MGR(CURRUSER);
				vo.setAPL_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setAPL_BRH_MGR_RPL_STATUS("Y");
				vo.setTEMP_CAL_YN("N");
				vo.setTEMP_CAL_EMP_ID(null);
				vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
				// 超過最適戶
				if (StringUtils.isNotBlank(inputVO.getFlag())  && inputVO.getFlaggedList().contains(ObjectUtils.toString(data.get("NEW_AO_CODE")))) {
					vo.setPROCESS_STATUS("L2");
					vo.setOVER_CUST_NO_LIMIT_UP_YN("Y");

					// mantis 0004267: 主管覆核客戶移轉 先埋log
					logger.info("CRM362NEW_AO-超過最適戶 L2:" + ObjectUtils.toString(data.get("NEW_AO_CODE")));
					logger.info("CRM362處理流程-超過最適戶 L2:" + vo.getPROCESS_STATUS());
				} else {
					//十保專區案例需總行放行自訂生效日
					vo.setPROCESS_STATUS("L5");
//					vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					// jacky
					ao_List.add(ObjectUtils.toString(data.get("ORG_AO_CODE")));
					ao_List.add(ObjectUtils.toString(data.get("NEW_AO_CODE")));
				}
				dam.update(vo);

//				if (StringUtils.isBlank(inputVO.getFlag()))
//					this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
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
					//十保專區案例需總行放行自訂生效日
//					vo.setPROCESS_STATUS("S");
					vo.setPROCESS_STATUS("L5");
					vo.setTEMP_CAL_YN("N");
					vo.setTEMP_CAL_EMP_ID(null);
					vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
					vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
//					this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
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
					//十保專區案例需總行放行自訂生效日
//					vo.setPROCESS_STATUS("S");
					vo.setPROCESS_STATUS("L5");
					vo.setTEMP_CAL_YN("N");
					vo.setTEMP_CAL_EMP_ID(null);
					vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
					vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
//					this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
				}
			}
		}
		
		//進行檢查是否均已分派完成    S6：個金主管指派完成 --所有客戶均以分派完成
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE PRJ_ID = :PRJ_CODE AND PROCESS_STATUS IN ('L1','L2','L3','L4') ");
		queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.isEmpty()){
			crm372.updatePlanStatus("S6", inputVO.getPrj_code());
		}
		
		this.sendRtnObject(return_VO);
	}
}