package com.systex.jbranch.app.server.fps.cam160;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM160InputVO extends PagingInputVO{
	
	private String campID;
	private Date importSDate;
	private Date importEDate;
	private String campName;
	private Date sDate;
	private Date sDate2;
	private Date eDate;
	private Date eDate2;
	
	private String type; // 刪除類型
	private String stepID;
	private String rvReason;
	
	private String his_campID;
	private String his_campName;
	private String his_stepID;
	private String his_modifier;
	private Date his_sDate;
	private Date his_sDate2;
	private Date his_eDate;
	private Date his_eDate2;
	private Date his_delSDate;
	private Date his_delEDate;

	public Date getsDate2() {
		return sDate2;
	}

	public void setsDate2(Date sDate2) {
		this.sDate2 = sDate2;
	}

	public String getHis_modifier() {
		return his_modifier;
	}

	public void setHis_modifier(String his_modifier) {
		this.his_modifier = his_modifier;
	}

	public String getHis_stepID() {
		return his_stepID;
	}

	public void setHis_stepID(String his_stepID) {
		this.his_stepID = his_stepID;
	}

	public String getHis_campID() {
		return his_campID;
	}

	public void setHis_campID(String his_campID) {
		this.his_campID = his_campID;
	}

	public String getHis_campName() {
		return his_campName;
	}

	public void setHis_campName(String his_campName) {
		this.his_campName = his_campName;
	}

	public Date getHis_sDate2() {
		return his_sDate2;
	}

	public void setHis_sDate2(Date his_sDate2) {
		this.his_sDate2 = his_sDate2;
	}

	public Date getHis_sDate() {
		return his_sDate;
	}

	public void setHis_sDate(Date his_sDate) {
		this.his_sDate = his_sDate;
	}

	public Date getHis_eDate2() {
		return his_eDate2;
	}

	public void setHis_eDate2(Date his_eDate2) {
		this.his_eDate2 = his_eDate2;
	}

	public Date getHis_eDate() {
		return his_eDate;
	}

	public void setHis_eDate(Date his_eDate) {
		this.his_eDate = his_eDate;
	}

	public Date getHis_delSDate() {
		return his_delSDate;
	}

	public void setHis_delSDate(Date his_delSDate) {
		this.his_delSDate = his_delSDate;
	}

	public Date getHis_delEDate() {
		return his_delEDate;
	}

	public void setHis_delEDate(Date his_delEDate) {
		this.his_delEDate = his_delEDate;
	}

	public String getRvReason() {
		return rvReason;
	}

	public void setRvReason(String rvReason) {
		this.rvReason = rvReason;
	}

	public String getStepID() {
		return stepID;
	}

	public void setStepID(String stepID) {
		this.stepID = stepID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getImportSDate() {
		return importSDate;
	}

	public void setImportSDate(Date importSDate) {
		this.importSDate = importSDate;
	}

	public Date getImportEDate() {
		return importEDate;
	}

	public void setImportEDate(Date importEDate) {
		this.importEDate = importEDate;
	}

	public String getCampName() {
		return campName;
	}

	public void setCampName(String campName) {
		this.campName = campName;
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

	public Date geteDate2() {
		return eDate2;
	}

	public void seteDate2(Date eDate2) {
		this.eDate2 = eDate2;
	}

	public String getCampID() {
		return campID;
	}

	public void setCampID(String campID) {
		this.campID = campID;
	}
	
	
}
