package com.systex.jbranch.app.server.fps.crm230;

import java.util.List;

import com.systex.jbranch.app.server.fps.crm231.CRM231InputVO;
import com.systex.jbranch.app.server.fps.crm2310.CRM2310InputVO;
import com.systex.jbranch.app.server.fps.crm2311.CRM2311InputVO;
import com.systex.jbranch.app.server.fps.crm232.CRM232InputVO;
import com.systex.jbranch.app.server.fps.crm233.CRM233InputVO;
import com.systex.jbranch.app.server.fps.crm234.CRM234InputVO;
import com.systex.jbranch.app.server.fps.crm235.CRM235InputVO;
import com.systex.jbranch.app.server.fps.crm236.CRM236InputVO;
import com.systex.jbranch.app.server.fps.crm237.CRM237InputVO;
import com.systex.jbranch.app.server.fps.crm238.CRM238InputVO;
import com.systex.jbranch.app.server.fps.crm239.CRM239InputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM230_ALLInputVO extends PagingInputVO{

	private CRM230InputVO crm230inputVO;
	private CRM231InputVO crm231inputVO; 
	private CRM232InputVO crm232inputVO; 
	private CRM233InputVO crm233inputVO; 
	private CRM234InputVO crm234inputVO; 
	private CRM235InputVO crm235inputVO; 
	private CRM236InputVO crm236inputVO; 
	private CRM237InputVO crm237inputVO; 
	private CRM238InputVO crm238inputVO; 
	private CRM239InputVO crm239inputVO; 
	private CRM2310InputVO crm2310inputVO;
	private CRM2311InputVO crm2311inputVO;
	
	private Object availRegionList;
	private Object availAreaList;
	private Object availBranchList;
	private Object loginEmpID;
	private Object loginRole;
	
	
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
	public void setLoginEmpID(List loginEmpID) {
		this.loginEmpID = loginEmpID;
	}
	public CRM230InputVO getCrm230inputVO() {
		return crm230inputVO;
	}
	public void setCrm230inputVO(CRM230InputVO crm230inputVO) {
		this.crm230inputVO = crm230inputVO;
	}
	public CRM231InputVO getCrm231inputVO() {
		return crm231inputVO;
	}
	public void setCrm231inputVO(CRM231InputVO crm231inputVO) {
		this.crm231inputVO = crm231inputVO;
	}
	public CRM232InputVO getCrm232inputVO() {
		return crm232inputVO;
	}
	public void setCrm232inputVO(CRM232InputVO crm232inputVO) {
		this.crm232inputVO = crm232inputVO;
	}
	public CRM233InputVO getCrm233inputVO() {
		return crm233inputVO;
	}
	public void setCrm233inputVO(CRM233InputVO crm233inputVO) {
		this.crm233inputVO = crm233inputVO;
	}
	public CRM234InputVO getCrm234inputVO() {
		return crm234inputVO;
	}
	public void setCrm234inputVO(CRM234InputVO crm234inputVO) {
		this.crm234inputVO = crm234inputVO;
	}
	public CRM235InputVO getCrm235inputVO() {
		return crm235inputVO;
	}
	public void setCrm235inputVO(CRM235InputVO crm235inputVO) {
		this.crm235inputVO = crm235inputVO;
	}
	public CRM236InputVO getCrm236inputVO() {
		return crm236inputVO;
	}
	public void setCrm236inputVO(CRM236InputVO crm236inputVO) {
		this.crm236inputVO = crm236inputVO;
	}
	public CRM237InputVO getCrm237inputVO() {
		return crm237inputVO;
	}
	public void setCrm237inputVO(CRM237InputVO crm237inputVO) {
		this.crm237inputVO = crm237inputVO;
	}
	public CRM238InputVO getCrm238inputVO() {
		return crm238inputVO;
	}
	public void setCrm238inputVO(CRM238InputVO crm238inputVO) {
		this.crm238inputVO = crm238inputVO;
	}
	public CRM239InputVO getCrm239inputVO() {
		return crm239inputVO;
	}
	public void setCrm239inputVO(CRM239InputVO crm239inputVO) {
		this.crm239inputVO = crm239inputVO;
	}
	public CRM2310InputVO getCrm2310inputVO() {
		return crm2310inputVO;
	}
	public void setCrm2310inputVO(CRM2310InputVO crm2310inputVO) {
		this.crm2310inputVO = crm2310inputVO;
	}
	public CRM2311InputVO getCrm2311inputVO() {
		return crm2311inputVO;
	}
	public void setCrm2311inputVO(CRM2311InputVO crm2311inputVO) {
		this.crm2311inputVO = crm2311inputVO;
	}
}
