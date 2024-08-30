package com.systex.jbranch.app.server.fps.ins400;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS400OutputVO extends PagingOutputVO {
	public INS400OutputVO() {
	}
	private List resultList;

	public List getResultList() {
		return resultList;
	}
	private List<Map<String,Object>> outputList;

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getOutputList() {
		return outputList;
	}

	public void setOutputList(List<Map<String, Object>> outputList) {
		this.outputList = outputList;
	}
	
}
