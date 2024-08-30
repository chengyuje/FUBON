package com.systex.jbranch.app.server.fps.sqm410;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.app.server.fps.crm453.CRM453InputVO;
import com.systex.jbranch.app.server.fps.crm453.CRM453OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.common.xmlInfo.XMLInfoInputVO;
import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.fubon.bth.BTSQM410;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sqm410
 * 
 * @author Cathy
 * @date 2019/10/29
 * @spec WMS-CR-20190906-01_擬增設M+系統通知批示服務滿意度調查案件功能
 */
@Component("sqm410")
@Scope("request")
public class SQM410 extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataAccessManager dam = null;
	
	/**
	 * 顯示轄下滿意度未處理件數
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		SQM410InputVO inputVO = (SQM410InputVO) body ;
		SQM410OutputVO outputVO = new SQM410OutputVO();
		dam = this.getDataAccessManager();
		logger.info("M+撈資料前所用的ID:  " + inputVO.getLoginEmpID());
		// 取得滿意度未處理資訊
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT SUBSTR(A.DATA_DATE, 0, 6) AS YEARMON, A.QTN_TYPE, ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME, A.BRANCH_NBR, ORG.BRANCH_NAME, count(1) AS CASE_COUNT ");
		sb.append("  FROM TBSQM_CSM_IMPROVE_MAST A ");
		sb.append("  left join TBCRM_CUST_MAST CM ON CM.CUST_ID = A.CUST_ID ");
		sb.append("  left join TBPMS_ORG_REC_N ORG on ORG.dept_id = A.branch_nbr and to_date(A.TRADE_DATE,'yyyymmdd') between ORG.START_TIME and ORG.END_TIME ");
		sb.append("  WHERE A.DELETE_FLAG IS NULL AND A.HO_CHECK = 'Y' AND NVL(A.CASE_STATUS, ' ') <> 'N' ");
		sb.append("  AND CM.UEMP_ID IS NULL ");  //排除高端客戶
		sb.append("  AND A.CASE_NO IS NOT NULL AND A.OWNER_EMP_ID = :empId ");
		sb.append("  GROUP BY SUBSTR(A.DATA_DATE, 0, 6), A.QTN_TYPE, ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME, A.BRANCH_NBR, ORG.BRANCH_NAME ");
		sb.append("  ORDER BY SUBSTR(A.DATA_DATE, 0, 6), ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME, A.BRANCH_NBR, ORG.BRANCH_NAME, A.QTN_TYPE ");

		queryCondition.setObject("empId", inputVO.getLoginEmpID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);
		
		outputVO.setResultList(resultList);
		logger.info("M+撈資料後所用的sql: " + sb.toString() );
		logger.info("M+撈資料後所用的ID:  " + inputVO.getLoginEmpID());
		//處理代理人資訊
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("  SELECT SUBSTR(A.DATA_DATE, 0, 6) AS YEARMON, A.QTN_TYPE, ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME, A.BRANCH_NBR, ORG.BRANCH_NAME, count(1) AS CASE_COUNT ");
		sb.append("  FROM TBSQM_CSM_IMPROVE_MAST A ");
		sb.append("  left join TBCRM_CUST_MAST CM ON CM.CUST_ID = A.CUST_ID ");
		sb.append("  left join TBPMS_ORG_REC_N ORG on ORG.dept_id = A.branch_nbr and to_date(A.TRADE_DATE,'yyyymmdd') between ORG.START_TIME and ORG.END_TIME ");
		sb.append("  WHERE A.DELETE_FLAG IS NULL AND A.HO_CHECK = 'Y' AND NVL(A.CASE_STATUS, ' ') <> 'N' ");
		sb.append("  AND CM.UEMP_ID IS NULL ");  //排除高端客戶
		sb.append("  AND A.CASE_NO IS NOT NULL AND A.OWNER_EMP_ID IN (SELECT EMP_ID FROM TBORG_AGENT WHERE AGENT_ID = :empId AND AGENT_STATUS = 'S' AND SYSDATE BETWEEN START_DATE AND END_DATE) ");
		sb.append("  GROUP BY SUBSTR(A.DATA_DATE, 0, 6), A.QTN_TYPE, ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME, A.BRANCH_NBR, ORG.BRANCH_NAME ");
		sb.append("  ORDER BY SUBSTR(A.DATA_DATE, 0, 6), ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME, A.BRANCH_NBR, ORG.BRANCH_NAME, A.QTN_TYPE ");

		queryCondition.setObject("empId", inputVO.getLoginEmpID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> agentResultList = dam.exeQuery(queryCondition);
		outputVO.setAgentList(agentResultList);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("  SELECT JOB_TITLE_NAME FROM TBORG_MEMBER WHERE EMP_ID =:empId ");
		queryCondition.setObject("empId", inputVO.getLoginEmpID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> empList = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(empList) && empList.get(0).get("JOB_TITLE_NAME") != null){
			outputVO.setJobTitleName(empList.get(0).get("JOB_TITLE_NAME").toString());
		}
		
		//取前端需要的XML資料
		XMLInfo xmlInfo = new XMLInfo();
		List<String> pTypeList = new ArrayList<String>();
		pTypeList.add("SQM.QTN_TYPE");
		outputVO.setQtnTypeList(xmlInfo.getXMLInfo(pTypeList));
				
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 取得轄下分行滿意度未處理列表
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getCaseList(Object body, IPrimitiveMap header) throws JBranchException {
		SQM410InputVO inputVO = (SQM410InputVO) body ;
		SQM410OutputVO outputVO = new SQM410OutputVO();
		dam = this.getDataAccessManager();
		
		// 取得滿意度未處理資訊
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT DISTINCT A.*, SUBSTR(A.DATA_DATE, 0, 6) AS YEARMON, ORG.BRANCH_NAME, CM.CON_DEGREE, ");
		sb.append(" 	FRQ.FRQ_DAY ,to_char(VISIT.CREATETIME,'YYYYMMDD') AS LAST_VISIT_DATE ");
		sb.append("  FROM TBSQM_CSM_IMPROVE_MAST A ");
		sb.append("  left join TBCRM_CUST_MAST CM ON CM.CUST_ID = A.CUST_ID ");
		sb.append("  left join TBPMS_ORG_REC_N ORG on ORG.dept_id = A.branch_nbr and to_date(A.TRADE_DATE,'yyyymmdd') between ORG.START_TIME and ORG.END_TIME ");
		sb.append("  left join VWCRM_CUST_MGMT_FRQ_MAP FRQ ON FRQ.CON_DEGREE = CM.CON_DEGREE AND FRQ.VIP_DEGREE = NVL(CM.VIP_DEGREE,'M') ");
		sb.append("  LEFT JOIN ");
		sb.append("  (  SELECT a.CUST_ID,max(A.CREATETIME) AS CREATETIME ");
		sb.append("  	 FROM TBCRM_CUST_VISIT_RECORD a  ");
		sb.append("  	 INNER JOIN TBSQM_CSM_IMPROVE_MAST B  ");
		sb.append("  	 ON a.CUST_ID = b.CUST_ID AND a.CREATETIME <= to_date(B.SEND_DATE,'yyyymmdd')  ");
		sb.append("  	 GROUP BY A.CUST_ID  ");
		sb.append("  )VISIT ON VISIT.CREATETIME <= to_date(A.SEND_DATE,'yyyymmdd') AND VISIT.CUST_ID = A.CUST_ID  ");
		sb.append("  WHERE A.DELETE_FLAG IS NULL AND A.HO_CHECK = 'Y' AND NVL(A.CASE_STATUS, ' ') <> 'N' ");
		sb.append("  AND CM.UEMP_ID IS NULL ");  //排除高端客戶
		sb.append("  AND A.CASE_NO IS NOT NULL AND (A.OWNER_EMP_ID = :empId ");
		sb.append("  OR A.OWNER_EMP_ID IN (SELECT EMP_ID FROM TBORG_AGENT WHERE AGENT_ID = :empId AND AGENT_STATUS = 'S' AND SYSDATE BETWEEN START_DATE AND END_DATE)) ");
		sb.append("  AND A.BRANCH_NBR = :branchNbr ");

		queryCondition.setObject("empId", inputVO.getLoginEmpID());
		queryCondition.setObject("branchNbr", inputVO.getBranchID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);
		
		outputVO.setResultList(resultList);
						
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * M+ userID 解密
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getEmpIdByAuserId (Object body, IPrimitiveMap header) throws Exception {
		CRM453InputVO inputVO = (CRM453InputVO) body ;
		CRM453OutputVO outputVO = new CRM453OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		// 抓參數
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("MPLUS.API_PASSWORD", FormatHelper.FORMAT_3);
		String aesKey = headmgrMap.get("AesKey");
		// 取得M+傳來的加密ID
		String auserID = inputVO.getAuserid();
		logger.info("M+埋LOG 解密前Auserid: " + auserID);
		String mplus_uid = "";
		if (StringUtils.isNotBlank(auserID)) {
			// AES解密
			mplus_uid = AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding(aesKey, auserID);
//			System.out.println("==================");
//			System.out.println(aesKey);
//			System.out.println(auserID);
//			System.out.println(mplus_uid);
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT MP.EMP_NUMBER, P.PRIVILEGEID FROM ( ");
			sql.append("    SELECT EMP_NUMBER FROM TBAPI_MPLUS WHERE MPLUS_UID = :mplus_uid ");
			sql.append(") MP ");
			sql.append("LEFT JOIN TBORG_MEMBER_ROLE R ON MP.EMP_NUMBER = R.EMP_ID ");
			sql.append("LEFT JOIN TBSYSSECUROLPRIASS P ON R.ROLE_ID = P.ROLEID ");
			sql.append(" WHERE P.PRIVILEGEID in ('012','013') "); //限制只處理督導及處長
			queryCondition.setObject("mplus_uid", mplus_uid);
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			// 有可能一個 MPLUS_UID 會對到多個 EMP_NUMBER，此時需取最大角色。
			String emp_number = "";
			int pri = 0;
			String privilegeID = "";
			for (Map<String, Object> map : list) {
				if (map.get("PRIVILEGEID") != null && map.get("EMP_NUMBER") != null) {
					String priID = map.get("PRIVILEGEID").toString();
					privilegeID = StringUtils.isBlank(privilegeID) ? map.get("PRIVILEGEID").toString() : privilegeID;
					if (priID.length() > 3 && "UHRM".equals(priID.substring(0, 4))) {
						emp_number = map.get("EMP_NUMBER").toString();
						break;
					} else {
						if (Integer.parseInt(priID) > pri) {
							pri = Integer.parseInt(map.get("PRIVILEGEID").toString());
							privilegeID = map.get("PRIVILEGEID").toString();
							emp_number = map.get("EMP_NUMBER").toString();
						}
					}
				}
			}
			Map<String, Object> resultMap = new HashMap<String, Object>(); 
			resultMap.put("EMP_NUMBER", emp_number);
			resultMap.put("PRIVILEGEID", privilegeID);
			resultList.add(resultMap);
			outputVO.setResultList(resultList);
			logger.info("M+埋LOG getEmpIdByAuserId回送前ID: " + resultMap.get("EMP_NUMBER"));
		}
		
		this.sendRtnObject(outputVO);
	}
	
	public void pushAuthMessage(Object body, IPrimitiveMap<?> header) throws Exception {
		BTSQM410 bt = (BTSQM410) PlatformContext.getBean("btsqm410");
		bt.pushAuthMessage(body, header);
	}
}