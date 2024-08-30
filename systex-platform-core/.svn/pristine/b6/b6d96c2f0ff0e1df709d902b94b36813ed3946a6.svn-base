package com.systex.jbranch.platform.common.scheduler;

import java.sql.Connection;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterVO;

public class JDBCScheduleJob implements JDBCScheduleJobIF {
// ------------------------------ FIELDS ------------------------------

    //	private List sessionList = new ArrayList();
    private AuditLogUtil audit = null;
    private TbsysschdadmasterVO mvo = null;
	private Logger logger = LoggerFactory.getLogger(JDBCScheduleJob.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface JDBCScheduleJobIF ---------------------

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void executeScheduleJob(SessionFactory sessionFactory, AuditLogUtil audit, Map jobInfoMap, Map scheduleParaMap, TbsysschdadmasterVO mvo) throws Exception {
        Session session = null;
        Connection conn = null;
        
        try {
            this.audit = audit;
            this.mvo = mvo;
            //取得job相關參數
            String classname = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_CLASSNAME);
            String precondition = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_PRECONDITION);
            String postcondition = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_POSTCONDITION);
            String parameterStr = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_PARAMETER);
            Map jobParaMap = ScheduleUtil.getParameter(parameterStr);//key1=value1;key2=value2
//            session = sessionFactory.openSession();
//			sessionList.add(session);
            conn = SessionFactoryUtils.getDataSource(sessionFactory).getConnection();
            conn.setAutoCommit(false);
            //JobBase jobBase = (JobBase) Class.forName(classname).newInstance();
            JobBase jobBase = (JobBase) jobInfoMap.get(SchedulerHelper.JOB_INSTANCE);//eric;

            scheduleParaMap.put(SchedulerHelper.AUDIT_PARAMETER_KEY, audit);

            //執行PreCondition
            exePrecondition(conn, precondition, jobParaMap, scheduleParaMap, (PreConditionIF) jobInfoMap.get(SchedulerHelper.PRE_INSTANCE));

            //執行Job
            exeJob(conn, classname, jobParaMap, scheduleParaMap, jobBase);

            //執行PostCondition
            exePostcondition(conn, postcondition, jobParaMap, scheduleParaMap, (PostConditionIF) jobInfoMap.get(SchedulerHelper.POST_INSTANCE));
        }
        finally {
        	if (conn != null) {
                try {
                	conn.close();
                }
                catch (Exception e) {
                    //ingore
                }
            }
        	
            if (session != null) {
                try {
                    session.close();
                }
                catch (Exception e) {
                    //ingore
                }
            }
        }
    }

    /**
     * 執行Job Class
     *
     * @param transientVars
     * @param args
     * @param classname
     * @param jobParaMap
     * @param scheduleParaMap
     */
    private void exeJob(Connection con, String classname, Map jobParaMap,
                        Map scheduleParaMap, JobBase jobBase) throws Exception {
        if (classname == null || classname.trim().length() == 0) {
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("start job " + classname);
        }

        jobBase.setAuditLogUtil(audit);
        try {
            audit.audit("start job");
            jobBase.execute(con, jobParaMap, scheduleParaMap);
            updateAuditLog(jobBase);
            audit.audit("end job");
        }
        catch (Exception e) {
            audit.audit("execute job class error " + e.getMessage());
            logger.error("execute job class error " + classname, e);
            throw e;
        }
    }

    private void updateAuditLog(JobBase jobBase) throws Exception {
        long failrecord = jobBase.getFailRecord();
        long insertRecord = jobBase.getInsertRecord();
        long totalRecord = jobBase.getTotalRecord();
        long updateRecord = jobBase.getUdpateRecord();
        String result = String.valueOf(jobBase.getResult());
        String memo = jobBase.getMemo();
        if (failrecord == -1 && mvo.getfailrecord() != null) {
            failrecord = mvo.getfailrecord().longValue();
        }
        if (insertRecord == -1 && mvo.getInsertrecord() != null) {
            insertRecord = mvo.getInsertrecord().longValue();
        }
        if (totalRecord == -1 && mvo.gettotalrecord() != null) {
            totalRecord = mvo.gettotalrecord().longValue();
        }
        if (updateRecord == -1 && mvo.getUpdaterecord() != null) {
            updateRecord = mvo.getUpdaterecord().longValue();
        }
        if (result == null || "0".equals(result)) {
            result = mvo.getresult();
        }
        if (memo == null) {
            memo = mvo.getMemo();
        }

        audit.updateAudit(memo, result, failrecord,
                insertRecord, totalRecord, updateRecord);
    }

    /**
     * 執行Post Condition
     *
     * @param postcondition
     * @param jobParaMap
     * @param scheduleParaMap
     */
    private void exePostcondition(Connection con, String postcondition, Map jobParaMap, Map scheduleParaMap, PostConditionIF postc) throws Exception {
        if (postcondition == null || postcondition.trim().length() == 0) {
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("start postcondition " + postcondition);
        }
        try {
            audit.audit("start postcondition");
            postc.process(con, jobParaMap, scheduleParaMap);
            audit.audit("end postcondition");
        }
        catch (Exception e) {
            audit.audit("execute postcondition error " + e.getMessage());
            logger.error("execute postcondition error " + postcondition, e);
            throw e;
        }
    }

    /**
     * 執行PreCondition
     *
     * @param precondition
     * @param jobParaMap
     * @param scheduleParaMap
     */
    private void exePrecondition(Connection con, String precondition, Map jobParaMap, Map scheduleParaMap, PreConditionIF prec) throws Exception {
        if (precondition == null || precondition.trim().length() == 0) {
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("start precondition " + precondition);
        }

        try {
            audit.audit("start precondition");
            prec.process(con, jobParaMap, scheduleParaMap);
            audit.audit("end precondition");
        }
        catch (Exception e) {
            audit.audit("execute precondition error " + e.getMessage());
            logger.error("execute precondition error " + precondition, e);
            throw e;
        }
    }
}
