package com.systex.jbranch.platform.common.dataaccess.helper;

/**
 * 定義DataAccess所用到的常數資訊
 *
 */
public class DataAccessHelper {
	public static final Class DAO_FACTORY_HIBERNATE = null;
		//com.systex.jbranch.platform.common.dao.impl.hibernate.HibernateDaoFactory.class;
	
	//public static final Class DAO_FACTORY_TOPLINK = 
	
//	public static final Class TRANSACTION_JDBC_HIBERNATE = 
//		com.systex.jbranch.platform.common.dao.transaction.HibernateJDBCTransactionUtil.class;
	
	//public static final Class TRANSACTION_JTA_XXX =  
	//public static final Class TRANSACTION_JDBC_TOPLINK =
	
	public static final byte QUERY_LANGUAGE_TYPE_HQL = 1;
	public static final byte QUERY_LANGUAGE_TYPE_SQL = 2;
	public static final byte QUERY_LANGUAGE_TYPE_VAR_SQL = 3;
	
	public static final byte TRANSACTION_MANAGEMENT_JDBC = 1;
	public static final byte TRANSACTION_MANAGEMENT_JTA = 2;
	
	public static final String DS_FILTER_SUFFIX = ".cfg.xml";
	//public static final String DEFAULT_DS_ID = "default";
	public static final String PLATFORM_DAOFACTORY_ID = "pfdaoFactory";
	
	//常數
	public static final String DEFAULT_USER = "SYSTEM";
	//public static final String DEFAULT_TRANSACTION_ID = "$transaction@platform";
	
	//Bean ID
	public static final String DATASOURCE_RULE_ID       = "datasourcerule";
	public static final String DAOMANAGER_ID            = "daomanager";
	public static final String TRANSACTION_ID           = "independenttransactoin";
	public static final String QUERY_UTILITY_FACTORY_ID = "queryutilityfactory";
	public static final String INDEPENDENT_TRANSACTION  = "independenttransaction";
	public static final String DAO_PROXY_ID             = "daoproxy";
	public static final String QUERY_UTILITY_PROXY_ID   = "queryutilityproxy";
	public static final String SERIAL_NUMBER_ID         = "serialnumberutility";
	
	
	//ERROR MSG
	//public static final String ERR_MSG_DATA_ACCESS_EXCEPTION             = "eh1_01_daoErr_001";
	//public static final String ERR_MSG_DAOFACTORY_NOT_FOUND              = "eh1_01_daoErr_002";
	//public static final String ERR_MSG_DAO_NOT_FOUND                     = "eh1_01_daoErr_003";
	//public static final String ERR_MSG_DATASOURCE_NOT_FOUND              = "eh1_01_daoErr_004";
	//public static final String ERR_MSG_LOGIN_ID_NOT_FOUND                = "eh1_01_daoErr_005";
	//public static final String ERR_MSG_TRANSACTION_MANAGEMENT_NOT_FOUND  = "eh1_01_daoErr_006";
	//public static final String ERR_MSG_FIELDS_MAP_KEY_ERR                = "eh1_01_daoErr_007";
//	public static final String ERR_MSG_DAO_SERIAL_NUMBER_ERR             = "eh1_01_daoErr_008";
//	public static final String ERR_MSG_TRANSACTION_NOT_FOUND             = "eh1_01_daoErr_009";
//	public static final String ERR_MSG_CONNECTION_NOT_BEGIN              = "eh1_01_daoErr_010";
	//public static final String ERR_MSG_DATA_SOURCE_EXCEPTION             = "eh1_01_daoErr_011";
//	public static final String ERR_MSG_DATA_ACCESS_INIT_EXCEPTION        = "eh1_01_daoErr_012";
//	public static final String ERR_MSG_TOROLLBACK                        = "eh1_01_daoErr_013";
//	public static final String ERR_MSG_VO_NOT_FOUND                      = "eh1_01_daoErr_014";
	//public static final String ERR_MSG_BEAN_INIT_ERR                     = "eh1_01_daoErr_015";
	//public static final String ERR_MSG_QUERY_STRING_ERR                  = "eh1_01_daoErr_016";
	//public static final String ERR_MSG_TABLE_UID_ERR                     = "eh1_01_daoErr_017";
	
	
}
