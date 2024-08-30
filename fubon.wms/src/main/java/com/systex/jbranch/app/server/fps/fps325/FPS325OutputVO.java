package com.systex.jbranch.app.server.fps.fps325;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
import java.util.List;
import java.util.Map;

public class FPS325OutputVO extends PagingOutputVO {
	public FPS325OutputVO(){
	}
	
	private List<Map<String,Object>> outputList;

	private Double maxPfoRtn;

	private Double minPfoRtn;

	private String periodStartDate;

	private String periodEndDate;

	private List<Map<String,Object>> efList;

	private List<Map<String,Object>> cmlList;

  public List<Map<String, Object>> getOutputList() {
		return outputList;
	}

	public void setOutputList(List<Map<String, Object>> outputList) {
		this.outputList = outputList;
	}

	public Double getMaxPfoRtn() {
		return maxPfoRtn;
	}

	public void setMaxPfoRtn(Double maxPfoRtn) {
		this.maxPfoRtn = maxPfoRtn;
	}

	public Double getMinPfoRtn() {
		return minPfoRtn;
	}

	public void setMinPfoRtn(Double minPfoRtn) {
		this.minPfoRtn = minPfoRtn;
	}

	public String getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(String periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public String getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(String periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public List<Map<String, Object>> getEfList() {
		return efList;
	}

	public void setEfList(List<Map<String, Object>> efList) {
		this.efList = efList;
	}

	public List<Map<String, Object>> getCmlList() {
		return cmlList;
	}

	public void setCmlList(List<Map<String, Object>> cmlList) {
		this.cmlList = cmlList;
	}
}
