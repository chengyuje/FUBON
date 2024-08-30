package com.systex.jbranch.app.server.fps.pms141;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms141")
@Scope("request")
public class PMS141 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;

	public void getPersonalBonusCal (Object body, IPrimitiveMap header) throws Exception {
		
		PMS141InputVO inputVO = (PMS141InputVO) body;
		PMS141OutputVO outputVO = new PMS141OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT BONUS_RATE * 100 AS BONUS_RATE ");
		sb.append("FROM TBPMS_G_PERSONAL_BONUS_CAL ");
		sb.append("WHERE JOB_TITLE_NAME = :jobTitleName ");
		sb.append("AND INCOME_MIN <= :arbrm ");
		sb.append("AND (CASE WHEN INCOME_MAX IS NULL THEN 99999999999 ELSE INCOME_MAX END) >= :arbrm ");
		
		queryCondition.setObject("jobTitleName", inputVO.getJOB_TITLE_NAME());
		queryCondition.setObject("arbrm", inputVO.getARBRM());
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			outputVO.setBONUS_RATE(((BigDecimal) list.get(0).get("BONUS_RATE")).toString());
		}
		
		sendRtnObject(outputVO);
	}
	
	
}
