package com.systex.jbranch.app.server.fps.prd251;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD251InputVO extends PagingInputVO {
	private Date last_sDate;
	private Date last_eDate;
	private String prd_id;
	private String cname;
	private String cust_id;
	private BigDecimal limit_price;
	private BigDecimal channel_fee;
	private String status;
	
	private List<Map<String, Object>> review_list;
	
	
	public Date getLast_sDate() {
		return last_sDate;
	}
	public void setLast_sDate(Date last_sDate) {
		this.last_sDate = last_sDate;
	}
	public Date getLast_eDate() {
		return last_eDate;
	}
	public void setLast_eDate(Date last_eDate) {
		this.last_eDate = last_eDate;
	}
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public BigDecimal getLimit_price() {
		return limit_price;
	}
	public void setLimit_price(BigDecimal limit_price) {
		this.limit_price = limit_price;
	}
	public BigDecimal getChannel_fee() {
		return channel_fee;
	}
	public void setChannel_fee(BigDecimal channel_fee) {
		this.channel_fee = channel_fee;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Map<String, Object>> getReview_list() {
		return review_list;
	}
	public void setReview_list(List<Map<String, Object>> review_list) {
		this.review_list = review_list;
	}
}