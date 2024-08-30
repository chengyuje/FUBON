package com.systex.jbranch.app.server.fps.pms102;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 潛力金流名單<br>
 * Comments Name : PMS102OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月17日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS102OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List resultList2;
	
	private List bePlanList;
	private List totallist;

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getBePlanList() {
		return bePlanList;
	}

	public void setBePlanList(List bePlanList) {
		this.bePlanList = bePlanList;
	}

	public List getTotallist() {
		return totallist;
	}

	public void setTotallist(List totallist) {
		this.totallist = totallist;
	}
}
