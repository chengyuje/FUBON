package com.systex.jbranch.platform.common.scheduler;

import java.util.List;

public interface AuditLogProxyIF {
	public static final String BEAN_ID = "scheduleaudit";
	public void audit(AuditLog auditLog, String message) throws Exception;
	public void updateAudit(AuditLog auditLog, String memo, String result,
			  long failrecord, long insertrecord, 
              long totalrecord, long updaterecord) throws Exception;
	public void updateAudit(AuditLog auditLog, String memo, String result) throws Exception;
	public void updateAuditDescription(AuditLog auditLog, String description, List args) throws Exception;
	public void updateAuditMemo(AuditLog auditLog, String memo, List args) throws Exception;
	public void updateAudit(AuditLog auditLog, String description, String memo, String result) throws Exception;
	public void updateAudit(AuditLog auditLog, String result) throws Exception;
}
