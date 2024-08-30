package com.systex.jbranch.app.server.fps.pms331;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

//
public class PMS331InputVO extends PagingInputVO {

	private String dataMonth;
	private String rc_id;   //區域中心
	private String op_id;	//營運區
	private String br_id;	//分行
	private String aoEmp;
	private String assignType;

	private List<Map<String, Object>> list;
	private List<Map<String, Object>> list2;

	public String getDataMonth() {
		return dataMonth;
	}

	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
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

	public String getAoEmp() {
		return aoEmp;
	}

	public void setAoEmp(String aoEmp) {
		this.aoEmp = aoEmp;
	}

	public String getAssignType() {
		return assignType;
	}

	public void setAssignType(String assignType) {
		this.assignType = assignType;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public List<Map<String, Object>> getList2() {
		return list2;
	}

	public void setList2(List<Map<String, Object>> list2) {
		this.list2 = list2;
	}

}
