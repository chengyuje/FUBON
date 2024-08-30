package com.systex.jbranch.app.server.fps.fpsjlb.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;

@Repository("fpsjlbDao")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FubonjlbFcbDao extends FpsjlbDao{
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String , Object>> queryCustIandIMark(String custID) throws Exception {
		DataAccessManager queryObj = getDataAccessManager();
		
		return queryObj.exeQuery(queryObj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL)
			.setQueryString(new StringBuffer()
				.append(" select 1 from TBCRM_CUST_NOTE where INV_YN ='Y' OR INS_YN = 'Y' AND CUST_ID :custId ")
				.toString())		
				.setObject("custId", "custID"));
	}
}
