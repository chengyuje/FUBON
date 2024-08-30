package com.systex.jbranch.app.server.fps.prd250;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD250OutputVO extends PagingOutputVO {
	private String bond_name;
	private String rick_id;
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
	private List errorList6;


	public String getBond_name() {
		return bond_name;
	}
	public void setBond_name(String bond_name) {
		this.bond_name = bond_name;
	}
	public String getRick_id() {
		return rick_id;
	}
	public void setRick_id(String rick_id) {
		this.rick_id = rick_id;
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

	public List getErrorList6() {
		return errorList6;
	}

	public void setErrorList6(List errorList6) {
		this.errorList6 = errorList6;
	}
}
