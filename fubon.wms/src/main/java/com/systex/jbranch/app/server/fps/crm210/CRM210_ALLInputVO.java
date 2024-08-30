package com.systex.jbranch.app.server.fps.crm210;

import com.systex.jbranch.app.server.fps.crm211.CRM211InputVO;
import com.systex.jbranch.app.server.fps.crm221.CRM221InputVO;
import com.systex.jbranch.app.server.fps.crm331.CRM331InputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM210_ALLInputVO extends PagingInputVO {
	private CRM210InputVO crm210inputVO;
	private CRM211InputVO crm211inputVO; 
	private CRM221InputVO crm221inputVO;
	private CRM331InputVO crm331inputVO;
	
	private Object availRegionList;
	private Object availAreaList;
	private Object availBranchList;
	private Object loginEmpID;
	private Object loginRole;
	
	
	public CRM210InputVO getCrm210inputVO() {
		return crm210inputVO;
	}
	public void setCrm210inputVO(CRM210InputVO crm210inputVO) {
		this.crm210inputVO = crm210inputVO;
	}
	public CRM211InputVO getCrm211inputVO() {
		return crm211inputVO;
	}
	public void setCrm211inputVO(CRM211InputVO crm211inputVO) {
		this.crm211inputVO = crm211inputVO;
	}
	public CRM221InputVO getCrm221inputVO() {
		return crm221inputVO;
	}
	public void setCrm221inputVO(CRM221InputVO crm221inputVO) {
		this.crm221inputVO = crm221inputVO;
	}
	public CRM331InputVO getCrm331inputVO() {
		return crm331inputVO;
	}
	public void setCrm331inputVO(CRM331InputVO crm331inputVO) {
		this.crm331inputVO = crm331inputVO;
	}
	public Object getAvailRegionList() {
		return availRegionList;
	}
	public void setAvailRegionList(Object availRegionList) {
		this.availRegionList = availRegionList;
	}
	public Object getAvailAreaList() {
		return availAreaList;
	}
	public void setAvailAreaList(Object availAreaList) {
		this.availAreaList = availAreaList;
	}
	public Object getAvailBranchList() {
		return availBranchList;
	}
	public void setAvailBranchList(Object availBranchList) {
		this.availBranchList = availBranchList;
	}
	public Object getLoginEmpID() {
		return loginEmpID;
	}
	public void setLoginEmpID(Object loginEmpID) {
		this.loginEmpID = loginEmpID;
	}
	public Object getLoginRole() {
		return loginRole;
	}
	public void setLoginRole(Object loginRole) {
		this.loginRole = loginRole;
	}
	
}