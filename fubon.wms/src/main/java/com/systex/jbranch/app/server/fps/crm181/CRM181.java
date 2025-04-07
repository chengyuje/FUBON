package com.systex.jbranch.app.server.fps.crm181;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_MD_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.info.fubonuser.WmsUser;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * CRM181
 * CREATOR  : 2016/09/22 Joe
 * MODIFIER : 2021/11/25 Ocean
 */
@Component("crm181")
@Scope("request")
public class CRM181 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");
	
	@Autowired
	private WmsUser wmsUser;

	//理專未完成今日聯繫名單總數查詢
	public void count(Object body, IPrimitiveMap header) throws JBranchException {

		CRM181OutputVO return_VO = new CRM181OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT COUNT(*) AS COUNT ");
		sql.append("FROM TBCAM_SFA_LEADS  ");
		sql.append("WHERE EXPECTED_DATE = TRUNC(SYSDATE) ");
		sql.append("AND LEAD_STATUS < '03' ");
		sql.append("AND BRANCH_ID IN (:branch)  ");
		
		queryCondition.setObject("branch", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		queryCondition.setQueryString(ObjectUtils.toString(sql));
		return_VO.setDataCount(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(return_VO);
	}

	public void getMustDoListCnt(Object body, IPrimitiveMap header) throws Exception {
		
		initUUID();

		CRM181InputVO inputVO = (CRM181InputVO) body;
		CRM181OutputVO return_VO = new CRM181OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("       DISPLAY_NO, ");
		sql.append("       DISPLAY_DAY, ");
		sql.append("       PRIVILEGEID, ");
		sql.append("       ROLE_LINK_YN, ");
		sql.append("       EFFDATE, ");
		sql.append("       RPT_NAME, ");
		sql.append("       FRQ_TYPE, ");
		sql.append("       FRQ_MWD, ");
		sql.append("       RPT_PROG_URL, ");
		sql.append("       CALL_FUNC_NAME, ");
		sql.append("       PASS_PARAMS, ");
		sql.append("       SHOW_YN, ");
		sql.append("       SEQ ");
		sql.append("FROM TBCRM_WKPG_BY_EMPS ");
		sql.append("WHERE E_EMP_ID = :emp_id ");
		sql.append("AND E_ROLE_ID = :roleID ");
		sql.append("AND E_PRIVILEGEID = :privilegeID ");
		
		switch (getUserVariable(FubonSystemVariableConsts.LOGINBRH).toString()) {
			case "000":
				break;
			default:
				sql.append("AND E_BRANCH_NBR = :branch_nbr ");
				queryCondition.setObject("branch_nbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH).toString());
				break;
		}
		
		sql.append("AND SHOW_YN = 'Y' ");
		sql.append("ORDER BY DISPLAY_NO  ");

		queryCondition.setObject("privilegeID", inputVO.getPriId());
		queryCondition.setObject("emp_id", inputVO.getEmpId());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		//#2291 網行銀高齡交易季報 PCK是每月 但顯示每季
		if(null != list && list.size() > 0) {
			for(Map map : list) {
				if("網行銀高齡交易季報".equals(map.get("RPT_NAME"))) {
					map.put("FRQ_TYPE", "S");
					break;
				}
			}
		}
		
		return_VO.setResultList(list);
		

		
		// 報表設定改至改至JAVA 批次 BTCRM181 : #0001930_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_必辦工作清單
				
		// 其他通知，改至ORACLE PABTH_BTCRM181 === start : #0001930_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_必辦工作清單
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT E_SEQ, ");
		sql.append("       E_EMP_ID, ");
		sql.append("       E_ROLE_ID, ");
		sql.append("       E_PRIVILEGEID, ");
		sql.append("       E_REGION_CENTER_ID, ");
		sql.append("       E_BRANCH_AREA_ID, ");
		sql.append("       E_BRANCH_NBR, ");
		sql.append("       CAM_IVG_PLAN_CONTENT_CNT, ");
		sql.append("       SQM_CSM_IMPROVE_MAST_CNT, ");
		sql.append("       SQM_RSA_MAST_CNT, ");
//		sql.append("       MGM_CNT, ");
//		sql.append("       CRM_CUST_COMPLAIN_CNT, ");
		sql.append("       CRM_CUST_ASSET_CNT, ");
		sql.append("       TRUNC(PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', -1)) LAST_DATE ");
		sql.append("FROM TBCRM_WKPG_CNT_BY_EMPS ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND E_EMP_ID = :emp_id ");
		sql.append("AND E_ROLE_ID = :role_id ");
		
		queryCondition.setObject("emp_id", inputVO.getEmpId());
		queryCondition.setObject("role_id", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> cntList = dam.exeQuery(queryCondition);
		
		if (cntList.size() > 0) {
			return_VO.setCus130Count(cntList.get(0).get("CAM_IVG_PLAN_CONTENT_CNT").toString());	// 回報計畫
			return_VO.setSqm120Count(cntList.get(0).get("SQM_CSM_IMPROVE_MAST_CNT").toString());	// 客戶滿意度報告回覆
			return_VO.setSqm320Count(cntList.get(0).get("SQM_RSA_MAST_CNT").toString());			// 客戶服務定期查核
			return_VO.setCrm8502Count(cntList.get(0).get("CRM_CUST_ASSET_CNT").toString());				
		}
		// 其他通知，改至ORACLE PABTH_BTCRM181 === end
		
		//待覆核總數-僅給予覆核權限者執行
		if (ObjectUtils.equals(inputVO.getPri_type(), "show")) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql_0 = new StringBuffer();
			sql_0.append("SELECT COUNT(REMIND_MSG) AS REVIEW_COUNT ");
			sql_0.append("FROM (SELECT * FROM table(FN_GET_REVIEW_LIST('").append(ws.getUser().getUserID()).append("', '").append(getUserVariable(FubonSystemVariableConsts.LOGINROLE)).append("', '").append(getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST)).append("')))");
			queryCondition.setQueryString(sql_0.toString());
			List<Map<String, Object>> list_0 = dam.exeQuery(queryCondition);
			return_VO.setReviewCount(ObjectUtils.toString(list_0.get(0).get("REVIEW_COUNT")));
		}

		this.sendRtnObject(return_VO);
	}

	public void jumpToRptPage(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM181InputVO inputVO = (CRM181InputVO) body;
		dam = this.getDataAccessManager();

		if (StringUtils.isBlank(inputVO.getSeqM_No())) {
			TBCRM_WKPG_MD_MASTVO vo1 = new TBCRM_WKPG_MD_MASTVO();
			Timestamp currentTM = new Timestamp(System.currentTimeMillis());
			BigDecimal seqno = new BigDecimal(getSEQ());

			vo1.setSEQ(seqno.toString());
			vo1.setPRIVILEGEID(inputVO.getPriId());
			
			if ("Y".equals(inputVO.getRole_link_yn())) {
				vo1.setEMP_ID(getUserVariable(FubonSystemVariableConsts.LOGINBRH).toString());
			} else {
				vo1.setEMP_ID(inputVO.getEmpId());
				vo1.setCLICK_YN("Y");
			}
			
			vo1.setROLE_LINK_YN(inputVO.getRole_link_yn());
			vo1.setFRQ_TYPE(inputVO.getFrq_type());
			vo1.setFRQ_MWD(inputVO.getFrq_mwd());
			vo1.setDISPLAY_NO(inputVO.getDisplay_no());
			vo1.setCLICK_DATE(currentTM);
			vo1.setRPT_NAME(inputVO.getRpt_name());
			vo1.setRPT_PROG_URL(inputVO.getRpt_prog_url());
			vo1.setCALL_FUNC_NAME(inputVO.getCall_func_name());
			vo1.setPASS_PARAMS(inputVO.getPass_params());
			vo1.setFRQ_YEAR(ObjectUtils.toString(new SimpleDateFormat("yyyy").format(new Date())));
			vo1.setDISPLAY_DAY(inputVO.getDisplay_day());
			vo1.setCreator(inputVO.getEmpId());
			vo1.setCreatetime(currentTM);
			vo1.setModifier(inputVO.getEmpId());
			vo1.setLastupdate(currentTM);
			
			dam.create(vo1);
		} else {
			Timestamp currentTM = new Timestamp(System.currentTimeMillis());
			TBCRM_WKPG_MD_MASTVO vo = new TBCRM_WKPG_MD_MASTVO();
			vo = (TBCRM_WKPG_MD_MASTVO) dam.findByPKey(TBCRM_WKPG_MD_MASTVO.TABLE_UID, inputVO.getSeqM_No());
			if (vo != null) {
				if (!"PMS424".equals(inputVO.getRpt_prog_url()) && !"PMS424U".equals(inputVO.getRpt_prog_url())) {
					vo.setCLICK_YN("Y");
				}
				vo.setCLICK_DATE(currentTM);
				dam.update(vo);
			}
		}
		
		this.sendRtnObject(null);
	}

	// 產生seq No
	private String getSEQ() throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCRM_WKPG_MD_MAST.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	// 取得近三日國外匯入款入帳清單
	public void getFetlirW(Object body, IPrimitiveMap header) throws JBranchException {
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); 	//業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); 	//營運區
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);//個金主管
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);	//UHRM科/處主管
		
		CRM181OutputVO outputVO = new CRM181OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT F.BUSINESS_NO, ");
		sb.append("       TO_DATE(F.REL_DATE, 'yyyyMMdd') AS REL_DATE, ");
		sb.append("       C.CUST_ID, ");
		sb.append("       C.BRA_NBR AS BRANCH_NBR, ");
		sb.append("       F.ACCT_NO, ");
		sb.append("       F.PAY_CURR, ");
		sb.append("       F.PAY_AMT, ");
		sb.append("       F.DEBT_ACCT1, ");
		sb.append("       F.CURR_ACCT1, ");
		sb.append("       F.COST_AMT1, ");
		sb.append("       F.DEBT_ACCT2, ");
		sb.append("       F.CURR_ACCT2, ");
		sb.append("       F.COST_AMT2, ");
		sb.append("       C.CUST_NAME, ");
		sb.append("       C.AO_CODE ");
		sb.append("FROM TBCRM_PY2011O F ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST C ON F.IDNO = C.CUST_ID ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND F.REL_DATE >= TO_CHAR(SYSDATE-3, 'yyyyMMdd') ");
		sb.append("AND F.REL_DATE <= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
		
		if (fcMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
			sb.append("AND C.AO_CODE IN (:aoList) ");
			queryCondition.setObject("aoList", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
		} else if (armgrMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE)) || 
				   mbrmgrMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE)) || 
				   bmmgrMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
			sb.append("AND C.BRA_NBR IN (:branchList) ");
			queryCondition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else if (uhrmmgrMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
			sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO T WHERE T.UHRM_CODE = C.AO_CODE) ");
		}
		
		sb.append("ORDER BY F.REL_DATE DESC ");

		queryCondition.setQueryString(sb.toString());
		
		if (psopMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
			outputVO.setFetilrListCount(0);
			outputVO.setFetilrListW(null);
		} else {
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			outputVO.setFetilrListCount(list.size());
			outputVO.setFetilrListW(list);
		}
				
		this.sendRtnObject(outputVO);
	}

	/***
	 * 2022年調換換手系統管控需求(未輪調)
	 * 分行主管異常通報為"正常"，但電子/實體回函有異常，於首頁提醒
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void get2022RotationBRMsg(Object body, IPrimitiveMap header) throws JBranchException {		
		
		CRM181OutputVO outputVO = new CRM181OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT A.CUST_ID ");
		sb.append(" FROM TBPMS_ROTATION_MAIN A ");
		sb.append(" INNER JOIN TBCRM_CUST_MAST B ON B.CUST_ID = A.CUST_ID ");
		sb.append(" WHERE B.BRA_NBR IN (:branchList) "); //轄下客戶
		sb.append(" AND A.STATEMENT_SEND_TYPE IN ('1', '2') "); //電子/實體函證
		sb.append(" AND A.PROCESS_STATUS NOT IN ('4', '5', '6') "); //尚未處裡完成的資料
		sb.append(" AND A.REC_DATE < A.RECEIVE_DATE "); //錄音日期<回函日期
		sb.append(" AND A.BRN_MGM_YN = 'Y' AND (A.RTN_STATUS_AST = 'N' OR A.RTN_STATUS_NP = 'Y') "); //分行主管回覆正常，但回函為:帳務不符或有不當情事
		
		queryCondition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String msg = "";
		if(CollectionUtils.isNotEmpty(list)) {
			for(Map<String, Object> map: list) {
				msg = msg + (StringUtils.isBlank(msg) ? "" : ", ") + map.get("CUST_ID").toString();
			}
		}
		
		outputVO.setPms429Msg(msg);
		
		this.sendRtnObject(outputVO);
	}
	
	//取得未具證配息通知資料(境外私募基金)
	public void getOvsPriDividend(Object body, IPrimitiveMap header) throws JBranchException {
		CRM181OutputVO outputVO = new CRM181OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT A.*, B.FUND_CNAME_A AS PRD_NAME, B.FUND_CNAME_S AS PRD_NAME_S, C.CUST_NAME, B.FUND_CNAME AS PRD_CNAME ");
		sb.append(" FROM TBCRM_FUND_OVS_PRI_DIV_NOTI A ");
		sb.append(" INNER JOIN TBPRD_FUND B ON B.PRD_ID = A.PRD_ID ");
		sb.append(" LEFT JOIN TBCRM_CUST_MAST C ON C.CUST_ID = A.CUST_ID ");
		sb.append(" WHERE TRUNC(A.TXN_DATE) BETWEEN TRUNC(SYSDATE - 30) AND TRUNC(SYSDATE) "); //最近30日內資料
		sb.append(" AND NVL(A.READ_YN, 'N') = 'N' "); //理專未讀
		sb.append(" AND A.EMP_ID = :empId "); //登入理專轄下客戶
		
		queryCondition.setObject("empId", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);		
		outputVO.setOvsPriDivList(list);
		
		this.sendRtnObject(outputVO);
	}

	//將未具證配息通知設為已讀
	public void setOvsPriDividendRead(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
		CRM181OutputVO outputVO = new CRM181OutputVO();
		CRM181InputVO inputVO = (CRM181InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("UPDATE TBCRM_FUND_OVS_PRI_DIV_NOTI ");
		sb.append("SET READ_YN = 'Y', LASTUPDATE = SYSDATE, MODIFIER = :loginId ");
		sb.append("WHERE CERT_NBR = :certNbr AND PRD_ID = :prdId AND TRUNC(TXN_DATE) = TRUNC(:txnDate) ");
		
		queryCondition.setObject("certNbr", inputVO.getCERT_NBR());
		queryCondition.setObject("prdId", inputVO.getPRD_ID());
		queryCondition.setObject("txnDate", new Timestamp(inputVO.getTXN_DATE().getTime()));
		queryCondition.setObject("loginId", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(outputVO);
	}
}