package com.systex.jbranch.app.server.fps.org220;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG220InputVO extends PagingInputVO {
	
	private String RPT_TYPE;      // radio : 組織選取、員工編碼
	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
//	private String aocode;        // 代理人理專
	private String emp_id;        // 員工編碼 
	private String emp_id_txt;
	private Date sCreDate;        // 代理起始日期
	private Date eCreDate;        // 代理結束日期
	private String sTime;         // 代理起始時間
	private String eTime;         // 代理結束時間
	private BigDecimal seq_no;    // 序號 
	
	private String agent_desc;    // 備註
	private String agent_status;  // 代理狀態
	private String emp_rc_id;     // 被代理人區域中心
	private String emp_op_id;	  // 被代理人營運區
	private String emp_br_id;     // 被代理人分行
	private String emp_ao_id;     // 被代理人ID
	private String dept_id;       // 被代理人組織
	private String agent_dept_id; // 代理人組織         
	
	private String emp_name;	  //被代理人名稱
	
	private String mroleid;       //主管角色ID
	private String emp_login_id;
	private String act_type;
	
	private String uEmpID;
	
	public String getuEmpID() {
		return uEmpID;
	}

	public void setuEmpID(String uEmpID) {
		this.uEmpID = uEmpID;
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
	
	public String getEmp_rc_id() {
		return emp_rc_id;
	}
	
	public void setEmp_rc_id(String emp_rc_id) {
		this.emp_rc_id = emp_rc_id;
	}
	
	public String getEmp_op_id() {
		return emp_op_id;
	}
	
	public void setEmp_op_id(String emp_op_id) {
		this.emp_op_id = emp_op_id;
	}
	
	public String getEmp_br_id() {
		return emp_br_id;
	}
	
	public void setEmp_br_id(String emp_br_id) {
		this.emp_br_id = emp_br_id;
	}
	
	public String getEmp_ao_id() {
		return emp_ao_id;
	}
	
	public void setEmp_ao_id(String emp_ao_id) {
		this.emp_ao_id = emp_ao_id;
	}
	
	public String getRPT_TYPE() {
		return RPT_TYPE;
	}
	
	public void setRPT_TYPE(String rPT_TYPE) {
		RPT_TYPE = rPT_TYPE;
	}
	
	public String getEmp_id_txt() {
		return emp_id_txt;
	}

	public void setEmp_id_txt(String emp_id_txt) {
		this.emp_id_txt = emp_id_txt;
	}

	public String getEmp_id() {
		return emp_id;
	}
	
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
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
	
	public String getsTime() {
		return sTime;
	}
	
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	
	public String geteTime() {
		return eTime;
	}
	
	public void seteTime(String eTime) {
		this.eTime = eTime;
	}
	
	public BigDecimal getSeq_no() {
		return seq_no;
	}
	
	public void setSeq_no(BigDecimal seq_no) {
		this.seq_no = seq_no;
	}
	
	public String getAgent_desc() {
		return agent_desc;
	}
	
	public void setAgent_desc(String agent_desc) {
		this.agent_desc = agent_desc;
	}
	
	public String getDept_id() {
		return dept_id;
	}
	
	public void setDept_id(String dept_id) {
		this.dept_id = dept_id;
	}
	
	public String getAgent_dept_id() {
		return agent_dept_id;
	}
	
	public void setAgent_dept_id(String agent_dept_id) {
		this.agent_dept_id = agent_dept_id;
	}
	
	public String getAgent_status() {
		return agent_status;
	}
	
	public void setAgent_status(String agent_status) {
		this.agent_status = agent_status;
	}
	
	public String getEmp_name() {
		return emp_name;
	}
	
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	
	public String getMroleid() {
		return mroleid;
	}
	
	public void setMroleid(String mroleid) {
		this.mroleid = mroleid;
	}
	
	public String getEmp_login_id() {
		return emp_login_id;
	}
	
	public void setEmp_login_id(String emp_login_id) {
		this.emp_login_id = emp_login_id;
	}
	
	public String getAct_type() {
		return act_type;
	}
	
	public void setAct_type(String act_type) {
		this.act_type = act_type;
	}
	
}
