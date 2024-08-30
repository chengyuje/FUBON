package com.systex.jbranch.app.server.fps.ins260;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS260InputVO extends PagingInputVO {
	private Boolean ins200_from_ins132;
	private String planKeyno;
	private List<String> chkType;
	private String INS200_FamilyGap;
	private String INS200_Accident;
	private String INS200_Health;
	private String custId;
	

	public Boolean getIns200_from_ins132() {
		return ins200_from_ins132;
	}
	public void setIns200_from_ins132(Boolean ins200_from_ins132) {
		this.ins200_from_ins132 = ins200_from_ins132;
	}
	public String getPlanKeyno() {
		return planKeyno;
	}
	public void setPlanKeyno(String planKeyno) {
		this.planKeyno = planKeyno;
	}
	public List<String> getChkType() {
		return chkType;
	}
	public void setChkType(List<String> chkType) {
		this.chkType = chkType;
	}
	public String getINS200_FamilyGap() {
		return INS200_FamilyGap;
	}
	public void setINS200_FamilyGap(String iNS200_FamilyGap) {
		INS200_FamilyGap = iNS200_FamilyGap;
	}
	public String getINS200_Accident() {
		return INS200_Accident;
	}
	public void setINS200_Accident(String iNS200_Accident) {
		INS200_Accident = iNS200_Accident;
	}
	public String getINS200_Health() {
		return INS200_Health;
	}
	public void setINS200_Health(String iNS200_Health) {
		INS200_Health = iNS200_Health;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	
}