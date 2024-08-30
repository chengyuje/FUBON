package com.systex.jbranch.platform.common.dataaccess.util;


import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.ErrMsgHelper;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供DataAccess模組底層所需要的Tool
 */
public class DataAccessUtil {
// ------------------------------ FIELDS ------------------------------

    private static Logger logger = LoggerFactory.getLogger(DataAccessUtil.class);

// -------------------------- STATIC METHODS --------------------------

    /**
     * 取得指定table的Dao物件
     * (Dao物件的class name為 VO中的table uid + "HibernateDAO")
     *
     * @param tableUid
     * @return
     * @throws DAOException
     */
    public static Object getDao(String tableUid) throws DAOException {
        try {
            return Class.forName(tableUid + "HibernateDAO").newInstance();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DAOException(ErrMsgHelper.TABLE_UID_ERROR);
        }
    }

    /**
     * 取得指定table的VO物件
     * (VO物件的class name為 VO中的table uid + "VO")
     *
     * @param tableUid
     * @return
     * @throws DAOException
     */
    public static Class getVOClass(String tableUid) throws DAOException {
        try {
            return Class.forName(tableUid + "VO");
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DAOException(ErrMsgHelper.TABLE_UID_ERROR);
        }
    }

    /**
     * 利用DataSource物件取得所對應的SessionFactory
     *
     * @param dataSource
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public static SessionFactory getSessionFactory(
            com.systex.jbranch.platform.common.dataaccess.datasource.DataSource dataSource)
            throws DAOException, JBranchException {
        return (SessionFactory) PlatformContext.getBean(dataSource
                .getDataSource());
    }
}
