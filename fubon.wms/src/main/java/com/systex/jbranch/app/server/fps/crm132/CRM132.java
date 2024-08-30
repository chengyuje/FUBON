package com.systex.jbranch.app.server.fps.crm132;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm132")
@Scope("request")
public class CRM132 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	
	public void query (Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgr = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		
		CRM132InputVO inputVO = (CRM132InputVO) body;
		CRM132OutputVO outputVO = new CRM132OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT TO_CHAR(CAMP.START_DATE, 'YYYY/MM/DD') AS START_DATE, ");
		sb.append("       TRUNC(CAMP.START_DATE) AS CAMP_START_DATE, ");
		sb.append("       CAMP.CAMPAIGN_ID, ");
		sb.append("       CAMP.STEP_ID, ");
		sb.append("       CAMP.CAMPAIGN_NAME, ");
		sb.append("       CAMP.LEAD_TYPE, ");
		sb.append("       COUNT(1) AS LEADS ");
		sb.append("FROM ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("  WHERE LEADS.LEAD_STATUS < '03' ");
		sb.append("  AND NVL(LEADS.EMP_ID,' ') <> ' ' ");
		sb.append("  AND TRUNC(LEADS.START_DATE) <= TRUNC(SYSDATE) ");
		sb.append("  AND LEADS.LEAD_NAME LIKE 'UMa-%' ");
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") > -1) {
			// 20200602 ADD BY OCEAN : 0000166: WMS-CR-20200226-01_高端業管功能改採兼任分行角色調整相關功能_登入底層+行銷模組
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID) ");
		} else if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("bs") > -1) {
			sb.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.EMP_ID = LEADS.EMP_ID) ");
		} else {
			sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = LEADS.EMP_ID) ");
			sb.append("  AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE BS.EMP_ID = LEADS.EMP_ID) ");
		}
		
		switch (inputVO.getPri_id()) {
			case "001": 
			case "002": 
			case "003": 
			case "004":
			case "005":
			case "007": 
			case "008": 
			case "004AO":
				sb.append("  AND LEADS.EMP_ID = :emp ");
				sb.append("  AND LEADS.BRANCH_ID IN (:brNbrList) ");
				queryCondition.setObject("emp", ws.getUser().getUserID());
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				break;
			default:
				if (!headmgr.containsKey(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINROLE)))) {
					sb.append("  AND NVL(LEADS.AO_CODE, ' ') <> ' ' ");
					sb.append("  AND LEADS.BRANCH_ID IN (:brNbrList) ");
					queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				break;
		}
		
		sb.append(") LEADS ");
		sb.append("LEFT JOIN TBCAM_SFA_CAMPAIGN CAMP ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("WHERE CAMP.CAMPAIGN_NAME LIKE 'UMa-%' ");
		sb.append("AND CAMP.REMOVE_FLAG = 'N' ");
		sb.append("AND TRUNC(CAMP.END_DATE) >= TRUNC(SYSDATE) ");
		sb.append("GROUP BY TO_CHAR(CAMP.START_DATE, 'YYYY/MM/DD'), TRUNC(CAMP.START_DATE), CAMP.CAMPAIGN_ID, CAMP.STEP_ID, CAMP.CAMPAIGN_NAME, CAMP.LEAD_TYPE ");

		sb.append("ORDER BY START_DATE DESC, CAMPAIGN_NAME DESC ");

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
}
