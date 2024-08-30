package com.systex.jbranch.app.server.fps.insjlb.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**for 富邦**/
@Repository("insjlbDao")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings("unchecked")
public class FubonInsjlbDao extends AbstractInsjlbDao implements FubonInsjlbDaoInf{

	/**給附項目中文*/
	@Override
	public List<Map<String , Object>> querySortMap() throws JBranchException{
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(genSqlCondition(dam).setQueryString(new StringBuffer()
			.append(" SELECT COALESCE(C.PARAM_CODE, A.COVER_NO) AS SORTNO, COALESCE( ")
			.append("     C.PARAM_NAME, ")
			.append("     CASE ")
			.append("       WHEN A.PAY_WAY IS NULL ")
			.append("       THEN A.COVER_DESC ")
			.append("       WHEN A. PAY_WAY = '' ")
			.append("       THEN A.COVER_DESC ")
			.append("       ELSE A.COVER_DESC ||'(' ||A.PAY_WAY ||')'   ")
			.append("     END ")
			.append(" ) AS DSC ")
			.append(" FROM TBPRD_INSDATA_COVER_DTL A ")
			.append(" FULL OUTER JOIN( ")
			.append("   SELECT * FROM TBSYSPARAMETER B ")
			.append("   WHERE B.PARAM_TYPE ='TBPRD_INSDATA_COVER_DTL' ")
			.append("   AND B.PARAM_STATUS <> '3' ")
			.append(" ) C ")
			.append(" ON C.PARAM_CODE = A.COVER_NO ")
			.append(" ORDER BY COVER_NO ")
			.toString()
		));
	}

	/**查詢家庭成員與財務安全資訊*/
	@Override
	public Map<String, Object> queryFamilyMemberFinanceSafetyInf(String custId) throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		String queryStr = "SELECT cfd.* FROM TBINS_CUST_FAMILY_DATA cfd WHERE cfd.CUST_ID = :CUST_ID ";
		
		List<Map<String , Object>> resultList = 
			dam.exeQuery(genSqlCondition(dam).setQueryString(queryStr).setObject("CUST_ID", custId));
		
		//每個客戶只會有一筆
		if(CollectionUtils.isNotEmpty(resultList)){
			return resultList.get(0);
		}
		
		return null;
	}
	
	/**保障缺口對應SORTNO並轉換為Mapping用Ｍap*/
	public List<Map<String, Object>> querySecurityGapSortNo() throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(genSqlCondition(dam) .setQueryString(new StringBuffer()
			.append(" SELECT PARAM_CODE, PARAM_NAME FROM TBSYSPARAMETER ")
			.append(" WHERE PARAM_TYPE = 'OLDITEM_SORTNO_MAP' ")
			.append(" AND PARAM_CODE IN('L' , 'P' , 'HD' , 'D') ")
			.toString()
		));
	}
	
	/**子女教育試算*/
	public List<Map<String, Object>> queryLstSppedu(String custId) throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(genSqlCondition(dam)
			.setQueryString(" SELECT * FROM TBINS_SPPEDU_DETAIL WHERE CUST_ID = :CUST_ID ")
			.setObject("CUST_ID", custId)
		);
	}
}
