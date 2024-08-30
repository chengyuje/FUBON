package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class IPSDataVO implements Serializable {
	
	private String ipsNo;			//IPS編號
	private String custID;			//客戶ID	
	private String custName;		//客戶名稱
	private String quAnswer1;		//問題1答案
	private String quAnswer2;		//問題2答案
	private String quAnswer3;		//問題3答案 
	private String quAnswer4;		//問題4答案
	private String quAnswer5;		//問題5答案
	private String quAnswer6;		//問題6答案
	private String quAnswer7;		//問題7答案
	private List quAnswer8List;		//問題8答案
	private String quComment;		//附註事項
	private String ipsStatus;		//IPS有效狀態
	private String branchID;		//登錄者分行代碼	
	private String branchName;		//登錄者分行名稱
	private Date lastUpdate;		//上次維護日期
	private String modifier;		//上次維護人員代碼
	private String modifierName;	//上次維護人員名稱	
	private Boolean isDeleted;		//是否刪除此筆資料Flag
	private List IPSQustionData;	//IPS問卷題目清單
	public String getIpsNo() {
		return ipsNo;
	}
	public void setIpsNo(String ipsNo) {
		this.ipsNo = ipsNo;
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
	public String getQuAnswer1() {
		return quAnswer1;
	}
	public void setQuAnswer1(String quAnswer1) {
		this.quAnswer1 = quAnswer1;
	}
	public String getQuAnswer2() {
		return quAnswer2;
	}
	public void setQuAnswer2(String quAnswer2) {
		this.quAnswer2 = quAnswer2;
	}
	public String getQuAnswer3() {
		return quAnswer3;
	}
	public void setQuAnswer3(String quAnswer3) {
		this.quAnswer3 = quAnswer3;
	}
	public String getQuAnswer4() {
		return quAnswer4;
	}
	public void setQuAnswer4(String quAnswer4) {
		this.quAnswer4 = quAnswer4;
	}
	public String getQuAnswer5() {
		return quAnswer5;
	}
	public void setQuAnswer5(String quAnswer5) {
		this.quAnswer5 = quAnswer5;
	}
	public String getQuAnswer6() {
		return quAnswer6;
	}
	public void setQuAnswer6(String quAnswer6) {
		this.quAnswer6 = quAnswer6;
	}
	public String getQuAnswer7() {
		return quAnswer7;
	}
	public void setQuAnswer7(String quAnswer7) {
		this.quAnswer7 = quAnswer7;
	}
	public List getQuAnswer8List() {
		return quAnswer8List;
	}
	public void setQuAnswer8List(List quAnswer8List) {
		this.quAnswer8List = quAnswer8List;
	}
	public String getQuComment() {
		return quComment;
	}
	public void setQuComment(String quComment) {
		this.quComment = quComment;
	}
	public String getIpsStatus() {
		return ipsStatus;
	}
	public void setIpsStatus(String ipsStatus) {
		this.ipsStatus = ipsStatus;
	}
	public String getBranchID() {
		return branchID;
	}
	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getModifierName() {
		return modifierName;
	}
	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public List getIPSQustionData() {
		return IPSQustionData;
	}
	public void setIPSQustionData(List qustionData) {
		IPSQustionData = qustionData;
	}
}
