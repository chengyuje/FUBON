package com.systex.jbranch.platform.common.scheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import com.systex.jbranch.platform.common.dataaccess.datasource.DataSource;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSWORKDAYRULEVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterVO;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.common.workday.utils.WorkDayUtils;

public class ScheduleSyncJob implements Job {
// ------------------------------ FIELDS ------------------------------

    private static String SCHEDULER_ALIVE_LOG = "SchedulerAlive";

    private static boolean IS_FAILED = false;
	private Logger logger = LoggerFactory.getLogger(ScheduleSyncJob.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Job ---------------------

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        Session session = null;
        Transaction transaction = null;
        ScheduleManagement schedule = new ScheduleManagement();
        try {
            WorkDayUtils workDayUtils = (WorkDayUtils) PlatformContext.getBean("workDayUtils");
            Map<String, TBSYSWORKDAYRULEVO> ruleMap = workDayUtils.getWorkDayRuleMap();

            DataSource ds = new DataSource();
            SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
            session = sf.openSession();
            transaction = session.beginTransaction();

            String processor = PropertyUtil.getInstance().getProcessor();
            Query query = session.createQuery("from TbsysschdVO where isscheduled='N' and (istriggered<>'Y' or istriggered is null) and (onetime='N' or onetime is null) and processor=?");
            query.setString(0, processor);
            List<TbsysschdVO> list = query.list();
            transaction.commit();
            IS_FAILED = false;
            //搜尋尚未Sync的排程設定，逐筆加入排程
            for (TbsysschdVO vo2 : list) {
                TBSYSWORKDAYRULEVO vo = ruleMap.get(vo2.getscheduleid());
                String calendarDataProvider = null;
                if (vo != null) {
                    calendarDataProvider = vo.getCALENDAR_PROVIDER_ID();
                }
                if (calendarDataProvider != null && !PlatformContext.beanExists(calendarDataProvider)) {
                    logger.warn("排程[" + vo2.getscheduleid() + "]加入calendarDataProvider[" + calendarDataProvider + "]失敗");
                }
                try {
                    if (schedule.isScheduled(vo2.getscheduleid())) {
                        if ("Y".equals(vo2.getIsuse())) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("reschedule " + vo2.getscheduleid());
                            }
                            //schedule.reSchedule(vo2.getscheduleid(), vo2.getcronexpression());
                            CronTrigger trigger = new CronTrigger(
                                    vo2.getscheduleid() + "_trigger",//name
                                    vo2.getscheduleid() + "_group",//group
                                    vo2.getscheduleid(),//jobName
                                    SchedulerHelper.DEFAULT_GROUP,//jobGroup
                                    vo2.getcronexpression());
                            if (vo != null) {
                                trigger.setCalendarName(vo.getRULE_ID());
                            }


                            vo2.setisscheduled("Y");
//                            if (logger.isDebugEnabled()) {
                                String msg = "firetime:" + StdSchedulerFactory.getDefaultScheduler().rescheduleJob(
                                        vo2.getscheduleid() + "_trigger",
                                        vo2.getscheduleid() + "_group",
                                        trigger) + ",calendarDataProvider=" + calendarDataProvider;
                                logger.debug(msg);
                                logger.debug("加入排程[" + vo2.getscheduleid() + "]");
//                            }
                        }
                        else {
                            //停用時將scheduler刪除
                            StdSchedulerFactory.getDefaultScheduler().deleteJob(vo2.getscheduleid(), SchedulerHelper.DEFAULT_GROUP);
                            vo2.setisscheduled("N");
                            if (logger.isDebugEnabled()) {
                                logger.debug("刪除排程[" + vo2.getscheduleid() + "]");
                            }
                        }
                    }
                    else if ("Y".equals(vo2.getIsuse())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("add schedule " + vo2.getscheduleid());
                        }
                        //schedule.addSchedule(vo2.getscheduleid(), vo2.getcronexpression());
                        try {
                            JobDetail jd = new JobDetail(vo2.getscheduleid(), SchedulerHelper.DEFAULT_GROUP, ScheduleProxy.class);
                            jd.getJobDataMap().put(SchedulerHelper.SCHEDULE_ID_KEY, vo2.getscheduleid());
                            jd.getJobDataMap().put(SchedulerHelper.SCHEDULE_TYPE_KEY, SchedulerHelper.AUTO_START);

                            CronTrigger trigger = new CronTrigger(
                                    vo2.getscheduleid() + "_trigger",
                                    vo2.getscheduleid() + "_group",
                                    vo2.getscheduleid(),
                                    SchedulerHelper.DEFAULT_GROUP,
                                    vo2.getcronexpression());
                            if (vo != null) {
                                trigger.setCalendarName(vo.getRULE_ID());
                            }
                            StdSchedulerFactory.getDefaultScheduler().scheduleJob(jd, trigger);
                            vo2.setisscheduled("Y");
                            if (logger.isDebugEnabled()) {
                                logger.debug("scheduleid=" + vo2.getscheduleid() + ",calendarDataProvider=" + calendarDataProvider);
                            }
                        }
                        catch (Exception e) {
                            throw e;
                        }
                    }


//					JobDetail jd = new JobDetail(vo2.getscheduleid(), SchedulerHelper.DEFAULT_GROUP, ScheduleProxy.class);
//					jd.getJobDataMap().put(SchedulerHelper.SCHEDULE_ID_KEY, vo2.getscheduleid());
//					jd.getJobDataMap().put(SchedulerHelper.SCHEDULE_TYPE_KEY, SchedulerHelper.AUTO_START);
//					CronTrigger trigger = new CronTrigger(vo2.getscheduleid()+"_trigger",
//							vo2.getscheduleid()+"_group", vo2.getscheduleid(),SchedulerHelper.DEFAULT_GROUP, vo2.getcronexpression());
//					StdSchedulerFactory.getDefaultScheduler().scheduleJob(jd, trigger);
                    StdSchedulerFactory.getDefaultScheduler().start();
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    String msg = StringUtil.getStackTraceAsString(e);
                    ScheduleUtil.audit(sf, vo2.getscheduleid()
                            , vo2.getschedulename()
                            , SchedulerHelper.STATUS_FINISHED
                            , SchedulerHelper.SCHEDULE_WORKSTATION
                            , SchedulerHelper.RESULT_FAILURE, msg);

                    vo2.setisscheduled("N");
                }
                finally {
                    try {
                        vo2.setlasttry(new Timestamp(System.currentTimeMillis()));
                        transaction = session.beginTransaction();
                        session.update(vo2);
                    }
                    catch (Exception e1) {
                        logger.error(e1.getMessage(), e1);
                    }
                    try {
                        transaction.commit();
                    }
                    catch (Exception e) {
                    	transaction.rollback();
                    }
                }
            }

            //立即執行的Job(包含一次性Job)
            try {
                transaction = session.beginTransaction();
                query = session.createQuery("from TbsysschdVO where istriggered='Y' and isuse='Y' and processor=?");
                query.setString(0, processor);
                list = query.list();
                transaction.commit();
                //搜尋尚未Sync的排程設定，逐筆加入排程

                for (TbsysschdVO vo2 : list) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("一次性Job id=" + vo2.getscheduleid() + ",isscheduled=" + vo2.getisscheduled());
                    }
                    try {
                        String sid = vo2.getscheduleid();
                        String crontab = vo2.getcronexpression();

                        JobDataMap jm = new JobDataMap();
                        jm.put(SchedulerHelper.SCHEDULE_TYPE_KEY, SchedulerHelper.MANUAL_START);

                        if (logger.isDebugEnabled()) {
                            logger.debug("run schedule " + vo2.getscheduleid());
                        }
                        transaction = session.beginTransaction();

                        boolean isOnetime = false;
                        if ("Y".equals(vo2.getOnetime())) {    //若為一次性Job時
                            isOnetime = true;
                            jm.put("ONETIME", "Y");
                            vo2.setOnetime("N");
                            vo2.setIsuse("N");
                        }
                        vo2.setlasttry(new Timestamp(System.currentTimeMillis()));
                        vo2.setIstriggered("N");
                        session.update(vo2);
                        transaction.commit();

                        if (!schedule.isScheduled(sid)) {
                            schedule.addSchedule(sid, crontab);
                            if (logger.isDebugEnabled()) {
                                if (isOnetime) {
                                    logger.debug("加入一次性排程[" + vo2.getscheduleid() + "]");
                                }
                                else {
                                    logger.debug("加入立即執行排程[" + vo2.getscheduleid() + "]");
                                }
                            }
                        }
                        this.saveLog("manual " + sid, "begin");
                        StdSchedulerFactory.getDefaultScheduler().triggerJobWithVolatileTrigger(sid, SchedulerHelper.DEFAULT_GROUP, jm);
                        this.saveLog("manual " + sid, "end");
                    }
                    catch (Exception e) {
                    	logger.error(e.getMessage(), e);
                    	transaction.rollback();
                        
                        String msg = StringUtil.getStackTraceAsString(e);
                        ScheduleUtil.audit(sf, vo2.getscheduleid()
                                , vo2.getschedulename()
                                , SchedulerHelper.STATUS_FINISHED
                                , SchedulerHelper.SCHEDULE_WORKSTATION
                                , SchedulerHelper.RESULT_FAILURE, msg);
                    }
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            	transaction.rollback();
            }
            if (logger.isInfoEnabled()) {
                logger.info("check scheduer sync...");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        	transaction.rollback();
            
            if (!IS_FAILED) {
                IS_FAILED = true;
                TbsysschdadmasterVO vo = new TbsysschdadmasterVO();
                vo.setstatus("2");
                vo.setresult("2");
                String errorMsg = String.format("%s connect to DB Server fail,reason:%s"
                        , getIPAddress()
                        , StringUtil.getStackTraceAsString(getDeepCause(e)));
                vo.setdescription(errorMsg);
                String msg = ScheduleUtil.getSchedulerAuditLog(vo);
                logger.error(msg);
            }
        }
        finally {
            if (logger.isDebugEnabled()) {
                logger.debug("執行SyncJob檢查");
            }
            this.saveLog("execute", "POLLING FINISH");
            try {
                session.close();
            }
            catch (Exception e) {
            	logger.error(e.getMessage(), e);
            }
        }
    }

    private Throwable getDeepCause(Throwable e) {
        if (e == null) {
            return null;
        }
        if (e.getCause() != null) {
            return getDeepCause(e.getCause());
        }
        else {
            return e;
        }
    }

    private String getIPAddress() {
        byte[] addr = null;
        try {
            addr = InetAddress.getLocalHost().getAddress();
        }
        catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        if (addr == null || addr.length != 4) {
            return null;
        }
        return String.format("%s.%s.%s.%s", addr[0] & 0xff, addr[1] & 0xff, addr[2] & 0xff, addr[3] & 0xff);
    }
    
    // 儲存Batch LOG
    private void saveLog(String op, String msg) {
    	try {
    		DataAccessManager dam = PlatformContext.getBean(DataAccessManager.class);
    		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		StringBuffer sb = new StringBuffer();
    		sb.append("INSERT INTO TBSYSFTPLOG (SEQ, FTPSETTINGID, PROCESSOR, OPERATOR, RESULT,CREATOR, CREATETIME) ");
    		sb.append("VALUES (TBSYSFTPLOG_SEQ.nextval, ?, ?, ?, ?, ?, sysdate) ");
    		queryCondition.setQueryString(sb.toString());
    		queryCondition.setString(1, "ScheduleSyncJob");
    		queryCondition.setString(2, InetAddress.getLocalHost().getHostName());
    		queryCondition.setString(3, op);
    		queryCondition.setString(4, msg);
    		queryCondition.setString(5, "SYS");
    		dam.exeUpdate(queryCondition);
    		
    		if ("POLLING FINISH".equals(msg)) this.deleteLog();
    	} catch (Exception e) {
    		logger.warn("save log", e);
    	}
    }
    
