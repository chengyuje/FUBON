package com.systex.jbranch.platform.common.dataaccess.delegate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanMap;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;
import com.systex.jbranch.platform.common.dataaccess.dao.DaoProxyIF;
import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.ErrMsgHelper;
import com.systex.jbranch.platform.common.dataaccess.daomanager.DaoManagerIF;
import com.systex.jbranch.platform.common.dataaccess.datasource.DataSource;
import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityFactoryIF;
import com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF;
import com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityProxyIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.query.ScrollableQueryResultIF;
import com.systex.jbranch.platform.common.dataaccess.transaction.IndependentTransactionIF;
import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.util.PlatformContext;


/**
 * DataAccessManager為控制資料存取的主要類別，
 * 此類別負責與DataSource作連結，
 * 藉由此類別可執行對資料庫的存取與直接下Query Language來做操作
 */
public class DataAccessManager {
// ------------------------------ FIELDS ------------------------------

    public static final String TURN_LOG = "TURN_LOG";

    public static final byte QUERY_LANGUAGE_TYPE_HQL = DataAccessHelper.QUERY_LANGUAGE_TYPE_HQL;
    public static final byte QUERY_LANGUAGE_TYPE_SQL = DataAccessHelper.QUERY_LANGUAGE_TYPE_SQL;
    public static final byte QUERY_LANGUAGE_TYPE_VAR_SQL = DataAccessHelper.QUERY_LANGUAGE_TYPE_VAR_SQL;

    private static Set<String> IGNORE_FIELDS = new HashSet<String>();

    private DaoManagerIF daoManager;
    private QueryUtilityFactoryIF queryUtilityFactory;
    private DaoProxyIF daoProxy;
    private QueryUtilityProxyIF queryUtilityProxy;
    private IndependentTransactionIF independentTransaction;
    private Hashtable<String, DaoIF> daoMap = new Hashtable<String, DaoIF>();

    private byte transactionManagement = DataAccessHelper.TRANSACTION_MANAGEMENT_JTA;
    private DataSource dataSource;
    private boolean autoCommit = false;
    private long currentTimeMillis = System.currentTimeMillis();
	private Logger logger = LoggerFactory.getLogger(DataAccessManager.class);

// -------------------------- STATIC METHODS --------------------------

    static {
        IGNORE_FIELDS.add("class");
        IGNORE_FIELDS.add("tableuid");
        IGNORE_FIELDS.add("comp_id");
    }

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * 預設Contrustor，連結到預設的DataSource
     *
     * @throws DAOException
     * @throws JBranchException
     */
    public DataAccessManager() throws DAOException, JBranchException {
        dataSource = new com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
        daoManager = (DaoManagerIF) PlatformContext.getBean(DataAccessHelper.DAOMANAGER_ID);
        independentTransaction = (IndependentTransactionIF) PlatformContext.getBean(DataAccessHelper.TRANSACTION_ID);
        //transaction = (TransactionIF) PlatformContext.getBean(DataAccessHelper.TRANSACTION_ID);
        queryUtilityFactory = (QueryUtilityFactoryIF) PlatformContext.getBean(DataAccessHelper.QUERY_UTILITY_FACTORY_ID);
        daoProxy = (DaoProxyIF) PlatformContext.getBean(DataAccessHelper.DAO_PROXY_ID);
        queryUtilityProxy = (QueryUtilityProxyIF) PlatformContext.getBean(DataAccessHelper.QUERY_UTILITY_PROXY_ID);
        checkBean();
    }

    public DataAccessManager(boolean forSpring) {
    }

    /**
     * 連結到指定DataSource的Contrustor
     *
     * @param dataSourceId
     * @throws DAOException
     * @throws JBranchException
     */
    public DataAccessManager(String dataSourceId) throws DAOException, JBranchException {
        dataSource = new com.systex.jbranch.platform.common.dataaccess.datasource.DataSource(dataSourceId);
        daoManager = (DaoManagerIF) PlatformContext.getBean(DataAccessHelper.DAOMANAGER_ID);
        independentTransaction = (IndependentTransactionIF) PlatformContext.getBean(DataAccessHelper.TRANSACTION_ID);
        //transaction = (TransactionIF) PlatformContext.getBean(DataAccessHelper.TRANSACTION_ID);
        queryUtilityFactory = (QueryUtilityFactoryIF) PlatformContext.getBean(DataAccessHelper.QUERY_UTILITY_FACTORY_ID);
        daoProxy = (DaoProxyIF) PlatformContext.getBean(DataAccessHelper.DAO_PROXY_ID);
        queryUtilityProxy = (QueryUtilityProxyIF) PlatformContext.getBean(DataAccessHelper.QUERY_UTILITY_PROXY_ID);
        checkBean();
    }

