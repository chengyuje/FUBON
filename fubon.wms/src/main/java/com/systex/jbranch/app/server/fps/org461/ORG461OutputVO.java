package com.systex.jbranch.app.server.fps.org461;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG461OutputVO extends PagingOutputVO {
	
	private List aoGoalLst = null;
	private List aoRankLst = null;
	private List aoHistLst = null;

	public List getAoHistLst() {
		return aoHistLst;
	}

	public void setAoHistLst(List aoHistLst) {
		this.aoHistLst = aoHistLst;
	}

	public List getAoGoalLst() {
		return aoGoalLst;
	}

	public void setAoGoalLst(List aoGoalLst) {
		this.aoGoalLst = aoGoalLst;
	}

	public List getAoRankLst() {
		return aoRankLst;
	}

	public void setAoRankLst(List aoRankLst) {
		this.aoRankLst = aoRankLst;
	}
	
}
