package com.systex.jbranch.app.server.fps.crm710;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM710InputVO extends PagingInputVO {

	private String empID;
	private String custID;
	private String custName;
	private String importSDate;
	private String importEDate;
	private String prodType;
	private String tradeType;

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getImportSDate() {
		return importSDate;
	}

	public void setImportSDate(String importSDate) {
		this.importSDate = importSDate;
	}

	public String getImportEDate() {
		return importEDate;
	}

	public void setImportEDate(String importEDate) {
		this.importEDate = importEDate;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
}
