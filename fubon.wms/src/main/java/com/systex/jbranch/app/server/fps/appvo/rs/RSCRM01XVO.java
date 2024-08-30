package com.systex.jbranch.app.server.fps.appvo.rs;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class RSCRM01XVO extends PagingInputVO{
	
	private String RPTTYPE;  //報表類型 1.客戶CIF增減報表 2.客戶CIF增減報表_淨增減明細 3.客戶AUM增減報表 4.客戶AUM增減報表_淨增減明細
	private Date DataDate; //資料統計日期
	private String OrgId; //組織代碼
	private String AoId; //理財專員之員工編號
	private String VipCodeI; //客戶等級
	private String EmpId; //操作者員工編號
	private List RSCRM011List; //名單執行明細報表資料List
	
	public String getRPTTYPE() {
		return RPTTYPE;
	}
	public void setRPTTYPE(String rPTTYPE) {
		RPTTYPE = rPTTYPE;
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
	public String getVipCodeI() {
		return VipCodeI;
	}
	public void setVipCodeI(String vipCodeI) {
		VipCodeI = vipCodeI;
	}
	public String getEmpId() {
		return EmpId;
	}
	public void setEmpId(String empId) {
		EmpId = empId;
	}
	public List getRSCRM011List() {
		return RSCRM011List;
	}
	public void setRSCRM011List(List rSCRM011List) {
		RSCRM011List = rSCRM011List;
	}
	
}
