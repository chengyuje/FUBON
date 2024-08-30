package com.systex.jbranch.platform.common.dataaccess.transaction;

import java.util.List;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;
import com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;

public interface IndependentTransactionIF {
	public Object executeMethod(Object target, String methodName, List args) throws DAOException;
	public Object executeMethod(Object target, String methodName) throws DAOException;	
}
