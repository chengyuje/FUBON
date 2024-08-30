package com.systex.jbranch.app.server.fps.kyc311;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC311OutputVO extends PagingOutputVO{
	
	private boolean checkCOM_Experience;
	private Map<String, Object> QNineAnsChange;
	private Map<String, Map<String, Object>> Comparison;
	private String Q3ProdDecrease;	//Q3投資商品上次有本次沒有
	private List<Map<String, Object>> Q3ProdExpChgList;	//Q3投資年期上升2級以上


	public Map<String, Object> getQNineAnsChange() {
		return QNineAnsChange;
	}

	public Map<String, Map<String, Object>> getComparison() {
		return Comparison;
	}

	public void setQNineAnsChange(Map<String, Object> qNineAnsChange) {
		QNineAnsChange = qNineAnsChange;
	}

	public void setComparison(Map<String, Map<String, Object>> comparison) {
		Comparison = comparison;
	}

	public boolean isCheckCOM_Experience() {
		return checkCOM_Experience;
	}

	public void setCheckCOM_Experience(boolean checkCOM_Experience) {
		this.checkCOM_Experience = checkCOM_Experience;
	}

	public String getQ3ProdDecrease() {
		return Q3ProdDecrease;
	}

	public void setQ3ProdDecrease(String q3ProdDecrease) {
		Q3ProdDecrease = q3ProdDecrease;
	}

	public List<Map<String, Object>> getQ3ProdExpChgList() {
		return Q3ProdExpChgList;
	}

	public void setQ3ProdExpChgList(List<Map<String, Object>> q3ProdExpChgList) {
		Q3ProdExpChgList = q3ProdExpChgList;
	}

	
}
