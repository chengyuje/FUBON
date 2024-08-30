package com.systex.jbranch.app.server.fps.prd313;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("prd313")
@Scope("request")
public class PRD313 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void getPrdDtl(Object body, IPrimitiveMap header) throws JBranchException {
		
		PRD313InputVO inputVO = (PRD313InputVO) body;
		PRD313OutputVO outputVO = new PRD313OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
		StringBuffer sb = new StringBuffer();		
		
		sb.append("SELECT * ");
		sb.append("FROM TBPRD_TFB_DOMESTIC_SN ");
		sb.append("WHERE STOCK_CODE = :stockCode ");
		sb.append("AND (SNAP_DATE, STOCK_CODE) IN (SELECT MAX(SNAP_DATE), STOCK_CODE AS SNAP_DATE FROM TBPRD_TFB_DOMESTIC_SN GROUP BY STOCK_CODE)  ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("stockCode", inputVO.getSTOCK_CODE());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);	
	}

	
}