package com.systex.jbranch.app.server.fps.crm681;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.util.List;

public class CRM681InputVO extends PagingInputVO{
	private String cust_id;
	private String obuFlag;

	/** 客戶帳戶明細 **/
	private List<CBSUtilOutputVO> acctData;
	
	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}


	public List<CBSUtilOutputVO> getAcctData() {
		return acctData;
	}

	public void setAcctData(List<CBSUtilOutputVO> acctData) {
		this.acctData = acctData;
	}

	public String getObuFlag() {
		return obuFlag;
	}

	public void setObuFlag(String obuFlag) {
		this.obuFlag = obuFlag;
	}
}
