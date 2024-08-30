package com.systex.jbranch.app.server.fps.kyc110;


import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC110InputVO extends PagingInputVO{
	
	private String QUESTION_DESC;
	private Date sDate;
	private Date eDate;
	private String QUESTION_VERSION;
	
	public String getQUESTION_VERSION() {
		return QUESTION_VERSION;
	}
	public void setQUESTION_VERSION(String qUESTION_VERSION) {
		QUESTION_VERSION = qUESTION_VERSION;
	}
	public String getQUESTION_DESC() {
		return QUESTION_DESC;
	}
	public void setQUESTION_DESC(String qUESTION_DESC) {
		QUESTION_DESC = qUESTION_DESC;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	
	
	
}
