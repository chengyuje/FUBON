package com.systex.jbranch.app.server.fps.pms202;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 主管評估排程管理<br>
 * Comments Name : PMS202OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS202OutputVO extends PagingOutputVO {
	private List resultList;
	private List aolist;

	public List getAolist() {
		return aolist;
	}
	public void setAolist(List aolist) {
		this.aolist = aolist;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
