package com.systex.jbranch.platform.common.scheduler;

import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterVO;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AuditLogUtil {
// ------------------------------ FIELDS ------------------------------

    private AuditLogProxyIF proxy = null;
    private AuditLog instance = null;
	private Logger logger = LoggerFactory.getLogger(AuditLogUtil.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public AuditLogUtil(TbsysschdadmasterVO mvo, SessionFactory sf) throws Exception {
        try {
            instance = new AuditLog(mvo, sf);
            proxy = (AuditLogProxyIF) PlatformContext.getBean(AuditLogProxyIF.BEAN_ID);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public void audit(String message) throws Exception {
        proxy.audit(instance, message);
    }

    public String getDescription() {
        return this.instance.getDescription();
    }

    public long getFailRecord() {
        return instance.getFailRecord();
    }

    public long getInsertRecord() {
        return instance.getInsertRecord();
    }

    public String getMemo() {
        return this.instance.getMemo();
    }

    public String getResult() {
        return instance.getResult();
    }

    public long getTotalRecord() {
        return instance.getTotalRecord();
    }

    public long getUdpateRecord() {
        return instance.getUdpateRecord();
    }

    public void setDescription(String description) {
        this.instance.setDescription(description);
    }

    public void setMemo(String memo) {
        this.instance.setMemo(memo);
    }

    public void updateAudit(String result) throws Exception {
        proxy.updateAudit(instance, result);
    }

    public void updateAudit(String memo, String result) throws Exception {
        proxy.updateAudit(instance, memo, result);
    }

    public void updateAudit(String description, String memo, String result) throws Exception {
        proxy.updateAudit(instance, description, memo, result);
    }

    public void updateAudit(String memo, String result,
                            long failrecord, long insertrecord,
                            long totalrecord, long updaterecord) throws Exception {
        proxy.updateAudit(instance, memo, result, failrecord, insertrecord, totalrecord, updaterecord);
    }

    public void updateAuditDescription(String description, List args) throws Exception {
        proxy.updateAuditDescription(instance, description, args);
    }

    public void updateAuditMemo(String memo, List args) throws Exception {
        proxy.updateAuditMemo(instance, memo, args);
    }
}
