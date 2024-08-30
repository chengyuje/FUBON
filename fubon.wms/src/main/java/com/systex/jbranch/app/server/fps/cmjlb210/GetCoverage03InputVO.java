package com.systex.jbranch.app.server.fps.cmjlb210;

import java.util.List;


/**
 * Method getCoverage03 之 inputVO
 */
public class GetCoverage03InputVO {

	private List lstPolicyDetail;	// 保單明細資訊
	private List lstPolicyMaster;	// 保單主檔
	private List lstRelation;

	public List getLstPolicyDetail() {
		return lstPolicyDetail;
	}
	public void setLstPolicyDetail(List lstPolicyDetail) {
		this.lstPolicyDetail = lstPolicyDetail;
	}
	public List getLstPolicyMaster() {
		return lstPolicyMaster;
	}
	public void setLstPolicyMaster(List lstPolicyMaster) {
		this.lstPolicyMaster = lstPolicyMaster;
	}
	public List getLstRelation() {
		return lstRelation;
	}
	public void setLstRelation(List lstRelation) {
		this.lstRelation = lstRelation;
	}
}
