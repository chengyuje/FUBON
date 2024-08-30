package com.systex.jbranch.app.server.fps.crm511;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM511InputVO extends PagingInputVO {
	private String qstn_id;
	private String display_layer;
	private String display_order;
	private String qstn_content;
	private String word_surgery;
	private String qstn_type;
	private String qstn_format;
	private String opt_yn;
	private String memo_yn;
	private List<String> vip_degree;
	private List<String> aum_degree;
	private Date bgn_sDate;
	private Date bgn_eDate;
	private Date end_sDate;
	private Date end_eDate;
	private List<Map<String,String>> au_list;
	private List<Map<String, Object>> data;
	
	
	public String getQstn_id() {
		return qstn_id;
	}
	public void setQstn_id(String qstn_id) {
		this.qstn_id = qstn_id;
	}
	public String getDisplay_layer() {
		return display_layer;
	}
	public void setDisplay_layer(String display_layer) {
		this.display_layer = display_layer;
	}
	public String getDisplay_order() {
		return display_order;
	}
	public void setDisplay_order(String display_order) {
		this.display_order = display_order;
	}
	public String getQstn_content() {
		return qstn_content;
	}
	public void setQstn_content(String qstn_content) {
		this.qstn_content = qstn_content;
	}
	public String getWord_surgery() {
		return word_surgery;
	}
	public void setWord_surgery(String word_surgery) {
		this.word_surgery = word_surgery;
	}
	public String getQstn_type() {
		return qstn_type;
	}
	public void setQstn_type(String qstn_type) {
		this.qstn_type = qstn_type;
	}
	public String getQstn_format() {
		return qstn_format;
	}
	public void setQstn_format(String qstn_format) {
		this.qstn_format = qstn_format;
	}
	public String getOpt_yn() {
		return opt_yn;
	}
	public void setOpt_yn(String opt_yn) {
		this.opt_yn = opt_yn;
	}
	public String getMemo_yn() {
		return memo_yn;
	}
	public void setMemo_yn(String memo_yn) {
		this.memo_yn = memo_yn;
	}
	public List<String> getVip_degree() {
		return vip_degree;
	}
	public void setVip_degree(List<String> vip_degree) {
		this.vip_degree = vip_degree;
	}
	public List<String> getAum_degree() {
		return aum_degree;
	}
	public void setAum_degree(List<String> aum_degree) {
		this.aum_degree = aum_degree;
	}
	public Date getBgn_sDate() {
		return bgn_sDate;
	}
	public void setBgn_sDate(Date bgn_sDate) {
		this.bgn_sDate = bgn_sDate;
	}
	public Date getBgn_eDate() {
		return bgn_eDate;
	}
	public void setBgn_eDate(Date bgn_eDate) {
		this.bgn_eDate = bgn_eDate;
	}
	public Date getEnd_sDate() {
		return end_sDate;
	}
	public void setEnd_sDate(Date end_sDate) {
		this.end_sDate = end_sDate;
	}
	public Date getEnd_eDate() {
		return end_eDate;
	}
	public void setEnd_eDate(Date end_eDate) {
		this.end_eDate = end_eDate;
	}
	public List<Map<String, String>> getAu_list() {
		return au_list;
	}
	public void setAu_list(List<Map<String, String>> au_list) {
		this.au_list = au_list;
	}
	public List<Map<String, Object>> getData() {
		return data;
	}
	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}
}
