package com.systex.jbranch.app.server.fps.prd250;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD250InputVO extends PagingInputVO {
	private String prd_id;
	private String isin_code;
	private String bond_pri;
	private String rating_sp;
	private String rating_moddy;
	private String rating_fitch;
	private String bond_rating_sp;
	private String bond_rating_moddy;
	private String bond_rating_fitch;
	private String buyback;
	private String checklist;
	private String yield;
	private String multiple;
	private Date multi_sDate;
	private Date multi_eDate;
	private String status;

	private String ptype;

	private String fileName;
	private String fileRealName;

	private List<Map<String, Object>> review_list;
	private Date date;
	private String stock_bond_type;

	private String bondProject;
	private String bondCustLevel;

	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getIsin_code() {
		return isin_code;
	}
	public void setIsin_code(String isin_code) {
		this.isin_code = isin_code;
	}
	public String getBond_pri() {
		return bond_pri;
	}
	public void setBond_pri(String bond_pri) {
		this.bond_pri = bond_pri;
	}
	public String getRating_sp() {
		return rating_sp;
	}
	public void setRating_sp(String rating_sp) {
		this.rating_sp = rating_sp;
	}
	public String getRating_moddy() {
		return rating_moddy;
	}
	public void setRating_moddy(String rating_moddy) {
		this.rating_moddy = rating_moddy;
	}
	public String getRating_fitch() {
		return rating_fitch;
	}
	public void setRating_fitch(String rating_fitch) {
		this.rating_fitch = rating_fitch;
	}
	public String getBond_rating_sp() {
		return bond_rating_sp;
	}
	public void setBond_rating_sp(String bond_rating_sp) {
		this.bond_rating_sp = bond_rating_sp;
	}
	public String getBond_rating_moddy() {
		return bond_rating_moddy;
	}
	public void setBond_rating_moddy(String bond_rating_moddy) {
		this.bond_rating_moddy = bond_rating_moddy;
	}
	public String getBond_rating_fitch() {
		return bond_rating_fitch;
	}
	public void setBond_rating_fitch(String bond_rating_fitch) {
		this.bond_rating_fitch = bond_rating_fitch;
	}
	public String getBuyback() {
		return buyback;
	}
	public void setBuyback(String buyback) {
		this.buyback = buyback;
	}
	public String getChecklist() {
		return checklist;
	}
	public void setChecklist(String checklist) {
		this.checklist = checklist;
	}
	public String getYield() {
		return yield;
	}
	public void setYield(String yield) {
		this.yield = yield;
	}
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String multiple) {
		this.multiple = multiple;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getStock_bond_type() {
		return stock_bond_type;
	}
	public void setStock_bond_type(String stock_bond_type) {
		this.stock_bond_type = stock_bond_type;
	}

	public String getBondProject() {
		return bondProject;
	}

	public void setBondProject(String bondProject) {
		this.bondProject = bondProject;
	}

	public String getBondCustLevel() {
		return bondCustLevel;
	}

	public void setBondCustLevel(String bondCustLevel) {
		this.bondCustLevel = bondCustLevel;
	}
}
