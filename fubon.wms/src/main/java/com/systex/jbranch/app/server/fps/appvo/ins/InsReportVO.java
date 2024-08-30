package com.systex.jbranch.app.server.fps.appvo.ins;

import java.util.List;
import java.util.Map;

public class InsReportVO {
	private String insWelcome;		//迎賓文字
	private String custID;			//要保人ID
	private String custName;		//要保人姓名
	private String insuredID;		//被保險人ID
	private String insuredName;		//被保險人姓名
	private Number insuredAge;		//被保險人年齡
	private StatementVO statementVO;//需求問卷答案資料
	private List<Map<String, Object>> iPolicyList;		//行內保單資料清單
	private List<Map<String, Object>> oPolicyList;		//行外保單資料清單
	private List<Map<String, Object>> investList;		//現有保障&缺口清單
	private List<Map<String, Object>> investChartList;	//保障分析圖資料清單
	private List<Map<String, Object>> investProdList;	//推薦商品清單
	private List<Map<String, Object>> cashFlowList;		//保單金流表資料清單
	private List<Map<String, Object>> dmList;			//保險商品DM檔案清單
	
	public String getInsWelcome() {
		return insWelcome;
	}
	public void setInsWelcome(String insWelcome) {
		this.insWelcome = insWelcome;
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
	public String getInsuredID() {
		return insuredID;
	}
	public void setInsuredID(String insuredID) {
		this.insuredID = insuredID;
	}
	public String getInsuredName() {
		return insuredName;
	}
	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}
	public Number getInsuredAge() {
		return insuredAge;
	}
	public void setInsuredAge(Number insuredAge) {
		this.insuredAge = insuredAge;
	}
	public StatementVO getStatementVO() {
		return statementVO;
	}
	public void setStatementVO(StatementVO statementVO) {
		this.statementVO = statementVO;
	}
	public List<Map<String, Object>> getIPolicyList() {
		return iPolicyList;
	}
	public void setIPolicyList(List<Map<String, Object>> policyList) {
		iPolicyList = policyList;
	}
	public List<Map<String, Object>> getOPolicyList() {
		return oPolicyList;
	}
	public void setOPolicyList(List<Map<String, Object>> policyList) {
		oPolicyList = policyList;
	}
	public List<Map<String, Object>> getInvestList() {
		return investList;
	}
	public void setInvestList(List<Map<String, Object>> investList) {
		this.investList = investList;
	}
	public List<Map<String, Object>> getInvestChartList() {
		return investChartList;
	}
	public void setInvestChartList(List<Map<String, Object>> investChartList) {
		this.investChartList = investChartList;
	}
	public List<Map<String, Object>> getInvestProdList() {
		return investProdList;
	}
	public void setInvestProdList(List<Map<String, Object>> investProdList) {
		this.investProdList = investProdList;
	}
	public List<Map<String, Object>> getCashFlowList() {
		return cashFlowList;
	}
	public void setCashFlowList(List<Map<String, Object>> cashFlowList) {
		this.cashFlowList = cashFlowList;
	}
	public List<Map<String, Object>> getDmList() {
		return dmList;
	}
	public void setDmList(List<Map<String, Object>> dmList) {
		this.dmList = dmList;
	}
	
}