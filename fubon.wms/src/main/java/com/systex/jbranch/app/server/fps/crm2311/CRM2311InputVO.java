package com.systex.jbranch.app.server.fps.crm2311;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM2311InputVO extends CRM230InputVO{
	//消金商品
	private String debit_type;				// 負債類型 1:房貸 2:信貸 3:留貸 4:學貸 5:綜定質借 6:存單質借/信託質借
	private String currency_id;				// 幣別
	private String ori_amt_min;				// 原貸金額/訂約金額
	private String ori_amt_max;				// 原貸金額/訂約金額
	private String loan_bal_min;			// 貸款餘額/已透支金額
	private String loan_bal_max;			// 貸款餘額/已透支金額
	private String int_rate_min;			// 適用利率
	private String int_rate_max;			// 適用利率
	private Date due_sDate;					// 到期日
	private Date due_eDate;					// 到期日
	private String retention_rate_min;		// 擔保維持率
	private String retention_rate_max;		// 擔保維持率
	
	public String getDebit_type() {
		return debit_type;
	}
	
	public void setDebit_type(String debit_type) {
		this.debit_type = debit_type;
	}
	
	public String getCurrency_id() {
		return currency_id;
	}
	
	public void setCurrency_id(String currency_id) {
		this.currency_id = currency_id;
	}
	
	public String getOri_amt_min() {
		return ori_amt_min;
	}
	
	public void setOri_amt_min(String ori_amt_min) {
		this.ori_amt_min = ori_amt_min;
	}
	
	public String getOri_amt_max() {
		return ori_amt_max;
	}
	
	public void setOri_amt_max(String ori_amt_max) {
		this.ori_amt_max = ori_amt_max;
	}
	
	public String getLoan_bal_min() {
		return loan_bal_min;
	}
	
	public void setLoan_bal_min(String loan_bal_min) {
		this.loan_bal_min = loan_bal_min;
	}
	
	public String getLoan_bal_max() {
		return loan_bal_max;
	}
	
	public void setLoan_bal_max(String loan_bal_max) {
		this.loan_bal_max = loan_bal_max;
	}
	
	public String getInt_rate_min() {
		return int_rate_min;
	}
	
	public void setInt_rate_min(String int_rate_min) {
		this.int_rate_min = int_rate_min;
	}
	
	public String getInt_rate_max() {
		return int_rate_max;
	}
	
	public void setInt_rate_max(String int_rate_max) {
		this.int_rate_max = int_rate_max;
	}
	
	public Date getDue_sDate() {
		return due_sDate;
	}
	
	public void setDue_sDate(Date due_sDate) {
		this.due_sDate = due_sDate;
	}
	
	public Date getDue_eDate() {
		return due_eDate;
	}
	
	public void setDue_eDate(Date due_eDate) {
		this.due_eDate = due_eDate;
	}
	
	public String getRetention_rate_min() {
		return retention_rate_min;
	}
	
	public void setRetention_rate_min(String retention_rate_min) {
		this.retention_rate_min = retention_rate_min;
	}
	
	public String getRetention_rate_max() {
		return retention_rate_max;
	}
	
	public void setRetention_rate_max(String retention_rate_max) {
		this.retention_rate_max = retention_rate_max;
	}
	
}
