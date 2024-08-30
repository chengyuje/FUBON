package com.systex.jbranch.app.server.fps.ins100;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS100InputVO extends PagingInputVO {
	private String custId;				//被保人
	
	private String cust_name;
	private String add_custId;
	private String age;
	private String gender;
	private String click;
	
	private String roleSelect;			//角色  1: 本人  2: 關係戶
	private String idSelect;			//對象  1: 要保人  2: 被保人
	private String fileName;
	private String realfileName;
	
	private String policyNbr;			//保單號碼
	private boolean isFilter; 			// 是否過濾
	
	private List<Map<String, Object>> resultList;
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	public String getRoleSelect() {
		return roleSelect;
	}
	public void setRoleSelect(String roleSelect) {
		this.roleSelect = roleSelect;
	}
	
	public String getIdSelect() {
		return idSelect;
	}
	public void setIdSelect(String idSelect) {
		this.idSelect = idSelect;
	}
	
	public String getPolicyNbr() {
		return policyNbr;
	}
	public void setPolicyNbr(String policyNbr) {
		this.policyNbr = policyNbr;
	}
	public boolean isFilter() {
		return isFilter;
	}
	public void setFilter(boolean isFilter) {
		this.isFilter = isFilter;
	}
	
	@Override
	public String toString() {
		return "INS100InputVO [custId=" + custId + ", roleSelect=" + roleSelect
				+ ", idSelect=" + idSelect + ", policyNbr=" + policyNbr + "]";
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public String getCust_name() {
		return cust_name;
	}
	public String getAdd_custId() {
		return add_custId;
	}
	public String getAge() {
		return age;
	}
	public String getGender() {
		return gender;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public void setAdd_custId(String add_custId) {
		this.add_custId = add_custId;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getClick() {
		return click;
	}
	public void setClick(String click) {
		this.click = click;
	}
	public String getRealfileName() {
		return realfileName;
	}
	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}		

}
