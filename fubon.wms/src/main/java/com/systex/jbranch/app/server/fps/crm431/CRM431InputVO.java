package com.systex.jbranch.app.server.fps.crm431;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM431InputVO extends PagingInputVO {
	
	private String cust_id;              						 //客戶ID
	private List<String> statusList;			  				 //授權狀態
	private Date auth_date_bgn;        							 //授權日期(起)
	private Date auth_date_end;        							 //授權日期(迄)
	private String apply_seq;									 //議價編號
	private String apply_cat;                                    //議價種類
	private String auth_status;                                  //授權狀態
	private String apply_status;								 //申請狀態
	private String comments;                                     //簽核意見
	private String highest_auth_lv; 							 //最高授權層級
	private List<Map<String,Object>> acceptList;                 //全選
	private Boolean fromMPlus;									 //判斷是否由M+覆核
	
	private String actionType;
	private String con_degree;
	private String prod_type;
	
	private String empID;
	
	private List<Map<String, Object>> reviewList;
	
	public List<Map<String, Object>> getReviewList() {
		return reviewList;
	}

	public void setReviewList(List<Map<String, Object>> reviewList) {
		this.reviewList = reviewList;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getProd_type() {
		return prod_type;
	}

	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
	}

	public String getCon_degree() {
		return con_degree;
	}

	public void setCon_degree(String con_degree) {
		this.con_degree = con_degree;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getCust_id() {
		return cust_id;
	}
	
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
	public List<String> getStatusList() {
		return statusList;
	}
	
	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}
	
	public Date getAuth_date_bgn() {
		return auth_date_bgn;
	}
	
	public void setAuth_date_bgn(Date auth_date_bgn) {
		this.auth_date_bgn = auth_date_bgn;
	}
	
	public Date getAuth_date_end() {
		return auth_date_end;
	}
	
	public void setAuth_date_end(Date auth_date_end) {
		this.auth_date_end = auth_date_end;
	}
	
	public String getApply_seq() {
		return apply_seq;
	}
	
	public void setApply_seq(String apply_seq) {
		this.apply_seq = apply_seq;
	}
	
	public String getApply_cat() {
		return apply_cat;
	}
	
	public void setApply_cat(String apply_cat) {
		this.apply_cat = apply_cat;
	}
	
	public String getAuth_status() {
		return auth_status;
	}
	
	public void setAuth_status(String auth_status) {
		this.auth_status = auth_status;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getHighest_auth_lv() {
		return highest_auth_lv;
	}
	
	public void setHighest_auth_lv(String highest_auth_lv) {
		this.highest_auth_lv = highest_auth_lv;
	}
	
	public List<Map<String, Object>> getAcceptList() {
		return acceptList;
	}
	
	public void setAcceptList(List<Map<String, Object>> acceptList) {
		this.acceptList = acceptList;
	}
	
	public String getApply_status() {
		return apply_status;
	}
	
	public void setApply_status(String apply_status) {
		this.apply_status = apply_status;
	}

	public Boolean getFromMPlus() {
		return fromMPlus;
	}

	public void setFromMPlus(Boolean fromMPlus) {
		this.fromMPlus = fromMPlus;
	}
	
}
