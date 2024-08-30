package com.systex.jbranch.platform.common.scheduler;

public class SchedulerHelper {
	public static final String DEFAULT_GROUP = "defaultgroup";
	
	public static final String SCHEDULE_PARAMETER_KEY  = "scheduleparameterkey"; 
	public static final String SCHEDULE_TYPE_KEY       = "scheduletypekey";
	public static final String SCHEDULE_NAME_EKY       = "schedulenamekey";
	public static final String SCHEDULE_REPEATE_KEY    = "schedulerepeatekey";
	public static final String SCHEDULE_ID_KEY         = "scheduleidkey";
	public static final String SCHEDULE_LASTTRY_KEY    = "schedulelasttrykey";
	public static final String SCHEDULE_REPEATE_INTERVAL_KEY    = "schedulerepeateintervalkey";
	 
	public static final String JOB_INFO_KEY_JOBID = "jobid";
	public static final String JOB_INFO_KEY_JOBNAME = "jobname";
	public static final String JOB_INFO_KEY_CLASSNAME = "classname";
	public static final String JOB_INFO_KEY_PRECONDITION = "precondition";
	public static final String JOB_INFO_KEY_POSTCONDITION = "postcondition";
	public static final String JOB_INFO_KEY_PRECONDITION_INFO = "preconditioninfo";
	public static final String JOB_INFO_KEY_POSTCONDITION_INFO = "postconditioninfo";
	public static final String JOB_INFO_KEY_PARAMETER = "parameter";
	public static final String JOB_INFO_KEY_JOBTYPE   = "jobtypekey";
	public static final String JOB_PARAMETER_KEY      = "jobparameterkey";
	public static final String AUDIT_PARAMETER_KEY        = "auditparameterkey";
	
	public static final String STATUS_RUNNING          = "1";
	public static final String STATUS_FINISHED         = "2";
	public static final String RESULT_SUCCESS  = "1";  
	public static final String RESULT_FAILURE  = "2";
	public static final String RESULT_WARN  = "3";
	public static final String DATA_ACCESS_JOB = "dataaccessjob";
	public static final String JDBC_JOB        = "jdbcjob";
	public static final String UNKNOW_JOB      = "unknowjob";
 
	public static final String STOP_SCHEDULE_EXCEPTOIN = "stop_schedule";
	public static final String STOP_JOB_EXCEPTION      = "stop_job";
	public static final String SCHEDULE_MASTER_LOG_ID  = "schedulemasterlogid";
	public static final String SCHEDULE_DETAIL_LOG_ID  = "scheduledetaillogid";
	public static final String AUTO_START              = "1";//定時自動啟動
	public static final String MANUAL_START            = "2";//手動啟動
	public static final String ONETIME_START           = "3";//一次執行
	public static final String MASTER_SERIAL_ID        = "schedulemaster";
	public static final String SCHEDULER_USER          = "SCHEDULER";
	
	public static final String SCHEDULE_BRANCH         = "SCHEDULER";
	public static final String SCHEDULE_WORKSTATION    = "SCHEDULER";
	public static final String JOB_INSTANCE            = "job_instance";
	public static final String POST_INSTANCE            ="post_instance";
	public static final String PRE_INSTANCE            = "pre_instance";
	
	public static final String SCHEDULER_TYPE          = "scheduler";
	
	public static final String PARAM_TYPE				 = "param_type";
	
	public static final String EXEC_CLASS				 = "exec_class";
	
	public static final String EXEC_UNIT				 = "exec_unit";
	public static final String PROCESSOR				 = "processor";
	public static final String DEFAULT_CALENDAR 		 = "default_calendar";
	public static final String SCHEDULER_STOP 			 = "scheduler_stop";

	public static final Object JOB_INFO_KEY_BEANNAME = "job_info_key_beanname";
}
