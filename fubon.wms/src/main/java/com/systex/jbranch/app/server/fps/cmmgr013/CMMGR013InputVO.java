package com.systex.jbranch.app.server.fps.cmmgr013;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CMMGR013InputVO extends PagingInputVO {
	
	private String cmbBRCHID;
	private String empID;
	private String cmbRoleID;
	private int radioChange;

	public String getCmbBRCHID() {
		return cmbBRCHID;
	}
	public void setCmbBRCHID(String cmbBRCHID) {
		this.cmbBRCHID = cmbBRCHID;
	}
	public String getEmpID() {
		return empID;
	}
	public void setEmpID(String empID) {
		this.empID = empID;
	}
	public String getCmbRoleID() {
		return cmbRoleID;
	}
	public void setCmbRoleID(String cmbRoleID) {
		this.cmbRoleID = cmbRoleID;
	}
	public int getRadioChange() {
		return radioChange;
	}
	public void setRadioChange(int radioChange) {
		this.radioChange = radioChange;
	}
	
}
