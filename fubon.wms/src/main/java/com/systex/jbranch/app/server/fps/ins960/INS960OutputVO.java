package com.systex.jbranch.app.server.fps.ins960;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS960OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> insOutputList;
	private List<Map<String, Object>> insDataOutputList;
	private List<Map<String, Object>> outputList;
//	private BigDecimal para_no;
//	private String cal_desc;
//	private String status;
//	private List suggestList;
//	private List reportList;
	public List<Map<String, Object>> getInsOutputList() {
		return insOutputList;
	}
	public void setInsOutputList(List<Map<String, Object>> insOutputList) {
		this.insOutputList = insOutputList;
	}
	public List<Map<String, Object>> getInsDataOutputList() {
		return insDataOutputList;
	}
	public void setInsDataOutputList(List<Map<String, Object>> insDataOutputList) {
		this.insDataOutputList = insDataOutputList;
	}
	public List<Map<String, Object>> getOutputList() {
		return outputList;
	}
	public void setOutputList(List<Map<String, Object>> outputList) {
		this.outputList = outputList;
	}
	

//	public BigDecimal getPara_no() {
//		return para_no;
//	}
//	public void setPara_no(BigDecimal para_no) {
//		this.para_no = para_no;
//	}
//	public String getCal_desc() {
//		return cal_desc;
//	}
//	public void setCal_desc(String cal_desc) {
//		this.cal_desc = cal_desc;
//	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
//	public List getSuggestList() {
//		return suggestList;
//	}
//	public void setSuggestList(List suggestList) {
//		this.suggestList = suggestList;
//	}
//	public List getReportList() {
//		return reportList;
//	}
//	public void setReportList(List reportList) {
//		this.reportList = reportList;
//	}

}