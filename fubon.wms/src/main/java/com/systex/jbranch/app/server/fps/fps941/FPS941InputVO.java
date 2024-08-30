package com.systex.jbranch.app.server.fps.fps941;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS941InputVO extends PagingInputVO {
	private String param_no;
	private Date date;
	private List<Map<String, Object>> manualfList;
	private List<Map<String, Object>> manualmList;
	private List<Map<String, Object>> warningList;
	private String pictureName;
	private String realpictureName;
	private String exipicture;
	private String status;
	
	
	public String getParam_no() {
		return param_no;
	}
	public void setParam_no(String param_no) {
		this.param_no = param_no;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<Map<String, Object>> getManualfList() {
		return manualfList;
	}
	public void setManualfList(List<Map<String, Object>> manualfList) {
		this.manualfList = manualfList;
	}
	public List<Map<String, Object>> getManualmList() {
		return manualmList;
	}
	public void setManualmList(List<Map<String, Object>> manualmList) {
		this.manualmList = manualmList;
	}
	public List<Map<String, Object>> getWarningList() {
		return warningList;
	}
	public void setWarningList(List<Map<String, Object>> warningList) {
		this.warningList = warningList;
	}
	public String getPictureName() {
		return pictureName;
	}
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}
	public String getRealpictureName() {
		return realpictureName;
	}
	public void setRealpictureName(String realpictureName) {
		this.realpictureName = realpictureName;
	}
	public String getExipicture() {
		return exipicture;
	}
	public void setExipicture(String exipicture) {
		this.exipicture = exipicture;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}