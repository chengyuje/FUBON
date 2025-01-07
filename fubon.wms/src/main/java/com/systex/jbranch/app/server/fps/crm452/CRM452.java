package com.systex.jbranch.app.server.fps.crm452;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * crm452
 * 
 * @author 
 * @date 
 * @spec null
 */
@Component("crm452")
@Scope("request")
public class CRM452 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void viewDetail(Object body, IPrimitiveMap header) throws JBranchException {
		//logger.info("#1585埋log追蹤viewDetail(): 進入viewDetail()");
		//System.out.println("#1585埋log追蹤viewDetail(): 進入viewDetail()");
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		
		CRM452InputVO inputVO = (CRM452InputVO) body ;
		CRM452OutputVO outputVO = new CRM452OutputVO();
		List<Map<String, Object>> applyList = new ArrayList<Map<String,Object>>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT EMP_ID, ROLE_ID ");
		sb.append("FROM VWORG_EMP_INFO ");
		sb.append("WHERE EMP_ID = :empID ");
		
		sb.append("UNION ");
		
		sb.append("SELECT EMP_ID, ROLE_ID ");
		sb.append("FROM VWORG_EMP_INFO INFO ");
		sb.append("WHERE EXISTS ( ");
		sb.append("  SELECT EMP_ID ");
		sb.append("  FROM TBORG_AGENT AG ");
		sb.append("  WHERE AG.AGENT_ID = :empID ");
		sb.append("  AND AG.AGENT_STATUS = 'S' ");
		sb.append("  AND AG.EMP_ID = INFO.EMP_ID ");
		sb.append(") ");
		