    /**
     * 指定使用者的資料庫連線，會依據UUID來區分該使用的帳號密碼連線資訊，區分的規則依照所實作的DataSourceRule
     *
     * @param uuid
     * @throws DAOException
     * @throws JBranchException
     */
    public DataAccessManager(UUID uuid) throws DAOException, JBranchException {
        dataSource = new com.systex.jbranch.platform.common.dataaccess.datasource.DataSource(uuid);
        daoManager = (DaoManagerIF) PlatformContext.getBean(DataAccessHelper.DAOMANAGER_ID);
        independentTransaction = (IndependentTransactionIF) PlatformContext.getBean(DataAccessHelper.TRANSACTION_ID);
        //transaction = (TransactionIF) PlatformContext.getBean(DataAccessHelper.TRANSACTION_ID);
        queryUtilityFactory = (QueryUtilityFactoryIF) PlatformContext.getBean(DataAccessHelper.QUERY_UTILITY_FACTORY_ID);
        daoProxy = (DaoProxyIF) PlatformContext.getBean(DataAccessHelper.DAO_PROXY_ID);
        queryUtilityProxy = (QueryUtilityProxyIF) PlatformContext.getBean(DataAccessHelper.QUERY_UTILITY_PROXY_ID);
        checkBean();
    }

    /**
     * 連結到指定的DataSource，並利用UUID來區分此連到此DataSource所使用的帳號密碼連線資訊
     *
     * @param dbID
     * @param uuid
     * @throws DAOException
     * @throws JBranchException
     */
    public DataAccessManager(String dbID, UUID uuid) throws DAOException, JBranchException {
        dataSource = new com.systex.jbranch.platform.common.dataaccess.datasource.DataSource(dbID, uuid);
        daoManager = (DaoManagerIF) PlatformContext.getBean(DataAccessHelper.DAOMANAGER_ID);
        independentTransaction = (IndependentTransactionIF) PlatformContext.getBean(DataAccessHelper.TRANSACTION_ID);
        //transaction = (TransactionIF) PlatformContext.getBean(DataAccessHelper.TRANSACTION_ID);
        queryUtilityFactory = (QueryUtilityFactoryIF) PlatformContext.getBean(DataAccessHelper.QUERY_UTILITY_FACTORY_ID);
        daoProxy = (DaoProxyIF) PlatformContext.getBean(DataAccessHelper.DAO_PROXY_ID);
        queryUtilityProxy = (QueryUtilityProxyIF) PlatformContext.getBean(DataAccessHelper.QUERY_UTILITY_PROXY_ID);
        checkBean();
    }

