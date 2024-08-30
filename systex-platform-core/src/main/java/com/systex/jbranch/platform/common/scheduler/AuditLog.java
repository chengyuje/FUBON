package com.systex.jbranch.platform.common.scheduler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.multiLang.MultiLangUtil;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterVO;

public class AuditLog {
// ------------------------------ FIELDS ------------------------------

    private TbsysschdadmasterVO mvo = null;
    private SessionFactory sf = null;

    private long failRecord = -1;
    private long totalRecord = -1;
    private long insertRecord = -1;
    private long udpateRecord = -1;
    private String result = "0";//(1=success, 2=failure, 3=warning)
	private Logger logger = LoggerFactory.getLogger(AuditLog.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public AuditLog(TbsysschdadmasterVO mvo, SessionFactory sf) {
        this.mvo = mvo;
        this.sf = sf;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * 記錄排程稽核記錄
     *
     * @param message
     */
    public void audit(String message) throws Exception {
        try {
            ScheduleUtil.audit(sf,mvo.getcomp_id().getscheduleid() ,mvo.getcomp_id().getauditid(),
                    mvo.gettype(), message, SchedulerHelper.SCHEDULER_USER);
//			mvo.setdescription(message);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public String getDescription() {
        return this.mvo.getdescription();
    }

    public String getMemo() {
        return this.mvo.getMemo();
    }

    public void setDescription(String description) {
        this.mvo.setdescription(description);
    }

    public void setMemo(String memo) {
        this.mvo.setMemo(memo);
    }

    public void updateAudit(String result) throws Exception {
        try {
            mvo.setresult(result);
            ScheduleUtil.updateAuditMaster(sf, mvo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public void updateAudit(String memo, String result) throws Exception {
        try {
            mvo.setMemo(getMultLangMsg(memo));
            mvo.setresult(result);
            ScheduleUtil.updateAuditMaster(sf, mvo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public void updateAudit(String description, String memo, String result) throws Exception {
        try {
            mvo.setdescription(getMultLangMsg(description));
            mvo.setMemo(getMultLangMsg(memo));
            mvo.setresult(result);
            ScheduleUtil.updateAuditMaster(sf, mvo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private String getMultLangMsg(String msg) {
        return getMultLangMsg(msg, null);
    }

    public void updateAuditDescription(String description, List args) throws Exception {
        try {
            mvo.setdescription(getMultLangMsg(description, args));
            ScheduleUtil.updateAuditMaster(sf, mvo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新排程稽核記錄主檔
     *
     * @param sf
     * @param invo
     * @throws Exception
     */
    public void updateAuditMaster(String memo, String result,
                                  long failrecord, long insertrecord,
                                  long totalrecord, long updaterecord) throws Exception {
        try {
//			mvo.setdescription(invo.getdescription());
//			mvo.setendtime(invo.getendtime());
//			mvo.setstatus(invo.getstatus());

            this.setResult(result);
            this.setFailRecord(failrecord);
            this.setInsertRecord(insertrecord);
            this.setTotalRecord(totalrecord);
            this.setUdpateRecord(updaterecord);

            mvo.setfailrecord(new BigDecimal(failrecord));
            mvo.setMemo(getMultLangMsg(memo));
            mvo.setresult(result);
            mvo.setInsertrecord(new BigDecimal(insertrecord));
            mvo.settotalrecord(new BigDecimal(totalrecord));
            mvo.setUpdaterecord(new BigDecimal(updaterecord));
            mvo.setLastupdate(new Timestamp(System.currentTimeMillis()));
            mvo.setModifier(SchedulerHelper.SCHEDULER_USER);
            mvo.setendtime(new Timestamp(System.currentTimeMillis()));
            ScheduleUtil.updateAuditMaster(sf, mvo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public void updateAuditMemo(String memo, List args) throws Exception {
        try {
            mvo.setMemo(getMultLangMsg(memo, args));
            ScheduleUtil.updateAuditMaster(sf, mvo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private String getMultLangMsg(String msg, List args) {
        if (msg == null) {
            return null;
        }
        String result = null;
        if (args == null) {
            result = MultiLangUtil.getMessage(null, msg);
        }
        else {
            result = MultiLangUtil.getMessage(null, msg, args);
        }
        if (result.length() > 1024) {
            result = result.substring(0, 1024);
        }
        return result;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public long getFailRecord() {
        return failRecord;
    }

    public void setFailRecord(long failRecord) {
        this.failRecord = failRecord;
    }

    public long getInsertRecord() {
        return insertRecord;
    }

    public void setInsertRecord(long insertRecord) {
        this.insertRecord = insertRecord;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
    }

    public long getUdpateRecord() {
        return udpateRecord;
    }

    public void setUdpateRecord(long udpateRecord) {
        this.udpateRecord = udpateRecord;
    }
}
