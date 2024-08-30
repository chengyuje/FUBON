package com.systex.jbranch.app.server.fps.pms334;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 分行最適經營客戶管理報表InputVO<br>
 * Comments Name : PMS334InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月06日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
public class PMS334InputVO extends PagingInputVO{
	private String sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String roleType;
	private String strDate;
	private String reportDate;
	private String reportType;
	private String cust_type;
	
	private String dataMonth;	//日期
	private String rc_id;	//區域中心
	private String op_id;	//營運區
	private String br_id;	//分行
	private String emp_id;	//員編
	
	private String CUST_DEGREE;	
	private String VIP_DEGREE;
	private String CUST_ID;	//客戶ID
	private String IND;	

	
	public String getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
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
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getStrDate() {
		return strDate;
	}
	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getCust_type() {
		return cust_type;
	}
	public void setCust_type(String cust_type) {
		this.cust_type = cust_type;
	}
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public String getIND() {
		return IND;
	}
	public void setIND(String iND) {
		IND = iND;
	}
	public String getCUST_DEGREE() {
		return CUST_DEGREE;
	}
	public String getVIP_DEGREE() {
		return VIP_DEGREE;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_DEGREE(String cUST_DEGREE) {
		CUST_DEGREE = cUST_DEGREE;
	}
	public void setVIP_DEGREE(String vIP_DEGREE) {
		VIP_DEGREE = vIP_DEGREE;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getDataMonth() {
		return dataMonth;
	}
	public String getRc_id() {
		return rc_id;
	}
	public String getOp_id() {
		return op_id;
	}
	public String getBr_id() {
		return br_id;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}
	public void setRc_id(String rc_id) {
		this.rc_id = rc_id;
	}
	public void setOp_id(String op_id) {
		this.op_id = op_id;
	}
	public void setBr_id(String br_id) {
		this.br_id = br_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	
}
