package com.systex.jbranch.app.server.fps.appvo.rs;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class RSCAM02XVO extends PagingInputVO{
	
	private String RPTTYPE;  //報表類型 1.名單執行明細報表 2.未分派明細 3.無效名單明細 4.無意願明細
	private String OrgId; //組織代碼 
	private String AoId; //理財專員之員工編號
	private String EmpId; //操作者員工編號
	private String BranchFlag; //分行查詢註記
	private String CamId; //行銷活動ID
	public String getRPTTYPE() {
		return RPTTYPE;
	}
	public void setRPTTYPE(String rPTTYPE) {
		RPTTYPE = rPTTYPE;
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
	public String getBranchFlag() {
		return BranchFlag;
	}
	public void setBranchFlag(String branchFlag) {
		BranchFlag = branchFlag;
	}
	public String getCamId() {
		return CamId;
	}
	public void setCamId(String camId) {
		CamId = camId;
	}
	
	
	
}
