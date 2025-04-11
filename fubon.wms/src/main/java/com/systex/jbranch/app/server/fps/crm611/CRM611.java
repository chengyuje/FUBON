package com.systex.jbranch.app.server.fps.crm611;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.FP032151Service;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputDetailsVO;
import com.systex.jbranch.fubon.webservice.rs.SeniorCitizenClientRS;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @Description :
 * @Author : 20160527 moron
 * @Editor : 20161116 Stella : getCust 新增消金人員查詢結果欄位
 * @Editor : 20220523 ocean  : WMS-CR-20220517-01_新增實動戶上傳註記
 * @Editor : 20240201 SamTu  : #1890: 客戶首頁 - 提醒視窗增加失聯戶提示訊息
 */

@Component("crm611")
@Scope("request")
public class CRM611 extends EsbUtil {
	
	@Autowired
	CBSService cbsservice;
	
	@Autowired
	private FP032151Service fp032151Service;
	
	private DataAccessManager dam = null;

	private String apiParam = "SYS.SENIOR_CITIZEN_URL";
	
	public void getCust(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		CRM611InputVO inputVO = (CRM611InputVO) body;
		CRM611OutputVO return_VO = new CRM611OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT ");

		/** 客戶資料 **/
		sql.append("	   TCM.CUST_ID, ");
		sql.append(" 	   TCM.CUST_NAME, ");
		sql.append(" 	   TCM.BIRTH_DATE, ");
		sql.append("       CASE WHEN ROUND(MONTHS_BETWEEN(SYSDATE, TCM.BIRTH_DATE) / 12, 2) >= 64.5 THEN 'Y' ELSE 'N' END AS SENIOR_CITIZEN_CUST, ");
		sql.append(" 	   TCM.AGE, ");
		sql.append(" 	   TCM.GENDER, ");
		sql.append(" 	   TCM.SAL_COMPANY, ");
		sql.append("	   TCM.CON_DEGREE, ");
		sql.append(" 	   AO.EMP_NAME, ");
		sql.append(" 	   TCM.VIP_DEGREE, ");
		sql.append(" 	   TCM.AO_CODE, ");
		
		/** AO CODE類型：1主 2副 3維護 **/
		sql.append("	   CASE WHEN AO.TYPE = '1' THEN '(主)' "); 		
		sql.append("	        WHEN AO.TYPE = '2' THEN '(副)' "); 										
		sql.append("	        WHEN AO.TYPE = '3' THEN '(維護)'"); 										
		sql.append("	   ELSE '' END AS C_TYPE_NAME , ");
		
		sql.append(" 	   TCM.BRA_NBR, ");
		sql.append(" 	   INFO.BRANCH_NBR, ");
		sql.append(" 	   INFO.BRANCH_NAME, ");
		sql.append(" 	   AO.BRA_NBR AS AO_BRA_NBR, ");
		sql.append(" 	   AO.BRANCH_NAME AS AO_BRANCH_NAME, ");
		sql.append(" 	   DEFN.DEPT_NAME, ");
		
		/** 客戶註記 **/
		sql.append(" 	   TCN.CO_ACCT_YN, ");
		sql.append(" 	   TCN.SAL_ACC_YN, ");
		sql.append(" 	   CONCAT(TCN.RM_ID, TCN.RM_NAME) AS CO_RM, ");
		sql.append(" 	   TCN.COMM_NS_YN, ");
		sql.append(" 	   TCN.COMM_RS_YN, ");
		sql.append(" 	   TCN.COMPLAIN_YN, ");
		sql.append(" 	   TCN.SP_CUST_YN, ");
		sql.append(" 	   (CASE WHEN TCN.COMM_NS_YN = 'Y' THEN 'NS' WHEN TCN.COMM_RS_YN = 'Y' THEN 'RS' WHEN TCN.SP_CUST_YN = 'Y' THEN '特定註記' END) AS CUST_NOTE, ");
		sql.append(" 	   TCM.AUM_AMT, ");
		sql.append(" 	   TCN.INV_TRST_YN, ");
		
		/** KYC **/
		sql.append(" 	   EXAM.CUST_RISK_AFR, ");
		sql.append(" 	   EXAM.EXPIRY_DATE as KYC_EXPIRY_DATE, ");
		sql.append(" 	   CASE WHEN TRUNC(EXAM.EXPIRY_DATE) >= TRUNC(SYSDATE) THEN 'Y' ELSE 'N' END AS KYC_EXPIRY_YN, ");
		
		/** 其它 **/
		sql.append("	   para1.PARAM_NAME as COUNTRY_NAME, ");
		sql.append(" 	   TCM.ANNUAL_INCOME_AMT, ");
		
		/** UHRM **/
		sql.append("	   CASE WHEN UHRM.EMP_ID IS NOT NULL THEN UHRM.EMP_ID ELSE NULL END AS UEMP_ID, ");
		sql.append(" 	   CASE WHEN UHRM.EMP_ID IS NOT NULL THEN AO.EMP_NAME || (CASE WHEN AO.TYPE = '1' THEN '(計績)' WHEN AO.TYPE = '3' THEN '(維護)' ELSE '' END) ELSE NULL END AS UEMP_NAME, "); 
		
		/** 投資比重與KYC資產配直佔比(Y:超過/N:未超過) **/
		sql.append("	   AMT_KYC.AMT_KYC_FLAG, ");
		
		/** WMS-CR-20250124-01_新增客戶ROA資訊相關功能 **/
		sql.append("	   ROA.ROA, ");
		
		/** 授信異常註記 **/
		sql.append("	   TCN.CREDIT_ABNORMAL ");
		
		sql.append("FROM TBCRM_CUST_MAST TCM ");
		sql.append("LEFT JOIN VWORG_AO_INFO AO ON TCM.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN TBORG_UHRM_BRH UHRM ON AO.EMP_ID = UHRM.EMP_ID ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO INFO ON TCM.BRA_NBR = INFO.BRANCH_NBR ");
		sql.append("LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = TCM.BRA_NBR ");
		sql.append("LEFT JOIN TBCRM_CUST_NOTE TCN ON TCN.CUST_ID = TCM.CUST_ID ");
		sql.append("LEFT JOIN TBSYSPARAMETER para1 ON TCM.COUNTRY_NBR = para1.PARAM_CODE and para1.PARAM_TYPE = 'CRM.COUNTRY_MAP' ");
		sql.append("LEFT JOIN (SELECT * FROM TBKYC_INVESTOREXAM_M_HIS WHERE CUST_ID = :cust_id AND STATUS = '03' ORDER BY CREATE_DATE DESC FETCH FIRST 1 ROWS ONLY) EXAM on EXAM.CUST_ID = TCM.CUST_ID ");
		
		/** 高齡客戶之投資比重 **/
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT AMT_BASE.CUST_ID, ");
		sql.append("         AMT_BASE.AUM_SEC_TOTAL, ");
		sql.append("         AMT_BASE.INV_AMT, ");
		sql.append("         AMT_BASE.ROA, ");
		sql.append("         CASE WHEN AMT_BASE.AUM_SEC_TOTAL = 0 THEN 0 ELSE ROUND(AMT_BASE.INV_AMT / AMT_BASE.AUM_SEC_TOTAL, 2) END AS INV_RATIO, ");
		sql.append("         CASE WHEN AMT_BASE.ANS_INV_RATIO IS NULL THEN 0 ELSE (AMT_BASE.ANS_INV_RATIO * 100) END || '%' AS  ANS_INV_RATIO, ");
		sql.append("         CASE WHEN (CASE WHEN AMT_BASE.AUM_SEC_TOTAL = 0 THEN 0 ELSE ROUND(AMT_BASE.INV_AMT / AMT_BASE.AUM_SEC_TOTAL, 2) END) > AMT_BASE.ANS_INV_RATIO THEN 'Y' ELSE 'N' END AS AMT_KYC_FLAG ");
		sql.append("  FROM ( ");
		sql.append("    SELECT AMT.CUST_ID, AMT.CUST_NAME, AMT.BRA_NBR, AMT.AO_CODE, AMT.AO_02 AS EMP_ID, AMT.ROA, ");
		sql.append("           AMT.AUM_SEC_TOTAL, ");
		sql.append("           (AMT.AMT_09 + AMT.AMT_22 + AMT.AMT_23 + AMT.AMT_12 + AMT.AMT_13 + AMT.AMT_11 + AMT.AMT_21 + AMT.AMT_15 + AMT.AMT_16 + AMT.AMT_14 + AMT.AMT_25 + AMT.AMT_26 + AMT_EXTEND.AMT_17_INV) AS INV_AMT, ");
		sql.append("           AMT_EXTEND.ANS_INV_RATIO ");
		sql.append("     FROM MVCRM_AST_AMT AMT ");
		sql.append("     LEFT JOIN MVCRM_AST_AMT_EXTEND AMT_EXTEND ON AMT.CUST_ID = AMT_EXTEND.CUST_ID ");
		sql.append("     WHERE AMT_EXTEND.ANS_INV_RATIO IS NOT NULL ");
		sql.append("    AND CASE WHEN AMT.AUM_SEC_TOTAL = 0 THEN 0 ELSE ROUND((AMT.AMT_09 + AMT.AMT_22 + AMT.AMT_23 + AMT.AMT_12 + AMT.AMT_13 + AMT.AMT_11 + AMT.AMT_21 + AMT.AMT_15 + AMT.AMT_16 + AMT.AMT_14 + AMT.AMT_25 + AMT.AMT_26 + AMT_EXTEND.AMT_17_INV) / AMT.AUM_SEC_TOTAL, 2) END > AMT_EXTEND.ANS_INV_RATIO ");
		sql.append("    AND CASE WHEN ROUND(MONTHS_BETWEEN(SYSDATE, AMT.BIRTH_DATE) / 12, 2) >= 64.5 THEN 'Y' ELSE 'N' END = 'Y' ");
		sql.append("  ) AMT_BASE ");
		sql.append(") AMT_KYC ON TCM.CUST_ID = AMT_KYC.CUST_ID ");
		
		/** WMS-CR-20250124-01_新增客戶ROA資訊相關功能 **/
		sql.append("LEFT JOIN ( ");
		sql.append("SELECT CUST_ID, ROA FROM MVCRM_AST_AMT WHERE CUST_ID = :cust_id AND ROA IS NOT NULL ");
		sql.append(") ROA ON ROA.CUST_ID = TCM.CUST_ID ");
		
		sql.append("WHERE TCM.CUST_ID = :cust_id ");
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		queryCondition.setQueryString(sql.toString().replaceAll("\\s+", " "));
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		// #1523 : 樂齡專案 === start
		if (StringUtils.equals(String.valueOf(list.get(0).get("SENIOR_CITIZEN_CUST")), "Y")) {
			XmlInfo xmlinfo = new XmlInfo();
			Gson gson = JsonUtil.genDefaultGson();
			
			String apiName = "getCoreTodo";
			String url = xmlinfo.getVariable(apiParam, apiName, "F3");

			logger.info(apiName + " url:" + url);

			GenericMap inputGmap = new GenericMap();
			inputGmap.put("CUST_ID", inputVO.getCust_id());
			
			logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

			Map<String, Object> seniorCitizenMap = new SeniorCitizenClientRS().getMap(url, inputGmap);
			
			for (Map<String, Object> map : list) {
				logger.info(apiName + " return:" + seniorCitizenMap);
				logger.info("seniorCitizenMap.TODO return:" + seniorCitizenMap.get("TODO"));

				map.put("TODO", seniorCitizenMap.get("TODO"));
			}
		}
		// #1523 : 樂齡專案 === end
		
		return_VO.setResultList(list);
		
		if (!list.isEmpty()) {
			return_VO.setAO_CODE((String) list.get(0).get("AO_CODE"));
			
			if (StringUtils.isNotEmpty((String) list.get(0).get("AO_CODE"))) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				
				sql.append("SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :aocode");
				queryCondition.setObject("aocode", (String) list.get(0).get("AO_CODE"));
				
				queryCondition.setQueryString(sql.toString());
				
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				
				if (!list2.isEmpty()) {
					return_VO.setAO_ID((String) list2.get(0).get("EMP_ID"));
				}
			}
			
			return_VO.setVIP_DEGREE((String) list.get(0).get("VIP_DEGREE"));
			return_VO.setCUST_BRANCH((String) list.get(0).get("BRA_NBR"));
			
			if (StringUtils.isNotEmpty((String) list.get(0).get("BRA_NBR"))) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				
				sql.append("SELECT * FROM VWORG_DEFN_INFO WHERE BRANCH_NBR = :branchNbr");
				
				queryCondition.setObject("branchNbr", (String) list.get(0).get("BRA_NBR"));
				
				queryCondition.setQueryString(sql.toString());
				
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				
				if (!list2.isEmpty()) {
					return_VO.setCUST_AREA((String) list2.get(0).get("BRANCH_AREA_ID"));
					return_VO.setCUST_REGION((String) list2.get(0).get("REGION_CENTER_ID"));
				}
			}
			
