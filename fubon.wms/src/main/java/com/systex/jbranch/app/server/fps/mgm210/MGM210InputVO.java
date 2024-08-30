package com.systex.jbranch.app.server.fps.mgm210;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MGM210InputVO extends PagingInputVO {
	
	private String actSEQ;		//一筆
	private String[] actSeq; 	//多筆
	private String actStatus;
	

	public String getActSEQ() {
		return actSEQ;
	}
	public void setActSEQ(String actSEQ) {
		this.actSEQ = actSEQ;
	}
	public String[] getActSeq() {
		return actSeq;
	}
	public void setActSeq(String[] actSeq) {
		this.actSeq = actSeq;
	}
	public String getActStatus() {
		return actStatus;
	}
	public void setActStatus(String actStatus) {
		this.actStatus = actStatus;
	}
		
}
