package com.systex.jbranch.app.server.fps.appvo.pii;

import java.util.List;
import java.io.Serializable;

public class FPAFP920OutputVO implements Serializable{
	public FPAFP920OutputVO(){
		super();
	}
	
	private List<AFP920RelationVO> AFP920RelationList;  //41類關聯列表

	public List<AFP920RelationVO> getAFP920RelationList() {
		return AFP920RelationList;
	}

	public void setAFP920RelationList(List<AFP920RelationVO> relationList) {
		AFP920RelationList = relationList;
	}
	
}
