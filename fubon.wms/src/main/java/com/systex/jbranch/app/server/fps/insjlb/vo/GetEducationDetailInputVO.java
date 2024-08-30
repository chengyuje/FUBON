package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.Date;
import java.util.List;

public class GetEducationDetailInputVO {
	private Date lastUpdate;//子女教育明細最後更新日 //2012/12/20,賴禮強,ADD
	private List lstEduReq;
	private List lstEduReq1;
	private List lstEduReq2;
	
	public List getLstEduReq() {
		return lstEduReq;
	}
	public void setLstEduReq(List lstEduReq) {
		this.lstEduReq = lstEduReq;
	}
	public List getLstEduReq1() {
		return lstEduReq1;
	}
	public void setLstEduReq1(List lstEduReq1) {
		this.lstEduReq1 = lstEduReq1;
	}
	public List getLstEduReq2() {
		return lstEduReq2;
	}
	public void setLstEduReq2(List lstEduReq2) {
		this.lstEduReq2 = lstEduReq2;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
