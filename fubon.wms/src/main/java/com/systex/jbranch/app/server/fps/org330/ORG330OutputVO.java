package com.systex.jbranch.app.server.fps.org330;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG330OutputVO extends PagingOutputVO {
	
	private List roleLst;
	private List reviewList;
	
	private boolean exist;

	public List getReviewList() {
		return reviewList;
	}

	public void setReviewList(List reviewList) {
		this.reviewList = reviewList;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public List getRoleLst() {
		return roleLst;
	}

	public void setRoleLst(List roleLst) {
		this.roleLst = roleLst;
	}
}
