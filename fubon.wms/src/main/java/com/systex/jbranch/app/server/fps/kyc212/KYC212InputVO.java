package com.systex.jbranch.app.server.fps.kyc212;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC212InputVO extends PagingInputVO{
	
	private String EXAM_VERSION;
	private String QUESTION_VERSION;
	private List<Map<String,Object>> Weights;
	
	
	public List<Map<String, Object>> getWeights() {
		return Weights;
	}
	public void setWeights(List<Map<String, Object>> weights) {
		Weights = weights;
	}
	public String getEXAM_VERSION() {
		return EXAM_VERSION;
	}
	public void setEXAM_VERSION(String eXAM_VERSION) {
		EXAM_VERSION = eXAM_VERSION;
	}
	public String getQUESTION_VERSION() {
		return QUESTION_VERSION;
	}
	public void setQUESTION_VERSION(String qUESTION_VERSION) {
		QUESTION_VERSION = qUESTION_VERSION;
	}
	
	

}
