package com.systex.jbranch.platform.common.query.impl.hibernate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.query.ScrollableQueryResultIF;
import com.systex.jbranch.platform.common.dataaccess.util.ExceptionMessageUtil;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.query.helper.QueryErrMsgHelper;
import com.systex.jbranch.platform.common.query.result.impl.hibernate.ResultImpl;


/**
 * 以Hibernate實作QueryUtilityIF、QueryConditionIF介面
 * 此為一個抽象類別
 */
public abstract class QueryUtilityBase implements QueryUtilityIF, QueryConditionIF {
// ------------------------------ FIELDS ------------------------------

    private static final String MARK = "\"";

    protected static final byte TYPE_BIGDECIMAL = 1;
    protected static final byte TYPE_BIGINTEGER = 2;
    protected static final byte TYPE_BINARY = 3;
    protected static final byte TYPE_BOOLEAN = 4;
    protected static final byte TYPE_BYTE = 5;
    protected static final byte TYPE_DATE = 6;
    protected static final byte TYPE_DOUBLE = 7;
    protected static final byte TYPE_FLOAT = 8;
    protected static final byte TYPE_INTEGER = 9;
    protected static final byte TYPE_LONG = 10;
    protected static final byte TYPE_SHORT = 11;
    protected static final byte TYPE_STRING = 12;
    protected static final byte TYPE_TIME = 13;
    protected static final byte TYPE_TIMESTAMP = 14;

    protected String queryString;
    protected SessionFactory sessionFactory;
    protected Query query;
    protected boolean changeQuery = false;
    protected Class entityClass;
    private int timeout = -1;
    private boolean cacheable = false;
    private int firstResult = -1;
    private int maxResults = -1;
    private int fetchSize = -1;
    private byte queryType = -1;

    private Map<Integer, Object> indexMap = new HashMap<Integer, Object>();
    private Map<Integer, Byte> indexMapType = new HashMap<Integer, Byte>();
    private Map<String, Object> parameters = new HashMap<String, Object>();

    private Map<Integer, Integer> outParameter = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> outParamaterScale = new HashMap<Integer, Integer>();
	private Logger logger = LoggerFactory.getLogger(QueryUtilityBase.class);

    final protected static ExceptionMessageUtil exceptionMessageUtil = new ExceptionMessageUtil();
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface QueryConditionIF ---------------------

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setBigDecimal(int position, BigDecimal value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_BIGDECIMAL);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setBigInteger(int position, BigInteger value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_BIGINTEGER);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setBinary(int position, byte[] value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_BINARY);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setBoolean(int position, boolean value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_BOOLEAN);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setByte(int position, byte value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_BYTE);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setDate(int position, Date value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_DATE);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setDouble(int position, double value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_DOUBLE);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setFloat(int position, float value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_FLOAT);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setInteger(int position, int value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_INTEGER);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setLong(int position, long value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_LONG);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setShort(int position, short value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_SHORT);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setString(int position, String value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_STRING);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setTime(int position, Time value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_TIME);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setTimestamp(int position, Timestamp value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_TIMESTAMP);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF registerOutParameter(int parameterIndex, int sqlType) {
        outParameter.put(parameterIndex, sqlType);
        return this;
    }

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF registerOutParameter(int parameterIndex, int sqlType, int scale) {
        outParameter.put(parameterIndex, sqlType);
        outParamaterScale.put(parameterIndex, scale);
        return this;
    }