		queryCondition.setObject("empID", inputVO.getEmpID());
		queryCondition.setQueryString(sb.toString());
		//logger.info("#1585埋log追蹤viewDetail(): empID? " + inputVO.getEmpID());
		//System.out.println("#1585埋log追蹤viewDetail(): empID? " + inputVO.getEmpID());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		//logger.info("#1585埋log追蹤viewDetail(): 撈出幾筆empid? " + list.size() +"筆");
		//System.out.println("#1585埋log追蹤viewDetail(): 撈出幾筆empid? " + list.size() +"筆");
		if(list.size() > 0){
			for(Map<String, Object> map : list){			
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("WITH BASE AS ( ");
				sb.append("  SELECT CUST_ID, NVL(SUM(ACT_PRFT), 0) AS Y_PROFEE ");
				sb.append("  FROM TBCRM_CUST_PROFEE ");
				sb.append("  WHERE DATA_YEAR||DATA_MONTH BETWEEN TO_CHAR(CURRENT_DATE-365, 'YYYYMM') ");
				sb.append("  AND TO_CHAR(CURRENT_DATE, 'YYYYMM') ");
				sb.append("  GROUP BY CUST_ID ");
				sb.append(") ");
				
				if (StringUtils.equals("single", inputVO.getType())) {
					sb.append("	SELECT A.CREATOR, A.APPLY_SEQ, A.PROD_ID, A.PROD_NAME, A.TRUST_CURR_TYPE, A.TRUST_CURR, (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.SINGLE_TYPE' AND PARAM_CODE = A.APPLY_TYPE) AS APPLY_TYPE_NAME, A.APPLY_TYPE, ");
					sb.append("       A.PURCHASE_AMT, A.ENTRUST_UNIT, A.ENTRUST_AMT, A.DEFAULT_FEE_RATE, A.FEE_RATE, A.FEE, A.FEE_DISCOUNT, ");
					sb.append("       A.MGR_EMP_ID_1, A.MGR_EMP_ID_2, A.MGR_EMP_ID_3, A.MGR_EMP_ID_4, ");
					sb.append("       (SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) FROM TBSYSPARAMETER WHERE PARAM_TYPE = '").append((chkIsUHRM((String) map.get("EMP_ID"), (String) map.get("ROLE_ID")) ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV")).append("'||A.HIGHEST_AUTH_LV) AS HIGHEST_AUTH_LV_NAME, A.HIGHEST_AUTH_LV, A.AUTH_STATUS, A.APPLY_STATUS, A.APPLY_DATE, A.DISCOUNT_TYPE, A.BRG_REASON, A.CREATETIME, ");
					sb.append("       CUST.CUST_NAME, CUST.CUST_ID, CON_D.PARAM_NAME AS CON_DEGREE, CON_V.PARAM_NAME AS VIP_DEGREE, CUST.AUM_AMT, CUST.BRA_NBR, CUST.Y_PROFEE ");
					sb.append("FROM TBCRM_BRG_APPLY_SINGLE A ");
				} else {
					sb.append("SELECT A.CREATOR, A.APPLY_SEQ, A.TERMINATE_SEQ, A.APPLY_DATE, CASE WHEN A.APPLY_TYPE = '1' THEN '基金' WHEN A.APPLY_TYPE = '2' THEN '海外ETF/股票' ELSE '' END AS APPLY_TYPE_NAME, A.APPLY_TYPE, ");
					sb.append("       A.DMT_STOCK, A.DMT_BOND, A.DMT_BALANCED, A.FRN_STOCK, A.FRN_BOND, A.FRN_BALANCED, A.BUY_HK_MRK, A.BUY_US_MRK, A.SELL_HK_MRK, A.SELL_US_MRK, A.BUY_UK_MRK, A.SELL_UK_MRK, A.BUY_JP_MRK, A.SELL_JP_MRK, ");
					sb.append("       A.BRG_BEGIN_DATE, A.BRG_END_DATE, A.BRG_REASON, ");
					sb.append("       A.MGR_EMP_ID_1, A.MGR_EMP_ID_2, A.MGR_EMP_ID_3, A.MGR_EMP_ID_4, ");
					sb.append("       (SELECT TO_CHAR(RTRIM(XMLAGG(XMLELEMENT(E, PARAM_NAME, ',').EXTRACT('//text()') order by PARAM_NAME).GetClobVal(),',')) FROM TBSYSPARAMETER WHERE PARAM_TYPE = '").append((chkIsUHRM((String) map.get("EMP_ID"), (String) map.get("ROLE_ID")) ? "CRM.BRG_ROLEID_UHRM_LV" : "CRM.BRG_ROLEID_LV")).append("'||A.HIGHEST_AUTH_LV) AS HIGHEST_AUTH_LV_NAME, A.HIGHEST_AUTH_LV, A.AUTH_STATUS, A.APPLY_STATUS, A.CREATETIME, "); 
					sb.append("       CUST.CUST_NAME, CUST.CUST_ID, CON_D.PARAM_NAME AS CON_DEGREE, CON_V.PARAM_NAME AS VIP_DEGREE, CUST.AUM_AMT, CUST.BRA_NBR, CUST.Y_PROFEE ");
					sb.append("FROM TBCRM_BRG_APPLY_PERIOD A ");
				}
				
				sb.append("LEFT JOIN ( ");
				sb.append("  SELECT CM.CUST_ID, CM.CUST_NAME, CM.CON_DEGREE, CM.VIP_DEGREE, NVL(CM.AUM_AMT, 0) AS AUM_AMT, CM.BRA_NBR, NVL(CP.Y_PROFEE, 0) AS Y_PROFEE ");
				sb.append("  FROM TBCRM_CUST_MAST CM ");
				sb.append("  LEFT JOIN BASE CP ON CM.CUST_ID = CP.CUST_ID ");
				sb.append(") CUST ON CUST.CUST_ID = A.CUST_ID ");
				sb.append("LEFT JOIN TBSYSPARAMETER CON_D ON CON_D.PARAM_TYPE = 'CRM.CON_DEGREE' AND CON_D.PARAM_CODE = CUST.CON_DEGREE ");
				sb.append("LEFT JOIN (SELECT (SUBSTR(PARAM_NAME, 1, 4)||CHR(10)||CASE WHEN LENGTH(PARAM_NAME) > 4 THEN SUBSTR((PARAM_NAME), 5, LENGTH(PARAM_NAME)) END) AS PARAM_NAME, PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.VIP_DEGREE') CON_V ON CON_V.PARAM_CODE = CUST.VIP_DEGREE ");
				sb.append("WHERE A.APPLY_STATUS = '1' ");
				sb.append("AND A.CUST_ID = :custID ");
				sb.append("AND INSTR(DECODE(A.AUTH_STATUS, '0', A.MGR_EMP_ID_1, '1', A.MGR_EMP_ID_2, '2', A.MGR_EMP_ID_3, '3', A.MGR_EMP_ID_4), :emp_id) > 0 ");
				sb.append("AND A.AUTH_STATUS <> A.HIGHEST_AUTH_LV ");
				
				if(StringUtils.isNotBlank(inputVO.getSeqNum())){
					sb.append("AND A.MPLUS_BATCH = :seqNum ");
					queryCondition.setObject("seqNum", inputVO.getSeqNum());
				}else{
					sb.append("AND A.MPLUS_BATCH IS NULL ");
				}
				
				sb.append("ORDER BY A.CREATETIME, A.APPLY_SEQ ");

				queryCondition.setObject("custID", inputVO.getCustID());
				queryCondition.setObject("emp_id", (String) map.get("EMP_ID"));
				queryCondition.setQueryString(sb.toString());
				//logger.info("#1585埋log追蹤viewDetail(): 目前custid " + inputVO.getCustID());
				//logger.info("#1585埋log追蹤viewDetail(): 目前empid " + (String) map.get("EMP_ID"));
				//System.out.println("#1585埋log追蹤viewDetail(): 目前custid " + inputVO.getCustID());
				//System.out.println("#1585埋log追蹤viewDetail(): 目前empid " + (String) map.get("EMP_ID"));
				List<Map<String, Object>> tempList = dam.exeQuery(queryCondition);
				//logger.info("#1585埋log追蹤viewDetail(): 撈出幾筆 p1? " + tempList.size() +"筆");
				//System.out.println("#1585埋log追蹤viewDetail(): 撈出幾筆 p1? " + tempList.size() +"筆");
				for(Map<String, Object> tempMap : tempList){
					String apply_seq = tempMap.get("APPLY_SEQ").toString();
					String terminate_reason = "";
					String apply_status = tempMap.get("APPLY_STATUS").toString();
					if("4".equals(apply_status)){
						//若為"終止待覆核"，加入"終止原因(TERMINATE_REASON)"
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuffer();
						
						sb.append("SELECT * FROM ( ");
						sb.append("SELECT TERMINATE_REASON FROM TBCRM_BRG_APPROVAL_HISTORY WHERE APPLY_SEQ = :apply_seq ORDER BY LASTUPDATE DESC ");
						sb.append(")WHERE ROWNUM = 1 ");
						
						queryCondition.setObject("apply_seq", apply_seq);
						queryCondition.setQueryString(sb.toString());
						
						List<Map<String, Object>> terReaList = dam.exeQuery(queryCondition);
						
						terminate_reason = terReaList.get(0).get("TERMINATE_REASON").toString();
					}
					
					tempMap.put("TERMINATE_REASON", terminate_reason);
					
					applyList.add(tempMap);
				}
			}
		}
		
		outputVO.setApplyList(applyList);
		//logger.info("#1585埋log追蹤viewDetail(): 撈出幾筆 p2? " + applyList.size() +"筆");
		//logger.info("#1585埋log追蹤viewDetail(): 結束viewDetail()");
		//System.out.println("#1585埋log追蹤viewDetail(): 撈出幾筆 p2? " + applyList.size() +"筆");
		//System.out.println("#1585埋log追蹤viewDetail(): 結束viewDetail()");
		this.sendRtnObject(outputVO);
	} 
	
	public boolean chkIsUHRM(String empID, String roleID) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> FCMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (FCMap.containsKey(roleID)) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("SELECT DEPT_ID ");
			sb.append("FROM TBORG_MEMBER ");
			sb.append("WHERE EMP_ID = :empID ");
			sb.append("AND CHANGE_FLAG IN ('A', 'M', 'P') ");
			sb.append("AND SERVICE_FLAG = 'A' ");
			
			queryCondition.setObject("empID", empID);
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			///
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("SELECT BRANCH_NBR ");
			sb.append("FROM TBORG_UHRM_BRH ");
			sb.append("WHERE EMP_ID = :loginID ");

			queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> uhrmBreach = dam.exeQuery(queryCondition);
			
			if (list.size() > 0 && uhrmBreach.size() > 0) {
				return true;
			} else {
				return false;
			}
		}
		
		return false;
	}
}