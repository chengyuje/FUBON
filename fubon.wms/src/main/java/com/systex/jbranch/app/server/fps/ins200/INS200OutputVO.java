package com.systex.jbranch.app.server.fps.ins200;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS200OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> diseaseList;
	private List<Map<String, Object>> ltcareList;
	private List outputList;
	private int Age;
	private Boolean vaildcustid;
	private Boolean vaildparameter;
	private BigDecimal para_no;
	private BigDecimal type3_para_no;
	private String cal_desc;
	
	
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getDiseaseList() {
		return diseaseList;
	}
	public void setDiseaseList(List<Map<String, Object>> diseaseList) {
		this.diseaseList = diseaseList;
	}
	public List<Map<String, Object>> getLtcareList() {
		return ltcareList;
	}
	public void setLtcareList(List<Map<String, Object>> ltcareList) {
		this.ltcareList = ltcareList;
	}
	public List getOutputList() {
		return outputList;
	}
	public void setOutputList(List outputList) {
		this.outputList = outputList;
	}
	public int getAge() {
		return Age;
	}
	public void setAge(int age) {
		Age = age;
	}
	public Boolean getVaildcustid() {
		return vaildcustid;
	}
	public void setVaildcustid(Boolean vaildcustid) {
		this.vaildcustid = vaildcustid;
	}
	public Boolean getVaildparameter() {
		return vaildparameter;
	}
	public void setVaildparameter(Boolean vaildparameter) {
		this.vaildparameter = vaildparameter;
	}
	public BigDecimal getPara_no() {
		return para_no;
	}
	public void setPara_no(BigDecimal para_no) {
		this.para_no = para_no;
	}
	public BigDecimal getType3_para_no() {
		return type3_para_no;
	}
	public void setType3_para_no(BigDecimal type3_para_no) {
		this.type3_para_no = type3_para_no;
	}
	public String getCal_desc() {
		return cal_desc;
	}
	public void setCal_desc(String cal_desc) {
		this.cal_desc = cal_desc;
	}
}