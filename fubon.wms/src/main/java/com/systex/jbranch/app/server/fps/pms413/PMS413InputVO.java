package com.systex.jbranch.app.server.fps.pms413;

import java.math.BigDecimal;
import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS413InputVO extends PagingInputVO{	
	
	private Date sCreDate;
	private String comType;
	private String ip;
	private String rc_id;   //區域中心
	private String op_id;   //營運區
	private String br_id;   //分行
	private String rc_name;
	private String op_name;
	private String br_name;
	private String emp_id;
	private String emp_name;
	private BigDecimal seq;
	
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	
	private String tempIP;
		
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

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public String getTempIP() {
		return tempIP;
	}

	public void setTempIP(String tempIP) {
		this.tempIP = tempIP;
	}

	public String getComType() {
		return comType;
	}
	
	public void setComType(String comType) {
		this.comType = comType;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getRc_id() {
		return rc_id;
	}
	
	public void setRc_id(String rc_id) {
		this.rc_id = rc_id;
	}
	
	public String getOp_id() {
		return op_id;
	}
	
	public void setOp_id(String op_id) {
		this.op_id = op_id;
	}
	
	public String getBr_id() {
		return br_id;
	}
	
	public void setBr_id(String br_id) {
		this.br_id = br_id;
	}		
	
	public String getRc_name() {
		return rc_name;
	}
	
	public void setRc_name(String rc_name) {
		this.rc_name = rc_name;
	}
	
	public String getOp_name() {
		return op_name;
	}
	
	public void setOp_name(String op_name) {
		this.op_name = op_name;
	}
	
	public String getBr_name() {
		return br_name;
	}
	
	public void setBr_name(String br_name) {
		this.br_name = br_name;
	}
	
	public String getEmp_id() {
		return emp_id;
	}
	
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	
	public String getEmp_name() {
		return emp_name;
	}
	
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	
	public BigDecimal getSeq() {
		return seq;
	}
	
	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

}
