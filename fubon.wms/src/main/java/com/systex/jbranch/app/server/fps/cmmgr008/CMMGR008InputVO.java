package com.systex.jbranch.app.server.fps.cmmgr008;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;


public class CMMGR008InputVO extends PagingInputVO{
	
	public CMMGR008InputVO()
	{
	}
	
	public Date dtfStartDate;
	public Date dtfEndDate;
	public String tipBrchId;
	public String tipWsId;
	public String tipTxnCode;
	public String tipTellerId;
	public String tipRoleId;
	public String tipCustomerId;
	public String tipCustomerName;
	public String tipBizcodeName;
	public String tipMemo;

	public Date getDtfStartDate() {
		return dtfStartDate;
	}
	public void setDtfStartDate(Date dtfStartDate) {
		this.dtfStartDate = dtfStartDate;
	}
	public Date getDtfEndDate() {
		return dtfEndDate;
	}
	public void setDtfEndDate(Date dtfEndDate) {
		this.dtfEndDate = dtfEndDate;
	}
	public String getTipBrchId() {
		return tipBrchId;
	}
	public void setTipBrchId(String tipBrchId) {
		this.tipBrchId = tipBrchId;
	}
	public String getTipWsId() {
		return tipWsId;
	}
	public void setTipWsId(String tipWsId) {
		this.tipWsId = tipWsId;
	}
	public String getTipTxnCode() {
		return tipTxnCode;
	}
	public void setTipTxnCode(String tipTxnCode) {
		this.tipTxnCode = tipTxnCode;
	}
	public String getTipTellerId() {
		return tipTellerId;
	}
	public void setTipTellerId(String tipTellerId) {
		this.tipTellerId = tipTellerId;
	}
	public String getTipRoleId() {
		return tipRoleId;
	}
	public void setTipRoleId(String tipRoleId) {
		this.tipRoleId = tipRoleId;
	}
	public String getTipCustomerId() {
		return tipCustomerId;
	}
	public void setTipCustomerId(String tipCustomerId) {
		this.tipCustomerId = tipCustomerId;
	}
	public String getTipCustomerName() {
		return tipCustomerName;
	}
	public void setTipCustomerName(String tipCustomerName) {
		this.tipCustomerName = tipCustomerName;
	}
	public String getTipBizcodeName() {
		return tipBizcodeName;
	}
	public void setTipBizcodeName(String tipBizcodeName) {
		this.tipBizcodeName = tipBizcodeName;
	}
	public String getTipMemo() {
		return tipMemo;
	}
	public void setTipMemo(String tipMemo) {
		this.tipMemo = tipMemo;
	}
}
