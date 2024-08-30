package com.systex.jbranch.app.server.fps.ins410;



import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS410InputVO extends PagingInputVO{
	private String cust_id;			// 客戶ID	
	private String plan_status;		// 狀態
	private Date plan_sDate;		// 期間(起)
	private Date plan_eDate;		// 期間(迄)
	private String sppID;			// 投組編號
	private String KEYNO;
	private String sppType;
	private String sppName;

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getPlan_status() {
		return plan_status;
	}

	public void setPlan_status(String plan_status) {
		this.plan_status = plan_status;
	}

	public Date getPlan_sDate() {
		return plan_sDate;
	}

	public void setPlan_sDate(Date plan_sDate) {
		this.plan_sDate = plan_sDate;
	}

	public Date getPlan_eDate() {
		return plan_eDate;
	}

	public void setPlan_eDate(Date plan_eDate) {
		this.plan_eDate = plan_eDate;
	}

	public String getSppID() {
		return sppID;
	}

	public void setSppID(String sppID) {
		this.sppID = sppID;
	}

	public String getKEYNO() {
		return KEYNO;
	}

	public void setKEYNO(String kEYNO) {
		KEYNO = kEYNO;
	}

	public String getSppType() {
		return sppType;
	}

	public void setSppType(String sppType) {
		this.sppType = sppType;
	}

	public String getSppName() {
		return sppName;
	}

	public void setSppName(String sppName) {
		this.sppName = sppName;
	}
	
}
