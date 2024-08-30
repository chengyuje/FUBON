package com.systex.jbranch.app.server.fps.appvo.rs;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class RSPMS01XVO extends PagingInputVO{
	
	private String RPTTYPE;  //報表類型 1.分行統計 2.FC明細 3.OP直接銷售明細
	private String Trend; //趨勢圖 Y:需要, N:不需要
	private Date DataDate; //理財專員之員工編號
	private String OrgId; //組織代碼
	private String AoId; //理財專員之員工編號
	private String EmpId; //操作者員工編號
	private List RSPMS012List; //報表List
	public String getRPTTYPE() {
		return RPTTYPE;
	}
	public void setRPTTYPE(String rPTTYPE) {
		RPTTYPE = rPTTYPE;
	}
	public String getTrend() {
		return Trend;
	}
	public void setTrend(String trend) {
		Trend = trend;
	}
	public Date getDataDate() {
		return DataDate;
	}
	public void setDataDate(Date dataDate) {
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
	public List getRSPMS012List() {
		return RSPMS012List;
	}
	public void setRSPMS012List(List rSPMS012List) {
		RSPMS012List = rSPMS012List;
	}

}
