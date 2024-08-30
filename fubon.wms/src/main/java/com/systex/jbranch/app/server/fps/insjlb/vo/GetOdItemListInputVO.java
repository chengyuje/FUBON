package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;
import java.util.Map;

public class GetOdItemListInputVO {
	/**保險規劃缺口類型：<br>
	 * ---- L:壽險缺口 <br>
	 * ---- P:意外險缺口 <br> 
	 * ---- H:醫療住院卻口 <br>
	 * ---- C:癌症住院缺口 <br>
	 * ---- D:重大疾病缺口 <br>
	 * ---- W:長期看護缺口 ,<br>
	 * ---- R:生存金 , 可放入多個，使用逗號區隔 
	 */
	private String planTypes;
	/**客戶ID*/
	private String custId;
	/**登陸者分行*/
	private String loginBranch;
	/**登陸者的ao_code*/
	private List<String> loginAOCode;
	/**保單資料*/
	private List<Map> lstInsData;
	
	// 職等
	private String jobGrade;
	
	public String getPlanTypes() {
		return planTypes;
	}
	public void setPlanTypes(String planTypes) {
		this.planTypes = planTypes;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public List<Map> getLstInsData() {
		return lstInsData;
	}
	public void setLstInsData(List<Map> lstInsData) {
		this.lstInsData = lstInsData;
	}
	public String getLoginBranch() {
		return loginBranch;
	}
	public void setLoginBranch(String loginBranch) {
		this.loginBranch = loginBranch;
	}
	public List<String> getLoginAOCode() {
		return loginAOCode;
	}
	public void setLoginAOCode(List<String> loginAOCode) {
		this.loginAOCode = loginAOCode;
	}
	public String getJobGrade() {
		return jobGrade;
	}
	public void setJobGrade(String jobGrade) {
		this.jobGrade = jobGrade;
	}
	
}
