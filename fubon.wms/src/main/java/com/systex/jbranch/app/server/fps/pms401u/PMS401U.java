package com.systex.jbranch.app.server.fps.pms401u;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms401u")
@Scope("request")
public class PMS401U extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void isMainten(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS401UInputVO inputVO = (PMS401UInputVO) body;
		PMS401UOutputVO outputVO = new PMS401UOutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT CASE WHEN COUNT(*) = 0 THEN 'N' ELSE 'Y' END AS MAIN_YN ");
		sb.append("FROM TBSYSSECUROLPRIASS ASS ");
		sb.append("WHERE EXISTS ( ");
		sb.append("  SELECT PRIVILEGEID ");
		sb.append("  FROM TBSYSSECUPRIFUNMAP MAP ");
		sb.append("  WHERE ITEMID = :itemID ");
		sb.append("  AND FUNCTIONID = 'maintenance' ");
		sb.append("  AND ASS.PRIVILEGEID = MAP.PRIVILEGEID ");
		sb.append(") ");
		sb.append("AND ROLEID = :roleID ");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("itemID", inputVO.getItemID()); 
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)); 
		
		List<Map<String, Object>> priList = dam.exeQuery(queryCondition);

		outputVO.setResultList(priList);
		outputVO.setIsMaintenancePRI(priList.size() > 0 ? priList.get(0).get("MAIN_YN").toString() : "N");

		/// ===
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("SELECT DEPT_ID_30 AS REGION_CENTER_ID, DEPT_NAME_30 AS REGION_CENTER_NAME, ");
		sb.append("       DEPT_ID_40 AS BRANCH_AREA_ID, DEPT_NAME_40 AS BRANCH_AREA_NAME  ");
		sb.append("FROM VWORG_EMP_INFO ");
		sb.append("WHERE BRANCH_AREA_ID = :loginArea ");
		sb.append("AND EMP_ID = :loginID ");
		
		queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)); 
		queryCondition.setObject("loginArea", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA)); 

		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> uhrmOrgList = dam.exeQuery(queryCondition);
		
		if (uhrmOrgList.size() > 0) {
			outputVO.setUhrmORGList(uhrmOrgList);
		}
						
		sendRtnObject(outputVO);
	}
	
	public void getORG(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		XmlInfo xmlInfo = new XmlInfo();
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		PMS401UInputVO inputVO = (PMS401UInputVO) body;
		PMS401UOutputVO outputVO = new PMS401UOutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		switch (inputVO.getOrgType()) {
			case "getOP" :
				sb.append("SELECT DISTINCT REGION_CENTER_ID, ");
				sb.append("       REGION_CENTER_NAME, ");
				sb.append("       BRANCH_AREA_ID, ");
				sb.append("       BRANCH_AREA_NAME ");
				sb.append("FROM VWORG_DEFN_INFO ");
				sb.append("WHERE BRANCH_AREA_NAME LIKE '私銀%' "); 
				sb.append("AND REGION_CENTER_ID = :REGION_CENTER_ID ");
				sb.append("ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID ");
				
				queryCondition.setObject("REGION_CENTER_ID", inputVO.getUhrmRC()); 
				
				break;
			case "getAR" :
				sb.append("SELECT DISTINCT REGION_CENTER_ID, ");
				sb.append("       REGION_CENTER_NAME ");
				sb.append("FROM VWORG_DEFN_INFO DEPT_DTL ");
				sb.append("WHERE DEPT_DTL.BRANCH_AREA_NAME LIKE '私銀%' "); 
				sb.append("ORDER BY REGION_CENTER_ID ");
				
				break;
		}
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		switch (inputVO.getOrgType()) {
			case "getOP" :
				outputVO.setOPList(list);
				break;
			case "getAR" :
				outputVO.setARList(list);
				break;
		}
				
		sendRtnObject(outputVO);
	}
}