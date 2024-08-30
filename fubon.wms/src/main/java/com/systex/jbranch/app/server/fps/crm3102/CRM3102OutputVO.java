package com.systex.jbranch.app.server.fps.crm3102;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM3102OutputVO extends PagingOutputVO {
	private List allPRJ;
	private List resultList;
	private List PRJList;
	private List FailList;
	private List AOFailLst;
	private List csvList;
	private List list;
	private List resultList1;
	private List AOScssLst;
	private String error;
	private String CUST_NAME;
	
	
	public List getAllPRJ() {
		return allPRJ;
	}
	public void setAllPRJ(List allPRJ) {
		this.allPRJ = allPRJ;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getPRJList() {
		return PRJList;
	}
	public void setPRJList(List pRJList) {
		PRJList = pRJList;
	}
	public List getFailList() {
		return FailList;
	}
	public void setFailList(List failList) {
		FailList = failList;
	}
	public List getAOFailLst() {
		return AOFailLst;
	}
	public void setAOFailLst(List aOFailLst) {
		AOFailLst = aOFailLst;
	}
	
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getResultList1() {
		return resultList1;
	}
	public void setResultList1(List resultList1) {
		this.resultList1 = resultList1;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public List getAOScssLst() {
		return AOScssLst;
	}
	public void setAOScssLst(List aoScssLst) {
		this.AOScssLst = aoScssLst;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	
}
