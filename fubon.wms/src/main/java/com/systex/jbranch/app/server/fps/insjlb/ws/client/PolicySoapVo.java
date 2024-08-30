package com.systex.jbranch.app.server.fps.insjlb.ws.client;

import com.systex.jbranch.commons.soap.SoapVo;

public class PolicySoapVo<T> extends SoapVo{
	private PolicySourceConf policySourceConf;
	private T resultObj;

	public PolicySourceConf getPolicySourceConf() {
		return policySourceConf;
	}
	public void setPolicySourceConf(PolicySourceConf policySourceConf) {
		this.policySourceConf = policySourceConf;
	}
	public T getResultObj() {
		return resultObj;
	}
	public void setResultObj(T resultObj) {
		this.resultObj = resultObj;
	}
	
}
