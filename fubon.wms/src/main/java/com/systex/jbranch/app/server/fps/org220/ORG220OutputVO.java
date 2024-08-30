package com.systex.jbranch.app.server.fps.org220;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
public class ORG220OutputVO extends PagingOutputVO {
	
	
	private List resultList;      //查詢結果
	private List resultList2;	  //登入者資料
	private List resultList3;     //新增資料
	private List resultList4;     //限定代理人
	private List resultList5;     //主管
	private List resultList6;
	private List resultList7;     //檢核輸入EMP_ID是否為限定代理人
	private List mailMsg;
	private List allPerson;
	private List reviewdate;
	private List mailLst;
	
	private List empList;
	private List<Map<String, Object>> bsList;
	
	private String mailContent;
	
	public List<Map<String, Object>> getBsList() {
		return bsList;
	}

	public void setBsList(List<Map<String, Object>> bsList) {
		this.bsList = bsList;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public List getEmpList() {
		return empList;
	}

	public void setEmpList(List empList) {
		this.empList = empList;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public List getResultList3() {
		return resultList3;
	}

	public void setResultList3(List resultList3) {
		this.resultList3 = resultList3;
	}

	public List getResultList4() {
		return resultList4;
	}

	public void setResultList4(List resultList4) {
		this.resultList4 = resultList4;
	}

	public List getResultList5() {
		return resultList5;
	}

	public void setResultList5(List resultList5) {
		this.resultList5 = resultList5;
	}
	public List getResultList6() {
		return resultList6;
	}

	public void setResultList6(List resultList6) {
		this.resultList6 = resultList6;
	}

	public List getResultList7() {
		return resultList7;
	}

	public void setResultList7(List resultList7) {
		this.resultList7 = resultList7;
	}
	
	
	public List getMailMsg() {
		return mailMsg;
	}

	public void setMailMsg(List mailMsg) {
		this.mailMsg = mailMsg;
	}

	public List getAllPerson() {
		return allPerson;
	}

	public void setAllPerson(List allPerson) {
		this.allPerson = allPerson;
	}

	public List getReviewdate() {
		return reviewdate;
	}

	public void setReviewdate(List reviewdate) {
		this.reviewdate = reviewdate;
	}

	public List getMailLst() {
		return mailLst;
	}

	public void setMailLst(List mailLst) {
		this.mailLst = mailLst;
	}
	

}
