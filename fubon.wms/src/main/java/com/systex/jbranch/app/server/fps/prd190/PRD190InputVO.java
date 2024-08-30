package com.systex.jbranch.app.server.fps.prd190;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD190InputVO extends PagingInputVO{
	private String type;			//查詢類別 1.依客戶ID可申購 2.可申購 3.不可申購
	private String cust_id;			//查詢客戶ID
	private String prd_id;			//產品ID
	private String prd_name;		//產品名稱
	private String risk_level;		//風險等級P1-P4
	private String currency;		//計價幣別
	private String stock_bond_type;	//S:類股價  B:類債券
	private String core_type;
	private String riskType;        //客戶風險屬性 FPSProd_NANO 使用
	private String inv_level;       //投資策略 L1-L4
	
	
	public String getInv_level() {
		return inv_level;
	}
	public void setInv_level(String inv_level) {
		this.inv_level = inv_level;
	}
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
	public String getPrd_name() {
		return prd_name;
	}
	public void setPrd_name(String prd_name) {
		this.prd_name = prd_name;
	}
	public String getRisk_level() {
		return risk_level;
	}
	public void setRisk_level(String risk_level) {
		this.risk_level = risk_level;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getStock_bond_type() {
		return stock_bond_type;
	}
	public void setStock_bond_type(String stock_bond_type) {
		this.stock_bond_type = stock_bond_type;
	}
	public String getCore_type() {
		return core_type;
	}
	public void setCore_type(String core_type) {
		this.core_type = core_type;
	}
	public String getRiskType() {
		return riskType;
	}
	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}	
}
