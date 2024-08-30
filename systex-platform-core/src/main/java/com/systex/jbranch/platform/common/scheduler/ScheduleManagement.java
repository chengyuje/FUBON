package com.systex.jbranch.platform.common.scheduler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSWORKDAYRULEVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobclassVO;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.common.workday.utils.WorkDayUtils;

public class ScheduleManagement {
// ------------------------------ FIELDS ------------------------------

    private DataAccessManager dataAccessManager;
    private WorkDayUtils workDayUtils;
	private Logger logger = LoggerFactory.getLogger(ScheduleManagement.class);

// -------------------------- OTHER METHODS --------------------------

    /**
     * 手動執行一次排程
     *
     * @param session          傳入所需db資源
     * @param clazz            要執行的類別名稱(ex:com.systex.jbranch.app.server.fps.CMBTH103)，相對於TBSYSSCHDJOBCLASS資料表中CLASSNAME欄位。
     * @param sched_param_type 相對於TBSYSSCHD資料表中的scheduleparameter欄位。
     * @param job_param_type   相對於TBSYSSCHDJOB資料表中的parameters欄位。
     * @param process          相對於TBSYSSCHD資料表中的process欄位。
     * @throws JBranchException
     */
    public void addOneTime(String clazz, String sched_param_type, String job_param_type, String process) throws JBranchException {
        DataAccessManager dam = PlatformContext.getBean(DataAccessManager.class);
        addOneTime(dam, clazz, sched_param_type, job_param_type, process);
    }

    /**
     * 手動執行一次AP排程
     *
     * @param session          傳入所需db資源
     * @param clazz            要執行的類別名稱(ex:com.systex.jbranch.app.server.fps.CMBTH103)，相對於TBSYSSCHDJOBCLASS資料表中CLASSNAME欄位。
     * @param sched_param_type 相對於TBSYSSCHD資料表中的scheduleparameter欄位。
     * @param job_param_type   相對於TBSYSSCHDJOB資料表中的parameters欄位。
     * @throws JBranchException
     */
    public void addOneTimeAllAPProcess(String clazz, String sched_param_type, String job_param_type) throws JBranchException {
        DataAccessManager dam = PlatformContext.getBean(DataAccessManager.class);
        addOneTimeAllAPProcess(dam, clazz, sched_param_type, job_param_type);
    }

    /**
     * 手動執行一次AP排程
     *
     * @param session          傳入所需db資源
     * @param clazz            要執行的類別名稱(ex:com.systex.jbranch.app.server.fps.CMBTH103)，相對於TBSYSSCHDJOBCLASS資料表中CLASSNAME欄位。
     * @param sched_param_type 相對於TBSYSSCHD資料表中的scheduleparameter欄位。
     * @param job_param_type   相對於TBSYSSCHDJOB資料表中的parameters欄位。
     * @throws JBranchException
     */
    public void addOneTimeAllAPProcess(DataAccessManager dam, String clazz, String sched_param_type, String job_param_type) throws JBranchException {
        String[] processes = getAPProcessList(dam);
        for (int i = 0; i < processes.length; i++) {
            addOneTime(dam, clazz, sched_param_type, job_param_type, processes[i]);
        }
    }

