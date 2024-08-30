package com.systex.jbranch.app.server.fps.prd130;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD130InputVO extends PagingInputVO {
	private String type;
	private String cust_id;
	private String prd_id;
	private String bond_name;
	private String bond_cate;
	private String currency;
	private String face_value;
	private String maturity;
	private String ytm;
	private String rating_sp;
	private String risk_level;
	private String pi_YN;
	private String hnwc_YN;
	private String obu_YN;
	private String prod_type;

	private String stime;
	private Date sDate;
	private Date eDate;

	private String bondCustLevel;
	private String bondProject;
	
	private String trustTS; //M:金錢信託 S:特金

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getBond_name() {
		return bond_name;
	}
	public void setBond_name(String bond_name) {
		this.bond_name = bond_name;
	}
	public String getBond_cate() {
		return bond_cate;
	}
	public void setBond_cate(String bond_cate) {
		this.bond_cate = bond_cate;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getFace_value() {
		return face_value;
	}
	public void setFace_value(String face_value) {
		this.face_value = face_value;
	}
	public String getMaturity() {
		return maturity;
	}
	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}
	public String getYtm() {
		return ytm;
	}
	public void setYtm(String ytm) {
		this.ytm = ytm;
	}
	public String getRating_sp() {
		return rating_sp;
	}
	public void setRating_sp(String rating_sp) {
		this.rating_sp = rating_sp;
	}
	public String getRisk_level() {
		return risk_level;
	}
	public void setRisk_level(String risk_level) {
		this.risk_level = risk_level;
	}
	public String getPi_YN() {
		return pi_YN;
	}
	public void setPi_YN(String pi_YN) {
		this.pi_YN = pi_YN;
	}
	public String getHnwc_YN() {
		return hnwc_YN;
	}
	public void setHnwc_YN(String hnwc_YN) {
		this.hnwc_YN = hnwc_YN;
	}
	public String getObu_YN() {
		return obu_YN;
	}
	public void setObu_YN(String obu_YN) {
		this.obu_YN = obu_YN;
	}
	public String getProd_type() {
		return prod_type;
	}
	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
	}
	public String getStime() {
		return stime;
	}
	public void setStime(String stime) {
		this.stime = stime;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public String getBondCustLevel() {
		return bondCustLevel;
	}

	public void setBondCustLevel(String bondCustLevel) {
		this.bondCustLevel = bondCustLevel;
	}

	public String getBondProject() {
		return bondProject;
	}

	public void setBondProject(String bondProject) {
		this.bondProject = bondProject;
	}
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	
}