    /**
     * 設定資料庫操作的Timeout
     */
    public QueryConditionIF setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 設定資料庫操作是否要cache
     */
    public QueryConditionIF setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
        return this;
    }

    /**
     * 設定資料庫連線Fetch size
     */
    public QueryConditionIF setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    /**
     * 設定回傳的查詢結果從第幾筆開始
     */
    public QueryConditionIF setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    /**
     * 設定查詢結果最多回傳的筆數
     */
    public QueryConditionIF setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    /**
     * 設定查詢語句
     */
    public QueryConditionIF setQueryString(String queryString) {
        this.queryString = queryString;
        this.changeQuery = true;
        return this;
    }

    /**
     * 取得查詢語句
     */
    public String getQueryString() {
        return queryString;
    }

    public QueryConditionIF setObject(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

// --------------------- Interface QueryUtilityIF ---------------------

    /* (non-Javadoc)
      * @see com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF#executeQuery()
      */
    public List executeQuery() throws DAOException {
        //檢查QL字句
        if (this.queryString == null || this.queryString.trim().length() == 0) {
            throw new DAOException(QueryErrMsgHelper.QUERY_STRING_ERROR);
        }

        try {
            //queryString = queryString.replaceAll("'", mark);
            if (query == null || changeQuery) {
                if (logger.isDebugEnabled()) {
                    logger.debug("queryString = " + queryString);
                }
                query = sessionFactory.getCurrentSession().createQuery(queryString);
                changeQuery = false;
            }

            //設定參數
            setParameters();
            List list = query.list();
            return new ResultImpl(list, null, false);
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
            clearParameters();
        }
    }

    /* (non-Javadoc)
      * @see com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF#exeQuery()
      */
    public List exeQuery() throws DAOException {
        return executeQuery();
    }

    /**
     * 執行ScrollableQuery
     * (目前尚不支援此功能)
     */
    public ScrollableQueryResultIF executeScrollableQuery() throws DAOException {
        throw new DAOException(QueryErrMsgHelper.UNSUPPORTED_OPERATION);
    }

    /* (non-Javadoc)
      * @see com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF#executeUpdate()
      */
    public int executeUpdate() throws DAOException {
        //檢查QL字句
        if (this.queryString == null || this.queryString.trim().length() == 0) {
            throw new DAOException(QueryErrMsgHelper.QUERY_STRING_ERROR);
        }

        try {
            if (query == null || changeQuery) {
                if (logger.isDebugEnabled()) {
                    logger.debug("queryString = " + queryString);
                }
                if (queryType == DataAccessHelper.QUERY_LANGUAGE_TYPE_HQL) {
                    query = sessionFactory.getCurrentSession().createQuery(queryString);
                    changeQuery = false;
                }
                else {
                    query = sessionFactory.getCurrentSession().createSQLQuery(queryString);
                    changeQuery = false;
                }
            }

            //設定參數
            setParameters();
            return query.executeUpdate();
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
            clearParameters();
        }
    }

    /* (non-Javadoc)
      * @see com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityIF#exeUpdate()
      */
    public int exeUpdate() throws DAOException {
        //檢查QL字句
        if (this.queryString == null || this.queryString.trim().length() == 0) {
            throw new DAOException(QueryErrMsgHelper.QUERY_STRING_ERROR);
        }

        try {
            if (query == null || changeQuery) {
                if (logger.isDebugEnabled()) {
                    logger.debug("queryString = " + queryString);
                }
                if (queryType == DataAccessHelper.QUERY_LANGUAGE_TYPE_HQL) {
                    query = sessionFactory.getCurrentSession().createQuery(queryString);
                    changeQuery = false;
                }
                else {
                    query = sessionFactory.getCurrentSession().createSQLQuery(queryString);
                    changeQuery = false;
                }
            }

            //設定參數
            setParameters(true);
            return query.executeUpdate();
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
            clearParameters();
        }
    }

    public abstract ResultIF executeCallableQuery() throws DAOException;
    
    public abstract Map<Integer, Object> executeCallable() throws DAOException;

// -------------------------- OTHER METHODS --------------------------

    /**
     * 請參考QueryConditionIF
     */
    public QueryConditionIF setEntity(int position, Object value) {
        indexMap.put(position, value);
        indexMapType.put(position, TYPE_TIMESTAMP);
        return this;
    }

    /**
     * 清除參數
     */
    protected void clearParameters() {
        indexMap.clear();
        indexMapType.clear();
    }

    /**
     * 取得參數型態map
     *
     * @return
     */
    protected Map<Integer, Byte> getIndexType() {
        return indexMapType;
    }

    /**
     * 取得output參數的scale
     *
     * @return
     */
    protected Map<Integer, Integer> getOutParameterScale() {
        return outParamaterScale;
    }

    /**
     * 取得output參數的資料型態
     *
     * @return
     */
    protected Map<Integer, Integer> getOutParameterType() {
        return outParameter;
    }

    /**
     * 設定查詢語句flag成未修改
     */
    protected void resetChangeQuery() {
        changeQuery = false;
    }

    /**
     * 設定Query字句參數
     */
    protected void setParameters() throws DAOException {
        setParameters(false);
    }

    private void setParameters(boolean isReduce) throws DAOException {
        for (Integer ikey : indexMap.keySet()) {
            int idx = ikey;
            if (isReduce) {
                idx--;
            }
            setValue(query, indexMapType.get(ikey), indexMap.get(ikey), idx);
        }

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            setValue(query, entry.getValue(), entry.getKey());
        }
        if(query instanceof SQLQuery){
        	if(this.cacheable && this.entityClass != null){
            	query.setCacheable(this.cacheable);
                ((SQLQuery) query).addEntity(entityClass);
        	}
        }else{
        	query.setCacheable(this.cacheable);
        }
        
        if (timeout != -1) {
            query.setTimeout(this.timeout);
        }
        if (firstResult != -1) {
            query.setFirstResult(this.firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(this.maxResults);
        }
        if (fetchSize != -1) {
            query.setFetchSize(this.fetchSize);
        }
    }

    /**
     * 設定參數值
     *
     * @param query
     * @param type
     * @param value
     * @param index
     * @throws DAOException
     */
    protected void setValue(Query query, byte type, Object value, int index) throws DAOException {
        query.setParameter(index, value);
    }

    private void setValue(Query query, Object value, String name) {
        if (value == null) {
            query.setParameter(name, value);
        }
        else if (value.getClass().isArray()) {
            query.setParameterList(name, (Object[]) value);
        }
        else if (Collection.class.isInstance(value)) {
            query.setParameterList(name, (Collection) value);
        }
        else {
            query.setParameter(name, value);
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * 取得參數map
     *
     * @return
     */
    protected Map<Integer, Object> getIndexMap() {
        return indexMap;
    }

    /**
     * 取得此Query物件的資料庫連線SessionFactory
     *
     * @return
     */
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * 設定資料庫連線的SessionFactory
     *
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 判斷查詢語句是否有換過
     *
     * @return
     */
    protected boolean isChangeQuery() {
        return changeQuery;
    }

    /**
     * 取得資料庫連線Fetch size
     *
     * @return
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * 取得回傳的查詢結果從第幾筆開始
     *
     * @return
     */
    public int getFirstResult() {
        return firstResult;
    }

    /**
     * 取得查詢結果最多回傳的筆數
     *
     * @return
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * 取得資料庫操作的Timeout
     *
     * @return
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 設定查詢語法種類
     * DataAccessManager.QUERY_LANGUAGE_TYPE_HQL or
     * DataAccessManager.QUERY_LANGUAGE_TYPE_SQL
     *
     * @param queryType
     */
    public void setQueryType(byte queryType) {
        this.queryType = queryType;
    }
}
