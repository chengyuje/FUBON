package com.systex.jbranch.app.server.fps.ins132;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS132InputVO extends PagingInputVO {
	
	private String custID;              //被保人
	private String custName;            //被保人姓名
	private Date custBirth;			//被保人生日
	private String custGender;			//被保人性別
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public Date getCustBirth() {
		return custBirth;
	}
	public void setCustBirth(Date custBirth) {
		this.custBirth = custBirth;
	}
	public String getCustGender() {
		return custGender;
	}
	public void setCustGender(String custGender) {
		this.custGender = custGender;
	}
	
		
}
