package com.systex.jbranch.app.server.fps.cmmgr005;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR005InputVO extends PagingInputVO{
	
	public CMMGR005InputVO()
	{
	}
	private String auditid;
	private String scheduleId;
	private String jobId;
	private String type;
	private String status;
	private String result;
	private String hlbAuditId;
	private Date startDate;
	private Date endDate;
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getAuditid() {
		return auditid;
	}
	public void setAuditid(String auditid) {
		this.auditid = auditid;
	}
	public String getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public void setHlbAuditId(String hlbAuditId) {
		this.hlbAuditId = hlbAuditId;
	}
	public String getHlbAuditId() {
		return hlbAuditId;
	}
	
	


}
