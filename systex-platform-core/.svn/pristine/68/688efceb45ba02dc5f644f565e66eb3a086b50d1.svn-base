package com.systex.jbranch.platform.common.scheduler;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import com.systex.jbranch.platform.common.util.PlatformContext;

public abstract class JobBase {
// ------------------------------ FIELDS ------------------------------

    private AuditLogUtil audit = null;
    private List sessionList = null;

    //Log資訊
    private int failRecord = -1;
    private int totalRecord = -1;
    private int insertRecord = -1;
    private int udpateRecord = -1;
    private int result = 0;//(1=success, 2=failure, 3=wraning)
    private String status = null;
    private String note = null;
    private String memo = null;
	private Logger logger = LoggerFactory.getLogger(JobBase.class);

// -------------------------- OTHER METHODS --------------------------

    /**
     * 排程JOB需實作此method
     *
     * @param con
     * @param jobParaMap
     * @param scheduleParaMap
     */
    public abstract void execute(Connection con, Map jobParaMap, Map scheduleParaMap) throws Exception;

    public void setResult(String result) {
        this.result = Integer.parseInt(result);
    }

    protected Connection getConnection() throws Exception {
        try {
            com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                    com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
            SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
//            Session s = sf.openSession();
            Connection conn = SessionFactoryUtils.getDataSource(sf).getConnection();
//            sessionList.add(s);
            return conn;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    protected Connection getConnection(String dbId) throws Exception {
        try {
            com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                    com.systex.jbranch.platform.common.dataaccess.datasource.DataSource(dbId);
            SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
//            Session s = sf.openSession();
            Connection conn = SessionFactoryUtils.getDataSource(sf).getConnection();
//            sessionList.add(s);
            return conn;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 停止執行此Job
     */
    protected void stopJob() {
        audit("Calling Stop Job");
        throw new RuntimeException(SchedulerHelper.STOP_JOB_EXCEPTION);
    }

    protected void audit(String message) {
        try {
        	logger.debug(message);
            audit.audit(message);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 停止執行此排程
     */
    protected void stopSchedule() {
        audit("Calling Stop Schedule");
        throw new RuntimeException(SchedulerHelper.STOP_SCHEDULE_EXCEPTOIN);
    }

    protected void updateAudit(String result) {
        try {
            audit.updateAudit(result);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    void setAuditLogUtil(AuditLogUtil audit) {
        this.audit = audit;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    private void setSessionList(List sessionList) {
        this.sessionList = sessionList;
    }

    protected int getFailRecord() {
        return failRecord;
    }

    protected void setFailRecord(int failRecord) {
        this.failRecord = failRecord;
    }

    protected int getInsertRecord() {
        return insertRecord;
    }

    protected void setInsertRecord(int insertRecord) {
        this.insertRecord = insertRecord;
    }

    protected String getNote() {
        return note;
    }

    protected void setNote(String note) {
        this.note = note;
    }

    protected String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    protected int getTotalRecord() {
        return totalRecord;
    }

    protected void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    protected int getUdpateRecord() {
        return udpateRecord;
    }

    protected void setUdpateRecord(int udpateRecord) {
        this.udpateRecord = udpateRecord;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
