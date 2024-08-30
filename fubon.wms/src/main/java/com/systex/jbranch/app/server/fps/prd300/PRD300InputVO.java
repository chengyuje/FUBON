package com.systex.jbranch.app.server.fps.prd300;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD300InputVO extends PagingInputVO {
	private String prd_id;    			//產品代碼
	private String prd_name; 			//產品名稱
	private String riskcate_id;			//商品風險等級
	private String stock_bond_type;		//類股票/類債券
	private String core_type;			//核心/衛星
	private String currency_std_id;		//計價幣別
	private String is_sale;				//是否可銷售
	private String inv_level;			//投資策略等級
	
	public String getIs_sale() {
		return is_sale;
	}
	public void setIs_sale(String is_sale) {
		this.is_sale = is_sale;
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
	public String getRiskcate_id() {
		return riskcate_id;
	}
	public void setRiskcate_id(String riskcate_id) {
		this.riskcate_id = riskcate_id;
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
	public String getCurrency_std_id() {
		return currency_std_id;
	}
	public void setCurrency_std_id(String currency_std_id) {
		this.currency_std_id = currency_std_id;
	}
	public String getInv_level() {
		return inv_level;
	}
	public void setInv_level(String inv_level) {
		this.inv_level = inv_level;
	}
	
	
}
