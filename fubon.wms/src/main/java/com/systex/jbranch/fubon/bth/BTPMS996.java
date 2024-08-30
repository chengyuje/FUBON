package com.systex.jbranch.fubon.bth;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("btpms996")
@Scope("prototype")
public class BTPMS996 extends BizLogic {
	
	private DataAccessManager dam;
	
	public void doubleCheckStatus(Object body, IPrimitiveMap<?> header) throws Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("UPDATE TBPMS_ALLOW_LIST ");
		sb.append("SET STATUS = 'E' ");
		sb.append("WHERE ALLOW_END_DATE < TRUNC(SYSDATE) ");

		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
	}	
}