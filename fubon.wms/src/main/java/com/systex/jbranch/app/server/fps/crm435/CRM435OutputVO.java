package com.systex.jbranch.app.server.fps.crm435;

import java.util.List;

import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

@Component
@SuppressWarnings({"rawtypes"})
public class CRM435OutputVO extends PagingOutputVO {
	
	private List resultList;
	private String errMsg;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
