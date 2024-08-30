package com.systex.jbranch.app.server.fps.cmmgr015;

import java.util.List;
/**
 * 訊息維護
 *
 * @author SamTu
 * @date 20180719
 *
 */
public class CMMGR015OutputVO {
	private List resultList;
	private String sqlResult;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	  public String getSqlResult()
	  {
	    return this.sqlResult;
	  }

	  public void setSqlResult(String sqlResult)
	  {
	    this.sqlResult = sqlResult;
	  }
}
