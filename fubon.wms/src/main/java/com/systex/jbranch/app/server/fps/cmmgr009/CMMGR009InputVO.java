package com.systex.jbranch.app.server.fps.cmmgr009;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;


public class CMMGR009InputVO extends PagingInputVO{
	
	public CMMGR009InputVO()
	{
	}
	
	public String tipBranchId;
	public String tipWorkStationId;
	public String tipTeller;
	public String tipParamType;
	public String tipPtypeName;
	public String tipParamCode;
	public String tipParamName;
	public Date dtfEffectDate;
	public Date dtfLastUpdate;

	public String getTipBranchId() {
		return tipBranchId;
	}
	public void setTipBranchId(String tipBranchId) {
		this.tipBranchId = tipBranchId;
	}
	public String getTipWorkStationId() {
		return tipWorkStationId;
	}
	public void setTipWorkStationId(String tipWorkStationId) {
		this.tipWorkStationId = tipWorkStationId;
	}
	public String getTipTeller() {
		return tipTeller;
	}
	public void setTipTeller(String tipTeller) {
		this.tipTeller = tipTeller;
	}
	public String getTipParamType() {
		return tipParamType;
	}
	public void setTipParamType(String tipParamType) {
		this.tipParamType = tipParamType;
	}
	public String getTipPtypeName() {
		return tipPtypeName;
	}
	public void setTipPtypeName(String tipPtypeName) {
		this.tipPtypeName = tipPtypeName;
	}
	public String getTipParamCode() {
		return tipParamCode;
	}
	public void setTipParamCode(String tipParamCode) {
		this.tipParamCode = tipParamCode;
	}
	public String getTipParamName() {
		return tipParamName;
	}
	public void setTipParamName(String tipParamName) {
		this.tipParamName = tipParamName;
	}
	public Date getDtfEffectDate() {
		return dtfEffectDate;
	}
	public void setDtfEffectDate(Date dtfEffectDate) {
		this.dtfEffectDate = dtfEffectDate;
	}
	public Date getDtfLastUpdate() {
		return dtfLastUpdate;
	}
	public void setDtfLastUpdate(Date dtfLastUpdate) {
		this.dtfLastUpdate = dtfLastUpdate;
	}
}
