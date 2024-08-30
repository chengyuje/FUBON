package com.systex.jbranch.platform.reportserver.service;

import javax.jws.WebService;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.reportserver.service.vo.ExchangeRequestVO;
import com.systex.jbranch.platform.reportserver.service.vo.ExchangeResponseVO;

@WebService
public interface ReportService {
	public ExchangeResponseVO generate(ExchangeRequestVO rqVO) throws JBranchException;
	
	public ExchangeResponseVO queryReport(long genId) throws JBranchException;
}
