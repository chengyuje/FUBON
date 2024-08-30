package com.systex.jbranch.app.server.fps.cam150;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM150InputVO extends PagingInputVO{
	
	private BigDecimal seqNo;
	
	private String campID;
	private String campName;
	private String source_id;
	private String channel;
	private String checkStatus;
	private Date sDate;
	private Date sDate2;
	private Date eDate;
	private Date eDate2;
	private String docName;

	public BigDecimal getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(BigDecimal seqNo) {
		this.seqNo = seqNo;
	}

	public String getCampID() {
		return campID;
	}

	public void setCampID(String campID) {
		this.campID = campID;
	}

	public String getCampName() {
		return campName;
	}
	
	public void setCampName(String campName) {
		this.campName = campName;
	}
	
	public String getSource_id() {
		return source_id;
	}
	
	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getCheckStatus() {
		return checkStatus;
	}
	
	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}
	
	public Date getsDate() {
		return sDate;
	}
	
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	
	public Date getsDate2() {
		return sDate2;
	}
	
	public void setsDate2(Date sDate2) {
		this.sDate2 = sDate2;
	}
	
	public Date geteDate() {
		return eDate;
	}
	
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	
	public Date geteDate2() {
		return eDate2;
	}
	
	public void seteDate2(Date eDate2) {
		this.eDate2 = eDate2;
	}
	
	public String getDocName() {
		return docName;
	}
	
	public void seteDocName(String docName) {
		this.docName = docName;
	}
	
}
