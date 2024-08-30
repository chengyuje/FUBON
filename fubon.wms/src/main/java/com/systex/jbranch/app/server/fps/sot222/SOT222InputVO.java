package com.systex.jbranch.app.server.fps.sot222;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT222InputVO extends PagingInputVO {
	
	private String custID;
	private String prodID;
	private Boolean isOBU;

	public String getProdID() {
		return prodID;
	}

	public void setProdID(String prodID) {
		this.prodID = prodID;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public Boolean getIsOBU() {
		return isOBU;
	}

	public void setIsOBU(Boolean isOBU) {
		this.isOBU = isOBU;
	}

}
