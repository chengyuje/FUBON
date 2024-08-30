package com.systex.jbranch.fubon.commons.cbs.vo._017904_017904;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS017904OutputVO {

	private List<CBS017904OutputDetailsVO> details;
	private String AcctNO; // 主帳號:
	private String IDType; // 證件類型:
	private String IDNO; // 證件號碼:
	private String AcctName; // 戶名:
	private String LimtAmt; // 學程限額:
	private String AvabAmt; // 可用限額:
	private String TotAdvAmt; // 贷款金额合计:
	private String TotBalAmt; // 贷款余额合计:
	public List<CBS017904OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<CBS017904OutputDetailsVO> details) {
		this.details = details;
	}
	public String getAcctNO() {
		return AcctNO;
	}
	public void setAcctNO(String acctNO) {
		AcctNO = acctNO;
	}
	public String getIDType() {
		return IDType;
	}
	public void setIDType(String iDType) {
		IDType = iDType;
	}
	public String getIDNO() {
		return IDNO;
	}
	public void setIDNO(String iDNO) {
		IDNO = iDNO;
	}
	public String getAcctName() {
		return AcctName;
	}
	public void setAcctName(String acctName) {
		AcctName = acctName;
	}
	public String getLimtAmt() {
		return LimtAmt;
	}
	public void setLimtAmt(String limtAmt) {
		LimtAmt = limtAmt;
	}
	public String getAvabAmt() {
		return AvabAmt;
	}
	public void setAvabAmt(String avabAmt) {
		AvabAmt = avabAmt;
	}
	public String getTotAdvAmt() {
		return TotAdvAmt;
	}
	public void setTotAdvAmt(String totAdvAmt) {
		TotAdvAmt = totAdvAmt;
	}
	public String getTotBalAmt() {
		return TotBalAmt;
	}
	public void setTotBalAmt(String totBalAmt) {
		TotBalAmt = totBalAmt;
	}
	
	

}