    /**
     * 檢查Spring中與dataAccess相關的bean是否正常取得
     *
     * @throws DAOException
     */
    private void checkBean() throws DAOException {
        if (dataSource == null) {
            throw new DAOException(ErrMsgHelper.DATAACCESS_BEANS_NOT_INIT);
        }
        if (daoManager == null) {
            throw new DAOException(ErrMsgHelper.DATAACCESS_BEANS_NOT_INIT);
        }
        if (independentTransaction == null) {
            throw new DAOException(ErrMsgHelper.DATAACCESS_BEANS_NOT_INIT);
        }
        if (queryUtilityFactory == null) {
            throw new DAOException(ErrMsgHelper.DATAACCESS_BEANS_NOT_INIT);
        }
        if (daoProxy == null) {
            throw new DAOException(ErrMsgHelper.DATAACCESS_BEANS_NOT_INIT);
        }
        if (queryUtilityProxy == null) {
            throw new DAOException(ErrMsgHelper.DATAACCESS_BEANS_NOT_INIT);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * 在資料庫中建立一筆資料(insert)
     *
     * @param vo 資料物件
     * @return 回應此筆資料物件
     * @throws DAOException
     * @throws JBranchException
     */
    public Object create(Object vo) throws DAOException, JBranchException {
        try {
            if (vo == null || !(vo instanceof VOBase)) {
                throw new DAOException(ErrMsgHelper.VO_COULD_NOT_BE_NULL);
            }
            VOBase vob = (VOBase) vo;
            String tableuid = vob.getTableuid();

            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_create in [%s]", tableuid);

            if (autoCommit) {
                return daoProxy.newTransactionCreate(getDao(tableuid), vo);
            }
            else {
                return daoProxy.create(getDao(tableuid), vo);
            }
        }
        finally {
            diff("DAO_create out");
        }
    }

    /**
     * 刪除一筆資料庫資料(update)
     *
     * @param vo 資料物件
     * @return 回應此筆資料物件
     * @throws DAOException
     * @throws JBranchException
     */
    public void delete(Object vo) throws DAOException {
        try {
            if (vo == null || !(vo instanceof VOBase)) {
                throw new DAOException(ErrMsgHelper.VO_COULD_NOT_BE_NULL);
            }
            VOBase vob = (VOBase) vo;
            String tableuid = vob.getTableuid();

            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_delete in [%s]", tableuid);

            if (autoCommit) {
                daoProxy.newTransactionDelete(getDao(tableuid), vo);
            }
            else {
                daoProxy.delete(getDao(tableuid), vo);
            }
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
        finally {
            diff("DAO_delete out");
        }
    }

    /**
     * 執行查詢條件
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public List exeQuery(QueryConditionIF queryCondition) throws DAOException, JBranchException {
    	//Add: 2016/11/17 ArthurKO Add Sorter Class to Modify SQL Condition.
    	queryCondition = DataSortManager.getSorting(queryUtilityProxy, queryCondition);
        return queryUtilityProxy.exeQuery((QueryUtilityIF) queryCondition);
    }
    
    /**
     * 執行查詢不檢查排序
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public List exeQueryWithoutSort(QueryConditionIF queryCondition) throws DAOException, JBranchException {
        return queryUtilityProxy.exeQuery((QueryUtilityIF) queryCondition);
    }

    /**
     * 執行分頁查詢不檢查排序(所有資料都顯示在一頁)
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public ResultIF executePagingWithoutSort(QueryConditionIF queryCondition) throws DAOException, JBranchException {
     return queryUtilityProxy.executePaging((QueryUtilityIF) queryCondition);
    }

    /**
     * 執行更新條件
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public int exeUpdate(QueryConditionIF queryCondition) throws DAOException, JBranchException {
        if (autoCommit) {
            return queryUtilityProxy.newTransactionExeUpdate((QueryUtilityIF) queryCondition);
        }
        else {
            return queryUtilityProxy.exeUpdate((QueryUtilityIF) queryCondition);
        }
    }

    /**
     * 執行更新條件
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public ResultIF executeCallableQuery(QueryConditionIF queryCondition) throws DAOException, JBranchException {
        if (autoCommit) {
            return queryUtilityProxy.newTransactionExecuteCallableQuery((QueryUtilityIF) queryCondition);
        }
        else {
            return queryUtilityProxy.executeCallableQuery((QueryUtilityIF) queryCondition);
        }
    }
    
    /**
     * 執行更新條件
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public Map<Integer, Object> executeCallable(QueryConditionIF queryCondition) throws DAOException, JBranchException {
        if (autoCommit) {
            return queryUtilityProxy.newTransactionExecuteCallable((QueryUtilityIF) queryCondition);
        }
        else {
            return queryUtilityProxy.executeCallable((QueryUtilityIF) queryCondition);
        }
    }

    /**
     * 執行分頁查詢(所有資料都顯示在一頁)
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public ResultIF executePaging(QueryConditionIF queryCondition) throws DAOException, JBranchException {
    	//Add: 2016/11/17 ArthurKO Add Sorter Class to Modify SQL Condition.
    	queryCondition = DataSortManager.getSorting(queryUtilityProxy, queryCondition);
        return queryUtilityProxy.executePaging((QueryUtilityIF) queryCondition);
    }

    /**
     * 執行查詢條件
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public ResultIF executePaging(QueryConditionIF queryCondition, int pageNumber, int recordOfPage) throws DAOException, JBranchException {
    	//Add: 2016/11/17 ArthurKO Add Sorter Class to Modify SQL Condition.
    	queryCondition = DataSortManager.getSorting(queryUtilityProxy, queryCondition);
        return queryUtilityProxy.executePaging((QueryUtilityIF) queryCondition, pageNumber, recordOfPage);
    }

    /**
     * 執行查詢條件
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    @Deprecated
    public List executeQuery(QueryConditionIF queryCondition) throws DAOException, JBranchException {
    	//Add: 2016/11/17 ArthurKO Add Sorter Class to Modify SQL Condition.
    	queryCondition = DataSortManager.getSorting(queryUtilityProxy, queryCondition);
        return queryUtilityProxy.executeQuery((QueryUtilityIF) queryCondition);
    }

    /**
     * 執行查詢條件，並回傳Scrollable的資料物件
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public ScrollableQueryResultIF executeScrollableQuery(QueryConditionIF queryCondition) throws DAOException, JBranchException {
        return queryUtilityProxy.executeScrollableQuery((QueryUtilityIF) queryCondition);
    }

    /**
     * 執行更新條件
     *
     * @param queryCondition
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    @Deprecated
    public int executeUpdate(QueryConditionIF queryCondition) throws DAOException, JBranchException {
        if (autoCommit) {
            return queryUtilityProxy.newTransactionExecuteUpdate((QueryUtilityIF) queryCondition);
        }
        else {
            return queryUtilityProxy.executeUpdate((QueryUtilityIF) queryCondition);
        }
    }

    /**
     * Select 指定table內的所有資料，若沒有資料，將回傳Size為0的List
     *
     * @param tableUid table的id
     * @return 找到的資料
     * @throws NotFoundException
     * @throws DAOException
     */
    public List findAll(String tableUid) throws NotFoundException, DAOException {
        try {
            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_findAll in [%s]", tableUid);

            if (tableUid == null || tableUid.trim().length() == 0) {
                throw new DAOException(ErrMsgHelper.TABLE_UID_ERROR);
            }
            if(autoCommit){
            	return daoProxy.newTransactionFindAll(getDao(tableUid));
            }else{
            	return daoProxy.findAll(getDao(tableUid));
            }
        }
        catch (NotFoundException e) {
            throw e;
        }
        catch (DAOException e) {
            throw e;
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
        finally {
            diff("DAO_findAll out");
        }
    }

    /**
     * Select 指定table內的所有資料，若沒有資料，將回傳Size為0的List
     *
     * @param vo 指定的欄位條件值
     * @return 找到符合條件的所有資料
     * @throws NotFoundException
     * @throws DAOException
     */
    public List findByFields(VOBase vo) throws NotFoundException, DAOException {
        try {
            if (vo == null) {
                throw new DAOException(ErrMsgHelper.ENTITY_COULD_NOT_BE_NULL);
            }

            BeanMap voMap = new BeanMap(vo);
            List<String> fields = new ArrayList<String>();
            Iterator<String> it = voMap.keyIterator();
            String key = null;
            Object value = null;
            while (it.hasNext()) {
                key = it.next();

                if (IGNORE_FIELDS.contains(key)) {
                    continue;
                }

                value = voMap.get(key);
                if (value != null) {
                    fields.add(key);
                }
            }

            return findByFields(vo, fields.toArray(new String[fields.size()]));
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
    }

    /**
     * Select 指定table內的所有資料，若沒有資料，將回傳Size為0的List
     *
     * @param vo     指定的欄位條件值
     * @param fields 要找尋的欄位
     * @return 找到符合條件的所有資料
     * @throws NotFoundException
     * @throws DAOException
     */
    public List findByFields(Object vo, String... fields) throws NotFoundException, DAOException {
        try {
            if (vo == null || !(vo instanceof VOBase)) {
                throw new DAOException(ErrMsgHelper.ENTITY_COULD_NOT_BE_NULL);
            }
            if (fields == null || fields.length == 0) {
                throw new DAOException(ErrMsgHelper.FIELD_MAP_ERROR);
            }
            VOBase vob = (VOBase) vo;
            String tableuid = vob.getTableuid();

            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_findByFields in [%s]", tableuid);
            
            if(autoCommit){
            	return daoProxy.newTransactionFindByFields(getDao(tableuid), vo, fields);
            }else{
            	return daoProxy.findByFields(getDao(tableuid), vo, fields);
            }
        }
        catch (NotFoundException e) {
            throw e;
        }
        catch (DAOException e) {
            throw e;
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
        finally {
            diff("DAO_findByFields out");
        }
    }
    
    public List findByCriteria(String tableuid, List<Criterion> criterions) throws JBranchException {
    	
    	return findByCriteria(tableuid, criterions, null);
    }
    
    public List findByCriteria(String tableuid, List<Criterion> criterions, List<Order> orders) throws JBranchException {
    	
        if(autoCommit){
        	return daoProxy.newTransactionFindByCriteria(getDao(tableuid), criterions, orders);
        }else{
        	return daoProxy.findByCriteria(getDao(tableuid), criterions, orders);
        }
    }
    
    public List findByCriteria(String tableuid, List<Criterion> criterions, List<Order> orders, int firstResult, int maxResults) throws JBranchException {
    	
        if(autoCommit){
        	return daoProxy.newTransactionFindByCriteria(getDao(tableuid), criterions, orders, firstResult, maxResults);
        }else{
        	return daoProxy.findByCriteria(getDao(tableuid), criterions, orders, firstResult, maxResults);
        }
    }

    /**
     * 取得Dao物件
     *
     * @param daoId
     * @return Object
     * @throws DAOException, JBranchException
     */
    private DaoIF getDao(String tableUid) throws DAOException, JBranchException {
        if (daoMap.get(tableUid) == null) {
            DaoIF dao = (DaoIF) daoManager.getDao(transactionManagement, dataSource, tableUid);
            daoMap.put(tableUid, dao);
            return dao;
        }
        else {
            return daoMap.get(tableUid);
        }
    }

    private void diff(String format, Object... args) {
        try {
            long time = System.currentTimeMillis();
            long diff = time - currentTimeMillis;
            currentTimeMillis = time;
            String value = String.format(format, args);
            logger.debug(String.format("%d %s", diff, value));
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 用Primary Key去Select table資料並Lock住此筆資料，若找不到，將回傳null
     *
     * @param tableUid table的id
     * @param pKey     primary key
     * @param lock     是否要lock此筆資料(row lock)
     * @return 找到的資料
     * @throws NotFoundException
     * @throws DAOException
     */
    public Object findByPKey(String tableUid, Serializable pKey) throws NotFoundException, DAOException {
        try {
            if (tableUid == null || tableUid.trim().length() == 0) {
                throw new DAOException(ErrMsgHelper.TABLE_UID_ERROR);
            }
            if (pKey == null) {
                throw new DAOException(ErrMsgHelper.PRIMAVLY_KEY_COULD_NOT_BE_NULL);
            }
            logger.info("findByPkey: tableUid={}, pKey={}, autoCommit={}", tableUid, pKey, autoCommit);
            if(autoCommit){
            	return daoProxy.newTransactionFindByPKey(getDao(tableUid), pKey);
            }else{
            	return daoProxy.findByPKey(getDao(tableUid), pKey);
            }
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
    }

    /**
     * 用Primary Key去Select table資料並Lock住此筆資料，若找不到，將回傳null
     *
     * @param tableUid table的id
     * @param pKey     primary key
     * @param lock     是否要lock此筆資料(row lock)
     * @return 找到的資料
     * @throws NotFoundException
     * @throws DAOException
     */
    public Object findByPKey(String tableUid, Serializable pKey, boolean lock) throws NotFoundException,
            DAOException {
        try {
            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_findByPKey in [%s]", tableUid);

            if (tableUid == null || tableUid.trim().length() == 0) {
                throw new DAOException(ErrMsgHelper.TABLE_UID_ERROR);
            }
            if (pKey == null) {
                throw new DAOException(ErrMsgHelper.PRIMAVLY_KEY_COULD_NOT_BE_NULL);
            }
            if(autoCommit){
            	return daoProxy.newTransactionFindByPKey(getDao(tableUid), pKey, lock);
            }else{
            	return daoProxy.findByPKey(getDao(tableUid), pKey, lock);
            }
        }
        catch (NotFoundException e) {
            throw e;
        }
        catch (DAOException e) {
            throw e;
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
        finally {
            //diff("DAO_findByPKey out");
        }
    }

    /**
     * 取得Hibernate的Criteria物件
     *
     * @param tableUid
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    @Deprecated
    public Criteria getHibernateCriteria(String tableUid) throws DAOException, JBranchException {
        try {
            return daoManager.getHibernateCriteria(transactionManagement, dataSource, tableUid);
        }
        catch (DAOException e) {
            throw e;
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
    }

    /**
     * 取得SQL查詢條件
     *
     * @param queryLanguageType
     * @return 查詢條件物件
     * @throws DAOException
     * @throws JBranchException
     */
    public QueryConditionIF getQueryCondition() throws DAOException, JBranchException {
        return (QueryConditionIF) queryUtilityFactory.getQueryUtility(QUERY_LANGUAGE_TYPE_SQL, dataSource, transactionManagement);
    }

    /**
     * 取得查詢條件
     *
     * @param queryLanguageType
     * @return 查詢條件物件
     * @throws DAOException
     * @throws JBranchException
     */
    public QueryConditionIF getQueryCondition(byte queryLanguageType) throws DAOException, JBranchException {
        try {
            return (QueryConditionIF) queryUtilityFactory.getQueryUtility(queryLanguageType, dataSource, transactionManagement);
        }
        catch (DAOException e) {
            throw e;
        }
        catch (JBranchException e) {
            throw e;
        }
    }

    /**
     * 利用獨立的Transaction來執行所指定的物件method，此method執行完成，將不受外部是否commit或rollback影響，
     * 若執行失敗也不影響外部Transaction的成功或失敗。
     *
     * @param target
     * @param methodName
     * @return
     * @throws DAOException
     */
    public Object newTransactionExeMethod(Object target, String methodName) throws DAOException {
        if (target == null) {
            throw new DAOException(ErrMsgHelper.TARGET_OBJECT_IS_NULL);
        }
        return independentTransaction.executeMethod(target, methodName);
    }

    /**
     * 利用獨立的Transaction來執行所指定的物件method，此method執行完成，將不受外部是否commit或rollback影響，
     * 若執行失敗也不影響外部Transaction的成功或失敗。
     *
     * @param target
     * @param methodName
     * @param args       method所需傳入的參數，塞入list
     * @return
     * @throws DAOException
     */
    public Object newTransactionExeMethod(Object target, String methodName, List args) throws DAOException {
        if (target == null) {
            throw new DAOException(ErrMsgHelper.TARGET_OBJECT_IS_NULL);
        }
        return independentTransaction.executeMethod(target, methodName, args);
    }

    /**
     * 更新一筆資料庫資料(update)
     *
     * @param vo 資料物件
     * @return 回應此筆資料物件
     * @throws DAOException
     * @throws JBranchException
     */
    public Object update(Object vo) throws DAOException {
        try {
            if (vo == null || !(vo instanceof VOBase)) {
                throw new DAOException(ErrMsgHelper.VO_COULD_NOT_BE_NULL);
            }
            VOBase vob = (VOBase) vo;
            String tableuid = vob.getTableuid();

            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_update in [%s]", tableuid);

            if (autoCommit) {
                return daoProxy.newTransactionUpdate(getDao(tableuid), vo);
            }
            else {
                return daoProxy.update(getDao(tableuid), vo);
            }
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
        finally {
            diff("DAO_update out");
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * @return autoCommit
     */
    public boolean isAutoCommit() {
        return this.autoCommit;
    }

    /**
     * 設定AutoCommit，一旦設定以後，每一個對資料庫的select、insert、update都是獨立的transaction
     *
     * @param autoCommit
     */
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setDaoManager(DaoManagerIF daoManager) {
        this.daoManager = daoManager;
    }

    public void setDaoProxy(DaoProxyIF daoProxy) {
        this.daoProxy = daoProxy;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setIndependentTransaction(IndependentTransactionIF independentTransaction) {
        this.independentTransaction = independentTransaction;
    }

    public void setQueryUtilityFactory(QueryUtilityFactoryIF queryUtilityFactory) {
        this.queryUtilityFactory = queryUtilityFactory;
    }

    public void setQueryUtilityProxy(QueryUtilityProxyIF queryUtilityProxy) {
        this.queryUtilityProxy = queryUtilityProxy;
    }
}
