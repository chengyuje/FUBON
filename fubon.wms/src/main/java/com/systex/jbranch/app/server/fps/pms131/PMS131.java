package com.systex.jbranch.app.server.fps.pms131;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms131")
@Scope("request")
public class PMS131 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;

	public void getPersonalBonusCal (Object body, IPrimitiveMap header) throws Exception {
		
		PMS131InputVO inputVO = (PMS131InputVO) body;
		PMS131OutputVO outputVO = new PMS131OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT INCOME_MIN, CASE WHEN INCOME_MAX IS NULL THEN 99999999999 ELSE INCOME_MAX END AS INCOME_MAX, JOB_TITLE_NAME, BONUS_RATE * 100 AS BONUS_RATE ");
		sb.append("FROM TBPMS_G_PERSONAL_BONUS_CAL ");
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setBonusRateList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	
}
