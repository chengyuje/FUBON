package com.systex.jbranch.app.server.fps.cam220;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM220OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List assBranchList;
	private List logList;
	private List assBranchListByCust;
	private BigDecimal leadsCounts;
	private Integer rowCounts;

	public BigDecimal getLeadsCounts() {
		return leadsCounts;
	}

	public void setLeadsCounts(BigDecimal leadsCounts) {
		this.leadsCounts = leadsCounts;
	}

	public Integer getRowCounts() {
		return rowCounts;
	}

	public void setRowCounts(Integer rowCounts) {
		this.rowCounts = rowCounts;
	}

	public List getAssBranchListByCust() {
		return assBranchListByCust;
	}

	public void setAssBranchListByCust(List assBranchListByCust) {
		this.assBranchListByCust = assBranchListByCust;
	}

	public List getLogList() {
		return logList;
	}

	public void setLogList(List logList) {
		this.logList = logList;
	}

	public List getAssBranchList() {
		return assBranchList;
	}

	public void setAssBranchList(List assBranchList) {
		this.assBranchList = assBranchList;
	}

	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
