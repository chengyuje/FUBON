package com.systex.jbranch.app.server.fps.insjlb.ws.client.vo;
public class CoverageDataSet {
	private com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageTable[] CoverageTableArray = {};
	private com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverAgePrem[] CoverAgePremArray = {};
	private com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Expression[] ExpressionArray = {};
	public void setCoverageTableArray (com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageTable[] CoverageTableArray){
		this.CoverageTableArray = CoverageTableArray;
	}
	public com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageTable[] getCoverageTableArray(){
		return this.CoverageTableArray;
	}
	public void setCoverAgePremArray (com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverAgePrem[] CoverAgePremArray){
		this.CoverAgePremArray = CoverAgePremArray;
	}
	public com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverAgePrem[] getCoverAgePremArray(){
		return this.CoverAgePremArray;
	}
	public void setExpressionArray (com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Expression[] ExpressionArray){
		this.ExpressionArray = ExpressionArray;
	}
	public com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Expression[] getExpressionArray(){
		return this.ExpressionArray;
	}
}