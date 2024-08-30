package com.systex.jbranch.app.server.fps.crm822;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM822InputVO extends PagingInputVO {
	private String cust_id;	
	private String branch;
	private String CurAcc;
	private Date StartDt;
	private Date EndDt;

	public String getCust_id() {
		return cust_id;
	}
	
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
	public String getBranch() {
		return branch;
	}
	
	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getCurAcc() {
		return CurAcc;
	}

	public void setCurAcc(String curAcc) {
		CurAcc = curAcc;
	}

	public Date getStartDt() {
		return StartDt;
	}

	public void setStartDt(Date startDt) {
		StartDt = startDt;
	}

	public Date getEndDt() {
		return EndDt;
	}

	public void setEndDt(Date endDt) {
		EndDt = endDt;
	}
	
}
