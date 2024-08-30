package com.systex.jbranch.app.server.fps.fps330;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS330InputVO extends PagingInputVO{

	public FPS330InputVO(){	
	}
	private String PrdId;
	private String planID;
	private List<Map<String, Object>> paramList;
	public String getPrdId() {
		return PrdId;
	}
	public void setPrdId(String prdId) {
		PrdId = prdId;
	}
	public String getPlanID() {
		return planID;
	}
	public void setPlanID(String planID) {
		this.planID = planID;
	}
	public List<Map<String, Object>> getParamList() {
		return paramList;
	}
	public void setParamList(List<Map<String, Object>> paramList) {
		this.paramList = paramList;
	}
	
}
