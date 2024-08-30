package com.systex.jbranch.app.server.fps.pms224;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS224InputVO extends PagingInputVO {
	private String dataMonth;
	private String op_id;
	private String br_id;
	private String emp_id;

	public String getDataMonth() {
		return dataMonth;
	}

	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

}
