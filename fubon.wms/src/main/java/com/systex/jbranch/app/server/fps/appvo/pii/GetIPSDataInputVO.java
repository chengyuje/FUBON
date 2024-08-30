package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class GetIPSDataInputVO implements Serializable {

	private String ipsNo;//IPS編號

	public String getIpsNo() {
		return ipsNo;
	}

	public void setIpsNo(String ipsNo) {
		this.ipsNo = ipsNo;
	}
}