    /**
     * 取得所有AP清單
     *
     * @param dam
     * @return AP清單陣列
     * @throws JBranchException
     */
    public String[] getAPProcessList(DataAccessManager dam) throws JBranchException {
        QueryConditionIF qc = dam.getQueryCondition();
        qc.setQueryString("select param_code from TBSYSPARAMETER where param_type='TBSYSSCHD.PROCESSOR.AP'");
        List<Map<String, String>> list = dam.executeQuery(qc);
        String[] processList = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            processList[i] = list.get(i).get("PARAM_CODE");
        }
        return processList;
    }

    /**
     * 手動執行一次排程
     *
     * @param session          傳入所需db資源
     * @param clazz            要執行的類別名稱(ex:com.systex.jbranch.app.server.fps.CMBTH103)，相對於TBSYSSCHDJOBCLASS資料表中CLASSNAME欄位。
     * @param sched_param_type 相對於TBSYSSCHD資料表中的scheduleparameter欄位。
     * @param job_param_type   相對於TBSYSSCHDJOB資料表中的parameters欄位。
     * @param process          相對於TBSYSSCHD資料表中的process欄位。
     * @throws JBranchException
     */
    public void addOneTime(DataAccessManager dam, String clazz, String sched_param_type, String job_param_type, String process) throws JBranchException {
        long tm = Calendar.getInstance().getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String now = sdf.format(tm);
        try {
            Thread.sleep(10L);
        }
        catch (InterruptedException e) {
            //ingore
        }
        generateSchdVO(dam, sched_param_type, process, tm, now);

        generateAssVO(dam, tm, now);

        generateJobVO(dam, job_param_type, tm, now);

        generateClassVO(dam, clazz, tm, now);
    }

    private void generateSchdVO(DataAccessManager dam, String sched_param_type,
                                String process, long tm, String now) throws DAOException, JBranchException {
        TbsysschdVO schdVO = new TbsysschdVO();
        schdVO.setscheduleid(now + "SID");
        schdVO.setschedulename("onetime_schd");
        schdVO.setdescription("onetime_schd");
        schdVO.setprocessor(process);
        schdVO.setcronexpression("0 0 0 * * ?");
        schdVO.setrepeat(new BigDecimal(0));
        schdVO.setrepeatinterval(new BigDecimal(0));
        schdVO.setIsuse("Y");
        schdVO.setisscheduled("Y");
        schdVO.setIstriggered("Y");
        schdVO.setOnetime("Y");
        schdVO.setscheduleparameter(sched_param_type);
        schdVO.setJobstart(new BigDecimal(0));
        schdVO.setVersion(0L);
        schdVO.setCreatetime(new Timestamp(tm));
        schdVO.setCreator("SYSTEM");
        schdVO.setLastupdate(new Timestamp(tm));
        schdVO.setModifier("SYSTEM");

        dam.create(schdVO);
    }

    private void generateAssVO(DataAccessManager dam, long tm, String now) throws DAOException, JBranchException {
        TbsysschdjobassVO assVO = new TbsysschdjobassVO();
        TbsysschdjobassPK assPK = new TbsysschdjobassPK();
        assPK.setScheduleid(now + "SID");
        assPK.setJobid(now + "JID");
        assVO.setComp_id(assPK);
        assVO.setJoborder(new BigDecimal(1));
        assVO.setVersion(0L);
        assVO.setCreatetime(new Timestamp(tm));
        assVO.setCreator("SYSTEM");
        assVO.setLastupdate(new Timestamp(tm));
        assVO.setModifier("SYSTEM");
        dam.create(assVO);
    }

    private void generateJobVO(DataAccessManager dam, String job_param_type, long tm,
                               String now) throws DAOException, JBranchException {
        TbsysschdjobVO jobVO = new TbsysschdjobVO();
        jobVO.setjobid(now + "JID");
        jobVO.setjobname("onetime_job");
        jobVO.setdescription("onetime_job");
        jobVO.setclassid(now + "CID");
        jobVO.setparameters(job_param_type);
        jobVO.setVersion(0L);
        jobVO.setCreatetime(new Timestamp(tm));
        jobVO.setCreator("SYSTEM");
        jobVO.setLastupdate(new Timestamp(tm));
        jobVO.setModifier("SYSTEM");
        dam.create(jobVO);
    }

    private void generateClassVO(DataAccessManager dam, String clazz, long tm,
                                 String now) throws DAOException, JBranchException {
        TbsysschdjobclassVO classVO = new TbsysschdjobclassVO();
        classVO.setclassid(now + "CID");
        classVO.setclassname(clazz);
        classVO.setVersion(0L);
        classVO.setCreatetime(new Timestamp(tm));
        classVO.setCreator("SYSTEM");
        classVO.setLastupdate(new Timestamp(tm));
        classVO.setModifier("SYSTEM");
        dam.create(classVO);
    }

    /**
     * 手動執行一次排程
     *
     * @param session          傳入所需db資源
     * @param clazz            要執行的類別名稱(ex:com.systex.jbranch.app.server.fps.CMBTH103)，相對於TBSYSSCHDJOBCLASS資料表中CLASSNAME欄位。
     * @param sched_param_type 相對於TBSYSSCHD資料表中的scheduleparameter欄位。
     * @param job_param_type   相對於TBSYSSCHDJOB資料表中的parameters欄位。
     * @throws JBranchException
     */
    public void addOneTimeAllProcess(String clazz, String sched_param_type, String job_param_type) throws JBranchException {
        DataAccessManager dam = PlatformContext.getBean(DataAccessManager.class);
        addOneTimeAllProcess(dam, clazz, sched_param_type, job_param_type);
    }

    /**
     * 手動執行一次排程
     *
     * @param session          傳入所需db資源
     * @param clazz            要執行的類別名稱(ex:com.systex.jbranch.app.server.fps.CMBTH103)，相對於TBSYSSCHDJOBCLASS資料表中CLASSNAME欄位。
     * @param sched_param_type 相對於TBSYSSCHD資料表中的scheduleparameter欄位。
     * @param job_param_type   相對於TBSYSSCHDJOB資料表中的parameters欄位。
     * @throws JBranchException
     */
    public void addOneTimeAllProcess(DataAccessManager dam, String clazz, String sched_param_type, String job_param_type) throws JBranchException {
        String[] processes = getProcessList(dam);
        for (int i = 0; i < processes.length; i++) {
            addOneTime(dam, clazz, sched_param_type, job_param_type, processes[i]);
        }
    }

    /**
     * 取得所有Server清單
     *
     * @param dam
     * @return 清單陣列
     * @throws JBranchException
     */
    public String[] getProcessList(DataAccessManager dam) throws JBranchException {
        QueryConditionIF qc = dam.getQueryCondition();
        qc.setQueryString("select PARAM_CODE from TBSYSPARAMETER where param_type='TBSYSSCHD.PROCESSOR.AP' or param_type='TBSYSSCHD.PROCESSOR.DB'");
        List<Map<String, String>> list = dam.executeQuery(qc);
        String[] processList = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            processList[i] = list.get(i).get("PARAM_CODE");
        }
        return processList;
    }

    public void delAllJob() throws SchedulerException {
        Scheduler defScheduler = StdSchedulerFactory.getDefaultScheduler();
        String[] groupNames = defScheduler.getJobGroupNames();
        String[] jobs = null;
        for (int i = 0; i < groupNames.length; i++) {
            jobs = defScheduler.getJobNames(groupNames[i]);
            if (jobs != null) {
                for (int j = 0; j < jobs.length; j++) {
                    defScheduler.deleteJob(jobs[j], SchedulerHelper.DEFAULT_GROUP);
                }
            }
        }
    }

    public List<JobExecutionContext> getCurrentlyExecutingJobs() throws SchedulerException {
        return StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs();
    }
    
    public List<String> getCurrentlyExecutingSchedulerName() throws SchedulerException {
    	List<JobExecutionContext> jobList = StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs();
    	List<String> schedulerNames = new ArrayList();
    	for (int i = 0; i < jobList.size(); i++) {
    		JobExecutionContext context = jobList.get(i);
    		schedulerNames.add(context.getScheduler().getSchedulerName());
		}
    	return schedulerNames;
    }
    
    public List<String> getCurrentlyExecutingJobName() throws SchedulerException {
    	List<JobExecutionContext> jobList = StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs();
    	List<String> jobNames = new ArrayList();
    	for (int i = 0; i < jobList.size(); i++) {
    		JobExecutionContext context = jobList.get(i);
    		jobNames.add(context.getJobDetail().getName());
		}
    	return jobNames;
    }

    /**
     * 刪除指定的Schedule
     * @param scheduleID
     * @throws SchedulerException
     */
