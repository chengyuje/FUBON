package com.systex.jbranch.app.server.fps.crm141;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
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

/**
 * Create: 2016/06/29 BY walalala
 * Modify: 2016/12/19 BY Stella 去除三日條件、新增排序
 * Modify: 2020/06/02 BY Ocean 新增高端可視範圍限制
 * Modify: 2021/04/20 BY Ocean 新增銀證可視範圍限制
 * 
 */
@Component("crm141")
@Scope("request")
public class CRM141 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		
		CRM141InputVO inputVO = (CRM141InputVO) body;
		CRM141OutputVO outputVO = new CRM141OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT EMP_ID, ");
		sb.append("       ROLE_ID, ");
		sb.append("       PRIVILEGEID, ");
		sb.append("       BRANCH_NBR, ");
		sb.append("       CAMPAIGN_ID, ");
		sb.append("       STEP_ID, ");
		sb.append("       CAMPAIGN_NAME, ");
		sb.append("       START_DATE, ");
		sb.append("       LEAD_TYPE, ");
		sb.append("       COUNTS ");
		sb.append("FROM TBCAM_CAMPS_BY_PERSON ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND EMP_ID = :emp ");

		switch (inputVO.getPri_id()) {
			case "UHRM012":
			case "UHRM013":
				sb.append("AND BRANCH_NBR = '031' ");
				
				break;
			default:
				if (headmgrMap.containsKey(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINROLE)))) {
					sb.append("AND BRANCH_NBR = 'ALL' ");
					sb.append("AND ROLE_ID = :roleID ");
					queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
				} else {
					sb.append("AND BRANCH_NBR IN (:brNbrList) ");
					sb.append("AND ROLE_ID = :roleID ");
					queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
					queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
				}
				
				break;
		}
		
		queryCondition.setObject("emp", ws.getUser().getUserID());

		sb.append("ORDER BY CASE WHEN CAMPAIGN_ID = 'LM5800Q2' THEN 0 ELSE 1 END, START_DATE DESC "); 
		sb.append("FETCH FIRST 5 ROWS ONLY ");

		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}

	//CRM352
	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM141OutputVO outputVO = new CRM141OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// Jemmy 2017-07-26, turning performance
		sql.append("SELECT MIN(B.START_DATE) AS START_DATE, COUNT(DISTINCT(B.CUST_ID)) AS N_CONTACT ");
		sql.append("FROM TBCAM_SFA_LEADS_IMP A ");
		sql.append("LEFT JOIN TBCAM_SFA_LE_IMP_TEMP B ON A.SEQNO = B.IMP_SEQNO ");
		sql.append("WHERE B.AO_CODE = :ao_code AND A.CAMPAIGN_ID like '%TRS%' ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("ao_code", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));

		outputVO.setResultList2(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
}