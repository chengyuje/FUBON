package com.systex.jbranch.app.server.fps.prd210;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD210OutputVO extends PagingOutputVO {
	private String ins_name;
	private String currency;
	private Boolean canEdit;
	private String errorMsg;
	private List resultList;
	private List resultList2;
	private List resultList3;
	private Date lastDate;
	private List errorList;
	private List errorList2;
	private List errorList3;
	private List errorList4;
	private List errorList5;
	
	
	public String getIns_name() {
		return ins_name;
	}
	public void setIns_name(String ins_name) {
		this.ins_name = ins_name;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Boolean getCanEdit() {
		return canEdit;
	}
	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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
	public Date getLastDate() {
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	public List getErrorList() {
		return errorList;
	}
	public void setErrorList(List errorList) {
		this.errorList = errorList;
	}
	public List getErrorList2() {
		return errorList2;
	}
	public void setErrorList2(List errorList2) {
		this.errorList2 = errorList2;
	}
	public List getErrorList3() {
		return errorList3;
	}
	public void setErrorList3(List errorList3) {
		this.errorList3 = errorList3;
	}
	public List getErrorList4() {
		return errorList4;
	}
	public void setErrorList4(List errorList4) {
		this.errorList4 = errorList4;
	}
	public List getErrorList5() {
		return errorList5;
	}
	public void setErrorList5(List errorList5) {
		this.errorList5 = errorList5;
	}
}