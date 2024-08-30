/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_MD_MASTVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("btref")
@Scope("prototype")
public class BTREF extends BizLogic {
	
	private DataAccessManager dam = null;
	
	public void addWKPG() throws JBranchException {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT SEQ, USERID, PRI.PRIVILEGEID ");
		sb.append("FROM TBCAM_LOAN_SALEREC LS ");
		sb.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON LS.USERID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
		sb.append("WHERE (CONT_RSLT IS NULL OR CONT_RSLT = '01') ");
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> empList = dam.exeQuery(queryCondition);
		
		//內部事件通知
		if (empList.size() > 0) {
			for (Map<String, Object> map : empList) {
				TBCRM_WKPG_MD_MASTVO msvo = new TBCRM_WKPG_MD_MASTVO();
				msvo.setSEQ(getSeqNum());
				msvo.setPRIVILEGEID((String) map.get("PRIVILEGEID"));
				msvo.setEMP_ID((String) map.get("USERID"));
				msvo.setROLE_LINK_YN("N");
				msvo.setFRQ_TYPE("D");
				msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
				msvo.setDISPLAY_NO("103");
				msvo.setCLICK_YN("N");
				msvo.setRPT_NAME("待回報轉介案件-" + (String) map.get("SEQ"));
				msvo.setRPT_PROG_URL("REF120");
				msvo.setPASS_PARAMS(null);
				msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));
				
				dam.create(msvo);
			}
		}
	}
	
	public void monOneTimeJob () throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition;
		StringBuffer sb;
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("DELETE FROM TBCAM_REF_TARG_ROL WHERE YYYYMM = TO_CHAR(SYSDATE, 'yyyyMM')");
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("INSERT INTO TBCAM_REF_TARG_ROL (YYYYMM, SALES_ROLE, REF_PROD, MON_TARGET_CNT, MON_SUC_TARGET_CNT, YEAR_TARGET_CNT, YEAR_SUC_TARGET_CNT, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
		sb.append("SELECT TO_CHAR(SYSDATE, 'yyyyMM') AS YYYYMM, SALES_ROLE, REF_PROD, MON_TARGET_CNT, MON_SUC_TARGET_CNT, YEAR_TARGET_CNT, YEAR_SUC_TARGET_CNT, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBCAM_REF_TARG_ROL ");
		sb.append("WHERE YYYYMM = TO_CHAR(ADD_MONTHS(SYSDATE,-1), 'yyyyMM') ");
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("DELETE FROM TBCAM_REF_TARG_BRH WHERE YYYYMM = TO_CHAR(SYSDATE, 'yyyyMM')");
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("INSERT INTO TBCAM_REF_TARG_BRH (YYYYMM, BRANCH_NBR, REF_PROD, MON_TARGET_CNT, MON_SUC_TARGET_CNT, YEAR_TARGET_CNT, YEAR_SUC_TARGET_CNT, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
		sb.append("SELECT TO_CHAR(SYSDATE, 'yyyyMM') AS YYYYMM, BRANCH_NBR, REF_PROD, MON_TARGET_CNT, MON_SUC_TARGET_CNT, YEAR_TARGET_CNT, YEAR_SUC_TARGET_CNT, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBCAM_REF_TARG_BRH ");
		sb.append("WHERE YYYYMM = TO_CHAR(ADD_MONTHS(SYSDATE,-1), 'yyyyMM') ");
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
	}
	
	private String getSeqNum() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCRM_WKPG_MD_MAST.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
}