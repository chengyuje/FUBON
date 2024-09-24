package com.systex.jbranch.app.server.fps.eln100;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class ELN100InputVO extends PagingInputVO {
	private Date req_time_s;
	private Date req_time_e;
	private String link_prod_1;
	private String link_prod_2;
	private String link_prod_3;
	private String currency;
	private int duration;
	private String ko_type;
	private String ki_type;

	public Date getReq_time_s() {
		return req_time_s;
	}

	public void setReq_time_s(Date req_time_s) {
		this.req_time_s = req_time_s;
	}

	public Date getReq_time_e() {
		return req_time_e;
	}

	public void setReq_time_e(Date req_time_e) {
		this.req_time_e = req_time_e;
	}

	public String getLink_prod_1() {
		return link_prod_1;
	}

	public void setLink_prod_1(String link_prod_1) {
		this.link_prod_1 = link_prod_1;
	}

	public String getLink_prod_2() {
		return link_prod_2;
	}

	public void setLink_prod_2(String link_prod_2) {
		this.link_prod_2 = link_prod_2;
	}

	public String getLink_prod_3() {
		return link_prod_3;
	}

	public void setLink_prod_3(String link_prod_3) {
		this.link_prod_3 = link_prod_3;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getKo_type() {
		return ko_type;
	}

	public void setKo_type(String ko_type) {
		this.ko_type = ko_type;
	}

	public String getKi_type() {
		return ki_type;
	}

	public void setKi_type(String ki_type) {
		this.ki_type = ki_type;
	}
	
}
