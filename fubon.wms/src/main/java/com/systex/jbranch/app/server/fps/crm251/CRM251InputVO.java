package com.systex.jbranch.app.server.fps.crm251;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM251InputVO extends PagingInputVO{
	
	private String group_id;
	private String group_name;
	private String ao_code;
	private List<Map<String, Object>> chkId;
	
	private String uEmpID;
	
	public String getuEmpID() {
		return uEmpID;
	}
	
	public void setuEmpID(String uEmpID) {
		this.uEmpID = uEmpID;
	}
	
	public String getGroup_id() {
		return group_id;
	}
	
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	
	public String getGroup_name() {
		return group_name;
	}
	
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
	public String getAo_code() {
		return ao_code;
	}
	
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	
	public List<Map<String, Object>> getChkId() {
		return chkId;
	}
	
	public void setChkId(List<Map<String, Object>> chkId) {
		this.chkId = chkId;
	}
}
