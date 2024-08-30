package com.systex.jbranch.app.server.fps.crm810;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM810OutputVO extends PagingOutputVO {

	private BigDecimal gd_amt;
	private List<String> no_cur_list;
	private Map<String, BigDecimal> cur_list;
	private BigDecimal crm811_amt;
	private BigDecimal crm812_amt;
	private BigDecimal crm813_amt;
	
	


	
	
	
	public BigDecimal getCrm811_amt() {
		return crm811_amt;
	}
	public void setCrm811_amt(BigDecimal crm811_amt) {
		this.crm811_amt = crm811_amt;
	}
	public BigDecimal getCrm812_amt() {
		return crm812_amt;
	}
	public void setCrm812_amt(BigDecimal crm812_amt) {
		this.crm812_amt = crm812_amt;
	}
	public BigDecimal getCrm813_amt() {
		return crm813_amt;
	}
	public void setCrm813_amt(BigDecimal crm813_amt) {
		this.crm813_amt = crm813_amt;
	}
	public List<String> getNo_cur_list() {
		return no_cur_list;
	}
	public void setNo_cur_list(List<String> no_cur_list) {
		this.no_cur_list = no_cur_list;
	}
	public Map<String, BigDecimal> getCur_list() {
		return cur_list;
	}
	public void setCur_list(Map<String, BigDecimal> cur_list) {
		this.cur_list = cur_list;
	}
	public BigDecimal getGd_amt() {
		return gd_amt;
	}
	public void setGd_amt(BigDecimal gd_amt) {
		this.gd_amt = gd_amt;
	}
	
}
