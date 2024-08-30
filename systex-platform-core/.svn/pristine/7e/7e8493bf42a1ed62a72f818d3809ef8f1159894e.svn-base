package com.systex.jbranch.platform.common.dataaccess.query;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * 定義如何取得QueryUtility的介面
 *
 */
public interface QueryUtilityFactoryIF {
	
	/**
	 * 取得QueryUtility
	 * @return QueryUtilityIF
	 * @throws DAOException, JBranchException
	 */
	public QueryUtilityIF getQueryUtility(byte queryLanguage, 
				com.systex.jbranch.platform.common.dataaccess.datasource.DataSource dataSource,
				byte transactionManagement)throws DAOException, JBranchException;
}
