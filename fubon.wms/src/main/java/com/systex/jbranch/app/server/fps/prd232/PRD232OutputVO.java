package com.systex.jbranch.app.server.fps.prd232;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("request")
public class PRD232OutputVO extends PagingOutputVO {
	private List resultList;
	private List errorList;
	private List errorList2;
	private List errorList3;
	private List errorList4;
	private List errorList5;
	private String rowCount;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
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
	public String getRowCount() {
		return rowCount;
	}
	public void setRowCount(String rowCount) {
		this.rowCount = rowCount;
	}
}