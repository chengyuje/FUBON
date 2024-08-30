package com.systex.jbranch.app.server.fps.prd111;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD111OutputVO extends PagingOutputVO {
	private String warningMsg;
	private String errorMsg;
	private List<Map<String, Object>> prodDTL;
	private String fitnessMessage;						//以逗號分隔錯誤碼
	
	
	public String getWarningMsg() {
		return warningMsg;
	}
	public void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List<Map<String, Object>> getProdDTL() {
		return prodDTL;
	}
	public void setProdDTL(List<Map<String, Object>> prodDTL) {
		this.prodDTL = prodDTL;
	}
	public String getFitnessMessage() {
		return fitnessMessage;
	}
	public void setFitnessMessage(String fitnessMessage) {
		this.fitnessMessage = fitnessMessage;
	}	
	
}
