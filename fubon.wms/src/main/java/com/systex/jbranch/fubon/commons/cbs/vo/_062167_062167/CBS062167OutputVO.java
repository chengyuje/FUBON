package com.systex.jbranch.fubon.commons.cbs.vo._062167_062167;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS062167OutputVO {
	
	@XmlElement(name = "TxRepeat")
	private List<CBS062167OutputDetailsVO> details;

	private String Loan_acct1; // 放款帳號
	private String ID_Type1; // 借款人證件類型/ 號碼
	private String ID_No1; //
	private String ID_Name1; // 借款人名稱
	private String ID_Type2; // 所有權人證件類型/ 號碼
	private String ID_No2; //
	private String ID_Name2; // 所有權人名稱
	private String Estate_No1; // 不動產號碼
	private String Coll_NO1; // 擔保品編號
	private String Facility_NO1; // 額度編號
	private String Status1; // 擔保品大類
	private String EnqDate1; // 查詢日期
	private String ColStatus; // 擔保品狀態
	private String Next_Page1; // 下一頁
	private String LAST_Key_1; //
	private String Next_Coll_1; //
	private String LAST_CIF_FLAG_1; //
	private String Next_Fac_1; //
	public List<CBS062167OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<CBS062167OutputDetailsVO> details) {
		this.details = details;
	}
	public String getLoan_acct1() {
		return Loan_acct1;
	}
	public void setLoan_acct1(String loan_acct1) {
		Loan_acct1 = loan_acct1;
	}
	public String getID_Type1() {
		return ID_Type1;
	}
	public void setID_Type1(String iD_Type1) {
		ID_Type1 = iD_Type1;
	}
	public String getID_No1() {
		return ID_No1;
	}
	public void setID_No1(String iD_No1) {
		ID_No1 = iD_No1;
	}
	public String getID_Name1() {
		return ID_Name1;
	}
	public void setID_Name1(String iD_Name1) {
		ID_Name1 = iD_Name1;
	}
	public String getID_Type2() {
		return ID_Type2;
	}
	public void setID_Type2(String iD_Type2) {
		ID_Type2 = iD_Type2;
	}
	public String getID_No2() {
		return ID_No2;
	}
	public void setID_No2(String iD_No2) {
		ID_No2 = iD_No2;
	}
	public String getID_Name2() {
		return ID_Name2;
	}
	public void setID_Name2(String iD_Name2) {
		ID_Name2 = iD_Name2;
	}
	public String getEstate_No1() {
		return Estate_No1;
	}
	public void setEstate_No1(String estate_No1) {
		Estate_No1 = estate_No1;
	}
	public String getColl_NO1() {
		return Coll_NO1;
	}
	public void setColl_NO1(String coll_NO1) {
		Coll_NO1 = coll_NO1;
	}
	public String getFacility_NO1() {
		return Facility_NO1;
	}
	public void setFacility_NO1(String facility_NO1) {
		Facility_NO1 = facility_NO1;
	}
	public String getStatus1() {
		return Status1;
	}
	public void setStatus1(String status1) {
		Status1 = status1;
	}
	public String getEnqDate1() {
		return EnqDate1;
	}
	public void setEnqDate1(String enqDate1) {
		EnqDate1 = enqDate1;
	}
	public String getColStatus() {
		return ColStatus;
	}
	public void setColStatus(String colStatus) {
		ColStatus = colStatus;
	}
	public String getNext_Page1() {
		return Next_Page1;
	}
	public void setNext_Page1(String next_Page1) {
		Next_Page1 = next_Page1;
	}
	public String getLAST_Key_1() {
		return LAST_Key_1;
	}
	public void setLAST_Key_1(String lAST_Key_1) {
		LAST_Key_1 = lAST_Key_1;
	}
	public String getNext_Coll_1() {
		return Next_Coll_1;
	}
	public void setNext_Coll_1(String next_Coll_1) {
		Next_Coll_1 = next_Coll_1;
	}
	public String getLAST_CIF_FLAG_1() {
		return LAST_CIF_FLAG_1;
	}
	public void setLAST_CIF_FLAG_1(String lAST_CIF_FLAG_1) {
		LAST_CIF_FLAG_1 = lAST_CIF_FLAG_1;
	}
	public String getNext_Fac_1() {
		return Next_Fac_1;
	}
	public void setNext_Fac_1(String next_Fac_1) {
		Next_Fac_1 = next_Fac_1;
	}
	
	
	
	


}
