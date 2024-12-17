package com.systex.jbranch.app.server.fps.eln100;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ELN100InputVO extends PagingInputVO {
	private Date query_date_s;
	private Date query_date_e;
	private String bbg_code;
	private String bbg_code1;
	private String bbg_code2;
	private String bbg_code3;
	private String bbg_code4;
	private String bbg_code5;
	private String currency;
	private Integer tenor;
	private String ko_type;
	private String ki_type;
	private Integer type1;
	private Integer type2;
	private String rate1;
	private String rate2;
	private String rate3;
	private String rate4;
	private Map<String, Object> map;

	public Date getQuery_date_s() {
		return query_date_s;
	}

	public void setQuery_date_s(Date query_date_s) {
		this.query_date_s = query_date_s;
	}

	public Date getQuery_date_e() {
		return query_date_e;
	}

	public void setQuery_date_e(Date query_date_e) {
		this.query_date_e = query_date_e;
	}

	public String getBbg_code() {
		return bbg_code;
	}

	public void setBbg_code(String bbg_code) {
		this.bbg_code = bbg_code;
	}

	public String getBbg_code1() {
		return bbg_code1;
	}

	public void setBbg_code1(String bbg_code1) {
		this.bbg_code1 = bbg_code1;
	}

	public String getBbg_code2() {
		return bbg_code2;
	}

	public void setBbg_code2(String bbg_code2) {
		this.bbg_code2 = bbg_code2;
	}

	public String getBbg_code3() {
		return bbg_code3;
	}

	public void setBbg_code3(String bbg_code3) {
		this.bbg_code3 = bbg_code3;
	}

	public String getBbg_code4() {
		return bbg_code4;
	}

	public void setBbg_code4(String bbg_code4) {
		this.bbg_code4 = bbg_code4;
	}

	public String getBbg_code5() {
		return bbg_code5;
	}

	public void setBbg_code5(String bbg_code5) {
		this.bbg_code5 = bbg_code5;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getTenor() {
		return tenor;
	}

	public void setTenor(Integer tenor) {
		this.tenor = tenor;
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

	public Integer getType1() {
		return type1;
	}

	public void setType1(Integer type1) {
		this.type1 = type1;
	}

	public Integer getType2() {
		return type2;
	}

	public void setType2(Integer type2) {
		this.type2 = type2;
	}

	public String getRate1() {
		return rate1;
	}

	public void setRate1(String rate1) {
		this.rate1 = rate1;
	}

	public String getRate2() {
		return rate2;
	}

	public void setRate2(String rate2) {
		this.rate2 = rate2;
	}

	public String getRate3() {
		return rate3;
	}

	public void setRate3(String rate3) {
		this.rate3 = rate3;
	}

	public String getRate4() {
		return rate4;
	}

	public void setRate4(String rate4) {
		this.rate4 = rate4;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

}
