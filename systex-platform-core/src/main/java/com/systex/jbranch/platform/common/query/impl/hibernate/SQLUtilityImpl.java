package com.systex.jbranch.platform.common.query.impl.hibernate;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.query.helper.QueryErrMsgHelper;
import com.systex.jbranch.platform.common.query.result.impl.hibernate.ResultImpl;

/**
 * 繼承QueryUtilityBase，並以JDBC下SQL方式實作查詢動作
 */
public class SQLUtilityImpl extends QueryUtilityBase {
// ------------------------------ FIELDS ------------------------------

    private long currentTimeMillis = System.currentTimeMillis();
	private Logger logger = LoggerFactory.getLogger(SQLUtilityImpl.class);
    
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface QueryUtilityIF ---------------------


    /**
     * 請參照QueryUtilityIF
     */
    @Override
    public List executeQuery() throws DAOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_executeQuery in [%s]", this.getQueryString());

            int indexCount = 0;
            List<Object[]> list = new ArrayList<Object[]>();


            con = SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("queryString = " + queryString);
            }
            ps = con.prepareStatement(this.getQueryString(),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            setSQLParameters(ps);
            setQueryInfo(ps);
            //執行查詢
            diff("db_query in ");
            rs = ps.executeQuery();
            diff("db_query out");

            //取得欄位名稱
            ResultSetMetaData rsmt = rs.getMetaData();
            String[] columnNames = new String[rsmt.getColumnCount()];
            for (int i = 0; i < rsmt.getColumnCount(); i++) {
                columnNames[i] = rsmt.getColumnName(i + 1);
            }

            rs.last();
            int totalRecord = rs.getRow();
            int firstResult = this.getFirstResult();

            if (firstResult > 0 && totalRecord >= firstResult) {
                rs.absolute(firstResult - 1);
            }
            else {
                rs.beforeFirst();
            }

            //取得資料
            while (rs.next()) {
                //檢查查詢總比數
                if ((this.getMaxResults() > 0)
                        &&
                        (list.size() >= this.getMaxResults())) {
                    break;
                }

                if (this.getFirstResult() > 0) { //有設定起始筆數
                    if (this.getFirstResult() <= rs.getRow()) {
                        setRow(rs, list, columnNames.length);
                    }
                }
                else { //無設定起始筆數
                    setRow(rs, list, columnNames.length);
                }
            }

            Map<Object, Short> columnIndexMap = buildColumnIndexMap(columnNames);
            ResultImpl result = new ResultImpl(list, columnIndexMap, true);
            result.setColumnName(columnNames);
            return result;
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
            closeRS(rs);
            closePS(ps);
            closeConn(con);
            diff("DAO_executeQuery out");
        }
    }

    /* (non-Javadoc)
      * @see com.systex.jbranch.platform.common.query.impl.hibernate.QueryUtilityBase#exeQuery()
      */
    @Override
    public List exeQuery() throws DAOException {
        return executeQuery();
    }

    /**
     * 請參照QueryUtilityIF
     */
    public ResultIF executePaging(int page, int recordOfPage) throws DAOException {
        if (page < 1 || recordOfPage < 0) {
            throw new DAOException(QueryErrMsgHelper.PAGING_PARAMETER_ERROR);
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_executePaging in [%s]", this.getQueryString());

            List<Object[]> list = new ArrayList<Object[]>();
            if (logger.isDebugEnabled()) {
                logger.debug("queryString = " + queryString);
            }
            con = SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
            ps = con.prepareStatement(this.getQueryString(),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            setSQLParameters(ps);

            this.setFirstResult((page - 1) * recordOfPage + 1);//起始查尋的index
            this.setFetchSize(recordOfPage);//每頁筆數
            this.setMaxResults(recordOfPage);

            //執行查詢
            diff("db_query in");
            rs = ps.executeQuery();
            diff("db_query out");

            //取得欄位名稱
            ResultSetMetaData rsmt = rs.getMetaData();
            String[] columnNames = new String[rsmt.getColumnCount()];
            for (int i = 0; i < rsmt.getColumnCount(); i++) {
                columnNames[i] = rsmt.getColumnName(i + 1);
            }

            rs.last();
            int totalRecord = rs.getRow();
            int firstResult = this.getFirstResult();

            if (firstResult > 1 && totalRecord >= firstResult) {
                rs.absolute(firstResult - 1);
            }
            else {
                rs.beforeFirst();
            }

            //取得資料
            while (rs.next()) {
                //檢查查詢總比數
                if ((this.getMaxResults() > 0)
                        &&
                        (list.size() >= this.getMaxResults())) {
                    break;
                }

                if (this.getFirstResult() > 0) { //有設定起始筆數
                    setRow(rs, list, columnNames.length);
                }
                else { //無設定起始筆數
                    setRow(rs, list, columnNames.length);
                }
            }

            //===================計算總頁數 start=========================
            rs.last();
            int totalPage;
            if (recordOfPage <= 0) {
                totalPage = 1;
            }
            else if (totalRecord < recordOfPage) {
                totalPage = 1;
            }
            else if (totalRecord % recordOfPage == 0) {
                totalPage = totalRecord / recordOfPage;
            }
            else {
                totalPage = totalRecord / recordOfPage + 1;
            }
            //===================計算總頁數 end===========================

            Map<Object, Short> columnIndexMap = buildColumnIndexMap(columnNames);
            ResultImpl result = new ResultImpl(list, columnIndexMap, true);
            result.setColumnName(columnNames);
            result.setTotalPage(totalPage);
            result.setTotalRecord(totalRecord);
            return result;
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
            closeRS(rs);
            closePS(ps);
            closeConn(con);
            diff("DAO_executePaging out");
        }
    }

    /**
     * 請參照QueryUtilityIF
     */
    public ResultIF executePaging() throws DAOException {
        return executePaging(1, 0);
    }

    /**
     * 請參照QueryUtilityIF
     */
    public ResultIF executeCallableQuery() throws DAOException {
        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_executeCallableQuery in [%s]", this.getQueryString());

            con = SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("queryString = " + queryString);
            }
            cs = con.prepareCall(this.getQueryString(),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            setSQLParameters(cs);
            setQueryInfo(cs);
            registerOutParameter(cs);

            List<Object[]> list = new ArrayList<Object[]>();
            setSQLParameters(cs);
            //setQueryInfo();

            //執行查詢
            diff("db_query in");
            rs = cs.executeQuery();
            diff("db_query out");

            //取得欄位名稱
            ResultSetMetaData rsmt = rs.getMetaData();
            String[] columnNames = new String[rsmt.getColumnCount()];
            for (int i = 0; i < rsmt.getColumnCount(); i++) {
                columnNames[i] = rsmt.getColumnName(i + 1);
            }

            //取得資料
            while (rs.next()) {
                //檢查查詢總比數
                if ((this.getMaxResults() > 0)
                        &&
                        (list.size() >= this.getMaxResults())) {
                    break;
                }

                if (this.getFirstResult() > 0) { //有設定起始筆數
                    if (this.getFirstResult() <= rs.getRow()) {
                        setRow(rs, list, columnNames.length);
                    }
                    else {
                        continue;
                    }
                }
                else { //無設定起始筆數
                    setRow(rs, list, columnNames.length);
                }
            }

            Map<Object, Short> columnIndexMap = buildColumnIndexMap(columnNames);
            ResultImpl result = new ResultImpl(list, columnIndexMap, true);
            result.setColumnName(columnNames);
            setOutParameter(cs, result);
            return result;
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
            closeRS(rs);
            closePS(cs);
            closeConn(con);
            diff("DAO_executeCallableQuery out");
        }
    }
    

    /**
     * 請參照QueryUtilityIF
     * 
     */
    public Map<Integer, Object> executeCallable() throws DAOException {
    	Connection con = null;
        CallableStatement cs = null;
        Map<Integer, Object> mapReturn = new HashMap<Integer, Object>();
        
        try {
            currentTimeMillis = System.currentTimeMillis();
            diff("DAO_executeCallable in [%s]", this.getQueryString());

            con = SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
            
            if (logger.isDebugEnabled()) {
                logger.debug("queryString = " + queryString);
            }
            
            cs = con.prepareCall(this.getQueryString());

            setSQLParameters(cs);
            setQueryInfo(cs);
            registerOutParameter(cs);

            List<Object[]> list = new ArrayList<Object[]>();
            setSQLParameters(cs);
            //setQueryInfo();

            //執行查詢
            diff("db_query in");
            cs.execute();
            diff("db_query out");

//            if(!bolResult || !(this.getOutParameterType().size()>0)){
//            	return null;
//            }
            
            for(int i : this.getOutParameterType().keySet())
            {
            	mapReturn.put(i, cs.getObject(i));
            }
            
            return mapReturn;
            
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
//            closeRS(rs);
            closePS(cs);
            closeConn(con);
            diff("DAO_executeCallableQuery out");
        }
    }

    
    protected Map<Object, Short> buildColumnIndexMap(String[] columnNames) {
        Map<Object, Short> columnIndexMap = new HashMap<Object, Short>();
        for (short i = 0; i < columnNames.length; i++) {
            columnIndexMap.put(columnNames[i], i);
        }
        return columnIndexMap;
    }

    private void closeConn(Connection con) {
        if (con != null) {
            try {
                con.close();
            }
            catch (Exception e) {
                //ingore
            }
        }
    }

    private void closePS(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            }
            catch (Exception e) {
                //ingore
            }
        }
    }

    private void closeRS(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (Exception e) {
                //ingore
            }
        }
    }

    private void diff(String format, Object... args) {
        try {
            long time = System.currentTimeMillis();
            long diff = time - currentTimeMillis;
            currentTimeMillis = time;
            String value = String.format(format, args);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("%d %s", diff, value));
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void registerOutParameter(CallableStatement cs) throws DAOException {
        Map<Integer, Integer> outParameter = this.getOutParameterType();
        Map<Integer, Integer> outParamaterScale = this.getOutParameterScale();
        try {
            for (Iterator<Integer> itr = outParameter.keySet().iterator(); itr.hasNext();) {
                int index = itr.next();
                int sqlType = outParameter.get(index);
                int scale = -1;
                if (outParamaterScale.get(index) != null) {
                    scale = outParamaterScale.get(index);
                }

                if (scale != -1) {
                    cs.registerOutParameter(index, sqlType, scale);
                }
                else {
                    cs.registerOutParameter(index, sqlType);
                }
            }
        }
        catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
    }

    private void setOutParameter(CallableStatement cs, ResultImpl resultImpl) throws DAOException {
        Map<Integer, Integer> outParameter = this.getOutParameterScale();
        try {
            for (Iterator<Integer> itr = outParameter.keySet().iterator(); itr.hasNext();) {
                int index = itr.next();
                int sqlType = outParameter.get(index);

                switch (sqlType) {
                    case QueryConditionIF.SQL_TYPE_BIGINT:
                    case QueryConditionIF.SQL_TYPE_DECIMAL:
                    case QueryConditionIF.SQL_TYPE_DOUBLE:
                    case QueryConditionIF.SQL_TYPE_FLOAT:
                    case QueryConditionIF.SQL_TYPE_INTEGER:
                    case QueryConditionIF.SQL_TYPE_NUMERIC:
                    case QueryConditionIF.SQL_TYPE_SMALLINT:
                        BigDecimal bigDecimal = cs.getBigDecimal(index);
                        resultImpl.setOutParameter(index, bigDecimal);
                        continue;
                    case QueryConditionIF.SQL_TYPE_BINARY:
                        byte[] bytes = cs.getBytes(index);
                        resultImpl.setOutParameter(index, bytes);
                        continue;
                    case QueryConditionIF.SQL_TYPE_DATE:
                    case QueryConditionIF.SQL_TYPE_TIME:
                    case QueryConditionIF.SQL_TYPE_TIMESTAMP:
                        Timestamp timestamp = cs.getTimestamp(index);
                        resultImpl.setOutParameter(index, timestamp);
                        continue;
                    case QueryConditionIF.SQL_TYPE_VARCHAR:
                        String string = cs.getString(index);
                        resultImpl.setOutParameter(index, string);
                        continue;
                }
            }
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
    }

    /**
     * 設定Query參數
     */
    private void setQueryInfo(PreparedStatement ps) throws DAOException {
        try {
            if (this.getFetchSize() > 0) {
                ps.setFetchSize(this.getFetchSize());
            }
            //if( this.getMaxResults() > 0) ps.setMaxRows(this.getMaxResults());
            if (this.getTimeout() > 0) {
                ps.setQueryTimeout(this.getTimeout());
            }
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
    }

    private void setRow(ResultSet rs, List<Object[]> list, int columnCount) throws DAOException {
        try {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = rs.getObject(i + 1);
            }
            list.add(row);
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
    }

    /**
     * 設定Query字句參數
     */
    private void setSQLParameters(PreparedStatement ps) throws DAOException {
        Integer ikey;
        String skey;
        for (Iterator<Integer> itr = this.getIndexMap().keySet().iterator(); itr.hasNext();) {
            ikey = itr.next();
            setSQLValue(ps, this.getIndexType().get(ikey), this.getIndexMap().get(ikey), ikey);
        }

        //query.setCacheable(this.cacheable);
        //setQueryInfo();
    }

    private void setSQLValue(PreparedStatement ps, byte type, Object value, int index) throws DAOException {
        try {
            switch (type) {
                case TYPE_BIGDECIMAL:
                    ps.setBigDecimal(index, (BigDecimal) value);
                    return;
                case TYPE_BIGINTEGER:
                    ps.setBigDecimal(index, (BigDecimal) value);
                    return;
                case TYPE_BINARY:
                    ps.setBytes(index, (byte[]) value);
                    return;
                case TYPE_BOOLEAN:
                    ps.setBoolean(index, (Boolean) value);
                    return;
                case TYPE_BYTE:
                    ps.setByte(index, (Byte) value);
                    return;
                case TYPE_DATE:
                    ps.setDate(index, (Date) value);
                    return;
                case TYPE_DOUBLE:
                    ps.setDouble(index, (Double) value);
                    return;
                case TYPE_FLOAT:
                    ps.setFloat(index, (Float) value);
                    return;
                case TYPE_INTEGER:
                    ps.setInt(index, (Integer) value);
                    return;
                case TYPE_LONG:
                    ps.setLong(index, (Long) value);
                    return;
                case TYPE_SHORT:
                    ps.setShort(index, (Short) value);
                    return;
                case TYPE_STRING:
                    ps.setString(index, (String) value);
                    return;
                case TYPE_TIME:
                    ps.setTime(index, (Time) value);
                    return;
                case TYPE_TIMESTAMP:
                    ps.setTimestamp(index, (Timestamp) value);
                    return;
                //case TYPE_ENTITY:      ps.setEntity(index, value); return;
                default:
                    throw new DAOException(QueryErrMsgHelper.DATA_TYPE_ERROR);
            }
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
    }
}
