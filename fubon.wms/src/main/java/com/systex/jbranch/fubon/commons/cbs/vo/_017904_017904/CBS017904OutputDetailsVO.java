package com.systex.jbranch.fubon.commons.cbs.vo._017904_017904;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS017904OutputDetailsVO {

	private String Semester; // "學年度-學期"
	private String SocInsuNO; // "對保編號"
	private String Inter; // "借款人利率"
	private String LoanAmt; // "貸款金額"
	private String LoanBal; // "貸款餘額"
	private String PayDate; // "上繳日"
	private String NextPayDate; // "下次客戶應繳日"
	private String AutoIntFlag; // "自付利息註記"
	private String WorkFlag; // "在職註記"
	private String PartDisch; // "部分銷帳"
	
	
	public String getSemester() {
		return Semester;
	}
	public void setSemester(String semester) {
		Semester = semester;
	}
	public String getSocInsuNO() {
		return SocInsuNO;
	}
	public void setSocInsuNO(String socInsuNO) {
		SocInsuNO = socInsuNO;
	}
	public String getInter() {
		return Inter;
	}
	public void setInter(String inter) {
		Inter = inter;
	}
	public String getLoanAmt() {
		return LoanAmt;
	}
	public void setLoanAmt(String loanAmt) {
		LoanAmt = loanAmt;
	}
	public String getLoanBal() {
		return LoanBal;
	}
	public void setLoanBal(String loanBal) {
		LoanBal = loanBal;
	}
	public String getPayDate() {
		return PayDate;
	}
	public void setPayDate(String payDate) {
		PayDate = payDate;
	}
	public String getNextPayDate() {
		return NextPayDate;
	}
	public void setNextPayDate(String nextPayDate) {
		NextPayDate = nextPayDate;
	}
	public String getAutoIntFlag() {
		return AutoIntFlag;
	}
	public void setAutoIntFlag(String autoIntFlag) {
		AutoIntFlag = autoIntFlag;
	}
	public String getWorkFlag() {
		return WorkFlag;
	}
	public void setWorkFlag(String workFlag) {
		WorkFlag = workFlag;
	}
	public String getPartDisch() {
		return PartDisch;
	}
	public void setPartDisch(String partDisch) {
		PartDisch = partDisch;
	}

	
}
