package com.systex.jbranch.platform.common.query.factory.impl.hibernate;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.ErrMsgHelper;
import com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityFactoryIF;
import com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF;
import com.systex.jbranch.platform.common.dataaccess.util.DataAccessUtil;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.query.impl.hibernate.QueryUtilityBase;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 以Hibernate實作QueryUtilityFactoryIF
 * 主要用途用於取得QueryUtility物件
 */
public class QueryUtilityFactoryImpl implements QueryUtilityFactoryIF {
// ------------------------------ FIELDS ------------------------------

    private Map<Byte, ObjectFactory<QueryUtilityBase>> implementations = new HashMap<Byte, ObjectFactory<QueryUtilityBase>>();
    private Logger logger = LoggerFactory.getLogger(QueryUtilityFactoryImpl.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface QueryUtilityFactoryIF ---------------------

    /**
     * 取得QueryUtility
     *
     * @param queryLanguage
     * @param dataSource
     * @param transactionManagement
     * @return QueryUtilityIF
     * @throws DAOException, JBranchException
     */
    public QueryUtilityIF getQueryUtility(byte queryLanguage,
                                          com.systex.jbranch.platform.common.dataaccess.datasource.DataSource dataSource,
                                          byte transactionManagement) throws JBranchException {
        try {
            SessionFactory sessionFactory = DataAccessUtil.getSessionFactory(dataSource);
            return getUtility(sessionFactory, queryLanguage);
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

// -------------------------- OTHER METHODS --------------------------

    public void setImplementations(Map<String, ObjectFactory<QueryUtilityBase>> queryUtilities) throws ClassNotFoundException {
        for (Map.Entry<String, ObjectFactory<QueryUtilityBase>> entry : queryUtilities.entrySet()) {
            implementations.put(Byte.valueOf(entry.getKey()), entry.getValue());
        }
    }

    private QueryUtilityIF getUtility(SessionFactory sessionFactory, byte queryLanguage) throws JBranchException {
        try {
            if (!implementations.containsKey(queryLanguage)) {
                throw new DAOException(ErrMsgHelper.UNKNOW_QUERY_TYPE);
            }
            QueryUtilityBase queryUtility = implementations.get(queryLanguage).getObject();

            queryUtility.setQueryType(queryLanguage);
            queryUtility.setSessionFactory(sessionFactory);
            return queryUtility;
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
}
