package com.systex.jbranch.app.server.fps.fps320;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
import java.util.List;
import java.util.Map;

public class FPS320OutputVO extends PagingOutputVO {
	public FPS320OutputVO(){
		
	}
	
	private List<Map<String,Object>> outputList;
	private Map<String,Object> outputMap;

	public void setOutputList(List<Map<String, Object>> outputList) {
		this.outputList = outputList;
	}
	
	public List<Map<String, Object>> getOutputList() {
		return outputList;
	}

	public Map<String, Object> getOutputMap() {
		return outputMap;
	}

	public void setOutputMap(Map<String, Object> outputMap) {
		this.outputMap = outputMap;
	}

}
