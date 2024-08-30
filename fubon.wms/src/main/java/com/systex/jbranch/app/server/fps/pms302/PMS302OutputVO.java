package com.systex.jbranch.app.server.fps.pms302;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專即時速報InputVO<br>
 * Comments Name : PMS303InputVO.java<br>
 * Author :frank<br>
 * Date :2016年07月01日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS302OutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;
	private List cList;
	private String type;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List getcList() {
		return cList;
	}

	public void setcList(List cList) {
		this.cList = cList;
	}
}
