package com.systex.jbranch.app.server.fps.crm821;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM821InputVO extends PagingInputVO {
	private String cust_id;
	private String prod_id;
	private Date sDate;
	private Date eDate;
	private String isOBU;
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
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
	public String getIsOBU() {
		return isOBU;
	}
	public void setIsOBU(String isOBU) {
		this.isOBU = isOBU;
	}

}
