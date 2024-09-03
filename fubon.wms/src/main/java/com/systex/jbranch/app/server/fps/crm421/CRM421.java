package com.systex.jbranch.app.server.fps.crm421;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPLY_PERIODVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPLY_SINGLEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPROVAL_HISTORYVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_ETFVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_FUNDVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_STOCKVO;
import com.systex.jbranch.app.server.fps.crm431.CRM431;
import com.systex.jbranch.app.server.fps.crm451.CRM451;
import com.systex.jbranch.app.server.fps.prd110.PRD110;
import com.systex.jbranch.app.server.fps.prd110.PRD110InputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110OutputVO;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustAcctDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot709.PeriodFeeRateVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709;
import com.systex.jbranch.app.server.fps.sot709.SOT709InputVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709OutputVO;
import com.systex.jbranch.app.server.fps.sot709.SingleFeeRateVO;
import com.systex.jbranch.app.server.fps.sot710.SOT710;
import com.systex.jbranch.app.server.fps.sot710.SOT710InputVO;
import com.systex.jbranch.app.server.fps.sot710.SOT710OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/09/29
 * 
 */
@Component("crm421")
@Scope("request")
public class CRM421 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataAccessManager dam = null;

	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfYYYMMDD = new SimpleDateFormat("yyyMMdd");
	SimpleDateFormat sdfYYYY_MM_DD = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat sdfHHMMSS = new SimpleDateFormat("HHmmss");

	private static final String APPLY_STATUS_REVIEW = "1"; //待覆核
	private static final String APPLY_STATUS_AUTH = "2"; //已授權
	private static final String APPLY_STATUS_TERMINATE = "3"; //已終止
	private static final String APPLY_STATUS_TERMINATE_REVIEW = "4"; //終止待覆核
	private static final String APPLY_STATUS_REJECT = "9"; //已退回
	
	public boolean chkIsUHRM() throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> FCMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> uhrmMgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);
		
		if (uhrmMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
			return true;
		} else if (FCMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
			return false;
		} else if (uhrmMgrMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 取得客戶所屬分行
	 * 
	 * 2019/02/12 modify by ocean 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void getCustBranch (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT CM.BRA_NBR ");
		sql.append("FROM TBCRM_CUST_MAST CM ");
		sql.append("WHERE CM.CUST_ID = :custId ");
		sql.append("AND ( ");
		sql.append("     CM.AO_CODE IN (:loginAoCode) ");
		sql.append("  OR CM.UEMP_ID = :loginUser ");
		
		// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
		sql.append("  OR EXISTS ( ");
		sql.append("    SELECT AO.AO_CODE, DT_A.EMP_ID, DT_A.BRANCH_NBR ");
		sql.append("    FROM TBORG_DIAMOND_TEAM DT_A ");
		sql.append("    INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
		sql.append("    WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :loginUser) ");
		sql.append("    AND CM.AO_CODE = AO.AO_CODE ");
		sql.append("  ) ");
		
		sql.append(") ");
		
		queryCondition.setObject("custId", inputVO.getCust_id());
		queryCondition.setObject("loginAoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
		queryCondition.setObject("loginUser", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			outputVO.setBranchNbr((String) list.get(0).get("BRA_NBR"));
		} else {
			throw new APException("ehl_02_CRM421_008");
		}

		sendRtnObject(outputVO);
	}

	//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
	public void mobileInquire (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		CRM421InputVO inputVO = (CRM421InputVO) body;

		if (getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN) != null) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();

			String loginToken = getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN).toString();

			if ("mobile".equals(loginToken)) {
				sql.append("SELECT CUST_ID FROM VWCRM_MAO_QRY_CUST WHERE AO_CODE in (:loginAoCode) ");
				sql.append("AND CUST_ID = :cust_id ");
				queryCondition.setObject("loginAoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
				queryCondition.setObject("cust_id", inputVO.getCust_id());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> mobileList = dam.exeQuery(queryCondition);

				if (mobileList.size() < 1) {
					throw new JBranchException("ehl_01_crm110_001"); //該客戶未事先申請，故無法查詢。
				}
			}
		}
		sendRtnObject(null);
	}

	/*
	 * 查詢
	 * 
	 * 2016-10-11 add by walala
	 * 2016-12-23 modify by ocean : add 電文
	 * 
	 */
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		//沒有輸入客戶ID，由DB取得資料；否則由電文取得
		if (StringUtils.isBlank(inputVO.getCust_id())) {
			//單次議價
			sql.append("SELECT A.TRUST_CURR_TYPE, A.PURCHASE_AMT, A.ENTRUST_AMT,A.ENTRUST_UNIT,A.APPLY_SEQ, A.CUST_ID, C.CUST_NAME, '2' AS APPLY_CAT, (CASE WHEN A.APPLY_TYPE IN ('1','2','3') THEN '1' ELSE '2' END) AS PROD_CAT, A.APPLY_TYPE, A.DISCOUNT_TYPE, ");
			sql.append("NULL AS BRG_BEGIN_DATE, NULL AS BRG_END_DATE, A.BRG_REASON, A.PROD_ID, A.PROD_NAME, 'database' AS SOURCE, ");
			//		    sql.append("CASE WHEN A.DISCOUNT_TYPE = '1' THEN A.FEE_RATE||'' ELSE A.FEE_DISCOUNT||'' END AS DISCOUNT, ");
			sql.append("A.FEE_DISCOUNT AS DISCOUNT, A.FEE_RATE, INFO.AO_CODE, ");
			sql.append("A.APPLY_DATE, A.APPLY_STATUS, A.AUTH_STATUS, (SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) FROM TBSYSPARAMETER WHERE PARAM_TYPE = '").append(chkIsUHRM() ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV").append("'||A.HIGHEST_AUTH_LV) AS HIGHEST_AUTH_LV_NAME, A.HIGHEST_AUTH_LV, ");
			sql.append("NULL AS DMT_STOCK, NULL AS DMT_BOND, NULL AS DMT_BALANCED, NULL AS FRN_STOCK, NULL AS FRN_BOND , NULL AS FRN_BALANCED, NULL AS BUY_HK_MRK, NULL AS BUY_US_MRK, NULL AS BUY_UK_MRK, NULL AS SELL_HK_MRK, NULL AS SELL_US_MRK, NULL AS SELL_UK_MRK,  NULL AS BUY_JP_MRK,  NULL AS SELL_JP_MRK ");
			sql.append("FROM TBCRM_BRG_APPLY_SINGLE A, VWCRM_CUST_INFO C, VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
			sql.append("WHERE A.CUST_ID = C.CUST_ID AND A.CREATOR = INFO.EMP_ID ");
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append("AND INFO.AO_CODE IN (:loginAoCode) ");
			} else {
				sql.append("AND C.BRA_NBR IN (:loginBraNbr) ");
			}

			if (StringUtils.isNotBlank(inputVO.getApply_status())) {
				if (StringUtils.equals(APPLY_STATUS_REVIEW, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS IN ('1', '4') ");
				} else {
					sql.append("AND A.APPLY_STATUS = :apply_status ");
				}
			}

			if (!StringUtils.isBlank(inputVO.getCon_degree()))
				sql.append("AND C.CON_DEGREE = :con_degree ");

			if (inputVO.getApply_sDate() != null)
				sql.append("AND trunc(A.APPLY_DATE) >= trunc(:apply_sDate) ");

			if (inputVO.getApply_eDate() != null)
				sql.append("AND trunc(A.APPLY_DATE) <= trunc(:apply_eDate) ");

			//			//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
			//			if(getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN) != null){
			//				String loginToken = getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN).toString();
			//				if("mobile".equals(loginToken)){
			//					sql.append("AND A.CUST_ID in (SELECT CUST_ID FROM VWCRM_MAO_QRY_CUST WHERE AO_CODE in (:loginAoCode)) ");
			//					queryCondition.setObject("loginAoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			//				}
			//			}

			sql.append("UNION ");

			//期間議價之基金
			sql.append("SELECT NULL AS TRUST_CURR_TYPE, NULL AS PURCHASE_AMT, NULL AS ENTRUST_AMT,NULL AS ENTRUST_UNIT, A.APPLY_SEQ, A.CUST_ID, C.CUST_NAME, '1' AS APPLY_CAT, A.APPLY_TYPE AS PROD_CAT, A.APPLY_TYPE, '' AS DISCOUNT_TYPE, ");
			sql.append("A.BRG_BEGIN_DATE, A.BRG_END_DATE, A.BRG_REASON, NULL AS PROD_ID, NULL AS PROD_NAME, 'database' AS SOURCE, ");
			sql.append("NULL AS DISCOUNT, NULL AS FEE_RATE, INFO.AO_CODE, ");
			sql.append("A.APPLY_DATE, A.APPLY_STATUS, A.AUTH_STATUS, (SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) FROM TBSYSPARAMETER WHERE PARAM_TYPE = '").append(chkIsUHRM() ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV").append("'||A.HIGHEST_AUTH_LV) AS HIGHEST_AUTH_LV_NAME, A.HIGHEST_AUTH_LV, ");
			sql.append("A.DMT_STOCK, A.DMT_BOND, A.DMT_BALANCED, A.FRN_STOCK, A.FRN_BOND, A.FRN_BALANCED, ");
			sql.append(" A.BUY_HK_MRK, A.BUY_US_MRK, A.BUY_UK_MRK, A.SELL_HK_MRK, A.SELL_US_MRK, A.SELL_UK_MRK, A.BUY_JP_MRK, A.SELL_JP_MRK ");
			sql.append("FROM TBCRM_BRG_APPLY_PERIOD A, VWCRM_CUST_INFO C, VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
			sql.append("WHERE A.CUST_ID = C.CUST_ID AND A.CREATOR = INFO.EMP_ID ");
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append("AND INFO.AO_CODE IN (:loginAoCode) ");
			} else {
				sql.append("AND C.BRA_NBR IN (:loginBraNbr) ");
			}

			if (StringUtils.isNotEmpty(inputVO.getApply_status())) {
				if (StringUtils.equals(APPLY_STATUS_REVIEW, inputVO.getApply_status())) {
					sql.append("AND A.APPLY_STATUS IN ('1', '4') ");
				} else {
					sql.append("AND A.APPLY_STATUS = :apply_status ");
				}
			}

			if (!StringUtils.isBlank(inputVO.getCon_degree()))
				sql.append("AND C.CON_DEGREE = :con_degree ");

			if (inputVO.getApply_sDate() != null)
				sql.append("AND trunc(A.APPLY_DATE) >= trunc(:apply_sDate) ");

			if (inputVO.getApply_eDate() != null)
				sql.append("AND trunc(A.APPLY_DATE) <= trunc(:apply_eDate) ");

			//			//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
			//			if(getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN) != null){
			//				String loginToken = getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN).toString();
			//				if("mobile".equals(loginToken)){
			//					sql.append("AND A.CUST_ID in (SELECT CUST_ID FROM VWCRM_MAO_QRY_CUST WHERE AO_CODE in (:loginAoCode)) ");
			//					queryCondition.setObject("loginAoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			//				}
			//			}

			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				queryCondition.setObject("loginAoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			} else {
				queryCondition.setObject("loginBraNbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}

			if (!StringUtils.isBlank(inputVO.getCust_id()))
				queryCondition.setObject("cust_id", inputVO.getCust_id());

			if (StringUtils.isNotEmpty(inputVO.getApply_status()) && !StringUtils.equals(APPLY_STATUS_REVIEW, inputVO.getApply_status()))
				queryCondition.setObject("apply_status", inputVO.getApply_status());
			if (!StringUtils.isBlank(inputVO.getCon_degree()))
				queryCondition.setObject("con_degree", inputVO.getCon_degree());
			if (inputVO.getApply_sDate() != null)
				queryCondition.setObject("apply_sDate", new Timestamp(inputVO.getApply_sDate().getTime()));
			if (inputVO.getApply_eDate() != null)
				queryCondition.setObject("apply_eDate", new Timestamp(inputVO.getApply_eDate().getTime()));

			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> queryList = StringUtils.isNotBlank(inputVO.getCust_id()) ? new ArrayList<Map<String, Object>>() : dam.exeQuery(queryCondition);

			if (queryList.size() > 0 && !StringUtils.equals(inputVO.getApply_status(), APPLY_STATUS_REJECT)) {
				for (Map<String, Object> map : queryList) {
					map.put("CURRENT_STATUS", getCurrentStatus(map.get("HIGHEST_AUTH_LV").toString(), map.get("AUTH_STATUS").toString()));
					list.add(map);
				}
			} else {
				list.addAll(queryList);
			}
		} else {

			//			//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
			//			if(getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN) != null){
			//				String loginToken = getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN).toString();
			//				if("mobile".equals(loginToken)){
			//					sql.append("SELECT CUST_ID FROM VWCRM_MAO_QRY_CUST WHERE AO_CODE in (:loginAoCode) ");
			//					sql.append("AND CUST_ID = :cust_id ");
			//					queryCondition.setObject("loginAoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			//					queryCondition.setObject("cust_id", inputVO.getCust_id());
			//					queryCondition.setQueryString(sql.toString());
			//					List<Map<String,Object>> mobileList = dam.exeQuery(queryCondition);
			//					if(mobileList.size() < 1){
			//						throw new JBranchException("ehl_01_crm110_001");	//該客戶未事先申請，故無法查詢。
			//					}
			//				}
			//			}

			// === 電文 START
			if (StringUtils.isNotBlank(inputVO.getCust_id())) {
				// === 基金電文start ===
				SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
				SOT709InputVO inputVO_709 = new SOT709InputVO();

				inputVO_709.setCustId(inputVO.getCust_id());
				inputVO_709.setType("3");

				if (inputVO.getApply_sDate() != null)
					inputVO_709.setStartDate(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(inputVO.getApply_sDate())));
				if (inputVO.getApply_eDate() != null)
					inputVO_709.setEndDate(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(inputVO.getApply_eDate())));

				TBCRM_CUST_MASTVO mvo = new TBCRM_CUST_MASTVO();
				mvo = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCust_id());

				getNFEE086List(dam, list, sot709, inputVO_709, inputVO, mvo); // NFEE086 單次 - 定期(不)定額議價查詢
				getVN085NList(dam, list, sot709, inputVO_709, inputVO, mvo); // VN085N 單次 - 單筆額議價查詢
				getNFEE002List(dam, list, sot709, inputVO_709, inputVO, mvo); // NFEE002 期間 - 基金議價查詢
				// === 基金電文end ===

				// === ETF電文start ===
				SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
				SOT710InputVO inputVO_710 = new SOT710InputVO();

				inputVO_710.setCustID(inputVO.getCust_id());
				inputVO_710.setEmpID(ws.getUser().getUserID());

				if (inputVO.getApply_sDate() != null)
					inputVO_710.setStartDate(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(inputVO.getApply_sDate())));
				if (inputVO.getApply_eDate() != null)
					inputVO_710.setEndDate(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(inputVO.getApply_eDate())));

				getNRBRVC4List(dam, list, sot710, inputVO_710, inputVO, mvo); // NRBRVC4 單次 - ETF議價查詢
				getNRBRVC3List(dam, list, sot710, inputVO_710, inputVO, mvo); // NRBRVC3 期間 - ETF議價查詢
				// === ETF電文end ===
			}
			// === 電文 END
		}
		outputVO.setResultList(list);

		sendRtnObject(outputVO);
	}

	/*
	 * NFEE086 單筆 - 定期(不)定額議價查詢
	 * 
	 * 2016-12-22 add by ocean
	 * 
	 */
	public List<Map<String, Object>> getNFEE086List (DataAccessManager dam, List<Map<String, Object>> list, SOT709 sot709, SOT709InputVO inputVO_709, CRM421InputVO inputVO, TBCRM_CUST_MASTVO mvo) throws Exception {

		SOT709OutputVO nfee086OutputVO = new SOT709OutputVO();
		nfee086OutputVO = sot709.getSingleRegFeeRate4Bar(inputVO_709);
		List<com.systex.jbranch.app.server.fps.sot709.SingleFeeRateVO> nfee086CList = nfee086OutputVO.getSingleFeeRateList();

		for (SingleFeeRateVO vo : nfee086CList) {
			TBPRD_FUNDVO fvo = new TBPRD_FUNDVO();
			fvo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, vo.getFundNo().trim());

			TBCRM_BRG_APPLY_SINGLEVO svo = new TBCRM_BRG_APPLY_SINGLEVO();
			svo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, vo.getBeneCode());

			Map<String, Object> tempDTL = new HashMap<String, Object>();
			tempDTL.put("APPLY_TYPE", null == svo ? "2" : svo.getAPPLY_TYPE()); //1：基金單筆申購2：基金定期(不)定額申購4：海外ETF/股票申購
			tempDTL.put("SOURCE", "ESB");
			tempDTL.put("MODIFY_FLAG", null == svo ? true : false);
			tempDTL.put("BRG_REASON", null == svo ? null : svo.getBRG_REASON());
			tempDTL.put("HIGHEST_AUTH_LV", null == svo ? null : svo.getHIGHEST_AUTH_LV());
			tempDTL.put("HIGHEST_AUTH_LV_NAME", getHightLevelName(null == svo ? null : svo.getHIGHEST_AUTH_LV()));

			tempDTL.put("INVEST_AMT", vo.getInvestAmt()); //申購金額
			tempDTL.put("INVEST_CUR", vo.getInvestCur()); //申購幣別
			tempDTL.put("TRUST_CURR_TYPE", null == svo ? null : svo.getTRUST_CURR_TYPE()); //信託幣別
			tempDTL.put("TRUST_BRANCH", vo.getTrustBranch().trim()); //分行

			tempDTL.put("APPLY_SEQ", vo.getBeneCode().trim());
			tempDTL.put("CUST_ID", inputVO.getCust_id());
			tempDTL.put("CUST_NAME", mvo.getCUST_NAME());
			tempDTL.put("APPLY_CAT", "2"); //1：期間議價2：單次議價
			tempDTL.put("DISCOUNT_TYPE", "2"); //1:費率2:折扣
			tempDTL.put("PROD_CAT", "1"); //1：基金2：ETF
			tempDTL.put("PROD_ID", vo.getFundNo().trim());
			tempDTL.put("PROD_NAME", fvo.getFUND_CNAME()); //商品名稱

			BigDecimal discount = null;
			if (vo.getGroupCode() != null) {
				discount = new EsbUtil().decimalPoint(vo.getGroupCode(), 1);
			}
			tempDTL.put("DISCOUNT", (null == vo.getGroupCode()) ? null : discount); //折數
			//			tempDTL.put("APPLY_DATE", sdfYYYY_MM_DD.format(sdfYYYYMMDD.parse(String.valueOf(vo.getBeneDate().add(new BigDecimal("19110000")))))); //BeneDate: 交易日期 / CrtTime: 鍵機時間			
			tempDTL.put("APPLY_DATE", sdfYYYYMMDD.parse(vo.getBeneDate().add(new BigDecimal("19110000")).toString())); //BeneDate: 交易日期 / CrtTime: 鍵機時間			
			//TODO		tempDTL.put("APPLY_STATUS", (sdfYYYYMMDD.parse("00000000").equals(vo.getAuthDate()) && sdfHHMMSS.parse("000000").equals(vo.getAuthTime()) ? (null == svo ? "1" : svo.getAPPLY_STATUS()) : "2")); //2:已授權null == svo ? "2" : svo.getAPPLY_STATUS()

			if (null == svo) {
				if (StringUtils.isNotBlank(vo.getAppMgr())) { //有授權主管表"已授權"
					tempDTL.put("APPLY_STATUS", "2"); //2:已授權
				} else {
					tempDTL.put("APPLY_STATUS", "1"); //1:待覆核
				}
			} else {
				tempDTL.put("APPLY_STATUS", svo.getAPPLY_STATUS());
			}

			tempDTL.put("AUTH_STATUS", null == svo ? null : svo.getAUTH_STATUS()); //授權狀態

			if (svo != null && !StringUtils.equals("0", svo.getAPPLY_STATUS()))
				tempDTL.put("CURRENT_STATUS", getCurrentStatus(null == svo ? null : svo.getHIGHEST_AUTH_LV(), null == svo ? null : (String) svo.getAUTH_STATUS()));
			else
				tempDTL.put("CURRENT_STATUS", "");

			list.add(tempDTL);
		}

		return list;
	}

	/*
	 * VN085N 單筆 - 定期定額議價查詢
	 * 
	 * 2016-12-22 add by ocean
	 * 
	 */
	public List<Map<String, Object>> getVN085NList (DataAccessManager dam, List<Map<String, Object>> list, SOT709 sot709, SOT709InputVO inputVO_709, CRM421InputVO inputVO, TBCRM_CUST_MASTVO mvo) throws Exception {

		SOT709OutputVO vn085nOutputVO = new SOT709OutputVO();
		vn085nOutputVO = sot709.getSingleFeeRate4Bar(inputVO_709);
		List<com.systex.jbranch.app.server.fps.sot709.SingleFeeRateVO> vn085nCList = vn085nOutputVO.getSingleFeeRateList();

		for (SingleFeeRateVO vo : vn085nCList) {
			TBPRD_FUNDVO fvo = new TBPRD_FUNDVO();
			fvo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, vo.getFundNo().trim());

			TBCRM_BRG_APPLY_SINGLEVO svo = new TBCRM_BRG_APPLY_SINGLEVO();
			svo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, vo.getBeneCode());

			Map<String, Object> tempDTL = new HashMap<String, Object>();
			tempDTL.put("APPLY_TYPE", null == svo ? "1" : svo.getAPPLY_TYPE()); //1：基金單筆申購2：基金定期(不)定額申購4：海外ETF/股票申購
			tempDTL.put("SOURCE", "ESB");
			tempDTL.put("MODIFY_FLAG", null == svo ? true : false);
			tempDTL.put("BRG_REASON", null == svo ? null : svo.getBRG_REASON());
			tempDTL.put("HIGHEST_AUTH_LV", null == svo ? null : svo.getHIGHEST_AUTH_LV());
			tempDTL.put("HIGHEST_AUTH_LV_NAME", getHightLevelName(null == svo ? null : svo.getHIGHEST_AUTH_LV()));

			tempDTL.put("INVEST_AMT", vo.getInvestAmt()); //申購金額
			tempDTL.put("INVEST_CUR", vo.getInvestCur()); //申購幣別
			tempDTL.put("TRUST_CURR_TYPE", null == svo ? null : svo.getTRUST_CURR_TYPE()); //信託幣別
			tempDTL.put("TRUST_BRANCH", vo.getTrustBranch().trim()); //分行

			tempDTL.put("APPLY_SEQ", vo.getBeneCode().trim());
			tempDTL.put("CUST_ID", inputVO.getCust_id());
			tempDTL.put("CUST_NAME", mvo.getCUST_NAME());
			tempDTL.put("APPLY_CAT", "2"); //1：期間議價2：單次議價
			//			tempDTL.put("DISCOUNT_TYPE", vo.getBrgType()); //1:費率2:折扣

			String discount_type = (null == svo ? "1" : svo.getDISCOUNT_TYPE().toString());

			tempDTL.put("DISCOUNT_TYPE", discount_type); //1:費率2:折扣
			tempDTL.put("PROD_CAT", "1"); //1：基金2：ETF
			tempDTL.put("PROD_ID", vo.getFundNo().trim());
			tempDTL.put("PROD_NAME", fvo.getFUND_CNAME()); //商品名稱
			if ("1".equals(discount_type)) {
				tempDTL.put("FEE_RATE", vo.getFeeRate()); //手續費率				
			} else {
				tempDTL.put("DISCOUNT", svo.getFEE_DISCOUNT()); //手續折扣
			}
			//			tempDTL.put("APPLY_DATE", sdfYYYY_MM_DD.format(sdfYYYYMMDD.parse(String.valueOf(vo.getBeneDate().add(new BigDecimal("19110000")))))); //BeneDate: 交易日期 / CrtTime: 鍵機時間
			tempDTL.put("APPLY_DATE", sdfYYYYMMDD.parse(vo.getBeneDate().add(new BigDecimal("19110000")).toString()));
			//TODO		tempDTL.put("APPLY_STATUS", (sdfYYYYMMDD.parse("00000000").equals(vo.getAuthDate()) && sdfHHMMSS.parse("000000").equals(vo.getAuthTime()) ? (null == svo ? "1" : svo.getAPPLY_STATUS()) : "2")); //2:已授權null == svo ? "2" : svo.getAPPLY_STATUS()
			//			tempDTL.put("APPLY_STATUS", null == svo ? "2" : svo.getAPPLY_STATUS()); //2:已授權
			//			tempDTL.put("AUTH_STATUS", null == svo ? "2" : svo.getAUTH_STATUS()); //授權狀態

			if (null == svo) {
				if (StringUtils.isNotBlank(vo.getAppMgr())) { //有授權主管表"已授權"
					tempDTL.put("APPLY_STATUS", "2"); //2:已授權
				} else {
					tempDTL.put("APPLY_STATUS", "1"); //1:待覆核
				}
			} else {
				tempDTL.put("APPLY_STATUS", svo.getAPPLY_STATUS());
			}

			tempDTL.put("AUTH_STATUS", null == svo ? null : svo.getAUTH_STATUS()); //授權狀態

			if (svo != null && !StringUtils.equals("0", svo.getAPPLY_STATUS()))
				tempDTL.put("CURRENT_STATUS", getCurrentStatus(null == svo ? null : (String) svo.getHIGHEST_AUTH_LV(), null == svo ? null : (String) svo.getAUTH_STATUS()));
			else
				tempDTL.put("CURRENT_STATUS", "");

			list.add(tempDTL);
		}

		return list;
	}

	/*
	 * NFEE002 期間 - 基金議價查詢
	 * 
	 * 2016-12-22 add by ocean
	 * 
	 */
	public List<Map<String, Object>> getNFEE002List (DataAccessManager dam, List<Map<String, Object>> list, SOT709 sot709, SOT709InputVO inputVO_709, CRM421InputVO inputVO, TBCRM_CUST_MASTVO mvo) throws Exception {
		QueryConditionIF queryCondition;
		SOT709OutputVO nfee002OutputVO = new SOT709OutputVO();
		nfee002OutputVO = sot709.getPeriodFeeRate(inputVO_709);
		List<com.systex.jbranch.app.server.fps.sot709.PeriodFeeRateVO> nfee002CList = nfee002OutputVO.getPeriodFeeRateList();

		for (PeriodFeeRateVO vo : nfee002CList) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT APPLY_SEQ, APPLY_TYPE, BRG_REASON, APPLY_STATUS, HIGHEST_AUTH_LV, AUTH_STATUS, ");
			sb.append("DMT_STOCK, DMT_BOND, DMT_BALANCED, FRN_STOCK, FRN_BOND, FRN_BALANCED ");
			sb.append("FROM TBCRM_BRG_APPLY_PERIOD ");
			sb.append("WHERE CUST_ID = :custID ");
			sb.append("AND TRUNC(BRG_BEGIN_DATE) = TRUNC(:brgBeginDate) ");
			sb.append("AND TRUNC(BRG_END_DATE) = TRUNC(:brgEndDate) ");
			//			sb.append("AND TERMINATE_SEQ IS NOT NULL ");
			sb.append("AND APPLY_TYPE = '1' ");
			sb.append("AND APPLY_STATUS <> '9' "); //因為電文不會回傳"已退回"資料，所以過濾掉。
			if (null != vo.getTerminateDate()) {
				sb.append("AND APPLY_STATUS = '3' ");
			} else {
				sb.append("AND APPLY_STATUS <> '3' ");
				if (vo.getAuthDate() != null) {
					sb.append("AND (APPLY_STATUS = '2' OR APPLY_STATUS = '4') ");
				} else {
					sb.append("AND APPLY_STATUS <> '2' ");
				}
			}
			queryCondition.setObject("custID", inputVO.getCust_id());
			queryCondition.setObject("brgBeginDate", vo.getBrgBeginDate());
			queryCondition.setObject("brgEndDate", vo.getBrgEndDate());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> applySEQList = dam.exeQuery(queryCondition);

			Map<String, Object> tempDTL = new HashMap<String, Object>();
			tempDTL.put("APPLY_TYPE", applySEQList.size() > 0 ? applySEQList.get(0).get("APPLY_TYPE") : "1"); //1：基金2：海外ETF/股票
			tempDTL.put("SOURCE", applySEQList.size() == 0 ? "ESB" : "");
			tempDTL.put("MODIFY_FLAG", applySEQList.size() == 0 ? true : false);
			tempDTL.put("BRG_REASON", applySEQList.size() == 0 ? null : applySEQList.get(0).get("BRG_REASON"));
			tempDTL.put("HIGHEST_AUTH_LV", applySEQList.size() == 0 ? null : applySEQList.get(0).get("HIGHEST_AUTH_LV"));
			tempDTL.put("HIGHEST_AUTH_LV_NAME", getHightLevelName(applySEQList.size() == 0 ? null : applySEQList.get(0).get("HIGHEST_AUTH_LV").toString()));

			tempDTL.put("APPLY_SEQ", applySEQList.size() > 0 ? applySEQList.get(0).get("APPLY_SEQ") : null);
			tempDTL.put("CUST_ID", inputVO.getCust_id());
			tempDTL.put("CUST_NAME", mvo.getCUST_NAME());
			tempDTL.put("APPLY_CAT", "1"); //1：期間議價2：單次議價
			tempDTL.put("PROD_CAT", "1"); //1：基金2：ETF

			tempDTL.put("PROD_ID", null);
			tempDTL.put("PROD_NAME", null); //商品名稱
			tempDTL.put("DISCOUNT", null); //折數
			tempDTL.put("BRG_BEGIN_DATE", vo.getBrgBeginDate());
			tempDTL.put("BRG_END_DATE", vo.getBrgEndDate());
			tempDTL.put("APPLY_DATE", vo.getApplyDate()); //BeneDate: 交易日期 / CrtTime: 鍵機時間
			//TODO		tempDTL.put("APPLY_STATUS", (sdfYYYYMMDD.parse("00000000").equals(vo.getAuthDate()) && sdfHHMMSS.parse("000000").equals(vo.getAuthTime()) ? (applySEQList.size() == 0 ? "1" : applySEQList.get(0).get("APPLY_STATUS")) : "2")

			if (vo.getTerminateDate() != null) {
				tempDTL.put("APPLY_STATUS", "3"); //3:已終止
			} else if (vo.getAuthDate() != null) {
				if (applySEQList.size() > 0 && "4".equals(applySEQList.get(0).get("APPLY_STATUS").toString())) { //終止待覆核於電文的狀態依舊會是"已授權"，故改以資料庫狀態為準。
					tempDTL.put("APPLY_STATUS", "4"); //4:終止待覆核
				} else {
					tempDTL.put("APPLY_STATUS", "2"); //2:已授權					
				}
			} else {
				tempDTL.put("APPLY_STATUS", applySEQList.size() == 0 ? "1" : applySEQList.get(0).get("APPLY_STATUS"));
			}

			tempDTL.put("AUTH_STATUS", applySEQList.size() == 0 ? null : applySEQList.get(0).get("AUTH_STATUS")); //授權狀態

			tempDTL.put("DMT_STOCK", vo.getDmtStock());
			tempDTL.put("DMT_BOND", vo.getDmtBond());
			tempDTL.put("DMT_BALANCED", vo.getDmtBalanced());
			tempDTL.put("FRN_STOCK", vo.getFrnStock());
			tempDTL.put("FRN_BOND", vo.getFrnBond());
			tempDTL.put("FRN_BALANCED", vo.getFrnBalanced());

			//			tempDTL.put("DMT_STOCK", applySEQList.size() > 0 ? applySEQList.get(0).get("DMT_STOCK") : null);
			//			tempDTL.put("DMT_BOND", applySEQList.size() > 0 ? applySEQList.get(0).get("DMT_BOND") : null);
			//			tempDTL.put("DMT_BALANCED", applySEQList.size() > 0 ? applySEQList.get(0).get("DMT_BALANCED") : null);
			//			tempDTL.put("FRN_STOCK", applySEQList.size() > 0 ? applySEQList.get(0).get("FRN_STOCK") : null);
			//			tempDTL.put("FRN_BOND", applySEQList.size() > 0 ? applySEQList.get(0).get("FRN_BOND") : null);
			//			tempDTL.put("FRN_BALANCED", applySEQList.size() > 0 ? applySEQList.get(0).get("FRN_BALANCED") : null);
			tempDTL.put("BUY_HK_MRK", null);
			tempDTL.put("BUY_US_MRK", null);
			tempDTL.put("BUY_UK_MRK", null);
			tempDTL.put("BUY_JP_MRK", null);
			tempDTL.put("SELL_HK_MRK", null);
			tempDTL.put("SELL_US_MRK", null);
			tempDTL.put("SELL_UK_MRK", null);
			tempDTL.put("SELL_JP_MRK", null);

			if (applySEQList.size() != 0 && !StringUtils.equals("0", applySEQList.get(0).get("APPLY_STATUS").toString()))
				tempDTL.put("CURRENT_STATUS", getCurrentStatus(applySEQList.size() == 0 ? null : CharUtils.toString((Character) applySEQList.get(0).get("HIGHEST_AUTH_LV")), applySEQList.size() == 0 ? null : CharUtils.toString((Character) applySEQList.get(0).get("AUTH_STATUS"))));
			else
				tempDTL.put("CURRENT_STATUS", "");

			list.add(tempDTL);
		}

		return list;
	}

	/*
	 * NRBRVC4 單筆 - ETF單筆議價查詢
	 * 
	 * 2016-12-22 add by ocean
	 * 
	 */
	public List<Map<String, Object>> getNRBRVC4List (DataAccessManager dam, List<Map<String, Object>> list, SOT710 sot710, SOT710InputVO inputVO_710, CRM421InputVO inputVO, TBCRM_CUST_MASTVO mvo) throws Exception {

		SOT710OutputVO nrbrvc4OutputVO = new SOT710OutputVO();
		nrbrvc4OutputVO = sot710.getSingleFeeRate(inputVO_710);
		List<com.systex.jbranch.app.server.fps.sot710.SingleFeeRateVO> nrbrvc4CList = nrbrvc4OutputVO.getSingleFeeRateList();

		for (com.systex.jbranch.app.server.fps.sot710.SingleFeeRateVO vo : nrbrvc4CList) {
			TBPRD_ETFVO evo = new TBPRD_ETFVO();
			evo = (TBPRD_ETFVO) dam.findByPKey(TBPRD_ETFVO.TABLE_UID, vo.getProdId().trim());

			TBPRD_STOCKVO stvo = new TBPRD_STOCKVO();
			stvo = (TBPRD_STOCKVO) dam.findByPKey(TBPRD_STOCKVO.TABLE_UID, vo.getProdId().trim());

			TBCRM_BRG_APPLY_SINGLEVO svo = new TBCRM_BRG_APPLY_SINGLEVO();
			svo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, vo.getApplySeq());

			Map<String, Object> tempDTL = new HashMap<String, Object>();
			tempDTL.put("APPLY_TYPE", null == svo ? "4" : svo.getAPPLY_TYPE()); //1：基金單筆申購2：基金定期(不)定額申購4：海外ETF/股票申購
			tempDTL.put("SOURCE", "ESB");
			tempDTL.put("MODIFY_FLAG", null == svo ? true : false);
			tempDTL.put("BRG_REASON", null == svo ? null : svo.getBRG_REASON());
			tempDTL.put("HIGHEST_AUTH_LV", null == svo ? null : svo.getHIGHEST_AUTH_LV());
			tempDTL.put("HIGHEST_AUTH_LV_NAME", getHightLevelName(null == svo ? null : svo.getHIGHEST_AUTH_LV()));

			tempDTL.put("ENTRUST_UNIT", vo.getUnit()); //委託數量
			tempDTL.put("ENTRUST_AMT", vo.getPrice()); //委託價格
			tempDTL.put("FEE_RATE", vo.getFeeRate()); //折扣費率
			tempDTL.put("FEE_DISCOUNT", vo.getFeeDiscount()); //折扣數
			tempDTL.put("TRUST_CURR_TYPE", vo.getTrustCurrType()); //信託業務別

			tempDTL.put("APPLY_SEQ", vo.getApplySeq().trim());
			tempDTL.put("CUST_ID", inputVO.getCust_id());
			tempDTL.put("CUST_NAME", mvo.getCUST_NAME());
			tempDTL.put("APPLY_CAT", "2"); //1：期間議價2：單次議價
			tempDTL.put("DISCOUNT_TYPE", vo.getDiscountType()); //1:費率2:折扣
			tempDTL.put("PROD_CAT", "2"); //1：基金2：ETF
			tempDTL.put("PROD_ID", vo.getProdId().trim());
			tempDTL.put("PROD_NAME", (null != evo ? evo.getETF_CNAME() : (null != stvo ? stvo.getSTOCK_CNAME() : null))); //商品名稱
			tempDTL.put("DISCOUNT", (null == vo.getFeeDiscount()) ? null : vo.getFeeDiscount()); //折數
			//			tempDTL.put("APPLY_DATE", sdfYYYY_MM_DD.format(vo.getApplyDate())); //BeneDate: 交易日期 / CrtTime: 鍵機時間
			tempDTL.put("APPLY_DATE", vo.getApplyDate());
			tempDTL.put("APPLY_STATUS", (sdfYYYYMMDD.parse("00000000").equals(vo.getAuthDate()) && sdfHHMMSS.parse("000000").equals(vo.getAuthTime()) ? (null == svo ? "1" : svo.getAPPLY_STATUS()) : "2")); //2:已授權null == svo ? "2" : svo.getAPPLY_STATUS()
			tempDTL.put("AUTH_STATUS", null == svo ? null : svo.getAUTH_STATUS()); //授權狀態

			if (svo != null && !StringUtils.equals("0", svo.getAPPLY_STATUS()))
				tempDTL.put("CURRENT_STATUS", getCurrentStatus(null == svo ? null : (String) svo.getHIGHEST_AUTH_LV(), null == svo ? null : (String) svo.getAUTH_STATUS()));
			else
				tempDTL.put("CURRENT_STATUS", "");

			list.add(tempDTL);
		}

		return list;
	}

	/*
	 * NRBRVC3 期間 - ETF議價查詢
	 * 
	 * 2016-12-22 add by ocean
	 * 
	 */
	public List<Map<String, Object>> getNRBRVC3List (DataAccessManager dam, List<Map<String, Object>> list, SOT710 sot710, SOT710InputVO inputVO_710, CRM421InputVO inputVO, TBCRM_CUST_MASTVO mvo) throws Exception {

		QueryConditionIF queryCondition;

		SOT710OutputVO nrbrvc3OutputVO = new SOT710OutputVO();
		nrbrvc3OutputVO = sot710.getPeriodFeeRate(inputVO_710);
		List<com.systex.jbranch.app.server.fps.sot710.PeriodFeeRateVO> nrbrvc3CList = nrbrvc3OutputVO.getPeriodFeeRateList();

		for (com.systex.jbranch.app.server.fps.sot710.PeriodFeeRateVO vo : nrbrvc3CList) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT APPLY_SEQ, APPLY_TYPE, BRG_REASON, APPLY_STATUS, HIGHEST_AUTH_LV, AUTH_STATUS, ");
			sb.append("BUY_HK_MRK, BUY_US_MRK, BUY_UK_MRK, BUY_JP_MRK, SELL_HK_MRK, SELL_US_MRK, SELL_UK_MRK, SELL_JP_MRK ");
			sb.append("FROM TBCRM_BRG_APPLY_PERIOD ");
			sb.append("WHERE CUST_ID = :custID ");
			sb.append("AND TRUNC(BRG_BEGIN_DATE) = TRUNC(:brgBeginDate) ");
			sb.append("AND TRUNC(BRG_END_DATE) = TRUNC(:brgEndDate) ");
			sb.append("AND APPLY_TYPE = '2' ");
			sb.append("AND APPLY_STATUS <> '9' ");

			if (vo.getEndDate() != null) { //終止日期
				sb.append("AND APPLY_STATUS = '3' ");
			} else {
				sb.append("AND APPLY_STATUS <> '3' ");

				if (vo.getAuthDate() != null) { //授權日期
					sb.append("AND (APPLY_STATUS = '2' OR APPLY_STATUS = '4') ");
				} else {
					sb.append("AND APPLY_STATUS <> '2' ");
				}
			}

			queryCondition.setObject("custID", inputVO.getCust_id());
			queryCondition.setObject("brgBeginDate", vo.getBrgBeginDate());
			queryCondition.setObject("brgEndDate", vo.getBrgEndDate());

			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> applySEQList = dam.exeQuery(queryCondition);
			Map<String, Object> tempDTL = new HashMap<String, Object>();
			tempDTL.put("APPLY_TYPE", applySEQList.size() > 0 ? applySEQList.get(0).get("APPLY_TYPE") : "1"); //1：基金2：海外ETF/股票
			tempDTL.put("SOURCE", applySEQList.size() == 0 ? "ESB" : "");
			tempDTL.put("MODIFY_FLAG", applySEQList.size() == 0 ? true : false);
			tempDTL.put("BRG_REASON", applySEQList.size() == 0 ? null : applySEQList.get(0).get("BRG_REASON"));
			tempDTL.put("HIGHEST_AUTH_LV", applySEQList.size() == 0 ? null : applySEQList.get(0).get("HIGHEST_AUTH_LV"));
			tempDTL.put("HIGHEST_AUTH_LV_NAME", getHightLevelName(applySEQList.size() == 0 ? null : applySEQList.get(0).get("HIGHEST_AUTH_LV").toString()));
			tempDTL.put("APPLY_SEQ", applySEQList.size() > 0 ? applySEQList.get(0).get("APPLY_SEQ") : null);
			tempDTL.put("CUST_ID", inputVO.getCust_id());
			tempDTL.put("CUST_NAME", mvo.getCUST_NAME());
			tempDTL.put("APPLY_CAT", "1"); //1：期間議價2：單次議價
			tempDTL.put("PROD_CAT", "2"); //1：基金2：ETF
			tempDTL.put("PROD_ID", null);
			tempDTL.put("PROD_NAME", null); //商品名稱
			tempDTL.put("DISCOUNT", null); //折數
			tempDTL.put("BRG_BEGIN_DATE", vo.getBrgBeginDate());
			tempDTL.put("BRG_END_DATE", vo.getBrgEndDate());
			//			tempDTL.put("APPLY_DATE", sdfYYYY_MM_DD.format(vo.getApplyDate())); //BeneDate: 交易日期 / CrtTime: 鍵機時間
			tempDTL.put("APPLY_DATE", vo.getApplyDate());

			if (vo.getEndDate() != null) {
				tempDTL.put("APPLY_STATUS", "3"); //3:已終止
			} else if (vo.getAuthDate() != null) {
				//				tempDTL.put("APPLY_STATUS", "2");	//2:已授權

				if (applySEQList.size() > 0 && "4".equals(applySEQList.get(0).get("APPLY_STATUS").toString())) { //終止待覆核於電文的狀態依舊會是"已授權"，故改以資料庫狀態為準。
					tempDTL.put("APPLY_STATUS", "4"); //4:終止待覆核
				} else {
					tempDTL.put("APPLY_STATUS", "2"); //2:已授權					
				}
			} else {
				tempDTL.put("APPLY_STATUS", applySEQList.size() == 0 ? "1" : applySEQList.get(0).get("APPLY_STATUS"));
			}

			tempDTL.put("AUTH_STATUS", applySEQList.size() == 0 ? null : applySEQList.get(0).get("AUTH_STATUS")); //授權狀態
			tempDTL.put("DMT_STOCK", null);
			tempDTL.put("DMT_BOND", null);
			tempDTL.put("DMT_BALANCED", null);
			tempDTL.put("FRN_STOCK", null);
			tempDTL.put("FRN_BOND", null);
			tempDTL.put("FRN_BALANCED", null);

			tempDTL.put("BUY_HK_MRK", vo.getBuyHKMrk());
			tempDTL.put("BUY_US_MRK", vo.getBuyUSMrk());
			tempDTL.put("BUY_UK_MRK", vo.getBuyUKMrk());
			tempDTL.put("BUY_JP_MRK", vo.getBuyJPMrk());
			tempDTL.put("SELL_HK_MRK", vo.getSellHKMrk());
			tempDTL.put("SELL_US_MRK", vo.getSellUSMrk());
			tempDTL.put("SELL_UK_MRK", vo.getSellUKMrk());
			tempDTL.put("SELL_JP_MRK", vo.getSellJPMrk());

			//			tempDTL.put("BUY_HK_MRK", applySEQList.size() == 0 ? null : applySEQList.get(0).get("BUY_HK_MRK"));
			//			tempDTL.put("BUY_US_MRK", applySEQList.size() == 0 ? null : applySEQList.get(0).get("BUY_US_MRK"));
			//			tempDTL.put("SELL_HK_MRK", applySEQList.size() == 0 ? null : applySEQList.get(0).get("SELL_HK_MRK"));
			//			tempDTL.put("SELL_US_MRK", applySEQList.size() == 0 ? null : applySEQList.get(0).get("SELL_US_MRK"));

			if (applySEQList.size() != 0 && !StringUtils.equals("0", applySEQList.get(0).get("APPLY_STATUS").toString())) {
				tempDTL.put("CURRENT_STATUS", getCurrentStatus(applySEQList.size() == 0 ? null : CharUtils.toString((Character) applySEQList.get(0).get("HIGHEST_AUTH_LV")), applySEQList.size() == 0 ? null : CharUtils.toString((Character) applySEQList.get(0).get("AUTH_STATUS"))));
			} else {
				tempDTL.put("CURRENT_STATUS", "");
			}
			list.add(tempDTL);

		}

		return list;
	}

	private String getCurrentStatus (String highestLv, String currLv) {
		String sb = "";

		if (StringUtils.isNotBlank(highestLv) && StringUtils.isNotBlank(currLv)) {
			Hashtable dict = new Hashtable();
			dict.put("1", "第一階主管");
			dict.put("2", "第二階主管");
			dict.put("3", "第三階主管");
			dict.put("4", "第四階主管");

			String strAuth = "(已授權)";

			for (Integer i = 1; i <= new Integer(highestLv); i++) {
				sb = sb + (StringUtils.isNotEmpty(sb) ? "," : "");
				sb = sb + (String) dict.get(i.toString()) + (i <= new Integer(currLv) ? strAuth : "");
			}
		}

		return sb;
	}

	public CRM421OutputVO getCustDtl (Object body) throws JBranchException {

		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("SELECT CUST_ID, NVL(SUM(ACT_PRFT), 0) AS Y_PROFEE ");
		sb.append("FROM TBCRM_CUST_PROFEE ");
		sb.append("WHERE DATA_YEAR||DATA_MONTH BETWEEN TO_CHAR(CURRENT_DATE-365, 'YYYYMM') ");
		sb.append("AND TO_CHAR(CURRENT_DATE, 'YYYYMM') ");
		sb.append("GROUP BY CUST_ID ");
		sb.append(") ");
		sb.append("SELECT CM.CUST_ID, CM.CUST_NAME, CM.CON_DEGREE, CM.VIP_DEGREE, CON_D.PARAM_NAME AS CON_DEGREE_NAME, CON_V.PARAM_NAME AS VIP_DEGREE_NAME, NVL(CM.AUM_AMT, 0) AS AUM_AMT, CM.BRA_NBR, NVL(CP.Y_PROFEE, 0) AS Y_PROFEE ");
		sb.append("FROM TBCRM_CUST_MAST CM ");
		sb.append("LEFT JOIN BASE CP ON CM.CUST_ID = CP.CUST_ID ");
		sb.append("LEFT JOIN TBSYSPARAMETER CON_D ON CON_D.PARAM_TYPE = 'CRM.CON_DEGREE' AND CON_D.PARAM_CODE = CM.CON_DEGREE ");
		sb.append("LEFT JOIN TBSYSPARAMETER CON_V ON CON_V.PARAM_TYPE = 'CRM.VIP_DEGREE' AND CON_V.PARAM_CODE = CM.VIP_DEGREE ");
		sb.append("WHERE CM.CUST_ID = :cust_id ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		outputVO.setCustList(dam.exeQuery(queryCondition));

		return outputVO;
	}

	/*
	 * 取得客戶資訊
	 * 
	 * 2016-12-01 modify by ocean
	 * 
	 */
	public void initial (Object body, IPrimitiveMap header) throws JBranchException {
		sendRtnObject(this.getCustDtl(body));
	}

	/*
	 * 取得最高授權層級(前端入口)
	 * 
	 * 2016-12-01 modify by ocean
	 * 
	 */
	public void getHighest_auth_lv (Object body, IPrimitiveMap header) throws JBranchException {
		sendRtnObject(this.getHighest_auth_lv(body));
	}

	/*
	 * 取得最高授權層級(後端入口)
	 * 
	 * 2016-12-01 modify by ocean
	 * 
	 */
	public CRM421OutputVO getHighest_auth_lv (Object body) throws JBranchException {

		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO return_VO = new CRM421OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		if ("4".equals(inputVO.getApply_type())) {
			//
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT HIGHEST_LV, (SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) FROM TBSYSPARAMETER WHERE PARAM_TYPE = '").append(chkIsUHRM() ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV").append("'||HIGHEST_LV) AS HIGHEST_LV_NAME ");
			sb.append("FROM ( ");
			sb.append("  SELECT MAX(LEVEL_NO) AS HIGHEST_LV ");
			sb.append("  FROM ( ");
			sb.append("    SELECT MIN(LEVEL_NO) AS LEVEL_NO ");
			sb.append("    FROM TBCRM_BRG_SETUP ");
			sb.append("    WHERE PROD_TYPE       = :prod_type ");
			sb.append("    AND CON_DEGREE        = NVL(:con_degree, 'OTH') ");
			sb.append("    AND DISCOUNT_RNG_TYPE = '1' ");
			sb.append("    AND DISCOUNT          <= :discount ");
			
			if (chkIsUHRM()) {
				sb.append("AND SETUP_TYPE        = '9' ");
			} else {
				sb.append("AND SETUP_TYPE        = '0' ");
			}
			
			sb.append("    UNION ");
			
			sb.append("    SELECT MAX(LEVEL_NO) AS LEVEL_NO ");
			sb.append("    FROM TBCRM_BRG_SETUP ");
			sb.append("    WHERE PROD_TYPE       = :prod_type ");
			sb.append("    AND CON_DEGREE        = NVL(:con_degree, 'OTH') ");
			sb.append("    AND DISCOUNT_RNG_TYPE = '2' ");
			sb.append("    AND DISCOUNT          > :discount ");
			if (chkIsUHRM()) {
				sb.append("AND SETUP_TYPE        = '9' ");
			} else {
				sb.append("AND SETUP_TYPE        = '0' ");
			}
			
			sb.append("  ) ");
			sb.append(") ");
			
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("discount", inputVO.getHighest_auth_lv()); //inputVO.getHighest_auth_lv() ==> 此為期間議價的最低折數or單筆議價的折扣數
			queryCondition.setObject("prod_type", inputVO.getProd_type());
			queryCondition.setObject("con_degree", StringUtils.isBlank(inputVO.getCon_degree()) ? "OTH" : inputVO.getCon_degree());

			if (StringUtils.isNotBlank(inputVO.getProd_type()) && StringUtils.isNotBlank(inputVO.getHighest_auth_lv())) { //inputVO.getHighest_auth_lv() ==> 此為期間議價的最低折數or單筆議價的折扣數
				return_VO.setHighest_lvList(dam.exeQuery(queryCondition));
			}
		} else {
			sb.append("SELECT HIGHEST_LV, (SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) FROM TBSYSPARAMETER WHERE PARAM_TYPE = '").append(chkIsUHRM() ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV").append("'||HIGHEST_LV) AS HIGHEST_LV_NAME ");
			sb.append("FROM ( ");
			sb.append("  SELECT MAX(LEVEL_NO) AS HIGHEST_LV ");
			sb.append("  FROM ( ");
			sb.append("    SELECT MIN(LEVEL_NO) AS LEVEL_NO ");
			sb.append("    FROM TBCRM_BRG_SETUP ");
			sb.append("    WHERE PROD_TYPE       = :prod_type ");
			sb.append("    AND CON_DEGREE        = NVL(:con_degree, 'OTH') ");
			sb.append("    AND DISCOUNT_RNG_TYPE = '1' ");
			sb.append("    AND DISCOUNT          <= :discount ");
			
			if (chkIsUHRM()) {
				sb.append("AND SETUP_TYPE        = '9' ");
			} else {
				sb.append("AND SETUP_TYPE        = '0' ");
			}
			
			sb.append("    UNION ");
			
			sb.append("    SELECT MAX(LEVEL_NO) AS LEVEL_NO ");
			sb.append("    FROM TBCRM_BRG_SETUP ");
			sb.append("    WHERE PROD_TYPE       = :prod_type ");
			sb.append("    AND CON_DEGREE        = NVL(:con_degree, 'OTH') ");
			sb.append("    AND DISCOUNT_RNG_TYPE = '2' ");
			sb.append("    AND DISCOUNT          > :discount ");
			if (chkIsUHRM()) {
				sb.append("AND SETUP_TYPE        = '9' ");
			} else {
				sb.append("AND SETUP_TYPE        = '0' ");
			}
			
			sb.append("  ) ");
			sb.append(") ");

			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("discount", inputVO.getHighest_auth_lv()); //inputVO.getHighest_auth_lv() ==> 此為期間議價的最低折數or單筆議價的折扣數
			queryCondition.setObject("prod_type", inputVO.getProd_type());
			queryCondition.setObject("con_degree", StringUtils.isBlank(inputVO.getCon_degree()) ? "OTH" : inputVO.getCon_degree());

			if (StringUtils.isNotBlank(inputVO.getProd_type()) && StringUtils.isNotBlank(inputVO.getHighest_auth_lv())) { //inputVO.getHighest_auth_lv() ==> 此為期間議價的最低折數or單筆議價的折扣數
				return_VO.setHighest_lvList(dam.exeQuery(queryCondition));
			}
		}
		
		List<Map<String, Object>> rtnList = return_VO.getHighest_lvList();
		
		if (rtnList.size() > 0 && null == rtnList.get(0).get("HIGHEST_LV") && chkIsUHRM()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("HIGHEST_LV", "0");
			map.put("HIGHEST_LV_NAME", " ");
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list.add(map);
			
			return_VO.setHighest_lvList(list);
		}

		return return_VO;
	}

	/*
	 * 期間議價(送出覆核)(前端入口)
	 * 
	 * 2016-12-01 modify by ocean
	 * 
	 */
	public void applyPeriod (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		sendRtnObject(this.applyPeriod(body));
	}

	/*
	 * 期間議價(送出覆核)(後端入口)
	 * 
	 * 2016-12-01 modify by ocean
	 * 2019-07-04 modify by ocean
	 * 
	 */
	public CRM421OutputVO applyPeriod (Object body) throws JBranchException, Exception {
		
		dam = this.getDataAccessManager();
		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//===優惠起日與鍵機系統日不可以超過60天。 and 優惠期間不可以超過6個月。
		Calendar calendar = Calendar.getInstance();
		BigDecimal div = new BigDecimal("1000").multiply(new BigDecimal("60")).multiply(new BigDecimal("60")).multiply(new BigDecimal("24"));

		BigDecimal startDate = new BigDecimal(inputVO.getBrg_sdate().getTime());
		BigDecimal endDate = new BigDecimal(inputVO.getBrg_edate().getTime());
//		BigDecimal nowDate = new BigDecimal(calendar.getTime().getTime()); //Date.getTime() 獲得毫秒型日期
		BigDecimal nowDate = new BigDecimal(new SimpleDateFormat("yyyyMMdd").parse(cbsservice.getCBSTestDate().substring(0,8)).getTime()); //Date.getTime() 獲得毫秒型日期

		BigDecimal sysDateToStartDate = (startDate.subtract(nowDate)).divide(div, 0, BigDecimal.ROUND_DOWN); //計算間隔多少天，系統日至起始日
		if (sysDateToStartDate.compareTo(new BigDecimal("60")) == 1) {
			throw new JBranchException("ehl_01_CRM421_004"); //優惠起日與鍵機系統日不可以超過60天。
		}

		StringBuffer sb = new StringBuffer();
		sb.append("select trunc(add_months( :sdate, 6)) as STARTDATE from dual ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("sdate", inputVO.getBrg_sdate());
		List<Map<String, Object>> sdateList = dam.exeQuery(queryCondition);
		Date sdate = (Date) sdateList.get(0).get("STARTDATE");

		if (sdate != null) {
			if (inputVO.getBrg_edate().getTime() > sdate.getTime()) {
				throw new JBranchException("ehl_01_CRM421_005"); //優惠期間不可以超過6個月。
			}
		} else {
			throw new JBranchException("請輸入適用優惠期間起日");
		}
		//===

		//===檢核「利害關係人身份」  若客戶為利害關係人，則不可透過新理專系統申請期間議價
		FP032675DataVO outputVO_FP032675 = getFC032675Data(inputVO);

		if (isCustStackholder(inputVO) == true) {
			throw new JBranchException("ehl_02_CRM421_003"); //客戶為利害關係人身份，不得進行議價。
		}
		//===

		sb = new StringBuffer();
		sb.append("SELECT LEVEL_NO, ROLE_LIST ");
		sb.append("FROM TBCRM_BRG_SETUP ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND PROD_TYPE = (CASE WHEN :prodType IN ('1','2','3') THEN '1' ELSE '2' END) ");
		sb.append("AND CON_DEGREE = NVL(:con_degree, 'OTH') ");
		sb.append("AND LEVEL_NO <= :highest_auth_lv ");
		if (chkIsUHRM()) {
			sb.append("AND SETUP_TYPE = '9' ");
		} else {
			sb.append("AND SETUP_TYPE = '0' ");
		}
		sb.append("ORDER BY LEVEL_NO ");

		//先寫入資料庫，再發電文檢核「議價申請資料」
		SOT709InputVO inputVO_709 = new SOT709InputVO();
		SOT709OutputVO outputVO_709 = new SOT709OutputVO();
		String FUND_SEQ = "";
		if (StringUtil.isEqual(inputVO.getApplyPeriod_1(), "Y")) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer timerChecker = getCountApplyPeriod("getCounts");
			if (StringUtils.isNotBlank(inputVO.getTerminateSEQ())) {
				timerChecker.append("AND NOT (APPLY_STATUS = '4' AND APPLY_SEQ = :terminateSEQ )");

				queryCondition.setObject("terminateSEQ", inputVO.getSeq());
			}
			queryCondition.setQueryString(timerChecker.toString());
			queryCondition.setObject("applyType", "1");
			queryCondition.setObject("custID", inputVO.getCust_id());
			queryCondition.setObject("startDate", new Timestamp(inputVO.getBrg_sdate().getTime()));
			queryCondition.setObject("endDate", new Timestamp(inputVO.getBrg_edate().getTime()));

			List<Map<String, Object>> countRes = dam.exeQuery(queryCondition);

			if (((BigDecimal) countRes.get(0).get("COUNTS")).compareTo(new BigDecimal("0")) == 0) {
				// =START= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若高端客戶原已有期間議價，UHRM再申請時由系統自動發出終止→再新增
				Map<String, Object> tempMap = chkFundPeriod(dam, outputVO_FP032675, inputVO_709, outputVO_709, inputVO, FUND_SEQ, sb);
				
				FUND_SEQ = (String) tempMap.get("FUND_SEQ");
				outputVO_709 = (SOT709OutputVO) tempMap.get("outputVO_709");
				// =END=
			} else {
				// =START= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若高端客戶原已有期間議價，UHRM再申請時由系統自動發出終止→再新增
				if (chkIsUHRM()) {
					// 終止議價
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer timerCheckerGetList = getCountApplyPeriod("getList");
					if (StringUtils.isNotBlank(inputVO.getTerminateSEQ())) {
						timerCheckerGetList.append("AND NOT (APPLY_STATUS = '4' AND APPLY_SEQ = :terminateSEQ )");

						queryCondition.setObject("terminateSEQ", inputVO.getSeq());
					}
					queryCondition.setQueryString(timerCheckerGetList.toString());
					queryCondition.setObject("applyType", "1");
					queryCondition.setObject("custID", inputVO.getCust_id());
					queryCondition.setObject("startDate", new Timestamp(inputVO.getBrg_sdate().getTime()));
					queryCondition.setObject("endDate", new Timestamp(inputVO.getBrg_edate().getTime()));

					List<Map<String, Object>> timerChkList = dam.exeQuery(queryCondition);
					
					for (Map<String, Object> map : timerChkList) {
						CRM421InputVO terminatrInputVO = new CRM421InputVO();
						terminatrInputVO.setSeq((String) map.get("APPLY_SEQ"));
						terminatrInputVO.setApply_cat("1"); // 期間議價
						terminatrInputVO.setTerminateReason("UHRM申請之FUND議價期間已重覆申請，系統自動終止舊議價。");
						
						terminateByUHRM(terminatrInputVO, "apply");
					}
					
					// 申請議價
					Map<String, Object> tempMap = chkFundPeriod(dam, outputVO_FP032675, inputVO_709, outputVO_709, inputVO, FUND_SEQ, sb);
					
					FUND_SEQ = (String) tempMap.get("FUND_SEQ");
					outputVO_709 = (SOT709OutputVO) tempMap.get("outputVO_709");
				} else {
					throw new JBranchException("ehl_02_CRM421_001"); //議價期間重複申請
				}
				// =END=
			}
		}

		SOT710InputVO inputVO_710 = new SOT710InputVO();
		SOT710OutputVO outputVO_710 = new SOT710OutputVO();
		String ETF_SEQ = "";
		if (StringUtil.isEqual(inputVO.getApplyPeriod_2(), "Y")) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer timerChecker = getCountApplyPeriod("getCounts");
			if (StringUtils.isNotBlank(inputVO.getTerminateSEQ())) {
				timerChecker.append("AND NOT (APPLY_STATUS = '4' AND APPLY_SEQ = :terminateSEQ )");

				queryCondition.setObject("terminateSEQ", inputVO.getSeq());
			}
			queryCondition.setQueryString(timerChecker.toString());
			queryCondition.setObject("applyType", "2");
			queryCondition.setObject("custID", inputVO.getCust_id());
			queryCondition.setObject("startDate", new Timestamp(inputVO.getBrg_sdate().getTime()));
			queryCondition.setObject("endDate", new Timestamp(inputVO.getBrg_edate().getTime()));

			List<Map<String, Object>> countRes = dam.exeQuery(queryCondition);

			if (((BigDecimal) countRes.get(0).get("COUNTS")).compareTo(new BigDecimal("0")) == 0) {
				// =START= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若高端客戶原已有期間議價，UHRM再申請時由系統自動發出終止→再新增
				Map<String, Object> tempMap = chkEtfPeriod(dam, outputVO_FP032675, inputVO_710, outputVO_710, inputVO, ETF_SEQ, sb);
				
				ETF_SEQ = (String) tempMap.get("ETF_SEQ");
				outputVO_710 = (SOT710OutputVO) tempMap.get("outputVO_710");
				// =END=
			} else {
				// =START= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若高端客戶原已有期間議價，UHRM再申請時由系統自動發出終止→再新增
				if (chkIsUHRM()) {
					// 終止議價
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer timerCheckerGetList = getCountApplyPeriod("getList");
					if (StringUtils.isNotBlank(inputVO.getTerminateSEQ())) {
						timerChecker.append("AND NOT (APPLY_STATUS = '4' AND APPLY_SEQ = :terminateSEQ )");

						queryCondition.setObject("terminateSEQ", inputVO.getSeq());
					}
					queryCondition.setQueryString(timerCheckerGetList.toString());
					queryCondition.setObject("applyType", "2");
					queryCondition.setObject("custID", inputVO.getCust_id());
					queryCondition.setObject("startDate", new Timestamp(inputVO.getBrg_sdate().getTime()));
					queryCondition.setObject("endDate", new Timestamp(inputVO.getBrg_edate().getTime()));

					List<Map<String, Object>> timerChkList = dam.exeQuery(queryCondition);
					
					for (Map<String, Object> map : timerChkList) {
						CRM421InputVO terminatrInputVO = new CRM421InputVO();
						terminatrInputVO.setSeq((String) map.get("APPLY_SEQ"));
						terminatrInputVO.setApply_cat("1"); // 期間議價
						terminatrInputVO.setTerminateReason("UHRM申請之ETF議價期間已重覆申請，系統自動終止舊議價。");
						
						terminateByUHRM(terminatrInputVO, "apply");
					}
					
					// 申請議價
					Map<String, Object> tempMap = chkEtfPeriod(dam, outputVO_FP032675, inputVO_710, outputVO_710, inputVO, ETF_SEQ, sb);
					
					ETF_SEQ = (String) tempMap.get("ETF_SEQ");
					outputVO_710 = (SOT710OutputVO) tempMap.get("outputVO_710");
				} else {
					throw new JBranchException("ehl_02_CRM421_001"); //議價期間重複申請
				}
				// =END=
			}
		}

		Boolean returnStatus = false;
		if ((StringUtil.isEqual(inputVO.getApplyPeriod_1(), "Y") && StringUtils.isBlank(outputVO_709.getErrorMsg()) && StringUtil.isEqual(inputVO.getApplyPeriod_2(), "Y") && StringUtils.isBlank(outputVO_710.getErrorTxt())) || //兩筆都有申請
			(StringUtil.isEqual(inputVO.getApplyPeriod_1(), "Y") && StringUtils.isBlank(outputVO_709.getErrorMsg()) && StringUtil.isEqual(inputVO.getApplyPeriod_2(), "N")) || //只申請基金
			(StringUtil.isEqual(inputVO.getApplyPeriod_2(), "Y") && StringUtils.isBlank(outputVO_710.getErrorTxt())) && 
			StringUtil.isEqual(inputVO.getApplyPeriod_1(), "N")) //只申請ETF
		{
			returnStatus = true;
		}

		if (StringUtils.isBlank(inputVO.getTerminateSEQ())) {
			//IF 都檢核通過 ，發電文鍵機「議價申請資料」並更新表格資料狀態
			if (returnStatus) {
				String custID = "";
				String creator = ""; //申請理專
				String mgrEmpID_1 = "";
				String cBranchNbr = ""; //申請分行

				if (StringUtil.isEqual(inputVO.getApplyPeriod_1(), "Y")) {
					TBCRM_BRG_APPLY_PERIODVO vo = new TBCRM_BRG_APPLY_PERIODVO();
					vo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, FUND_SEQ);

					if (vo != null) {
						if (StringUtil.isEqual(inputVO.getTerminateAndApply(), "Y")) {
							vo.setAPPLY_STATUS("0");
						} else {
							vo.setAPPLY_STATUS("1");
						}

						custID = vo.getCUST_ID();
						creator = vo.getCreator(); //2017-8-26 by Jacky 基金也要取建立人
						mgrEmpID_1 = vo.getMGR_EMP_ID_1();
						cBranchNbr = vo.getC_BRANCH_NBR();

						dam.update(vo);
						//==== for CBS 模擬日測試用========
						setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", FUND_SEQ);
						//================================================================

						SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
						inputVO_709.setCheckCode("2");
						inputVO_709.setApplySeq(FUND_SEQ);

						if (StringUtil.isEqual(outputVO_FP032675.getObuFlag(), "Y")) {
							outputVO_709 = sot709.periodBargainApplyOBU(inputVO_709);
						} else {
							outputVO_709 = sot709.periodBargainApplyDBU(inputVO_709);
						}

						//電文出現錯誤
						if (StringUtils.isNotBlank(outputVO_709.getErrorCode()) && !StringUtils.equals("0000", outputVO_709.getErrorCode())) {
							String errorMsg = "";

							if (StringUtil.isEqual(inputVO.getApplyPeriod_1(), "Y")) {
								TBCRM_BRG_APPLY_PERIODVO vo2 = new TBCRM_BRG_APPLY_PERIODVO();
								vo2 = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, FUND_SEQ);
								if (null != vo2) {
									dam.delete(vo2);
								}

								if (StringUtils.isNotBlank(errorMsg)) {
									errorMsg += "；";
								}
								errorMsg += (StringUtils.isNotBlank(outputVO_709.getErrorCode()) ? "申請基金：" + outputVO_709.getErrorMsg() : "");
							}
							outputVO.setErrorMsg(errorMsg);
						}
					} else {
						throw new APException("ehl_01_common_005"); // 顯示資料不存在
					}
					
					// =START= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
					if (StringUtils.equals(vo.getHIGHEST_AUTH_LV(), "0")) { // 代表毋須覆核(3折含以上)直接通過
						CRM431 crm431 = (CRM431) PlatformContext.getBean("crm431");
						CRM421InputVO inputVO_421 = new CRM421InputVO();
						inputVO_421.setCust_id(vo.getCUST_ID());
						
						crm431.setPeriodFinal(dam, this, inputVO_421, null, null, vo, "9", "3", null);
					}
					// =END=
				}

				if (StringUtil.isEqual(inputVO.getApplyPeriod_2(), "Y")) {
					TBCRM_BRG_APPLY_PERIODVO vo = new TBCRM_BRG_APPLY_PERIODVO();
					vo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, ETF_SEQ);

					if (vo != null) {
						if (StringUtil.isEqual(inputVO.getTerminateAndApply(), "Y")) {
							vo.setAPPLY_STATUS("0");
						} else {
							vo.setAPPLY_STATUS("1");
						}

						custID = vo.getCUST_ID();
						creator = vo.getCreator();
						mgrEmpID_1 = vo.getMGR_EMP_ID_1();
						cBranchNbr = vo.getC_BRANCH_NBR();

						dam.update(vo);

						//==== for CBS 模擬日測試用========
						setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", ETF_SEQ);
						//================================================================

						SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
						inputVO_710.setApplySEQ(ETF_SEQ);
						inputVO_710.setCheckCode("2");

						outputVO_710 = sot710.periodBargainApply(inputVO_710);

						if (StringUtils.isNotBlank(outputVO_710.getErrorTxt()) && !StringUtils.equals("0000", outputVO_710.getErrorTxt())) {
							String errorMsg = "";

							if (StringUtil.isEqual(inputVO.getApplyPeriod_2(), "Y")) {
								TBCRM_BRG_APPLY_PERIODVO vos2 = new TBCRM_BRG_APPLY_PERIODVO();
								vos2 = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, ETF_SEQ);
								if (null != vos2) {
									dam.delete(vos2);
								}

								if (StringUtils.isNotBlank(errorMsg)) {
									errorMsg += "；";
								}
								errorMsg += (StringUtils.isNotBlank(outputVO_710.getErrorTxt()) ? "申請海外ETF/股票：" + outputVO_710.getErrorTxt() : "");
							}
							outputVO.setErrorMsg(errorMsg);
						}
					} else {
						throw new APException("ehl_01_common_005"); // 顯示資料不存在
					}
					
					// =START= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
					if (StringUtils.equals(vo.getHIGHEST_AUTH_LV(), "0")) { // 代表毋須覆核(3折含以上)直接通過
						CRM431 crm431 = (CRM431) PlatformContext.getBean("crm431");
						CRM421InputVO inputVO_421 = new CRM421InputVO();
						inputVO_421.setCust_id(vo.getCUST_ID());
						
						crm431.setPeriodFinal(dam, this, inputVO_421, null, null, vo, "9", "3", null);
					}
					// =END=
				}
				
				if (StringUtils.isBlank(outputVO.getErrorMsg())) {
					//要發M+的主管ID
					if (StringUtils.isNotBlank(mgrEmpID_1) && StringUtils.isNotBlank(custID)) {
						CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");
						//如果都沒值的取系統變數登入者ID 2017-8-26 by Jacky
						if (StringUtils.isBlank(creator)) {
							creator = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
						}
						crm451.pushAuthMessage(mgrEmpID_1, "period", custID, creator, null, (chkIsUHRM() ? "9" : "0"), cBranchNbr);
					}
				}

			} else { //檢核不過 
				String errorMsg = "";
				if (StringUtil.isEqual(inputVO.getApplyPeriod_1(), "Y")) {
					TBCRM_BRG_APPLY_PERIODVO vos1 = new TBCRM_BRG_APPLY_PERIODVO();
					vos1 = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, FUND_SEQ);
					if (null != vos1) {
						dam.delete(vos1);
					}

					errorMsg += (StringUtils.isNotBlank(outputVO_709.getErrorMsg()) ? "申請基金：" + outputVO_709.getErrorMsg() : "");
				}

				if (StringUtil.isEqual(inputVO.getApplyPeriod_2(), "Y")) {
					TBCRM_BRG_APPLY_PERIODVO vos2 = new TBCRM_BRG_APPLY_PERIODVO();
					vos2 = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, ETF_SEQ);
					if (null != vos2) {
						dam.delete(vos2);
					}

					if (StringUtils.isNotBlank(errorMsg)) {
						errorMsg += "；";
					}
					errorMsg += (StringUtils.isNotBlank(outputVO_710.getErrorTxt()) ? "申請海外ETF/股票：" + outputVO_710.getErrorTxt() : "");
				}

				outputVO.setErrorMsg(errorMsg);
			}
		}

		return outputVO;
	}
	
	/*
	 * 期間議價(送出覆核) - 取得FUND_SEQ 並打檢核電文
	 * 
	 * 2019-07-04 add by ocean : 原邏輯不變，因需共用而另寫method
	 */
	private Map<String, Object> chkFundPeriod (DataAccessManager dam, 
								  			   FP032675DataVO outputVO_FP032675,
								  			   SOT709InputVO inputVO_709, 
								  			   SOT709OutputVO outputVO_709, 
								  			   CRM421InputVO inputVO, 
								  			   String FUND_SEQ, 
								  			   StringBuffer sb) throws Exception {
		
		Map<String, Object> tempMap = new HashMap<String, Object>();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("con_degree", StringUtils.isBlank(inputVO.getCon_degree()) ? "OTH" : inputVO.getCon_degree());
		queryCondition.setObject("prodType", inputVO.getProd_type());
		queryCondition.setObject("highest_auth_lv", inputVO.getHighest_auth_lv_1());

		List<Map<String, String>> list = dam.exeQuery(queryCondition);

		if (list.size() > 0 || chkIsUHRM()) {
			if (StringUtils.isNotBlank(inputVO.getTerminateSEQ())) {
				FUND_SEQ = inputVO.getTerminateSEQ();
			} else {
				FUND_SEQ = getPeriod_Fund_SN();
			}

			tempMap.put("FUND_SEQ", FUND_SEQ);
			savePeriod(dam, FUND_SEQ, inputVO, "1", "0", "V");
		} else {
			throw new JBranchException("ehl_02_CRM421_002");
		}

		SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
		inputVO_709.setCheckCode("1");
		inputVO_709.setApplySeq(FUND_SEQ);

		if (StringUtil.isEqual(outputVO_FP032675.getObuFlag(), "Y")) {
			outputVO_709 = sot709.periodBargainApplyOBU(inputVO_709);
		} else {
			outputVO_709 = sot709.periodBargainApplyDBU(inputVO_709);
		}
		
		tempMap.put("outputVO_709", outputVO_709);
		
		return tempMap;
	}

	/*
	 * 期間議價(送出覆核) - 取得ETF_SEQ 並打檢核電文 
	 * 2019-07-04 add by ocean : 原邏輯不變，因需共用而另寫method
	 */
	private Map<String, Object> chkEtfPeriod (DataAccessManager dam, 
											  FP032675DataVO outputVO_FP032675,
											  SOT710InputVO inputVO_710, 
											  SOT710OutputVO outputVO_710, 
											  CRM421InputVO inputVO, 
											  String ETF_SEQ, 
											  StringBuffer sb) throws Exception {
		
		Map<String, Object> tempMap = new HashMap<String, Object>();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("con_degree", StringUtils.isBlank(inputVO.getCon_degree()) ? "OTH" : inputVO.getCon_degree());
		queryCondition.setObject("prodType", inputVO.getProd_type());
		queryCondition.setObject("highest_auth_lv", inputVO.getHighest_auth_lv_2());
	
		List<Map<String, String>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0 || chkIsUHRM()) {
			if (StringUtils.isNotBlank(inputVO.getTerminateSEQ())) {
				ETF_SEQ = inputVO.getTerminateSEQ();
			} else {
				ETF_SEQ = getPeriod_ETF_SN();
			}
	
			tempMap.put("ETF_SEQ", ETF_SEQ);
			savePeriod(dam, ETF_SEQ, inputVO, "2", "0", "V");
		} else {
			throw new JBranchException("ehl_02_CRM421_002"); //沒有設定資料
		}
	
		SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
		inputVO_710.setApplySEQ(ETF_SEQ);
		inputVO_710.setCheckCode("1");
	
		outputVO_710 = sot710.periodBargainApply(inputVO_710);
		
		tempMap.put("outputVO_710", outputVO_710);
		
		return tempMap;
	}
	
	/*
	 * 期間議價(送出覆核) - 判斷期間是否重覆申請
	 * 
	 * 2017-01-04 add by ocean
	 * 2019-07-04 modify by ocean
	 */
	public StringBuffer getCountApplyPeriod (String type) {

		StringBuffer sb = new StringBuffer();
		switch (type) {
			case "getCounts" :
				sb.append("SELECT COUNT(1) AS COUNTS ");
				break;
			case "getList" :
				sb.append("SELECT * ");
				break;
			default :
				sb.append("SELECT COUNT(1) AS COUNTS ");
		}
		
		sb.append("FROM TBCRM_BRG_APPLY_PERIOD AP ");
		sb.append("WHERE AP.CUST_ID = :custID ");
		sb.append("AND AP.APPLY_STATUS not in ('3','9') "); // 排除狀態為 3:已終止 & 9:已退回
		sb.append("AND AP.APPLY_TYPE = :applyType ");
		sb.append("AND ( ");
		sb.append("   (AP.BRG_BEGIN_DATE BETWEEN :startDate AND :endDate) ");
		sb.append("OR (AP.BRG_END_DATE BETWEEN :startDate AND :endDate) ");
		sb.append("OR (AP.BRG_BEGIN_DATE <= :startDate AND AP.BRG_BEGIN_DATE >= :endDate AND AP.BRG_END_DATE <= :endDate) ");
		sb.append("OR (AP.BRG_BEGIN_DATE >= :startDate AND AP.BRG_BEGIN_DATE <= :endDate AND AP.BRG_END_DATE >= :endDate) ");
		sb.append(") ");


		return sb;
	}

	/*
	 * 期間議價(送出覆核) - 用於INSERT DATABASE
	 * 
	 * 2017-01-04 add by ocean
	 * 2019-05-19 modify by ocean
	 */
	public void savePeriod (DataAccessManager dam, String applySEQ, CRM421InputVO inputVO, String applyType, String authStatus, String applyStatus) throws JBranchException, ParseException {

		initUUID();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBCRM_BRG_APPLY_PERIODVO vo = new TBCRM_BRG_APPLY_PERIODVO();
		
		vo.setAPPLY_SEQ(applySEQ);
		vo.setAPPLY_TYPE(applyType);
		vo.setCUST_ID(inputVO.getCust_id());
		
		if (chkIsUHRM()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			
			sb.append("SELECT BRANCH_NBR ");
			sb.append("FROM TBORG_UHRM_BRH ");
			sb.append("WHERE EMP_ID = :loginID ");

			queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);
			
			if (loginBreach.size() > 0) {
				vo.setC_BRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else {
			vo.setC_BRANCH_NBR((String) SysInfo.getInfoValue(SystemVariableConsts.LOGINBRH));
		}
		
		switch (Integer.valueOf(applyType)) {
			case 1:
				vo.setDMT_STOCK(getBigDecimal(inputVO.getDmt_stock()));
				vo.setDMT_BOND(getBigDecimal(inputVO.getDmt_bond()));
				vo.setDMT_BALANCED(getBigDecimal(inputVO.getDmt_balanced()));
				vo.setFRN_STOCK(getBigDecimal(inputVO.getFrn_stock()));
				vo.setFRN_BOND(getBigDecimal(inputVO.getFrn_bond()));
				vo.setFRN_BALANCED(getBigDecimal(inputVO.getFrn_balanced()));
				vo.setHIGHEST_AUTH_LV(inputVO.getHighest_auth_lv_1());
	
				vo.setMGR_EMP_ID_1(StringUtils.equals(inputVO.getHighest_auth_lv_1(), "0") ? null :
								   getReviewLevelEmpList(inputVO.getCust_id(), 
														 "1", 
														 inputVO.getProd_type(), 
														 (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID), 
														 vo.getC_BRANCH_NBR(),  
														 chkIsUHRM() ? "9" : "0")
								   ); //2016-12-01 僅存第一級，待第一級覆核過，則update下一級
				break;
			case 2:
				vo.setBUY_HK_MRK(getBigDecimal(inputVO.getBuy_hk_mrk()));
				vo.setBUY_US_MRK(getBigDecimal(inputVO.getBuy_us_mrk()));
				vo.setBUY_UK_MRK(getBigDecimal(inputVO.getBuy_uk_mrk()));
				vo.setBUY_JP_MRK(getBigDecimal(inputVO.getBuy_jp_mrk()));
				vo.setSELL_HK_MRK(getBigDecimal(inputVO.getSell_hk_mrk()));
				vo.setSELL_US_MRK(getBigDecimal(inputVO.getSell_us_mrk()));
				vo.setSELL_UK_MRK(getBigDecimal(inputVO.getSell_uk_mrk()));
				vo.setSELL_JP_MRK(getBigDecimal(inputVO.getSell_jp_mrk()));
				vo.setHIGHEST_AUTH_LV(inputVO.getHighest_auth_lv_2());
	
				vo.setMGR_EMP_ID_1(StringUtils.equals(inputVO.getHighest_auth_lv_2(), "0") ? null :
								   getReviewLevelEmpList(inputVO.getCust_id(), 
														 "1", 
														 inputVO.getProd_type(), 
														 (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID), 
														 vo.getC_BRANCH_NBR(), 
														 chkIsUHRM() ? "9" : "0")
								   ); //2016-12-01 僅存第一級，待第一級覆核過，則update下一級
				break;
		}

		vo.setBRG_BEGIN_DATE(new Timestamp(inputVO.getBrg_sdate().getTime()));
		vo.setBRG_END_DATE(new Timestamp(inputVO.getBrg_edate().getTime()));
		if (StringUtils.isNotBlank(inputVO.getBrg_reason())) {
			vo.setBRG_REASON(inputVO.getBrg_reason());
		}
//		String creator = ws.getUser().getUserID();

		vo.setAUTH_STATUS(authStatus);
		vo.setAPPLY_STATUS(applyStatus);
		vo.setAPPLY_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

		boolean isEnabledSimulatedDate = cbsservice.isEnabledSimulatedDate();
		if (isEnabledSimulatedDate) {
			// 如果模擬日有開啟，則使用模擬日 (for 測試環境)
			long fakeSysdate = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime();
			vo.setAPPLY_DATE(new Timestamp(fakeSysdate));
			dam.setAutoCommit(true);
		}
		// =======================================================================================

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		vo.setAPPLE_SETUP_TYPE(chkIsUHRM() ? "9" : "0");
		
		// ===
		
		dam.create(vo);

		if (isEnabledSimulatedDate) {
			// 如果模擬日有開啟，則使用模擬日 (for 測試環境)
			dam.setAutoCommit(false);
			setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", applySEQ);
		}
		// =======================================================================================
	}

	/*
	 * 取得表定手續費率
	 * 
	 * 2016-12-01 add by ocean
	 * 2019-06-19 modify by ocean
	 * 
	 */
	public void getDefaultFeeRate (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();

		//0000397: OBU客戶開放使用特金模組全功能
		//只限定"基金"才要檢核，ETF的話就算 OBU還是可以進行單次議價
//		if (!"4".equals(inputVO.getApply_type())) {
//			//發電文檢核OBU身份
//			//若客戶為OBU，不得進行單次議價
//			FP032675DataVO outputVO_FP032675 = getFC032675Data(inputVO);
//
//			if (StringUtil.isEqual(outputVO_FP032675.getObuFlag(), "Y")) {
//				throw new JBranchException("客戶為OBU，不得進行單次議價");
//			}
//
//		}

		//抓取客戶帳號中的最小分行
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		SOT701InputVO sot701_inputVO = new SOT701InputVO();
		sot701_inputVO.setCustID(inputVO.getCust_id());
		sot701_inputVO.setProdType("1");
		List<AcctVO> accVO = sot701.getCustAcctData(sot701_inputVO).getTrustAcctList();
		TreeSet<String> ts = new TreeSet<String>();
		String branchNbr = null;
		if (accVO.size() > 0) {
			for (AcctVO acc : accVO) {
                //20200305_CBS_彥德_期間議價E008錯誤_分行送錯
				String branch = acc.getBranch();
				ts.add(branch);
			}
			branchNbr = ts.first();
		}

		//查詢表定手續費
		BigDecimal defaultFeeRate = null;
		String errorMsg = "";

		String a = "1236"; //基金
		String b = "45"; //海外ETF/股票
		if (a.contains(inputVO.getApply_type())) {
			SOT709InputVO inputVO_709 = new SOT709InputVO();
			SOT709OutputVO outputVO_709 = new SOT709OutputVO();

			SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");

			inputVO_709.setCustId(inputVO.getCust_id());
			//			inputVO_709.setBranchNbr(inputVO.getBra_nbr());
			inputVO_709.setBranchNbr(branchNbr);
			inputVO_709.setTrustCurrType(inputVO.getTrustCurrType());

			if (StringUtil.isEqual(inputVO.getApply_type(), "1") || StringUtil.isEqual(inputVO.getApply_type(), "3")) {
				inputVO_709.setTradeSubType("1");
			} else if (StringUtil.isEqual(inputVO.getApply_type(), "2")) {
				inputVO_709.setTradeSubType("2");
			} else if (StringUtil.isEqual(inputVO.getApply_type(), "6")) {
				inputVO_709.setTradeSubType("6"); //基金動態鎖利
			}

			inputVO_709.setProdId(inputVO.getProd_id());
			inputVO_709.setGroupIfa("");

			inputVO_709.setPurchaseAmtL(getBigDecimal(inputVO.getPurchase_amt()));
			inputVO_709.setPurchaseAmtM(getBigDecimal(0));
			inputVO_709.setPurchaseAmtH(getBigDecimal(0));
			inputVO_709.setBthCoupon("");
			inputVO_709.setAutoCx("");
			inputVO_709.setBargainApplySeq("");

			outputVO_709 = sot709.getDefaultFeeRate(inputVO_709);
			if (StringUtils.isNotBlank(outputVO_709.getErrorMsg())) {
				errorMsg = outputVO_709.getErrorMsg();
			} else {
				defaultFeeRate = outputVO_709.getDefaultFeeRates().getDefaultFeeRateL();
			}
		} else if (b.contains(inputVO.getApply_type())) {
			// =START= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715

//			CustAcctDataVO custAcctDataVO = getCustAcctData(inputVO); //要先取的客戶信託帳號
//			List<AcctVO> creditAcct = custAcctDataVO.getCreditAcctList();
//			//帳戶資料
//			String acctNo = "";
//			if (creditAcct.size() > 0) {
//				acctNo = creditAcct.get(0).getAcctNo();
//			}

			SOT710InputVO inputVO_710 = new SOT710InputVO();
			SOT710OutputVO outputVO_710 = new SOT710OutputVO();

			SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");

//			if (StringUtils.isNotBlank(acctNo)) {
//				inputVO_710.setProdId(inputVO.getProd_id());
//				inputVO_710.setTrustAcct(acctNo);
//				inputVO_710.setTrustCurrType("Y");
//
//				outputVO_710 = sot710.getDefaultFeeRate(inputVO_710);
//			}

			inputVO_710.setProdId(inputVO.getProd_id());
			inputVO_710.setTrustAcct(branchNbr);
			inputVO_710.setTrustCurrType("Y");
			// =END= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715

			//#0516 補上Cust_id供後面抓分行
			inputVO_710.setCustID(inputVO.getCust_id());
			outputVO_710 = sot710.getDefaultFeeRate(inputVO_710);

			if (StringUtils.isNotBlank(outputVO_710.getErrorTxt())) {
				errorMsg = outputVO_710.getErrorTxt();
			} else {
				defaultFeeRate = outputVO_710.getDefaultFeeRate();
			}
		}

		outputVO.setDefaultFeeRate(defaultFeeRate);
		outputVO.setErrorMsg(errorMsg);

		this.sendRtnObject(outputVO);
	}

	/*
	 * 是否進行適配
	 *
	 * 2016-12-05 add by ocean
	 *
	 */
	private Boolean isFitness (String applyType) throws DAOException, JBranchException {
		Boolean isfitness = true;

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_NAME ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_TYPE = 'SOT.FITNESS_YN' and PARAM_CODE = :prodType ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("prodType", ("1236".contains(applyType)) ? "NF" : "ETF");

		List<Map<String, Object>> pList = dam.exeQuery(queryCondition);
		if (pList.size() > 0) {
			isfitness = ("Y".equals((String) pList.get(0).get("PARAM_NAME"))) ? true : false;
		}

		return isfitness;
	}

	/*
	 * 加入單筆議價(類似購物車)清單
	 *
	 * 2016-10-11 add by walala
	 * 2016-12-05 modify by ocean
	 * 2019-06-19 modify by ocean
	 *
	 */
	public void addToList (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		CRM421OutputVO outputVO = new CRM421OutputVO();
		outputVO = addToList(body);
		this.sendRtnObject(outputVO);
	}

	public CRM421OutputVO addToList (Object body) throws JBranchException, Exception {

		initUUID();
		
		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		//		WorkStation ws = DataManager.getWorkStation(uuid);

		String a = "1236";
		String b = "45";
		//===檢核「利害關係人身份」  若客戶為利害關係人，若為利害關係人不得低於行員費率
		//		FC032675DataVO outputVO_FC032675 = getFC032675Data(inputVO);

		if (isCustStackholder(inputVO) == true) {
			Map<String, BigDecimal> isCustStackholderRate = new HashMap<String, BigDecimal>();

			sb.append("SELECT PARAM_TYPE, PARAM_CODE ");
			sb.append("FROM TBSYSPARAMETER ");
			sb.append("WHERE PARAM_TYPE IN ('CRM.NF_EMP_RATE', 'CRM.ETF_EMP_DISCOUNT') ");
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> paramList = dam.exeQuery(queryCondition);

			for (Map<String, Object> map : paramList) {
				isCustStackholderRate.put((String) map.get("PARAM_TYPE"), new BigDecimal(map.get("PARAM_CODE").toString()));
			}

			// 客戶為利害關係人身份，不得低於行員費率。基金為0.5%；ETF為3折。
			if (a.contains(inputVO.getApply_type())) {
				if ((inputVO.getFee_rate()).compareTo(isCustStackholderRate.get("CRM.NF_EMP_RATE")) == -1) {
					throw new JBranchException("ehl_01_CRM421_006");
				}
			} else if (b.contains(inputVO.getApply_type())) {
				if ((inputVO.getFee_discount()).compareTo(isCustStackholderRate.get("CRM.ETF_EMP_DISCOUNT")) == -1) {
					throw new JBranchException("ehl_01_CRM421_006");
				}
			}
		}
		//===

		//===是否為行員/行員親屬，若為行員議價須由最高層級授權。同貢獻度等級為E級的流程
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();

		// 行員
		sb.append("SELECT 1 AS EXIST ");
		sb.append("FROM DUAL ");
		sb.append("WHERE EXISTS (SELECT 'x' FROM TBORG_MEMBER WHERE CUST_ID = :custID ");
		sb.append("and CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("and SERVICE_FLAG = 'A') ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("custID", inputVO.getCust_id());

		List<Map<String, Object>> existList = dam.exeQuery(queryCondition);

		if (existList.size() > 0) {
			CRM421InputVO tempInputVO = new CRM421InputVO();
			CRM421OutputVO tempOutputVO = new CRM421OutputVO();

			tempInputVO.setProd_type((a.contains(inputVO.getApply_type()) ? "1" : "4"));
			tempInputVO.setCon_degree("E");
			tempOutputVO = getHighest_auth_lv(body);

			List<Map<String, Object>> mapList = tempOutputVO.getHighest_lvList();
			if(CollectionUtils.isNotEmpty(mapList)) {
				inputVO.setHighest_auth_lv(mapList.get(0).get("HIGHEST_LV").toString());
			} else {
				inputVO.setHighest_auth_lv("");
			}
		}
		//===

		//先寫入資料庫，再發電文檢核「議價申請資料」。
		String APPLY_SEQ = "";
//		if (list.size() > 0 || (uhrmMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE)) && StringUtils.equals("0", inputVO.getHighest_auth_lv()))) {
		TBCRM_BRG_APPLY_SINGLEVO vo = new TBCRM_BRG_APPLY_SINGLEVO();
		String mark = StringUtils.equals("M", inputVO.getTrustTS()) ? "M" : "A";
		APPLY_SEQ = (StringUtil.isEqual(inputVO.getApply_type(), "4") ? getSingle_ETF_SN(mark) : getSingle_Fund_SN(mark));

		vo.setAPPLY_SEQ(APPLY_SEQ);
		vo.setAPPLY_TYPE(inputVO.getApply_type());
		//金錢信託、基金小額、定期定額，三個條件成立走[真]議價
		if (StringUtils.equals(inputVO.getTrustTS(), "M") && StringUtils.equals(inputVO.getApply_type(), "2") && StringUtils.equals(inputVO.getProd_type(), "1")){
			vo.setCUST_ID(inputVO.getFakeID());
		}else{
			vo.setCUST_ID(inputVO.getCust_id());
		}

		vo.setPROD_ID(inputVO.getProd_id());
		vo.setPROD_NAME(inputVO.getProd_name());
		vo.setTRUST_CURR_TYPE(inputVO.getTrustCurrType()); //信託業務別

		if ("N".equals(inputVO.getTrustCurrType())) {
			vo.setTRUST_CURR("TWD"); //信託幣別
		} else {
			vo.setTRUST_CURR(inputVO.getTrust_curr()); //信託幣別
		}

		vo.setPURCHASE_AMT(getBigDecimal(inputVO.getPurchase_amt()));
		vo.setENTRUST_UNIT(getBigDecimal(inputVO.getEntrust_unit()));
		vo.setENTRUST_AMT(getBigDecimal(inputVO.getEntrust_amt()));
		if (StringUtils.isNotBlank(inputVO.getBrg_reason())) {
			vo.setBRG_REASON(inputVO.getBrg_reason());
		}
		vo.setDEFAULT_FEE_RATE(inputVO.getDefaultFeeRate());
		vo.setFEE_RATE(inputVO.getFee_rate());

		if (StringUtil.isEqual(inputVO.getApply_type(), "2")) {
			Map<String, String> gmRoleMap = new XmlInfo().doGetVariable("CRM.SINGLE_REG_DISCOUNT", FormatHelper.FORMAT_3);
			vo.setFEE_DISCOUNT(new BigDecimal(gmRoleMap.get(addZeroForNum(String.valueOf(inputVO.getFee_discount()), 2))));
		} else {
			vo.setFEE_DISCOUNT(inputVO.getFee_discount());
		}

		vo.setFEE(getBigDecimal(inputVO.getFee()));
		vo.setDISCOUNT_TYPE(inputVO.getDiscount_type());
		
		if (chkIsUHRM()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("SELECT BRANCH_NBR ");
			sb.append("FROM TBORG_UHRM_BRH ");
			sb.append("WHERE EMP_ID = :loginID ");

			queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);
			
			if (loginBreach.size() > 0) {
				vo.setC_BRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else {
			vo.setC_BRANCH_NBR((String) SysInfo.getInfoValue(SystemVariableConsts.LOGINBRH));
		}
		
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		vo.setMGR_EMP_ID_1(StringUtils.equals(inputVO.getHighest_auth_lv(), "0") ? null :
						   getReviewLevelEmpList(inputVO.getCust_id(),
												 "1",
												 inputVO.getProd_type(),
												 (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID),
												 vo.getC_BRANCH_NBR(),
												 chkIsUHRM() ? "9" : "0")
						   ); //2016-12-01 僅存第一級，待第一級覆核過，則update下一級

		vo.setHIGHEST_AUTH_LV(inputVO.getHighest_auth_lv());
		vo.setAUTH_STATUS("0");
		vo.setAPPLY_STATUS("V");
		vo.setAPPLY_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		vo.setAPPLE_SETUP_TYPE(chkIsUHRM() ? "9" : "0");
		
		// ===

		dam.create(vo);

		//發單次議價的電文檢核 (參考下面文件發電文檢核，傳入參數APPLY_SEQ(流水號)、CRM421InputVO.APPLY_TYPE、檢核碼1(檢核))
		if (a.contains(inputVO.getApply_type())) {
			SOT709InputVO inputVO_709 = new SOT709InputVO();
			SOT709OutputVO outputVO_709 = new SOT709OutputVO();

			inputVO_709.setApplySeq(APPLY_SEQ);
			inputVO_709.setCheckCode("1");

			SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
			if (StringUtil.isEqual(inputVO.getApply_type(), "1") || StringUtil.isEqual(inputVO.getApply_type(), "3") || StringUtil.isEqual(inputVO.getApply_type(), "6")) {
				outputVO_709 = sot709.singleBargainApply(inputVO_709);
			} else if (StringUtil.isEqual(inputVO.getApply_type(), "2")) {
				outputVO_709 = sot709.singleRegBargainApply(inputVO_709);
			}

			//檢核通過，將資料狀態更新為「暫存-加入申請清單」，若不通過，將資料刪除
			addToListSingle(dam, outputVO, outputVO_709.getErrorMsg(), APPLY_SEQ);
		} else if (b.contains(inputVO.getApply_type())) {
			SOT710InputVO inputVO_710 = new SOT710InputVO();
			SOT710OutputVO outputVO_710 = new SOT710OutputVO();

			inputVO_710.setApplySEQ(APPLY_SEQ);
			inputVO_710.setCheckCode("1");

			SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
			outputVO_710 = sot710.singleBargainApply(inputVO_710);

			//檢核通過，將資料狀態更新為「暫存-加入申請清單」，若不通過，將資料刪除
			addToListSingle(dam, outputVO, outputVO_710.getErrorTxt(), APPLY_SEQ);
		}

		List applySeqList = inputVO.getApplySeqList();
		if (null == applySeqList) {
			applySeqList = new ArrayList();
		}
		applySeqList.add(APPLY_SEQ);
		outputVO.setApplySeqList(applySeqList);

		return outputVO;
	}

	/*
	 * 加入單筆議價(類似購物車)清單(共用)
	 *
	 * 2016-12-05 add by ocean
	 *
	 */
	public CRM421OutputVO addToListSingle (DataAccessManager dam, CRM421OutputVO outputVO, String errorMsg, String APPLY_SEQ) throws JBranchException, ParseException {

		if (StringUtils.isBlank(errorMsg)) {
			TBCRM_BRG_APPLY_SINGLEVO vos = new TBCRM_BRG_APPLY_SINGLEVO();
			vos = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, APPLY_SEQ);

			if (vos != null) {
				vos.setAPPLY_STATUS("0");
				dam.update(vos);
				// CBS 測試
				setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", APPLY_SEQ);
			} else {
				throw new APException("ehl_01_common_005"); // 顯示資料不存在
			}
		} else {
			TBCRM_BRG_APPLY_SINGLEVO vos = new TBCRM_BRG_APPLY_SINGLEVO();
			vos = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, APPLY_SEQ);

			if (vos != null) {
				dam.delete(vos);
			} else {
				throw new APException("ehl_01_common_005"); // 顯示資料不存在
			}

			outputVO.setErrorMsg(errorMsg);
		}

		return outputVO;
	}

	/*
	 * 取得單筆議價(類似購物車)清單
	 *
	 * 2016-12-01 modify by ocean : 條件修正
	 *
	 */
	public void inquirelist (Object body, IPrimitiveMap header) throws JBranchException {

		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT APPLY_SEQ, APPLY_TYPE, PROD_ID, PROD_NAME, TRUST_CURR, PURCHASE_AMT, DEFAULT_FEE_RATE, FEE_RATE, FEE_DISCOUNT, FEE ");
		sb.append("FROM TBCRM_BRG_APPLY_SINGLE ");

		if (null != inputVO.getApplySeqList() && inputVO.getApplySeqList().size() > 0) {
			sb.append("WHERE APPLY_SEQ IN (:applySeqList) ");
			queryCondition.setObject("applySeqList", inputVO.getApplySeqList());
		}

		queryCondition.setQueryString(sb.toString());
		if (null != inputVO.getApplySeqList() && inputVO.getApplySeqList().size() > 0) {
			outputVO.setTempList(dam.exeQuery(queryCondition));
		}

		outputVO.setApplySeqList(inputVO.getApplySeqList());

		sendRtnObject(outputVO);
	}

	/*
	 * 刪除單筆議價(類似購物車)清單
	 *
	 * 2016-12-05 modify by ocean : input applySeqList
	 *
	 */
	public void deletelist (Object body, IPrimitiveMap header) throws JBranchException {

		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();

		TBCRM_BRG_APPLY_SINGLEVO vo = new TBCRM_BRG_APPLY_SINGLEVO();
		vo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, inputVO.getSeq());

		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_005");
		}

		List applySeqList = inputVO.getApplySeqList();
		if (null == applySeqList) {
			applySeqList = new ArrayList();
		}
		applySeqList.remove(inputVO.getApplySeqList().indexOf(inputVO.getSeq()));
		outputVO.setApplySeqList(applySeqList);

		this.sendRtnObject(outputVO);
	}

	/**
	 * 單次議價(送出覆核)
	 *
	 * 2019-06-19 modify by ocean
	 * 2019-07-04 modify by ocean
	 */
	public void applySingle (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		this.applySingle(body);

		sendRtnObject(null);
	}

	public void applySingle (Object body) throws JBranchException, Exception {

		XmlInfo xmlInfo = new XmlInfo();

		//將參數APPLY_SEQ、檢核碼2(鍵機)轉傳下面文件發電文；若鍵機成功，將資料申請狀態更新為1 (待覆核)
		CRM421InputVO inputVO = (CRM421InputVO) body;
		dam = this.getDataAccessManager();

		String custID = "";
		String creator = ""; //申請理專
		String mgrEmpID_1 = "";
		String cBranchNbr = ""; //申請分行

		/***
		 * 2020-4-17 by Jacky 新增
		 * 增加判斷是否為金錢信託小額交易
		 */
		boolean isMtrustReg = false;

		for (Map<String, Object> data : inputVO.getList()) {
			if (StringUtils.equals(data.get("APPLY_TYPE").toString(), "1") || StringUtils.equals(data.get("APPLY_TYPE").toString(), "2")
					|| StringUtils.equals(data.get("APPLY_TYPE").toString(), "6")) {
				SOT709InputVO inputVO_709 = new SOT709InputVO();
				SOT709OutputVO outputVO_709 = new SOT709OutputVO();

				inputVO_709.setApplySeq(data.get("APPLY_SEQ").toString());
				inputVO_709.setCheckCode("2");
				SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
				if (StringUtils.equals(data.get("APPLY_TYPE").toString(), "1") || StringUtils.equals(data.get("APPLY_TYPE").toString(), "6")) {
					outputVO_709 = sot709.singleBargainApply(inputVO_709);
				} else if (StringUtils.equals(data.get("APPLY_TYPE").toString(), "2")) {
					//金錢信託小額在這判斷真假議價以及後續是否要送M+
					if(StringUtils.isNotBlank((String)data.get("CONTRACT_ID"))){ //金錢信託定額申購自動走議價流程不需人工覆核
						if(StringUtils.equals("M", data.get("APPLY_SEQ").toString().substring(0,1))){
							inputVO.setTrustTS("M");
						} else{
							inputVO.setTrustTS("S");
						}
					}
					outputVO_709 = sot709.singleRegBargainApply(inputVO_709);
				}

				if (StringUtils.isNotBlank(outputVO_709.getErrorMsg())) {
					throw new APException(data.get("PROD_NAME").toString() + "商品鍵機失敗，請重新申請，謝謝");
				}
			} else if (StringUtil.isEqual(data.get("APPLY_TYPE").toString(), "4")) {
				SOT710InputVO inputVO_710 = new SOT710InputVO();
				SOT710OutputVO outputVO_710 = new SOT710OutputVO();

				inputVO_710.setApplySEQ(data.get("APPLY_SEQ").toString());
				inputVO_710.setCheckCode("2");

				SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
				outputVO_710 = sot710.singleBargainApply(inputVO_710);

				if (StringUtils.isNotBlank(outputVO_710.getErrorTxt())) {
					throw new APException(data.get("PROD_NAME").toString() + "商品鍵機失敗，請重新申請，謝謝");
				}
			}

			TBCRM_BRG_APPLY_SINGLEVO vo = new TBCRM_BRG_APPLY_SINGLEVO();
			vo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, data.get("APPLY_SEQ").toString());

			if (null != vo) {
				vo.setAPPLY_STATUS("1");
				dam.update(vo);

				//==== for CBS 模擬日測試用========
				setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", data.get("APPLY_SEQ").toString());
				//================================================================

				//於下單程式加入log。作為往後追查問題的資訊來源(#4169)
				logger.info("發送" + vo.getCUST_ID() + "之單次議價申請：" + vo.getPROD_NAME() + " 手續費率：" + vo.getFEE_RATE() + " 手續費折數：" + vo.getFEE_DISCOUNT() + " 最高授權層級：" + vo.getHIGHEST_AUTH_LV());

				custID = vo.getCUST_ID();
				creator = vo.getCreator();
				mgrEmpID_1 = vo.getMGR_EMP_ID_1();
				cBranchNbr = vo.getC_BRANCH_NBR();

			} else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_005");
			}

			// =START= 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			if (StringUtils.equals(vo.getHIGHEST_AUTH_LV(), "0") ||   // 代表毋須覆核(3折含以上)直接通過
					 (StringUtils.equals(data.get("APPLY_TYPE").toString(), "2") && StringUtils.equals(inputVO.getTrustTS(), "M") && StringUtils.equals("99331241", data.get("CUST_ID").toString()))) { //金錢信託&小額&假議價自動覆核
				CRM431 crm431 = (CRM431) PlatformContext.getBean("crm431");
				CRM421InputVO inputVO_421 = new CRM421InputVO();
				inputVO_421.setCust_id(vo.getCUST_ID());

				crm431.setSingleFinal(dam, this, inputVO_421, null, null, vo, "9", "3", null);
			}
			// =END=
		}

		//要發M+的主管ID
		if (StringUtils.isNotBlank(mgrEmpID_1) && StringUtils.isNotBlank(custID)) {
			CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");
			//如果都沒值的取系統變數登入者ID 2017-8-26 by Jacky
			if (StringUtils.isBlank(creator)) {
				creator = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
			}
			crm451.pushAuthMessage(mgrEmpID_1, "single", custID, creator, null, chkIsUHRM() ? "9" : "0", cBranchNbr);
		}
	}

	/*
	 * 刪除議價
	 *
	 * 2016-12-14 modify by ocean : add 電文
	 *
	 */
	public void deleteApplyFlow (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		CRM421InputVO inputVO = (CRM421InputVO) body;
		dam = this.getDataAccessManager();

		String checkCode = "4"; //1檢核 2鍵機 3覆核4刪除 5修改

		if (StringUtil.isEqual(inputVO.getApply_cat(), "1")) { // 期間議價
			TBCRM_BRG_APPLY_PERIODVO vo = new TBCRM_BRG_APPLY_PERIODVO();
			vo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, inputVO.getSeq());

			if (vo != null) {
				if (StringUtils.equals("0", vo.getAPPLY_STATUS())) {
					dam.delete(vo);
				} else {
					SOT709InputVO inputVO_709 = new SOT709InputVO();
					SOT709OutputVO outputVO_709 = new SOT709OutputVO();
					SOT710InputVO inputVO_710 = new SOT710InputVO();
					SOT710OutputVO outputVO_710 = new SOT710OutputVO();
					if (StringUtils.equals("1", vo.getAPPLY_TYPE())) {
						inputVO_709.setApplySeq(vo.getAPPLY_SEQ());
						inputVO_709.setCheckCode(checkCode);

						FP032675DataVO outputVO_FP032675 = getFC032675Data(inputVO);

						SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
						if (StringUtil.isEqual(outputVO_FP032675.getObuFlag(), "Y")) {
							outputVO_709 = sot709.periodBargainApplyOBU(inputVO_709);
						} else {
							outputVO_709 = sot709.periodBargainApplyDBU(inputVO_709);
						}

						if (StringUtils.isNotBlank(outputVO_709.getErrorMsg())) {
							throw new APException(outputVO_709.getErrorMsg());
						} else {
							dam.delete(vo);
						}
					} else {
						inputVO_710.setApplySEQ(vo.getAPPLY_SEQ());
						inputVO_710.setCheckCode(checkCode);

						SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
						outputVO_710 = sot710.periodBargainApply(inputVO_710);

						if (StringUtils.isNotBlank(outputVO_710.getErrorTxt())) {
							throw new APException(outputVO_710.getErrorTxt());
						} else {
							dam.delete(vo);
						}
					}
				}
			} else {
				throw new APException("ehl_01_common_005"); // 顯示資料不存在
			}
		} else if (StringUtil.isEqual(inputVO.getApply_cat(), "2")) { //單次議價
			TBCRM_BRG_APPLY_SINGLEVO vo = new TBCRM_BRG_APPLY_SINGLEVO();
			vo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, inputVO.getSeq());

			if (vo != null) {
				if (StringUtils.equals("0", vo.getAPPLY_STATUS())) {
					dam.delete(vo);
				} else {
					SOT709InputVO inputVO_709 = new SOT709InputVO();
					SOT709OutputVO outputVO_709 = new SOT709OutputVO();
					SOT710InputVO inputVO_710 = new SOT710InputVO();
					SOT710OutputVO outputVO_710 = new SOT710OutputVO();
					if (StringUtils.equals("1", vo.getAPPLY_TYPE()) || StringUtils.equals("2", vo.getAPPLY_TYPE())
							||  StringUtils.equals("6", vo.getAPPLY_TYPE())) { //1：基金單筆申購 、2：基金定期(不)定額申購 、6：基金動態鎖利
						inputVO_709.setApplySeq(vo.getAPPLY_SEQ());
						inputVO_709.setCheckCode(checkCode);

						SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
						if (StringUtils.equals("1", vo.getAPPLY_TYPE()) || StringUtils.equals("6", vo.getAPPLY_TYPE())) {
							outputVO_709 = sot709.singleBargainApply(inputVO_709);
						} else if (StringUtils.equals("2", vo.getAPPLY_TYPE())) {
							outputVO_709 = sot709.singleRegBargainApply(inputVO_709);
						}

						if (StringUtils.isNotBlank(outputVO_709.getErrorMsg())) {
							throw new APException(outputVO_709.getErrorMsg());
						} else {
							dam.delete(vo);
						}
					} else { //4：海外ETF/股票申購
						inputVO_710.setApplySEQ(vo.getAPPLY_SEQ());
						inputVO_710.setCheckCode(checkCode);

						SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
						outputVO_710 = sot710.singleBargainApply(inputVO_710);

						if (StringUtils.isNotBlank(outputVO_710.getErrorTxt())) {
							throw new APException(outputVO_710.getErrorTxt());
						} else {
							dam.delete(vo);
						}
					}
				}
			} else {
				throw new APException("ehl_01_common_005"); // 顯示資料不存在
			}
		}

		this.sendRtnObject(null);
	}

	/*
	 * 終止(前端入口)
	 *
	 * 2016-12-01 modify by ocean
	 *
	 */
	public void terminate (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		if (chkIsUHRM()) {
			terminateByUHRM(body, "terminate");
		} else {
			terminate(body);
		}

		sendRtnObject(null);
	}

	/*
	 * 終止 by UHRM
	 *
	 * 2019-07-04 modify by ocean
	 */
	public void terminateByUHRM (Object body, String type) throws JBranchException, Exception {

		SerialNumberUtil sn = new SerialNumberUtil();
		CRM421InputVO inputVO = (CRM421InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_BRG_APPLY_PERIODVO pvo = null;

		//1：期間議價	2：單次議價
		if (StringUtils.equals(inputVO.getApply_cat(), "1")) {
			pvo = new TBCRM_BRG_APPLY_PERIODVO();
			pvo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, inputVO.getSeq());
		}

		if (null != pvo) {
			pvo.setAUTH_STATUS("0"); //尚未覆核
			pvo.setAPPLY_STATUS(APPLY_STATUS_TERMINATE_REVIEW); //終止待覆核("4");

			if (StringUtils.equals("apply", type)) {
				pvo.setHIGHEST_AUTH_LV("0");
				pvo.setMGR_EMP_ID_1(null);

				dam.update(pvo);

				// 議價 For CBS 環境先用模擬日代替
				setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", inputVO.getSeq());
				// =======================================================================================


				// 終止電文
				CRM431 crm431 = (CRM431) PlatformContext.getBean("crm431");
				CRM421InputVO inputVO_421 = new CRM421InputVO();
				inputVO_421.setCust_id(pvo.getCUST_ID());

				crm431.setPeriodFinal(dam, this, inputVO_421, null, null, pvo, "9", (StringUtils.equals(pvo.getAPPLY_TYPE(), "1") ? "5" : "6"), null);
			} else {
				pvo.setMGR_EMP_ID_1(getReviewLevelEmpList(pvo.getCUST_ID(), "1", pvo.getAPPLY_TYPE(), pvo.getCreator(), pvo.getC_BRANCH_NBR(), pvo.getAPPLE_SETUP_TYPE()));
				pvo.setHIGHEST_AUTH_LV("1"); //終止議價覆核僅需一階主管覆核。

				dam.update(pvo);
				// 議價 For CBS 環境先用模擬日代替
				setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", inputVO.getSeq());
				// =======================================================================================

			}
		}

		TBCRM_BRG_APPROVAL_HISTORYVO hvo = new TBCRM_BRG_APPROVAL_HISTORYVO();
		hvo.setAPPROVAL_SEQ(new BigDecimal(new SimpleDateFormat("yyyyMMdd").format(new Date()) + getSeqNum(sn, "SQ_TBCRM_BRG_APPROVAL_HISTORY", "00000000000", new java.sql.Timestamp(System.currentTimeMillis()), 1, new Long("99999999999"), "y", new Long("0")))); //簽核歷程編號
		hvo.setAPPLY_SEQ(inputVO.getSeq()); //議價編號
		hvo.setAPPLY_CAT(inputVO.getApply_cat()); //類別 1:期間 2:單次
		hvo.setTERMINATE_REASON(inputVO.getTerminateReason()); //終止原因

		dam.create(hvo);

		//要發M+的主管ID
		if (StringUtils.isNotBlank(pvo.getMGR_EMP_ID_1()) && StringUtils.isNotBlank(pvo.getCUST_ID())) {
			CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");

			if (null != pvo) {
				crm451.pushAuthMessage(pvo.getMGR_EMP_ID_1(), "period", pvo.getCUST_ID(), (StringUtils.isBlank(pvo.getCreator()) ? (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID) : pvo.getCreator()), null, "9", pvo.getC_BRANCH_NBR());
			}
		}
	}

	/*
	 * 終止 by 分行理專
	 *
	 * 2016-10-11 add by walala
	 * 2016-12-14 modify by ocean : add 電文
	 * 2019-07-04 modify by ocean : add UHRM
	 *
	 */
	public void terminate (Object body) throws JBranchException, Exception {

		initUUID();
		
		String custID = "";
		String creator = ""; //申請理專
		String mgrEmpID_1 = "";

		SerialNumberUtil sn = new SerialNumberUtil();
		CRM421InputVO inputVO = (CRM421InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBCRM_BRG_APPLY_SINGLEVO svo = null;
		TBCRM_BRG_APPLY_PERIODVO pvo = null;

		//1：期間議價	2：單次議價
		if (StringUtils.equals(inputVO.getApply_cat(), "2")) {
			svo = new TBCRM_BRG_APPLY_SINGLEVO();
			svo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, inputVO.getSeq());
		} else if (StringUtils.equals(inputVO.getApply_cat(), "1")) {
			pvo = new TBCRM_BRG_APPLY_PERIODVO();
			pvo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, inputVO.getSeq());
		}

		//終止後申請
		if (StringUtils.equals(inputVO.getApply_cat(), "1") && StringUtils.isNotBlank(inputVO.getTerminateSEQ())) {
			pvo.setTERMINATE_SEQ(inputVO.getTerminateSEQ());
		}

		if (null != svo) { //目前單次議價無終止功能
			String prodType = "";
			if ("1".equals(svo.getAPPLY_TYPE()) || "2".equals(svo.getAPPLY_TYPE())) { //申請類別 1：基金單筆申購 2：基金定期(不)定額申購 3：基金贖回再申購 4：海外ETF/股票申購 5：海外ETF股票贖回
				prodType = "1";
			} else if ("4".equals(svo.getAPPLY_TYPE())) {
				prodType = "2";
			}

			//重抓一次現職的一階主管，以防主管有異動。
			svo.setMGR_EMP_ID_1(getReviewLevelEmpList(svo.getCUST_ID(), "1", prodType, svo.getCreator(), svo.getC_BRANCH_NBR(), svo.getAPPLE_SETUP_TYPE()));
			svo.setAUTH_STATUS("0"); //尚未覆核
			svo.setAPPLY_STATUS(APPLY_STATUS_TERMINATE_REVIEW); //終止待覆核("4")
			svo.setHIGHEST_AUTH_LV("1"); //終止議價覆核僅需一階主管覆核。
			dam.update(svo);

			//==== for CBS 模擬日測試用========
			setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", inputVO.getSeq());
			//================================================================

			mgrEmpID_1 = svo.getMGR_EMP_ID_1();

		} else if (null != pvo) {
			if (chkIsUHRM()) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				
				sb.append("SELECT BRANCH_NBR ");
				sb.append("FROM TBORG_UHRM_BRH ");
				sb.append("WHERE EMP_ID = :loginID ");

				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);
				
				if (loginBreach.size() > 0) {
					pvo.setC_BRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
				} else {
					throw new APException("人員無有效分行"); //顯示錯誤訊息
				}
			} else {
				pvo.setC_BRANCH_NBR((String) SysInfo.getInfoValue(SystemVariableConsts.LOGINBRH));
			}
			
			pvo.setHIGHEST_AUTH_LV("1"); //終止議價覆核僅需一階主管覆核。
			pvo.setAUTH_STATUS("0"); //尚未覆核
			pvo.setAPPLY_STATUS(APPLY_STATUS_TERMINATE_REVIEW); //終止待覆核("4");

			//重抓一次現職的一階主管，以防主管有異動。
			pvo.setMGR_EMP_ID_1(getReviewLevelEmpList(pvo.getCUST_ID(), "1", pvo.getAPPLY_TYPE(), pvo.getCreator(), pvo.getC_BRANCH_NBR(), pvo.getAPPLE_SETUP_TYPE()));

			dam.update(pvo);

			//==== for CBS 模擬日測試用========
			setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", inputVO.getSeq());
			//================================================================

			mgrEmpID_1 = pvo.getMGR_EMP_ID_1();
		}

		TBCRM_BRG_APPROVAL_HISTORYVO hvo = new TBCRM_BRG_APPROVAL_HISTORYVO();
		hvo.setAPPROVAL_SEQ(new BigDecimal(new SimpleDateFormat("yyyyMMdd").format(new Date()) + getSeqNum(sn, "SQ_TBCRM_BRG_APPROVAL_HISTORY", "00000000000", new java.sql.Timestamp(System.currentTimeMillis()), 1, new Long("99999999999"), "y", new Long("0")))); //簽核歷程編號
		hvo.setAPPLY_SEQ(inputVO.getSeq()); //議價編號
		hvo.setAPPLY_CAT(inputVO.getApply_cat()); //類別 1:期間 2:單次
		hvo.setTERMINATE_REASON(inputVO.getTerminateReason()); //終止原因

		dam.create(hvo);

		//要發M+的主管ID
		if (StringUtils.isNotBlank(mgrEmpID_1) && StringUtils.isNotBlank(custID)) {
			CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");
			//如果都沒值的取系統變數登入者ID 2017-8-26 by Jacky
			if (StringUtils.isBlank(creator)) {
				creator = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
			}
			if (null != svo) {
				crm451.pushAuthMessage(mgrEmpID_1, "single", custID, creator, null, "0", (StringUtils.equals(inputVO.getApply_cat(), "2") ? svo.getC_BRANCH_NBR() : pvo.getC_BRANCH_NBR()));
			} else if (null != pvo) {
				crm451.pushAuthMessage(mgrEmpID_1, "period", custID, creator, null, "0", (StringUtils.equals(inputVO.getApply_cat(), "2") ? svo.getC_BRANCH_NBR() : pvo.getC_BRANCH_NBR()));
			}
		}
	}

	/*
	 * 終止後申請
	 *
	 * 2016-12-14 modify by ocean
	 *
	 */
	public void terminateAndApply (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		CRM421InputVO inputVO = (CRM421InputVO) body;

		String sn = "";

		if (StringUtils.equals(inputVO.getApply_type(), "1")) {
			sn = getPeriod_Fund_SN();
		} else if (StringUtils.equals(inputVO.getApply_type(), "2")) {
			sn = getPeriod_ETF_SN();
		} else {
			sn = "";
		}

		inputVO.setTerminateSEQ(sn);

		terminate(inputVO);
		sendRtnObject(applyPeriod(inputVO));
	}

	/*
	 * 修改
	 *
	 * 2016-10-11 add by walala
	 * 2016-12-27 modify by ocean
	 *
	 */
	public void updateApplyFlow (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();

		TBCRM_BRG_APPLY_SINGLEVO vo = new TBCRM_BRG_APPLY_SINGLEVO();
		vo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, inputVO.getSeq());

		String a = "1236";
		String b = "45";
		if (vo != null) {
			if (a.contains(inputVO.getApply_type())) {
				SOT709InputVO inputVO_709 = new SOT709InputVO();
				SOT709OutputVO outputVO_709 = new SOT709OutputVO();

				inputVO_709.setApplySeq(vo.getAPPLY_SEQ());
				inputVO_709.setCustId(vo.getCUST_ID());
				inputVO_709.setProdId(inputVO.getProd_id());
				inputVO_709.setPurchaseAmt(new BigDecimal(inputVO.getPurchase_amt()));
				inputVO_709.setTrustCurr(inputVO.getTrust_curr());
				inputVO_709.setFee_rate(inputVO.getFee_rate());
				inputVO_709.setDynamicYN(StringUtils.equals("6", inputVO.getApply_type()) ? "Y" : "");

				SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
				if (StringUtil.isEqual(inputVO.getApply_type(), "1") || StringUtil.isEqual(inputVO.getApply_type(), "3") || StringUtil.isEqual(inputVO.getApply_type(), "6")) {
					outputVO_709 = sot709.singleBargainModify(inputVO_709);
				} else if (StringUtil.isEqual(inputVO.getApply_type(), "2")) {
					inputVO_709.setProCode(inputVO.getFee_discount().toString());
					outputVO_709 = sot709.singleRegBargainModify(inputVO_709);
				}

				if (StringUtils.isNotBlank(outputVO_709.getErrorMsg())) {
					outputVO.setErrorMsg(outputVO_709.getErrorMsg());
				} else {
					updateApply(dam, inputVO, vo);
				}
			} else if (b.contains(inputVO.getApply_type())) {
				SOT710InputVO inputVO_710 = new SOT710InputVO();
				SOT710OutputVO outputVO_710 = new SOT710OutputVO();

				inputVO_710.setApplySEQ(vo.getAPPLY_SEQ());
				inputVO_710.setCustID(vo.getCUST_ID());
				inputVO_710.setProdId(inputVO.getProd_id());
				inputVO_710.setApplyType(inputVO.getApply_type());
				inputVO_710.setEntrustUnit(new BigDecimal(inputVO.getEntrust_unit()));
				inputVO_710.setEntrustPrice(new BigDecimal(inputVO.getEntrust_amt()));
				inputVO_710.setTradeDate(sdfYYYYMMDD.format(new Date()));
				inputVO_710.setFeeRate(inputVO.getFee_rate());
				inputVO_710.setFeeDiscount(inputVO.getFee_discount());
				inputVO_710.setTrustCurrType(inputVO.getTrustCurrType());
				inputVO_710.setDiscountType(inputVO.getDiscount_type());

				SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
				outputVO_710 = sot710.singleBargainModify(inputVO_710);

				if (StringUtils.isNotBlank(outputVO_710.getErrorTxt())) {
					outputVO.setErrorMsg(outputVO_710.getErrorTxt());
				} else {
					updateApply(dam, inputVO, vo);
				}
			}
		} else {
			throw new APException("ehl_01_common_005"); // 顯示資料不存在
		}

		this.sendRtnObject(outputVO);
	}

	/*
	 * 修改(共用)
	 *
	 * 2016-12-28 add by ocean
	 *
	 */
	public void updateApply (DataAccessManager dam, CRM421InputVO inputVO, TBCRM_BRG_APPLY_SINGLEVO vo) throws JBranchException, Exception {

		vo.setPROD_ID(inputVO.getProd_id());
		vo.setPROD_NAME(inputVO.getProd_name());
		vo.setTRUST_CURR(inputVO.getTrust_curr());
		vo.setTRUST_CURR_TYPE(inputVO.getTrustCurrType());
		vo.setPURCHASE_AMT(getBigDecimal(inputVO.getPurchase_amt()));
		vo.setENTRUST_UNIT(getBigDecimal(inputVO.getEntrust_unit()));
		vo.setENTRUST_AMT(getBigDecimal(inputVO.getEntrust_amt()));
		vo.setDEFAULT_FEE_RATE(inputVO.getDefaultFeeRate());
		vo.setFEE_RATE(inputVO.getFee_rate());

		if (StringUtil.isEqual(inputVO.getApply_type(), "2")) {
			Map<String, String> gmRoleMap = new XmlInfo().doGetVariable("CRM.SINGLE_REG_DISCOUNT", FormatHelper.FORMAT_3);
			vo.setFEE_DISCOUNT(new BigDecimal(gmRoleMap.get(String.valueOf(inputVO.getFee_discount()))));
		} else {
			vo.setFEE_DISCOUNT(inputVO.getFee_discount());
		}

		vo.setFEE(getBigDecimal(inputVO.getFee()));
		vo.setDISCOUNT_TYPE(inputVO.getDiscount_type());

		// 議價 For CBS 環境先用模擬日代替
//			vo.setAPPLY_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
		long fakeSysdate = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime();
		vo.setAPPLY_DATE(new Timestamp(fakeSysdate));
		// ===========================================================================//
		dam.update(vo);

		// 議價 For CBS 環境先用模擬日代替
		setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", inputVO.getSeq());
		// ===========================================================================//
	}

	/** 如果模擬日有開啟，則使用模擬日 (for 測試環境)  **/
	private void setfakeSysdate(String table, String seq) throws JBranchException {
		if (cbsservice.isEnabledSimulatedDate()) {
			Manager.manage(getDataAccessManager())
					.append("update " + table + " ")
					.append("set LASTUPDATE = to_date(:fake, 'yyyymmddhh24miss') ")
					.append("where APPLY_SEQ = :seq ")
					.put("fake", cbsservice.getCBSTestDate())
					.put("seq", seq)
					.update();
		}
	}

	/*
	 * 取得商品資訊
	 *
	 * 2016-12-05 modify by ocean : add 適配
	 *
	 */
	public void prod_inquire (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		SOT701OutputVO outputVO_701 = getSOTCustInfo(inputVO);

		String a = "1236";
		String b = "45";
		if (a.contains(inputVO.getApply_type())) {
			boolean SOT704Status = false;

			// 1.適配
			if (isFitness(inputVO.getApply_type())) {
				// 1.適配，由商品查詢取得，邏輯需一致
				PRD110OutputVO prdOutputVO = new PRD110OutputVO();
				PRD110InputVO prdInputVO = new PRD110InputVO();
				prdInputVO.setCust_id(inputVO.getCust_id().toUpperCase());
				prdInputVO.setType("4");
				prdInputVO.setFund_id(inputVO.getProd_id());
				//動態鎖利
				if(StringUtils.equals("6", inputVO.getApply_type())) prdInputVO.setDynamicType("M");
				
				PRD110 prd110 = (PRD110) PlatformContext.getBean("prd110");
				prdOutputVO = prd110.inquire(prdInputVO);

				if (CollectionUtils.isNotEmpty(prdOutputVO.getResultList())) {
					String warningMsg = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("warningMsg");
					String errId = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("errorID");

					if (StringUtils.isBlank(errId)) {
						SOT704Status = true;
					}

					outputVO.setErrorMsg(errId);
				}
			} else {
				SOT704Status = true;
			}

			// 2.查詢商品主檔
			if (SOT704Status) {
				sb.append("SELECT FUND.PRD_ID, FUND.FUND_CNAME, FUND.CURRENCY_STD_ID, FUND.RISKCATE_ID, INFO.FUS20 AS TRUST_CURR_TYPE ");
				sb.append("FROM TBPRD_FUND FUND, TBPRD_FUNDINFO INFO ");
				sb.append("WHERE FUND.PRD_ID = INFO.PRD_ID ");
				sb.append("AND FUND.PRD_ID = :prodID ");
				queryCondition.setObject("prodID", inputVO.getProd_id());
				queryCondition.setQueryString(sb.toString());

				outputVO.setProdList(dam.exeQuery(queryCondition));
			}
		} else if (b.contains(inputVO.getApply_type())) {
			sb = new StringBuffer();
			sb.append("SELECT PTYPE, PRD_ID, PNAME ");
			sb.append("FROM VWPRD_MASTER ");
			sb.append("WHERE PRD_ID = :prod_id ");
			sb.append("AND PTYPE in ('ETF', 'STOCK') ");
			queryCondition.setObject("prod_id", inputVO.getProd_id());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> pTypeList = dam.exeQuery(queryCondition);

			if (pTypeList.size() > 0) { // 區分 ETF 或 STOCK
				String pType = (String) pTypeList.get(0).get("PTYPE");

				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("SELECT PROD.PRD_ID, PROD.").append(pType).append("_CNAME AS PROD_NAME, PROD.CURRENCY_STD_ID, PROD.RISKCATE_ID, ");
				sb.append("PROD.MARKET_PRICE, PROD.TXN_AMOUNT, PROD.TXN_UNIT, PROD.TRADING_UNIT, ");
				sb.append("PROD.STOCK_CODE, NREXGFL.COUNTRY_CODE, ");
				sb.append("CLOS_P.SOU_DATE, CLOS_P.CUR_AMT ");
				sb.append("FROM TBPRD_").append(pType).append(" PROD ");
				sb.append("LEFT JOIN TBPRD_STOCK_NREXGFL NREXGFL ON PROD.STOCK_CODE = NREXGFL.STOCK_CODE ");
				sb.append("LEFT JOIN ( ");
				sb.append("SELECT PRICE.PRODUCT_NO, MAX_SOU.SOU_DATE, PRICE.CUR_AMT ");
				sb.append("FROM TBPRD_ETF_CLOSING_PRICE PRICE ");
				sb.append("LEFT JOIN (SELECT PRODUCT_NO, MAX(SOU_DATE) AS SOU_DATE FROM TBPRD_ETF_CLOSING_PRICE GROUP BY PRODUCT_NO) MAX_SOU ON MAX_SOU.PRODUCT_NO = PRICE.PRODUCT_NO AND MAX_SOU.SOU_DATE = PRICE.SOU_DATE ");
				sb.append(") CLOS_P ON CLOS_P.PRODUCT_NO = PROD.PRD_ID ");
				sb.append("WHERE PRD_ID = :prodID ");
				queryCondition.setObject("prodID", inputVO.getProd_id());
				queryCondition.setQueryString(sb.toString());

				outputVO.setProdList(dam.exeQuery(queryCondition));
			}
		}

		sendRtnObject(outputVO);
	}

	/*
	 * 取得levelNO的覆核人員
	 *
	 * custID : 客戶ID
	 * levelNO : 覆核層級
	 * prodType	: 商品類別
	 *
	 * 2016-12-01 add by ocean
	 *
	 */
	public String getReviewLevelEmpList (String custID, String levelNO, String prodType, String creator, String c_branchNbr, String setupType) throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT CASE WHEN REPLACE(R.SYS_ROLE, '_ROLE', '') <> 'HEADMGR' THEN DTL2.REGION_CENTER_ID ELSE NULL END AS REGION_CENTER_ID, ");
		sb.append("         CASE WHEN REPLACE(R.SYS_ROLE, '_ROLE', '') NOT IN ('HEADMGR', 'ARMGR') THEN DTL2.BRANCH_AREA_ID ELSE NULL END AS BRANCH_AREA_ID, ");
		sb.append("         CASE WHEN REPLACE(R.SYS_ROLE, '_ROLE', '') NOT IN ('HEADMGR', 'ARMGR', 'MBRMGR') THEN DTL2.BRANCH_NBR ELSE NULL END AS BRANCH_NBR, ");
		sb.append("         R.ROLE_ID, ");
		sb.append("         DTL2.SETUP_TYPE ");
		sb.append("  FROM TBORG_ROLE R, ");
		sb.append("       (SELECT DTL.REGION_CENTER_ID, DTL.BRANCH_AREA_ID, DTL.BRANCH_NBR, PRI.ROLEID, DTL.SETUP_TYPE ");
		sb.append("        FROM TBSYSSECUROLPRIASS PRI, ");
		sb.append("             (SELECT CUST_ID, CON_DEGREE, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, ");
		sb.append("                     PROD_TYPE, LEVEL_NO, DISCOUNT, REGEXP_SUBSTR(PAR.ROLE_LIST,'[^,]+', 1, TEMP_T.LEV) ROLE_LIST, SETUP_TYPE ");
		sb.append("              FROM ( ");
		sb.append("                SELECT CUST.CUST_ID, CUST.CON_DEGREE, ");
		sb.append("                       RC.DEPT_ID AS REGION_CENTER_ID, OP.DEPT_ID AS BRANCH_AREA_ID, BR.DEPT_ID AS BRANCH_NBR, ");
		sb.append("                       BRG.PROD_TYPE, BRG.LEVEL_NO, BRG.DISCOUNT, BRG.ROLE_LIST, BRG.SETUP_TYPE ");
		sb.append("                FROM TBCRM_CUST_MAST CUST ");
		sb.append("                LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND BR.DEPT_ID = :creatorBranchNbr ");
		sb.append("                LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND (BR.PARENT_DEPT_ID = OP.DEPT_ID OR OP.DEPT_ID = :creatorBranchNbr) ");
		sb.append("                LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND (OP.PARENT_DEPT_ID = RC.DEPT_ID OR RC.DEPT_ID = :creatorBranchNbr) ");
		sb.append("                LEFT JOIN TBCRM_BRG_SETUP BRG ON BRG.CON_DEGREE = NVL(CUST.CON_DEGREE, 'OTH') ");
		sb.append("                WHERE CUST.CUST_ID = :custID ");
		sb.append("                AND BRG.LEVEL_NO = :levelNO ");
		sb.append("                AND BRG.PROD_TYPE = :prodType ");
		sb.append("              ) PAR ");
		sb.append("              OUTER APPLY ( ");
		sb.append("                SELECT LEVEL AS LEV ");
		sb.append("                FROM DUAL ");
		sb.append("                CONNECT BY LEVEL <= REGEXP_COUNT(PAR.ROLE_LIST,',') + 1 ");
		sb.append("              ) TEMP_T ");
		sb.append("             ) DTL ");
		sb.append("        WHERE PRI.PRIVILEGEID = DTL.ROLE_LIST ");
		sb.append("       ) DTL2 ");
		sb.append("  WHERE INSTR((DTL2.ROLEID), R.ROLE_ID) > 0 ");
		sb.append(") ");

		sb.append("SELECT LISTAGG(MEM.EMP_ID, ',') WITHIN GROUP (ORDER BY MEM.EMP_ID) AS EMP_ID, BASE.SETUP_TYPE ");
		sb.append("FROM BASE ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT EMP_ID, ROLE_ID, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_DEPT_ID ");
		sb.append("  FROM VWORG_EMP_INFO ");
		sb.append(") MEM ");
		sb.append("ON MEM.ROLE_ID = BASE.ROLE_ID ");
		sb.append("WHERE MEM.EMP_ID IS NOT NULL ");
		if (StringUtils.equals("0", setupType)) {
			sb.append("AND (MEM.REGION_CENTER_ID = BASE.REGION_CENTER_ID OR (MEM.REGION_CENTER_ID IS NULL AND BASE.REGION_CENTER_ID IS NULL)) ");
			sb.append("AND (MEM.BRANCH_AREA_ID = BASE.BRANCH_AREA_ID OR (MEM.BRANCH_AREA_ID IS NULL AND BASE.BRANCH_AREA_ID IS NULL)) ");
			sb.append("AND (MEM.BRANCH_NBR = BASE.BRANCH_NBR OR (MEM.BRANCH_NBR IS NULL AND BASE.BRANCH_NBR IS NULL)) ");
		} else {
			sb.append("AND BASE.SETUP_TYPE = '9' ");
			sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER M WHERE M.EMP_ID = :creator AND MEM.EMP_DEPT_ID = M.DEPT_ID) ");

			queryCondition.setObject("creator", creator);
		}
		sb.append("GROUP BY BASE.SETUP_TYPE ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("levelNO", StringUtils.isNotBlank(levelNO) ? levelNO : (StringUtils.equals("9", setupType) ? 1 : levelNO));
		queryCondition.setObject("prodType", prodType);
		queryCondition.setObject("creatorBranchNbr", c_branchNbr);

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return (list.size() > 0 ? (String) list.get(0).get("EMP_ID") : "");
	}

	/*
	 * 取得期間議價(NF)的SEQNO
	 *
	 * 2016-10-11 add by walala
	 * 2016-12-01 review code by ocean
	 *
	 */
	private String getPeriod_Fund_SN () throws JBranchException {

		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Date date = new Date();
		String Year = Integer.parseInt(sdf.format(date)) - 1911 + "";
		sdf = new SimpleDateFormat("MMdd");
		String monthday = sdf.format(date);

		try {
			seqNum = Year + monthday + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_BRG_APPLY_PERIOD")));
		} catch (Exception e) {
			sn.createNewSerial("TBCRM_BRG_APPLY_PERIOD", "0000", 1, "d", null, 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = Year + monthday + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_BRG_APPLY_PERIOD")));
		}

		return seqNum;
	}

	/*
	 * 取得期間議價(ETF)的SEQNO
	 *
	 * 2016-10-11 add by walala
	 * 2016-12-01 review code by ocean
	 *
	 */
	private String getPeriod_ETF_SN () throws JBranchException {

		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());

		try {
			seqNum = date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_BRG_APPLY_PERIOD")));
		} catch (Exception e) {
			sn.createNewSerial("TBCRM_BRG_APPLY_PERIOD", "0000", 1, "d", null, 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_BRG_APPLY_PERIOD")));
		}

		return seqNum;
	}

	/*
	 * 取得單筆議價(NF)的SEQNO
	 *
	 * 2016-10-11 add by walala
	 * 2016-12-01 review code by ocean
	 *
	 */
	private String getSingle_Fund_SN (String mark) throws JBranchException {

		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Date date = new Date();
		String Year = Integer.parseInt(sdf.format(date)) - 1911 + "";
		sdf = new SimpleDateFormat("MMdd");
		String monthday = sdf.format(date);

		try {
			seqNum = mark + StringUtils.substring(Year, 1, Year.length()) + monthday + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_BRG_APPLY_SINGLE")));
		} catch (Exception e) {
			sn.createNewSerial("TBCRM_BRG_APPLY_SINGLE", "0000", 1, "d", null, 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = mark + StringUtils.substring(Year, 1, Year.length()) + monthday + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_BRG_APPLY_SINGLE")));
		}

		return seqNum;
	}

	/*
	 * 取得單筆議價(ETF)的SEQNO
	 *
	 * 2016-10-11 add by walala
	 * 2016-12-01 review code by ocean
	 *
	 */
	private String getSingle_ETF_SN (String mark) throws JBranchException {

		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());

		try {
			seqNum = mark + StringUtils.substring(date, 1, date.length()) + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_BRG_APPLY_SINGLE")));
		} catch (Exception e) {
			sn.createNewSerial("TBCRM_BRG_APPLY_SINGLE", "0000", 1, "d", null, 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = mark + StringUtils.substring(date, 1, date.length()) + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_BRG_APPLY_SINGLE")));
		}

		return seqNum;
	}

	/*
	 * 取得getFC032675Data(共用)
	 *
	 * 2016-12-29 add by ocean
	 *
	 */
	public FP032675DataVO getFC032675Data (CRM421InputVO inputVO) throws Exception {

		SOT701InputVO inputVO_701 = new SOT701InputVO();
		inputVO_701.setCustID(inputVO.getCust_id());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");

		return sot701.getFP032675Data(inputVO_701);
	}

	/*
	 * 取得isCustStackholder(共用)
	 *
	 * 2016-12-29 add by ocean
	 *
	 */
	public Boolean isCustStackholder (CRM421InputVO inputVO) throws Exception {

		SOT701InputVO inputVO_701 = new SOT701InputVO();
		inputVO_701.setCustID(inputVO.getCust_id());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");

		return sot701.isCustStakeholder(inputVO_701);
	}

	/*
	 * 取得getCustAcctData(共用)
	 *
	 * 2016-12-29 add by ocean
	 *
	 */
	public CustAcctDataVO getCustAcctData (CRM421InputVO inputVO) throws Exception {

		SOT701InputVO inputVO_701 = new SOT701InputVO();
		inputVO_701.setCustID(inputVO.getCust_id());
		inputVO_701.setProdType("2");
		inputVO_701.setTradeType("1");
		inputVO_701.setIsOBU(isCustStackholder(inputVO) ? "Y" : "N"); //發電文檢核利害關係人身份

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");

		return sot701.getCustAcctData(inputVO_701);
	}

	/*
	 * 取得isCustStackholder(共用)
	 *
	 * 2016-12-29 add by ocean
	 *
	 */
	public SOT701OutputVO getSOTCustInfo (CRM421InputVO inputVO) throws Exception {

		SOT701InputVO inputVO_701 = new SOT701InputVO();

		inputVO_701.setCustID(inputVO.getCust_id());
		inputVO_701.setProdType(("1236".contains(inputVO.getApply_type())) ? "1" : "2");
		inputVO_701.setTradeType("1");

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");

		return sot701.getSOTCustInfo(inputVO_701);
	}

	public String getHightLevelName (String highestAuthLV) throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if (StringUtils.isNotBlank(highestAuthLV)) {
			sb.append("SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) ");
			sb.append("FROM TBSYSPARAMETER ");
			sb.append("WHERE PARAM_TYPE = '").append((chkIsUHRM() ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV")).append("'||:highestAuthLV ");

			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("highestAuthLV", highestAuthLV);

			list = dam.exeQuery(queryCondition);
		}

		return (list.size() > 0 ? (String) list.get(0).get("AUTH_LEVEL") : null);
	}

	/*
	 * 平台序號
	 *
	 * 2016-12-30 add by ocean
	 *
	 */
	private String getSeqNum (SerialNumberUtil sn, String TXN_ID, String format, Timestamp timeStamp, Integer minNum, Long maxNum, String status, Long nowNum) throws JBranchException {

		String seqNum = "";

		try {
			seqNum = sn.getNextSerialNumber(TXN_ID);
		} catch (Exception e) {
			sn.createNewSerial(TXN_ID, format, 1, "d", timeStamp, minNum, maxNum, status, nowNum, null);
			seqNum = sn.getNextSerialNumber(TXN_ID);
		}

		return seqNum;
	}

	/*
	 * 轉Decimal
	 *
	 * 2016-10-11 add by walala
	 * 2016-12-01 review code by ocean
	 *
	 */
	public BigDecimal getBigDecimal (Object value) {

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

	private String addZeroForNum (String str, int strLength) {

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

	//查詢客戶是否有家庭戶
	public void checkCustPrv (Object body, IPrimitiveMap header) throws JBranchException {
		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT * FROM TBCRM_CUST_PRV WHERE CUST_ID_M = ( ");
		sql.append("SELECT CUST_ID_M FROM TBCRM_CUST_PRV WHERE CUST_ID_S = :cust_id ");
		sql.append("AND PRV_MBR_YN = 'Y' ) AND PRV_MBR_YN = 'Y' ");

		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);

		sendRtnObject(outputVO);
	}

	//查詢所有家庭戶之有效期間議價(基金/ETF)_電文
	public void getFamilyPeriod (Object body, IPrimitiveMap header) throws Exception {
		CRM421InputVO inputVO = (CRM421InputVO) body;
		CRM421OutputVO outputVO = new CRM421OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			Date sysdate = DateUtils.truncate(new Date(), Calendar.DATE);
			dam = this.getDataAccessManager();

			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT PRV.CUST_ID_S, PRV.PRV_MBR_MAST_YN, ");
			sql.append("CUST.CUST_NAME, CUST.AO_CODE, ORG.EMP_NAME FROM ( ");
			sql.append("SELECT CUST_ID_S, PRV_MBR_MAST_YN FROM TBCRM_CUST_PRV WHERE CUST_ID_M = ( ");
			sql.append("SELECT CUST_ID_M FROM TBCRM_CUST_PRV WHERE CUST_ID_S = :cust_id ");
			sql.append("AND PRV_MBR_YN = 'Y' ) AND PRV_MBR_YN = 'Y' ) PRV ");
			sql.append("LEFT JOIN TBCRM_CUST_MAST CUST ON PRV.CUST_ID_S = CUST.CUST_ID ");
			sql.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CUST.AO_CODE = AO.AO_CODE ");
			sql.append("LEFT JOIN TBORG_MEMBER ORG ON AO.EMP_ID = ORG.EMP_ID ");
			sql.append("ORDER BY PRV_MBR_MAST_YN, CUST_ID_S ");

			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			for (Map<String, Object> map : list) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				String cust_id = map.get("CUST_ID_S").toString();

				tempMap.put("PRV_MBR_MAST_YN", map.get("PRV_MBR_MAST_YN")); //主從戶註記
				tempMap.put("CUST_ID", cust_id); //客戶ID
				tempMap.put("CUST_NAME", map.get("CUST_NAME")); //客戶姓名
				tempMap.put("AO_CODE", map.get("AO_CODE")); //AO Code
				tempMap.put("EMP_NAME", map.get("EMP_NAME")); //理專姓名

				//取得基金期間議價資料
				SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
				SOT709InputVO inputVO_709 = new SOT709InputVO();
				inputVO_709.setCustId(cust_id);
				inputVO_709.setType("3");
				SOT709OutputVO nfee002OutputVO = new SOT709OutputVO();
				nfee002OutputVO = sot709.getPeriodFeeRate(inputVO_709);
				List<com.systex.jbranch.app.server.fps.sot709.PeriodFeeRateVO> nfee002CList = nfee002OutputVO.getPeriodFeeRateList();

				if (CollectionUtils.isNotEmpty(nfee002CList)) {
					for (PeriodFeeRateVO vo : nfee002CList) {
						//		            	Map<String, Object> tempMap = new HashMap<String, Object>();
						Date beginDate = DateUtils.truncate(vo.getBrgBeginDate(), Calendar.DATE);
						Date endDate = DateUtils.truncate(vo.getBrgEndDate(), Calendar.DATE);

						//有覆核日期表示已覆核，已覆核資料才算有效
						//判斷系統日是否介於適用優惠期間起日和適用優惠期間迄日,若符合則回傳適用優惠期間迄日
						if (vo.getAuthDate() != null && beginDate.compareTo(sysdate) <= 0 && endDate.compareTo(sysdate) >= 0) {
							//		                	tempMap.put("CUST_ID", cust_id);					//客戶ID
							//		                	tempMap.put("APPLY_TYPE", "1");						//申請類別 1：基金 2：海外ETF/股票
							tempMap.put("BRG_BEGIN_DATE", beginDate); //優惠期間(起)
							tempMap.put("BRG_END_DATE", endDate); //優惠期間(迄)
							tempMap.put("DMT_STOCK", vo.getDmtStock()); //國內股票型
							tempMap.put("FRN_STOCK", vo.getFrnStock()); //國外股票型
							tempMap.put("DMT_BOND", vo.getDmtBond()); //國內債券型
							tempMap.put("FRN_BOND", vo.getFrnBond()); //國外債券型
							tempMap.put("DMT_BALANCED", vo.getDmtBalanced()); //國內平衡型
							tempMap.put("FRN_BALANCED", vo.getFrnBalanced()); //國外平衡型

							//		                	resultList.add(tempMap);
						}
					}
				}
				//取得海外ETF/股票期間議價資料
				SOT710 sot710 = (SOT710) PlatformContext.getBean("sot710");
				SOT710InputVO inputVO_710 = new SOT710InputVO();
				//startDate為系統日–1年
				Calendar startCal = Calendar.getInstance();
				startCal.add(Calendar.YEAR, -1);
				Date startDate = startCal.getTime();
				//endDate為系統日
				Date endDate = new Date();
				inputVO_710.setCustID(cust_id);
				inputVO_710.setStartDate(startDate);
				inputVO_710.setEndDate(endDate);
				SOT710OutputVO nrbrvc3OutputVO = new SOT710OutputVO();
				nrbrvc3OutputVO = sot710.getPeriodFeeRate(inputVO_710);
				List<com.systex.jbranch.app.server.fps.sot710.PeriodFeeRateVO> nrbrvc3CList = nrbrvc3OutputVO.getPeriodFeeRateList();

				if (CollectionUtils.isNotEmpty(nrbrvc3CList)) {
					for (com.systex.jbranch.app.server.fps.sot710.PeriodFeeRateVO vo : nrbrvc3CList) {
						//		            	Map<String, Object> tempMap = new HashMap<String, Object>();
						Date begin_Date = DateUtils.truncate(vo.getBrgBeginDate(), Calendar.DATE);
						Date end_Date = DateUtils.truncate(vo.getBrgEndDate(), Calendar.DATE);

						//有覆核日期表示已覆核，已覆核資料才算有效
						//判斷系統日是否介於適用優惠期間起日和適用優惠期間迄日,若符合則回傳適用優惠期間迄日
						if (vo.getAuthDate() != null && begin_Date.compareTo(sysdate) <= 0 && end_Date.compareTo(sysdate) >= 0) {
							//		                	tempMap.put("CUST_ID", cust_id);					//客戶ID
							//		                	tempMap.put("APPLY_TYPE", "2");						//申請類別 1：基金 2：海外ETF/股票
							tempMap.put("ETF_BRG_BEGIN_DATE", begin_Date); //優惠期間(起)
							tempMap.put("ETF_BRG_END_DATE", end_Date); //優惠期間(迄)
							tempMap.put("BUY_HK_MRK", vo.getBuyHKMrk()); //買入香港交易所
							tempMap.put("SELL_HK_MRK", vo.getSellHKMrk()); //賣出香港交易所
							tempMap.put("BUY_US_MRK", vo.getBuyUSMrk()); //買入美國交易所
							tempMap.put("SELL_US_MRK", vo.getSellUSMrk()); //賣出美國交易所
							tempMap.put("BUY_UK_MRK", vo.getBuyUKMrk()); //買入倫敦交易所
							tempMap.put("SELL_UK_MRK", vo.getSellUKMrk()); //賣出倫敦交易所
							tempMap.put("BUY_JP_MRK", vo.getBuyJPMrk()); //買入日本交易所
							tempMap.put("SELL_JP_MRK", vo.getSellJPMrk()); //賣出日本交易所
							//		                	resultList.add(tempMap);
						}
					}
				}

				resultList.add(tempMap);
			}
			outputVO.setResultList(resultList);
		}
		sendRtnObject(outputVO);
	}
}