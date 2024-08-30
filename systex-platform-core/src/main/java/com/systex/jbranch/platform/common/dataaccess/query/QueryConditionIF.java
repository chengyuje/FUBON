package com.systex.jbranch.platform.common.dataaccess.query;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * 定義複雜查詢介面
 * 也就是使用SQL或是HQL的方式操作資料庫的介面
 *
 */
public interface QueryConditionIF {

	int SQL_TYPE_BIGINT      = java.sql.Types.BIGINT;
	int SQL_TYPE_BINARY      = java.sql.Types.BINARY;
	int SQL_TYPE_DATE        = java.sql.Types.DATE;
	int SQL_TYPE_DECIMAL     = java.sql.Types.DECIMAL;
	int SQL_TYPE_DOUBLE      = java.sql.Types.DOUBLE;
	int SQL_TYPE_FLOAT 	     = java.sql.Types.FLOAT;
	int SQL_TYPE_INTEGER     = java.sql.Types.INTEGER;
	int SQL_TYPE_NUMERIC     = java.sql.Types.NUMERIC;
	int SQL_TYPE_SMALLINT    = java.sql.Types.SMALLINT;
	int SQL_TYPE_TIME        = java.sql.Types.TIME;
	int SQL_TYPE_TIMESTAMP   = java.sql.Types.TIMESTAMP;
	int SQL_TYPE_VARCHAR     = java.sql.Types.VARCHAR;

	/**
	 * 設定查詢語法中參數為BigDecimal的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setBigDecimal(int position, BigDecimal value);

	/**
	 * 設定查詢語法中參數為BigInteger的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setBigInteger(int position, BigInteger value);

	/**
	 * 設定查詢語法中參數為Binary的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setBinary(int position, byte[] value);

	/**
	 * 設定查詢語法中參數為Boolean的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setBoolean(int position, boolean value);

	/**
	 * 設定查詢語法中參數為Byte的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setByte(int position, byte value);

	/**
	 * 設定查詢語法中參數為Date的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setDate(int position, Date value);

	/**
	 * 設定查詢語法中參數為Double的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setDouble(int position, double value);

	/**
	 * 設定查詢語法中參數為Float的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setFloat(int position, float value);

	/**
	 * 設定查詢語法中參數為Integer的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setInteger(int position, int value);

	/**
	 * 設定查詢語法中參數為Long的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setLong(int position, long value);

	/**
	 * 設定查詢語法中參數為Short的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setShort(int position, short value);

	/**
	 * 設定查詢語法中參數為String的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setString(int position, String value);

	/**
	 * 設定查詢語法中參數為Time的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setTime(int position, Time value);

	/**
	 * 設定查詢語法中參數為Timestamp的值
	 * @param position
	 * @param value
	 * @return
	 */
	QueryConditionIF setTimestamp(int position, Timestamp value);


	/**
	 * For StoredProcedure，設定output參數型態
	 * @param parameterIndex
	 * @param sqlType
	 * @return
	 */
	QueryConditionIF registerOutParameter(int parameterIndex, int sqlType);

	/**
	 * For StoredProcedure，設定output參數型態
	 * @param parameterIndex
	 * @param sqlType
	 * @param scale
	 * @return
	 */
	QueryConditionIF registerOutParameter(int parameterIndex, int sqlType, int scale);


	/**
	 * 設定資料操作的Timeout
	 * @param timeout
	 * @return
	 */
	QueryConditionIF setTimeout(int timeout);

	/**
	 * 設定資料庫操作的資料是否Cache
	 * @param cacheable
	 * @return
	 */
	QueryConditionIF setCacheable(boolean cacheable);

	/**
	 * 設定資料庫操作的FetchSize
	 * @param fetchSize
	 * @return
	 */
	QueryConditionIF setFetchSize(int fetchSize);

	/**
	 * 設定資料查詢，需要從第幾筆開始回傳
	 * @param firstResult
	 * @return
	 */
	QueryConditionIF setFirstResult(int firstResult);

	/**
	 * 設定資料查詢最多回傳幾筆
	 * @param maxResults
	 * @return
	 */
	QueryConditionIF setMaxResults(int maxResults);

	/**
	 * 設定資料查詢語句
	 * @param queryString
	 * @return
	 */
	QueryConditionIF setQueryString(String queryString);

	/**
	 * 取得資料查詢語句
	 * @return
	 */
	String getQueryString();

    QueryConditionIF setObject(String name, Object value);

    void setEntityClass(Class clazz);
}
