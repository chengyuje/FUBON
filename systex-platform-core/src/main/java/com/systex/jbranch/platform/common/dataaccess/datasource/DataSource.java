package com.systex.jbranch.platform.common.dataaccess.datasource;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 封裝SessionFactory的ID
 * 呼叫實作DataSourceRule的類別來取得正確對應的SessionFactory bean id
 */
public class DataSource {
// ------------------------------ FIELDS ------------------------------

	private Logger logger = LoggerFactory.getLogger(DataSource.class);
    private String dataSource;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * 建立預設sessionfactory所對應的datasource物件
     *
     * @throws DAOException
     */
    public DataSource() throws DAOException {
        try {
            DataSourceRuleIF rule = (DataSourceRuleIF) PlatformContext.getBean(DataAccessHelper.DATASOURCE_RULE_ID);
            dataSource = rule.getDefaultDataSource();
        }
        catch (DAOException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            DAOException je = new DAOException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    public DataSource(DataSourceRuleIF dataSourceRule) throws DAOException {
        try {
            dataSource = dataSourceRule.getDefaultDataSource();
        }
        catch (DAOException e) {
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
     * 建立指定datasource id所對應的DataSource物件
     *
     * @param dataSourceId
     * @throws DAOException
     */
    public DataSource(String dataSourceId) throws DAOException {
        try {
            dataSource = dataSourceId;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            DAOException je = new DAOException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 利用UUID來取得預設的DataSource，會依照DataSourceRule來判斷此UUID所對應的連線帳號密碼做權限管理
     *
     * @param uuid
     * @throws DAOException
     */
    public DataSource(UUID uuid) throws DAOException {
        try {
            DataSourceRuleIF rule = (DataSourceRuleIF) PlatformContext.getBean(DataAccessHelper.DATASOURCE_RULE_ID);
            dataSource = rule.getDefaultDBDataSource(uuid);
        }
        catch (DAOException e) {
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
     * 建立指定資料連線的DataSource物件，並依據UUID來判斷此連線該用的帳號密碼來做權限管理
     *
     * @param dbID
     * @param uuid
     * @throws DAOException
     */
    public DataSource(String dbID, UUID uuid) throws DAOException {
        try {
            DataSourceRuleIF rule = (DataSourceRuleIF) PlatformContext.getBean(DataAccessHelper.DATASOURCE_RULE_ID);
            dataSource = rule.getDataSource(dbID, uuid);
        }
        catch (DAOException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            DAOException je = new DAOException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * 取得此dataSource實體所對應的SessionFactory bean id
     *
     * @return
     */
    public String getDataSource() {
        return dataSource;
    }
}
