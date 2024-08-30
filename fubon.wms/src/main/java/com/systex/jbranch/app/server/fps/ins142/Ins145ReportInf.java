package com.systex.jbranch.app.server.fps.ins142;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@SuppressWarnings("rawtypes")
public interface Ins145ReportInf {
	public enum Type{
		COV01 , COV03
	}
	
	public String printReportINS145(List lstEx , Type type)throws DAOException, JBranchException;
	
	public String printReportINS145(
		List lstEx , Map<String , Object> insCompanyMap , Map<String , Object> sortNoNameMap , Type type
	)throws JBranchException;
		
}
		
