package com.systex.jbranch.platform.common.scheduler;

public class SchedulerErrMsg {
	public String ehl_01_009="ehl_01_common_009";//FTP存取錯誤
	public String ehl_01_010="ehl_01_common_010";//資料庫存取錯誤　
	public String ehl_01_011="ehl_01_common_011";//檔案存取錯誤
	public static final String LOAD_DRIVER_ERROR        = "ehl_01_common_019";
	public static final String GET_CONNECTION_ERROR     = "ehl_01_common_020";
	public static final String DB_ACCESS_ERROR          = "ehl_01_common_010";
	public static final String INIT_PRECONDITION_ERROR  = "ehl_01_common_013";//建立precondition發生錯誤
	public static final String INIT_JOB_ERROR           = "ehl_01_common_014";
	public static final String INIT_POSTCONDITION_ERROR = "ehl_01_common_015";//建立postcondition發生錯誤
	public static final String PARAMETER_PARSE_ERROR    = "ehl_01_common_016";	
	public static final String EXE_PRECONDITION_ERROR   = "ehl_01_common_017";//執行precondition發生錯誤
	public static final String EXE_POSTCONDITION_ERROR  = "ehl_01_common_018";//執行postcondition發生錯誤
	
	public static final String INIT_SCHEDULE_ERROR      = "ehl_01_common_021";//初始化排程發生錯誤
	public static final String SET_SCHEDULE_DATA_ERROR  = "ehl_01_common_022";//設定排程資料發生錯誤
	public static final String GET_JOBLIST_DATA_ERROR   = "ehl_01_common_023";//取得JobList發生錯誤
	public static final String GEN_AUDIT_MASTER_ERROR   = "ehl_01_common_024";//建立稽核記錄主檔發生錯誤
	
	
	public static final String UNKNOW_JOB_TYPE       = "pf_scheduler_error_001";
	public static final String SCHEDULE_ID_NOT_FOUND = "pf_scheduler_error_002";
}
