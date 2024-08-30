package com.systex.jbranch.app.server.fps.sot813;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT813InputVO extends PagingInputVO{
	private String tradeSeq; //下單交易序號
	
	//fitFlag case1下單:false、 case2 適配 :true
	private boolean fitFlag = Boolean.FALSE;
	private String cust_id;
	
	
	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}

	public boolean isFitFlag() {
		return fitFlag;
	}

	public void setFitFlag(boolean fitFlag) {
		this.fitFlag = fitFlag;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
}
