package com.systex.jbranch.app.server.fps.ins450;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS450OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> outputList;
	private List<Map<String, Object>> outputList1;
	private List paramList;
	private BigDecimal policyFEE;

	public List<Map<String, Object>> getOutputList() {
		return outputList;
	}

	public void setOutputList(List<Map<String, Object>> outputList) {
		this.outputList = outputList;
	}

	public List<Map<String, Object>> getOutputList1() {
		return outputList1;
	}

	public void setOutputList1(List<Map<String, Object>> outputList1) {
		this.outputList1 = outputList1;
	}

	public List getParamList() {
		return paramList;
	}

	public void setParamList(List paramList) {
		this.paramList = paramList;
	}

	public BigDecimal getPolicyFEE() {
		return policyFEE;
	}

	public void setPolicyFEE(BigDecimal policyFEE) {
		this.policyFEE = policyFEE;
	}
	
}
