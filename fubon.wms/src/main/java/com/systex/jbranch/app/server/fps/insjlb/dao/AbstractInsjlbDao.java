package com.systex.jbranch.app.server.fps.insjlb.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdInputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@SuppressWarnings("unchecked")
abstract public class AbstractInsjlbDao extends BizLogic implements InsjlbDaoInf{
	
	public QueryConditionIF genSqlCondition(DataAccessManager dam) throws JBranchException{
		return dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}
	
	public List<Map<String , Object>> queryInsCompanyAll(String comType) throws JBranchException{
		DataAccessManager dam = getDataAccessManager();
		
		return dam.exeQuery(genSqlCondition(dam).setQueryString(
			" select COM_ID , COM_NAME from TBPRD_INSDATA_COMPANY where COM_TYPE = :COM_TYPE order by COM_TYPE DESC, COM_ID ")
			.setObject("COM_TYPE", comType));
	}

	public List<Map<String , Object>> queryInsCompanyAll() throws JBranchException{
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(genSqlCondition(dam).setQueryString(
			" select COM_ID , COM_NAME from TBPRD_INSDATA_COMPANY order by COM_TYPE DESC, COM_ID "));
	}
	
	/**產品資訊**/
	public List<Map<String, Object>> queryThirdInsProdMsg(ThirdInsProdInputVO inputVO) throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF condition = genSqlCondition(dam);
		StringBuilder queryStr = new StringBuilder();
		
		queryStr.append(" select A.* , B.COM_NAME from TBPRD_INSDATA_PROD_MAIN A ");
		queryStr.append(" LEFT JOIN TBPRD_INSDATA_COMPANY B ON B.COM_ID = A.COM_ID ");
		
		//若有商品屬性編號
		if(StringUtils.isNotBlank(inputVO.getQid())){
			queryStr.append(" INNER JOIN TBPRD_INSDATA_QUERYITEM C ON C.QID = :qid AND C.KEY_NO = A.KEY_NO ");
			condition.setObject("qid" , inputVO.getQid());
		}
		
		//查詢條件
		queryStr.append(" WHERE A.IFCHS = 'Y' ");
		
		//保險公司代碼
		if(StringUtils.isNotBlank(inputVO.getInsCO())){
			queryStr.append(" and A.COM_ID = :comId ");
			condition.setObject("comId" , inputVO.getInsCO());
		}
		
		//產品ID
		if(StringUtils.isNotBlank(inputVO.getThirdProdCode())){
			queryStr.append(" and A.KEY_NO = :keyNo ");
			condition.setObject("keyNo" , inputVO.getThirdProdCode());
		}
		
		//產品關鍵字
		if(StringUtils.isNotBlank(inputVO.getProdName())){
			queryStr.append("AND A.PRD_NAME || A.PRD_ID like :prdNameId ");
			condition.setObject("prdNameId" , inputVO.getProdName());
		}
		
		//主約/附約
		if(StringUtils.isNotBlank(inputVO.getIsMain())){
			queryStr.append(" and A.IS_MAIN = :isMain ");
			condition.setObject("isMain" , inputVO.getIsMain());
		}
		
		//現售/停售
		if(StringUtils.isNotBlank(inputVO.getIsOld())){
			queryStr.append(" and A.IS_OLD = :isOld ");
			condition.setObject("isOld" , inputVO.getIsOld());
		}
		
		queryStr.append(" ORDER BY A.PRD_ID ");
		
		return dam.exeQuery(condition.setQueryString(queryStr.toString()));
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
	
	/**資訊源商品檔*/
	public List<Map<String, Object>> queryFinancialProduct(List<String> keyNos) throws JBranchException{
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(genSqlCondition(dam).setQueryString(new StringBuffer()
			.append(" select  ")
			.append(" 	A.*, B.COM_NAME, COALESCE(D.PARAM_NAME, C.PARAM_NAME) DFT_SETTING ")
			.append(" from TBPRD_INSDATA_PROD_MAIN A ")
			.append(" left JOIN TBPRD_INSDATA_COMPANY B ")
			.append(" ON A.COM_ID = B.COM_ID ")
			.append(" left JOIN TBSYSPARAMETER C ON C.PARAM_TYPE = 'TBPRD_INSDATA_PROD_MAIN.SETTING' ")
			.append(" AND C.PARAM_CODE =A.COM_ID AND C.PARAM_STATUS <> '3' ")
			.append(" left JOIN TBSYSPARAMETER D ON D.PARAM_TYPE = 'TBPRD_INSDATA_PROD_MAIN.SETTING' ")
			.append(" AND D.PARAM_CODE= A.KEY_NO AND D.PARAM_STATUS <> '3' ")
			.append(" where A.KEY_NO in(:KEY_NO) ")
			.toString()
		).setObject("KEY_NO", keyNos));
	}
	
	/**既有保障缺口對應SORTNO*/
	public List<Map<String, Object>> queryOlditemSortnoMap(String[] paramCodes) throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(genSqlCondition(dam).setQueryString(new StringBuffer()
			.append(" SELECT PARAM_CODE, PARAM_NAME FROM TBSYSPARAMETER ")
			.append(" WHERE PARAM_TYPE = 'OLDITEM_SORTNO_MAP' ")
			.append(" AND PARAM_CODE IN (:PARAM_CODE) ")
			.toString()
		)
		.setObject("PARAM_CODE", paramCodes));
	}

	@Override
	public List<Map<String, Object>> queryParameterForType(String paramType) throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		
		return dam.exeQuery(genSqlCondition(dam).setQueryString(new StringBuffer()
			.append(" SELECT PARAM_CODE, PARAM_NAME , PARAM_DESC , PARAM_STATUS FROM TBSYSPARAMETER ")
			.append(" WHERE PARAM_TYPE = :PARAM_TYPE ")
			.toString()
		).setObject("PARAM_TYPE" , paramType));
	}
	
	
}
