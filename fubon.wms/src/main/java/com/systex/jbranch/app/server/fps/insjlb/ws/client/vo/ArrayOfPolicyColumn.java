package com.systex.jbranch.app.server.fps.insjlb.ws.client.vo;
public class ArrayOfPolicyColumn {
	private com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PolicyColumn[] PolicyColumnArray = {};
	public void setPolicyColumnArray (com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PolicyColumn[] PolicyColumnArray){
		this.PolicyColumnArray = PolicyColumnArray;
	}
	public com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PolicyColumn[] getPolicyColumnArray(){
		return this.PolicyColumnArray;
	}
}