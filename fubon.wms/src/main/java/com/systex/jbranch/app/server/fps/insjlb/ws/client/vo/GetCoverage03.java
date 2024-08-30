package com.systex.jbranch.app.server.fps.insjlb.ws.client.vo;
public class GetCoverage03 {
	private com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfArrayOfPolicyDtlColumn PolicyDtl;
	private com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfPolicyColumn Policy;
	private com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfRelationColumn Relation;
	public void setPolicyDtl (com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfArrayOfPolicyDtlColumn PolicyDtl){
		this.PolicyDtl = PolicyDtl;
	}
	public com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfArrayOfPolicyDtlColumn getPolicyDtl(){
		return this.PolicyDtl;
	}
	public void setPolicy (com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfPolicyColumn Policy){
		this.Policy = Policy;
	}
	public com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfPolicyColumn getPolicy(){
		return this.Policy;
	}
	public void setRelation (com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfRelationColumn Relation){
		this.Relation = Relation;
	}
	public com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.ArrayOfRelationColumn getRelation(){
		return this.Relation;
	}
}