package com.systex.jbranch.app.server.fps.fps910;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS910InputVO extends PagingInputVO {
	private String param_no;
	private String inv_amt_type;
	private String setting_type;
	private String portfolio;
	private Date date;
	private List<Map<String, Object>> totalList;
	private String status;
	
	private String fileName;
	private String fileRealName;
	
	//add by Carley 2018/12/18
	private List<Map<String, Object>> stockList;
	private List<Map<String, Object>> bondsList;
	private String stock_bond_type;
	
	public String getParam_no() {
		return param_no;
	}
	public void setParam_no(String param_no) {
		this.param_no = param_no;
	}
	public String getInv_amt_type() {
		return inv_amt_type;
	}
	public void setInv_amt_type(String inv_amt_type) {
		this.inv_amt_type = inv_amt_type;
	}
	public String getSetting_type() {
		return setting_type;
	}
	public void setSetting_type(String setting_type) {
		this.setting_type = setting_type;
	}
	public String getPortfolio() {
		return portfolio;
	}
	public void setPortfolio(String portfolio) {
		this.portfolio = portfolio;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}
	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	public List<Map<String, Object>> getStockList() {
		return stockList;
	}
	public void setStockList(List<Map<String, Object>> stockList) {
		this.stockList = stockList;
	}
	public List<Map<String, Object>> getBondsList() {
		return bondsList;
	}
	public void setBondsList(List<Map<String, Object>> bondsList) {
		this.bondsList = bondsList;
	}
	public String getStock_bond_type() {
		return stock_bond_type;
	}
	public void setStock_bond_type(String stock_bond_type) {
		this.stock_bond_type = stock_bond_type;
	}
}