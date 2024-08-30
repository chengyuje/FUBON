package com.systex.jbranch.app.server.fps.fps400;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FPS400OutputVO extends PagingOutputVO {
	public FPS400OutputVO() {
	}
	
	private int AchiveRate;
	private List<Map<String, Object>> outputList;
	//add by Brian
	private String base64;
	private String message;
	private String planDate;
	
	private String preBusiDay;		//前一營業日
	private int targetYear;			//目標年期

	public void setOutputList(List<Map<String, Object>> outputList) {
		this.outputList = outputList;
	}

	public List<Map<String, Object>> getOutputList() {
		return outputList;
	}

	public int getAchiveRate() {
		return AchiveRate;
	}

	public void setAchiveRate(int achiveRate) {
		AchiveRate = achiveRate;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPlanDate() {
		return planDate;
	}

	public void setPlanDate(String planDate) {
		this.planDate = planDate;
	}

	public String getPreBusiDay() {
		return preBusiDay;
	}

	public void setPreBusiDay(String preBusiDay) {
		this.preBusiDay = preBusiDay;
	}

	public int getTargetYear() {
		return targetYear;
	}

	public void setTargetYear(int targetYear) {
		this.targetYear = targetYear;
	}
	
}
