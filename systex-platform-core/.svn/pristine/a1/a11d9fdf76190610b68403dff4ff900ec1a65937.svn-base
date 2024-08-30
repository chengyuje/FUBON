package com.systex.jbranch.platform.common.dataaccess.daomanager.impl.hibernate;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.DaoImpl;
import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.ErrMsgHelper;
import com.systex.jbranch.platform.common.dataaccess.daomanager.DaoManagerIF;
import com.systex.jbranch.platform.common.dataaccess.util.DataAccessUtil;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 以Spring與Hinernate實作DaoManager介面
 *
 * @version 1.0
 * @since 20080106
 */
public class DaoManagerImpl implements DaoManagerIF {
// ------------------------------ FIELDS ------------------------------

    private Logger logger = LoggerFactory.getLogger(DaoManagerImpl.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DaoManagerIF ---------------------

    /**
     * 由所指定的DataSource與交易管理型態來取得Dao物件
     *
     * @param transactionManagement
     * @param dataSource
     * @param daoId
     * @return Object
     * @throws DAOException, JBranchException
     */
    public Object getDao(byte transactionManagement,
                         com.systex.jbranch.platform.common.dataaccess.datasource.DataSource dataSource,
                         String tableuid)
            throws DAOException, JBranchException {
        try {
//		   SessionFactory sessionFactory = HibernateUtil.getSessionFactory(dataSource);
            SessionFactory sessionFactory = (SessionFactory) PlatformContext.getBean(dataSource.getDataSource());
            DaoImpl dao = (DaoImpl) DataAccessUtil.getDao(tableuid);
            dao.setSessionFactory(sessionFactory);
            return dao;

//		   switch(transactionManagement){
//		         case DataAccessHelper.TRANSACTION_MANAGEMENT_JTA:
//		        	     return getJTADao(sessionFactory, daoId);
//
//		         case DataAccessHelper.TRANSACTION_MANAGEMENT_JDBC:
//		        	     return getJDBCDao(sessionFactory, daoId);
//
//		         default:
//		        	     throw new DAOException(DataAccessHelper.ERR_MSG_TRANSACTION_NOT_FOUND);
//		   }
        }
        catch (DAOException e) {
            throw e;
        }
        catch (JBranchException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            DAOException je = new DAOException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 由所指定的DataSource與交易管理型態來取得VO Class
     *
     * @param transactionManagement
     * @param dataSource
     * @param daoId
     * @return Class
     * @throws DAOException, JBranchException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Criteria getHibernateCriteria(byte transactionManagement,
                                         com.systex.jbranch.platform.common.dataaccess.datasource.DataSource dataSource,
                                         String tableuid)
            throws DAOException, JBranchException {
        try {
            //SessionFactory sessionFactory = HibernateUtil.getSessionFactory(dataSource);
            SessionFactory sessionFactory = (SessionFactory) PlatformContext.getBean(dataSource.getDataSource());
            return sessionFactory.getCurrentSession().createCriteria(DataAccessUtil.getVOClass(tableuid));
        }
        catch (DAOException e) {
            throw e;
        }
        catch (JBranchException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            DAOException je = new DAOException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

//    private Object getJDBCDao(SessionFactory sessionFactory, String daoId) throws DAOException, JBranchException{
//    	try{
//	    	//取得DaoFactory
//			DaoFactoryIF daoFactoryIF = (DaoFactoryIF) PlatformContext.getBean(DataAccessUtil.getDaoFactoryID(daoId));
//
//			//取得Dao
//			DaoImpl dao = (DaoImpl) daoFactoryIF.getDao(DataAccessUtil.getDaoSerialNumber(daoId));
//
//		    //檢查是否有先啟動Transaction
//		    if(HibernateUtil.getCurrentTransactionId().trim().length()==0){
//		    	throw new DAOException(DataAccessHelper.ERR_MSG_TRANSACTION_NOT_FOUND);
//		    }
//
//		    //取得Session
//		    Session session = null;
//		    if(HibernateUtil.getSession(HibernateUtil.getCurrentTransactionId()) == null){
//		    	session = sessionFactory.openSession();
//
//		    	//啟動Transaction
//		    	Transaction transaction = session.beginTransaction();
//		    	Integer timeout = HibernateUtil.getTimeout(HibernateUtil.getCurrentTransactionId());
//
//		    	//設定Timeout
//		    	if(timeout != null){
//		    		transaction.setTimeout(timeout);
//		    	}
//		    	HibernateUtil.setSession(HibernateUtil.getCurrentTransactionId(), session);
//		    	HibernateUtil.setTransaction(HibernateUtil.getCurrentTransactionId(), transaction);
//		    }else{
//		    	session = HibernateUtil.getSession(HibernateUtil.getCurrentTransactionId());
//		    }
//
//		    dao.setSessionFactory(sessionFactory);
//
//			return dao;
//    	} catch(DAOException e){
//    		throw e;
//    	} catch(JBranchException e){
//    		throw e;
//    	} catch(Exception e){
//    		DAOException je = new DAOException(DataAccessHelper.ERR_MSG_DATA_ACCESS_EXCEPTION);
//    		je.setException(e);
//    		throw je;
//    	}
//    }
//
//    private Object getJTADao(SessionFactory sessionFactory, String daoId) throws JBranchException{
//    	try{
//    		DaoImpl dao = (DaoImpl) PlatformContext.getBean(daoId);
//    		dao.setSessionFactory(sessionFactory);
//    		//dao.setHibernateTemplate(new HibernateTemplate(sessionFactory));
//    		return dao;
//    	}catch(DAOException e){
//    		throw e;
//    	}catch(JBranchException e){
//    		throw e;
//    	}catch(Exception e){
//    		DAOException je = new DAOException(DataAccessHelper.ERR_MSG_DATA_ACCESS_EXCEPTION);
//    		je.setException(e);
//    		throw je;
//    	}
//    }

    private void checkDSInfo(String dataSourceID, String loginID, String daoFactoryID) throws DAOException {
        if (dataSourceID == null || dataSourceID.trim().length() == 0) {
            throw new DAOException(ErrMsgHelper.DATASOURCE_NOT_FOUND);
        }

        if (loginID == null || loginID.trim().length() == 0) {
            throw new DAOException(ErrMsgHelper.LOGIN_ID_NOT_FOUND);
        }

        if (daoFactoryID == null || daoFactoryID.trim().length() == 0) {
            throw new DAOException(ErrMsgHelper.DAOFACTORY_NOT_FOUND);
        }
    }
}
