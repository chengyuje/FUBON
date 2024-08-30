package com.systex.jbranch.app.server.fps.fps410;

import java.util.List;

import com.systex.jbranch.app.server.fps.fps400.FPS400InputVO;

public class FPS410InputVO extends FPS400InputVO{

	private String prdID;
	private String mfmktcat;
	private Boolean commRsYn;

	// table
	private String sppType;
	private String planName;

	private String RISK_ATTR;
	private String VOL_RISK_ATTR;
	private String PLAN_STATUS;
	private int INV_PERIOD;
	private int INV_AMT_ONETIME;
	private int INV_AMT_PER_MONTH;
	private int INV_AMT_TARGET;
	private String VALID_FLAG;
	private String TRACE_FLAG;

	private List<FPS410PrdInputVO> prdList;

	public FPS410InputVO() {
	}

	public String getSppType() {
		return sppType;
	}

	public void setSppType(String sppType) {
		this.sppType = sppType;
	}

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}

	public String getMfmktcat() {
		return mfmktcat;
	}

	public void setMfmktcat(String mfmktcat) {
		this.mfmktcat = mfmktcat;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}
	
	public String getRISK_ATTR() {
		return RISK_ATTR;
	}

	public void setRISK_ATTR(String rISK_ATTR) {
		RISK_ATTR = rISK_ATTR;
	}

	public String getVOL_RISK_ATTR() {
		return VOL_RISK_ATTR;
	}

	public void setVOL_RISK_ATTR(String vOL_RISK_ATTR) {
		VOL_RISK_ATTR = vOL_RISK_ATTR;
	}

	public String getPLAN_STATUS() {
		return PLAN_STATUS;
	}

	public void setPLAN_STATUS(String pLAN_STATUS) {
		PLAN_STATUS = pLAN_STATUS;
	}

	public int getINV_PERIOD() {
		return INV_PERIOD;
	}

	public void setINV_PERIOD(int iNV_PERIOD) {
		INV_PERIOD = iNV_PERIOD;
	}

	public int getINV_AMT_ONETIME() {
		return INV_AMT_ONETIME;
	}

	public void setINV_AMT_ONETIME(int iNV_AMT_ONETIME) {
		INV_AMT_ONETIME = iNV_AMT_ONETIME;
	}

	public int getINV_AMT_PER_MONTH() {
		return INV_AMT_PER_MONTH;
	}

	public void setINV_AMT_PER_MONTH(int iNV_AMT_PER_MONTH) {
		INV_AMT_PER_MONTH = iNV_AMT_PER_MONTH;
	}

	public int getINV_AMT_TARGET() {
		return INV_AMT_TARGET;
	}

	public void setINV_AMT_TARGET(int iNV_AMT_TARGET) {
		INV_AMT_TARGET = iNV_AMT_TARGET;
	}

	public String getVALID_FLAG() {
		return VALID_FLAG;
	}

	public void setVALID_FLAG(String vALID_FLAG) {
		VALID_FLAG = vALID_FLAG;
	}

	public String getTRACE_FLAG() {
		return TRACE_FLAG;
	}

	public void setTRACE_FLAG(String tRACE_FLAG) {
		TRACE_FLAG = tRACE_FLAG;
	}

	public List<FPS410PrdInputVO> getPrdList() {
		return prdList;
	}

	public void setPrdList(List<FPS410PrdInputVO> prdList) {
		this.prdList = prdList;
	}

	public Boolean getCommRsYn() {
		return commRsYn;
	}

	public void setCommRsYn(Boolean commRsYn) {
		this.commRsYn = commRsYn;
	}
	
}
