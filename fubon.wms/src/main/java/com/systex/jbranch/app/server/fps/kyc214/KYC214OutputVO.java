package com.systex.jbranch.app.server.fps.kyc214;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC214OutputVO extends PagingOutputVO{
	
	private List qstQustionLst;

	public List getQstQustionLst() {
		return qstQustionLst;
	}

	public void setQstQustionLst(List qstQustionLst) {
		this.qstQustionLst = qstQustionLst;
	}
	
	
}
