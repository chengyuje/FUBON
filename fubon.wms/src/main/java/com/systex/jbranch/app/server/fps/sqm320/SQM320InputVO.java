package com.systex.jbranch.app.server.fps.sqm320;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM320InputVO extends PagingInputVO {

	/***** 查詢條件專用START *****/
	private String yearQtr; //年季
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String cust_id;
	private String ao_code;
	private String sp_cust_yn; //特定客戶註記
	private String audited; //已訪查註記
	private String reivew_flag; //已覆核註記

	private String review_status; //訪查狀態
	private String cust_status; //身份別
	/***** 查詢條件專用END *****/

	private String ownerFlag;
	private String audit_type;
	private String save_type; //暫存、儲存

	private List<Map<String, Object>> dtlList;
	private List<Map<String, Object>> reviewList;
	private List<Map<String, Object>> deleteList;
	private String up_kyc_yn;

	private List<Map<String, Object>> reviewerList;

	private String memLoginFlag;
	private String delType;

	private String fileID;
	private String fileName;
	private String fileRealName;
	
	private String uhrmRC;
	private String uhrmOP;
	
	public String getUhrmRC() {
		return uhrmRC;
	}

	public void setUhrmRC(String uhrmRC) {
		this.uhrmRC = uhrmRC;
	}

	public String getUhrmOP() {
		return uhrmOP;
	}

	public void setUhrmOP(String uhrmOP) {
		this.uhrmOP = uhrmOP;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
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

	public String getDelType() {
		return delType;
	}

	public void setDelType(String delType) {
		this.delType = delType;
	}

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public List<Map<String, Object>> getReviewerList() {
		return reviewerList;
	}

	public void setReviewerList(List<Map<String, Object>> reviewerList) {
		this.reviewerList = reviewerList;
	}

	public String getUp_kyc_yn() {
		return up_kyc_yn;
	}

	public void setUp_kyc_yn(String up_kyc_yn) {
		this.up_kyc_yn = up_kyc_yn;
	}

	public String getReview_status() {
		return review_status;
	}

	public void setReview_status(String review_status) {
		this.review_status = review_status;
	}

	public String getCust_status() {
		return cust_status;
	}

	public void setCust_status(String cust_status) {
		this.cust_status = cust_status;
	}

	public String getYearQtr() {
		return yearQtr;
	}

	public void setYearQtr(String yearQtr) {
		this.yearQtr = yearQtr;
	}

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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getSp_cust_yn() {
		return sp_cust_yn;
	}

	public void setSp_cust_yn(String sp_cust_yn) {
		this.sp_cust_yn = sp_cust_yn;
	}

	public String getAudited() {
		return audited;
	}

	public void setAudited(String audited) {
		this.audited = audited;
	}

	public String getOwnerFlag() {
		return ownerFlag;
	}

	public void setOwnerFlag(String ownerFlag) {
		this.ownerFlag = ownerFlag;
	}

	public String getAudit_type() {
		return audit_type;
	}

	public void setAudit_type(String audit_type) {
		this.audit_type = audit_type;
	}

	public List<Map<String, Object>> getDtlList() {
		return dtlList;
	}

	public void setDtlList(List<Map<String, Object>> dtlList) {
		this.dtlList = dtlList;
	}

	public List<Map<String, Object>> getReviewList() {
		return reviewList;
	}

	public void setReviewList(List<Map<String, Object>> reviewList) {
		this.reviewList = reviewList;
	}

	public List<Map<String, Object>> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(List<Map<String, Object>> deleteList) {
		this.deleteList = deleteList;
	}

	public String getReivew_flag() {
		return reivew_flag;
	}

	public void setReivew_flag(String reivew_flag) {
		this.reivew_flag = reivew_flag;
	}

	public String getSave_type() {
		return save_type;
	}

	public void setSave_type(String save_type) {
		this.save_type = save_type;
	}

}
