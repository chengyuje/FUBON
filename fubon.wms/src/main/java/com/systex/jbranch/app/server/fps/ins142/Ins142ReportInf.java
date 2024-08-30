package com.systex.jbranch.app.server.fps.ins142;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface Ins142ReportInf {
	public List<String> printReportINS142(
		DoGetCoverage03OutputVO doOutputVO , Map<String, Object> sortNoNameMap
	) throws JBranchException;
}
