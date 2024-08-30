package com.systex.jbranch.ws.external.service.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("ExtjlbDao")
public class ExtjlbDao extends BizLogic implements ExtjlbDaoInf{
	
	public QueryConditionIF genSqlCondition(DataAccessManager dam) throws JBranchException{
		return dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}
	
	public QueryConditionIF getSqlQueryCondition(DataAccessManager dam) throws DAOException, JBranchException{
		return dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}

	/**資訊源產品檔**/
	public List<Map<String, Object>> queryInfSourceProdData(List<String> keynos) throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(genSqlCondition(dam).setQueryString(new StringBuffer()
			.append(" select A.PRD_ID , A.PRD_NAME, A.CONTENT_FILENAME , A.CLAUSE_FILENAME, '' as URL1, '' as URL2 ")
			.append(" from TBPRD_INSDATA_PROD_MAIN A ")
			.append(" where KEY_NO in(:keyno) ")
			.append(" order by PRD_ID ")
			.toString()
		)
		.setObject("keyno", keynos));
	}
	
	/**取參數設定*/
	public List<Map<String, Object>> queryParameterConf(String paramType) throws JBranchException{
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(genSqlCondition(dam).setQueryString(new StringBuffer()
			.append(" SELECT PARAM_CODE , PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = :PARAM_TYPE ")
			.toString()
		).setObject("PARAM_TYPE", paramType));
	}

	
	public List<Map<String, Object>> queryParameterForType(String paramType) throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		
		return dam.exeQuery(genSqlCondition(dam).setQueryString(new StringBuffer()
			.append(" SELECT PARAM_CODE, PARAM_NAME , PARAM_DESC , PARAM_STATUS FROM TBSYSPARAMETER ")
			.append(" WHERE PARAM_TYPE = :PARAM_TYPE ")
			.toString()
		).setObject("PARAM_TYPE" , paramType));
	}
	
	
}
