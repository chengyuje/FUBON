package com.systex.jbranch.platform.common.dataaccess.daomanager;

import org.hibernate.Criteria;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;


/**
 * DaoManagerIF為一個Interface，規範如何取得Dao物件的介面
 * @since 20080106
 * @version 1.0
 */
public interface DaoManagerIF {
	 
   /**
	 * 由所指定的DataSource與交易管理型態來取得Dao物件
	 * @param transactionManagement(此參數已無作用)
	 * @param dataSource
	 * @param daoId
	 * @return Object
	 * @throws DAOException, JBranchException
	 */
   public Object getDao(byte transactionManagement,
		   			com.systex.jbranch.platform.common.dataaccess.datasource.DataSource dataSource,
		   			String daoId)
   					throws DAOException, JBranchException;
   
   /**
    * 取得Hibernate的Criteria類別
    * @param transactionManagement(此參數已無作用)
    * @param dataSource
    * @param daoId
    * @return
    * @throws DAOException
    * @throws JBranchException
    */
   public Criteria getHibernateCriteria(byte transactionManagement,
  			com.systex.jbranch.platform.common.dataaccess.datasource.DataSource dataSource,
   			String daoId)
				throws DAOException, JBranchException;

}
