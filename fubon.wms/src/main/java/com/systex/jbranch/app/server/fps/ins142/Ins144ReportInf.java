package com.systex.jbranch.app.server.fps.ins142;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface Ins144ReportInf {
	public String printReportINS144(
		DoGetCoverage03OutputVO doOutputVO , List<Map<String, Object>> familyDataList
	) throws DAOException, JBranchException;
}
