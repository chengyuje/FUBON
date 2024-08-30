package com.systex.jbranch.app.server.fps.prd280;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD280InputVO extends PagingInputVO {
	private String prd_id;
	private Date buy_Date;
	private String fixed;
	private String fix_Date;
	private String floating;
	private String exchange;
	private String target;
	private String cnr_yield;
	private String rate_return;
	private String performance_review;
	private String status;
	
	private String ptype;
	
	private String fileName;
	private String fileRealName;
	
	private String prdType;
	private List<Map<String, Object>> review_list;
	private String stock_bond_type;
	
	private String si_project;
	private String si_customer_level;
	
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public Date getBuy_Date() {
		return buy_Date;
	}
	public void setBuy_Date(Date buy_Date) {
		this.buy_Date = buy_Date;
	}
	public String getFixed() {
		return fixed;
	}
	public void setFixed(String fixed) {
		this.fixed = fixed;
	}
	public String getFix_Date() {
		return fix_Date;
	}
	public void setFix_Date(String fix_Date) {
		this.fix_Date = fix_Date;
	}
	public String getFloating() {
		return floating;
	}
	public void setFloating(String floating) {
		this.floating = floating;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getCnr_yield() {
		return cnr_yield;
	}
	public void setCnr_yield(String cnr_yield) {
		this.cnr_yield = cnr_yield;
	}
	public String getRate_return() {
		return rate_return;
	}
	public void setRate_return(String rate_return) {
		this.rate_return = rate_return;
	}
	public String getPerformance_review() {
		return performance_review;
	}
	public void setPerformance_review(String performance_review) {
		this.performance_review = performance_review;
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
	public String getPrdType() {
		return prdType;
	}
	public void setPrdType(String prdType) {
		this.prdType = prdType;
	}
	public List<Map<String, Object>> getReview_list() {
		return review_list;
	}
	public void setReview_list(List<Map<String, Object>> review_list) {
		this.review_list = review_list;
	}
	public String getStock_bond_type() {
		return stock_bond_type;
	}
	public void setStock_bond_type(String stock_bond_type) {
		this.stock_bond_type = stock_bond_type;
	}
	public String getSi_project() {
		return si_project;
	}
	public void setSi_project(String si_project) {
		this.si_project = si_project;
	}
	public String getSi_customer_level() {
		return si_customer_level;
	}
	public void setSi_customer_level(String si_customer_level) {
		this.si_customer_level = si_customer_level;
	}
	
}