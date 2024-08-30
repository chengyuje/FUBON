package com.systex.jbranch.platform.common.scheduler;

import java.net.InetAddress;

import org.apache.commons.lang.ArrayUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.initiation.InitiatorIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.workday.utils.WorkDayUtils;

@Repository
public class ScheduleInitiator implements InitiatorIF {
// ------------------------------ FIELDS ------------------------------

	@Autowired
    private WorkDayUtils workDayUtils;
	
	@Autowired
    private PropertyUtil propertyUtil;
	
    @Autowired
    private DataAccessManager dataAccessManager;
    
    @Autowired
    @Qualifier("systemParameterDefaultKey")
    private String systemParameterDefaultKey;
	
	private Logger logger = LoggerFactory.getLogger(ScheduleInitiator.class);
	
	private static DefaultSchedulerMonitor monitor;
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface InitiatorIF ---------------------

    public void execute() throws Exception {
        try {
            //String startSchedule = propertyUtil.getStartSchedule();
        	String op = "start";
        	if (this.propertyUtil == null) {
        		op = "restart";
        		this.saveLog(op, "begin");
        		this.propertyUtil = (PropertyUtil) PlatformContext.getBean("propertyUtil");
        		if (this.propertyUtil == null) {
        			throw new JBranchException("PropertyUtil未被初始化");
        		}
        	} else {
        		this.saveLog(op, "begin");
        	}
            String cronExpression = propertyUtil.getCronExpression();
            String processor = propertyUtil.getProcessor();
            String standalone = propertyUtil.getStandALone();
            boolean isBatchServer = false;
            
            ScheduleManagement schedule = (ScheduleManagement) PlatformContext.getBean("scheduleManagement");
            if (this.dataAccessManager == null) {
            	this.dataAccessManager = PlatformContext.getBean(DataAccessManager.class);
            }
            String [] processList = schedule.getAPProcessList(dataAccessManager);
           
            if(!ArrayUtils.isEmpty(processList)){
            	if (null == systemParameterDefaultKey) systemParameterDefaultKey = InetAddress.getLocalHost().getHostName();
            	for(String proc : processList){
            		if(isBatchServer = proc.equals(systemParameterDefaultKey))
            			break;
            	}
            } else {
            	logger.warn("processList is empty!!!!!");
            }
            
            schedule.shutdown();
            schedule.delAllJob();

            //if (Boolean.parseBoolean(startSchedule)) {
            if(isBatchServer) {
                try {
                	if (this.workDayUtils == null) {
                		this.workDayUtils =  (WorkDayUtils) PlatformContext.getBean("workDayUtils");
                	}
                    workDayUtils.reloadAllRule();
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
//				schedule.deleteSchedule("syncJob");
                if (!schedule.isScheduled("syncJob")) {
                    JobDetail jd = new JobDetail("syncJob", SchedulerHelper.DEFAULT_GROUP, ScheduleSyncJob.class);
                    //CronTrigger trigger = new CronTrigger("trigger2", "group2", "syncJob", SchedulerHelper.DEFAULT_GROUP, "0/10 * * * * ?");
                    CronTrigger trigger = new CronTrigger("trigger2", "group2", "syncJob", SchedulerHelper.DEFAULT_GROUP, cronExpression);
                    trigger.setPriority(7); 	// Run high priority
                    StdSchedulerFactory.getDefaultScheduler().scheduleJob(jd, trigger);
                }
                schedule.scheduleAllJobs();
                // Add monitor
                StdSchedulerFactory.getDefaultScheduler().start();
                if (monitor == null) {
                	monitor = new DefaultSchedulerMonitor();
                	monitor.start();
                } else {
                	if (!monitor.isAlive()) {
                		monitor.start();
                	}
                }
            } else {
            	if (monitor != null) {
            		monitor=null;
             	}
            }
            this.saveLog(op, "end");
            if (logger.isInfoEnabled()) {
                logger.info(System.getProperty("line.separator") +
                        "**************Scheduler Start Success***************" + System.getProperty("line.separator")
                        + "Processor      : " + processor + System.getProperty("line.separator")
                        + "Standalone     : " + standalone + System.getProperty("line.separator")
                        + "cronExpression : " + cronExpression + System.getProperty("line.separator")
                        + "***************************************************");
            }
            
        }
        catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
            throw e1;
        } catch (Throwable e) {
        
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setPropertyUtil(PropertyUtil propertyUtil) {
        this.propertyUtil = propertyUtil;
    }

    public void setWorkDayUtils(WorkDayUtils workDayUtils) {
        this.workDayUtils = workDayUtils;
    }
    
    // 儲存Batch LOG
    private void saveLog(String op, String msg) {
    	try {
    		StringBuffer sb = new StringBuffer();
    		sb.append("INSERT INTO TBSYSFTPLOG (SEQ, FTPSETTINGID, PROCESSOR, OPERATOR, RESULT,CREATOR, CREATETIME) ");
    		sb.append("VALUES (TBSYSFTPLOG_SEQ.nextval, ?, ?, ?, ?, ?, sysdate) ");
    		DataAccessManager dam = PlatformContext.getBean(DataAccessManager.class);
    		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		queryCondition.setQueryString(sb.toString());
    		queryCondition.setString(1, "ScheduleInitiator");
    		queryCondition.setString(2, InetAddress.getLocalHost().getHostName());
    		queryCondition.setString(3, op);
    		queryCondition.setString(4, msg);
    		queryCondition.setString(5, "SYS");
    		dam.exeUpdate(queryCondition);
    	} catch (Exception e) {
    		logger.warn("save log", e);
    	}
    }
}
