package com.systex.jbranch.app.server.fps.crm621;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class CRM621InputVO extends PagingInputVO{
	private String cust_id;
	private String bra_nbr;
	private String ao_code;
	private String detail_YN;
	private String sp_contact_id;
	private String content;
	private String valid_type;
	private Date valid_bgn_Date;
	private Date valid_end_Date;
	
	private List<Map<String, String>> datas;

	private List<CBSUtilOutputVO> data067050_067101_2;// 電文資料 自然人 _067050_067101、法人: _067050_067102
	private List<CBSUtilOutputVO> data067050_067000;  // 電文資料_067050_067000
	
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
	public String getSp_contact_id() {
		return sp_contact_id;
	}
	public void setSp_contact_id(String sp_contact_id) {
		this.sp_contact_id = sp_contact_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getValid_type() {
		return valid_type;
	}
	public void setValid_type(String valid_type) {
		this.valid_type = valid_type;
	}
	public Date getValid_end_Date() {
		return valid_end_Date;
	}
	public void setValid_end_Date(Date valid_end_Date) {
		this.valid_end_Date = valid_end_Date;
	}
	public Date getValid_bgn_Date() {
		return valid_bgn_Date;
	}
	public void setValid_bgn_Date(Date valid_bgn_Date) {
		this.valid_bgn_Date = valid_bgn_Date;
	}
	public List<Map<String, String>> getDatas() {
		return datas;
	}
	public void setDatas(List<Map<String, String>> datas) {
		this.datas = datas;
	}
	public String getBra_nbr() {
		return bra_nbr;
	}
	public void setBra_nbr(String bra_nbr) {
		this.bra_nbr = bra_nbr;
	}
	public String getDetail_YN() {
		return detail_YN;
	}
	public void setDetail_YN(String detail_YN) {
		this.detail_YN = detail_YN;
	}

	public List<CBSUtilOutputVO> getData067050_067101_2() {
		return data067050_067101_2;
	}

	public void setData067050_067101_2(List<CBSUtilOutputVO> data067050_067101_2) {
		this.data067050_067101_2 = data067050_067101_2;
	}

	public List<CBSUtilOutputVO> getData067050_067000() {
		return data067050_067000;
	}

	public void setData067050_067000(List<CBSUtilOutputVO> data067050_067000) {
		this.data067050_067000 = data067050_067000;
	}
}
