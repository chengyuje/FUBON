package com.systex.jbranch.app.server.fps.prd270;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD270InputVO extends PagingInputVO {
	private String prd_id;
	private String isin_code;
	private String rating_sp;
	private String rating_moddy;
	private String rating_fitch;
	private String sn_rating_sp;
	private String sn_rating_moddy;
	private String sn_rating_fitch;
	private Date buy_Date;
	private String fix_Date;
	private String exchange;
	private String dividend;
	private String target;
	private String cnr_yield;
	private String rate_channel;
	private String performance_review;
	private String status;

	private String ptype;

	private String fileName;
	private String fileRealName;

	private List<Map<String, Object>> review_list;
	private String stock_bond_type;
	private String snProject;
	private String snCustLevel;

	private BigDecimal bond_value;

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

	public String getSn_rating_sp() {
		return sn_rating_sp;
	}

	public void setSn_rating_sp(String sn_rating_sp) {
		this.sn_rating_sp = sn_rating_sp;
	}

	public String getSn_rating_moddy() {
		return sn_rating_moddy;
	}

	public void setSn_rating_moddy(String sn_rating_moddy) {
		this.sn_rating_moddy = sn_rating_moddy;
	}

	public String getSn_rating_fitch() {
		return sn_rating_fitch;
	}

	public void setSn_rating_fitch(String sn_rating_fitch) {
		this.sn_rating_fitch = sn_rating_fitch;
	}

	public Date getBuy_Date() {
		return buy_Date;
	}

	public void setBuy_Date(Date buy_Date) {
		this.buy_Date = buy_Date;
	}

	public String getFix_Date() {
		return fix_Date;
	}

	public void setFix_Date(String fix_Date) {
		this.fix_Date = fix_Date;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getDividend() {
		return dividend;
	}

	public void setDividend(String dividend) {
		this.dividend = dividend;
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

	public String getRate_channel() {
		return rate_channel;
	}

	public void setRate_channel(String rate_channel) {
		this.rate_channel = rate_channel;
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

	public String getSnProject() {
		return snProject;
	}

	public void setSnProject(String snProject) {
		this.snProject = snProject;
	}

	public String getSnCustLevel() {
		return snCustLevel;
	}

	public void setSnCustLevel(String snCustLevel) {
		this.snCustLevel = snCustLevel;
	}

	public BigDecimal getBond_value() {
		return bond_value;
	}

	public void setBond_value(BigDecimal bond_value) {
		this.bond_value = bond_value;
	}

}
