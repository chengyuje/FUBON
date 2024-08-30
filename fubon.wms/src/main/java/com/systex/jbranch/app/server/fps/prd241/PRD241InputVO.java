package com.systex.jbranch.app.server.fps.prd241;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD241InputVO extends PagingInputVO {
	private String prd_id;
	private String yield;
	private String cnr_mult;
	private Date multi_sDate;
	private Date multi_eDate;
	private String status;
	
	private String ptype;
	
	private String fileName;
	private String fileRealName;
	
	private List<Map<String, Object>> review_list;
	
	private String passParams;   //首頁接參數
	
	
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getYield() {
		return yield;
	}
	public void setYield(String yield) {
		this.yield = yield;
	}
	public String getCnr_mult() {
		return cnr_mult;
	}
	public void setCnr_mult(String cnr_mult) {
		this.cnr_mult = cnr_mult;
	}
	public Date getMulti_sDate() {
		return multi_sDate;
	}
	public void setMulti_sDate(Date multi_sDate) {
		this.multi_sDate = multi_sDate;
	}
	public Date getMulti_eDate() {
		return multi_eDate;
	}
	public void setMulti_eDate(Date multi_eDate) {
		this.multi_eDate = multi_eDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
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
	public List<Map<String, Object>> getReview_list() {
		return review_list;
	}
	public void setReview_list(List<Map<String, Object>> review_list) {
		this.review_list = review_list;
	}
	public String getPassParams() {
		return passParams;
	}
	public void setPassParams(String passParams) {
		this.passParams = passParams;
	}
}