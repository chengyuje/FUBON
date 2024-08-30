package com.systex.jbranch.app.server.fps.crm513;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM513InputVO extends PagingInputVO {

	private String custID;
	private String branchNbr;
	private Date sDate;
	private Date eDate;
	
	private List<Map<String, String>> tradeList;
	
	public List<Map<String, String>> getTradeList() {
		return tradeList;
	}

	public void setTradeList(List<Map<String, String>> tradeList) {
		this.tradeList = tradeList;
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

}
