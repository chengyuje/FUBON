package com.systex.jbranch.app.server.fps.fps400;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS400InputVO extends PagingInputVO {

	// table
	private String custID;
	private String riskType;
	private String planID;
	private String SEQNO;
	private String action;
	private String flagType;
	private String flagYN;
	private Date sDate;
	private Date eDate;

	public FPS400InputVO() {
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getPlanID() {
		return planID;
	}

	public void setPlanID(String planID) {
		this.planID = planID;
	}

	public String getSEQNO() {
		return SEQNO;
	}

	public void setSEQNO(String sEQNO) {
		SEQNO = sEQNO;
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public String getFlagType() {
		return flagType;
	}

	public void setFlagType(String flagType) {
		this.flagType = flagType;
	}

	public String getFlagYN() {
		return flagYN;
	}

	public void setFlagYN(String flagYN) {
		this.flagYN = flagYN;
	}

	public Date getsDate() {
		return sDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

}
