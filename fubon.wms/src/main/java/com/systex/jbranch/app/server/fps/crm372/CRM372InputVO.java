package com.systex.jbranch.app.server.fps.crm372;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM372InputVO extends PagingInputVO {

	private String prj_code; 	// 專案代碼
	private String prd_name; 	// 專案名稱
	private String prj_status; 	// 專案目前進度
	private Date startDate; 	// 起始日期
	private Date endDate; 		// 結束日期
	private List<Map<String,Object>> csvDataList;	//csv清單
	
	private Date startDate_s;	//起始日期(起)
	private Date startDate_e;	//起始日期(迄)
	private Date endDate_s;		//結束日期(起)
	private Date endDate_e;		//結束日期(迄)
	private Date createDate_s;	//建立日期(起)
	private Date createDate_e;	//建立日期(迄)
	private String emp_id;		//理專ID
	private String chg_type;	//類別 換手 :C	輪調 :P
	private String creator;     //建立人
	
	
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getStartDate_s() {
		return startDate_s;
	}
	public void setStartDate_s(Date startDate_s) {
		this.startDate_s = startDate_s;
	}
	public Date getStartDate_e() {
		return startDate_e;
	}
	public void setStartDate_e(Date startDate_e) {
		this.startDate_e = startDate_e;
	}
	public Date getEndDate_s() {
		return endDate_s;
	}
	public void setEndDate_s(Date endDate_s) {
		this.endDate_s = endDate_s;
	}
	public Date getEndDate_e() {
		return endDate_e;
	}
	public void setEndDate_e(Date endDate_e) {
		this.endDate_e = endDate_e;
	}
	public Date getCreateDate_s() {
		return createDate_s;
	}
	public void setCreateDate_s(Date createDate_s) {
		this.createDate_s = createDate_s;
	}
	public Date getCreateDate_e() {
		return createDate_e;
	}
	public void setCreateDate_e(Date createDate_e) {
		this.createDate_e = createDate_e;
	}
	private String fileName;
	private String fileRealName;
	public String getPrj_code() {
		return prj_code;
	}
	public void setPrj_code(String prj_code) {
		this.prj_code = prj_code;
	}
	public String getPrd_name() {
		return prd_name;
	}
	public void setPrd_name(String prd_name) {
		this.prd_name = prd_name;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	public List<Map<String,Object>> getCsvDataList() {
		return csvDataList;
	}
	public void setCsvDataList(List<Map<String,Object>> csvDataList) {
		this.csvDataList = csvDataList;
	}
	public String getPrj_status() {
		return prj_status;
	}
	public void setPrj_status(String prj_status) {
		this.prj_status = prj_status;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getChg_type() {
		return chg_type;
	}
	public void setChg_type(String chg_type) {
		this.chg_type = chg_type;
	}
	
}
