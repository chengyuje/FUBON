package com.systex.jbranch.app.server.fps.pms401;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS401InputdetailVO extends PagingInputVO {

	private String txDate;
	private String empID;

	public String getTxDate() {
		return txDate;
	}

	public void setTxDate(String txDate) {
		this.txDate = txDate;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

}
