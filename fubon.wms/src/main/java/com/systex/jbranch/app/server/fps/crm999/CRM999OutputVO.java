package com.systex.jbranch.app.server.fps.crm999;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM999OutputVO extends PagingOutputVO {
	
	private List resultList1,resultList2;
	private CRM999InputVO paramVO;
	private String msg;
	private String errorMsg;

	public List getResultList1() {
		return resultList1;
	}

	public void setResultList1(List resultList1) {
		this.resultList1 = resultList1;
	}

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public CRM999InputVO getParamVO() {
		return paramVO;
	}

	public void setParamVO(CRM999InputVO paramVO) {
		this.paramVO = paramVO;
	}

	

}
