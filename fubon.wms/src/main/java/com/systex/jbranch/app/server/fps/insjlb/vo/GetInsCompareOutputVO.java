package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;
import java.util.Map;

public class GetInsCompareOutputVO {
	private List<Map> lstExpressionCombind;
	private String printType;
	
	public List<Map> getLstExpressionCombind() {
		return lstExpressionCombind;
	}
	public void setLstExpressionCombind(List<Map> lstExpressionCombind) {
		this.lstExpressionCombind = lstExpressionCombind;
	}
	public String getPrintType() {
		return printType;
	}
	public void setPrintType(String printType) {
		this.printType = printType;
	}
}
