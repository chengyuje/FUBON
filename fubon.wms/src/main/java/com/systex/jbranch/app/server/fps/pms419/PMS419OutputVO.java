package com.systex.jbranch.app.server.fps.pms419;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS419OutputVO extends PagingOutputVO {
	private List resultList_1;	//單筆申購
	private List resultList_2;	//定期不定額
	private List resultList_3;	//轉換
	private List resultList_4;	//贖回
	private List totalList;
	private String type;

	public List getResultList_1() {
		return resultList_1;
	}

	public void setResultList_1(List resultList_1) {
		this.resultList_1 = resultList_1;
	}

	public List getTotalList() {
		return totalList;
	}

	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List getResultList_2() {
		return resultList_2;
	}

	public void setResultList_2(List resultList_2) {
		this.resultList_2 = resultList_2;
	}

	public List getResultList_3() {
		return resultList_3;
	}

	public void setResultList_3(List resultList_3) {
		this.resultList_3 = resultList_3;
	}

	public List getResultList_4() {
		return resultList_4;
	}

	public void setResultList_4(List resultList_4) {
		this.resultList_4 = resultList_4;
	}

}
