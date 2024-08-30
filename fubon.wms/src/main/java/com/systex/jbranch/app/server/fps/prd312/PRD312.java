package com.systex.jbranch.app.server.fps.prd312;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("prd312")
@Scope("request")
public class PRD312 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void getPrdDtl(Object body, IPrimitiveMap header) throws JBranchException {
		
		PRD312InputVO inputVO = (PRD312InputVO) body;
		PRD312OutputVO outputVO = new PRD312OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
		StringBuffer sb = new StringBuffer();		
		
		sb.append("SELECT * ");
		sb.append("FROM TBPRD_TFB_FOREIGN_SN ");
		sb.append("WHERE STOCK_CODE = :stockCode ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("stockCode", inputVO.getSTOCK_CODE());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);	
	}

	
}