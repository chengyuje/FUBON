package com.systex.jbranch.app.server.fps.org110;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG110InputVO extends PagingInputVO {

	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
	private String EMP_ID;
	private String EMP_NAME;
	private String JOB_TITLE_NAME;
	private String AO_CODE;
	private String PHOTO_FLAG;
	private Date UNAUTH_DATE;
	private Date REAUTH_DATE;
	
	public String getRegion_center_id() {
		return region_center_id;
	}
	
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	
	public String getBranch_area_id() {
		return branch_area_id;
	}
	
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	
	public String getBranch_nbr() {
		return branch_nbr;
	}
	
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	
	public String getEMP_ID() {
		return EMP_ID;
	}
	
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	
	public String getEMP_NAME() {
		return EMP_NAME;
	}
	
	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}
	
	public String getJOB_TITLE_NAME() {
		return JOB_TITLE_NAME;
	}
	
	public void setJOB_TITLE_NAME(String jOB_TITLE_NAME) {
		JOB_TITLE_NAME = jOB_TITLE_NAME;
	}
	
	public String getAO_CODE() {
		return AO_CODE;
	}
	
	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}
	
	public String getPHOTO_FLAG() {
		return PHOTO_FLAG;
	}
	public void setPHOTO_FLAG(String pHOTO_FLAG) {
		PHOTO_FLAG = pHOTO_FLAG;
	}

	public Date getUNAUTH_DATE() {
		return UNAUTH_DATE;
	}

	public void setUNAUTH_DATE(Date uNAUTH_DATE) {
		UNAUTH_DATE = uNAUTH_DATE;
	}

	public Date getREAUTH_DATE() {
		return REAUTH_DATE;
	}

	public void setREAUTH_DATE(Date rEAUTH_DATE) {
		REAUTH_DATE = rEAUTH_DATE;
	}
	
}
