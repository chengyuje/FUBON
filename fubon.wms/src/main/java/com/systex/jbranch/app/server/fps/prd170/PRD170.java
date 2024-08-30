package com.systex.jbranch.app.server.fps.prd170;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd170
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("prd170")
@Scope("request")
public class PRD170 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD170.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
//		PRD170InputVO inputVO = (PRD170InputVO) body;
//		PRD170OutputVO return_VO = new PRD170OutputVO();
//		dam = this.getDataAccessManager();
//		
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT a.*, b.EMP_NAME FROM TBCRM_DKYC_QSTN_SET a ");
//		sql.append("left join TBORG_MEMBER b on a.MODIFIER = b.EMP_ID where 1=1 ");
		
	}
	
	
	
}