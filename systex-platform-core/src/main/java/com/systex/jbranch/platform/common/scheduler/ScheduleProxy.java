package com.systex.jbranch.platform.common.scheduler;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import com.systex.jbranch.platform.common.classloader.DynamicFileExtClassLoader;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobclassVO;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.conversation.ConversationIF;

public class ScheduleProxy implements Job {
// ------------------------------ FIELDS ------------------------------

    private DataAccessScheduleJobIF dataAccessScheduleJob = null;
    private JDBCScheduleJobIF jdbcScheduleJob = null;
    private SessionFactory sf = null;
    private Logger logger = LoggerFactory.getLogger(ScheduleProxy.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Job ---------------------

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();//context.getJobDetail().getJobDataMap();
        TbsysschdVO vo = null;
        String scheduleId = null;
        String fileSeparator = System.getProperty("file.separator");
        StringBuffer libPath = new StringBuffer();
        libPath.append(DataManager.getRoot()).append(fileSeparator).append("Platform").append(fileSeparator).append("Schedule").append(fileSeparator).append("lib").append(fileSeparator);
        if (logger.isDebugEnabled()) {
            logger.debug("DynamicFileExtClassLoader libPath=" + libPath.toString());
        }
        DynamicFileExtClassLoader dyClassLoader = new DynamicFileExtClassLoader(libPath.toString());
        
        try {
            dataAccessScheduleJob = (DataAccessScheduleJobIF) PlatformContext.getBean(
                    DataAccessScheduleJobIF.BEAN_ID);
            jdbcScheduleJob = (JDBCScheduleJobIF) PlatformContext.getBean(
                    JDBCScheduleJobIF.BEAN_ID);

            //取得Quartz排程參數

            scheduleId = dataMap.getString(SchedulerHelper.SCHEDULE_ID_KEY);
            String type = dataMap.getString(SchedulerHelper.SCHEDULE_TYPE_KEY);
            String processor = PropertyUtil.getInstance().getProcessor();

            //取得資料庫連線
            initDataSource();

            if (!checkProcessor(scheduleId, processor)) {
                StdSchedulerFactory.getDefaultScheduler().deleteJob(scheduleId, SchedulerHelper.DEFAULT_GROUP);
                if (logger.isInfoEnabled()) {
                    logger.info("processor已更動，移除[" + scheduleId + "]排程");
                }
                return;
            }
            if (logger.isInfoEnabled()) {
                logger.info("執行Schedule [" + scheduleId + "], type=[" + type + "]");
            }

            //取得UUID
            UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
            if (uuid == null) {//若沒有則建立一個虛擬的uuid
                uuid = new UUID();
                uuid.setBranchID(SchedulerHelper.SCHEDULE_BRANCH);
                uuid.setWsId(SchedulerHelper.SCHEDULE_WORKSTATION);
                uuid.setSectionID(this.toString() + System.currentTimeMillis());
                ThreadDataPool.setData(ThreadDataPool.KEY_UUID, uuid);
            }

            //取得排程資料
            vo = getScheduleInfo(scheduleId);

            //取得排程參數
            Map scheduleParaMap = ScheduleUtil.getParameter(vo.getscheduleparameter());

            //已成功執行的Job
            Set<String> successSet = new HashSet<String>();

            //取得Schedule下所有JOB
            List jobList = getJobList(dyClassLoader, scheduleId);

            //取得Job執行條件
            Map<String, List<String>> jobCondition = getJobCondition(scheduleId);

            //依序執行Job
            for (int i = 0; i < jobList.size(); i++) {
                Object stopFlag = scheduleParaMap.get(SchedulerHelper.SCHEDULER_STOP);
                if (stopFlag != null && (Boolean) stopFlag) {
                    break;
                }

                TbsysschdadmasterVO mvo = null;
                String jobid = "";
                try {
                    //呼叫job時，傳入的參數
                    Map inputMap = new HashMap();

                    //取得目前執行的Job資訊
                    Map job = (Map) jobList.get(i);
                    String schedulerParam = (String) job.get(SchedulerHelper.SCHEDULE_PARAMETER_KEY);
                    String className = (String) job.get(SchedulerHelper.JOB_INFO_KEY_CLASSNAME);
                    jobid = (String) job.get(SchedulerHelper.JOB_INFO_KEY_JOBID);
                    String jobname = (String) job.get(SchedulerHelper.JOB_INFO_KEY_JOBNAME);
                    String parameters = (String) job.get(SchedulerHelper.JOB_INFO_KEY_PARAMETER);

                    Map jobParameter = ScheduleUtil.getParameter(parameters);

//					LogUtil.writeLog(LogHelper.SCHEDULER_TYPE,
//					         LogUtil.LEVEL_INFO,
//					         "process "+scheduleId+", "+jobid);

                    if (logger.isDebugEnabled()) {
                        logger.debug("執行Job[" + jobid + "]");
                    }
                    //建立排程記錄主檔
                    mvo = createAuditMaster(scheduleId, vo.getschedulename(), jobid,
                            jobname, type, SchedulerHelper.STATUS_RUNNING, SchedulerHelper.SCHEDULER_USER);
                    dataMap.put(SchedulerHelper.EXEC_UNIT, mvo.getcomp_id().getauditid());//執行單位代號
                    dataMap.put(SchedulerHelper.JOB_INFO_KEY_JOBID, jobid);

                    //job的input參數:jobParameter/scheduleParameter
                    AuditLogUtil audit = new AuditLogUtil(mvo, sf);

                    String ip = InetAddress.getLocalHost().getHostAddress();
                    audit.audit("執行[" + className + "], 排程參數[" + schedulerParam + "], Job參數[" + parameters + "], 執行者[" + processor + "], IP[" + ip + "]");

                    boolean isJump = false;
                    List<String> requiredJobidList = jobCondition.get(jobid);
                    if (requiredJobidList != null) {
                        for (int j = 0; j < requiredJobidList.size(); j++) {
                            String requiredJobid = requiredJobidList.get(j);
                            if (!successSet.contains(requiredJobid)) {
                                String msg = "必要的Job[" + requiredJobid + "]尚未執行成功。";
                                if (logger.isDebugEnabled()) {
                                    logger.debug(msg);
                                }
                                updateAuditMaster(mvo, SchedulerHelper.RESULT_FAILURE,
                                        SchedulerHelper.STATUS_FINISHED, msg);
                                isJump = true;
                            }
                        }
                    }
                    if (isJump) {
                        continue;
                    }
                    //判斷交易類型
                    String jobType = (String) job.get(SchedulerHelper.JOB_INFO_KEY_JOBTYPE);

                    //執行job
                    if (SchedulerHelper.DATA_ACCESS_JOB.equals(jobType)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("process " + scheduleId + ", DataAccess Job " + jobid);
                        }
                        //初始化此job的ConversationIF
                        ConversationIF conversation = new ConversationImpl(uuid, inputMap);
                        inputMap.put(SchedulerHelper.JOB_PARAMETER_KEY, jobParameter);
                        inputMap.put(SchedulerHelper.SCHEDULE_PARAMETER_KEY, scheduleParaMap);
                        inputMap.put(SchedulerHelper.AUDIT_PARAMETER_KEY, audit);
                        this.saveLog(jobid, "begin");
                        dataAccessScheduleJob.executeScheduleJob(uuid, conversation, job, scheduleParaMap);
                        this.saveLog(jobid, "end");
                    }
                    else if (SchedulerHelper.JDBC_JOB.equals(jobType)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("process " + scheduleId + ", JDBC Job " + jobid);
                        }
                        jdbcScheduleJob.executeScheduleJob(sf, audit, job, scheduleParaMap, mvo);
                    }
                    else {
                        if (logger.isInfoEnabled()) {
                            logger.info("process " + scheduleId + ", unknow type Job " + jobid);
                        }

                        if (job.get(SchedulerHelper.JOB_INSTANCE) == null) {
                            throw new Exception((String) job.get(SchedulerHelper.JOB_INFO_KEY_JOBTYPE));
                        }
                        else {
                            throw new JBranchException(SchedulerErrMsg.UNKNOW_JOB_TYPE);
                        }
                    }

                    String result = SchedulerHelper.RESULT_SUCCESS;
                    long failRecord = 0;
                    long totalRecord = 0;

                    boolean isOutCheck = false;
                    if (SchedulerHelper.JDBC_JOB.equals((String) job.get(SchedulerHelper.JOB_INFO_KEY_JOBTYPE))) {
                        JobBase jobBase = (JobBase) job.get(SchedulerHelper.JOB_INSTANCE);
                        //若job已有訊息，則使用job訊息
                        result = String.valueOf(jobBase.getResult());
                        if ("0".equals(result)) {
                            isOutCheck = true;
                            failRecord = jobBase.getFailRecord();
                            totalRecord = jobBase.getTotalRecord();
                        }
                        //更新排程記錄主檔
                    }
                    else {
                        result = audit.getResult();
                        if ("0".equals(result)) {
                            isOutCheck = true;
                            failRecord = audit.getFailRecord();
                            totalRecord = audit.getTotalRecord();
                        }
                    }

                    if (isOutCheck) {
                        if (failRecord > 0 || totalRecord == 0) {
                            result = SchedulerHelper.RESULT_WARN;
                        }
                        else {
                            result = SchedulerHelper.RESULT_SUCCESS;
                        }
                    }

                    updateAuditMaster(mvo, result,
                            SchedulerHelper.STATUS_FINISHED, mvo.getdescription());

                    if (result.equals(SchedulerHelper.RESULT_SUCCESS) || result.equals(SchedulerHelper.RESULT_WARN)) {
                        successSet.add(jobid);
                    }


                    mvo = null;
                }
                catch (JBranchException e) {
                    logger.error(e.getMessage(), e);
                    this.saveLog(jobid, "end=>" + e == null ? "FAIL" : e.getMessage());
                    updateAuditMaster(mvo, SchedulerHelper.RESULT_FAILURE,
                            SchedulerHelper.STATUS_FINISHED, e.getMessage());
                }
                catch (Exception e) {//在  APServer下 RuntimeException 包了 JBranchException
                    logger.error(e.getMessage(), e);
                    this.saveLog(jobid, "end=>" + e == null ? "FAIL" : e.getMessage());
                    Throwable t = e.getCause();
                    if (t != null && (t instanceof JBranchException)) {
                        logger.error(new APException(t.getMessage()).getMessage());
                        updateAuditMaster(mvo, SchedulerHelper.RESULT_FAILURE,
                                SchedulerHelper.STATUS_FINISHED,
                                new APException(t.getMessage()).getMessage());
                    }
                    else if (t != null) {
                        String errMsg = ScheduleUtil.getStackTraceAsString(t);
                        if (errMsg != null && errMsg.length() > 1024) {
                            errMsg = errMsg.substring(0, 1024);
                        }
                        updateAuditMaster(mvo, SchedulerHelper.RESULT_FAILURE,
                                SchedulerHelper.STATUS_FINISHED, errMsg);
                    }
                    else {
                        String errMsg = ScheduleUtil.getStackTraceAsString(e);
                        if (errMsg != null && errMsg.length() > 1024) {
                            errMsg = errMsg.substring(0, 1024);
                        }
                        updateAuditMaster(mvo, SchedulerHelper.RESULT_FAILURE,
                                SchedulerHelper.STATUS_FINISHED, errMsg);
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Job[" + jobid + "]結束");
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally{
        	if(dyClassLoader != null){
        		dyClassLoader.close();
        	}
        }

        if (scheduleId != null && "Y".equals(dataMap.getString("ONETIME"))) {
            try {
                StdSchedulerFactory.getDefaultScheduler().deleteJob(scheduleId, SchedulerHelper.DEFAULT_GROUP);
                delAllByScheduleID(scheduleId, sf);
                if (logger.isDebugEnabled()) {
                    logger.debug("刪除一次性排程[" + scheduleId + "]");
                }
            }
            catch (Exception e) {
                logger.error("刪除一次性排程失敗[" + scheduleId + "]", e);
            }
        }
    }

    private boolean checkProcessor(String schedulerId, String nowProcessor) {
        Session session = null;
        try {
            session = sf.openSession();
            Query query = session.createQuery("from TbsysschdVO where SCHEDULEID=?");
            query.setString(0, schedulerId);
            List result = query.list();
            if (result == null || result.size() <= 0) {
                return true;
            }
            String dbProcessor = ((TbsysschdVO) result.get(0)).getprocessor();
            return nowProcessor.equals(dbProcessor);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return true;
        }
        finally {
            try {
                session.close();
            }
            catch (Exception e) {
            }
        }
    }

    //建立master log
    private TbsysschdadmasterVO createAuditMaster(String scheduleId, String scheduleName, String jobid,
                                                  String jobname, String type, String status, String user) throws Exception {
        try {
            return ScheduleUtil.createAuditMaster(sf, scheduleId, scheduleName,
                    jobid, jobname, type, status, user);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private void delAllByScheduleID(String scheduleId, SessionFactory sf) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete onetime schdule...["
                    + scheduleId + "]");
        }

        Session session = sf.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from TbsysschdVO where SCHEDULEID=?");
            query.setParameter(0, scheduleId);
            List<TbsysschdVO> sched = query.list();
            TbsysschdVO vo = null;
            if (sched != null && sched.size() > 0) {
                vo = sched.get(0);
            }
            if (vo != null) {
                query = session.createQuery("from TbsysschdjobassVO where SCHEDULEID=?");
                query.setParameter(0, scheduleId);
                List<TbsysschdjobassVO> assList = query.list();
                for (int i = 0; i < assList.size(); i++) {
                    TbsysschdjobassVO ass = assList.get(i);
                    query = session.createQuery("from TbsysschdjobVO where JOBID=?");
                    query.setParameter(0, ass.getComp_id().getJobid());
                    List<TbsysschdjobVO> jobList = query.list();
                    delJobAndClass(session, jobList);
                    session.delete(ass);
                }
                session.delete(vo);
                transaction.commit();
                if (logger.isDebugEnabled()) {
                    logger.debug("deleted onetime schdule["
                            + scheduleId + "].");
                }
            }
        }
        catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
        finally {
            if (session != null) {
                try {
                    session.close();
                }
                catch (HibernateException e) {
                    //ingore
                }
            }
        }
    }

    private void delJobAndClass(Session session, List<TbsysschdjobVO> jobList) {
        Query query;
        for (int i = 0; i < jobList.size(); i++) {
            TbsysschdjobVO job = jobList.get(i);
            query = session.createQuery("from TbsysschdjobclassVO where CLASSID=?");
            query.setParameter(0, job.getclassid());
            List<TbsysschdjobclassVO> classList = query.list();
            for (int j = 0; j < classList.size(); j++) {
                TbsysschdjobclassVO clazz = classList.get(j);
                session.delete(clazz);
            }
            session.delete(job);
        }
    }

    //刪除指定的scheduleId
    //取得排程相關資訊
    private void deleteScheduleId(TbsysschdVO vo) {
        Session session = null;
        Transaction transaction = null;
        try {
            StdSchedulerFactory.getDefaultScheduler().deleteJob(vo.getscheduleid(), SchedulerHelper.DEFAULT_GROUP);

            session = sf.openSession();
            transaction = session.beginTransaction();

            if (vo != null) {
                session.delete(vo);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
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

    private Map<String, List<String>> getJobCondition(String scheduleID) throws JBranchException {
        Session session = null;
        Connection conn = null;

        try {
            String sqlStr = "select SCHEDULEID, JOBID, REQUIREDJOBID from TBSYSSCHDJOBCONDI" +
                    " where SCHEDULEID=?";
            if (logger.isDebugEnabled()) {
                logger.debug("sqlStr = " + sqlStr);
            }

//            session = sf.openSession();
            conn = SessionFactoryUtils.getDataSource(sf).getConnection();
            
            PreparedStatement pstmt = conn.prepareStatement(sqlStr);

            pstmt.setString(1, scheduleID);
            ResultSet rs = pstmt.executeQuery();

            Map<String, List<String>> jobCondition = new HashMap<String, List<String>>();
            while (rs.next()) {
                String JOBID = rs.getString("JOBID");
                String REQUIREDJOBID = rs.getString("REQUIREDJOBID");
                List requiredList = jobCondition.get(JOBID);
                if (requiredList == null) {
                    requiredList = new ArrayList<String>();
                    jobCondition.put(JOBID, requiredList);
                }
                requiredList.add(REQUIREDJOBID);
            }
            return jobCondition;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JBranchException(SchedulerErrMsg.GET_JOBLIST_DATA_ERROR);
        }
        finally {
        	try {
        		if (conn != null) {
        			conn.close();
        		}
            } catch (Exception e) {
            }
        	
            try {
            	if (session != null) {
            		session.close();
        		}
            } catch (Exception e) {
            }
        }
    }

    /**
     * 取得此Schedule之下所有Job
     *
     * @param jobID
     * @return
     */
    private List getJobList(DynamicFileExtClassLoader dyClassLoader, String scheduleID) throws JBranchException {
        Session session = null;
        Connection conn = null;

        String standAlone = "true";
        StringBuffer jobStr = new StringBuffer("Job List : ");

        try {
            standAlone = PropertyUtil.getInstance().getStandALone();
        }
        catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }

        try {
            String sqlStr = "select  d.SCHEDULEPARAMETER, a.jobid, b.beanname, " +
                    "        a.precondition, a.postcondition, a.parameters, b.classname " +
                    "  from  Tbsysschdjobass c left join Tbsysschdjob a on c.jobid = a.jobid " +
                    "                          left join Tbsysschdjobclass b on a.classid = b.classid  " +
                    "                          left join Tbsysschd d on d.scheduleid = c.scheduleid " +
                    " where  c.scheduleid=? and c.joborder >= d.jobstart order by c.joborder";
            if (logger.isDebugEnabled()) {
                logger.debug(sqlStr);
            }

            List jobList = new ArrayList();
//            session = sf.openSession();
            conn = SessionFactoryUtils.getDataSource(sf).getConnection();
            
            PreparedStatement pstmt = conn.prepareStatement(sqlStr);

            pstmt.setString(1, scheduleID);
            ResultSet rs = pstmt.executeQuery();
            Object jobObj = null;
            Object preObj = null;
            Object postObj = null;
            while (rs.next()) {//依序取出每個Job資訊存入List中
                if (logger.isDebugEnabled()) {
                    logger.debug("##########################################");
                }
                Map job = new HashMap();
                job.put(SchedulerHelper.SCHEDULE_PARAMETER_KEY, rs.getString("SCHEDULEPARAMETER"));
                job.put(SchedulerHelper.JOB_INFO_KEY_JOBID, rs.getString("jobid"));
                job.put(SchedulerHelper.JOB_INFO_KEY_CLASSNAME, rs.getString("classname"));
                job.put(SchedulerHelper.JOB_INFO_KEY_BEANNAME, rs.getString("beanname"));
                job.put(SchedulerHelper.JOB_INFO_KEY_PRECONDITION, rs.getString("precondition"));
                job.put(SchedulerHelper.JOB_INFO_KEY_POSTCONDITION, rs.getString("postcondition"));
                String precondition = rs.getString("precondition");
                String postcondition = rs.getString("postcondition");
                String classname = (String) job.get(SchedulerHelper.JOB_INFO_KEY_CLASSNAME);
                String beanname = (String) job.get(SchedulerHelper.JOB_INFO_KEY_BEANNAME);
                job.put(SchedulerHelper.JOB_INFO_KEY_PARAMETER, rs.getString("parameters"));

                jobList.add(job);

                jobStr.append(job.get(SchedulerHelper.JOB_INFO_KEY_JOBID) + " ");

                try {
                    if (!Boolean.parseBoolean(standAlone)) {//不是Standalone時
                        //取得Job Instance
                        try {
//                            jobObj = Class.forName(classname).newInstance();
                        	jobObj = PlatformContext.getBean(beanname);
                            job.put(SchedulerHelper.JOB_INSTANCE, jobObj);
                            if (jobObj instanceof BizLogic) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("job=BizlogicJob " + classname);
                                }
                                job.put(SchedulerHelper.JOB_INFO_KEY_JOBTYPE, SchedulerHelper.DATA_ACCESS_JOB);
                            }
                            else if (jobObj instanceof JobBase) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("job=JobBase " + classname);
                                }
                                job.put(SchedulerHelper.JOB_INFO_KEY_JOBTYPE, SchedulerHelper.JDBC_JOB);
                            }
                            else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("job=UnknowJob " + classname);
                                }
                                job.put(SchedulerHelper.JOB_INFO_KEY_JOBTYPE, SchedulerHelper.UNKNOW_JOB);
                            }
                        }
                        catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            String errMsg = ScheduleUtil.getStackTraceAsString(e);
                            job.put(SchedulerHelper.JOB_INSTANCE, null);
                            job.put(SchedulerHelper.JOB_INFO_KEY_JOBTYPE, StringUtils.substring(errMsg, 0, 1024));
                        }

                        //取得Precondition Instance
                        if (precondition != null && precondition.trim().length() > 0) {
                            try {
//                                Class preClazz = Class.forName(precondition);
//                                preObj = preClazz.newInstance();
                                preObj = PlatformContext.getBean(precondition);
                                job.put(SchedulerHelper.PRE_INSTANCE, preObj);
                            }
                            catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                String errMsg = ScheduleUtil.getStackTraceAsString(e);
                                job.put(SchedulerHelper.PRE_INSTANCE, null);
                                job.put(SchedulerHelper.JOB_INFO_KEY_PRECONDITION_INFO, StringUtils.substring(errMsg, 0, 1024));
                            }
                        }
                        else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("precondition=" + precondition);
                            }
                        }

                        //取得Postcondition Instance
                        if (postcondition != null && postcondition.trim().length() > 0) {
                            try {
//                                Class postClazz = Class.forName(postcondition);
//                                postObj = postClazz.newInstance();
                                postObj = PlatformContext.getBean(postcondition);
                                job.put(SchedulerHelper.POST_INSTANCE, postObj);
                            }
                            catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                String errMsg = ScheduleUtil.getStackTraceAsString(e);
                                job.put(SchedulerHelper.POST_INSTANCE, null);
                                job.put(SchedulerHelper.JOB_INFO_KEY_POSTCONDITION_INFO, StringUtils.substring(errMsg, 0, 1024));
                            }
                        }
                        else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("postcondition=" + postcondition);
                            }
                        }
                    }
                    else {//Standalone時

                        //取得Job Instance
                        try {
                            if (logger.isDebugEnabled()) {
                                logger.debug("loading..." + classname);
                            }
                            Class clazz = Class.forName(classname, true, dyClassLoader);
                            jobObj = clazz.newInstance();
                            job.put(SchedulerHelper.JOB_INSTANCE, jobObj);
                            if (jobObj instanceof BizLogic) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("job=BizlogicJob " + classname);
                                }
                                job.put(SchedulerHelper.JOB_INFO_KEY_JOBTYPE, SchedulerHelper.DATA_ACCESS_JOB);
                            }
                            else if (jobObj instanceof JobBase) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("job=JobBase " + classname);
                                }
                                job.put(SchedulerHelper.JOB_INFO_KEY_JOBTYPE, SchedulerHelper.JDBC_JOB);
                            }
                            else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("job=UnknowJob " + classname);
                                }
                                job.put(SchedulerHelper.JOB_INFO_KEY_JOBTYPE, SchedulerHelper.UNKNOW_JOB);
                            }
                        }
                        catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            String errMsg = ScheduleUtil.getStackTraceAsString(e);
                            job.put(SchedulerHelper.JOB_INSTANCE, null);
                            job.put(SchedulerHelper.JOB_INFO_KEY_JOBTYPE, StringUtils.substring(errMsg, 0, 1024));
                        }

                        //取得Precondition Instance
                        if (precondition != null && precondition.trim().length() > 0) {
                            try {
                                Class preClazz = dyClassLoader.getClass(precondition);
                                preObj = preClazz.newInstance();
                                job.put(SchedulerHelper.PRE_INSTANCE, preObj);
                            }
                            catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                String errMsg = ScheduleUtil.getStackTraceAsString(e);
                                job.put(SchedulerHelper.PRE_INSTANCE, null);
                                job.put(SchedulerHelper.JOB_INFO_KEY_PRECONDITION_INFO, StringUtils.substring(errMsg, 0, 1024));
                            }
                        }
                        else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("precondition=" + precondition);
                            }
                        }

                        if (postcondition != null && postcondition.trim().length() > 0) {
                            try {
                                Class postClazz = dyClassLoader.getClass(postcondition);
                                postObj = postClazz.newInstance();
                                job.put(SchedulerHelper.POST_INSTANCE, postObj);
                            }
                            catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                String errMsg = ScheduleUtil.getStackTraceAsString(e);
                                job.put(SchedulerHelper.POST_INSTANCE, null);
                                job.put(SchedulerHelper.JOB_INFO_KEY_POSTCONDITION_INFO, StringUtils.substring(errMsg, 0, 1024));
                            }
                        }
                        else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("postcondition=" + postcondition);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            if (logger.isInfoEnabled()) {
                logger.info(jobStr.toString());
            }

            return jobList;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JBranchException(SchedulerErrMsg.GET_JOBLIST_DATA_ERROR);
        }
        finally {
        	try {
        		if (conn != null) {
        			conn.close();
        		}
            } catch (Exception e) {
            }
        	
            try {
            	if (session != null) {
            		session.close();
        		}
            } catch (Exception e) {
            }
        }
    }

    //取得排程相關資訊
    private TbsysschdVO getScheduleInfo(String scheduleId) throws JBranchException, Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            TbsysschdVO vo = (TbsysschdVO) session.get(TbsysschdVO.class, scheduleId);
            if (vo == null) {
                throw new JBranchException(SchedulerErrMsg.SCHEDULE_ID_NOT_FOUND);
            }
            return vo;
        }
        catch (JBranchException je) {
            throw je;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
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

    //取得資料庫連線
    private void initDataSource() {
        try {
            com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                    com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
            sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void updateAuditMaster(TbsysschdadmasterVO mvo, String result, String status, String description) throws Exception {
        try {
            if (mvo == null) {
                return;
            }
            mvo.setresult(result);
            mvo.setdescription(description);
            mvo.setstatus(status);

            mvo.setendtime(new Timestamp(System.currentTimeMillis()));
            mvo.setLastupdate(new Timestamp(System.currentTimeMillis()));
            ScheduleUtil.updateAuditMaster(sf, mvo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
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
    		queryCondition.setString(1, "ScheduleProxy");
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
