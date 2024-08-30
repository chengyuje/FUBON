package com.systex.jbranch.app.server.fps.pms301;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS301InputdtatilVO extends PagingInputVO {
	private Date sCreDate;   //資料統計日期
	private  String branch_nbr ;// 分行
	private  String srchType  ;// 當日、MTD
	private  String tx_ym  ;// MTD帶入月份
	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
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
	
	
	
	
	
	
	
	
}
