package com.systex.jbranch.app.server.fps.pms302;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS302InputdetailVO extends PagingInputVO{
	private Date sCreDate;   //資料統計日期
	private  String ao_code ;// 分行
	private  String srchType  ;// 當日、MTD
	private  String tx_ym  ;// MTD帶入月份
	private  String branch_nbr  ;// MTD帶入月份
	private String cust_id; // 客戶ID

	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getSrchType() {
		return srchType;
	}
	public void setSrchType(String srchType) {
		this.srchType = srchType;
	}
	public String getTx_ym() {
		return tx_ym;
	}
	public void setTx_ym(String tx_ym) {
		this.tx_ym = tx_ym;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getCust_id() { return cust_id; }
	public void setCust_id(String cust_id) { this.cust_id = cust_id; }
}
