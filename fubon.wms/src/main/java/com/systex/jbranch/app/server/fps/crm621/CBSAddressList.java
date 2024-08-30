package com.systex.jbranch.app.server.fps.crm621;

public class CBSAddressList {
	private String ZIP_COD;
	private String DATA;
	private String MEMO;

	public CBSAddressList() {
		this.ZIP_COD = "";
		this.DATA = "";
		this.MEMO = "";
	}

	public String getMEMO() {
		return MEMO;
	}

	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}

	public String getZIP_COD() {
		return ZIP_COD;
	}

	public void setZIP_COD(String zIP_COD) {
		ZIP_COD = zIP_COD;
	}

	public String getDATA() {
		return DATA;
	}

	public void setDATA(String dATA) {
		DATA = dATA;
	}

}