			return_VO.setRISK_ATTR((String) list.get(0).get("CUST_RISK_ATR"));
		}
		
		this.sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		
		CRM611InputVO inputVO = (CRM611InputVO) body;
		CRM611OutputVO return_VO = new CRM611OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		sql.append("SELECT MAST.SAL_COMPANY, ");
		sql.append("       NOTE.PREF_INFORM_WAY, ");
		sql.append("       NOTE.PREMIUM_CODE, ");
		sql.append("       NOTE.COMPLAIN_YN, ");
		sql.append("       NOTE.LASTUPDATE, ");
		sql.append("       MAST.VIP_ATR, ");
		sql.append("       NOTE.CAL_PFM_NOTE, ");
		sql.append("       NOTE.INS_RECORD_YN, ");
		sql.append("       MKT.BIG_CO_REFUSE_PROM_YN, ");
		sql.append("       MAST.FAMILY_DEGREE, ");
		sql.append("       XPE.EXPERIENCE_LEVEL, ");
		sql.append("       PT.POTENTIAL_LEVEL, ");
		sql.append("       CASE WHEN AN.CUST_ID IS NULL THEN 'N' ELSE 'Y' END AS IS_ACTUAL,"); // add by ocean : WMS-CR-20220517-01_新增實動戶上傳註記
		sql.append("       TCP.PRIVATE, "); // 2380 私銀註記欄位新增
		sql.append("       TCP.EFFECTIVE_DATE "); // 2380 私銀註記欄位新增
		sql.append("FROM TBCRM_CUST_MAST MAST ");
		sql.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID  ");
		sql.append("LEFT JOIN TBCRM_CUST_AGR_MKT_NOTE MKT ON MKT.CUST_ID = MAST.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_XP_DEGREE XPE ON XPE.CUST_ID = MAST.CUST_ID AND TRUNC(SYSDATE) BETWEEN TRUNC(XPE.EXPERIENCE_BEGIN_DATE) AND TRUNC(XPE.EXPERIENCE_END_DATE) ");
		sql.append("LEFT JOIN TBCRM_CUST_PT_DEGREE PT ON PT.CUST_ID = MAST.CUST_ID AND TRUNC(SYSDATE) BETWEEN TRUNC(PT.POTENTIAL_BEGIN_DATE) AND TRUNC(PT.POTENTIAL_END_DATE)");
		sql.append("LEFT JOIN TBPMS_CUST_ACTUAL AN ON AN.YYYYMM = (SELECT MAX(YYYYMM) FROM TBPMS_CUST_ACTUAL) AND AN.CUST_ID = MAST.CUST_ID "); // 實動戶註記
		// 2380 私銀註記欄位新增
		sql.append("LEFT JOIN TBCRM_CUST_PRIVATE TCP ON MAST.CUST_ID = TCP.CUST_ID  ");
		
		sql.append("WHERE MAST.CUST_ID = :custID");
		
		queryCondition.setObject("custID", inputVO.getCust_id());
		
		queryCondition.setQueryString(sql.toString().replaceAll("\\s+", " "));
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (!list.isEmpty()) {
			resultList.add(list.get(0));
			Map<String, Object> map = new HashMap<String, Object>();
			
			if (StringUtils.isNotBlank((String) list.get(0).get("SAL_COMPANY"))) { //薪轉註記		
				map.put("isSalFlag", "Y");
			} else {
				map.put("isSalFlag", "N");
			}
			
			resultList.add(map);
		}
		
		return_VO.setResultList(resultList);
		
		//發送電文
		FP032151OutputVO fp032151OutputVO;
		
		if (inputVO.getData067050_067101_2() != null && inputVO.getData067050_067000() != null && inputVO.getData067050_067112() != null)
			fp032151OutputVO = fp032151Service.transfer(inputVO.getCust_id(), inputVO.getData067050_067101_2(), inputVO.getData067050_067000(), inputVO.getData067050_067112());
		else
			fp032151OutputVO = fp032151Service.search(inputVO.getCust_id());

		if (StringUtils.isNotEmpty(fp032151OutputVO.getBDAY())) {
			fp032151OutputVO.setAGE(cbsservice.changeToAge(fp032151OutputVO.getBDAY()));
			fp032151OutputVO.setBDAY_D(cbsservice.changeDateView(fp032151OutputVO.getBDAY(), "2"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getRISK_DATE_01())) {
			fp032151OutputVO.setRISK_DATE_01_D(toAdYearMMdd(fp032151OutputVO.getRISK_DATE_01(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getBILLS_UPD_DATE())) {
			fp032151OutputVO.setBILLS_UPD_DATE_D(toAdYearMMdd(fp032151OutputVO.getBILLS_UPD_DATE(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getBILLS_STR_DATE())) {
			fp032151OutputVO.setBILLS_STR_DATE_D(toAdYearMMdd(fp032151OutputVO.getBILLS_STR_DATE(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getLST_TX_DATE1())) {
			fp032151OutputVO.setLST_TX_DATE1_D(toAdYearMMdd(fp032151OutputVO.getLST_TX_DATE1(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getLST_TX_DATE2())) {
			fp032151OutputVO.setLST_TX_DATE2_D(toAdYearMMdd(fp032151OutputVO.getLST_TX_DATE2(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getLST_TX_DATE3())) {
			fp032151OutputVO.setLST_TX_DATE3_D(toAdYearMMdd(fp032151OutputVO.getLST_TX_DATE3(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getLST_TX_DATE5())) {
			fp032151OutputVO.setLST_TX_DATE5_D(toAdYearMMdd(fp032151OutputVO.getLST_TX_DATE5(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getLST_TX_DATE6())) {
			fp032151OutputVO.setLST_TX_DATE6_D(toAdYearMMdd(fp032151OutputVO.getLST_TX_DATE6(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getMTN_DATE_RAT())) {
			fp032151OutputVO.setMTN_DATE_RAT_D(toAdYearMMdd(fp032151OutputVO.getMTN_DATE_RAT(), "yyyyMMdd"));
		}
		
		if (StringUtils.isNotEmpty(fp032151OutputVO.getPD_DATE())) {
			fp032151OutputVO.setPD_DATE_D(toAdYearMMdd(fp032151OutputVO.getPD_DATE(), "yyyyMMdd"));
		}

		return_VO.setFp032151OutputVO(fp032151OutputVO);

		this.sendRtnObject(return_VO);
	}

	public void save(Object body, IPrimitiveMap header) throws JBranchException {

		CRM611InputVO inputVO = (CRM611InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_CUST_NOTEVO vo = new TBCRM_CUST_NOTEVO();
		vo = (TBCRM_CUST_NOTEVO) dam.findByPKey(TBCRM_CUST_NOTEVO.TABLE_UID, inputVO.getCust_id());
		if (vo != null) {
			vo.setPREF_INFORM_WAY(inputVO.getInfrom_way());
			dam.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}

		this.sendRtnObject(null);
	}
	
	public void getLostFlag(Object body, IPrimitiveMap header) throws Exception {
		CRM611InputVO inputVO = (CRM611InputVO) body;
		CRM611OutputVO outputVO = new CRM611OutputVO();
		
		
		WMS032154OutputDetailsVO wms032154OutputDetailsVO = new WMS032154OutputDetailsVO();
		//取得電文 電話資料
		SOT701InputVO sot701inputVO = new SOT701InputVO();
		sot701inputVO.setCustID(inputVO.getCust_id());
		SOT701 sot701 = (SOT701) PlatformContext.getBean(SOT701.class.getSimpleName().toLowerCase());
		wms032154OutputDetailsVO = sot701.getPhoneData(sot701inputVO);
        outputVO.setLostFlag(checkLostContact(wms032154OutputDetailsVO));			
		this.sendRtnObject(outputVO);
	}
	
	/*
	 * 檢查失聯註記
	 */
	private boolean checkLostContact(WMS032154OutputDetailsVO detail) throws Exception {

		if("Y".equals(detail.getLOST_FLG1()) ||
				"Y".equals(detail.getLOST_FLG2()) ||
				"Y".equals(detail.getLOST_FLG3()) ||
				"Y".equals(detail.getLOST_FLG4()) ||
				"Y".equals(detail.getLOST_FLG5()) ||
				"Y".equals(detail.getLOST_FLG6()) ||
				"Y".equals(detail.getLOST_FLG7()) ||
				"Y".equals(detail.getLOST_FLG8()) ||
				"Y".equals(detail.getLOST_FLG9()) ||
				"Y".equals(detail.getLOST_FLG10()) ||
				"Y".equals(detail.getLOST_FLG11()) ||
				"Y".equals(detail.getLOST_FLG12()) ||
				"Y".equals(detail.getLOST_FLG13()) ||
				"Y".equals(detail.getLOST_FLG14()) ||
				"Y".equals(detail.getLOST_FLG15()) ||
				"Y".equals(detail.getLOST_FLG16()) ||
				"Y".equals(detail.getLOST_FLG17()) ||
				"Y".equals(detail.getLOST_FLG18()) ||
				"Y".equals(detail.getLOST_FLG19()) ){
			return true;	
		} else {
			return false;
		}
	}

}