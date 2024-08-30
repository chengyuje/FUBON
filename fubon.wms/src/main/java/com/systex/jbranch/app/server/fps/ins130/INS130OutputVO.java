package com.systex.jbranch.app.server.fps.ins130;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS130OutputVO extends PagingInputVO {
	private List familyFinlist; //家庭財務安全資料
	private List custMastlist;  //客戶主檔
	
	//add by Brian
	private List reportList; //家庭財務安全PDF資料

	public List getFamilyFinlist() {
		return familyFinlist;
	}

	public void setFamilyFinlist(List familyFinlist) {
		this.familyFinlist = familyFinlist;
	}

	public List getCustMastlist() {
		return custMastlist;
	}

	public void setCustMastlist(List custMastlist) {
		this.custMastlist = custMastlist;
	}

	public List getReportList() {
		return reportList;
	}

	public void setReportList(List reportList) {
		this.reportList = reportList;
	}	
		
}
