package com.systex.jbranch.app.server.fps.appvo.rs;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class RSCRM02XVO extends PagingInputVO{
	private String RPTTYPE;
	private String TypeDetail;
	private Date YM;
	private Date DataDate;
	private String OrgId;
	private String AoId;
	private String EmpId;
	private String BranchFlag;
	private List RSCRM022List;
	private List RSCRM021List;
	
	public List getRSCRM022List() {
	    return RSCRM022List;
	}
	public void setRSCRM022List(List rSCRM022List) {
	    RSCRM022List = rSCRM022List;
	}
	public List getRSCRM021List() {
	    return RSCRM021List;
	}
	public void setRSCRM021List(List rSCRM021List) {
	    RSCRM021List = rSCRM021List;
	}
	public String getRPTTYPE() {
		return RPTTYPE;
	}
	public String getBranchFlag() {
		return BranchFlag;
	}
	public void setBranchFlag(String branchFlag) {
		BranchFlag = branchFlag;
	}
	public void setRPTTYPE(String rPTTYPE) {
		RPTTYPE = rPTTYPE;
	}
	public String getTypeDetail() {
		return TypeDetail;
	}
	public void setTypeDetail(String typeDetail) {
		TypeDetail = typeDetail;
	}
	public String getOrgId() {
		return OrgId;
	}
	public void setOrgId(String orgId) {
		OrgId = orgId;
	}
	public String getAoId() {
		return AoId;
	}
	public void setAoId(String aoId) {
		AoId = aoId;
	}
	public String getEmpId() {
		return EmpId;
	}
	public void setEmpId(String empId) {
		EmpId = empId;
	}
	public Date getYM() {
	    return YM;
	}
	public void setYM(Date yM) {
	    YM = yM;
	}
	public Date getDataDate() {
	    return DataDate;
	}
	public void setDataDate(Date dataDate) {
	    DataDate = dataDate;
	}
	
	
}
