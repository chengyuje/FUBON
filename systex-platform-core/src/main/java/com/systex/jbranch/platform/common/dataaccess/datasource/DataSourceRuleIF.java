package com.systex.jbranch.platform.common.dataaccess.datasource;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * Data Source的判斷規則介面
 * @version 1.0
 * @since   20081111
 */
public interface DataSourceRuleIF {
	
	/**
	 * 取得指定data source之下，UUID所歸屬的Login ID
	 * @param uuid
	 * @return
	 */
	//String parse(String dataSourceID, UUID uuid) throws JBranchException;

	/**
	 * 取得預設的DataSource
	 * @return datasource
	 */
	String getDefaultDataSource() throws JBranchException;
	
	/**
	 * 依照使用者身分取得預設資料庫的dataSource
	 * @param uuid
	 * @return
	 * @throws JBranchException
	 */
	String getDefaultDBDataSource(UUID uuid) throws JBranchException;
	
	/**
	 * 依照使用者身分取得指定資料庫的dataSource
	 * @param dbID
	 * @param uuid
	 * @return
	 * @throws JBranchException
	 */
	String getDataSource(String dbID, UUID uuid) throws JBranchException;

}
