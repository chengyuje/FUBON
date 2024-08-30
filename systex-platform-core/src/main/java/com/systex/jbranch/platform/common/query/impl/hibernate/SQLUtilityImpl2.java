package com.systex.jbranch.platform.common.query.impl.hibernate;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.internal.AbstractScrollableResults;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.query.helper.QueryErrMsgHelper;
import com.systex.jbranch.platform.common.query.result.impl.hibernate.ResultImpl;

/**
 * 繼承QueryUtilityBase，並以Hibernate下HQL方式實作查詢動作
 * (目前尚未提供使用)
 */
public class SQLUtilityImpl2 extends SQLUtilityImpl {
// ------------------------------ FIELDS ------------------------------

	private Logger logger = LoggerFactory.getLogger(SQLUtilityImpl2.class);
    private boolean upperCase;
    
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface QueryUtilityIF ---------------------

    public List executeQuery() throws DAOException {
        //檢查QL字句
        if (this.queryString == null || this.queryString.trim().length() == 0) {
            throw new DAOException(QueryErrMsgHelper.QUERY_STRING_ERROR);
        }

        try {
            checkQuery();

            //設定參數
            // 2017/5/11
            if (this.getMaxResults() != -1)
            	this.setMaxResults(this.getMaxResults() + 1);
            setParameters();
            ResultTransformer transformer = getTransformer();
            query = query.setResultTransformer(transformer);
            List list = query.list();
            return new ResultImpl(list, null, false);
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
            clearParameters();
        }
    }

    /**
     * 請參照QueryUtilityIF
     * (目前尚未提供使用)
     */
    public ResultIF executePaging(int page, int recordOfPage) throws DAOException {
        if (this.queryString == null || this.queryString.trim().length() == 0) {
            throw new DAOException(QueryErrMsgHelper.QUERY_STRING_ERROR);
        }

        try {
        	List<Object[]> list = new ArrayList<Object[]>();
            checkQuery();
            //設定參數
            setParameters();
            query.setReadOnly(true);
//            if (recordOfPage != -1) {
//                this.query.setFetchSize(recordOfPage);
//                this.query.setMaxResults(recordOfPage);
//                this.query.setFirstResult((page - 1) * recordOfPage);
//            }
            
            ScrollableResults rs = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
            rs.last();
            int totalRecord = rs.getRowNumber() + 1;	//從0開始，與jdbc不同
            rs.beforeFirst();
            int totalPage;
            if (recordOfPage <= 0) {
                totalPage = 1;
            }else if (totalRecord < recordOfPage) {
                totalPage = 1;
            }else if (totalRecord % recordOfPage == 0) {
                totalPage = totalRecord / recordOfPage;
            }else {
                totalPage = totalRecord / recordOfPage + 1;
            }
            

//            ResultTransformer transformer = getTransformer();
//            query = query.setResultTransformer(transformer);
            
            //取得欄位名稱(暫時寫法)
            Field resultSetFiled = AbstractScrollableResults.class.getDeclaredField("resultSet");
            resultSetFiled.setAccessible(true);
            ResultSet resultSet = (ResultSet) resultSetFiled.get(rs);
            ResultSetMetaData metaData = resultSet.getMetaData();
            
            String[] columnNames = new String[metaData.getColumnCount()];
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }
            
            int firstRow = (page - 1) * recordOfPage;
            if (firstRow >= totalRecord) {
				firstRow = totalRecord;
			}
            int lastRow = firstRow + recordOfPage;
            if (lastRow >= totalRecord) {
            	lastRow = totalRecord;
			}
            if (firstRow == 0) {
				rs.beforeFirst();
			}else{
				rs.setRowNumber(firstRow - 1);//該筆之前
			}
            int scroll = firstRow;
            //取得資料
            while (rs.next() && scroll < lastRow) {
                Object[] rowData = new Object[columnNames.length];
                for (int i = 0; i < rowData.length; i++) {
					rowData[i] = rs.get(i);
				}
            	list.add(rowData);
            	scroll++;
            }
            rs.close();
            int beginRow = firstRow + 1;
            if (lastRow < beginRow) {
				beginRow = lastRow;
			}
            Map<Object, Short> columnIndexMap = buildColumnIndexMap(columnNames);
            ResultImpl result = new ResultImpl(list, columnIndexMap, true);
            result.setColumnName(columnNames);
            result.setTotalRecord(totalRecord);
            result.setTotalPage(totalPage);
            result.setBeginRow(beginRow);
            result.setEndRow(lastRow);
            return result;
        }catch (Exception e) {
        	throw exceptionMessageUtil.processException(e);
        }
        finally {
            clearParameters();
        }
    }
    
    /**
     * 請參照QueryUtilityIF
     * (目前尚未提供使用)
     */
    public ResultIF executePaging() throws DAOException {
        return executePaging(0, -1);
    }

    private void checkQuery() {
        if (query == null || changeQuery) {
            if (logger.isDebugEnabled()) {
                logger.debug("queryString = " + queryString);
            }
            query = sessionFactory.getCurrentSession().createSQLQuery(queryString);
            changeQuery = false;
        }
    }

    private ResultTransformer getTransformer() {
//        return entityClass != null ? Transformers.aliasToBean(entityClass) : Transformers.ALIAS_TO_ENTITY_MAP;
        return entityClass != null ? Transformers.aliasToBean(entityClass) : new ResultTransformer() {
            public Object transformTuple(Object[] tuple, String[] aliases) {
                int length = tuple.length;
                Map<String, Object> result = new HashMap<String, Object>(length);
                for (int i = 0; i < length; i++) {
                    String alias = aliases[i];
                    if (alias != null) {
                        alias = upperCase ? StringUtils.upperCase(alias) : alias;
                        result.put(alias, tuple[i]);
                    }
                }
                return result;
            }

            public List transformList(List collection) {
                return collection;
            }
        };
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }
}