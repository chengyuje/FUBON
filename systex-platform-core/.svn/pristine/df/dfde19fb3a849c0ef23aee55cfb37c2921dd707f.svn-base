package com.systex.jbranch.platform.common.dataaccess.query;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.errHandle.DAOException;

/**
 * 此Proxy為控制QueryUtility的Transaction用，
 * 全部method皆對應至QueryUtilityIF，此物件定義在Spring的bean中，
 * 為singleton
 *
 */
public class QueryUtilityProxy implements QueryUtilityProxyIF{
	

	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityProxyIF#executeQuery(com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Deprecated
	public List executeQuery(QueryUtilityIF utility)throws DAOException{
		return utility.executeQuery();
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityProxyIF#exeQuery(com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List exeQuery(QueryUtilityIF utility) throws DAOException {
		return utility.exeQuery();
	}
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public ScrollableQueryResultIF executeScrollableQuery(QueryUtilityIF utility)throws DAOException{
		return utility.executeScrollableQuery();
	}
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public ResultIF executePaging(QueryUtilityIF utility,int pageNumber,int recordOfPage) throws DAOException{
		return utility.executePaging(pageNumber, recordOfPage);
	}
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public ResultIF executePaging(QueryUtilityIF utility)throws DAOException{
		return utility.executePaging();
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityProxyIF#executeUpdate(com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF)
	 */
	@Deprecated
	@Transactional(propagation = Propagation.REQUIRED)
	public int executeUpdate(QueryUtilityIF utility)throws DAOException{
		return utility.executeUpdate();
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityProxyIF#exeUpdate(com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int exeUpdate(QueryUtilityIF utility) throws DAOException {
		return utility.exeUpdate();
	}

	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public ResultIF executeCallableQuery(QueryUtilityIF utility) throws DAOException{
		return utility.executeCallableQuery();
	}
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ResultIF newTransactionExecuteCallableQuery(QueryUtilityIF utility)throws DAOException{
		return utility.executeCallableQuery();
	}

	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<Integer, Object> executeCallable(QueryUtilityIF utility) throws DAOException{
		return utility.executeCallable();
	}
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<Integer, Object> newTransactionExecuteCallable(QueryUtilityIF utility)throws DAOException{
		return utility.executeCallable();
	}
	
	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int newTransactionExecuteUpdate(QueryUtilityIF utility)throws DAOException{
		return utility.executeUpdate();
	}

	/**
	 * 請參照QueryUtilityIF
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int newTransactionExeUpdate(QueryUtilityIF utility)throws DAOException{
		return utility.exeUpdate();
	}
}
