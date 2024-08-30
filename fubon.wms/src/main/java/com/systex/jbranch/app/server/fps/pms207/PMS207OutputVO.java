package com.systex.jbranch.app.server.fps.pms207;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS207OutputVO extends PagingOutputVO{
	private List dateList;
	private List mastList;
	private List diffList_w;
	private List diffList_d;
	private List dfCustList;
	private List dfPrdList1;
	private List dfPrdList2;
	private List dfPrdList3;
	private List empDtl;
	
	public List getDateList() {
		return dateList;
	}
	public void setDateList(List dateList) {
		this.dateList = dateList;
	}
	public List getMastList() {
		return mastList;
	}
	public void setMastList(List mastList) {
		this.mastList = mastList;
	}
	public List getDfCustList() {
		return dfCustList;
	}
	public void setDfCustList(List dfCustList) {
		this.dfCustList = dfCustList;
	}
	public List getDfPrdList1() {
		return dfPrdList1;
	}
	public void setDfPrdList1(List dfPrdList1) {
		this.dfPrdList1 = dfPrdList1;
	}
	public List getDfPrdList2() {
		return dfPrdList2;
	}
	public void setDfPrdList2(List dfPrdList2) {
		this.dfPrdList2 = dfPrdList2;
	}
	public List getDfPrdList3() {
		return dfPrdList3;
	}
	public void setDfPrdList3(List dfPrdList3) {
		this.dfPrdList3 = dfPrdList3;
	}
	public List getDiffList_w() {
		return diffList_w;
	}
	public List getDiffList_d() {
		return diffList_d;
	}
	public void setDiffList_w(List diffList_w) {
		this.diffList_w = diffList_w;
	}
	public List getEmpDtl() {
		return empDtl;
	}
	public void setEmpDtl(List empDtl) {
		this.empDtl = empDtl;
	}
	public void setDiffList_d(List diffList_d) {
		this.diffList_d = diffList_d;
	}
	
	
}
