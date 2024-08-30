package com.systex.jbranch.platform.common.dataaccess.util;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.DuplicateException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.db.DBAssist;

public class ExceptionMessageUtil {
	
	private Logger logger = LoggerFactory.getLogger(ExceptionMessageUtil.class);
    private DBAssist dbAssist;
    
    public ExceptionMessageUtil(){
    	try {
			dbAssist = PlatformContext.getBean(DBAssist.class);
		} catch (JBranchException e) {
			logger.warn("not found dbAssist");
		}
    }
	
	public static String getNativeMessage(Throwable e){
		if(e.getCause() != null){
			return getNativeMessage(e.getCause());
		}else{
			return e.getMessage();
		}
	}
	
	public static Throwable getNativeCause(Throwable e){
		if(e.getCause() != null){
			return getNativeCause(e.getCause());
		}else{
			return e;
		}
	}
	
	public DAOException processException(
			org.hibernate.exception.ConstraintViolationException e)
			throws DAOException, DuplicateException {
		if(dbAssist == null){
			DAOException de = new DAOException(EnumErrInputType.MSG, e.getMessage());
			de.setException(e);
			return de;
		}
		boolean isDuplicate = dbAssist.isDuplicate(e.getErrorCode());
		if(isDuplicate){
		    DuplicateException de = new DuplicateException(EnumErrInputType.MSG, e.getMessage());
		    de.setException(e);
		    return de;
		}
		return searchRootCause(e);
	}

	public DAOException processException(Exception e) throws DAOException{
		if(dbAssist == null){
			DAOException de = new DAOException(EnumErrInputType.MSG, e.getMessage());
			de.setException(e);
			return de;
		}
		return searchRootCause(e);
	}
	
	private DAOException searchRootCause(Exception e) {
		Throwable rootCause = getNativeCause(e);
		if(rootCause instanceof SQLException){
			SQLException sqlException = (SQLException) rootCause;
			String desc = dbAssist.getDescription(sqlException.getSQLState());
			DAOException daoException = new DAOException(EnumErrInputType.MSG, "SQLException ErrorCode[" + sqlException.getErrorCode() + "], SQLState[" + sqlException.getSQLState() + "][" + desc + "]");
			daoException.setException(e);
			return daoException;
		}else{
            DAOException jbe = new DAOException(EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            return jbe;
		}
	}
	

}
