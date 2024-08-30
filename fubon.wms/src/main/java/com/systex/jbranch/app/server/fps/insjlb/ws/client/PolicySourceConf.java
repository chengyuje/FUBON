package com.systex.jbranch.app.server.fps.insjlb.ws.client;

public enum PolicySourceConf {
	GET_COVERAGE01("GetCoverage01"),
	GET_COVERAGE03("GetCoverage03"),
	GET_EXPRESSION_KIND ("GetExpressionKIND"),
	GET_EXPRESSION_TABLE_NET("GetExpressionTable_NET"),
	GET_HTML_CLAUSE_BINARY("GetHtmlClauseBinary");

	private String serviceName;
	
	PolicySourceConf(String serviceName){
		this.serviceName = serviceName;
	}
	
	public String getName(){
		return serviceName;
	}
}
