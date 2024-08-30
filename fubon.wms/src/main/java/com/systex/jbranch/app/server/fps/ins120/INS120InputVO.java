package com.systex.jbranch.app.server.fps.ins120;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS120InputVO extends PagingInputVO {
	private String agree_keyno;		//主鍵
	private String branchId;		//分行
	private String aoCode;			//理專員編
	private String custId;			//被保人
	private String custName;		//被保人
	private Date agreeSdate;		//同意書上傳日期(起)
	private Date agreeEdate;		//同意書上傳日期(迄)
		
	public String getAGREE_KEYNO() {
		return agree_keyno;
	}
	public void setAGREE_KEYNO(String AGREE_KEYNO) {
		this.agree_keyno = AGREE_KEYNO;
	}
	
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
	public String getAoCode() {
		return aoCode;
	}
	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public Date getAgreeSdate() {
		return agreeSdate;
	}
	public void setAgreeSdate(Date agreeSdate) {
		this.agreeSdate = agreeSdate;
	}
	
	public Date getAgreeEdate() {
		return agreeEdate;
	}
	public void setAgreeEdate(Date agreeEdate) {
		this.agreeEdate = agreeEdate;
	}
	
	@Override
	public String toString() {
		return "INS120InputVO [AGREE_KEYNO=" + ",AGREE_KEYNO" + ", branchId=" + branchId
				+ ", aoCode=" + aoCode + ", custId=" + custId + ", custName="
				+ custName + ", agreeSdate=" + agreeSdate + ", agreeEdate="
				+ agreeEdate + "]";
	}
				
}
