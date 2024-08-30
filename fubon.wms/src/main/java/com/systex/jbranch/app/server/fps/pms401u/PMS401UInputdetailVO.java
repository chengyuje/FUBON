package com.systex.jbranch.app.server.fps.pms401u;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS401UInputdetailVO extends PagingInputVO{
	private Date sCreDate;   //資料統計日期
	private Date eCreDate;   //資料統計日期
	private  String cust_id ;// 分行
	
	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public Date geteCreDate() {
		return eCreDate;
	}
	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	
}
