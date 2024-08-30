package com.systex.jbranch.app.server.fps.crm8502;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM8502InputVO extends PagingInputVO {
	
	private BigDecimal seq;				//客戶資況表申請流水號
	private String custID;				//客戶ID
	private String branchNbr;			//分行別
	private Date applySdate;			//申請起日
	private Date applyEdate;			//申請迄日
	private String printStatus;			//列印狀態
	private Date applyDate;				//申請日期
		
	public BigDecimal getSeq() {
		return seq;
	}
	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	
	public Date getApplySdate() {
		return applySdate;
	}
	public void setApplySdate(Date applySdate) {
		this.applySdate = applySdate;
	}
	
	public Date getApplyEdate() {
		return applyEdate;
	}
	public void setApplyEdate(Date applyEdate) {
		this.applyEdate = applyEdate;
	}
	
	public String getPrintStatus() {
		return printStatus;
	}
	public void setPrintStatus(String printStatus) {
		this.printStatus = printStatus;
	}
	
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	
	@Override
	public String toString() {
		return "CRM8502InputVO [seq=" + seq + ", custID=" + custID
				+ ", branchNbr=" + branchNbr + ", applySdate=" + applySdate
				+ ", applyEdate=" + applyEdate + ", printStatus=" + printStatus
				+ ", applyDate=" + applyDate + "]";
	}	
		
}
