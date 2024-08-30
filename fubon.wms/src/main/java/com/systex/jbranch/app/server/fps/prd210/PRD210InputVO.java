package com.systex.jbranch.app.server.fps.prd210;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD210InputVO extends PagingInputVO {
	private String prd_id;
	private String retired;
	private String education;
	private String purpose;
	private String life;
	private String accident;
	private String medical;
	private String diseases;
	private String is_inv;
	private BigDecimal base_amt_of_purchase;
	private String status;
	
	private String passParams;   //首頁接參數
	
	private String fileName;
	private String fileRealName;
	
	private List<Map<String, Object>> review_list;
	private List<Map<String, Object>> review_list2;
	private Date date;
	private Date date2;
	private String ins_type;
	private String target_id;
	private String stock_bond_type;

	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getRetired() {
		return retired;
	}
	public void setRetired(String retired) {
		this.retired = retired;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getLife() {
		return life;
	}
	public void setLife(String life) {
		this.life = life;
	}
	public String getAccident() {
		return accident;
	}
	public void setAccident(String accident) {
		this.accident = accident;
	}
	public String getMedical() {
		return medical;
	}
	public void setMedical(String medical) {
		this.medical = medical;
	}
	public String getDiseases() {
		return diseases;
	}
	public void setDiseases(String diseases) {
		this.diseases = diseases;
	}
	public String getIs_inv() {
		return is_inv;
	}
	public void setIs_inv(String is_inv) {
		this.is_inv = is_inv;
	}
	public BigDecimal getBase_amt_of_purchase() {
		return base_amt_of_purchase;
	}
	public void setBase_amt_of_purchase(BigDecimal base_amt_of_purchase) {
		this.base_amt_of_purchase = base_amt_of_purchase;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPassParams() {
		return passParams;
	}
	public void setPassParams(String passParams) {
		this.passParams = passParams;
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
	public List<Map<String, Object>> getReview_list2() {
		return review_list2;
	}
	public void setReview_list2(List<Map<String, Object>> review_list2) {
		this.review_list2 = review_list2;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getDate2() {
		return date2;
	}
	public void setDate2(Date date2) {
		this.date2 = date2;
	}
	public String getIns_type() {
		return ins_type;
	}
	public void setIns_type(String ins_type) {
		this.ins_type = ins_type;
	}
	public String getTarget_id() {
		return target_id;
	}
	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}
	public String getStock_bond_type() {
		return stock_bond_type;
	}
	public void setStock_bond_type(String stock_bond_type) {
		this.stock_bond_type = stock_bond_type;
	}
}