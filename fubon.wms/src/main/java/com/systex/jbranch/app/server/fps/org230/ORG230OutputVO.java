package com.systex.jbranch.app.server.fps.org230;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG230OutputVO extends PagingOutputVO {
	private List branchMbrQuotaLst    = null;
	
	public List getBranchMbrQuotaLst() {
		return branchMbrQuotaLst;
	}
	public void setBranchMbrQuotaLst(List branchMbrQuotaLst) {
		this.branchMbrQuotaLst = branchMbrQuotaLst;
	}
	
}