//	public void deleteSchedule(String scheduleID) throws SchedulerException{
//		StdSchedulerFactory.getDefaultScheduler().deleteJob(scheduleID, SchedulerHelper.DEFAULT_GROUP);
//	}

    /**
     * 暫停指定的Schedule ID
     *
     * @param scheduleID
     * @throws Exception
     */
    public void pause(String scheduleID) throws Exception {
        if (dataAccessManager == null) {
            throw new JBranchException("DataAccessManager init failed!");
        }
        TbsysschdVO vo2 = (TbsysschdVO) dataAccessManager.findByPKey(TbsysschdVO.TABLE_UID, scheduleID);

        if (vo2 == null) {
            throw new JBranchException(SchedulerErrMsg.SCHEDULE_ID_NOT_FOUND);
        }
        vo2.setIsuse("N");
        vo2.setisscheduled("N");
        dataAccessManager.update(vo2);
//		deleteSchedule(scheduleID);
    }

    /**
     * 重新排Schedule的時間
     *
     * @param scheduleId
     * @param cronExpression
     * @throws ParseException
     * @throws SchedulerException
     * @throws JBranchException
     * @throws SQLException
     */
    public void reSchedule(String scheduleId, String cronExpression) throws ParseException, SchedulerException, JBranchException, SQLException {
        CronTrigger trigger = new CronTrigger(
                scheduleId + "_trigger",//name
                scheduleId + "_group",//group
                scheduleId,//jobName
                SchedulerHelper.DEFAULT_GROUP,//jobGroup
                cronExpression);
        Map<String, TBSYSWORKDAYRULEVO> ruleMap = workDayUtils.getWorkDayRuleMap();
        TBSYSWORKDAYRULEVO vo = ruleMap.get(scheduleId);
        String calendarDataProvider = null;
        if (vo != null) {
            calendarDataProvider = vo.getCALENDAR_PROVIDER_ID();
        }
        if (calendarDataProvider != null && !PlatformContext.beanExists(calendarDataProvider)) {
            logger.warn("排程[" + scheduleId + "]加入calendarDataProvider[" + calendarDataProvider + "]失敗");
        }
        if (vo != null) {
            trigger.setCalendarName(vo.getRULE_ID());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("firetime:" + StdSchedulerFactory.getDefaultScheduler().rescheduleJob(
                    scheduleId + "_trigger",
                    scheduleId + "_group",
                    trigger));
        }

        StdSchedulerFactory.getDefaultScheduler().start();
    }

    public void refreshTrigger(String scheduId, String calendarDataProvider, String cron) throws ParseException, SchedulerException {
        if (calendarDataProvider != null && !PlatformContext.beanExists(calendarDataProvider)) {
            logger.warn("排程[" + scheduId + "]加入calendarDataProvider[" + calendarDataProvider + "]失敗");
        }
        if (isScheduled(scheduId)) {
            CronTrigger trigger = new CronTrigger(
                    scheduId + "_trigger",//name
                    scheduId + "_group",//group
                    scheduId,//jobName
                    SchedulerHelper.DEFAULT_GROUP,//jobGroup
                    cron);
            trigger.setCalendarName(calendarDataProvider);

            StdSchedulerFactory.getDefaultScheduler().rescheduleJob(
                    scheduId + "_trigger",
                    scheduId + "_group",
                    trigger);
        }
    }

    /**
     * 檢查ScheduleId是否已存在
     *
     * @param scheduleId
     * @return
     * @throws SchedulerException
     */
    public boolean isScheduled(String scheduleId) throws SchedulerException {
        String[] groupNames = StdSchedulerFactory.getDefaultScheduler().getJobGroupNames();
        String[] jobs = null;
        for (int i = 0; i < groupNames.length; i++) {
            jobs = StdSchedulerFactory.getDefaultScheduler().getJobNames(groupNames[i]);
            for (int j = 0; j < jobs.length; j++) {
                if (jobs[j].equals(scheduleId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 重新啟用指定的Schedule ID
     *
     * @param scheduleID
     * @throws JBranchException
     * @throws ParseException
     * @throws SchedulerException
     * @throws SQLException
     */
    public void resume(String scheduleID) throws JBranchException, SchedulerException, ParseException, SQLException {
        if (dataAccessManager == null) {
            throw new JBranchException("DataAccessManager init failed!");
        }
        TbsysschdVO vo2 = (TbsysschdVO) dataAccessManager.findByPKey(TbsysschdVO.TABLE_UID, scheduleID);
        if (vo2 == null) {
            throw new JBranchException(SchedulerErrMsg.SCHEDULE_ID_NOT_FOUND);
        }
        Map<String, TBSYSWORKDAYRULEVO> ruleMap = workDayUtils.getWorkDayRuleMap();
        TBSYSWORKDAYRULEVO vo = ruleMap.get(vo2.getscheduleid());
        String calendarDataProvider = null;
        if (vo != null) {
            calendarDataProvider = vo.getCALENDAR_PROVIDER_ID();
        }
        if (calendarDataProvider != null && !PlatformContext.beanExists(calendarDataProvider)) {
            logger.warn("排程[" + vo2.getscheduleid() + "]加入calendarDataProvider[" + calendarDataProvider + "]失敗");
        }
        if (isScheduled(scheduleID)) {
            if (logger.isDebugEnabled()) {
                logger.debug("resume " + vo2.getscheduleid());
            }
            CronTrigger trigger = new CronTrigger(
                    vo2.getscheduleid() + "_trigger",//name
                    vo2.getscheduleid() + "_group",//group
                    vo2.getscheduleid(),//jobName
                    SchedulerHelper.DEFAULT_GROUP,//jobGroup
                    vo2.getcronexpression());
            if (vo != null) {
                trigger.setCalendarName(vo.getRULE_ID());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("firetime:" + StdSchedulerFactory.getDefaultScheduler().rescheduleJob(
                        vo2.getscheduleid() + "_trigger",
                        vo2.getscheduleid() + "_group",
                        trigger));
            }
        }
        else {
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
        }
        vo2.setIsuse("Y");
        dataAccessManager.update(vo2);
    }

    /**
     * 手動呼叫排程
     *
     * @param scheduleId
     * @throws Exception
     */
    public void run(String scheduleId) throws Exception {
        run(scheduleId, false);
    }

    public void scheduleAllJobs() throws Exception {
        Session session = null;
//		Transaction transaction = null;
        try {
            Map<String, TBSYSWORKDAYRULEVO> extMap = workDayUtils.getWorkDayRuleMap();
            if (logger.isDebugEnabled()) {
                logger.debug("從DB載入排程");
            }
            com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                    com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
            SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
            session = sf.openSession();
//			transaction       = session.beginTransaction();
            //String processor  = StdSchedulerFactory.getDefaultScheduler().getSchedulerName();
            String processor = PropertyUtil.getInstance().getProcessor();
            Query query = session.createQuery("from TbsysschdVO where isuse='Y' and processor=?");
            query.setString(0, processor);
            List<TbsysschdVO> list = query.list();
//			transaction.commit();

            //排程設定，逐筆加入排程
            for (TbsysschdVO vo2 : list) {
                try {
                    TBSYSWORKDAYRULEVO vo = extMap.get(vo2.getscheduleid());
                    String calendarDataProvider = null;
                    if (vo != null) {
                        calendarDataProvider = vo.getCALENDAR_PROVIDER_ID();
                    }
                    if (calendarDataProvider != null && !PlatformContext.beanExists(calendarDataProvider)) {
                        logger.warn("排程[{}]加入calendarDataProvider[{}]失敗", vo2.getscheduleid(), calendarDataProvider);
                    }
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
                    StdSchedulerFactory.getDefaultScheduler().start();

                    vo2.setlasttry(new Timestamp(System.currentTimeMillis()));
                    vo2.setisscheduled("Y");
                    try {
//						transaction = session.beginTransaction();
                        session.saveOrUpdate(vo2);
                    	logger.info("加入排程scheduleid="+vo2.getscheduleid());
//						transaction.commit();
                    }
                    catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    String msg = StringUtil.getStackTraceAsString(e);

                    vo2.setisscheduled("N");
                    try {
//						transaction = session.beginTransaction();
                        session.saveOrUpdate(vo2);
//						transaction.commit();
                    }
                    catch (Exception ex) {
                        logger.error(e.getMessage(), e);
                    }

                    ScheduleUtil.audit(sf, vo2.getscheduleid()
                            , vo2.getschedulename()
                            , SchedulerHelper.STATUS_FINISHED
                            , SchedulerHelper.SCHEDULE_WORKSTATION
                            , SchedulerHelper.RESULT_FAILURE, msg);
                }
            }
            //session.flush();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
//			try{transaction.commit();}catch(Exception e){}
            try {
                session.close();
            }
            catch (Exception e) {
            }
        }
    }

    public static void shutdown() throws SchedulerException {
        StdSchedulerFactory.getDefaultScheduler().shutdown();
    }

    public static void start() throws SchedulerException {
        StdSchedulerFactory.getDefaultScheduler().start();
    }

    /**
     * 手動呼叫排程
     *
     * @param scheduleId
     * @param isOneTime
     * @throws Exception
     */
    private void run(String scheduleId, boolean isOneTime) throws Exception {
        if (!this.isScheduled(scheduleId)) {
            //throw new Exception("schedule "+scheduleId+" not found");
            Session session = null;
            Transaction transaction = null;
            try {
                com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                        com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
                SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
                session = sf.openSession();
                transaction = session.beginTransaction();
                String hql = "from TbsysschdVO where scheduleid=?";
                if (isOneTime) {
                    hql += " and ONETIME='Y'";
                }
                Query query = session.createQuery(hql);
                query.setString(0, scheduleId);

                List<TbsysschdVO> list = query.list();
                if (list == null || list.size() == 0) {
                    throw new JBranchException(SchedulerErrMsg.SCHEDULE_ID_NOT_FOUND);
                }

                TbsysschdVO vo2 = list.get(0);
                String cronExpression = vo2.getcronexpression();
                this.addSchedule(scheduleId, cronExpression);
            }
            finally {
                try {
                    transaction.commit();
                }
                catch (Exception e) {
                }
                try {
                    session.close();
                }
                catch (Exception e) {
                }
            }
        }
        JobDataMap jm = new JobDataMap();
        jm.put(SchedulerHelper.SCHEDULE_TYPE_KEY, SchedulerHelper.MANUAL_START);
        StdSchedulerFactory.getDefaultScheduler().triggerJobWithVolatileTrigger(scheduleId, SchedulerHelper.DEFAULT_GROUP, jm);
        if (isOneTime) {
            StdSchedulerFactory.getDefaultScheduler().deleteJob(scheduleId, SchedulerHelper.DEFAULT_GROUP);
        }
    }

    /**
     * 新增Schedule
     *
     * @param scheduleId
     * @param cronExpression
     * @throws SchedulerException
     * @throws ParseException
     * @throws Exception
     */
    public void addSchedule(String scheduleId, String cronExpression) throws SchedulerException, ParseException {
        JobDetail jd = new JobDetail(scheduleId, SchedulerHelper.DEFAULT_GROUP, ScheduleProxy.class);
        jd.getJobDataMap().put(SchedulerHelper.SCHEDULE_ID_KEY, scheduleId);
        jd.getJobDataMap().put(SchedulerHelper.SCHEDULE_TYPE_KEY, SchedulerHelper.AUTO_START);

        CronTrigger trigger = new CronTrigger(
                scheduleId + "_trigger",
                scheduleId + "_group",
                scheduleId,
                SchedulerHelper.DEFAULT_GROUP,
                cronExpression);//StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs();

        StdSchedulerFactory.getDefaultScheduler().scheduleJob(jd, trigger);
        StdSchedulerFactory.getDefaultScheduler().start();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    public void setWorkDayUtils(WorkDayUtils workDayUtils) {
        this.workDayUtils = workDayUtils;
    }
}
