package com.systex.jbranch.app.server.fps.appvo.rs;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class RSPMS02XVO extends PagingInputVO{
	
	private String RPTTYPE;  //報表類型 1.名單執行明細報表 2.未分派明細 3.無效名單明細 4.無意願明細
	private String DataDate; //組織代碼 
	private String OrgId; //理財專員之員工編號
	private String AoId; //操作者員工編號
	private String EmpId; //分行查詢註記
	private List RSPMS021List; //報表List
	public String getRPTTYPE() {
		return RPTTYPE;
	}
	public void setRPTTYPE(String rPTTYPE) {
		RPTTYPE = rPTTYPE;
	}
	public String getDataDate() {
		return DataDate;
	}
	public void setDataDate(String dataDate) {
		DataDate = dataDate;
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
	public List getRSPMS021List() {
		return RSPMS021List;
	}
	public void setRSPMS021List(List rSPMS021List) {
		RSPMS021List = rSPMS021List;
	}

}