//    private void deleteLog() {
//    	Connection conn = null;
//    	Statement st = null;
//    	try {
//    		com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
//                    com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
//            SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
//			javax.sql.DataSource dataSource = SessionFactoryUtils.getDataSource(sf);
//			conn = dataSource.getConnection();
//			conn.setAutoCommit(true);
//    		StringBuffer sb = new StringBuffer();
//    		sb.append("delete from tbsysftplog where ftpsettingid='ScheduleSyncJob' and result='POLLING FINISH' ");
//    		sb.append("and seq not in (select seq from (select seq from tbsysftplog ");
//    		sb.append("where ftpsettingid='ScheduleSyncJob' and result='POLLING FINISH' order by seq desc) where rownum < 11) ");
//    		st = conn.createStatement();
//    		st.execute(sb.toString());
//    		conn.commit();
//    	} catch (Exception e) {
//    		logger.warn("delete log", e);
//    	} finally {
//	        if (st != null) try { st.close(); } catch (Exception e) {}
//	        if (conn != null) try { conn.close(); } catch (Exception e) {}
//	    }
//    }
    private void deleteLog() {
    	try {
	    	DataAccessManager dam = PlatformContext.getBean(DataAccessManager.class);
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("delete from tbsysftplog where ftpsettingid='ScheduleSyncJob' and result='POLLING FINISH' ");
			sb.append("and seq not in (select seq from (select seq from tbsysftplog ");
			sb.append("where ftpsettingid='ScheduleSyncJob' and result='POLLING FINISH' order by seq desc) where rownum < 11) ");
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);
    	} catch (Exception e) {
    		logger.warn("delete log", e);
    	}
    }
}
