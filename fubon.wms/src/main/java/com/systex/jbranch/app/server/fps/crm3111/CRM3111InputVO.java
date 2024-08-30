package com.systex.jbranch.app.server.fps.crm3111;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM3111InputVO extends PagingInputVO {
	
	private String region_center_id;
	private String branch_area_id;
	private String new_ao_brh; //分行別
	private String new_ao_code; //新理專
	private Date sCreDate; //客戶異動日期(起)
	private Date eCreDate; //客戶異動日期(迄)
	private String cust_id; //客戶ID
	private String cust_name; //客戶姓名
	private String act_type; //查詢類型
	private String con_degree;
	private String vip_degree;
	
	private String visitRecordType;

	public String getVisitRecordType() {
		return visitRecordType;
	}

	public void setVisitRecordType(String visitRecordType) {
		this.visitRecordType = visitRecordType;
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

	public String getNew_ao_brh() {
		return new_ao_brh;
	}

	public void setNew_ao_brh(String new_ao_brh) {
		this.new_ao_brh = new_ao_brh;
	}

	public String getNew_ao_code() {
		return new_ao_code;
	}

	public void setNew_ao_code(String new_ao_code) {
		this.new_ao_code = new_ao_code;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getAct_type() {
		return act_type;
	}

	public void setAct_type(String act_type) {
		this.act_type = act_type;
	}

	public String getCon_degree() {
		return con_degree;
	}

	public void setCon_degree(String con_degree) {
		this.con_degree = con_degree;
	}

	public String getVip_degree() {
		return vip_degree;
	}

	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}
}