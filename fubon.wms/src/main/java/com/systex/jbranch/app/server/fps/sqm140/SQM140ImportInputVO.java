package com.systex.jbranch.app.server.fps.sqm140;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM140ImportInputVO extends PagingInputVO{	
	
	private Date eCreDate;
	/***** 可試範圍專用START *****/
	private Date sCreDate;
	private String branch_nbr;
	private String qtn_type;
	private String emp_id;
	private String cust_id;
	private String import_check;
	private List<Map<String, Object>> importList;
	private Date trade_date;
	/***** 可試範圍專用END *****/
	
	public Date geteCreDate() {
		return eCreDate;
	}
	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}
	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	
	public String getQtn_type() {
		return qtn_type;
	}
	public void setQtn_type(String qtn_type) {
		this.qtn_type = qtn_type;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getImport_check() {
		return import_check;
	}
	public void setImport_check(String import_check) {
		this.import_check = import_check;
	}
	public List<Map<String, Object>> getImportList() {
		return importList;
	}
	public void setImportList(List<Map<String, Object>> importList) {
		this.importList = importList;
	}
	public Date getTrade_date() {
		return trade_date;
	}
	public void setTrade_date(Date trade_date) {
		this.trade_date = trade_date;
	}
	
		
	

}
