package com.systex.jbranch.app.server.fps.pms316;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class PMS316OutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;
	private String incomeStr;

    public String getIncomeStr() {
        return incomeStr;
    }

    public void setIncomeStr(String incomeStr) {
        this.incomeStr = incomeStr;
    }

    public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getTotalList() {
		return totalList;
	}

	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
}
