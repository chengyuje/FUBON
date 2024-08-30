package com.systex.jbranch.app.server.fps.pms308;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS308OutputVO extends PagingOutputVO {
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
