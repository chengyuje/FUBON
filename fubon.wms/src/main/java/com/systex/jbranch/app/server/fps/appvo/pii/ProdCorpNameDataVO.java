package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class ProdCorpNameDataVO implements Serializable {

	private String restraintCorp;	//限制公司代碼     @xiaoxichen
	private String corpName;	//基金公司名稱
	
	public String getRestraintCorp() {
		return restraintCorp;
	}
	public void setRestraintCorp(String restraintCorp) {
		this.restraintCorp = restraintCorp;
	}
	public String getCorpName() {
		return corpName;
	}
	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}	
}
