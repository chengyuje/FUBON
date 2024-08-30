package com.systex.jbranch.app.server.fps.prd110;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD110InputVO extends PagingInputVO {
	private String type;
	private String cust_id;
	private String fund_id;
	private String fund_name;
	private String risk_level;
	private String currency;
	private String dividend_type;
	private String dividend_fre;
	private String fund_type;
	private String inv_area;
	private String inv_target;
	private String trust_com;
	private String roi_dt;
	private String roi;
	private String obu_YN;
	private String prod_type;
	
	private Date sDate;
	private Date eDate;
	private String stime;
	private String sameSerialYN;		//同系列商品
	private String sameSerialProdId;	//同系列商品代碼
	
	// for ocean
	private String tradeType;
	private String main_prd;
	
	private String trustType;			//信託業務別 Y：外幣信託 N：台幣信託 C：國內基金
	
	private String isBackend;			//是否為後收型基金
	private String stock_bond_type;		//股債類型 S：股票型 B：債券型
	
	private String fund_subject;		//主題名稱
	private String fund_project;		//專案名稱
	
	private String fund_customer_level;		//客群名稱
	private String trustTS; //M:金錢信託 S:特金
	private String dynamicType; //動態鎖利類別 M:母基金 C:子基金
	private String dynamicProdCurrM; //動態鎖利母基金計價幣別
	private String inProdIdM; //動態鎖利轉入母基金
	private String fromSOTProdYN; //從下單過來
	private String fromPRD111YN; //從動態鎖利適配過來
	
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
	public String getFund_id() {
		return fund_id;
	}
	public void setFund_id(String fund_id) {
		this.fund_id = fund_id;
	}
	public String getFund_name() {
		return fund_name;
	}
	public void setFund_name(String fund_name) {
		this.fund_name = fund_name;
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
	public String getDividend_type() {
		return dividend_type;
	}
	public void setDividend_type(String dividend_type) {
		this.dividend_type = dividend_type;
	}
	public String getDividend_fre() {
		return dividend_fre;
	}
	public void setDividend_fre(String dividend_fre) {
		this.dividend_fre = dividend_fre;
	}
	public String getFund_type() {
		return fund_type;
	}
	public void setFund_type(String fund_type) {
		this.fund_type = fund_type;
	}
	public String getInv_area() {
		return inv_area;
	}
	public void setInv_area(String inv_area) {
		this.inv_area = inv_area;
	}
	public String getInv_target() {
		return inv_target;
	}
	public void setInv_target(String inv_target) {
		this.inv_target = inv_target;
	}
	public String getTrust_com() {
		return trust_com;
	}
	public void setTrust_com(String trust_com) {
		this.trust_com = trust_com;
	}
	public String getRoi_dt() {
		return roi_dt;
	}
	public void setRoi_dt(String roi_dt) {
		this.roi_dt = roi_dt;
	}
	public String getRoi() {
		return roi;
	}
	public void setRoi(String roi) {
		this.roi = roi;
	}
	public String getObu_YN() {
		return obu_YN;
	}
	public void setObu_YN(String obu_YN) {
		this.obu_YN = obu_YN;
	}
	public String getProd_type() {
		return prod_type;
	}
	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	public String getStime() {
		return stime;
	}
	public void setStime(String stime) {
		this.stime = stime;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getMain_prd() {
		return main_prd;
	}
	public void setMain_prd(String main_prd) {
		this.main_prd = main_prd;
	}
	public String getTrustType() {
		return trustType;
	}
	public void setTrustType(String trustType) {
		this.trustType = trustType;
	}
	public String getSameSerialYN() {
		return sameSerialYN;
	}
	public void setSameSerialYN(String sameSerialYN) {
		this.sameSerialYN = sameSerialYN;
	}
	public String getSameSerialProdId() {
		return sameSerialProdId;
	}
	public void setSameSerialProdId(String sameSerialProdId) {
		this.sameSerialProdId = sameSerialProdId;
	}
	public String getIsBackend() {
		return isBackend;
	}
	public void setIsBackend(String isBackend) {
		this.isBackend = isBackend;
	}
	public String getStock_bond_type() {
		return stock_bond_type;
	}
	public void setStock_bond_type(String stock_bond_type) {
		this.stock_bond_type = stock_bond_type;
	}
	public String getFund_subject() {
		return fund_subject;
	}
	public void setFund_subject(String fund_subject) {
		this.fund_subject = fund_subject;
	}
	public String getFund_project() {
		return fund_project;
	}
	public void setFund_project(String fund_project) {
		this.fund_project = fund_project;
	}
	public String getFund_customer_level() {
		return fund_customer_level;
	}
	public void setFund_customer_level(String fund_customer_level) {
		this.fund_customer_level = fund_customer_level;
	}
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	public String getDynamicType() {
		return dynamicType;
	}
	public void setDynamicType(String dynamicType) {
		this.dynamicType = dynamicType;
	}
	public String getDynamicProdCurrM() {
		return dynamicProdCurrM;
	}
	public void setDynamicProdCurrM(String dynamicProdCurrM) {
		this.dynamicProdCurrM = dynamicProdCurrM;
	}
	public String getInProdIdM() {
		return inProdIdM;
	}
	public void setInProdIdM(String inProdIdM) {
		this.inProdIdM = inProdIdM;
	}
	public String getFromSOTProdYN() {
		return fromSOTProdYN;
	}
	public void setFromSOTProdYN(String fromSOTProdYN) {
		this.fromSOTProdYN = fromSOTProdYN;
	}
	public String getFromPRD111YN() {
		return fromPRD111YN;
	}
	public void setFromPRD111YN(String fromPRD111YN) {
		this.fromPRD111YN = fromPRD111YN;
	}
	
}

