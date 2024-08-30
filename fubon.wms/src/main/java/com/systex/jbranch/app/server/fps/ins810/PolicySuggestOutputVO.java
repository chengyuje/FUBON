package com.systex.jbranch.app.server.fps.ins810;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PolicySuggestOutputVO {

	// 商品建議
	private List<Map<String, Object>> suggestPrdList;
	
	// 費率
	private BigDecimal premRate;
	
	// 匯率
	private Map<String, BigDecimal> refExcRateMap;
	
	public PolicySuggestOutputVO(){
		
	}
	
	public PolicySuggestOutputVO(BigDecimal premRate, Map<String, BigDecimal> map) {
		this.premRate = premRate;
		this.refExcRateMap = map;
	}

	public List<Map<String, Object>> getSuggestPrdList() {
		return suggestPrdList;
	}

	public void setSuggestPrdList(List<Map<String, Object>> suggestPrdList) {
		this.suggestPrdList = suggestPrdList;
	}

	public BigDecimal getPremRate() {
		return premRate;
	}

	public void setPremRate(BigDecimal premRate) {
		this.premRate = premRate;
	}

	public Map<String, BigDecimal> getRefExcRateMap() {
		return refExcRateMap;
	}

	public void setRefExcRateMap(Map<String, BigDecimal> refExcRateMap) {
		this.refExcRateMap = refExcRateMap;
	}
	
	
	
	
}
