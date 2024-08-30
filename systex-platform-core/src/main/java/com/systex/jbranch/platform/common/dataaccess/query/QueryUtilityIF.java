package com.systex.jbranch.platform.common.dataaccess.query;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.DAOException;


/**
 * 定義資料庫操作複雜查詢的介面
 * 也就是執行SQL或HQL的介面
 *
 */
public interface QueryUtilityIF {
	
	/**
	 * 執行查詢
	 * @return 回傳查詢到的資料List
	 * @throws DAOException
	 */
	@Deprecated
	List executeQuery()throws DAOException;
	
	/**
	 * 執行查詢
	 * @return 回傳查詢到的資料List
	 * @throws DAOException
	 */
	List exeQuery()throws DAOException;
	
	/**
	 * 執行ScrollableQuery
	 * @return
	 * @throws DAOException
	 */
	ScrollableQueryResultIF executeScrollableQuery()throws DAOException;
	
	/**
	 * 執行資料新增、修改、刪除指令
	 * @return
	 */
	@Deprecated
	int executeUpdate()throws DAOException;
	
	/**
	 * 執行資料新增、修改、刪除指令
	 * @return
	 */
	int exeUpdate()throws DAOException;
	
	/**
	 * 執行分頁查詢
	 * @param pageNumber 頁數
	 * @param recordOfPage 每頁需要幾筆
	 * @return
	 * @throws DAOException
	 */
	ResultIF executePaging(int pageNumber,int recordOfPage) throws DAOException;
	
	/**
	 * 執行分頁查詢，全部資料都放在第一頁
	 * @return
	 * @throws DAOException
	 */
	ResultIF executePaging()throws DAOException;
	
	/**
	 * 執行Callable的Query
	 * @return
	 * @throws DAOException
	 */
	ResultIF executeCallableQuery() throws DAOException;
	
	/**
	 * 執行Callable
	 * @return
	 * @throws DAOException
	 */
	Map<Integer, Object> executeCallable() throws DAOException;
}
