package com.systex.jbranch.platform.common.scheduler;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class AuditLogProxy implements AuditLogProxyIF{
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void audit(AuditLog auditLog, String message) throws Exception{
		try {
			auditLog.audit(message);
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateAudit(AuditLog auditLog, String memo, String result,
			  long failrecord, long insertrecord, 
              long totalrecord, long updaterecord) throws Exception{
		try {
			auditLog.updateAuditMaster(memo, result, failrecord, insertrecord, totalrecord, updaterecord);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateAudit(AuditLog auditLog, String memo, String result) throws Exception{
		try {
			auditLog.updateAudit(memo, result);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateAuditDescription(AuditLog auditLog, String description, List args) throws Exception{
		try {
			auditLog.updateAuditDescription(description, args);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateAuditMemo(AuditLog auditLog, String memo, List args) throws Exception{
		try {
			auditLog.updateAuditMemo(memo, args);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateAudit(AuditLog auditLog, String description, String memo, String result) throws Exception{
		try {
			auditLog.updateAudit(description, memo, result);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateAudit(AuditLog auditLog, String result) throws Exception{
		try {
			auditLog.updateAudit(result);
		} catch (Exception e) {
			throw e;
		}
	}

}
