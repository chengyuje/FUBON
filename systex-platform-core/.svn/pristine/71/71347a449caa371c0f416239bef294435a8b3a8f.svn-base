package com.systex.jbranch.platform.common.dataaccess.query;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.DAOException;

/**
 * 此介面為定義QueryUtilityProxy的interface
 * 全部method皆對應至QueryUtilityIF
 */
public interface QueryUtilityProxyIF {
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Deprecated
	public List executeQuery(QueryUtilityIF utility)throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	public List exeQuery(QueryUtilityIF utility)throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	public ResultIF executePaging(QueryUtilityIF utility,int pageNumber,int recordOfPage) throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	public ResultIF executePaging(QueryUtilityIF utility)throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	public ScrollableQueryResultIF executeScrollableQuery(QueryUtilityIF utility)throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Deprecated
	public int executeUpdate(QueryUtilityIF utility)throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	public int exeUpdate(QueryUtilityIF utility)throws DAOException;

	/**
	 * 請參照QueryUtilityIF
	 */
	public ResultIF executeCallableQuery(QueryUtilityIF utility) throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	public ResultIF newTransactionExecuteCallableQuery(QueryUtilityIF utility)throws DAOException;

	/**
	 * 請參照QueryUtilityIF
	 */
	public Map<Integer, Object> executeCallable(QueryUtilityIF utility) throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	public Map<Integer, Object> newTransactionExecuteCallable(QueryUtilityIF utility)throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Deprecated
	public int newTransactionExecuteUpdate(QueryUtilityIF utility)throws DAOException;
	
	/**
	 * 請參照QueryUtilityIF
	 */
	public int newTransactionExeUpdate(QueryUtilityIF utility)throws DAOException;
}
