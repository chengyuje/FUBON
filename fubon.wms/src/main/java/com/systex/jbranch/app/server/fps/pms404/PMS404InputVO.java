package com.systex.jbranch.app.server.fps.pms404;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS404InputVO extends PagingInputVO {
	private Date sCreDate; // 起訖日-起
	private Date eCreDate; // 起訖日-訖
	private String rc_id; // 區域中心
	private String op_id; // 營運區
	private String br_id; // 分行
	private String ao_code; // 理專
	private String emp_id; // 員編

	private List<Map<String, Object>> list;

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

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	// public Date getTRADE_DATE() {
	// return TRADE_DATE;
	// }
	// public void setTRADE_DATE(Date tRADE_DATE) {
	// TRADE_DATE = tRADE_DATE;
	// }
	// public String getTXN_ID() {
	// return TXN_ID;
	// }
	// public void setTXN_ID(String tXN_ID) {
	// TXN_ID = tXN_ID;
	// }
	// public String getSUPERVISOR_FLAG() {
	// return SUPERVISOR_FLAG;
	// }
	// public void setSUPERVISOR_FLAG(String sUPERVISOR_FLAG) {
	// SUPERVISOR_FLAG = sUPERVISOR_FLAG;
	// }
	// public String getNOTE() {
	// return NOTE;
	// }
	// public void setNOTE(String nOTE) {
	// NOTE = nOTE;
	// }
	// public String getMODIFIER() {
	// return MODIFIER;
	// }
	// public void setMODIFIER(String mODIFIER) {
	// MODIFIER = mODIFIER;
	// }
	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

}
