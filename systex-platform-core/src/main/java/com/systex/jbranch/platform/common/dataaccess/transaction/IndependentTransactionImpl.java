package com.systex.jbranch.platform.common.dataaccess.transaction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.util.ExceptionMessageUtil;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.util.ReflectionUtil;

public class IndependentTransactionImpl implements IndependentTransactionIF{

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object executeMethod(Object target, String methodName, List args) throws DAOException{
		try {
			return ReflectionUtil.executeMethod(target, methodName, args);
		} catch (Exception e1) {
			throw new RuntimeException(ExceptionMessageUtil.getNativeMessage(e1), e1);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object executeMethod(Object target, String methodName) throws DAOException{
		return executeMethod( target,  methodName, null);
	}
	
	
}
